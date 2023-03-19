package com.aidventory.core.common.designsystem.component.dialogs

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.aidventory.core.R

@Composable
fun DeleteDialog(
    open: Boolean,
    title: String,
    text: String,
    modifier: Modifier = Modifier,
    onConfirmClick: () -> Unit = {},
    onDismissClick: () -> Unit = {}
) {
    AppDialog(
        modifier = modifier,
        open = open,
        title = { AppDialogTitle(text = title) },
        content = { AppDialogMessageContent(text = text) },
        positiveButtonText = stringResource(id = R.string.dialog_button_delete),
        onPositiveButtonClick = onConfirmClick,
        negativeButtonText = stringResource(id = R.string.dialog_button_cancel),
        onNegativeButtonClick = onDismissClick
    )
}