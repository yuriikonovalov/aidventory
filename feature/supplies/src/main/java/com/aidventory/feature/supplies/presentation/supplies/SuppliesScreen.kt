package com.aidventory.feature.supplies.presentation.supplies

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.window.layout.DisplayFeature
import com.aidventory.core.barcode.sendBarcodeIntent
import com.aidventory.core.common.AppDateTimeFormatter
import com.aidventory.core.common.designsystem.component.*
import com.aidventory.core.common.designsystem.component.dialogs.DeleteDialog
import com.aidventory.core.common.designsystem.component.dialogs.ShareQrDialog
import com.aidventory.core.common.designsystem.icon.AidventoryIcons
import com.aidventory.core.domain.entities.Container
import com.aidventory.core.domain.entities.Supply
import com.aidventory.core.domain.entities.SupplyUse
import com.aidventory.core.domain.model.SupplyFilterParams
import com.aidventory.core.domain.model.SupplySortingParams
import com.aidventory.feature.supplies.R
import com.aidventory.feature.supplies.navigation.navigateToMoveSupplyScreen
import com.aidventory.feature.supplies.navigation.navigateToQuickSearchScreen
import com.google.accompanist.adaptive.HorizontalTwoPaneStrategy
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SuppliesScreen(
    windowSizeClass: WindowSizeClass,
    displayFeatures: List<DisplayFeature>,
    navController: NavHostController,
    viewModel: SuppliesViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val widthSizeClass by rememberUpdatedState(windowSizeClass.widthSizeClass)

    val sideEffect by viewModel.sideEffect.collectAsStateWithLifecycle()
    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/pdf"),
        onResult = {
            it?.let {
                viewModel.saveBarcode(it)
            }
        }
    )

    when (sideEffect) {
        is SuppliesSideEffect.SendBarcodeIntent -> {
            context.sendBarcodeIntent((sideEffect as SuppliesSideEffect.SendBarcodeIntent).uri)
            viewModel.consumeSideEffect()
        }

        else -> {
            // when null
        }
    }

    var isShareDialogOpen by remember {
        mutableStateOf(false)
    }


    val onNavigationClick = state.getOnNavigationClickAction(
        showListAndDetail = widthSizeClass.showListAndDetail(),
        closeDetails = viewModel::closeDetails,
        navigateUp = navController::navigateUp
    )

    // For WindowWidthSizeClass.Compact a modal bottom sheet is shown, for larger screens - a dialog.
    val isModalContentDialogOpen =
        state.isModalContentVisible && widthSizeClass != WindowWidthSizeClass.Compact

    val modalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )
    modalBottomSheetState.onDismiss(
        condition = !isModalContentDialogOpen,
        action = viewModel::hideModalContent
    )

    val isBottomSheetExpanded =
        state.isModalContentVisible && widthSizeClass == WindowWidthSizeClass.Compact

    if (isBottomSheetExpanded) {
        coroutineScope.launch { modalBottomSheetState.show() }
    } else {
        coroutineScope.launch { modalBottomSheetState.hide() }
    }

    var isDeleteDialogOpen by remember { mutableStateOf(false) }

    SuppliesScreenContent(
        windowSizeClass = windowSizeClass,
        displayFeatures = displayFeatures,
        modalContentType = state.modalContentType,
        isLoading = state.isSuppliesLoading,
        isDetailOpen = state.isDetailOpen,
        showListAndDetail = widthSizeClass.showListAndDetail(),
        isModalContentDialogOpen = isModalContentDialogOpen,
        modalBottomSheetState = modalBottomSheetState,
        supplies = state.supplies,
        supplyUses = state.supplyUses,
        containers = state.containers,
        supplyFilterParams = state.supplyFilterParams,
        supplySortingParams = state.supplySortingParams,
        selectedSupplyBarcode = state.selectedSupplyBarcode,
        selectedSupply = state.selectedSupply,
        updateIsDetailOpen = viewModel::updateIsDetailOpen,
        onDeleteActionClick = { isDeleteDialogOpen = true },
        onSupplyItemClick = viewModel::selectSupply,
        onNavigationClick = onNavigationClick,
        onShareClick = { isShareDialogOpen = true },
        onSortingActionClick = viewModel::showSortingOptions,
        onFilterActionClick = viewModel::showFilters,
        onSortOptionClick = viewModel::changeSorting,
        onContainerOptionClick = viewModel::changeContainerFilter,
        onSupplyUseOptionClick = viewModel::changeSupplyUseFilter,
        onClearFiltersClick = viewModel::clearFilters,
        onModalDismiss = viewModel::hideModalContent,
        onSearchActionClick = { barcode ->
            navController.navigateToQuickSearchScreen(barcode)
        },
        onMoveActionClick = { supplyBarcode, containerBarcode ->
            navController.navigateToMoveSupplyScreen(
                supplyBarcode = supplyBarcode,
                containerBarcode = containerBarcode
            )
        }
    )


    DeleteDialog(
        open = isDeleteDialogOpen,
        title = stringResource(R.string.supplies_delete_dialog_title),
        text = stringResource(R.string.supplies_delete_dialog_text),
        onDismissClick = { isDeleteDialogOpen = false },
        onConfirmClick = {
            viewModel.clickDeleteSupply(state.selectedSupply?.barcode)
            // Close the detail pane after deleting.
            viewModel.closeDetails()
            isDeleteDialogOpen = false
        }
    )

    ShareQrDialog(
        open = isShareDialogOpen,
        onSaveClick = {
            val barcode = state.selectedSupplyBarcode!!
            val fileName = "aidventory-$barcode"
            createDocumentLauncher.launch(fileName)
            isShareDialogOpen = false
        },
        onSendClick = {
            viewModel.sendBarcode()
            isShareDialogOpen = false
        },
        onDismiss = { isShareDialogOpen = false })

    BackHandler(modalBottomSheetState.isVisible) {
        coroutineScope.launch {
            modalBottomSheetState.hide()
        }
    }
}

private fun WindowWidthSizeClass.showListAndDetail() = when (this) {
    WindowWidthSizeClass.Compact, WindowWidthSizeClass.Medium -> false
    else -> true
}


@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterialApi::class)

@Composable
private fun SuppliesScreenContent(
    windowSizeClass: WindowSizeClass,
    displayFeatures: List<DisplayFeature>,
    modalContentType: ModalContentType,
    isLoading: Boolean,
    isDetailOpen: Boolean,
    showListAndDetail: Boolean,
    isModalContentDialogOpen: Boolean,
    modalBottomSheetState: ModalBottomSheetState,
    supplies: List<Supply>,
    supplyUses: List<SupplyUse>,
    containers: List<Container>,
    supplyFilterParams: SupplyFilterParams,
    supplySortingParams: SupplySortingParams,
    selectedSupplyBarcode: String?,
    selectedSupply: Supply?,
    updateIsDetailOpen: (Boolean) -> Unit,
    onDeleteActionClick: () -> Unit,
    onSupplyItemClick: (String) -> Unit,
    onNavigationClick: () -> Unit,
    onShareClick: () -> Unit,
    onSortingActionClick: () -> Unit,
    onFilterActionClick: () -> Unit,
    onSortOptionClick: (SupplySortingParams) -> Unit,
    onContainerOptionClick: (containerQr: String) -> Unit,
    onSupplyUseOptionClick: (id: Int) -> Unit,
    onClearFiltersClick: () -> Unit,
    onModalDismiss: () -> Unit,
    onSearchActionClick: (String) -> Unit,
    onMoveActionClick: (supplyBarcode: String, containerBarcode: String?) -> Unit
) {
    ModalBottomSheetLayout(
        sheetShape = MaterialTheme.shapes.large.copy(
            bottomStart = ZeroCornerSize,
            bottomEnd = ZeroCornerSize
        ),
        sheetState = modalBottomSheetState,
        sheetContent = {
            SuppliesModalBottomSheet(
                modalContentType = modalContentType,
                supplyUses = supplyUses,
                containers = containers,
                supplyFilterParams = supplyFilterParams,
                supplySortingParams = supplySortingParams,
                onSortOptionClick = onSortOptionClick,
                onContainerOptionClick = onContainerOptionClick,
                onSupplyUseOptionClick = onSupplyUseOptionClick,
                onClearFiltersClick = onClearFiltersClick,
            )
        },
        content = {
            SuppliesContent(
                displayFeatures = displayFeatures,
                windowSizeClass = windowSizeClass,
                isLoading = isLoading,
                supplies = supplies,
                selectedSupply = selectedSupply,
                isDetailOpen = isDetailOpen,
                showListAndDetail = showListAndDetail,
                onNavigationClick = onNavigationClick,
                onSortingActionClick = onSortingActionClick,
                onFilterActionClick = onFilterActionClick,
                updateIsDetailOpen = updateIsDetailOpen,
                selectedSupplyBarcode = selectedSupplyBarcode,
                onSupplyItemClick = onSupplyItemClick,
                onShareActionClick = onShareClick,
                onDeleteActionClick = onDeleteActionClick,
                onSearchActionClick = onSearchActionClick,
                onMoveActionClick = onMoveActionClick
            )
        }
    )

    if (isModalContentDialogOpen) {
        SuppliesDialog(
            modifier = Modifier,
            modalContentType = modalContentType,
            supplyUses = supplyUses,
            containers = containers,
            supplyFilterParams = supplyFilterParams,
            supplySortingParams = supplySortingParams,
            onSortOptionClick = onSortOptionClick,
            onContainerOptionClick = onContainerOptionClick,
            onSupplyUseOptionClick = onSupplyUseOptionClick,
            onClearFiltersClick = onClearFiltersClick,
            onModalDismiss = onModalDismiss
        )
    }
}


@Composable

private fun SuppliesContent(
    supplies: List<Supply>,
    isLoading: Boolean,
    onNavigationClick: () -> Unit,
    onSortingActionClick: () -> Unit,
    onFilterActionClick: () -> Unit,
    displayFeatures: List<DisplayFeature>,
    windowSizeClass: WindowSizeClass,
    isDetailOpen: Boolean,
    showListAndDetail: Boolean,
    updateIsDetailOpen: (Boolean) -> Unit,
    selectedSupplyBarcode: String?,
    onSupplyItemClick: (String) -> Unit,
    selectedSupply: Supply?,
    onShareActionClick: () -> Unit,
    onDeleteActionClick: () -> Unit,
    onMoveActionClick: (supplyBarcode: String, containerBarcode: String?) -> Unit,
    onSearchActionClick: (String) -> Unit
) {
    val isTwoColumn = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Medium

    Column {
        SuppliesTopAppBar(
            windowSizeClass = windowSizeClass,
            isDetailOpen = isDetailOpen,
            showListAndDetail = showListAndDetail,
            isShareActionAvailable = selectedSupply?.isBarcodeGenerated ?: false,
            onSortingActionClick = onSortingActionClick,
            onFilterActionClick = onFilterActionClick,
            onNavigationClick = onNavigationClick,
            onDeleteClick = onDeleteActionClick,
            onShareClick = onShareActionClick,
            onMoveClick = {
                onMoveActionClick(
                    selectedSupply!!.barcode,
                    selectedSupply.container?.barcode
                )
            }
        )
        AnimatedVisibility(visible = isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
        if (supplies.isEmpty() && !isLoading) {
            // TODO: Different message when there's no supplies and when there's no supplies for the selected filters.
            EmptyContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            )
        } else {
            ListDetailPane(
                isDetailOpen = isDetailOpen,
                setIsDetailOpen = updateIsDetailOpen,
                showListAndDetail = showListAndDetail,
                detailKey = selectedSupplyBarcode,
                list = { isDetailVisible ->
                    SupplyList(
                        isTwoColumn = isTwoColumn,
                        supplies = supplies,
                        selectedSupplyBarcode = selectedSupplyBarcode,
                        isDetailVisible = isDetailVisible,
                        onItemClick = { barcode ->
                            onSupplyItemClick(barcode)
                            // Consider the detail to now be open. This acts like a navigation if
                            // there isn't room for both list and detail, and also will result
                            // in the detail remaining open in the case of resize.
                            updateIsDetailOpen(true)
                        }
                    )
                },
                detail = {
                    SupplyDetail(
                        supply = selectedSupply,
                        showListAndDetail = showListAndDetail,
                        modifier = Modifier.padding(16.dp),
                        onShareActionClick = onShareActionClick,
                        onMoveActionClick = {
                            onMoveActionClick(
                                selectedSupply!!.barcode,
                                selectedSupply.container?.barcode
                            )
                        },
                        onSearchActionClick = onSearchActionClick,
                        onDeleteActionClick = onDeleteActionClick
                    )
                },
                twoPaneStrategy = HorizontalTwoPaneStrategy(splitFraction = 1f / 2f),
                displayFeatures = displayFeatures
            )
        }
    }
}

@Composable
private fun SupplyList(
    isTwoColumn: Boolean,
    supplies: List<Supply>,
    selectedSupplyBarcode: String?,
    isDetailVisible: Boolean,
    onItemClick: (barcode: String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (isTwoColumn) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            itemsIndexed(supplies) { index, supply ->
                val isSelected =
                    selectedSupplyBarcode != null && supply.barcode == selectedSupplyBarcode
                val isLastItem = index == supplies.lastIndex
                SupplyListItem(
                    supply = supply,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onItemClick(supply.barcode) },
                    isSelected = isSelected,
                    isLastItem = isLastItem,
                    isDetailVisible = isDetailVisible
                )
            }
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = modifier.padding(horizontal = 16.dp)
        ) {
            itemsIndexed(supplies) { index, supply ->
                val isSelected =
                    selectedSupplyBarcode != null && supply.barcode == selectedSupplyBarcode
                val isLastItem = index == supplies.lastIndex
                SupplyListItem(
                    supply = supply,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onItemClick(supply.barcode) },
                    isSelected = isSelected,
                    isLastItem = isLastItem,
                    isDetailVisible = isDetailVisible
                )
            }
        }
    }

}

@Composable
private fun SupplyDetail(
    supply: Supply?,
    showListAndDetail: Boolean,
    onShareActionClick: () -> Unit,
    onDeleteActionClick: () -> Unit,
    onMoveActionClick: () -> Unit,
    onSearchActionClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (showListAndDetail) {
        if (supply != null) {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .navigationBarsPadding(),
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        if (supply.isBarcodeGenerated) {
                            SupplyDetailAction(
                                icon = AidventoryIcons.Share.imageVector,
                                iconContentDescription = null,
                                onActionClick = onShareActionClick,
                                modifier = Modifier.padding(4.dp)
                            )
                        }
                        SupplyDetailAction(
                            icon = AidventoryIcons.MoveUp.imageVector,
                            iconContentDescription = null,
                            onActionClick = onMoveActionClick,
                            modifier = Modifier.padding(4.dp)
                        )
                        SupplyDetailAction(
                            icon = AidventoryIcons.DeleteBorder.imageVector,
                            iconContentDescription = null,
                            onActionClick = onDeleteActionClick,
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                    SupplyDetailContent(
                        modifier = Modifier.fillMaxWidth(),
                        supply = supply,
                        onSearchClick = { onSearchActionClick(supply.barcode) }
                    )
                }
            }
        } else {
            SupplyDetailPlaceholder(
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding()
                    .padding(32.dp)
            )
        }
    } else {
        if (supply != null) {
            SupplyDetailContent(
                modifier = Modifier.fillMaxWidth(),
                supply = supply,
                onSearchClick = { onSearchActionClick(supply.barcode) }
            )
        } else {
            SupplyDetailPlaceholder(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp)
            )
        }
    }
}

@Composable
private fun SupplyDetailAction(
    icon: ImageVector,
    iconContentDescription: String?,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FilledTonalIconButton(
        modifier = modifier,
        onClick = onActionClick,
        shape = MaterialTheme.shapes.small,
        colors = IconButtonDefaults.filledTonalIconButtonColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    ) {
        Icon(imageVector = icon, contentDescription = iconContentDescription)
    }
}

@Composable
private fun EmptyContent(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(id = R.string.supplies_empty_text),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SupplyDetailPlaceholder(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = R.string.supply_detail_not_selected_placeholder),
            textAlign = TextAlign.Center
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SupplyListItem(
    supply: Supply,
    onClick: () -> Unit,
    isDetailVisible: Boolean,
    isSelected: Boolean,
    isLastItem: Boolean,
    modifier: Modifier = Modifier
) {
    val containerColor = if (isDetailVisible && isSelected) {
        MaterialTheme.colorScheme.surfaceVariant
    } else {
        MaterialTheme.colorScheme.surface
    }

    // Applies padding to the last item in the list.
    val modifierWithInsets = if (isLastItem) {
        Modifier.navigationBarsPadding()
    } else {
        Modifier
    }

    ElevatedCard(
        modifier = modifier.then(modifierWithInsets),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        onClick = onClick,
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = supply.name,
                style = MaterialTheme.typography.headlineSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            val expiry = supply.expiry
                ?.format(AppDateTimeFormatter.fullDate())
                ?: stringResource(id = R.string.supply_list_item_not_applicable)
            Text(
                text = stringResource(R.string.supply_list_item_expiry, expiry),
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

private fun SuppliesUiState.getOnNavigationClickAction(
    showListAndDetail: Boolean,
    closeDetails: () -> Unit,
    navigateUp: () -> Unit
): () -> Unit {
    // When the detail screen is open as a separate screen (it implies showListAndDetail is false),
    // then a click on the navigation button should close the detail screen
    // so that user can see the list of containers.
    return if (isDetailOpen && !showListAndDetail) {
        closeDetails
    } else {
        navigateUp
    }
}
