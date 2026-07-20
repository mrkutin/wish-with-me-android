package me.wishwith.android.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import me.wishwith.android.data.local.entity.ItemEntity

@Dao
interface ItemDao {
    @Query("SELECT * FROM items WHERE id = :id AND soft_deleted = 0")
    suspend fun getById(id: String): ItemEntity?

    @Query("SELECT * FROM items WHERE wishlist_id = :wishlistId AND soft_deleted = 0 ORDER BY created_at DESC")
    fun observeByWishlistId(wishlistId: String): Flow<List<ItemEntity>>

    @Query("SELECT * FROM items WHERE wishlist_id = :wishlistId AND soft_deleted = 0 ORDER BY created_at DESC")
    suspend fun getByWishlistId(wishlistId: String): List<ItemEntity>

    @Query("SELECT COUNT(*) FROM items WHERE wishlist_id = :wishlistId AND soft_deleted = 0")
    suspend fun countByWishlistId(wishlistId: String): Int

    @Query("SELECT COUNT(*) FROM items WHERE wishlist_id = :wishlistId AND soft_deleted = 0")
    fun observeCountByWishlistId(wishlistId: String): Flow<Int>

    @Query("SELECT * FROM items WHERE soft_deleted = 0")
    suspend fun getAll(): List<ItemEntity>

    @Query("SELECT id FROM items WHERE soft_deleted = 0")
    suspend fun getAllIds(): List<String>

    @Query("SELECT * FROM items WHERE is_dirty = 1")
    suspend fun getDirty(): List<ItemEntity>

    @Upsert
    suspend fun upsert(entity: ItemEntity)

    @Query("UPDATE items SET is_dirty = 0, last_synced_at = :syncedAt WHERE id = :id")
    suspend fun markClean(id: String, syncedAt: Long = System.currentTimeMillis())

    @Query("DELETE FROM items WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM items WHERE wishlist_id = :wishlistId")
    suspend fun deleteByWishlistId(wishlistId: String)

    @Query("DELETE FROM items")
    suspend fun deleteAll()
}
