package me.wishwith.android.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shares")
data class ShareEntity(
    @PrimaryKey
    val id: String,
    val rev: String? = null,
    @ColumnInfo(name = "wishlist_id")
    val wishlistId: String,
    @ColumnInfo(name = "owner_id")
    val ownerId: String,
    val token: String,
    @ColumnInfo(name = "link_type")
    val linkType: String,
    @ColumnInfo(name = "expires_at")
    val expiresAt: String? = null,
    @ColumnInfo(name = "access_count")
    val accessCount: Int = 0,
    val revoked: Boolean = false,
    @ColumnInfo(name = "granted_users")
    val grantedUsers: List<String> = emptyList(),
    val access: List<String> = emptyList(),
    @ColumnInfo(name = "created_at")
    val createdAt: String = "",
    @ColumnInfo(name = "updated_at")
    val updatedAt: String? = null,
    @ColumnInfo(name = "qr_code_base64")
    val qrCodeBase64: String? = null,
    @ColumnInfo(name = "is_dirty")
    val isDirty: Boolean = false,
    @ColumnInfo(name = "soft_deleted")
    val softDeleted: Boolean = false,
    @ColumnInfo(name = "last_synced_at")
    val lastSyncedAt: Long? = null
)
