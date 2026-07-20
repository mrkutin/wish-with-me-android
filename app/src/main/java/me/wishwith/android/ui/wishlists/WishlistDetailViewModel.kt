package me.wishwith.android.ui.wishlists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.wishwith.android.data.local.dao.MarkDao
import me.wishwith.android.data.local.entity.ItemEntity
import me.wishwith.android.data.local.entity.MarkEntity
import me.wishwith.android.data.local.entity.WishlistEntity
import me.wishwith.android.data.repository.ItemRepository
import me.wishwith.android.data.repository.WishlistRepository
import me.wishwith.android.domain.sync.SyncEngine
import me.wishwith.android.domain.sync.SyncState
import me.wishwith.android.util.TokenManager
import javax.inject.Inject

@HiltViewModel
class WishlistDetailViewModel @Inject constructor(
    private val wishlistRepository: WishlistRepository,
    private val itemRepository: ItemRepository,
    private val markDao: MarkDao,
    private val tokenManager: TokenManager,
    private val syncEngine: SyncEngine
) : ViewModel() {

    val syncState: StateFlow<SyncState> = syncEngine.syncState

    private val _wishlist = MutableStateFlow<WishlistEntity?>(null)
    val wishlist: StateFlow<WishlistEntity?> = _wishlist.asStateFlow()

    private val _items = MutableStateFlow<List<ItemEntity>>(emptyList())
    val items: StateFlow<List<ItemEntity>> = _items.asStateFlow()

    private val _marks = MutableStateFlow<List<MarkEntity>>(emptyList())
    val marks: StateFlow<List<MarkEntity>> = _marks.asStateFlow()

    val isOwner: StateFlow<Boolean> = _wishlist
        .map { it?.ownerId == tokenManager.currentUserId }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun loadWishlist(wishlistId: String) {
        viewModelScope.launch {
            wishlistRepository.observeById(wishlistId).collect { _wishlist.value = it }
        }
        viewModelScope.launch {
            itemRepository.observeByWishlistId(wishlistId).collect { _items.value = it }
        }
        viewModelScope.launch {
            // Only load marks if not owner (surprise mode)
            markDao.observeByWishlistId(wishlistId).collect { marks ->
                _marks.value = if (tokenManager.currentUserId != _wishlist.value?.ownerId) marks else emptyList()
            }
        }
    }

    fun createItemByUrl(wishlistId: String, url: String) {
        viewModelScope.launch {
            itemRepository.createByUrl(wishlistId, url)
        }
    }

    fun createItemManually(
        wishlistId: String,
        title: String,
        description: String?,
        price: Double?,
        currency: String?,
        quantity: Int,
        sourceUrl: String?,
        imageBase64: String?
    ) {
        viewModelScope.launch {
            itemRepository.createManually(
                wishlistId, title, description, price, currency, quantity, sourceUrl, imageBase64
            )
        }
    }

    fun updateItem(
        item: ItemEntity,
        title: String,
        description: String?,
        price: Double?,
        currency: String?,
        quantity: Int,
        sourceUrl: String?,
        imageBase64: String?
    ) {
        viewModelScope.launch {
            itemRepository.update(item, title, description, price, currency, quantity, sourceUrl, imageBase64)
        }
    }

    fun deleteItem(item: ItemEntity) {
        viewModelScope.launch {
            itemRepository.delete(item)
        }
    }
}
