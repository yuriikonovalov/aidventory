package com.aidventory.feature.scanner.navigation

import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import com.aidventory.feature.scanner.presentation.ScannerScreen
import com.google.accompanist.navigation.animation.composable

const val scannerNavigationRoute = "scanner_route"

fun NavController.navigateToScannerScreen(navOptions: NavOptions? = null) {
    navigate(scannerNavigationRoute, navOptions)
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.scannerRoute(
    windowSizeClass: WindowSizeClass,
    navController: NavHostController
) {
    composable(
        route = scannerNavigationRoute,
        exitTransition = { ExitTransition.None },
        popExitTransition = { ExitTransition.None }
    ) {
        ScannerScreen(
            windowSizeClass = windowSizeClass,
            navController = navController
        )
    }
}