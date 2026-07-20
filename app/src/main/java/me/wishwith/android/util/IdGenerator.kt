package me.wishwith.android.util

import java.util.UUID

object IdGenerator {

    fun create(type: String): String {
        val uuid = UUID.randomUUID().toString().lowercase()
        return "$type:$uuid"
    }

    fun extractType(docId: String): String {
        return docId.substringBefore(":")
    }

    fun extractUuid(docId: String): String {
        return docId.substringAfter(":")
    }
}
