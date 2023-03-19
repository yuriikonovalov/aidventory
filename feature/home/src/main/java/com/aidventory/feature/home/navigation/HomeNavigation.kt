package com.aidventory.feature.home.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import com.google.accompanist.navigation.animation.navigation
import androidx.window.layout.DisplayFeature
import com.aidventory.feature.home.presentation.home.HomeScreen
import com.aidventory.feature.home.presentation.search.SearchScreen
import com.google.accompanist.navigation.animation.composable

const val homeGraphRoute = "home_graph"
const val homeScreenRoute = "home"
const val searchScreenRoute = "search"

fun NavController.navigateToHomeScreen(navOptions: NavOptions? = null) {
    navigate(homeGraphRoute, navOptions)
}

fun NavController.navigateToSearchScreen(navOptions: NavOptions? = null) {
    navigate(searchScreenRoute, navOptions)
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.homeGraph(
    windowSizeClass: WindowSizeClass,
    navController: NavHostController
) {
    navigation(
        route = homeGraphRoute,
        startDestination = homeScreenRoute
    ) {
        composable(route = homeScreenRoute) {
            HomeScreen(windowSizeClass = windowSizeClass, navController = navController)
        }

        composable(route = searchScreenRoute) {
            SearchScreen(
                windowSizeClass = windowSizeClass,
                navController = navController
            )
        }
    }
}