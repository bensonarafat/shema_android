package com.pulsereality.shema.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.GraphicEq
import androidx.compose.material.icons.outlined.Home
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    // Auth & Onboarding
    data object Welcome : Screen("welcome")
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object ForgotPassword : Screen("forgot_password")
    data object Onboarding : Screen("onboarding")
    data object Pricing : Screen("pricing")

    // Main Tabs (matching iOS AppTabs)
    data object Home : Screen("tab_home")
    data object Bible : Screen("tab_bible")
    data object Worship : Screen("tab_worship")
    data object Community : Screen("tab_community")
    data object Me : Screen("tab_me")

    // Sub-screens
    data object DevotionReader : Screen("devotion_reader/{step}") {
        fun createRoute(step: String = "passage") = "devotion_reader/$step"
    }
    data object FullAudioPlayer : Screen("full_audio_player")
    data object BibleSearch : Screen("bible_search")
    data object Settings : Screen("settings")
    data object StreakDetails : Screen("streak_details")
    data object PrayerRequest : Screen("prayer_request")
}

enum class NavigationTab(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val isAvatar: Boolean = false
) {
    HOME(
        route = Screen.Home.route,
        title = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    ),
    BIBLE(
        route = Screen.Bible.route,
        title = "Bible",
        selectedIcon = Icons.Filled.Book,
        unselectedIcon = Icons.Outlined.Book
    ),
    WORSHIP(
        route = Screen.Worship.route,
        title = "Worship",
        selectedIcon = Icons.Filled.GraphicEq,
        unselectedIcon = Icons.Outlined.GraphicEq
    ),
    COMMUNITY(
        route = Screen.Community.route,
        title = "Community",
        selectedIcon = Icons.Filled.Favorite,
        unselectedIcon = Icons.Outlined.FavoriteBorder
    ),
    ME(
        route = Screen.Me.route,
        title = "Me",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
        isAvatar = true
    );

    companion object {
        fun fromRoute(route: String?): NavigationTab {
            return entries.firstOrNull { it.route == route } ?: HOME
        }
    }
}
