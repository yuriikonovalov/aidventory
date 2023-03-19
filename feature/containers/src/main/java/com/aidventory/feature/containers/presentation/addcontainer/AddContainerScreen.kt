package com.aidventory.feature.containers.presentation.addcontainer

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.aidventory.core.common.designsystem.component.AidventoryTopAppBar
import com.aidventory.core.common.designsystem.icon.AidventoryIcons
import com.aidventory.core.common.designsystem.theme.AidventoryTheme
import com.aidventory.core.barcode.sendBarcodeIntent
import com.aidventory.feature.containers.R

@Composable
fun AddContainerScreen(
    onNavigationClick: () -> Unit = {},
    viewModel: AddContainerViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    // Always true because the screen is only used for Compact WindowWidthClass.
    val isCompact = true
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

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
        is AddContainerSideEffect.SendBarcodeIntent -> {
            context.sendBarcodeIntent((sideEffect as AddContainerSideEffect.SendBarcodeIntent).uri)
            viewModel.consumeSideEffect()
        }

        else -> {
            // when null
        }
    }

    AddContainerScreenContent(
        modifier = Modifier.fillMaxSize(),
        isCompact = isCompact,
        state = uiState,
        onNavigationClick = onNavigationClick,
        onInputChanged = viewModel::inputName,
        onAddContainerClick = viewModel::addContainer,
        onSendClick = viewModel::sendQRCodePdf,
        onSaveClick = { fileName -> createDocumentLauncher.launch(fileName) }
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddContainerScreenContent(
    isCompact: Boolean,
    state: AddContainerUiState,
    onNavigationClick: () -> Unit,
    onInputChanged: (value: String) -> Unit,
    onAddContainerClick: () -> Unit,
    onSendClick: () -> Unit,
    onSaveClick: (fileName: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        when (state) {
            is AddContainerUiState.AddContainer -> {
                AidventoryTopAppBar(
                    navigationIcon = AidventoryIcons.ArrowBack.imageVector,
                    onNavigationClick = onNavigationClick
                )

                AddContainerContent(
                    modifier = Modifier
                        .navigationBarsPadding()
                        .imePadding()
                        .padding(16.dp),
                    isCompact = isCompact,
                    label = stringResource(R.string.add_container_label_name),
                    supportingText = stringResource(R.string.add_container_supporting_text),
                    placeholder = stringResource(R.string.add_container_placeholder),
                    isError = state.emptyNameError,
                    value = state.name,
                    onValueChange = onInputChanged,
                    onSaveClick = onAddContainerClick
                )
            }

            is AddContainerUiState.ShareBarcode -> {
                AidventoryTopAppBar(
                    navigationIcon = AidventoryIcons.Close.imageVector,
                    onNavigationClick = onNavigationClick
                )
                ShareBarcodeContent(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .navigationBarsPadding()
                        .padding(bottom = 16.dp, start = 16.dp, end = 16.dp),
                    isCompact = isCompact,
                    rawValue = state.barcode,
                    onSendClick = onSendClick,
                    onSaveClick = { onSaveClick("aidventory-${state.barcode}") }
                )
            }
        }

    }
}

@Preview(device = Devices.PHONE)
@Composable
private fun AddContainerScreenPreviewPhone() {
    AidventoryTheme {
        AddContainerScreen()
    }
}