package me.wishwith.android.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import me.wishwith.android.data.local.entity.MarkEntity

@Dao
interface MarkDao {
    @Query("SELECT * FROM marks WHERE id = :id AND soft_deleted = 0")
    suspend fun getById(id: String): MarkEntity?

    @Query("SELECT * FROM marks WHERE wishlist_id = :wishlistId AND soft_deleted = 0")
    fun observeByWishlistId(wishlistId: String): Flow<List<MarkEntity>>

    @Query("SELECT * FROM marks WHERE wishlist_id = :wishlistId AND soft_deleted = 0")
    suspend fun getByWishlistId(wishlistId: String): List<MarkEntity>

    @Query("SELECT * FROM marks WHERE item_id = :itemId AND soft_deleted = 0")
    suspend fun getByItemId(itemId: String): List<MarkEntity>

    @Query("SELECT * FROM marks WHERE soft_deleted = 0")
    suspend fun getAll(): List<MarkEntity>

    @Query("SELECT id FROM marks WHERE soft_deleted = 0")
    suspend fun getAllIds(): List<String>

    @Query("SELECT * FROM marks WHERE is_dirty = 1")
    suspend fun getDirty(): List<MarkEntity>

    @Upsert
    suspend fun upsert(entity: MarkEntity)

    @Query("UPDATE marks SET is_dirty = 0, last_synced_at = :syncedAt WHERE id = :id")
    suspend fun markClean(id: String, syncedAt: Long = System.currentTimeMillis())

    @Query("DELETE FROM marks WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM marks WHERE wishlist_id = :wishlistId")
    suspend fun deleteByWishlistId(wishlistId: String)

    @Query("DELETE FROM marks")
    suspend fun deleteAll()
}
