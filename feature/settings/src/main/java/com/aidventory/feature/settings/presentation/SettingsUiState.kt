package com.aidventory.feature.settings.presentation

import com.aidventory.feature.settings.SettingCategory

data class SettingsUiState(
    val isDetailOpen: Boolean = false,
    val selectedSettingCategory: SettingCategory? = null
)