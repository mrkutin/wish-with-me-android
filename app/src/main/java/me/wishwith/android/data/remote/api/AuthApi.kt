package me.wishwith.android.data.remote.api

import me.wishwith.android.data.remote.dto.AuthResponse
import me.wishwith.android.data.remote.dto.LoginRequest
import me.wishwith.android.data.remote.dto.LogoutRequest
import me.wishwith.android.data.remote.dto.RefreshRequest
import me.wishwith.android.data.remote.dto.RegisterRequest
import me.wishwith.android.data.remote.dto.TokenResponse
import me.wishwith.android.data.remote.dto.UserDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApi {
    @POST("api/v2/auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("api/v2/auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("api/v2/auth/refresh")
    suspend fun refresh(@Body request: RefreshRequest): TokenResponse

    @POST("api/v2/auth/logout")
    suspend fun logout(@Body request: LogoutRequest): Response<Unit>

    @GET("api/v2/auth/me")
    suspend fun me(): UserDto
}
