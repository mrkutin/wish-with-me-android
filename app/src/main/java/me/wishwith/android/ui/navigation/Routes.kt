package me.wishwith.android.ui.navigation

sealed class Routes(val route: String) {
    object Login : Routes("login")
    object Register : Routes("register")
    object Home : Routes("home")
    object Wishlists : Routes("wishlists")
    object WishlistDetail : Routes("wishlist/{wishlistId}") {
        fun createRoute(wishlistId: String) = "wishlist/$wishlistId"
    }
    object SharedBookmarks : Routes("shared_bookmarks")
    object SharedWishlist : Routes("shared_wishlist/{wishlistId}") {
        fun createRoute(wishlistId: String) = "shared_wishlist/$wishlistId"
    }
    object Profile : Routes("profile")
    object Settings : Routes("settings")
}

enum class MainTab(val route: String) {
    HOME("home"),
    WISHLISTS("wishlists"),
    SHARED("shared_bookmarks"),
    PROFILE("profile")
}
