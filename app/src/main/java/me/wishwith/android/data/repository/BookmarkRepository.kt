package me.wishwith.android.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import me.wishwith.android.data.local.dao.BookmarkDao
import me.wishwith.android.data.local.entity.BookmarkEntity
import me.wishwith.android.domain.sync.SyncEngine
import me.wishwith.android.util.TokenManager
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookmarkRepository @Inject constructor(
    private val bookmarkDao: BookmarkDao,
    private val tokenManager: TokenManager,
    private val syncEngine: SyncEngine
) {
    fun observeMyBookmarks(): Flow<List<BookmarkEntity>> {
        val userId = tokenManager.currentUserId ?: return flowOf(emptyList())
        return bookmarkDao.observeByUserId(userId)
    }

    suspend fun getByWishlistId(wishlistId: String): BookmarkEntity? {
        val userId = tokenManager.currentUserId ?: return null
        return bookmarkDao.getByWishlistAndUser(wishlistId, userId)
    }

    suspend fun delete(bookmark: BookmarkEntity) {
        bookmarkDao.upsert(bookmark.copy(
            softDeleted = true,
            isDirty = true,
            updatedAt = Instant.now().toString()
        ))
        syncEngine.triggerSync()
    }
}
