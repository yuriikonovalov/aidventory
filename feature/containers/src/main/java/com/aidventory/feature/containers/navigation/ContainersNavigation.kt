package com.aidventory.feature.containers.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.*
import androidx.navigation.compose.dialog
import com.google.accompanist.navigation.animation.navigation
import androidx.window.layout.DisplayFeature
import com.aidventory.feature.containers.presentation.addcontainer.AddContainerDialog
import com.aidventory.feature.containers.presentation.addcontainer.AddContainerScreen
import com.aidventory.feature.containers.presentation.containers.ContainersScreen
import com.google.accompanist.navigation.animation.composable

internal object ContainersScreenNavigation {
    private const val ROUTE_PATH = "containers"
    const val ROUTE_ARGUMENT = "barcode"
    const val SCREEN_ROUTE = "$ROUTE_PATH?$ROUTE_ARGUMENT={$ROUTE_ARGUMENT}"
    fun navigationRoute(barcode: String? = null) = barcode
        ?.let { "$ROUTE_PATH?$ROUTE_ARGUMENT=$it" }
        ?: ROUTE_PATH
}

const val containersGraphRoute = "containers_graph"
const val addContainerScreenRoute = "add_container"
const val addContainerDialogRoute = "add_container_dialog"

fun NavController.navigateToContainersGraph(navOptions: NavOptions? = null) {
    navigate(containersGraphRoute, navOptions)
}

fun NavController.navigateToContainersScreen(
    barcode: String? = null,
    navOptions: NavOptions? = null
) {
    navigate(ContainersScreenNavigation.navigationRoute(barcode), navOptions)
}

fun NavController.navigateToAddContainerScreen(navOptions: NavOptions? = null) {
    navigate(addContainerScreenRoute, navOptions)
}

fun NavController.navigateToAddContainerDialog(navOptions: NavOptions? = null) {
    navigate(addContainerDialogRoute, navOptions)
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.containersGraph(
    windowSizeClass: WindowSizeClass,
    displayFeatures: List<DisplayFeature>,
    navController: NavHostController
) {
    navigation(
        route = containersGraphRoute,
        startDestination = ContainersScreenNavigation.SCREEN_ROUTE
    ) {
        composable(
            route = ContainersScreenNavigation.SCREEN_ROUTE,
            arguments = listOf(
                navArgument(ContainersScreenNavigation.ROUTE_ARGUMENT) {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) {
            ContainersScreen(
                windowSizeClass = windowSizeClass,
                displayFeatures = displayFeatures,
                onNavigateUp = navController::navigateUp,
                onNavigateToAddContainerScreen = navController::navigateToAddContainerScreen,
                onNavigateToAddContainerDialog = navController::navigateToAddContainerDialog
            )
        }

        composable(route = addContainerScreenRoute) {
            AddContainerScreen(onNavigationClick = navController::navigateUp)
        }

        dialog(
            route = addContainerDialogRoute,
            dialogProperties = DialogProperties(
                dismissOnClickOutside = true,
                // A workaround to a bug https://issuetracker.google.com/issues/221643630
                usePlatformDefaultWidth = false
            )
        ) {
            AddContainerDialog(onCloseClick = navController::navigateUp)
        }
    }
}