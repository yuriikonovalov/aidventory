package com.aidventory.core.common.designsystem.component.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties


@Composable
fun AppDialog(
    open: Boolean,
    title: @Composable () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
    negativeButtonText: String,
    onNegativeButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = onNegativeButtonClick,
    positiveButtonText: String = "",
    onPositiveButtonClick: (() -> Unit)? = null,
    secondPositiveButtonText: String = "",
    onSecondPositiveButtonClick: (() -> Unit)? = null,
    properties: DialogProperties = DialogProperties()
) {
    if (open)
        Dialog(onDismissRequest = onDismiss, properties = properties) {
            Surface(
                modifier = modifier,
                shape = MaterialTheme.shapes.large,
                border = BorderStroke(0.5.dp, Color.DarkGray)
            ) {
                Column {
                    title()
                    Column(modifier = Modifier.padding(bottom = 16.dp)) {
                        content()
                    }

                    Divider(modifier = Modifier.fillMaxWidth(), thickness = 0.5.dp)
                    when {
                        onPositiveButtonClick != null && onSecondPositiveButtonClick != null -> {
                            DoublePositiveButton(
                                firstPositiveButtonText = positiveButtonText,
                                onFirstPositiveButtonClick = onPositiveButtonClick,
                                secondPositiveButtonText = secondPositiveButtonText,
                                onSecondPositiveButtonClick = onSecondPositiveButtonClick,
                                negativeButtonText = negativeButtonText,
                                onNegativeButtonClick = onNegativeButtonClick
                            )
                        }

                        onPositiveButtonClick != null -> {
                            SinglePositiveButton(
                                firstPositiveButtonText = positiveButtonText,
                                onFirstPositiveButtonClick = onPositiveButtonClick,
                                negativeButtonText = negativeButtonText,
                                onNegativeButtonClick = onNegativeButtonClick
                            )
                        }

                        else -> {
                            NegativeButton(
                                modifier = Modifier.fillMaxWidth(),
                                text = negativeButtonText,
                                onClick = onNegativeButtonClick
                            )
                        }
                    }
                }
            }
        }
}

@Composable
fun DoublePositiveButton(
    firstPositiveButtonText: String,
    onFirstPositiveButtonClick: () -> Unit,
    secondPositiveButtonText: String,
    onSecondPositiveButtonClick: (() -> Unit),
    negativeButtonText: String,
    onNegativeButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            PositiveButton(
                modifier = Modifier.weight(1f),
                text = firstPositiveButtonText,
                onClick = onFirstPositiveButtonClick
            )
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(0.5.dp)
                    .background(Color.Gray)
            )
            PositiveButton(
                modifier = Modifier.weight(1f),
                text = secondPositiveButtonText,
                onClick = onSecondPositiveButtonClick
            )
        }
        Divider(modifier = Modifier.fillMaxWidth(), thickness = 0.5.dp)
        NegativeButton(
            modifier = Modifier.fillMaxWidth(),
            text = negativeButtonText,
            onClick = onNegativeButtonClick
        )
    }
}

@Composable
fun SinglePositiveButton(
    firstPositiveButtonText: String,
    onFirstPositiveButtonClick: () -> Unit,
    negativeButtonText: String,
    onNegativeButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        NegativeButton(
            modifier = Modifier.weight(1f),
            text = negativeButtonText,
            onClick = onNegativeButtonClick
        )
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(0.5.dp)
                .background(Color.Gray)
        )
        PositiveButton(
            modifier = Modifier.weight(1f),
            text = firstPositiveButtonText,
            onClick = onFirstPositiveButtonClick
        )
    }
}


@Composable
fun AppDialogTitle(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier.padding(16.dp),
        text = text,
        style = MaterialTheme.typography.titleMedium
    )
}

@Composable
fun AppDialogMessageContent(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier.padding(horizontal = 16.dp),
        text = text,
        style = MaterialTheme.typography.bodyMedium
    )
}

@Composable
private fun PositiveButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextButton(
        modifier = modifier,
        onClick = onClick,
    ) {
        Text(text = text)
    }
}

@Composable
private fun NegativeButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextButton(
        modifier = modifier,
        onClick = onClick,
        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
    ) {
        Text(text = text)
    }
}