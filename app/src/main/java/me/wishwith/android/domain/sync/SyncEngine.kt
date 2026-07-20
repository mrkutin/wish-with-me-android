package me.wishwith.android.domain.sync

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import me.wishwith.android.data.local.dao.BookmarkDao
import me.wishwith.android.data.local.dao.ItemDao
import me.wishwith.android.data.local.dao.MarkDao
import me.wishwith.android.data.local.dao.ShareDao
import me.wishwith.android.data.local.dao.UserDao
import me.wishwith.android.data.local.dao.WishlistDao
import me.wishwith.android.data.remote.api.SyncApi
import me.wishwith.android.data.remote.dto.SyncPushRequest
import me.wishwith.android.util.TokenManager
import javax.inject.Inject
import javax.inject.Singleton

enum class SyncState {
    IDLE, SYNCING, ERROR, OFFLINE
}

@Singleton
class SyncEngine @Inject constructor(
    private val syncApi: SyncApi,
    private val tokenManager: TokenManager,
    private val networkMonitor: NetworkMonitor,
    private val wishlistDao: WishlistDao,
    private val itemDao: ItemDao,
    private val markDao: MarkDao,
    private val bookmarkDao: BookmarkDao,
    private val userDao: UserDao,
    private val shareDao: ShareDao
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val syncMutex = Mutex()
    private var periodicJob: Job? = null
    private var debounceJob: Job? = null

    private val _syncState = MutableStateFlow(SyncState.IDLE)
    val syncState: StateFlow<SyncState> = _syncState.asStateFlow()

    private val collections = listOf("wishlists", "items", "marks", "bookmarks", "users", "shares")

    fun start() {
        networkMonitor.start()
        startPeriodicSync()

        scope.launch {
            networkMonitor.isConnected.collect { connected ->
                if (connected) {
                    triggerSync()
                } else {
                    _syncState.value = SyncState.OFFLINE
                }
            }
        }
    }

    fun stop() {
        periodicJob?.cancel()
        debounceJob?.cancel()
        networkMonitor.stop()
    }

    fun triggerSync() {
        debounceJob?.cancel()
        debounceJob = scope.launch {
            delay(1000) // 1s debounce
            fullSync()
        }
    }

    suspend fun fullSync() = withContext(Dispatchers.IO) {
        if (!networkMonitor.isConnected.value) {
            _syncState.value = SyncState.OFFLINE
            return@withContext
        }
        if (!tokenManager.hasTokens()) return@withContext

        syncMutex.withLock {
            _syncState.value = SyncState.SYNCING
            try {
                for (collection in collections) {
                    pushCollection(collection)
                    pullCollection(collection)
                }
                _syncState.value = SyncState.IDLE
            } catch (e: Exception) {
                _syncState.value = SyncState.ERROR
            }
        }
    }

    private suspend fun pushCollection(collection: String) {
        val dirtyDocs = getDirtyDocuments(collection)
        if (dirtyDocs.isEmpty()) return

        val request = SyncPushRequest(dirtyDocs)
        val response = syncApi.push(collection, request)

        val conflictIds = response.conflicts.map { it.documentId }.toSet()

        // Mark clean all docs that were not conflicted
        dirtyDocs.forEach { doc ->
            val docId = extractDocId(doc) ?: return@forEach
            if (docId !in conflictIds) {
                markDocClean(collection, docId)
            }
        }

        // Handle conflicts: accept server version
        response.conflicts.forEach { conflict ->
            conflict.serverDocument?.let { serverDoc ->
                upsertDocument(collection, serverDoc, forceOverwrite = true)
            }
        }
    }

    private suspend fun pullCollection(collection: String) {
        val response = syncApi.pull(collection)
        val serverIds = mutableSetOf<String>()

        response.documents.forEach { doc ->
            val docId = extractDocId(doc) ?: return@forEach
            serverIds.add(docId)
            upsertDocument(collection, doc, forceOverwrite = false)
        }

        // Orphan cleanup: delete local docs not on server (unless dirty)
        cleanupOrphans(collection, serverIds)
    }

    private fun extractDocId(doc: Map<String, JsonElement>): String? {
        return (doc["_id"] as? JsonPrimitive)?.content
    }

    private suspend fun getDirtyDocuments(collection: String): List<Map<String, JsonElement>> {
        return when (collection) {
            "wishlists" -> wishlistDao.getDirty().map { EntitySerializer.wishlistToMap(it) }
            "items" -> itemDao.getDirty().map { EntitySerializer.itemToMap(it) }
            "marks" -> markDao.getDirty().map { EntitySerializer.markToMap(it) }
            "bookmarks" -> bookmarkDao.getDirty().map { EntitySerializer.bookmarkToMap(it) }
            "users" -> userDao.getDirty().map { EntitySerializer.userToMap(it) }
            "shares" -> shareDao.getDirty().map { EntitySerializer.shareToMap(it) }
            else -> emptyList()
        }
    }

    private suspend fun markDocClean(collection: String, docId: String) {
        when (collection) {
            "wishlists" -> wishlistDao.markClean(docId)
            "items" -> itemDao.markClean(docId)
            "marks" -> markDao.markClean(docId)
            "bookmarks" -> bookmarkDao.markClean(docId)
            "users" -> userDao.markClean(docId)
            "shares" -> shareDao.markClean(docId)
        }
    }

    private suspend fun upsertDocument(
        collection: String,
        doc: Map<String, JsonElement>,
        forceOverwrite: Boolean
    ) {
        val docId = extractDocId(doc) ?: return

        when (collection) {
            "wishlists" -> {
                if (!forceOverwrite) {
                    val local = wishlistDao.getById(docId)
                    if (local?.isDirty == true) return
                }
                val entity = EntitySerializer.mapToWishlist(doc)
                if (entity.softDeleted) wishlistDao.deleteById(docId)
                else wishlistDao.upsert(entity)
            }
            "items" -> {
                if (!forceOverwrite) {
                    val local = itemDao.getById(docId)
                    if (local?.isDirty == true) return
                }
                val entity = EntitySerializer.mapToItem(doc)
                if (entity.softDeleted) itemDao.deleteById(docId)
                else itemDao.upsert(entity)
            }
            "marks" -> {
                if (!forceOverwrite) {
                    val local = markDao.getById(docId)
                    if (local?.isDirty == true) return
                }
                val entity = EntitySerializer.mapToMark(doc)
                if (entity.softDeleted) markDao.deleteById(docId)
                else markDao.upsert(entity)
            }
            "bookmarks" -> {
                if (!forceOverwrite) {
                    val local = bookmarkDao.getById(docId)
                    if (local?.isDirty == true) return
                }
                val entity = EntitySerializer.mapToBookmark(doc)
                if (entity.softDeleted) bookmarkDao.deleteById(docId)
                else bookmarkDao.upsert(entity)
            }
            "users" -> {
                if (!forceOverwrite) {
                    val local = userDao.getById(docId)
                    if (local?.isDirty == true) return
                }
                val entity = EntitySerializer.mapToUser(doc)
                if (entity.softDeleted) userDao.deleteById(docId)
                else userDao.upsert(entity)
            }
            "shares" -> {
                if (!forceOverwrite) {
                    val local = shareDao.getById(docId)
                    if (local?.isDirty == true) return
                }
                val entity = EntitySerializer.mapToShare(doc)
                if (entity.softDeleted) shareDao.deleteById(docId)
                else shareDao.upsert(entity)
            }
        }
    }

    private suspend fun cleanupOrphans(collection: String, serverIds: Set<String>) {
        val dirtyIds = getDirtyDocuments(collection).mapNotNull { doc ->
            extractDocId(doc)
        }.toSet()

        val localIds = when (collection) {
            "wishlists" -> wishlistDao.getAllIds()
            "items" -> itemDao.getAllIds()
            "marks" -> markDao.getAllIds()
            "bookmarks" -> bookmarkDao.getAllIds()
            "users" -> userDao.getAllIds()
            "shares" -> shareDao.getAllIds()
            else -> emptyList()
        }

        localIds.forEach { localId ->
            if (localId !in serverIds && localId !in dirtyIds) {
                when (collection) {
                    "wishlists" -> wishlistDao.deleteById(localId)
                    "items" -> itemDao.deleteById(localId)
                    "marks" -> markDao.deleteById(localId)
                    "bookmarks" -> bookmarkDao.deleteById(localId)
                    "users" -> userDao.deleteById(localId)
                    "shares" -> shareDao.deleteById(localId)
                }
            }
        }
    }

    private fun startPeriodicSync() {
        periodicJob?.cancel()
        periodicJob = scope.launch {
            while (true) {
                delay(30_000) // 30 seconds
                if (tokenManager.hasTokens()) {
                    fullSync()
                }
            }
        }
    }
}
