package com.aidventory.feature.scanner.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aidventory.core.common.result.Result
import com.aidventory.core.domain.model.SearchByBarcodeResult
import com.aidventory.core.domain.usecases.SearchByBarcodeUseCase
import com.aidventory.core.barcode.processing.BarcodeProcessor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ScannerViewModel @Inject constructor(
    private val searchByBarcode: SearchByBarcodeUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<ScannerUiState>(ScannerUiState.Sense)
    val uiState = _uiState.asStateFlow()

    fun changeBarcodeProcessorState(processorState: BarcodeProcessor.State) {
        when (processorState) {
            BarcodeProcessor.State.Sense -> {
                _uiState.value = ScannerUiState.Sense
            }

            is BarcodeProcessor.State.Recognize -> {
                _uiState.value = ScannerUiState.Recognize
            }

            is BarcodeProcessor.State.Communicate -> {
                val barcode = processorState.barcode.rawValue!!
                viewModelScope.launch {
                    val result = searchByBarcode(barcode = barcode)
                    val searchByBarcodeResult = result.asSearchByBarcodeResult(barcode)
                    _uiState.value = when {
                        searchByBarcodeResult.isMultipleResult() -> searchByBarcodeResult.asMultipleSearchResult()
                        searchByBarcodeResult.isSupplyResult() -> searchByBarcodeResult.asSupplySearchResult()
                        searchByBarcodeResult.isContainerResult() -> searchByBarcodeResult.asContainerSearchResult()
                        else -> ScannerUiState.NotFoundResult(barcode)
                    }
                }
            }
        }

    }

    fun startScanning() {
        _uiState.value = ScannerUiState.Sense
    }

    fun selectSupplyResult(barcode: String) {
        _uiState.value = ScannerUiState.SupplyResult(barcode)
    }

    fun selectContainerResult(barcode: String) {
        _uiState.value = ScannerUiState.ContainerResult(barcode)
    }

    private fun SearchByBarcodeResult.asMultipleSearchResult(): ScannerUiState {
        return ScannerUiState.MultipleResult(
            supply = supply!!,
            container = container!!
        )
    }

    private fun SearchByBarcodeResult.asSupplySearchResult(): ScannerUiState {
        return ScannerUiState.SupplyResult(supply!!.barcode)
    }

    private fun SearchByBarcodeResult.asContainerSearchResult(): ScannerUiState {
        return ScannerUiState.ContainerResult(container!!.barcode)
    }

    private fun Result<SearchByBarcodeResult>.asSearchByBarcodeResult(barcode: String): SearchByBarcodeResult {
        return when (this) {
            is Result.Success -> this.data
            /* Result.Error doesn't return the barcode as Result.Success does.
            So if Result.Error is returned, pass the barcode
            so that it can be used for SearchResult.NotFound. */
            else -> SearchByBarcodeResult(barcode = barcode)
        }
    }
}