package com.aidventory.core.common.designsystem.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionWrapper(
    permission: String,
    onLaunchPermissionRequest: () -> Unit = {},
    permissionNotGrantedContent: @Composable (shouldShowRationale: Boolean, requestPermission: () -> Unit) -> Unit,
    content: @Composable () -> Unit = {}
) {
    val permissionState = rememberPermissionState(permission) {
        onLaunchPermissionRequest()
    }

    if (permissionState.status.isGranted) {
        content()
    } else {
        permissionNotGrantedContent(
            permissionState.status.shouldShowRationale,
            permissionState::launchPermissionRequest
        )
    }

    LaunchedEffect(Unit) {
        permissionState.launchPermissionRequest()
    }
}

