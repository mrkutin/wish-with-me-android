package me.wishwith.android.ui.auth

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
import me.wishwith.android.data.local.entity.UserEntity
import me.wishwith.android.data.repository.AuthRepository
import me.wishwith.android.domain.sync.NetworkMonitor
import me.wishwith.android.domain.sync.SyncEngine
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val syncEngine: SyncEngine,
    private val networkMonitor: NetworkMonitor
) : ViewModel() {

    val isAuthenticated: StateFlow<Boolean> = authRepository.isAuthenticated

    val currentUser: StateFlow<UserEntity?> = authRepository.currentUser

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    val isOffline: StateFlow<Boolean> = networkMonitor.isConnected
        .map { connected -> !connected }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    init {
        viewModelScope.launch {
            authRepository.loadStoredAuth()
        }
    }

    fun login(email: String, password: String) {
        if (!validateLoginFields(email, password)) return
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                authRepository.login(email.trim(), password)
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Login failed"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun register(email: String, password: String, confirmPassword: String, name: String) {
        if (!validateRegisterFields(email, password, confirmPassword, name)) return
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                authRepository.register(email.trim(), password, name.trim(), "en")
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Registration failed"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            syncEngine.stop()
            authRepository.logout()
        }
    }

    fun onAuthenticated() {
        syncEngine.start()
        viewModelScope.launch {
            syncEngine.fullSync()
        }
    }

    fun handleOAuthSuccess(accessToken: String, refreshToken: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                authRepository.handleOAuthTokens(accessToken, refreshToken)
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun handleOAuthError(error: String, email: String?) {
        _errorMessage.value = when (error) {
            "email_exists" -> "This email is already registered"
            "already_linked" -> "Account already linked"
            else -> "OAuth error: $error"
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    private fun validateLoginFields(email: String, password: String): Boolean {
        if (email.isBlank()) {
            _errorMessage.value = "Email is required"
            return false
        }
        if (password.isBlank()) {
            _errorMessage.value = "Password is required"
            return false
        }
        return true
    }

    private fun validateRegisterFields(
        email: String,
        password: String,
        confirmPassword: String,
        name: String
    ): Boolean {
        if (name.isBlank()) {
            _errorMessage.value = "Name is required"
            return false
        }
        if (email.isBlank()) {
            _errorMessage.value = "Email is required"
            return false
        }
        if (password.length < 8) {
            _errorMessage.value = "Password must be at least 8 characters"
            return false
        }
        if (password != confirmPassword) {
            _errorMessage.value = "Passwords don't match"
            return false
        }
        return true
    }
}
