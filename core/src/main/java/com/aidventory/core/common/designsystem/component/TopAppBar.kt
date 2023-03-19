package com.aidventory.core.common.designsystem.component

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow


/**
 *  By design a top app bar is center aligned only for devices with the screen that is wider
 *  than WindowWidthSizeClass.Compact.
 */
fun WindowSizeClass.isTopAppBarCenterAligned() = widthSizeClass != WindowWidthSizeClass.Compact


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AidventoryTopAppBar(
    modifier: Modifier = Modifier,
    title: String = "",
    centerAligned: Boolean = false,
    navigationIcon: ImageVector? = null,
    navigationIconContentDescription: String? = null,
    onNavigationClick: () -> Unit = {},
    colors: TopAppBarColors = TopAppBarDefaults.centerAlignedTopAppBarColors(),
    scrollBehavior: TopAppBarScrollBehavior? = null,
    actions: @Composable RowScope.() -> Unit = {},
) {

    when {
        navigationIcon != null && centerAligned -> {
            AidventoryCenterAlignedTopAppBar(
                title = title,
                navigationIcon = navigationIcon,
                navigationIconContentDescription = navigationIconContentDescription,
                onNavigationClick = onNavigationClick,
                modifier = modifier,
                colors = colors,
                scrollBehavior = scrollBehavior,
                actions = actions
            )
        }
        navigationIcon != null && !centerAligned -> {
            AidventoryTopAppBar(
                title = title,
                navigationIcon = navigationIcon,
                navigationIconContentDescription = navigationIconContentDescription,
                onNavigationClick = onNavigationClick,
                modifier = modifier,
                colors = colors,
                scrollBehavior = scrollBehavior,
                actions = actions
            )
        }
        navigationIcon == null && centerAligned -> {
            AidventoryCenterAlignedTopAppBar(
                title = title,
                modifier = modifier,
                colors = colors,
                scrollBehavior = scrollBehavior,
                actions = actions
            )
        }
        else -> {
            AidventoryTopAppBar(
                title = title,
                modifier = modifier,
                colors = colors,
                scrollBehavior = scrollBehavior,
                actions = actions
            )
        }
    }
}

/**
 *  [TopAppBar] with a navigation icon.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AidventoryTopAppBar(
    title: String,
    navigationIcon: ImageVector,
    navigationIconContentDescription: String?,
    onNavigationClick: () -> Unit,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {},
    colors: TopAppBarColors = TopAppBarDefaults.centerAlignedTopAppBarColors(),
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    TopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavigationClick) {
                Icon(
                    imageVector = navigationIcon,
                    contentDescription = navigationIconContentDescription,
                )
            }
        },
        actions = actions,
        colors = colors,
        scrollBehavior = scrollBehavior
    )
}

/**
 *  [CenterAlignedTopAppBar] with a navigation icon.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AidventoryCenterAlignedTopAppBar(
    title: String,
    navigationIcon: ImageVector,
    navigationIconContentDescription: String?,
    onNavigationClick: () -> Unit,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {},
    colors: TopAppBarColors = TopAppBarDefaults.centerAlignedTopAppBarColors(),
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavigationClick) {
                Icon(
                    imageVector = navigationIcon,
                    contentDescription = navigationIconContentDescription,
                )
            }
        },
        actions = actions,
        colors = colors,
        scrollBehavior = scrollBehavior
    )
}

/**
 *  [TopAppBar] without a navigation icon.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AidventoryTopAppBar(
    title: String,
    actions: @Composable RowScope.() -> Unit,
    modifier: Modifier = Modifier,
    colors: TopAppBarColors = TopAppBarDefaults.centerAlignedTopAppBarColors(),
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    TopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        actions = actions,
        colors = colors,
        scrollBehavior = scrollBehavior
    )
}

/**
 *  [CenterAlignedTopAppBar] without a navigation icon.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AidventoryCenterAlignedTopAppBar(
    title: String,
    actions: @Composable RowScope.() -> Unit,
    modifier: Modifier = Modifier,
    colors: TopAppBarColors = TopAppBarDefaults.centerAlignedTopAppBarColors(),
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        actions = actions,
        colors = colors,
        scrollBehavior = scrollBehavior
    )
}

@Composable
fun AidventoryTopAppBarAction(
    icon: ImageVector,
    iconContentDescription: String?,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        modifier = modifier,
        onClick = onActionClick
    ) {
        Icon(imageVector = icon, contentDescription = iconContentDescription)
    }
}