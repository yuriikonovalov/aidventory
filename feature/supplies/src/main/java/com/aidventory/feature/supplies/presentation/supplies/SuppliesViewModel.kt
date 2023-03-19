package com.aidventory.feature.supplies.presentation.supplies

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aidventory.core.common.result.Result
import com.aidventory.core.domain.entities.Supply
import com.aidventory.core.domain.model.SupplyFilterParams
import com.aidventory.core.domain.model.SupplySortingParams
import com.aidventory.core.domain.usecases.SendQrUseCase
import com.aidventory.core.domain.usecases.SaveQrUseCase
import com.aidventory.core.domain.usecases.DeleteSupplyUseCase
import com.aidventory.core.domain.usecases.GetContainersUseCase
import com.aidventory.core.domain.usecases.GetSuppliesUseCase
import com.aidventory.core.domain.usecases.GetSupplyUsesUseCase
import com.aidventory.feature.supplies.navigation.SuppliesScreenNavigation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SuppliesViewModel @Inject constructor(
    private val getSupplies: GetSuppliesUseCase,
    private val deleteSupply: DeleteSupplyUseCase,
    private val saveQr: SaveQrUseCase,
    private val sendQr: SendQrUseCase,
    getContainers: GetContainersUseCase,
    getSupplyUses: GetSupplyUsesUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(SuppliesUiState())
    val uiState = _uiState.asStateFlow()

    private val _sideEffect = MutableStateFlow<SuppliesSideEffect?>(null)
    val sideEffect = _sideEffect.asStateFlow()

    init {
        var barcode: String? = savedStateHandle[SuppliesScreenNavigation.ARGUMENT]
        collectContainers(getContainers)
        collectSupplyUses(getSupplyUses)

        viewModelScope.launch {
            uiState
                .map { state ->
                    Params(
                        sortingParams = state.supplySortingParams,
                        filterParams = state.supplyFilterParams
                    )
                }
                .distinctUntilChanged()
                .collectLatest {
                    getSupplies(
                        supplySortingParams = it.sortingParams,
                        supplyFilterParams = it.filterParams
                    ).collectLatest { result ->
                        result.applyToUiState()
                        // Update the selected supply barcode to the received barcode from the arguments.
                        barcode?.let {
                            // If we come from the scanner screen, we pass the barcode of a supply,
                            // so this block should be invoked only one time.
                            // In order to stop this block from invoking during each emission caused by
                            // changes in DB (e.g. deleting) or by filter/sort changes,
                            // the barcode must be set to null after updating state.
                            _uiState.update { state ->
                                state.copy(
                                    selectedSupplyBarcode = barcode,
                                    isDetailOpen = true
                                )
                            }
                            barcode = null
                        }
                    }
                }
        }
    }


    fun consumeSideEffect() {
        _sideEffect.value = null
    }

    private fun collectSupplyUses(getSupplyUses: GetSupplyUsesUseCase) {
        viewModelScope.launch {
            getSupplyUses()
                .collectLatest { result ->
                    // Update the state with supply uses only when the result is Success.
                    val supplyUses =
                        if (result is Result.Success) result.data else uiState.value.supplyUses
                    _uiState.update { it.copy(supplyUses = supplyUses) }
                }
        }
    }

    private fun collectContainers(getContainers: GetContainersUseCase) {
        viewModelScope.launch {
            getContainers().collectLatest { result ->
                // Update the state with containers only when the result is Success.
                val containers =
                    if (result is Result.Success) result.data else uiState.value.containers
                _uiState.update { it.copy(containers = containers) }
            }
        }
    }

    fun showSortingOptions() {
        _uiState.update { it.updateModalContentType(ModalContentType.SORT) }
    }

    fun showFilters() {
        _uiState.update { it.updateModalContentType(ModalContentType.FILTER) }
    }

    fun clearFilters() {
        _uiState.update { it.clearFilters() }
    }

    fun hideModalContent() {
        _uiState.update { it.updateModalContentVisibility(isVisible = false) }
    }

    fun changeSorting(supplySortingParams: SupplySortingParams) {
        _uiState.update { it.updateSupplySortingParams(supplySortingParams) }
    }

    fun changeSupplyUseFilter(supplyUseId: Int) {
        _uiState.update { it.updateSupplyUsesFilterParams(supplyUseId) }
    }

    fun changeContainerFilter(containerBarcode: String) {
        _uiState.update { it.updateContainerFilterParams(containerBarcode) }
    }

    fun closeDetails() {
        _uiState.update { it.copy(isDetailOpen = false) }
    }

    fun updateIsDetailOpen(isDetailOpen: Boolean) {
        _uiState.update { it.copy(isDetailOpen = isDetailOpen) }
    }

    fun selectSupply(barcode: String?) {
        _uiState.update { it.copy(selectedSupplyBarcode = barcode) }
    }

    fun clickDeleteSupply(barcode: String?) {
        requireNotNull(barcode)
        viewModelScope.launch {
            deleteSupply(barcode)
        }
    }

    private fun Result<List<Supply>>.applyToUiState() {
        when (this) {
            is Result.Error -> _uiState.update {
                it.copy(
                    supplies = emptyList(),
                    isSuppliesLoading = false,
                    selectedSupplyBarcode = null
                )
            }

            is Result.Success -> _uiState.update {
                it.copy(supplies = this.data, isSuppliesLoading = false)
            }

            Result.Loading -> _uiState.update { it.copy(isSuppliesLoading = true) }
        }
    }

    fun saveBarcode(uri: Uri) {
        val barcode = uiState.value.selectedSupplyBarcode!!
        viewModelScope.launch {
            saveQr(barcode, uri)
        }
    }

    fun sendBarcode() {
        val barcode = uiState.value.selectedSupplyBarcode!!
        viewModelScope.launch {
            val result = sendQr(barcode)
            when (result) {
                is Result.Success -> {
                    _sideEffect.value = SuppliesSideEffect.SendBarcodeIntent(result.data)
                }

                else -> {}
            }
        }
    }

    data class Params(
        val sortingParams: SupplySortingParams,
        val filterParams: SupplyFilterParams
    )
}


sealed interface SuppliesSideEffect {
    data class SendBarcodeIntent(val uri: Uri) : SuppliesSideEffect
}
