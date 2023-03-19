package com.aidventory.feature.supplies.presentation.addsupply

import android.Manifest
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aidventory.core.common.designsystem.component.BarcodeScannerBottomSheet
import com.aidventory.core.common.designsystem.component.PermissionWrapper
import com.aidventory.core.common.designsystem.component.onDismiss
import com.aidventory.core.common.designsystem.icon.AidventoryIcons
import com.aidventory.core.barcode.processing.BarcodeProcessor
import com.aidventory.core.barcode.camera.BarcodeScannerLayout
import com.aidventory.core.barcode.camera.TorchToggleButton
import com.aidventory.feature.supplies.R
import kotlinx.coroutines.launch


@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun AddSupplySupplyStepContent(
    widthSizeClass: WindowWidthSizeClass,
    state: AddSupplyUiState.SupplyScannerState,
    onBarcodeProcessorStateChanged: (BarcodeProcessor.State) -> Unit,
    onAddWithoutScanningClick: () -> Unit,
    onHideBottomSheet: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isCameraActive = state !is AddSupplyUiState.SupplyScannerState.AlreadyExistScanResult
    val isSensing = state is AddSupplyUiState.SupplyScannerState.Sense
    var isInitialRequest by rememberSaveable { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    val modalBottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    modalBottomSheetState.onDismiss(action = onHideBottomSheet)

    val isBottomSheetExpanded = state is AddSupplyUiState.SupplyScannerState.AlreadyExistScanResult

    if (isBottomSheetExpanded) {
        coroutineScope.launch { modalBottomSheetState.show() }
    } else {
        coroutineScope.launch { modalBottomSheetState.hide() }
    }

    PermissionWrapper(
        permission = Manifest.permission.CAMERA,
        onLaunchPermissionRequest = { isInitialRequest = false },
        permissionNotGrantedContent = { shouldShowRationale, requestPermission ->
            PermissionNotGrantedContent(
                modifier = modifier,
                isInitialRequest = isInitialRequest,
                shouldShowRationale = shouldShowRationale,
                onContinueToNextStep = onAddWithoutScanningClick,
                requestPermission = requestPermission
            )
        },
        content = {
            ModalBottomSheetLayout(
                modifier = Modifier.addSupplyBarcodeScannerLayoutModifier(widthSizeClass),
                sheetBackgroundColor = Color.Transparent,
                sheetState = modalBottomSheetState,
                sheetElevation = 0.dp,
                sheetContent = {
                    SupplyAlreadyExistBottomSheet(
                        widthSizeClass = widthSizeClass,
                        state = state,
                        onScanClick = onHideBottomSheet
                    )
                },
                content = {
                    SupplyBarcodeScannerLayout(
                        isCameraActive = isCameraActive,
                        isSensing = isSensing,
                        onBarcodeProcessorStateChanged = onBarcodeProcessorStateChanged,
                        onAddWithoutScanningClick = onAddWithoutScanningClick
                    )
                }
            )
        }
    )
}

@Composable
private fun PermissionNotGrantedContent(
    isInitialRequest: Boolean,
    shouldShowRationale: Boolean,
    onContinueToNextStep: () -> Unit,
    requestPermission: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .navigationBarsPadding()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (shouldShowRationale) {
            Text(text = stringResource(R.string.add_supply_supply_scanner_rationale))
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(onClick = requestPermission) {
                Text(text = stringResource(R.string.scanner_button_grant_permission))
            }
            Text(
                modifier = Modifier.padding(vertical = 8.dp),
                text = stringResource(R.string.or)
            )
            FilledTonalButton(onClick = onContinueToNextStep) {
                Text(text = stringResource(R.string.add_supply_supply_scanner_continue))
            }
        } else {
            // In this case the permission is not granted and rationale should not be show
            // but the moving to the next step should not happen as well.
            if (!isInitialRequest) {
                Text(text = stringResource(R.string.add_supply_supply_scanner_permission_not_granted))
                Spacer(modifier = Modifier.height(16.dp))
                FilledTonalButton(onClick = onContinueToNextStep) {
                    Text(text = stringResource(R.string.add_supply_supply_scanner_continue))
                }
            }
        }
    }
}

@Composable
private fun SupplyBarcodeScannerLayout(
    isCameraActive: Boolean,
    isSensing: Boolean,
    onBarcodeProcessorStateChanged: (BarcodeProcessor.State) -> Unit,
    onAddWithoutScanningClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    BarcodeScannerLayout(
        modifier = modifier,
        onBarcodeProcessorStateChanged = onBarcodeProcessorStateChanged,
        isSensing = isSensing,
        isActive = isCameraActive
    ) { toggleTorch, isTorchOn, _ ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.align(Alignment.BottomCenter)) {
                TorchToggleButton(
                    modifier = Modifier.align(Alignment.End),
                    isTorchOn = isTorchOn,
                    onClick = toggleTorch
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    text = stringResource(R.string.add_supply_or_text),
                    textAlign = TextAlign.Center,
                    color = Color.White
                )

                FilledTonalButton(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .safeContentPadding()
                        .padding(bottom = 16.dp),
                    onClick = onAddWithoutScanningClick
                ) {
                    Icon(
                        imageVector = AidventoryIcons.Add.imageVector,
                        contentDescription = null
                    )
                    Text(stringResource(R.string.add_supply_scanner_step_button))
                }
            }
        }
    }
}

@Composable
private fun SupplyAlreadyExistBottomSheet(
    widthSizeClass: WindowWidthSizeClass,
    state: AddSupplyUiState.SupplyScannerState,
    onScanClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf("") }
    if (state is AddSupplyUiState.SupplyScannerState.AlreadyExistScanResult) {
        name = state.supply.name
    }

    BarcodeScannerBottomSheet(
        modifier = modifier.navigationBarsPadding(),
        widthSizeClass = widthSizeClass
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = stringResource(R.string.add_supply_supply_already_exist))
            Text(
                modifier = Modifier.padding(vertical = 8.dp),
                text = name,
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(16.dp))
            FilledTonalButton(onClick = onScanClick) {
                Text(text = stringResource(R.string.button_scan_another_supply))
            }
        }
    }
}
