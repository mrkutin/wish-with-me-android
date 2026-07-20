package me.wishwith.android.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import me.wishwith.android.data.local.dao.BookmarkDao
import me.wishwith.android.data.local.dao.ItemDao
import me.wishwith.android.data.local.dao.MarkDao
import me.wishwith.android.data.local.dao.ShareDao
import me.wishwith.android.data.local.dao.UserDao
import me.wishwith.android.data.local.dao.WishlistDao
import me.wishwith.android.data.local.entity.UserEntity
import me.wishwith.android.data.remote.api.AuthApi
import me.wishwith.android.data.remote.dto.LoginRequest
import me.wishwith.android.data.remote.dto.LogoutRequest
import me.wishwith.android.data.remote.dto.RegisterRequest
import me.wishwith.android.data.remote.dto.UserDto
import me.wishwith.android.util.TokenManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val authApi: AuthApi,
    private val tokenManager: TokenManager,
    private val userDao: UserDao,
    private val wishlistDao: WishlistDao,
    private val itemDao: ItemDao,
    private val markDao: MarkDao,
    private val shareDao: ShareDao,
    private val bookmarkDao: BookmarkDao
) {
    private val _isAuthenticated = MutableStateFlow(tokenManager.hasTokens())
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    val currentUserId: String? get() = tokenManager.currentUserId

    suspend fun login(email: String, password: String) {
        val response = authApi.login(LoginRequest(email, password))
        storeAuthResponse(response.accessToken, response.refreshToken, response.user)
    }

    suspend fun register(email: String, password: String, name: String, locale: String) {
        val response = authApi.register(RegisterRequest(email, password, name, locale))
        storeAuthResponse(response.accessToken, response.refreshToken, response.user)
    }

    suspend fun handleOAuthTokens(accessToken: String, refreshToken: String) {
        tokenManager.accessToken = accessToken
        tokenManager.refreshToken = refreshToken
        val user = authApi.me()
        storeAuthResponse(accessToken, refreshToken, user)
    }

    suspend fun loadStoredAuth(): Boolean {
        if (!tokenManager.hasTokens()) return false
        return try {
            val user = authApi.me()
            val entity = user.toEntity()
            userDao.upsert(entity)
            tokenManager.currentUserId = user.id
            _currentUser.value = entity
            _isAuthenticated.value = true
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun logout() {
        try {
            val refreshToken = tokenManager.refreshToken
            if (refreshToken != null) {
                authApi.logout(LogoutRequest(refreshToken))
            }
        } catch (_: Exception) { }

        tokenManager.clearTokens()
        clearLocalData()
        _currentUser.value = null
        _isAuthenticated.value = false
    }

    private suspend fun storeAuthResponse(accessToken: String, refreshToken: String, user: UserDto) {
        tokenManager.accessToken = accessToken
        tokenManager.refreshToken = refreshToken
        tokenManager.currentUserId = user.id

        val entity = user.toEntity()
        userDao.upsert(entity)
        _currentUser.value = entity
        _isAuthenticated.value = true
    }

    private suspend fun clearLocalData() {
        userDao.deleteAll()
        wishlistDao.deleteAll()
        itemDao.deleteAll()
        markDao.deleteAll()
        shareDao.deleteAll()
        bookmarkDao.deleteAll()
    }

    private fun UserDto.toEntity() = UserEntity(
        id = id,
        email = email,
        name = name,
        avatarBase64 = avatarBase64,
        bio = bio,
        publicUrlSlug = publicUrlSlug,
        locale = locale,
        birthday = birthday,
        access = listOf(id),
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
