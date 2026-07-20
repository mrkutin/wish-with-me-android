package me.wishwith.android.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey
    val id: String,
    val rev: String? = null,
    @ColumnInfo(name = "user_id")
    val userId: String,
    @ColumnInfo(name = "share_id")
    val shareId: String,
    @ColumnInfo(name = "wishlist_id")
    val wishlistId: String? = null,
    @ColumnInfo(name = "owner_name")
    val ownerName: String? = null,
    @ColumnInfo(name = "owner_avatar_base64")
    val ownerAvatarBase64: String? = null,
    @ColumnInfo(name = "wishlist_name")
    val wishlistName: String? = null,
    @ColumnInfo(name = "wishlist_icon")
    val wishlistIcon: String? = null,
    @ColumnInfo(name = "wishlist_icon_color")
    val wishlistIconColor: String? = null,
    val access: List<String> = emptyList(),
    @ColumnInfo(name = "created_at")
    val createdAt: String = "",
    @ColumnInfo(name = "updated_at")
    val updatedAt: String = "",
    @ColumnInfo(name = "last_accessed_at")
    val lastAccessedAt: String = "",
    @ColumnInfo(name = "is_dirty")
    val isDirty: Boolean = false,
    @ColumnInfo(name = "soft_deleted")
    val softDeleted: Boolean = false,
    @ColumnInfo(name = "last_synced_at")
    val lastSyncedAt: Long? = null
)
