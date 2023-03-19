package com.aidventory.feature.supplies.presentation.addsupply

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.aidventory.core.domain.entities.SupplyUse
import com.aidventory.core.barcode.processing.BarcodeProcessor
import com.aidventory.core.common.designsystem.component.dialogs.AppDialog
import com.aidventory.core.common.designsystem.component.dialogs.AppDialogMessageContent
import com.aidventory.core.common.designsystem.component.dialogs.AppDialogTitle
import com.aidventory.feature.supplies.R
import java.time.LocalDate


@Composable
internal fun AddSupplyScreen(
    windowWidthSizeClass: WindowWidthSizeClass,
    navController: NavHostController,
    viewModel: AddSupplyViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    if (state.isDone) {
        LaunchedEffect(Unit) {
            navController.navigateUp()
        }
    }

    var isLeavingConfirmationDialogOpen by remember { mutableStateOf(false) }

    val onNavigateClick = if (state.isContainerScanning()) {
        viewModel::closeContainerScanner
    } else {
        { isLeavingConfirmationDialogOpen = true }
    }

    AddSupplyScreenContent(
        modifier = Modifier
            .fillMaxSize()
            .addSupplyScreenModifier(widthSizeClass = windowWidthSizeClass),
        widthSizeClass = windowWidthSizeClass,
        state = state,
        onNavigationClick = onNavigateClick,
        onNextClick = viewModel::toNextStep,
        onPreviousClick = viewModel::toPreviousStep,
        onScanSupply = viewModel::scanSupply,
        onBarcodeProcessorStateChange = viewModel::changeBarcodeProcessorState,
        onNameChange = viewModel::changeName,
        onSupplyUseClick = viewModel::clickSupplyUse,
        onExpiryChange = viewModel::changeExpiry,
        onChooseContainerClick = viewModel::clickChooseContainer,
        onScanContainerClick = viewModel::clickScanContainer,
        onContainerChanged = viewModel::selectContainer,
        onClearSelectedContainer = viewModel::clearSelectedContainer,
        onAddSupplyClick = viewModel::addSupply,
        onDismissChooseContainerModalContent = viewModel::hideChooseContainerView,
        onLeaveScanning = viewModel::closeContainerScanner,
        onStartContainerScanning = viewModel::clickScanContainer
    )

    LeavingAlertDialog(
        open = isLeavingConfirmationDialogOpen,
        onConfirmButtonClick = {
            isLeavingConfirmationDialogOpen = false // Close the dialog.
            navController.navigateUp() // Navigate from the screen.
        },
        onDismissButtonClick = { isLeavingConfirmationDialogOpen = false }
    )

    BackHandler(enabled = true) {
        if (state.isContainerScanning()) {
            viewModel.closeContainerScanner()
        } else {
            isLeavingConfirmationDialogOpen = true
        }
    }
}

private fun Modifier.addSupplyScreenModifier(widthSizeClass: WindowWidthSizeClass): Modifier {
    return when (widthSizeClass) {
        WindowWidthSizeClass.Compact -> this
        WindowWidthSizeClass.Medium -> this.padding(horizontal = 16.dp)
        WindowWidthSizeClass.Expanded -> this.padding(start = 32.dp, end = 16.dp)
        else -> this
    }
}

internal fun Modifier.addSupplyBarcodeScannerLayoutModifier(widthSizeClass: WindowWidthSizeClass): Modifier =
    composed {
        when (widthSizeClass) {
            WindowWidthSizeClass.Compact -> this
            else -> this
                .navigationBarsPadding()
                .padding(bottom = 16.dp)
                .clip(
                    MaterialTheme.shapes.large.copy(
                        topStart = ZeroCornerSize,
                        topEnd = ZeroCornerSize
                    )
                )
        }
    }

@Composable
private fun LeavingAlertDialog(
    open: Boolean,
    onConfirmButtonClick: () -> Unit,
    onDismissButtonClick: () -> Unit,
) {
    AppDialog(
        open = open,
        onNegativeButtonClick = onDismissButtonClick,
        title = { AppDialogTitle(text = stringResource(R.string.add_supply_leaving_dialog_title)) },
        content = {
            AppDialogMessageContent(
                text = stringResource(R.string.add_supply_leaving_dialog_text)
            )
        },
        onPositiveButtonClick = onConfirmButtonClick,
        positiveButtonText = stringResource(R.string.add_supply_leaving_dialog_confirm_button),
        negativeButtonText = stringResource(R.string.add_supply_leaving_dialog_dismiss_button)
    )
}

private fun AddSupplyUiState.isContainerScanning(): Boolean {
    return step is AddSupplyUiState.Step.Container
            && step.containerStepState is AddSupplyUiState.ContainerStepState.Scan
}

@Composable
private fun AddSupplyScreenContent(
    widthSizeClass: WindowWidthSizeClass,
    state: AddSupplyUiState,
    onNavigationClick: () -> Unit,
    onNextClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onScanSupply: () -> Unit,
    onBarcodeProcessorStateChange: (BarcodeProcessor.State) -> Unit,
    onNameChange: (String) -> Unit,
    onSupplyUseClick: (SupplyUse) -> Unit,
    onExpiryChange: (LocalDate?) -> Unit,
    onChooseContainerClick: () -> Unit,
    onScanContainerClick: () -> Unit,
    onContainerChanged: (String) -> Unit,
    onClearSelectedContainer: () -> Unit,
    onAddSupplyClick: () -> Unit,
    modifier: Modifier = Modifier,
    onDismissChooseContainerModalContent: () -> Unit,
    onLeaveScanning: () -> Unit,
    onStartContainerScanning: () -> Unit
) {
    Column(modifier = modifier) {
        AddSupplyTopAppBar(
            stepOrdinal = state.step.ordinal,
            totalSteps = state.step.totalSteps,
            title = stringResource(state.step.asStringRes()),
            onNavigationClick = onNavigationClick
        )

        Box(modifier = Modifier.weight(1f)) {
            when (state.step) {
                is AddSupplyUiState.Step.Supply -> {
                    AddSupplySupplyStepContent(
                        modifier = modifier.fillMaxSize(),
                        widthSizeClass = widthSizeClass,
                        state = state.step.supplyScannerState,
                        onBarcodeProcessorStateChanged = onBarcodeProcessorStateChange,
                        onAddWithoutScanningClick = onNextClick,
                        onHideBottomSheet = onScanSupply
                    )
                }

                is AddSupplyUiState.Step.Name -> {
                    AddSupplyNameStepContent(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .navigationBarsPadding()
                            .imePadding()
                            .padding(16.dp),
                        isExpanded = widthSizeClass == WindowWidthSizeClass.Expanded,
                        name = state.name,
                        onNameChange = onNameChange,
                        isError = state.step.isError,
                        isPreviousButtonVisible = state.isPreviousButtonVisible,
                        onPreviousClick = onPreviousClick,
                        onNextClick = onNextClick
                    )
                }

                AddSupplyUiState.Step.SupplyUses -> {
                    AddSupplySupplyUsesStepContent(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .navigationBarsPadding()
                            .padding(16.dp),
                        isExpanded = widthSizeClass == WindowWidthSizeClass.Expanded,
                        supplyUses = state.supplyUses,
                        selectedSupplyUses = state.selectedSupplyUses,
                        onSupplyUseClick = onSupplyUseClick,
                        isPreviousButtonVisible = state.isPreviousButtonVisible,
                        onPreviousClick = onPreviousClick,
                        onNextClick = onNextClick
                    )
                }

                AddSupplyUiState.Step.Expiry -> {
                    AddSupplyExpiryDateContent(
                        modifier = Modifier
                            .fillMaxSize()
                            .navigationBarsPadding()
                            .padding(16.dp),
                        isExpanded = widthSizeClass == WindowWidthSizeClass.Expanded,
                        date = state.expiry,
                        onDateChange = onExpiryChange,
                        isPreviousButtonVisible = state.isPreviousButtonVisible,
                        onPreviousClick = onPreviousClick,
                        onNextClick = onNextClick
                    )
                }

                is AddSupplyUiState.Step.Container -> {
                    AddSupplyContainerStepContent(
                        modifier = Modifier.fillMaxSize(),
                        widthSizeClass = widthSizeClass,
                        state = state.step.containerStepState,
                        containers = state.containers,
                        selectedContainer = state.selectedContainer,
                        onNextClick = onAddSupplyClick,
                        onPreviousClick = onPreviousClick,
                        onClearContainerClick = onClearSelectedContainer,
                        onScanContainerClick = onScanContainerClick,
                        onContainerClick = onContainerChanged,
                        onBarcodeProcessorStateChanged = onBarcodeProcessorStateChange,
                        isPreviousButtonVisible = state.isPreviousButtonVisible,
                        onChooseContainerClick = onChooseContainerClick,
                        onDismissChooseContainerModalContent = onDismissChooseContainerModalContent,
                        onLeaveScanning = onLeaveScanning,
                        onStartScanning = onStartContainerScanning
                    )
                }
            }
        }
    }
}

@Composable
internal fun StepButtons(
    nextButtonText: String,
    previousButtonText: String,
    onNextClick: () -> Unit,
    onPreviousClick: () -> Unit,
    isPreviousButtonVisible: Boolean,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        OutlinedButton(
            modifier = Modifier
                .weight(1f)
                .padding(end = 16.dp),
            onClick = onPreviousClick,
            enabled = isPreviousButtonVisible
        ) {
            Text(previousButtonText)
        }

        FilledTonalButton(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp),
            onClick = onNextClick
        ) {
            Text(nextButtonText)
        }
    }
}