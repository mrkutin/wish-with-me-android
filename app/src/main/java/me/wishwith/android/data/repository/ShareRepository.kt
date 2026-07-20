package me.wishwith.android.data.repository

import kotlinx.coroutines.flow.Flow
import me.wishwith.android.data.local.dao.ShareDao
import me.wishwith.android.data.local.entity.ShareEntity
import me.wishwith.android.data.remote.api.ShareApi
import me.wishwith.android.data.remote.dto.CreateShareRequest
import me.wishwith.android.data.remote.dto.CreateShareResponse
import me.wishwith.android.data.remote.dto.GrantAccessResponse
import me.wishwith.android.domain.sync.SyncEngine
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShareRepository @Inject constructor(
    private val shareApi: ShareApi,
    private val shareDao: ShareDao,
    private val syncEngine: SyncEngine
) {
    fun observeByWishlistId(wishlistId: String): Flow<List<ShareEntity>> =
        shareDao.observeByWishlistId(wishlistId)

    suspend fun createShareLink(wishlistId: String, linkType: String): CreateShareResponse {
        val response = shareApi.createShareLink(wishlistId, CreateShareRequest(linkType))
        syncEngine.fullSync()
        return response
    }

    suspend fun revokeShareLink(wishlistId: String, shareId: String) {
        shareApi.revokeShareLink(wishlistId, shareId)
        syncEngine.fullSync()
    }

    suspend fun grantAccess(token: String): GrantAccessResponse {
        val response = shareApi.grantAccess(token)
        syncEngine.fullSync()
        return response
    }
}
