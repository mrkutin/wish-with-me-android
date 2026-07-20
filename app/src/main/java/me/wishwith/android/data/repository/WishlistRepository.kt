package me.wishwith.android.data.repository

import kotlinx.coroutines.flow.Flow
import me.wishwith.android.data.local.dao.ItemDao
import me.wishwith.android.data.local.dao.MarkDao
import me.wishwith.android.data.local.dao.WishlistDao
import me.wishwith.android.data.local.entity.WishlistEntity
import me.wishwith.android.domain.sync.SyncEngine
import me.wishwith.android.util.IdGenerator
import me.wishwith.android.util.TokenManager
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WishlistRepository @Inject constructor(
    private val wishlistDao: WishlistDao,
    private val itemDao: ItemDao,
    private val markDao: MarkDao,
    private val tokenManager: TokenManager,
    private val syncEngine: SyncEngine
) {
    fun observeMyWishlists(): Flow<List<WishlistEntity>> {
        val userId = tokenManager.currentUserId ?: return kotlinx.coroutines.flow.flowOf(emptyList())
        return wishlistDao.observeByOwnerId(userId)
    }

    suspend fun getById(id: String): WishlistEntity? = wishlistDao.getById(id)

    fun observeById(id: String): Flow<WishlistEntity?> = wishlistDao.observeById(id)

    suspend fun create(name: String, description: String?, icon: String, iconColor: String?): WishlistEntity {
        val userId = tokenManager.currentUserId!!
        val now = Instant.now().toString()
        val entity = WishlistEntity(
            id = IdGenerator.create("wishlist"),
            ownerId = userId,
            name = name,
            description = description,
            icon = icon,
            iconColor = iconColor,
            access = listOf(userId),
            createdAt = now,
            updatedAt = now,
            isDirty = true
        )
        wishlistDao.upsert(entity)
        syncEngine.triggerSync()
        return entity
    }

    suspend fun update(wishlist: WishlistEntity, name: String, description: String?, icon: String, iconColor: String?) {
        val updated = wishlist.copy(
            name = name,
            description = description,
            icon = icon,
            iconColor = iconColor,
            updatedAt = Instant.now().toString(),
            isDirty = true
        )
        wishlistDao.upsert(updated)
        syncEngine.triggerSync()
    }

    suspend fun delete(wishlist: WishlistEntity) {
        // Soft delete wishlist
        wishlistDao.upsert(wishlist.copy(softDeleted = true, isDirty = true, updatedAt = Instant.now().toString()))
        // Cascade soft delete items
        val items = itemDao.getByWishlistId(wishlist.id)
        items.forEach { item ->
            itemDao.upsert(item.copy(softDeleted = true, isDirty = true, updatedAt = Instant.now().toString()))
        }
        // Cascade soft delete marks
        val marks = markDao.getByWishlistId(wishlist.id)
        marks.forEach { mark ->
            markDao.upsert(mark.copy(softDeleted = true, isDirty = true, updatedAt = Instant.now().toString()))
        }
        syncEngine.triggerSync()
    }
}
