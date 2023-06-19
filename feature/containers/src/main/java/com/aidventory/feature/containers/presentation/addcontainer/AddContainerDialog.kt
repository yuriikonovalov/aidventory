package com.aidventory.feature.containers.presentation.addcontainer

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aidventory.core.barcode.sendBarcodeIntent
import com.aidventory.core.common.designsystem.component.dialogs.DoublePositiveButton
import com.aidventory.core.common.designsystem.component.dialogs.SinglePositiveButton
import com.aidventory.core.common.designsystem.theme.AidventoryTheme
import com.aidventory.feature.containers.R

@Composable
internal fun AddContainerDialog(
    onCloseClick: () -> Unit = {},
    viewModel: AddContainerViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/pdf"),
        onResult = { uri ->
            uri?.let {
                viewModel.saveBarcode(it)
            }
        }
    )

    val sideEffect by viewModel.sideEffect.collectAsStateWithLifecycle()

    when (sideEffect) {
        is AddContainerSideEffect.SendBarcodeIntent -> {
            context.sendBarcodeIntent((sideEffect as AddContainerSideEffect.SendBarcodeIntent).uri)
            viewModel.consumeSideEffect()
        }

        else -> {
            // when null
        }
    }

    AddContainerDialogContent(
        // Always false because the dialog is only used for Medium And Expanded WindowWidthClass.
        isCompact = false,
        state = uiState,
        onInputChanged = viewModel::inputName,
        onAddContainerClick = viewModel::addContainer,
        onCloseClick = onCloseClick,
        onSendClick = viewModel::sendQRCodePdf,
        onSaveClick = { fileName ->
            createDocumentLauncher.launch(fileName)
        }
    )
}


@Composable
private fun AddContainerDialogContent(
    isCompact: Boolean,
    state: AddContainerUiState,
    onInputChanged: (value: String) -> Unit,
    onAddContainerClick: () -> Unit,
    onCloseClick: () -> Unit,
    onSendClick: () -> Unit,
    onSaveClick: (fileName: String) -> Unit
) {
    Surface(
        modifier = Modifier
            .wrapContentSize()
            .sizeIn(maxWidth = 400.dp)
            .animateContentSize(),
        shape = MaterialTheme.shapes.large
    ) {
        Column {
            when (state) {
                is AddContainerUiState.AddContainer -> {
                    AddContainerContent(
                        modifier = Modifier
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState()),
                        isCompact = isCompact,
                        label = stringResource(R.string.add_container_label_name),
                        supportingText = stringResource(R.string.add_container_supporting_text),
                        placeholder = stringResource(R.string.add_container_placeholder),
                        isError = state.emptyNameError,
                        value = state.name,
                        onValueChange = onInputChanged,
                        onSaveClick = onAddContainerClick
                    )
                    Divider(modifier = Modifier.fillMaxWidth(), thickness = 0.5.dp)
                    SinglePositiveButton(
                        firstPositiveButtonText = stringResource(R.string.share_qr_button_save),
                        onFirstPositiveButtonClick = onAddContainerClick,
                        negativeButtonText = stringResource(com.aidventory.core.R.string.dialog_button_cancel),
                        onNegativeButtonClick = onCloseClick
                    )
                }

                is AddContainerUiState.ShareBarcode -> {
                    ShareBarcodeContent(
                        modifier = Modifier
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState()),
                        isCompact = isCompact,
                        rawValue = state.barcode,
                        onSendClick = onSendClick,
                        onSaveClick = { onSaveClick("aidventory-${state.barcode}") }
                    )
                    Divider(modifier = Modifier.fillMaxWidth(), thickness = 0.5.dp)
                    DoublePositiveButton(
                        firstPositiveButtonText = stringResource(R.string.share_qr_button_send),
                        onFirstPositiveButtonClick = onSendClick,
                        secondPositiveButtonText = stringResource(R.string.share_qr_button_save),
                        onSecondPositiveButtonClick = { onSaveClick("aidventory-${state.barcode}") },
                        negativeButtonText = stringResource(com.aidventory.core.R.string.dialog_button_cancel),
                        onNegativeButtonClick = onCloseClick
                    )
                }
            }
        }
    }
}


@Preview(device = Devices.TABLET)
@Composable
private fun AddContainerDialogPreviewTablet() {
    AidventoryTheme {
        AddContainerDialog()
    }
}