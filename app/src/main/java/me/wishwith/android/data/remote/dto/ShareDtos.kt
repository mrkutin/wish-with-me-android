package me.wishwith.android.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateShareRequest(
    @SerialName("link_type")
    val linkType: String
)

@Serializable
data class CreateShareResponse(
    val id: String,
    @SerialName("wishlist_id")
    val wishlistId: String,
    val token: String,
    @SerialName("link_type")
    val linkType: String,
    @SerialName("expires_at")
    val expiresAt: String? = null,
    @SerialName("access_count")
    val accessCount: Int = 0,
    @SerialName("created_at")
    val createdAt: String = "",
    @SerialName("share_url")
    val shareUrl: String = "",
    @SerialName("qr_code_base64")
    val qrCodeBase64: String? = null
)

@Serializable
data class GrantAccessResponse(
    @SerialName("wishlist_id")
    val wishlistId: String,
    val permissions: List<String> = emptyList()
)
