package me.wishwith.android.util

import androidx.compose.ui.graphics.Color

object IconColorMapper {

    private val BrandPrimary = Color(0xFF4F46E5)

    private val colorMap = mapOf(
        "primary" to BrandPrimary,
        "red" to Color.Red,
        "pink" to Color(0xFFE91E63),
        "purple" to Color(0xFF9C27B0),
        "deep-purple" to Color(0xFF673AB7),
        "indigo" to Color(0xFF3F51B5),
        "blue" to Color.Blue,
        "cyan" to Color.Cyan,
        "teal" to Color(0xFF009688),
        "green" to Color.Green,
        "orange" to Color(0xFFFF9800),
        "brown" to Color(0xFF795548),
    )

    val defaultColor = BrandPrimary
    const val defaultColorName = "primary"

    val allColorEntries: List<Pair<String, Color>> = colorMap.entries.map { it.key to it.value }

    fun getColor(name: String?): Color {
        if (name == null) return defaultColor
        return colorMap[name] ?: defaultColor
    }
}
