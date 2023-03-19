package com.aidventory.feature.containers.presentation.containers

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.layout.DisplayFeature
import com.aidventory.core.barcode.sendBarcodeIntent
import com.aidventory.core.common.AppDateTimeFormatter
import com.aidventory.core.common.designsystem.component.AidventoryTopAppBar
import com.aidventory.core.common.designsystem.component.AidventoryTopAppBarAction
import com.aidventory.core.common.designsystem.component.dialogs.DeleteDialog
import com.aidventory.core.common.designsystem.component.EmptyContent
import com.aidventory.core.common.designsystem.component.ListDetailPane
import com.aidventory.core.common.designsystem.component.dialogs.AppDialog
import com.aidventory.core.common.designsystem.component.dialogs.AppDialogMessageContent
import com.aidventory.core.common.designsystem.component.dialogs.AppDialogTitle
import com.aidventory.core.common.designsystem.component.dialogs.ShareQrDialog
import com.aidventory.core.common.designsystem.component.isTopAppBarCenterAligned
import com.aidventory.core.common.designsystem.icon.AidventoryIcons
import com.aidventory.core.common.designsystem.theme.AidventoryTheme
import com.aidventory.core.domain.entities.ContainerWithContent
import com.aidventory.core.domain.entities.Supply
import com.aidventory.core.utils.PreviewWindowSizeClass
import com.aidventory.feature.containers.R
import com.google.accompanist.adaptive.HorizontalTwoPaneStrategy
import java.time.LocalDate

@Composable
internal fun ContainersScreen(
    windowSizeClass: WindowSizeClass,
    displayFeatures: List<DisplayFeature>,
    onNavigateUp: () -> Unit = {},
    onNavigateToAddContainerScreen: () -> Unit = {},
    onNavigateToAddContainerDialog: () -> Unit = {},
    viewModel: ContainersViewModel = hiltViewModel()
) {

    val context = LocalContext.current
    val widthSizeClass by rememberUpdatedState(windowSizeClass.widthSizeClass)
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val sideEffect by viewModel.sideEffect.collectAsStateWithLifecycle()
    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/pdf"),
        onResult = { uri -> uri?.let { viewModel.saveBarcode(it) } }
    )

    when (sideEffect) {
        is ContainersSideEffect.SendBarcodeIntent -> {
            context.sendBarcodeIntent((sideEffect as ContainersSideEffect.SendBarcodeIntent).uri)
            viewModel.consumeSideEffect()
        }

        else -> {
            // when null
        }
    }


    val showListAndDetail = remember(widthSizeClass) {
        when (widthSizeClass) {
            WindowWidthSizeClass.Compact, WindowWidthSizeClass.Medium -> false
            WindowWidthSizeClass.Expanded -> true
            else -> true
        }
    }

    val onAddActionClick = remember(widthSizeClass) {
        widthSizeClass.getOnAddClickAction(
            onNavigateToAddContainerScreen,
            onNavigateToAddContainerDialog
        )
    }

    val onNavigationClick = state.getOnNavigationClickAction(
        showListAndDetail = showListAndDetail,
        closeDetails = { viewModel.updateIsDetailOpen(false) },
        navigateUp = onNavigateUp
    )

    ContainersScreenContent(
        state = state,
        isDetailOpen = state.isDetailOpen,
        showListAndDetail = showListAndDetail,
        onNavigationClick = onNavigationClick,
        displayFeatures = displayFeatures,
        windowSizeClass = windowSizeClass,
        updateIsDetailOpen = viewModel::updateIsDetailOpen,
        updateSelectedContainerBarcode = viewModel::updateSelectedContainerBarcode,
        onAddActionClick = onAddActionClick,
        updateIsDeleteDialogOpen = viewModel::updateIsDeleteDialogOpen,
        onDeleteClick = viewModel::deleteContainer,
        onSendBarcodeClick = viewModel::sendBarcode,
        onSaveBarcodeClick = { barcode -> createDocumentLauncher.launch("aidventory-$barcode") },
        onCloseShareQrErrorDialogClick = viewModel::closeShareQrErrorDialog
    )
}

@Composable
private fun ContainersScreenContent(
    state: ContainersUiState,
    isDetailOpen: Boolean,
    showListAndDetail: Boolean,
    onNavigationClick: () -> Unit,
    displayFeatures: List<DisplayFeature>,
    windowSizeClass: WindowSizeClass, updateIsDetailOpen: (Boolean) -> Unit,
    updateSelectedContainerBarcode: (barcode: String?) -> Unit,
    onAddActionClick: () -> Unit,
    updateIsDeleteDialogOpen: (Boolean) -> Unit,
    onDeleteClick: () -> Unit,
    onSendBarcodeClick: () -> Unit,
    onSaveBarcodeClick: (String) -> Unit,
    onCloseShareQrErrorDialogClick: () -> Unit,
) {
    var isShareDialogOpen by remember { mutableStateOf(false) }


    ContainersContent(
        containersWithContent = state.containersWithContent,
        onNavigationClick = onNavigationClick,
        displayFeatures = displayFeatures,
        windowSizeClass = windowSizeClass,
        isDetailOpen = isDetailOpen,
        showListAndDetail = showListAndDetail,
        updateIsDetailOpen = updateIsDetailOpen,
        selectedContainerBarcode = state.selectedContainerBarcode,
        updateSelectedContainerBarcode = updateSelectedContainerBarcode,
        selectedContainer = state.selectedContainer,
        onShareActionClick = { isShareDialogOpen = true },
        updateIsDeleteDialogOpen = { updateIsDeleteDialogOpen(true) },
        onAddActionClick = onAddActionClick
    )

    DeleteDialog(
        open = state.isDeleteDialogOpen,
        title = stringResource(R.string.dialog_delete_container_title),
        text = stringResource(R.string.dialog_delete_container_text),
        onConfirmClick = onDeleteClick,
        onDismissClick = { updateIsDeleteDialogOpen(false) }
    )

    ShareQrDialog(
        open = isShareDialogOpen,
        onSaveClick = {
            val barcode = state.selectedContainerBarcode!!
            onSaveBarcodeClick(barcode)
            isShareDialogOpen = false
        },
        onSendClick = {
            isShareDialogOpen = false
            onSendBarcodeClick()
        },
        onDismiss = { isShareDialogOpen = false }
    )

    ShareQrErrorDialog(
        open = state.isShareQrErrorDialogOpen,
        onCloseButtonClick = onCloseShareQrErrorDialogClick
    )
}

@Composable
private fun ShareQrErrorDialog(
    open: Boolean,
    onCloseButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AppDialog(
        modifier = modifier,
        open = open,
        title = { AppDialogTitle(text = stringResource(R.string.containers_share_qr_dialog_title)) },
        content = { AppDialogMessageContent(text = stringResource(R.string.containers_share_qr_dialog_message)) },
        negativeButtonText = stringResource(R.string.containers_share_qr_dialog_button_close),
        onNegativeButtonClick = onCloseButtonClick
    )
}

@Composable
private fun ContainersContent(
    containersWithContent: List<ContainerWithContent>,
    onNavigationClick: () -> Unit,
    displayFeatures: List<DisplayFeature>,
    windowSizeClass: WindowSizeClass,
    isDetailOpen: Boolean,
    showListAndDetail: Boolean,
    updateIsDetailOpen: (Boolean) -> Unit,
    selectedContainerBarcode: String?,
    updateSelectedContainerBarcode: (String?) -> Unit,
    selectedContainer: ContainerWithContent?,
    onShareActionClick: () -> Unit,
    updateIsDeleteDialogOpen: () -> Unit,
    onAddActionClick: () -> Unit
) {
    val isTwoColumn = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Medium

    Column {
        ContainersTopAppBar(
            windowSizeClass = windowSizeClass,
            isDetailOpen = isDetailOpen,
            showListAndDetail = showListAndDetail,
            onNavigationClick = onNavigationClick,
            onAddActionClick = onAddActionClick,
            onShareActionClick = onShareActionClick,
            onDeleteActionClick = updateIsDeleteDialogOpen
        )
        if (containersWithContent.isEmpty()) {
            EmptyContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .systemBarsPadding(),
                text = stringResource(R.string.containers_empty_text)
            )
        } else {
            ListDetailPane(
                isDetailOpen = isDetailOpen,
                setIsDetailOpen = updateIsDetailOpen,
                showListAndDetail = showListAndDetail,
                detailKey = selectedContainerBarcode,
                list = { isDetailVisible ->
                    ContainerList(
                        isTwoColumn = isTwoColumn,
                        containers = containersWithContent,
                        selectedContainerBarcode = selectedContainerBarcode,
                        isDetailVisible = isDetailVisible,
                        onItemClick = { barcode ->
                            updateSelectedContainerBarcode(barcode)
                            // Consider the detail to now be open. This acts like a navigation if
                            // there isn't room for both list and detail, and also will result
                            // in the detail remaining open in the case of resize.
                            updateIsDetailOpen(true)
                        }
                    )
                },
                detail = {
                    ContainerDetail(
                        container = selectedContainer,
                        showListAndDetail = showListAndDetail,
                        modifier = Modifier.padding(16.dp),
                        onShareActionClick = onShareActionClick,
                        onDeleteActionClick = updateIsDeleteDialogOpen
                    )
                },
                twoPaneStrategy = HorizontalTwoPaneStrategy(splitFraction = 1f / 2f),
                displayFeatures = displayFeatures
            )
        }
    }
}

@Composable
private fun ContainerList(
    isTwoColumn: Boolean,
    containers: List<ContainerWithContent>,
    selectedContainerBarcode: String?,
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
            itemsIndexed(containers) { index, containerWithContent ->
                val barcode = containerWithContent.container.barcode
                val isSelected = barcode == selectedContainerBarcode
                val isLastItem = if (containers.size % 2 == 0) {
                    index == containers.lastIndex || index == containers.lastIndex - 1
                } else {
                    index == containers.lastIndex
                }
                ContainerListItem(
                    containerWithContent = containerWithContent,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onItemClick(barcode) },
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
            itemsIndexed(containers) { index, containerWithContent ->
                val barcode = containerWithContent.container.barcode
                val isSelected = barcode == selectedContainerBarcode
                val isLastItem = index == containers.lastIndex
                ContainerListItem(
                    containerWithContent = containerWithContent,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onItemClick(barcode) },
                    isSelected = isSelected,
                    isLastItem = isLastItem,
                    isDetailVisible = isDetailVisible
                )
            }
        }
    }
}

@Composable
private fun ContainerDetail(
    container: ContainerWithContent?,
    showListAndDetail: Boolean,
    onShareActionClick: () -> Unit,
    onDeleteActionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (showListAndDetail) {
        if (container != null) {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .navigationBarsPadding(),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    AidventoryTopAppBarAction(
                        icon = AidventoryIcons.Share.imageVector,
                        iconContentDescription = stringResource(id = R.string.top_app_bar_action_share),
                        onActionClick = onShareActionClick
                    )
                    AidventoryTopAppBarAction(
                        icon = AidventoryIcons.DeleteBorder.imageVector,
                        iconContentDescription = stringResource(id = R.string.top_app_bar_action_delete),
                        onActionClick = onDeleteActionClick
                    )
                }
                ContainerDetailContent(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    container = container
                )
            }
        } else {
            ContainerDetailNotSelectedContent(
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding()
                    .padding(32.dp)
            )
        }
    } else {
        if (container != null) {
            ContainerDetailContent(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                container = container
            )
        } else {
            ContainerDetailNotSelectedContent(
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding()
                    .padding(32.dp)
            )
        }
    }
}


@Composable
private fun ContainerDetailNotSelectedContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = R.string.container_detail_not_selected_placeholder),
            textAlign = TextAlign.Center
        )
    }
}


@Composable
private fun ContainerDetailContent(
    container: ContainerWithContent,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Container's name
        Text(
            text = container.container.name,
            style = MaterialTheme.typography.headlineMedium
        )
        // Created date
        Row(modifier = Modifier.padding(top = 8.dp)) {
            Text(
                text = stringResource(R.string.container_details_label_created),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = container.container.created.format(AppDateTimeFormatter.fullDate()),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        if (container.content.isNotEmpty()) {
            // Number of items
            Row {
                Text(
                    text = stringResource(R.string.container_details_label_number_of_supplies),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = container.content.size.toString(),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }


        Divider(modifier = Modifier.padding(vertical = 16.dp))

        if (container.content.isEmpty()) {
            ContainerContentEmptyPlaceholder(modifier = Modifier.fillMaxSize())
        } else {
            LazyColumn(
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(container.content, key = { it.barcode }) { supply ->
                    SupplyListItem(supply = supply)
                }
            }
        }
    }
}

@Composable
private fun ContainerContentEmptyPlaceholder(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.container_detail_container_content_empty),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SupplyListItem(
    supply: Supply,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(
                text = supply.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = supply.expiry?.format(AppDateTimeFormatter.fullDate())
                    ?: stringResource(id = R.string.container_detail_supply_list_item_expiry_none),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium,
                color = if (supply.expiry?.isBefore(LocalDate.now()) == true) {
                    MaterialTheme.colorScheme.error
                } else {
                    Color.Unspecified
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContainerListItem(
    containerWithContent: ContainerWithContent,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    isSelected: Boolean,
    isLastItem: Boolean,
    isDetailVisible: Boolean
) {
    val containerColor = if (isDetailVisible && isSelected) {
        MaterialTheme.colorScheme.surfaceVariant
    } else {
        MaterialTheme.colorScheme.surface
    }

    val modifierWithInsets = if (isLastItem) {
        Modifier.navigationBarsPadding()
    } else {
        Modifier
    }

    ElevatedCard(
        modifier = modifier.then(modifierWithInsets),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        onClick = onClick,
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(
                text = containerWithContent.container.name,
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = pluralStringResource(
                    id = R.plurals.container_card_items_quantity,
                    count = containerWithContent.content.size,
                    containerWithContent.content.size
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = LocalContentColor.current.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}


private fun WindowWidthSizeClass.getOnAddClickAction(
    onNavigateToAddContainerScreen: () -> Unit,
    onNavigateToAddContainerDialog: () -> Unit,
): () -> Unit {
    // If the current screen's width class is Compact (phone), then we open a full screen
    // for adding a container. When the screen width is larger than Compact, then we open
    // a dialog for adding a container.
    //
    // The decision is made based on the fact that there's only one input field on the screen
    // and a button so it won't be user-friendly to open a full screen for large devices.
    return if (this == WindowWidthSizeClass.Compact) {
        onNavigateToAddContainerScreen
    } else {
        onNavigateToAddContainerDialog
    }
}

private fun ContainersUiState.getOnNavigationClickAction(
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContainersTopAppBar(
    windowSizeClass: WindowSizeClass,
    isDetailOpen: Boolean,
    showListAndDetail: Boolean,
    modifier: Modifier = Modifier,
    onNavigationClick: () -> Unit = {},
    onAddActionClick: () -> Unit = {},
    onShareActionClick: () -> Unit = {},
    onDeleteActionClick: () -> Unit = {}
) {
    val title = if (showListAndDetail || !isDetailOpen) {
        stringResource(R.string.top_app_bar_title_containers)
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
                icon = AidventoryIcons.Add.imageVector,
                iconContentDescription = stringResource(id = R.string.top_app_bar_action_add),
                onActionClick = onAddActionClick
            )
        } else {
            AidventoryTopAppBarAction(
                icon = AidventoryIcons.Share.imageVector,
                iconContentDescription = stringResource(id = R.string.top_app_bar_action_share),
                onActionClick = onShareActionClick
            )
            AidventoryTopAppBarAction(
                icon = AidventoryIcons.DeleteBorder.imageVector,
                iconContentDescription = stringResource(id = R.string.top_app_bar_action_delete),
                onActionClick = onDeleteActionClick
            )

        }
    }
}

@Preview(showBackground = true, device = Devices.PHONE)
@Composable
private fun ContainerScreenPreviewPhone() {
    AidventoryTheme {
        ContainersScreen(
            windowSizeClass = PreviewWindowSizeClass.PHONE,
            displayFeatures = emptyList()
        )
    }
}


@Preview(showBackground = true, device = Devices.FOLDABLE)
@Composable
private fun ContainerScreenPreviewFoldable() {
    AidventoryTheme {
        ContainersScreen(
            windowSizeClass = PreviewWindowSizeClass.FOLDABLE,
            displayFeatures = emptyList()
        )
    }
}

