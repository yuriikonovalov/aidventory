package com.aidventory.core.common.designsystem.component.dialogs

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.aidventory.core.R

@Composable
fun ShareQrDialog(
    open: Boolean,
    onSaveClick: () -> Unit,
    onSendClick: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AppDialog(
        modifier = modifier,
        open = open,
        title = { AppDialogTitle(text = stringResource(R.string.share_qr_text)) },
        content = { AppDialogMessageContent(text = stringResource(R.string.share_qr_message)) },
        positiveButtonText = stringResource(R.string.share_qr_button_send),
        onPositiveButtonClick = onSendClick,
        secondPositiveButtonText = stringResource(id = R.string.share_qr_button_save),
        onSecondPositiveButtonClick = onSaveClick,
        negativeButtonText = stringResource(R.string.dialog_button_cancel),
        onNegativeButtonClick = onDismiss
    )
}