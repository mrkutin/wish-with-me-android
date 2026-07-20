package me.wishwith.android.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import me.wishwith.android.data.local.entity.UserEntity

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :id AND soft_deleted = 0")
    suspend fun getById(id: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id AND soft_deleted = 0")
    fun observeById(id: String): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE soft_deleted = 0")
    suspend fun getAll(): List<UserEntity>

    @Query("SELECT id FROM users WHERE soft_deleted = 0")
    suspend fun getAllIds(): List<String>

    @Query("SELECT * FROM users WHERE is_dirty = 1")
    suspend fun getDirty(): List<UserEntity>

    @Upsert
    suspend fun upsert(entity: UserEntity)

    @Query("UPDATE users SET is_dirty = 0, last_synced_at = :syncedAt WHERE id = :id")
    suspend fun markClean(id: String, syncedAt: Long = System.currentTimeMillis())

    @Query("DELETE FROM users WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM users")
    suspend fun deleteAll()
}
