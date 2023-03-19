package com.aidventory.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aidventory.core.common.result.Result
import com.aidventory.core.domain.entities.Supply
import com.aidventory.core.domain.model.UserPreferences
import com.aidventory.core.domain.interfaces.repositories.UserPreferencesRepository
import com.aidventory.core.domain.usecases.GetExpiredSuppliesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    userPreferencesRepository: UserPreferencesRepository,
    getExpiredSupplies: GetExpiredSuppliesUseCase
) : ViewModel() {

    val uiState: StateFlow<MainActivityUiState> = combine(
        userPreferencesRepository.userPreferences,
        getExpiredSupplies()
    ) { userPreferences, expiredSuppliesResult ->
        MainActivityUiState.Success(
            userPreferences = userPreferences,
            expiredBadgeValue = expiredSuppliesResult.expiredBadgeValue
        )
    }
        .stateIn(
            scope = viewModelScope,
            initialValue = MainActivityUiState.Loading,
            started = SharingStarted.WhileSubscribed(5000)
        )

    /**
     * A number that should be shown as a badge of the expired screen icon on the navigation views.
     */
    private val Result<Map<String?, List<Supply>>>.expiredBadgeValue: Int
        get() = if (this is Result.Success) data.values.flatten().size else 0
}


sealed interface MainActivityUiState {
    object Loading : MainActivityUiState
    data class Success(
        val userPreferences: UserPreferences,
        val expiredBadgeValue: Int
    ) : MainActivityUiState
}