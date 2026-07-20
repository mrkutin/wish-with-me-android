package me.wishwith.android.data.remote.interceptor

import me.wishwith.android.util.TokenManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val tokenManager: TokenManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val token = tokenManager.accessToken

        val builder = request.newBuilder()
        if (token != null) {
            builder.header("Authorization", "Bearer $token")
        }
        if (request.body != null) {
            builder.header("Content-Type", "application/json")
        }

        return chain.proceed(builder.build())
    }
}
