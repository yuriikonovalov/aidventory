package com.aidventory.feature.supplies.presentation.supplies

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aidventory.core.common.designsystem.component.AidventoryTopAppBar
import com.aidventory.core.common.designsystem.component.AidventoryTopAppBarAction
import com.aidventory.core.common.designsystem.component.dialogs.AppDialog
import com.aidventory.core.common.designsystem.component.isTopAppBarCenterAligned
import com.aidventory.core.common.designsystem.icon.AidventoryIcons
import com.aidventory.core.domain.entities.Container
import com.aidventory.core.domain.entities.SupplyUse
import com.aidventory.core.domain.model.SupplyFilterParams
import com.aidventory.core.domain.model.SupplySortingParams
import com.aidventory.feature.supplies.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SuppliesTopAppBar(
    windowSizeClass: WindowSizeClass,
    isDetailOpen: Boolean,
    showListAndDetail: Boolean,
    isShareActionAvailable: Boolean,
    modifier: Modifier = Modifier,
    onNavigationClick: () -> Unit = {},
    onFilterActionClick: () -> Unit = {},
    onSortingActionClick: () -> Unit = {},
    onShareClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onMoveClick: () -> Unit = {}
) {
    val title = if (showListAndDetail || !isDetailOpen) {
        stringResource(R.string.top_app_bar_title_supplies)
    } else {
        ""
    }

    AidventoryTopAppBar(
        modifier = modifier,
        title = title,
        centerAligned = windowSizeClass.isTopAppBarCenterAligned(),
        navigationIcon = AidventoryIcons.ArrowBack.imageVector,
        navigationIconContentDescription = stringResource(id = R.string.top_app_bar_action_back),
        onNavigationClick = onNavigationClick
    ) {
        if (showListAndDetail || !isDetailOpen) {
            AidventoryTopAppBarAction(
                icon = AidventoryIcons.Sort.imageVector,
                iconContentDescription = stringResource(id = R.string.supplies_sort_content_description),
                onActionClick = onSortingActionClick
            )
            AidventoryTopAppBarAction(
                icon = AidventoryIcons.Filter.imageVector,
                iconContentDescription = stringResource(id = R.string.supplies_filter_content_description),
                onActionClick = onFilterActionClick
            )
        } else {
            // Actions to be shown on the detail screen.
            if (isShareActionAvailable) {
                AidventoryTopAppBarAction(
                    icon = AidventoryIcons.Share.imageVector,
                    iconContentDescription = stringResource(id = R.string.supplies_share_content_description),
                    onActionClick = onShareClick
                )
            }
            AidventoryTopAppBarAction(
                icon = AidventoryIcons.MoveUp.imageVector,
                iconContentDescription = stringResource(id = R.string.supplies_change_container_content_description),
                onActionClick = onMoveClick
            )
            AidventoryTopAppBarAction(
                icon = AidventoryIcons.DeleteBorder.imageVector,
                iconContentDescription = stringResource(id = R.string.supplies_delete_content_description),
                onActionClick = onDeleteClick
            )
        }
    }
}


@Composable
internal fun SuppliesDialog(
    modalContentType: ModalContentType,
    supplyUses: List<SupplyUse>,
    containers: List<Container>,
    supplyFilterParams: SupplyFilterParams,
    supplySortingParams: SupplySortingParams,
    onSortOptionClick: (SupplySortingParams) -> Unit,
    onContainerOptionClick: (containerQr: String) -> Unit,
    onSupplyUseOptionClick: (id: Int) -> Unit,
    onClearFiltersClick: () -> Unit,
    onModalDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AppDialog(
        modifier = modifier,
        open = true,
        title = { },
        content = {
            SuppliesModalContent(
                modifier = Modifier.padding(16.dp),
                modalContentType = modalContentType,
                supplyUses = supplyUses,
                containers = containers,
                supplyFilterParams = supplyFilterParams,
                supplySortingParams = supplySortingParams,
                onSortOptionClick = onSortOptionClick,
                onContainerOptionClick = onContainerOptionClick,
                onSupplyUseOptionClick = onSupplyUseOptionClick,
                onClearFiltersClick = onClearFiltersClick
            )
        },
        negativeButtonText = stringResource(R.string.supplies_dialog_button_close),
        onNegativeButtonClick = onModalDismiss
    )
}

@Composable
internal fun SuppliesModalBottomSheet(
    modalContentType: ModalContentType,
    supplyUses: List<SupplyUse>,
    containers: List<Container>,
    supplyFilterParams: SupplyFilterParams,
    supplySortingParams: SupplySortingParams,
    onSortOptionClick: (SupplySortingParams) -> Unit,
    onContainerOptionClick: (containerQr: String) -> Unit,
    onSupplyUseOptionClick: (id: Int) -> Unit,
    onClearFiltersClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(modifier = modifier) {
        Column(
            modifier = Modifier
                .navigationBarsPadding()
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 8.dp),
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
            SuppliesModalContent(
                modalContentType = modalContentType,
                supplyUses = supplyUses,
                containers = containers,
                supplyFilterParams = supplyFilterParams,
                supplySortingParams = supplySortingParams,
                onSortOptionClick = onSortOptionClick,
                onContainerOptionClick = onContainerOptionClick,
                onSupplyUseOptionClick = onSupplyUseOptionClick,
                onClearFiltersClick = onClearFiltersClick
            )
        }
    }
}

@Composable
internal fun SuppliesModalContent(
    modalContentType: ModalContentType,
    supplyUses: List<SupplyUse>,
    containers: List<Container>,
    supplyFilterParams: SupplyFilterParams,
    supplySortingParams: SupplySortingParams,
    onSortOptionClick: (SupplySortingParams) -> Unit,
    onContainerOptionClick: (containerQr: String) -> Unit,
    onSupplyUseOptionClick: (id: Int) -> Unit,
    onClearFiltersClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (modalContentType) {
        ModalContentType.SORT -> SortModalContent(
            modifier = modifier,
            supplySortingParams = supplySortingParams,
            onSortOptionClick = onSortOptionClick
        )

        ModalContentType.FILTER -> FilterModalContent(
            modifier = modifier,
            supplyUses = supplyUses,
            containers = containers,
            filterParams = supplyFilterParams,
            onContainerOptionClick = onContainerOptionClick,
            onSupplyUseOptionClick = onSupplyUseOptionClick,
            onClearFiltersClick = onClearFiltersClick
        )
    }
}

@Composable
private fun FilterModalContent(
    supplyUses: List<SupplyUse>,
    containers: List<Container>,
    filterParams: SupplyFilterParams,
    onContainerOptionClick: (barcode: String) -> Unit,
    onSupplyUseOptionClick: (id: Int) -> Unit,
    onClearFiltersClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val clearAllButtonEnabled = filterParams.filterContainerBarcodes.isNotEmpty()
            || filterParams.filterSupplyUseIds.isNotEmpty()

    Column(modifier = modifier) {
        TextButton(
            modifier = Modifier.align(Alignment.End),
            onClick = onClearFiltersClick,
            enabled = clearAllButtonEnabled
        ) {
            Icon(imageVector = AidventoryIcons.Close.imageVector, contentDescription = null)
            Text(text = stringResource(id = R.string.supply_filter_clear_button))
        }

        // There's always default (pre-installed) supply uses.
        SupplyUseFilter(
            modifier = Modifier.fillMaxWidth(),
            supplyUses = supplyUses,
            selectedSupplyUseIds = filterParams.filterSupplyUseIds.toList(),
            onSupplyUseOptionClick = onSupplyUseOptionClick
        )

        // If there's no containers then we don't show the view.
        if (containers.isNotEmpty()) {
            Spacer(modifier = Modifier.height(32.dp))
            ContainerFilter(
                modifier = Modifier.fillMaxWidth(),
                containers = containers,
                selectedContainerQrCodes = filterParams.filterContainerBarcodes.toList(),
                onContainerOptionClick = onContainerOptionClick
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun SupplyUseFilter(
    supplyUses: List<SupplyUse>,
    selectedSupplyUseIds: List<Int>,
    onSupplyUseOptionClick: (id: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = R.string.supply_filter_supply_use_label),
            textAlign = TextAlign.Center
        )
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 8.dp)
        )
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            supplyUses.forEach { supplyUse ->
                val selected = supplyUse.id in selectedSupplyUseIds
                FilterChip(
                    modifier = Modifier.padding(end = 16.dp),
                    label = { Text(text = supplyUse.displayName(LocalContext.current)) },
                    selected = selected,
                    onClick = { onSupplyUseOptionClick(supplyUse.id) }
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun ContainerFilter(
    containers: List<Container>,
    selectedContainerQrCodes: List<String>,
    onContainerOptionClick: (containerQr: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = R.string.supply_filter_container_label),
            textAlign = TextAlign.Center
        )
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 8.dp)
        )

        FlowRow(modifier = Modifier.fillMaxWidth()) {
            containers.forEach { container ->
                val selected = container.barcode in selectedContainerQrCodes
                FilterChip(
                    modifier = Modifier.padding(end = 16.dp),
                    label = { Text(text = container.name) },
                    selected = selected,
                    onClick = { onContainerOptionClick(container.barcode) }
                )
            }
        }
    }
}

@Composable
private fun SortModalContent(
    onSortOptionClick: (SupplySortingParams) -> Unit,
    supplySortingParams: SupplySortingParams,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = R.string.supply_sort_label),
            textAlign = TextAlign.Center
        )
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        )
        SortOption(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = R.string.supply_sort_option_default),
            selected = supplySortingParams.isDefault,
            onClick = { onSortOptionClick(SupplySortingParams.Default) }
        )
        SortOption(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = R.string.supply_sort_option_name_asc),
            selected = supplySortingParams.nameASC,
            onClick = { onSortOptionClick(SupplySortingParams.NameASC) }
        )

        SortOption(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = R.string.supply_sort_option_name_desc),
            selected = supplySortingParams.nameDESC,
            onClick = { onSortOptionClick(SupplySortingParams.NameDESC) }
        )

        SortOption(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = R.string.supply_sort_option_expiry_asc),
            selected = supplySortingParams.expiryASC,
            onClick = { onSortOptionClick(SupplySortingParams.ExpiryASC) }
        )

        SortOption(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = R.string.supply_sort_option_expiry_desc),
            selected = supplySortingParams.expiryDESC,
            onClick = { onSortOptionClick(SupplySortingParams.ExpiryDESC) }
        )
    }
}

@Composable
private fun SortOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = onClick)
    ) {
        RadioButton(
            modifier = Modifier.align(Alignment.CenterVertically),
            selected = selected,
            onClick = onClick
        )
        Text(
            modifier = Modifier.align(Alignment.CenterVertically),
            text = text
        )
    }
}
