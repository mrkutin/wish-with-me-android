package me.wishwith.android.data.repository

import kotlinx.coroutines.flow.Flow
import me.wishwith.android.data.local.dao.ItemDao
import me.wishwith.android.data.local.dao.WishlistDao
import me.wishwith.android.data.local.entity.ItemEntity
import me.wishwith.android.domain.sync.SyncEngine
import me.wishwith.android.util.IdGenerator
import me.wishwith.android.util.TokenManager
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ItemRepository @Inject constructor(
    private val itemDao: ItemDao,
    private val wishlistDao: WishlistDao,
    private val tokenManager: TokenManager,
    private val syncEngine: SyncEngine
) {
    fun observeByWishlistId(wishlistId: String): Flow<List<ItemEntity>> =
        itemDao.observeByWishlistId(wishlistId)

    suspend fun countByWishlistId(wishlistId: String): Int =
        itemDao.countByWishlistId(wishlistId)

    fun observeCountByWishlistId(wishlistId: String): Flow<Int> =
        itemDao.observeCountByWishlistId(wishlistId)

    suspend fun createByUrl(wishlistId: String, url: String): ItemEntity {
        val userId = tokenManager.currentUserId!!
        val wishlist = wishlistDao.getById(wishlistId)
        val now = Instant.now().toString()
        val entity = ItemEntity(
            id = IdGenerator.create("item"),
            wishlistId = wishlistId,
            ownerId = userId,
            title = url,
            sourceUrl = url,
            status = "pending",
            access = wishlist?.access ?: listOf(userId),
            createdAt = now,
            updatedAt = now,
            isDirty = true
        )
        itemDao.upsert(entity)
        syncEngine.triggerSync()
        return entity
    }

    suspend fun createManually(
        wishlistId: String,
        title: String,
        description: String?,
        price: Double?,
        currency: String?,
        quantity: Int,
        sourceUrl: String?,
        imageBase64: String?
    ): ItemEntity {
        val userId = tokenManager.currentUserId!!
        val wishlist = wishlistDao.getById(wishlistId)
        val now = Instant.now().toString()
        val entity = ItemEntity(
            id = IdGenerator.create("item"),
            wishlistId = wishlistId,
            ownerId = userId,
            title = title,
            description = description,
            price = price,
            currency = currency,
            quantity = quantity,
            sourceUrl = sourceUrl,
            imageBase64 = imageBase64,
            status = "resolved",
            access = wishlist?.access ?: listOf(userId),
            createdAt = now,
            updatedAt = now,
            isDirty = true
        )
        itemDao.upsert(entity)
        syncEngine.triggerSync()
        return entity
    }

    suspend fun update(
        item: ItemEntity,
        title: String,
        description: String?,
        price: Double?,
        currency: String?,
        quantity: Int,
        sourceUrl: String?,
        imageBase64: String?
    ) {
        val updated = item.copy(
            title = title,
            description = description,
            price = price,
            currency = currency,
            quantity = quantity,
            sourceUrl = sourceUrl,
            imageBase64 = imageBase64,
            updatedAt = Instant.now().toString(),
            isDirty = true
        )
        itemDao.upsert(updated)
        syncEngine.triggerSync()
    }

    suspend fun delete(item: ItemEntity) {
        itemDao.upsert(item.copy(softDeleted = true, isDirty = true, updatedAt = Instant.now().toString()))
        syncEngine.triggerSync()
    }
}
