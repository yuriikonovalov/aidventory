package com.aidventory.feature.settings.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import com.google.accompanist.navigation.animation.navigation
import androidx.window.layout.DisplayFeature
import com.aidventory.feature.settings.presentation.SettingsScreen
import com.google.accompanist.navigation.animation.composable

const val settingsScreenRoute = "settings_route"
const val settingsGraphRoute = "settings_graph"

fun NavController.navigateToSettingsGraph(navOptions: NavOptions? = null) {
    navigate(settingsGraphRoute, navOptions)
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.settingsGraph(
    windowSizeClass: WindowSizeClass,
    displayFeatures: List<DisplayFeature>,
    navController: NavController
) {
    navigation(route = settingsGraphRoute, startDestination = settingsScreenRoute) {
        composable(route = settingsScreenRoute) {
            SettingsScreen(
                windowSizeClass = windowSizeClass,
                displayFeatures = displayFeatures,
                navController = navController
            )
        }
    }
}