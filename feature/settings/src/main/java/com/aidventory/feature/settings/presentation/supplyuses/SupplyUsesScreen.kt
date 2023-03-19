package com.aidventory.feature.settings.presentation.supplyuses

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aidventory.core.common.designsystem.component.dialogs.AppDialog
import com.aidventory.core.common.designsystem.component.dialogs.DeleteDialog
import com.aidventory.core.common.designsystem.icon.AidventoryIcons
import com.aidventory.core.domain.entities.SupplyUse
import com.aidventory.feature.settings.R

@Composable
internal fun SupplyUsesScreen(
    viewModel: SupplyUsesViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    SupplyUsesScreenContent(
        modifier = Modifier.padding(horizontal = 16.dp),
        isAddSupplyUseDialogOpen = state.isAddSupplyUseDialogOpen,
        supplyUses = state.supplyUses,
        name = state.name,
        isNameError = state.isNameError,
        onNameChanged = viewModel::inputName,
        onDeleteClick = viewModel::delete,
        onSaveSupplyUseClick = viewModel::saveSupplyUse,
        onCloseAddSupplyDialogClick = viewModel::closeAddSupplyUseDialog,
        onAddSupplyUseClick = viewModel::openAddSupplyUseDialog
    )
}


@Composable
private fun SupplyUsesScreenContent(
    isAddSupplyUseDialogOpen: Boolean,
    supplyUses: List<SupplyUse>,
    name: String,
    isNameError: Boolean,
    onNameChanged: (String) -> Unit,
    onDeleteClick: (id: Int) -> Unit,
    onSaveSupplyUseClick: () -> Unit,
    onCloseAddSupplyDialogClick: () -> Unit,
    onAddSupplyUseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var supplyUseToDelete by remember { mutableStateOf<SupplyUse?>(null) }

    Column(modifier = modifier) {
        TextButton(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = onAddSupplyUseClick
        ) {
            Icon(imageVector = AidventoryIcons.Add.imageVector, contentDescription = null)
            Text(stringResource(R.string.supply_uses_button_new))
        }
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(
                items = supplyUses,
                key = { _, item -> item.id }
            ) { index, item ->
                val isLastItem = index == supplyUses.lastIndex
                SupplyUseItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(if (isLastItem) Modifier.navigationBarsPadding() else Modifier),
                    supplyUse = item,
                    onDeleteClick = { supplyUseToDelete = item }
                )
            }
        }
    }

    supplyUseToDelete?.let { supplyUse ->
        DeleteDialog(
            open = true,
            title = stringResource(R.string.supply_uses_delete_dialog_title),
            text = stringResource(
                R.string.supply_uses_delete_dialog_text,
                supplyUse.displayName(context)
            ),
            onConfirmClick = {
                onDeleteClick(supplyUse.id)
                supplyUseToDelete = null
            },
            onDismissClick = { supplyUseToDelete = null }
        )
    }

    AddSupplyUseDialog(
        open = isAddSupplyUseDialogOpen,
        value = name,
        onValueChange = onNameChanged,
        isError = isNameError,
        onSaveClick = onSaveSupplyUseClick,
        onCloseClick = onCloseAddSupplyDialogClick
    )
}

@Composable
private fun SupplyUseItem(
    supplyUse: SupplyUse,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = supplyUse.displayName(context)
        )
        IconButton(onClick = onDeleteClick) {
            Icon(
                imageVector = AidventoryIcons.DeleteBorder.imageVector,
                contentDescription = stringResource(R.string.supply_use_button_delete)
            )
        }
    }
}

@Composable
private fun AddSupplyUseDialog(
    open: Boolean,
    isError: Boolean,
    value: String,
    onValueChange: (String) -> Unit,
    onCloseClick: () -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AppDialog(
        modifier = modifier,
        open = open,
        title = { },
        content = {
            val focusRequester = remember { FocusRequester() }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                modifier = Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 8.dp
                ),
                text = stringResource(id = R.string.supply_uses_add_dialog_label),
                style = MaterialTheme.typography.labelLarge
            )
            OutlinedTextField(
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .padding(horizontal = 16.dp),
                value = value,
                onValueChange = onValueChange,
                placeholder = { Text(stringResource(R.string.supply_uses_add_dialog_placeholder)) },
                supportingText = { Text(stringResource(R.string.supply_uses_add_dialog_error)) },
                isError = isError,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
            )
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }
        },
        positiveButtonText = stringResource(R.string.button_save),
        onPositiveButtonClick = onSaveClick,
        negativeButtonText = stringResource(R.string.button_cancel),
        onNegativeButtonClick = onCloseClick
    )
}



