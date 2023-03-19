package com.aidventory.feature.supplies.presentation.quicksearch

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aidventory.core.common.result.Result
import com.aidventory.core.domain.usecases.GetSupplyUseCase
import com.aidventory.core.domain.usecases.IsSupplyInContainerUseCase
import com.aidventory.core.barcode.processing.BarcodeProcessor
import com.aidventory.feature.supplies.navigation.QuickSearchScreenNavigation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuickSearchViewModel @Inject constructor(
    private val isSupplyInContainer: IsSupplyInContainerUseCase,
    getSupplyUseCase: GetSupplyUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _uiState: MutableStateFlow<QuickSearchUiState>

    init {
        val supplyBarcode: String = requireNotNull(
            savedStateHandle[QuickSearchScreenNavigation.ARGUMENT]
        )
        _uiState = MutableStateFlow(
            QuickSearchUiState(
                supplyBarcode = supplyBarcode,
                scanState = QuickSearchUiState.ScanState.Sense
            )
        )

        viewModelScope.launch {
            getSupplyUseCase(supplyBarcode).also { result ->
                when (result) {
                    is Result.Success -> _uiState.update { it.copy(supply = result.data) }
                    is Result.Error -> {}
                    Result.Loading -> {}
                }
            }
        }
    }

    val uiState = _uiState.asStateFlow()

    fun changeBarcodeProcessorState(processorState: BarcodeProcessor.State) {
        when (processorState) {
            BarcodeProcessor.State.Sense -> {
                _uiState.update { it.copy(scanState = QuickSearchUiState.ScanState.Sense) }
            }

            is BarcodeProcessor.State.Recognize -> {
                _uiState.update { it.copy(scanState = QuickSearchUiState.ScanState.Recognize) }
            }

            is BarcodeProcessor.State.Communicate -> {
                val containerBarcode = requireNotNull(processorState.barcode.rawValue)
                viewModelScope.launch {
                    val result = isSupplyInContainer(
                        supplyBarcode = uiState.value.supplyBarcode,
                        containerBarcode = containerBarcode
                    )
                    when (result) {
                        is Result.Success -> {
                            val supplyInContainer = result.data
                            if (supplyInContainer) {
                                _uiState.update { it.copy(scanState = QuickSearchUiState.ScanState.FoundScanResult) }
                            } else {
                                _uiState.update { it.copy(scanState = QuickSearchUiState.ScanState.NotFoundScanResult) }
                            }
                        }

                        is Result.Error -> {
                            _uiState.update {
                                it.copy(scanState = QuickSearchUiState.ScanState.NotFoundScanResult)
                            }
                        }

                        Result.Loading -> {}
                    }
                }
            }
        }
    }

    fun startScanning() {
        _uiState.update { it.copy(scanState = QuickSearchUiState.ScanState.Sense) }
    }
}