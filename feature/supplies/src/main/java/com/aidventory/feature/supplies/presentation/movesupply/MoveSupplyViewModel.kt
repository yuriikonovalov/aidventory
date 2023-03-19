package com.aidventory.feature.supplies.presentation.movesupply

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aidventory.core.common.result.Result
import com.aidventory.core.domain.usecases.GetContainersUseCase
import com.aidventory.core.domain.usecases.MoveSupplyUseCase
import com.aidventory.core.barcode.processing.BarcodeProcessor
import com.aidventory.feature.supplies.navigation.MoveSupplyScreenNavigation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoveSupplyViewModel @Inject constructor(
    private val moveSupply: MoveSupplyUseCase,
    getContainers: GetContainersUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _uiState = MutableStateFlow(MoveSupplyUiState())
    val uiState = _uiState.asStateFlow()

    init {
        val supplyBarcode: String =
            requireNotNull(savedStateHandle[MoveSupplyScreenNavigation.SUPPLY_ARGUMENT])
        val containerBarcode: String? =
            savedStateHandle[MoveSupplyScreenNavigation.CONTAINER_ARGUMENT]

        viewModelScope.launch {
            getContainers()
                .map { result ->
                    when (result) {
                        is Result.Success -> result.data
                        is Result.Error -> emptyList()
                        Result.Loading -> emptyList()
                    }
                }
                .collect { containers ->
                    val availableContainers = containers.filter { it.barcode != containerBarcode }
                    _uiState.update {
                        it.copy(
                            containers = availableContainers,
                            supplyBarcode = supplyBarcode
                        )
                    }
                }
        }
    }

    fun changeBarcodeProcessorState(processorState: BarcodeProcessor.State) {
        if (uiState.value.mode !is MoveSupplyUiState.Mode.Scan) return

        when (processorState) {
            BarcodeProcessor.State.Sense -> _uiState.update { it.copy(mode = MoveSupplyUiState.Mode.Scan.Sense) }
            is BarcodeProcessor.State.Recognize -> _uiState.update { it.copy(mode = MoveSupplyUiState.Mode.Scan.Recognize) }
            is BarcodeProcessor.State.Communicate -> {
                val barcode = requireNotNull(processorState.barcode.rawValue)
                val container = uiState.value.containers.find { it.barcode == barcode }
                if (container != null) {
                    _uiState.update {
                        it.copy(mode = MoveSupplyUiState.Mode.Scan.FoundScanResult(container))
                    }
                } else {
                    _uiState.update {
                        it.copy(mode = MoveSupplyUiState.Mode.Scan.NotFoundScanResult)
                    }
                }
            }
        }
    }

    fun startScanning() {
        _uiState.update { it.copy(mode = MoveSupplyUiState.Mode.Scan.Sense) }
    }

    fun chooseContainer(containerBarcode: String) {
        viewModelScope.launch {
            moveSupply(uiState.value.supplyBarcode, containerBarcode)
            _uiState.update { it.copy(isDone = true) }
        }
    }

    fun selectChooseMode() {
        _uiState.update { it.copy(mode = MoveSupplyUiState.Mode.Choose) }
    }

    fun selectScanMode() {
        _uiState.update { it.copy(mode = MoveSupplyUiState.Mode.Scan.Sense) }
    }
}