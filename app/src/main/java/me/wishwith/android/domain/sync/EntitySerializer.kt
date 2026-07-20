package me.wishwith.android.domain.sync

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import me.wishwith.android.data.local.entity.BookmarkEntity
import me.wishwith.android.data.local.entity.ItemEntity
import me.wishwith.android.data.local.entity.MarkEntity
import me.wishwith.android.data.local.entity.ShareEntity
import me.wishwith.android.data.local.entity.UserEntity
import me.wishwith.android.data.local.entity.WishlistEntity

object EntitySerializer {

    // -- Helpers --

    private fun str(e: JsonElement?): String = e?.jsonPrimitive?.contentOrNull ?: ""
    private fun strN(e: JsonElement?): String? = e?.jsonPrimitive?.contentOrNull
    private fun bool(e: JsonElement?, default: Boolean = false): Boolean =
        e?.jsonPrimitive?.booleanOrNull ?: default
    private fun dbl(e: JsonElement?): Double? = e?.jsonPrimitive?.doubleOrNull
    private fun intVal(e: JsonElement?): Int = e?.jsonPrimitive?.intOrNull ?: 0
    private fun strList(e: JsonElement?): List<String> {
        return try {
            e?.jsonArray?.map { it.jsonPrimitive.content } ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }

    /** Handle quantity quirk: server may send boolean instead of int */
    private fun quantity(e: JsonElement?): Int {
        if (e == null || e is JsonNull) return 1
        val prim = e.jsonPrimitive
        prim.intOrNull?.let { return it }
        prim.booleanOrNull?.let { return if (it) 1 else 0 }
        return 1
    }

    private fun jp(value: String?): JsonElement = if (value != null) JsonPrimitive(value) else JsonNull
    private fun jp(value: Int): JsonElement = JsonPrimitive(value)
    private fun jp(value: Double?): JsonElement = if (value != null) JsonPrimitive(value) else JsonNull
    private fun jp(value: Boolean): JsonElement = JsonPrimitive(value)
    private fun jpList(value: List<String>): JsonElement =
        JsonArray(value.map { JsonPrimitive(it) })

    // -- Wishlist --

    fun wishlistToMap(entity: WishlistEntity): Map<String, JsonElement> = buildMap {
        put("_id", jp(entity.id))
        if (entity.rev != null) put("_rev", jp(entity.rev))
        put("type", jp("wishlist"))
        put("owner_id", jp(entity.ownerId))
        put("name", jp(entity.name))
        put("description", jp(entity.description))
        put("icon", jp(entity.icon))
        put("icon_color", jp(entity.iconColor))
        put("is_public", jp(entity.isPublic))
        put("access", jpList(entity.access))
        put("created_at", jp(entity.createdAt))
        put("updated_at", jp(entity.updatedAt))
        if (entity.softDeleted) put("_deleted", jp(true))
    }

    fun mapToWishlist(map: Map<String, JsonElement>): WishlistEntity = WishlistEntity(
        id = str(map["_id"]),
        rev = strN(map["_rev"]),
        ownerId = str(map["owner_id"]),
        name = str(map["name"]),
        description = strN(map["description"]),
        icon = strN(map["icon"]) ?: "card_giftcard",
        iconColor = strN(map["icon_color"]),
        isPublic = bool(map["is_public"]),
        access = strList(map["access"]),
        createdAt = str(map["created_at"]),
        updatedAt = str(map["updated_at"]),
        softDeleted = bool(map["_deleted"]),
        isDirty = false,
        lastSyncedAt = System.currentTimeMillis()
    )

    // -- Item --

    fun itemToMap(entity: ItemEntity): Map<String, JsonElement> = buildMap {
        put("_id", jp(entity.id))
        if (entity.rev != null) put("_rev", jp(entity.rev))
        put("type", jp("item"))
        put("wishlist_id", jp(entity.wishlistId))
        put("owner_id", jp(entity.ownerId))
        put("title", jp(entity.title))
        put("description", jp(entity.description))
        put("price", jp(entity.price))
        put("currency", jp(entity.currency))
        put("quantity", jp(entity.quantity))
        put("source_url", jp(entity.sourceUrl))
        put("image_url", jp(entity.imageUrl))
        put("image_base64", jp(entity.imageBase64))
        put("status", jp(entity.status))
        put("resolve_confidence", jp(entity.resolveConfidence))
        put("resolve_error", jp(entity.resolveError))
        put("resolved_at", jp(entity.resolvedAt))
        put("access", jpList(entity.access))
        put("created_at", jp(entity.createdAt))
        put("updated_at", jp(entity.updatedAt))
        if (entity.softDeleted) put("_deleted", jp(true))
    }

    fun mapToItem(map: Map<String, JsonElement>): ItemEntity = ItemEntity(
        id = str(map["_id"]),
        rev = strN(map["_rev"]),
        wishlistId = str(map["wishlist_id"]),
        ownerId = str(map["owner_id"]),
        title = str(map["title"]),
        description = strN(map["description"]),
        price = dbl(map["price"]),
        currency = strN(map["currency"]),
        quantity = quantity(map["quantity"]),
        sourceUrl = strN(map["source_url"]),
        imageUrl = strN(map["image_url"]),
        imageBase64 = strN(map["image_base64"]),
        status = strN(map["status"]) ?: "resolved",
        resolveConfidence = dbl(map["resolve_confidence"]),
        resolveError = strN(map["resolve_error"]),
        resolvedAt = strN(map["resolved_at"]),
        access = strList(map["access"]),
        createdAt = str(map["created_at"]),
        updatedAt = str(map["updated_at"]),
        softDeleted = bool(map["_deleted"]),
        isDirty = false,
        lastSyncedAt = System.currentTimeMillis()
    )

    // -- Mark --

    fun markToMap(entity: MarkEntity): Map<String, JsonElement> = buildMap {
        put("_id", jp(entity.id))
        if (entity.rev != null) put("_rev", jp(entity.rev))
        put("type", jp("mark"))
        put("item_id", jp(entity.itemId))
        put("wishlist_id", jp(entity.wishlistId))
        put("owner_id", jp(entity.ownerId))
        put("marked_by", jp(entity.markedBy))
        put("quantity", jp(entity.quantity))
        put("access", jpList(entity.access))
        put("created_at", jp(entity.createdAt))
        put("updated_at", jp(entity.updatedAt))
        if (entity.softDeleted) put("_deleted", jp(true))
    }

    fun mapToMark(map: Map<String, JsonElement>): MarkEntity = MarkEntity(
        id = str(map["_id"]),
        rev = strN(map["_rev"]),
        itemId = str(map["item_id"]),
        wishlistId = str(map["wishlist_id"]),
        ownerId = str(map["owner_id"]),
        markedBy = str(map["marked_by"]),
        quantity = quantity(map["quantity"]),
        access = strList(map["access"]),
        createdAt = str(map["created_at"]),
        updatedAt = str(map["updated_at"]),
        softDeleted = bool(map["_deleted"]),
        isDirty = false,
        lastSyncedAt = System.currentTimeMillis()
    )

    // -- User --

    fun userToMap(entity: UserEntity): Map<String, JsonElement> = buildMap {
        put("_id", jp(entity.id))
        if (entity.rev != null) put("_rev", jp(entity.rev))
        put("type", jp("user"))
        put("email", jp(entity.email))
        put("name", jp(entity.name))
        put("avatar_base64", jp(entity.avatarBase64))
        put("bio", jp(entity.bio))
        put("public_url_slug", jp(entity.publicUrlSlug))
        put("locale", jp(entity.locale))
        put("birthday", jp(entity.birthday))
        put("access", jpList(entity.access))
        put("created_at", jp(entity.createdAt))
        put("updated_at", jp(entity.updatedAt))
        if (entity.softDeleted) put("_deleted", jp(true))
    }

    fun mapToUser(map: Map<String, JsonElement>): UserEntity = UserEntity(
        id = str(map["_id"]),
        rev = strN(map["_rev"]),
        email = str(map["email"]),
        name = str(map["name"]),
        avatarBase64 = strN(map["avatar_base64"]),
        bio = strN(map["bio"]),
        publicUrlSlug = strN(map["public_url_slug"]),
        locale = strN(map["locale"]) ?: "en",
        birthday = strN(map["birthday"]),
        access = strList(map["access"]),
        createdAt = str(map["created_at"]),
        updatedAt = str(map["updated_at"]),
        softDeleted = bool(map["_deleted"]),
        isDirty = false,
        lastSyncedAt = System.currentTimeMillis()
    )

    // -- Share --

    fun shareToMap(entity: ShareEntity): Map<String, JsonElement> = buildMap {
        put("_id", jp(entity.id))
        if (entity.rev != null) put("_rev", jp(entity.rev))
        put("type", jp("share"))
        put("wishlist_id", jp(entity.wishlistId))
        put("owner_id", jp(entity.ownerId))
        put("token", jp(entity.token))
        put("link_type", jp(entity.linkType))
        put("expires_at", jp(entity.expiresAt))
        put("access_count", jp(entity.accessCount))
        put("revoked", jp(entity.revoked))
        put("granted_users", jpList(entity.grantedUsers))
        put("access", jpList(entity.access))
        put("created_at", jp(entity.createdAt))
        put("updated_at", jp(entity.updatedAt))
        put("qr_code_base64", jp(entity.qrCodeBase64))
        if (entity.softDeleted) put("_deleted", jp(true))
    }

    fun mapToShare(map: Map<String, JsonElement>): ShareEntity = ShareEntity(
        id = str(map["_id"]),
        rev = strN(map["_rev"]),
        wishlistId = str(map["wishlist_id"]),
        ownerId = str(map["owner_id"]),
        token = str(map["token"]),
        linkType = str(map["link_type"]),
        expiresAt = strN(map["expires_at"]),
        accessCount = intVal(map["access_count"]),
        revoked = bool(map["revoked"]),
        grantedUsers = strList(map["granted_users"]),
        access = strList(map["access"]),
        createdAt = str(map["created_at"]),
        updatedAt = strN(map["updated_at"]),
        qrCodeBase64 = strN(map["qr_code_base64"]),
        softDeleted = bool(map["_deleted"]),
        isDirty = false,
        lastSyncedAt = System.currentTimeMillis()
    )

    // -- Bookmark --

    fun bookmarkToMap(entity: BookmarkEntity): Map<String, JsonElement> = buildMap {
        put("_id", jp(entity.id))
        if (entity.rev != null) put("_rev", jp(entity.rev))
        put("type", jp("bookmark"))
        put("user_id", jp(entity.userId))
        put("share_id", jp(entity.shareId))
        put("wishlist_id", jp(entity.wishlistId))
        put("owner_name", jp(entity.ownerName))
        put("owner_avatar_base64", jp(entity.ownerAvatarBase64))
        put("wishlist_name", jp(entity.wishlistName))
        put("wishlist_icon", jp(entity.wishlistIcon))
        put("wishlist_icon_color", jp(entity.wishlistIconColor))
        put("access", jpList(entity.access))
        put("created_at", jp(entity.createdAt))
        put("updated_at", jp(entity.updatedAt))
        put("last_accessed_at", jp(entity.lastAccessedAt))
        if (entity.softDeleted) put("_deleted", jp(true))
    }

    fun mapToBookmark(map: Map<String, JsonElement>): BookmarkEntity = BookmarkEntity(
        id = str(map["_id"]),
        rev = strN(map["_rev"]),
        userId = str(map["user_id"]),
        shareId = str(map["share_id"]),
        wishlistId = strN(map["wishlist_id"]),
        ownerName = strN(map["owner_name"]),
        ownerAvatarBase64 = strN(map["owner_avatar_base64"]),
        wishlistName = strN(map["wishlist_name"]),
        wishlistIcon = strN(map["wishlist_icon"]),
        wishlistIconColor = strN(map["wishlist_icon_color"]),
        access = strList(map["access"]),
        createdAt = str(map["created_at"]),
        updatedAt = str(map["updated_at"]),
        lastAccessedAt = str(map["last_accessed_at"]),
        softDeleted = bool(map["_deleted"]),
        isDirty = false,
        lastSyncedAt = System.currentTimeMillis()
    )
}
