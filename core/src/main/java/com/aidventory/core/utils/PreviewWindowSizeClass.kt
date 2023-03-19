package com.aidventory.core.utils

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
object PreviewWindowSizeClass {
    val PHONE = WindowSizeClass.calculateFromSize(DpSize(width = 411.dp, height = 891.dp))
    val FOLDABLE = WindowSizeClass.calculateFromSize(DpSize(width = 673.dp, height = 841.dp))
    val TABLET = WindowSizeClass.calculateFromSize(DpSize(width = 1280.dp, height = 800.dp))
    val DESKTOP = WindowSizeClass.calculateFromSize(DpSize(width = 1920.dp, height = 1080.dp))
}