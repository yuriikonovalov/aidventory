package com.aidventory.feature.expired.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.aidventory.core.common.designsystem.component.AidventoryTopAppBar
import com.aidventory.core.common.designsystem.component.dialogs.DeleteDialog
import com.aidventory.core.common.designsystem.icon.AidventoryIcons
import com.aidventory.core.domain.entities.Supply
import com.aidventory.feature.expired.R

@Composable
internal fun ExpiredScreen(
    windowSizeClass: WindowSizeClass,
    viewModel: ExpiredViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    ExpiredScreenContent(
        widthSizeClass = windowSizeClass.widthSizeClass,
        modifier = Modifier.fillMaxWidth(),
        isLoading = state.isLoading,
        supplies = state.suppliesByContainer,
        onDeleteSupplyClick = viewModel::deleteExpiredSupply
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExpiredScreenContent(
    widthSizeClass: WindowWidthSizeClass,
    isLoading: Boolean,
    supplies: Map<String?, List<Supply>>,
    onDeleteSupplyClick: (barcode: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var barcodeToDelete by remember { mutableStateOf<String?>(null) }

    Column(modifier = modifier) {
        AidventoryTopAppBar(
            title = stringResource(R.string.expired_top_app_bar_title),
            modifier = Modifier.fillMaxWidth(),
            centerAligned = widthSizeClass != WindowWidthSizeClass.Compact
        )
        AnimatedVisibility(visible = isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
        if (supplies.isEmpty()) {
            ExpiredEmptyContent(
                modifier = Modifier
                    .fillMaxWidth(if (widthSizeClass == WindowWidthSizeClass.Expanded) 0.6f else 1f)
                    .padding(16.dp)
                    .weight(1f)
                    .align(Alignment.CenterHorizontally)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth(if (widthSizeClass == WindowWidthSizeClass.Expanded) 0.6f else 1f)
                    .padding(horizontal = 16.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                supplies.keys.forEach { containerBarcode ->
                    val container = supplies[containerBarcode]!!.first().container
                    val suppliesInContainer = supplies[containerBarcode]!!
                    item {
                        ContainerGroupHeader(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 32.dp),
                            containerName = container?.name,
                            numberOfSupplies = suppliesInContainer.size
                        )
                    }
                    items(
                        items = suppliesInContainer,
                        key = { it.barcode }
                    ) { supply ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                modifier = Modifier.weight(1f),
                                text = supply.name,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            IconButton(onClick = { barcodeToDelete = supply.barcode }) {
                                Icon(
                                    imageVector = AidventoryIcons.DeleteBorder.imageVector,
                                    contentDescription = stringResource(R.string.expired_delete_supply)
                                )
                            }
                        }
                    }
                }
            }
        }
    }


    DeleteDialog(
        open = barcodeToDelete != null,
        title = stringResource(R.string.expired_dialog_delete_title),
        text = stringResource(R.string.expired_dialog_delete_text),
        onConfirmClick = {
            onDeleteSupplyClick(barcodeToDelete!!)
            barcodeToDelete = null
        },
        onDismissClick = {
            barcodeToDelete = null
        }
    )
}

@Composable
private fun ExpiredEmptyContent(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.expired_empty_content_text),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ContainerGroupHeader(
    containerName: String?,
    numberOfSupplies: Int,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = containerName ?: stringResource(R.string.expired_without_container_text),
                style = MaterialTheme.typography.labelLarge
            )
            Text(
                text = pluralStringResource(
                    id = R.plurals.expired_number_of_supplies,
                    numberOfSupplies,
                    numberOfSupplies
                ),
                style = MaterialTheme.typography.labelLarge
            )
        }

        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )
    }
}
