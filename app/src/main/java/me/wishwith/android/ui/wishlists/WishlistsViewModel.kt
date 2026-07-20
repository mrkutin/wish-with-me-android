package me.wishwith.android.ui.wishlists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.wishwith.android.data.local.entity.WishlistEntity
import me.wishwith.android.data.repository.ItemRepository
import me.wishwith.android.data.repository.WishlistRepository
import me.wishwith.android.domain.sync.SyncEngine
import me.wishwith.android.domain.sync.SyncState
import javax.inject.Inject

@HiltViewModel
class WishlistsViewModel @Inject constructor(
    private val wishlistRepository: WishlistRepository,
    private val itemRepository: ItemRepository,
    private val syncEngine: SyncEngine
) : ViewModel() {

    val wishlists: StateFlow<List<WishlistEntity>> = wishlistRepository.observeMyWishlists()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val syncState: StateFlow<SyncState> = syncEngine.syncState

    private val _itemCounts = MutableStateFlow<Map<String, Int>>(emptyMap())
    val itemCounts: StateFlow<Map<String, Int>> = _itemCounts.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    init {
        viewModelScope.launch {
            wishlists.collect { list ->
                val counts = mutableMapOf<String, Int>()
                list.forEach { wishlist ->
                    counts[wishlist.id] = itemRepository.countByWishlistId(wishlist.id)
                }
                _itemCounts.value = counts
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            syncEngine.fullSync()
            _isRefreshing.value = false
        }
    }

    fun createWishlist(name: String, description: String?, icon: String, iconColor: String?) {
        viewModelScope.launch {
            wishlistRepository.create(name, description, icon, iconColor)
        }
    }

    fun updateWishlist(wishlist: WishlistEntity, name: String, description: String?, icon: String, iconColor: String?) {
        viewModelScope.launch {
            wishlistRepository.update(wishlist, name, description, icon, iconColor)
        }
    }

    fun deleteWishlist(wishlist: WishlistEntity) {
        viewModelScope.launch {
            wishlistRepository.delete(wishlist)
        }
    }
}
