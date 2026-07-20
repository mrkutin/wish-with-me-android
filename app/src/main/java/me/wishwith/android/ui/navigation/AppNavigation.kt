package me.wishwith.android.ui.navigation

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import me.wishwith.android.ui.auth.AuthViewModel
import me.wishwith.android.ui.auth.LoginScreen
import me.wishwith.android.ui.auth.RegisterScreen
import me.wishwith.android.ui.components.FloatingTabBar
import me.wishwith.android.ui.components.OfflineBanner
import me.wishwith.android.ui.home.HomeScreen
import me.wishwith.android.ui.profile.ProfileScreen
import me.wishwith.android.ui.profile.SettingsScreen
import me.wishwith.android.ui.shared.SharedBookmarksScreen
import me.wishwith.android.ui.shared.SharedWishlistScreen
import me.wishwith.android.ui.wishlists.WishlistDetailScreen
import me.wishwith.android.ui.wishlists.WishlistsScreen
import me.wishwith.android.util.OAuthHelper

@Composable
fun AppNavigation(
    deepLinkIntent: Intent? = null,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()
    val isOffline by authViewModel.isOffline.collectAsState()
    var isTabBarCollapsed by rememberSaveable { mutableStateOf(false) }
    var pendingShareToken by rememberSaveable { mutableStateOf<String?>(null) }
    var lastConsumedDeepLink by rememberSaveable { mutableStateOf<String?>(null) }

    // Handle deep links
    LaunchedEffect(deepLinkIntent) {
        val uri = deepLinkIntent?.data ?: return@LaunchedEffect
        val uriString = uri.toString()
        if (uriString == lastConsumedDeepLink) return@LaunchedEffect
        lastConsumedDeepLink = uriString
        handleDeepLink(uri, isAuthenticated, navController, authViewModel) { token ->
            pendingShareToken = token
        }
    }

    // Consume pending share token after auth
    LaunchedEffect(isAuthenticated, pendingShareToken) {
        if (isAuthenticated && pendingShareToken != null) {
            val token = pendingShareToken
            pendingShareToken = null
            if (token != null) {
                navController.navigate("shared_wishlist/token:$token")
            }
        }
    }

    // Navigate on auth state changes
    LaunchedEffect(isAuthenticated) {
        if (isAuthenticated) {
            navController.navigate(Routes.Home.route) {
                popUpTo(0) { inclusive = true }
            }
            authViewModel.onAuthenticated()
        } else {
            navController.navigate(Routes.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showTabBar = isAuthenticated && currentRoute != null && (
        currentRoute in listOf(
            Routes.Home.route,
            Routes.Wishlists.route,
            Routes.SharedBookmarks.route,
            Routes.Profile.route
        ) ||
        currentRoute.startsWith("wishlist/") ||
        currentRoute.startsWith("shared_wishlist/")
    )

    // Reset collapse when leaving detail screens
    LaunchedEffect(currentRoute) {
        if (currentRoute in listOf(
                Routes.Home.route,
                Routes.Wishlists.route,
                Routes.Profile.route
            )
        ) {
            isTabBarCollapsed = false
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            NavHost(
                navController = navController,
                startDestination = if (isAuthenticated) Routes.Home.route else Routes.Login.route,
                modifier = Modifier.fillMaxSize()
            ) {
                composable(Routes.Login.route) {
                    LoginScreen(
                        viewModel = authViewModel,
                        onNavigateToRegister = {
                            navController.navigate(Routes.Register.route)
                        }
                    )
                }
                composable(Routes.Register.route) {
                    RegisterScreen(
                        viewModel = authViewModel,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
                composable(Routes.Home.route) {
                    HomeScreen(
                        onNavigateToWishlists = {
                            navController.navigate(Routes.Wishlists.route) {
                                launchSingleTop = true
                            }
                        },
                        onNavigateToShared = {
                            navController.navigate(Routes.SharedBookmarks.route) {
                                launchSingleTop = true
                            }
                        },
                        onNavigateToProfile = {
                            navController.navigate(Routes.Profile.route) {
                                launchSingleTop = true
                            }
                        },
                        onLogout = { authViewModel.logout() }
                    )
                }
                composable(Routes.Wishlists.route) {
                    WishlistsScreen(
                        onNavigateToDetail = { wishlistId ->
                            navController.navigate(Routes.WishlistDetail.createRoute(wishlistId))
                        }
                    )
                }
                composable(
                    Routes.WishlistDetail.route,
                    arguments = listOf(navArgument("wishlistId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val wishlistId = backStackEntry.arguments?.getString("wishlistId") ?: return@composable
                    WishlistDetailScreen(
                        wishlistId = wishlistId,
                        onNavigateBack = { navController.popBackStack() },
                        isTabBarCollapsed = isTabBarCollapsed,
                        onTabBarCollapseChanged = { isTabBarCollapsed = it }
                    )
                }
                composable(Routes.SharedBookmarks.route) {
                    SharedBookmarksScreen(
                        onNavigateToSharedWishlist = { wishlistId ->
                            navController.navigate(Routes.SharedWishlist.createRoute(wishlistId))
                        }
                    )
                }
                composable(
                    Routes.SharedWishlist.route,
                    arguments = listOf(navArgument("wishlistId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val wishlistId = backStackEntry.arguments?.getString("wishlistId") ?: return@composable
                    SharedWishlistScreen(
                        wishlistId = wishlistId,
                        onNavigateBack = { navController.popBackStack() },
                        isTabBarCollapsed = isTabBarCollapsed,
                        onTabBarCollapseChanged = { isTabBarCollapsed = it }
                    )
                }
                composable(Routes.Profile.route) {
                    ProfileScreen(
                        onNavigateToSettings = {
                            navController.navigate(Routes.Settings.route)
                        }
                    )
                }
                composable(Routes.Settings.route) {
                    SettingsScreen(
                        onNavigateBack = { navController.popBackStack() },
                        onLogout = { authViewModel.logout() }
                    )
                }
            }

            // Offline banner
            AnimatedVisibility(
                visible = isOffline && isAuthenticated,
                enter = slideInVertically { -it },
                exit = slideOutVertically { -it },
                modifier = Modifier.align(Alignment.TopCenter)
            ) {
                OfflineBanner()
            }

            // Floating tab bar
            if (showTabBar) {
                FloatingTabBar(
                    selectedTab = currentRouteToTab(currentRoute),
                    isCollapsed = isTabBarCollapsed,
                    onTabSelected = { tab ->
                        navController.navigate(tab.route) {
                            popUpTo(Routes.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .windowInsetsPadding(WindowInsets.navigationBars)
                )
            }
        }
    }
}

private fun currentRouteToTab(route: String?): MainTab {
    return when {
        route == null -> MainTab.HOME
        route.startsWith("wishlist/") -> MainTab.WISHLISTS
        route.startsWith("shared_wishlist/") -> MainTab.SHARED
        route == Routes.Wishlists.route -> MainTab.WISHLISTS
        route == Routes.SharedBookmarks.route -> MainTab.SHARED
        route == Routes.Profile.route || route == Routes.Settings.route -> MainTab.PROFILE
        else -> MainTab.HOME
    }
}

private fun handleDeepLink(
    uri: Uri,
    isAuthenticated: Boolean,
    navController: NavHostController,
    authViewModel: AuthViewModel,
    onPendingToken: (String) -> Unit
) {
    val host = uri.host
    val path = uri.path

    // OAuth callback
    if (host == "auth" && path?.startsWith("/callback") == true) {
        val result = OAuthHelper.parseCallback(uri)
        when (result) {
            is OAuthHelper.OAuthResult.Success -> {
                authViewModel.handleOAuthSuccess(result.accessToken, result.refreshToken)
            }
            is OAuthHelper.OAuthResult.Linked -> {
                // Account linked successfully, settings will refresh
            }
            is OAuthHelper.OAuthResult.Error -> {
                authViewModel.handleOAuthError(result.error, result.email)
            }
        }
        return
    }

    // Share deep link
    val token = when {
        host == "s" -> uri.lastPathSegment
        host == "wishwith.me" || host == "www.wishwith.me" -> uri.lastPathSegment
        else -> null
    }

    if (token != null) {
        if (isAuthenticated) {
            navController.navigate("shared_wishlist/token:$token")
        } else {
            onPendingToken(token)
        }
    }
}
