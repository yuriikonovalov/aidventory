package com.aidventory.core.common.designsystem.component

import android.annotation.SuppressLint
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState

@SuppressLint("ComposableNaming")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ModalBottomSheetState.onDismiss(condition: Boolean = true, action: () -> Unit) {
    val rememberedAction by rememberUpdatedState(action)
    if (currentValue != ModalBottomSheetValue.Hidden) {
        DisposableEffect(Unit) {
            onDispose {
                if (condition) {
                    rememberedAction()
                }
            }
        }
    }
}