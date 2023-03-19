package com.aidventory.feature.settings.presentation.data

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aidventory.core.barcode.sendBarcodeIntent
import com.aidventory.core.common.designsystem.component.dialogs.AppDialog
import com.aidventory.core.common.designsystem.component.dialogs.AppDialogMessageContent
import com.aidventory.core.common.designsystem.component.dialogs.AppDialogTitle
import com.aidventory.core.common.designsystem.component.dialogs.DeleteDialog
import com.aidventory.feature.settings.R

@Composable
internal fun DataScreen(
    viewModel: DataViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json"),
        onResult = { uri -> uri?.let { viewModel.saveExportData(uri) } }
    )

    val openDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri -> uri?.let { viewModel.importData(uri) } }
    )

    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val sideEffect by viewModel.sideEffect.collectAsStateWithLifecycle()

    when (sideEffect) {
        is DataSideEffect.SendDataIntent -> {
            context.sendBarcodeIntent((sideEffect as DataSideEffect.SendDataIntent).uri)
            viewModel.consumeSideEffect()
        }

        else -> {
            // when null
        }
    }

    BackupDialog(
        state = state,
        onNegativeButtonClick = viewModel::cancel
    )

    DataScreenContent(
        modifier = Modifier.padding(horizontal = 16.dp),
        onImportClick = { openDocumentLauncher.launch(arrayOf("application/json")) },
        onSaveExportClick = { createDocumentLauncher.launch("aidventory-backup") },
        onSendExportClick = viewModel::sendExportData,
        onClearClick = viewModel::clear
    )
}

@Composable
private fun DataScreenContent(
    onImportClick: () -> Unit,
    onSaveExportClick: () -> Unit,
    onSendExportClick: () -> Unit,
    onClearClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    var isExportDialogOpen by remember { mutableStateOf(false) }
    var isClearDialogOpen by remember { mutableStateOf(false) }

    DeleteDialog(
        open = isClearDialogOpen,
        title = stringResource(R.string.data_delete_dialog_title),
        text = stringResource(R.string.data_delete_dialog_text),
        onConfirmClick = {
            onClearClick()
            isClearDialogOpen = false
        },
        onDismissClick = { isClearDialogOpen = false }
    )

    ExportDataDialog(
        isOpen = isExportDialogOpen,
        onSaveClick = {
            onSaveExportClick()
            isExportDialogOpen = false
        },
        onSendClick = {
            isExportDialogOpen = false
            onSendExportClick()
        },
        onDismiss = { isExportDialogOpen = false })


    Column(modifier = modifier) {
        DataActionItem(
            text = stringResource(R.string.data_action_export),
            description = stringResource(R.string.data_action_export_description),
            onClick = { isExportDialogOpen = true }
        )
        DataActionItem(
            text = stringResource(R.string.data_action_import),
            description = stringResource(R.string.data_action_import_description),
            onClick = onImportClick
        )
        DataActionItem(
            text = stringResource(R.string.data_action_clear),
            description = stringResource(R.string.data_action_clear_description),
            onClick = { isClearDialogOpen = true }
        )
    }
}

@Composable
private fun DataActionItem(
    text: String,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}


@Composable
private fun ExportDataDialog(
    isOpen: Boolean,
    onSaveClick: () -> Unit,
    onSendClick: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AppDialog(
        modifier = modifier,
        open = isOpen,
        title = { AppDialogTitle(text = stringResource(R.string.data_export_dialog_title)) },
        content = { AppDialogMessageContent(text = stringResource(R.string.data_export_dialog_text)) },
        positiveButtonText = stringResource(R.string.data_export_dialog_button_send),
        onPositiveButtonClick = onSendClick,
        secondPositiveButtonText = stringResource(R.string.data_export_dialog_button_save),
        onSecondPositiveButtonClick = onSaveClick,
        negativeButtonText = stringResource(R.string.button_cancel),
        onNegativeButtonClick = onDismiss
    )
}