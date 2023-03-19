package com.aidventory.feature.settings.presentation

import androidx.lifecycle.ViewModel
import com.aidventory.feature.settings.SettingCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState = _uiState.asStateFlow()

    fun setIsDetailOpen(isDetailOpen: Boolean) {
        _uiState.update {
            it.copy(
                isDetailOpen = isDetailOpen,
                // If hide detail then reset the selected setting category as well.
                selectedSettingCategory = if (!isDetailOpen) null else it.selectedSettingCategory
            )
        }
    }

    fun closeDetails() {
        setIsDetailOpen(false)
    }

    fun selectSettingCategory(settingCategory: SettingCategory) {
        _uiState.update {
            it.copy(
                selectedSettingCategory = settingCategory,
                isDetailOpen = true
            )
        }
    }
}