package me.wishwith.android.data.remote.api

import me.wishwith.android.data.remote.dto.SyncPullResponse
import me.wishwith.android.data.remote.dto.SyncPushRequest
import me.wishwith.android.data.remote.dto.SyncPushResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface SyncApi {
    @POST("api/v2/sync/push/{collection}")
    suspend fun push(
        @Path("collection") collection: String,
        @Body request: SyncPushRequest
    ): SyncPushResponse

    @GET("api/v2/sync/pull/{collection}")
    suspend fun pull(
        @Path("collection") collection: String
    ): SyncPullResponse
}
