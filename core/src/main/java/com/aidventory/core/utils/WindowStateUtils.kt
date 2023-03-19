package com.aidventory.core.utils

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass

/**
 * Different type of navigation supported by app depending on device size and state.
 */
enum class NavigationType {
    BOTTOM_NAVIGATION, NAVIGATION_RAIL, PERMANENT_NAVIGATION_DRAWER
}

/**
 * Different position of navigation content inside Navigation Rail, Navigation Drawer depending on device size and state.
 */
enum class NavigationContentPosition {
    TOP, CENTER
}

fun NavigationContentPosition.toVerticalArrangement(): Arrangement.Vertical {
    return when (this) {
        NavigationContentPosition.CENTER -> Arrangement.Center
        else -> Arrangement.Top
    }
}

/**
 * App Content shown depending on device size and state.
 */
enum class ContentType {
    SINGLE_PANE, DUAL_PANE
}


/**
 * Returns [NavigationType] based on the current [WindowSizeClass.widthSizeClass] and [DevicePosture].
 */
fun WindowSizeClass.getNavigationType(foldingDevicePosture: DevicePosture): NavigationType {
    return when (widthSizeClass) {
        WindowWidthSizeClass.Compact -> NavigationType.BOTTOM_NAVIGATION
        WindowWidthSizeClass.Medium -> NavigationType.NAVIGATION_RAIL
        WindowWidthSizeClass.Expanded -> {
            if (foldingDevicePosture is DevicePosture.BookPosture) {
                NavigationType.PERMANENT_NAVIGATION_DRAWER
            } else {
//                NavigationType.NAVIGATION_RAIL
                NavigationType.PERMANENT_NAVIGATION_DRAWER
            }
        }

        else -> NavigationType.BOTTOM_NAVIGATION
    }
}

/**
 * Returns [ContentType] based on the current [WindowSizeClass.widthSizeClass] and [DevicePosture].
 */
fun WindowSizeClass.getContentType(foldingDevicePosture: DevicePosture): ContentType {
    return when (widthSizeClass) {
        WindowWidthSizeClass.Compact -> ContentType.SINGLE_PANE
        WindowWidthSizeClass.Medium -> {
            if (foldingDevicePosture != DevicePosture.NormalPosture) {
                ContentType.DUAL_PANE
            } else {
                ContentType.SINGLE_PANE
            }
        }

        WindowWidthSizeClass.Expanded -> ContentType.DUAL_PANE
        else -> ContentType.SINGLE_PANE
    }
}

/**
 * Returns [NavigationContentPosition] based on the current [WindowSizeClass.widthSizeClass].
 */
fun WindowSizeClass.getNavigationContentPosition(): NavigationContentPosition {
    return when (widthSizeClass) {
        WindowWidthSizeClass.Medium,
        WindowWidthSizeClass.Expanded -> NavigationContentPosition.CENTER
        // We don't care about WindowWidthSizeClass.Compact here
        // because NavigationType.BOTTOM_NAVIGATION is used in this case.
        // It's not planned to set a content position for BOTTOM_NAVIGATION.
        else -> NavigationContentPosition.TOP
    }
}