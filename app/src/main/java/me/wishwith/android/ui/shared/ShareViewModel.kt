package me.wishwith.android.ui.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import me.wishwith.android.data.local.entity.ShareEntity
import me.wishwith.android.data.repository.ShareRepository
import javax.inject.Inject

@HiltViewModel
class ShareViewModel @Inject constructor(
    private val shareRepository: ShareRepository
) : ViewModel() {

    private val _shares = MutableStateFlow<List<ShareEntity>>(emptyList())
    val shares: StateFlow<List<ShareEntity>> = _shares.asStateFlow()

    private val _isCreating = MutableStateFlow(false)
    val isCreating: StateFlow<Boolean> = _isCreating.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private var sharesJob: Job? = null

    fun loadShares(wishlistId: String) {
        sharesJob?.cancel()
        sharesJob = viewModelScope.launch {
            shareRepository.observeByWishlistId(wishlistId).collect {
                _shares.value = it
            }
        }
    }

    fun createShareLink(wishlistId: String, linkType: String) {
        viewModelScope.launch {
            _isCreating.value = true
            try {
                shareRepository.createShareLink(wishlistId, linkType)
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isCreating.value = false
            }
        }
    }

    fun revokeShareLink(wishlistId: String, share: ShareEntity) {
        viewModelScope.launch {
            try {
                shareRepository.revokeShareLink(wishlistId, share.id)
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }

    fun getShareUrl(share: ShareEntity): String =
        "https://wishwith.me/s/${share.token}"
}
