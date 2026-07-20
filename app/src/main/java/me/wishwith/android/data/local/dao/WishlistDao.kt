package me.wishwith.android.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import me.wishwith.android.data.local.entity.WishlistEntity

@Dao
interface WishlistDao {
    @Query("SELECT * FROM wishlists WHERE id = :id AND soft_deleted = 0")
    suspend fun getById(id: String): WishlistEntity?

    @Query("SELECT * FROM wishlists WHERE id = :id AND soft_deleted = 0")
    fun observeById(id: String): Flow<WishlistEntity?>

    @Query("SELECT * FROM wishlists WHERE owner_id = :ownerId AND soft_deleted = 0 ORDER BY updated_at DESC")
    fun observeByOwnerId(ownerId: String): Flow<List<WishlistEntity>>

    @Query("SELECT * FROM wishlists WHERE owner_id = :ownerId AND soft_deleted = 0 ORDER BY updated_at DESC")
    suspend fun getByOwnerId(ownerId: String): List<WishlistEntity>

    @Query("SELECT * FROM wishlists WHERE soft_deleted = 0")
    suspend fun getAll(): List<WishlistEntity>

    @Query("SELECT id FROM wishlists WHERE soft_deleted = 0")
    suspend fun getAllIds(): List<String>

    @Query("SELECT * FROM wishlists WHERE is_dirty = 1")
    suspend fun getDirty(): List<WishlistEntity>

    @Upsert
    suspend fun upsert(entity: WishlistEntity)

    @Query("UPDATE wishlists SET is_dirty = 0, last_synced_at = :syncedAt WHERE id = :id")
    suspend fun markClean(id: String, syncedAt: Long = System.currentTimeMillis())

    @Query("DELETE FROM wishlists WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM wishlists")
    suspend fun deleteAll()
}
