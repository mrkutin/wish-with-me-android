package me.wishwith.android.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import me.wishwith.android.data.local.entity.BookmarkEntity

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM bookmarks WHERE id = :id AND soft_deleted = 0")
    suspend fun getById(id: String): BookmarkEntity?

    @Query("SELECT * FROM bookmarks WHERE user_id = :userId AND soft_deleted = 0 ORDER BY last_accessed_at DESC")
    fun observeByUserId(userId: String): Flow<List<BookmarkEntity>>

    @Query("SELECT * FROM bookmarks WHERE user_id = :userId AND soft_deleted = 0 ORDER BY last_accessed_at DESC")
    suspend fun getByUserId(userId: String): List<BookmarkEntity>

    @Query("SELECT * FROM bookmarks WHERE wishlist_id = :wishlistId AND user_id = :userId AND soft_deleted = 0 LIMIT 1")
    suspend fun getByWishlistAndUser(wishlistId: String, userId: String): BookmarkEntity?

    @Query("SELECT * FROM bookmarks WHERE soft_deleted = 0")
    suspend fun getAll(): List<BookmarkEntity>

    @Query("SELECT id FROM bookmarks WHERE soft_deleted = 0")
    suspend fun getAllIds(): List<String>

    @Query("SELECT * FROM bookmarks WHERE is_dirty = 1")
    suspend fun getDirty(): List<BookmarkEntity>

    @Upsert
    suspend fun upsert(entity: BookmarkEntity)

    @Query("UPDATE bookmarks SET is_dirty = 0, last_synced_at = :syncedAt WHERE id = :id")
    suspend fun markClean(id: String, syncedAt: Long = System.currentTimeMillis())

    @Query("DELETE FROM bookmarks WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM bookmarks")
    suspend fun deleteAll()
}
