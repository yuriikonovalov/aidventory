package com.aidventory.feature.supplies.presentation.addsupply

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aidventory.core.common.designsystem.icon.AidventoryIcons
import com.aidventory.feature.supplies.R
import java.time.LocalDate
import java.time.YearMonth

@Composable
internal fun AddSupplyExpiryDateContent(
    isExpanded: Boolean,
    date: LocalDate?,
    isPreviousButtonVisible: Boolean,
    modifier: Modifier = Modifier,
    onDateChange: (LocalDate?) -> Unit,
    onNextClick: () -> Unit,
    onPreviousClick: () -> Unit
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.add_supply_expiry_date_text),
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(16.dp))
        BoxWithConstraints(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            val columnModifier =
                if (maxWidth > maxHeight) Modifier.fillMaxHeight() else Modifier.fillMaxWidth()
            val shouldUseWeight = maxWidth > maxHeight

            Column(
                modifier = columnModifier.verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Calendar(
                    selectedDate = date,
                    onDateChanged = onDateChange,
                    modifier = Modifier
                        .widthIn(max = 500.dp)
                        .heightIn(min = 300.dp)
                        .then(if (shouldUseWeight) Modifier.weight(1f) else Modifier),
                    minDate = LocalDate.now(),
                    initialMonth = YearMonth.now(),
                )
                TextButton(
                    modifier = Modifier.padding(vertical = 16.dp),
                    enabled = date != null,
                    onClick = { onDateChange(null) }
                )
                {
                    Icon(
                        imageVector = AidventoryIcons.Close.imageVector,
                        contentDescription = null
                    )
                    Text(stringResource(R.string.add_supply_expiry_date_button_clear))
                }
            }
        }

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