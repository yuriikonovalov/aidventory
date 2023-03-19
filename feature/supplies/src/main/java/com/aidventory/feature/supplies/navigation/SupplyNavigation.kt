package com.aidventory.feature.supplies.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.navigation.*
import com.google.accompanist.navigation.animation.navigation
import androidx.window.layout.DisplayFeature
import com.aidventory.feature.supplies.presentation.addsupply.AddSupplyScreen
import com.aidventory.feature.supplies.presentation.movesupply.MoveSupplyScreen
import com.aidventory.feature.supplies.presentation.quicksearch.QuickSearchScreen
import com.aidventory.feature.supplies.presentation.supplies.SuppliesScreen
import com.google.accompanist.navigation.animation.composable

internal object SuppliesScreenNavigation {
    private const val ROUTE_PATH = "supplies"
    const val ARGUMENT = "barcode"
    const val SCREEN_ROUTE = "$ROUTE_PATH?$ARGUMENT={$ARGUMENT}"
    fun navigationRoute(barcode: String? = null) = barcode
        ?.let { "$ROUTE_PATH?$ARGUMENT=$it" }
        ?: ROUTE_PATH
}

internal object AddSupplyScreenNavigation {
    private const val ROUTE_PATH = "add_supply"
    const val ARGUMENT = "barcode"
    const val SCREEN_ROUTE = "$ROUTE_PATH?$ARGUMENT={$ARGUMENT}"
    fun navigationRoute(barcode: String? = null) = barcode
        ?.let { "$ROUTE_PATH?$ARGUMENT=$it" }
        ?: ROUTE_PATH
}

internal object QuickSearchScreenNavigation {
    private const val ROUTE_PATH = "quick_search"
    const val ARGUMENT = "barcode"
    const val SCREEN_ROUTE = "$ROUTE_PATH/{$ARGUMENT}"
    fun navigationRoute(barcode: String) = "$ROUTE_PATH/$barcode"
}

internal object MoveSupplyScreenNavigation {
    private const val ROUTE_PATH = "move_supply"
    const val SUPPLY_ARGUMENT = "supply_barcode"
    const val CONTAINER_ARGUMENT = "container_barcode"
    const val SCREEN_ROUTE =
        "$ROUTE_PATH/{$SUPPLY_ARGUMENT}?$CONTAINER_ARGUMENT={$CONTAINER_ARGUMENT}"

    fun navigationRoute(supplyBarcode: String, containerBarcode: String?) = containerBarcode
        ?.let { "${ROUTE_PATH}/$supplyBarcode?${CONTAINER_ARGUMENT}=$it" }
        ?: "${ROUTE_PATH}/$supplyBarcode"
}

const val suppliesGraphRoute = "supplies_graph"

fun NavController.navigateToAddSupplyScreen(
    barcode: String? = null,
    navOptions: NavOptions? = null
) {
    navigate(AddSupplyScreenNavigation.navigationRoute(barcode), navOptions)
}

/**
 * @param barcode a barcode of the supply to search a container for.
 */
fun NavController.navigateToQuickSearchScreen(
    barcode: String,
    navOptions: NavOptions? = null
) {
    navigate(QuickSearchScreenNavigation.navigationRoute(barcode), navOptions)
}

/**
 * @param containerBarcode a barcode of the current supply.
 */
fun NavController.navigateToMoveSupplyScreen(
    supplyBarcode: String,
    containerBarcode: String? = null,
    navOptions: NavOptions? = null
) {
    navigate(
        MoveSupplyScreenNavigation.navigationRoute(supplyBarcode, containerBarcode),
        navOptions
    )
}

fun NavController.navigateToSuppliesGraph(navOptions: NavOptions? = null) {
    navigate(suppliesGraphRoute, navOptions)
}

fun NavController.navigateToSuppliesScreen(
    barcode: String? = null,
    navOptions: NavOptions? = null
) {
    navigate(SuppliesScreenNavigation.navigationRoute(barcode), navOptions)
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.suppliesGraph(
    windowSizeClass: WindowSizeClass,
    displayFeatures: List<DisplayFeature>,
    navController: NavHostController
) {
    navigation(
        route = suppliesGraphRoute,
        startDestination = SuppliesScreenNavigation.SCREEN_ROUTE
    ) {
        composable(
            route = SuppliesScreenNavigation.SCREEN_ROUTE,
            arguments = listOf(
                navArgument(SuppliesScreenNavigation.ARGUMENT) {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) {
            SuppliesScreen(
                windowSizeClass = windowSizeClass,
                displayFeatures = displayFeatures,
                navController = navController
            )
        }

        composable(
            route = AddSupplyScreenNavigation.SCREEN_ROUTE,
            arguments = listOf(
                navArgument(AddSupplyScreenNavigation.ARGUMENT) {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) {
            AddSupplyScreen(
                windowWidthSizeClass = windowSizeClass.widthSizeClass,
                navController = navController
            )
        }

        composable(
            route = QuickSearchScreenNavigation.SCREEN_ROUTE,
            arguments = listOf(
                navArgument(QuickSearchScreenNavigation.ARGUMENT) { type = NavType.StringType }
            )
        ) {
            QuickSearchScreen(
                windowWidthSizeClass = windowSizeClass.widthSizeClass,
                navController = navController
            )
        }

        composable(
            route = MoveSupplyScreenNavigation.SCREEN_ROUTE,
            arguments = listOf(
                navArgument(MoveSupplyScreenNavigation.SUPPLY_ARGUMENT) {
                    type = NavType.StringType
                },
                navArgument(MoveSupplyScreenNavigation.CONTAINER_ARGUMENT) {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) {
            MoveSupplyScreen(
                windowWidthSizeClass = windowSizeClass.widthSizeClass,
                navController = navController
            )
        }
    }
}