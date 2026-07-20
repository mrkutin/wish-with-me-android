package me.wishwith.android.ui.profile

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import me.wishwith.android.data.remote.api.OAuthApi
import me.wishwith.android.data.remote.dto.ConnectedAccount
import me.wishwith.android.util.LocaleHelper
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val oAuthApi: OAuthApi,
    private val prefs: SharedPreferences
) : ViewModel() {

    private val _locale = MutableStateFlow(LocaleHelper.getLocale(prefs))
    val locale: StateFlow<String> = _locale.asStateFlow()

    private val _connectedAccounts = MutableStateFlow<List<ConnectedAccount>>(emptyList())
    val connectedAccounts: StateFlow<List<ConnectedAccount>> = _connectedAccounts.asStateFlow()

    private val _hasPassword = MutableStateFlow(true)
    val hasPassword: StateFlow<Boolean> = _hasPassword.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadConnectedAccounts()
    }

    fun setLocale(locale: String) {
        LocaleHelper.setLocale(prefs, locale)
        _locale.value = locale
    }

    fun loadConnectedAccounts() {
        viewModelScope.launch {
            try {
                val response = oAuthApi.getConnectedAccounts()
                _connectedAccounts.value = response.accounts
                _hasPassword.value = response.hasPassword
            } catch (_: Exception) { }
        }
    }

    fun unlinkProvider(provider: String) {
        viewModelScope.launch {
            try {
                oAuthApi.unlink(provider)
                loadConnectedAccounts()
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }

    fun initiateLink(provider: String): String? {
        // Return authorize URL for Chrome Custom Tab
        return null // Handled by OAuthHelper
    }
}
