package me.wishwith.android.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import me.wishwith.android.data.local.dao.UserDao
import me.wishwith.android.data.local.entity.UserEntity
import me.wishwith.android.domain.sync.SyncEngine
import me.wishwith.android.util.TokenManager
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userDao: UserDao,
    private val tokenManager: TokenManager,
    private val syncEngine: SyncEngine
) : ViewModel() {

    private val _user = MutableStateFlow<UserEntity?>(null)
    val user: StateFlow<UserEntity?> = _user.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val slugRegex = Regex("^[a-z0-9-]+$")
    private var profileJob: Job? = null

    init {
        loadProfile()
    }

    fun loadProfile() {
        profileJob?.cancel()
        profileJob = viewModelScope.launch {
            val userId = tokenManager.currentUserId ?: return@launch
            userDao.observeById(userId).collect { _user.value = it }
        }
    }

    fun isSlugValid(slug: String): Boolean {
        return slug.isEmpty() || slugRegex.matches(slug)
    }

    fun canSave(name: String, slug: String): Boolean {
        return name.isNotBlank() && isSlugValid(slug)
    }

    fun saveProfile(
        name: String,
        bio: String?,
        publicUrlSlug: String?,
        birthday: String?,
        avatarBase64: String?
    ) {
        val current = _user.value ?: return
        viewModelScope.launch {
            _isSaving.value = true
            _errorMessage.value = null
            try {
                val updated = current.copy(
                    name = name,
                    bio = bio,
                    publicUrlSlug = publicUrlSlug,
                    birthday = birthday,
                    avatarBase64 = avatarBase64 ?: current.avatarBase64,
                    updatedAt = Instant.now().toString(),
                    isDirty = true
                )
                userDao.upsert(updated)
                _user.value = updated
                syncEngine.triggerSync()
                _successMessage.value = "Profile saved"
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isSaving.value = false
            }
        }
    }

    fun updateAvatar(base64: String) {
        val current = _user.value ?: return
        viewModelScope.launch {
            val updated = current.copy(
                avatarBase64 = base64,
                updatedAt = Instant.now().toString(),
                isDirty = true
            )
            userDao.upsert(updated)
            _user.value = updated
            syncEngine.triggerSync()
        }
    }

    fun clearMessages() {
        _successMessage.value = null
        _errorMessage.value = null
    }
}
