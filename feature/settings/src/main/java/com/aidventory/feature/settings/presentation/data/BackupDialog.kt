package com.aidventory.feature.settings.presentation.data

import android.annotation.SuppressLint
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.aidventory.core.common.designsystem.component.dialogs.AppDialog
import com.aidventory.core.common.designsystem.component.dialogs.AppDialogMessageContent
import com.aidventory.core.common.designsystem.component.dialogs.AppDialogTitle
import com.aidventory.feature.settings.R

@Composable
internal fun BackupDialog(
    state: DataUiState?,
    onNegativeButtonClick: () -> Unit
) {
    if (state != null) {
        AppDialog(
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false,
                usePlatformDefaultWidth = false
            ),
            modifier = Modifier
                .widthIn(max = 400.dp)
                .padding(horizontal = 16.dp)
                .animateContentSize(),
            open = true,
            title = { AppDialogTitle(text = state.toTitleText()) },
            content = { state.toContent() },
            negativeButtonText = state.toNegativeButtonText(),
            onNegativeButtonClick = onNegativeButtonClick
        )
    }
}

@Composable
private fun DataUiState.toNegativeButtonText(): String {
    return when (this) {
        DataUiState.ImportUiState.Working,
        DataUiState.SaveExportUiState.Working,
        DataUiState.SendExportUiState.Working -> stringResource(R.string.data_backup_dialog_button_cancel)

        else -> stringResource(R.string.data_backup_dialog_button_close)
    }
}

@Composable
private fun DataUiState.toTitleText(): String {
    return when (this) {
        DataUiState.ImportUiState.Success,
        DataUiState.ImportUiState.Failure,
        DataUiState.ImportUiState.Working -> stringResource(R.string.data_backup_dialog_title_import)

        else -> stringResource(R.string.data_backup_dialog_title_export)
    }
}

@SuppressLint("ComposableNaming")
@Composable
private fun DataUiState.toContent() {
    when (this) {
        DataUiState.ImportUiState.Failure -> {
            AppDialogMessageContent(text = stringResource(R.string.data_backup_dialog_import_failed))
        }

        DataUiState.ImportUiState.Success -> {
            AppDialogMessageContent(text = stringResource(R.string.data_backup_dialog_import_finished))
        }

        DataUiState.ImportUiState.Working -> {
            WorkingStateContent(text = stringResource(R.string.data_backup_dialog_import_working))
        }

        DataUiState.SaveExportUiState.Failure -> {
            AppDialogMessageContent(text = stringResource(R.string.data_backup_dialog_export_failed))
        }

        DataUiState.SaveExportUiState.Success -> {
            AppDialogMessageContent(text = stringResource(R.string.data_backup_dialog_export_finished))
        }

        DataUiState.SaveExportUiState.Working -> {
            WorkingStateContent(text = stringResource(R.string.data_backup_dialog_export_working))
        }

        DataUiState.SendExportUiState.Working -> {
            WorkingStateContent(text = stringResource(R.string.data_backup_dialog_export_working))
        }

        DataUiState.SendExportUiState.Failure -> {
            AppDialogMessageContent(text = stringResource(R.string.data_backup_dialog_export_failed))
        }
    }
}

@Composable
private fun WorkingStateContent(
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(text = text, modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.width(16.dp))
        CircularProgressIndicator(
            modifier = Modifier
                .size(24.dp)
                .align(Alignment.CenterVertically),
            strokeCap = StrokeCap.Round
        )
    }
}
