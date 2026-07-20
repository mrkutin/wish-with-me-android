package me.wishwith.android.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Redeem
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

object IconMapper {

    private val iconMap = mapOf(
        "card_giftcard" to Icons.Filled.CardGiftcard,
        "checklist" to Icons.Filled.Checklist,
        "celebration" to Icons.Filled.Celebration,
        "cake" to Icons.Filled.Cake,
        "favorite" to Icons.Filled.Favorite,
        "star" to Icons.Filled.Star,
        "redeem" to Icons.Filled.Redeem,
        "shopping_bag" to Icons.Filled.ShoppingBag,
        "shopping_cart" to Icons.Filled.ShoppingCart,
        "home" to Icons.Filled.Home,
        "flight" to Icons.Filled.Flight,
        "child_care" to Icons.Filled.ChildCare,
        "pets" to Icons.Filled.Pets,
        "devices" to Icons.Filled.Devices,
        "checkroom" to Icons.Filled.Checkroom,
        "auto_stories" to Icons.Filled.AutoStories,
        "sports_esports" to Icons.Filled.SportsEsports,
        "palette" to Icons.Filled.Palette,
        "music_note" to Icons.Filled.MusicNote,
        "restaurant" to Icons.Filled.Restaurant,
        "fitness_center" to Icons.Filled.FitnessCenter,
        "photo_camera" to Icons.Filled.PhotoCamera,
        "spa" to Icons.Filled.Spa,
        "directions_car" to Icons.Filled.DirectionsCar,
        "diamond" to Icons.Filled.Diamond,
    )

    val defaultIcon = Icons.Filled.CardGiftcard
    const val defaultIconName = "card_giftcard"

    val allIconNames: List<String> = iconMap.keys.toList()

    fun getIcon(name: String?): ImageVector {
        if (name == null) return defaultIcon
        return iconMap[name] ?: defaultIcon
    }
}
