package com.aidventory.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.window.layout.DisplayFeature
import com.aidventory.feature.containers.navigation.containersGraph
import com.aidventory.feature.expired.navigation.expiredRoute
import com.aidventory.feature.home.navigation.homeGraph
import com.aidventory.feature.home.navigation.homeGraphRoute
import com.aidventory.feature.scanner.navigation.scannerRoute
import com.aidventory.feature.settings.navigation.settingsGraph
import com.aidventory.feature.supplies.navigation.suppliesGraph
import com.google.accompanist.navigation.animation.AnimatedNavHost


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AidventoryNavHost(
    navController: NavHostController,
    windowSizeClass: WindowSizeClass,
    displayFeatures: List<DisplayFeature>,
    modifier: Modifier = Modifier,
    startDestination: String = homeGraphRoute
) {
    AnimatedNavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination,
        enterTransition = { fadeIn() },
        exitTransition = { fadeOut() }
    ) {
        homeGraph(
            windowSizeClass = windowSizeClass,
            navController = navController
        )
        scannerRoute(
            windowSizeClass = windowSizeClass,
            navController = navController
        )

        expiredRoute(windowSizeClass = windowSizeClass)

        settingsGraph(
            windowSizeClass = windowSizeClass,
            displayFeatures = displayFeatures,
            navController = navController
        )

        containersGraph(
            windowSizeClass = windowSizeClass,
            displayFeatures = displayFeatures,
            navController = navController
        )
        suppliesGraph(
            windowSizeClass = windowSizeClass,
            displayFeatures = displayFeatures,
            navController = navController
        )
    }
}