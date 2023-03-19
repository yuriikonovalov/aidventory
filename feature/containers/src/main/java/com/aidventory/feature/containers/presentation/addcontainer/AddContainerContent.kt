package com.aidventory.feature.containers.presentation.addcontainer

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aidventory.core.barcode.createQRCodeBitmap
import com.aidventory.core.common.designsystem.icon.AidventoryIcons
import com.aidventory.core.common.designsystem.theme.AidventoryTheme
import com.aidventory.feature.containers.R


@Composable
internal fun AddContainerContent(
    isCompact: Boolean,
    label: String,
    supportingText: String,
    placeholder: String,
    isError: Boolean,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    onSaveClick: () -> Unit = {}
) {
    val focusRequester = remember { FocusRequester() }
    Column(modifier = modifier) {
        Text(
            modifier = Modifier.padding(bottom = 8.dp),
            text = label,
            style = MaterialTheme.typography.labelLarge
        )
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            maxLines = 1,
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder) },
            supportingText = { Text(supportingText) },
            isError = isError,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
        )

        // If isCompact is true (mobile->fullscreen) then we make the spacer take up
        // all of the available space between the above and below views.
        // Otherwise (for a dialog) it takes up only 32dp.
        Spacer(
            modifier = Modifier.then(
                if (isCompact) Modifier.weight(1f) else Modifier.height(32.dp)
            )
        )

        // Show this button only for WindowWidthSizeClass.Compact.
        // Dialogs have their own buttons when it's Medium and Expanded.
        if (isCompact)
            FilledTonalButton(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = onSaveClick
            ) {
                Text(text = stringResource(R.string.dialog_button_add_container))
            }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}


@Composable
internal fun ShareBarcodeContent(
    isCompact: Boolean,
    rawValue: String,
    onSendClick: () -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val color = MaterialTheme.colorScheme.onBackground.toArgb()
    val bitmap = remember(rawValue, color) {
        createQRCodeBitmap(rawValue, 200, color)
    }

    Column(modifier = modifier) {
        Text(
            modifier = Modifier.padding(top = 16.dp, bottom = 16.dp),
            text = stringResource(id = R.string.share_qr_text1)
        )

        Image(
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.CenterHorizontally),
            bitmap = bitmap!!.asImageBitmap(),
            contentDescription = null
        )

        Text(
            modifier = Modifier.padding(vertical = 16.dp),
            text = stringResource(id = R.string.share_qr_text2)
        )

        Spacer(
            modifier = Modifier.then(
                if (isCompact) Modifier.weight(1f) else Modifier.height(32.dp)
            )
        )


        // Show this button only for WindowWidthSizeClass.Compact.
        // Dialogs have their own buttons when it's Medium and Expanded.
        if (isCompact)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
            ) {
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onSendClick
                ) {
                    Icon(imageVector = AidventoryIcons.Send.imageVector, contentDescription = null)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = stringResource(id = R.string.share_qr_button_send))
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onSaveClick,
                ) {
                    Icon(imageVector = AidventoryIcons.Save.imageVector, contentDescription = null)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = stringResource(id = R.string.share_qr_button_save))
                }
            }
    }
}

@Preview(device = Devices.PHONE)
@Composable
private fun AddContainerSectionPreviewPhone() {
    AidventoryTheme {
        AddContainerContent(
            isCompact = true,
            label = "Name",
            value = "a",
            onValueChange = {},
            supportingText = "Cannot be empty",
            placeholder = "eg. Container #1",
            isError = false
        )
    }
}