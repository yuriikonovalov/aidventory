package com.aidventory.core.common.designsystem.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AssistChip
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aidventory.core.R
import com.aidventory.core.common.AppDateTimeFormatter
import com.aidventory.core.common.designsystem.theme.AidventoryTheme
import com.aidventory.core.domain.entities.Container
import com.aidventory.core.domain.entities.Supply
import com.aidventory.core.domain.entities.SupplyUse
import java.time.LocalDate

@Composable
fun SupplyDetailContent(
    supply: Supply,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Name(
            modifier = Modifier.padding(horizontal = 16.dp),
            name = supply.name
        )
        SupplyUses(
            // Horizontal padding is handled by the child items because the LazyRow's item padding.
            modifier = Modifier.padding(vertical = 16.dp),
            labelText = stringResource(id = R.string.supply_detail_supply_uses_label),
            placeholderText = stringResource(id = R.string.supply_detail_supply_uses_placeholder),
            supplyUses = supply.uses
        )
        Expiry(
            modifier = Modifier.padding(16.dp),
            labelText = stringResource(id = R.string.supply_detail_expiry_label),
            placeholderText = stringResource(id = R.string.supply_detail_expiry_placeholder),
            expiry = supply.expiry
        )
        SupplyContainer(
            modifier = Modifier.padding(16.dp),
            labelText = stringResource(id = R.string.supply_detail_container_label),
            placeholderText = stringResource(id = R.string.supply_detail_container_placeholder),
            containerName = supply.container?.name
        )

        Spacer(modifier = Modifier.height(32.dp))

        FilledTonalButton(
            onClick = onSearchClick,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            enabled = supply.container != null
        ) {
            Text(text = stringResource(id = R.string.supply_detail_search_button))
        }
        if (supply.container == null) {
            Text(
                modifier = Modifier.padding(16.dp),
                text = stringResource(R.string.supply_detail_content_quick_search_disabled_text),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun Name(
    name: String,
    modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier,
        text = name,
        style = MaterialTheme.typography.headlineLarge
    )
}

@Composable
private fun SupplyUses(
    labelText: String,
    placeholderText: String,
    supplyUses: List<SupplyUse>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Label(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            text = labelText
        )
        if (supplyUses.isEmpty()) {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = placeholderText
            )
        } else {
            LazyRow {
                itemsIndexed(items = supplyUses, key = { _, item -> item.id }) { index, supplyUse ->
                    val padding = chipPadding(index, supplyUses.lastIndex)
                    AssistChip(
                        modifier = Modifier.padding(padding),
                        shape = MaterialTheme.shapes.extraLarge,
                        label = {
                            Text(
                                modifier = Modifier.padding(8.dp),
                                text = supplyUse.displayName(LocalContext.current)
                            )
                        },
                        onClick = {}
                    )
                }
            }
        }
    }
}


@Composable
private fun Expiry(
    labelText: String,
    placeholderText: String,
    expiry: LocalDate?,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Label(text = labelText)
        Spacer(Modifier.height(8.dp))
        val color = if (expiry?.isBefore(LocalDate.now()) == true) {
            MaterialTheme.colorScheme.error
        } else {
            Color.Unspecified
        }
        Text(
            text = expiry?.format(AppDateTimeFormatter.fullDate()) ?: placeholderText,
            color = color
        )
    }
}

@Composable
private fun SupplyContainer(
    labelText: String,
    placeholderText: String,
    containerName: String?,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Label(text = labelText)
        Spacer(Modifier.height(8.dp))
        Text(text = containerName ?: placeholderText)
    }
}

@Composable
private fun Label(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier,
        text = text,
        style = MaterialTheme.typography.labelLarge
    )
}

/**
 * Calculate the padding of a chip based on its position in the list.
 */
private fun chipPadding(index: Int, lastIndex: Int): PaddingValues {
    return when (index) {
        0 -> PaddingValues(start = 16.dp, end = 8.dp)
        lastIndex -> PaddingValues(start = 8.dp, end = 16.dp)
        else -> PaddingValues(horizontal = 16.dp)
    }
}


@Preview(
    device = Devices.PHONE,
    showSystemUi = true,
    showBackground = true,
    backgroundColor = 0xFFFCFCFF
)
@Composable
private fun SupplyDetailContentPreview() {
    AidventoryTheme {
        SupplyDetailContent(
            supply = Supply(
                barcode = "code1",
                name = "Paracetamol",
                uses = listOf(
                    SupplyUse(1, "pain-killer", true),
                    SupplyUse(2, "cough", true)
                ),
                expiry = LocalDate.now().plusMonths(2),
                container = Container("qr1", "Container #1", LocalDate.now().minusDays(5)),
                isBarcodeGenerated = false
            ),
            onSearchClick = {}
        )
    }
}
