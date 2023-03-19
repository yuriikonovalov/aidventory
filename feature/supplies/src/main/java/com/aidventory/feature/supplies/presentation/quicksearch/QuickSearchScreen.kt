package com.aidventory.feature.supplies.presentation.quicksearch

import android.Manifest
import android.annotation.SuppressLint
import androidx.annotation.RawRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
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
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.aidventory.core.common.designsystem.component.AidventoryTopAppBar
import com.aidventory.core.common.designsystem.component.AidventoryTopAppBarAction
import com.aidventory.core.common.designsystem.component.BarcodeScannerBottomSheet
import com.aidventory.core.common.designsystem.component.PermissionWrapper
import com.aidventory.core.common.designsystem.component.onDismiss
import com.aidventory.core.common.designsystem.icon.AidventoryIcons
import com.aidventory.core.barcode.processing.BarcodeProcessor
import com.aidventory.core.barcode.camera.BarcodeScannerLayout
import com.aidventory.core.barcode.camera.barcodeScannerLayoutModifier
import com.aidventory.core.barcode.camera.torchToggleContentDescription
import com.aidventory.core.barcode.camera.torchToggleIcon
import com.aidventory.feature.supplies.R
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import kotlinx.coroutines.launch

@Composable
internal fun QuickSearchScreen(
    windowWidthSizeClass: WindowWidthSizeClass,
    navController: NavHostController,
    viewModel: QuickSearchViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    // A flag that is used to bind or unbind camera use cases (preview, image analysis).
    val isCameraActive = state.scanState is QuickSearchUiState.ScanState.Sense
            || state.scanState is QuickSearchUiState.ScanState.Recognize

    // A flag to decide whether to show or hide a scanner box animation.
    val isSensing = state.scanState is QuickSearchUiState.ScanState.Sense

    QuickSearchContent(
        widthSizeClass = windowWidthSizeClass,
        supplyName = state.supply?.name ?: "",
        state = state.scanState,
        isCameraActive = isCameraActive,
        isSensing = isSensing,
        onBarcodeProcessorStateChanged = viewModel::changeBarcodeProcessorState,
        onStartScanning = viewModel::startScanning,
        onNavigateUp = navController::navigateUp
    )
}

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun QuickSearchContent(
    widthSizeClass: WindowWidthSizeClass,
    supplyName: String,
    state: QuickSearchUiState.ScanState,
    isCameraActive: Boolean,
    isSensing: Boolean,
    onBarcodeProcessorStateChanged: (BarcodeProcessor.State) -> Unit,
    onStartScanning: () -> Unit,
    onNavigateUp: () -> Unit
) {
    /* A flag to check if it is the initial permission request.
           It's needed to decide whether to hide or show the message explaining the purpose of
           requesting the camera permission in the case when the permission is not granted and
           shouldShowRationale equals to false. */
    var isInitialPermissionRequest by rememberSaveable { mutableStateOf(true) }

    val coroutineScope = rememberCoroutineScope()
    val modalBottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    modalBottomSheetState.onDismiss(action = onStartScanning)

    val isBottomSheetExpanded = state is QuickSearchUiState.ScanState.NotFoundScanResult
            || state is QuickSearchUiState.ScanState.FoundScanResult

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
                        onScanClick = onStartScanning,
                        onDoneClick = onNavigateUp
                    )
                },
                content = {
                    BarcodeScannerLayout(
                        onBarcodeProcessorStateChanged = onBarcodeProcessorStateChanged,
                        isActive = isCameraActive,
                        isSensing = isSensing
                    ) { toggleTorch, torchOn, scannerBox ->
                        val scannerBoxTop = with(LocalDensity.current) {
                            scannerBox.top.toDp()
                        }
                        /* Padding that should be applied to any composable that should be displayed
                        below the scanner box in order to avoid overlapping. */
                        val scannerBoxBottom = with(LocalDensity.current) {
                            scannerBox.bottom.toDp()
                        }
                        AidventoryTopAppBar(
                            modifier = Modifier.fillMaxWidth(),
                            navigationIcon = AidventoryIcons.ArrowBack.imageVector,
                            onNavigationClick = onNavigateUp
                        ) {
                            AidventoryTopAppBarAction(
                                onActionClick = toggleTorch,
                                iconContentDescription = torchOn.torchToggleContentDescription(),
                                icon = torchOn.torchToggleIcon()
                            )
                        }
                        Box(
                            modifier = Modifier
                                .height(scannerBoxTop)
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Column {
                                Text(
                                    text = stringResource(R.string.quick_search_looking_for_label),
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = supplyName,
                                    color = Color.White,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .navigationBarsPadding()
                                .padding(top = scannerBoxBottom)
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
                Text(text = stringResource(R.string.quick_search_scanner_rationale))
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedButton(onClick = requestPermission) {
                    Text(text = stringResource(R.string.scanner_button_grant_permission))
                }
                Text(
                    modifier = Modifier.padding(vertical = 8.dp),
                    text = stringResource(R.string.or)
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


@Composable
internal fun BottomSheetContent(
    widthSizeClass: WindowWidthSizeClass,
    state: QuickSearchUiState.ScanState,
    onScanClick: () -> Unit,
    onDoneClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var mutableState by remember {
        mutableStateOf<QuickSearchUiState.ScanState>(QuickSearchUiState.ScanState.NotFoundScanResult)
    }
    // Update mutable state only when the state value is one of the used is this bottom sheet.
    if (state is QuickSearchUiState.ScanState.NotFoundScanResult ||
        state is QuickSearchUiState.ScanState.FoundScanResult
    ) {
        mutableState = state
    }

    // A flag to restart a lottie animation.
    // Changing to one of the following states is only possible from other states,
    // so this checking behaves as a reset flag for LottieAnimation.
    val isPlaying = state is QuickSearchUiState.ScanState.NotFoundScanResult ||
            state is QuickSearchUiState.ScanState.FoundScanResult

    BarcodeScannerBottomSheet(
        modifier = modifier.navigationBarsPadding(),
        widthSizeClass = widthSizeClass
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (mutableState) {
                is QuickSearchUiState.ScanState.NotFoundScanResult -> {
                    ResultLottieAnimation(
                        isPlaying = isPlaying,
                        rawRes = R.raw.failed_status
                    )
                    FilledTonalButton(onClick = onScanClick) {
                        Text(text = stringResource(R.string.quick_search_button_scan))
                    }
                }

                is QuickSearchUiState.ScanState.FoundScanResult -> {
                    ResultLottieAnimation(
                        isPlaying = isPlaying,
                        rawRes = R.raw.success_status
                    )
                    OutlinedButton(onClick = onDoneClick) {
                        Text(text = stringResource(R.string.quick_search_button_done))
                    }
                }

                else -> {
                    // no-op, other cases are handled by the parent composable.
                }
            }

        }
    }
}

@Composable
private fun ResultLottieAnimation(
    @RawRes rawRes: Int,
    isPlaying: Boolean
) {
    val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(rawRes))
    LottieAnimation(
        modifier = Modifier
            .sizeIn(
                minWidth = 100.dp,
                minHeight = 100.dp,
                maxWidth = 200.dp,
                maxHeight = 200.dp
            ),
        composition = composition,
        isPlaying = isPlaying
    )
}

