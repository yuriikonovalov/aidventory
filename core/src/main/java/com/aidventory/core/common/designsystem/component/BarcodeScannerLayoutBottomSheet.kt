package com.aidventory.core.common.designsystem.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun BarcodeScannerBottomSheet(
    widthSizeClass: WindowWidthSizeClass,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        shape = MaterialTheme.shapes.large,
        modifier = modifier.bottomSheetPadding(widthSizeClass)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            content()
        }
    }
}

private fun Modifier.bottomSheetPadding(widthSizeClass: WindowWidthSizeClass): Modifier {
    val padding = if (widthSizeClass == WindowWidthSizeClass.Compact) 16.dp else 32.dp
    return this.padding(padding)
}
