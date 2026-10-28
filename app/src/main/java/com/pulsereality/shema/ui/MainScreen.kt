package com.pulsereality.shema.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.pulsereality.shema.ui.components.MiniPlayerBar
import com.pulsereality.shema.ui.components.ShemaBottomBar
import com.pulsereality.shema.ui.navigation.NavigationTab
import com.pulsereality.shema.ui.navigation.Screen
import com.pulsereality.shema.ui.screens.BibleScreen
import com.pulsereality.shema.ui.screens.CommunityScreen
import com.pulsereality.shema.ui.screens.DevotionalReaderScreen
import com.pulsereality.shema.ui.screens.FocusSettingsScreen
import com.pulsereality.shema.ui.screens.HomeScreen
import com.pulsereality.shema.ui.screens.MeScreen
import com.pulsereality.shema.ui.screens.PricingScreen
import com.pulsereality.shema.ui.screens.WorshipPlayerScreen
import com.pulsereality.shema.ui.screens.WorshipScreen
import com.pulsereality.shema.ui.theme.BackgroundDark
import com.pulsereality.shema.ui.viewmodels.BibleViewModel
import com.pulsereality.shema.ui.viewmodels.CommunityViewModel
import com.pulsereality.shema.ui.viewmodels.DevotionUiState
import com.pulsereality.shema.ui.viewmodels.HomeViewModel
import com.pulsereality.shema.ui.viewmodels.WorshipViewModel

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel = viewModel(),
    worshipViewModel: WorshipViewModel = viewModel(),
    bibleViewModel: BibleViewModel = viewModel(),
    communityViewModel: CommunityViewModel = viewModel()
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val isDevotionReaderOpen = currentRoute?.startsWith("devotion_reader") == true

    // Worship Player State
    val currentHymn by worshipViewModel.currentHymn.collectAsState()
    val currentStem by worshipViewModel.currentStem.collectAsState()
    val isPlaying by worshipViewModel.isPlaying.collectAsState()
    val currentTime by worshipViewModel.currentTime.collectAsState()
    val duration by worshipViewModel.duration.collectAsState()

    var isFullPlayerPresented by remember { mutableStateOf(false) }
    var isPaywallPresented by remember { mutableStateOf(false) }

    val progress = if (duration > 0) (currentTime / duration).toFloat() else 0f

    Box(modifier = modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = BackgroundDark,
            bottomBar = {
                if (!isDevotionReaderOpen) {
                    Column {
                        // Spotify-style Mini-Player floating above bottom nav
                        if (currentHymn != null) {
                            MiniPlayerBar(
                                visible = true,
                                title = currentHymn?.title ?: "",
                                subtitle = currentHymn?.author ?: "Traditional",
                                currentStem = currentStem.rawValue,
                                isPlaying = isPlaying,
                                progress = progress,
                                onPlayPauseClick = { worshipViewModel.togglePlayPause() },
                                onClick = { isFullPlayerPresented = true }
                            )
                        }

                        ShemaBottomBar(
                            currentRoute = currentRoute,
                            onTabSelected = { tab ->
                                navController.navigate(tab.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                NavHost(
                    navController = navController,
                    startDestination = Screen.Home.route,
                    modifier = Modifier.fillMaxSize()
                ) {
                    composable(Screen.Home.route) {
                        HomeScreen(
                            onNavigateToDevotionStep = { step ->
                                navController.navigate("devotion_reader/$step")
                            }
                        )
                    }

                    composable(
                        route = "devotion_reader/{step}",
                        arguments = listOf(
                            navArgument("step") { type = NavType.StringType; defaultValue = "passage" }
                        )
                    ) { backStackEntry ->
                        val step = backStackEntry.arguments?.getString("step") ?: "passage"
                        val stepIndex = when (step.lowercase()) {
                            "passage" -> 1
                            "commentary" -> 2
                            "reflection" -> 3
                            "prayer" -> 4
                            else -> 1
                        }

                        val uiState by homeViewModel.uiState.collectAsState()
                        if (uiState is DevotionUiState.Success) {
                            val success = uiState as DevotionUiState.Success
                            DevotionalReaderScreen(
                                devotion = success.core,
                                locale = success.locale,
                                initialStepIndex = stepIndex,
                                onBackToHome = { navController.popBackStack() },
                                onDevotionCompleted = {
                                    homeViewModel.markStepCompleted(4)
                                }
                            )
                        }
                    }

                    composable(Screen.Worship.route) {
                        WorshipScreen(
                            onPlayHymn = { demoHymn ->
                                val hymn = com.pulsereality.shema.data.model.Hymn(
                                    id = demoHymn.id,
                                    number = demoHymn.number,
                                    title = demoHymn.title,
                                    author = demoHymn.author,
                                    category = demoHymn.category,
                                    duration = 222.0,
                                    lyrics = listOf(
                                        com.pulsereality.shema.data.model.LyricLine("l1", 0.0, "Holy, holy, holy! Lord God Almighty!", 1),
                                        com.pulsereality.shema.data.model.LyricLine("l2", 6.5, "Early in the morning our song shall rise to Thee;", 1),
                                        com.pulsereality.shema.data.model.LyricLine("l3", 14.0, "Holy, holy, holy, merciful and mighty!", 1),
                                        com.pulsereality.shema.data.model.LyricLine("l4", 21.5, "God in three Persons, blessed Trinity!", 1),
                                        com.pulsereality.shema.data.model.LyricLine("l5", 30.0, "Holy, holy, holy! All the saints adore Thee,", 2),
                                        com.pulsereality.shema.data.model.LyricLine("l6", 37.0, "Casting down their golden crowns around the glassy sea;", 2),
                                        com.pulsereality.shema.data.model.LyricLine("l7", 45.0, "Cherubim and seraphim falling down before Thee,", 2),
                                        com.pulsereality.shema.data.model.LyricLine("l8", 52.0, "Which wert, and art, and evermore shalt be.", 2)
                                    )
                                )
                                worshipViewModel.playHymn(hymn)
                                isFullPlayerPresented = true
                            }
                        )
                    }

                    composable(Screen.Bible.route) {
                        BibleScreen(viewModel = bibleViewModel)
                    }

                    composable(Screen.Community.route) {
                        CommunityScreen(viewModel = communityViewModel)
                    }

                    composable(Screen.Me.route) {
                        MeScreen(
                            onNavigateToSettings = { settingKey ->
                                navController.navigate(settingKey)
                            },
                            onUpgradeClick = {
                                isPaywallPresented = true
                            }
                        )
                    }

                    composable("focus_settings") {
                        FocusSettingsScreen(
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable("pricing") {
                        PricingScreen(
                            onDismiss = { navController.popBackStack() },
                            onSubscribed = { navController.popBackStack() }
                        )
                    }
                }
            }
        }

        // Full Screen Worship Player Modal Overlay (Matching iOS PlayerView)
        AnimatedVisibility(
            visible = isFullPlayerPresented,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
        ) {
            WorshipPlayerScreen(
                viewModel = worshipViewModel,
                onDismiss = { isFullPlayerPresented = false }
            )
        }

        // Full Screen Subscription Paywall Modal Overlay (Matching iOS PaywallView)
        AnimatedVisibility(
            visible = isPaywallPresented,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
        ) {
            PricingScreen(
                onDismiss = { isPaywallPresented = false },
                onSubscribed = { isPaywallPresented = false }
            )
        }
    }
}
