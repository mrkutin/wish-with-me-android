package me.wishwith.android.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import me.wishwith.android.data.local.entity.ShareEntity

@Dao
interface ShareDao {
    @Query("SELECT * FROM shares WHERE id = :id AND soft_deleted = 0")
    suspend fun getById(id: String): ShareEntity?

    @Query("SELECT * FROM shares WHERE wishlist_id = :wishlistId AND soft_deleted = 0 AND revoked = 0 ORDER BY created_at DESC")
    fun observeByWishlistId(wishlistId: String): Flow<List<ShareEntity>>

    @Query("SELECT * FROM shares WHERE wishlist_id = :wishlistId AND soft_deleted = 0 AND revoked = 0")
    suspend fun getByWishlistId(wishlistId: String): List<ShareEntity>

    @Query("SELECT * FROM shares WHERE soft_deleted = 0")
    suspend fun getAll(): List<ShareEntity>

    @Query("SELECT id FROM shares WHERE soft_deleted = 0")
    suspend fun getAllIds(): List<String>

    @Query("SELECT * FROM shares WHERE is_dirty = 1")
    suspend fun getDirty(): List<ShareEntity>

    @Upsert
    suspend fun upsert(entity: ShareEntity)

    @Query("UPDATE shares SET is_dirty = 0, last_synced_at = :syncedAt WHERE id = :id")
    suspend fun markClean(id: String, syncedAt: Long = System.currentTimeMillis())

    @Query("DELETE FROM shares WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM shares")
    suspend fun deleteAll()
}
