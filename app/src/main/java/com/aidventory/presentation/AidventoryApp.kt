package com.aidventory.presentation


import android.Manifest
import android.os.Build
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.window.layout.DisplayFeature
import androidx.window.layout.FoldingFeature
import com.aidventory.core.common.designsystem.component.AidventoryNavigationBar
import com.aidventory.core.common.designsystem.component.AidventoryNavigationBarItem
import com.aidventory.core.common.designsystem.component.AidventoryNavigationRail
import com.aidventory.core.common.designsystem.component.AidventoryNavigationRailItem
import com.aidventory.core.common.designsystem.theme.AidventoryTheme
import com.aidventory.core.utils.*
import com.aidventory.feature.home.navigation.homeScreenRoute
import com.aidventory.feature.expired.navigation.expiredScreenRoute
import com.aidventory.navigation.AidventoryNavHost
import com.aidventory.navigation.TopLevelDestination
import com.aidventory.navigation.rememberTopLevelDestinationNavigationAction
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AidventoryApp(
    windowSizeClass: WindowSizeClass,
    displayFeatures: List<DisplayFeature>,
    expiredBadgeValue: Int
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val notificationPermissionLauncher = rememberPermissionState(
            permission = Manifest.permission.POST_NOTIFICATIONS
        )
        LaunchedEffect(Unit) {
            notificationPermissionLauncher.launchPermissionRequest()
        }
    }

    val foldingFeature = displayFeatures.filterIsInstance<FoldingFeature>().firstOrNull()
    val foldingDevicePosture = foldingFeature.toDevicePosture()
    val navigationType = windowSizeClass.getNavigationType(foldingDevicePosture)

    AidventoryNavigationWrapper(
        windowSizeClass = windowSizeClass,
        displayFeatures = displayFeatures,
        navigationType = navigationType,
        expiredBadgeValue = expiredBadgeValue
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun AidventoryNavigationWrapper(
    windowSizeClass: WindowSizeClass,
    displayFeatures: List<DisplayFeature>,
    navigationType: NavigationType,
    expiredBadgeValue: Int
) {
    val navController = rememberAnimatedNavController()
    val topLevelDestinationNavigationAction =
        rememberTopLevelDestinationNavigationAction(navController = navController)
    val topLevelDestinations = TopLevelDestination.values().asList()
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination
    val isBottomBarVisible = currentDestination.isBottomBarVisible()

    if (navigationType == NavigationType.PERMANENT_NAVIGATION_DRAWER) {
        AidventoryPermanentNavigationDrawer(
            destinations = topLevelDestinations,
            onNavigateToTopDestination = topLevelDestinationNavigationAction::navigate,
            currentDestination = currentDestination,
            expiredBadgeValue = expiredBadgeValue,
            content = {
                AidventoryNavHost(
                    modifier = Modifier.fillMaxSize(),
                    navController = navController,
                    windowSizeClass = windowSizeClass,
                    displayFeatures = displayFeatures,
                )
            },
        )
    } else {
        Row(modifier = Modifier.fillMaxSize()) {
            AnimatedVisibility(visible = navigationType == NavigationType.NAVIGATION_RAIL) {
                AidventoryNavRail(
                    destinations = topLevelDestinations,
                    onNavigateToTopDestination = topLevelDestinationNavigationAction::navigate,
                    currentDestination = currentDestination,
                    navigationContentPosition = NavigationContentPosition.CENTER,
                    expiredBadgeValue = expiredBadgeValue
                )
            }

            Column(modifier = Modifier.fillMaxSize()) {
                AidventoryNavHost(
                    navController = navController,
                    displayFeatures = displayFeatures,
                    windowSizeClass = windowSizeClass,
                    modifier = Modifier.weight(1f)
                )
                if (navigationType == NavigationType.BOTTOM_NAVIGATION && isBottomBarVisible) {
                    AidventoryBottomBar(
                        destinations = topLevelDestinations,
                        onNavigateToTopDestination = topLevelDestinationNavigationAction::navigate,
                        currentDestination = currentDestination,
                        expiredBadgeValue = expiredBadgeValue
                    )
                }
            }
        }
    }

}

@Composable
private fun AidventoryPermanentNavigationDrawer(
    destinations: List<TopLevelDestination>,
    onNavigateToTopDestination: (TopLevelDestination) -> Unit,
    currentDestination: NavDestination?,
    expiredBadgeValue: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    PermanentNavigationDrawer(
        modifier = modifier,
        drawerContent = {
            PermanentDrawerSheet(modifier = Modifier.width(240.dp)) {
                Spacer(modifier = Modifier.height(16.dp))
                destinations.forEach { destination ->
                    val selected = currentDestination
                        .isTopLevelDestinationInHierarchy(destination)
                    NavigationDrawerItem(
                        modifier = Modifier.padding(4.dp),
                        selected = selected,
                        onClick = { onNavigateToTopDestination(destination) },
                        icon = {
                            BadgeWrapper(
                                showBadge = destination == TopLevelDestination.EXPIRED,
                                badgeValue = expiredBadgeValue
                            ) {
                                NavigationItemIcon(
                                    destination = destination,
                                    isSelected = selected
                                )
                            }

                        },
                        label = {
                            Text(
                                stringResource(destination.iconTextId),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    )
                }
            }
        },
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BadgeWrapper(
    showBadge: Boolean,
    badgeValue: Int,
    content: @Composable () -> Unit
) {
    if (showBadge && badgeValue > 0) {
        BadgedBox(badge = {
            Badge { Text(text = badgeValue.toString()) }
        }) {
            content()
        }
    } else {
        content()
    }
}

@Composable
private fun AidventoryNavRail(
    destinations: List<TopLevelDestination>,
    onNavigateToTopDestination: (TopLevelDestination) -> Unit,
    currentDestination: NavDestination?,
    navigationContentPosition: NavigationContentPosition,
    expiredBadgeValue: Int,
    modifier: Modifier = Modifier
) {
    AidventoryNavigationRail {
        Column(
            modifier = modifier
                .fillMaxHeight()
                .padding(horizontal = 8.dp),
            verticalArrangement = navigationContentPosition.toVerticalArrangement()
        ) {
            destinations.forEach { destination ->
                val selected =
                    currentDestination.isTopLevelDestinationInHierarchy(destination)
                AidventoryNavigationRailItem(
                    modifier = Modifier.padding(vertical = 8.dp),
                    selected = selected,
                    onClick = { onNavigateToTopDestination(destination) },
                    icon = {
                        BadgeWrapper(
                            showBadge = destination == TopLevelDestination.EXPIRED,
                            badgeValue = expiredBadgeValue
                        ) {
                            NavigationItemIcon(
                                destination = destination,
                                isSelected = selected
                            )
                        }
                    },
                    label = {
                        Text(
                            stringResource(destination.iconTextId),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                )
            }
        }

    }
}

@Composable
private fun AidventoryBottomBar(
    destinations: List<TopLevelDestination>,
    onNavigateToTopDestination: (TopLevelDestination) -> Unit,
    currentDestination: NavDestination?,
    expiredBadgeValue: Int,
    modifier: Modifier = Modifier
) {
    AidventoryNavigationBar(modifier = modifier) {
        destinations.forEach { destination ->
            val selected = currentDestination.isTopLevelDestinationInHierarchy(destination)
            AidventoryNavigationBarItem(
                selected = selected,
                onClick = { onNavigateToTopDestination(destination) },
                icon = {
                    BadgeWrapper(
                        showBadge = destination == TopLevelDestination.EXPIRED,
                        badgeValue = expiredBadgeValue
                    ) {
                        NavigationItemIcon(
                            destination = destination,
                            isSelected = selected
                        )
                    }
                },
                label = {
                    Text(
                        stringResource(destination.iconTextId),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            )
        }
    }
}

@Composable
private fun NavigationItemIcon(destination: TopLevelDestination, isSelected: Boolean) {
    val imageVector = if (isSelected) {
        destination.selectedIcon.imageVector
    } else {
        destination.unselectedIcon.imageVector
    }
    Icon(
        imageVector = imageVector,
        contentDescription = null
    )
}

private fun NavDestination?.isTopLevelDestinationInHierarchy(destination: TopLevelDestination): Boolean {
    return this?.hierarchy?.any {
        it.route?.contains(destination.name, ignoreCase = true) ?: false
    } ?: false
}

private fun NavDestination?.isBottomBarVisible(): Boolean {
    return this?.route in listOf(
        homeScreenRoute,
        expiredScreenRoute
    )
}

@Preview(showBackground = true, device = Devices.DESKTOP)
@Composable
private fun AidventoryAppPreviewDesktop() {
    AidventoryTheme {
        AidventoryApp(
            windowSizeClass = PreviewWindowSizeClass.DESKTOP,
            displayFeatures = emptyList(),
            expiredBadgeValue = 6
        )
    }
}