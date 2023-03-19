package com.aidventory.feature.supplies.presentation.addsupply

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.aidventory.feature.supplies.R


@Composable
internal fun AddSupplyNameStepContent(
    isExpanded: Boolean, name: String,
    isError: Boolean,
    isPreviousButtonVisible: Boolean,
    onNameChange: (String) -> Unit,
    onNextClick: () -> Unit,
    onPreviousClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }

    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.add_supply_name_text),
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(if (isExpanded) 0.6f else 1f)
                .align(Alignment.CenterHorizontally)
                .focusRequester(focusRequester),
            maxLines = 1,
            value = name,
            onValueChange = onNameChange,
            placeholder = {
                Text(stringResource(R.string.add_supply_name_input_placeholder))
            },
            isError = isError,
            supportingText = {
                Text(stringResource(R.string.add_supply_name_input_supporting_text))
            },

            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
        )
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

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}