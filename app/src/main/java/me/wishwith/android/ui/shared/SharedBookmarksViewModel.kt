package me.wishwith.android.ui.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.wishwith.android.data.local.entity.BookmarkEntity
import me.wishwith.android.data.repository.BookmarkRepository
import me.wishwith.android.domain.sync.SyncEngine
import javax.inject.Inject

@HiltViewModel
class SharedBookmarksViewModel @Inject constructor(
    private val bookmarkRepository: BookmarkRepository,
    private val syncEngine: SyncEngine
) : ViewModel() {

    val bookmarks: StateFlow<List<BookmarkEntity>> = bookmarkRepository.observeMyBookmarks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            syncEngine.fullSync()
            _isRefreshing.value = false
        }
    }

    fun deleteBookmark(bookmark: BookmarkEntity) {
        viewModelScope.launch {
            bookmarkRepository.delete(bookmark)
        }
    }
}
