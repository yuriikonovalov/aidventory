package com.aidventory.feature.expired.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.navDeepLink
import com.aidventory.core.notification.SupplyExpiryNotification.EXPIRED_SCREEN_DEEP_LINK_URI
import com.aidventory.feature.expired.presentation.ExpiredScreen
import com.google.accompanist.navigation.animation.composable

const val expiredScreenRoute = "expired_route"

fun NavController.navigateToExpiredScreen(navOptions: NavOptions? = null) {
    navigate(expiredScreenRoute, navOptions)
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.expiredRoute(
    windowSizeClass: WindowSizeClass
) {
    composable(
        route = expiredScreenRoute,
        deepLinks = listOf(navDeepLink { uriPattern = "$EXPIRED_SCREEN_DEEP_LINK_URI/expired" })
    ) {
        ExpiredScreen(windowSizeClass = windowSizeClass)
    }
}
