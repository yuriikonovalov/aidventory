package com.aidventory.presentation.onboarding

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aidventory.core.domain.interfaces.repositories.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    var uiState by mutableStateOf<OnboardingUiState>(OnboardingUiState.ManageSuppliesFeature)
        private set

    fun next() {
        when (uiState) {
            OnboardingUiState.ManageSuppliesFeature -> uiState = OnboardingUiState.ScannerFeature
            OnboardingUiState.ScannerFeature -> uiState = OnboardingUiState.NotificationFeature
            else -> {}
        }
    }

    fun getStarted() {
        viewModelScope.launch {
            userPreferencesRepository.setShouldHideOnboarding(true)
        }
    }
}

sealed interface OnboardingUiState {
    object ManageSuppliesFeature : OnboardingUiState
    object ScannerFeature : OnboardingUiState
    object NotificationFeature : OnboardingUiState
}