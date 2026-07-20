package me.wishwith.android.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wishlists")
data class WishlistEntity(
    @PrimaryKey
    val id: String,
    val rev: String? = null,
    @ColumnInfo(name = "owner_id")
    val ownerId: String,
    val name: String,
    val description: String? = null,
    val icon: String = "card_giftcard",
    @ColumnInfo(name = "icon_color")
    val iconColor: String? = null,
    @ColumnInfo(name = "is_public")
    val isPublic: Boolean = false,
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
