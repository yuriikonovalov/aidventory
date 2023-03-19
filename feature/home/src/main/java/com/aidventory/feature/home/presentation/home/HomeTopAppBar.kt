package com.aidventory.feature.home.presentation.home

import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.aidventory.core.common.designsystem.component.AidventoryTopAppBar
import com.aidventory.core.common.designsystem.component.AidventoryTopAppBarAction
import com.aidventory.core.common.designsystem.component.isTopAppBarCenterAligned
import com.aidventory.core.common.designsystem.icon.AidventoryIcons
import com.aidventory.core.common.designsystem.theme.AidventoryTheme
import com.aidventory.core.utils.PreviewWindowSizeClass

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeTopAppBar(
    windowSizeClass: WindowSizeClass,
    title: String,
    modifier: Modifier = Modifier,
    onSearchClick: () -> Unit,
    onSettingsClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior? = null
) {

    AidventoryTopAppBar(
        modifier = modifier,
        title = title,
        centerAligned = windowSizeClass.isTopAppBarCenterAligned(),
        scrollBehavior = scrollBehavior
    ) {
        AidventoryTopAppBarAction(
            icon = AidventoryIcons.Search.imageVector,
            iconContentDescription = null,
            onActionClick = onSearchClick
        )
        AidventoryTopAppBarAction(
            icon = AidventoryIcons.SettingsBorder.imageVector,
            iconContentDescription = null,
            onActionClick = onSettingsClick
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, device = Devices.PHONE)
@Composable
private fun HomeTopAppBarPreviewPhone() {
    AidventoryTheme {
        HomeTopAppBar(
            windowSizeClass = PreviewWindowSizeClass.PHONE,
            title = "Home",
            onSearchClick = {},
            onSettingsClick = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, device = Devices.TABLET)
@Composable
private fun HomeTopAppBarPreviewTablet() {
    AidventoryTheme {
        HomeTopAppBar(
            windowSizeClass = PreviewWindowSizeClass.FOLDABLE,
            title = "Home",
            onSearchClick = {},
            onSettingsClick = {}
        )
    }
}