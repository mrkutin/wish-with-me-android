package me.wishwith.android.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream

object ImageUtils {

    fun resizeAndEncode(
        imageBytes: ByteArray,
        maxSize: Int = 800,
        quality: Int = 70
    ): String? {
        return try {
            val original = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                ?: return null

            val scaled = scaleDown(original, maxSize)
            val outputStream = ByteArrayOutputStream()
            scaled.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            val base64 = Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
            "data:image/jpeg;base64,$base64"
        } catch (e: Exception) {
            null
        }
    }

    fun resizeAvatar(imageBytes: ByteArray): String? {
        return resizeAndEncode(imageBytes, maxSize = 200, quality = 80)
    }

    fun resizeItemImage(imageBytes: ByteArray): String? {
        return resizeAndEncode(imageBytes, maxSize = 800, quality = 70)
    }

    private fun scaleDown(bitmap: Bitmap, maxSize: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        if (width <= maxSize && height <= maxSize) return bitmap

        val ratio = width.toFloat() / height.toFloat()
        val newWidth: Int
        val newHeight: Int
        if (width > height) {
            newWidth = maxSize
            newHeight = (maxSize / ratio).toInt()
        } else {
            newHeight = maxSize
            newWidth = (maxSize * ratio).toInt()
        }
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }

    fun isValidBase64Image(data: String): Boolean {
        return data.startsWith("data:image/jpeg;base64,") ||
                data.startsWith("data:image/png;base64,")
    }
}
