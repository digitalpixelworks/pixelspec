package io.android.pixelspec.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.android.pixelspec.presentation.screen.about.AboutScreen
import io.android.pixelspec.presentation.screen.apps.AppsScreen
import io.android.pixelspec.presentation.screen.home.HomeScreen
import io.android.pixelspec.presentation.screen.settings.SettingsScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController, startDestination = Destinations.HOME
    ) {
        composable(Destinations.HOME) {
            HomeScreen(
                onAppsClick = { navController.navigate(Destinations.APPS) },
                onSettingsClick = { navController.navigate(Destinations.SETTINGS) })
        }

        composable(Destinations.APPS) {
            AppsScreen(
                onBackClick = { navController.popBackStack() })
        }

        composable(Destinations.SETTINGS) {
            SettingsScreen(
                onBackClick = { navController.popBackStack() },
                onAboutClick = { navController.navigate(Destinations.ABOUT) })
        }

        composable(Destinations.ABOUT) {
            AboutScreen(
                onBackClick = { navController.popBackStack() })
        }
    }
}