package com.aidventory.feature.supplies.presentation.movesupply

import android.Manifest
import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.aidventory.core.domain.entities.Container
import com.aidventory.core.barcode.processing.BarcodeProcessor
import com.aidventory.core.barcode.camera.BarcodeScannerLayout
import com.aidventory.core.barcode.camera.barcodeScannerLayoutModifier
import com.aidventory.core.barcode.camera.torchToggleContentDescription
import com.aidventory.core.barcode.camera.torchToggleIcon
import com.aidventory.feature.supplies.R
import kotlinx.coroutines.launch

@Composable
internal fun MoveSupplyScreen(
    windowWidthSizeClass: WindowWidthSizeClass,
    navController: NavHostController,
    viewModel: MoveSupplyViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    if (state.isDone) {
        LaunchedEffect(Unit) {
            navController.navigateUp()
        }
    }

    MoveSupplyContent(
        modifier = Modifier.fillMaxSize(),
        widthSizeClass = windowWidthSizeClass,
        containers = state.containers,
        mode = state.mode,
        onNavigateUp = navController::navigateUp,
        onChooseModeClick = viewModel::selectChooseMode,
        onScanModeClick = viewModel::selectScanMode,
        onStartScanningClick = viewModel::startScanning,
        onContainerClick = viewModel::chooseContainer,
        onBarcodeProcessorStateChanged = viewModel::changeBarcodeProcessorState
    )
}

@Composable
private fun MoveSupplyContent(
    widthSizeClass: WindowWidthSizeClass,
    containers: List<Container>,
    mode: MoveSupplyUiState.Mode,
    onNavigateUp: () -> Unit,
    onChooseModeClick: () -> Unit,
    onScanModeClick: () -> Unit,
    onStartScanningClick: () -> Unit,
    onContainerClick: (String) -> Unit,
    onBarcodeProcessorStateChanged: (BarcodeProcessor.State) -> Unit,
    modifier: Modifier = Modifier
) {
    if (containers.isEmpty()) {
        EmptyContent(
            modifier = modifier,
            onNavigateUp = onNavigateUp
        )
    } else {
        when (mode) {
            is MoveSupplyUiState.Mode.Choose -> {
                ChooseModeContent(
                    modifier = modifier,
                    isTwoColumn = widthSizeClass != WindowWidthSizeClass.Compact,
                    containers = containers,
                    onScanModeClick = onScanModeClick,
                    onNavigateUp = onNavigateUp,
                    onContainerClick = onContainerClick
                )
            }

            is MoveSupplyUiState.Mode.Scan -> {
                ScanModeContent(
                    widthSizeClass = widthSizeClass,
                    state = mode,
                    onBarcodeProcessorStateChanged = onBarcodeProcessorStateChanged,
                    onContainerClick = onContainerClick,
                    onNavigateUp = onNavigateUp,
                    onChooseModeClick = onChooseModeClick,
                    onStartScanningClick = onStartScanningClick
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EmptyContent(
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {

    Column(modifier = modifier) {
        AidventoryTopAppBar(
            modifier = Modifier.fillMaxWidth(),
            onNavigationClick = onNavigateUp,
            navigationIcon = AidventoryIcons.ArrowBack.imageVector
        )
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                textAlign = TextAlign.Center,
                text = stringResource(R.string.move_supply_empty_text)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChooseModeContent(
    isTwoColumn: Boolean,
    containers: List<Container>,
    onScanModeClick: () -> Unit,
    onNavigateUp: () -> Unit,
    onContainerClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        TopAppBar(
            navigationIcon = {
                IconButton(onClick = onNavigateUp) {
                    Icon(
                        imageVector = AidventoryIcons.ArrowBack.imageVector,
                        contentDescription = null
                    )
                }
            },
            title = {
                ModeSwitch(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    isChooseMode = true,
                    onChooseModeClick = {},
                    onScanModeClick = onScanModeClick
                )
            },
            actions = {
                IconButton(enabled = false, onClick = { }) {

                }
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(text = stringResource(R.string.move_supply_choose_mode_text))
            Spacer(modifier = Modifier.height(16.dp))

            if (isTwoColumn) {
                LazyVerticalGrid(columns = GridCells.Fixed(2)) {
                    itemsIndexed(
                        items = containers,
                        key = { _, item -> item.barcode }) { index, container ->
                        val isLastItem = if (containers.size % 2 == 0) {
                            // The grid has 2 columns so when there's the last row is full (2 items),
                            // then those items are both considered as a last item to which
                            // the bottom padding should be applied.
                            index == containers.lastIndex || index == containers.lastIndex - 1
                        } else {
                            index == containers.lastIndex
                        }
                        ContainerCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            container = container,
                            isLastItem = isLastItem,
                            onContainerClick = { onContainerClick(container.barcode) }
                        )
                    }
                }
            } else {
                LazyColumn {
                    itemsIndexed(
                        items = containers,
                        key = { _, item -> item.barcode }) { index, container ->
                        val isLastItem = index == containers.lastIndex
                        ContainerCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            container = container,
                            isLastItem = isLastItem,
                            onContainerClick = onContainerClick
                        )
                    }
                }
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContainerCard(
    container: Container,
    isLastItem: Boolean,
    onContainerClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier
            .then(if (isLastItem) Modifier.navigationBarsPadding() else Modifier),
        onClick = { onContainerClick(container.barcode) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = container.name,
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun ScanModeContent(
    widthSizeClass: WindowWidthSizeClass,
    state: MoveSupplyUiState.Mode.Scan,
    onBarcodeProcessorStateChanged: (BarcodeProcessor.State) -> Unit,
    onContainerClick: (String) -> Unit,
    onNavigateUp: () -> Unit,
    onChooseModeClick: () -> Unit,
    onStartScanningClick: () -> Unit
) {
    // A flag that is used to bind or unbind camera use cases (preview, image analysis).
    val isCameraActive = state is MoveSupplyUiState.Mode.Scan.Sense
            || state is MoveSupplyUiState.Mode.Scan.Recognize

    // A flag to decide whether to show or hide a scanner box animation.
    val isSensing = state is MoveSupplyUiState.Mode.Scan.Sense

    val coroutineScope = rememberCoroutineScope()
    val modalBottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    modalBottomSheetState.onDismiss(action = onStartScanningClick)

    val isBottomSheetExpanded = state is MoveSupplyUiState.Mode.Scan.NotFoundScanResult
            || state is MoveSupplyUiState.Mode.Scan.FoundScanResult

    if (isBottomSheetExpanded) {
        coroutineScope.launch { modalBottomSheetState.show() }
    } else {
        coroutineScope.launch { modalBottomSheetState.hide() }
    }

    /* A flag to check if it is the initial permission request.
              It's needed to decide whether to hide or show the message explaining the purpose of
              requesting the camera permission in the case when the permission is not granted and
              shouldShowRationale equals to false. */
    var isInitialPermissionRequest by rememberSaveable { mutableStateOf(true) }

    PermissionWrapper(
        permission = Manifest.permission.CAMERA,
        onLaunchPermissionRequest = { isInitialPermissionRequest = false },
        permissionNotGrantedContent = { shouldShowRationale, requestPermission ->
            PermissionNotGrantedContent(
                modifier = Modifier.fillMaxSize(),
                isInitialRequest = isInitialPermissionRequest,
                shouldShowRationale = shouldShowRationale,
                onNavigateUp = onNavigateUp,
                onChooseModeClick = onChooseModeClick,
                requestPermission = requestPermission
            )
        },
        content = {
            ModalBottomSheetLayout(
                sheetState = modalBottomSheetState,
                sheetBackgroundColor = Color.Transparent,
                sheetElevation = 0.dp,
                sheetContent = {
                    BottomSheetContent(
                        widthSizeClass = widthSizeClass,
                        state = state,
                        onScanClick = onStartScanningClick,
                        onContainerClick = onContainerClick,
                    )
                },
                content = {
                    BarcodeScannerLayout(
                        modifier = Modifier.barcodeScannerLayoutModifier(widthSizeClass),
                        onBarcodeProcessorStateChanged = onBarcodeProcessorStateChanged,
                        isActive = isCameraActive,
                        isSensing = isSensing
                    ) { toggleTorch, torchOn, scannerBox ->
                        /* Padding that should be applied to any composable that should be displayed
                        below the scanner box in order to avoid overlapping. */
                        val scannerBoxBottom = with(LocalDensity.current) {
                            scannerBox.bottom.toDp()
                        }
                        TopAppBar(
                            navigationIcon = {
                                IconButton(onClick = onNavigateUp) {
                                    Icon(
                                        imageVector = AidventoryIcons.ArrowBack.imageVector,
                                        contentDescription = null
                                    )
                                }
                            },
                            title = {
                                ModeSwitch(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp),
                                    isChooseMode = false,
                                    onChooseModeClick = onChooseModeClick,
                                    onScanModeClick = {}
                                )
                            },
                            actions = {
                                AidventoryTopAppBarAction(
                                    onActionClick = toggleTorch,
                                    iconContentDescription = torchOn.torchToggleContentDescription(),
                                    icon = torchOn.torchToggleIcon()
                                )
                            }
                        )

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
    onChooseModeClick: () -> Unit,
    requestPermission: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        TopAppBar(
            navigationIcon = {
                IconButton(onClick = onNavigateUp) {
                    Icon(
                        imageVector = AidventoryIcons.ArrowBack.imageVector,
                        contentDescription = stringResource(R.string.top_app_bar_action_navigate_up)
                    )
                }
            },
            title = {
                ModeSwitch(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    isChooseMode = false,
                    onChooseModeClick = onChooseModeClick,
                    onScanModeClick = {}
                )
            },
            actions = {
                IconButton(enabled = false, onClick = {  }) {
                    // Used as a placeholder to keep the ModeSwitch the same width.
                }
            }
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
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
                    FilledTonalButton(onClick = onChooseModeClick) {
                        Text(text = stringResource(R.string.scanner_button_go_back))
                    }
                }
            }
        }
    }
}


@Composable
private fun BottomSheetContent(
    widthSizeClass: WindowWidthSizeClass,
    state: MoveSupplyUiState.Mode.Scan,
    onScanClick: () -> Unit,
    onContainerClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var mutableState by remember {
        mutableStateOf<MoveSupplyUiState.Mode.Scan>(MoveSupplyUiState.Mode.Scan.NotFoundScanResult)
    }
    /* Check the instance of provided state values and update the local mutable state only when
    the values are instances of states that are used in this bottom sheet.
    This is required to not cancel a bottom sheet show/hide animation coroutine when the state value
    is other then ones in the following if condition.
    Launching show/hide suspend function is based on the state value. It means that
    the change of the state value invokes recomposition of this composable function before
    an animation coroutine is finished. As a result it produces a flicker effect instead of
    a swipe effect for showing and hiding the bottom sheet.*/
    if (state is MoveSupplyUiState.Mode.Scan.NotFoundScanResult ||
        state is MoveSupplyUiState.Mode.Scan.FoundScanResult
    ) {
        mutableState = state
    }

    BarcodeScannerBottomSheet(
        widthSizeClass = widthSizeClass,
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (mutableState) {
                is MoveSupplyUiState.Mode.Scan.NotFoundScanResult -> {
                    Text(text = stringResource(R.string.move_supply_not_found_result_text))
                    Spacer(modifier = Modifier.height(16.dp))
                    FilledTonalButton(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        onClick = onScanClick
                    ) {
                        Text(text = stringResource(R.string.button_scan_again))
                    }
                }

                is MoveSupplyUiState.Mode.Scan.FoundScanResult -> {
                    Text(
                        text = stringResource(R.string.move_supply_found_result_text) +
                                " \"${(mutableState as MoveSupplyUiState.Mode.Scan.FoundScanResult).container.name}\""
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    FilledTonalButton(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        onClick = {
                            onContainerClick((mutableState as MoveSupplyUiState.Mode.Scan.FoundScanResult).container.barcode)
                        }
                    ) {
                        Text(text = stringResource(R.string.move_supply_button_choose_container))
                    }

                    Text(
                        modifier = Modifier.padding(vertical = 8.dp),
                        text = stringResource(R.string.or)
                    )

                    OutlinedButton(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        onClick = onScanClick
                    ) {
                        Text(text = stringResource(R.string.button_scan_again))
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
private fun ModeSwitch(
    isChooseMode: Boolean,
    onChooseModeClick: () -> Unit,
    onScanModeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center
    ) {
        TextButton(
            modifier = Modifier
                .widthIn(
                    min = 100.dp,
                    max = 200.dp
                ),
            onClick = onChooseModeClick,
            colors = ButtonDefaults.textButtonColors(
                containerColor = if (isChooseMode) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent,
                contentColor = if (isChooseMode) MaterialTheme.colorScheme.onSecondaryContainer else Color.Unspecified
            )
        ) {
            Text(text = stringResource(R.string.move_supply_button_choose))
        }

        TextButton(
            modifier = Modifier
                .widthIn(
                    min = 100.dp,
                    max = 200.dp
                ),
            onClick = onScanModeClick,
            colors = ButtonDefaults.textButtonColors(
                containerColor = if (isChooseMode) Color.Transparent else MaterialTheme.colorScheme.secondaryContainer,
                contentColor = if (isChooseMode) Color.Unspecified else MaterialTheme.colorScheme.onSecondaryContainer
            )
        ) {
            Text(text = stringResource(R.string.move_supply_button_scan))
        }
    }
}