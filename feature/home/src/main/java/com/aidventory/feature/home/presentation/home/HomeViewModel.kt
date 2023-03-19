package com.aidventory.feature.home.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aidventory.core.common.result.Result
import com.aidventory.core.domain.model.SupplyFilterParams
import com.aidventory.core.domain.model.SupplySortingParams
import com.aidventory.core.domain.usecases.GetContainersUseCase
import com.aidventory.core.domain.usecases.GetSuppliesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class HomeViewModel @Inject constructor(
    private val getSupplies: GetSuppliesUseCase,
    private val getContainers: GetContainersUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadSupplies()
        loadContainers()
    }

    private fun loadSupplies() {
        viewModelScope.launch {
            getSupplies(SupplySortingParams.Default, SupplyFilterParams())
                .collectLatest { result ->
                    when (result) {
                        is Result.Success -> {
                            _uiState.update {
                                it.copy(
                                    supplies = result.data,
                                    isSuppliesLoading = false
                                )
                            }
                        }

                        is Result.Error -> {
                            _uiState.update { it.copy(isSuppliesLoading = false) }
                        }

                        Result.Loading -> {
                            _uiState.update { it.copy(isSuppliesLoading = true) }
                        }
                    }
                }
        }
    }

    private fun loadContainers() {
        viewModelScope.launch {
            getContainers()
                .collectLatest { result ->
                    when (result) {
                        is Result.Success -> {
                            _uiState.update {
                                it.copy(
                                    containers = result.data,
                                    isContainersLoading = false
                                )
                            }
                        }

                        is Result.Error -> {
                            _uiState.update { it.copy(isContainersLoading = false) }
                        }

                        Result.Loading -> {
                            _uiState.update { it.copy(isContainersLoading = true) }
                        }
                    }
                }
        }
    }
}