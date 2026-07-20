package me.wishwith.android.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class SyncPushRequest(
    val documents: List<Map<String, JsonElement>>
)

@Serializable
data class SyncPushResponse(
    val conflicts: List<SyncConflict> = emptyList()
)

@Serializable
data class SyncConflict(
    @SerialName("document_id")
    val documentId: String,
    val error: String? = null,
    @SerialName("server_document")
    val serverDocument: Map<String, JsonElement>? = null
)

@Serializable
data class SyncPullResponse(
    val documents: List<Map<String, JsonElement>> = emptyList()
)
