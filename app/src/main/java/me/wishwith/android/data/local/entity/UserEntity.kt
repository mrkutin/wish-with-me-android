package me.wishwith.android.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String,
    val rev: String? = null,
    val email: String,
    val name: String,
    @ColumnInfo(name = "avatar_base64")
    val avatarBase64: String? = null,
    val bio: String? = null,
    @ColumnInfo(name = "public_url_slug")
    val publicUrlSlug: String? = null,
    val locale: String = "en",
    val birthday: String? = null,
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
