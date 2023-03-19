package com.aidventory.feature.scanner.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aidventory.core.common.designsystem.component.BarcodeScannerBottomSheet
import com.aidventory.core.domain.entities.Container
import com.aidventory.core.domain.entities.Supply
import com.aidventory.feature.scanner.R


@Composable
internal fun BottomSheetContent(
    widthSizeClass: WindowWidthSizeClass,
    state: ScannerUiState,
    isTwoColumn: Boolean,
    onAddAsSupplyClick: (String) -> Unit,
    onScanClick: () -> Unit,
    onSupplyResultClick: (String) -> Unit,
    onContainerResultClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var mutableState by remember {
        mutableStateOf<ScannerUiState>(ScannerUiState.NotFoundResult(""))
    }

    if (state is ScannerUiState.NotFoundResult || state is ScannerUiState.MultipleResult) {
        mutableState = state
    }

    BarcodeScannerBottomSheet(
        modifier = modifier.navigationBarsPadding(),
        widthSizeClass = widthSizeClass
    ) {
        when (mutableState) {
            is ScannerUiState.NotFoundResult -> BottomSheetNotFoundContent(
                modifier = Modifier.fillMaxWidth(),
                onAddAsSupplyClick = { onAddAsSupplyClick(state.barcode) },
                onScanClick = onScanClick
            )

            is ScannerUiState.MultipleResult -> {
                val multipleResultState = (mutableState as ScannerUiState.MultipleResult)
                BottomSheetFoundContent(
                    modifier = Modifier.fillMaxWidth(),
                    isTwoColumn = isTwoColumn,
                    supply = multipleResultState.supply,
                    container = multipleResultState.container,
                    onSupplyResultClick = { onSupplyResultClick(multipleResultState.supply.barcode) },
                    onContainerResultClick = { onContainerResultClick(multipleResultState.container.barcode) },
                    onScanClick = onScanClick
                )
            }

            else -> {
                // no-op, other cases are handled by the parent composable.
            }
        }
    }
}

@Composable
private fun BottomSheetNotFoundContent(
    onAddAsSupplyClick: () -> Unit,
    onScanClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            text = stringResource(R.string.bottom_sheet_not_found_text)
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = onAddAsSupplyClick
        ) {
            Text(text = stringResource(R.string.bottom_sheet_not_found_button_add))
        }
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            textAlign = TextAlign.Center,
            text = stringResource(R.string.bottom_sheet_not_found_or)
        )
        FilledTonalButton(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = onScanClick
        ) {
            Text(text = stringResource(R.string.bottom_sheet_not_found_button_scan))
        }
    }
}


@Composable
private fun BottomSheetFoundContent(
    isTwoColumn: Boolean,
    supply: Supply,
    container: Container,
    onSupplyResultClick: () -> Unit,
    onContainerResultClick: () -> Unit,
    onScanClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Text(text = stringResource(R.string.scanner_multiple_result_text))
        Spacer(modifier = Modifier.height(24.dp))
        if (isTwoColumn) {
            Row(modifier = Modifier.fillMaxWidth()) {
                MultipleResultItem(
                    modifier = Modifier.weight(1f),
                    label = stringResource(R.string.scanner_multiple_result_supply_label),
                    text = supply.name,
                    onClick = onSupplyResultClick
                )
                Spacer(modifier = Modifier.width(16.dp))
                MultipleResultItem(
                    modifier = Modifier.weight(1f),
                    label = stringResource(R.string.scanner_multiple_result_container_label),
                    text = container.name,
                    onClick = onContainerResultClick
                )
            }
        } else {
            MultipleResultItem(
                modifier = Modifier.fillMaxWidth(),
                label = stringResource(R.string.scanner_multiple_result_supply_label),
                text = supply.name,
                onClick = onSupplyResultClick
            )
            Spacer(modifier = Modifier.height(16.dp))
            MultipleResultItem(
                modifier = Modifier.fillMaxWidth(),
                label = stringResource(R.string.scanner_multiple_result_container_label),
                text = container.name,
                onClick = onContainerResultClick
            )
        }

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            text = stringResource(R.string.bottom_sheet_not_found_or),
            textAlign = TextAlign.Center
        )
        FilledTonalButton(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = onScanClick
        ) {
            Text(text = stringResource(R.string.bottom_sheet_not_found_button_scan))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MultipleResultItem(
    label: String,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        onClick = onClick
    ) {
        Box(
            modifier = Modifier
                .clip(
                    MaterialTheme.shapes.medium.copy(
                        topEnd = ZeroCornerSize,
                        bottomStart = ZeroCornerSize
                    )
                )
                .background(MaterialTheme.colorScheme.background)
                .padding(bottom = 2.dp, end = 2.dp)
        ) {
            Text(
                modifier = Modifier
                    .clip(
                        MaterialTheme.shapes.medium.copy(
                            topEnd = ZeroCornerSize,
                            bottomStart = ZeroCornerSize
                        )
                    )
                    .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f))
                    .padding(8.dp),
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.surfaceVariant
            )
        }

        Text(
            modifier = Modifier.padding(8.dp),
            text = text,
            fontWeight = FontWeight.SemiBold
        )
    }
}