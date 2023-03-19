package com.aidventory.feature.settings.presentation.supplyuses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aidventory.core.common.result.Result
import com.aidventory.core.domain.usecases.AddSupplyUseUseCase
import com.aidventory.core.domain.usecases.DeleteSupplyUseUseCase
import com.aidventory.core.domain.usecases.GetSupplyUsesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SupplyUsesViewModel @Inject constructor(
    private val getSupplyUses: GetSupplyUsesUseCase,
    private val addSupplyUse: AddSupplyUseUseCase,
    private val deleteSupplyUse: DeleteSupplyUseUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SupplyUsesUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getSupplyUses()
                .collect { result ->
                    when (result) {
                        Result.Loading -> _uiState.update { it.copy(isLoading = true) }
                        is Result.Error -> _uiState.update {
                            it.copy(isLoading = false, supplyUses = emptyList())
                        }

                        is Result.Success -> _uiState.update {
                            it.copy(isLoading = false, supplyUses = result.data)
                        }
                    }
                }
        }
    }

    fun saveSupplyUse() {
        if (uiState.value.name.isEmpty()) {
            _uiState.update { it.copy(isNameError = true) }
        } else {
            viewModelScope.launch {
                addSupplyUse(uiState.value.name)
                closeAddSupplyUseDialog()
            }
        }
    }

    fun openAddSupplyUseDialog() {
        _uiState.update { it.copy(isAddSupplyUseDialogOpen = true) }
    }

    fun closeAddSupplyUseDialog() {
        _uiState.update {
            it.copy(
                isAddSupplyUseDialogOpen = false,
                name = "",
                isNameError = false
            )
        }
    }

    fun delete(id: Int) {
        viewModelScope.launch {
            deleteSupplyUse(id)
        }
    }

    fun inputName(text: String) {
        _uiState.update { it.copy(name = text, isNameError = false) }
    }
}