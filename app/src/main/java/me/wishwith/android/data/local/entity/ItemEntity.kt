package me.wishwith.android.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "items")
data class ItemEntity(
    @PrimaryKey
    val id: String,
    val rev: String? = null,
    @ColumnInfo(name = "wishlist_id")
    val wishlistId: String,
    @ColumnInfo(name = "owner_id")
    val ownerId: String,
    val title: String,
    val description: String? = null,
    val price: Double? = null,
    val currency: String? = null,
    val quantity: Int = 1,
    @ColumnInfo(name = "source_url")
    val sourceUrl: String? = null,
    @ColumnInfo(name = "image_url")
    val imageUrl: String? = null,
    @ColumnInfo(name = "image_base64")
    val imageBase64: String? = null,
    val status: String = "resolved",
    @ColumnInfo(name = "resolve_confidence")
    val resolveConfidence: Double? = null,
    @ColumnInfo(name = "resolve_error")
    val resolveError: String? = null,
    @ColumnInfo(name = "resolved_at")
    val resolvedAt: String? = null,
    val access: List<String> = emptyList(),
    @ColumnInfo(name = "created_at")
    val createdAt: String = "",
    @ColumnInfo(name = "updated_at")
    val updatedAt: String = "",
    @ColumnInfo(name = "is_dirty")
    val isDirty: Boolean = false,
    @ColumnInfo(name = "soft_deleted")
    val softDeleted: Boolean = false,
    @ColumnInfo(name = "last_synced_at")
    val lastSyncedAt: Long? = null
)
