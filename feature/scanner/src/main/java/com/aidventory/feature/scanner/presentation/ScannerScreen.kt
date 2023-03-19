package com.aidventory.feature.scanner.presentation

import android.Manifest
import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.navOptions
import com.aidventory.core.common.designsystem.component.AidventoryTopAppBar
import com.aidventory.core.common.designsystem.component.AidventoryTopAppBarAction
import com.aidventory.core.common.designsystem.component.PermissionWrapper
import com.aidventory.core.common.designsystem.component.onDismiss
import com.aidventory.core.common.designsystem.icon.AidventoryIcons
import com.aidventory.core.barcode.processing.BarcodeProcessor
import com.aidventory.core.barcode.camera.BarcodeScannerLayout
import com.aidventory.core.barcode.camera.barcodeScannerLayoutModifier
import com.aidventory.feature.containers.navigation.navigateToContainersScreen
import com.aidventory.feature.scanner.R
import com.aidventory.feature.scanner.navigation.scannerNavigationRoute
import com.aidventory.feature.supplies.navigation.navigateToAddSupplyScreen
import com.aidventory.feature.supplies.navigation.navigateToSuppliesScreen
import kotlinx.coroutines.launch

@Composable
internal fun ScannerScreen(
    windowSizeClass: WindowSizeClass,
    navController: NavController,
    viewModel: ScannerViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    when (state) {
        is ScannerUiState.SupplyResult -> {
            LaunchedEffect(Unit) {
                navController.navigateToSuppliesScreen(
                    barcode = state.barcode,
                    navOptions = navOptions {
                        popUpTo(scannerNavigationRoute) { inclusive = true }
                    })
            }
        }

        is ScannerUiState.ContainerResult -> {
            LaunchedEffect(Unit) {
                navController.navigateToContainersScreen(
                    barcode = state.barcode,
                    navOptions = navOptions {
                        popUpTo(scannerNavigationRoute) { inclusive = true }
                    })
            }
        }

        else -> {
            // no-op, other cases are handled by child composables.
        }
    }

    // A flag that is used to bind or unbind camera use cases (preview, image analysis).
    val isCameraActive by remember {
        derivedStateOf {
            state is ScannerUiState.Sense || state is ScannerUiState.Recognize
        }
    }

    // A flag to decide whether to show or hide a scanner box animation.
    val isSensing by remember(state is ScannerUiState.Sense) {
        mutableStateOf(state is ScannerUiState.Sense)
    }

    ScannerScreenContent(
        widthSizeClass = windowSizeClass.widthSizeClass,
        isCameraActive = isCameraActive,
        isSensing = isSensing,
        state = state,
        onBarcodeProcessorStateChanged = viewModel::changeBarcodeProcessorState,
        onNavigateUp = navController::navigateUp,
        onScanClick = viewModel::startScanning,
        onSupplyResultClick = viewModel::selectSupplyResult,
        onContainerResultClick = viewModel::selectContainerResult,
        onAddSupplyClick = { barcode ->
            navController.navigateToAddSupplyScreen(barcode, navOptions {
                popUpTo(scannerNavigationRoute) { inclusive = true }
            })
        }
    )
}

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
private fun ScannerScreenContent(
    widthSizeClass: WindowWidthSizeClass,
    isCameraActive: Boolean,
    isSensing: Boolean,
    state: ScannerUiState,
    onBarcodeProcessorStateChanged: (BarcodeProcessor.State) -> Unit,
    onNavigateUp: () -> Unit,
    onScanClick: () -> Unit,
    onSupplyResultClick: (String) -> Unit,
    onContainerResultClick: (String) -> Unit,
    onAddSupplyClick: (String) -> Unit,
) {
    /* A flag to check if it is the initial permission request.
       It's needed to decide whether to hide or show the message explaining the purpose of
       requesting the camera permission in the case when the permission is not granted and
       shouldShowRationale equals to false. */
    var isInitialPermissionRequest by rememberSaveable { mutableStateOf(true) }

    val coroutineScope = rememberCoroutineScope()
    val modalBottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    modalBottomSheetState.onDismiss(action = onScanClick)

    val isBottomSheetExpanded =
        state is ScannerUiState.NotFoundResult || state is ScannerUiState.MultipleResult

    if (isBottomSheetExpanded) {
        coroutineScope.launch { modalBottomSheetState.show() }
    } else {
        coroutineScope.launch { modalBottomSheetState.hide() }
    }


    PermissionWrapper(
        permission = Manifest.permission.CAMERA,
        onLaunchPermissionRequest = { isInitialPermissionRequest = false },
        permissionNotGrantedContent = { shouldShowRationale, requestPermission ->
            PermissionNotGrantedContent(
                modifier = Modifier.fillMaxSize(),
                isInitialRequest = isInitialPermissionRequest,
                shouldShowRationale = shouldShowRationale,
                onNavigateUp = onNavigateUp,
                requestPermission = requestPermission
            )
        },
        content = {
            ModalBottomSheetLayout(
                modifier = Modifier.barcodeScannerLayoutModifier(widthSizeClass),
                sheetState = modalBottomSheetState,
                sheetBackgroundColor = Color.Transparent,
                sheetElevation = 0.dp,
                sheetContent = {
                    BottomSheetContent(
                        widthSizeClass = widthSizeClass,
                        state = state,
                        isTwoColumn = widthSizeClass != WindowWidthSizeClass.Compact,
                        onAddAsSupplyClick = onAddSupplyClick,
                        onScanClick = onScanClick,
                        onSupplyResultClick = onSupplyResultClick,
                        onContainerResultClick = onContainerResultClick
                    )
                },
                content = {
                    BarcodeScannerLayout(
                        onBarcodeProcessorStateChanged = onBarcodeProcessorStateChanged,
                        isActive = isCameraActive,
                        isSensing = isSensing
                    ) { toggleTorch, torchOn, scannerBox ->
                        /* Padding that should be applied to any composable that should be displayed
                        below the scanner box in order to avoid overlapping. */
                        val scannerBoxPadding = with(LocalDensity.current) {
                            scannerBox.bottom.toDp()
                        }
                        AidventoryTopAppBar(
                            modifier = Modifier.fillMaxWidth(),
                            navigationIcon = AidventoryIcons.ArrowBack.imageVector,
                            onNavigationClick = onNavigateUp
                        ) {
                            AidventoryTopAppBarAction(
                                onActionClick = toggleTorch,
                                iconContentDescription = stringResource(
                                    if (torchOn) {
                                        R.string.scanner_turn_torch_off
                                    } else {
                                        R.string.scanner_turn_torch_on
                                    }
                                ),
                                icon = if (torchOn) {
                                    AidventoryIcons.FlashOff.imageVector
                                } else {
                                    AidventoryIcons.FlashOn.imageVector
                                }
                            )
                        }
                        Box(
                            modifier = Modifier
                                .navigationBarsPadding()
                                .padding(top = scannerBoxPadding)
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                modifier = Modifier
                                    .clip(MaterialTheme.shapes.medium)
                                    .background(Color.Gray.copy(alpha = 0.5f))
                                    .padding(8.dp),
                                text = stringResource(R.string.quick_search_scanner_hint_text),
                                color = Color.White,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PermissionNotGrantedContent(
    isInitialRequest: Boolean,
    shouldShowRationale: Boolean,
    onNavigateUp: () -> Unit,
    requestPermission: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            AidventoryTopAppBar(
                modifier = Modifier.fillMaxWidth(),
                navigationIcon = AidventoryIcons.ArrowBack.imageVector,
                onNavigationClick = onNavigateUp
            )
        }
    ) { scaffoldPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            if (shouldShowRationale) {
                Text(text = stringResource(R.string.scanner_rationale))
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedButton(onClick = requestPermission) {
                    Text(text = stringResource(R.string.scanner_button_grant_permission))
                }
                Text(
                    modifier = Modifier.padding(vertical = 8.dp),
                    text = stringResource(R.string.scanner_or)
                )
                FilledTonalButton(onClick = onNavigateUp) {
                    Text(text = stringResource(R.string.scanner_button_go_back))
                }
            } else {
                // In this case the permission is not granted and rationale should not be show
                // but the moving to the next step should not happen as well.
                if (!isInitialRequest) {
                    Text(text = stringResource(R.string.scanner_permission_not_granted))
                    Spacer(modifier = Modifier.height(16.dp))
                    FilledTonalButton(onClick = onNavigateUp) {
                        Text(text = stringResource(R.string.scanner_button_go_back))
                    }
                }
            }
        }
    }
}