package me.wishwith.android.ui.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.wishwith.android.data.local.dao.ItemDao
import me.wishwith.android.data.local.dao.MarkDao
import me.wishwith.android.data.local.dao.WishlistDao
import me.wishwith.android.data.local.entity.ItemEntity
import me.wishwith.android.data.local.entity.MarkEntity
import me.wishwith.android.data.local.entity.WishlistEntity
import me.wishwith.android.data.repository.ShareRepository
import me.wishwith.android.domain.sync.SyncEngine
import me.wishwith.android.util.IdGenerator
import me.wishwith.android.util.TokenManager
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class SharedWishlistViewModel @Inject constructor(
    private val wishlistDao: WishlistDao,
    private val itemDao: ItemDao,
    private val markDao: MarkDao,
    private val shareRepository: ShareRepository,
    private val tokenManager: TokenManager,
    private val syncEngine: SyncEngine
) : ViewModel() {

    private val _wishlist = MutableStateFlow<WishlistEntity?>(null)
    val wishlist: StateFlow<WishlistEntity?> = _wishlist.asStateFlow()

    private val _items = MutableStateFlow<List<ItemEntity>>(emptyList())
    val items: StateFlow<List<ItemEntity>> = _items.asStateFlow()

    private val _marks = MutableStateFlow<List<MarkEntity>>(emptyList())
    val marks: StateFlow<List<MarkEntity>> = _marks.asStateFlow()

    private val _permissions = MutableStateFlow<List<String>>(emptyList())
    val permissions: StateFlow<List<String>> = _permissions.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    val currentUserId: String?
        get() = tokenManager.currentUserId

    val canMarkItems: StateFlow<Boolean> = _permissions.map { perms ->
        perms.contains("mark") || perms.isEmpty()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    private var wishlistJob: Job? = null
    private var itemsJob: Job? = null
    private var marksJob: Job? = null

    fun loadByWishlistId(wishlistId: String) {
        wishlistJob?.cancel()
        itemsJob?.cancel()
        marksJob?.cancel()
        wishlistJob = viewModelScope.launch {
            wishlistDao.observeById(wishlistId).collect { _wishlist.value = it }
        }
        itemsJob = viewModelScope.launch {
            itemDao.observeByWishlistId(wishlistId).collect { _items.value = it }
        }
        marksJob = viewModelScope.launch {
            markDao.observeByWishlistId(wishlistId).collect { _marks.value = it }
        }
    }

    fun grantAccessAndLoad(token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val result = shareRepository.grantAccess(token)
                _permissions.value = result.permissions
                loadByWishlistId(result.wishlistId)
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to access wishlist"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun markItem(itemId: String) {
        val userId = tokenManager.currentUserId ?: return
        val wishlist = _wishlist.value ?: return
        val now = Instant.now().toString()

        val mark = MarkEntity(
            id = IdGenerator.create("mark"),
            itemId = itemId,
            wishlistId = wishlist.id,
            ownerId = wishlist.ownerId,
            markedBy = userId,
            quantity = 1,
            access = wishlist.access.filter { it != wishlist.ownerId },
            createdAt = now,
            updatedAt = now,
            isDirty = true
        )
        viewModelScope.launch {
            markDao.upsert(mark)
            syncEngine.triggerSync()
        }
    }

    fun unmarkItem(mark: MarkEntity) {
        viewModelScope.launch {
            markDao.upsert(mark.copy(
                softDeleted = true,
                isDirty = true,
                updatedAt = Instant.now().toString()
            ))
            syncEngine.triggerSync()
        }
    }
}
