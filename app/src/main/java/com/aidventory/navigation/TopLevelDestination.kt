package com.aidventory.navigation

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.navOptions
import com.aidventory.core.common.designsystem.icon.AidventoryIcons
import com.aidventory.core.common.designsystem.icon.Icon
import com.aidventory.feature.home.navigation.navigateToHomeScreen
import com.aidventory.feature.expired.navigation.navigateToExpiredScreen
import com.aidventory.feature.scanner.navigation.navigateToScannerScreen
import com.aidventory.feature.home.R as homeR
import com.aidventory.feature.expired.R as expiredR
import com.aidventory.feature.scanner.R as scannerR

enum class TopLevelDestination(
    val selectedIcon: Icon,
    val unselectedIcon: Icon,
    @StringRes val iconTextId: Int
) {
    HOME(
        selectedIcon = AidventoryIcons.Home,
        unselectedIcon = AidventoryIcons.HomeBorder,
        iconTextId = homeR.string.top_app_bar_title_home
    ),
    SCANNER(
        selectedIcon = AidventoryIcons.Scanner,
        unselectedIcon = AidventoryIcons.Scanner,
        iconTextId = scannerR.string.scanner
    ),
    EXPIRED(
        selectedIcon = AidventoryIcons.Delete,
        unselectedIcon = AidventoryIcons.DeleteBorder,
        iconTextId = expiredR.string.expired
    )
}

class TopLevelDestinationNavigationAction(private val navController: NavHostController) {

    fun navigate(topLevelDestination: TopLevelDestination) {
        val topLevelNavOptions = navOptions {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            // on the back stack as users select items
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            // Avoid multiple copies of the same destination when
            // reselecting the same item
            launchSingleTop = true
            // Restore state when reselecting a previously selected item
            restoreState = true
        }

        when (topLevelDestination) {
            TopLevelDestination.HOME -> navController.navigateToHomeScreen(topLevelNavOptions)
            TopLevelDestination.SCANNER -> navController.navigateToScannerScreen(topLevelNavOptions)
            TopLevelDestination.EXPIRED -> navController.navigateToExpiredScreen(topLevelNavOptions)
        }
    }
}

@Composable
fun rememberTopLevelDestinationNavigationAction(navController: NavHostController): TopLevelDestinationNavigationAction {
    return remember(navController) {
        TopLevelDestinationNavigationAction(navController)
    }
}
