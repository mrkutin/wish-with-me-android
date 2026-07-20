package me.wishwith.android.data.remote.interceptor

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import me.wishwith.android.data.remote.dto.RefreshRequest
import me.wishwith.android.data.remote.dto.TokenResponse
import me.wishwith.android.util.TokenManager
import okhttp3.Authenticator
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.Route
import java.util.concurrent.TimeUnit

class TokenAuthenticator(
    private val tokenManager: TokenManager,
    private val json: Json,
    private val baseUrl: String
) : Authenticator {

    private val mutex = Mutex()
    private val refreshClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    override fun authenticate(route: Route?, response: Response): Request? {
        // Don't retry if we already retried
        if (response.request.header("X-Retry") != null) return null

        return runBlocking {
            mutex.withLock {
                // Check if another thread already refreshed the token
                val currentToken = tokenManager.accessToken
                val requestToken = response.request.header("Authorization")?.removePrefix("Bearer ")

                if (currentToken != null && currentToken != requestToken) {
                    // Token was already refreshed by another request
                    return@runBlocking response.request.newBuilder()
                        .header("Authorization", "Bearer $currentToken")
                        .header("X-Retry", "true")
                        .build()
                }

                // Try to refresh
                val refreshToken = tokenManager.refreshToken ?: return@runBlocking null

                try {
                    val refreshBody = json.encodeToString(
                        RefreshRequest.serializer(),
                        RefreshRequest(refreshToken)
                    )
                    val contentType = "application/json".toMediaType()
                    val refreshRequest = Request.Builder()
                        .url("${baseUrl}api/v2/auth/refresh")
                        .post(refreshBody.toRequestBody(contentType))
                        .build()

                    val refreshResponse = refreshClient.newCall(refreshRequest).execute()

                    if (refreshResponse.isSuccessful) {
                        val body = refreshResponse.body?.string() ?: return@runBlocking null
                        val tokenResponse = json.decodeFromString<TokenResponse>(body)
                        tokenManager.accessToken = tokenResponse.accessToken
                        tokenManager.refreshToken = tokenResponse.refreshToken

                        response.request.newBuilder()
                            .header("Authorization", "Bearer ${tokenResponse.accessToken}")
                            .header("X-Retry", "true")
                            .build()
                    } else {
                        tokenManager.clearTokens()
                        null
                    }
                } catch (e: Exception) {
                    tokenManager.clearTokens()
                    null
                }
            }
        }
    }
}
