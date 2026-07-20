package me.wishwith.android.data.remote.api

import me.wishwith.android.data.remote.dto.ConnectedAccountsResponse
import me.wishwith.android.data.remote.dto.OAuthLinkInitiateResponse
import me.wishwith.android.data.remote.dto.OAuthProvidersResponse
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface OAuthApi {
    @GET("api/v1/oauth/providers")
    suspend fun getProviders(): OAuthProvidersResponse

    @GET("api/v1/oauth/connected")
    suspend fun getConnectedAccounts(): ConnectedAccountsResponse

    @POST("api/v1/oauth/{provider}/link/initiate")
    suspend fun initiateLink(
        @Path("provider") provider: String
    ): OAuthLinkInitiateResponse

    @DELETE("api/v1/oauth/{provider}/unlink")
    suspend fun unlink(@Path("provider") provider: String): Response<Unit>
}
