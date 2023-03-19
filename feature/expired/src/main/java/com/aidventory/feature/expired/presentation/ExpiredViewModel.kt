package com.aidventory.feature.expired.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aidventory.core.common.result.Result
import com.aidventory.core.domain.usecases.DeleteSupplyUseCase
import com.aidventory.core.domain.usecases.GetExpiredSuppliesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class ExpiredViewModel @Inject constructor(
    private val deleteSupply: DeleteSupplyUseCase,
    private val getExpiredSupplies: GetExpiredSuppliesUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(ExpiredUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getExpiredSupplies()
                .collect { result ->
                    when (result) {
                        is Result.Success -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    suppliesByContainer = result.data
                                )
                            }
                        }

                        is Result.Error -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    suppliesByContainer = emptyMap()
                                )
                            }
                        }

                        Result.Loading -> {
                            _uiState.update { it.copy(isLoading = true) }
                        }
                    }
                }
        }
    }

    fun deleteExpiredSupply(barcode: String) {
        viewModelScope.launch {
            deleteSupply(barcode)
        }
    }
}