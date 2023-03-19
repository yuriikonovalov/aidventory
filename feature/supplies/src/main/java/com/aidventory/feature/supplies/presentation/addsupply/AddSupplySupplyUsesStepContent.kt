package com.aidventory.feature.supplies.presentation.addsupply

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aidventory.core.domain.entities.SupplyUse
import com.aidventory.feature.supplies.R

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
internal fun AddSupplySupplyUsesStepContent(
    isExpanded: Boolean,
    supplyUses: List<SupplyUse>,
    selectedSupplyUses: List<SupplyUse>,
    isPreviousButtonVisible: Boolean,
    modifier: Modifier = Modifier,
    onSupplyUseClick: (SupplyUse) -> Unit,
    onNextClick: () -> Unit,
    onPreviousClick: () -> Unit
) {
    val context = LocalContext.current
    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.add_supply_supply_uses_text),
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(8.dp))
        FlowRow {
            supplyUses.forEach { supplyUse ->
                val selected = supplyUse.id in selectedSupplyUses.map { it.id }
                FilterChip(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    label = { Text(supplyUse.displayName(context)) },
                    selected = selected,
                    onClick = { onSupplyUseClick(supplyUse) }
                )
            }

        }

        Spacer(modifier = Modifier.weight(1f))
        StepButtons(
            modifier = Modifier
                .fillMaxWidth(if (isExpanded) 0.6f else 1f)
                .align(Alignment.CenterHorizontally),
            onNextClick = onNextClick,
            onPreviousClick = onPreviousClick,
            isPreviousButtonVisible = isPreviousButtonVisible,
            nextButtonText = stringResource(id = R.string.add_supply_add_step_button_next),
            previousButtonText = stringResource(id = R.string.add_supply_add_step_button_previous)
        )
    }
}