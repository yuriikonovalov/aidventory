package com.aidventory.feature.supplies.presentation.addsupply

import android.Manifest
import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.aidventory.core.common.designsystem.component.BarcodeScannerBottomSheet
import com.aidventory.core.common.designsystem.component.PermissionWrapper
import com.aidventory.core.common.designsystem.component.onDismiss
import com.aidventory.core.common.designsystem.icon.AidventoryIcons
import com.aidventory.core.domain.entities.Container
import com.aidventory.core.barcode.processing.BarcodeProcessor
import com.aidventory.core.barcode.camera.BarcodeScannerLayout
import com.aidventory.core.barcode.camera.TorchToggleButton
import com.aidventory.core.common.designsystem.component.dialogs.AppDialog
import com.aidventory.feature.supplies.R
import kotlinx.coroutines.launch

@Composable
internal fun AddSupplyContainerStepContent(
    widthSizeClass: WindowWidthSizeClass,
    state: AddSupplyUiState.ContainerStepState,
    containers: List<Container>,
    selectedContainer: Container?,
    isPreviousButtonVisible: Boolean,
    onNextClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onClearContainerClick: () -> Unit,
    onScanContainerClick: () -> Unit,
    onChooseContainerClick: () -> Unit,
    onContainerClick: (String) -> Unit,
    onBarcodeProcessorStateChanged: (BarcodeProcessor.State) -> Unit,
    onDismissChooseContainerModalContent: () -> Unit,
    onLeaveScanning: () -> Unit,
    onStartScanning: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (state) {
        is AddSupplyUiState.ContainerStepState.Scan -> {
            ContainerScannerContent(
                widthSizeClass = widthSizeClass,
                onBarcodeProcessorStateChanged = onBarcodeProcessorStateChanged,
                state = state,
                onGoBack = onLeaveScanning,
                onHideBottomSheet = onStartScanning,
                onScanClick = onStartScanning
            )
        }

        else -> {
            ContainerContent(
                modifier = modifier,
                widthSizeClass = widthSizeClass,
                state = state,
                containers = containers,
                selectedContainer = selectedContainer,
                isPreviousButtonVisible = isPreviousButtonVisible,
                onSaveClick = onNextClick,
                onPreviousClick = onPreviousClick,
                onClearContainerClick = onClearContainerClick,
                onScanContainerClick = onScanContainerClick,
                onChooseContainerClick = onChooseContainerClick,
                onContainerClick = onContainerClick,
                onDismissChooseContainerModalContent = onDismissChooseContainerModalContent
            )
        }
    }
}


@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ContainerScannerContent(
    widthSizeClass: WindowWidthSizeClass,
    state: AddSupplyUiState.ContainerStepState.Scan,
    onBarcodeProcessorStateChanged: (BarcodeProcessor.State) -> Unit,
    onGoBack: () -> Unit,
    onHideBottomSheet: () -> Unit,
    onScanClick: () -> Unit
) {
    var isInitialRequest by rememberSaveable { mutableStateOf(true) }
    val isCameraActive = state !is AddSupplyUiState.ContainerStepState.Scan.NotFoundScanResult
    val isSensing = state is AddSupplyUiState.ContainerStepState.Scan.Sense
    val coroutineScope = rememberCoroutineScope()

    val modalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )
    modalBottomSheetState.onDismiss(action = onHideBottomSheet)

    val isBottomSheetExpanded by remember(state) {
        mutableStateOf(state is AddSupplyUiState.ContainerStepState.Scan.NotFoundScanResult)
    }

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
                isInitialRequest = isInitialRequest,
                shouldShowRationale = shouldShowRationale,
                onGoBack = onGoBack,
                requestPermission = requestPermission
            )
        },
        content = {
            ModalBottomSheetLayout(
                modifier = Modifier.addSupplyBarcodeScannerLayoutModifier(widthSizeClass),
                sheetState = modalBottomSheetState,
                sheetElevation = 0.dp,
                sheetBackgroundColor = Color.Transparent,
                sheetContent = {
                    ContainerNotFoundBottomSheet(
                        widthSizeClass = widthSizeClass,
                        onScanClick = onScanClick
                    )
                },
                content = {
                    BarcodeScannerLayout(
                        onBarcodeProcessorStateChanged = onBarcodeProcessorStateChanged,
                        isActive = isCameraActive,
                        isSensing = isSensing
                    ) { toggleTorch, isTorchOn, scannerBox ->
                        /* Padding that should be applied to any composable that should be displayed
                        below the scanner box in order to avoid overlapping. */
                        val scannerBoxPadding = with(LocalDensity.current) {
                            scannerBox.bottom.toDp()
                        }
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.End
                        ) {
                            TorchToggleButton(
                                modifier = Modifier.align(Alignment.End),
                                isTorchOn = isTorchOn,
                                onClick = toggleTorch
                            )
                            Spacer(modifier = Modifier.weight(1f))
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


@Composable
private fun ContainerNotFoundBottomSheet(
    widthSizeClass: WindowWidthSizeClass,
    modifier: Modifier = Modifier,
    onScanClick: () -> Unit
) {
    BarcodeScannerBottomSheet(
        modifier = modifier.navigationBarsPadding(),
        widthSizeClass = widthSizeClass
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.padding(vertical = 16.dp),
                text = stringResource(R.string.add_supply_bottom_sheet_container_not_found)
            )
            FilledTonalButton(onClick = onScanClick) {
                Text(text = stringResource(R.string.button_scan_again))
            }
        }
    }
}

@Composable
private fun PermissionNotGrantedContent(
    isInitialRequest: Boolean,
    shouldShowRationale: Boolean,
    onGoBack: () -> Unit,
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
            Text(text = stringResource(R.string.add_supply_container_scanner_rationale))
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(onClick = requestPermission) {
                Text(text = stringResource(R.string.add_supply_container_scanner_button_grant_permission))
            }
            Text(
                modifier = Modifier.padding(vertical = 8.dp),
                text = stringResource(R.string.add_supply_container_scanner_button_or)
            )
            FilledTonalButton(onClick = onGoBack) {
                Text(text = stringResource(R.string.scanner_button_go_back))
            }
        } else {
            // In this case the permission is not granted and rationale should not be show
            // but the moving to the next step should not happen as well.
            if (!isInitialRequest) {
                Text(text = stringResource(R.string.scanner_permission_not_granted))
                Spacer(modifier = Modifier.height(16.dp))
                FilledTonalButton(onClick = onGoBack) {
                    Text(text = stringResource(R.string.scanner_button_go_back))
                }
            }
        }
    }
}


@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ContainerContent(
    widthSizeClass: WindowWidthSizeClass,
    state: AddSupplyUiState.ContainerStepState,
    selectedContainer: Container?,
    containers: List<Container>,
    onContainerClick: (String) -> Unit,
    onDismissChooseContainerModalContent: () -> Unit,
    isPreviousButtonVisible: Boolean,
    onSaveClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onClearContainerClick: () -> Unit,
    onScanContainerClick: () -> Unit,
    onChooseContainerClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()
    val isContainerDialogOpen by remember(state, widthSizeClass) {
        mutableStateOf(state.isContainerDialogOpen(widthSizeClass))
    }

    val modalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )
    modalBottomSheetState.onDismiss(
        condition = !isContainerDialogOpen,
        action = onDismissChooseContainerModalContent
    )

    val isBottomSheetExpanded by remember(state, widthSizeClass) {
        mutableStateOf(state.isBottomSheetExpanded(widthSizeClass))
    }

    if (isBottomSheetExpanded) {
        coroutineScope.launch { modalBottomSheetState.show() }
    } else {
        coroutineScope.launch { modalBottomSheetState.hide() }
    }


    ModalBottomSheetLayout(
        modifier = modifier,
        sheetState = modalBottomSheetState,
        sheetShape = MaterialTheme.shapes.large.copy(
            bottomStart = ZeroCornerSize,
            bottomEnd = ZeroCornerSize
        ),
        sheetContent = {
            ChooseContainerBottomSheet(
                containers = containers,
                onContainerClick = onContainerClick,
                selectedContainer = selectedContainer
            )
        },
        content = {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .navigationBarsPadding()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.add_supply_container_text),
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(32.dp))
                selectedContainer?.let {
                    SelectedContainer(
                        modifier = Modifier
                            .fillMaxWidth(if (widthSizeClass == WindowWidthSizeClass.Expanded) 0.6f else 1f)
                            .align(Alignment.CenterHorizontally),
                        container = it, onClearContainerClick = onClearContainerClick
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                ContainerSelectionModeButtons(
                    modifier = Modifier
                        .fillMaxWidth(if (widthSizeClass == WindowWidthSizeClass.Expanded) 0.6f else 1f)
                        .align(Alignment.CenterHorizontally),
                    onChooseContainerClick = onChooseContainerClick,
                    onScanContainerClick = onScanContainerClick
                )

                AlternativeText(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp)
                )

                Spacer(modifier = Modifier.weight(1f))
                StepButtons(
                    modifier = Modifier
                        .fillMaxWidth(if (widthSizeClass == WindowWidthSizeClass.Expanded) 0.6f else 1f)
                        .align(Alignment.CenterHorizontally),
                    nextButtonText = stringResource(R.string.add_supply_add_step_button_save),
                    previousButtonText = stringResource(R.string.add_supply_add_step_button_previous),
                    onNextClick = onSaveClick,
                    onPreviousClick = onPreviousClick,
                    isPreviousButtonVisible = isPreviousButtonVisible
                )
            }

        }
    )


    ContainerDialog(
        isContainerDialogOpen = isContainerDialogOpen,
        selectedContainer = selectedContainer,
        containers = containers,
        onContainerClick = onContainerClick,
        onDismiss = onDismissChooseContainerModalContent
    )


    BackHandler(modalBottomSheetState.isVisible) {
        coroutineScope.launch {
            modalBottomSheetState.hide()
        }
    }
}

private fun AddSupplyUiState.ContainerStepState.isContainerDialogOpen(
    widthSizeClass: WindowWidthSizeClass
): Boolean {
    return this is AddSupplyUiState.ContainerStepState.Choose && widthSizeClass != WindowWidthSizeClass.Compact
}

private fun AddSupplyUiState.ContainerStepState.isBottomSheetExpanded(
    widthSizeClass: WindowWidthSizeClass
): Boolean {
    return this is AddSupplyUiState.ContainerStepState.Choose && widthSizeClass == WindowWidthSizeClass.Compact
}


@Composable
private fun AlternativeText(
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = R.string.add_supply_or_text),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = R.string.add_supply_step_container_alternative_text),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelLarge
        )
    }
}


@Composable
private fun ContainerSelectionModeButtons(
    onChooseContainerClick: () -> Unit,
    onScanContainerClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        OutlinedButton(
            modifier = Modifier.weight(1f),
            onClick = onChooseContainerClick
        ) {
            Text(text = stringResource(id = R.string.add_supply_container_choose_button_text))
        }

        Spacer(modifier = Modifier.width(16.dp))

        OutlinedButton(
            modifier = Modifier.weight(1f),
            onClick = onScanContainerClick
        ) {
            Text(text = stringResource(id = R.string.add_supply_container_scan_barcode_button_text))
        }
    }
}

@Composable
private fun SelectedContainer(
    container: Container,
    modifier: Modifier = Modifier,
    onClearContainerClick: () -> Unit
) {
    Card(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 16.dp, bottom = 16.dp, end = 8.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(id = R.string.add_supply_container_current_container_text),
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    text = container.name,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            IconButton(
                modifier = Modifier.align(Alignment.CenterVertically),
                onClick = onClearContainerClick
            ) {
                Icon(
                    AidventoryIcons.Close.imageVector,
                    contentDescription = stringResource(R.string.add_supply_container_step_button_clear_selected_container)
                )
            }
        }
    }
}

@Composable
private fun ContainerDialog(
    isContainerDialogOpen: Boolean,
    selectedContainer: Container?,
    containers: List<Container>,
    onContainerClick: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AppDialog(
        modifier = modifier,
        open = isContainerDialogOpen,
        title = { },
        content = {
            Spacer(modifier = Modifier.height(16.dp))
            ContainerModalContent(
                selectedContainer = selectedContainer,
                containers = containers,
                onContainerClick = onContainerClick
            )
        },
        negativeButtonText = stringResource(R.string.supplies_dialog_button_close),
        onNegativeButtonClick = onDismiss
    )
}

@Composable
private fun ChooseContainerBottomSheet(
    selectedContainer: Container?,
    containers: List<Container>,
    onContainerClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(modifier = modifier) {
        Column(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 8.dp)
                .navigationBarsPadding(),
        ) {
            Box(
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .width(40.dp)
                    .height(2.dp)
                    .align(Alignment.CenterHorizontally)
                    .clip(RoundedCornerShape(1.dp))
                    .background(LocalContentColor.current)
            )
            ContainerModalContent(
                modifier = Modifier,
                selectedContainer = selectedContainer,
                containers = containers,
                onContainerClick = onContainerClick
            )
        }

    }
}

@Composable
private fun ContainerModalContent(
    selectedContainer: Container?,
    containers: List<Container>,
    onContainerClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (containers.isEmpty()) {
        Box(modifier = modifier.padding(16.dp)) {
            Text(text = stringResource(R.string.add_supply_container_step_empty_text))
        }
    } else {
        LazyColumn(modifier = modifier) {
            items(items = containers, key = { it.barcode }) { container ->
                val selected = selectedContainer?.barcode == container.barcode
                ModalListItem(
                    modifier = Modifier.fillMaxWidth(),
                    container = container,
                    onClick = onContainerClick,
                    selected = selected
                )
            }
        }
    }
}

@Composable
private fun ModalListItem(
    container: Container,
    selected: Boolean,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = { onClick(container.barcode) })
    ) {
        RadioButton(selected = selected, onClick = { onClick(container.barcode) })
        Text(
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.CenterVertically),
            text = container.name
        )
    }
}