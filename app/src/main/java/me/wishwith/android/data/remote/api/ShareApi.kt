package me.wishwith.android.data.remote.api

import me.wishwith.android.data.remote.dto.CreateShareRequest
import me.wishwith.android.data.remote.dto.CreateShareResponse
import me.wishwith.android.data.remote.dto.GrantAccessResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Path

interface ShareApi {
    @POST("api/v1/wishlists/{wishlistId}/share")
    suspend fun createShareLink(
        @Path("wishlistId") wishlistId: String,
        @Body request: CreateShareRequest
    ): CreateShareResponse

    @DELETE("api/v1/wishlists/{wishlistId}/share/{shareId}")
    suspend fun revokeShareLink(
        @Path("wishlistId") wishlistId: String,
        @Path("shareId") shareId: String
    ): Response<Unit>

    @POST("api/v1/shared/{token}/grant-access")
    suspend fun grantAccess(
        @Path("token") token: String
    ): GrantAccessResponse
}
