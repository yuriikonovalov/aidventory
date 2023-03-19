package com.aidventory.feature.supplies.presentation.addsupply

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aidventory.core.barcode.processing.BarcodeProcessor
import com.aidventory.core.common.result.Result
import com.aidventory.core.domain.entities.Supply
import com.aidventory.core.domain.entities.SupplyUse
import com.aidventory.core.domain.usecases.AddSupplyUseCase
import com.aidventory.core.domain.usecases.GetContainersUseCase
import com.aidventory.core.domain.usecases.GetSupplyUseCase
import com.aidventory.core.domain.usecases.GetSupplyUsesUseCase
import com.aidventory.feature.supplies.navigation.AddSupplyScreenNavigation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class AddSupplyViewModel @Inject constructor(
    private val addSupply: AddSupplyUseCase,
    private val getContainers: GetContainersUseCase,
    private val getSupplyUses: GetSupplyUsesUseCase,
    private val getSupply: GetSupplyUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddSupplyUiState())
    val uiState = _uiState.asStateFlow()

    init {
        // Receive a barcode from ScannerScreen when user clicks 'add as a supply'.
        val barcode: String? = savedStateHandle[AddSupplyScreenNavigation.ARGUMENT]
        barcode?.let {
            _uiState.update { state ->
                state.copy(
                    barcode = barcode,
                    step = uiState.value.step.nextStep
                )
            }
        }

        collectContainers()
        collectSupplyUses()
    }

    fun changeExpiry(date: LocalDate?) = _uiState.update { it.copy(expiry = date) }
    fun changeName(name: String) = _uiState.update { it.updateName(name) }
    fun clickSupplyUse(supplyUse: SupplyUse) {
        _uiState.update { it.updateSelectedSupplyUses(supplyUse) }
    }

    fun addSupply() {
        viewModelScope.launch {
            with(uiState.value) {
                addSupply(
                    barcode = barcode,
                    name = name,
                    containerBarcode = selectedContainer?.barcode,
                    expiry = expiry,
                    uses = selectedSupplyUses
                )
            }
            _uiState.update { it.copy(isDone = true) }
        }
    }

    fun toNextStep() = _uiState.update { it.updateToNextStep() }
    fun toPreviousStep() = _uiState.update { it.updateToPreviousStep() }

    fun changeBarcodeProcessorState(processorState: BarcodeProcessor.State) {
        when (uiState.value.step) {
            is AddSupplyUiState.Step.Supply -> changeBarcodeProcessorStateForSupplyScanner(
                processorState
            )

            is AddSupplyUiState.Step.Container -> changeBarcodeProcessorStateForContainerScanner(
                processorState
            )

            else -> {
                // no-op as barcode scanning is only used for the supply and container steps.
            }
        }
    }

    private fun changeBarcodeProcessorStateForSupplyScanner(processorState: BarcodeProcessor.State) {
        when (processorState) {
            BarcodeProcessor.State.Sense -> _uiState.update {
                it.copy(
                    step = AddSupplyUiState.Step.Supply(AddSupplyUiState.SupplyScannerState.Sense)
                )
            }

            is BarcodeProcessor.State.Recognize -> _uiState.update {
                it.copy(
                    step = AddSupplyUiState.Step.Supply(AddSupplyUiState.SupplyScannerState.Recognize)
                )
            }

            is BarcodeProcessor.State.Communicate -> {
                val barcode = requireNotNull(processorState.barcode.rawValue)
                viewModelScope.launch {
                    val supplyResult = getSupply(barcode)
                    if (supplyResult.isSupplyAlreadyExists()) {
                        _uiState.update {
                            it.copy(
                                step = AddSupplyUiState.Step.Supply(
                                    AddSupplyUiState.SupplyScannerState.AlreadyExistScanResult(
                                        (supplyResult as Result.Success).data
                                    )
                                )
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                barcode = barcode,
                                step = uiState.value.step.nextStep
                            )
                        }
                    }
                }
            }
        }
    }

    private fun changeBarcodeProcessorStateForContainerScanner(processorState: BarcodeProcessor.State) {
        // TODO: Write a test to check this. Now it seems to work.
        // Try to stop accidental emitting Scan.Sense after a container is found. (1 in 5-10 scans)
        if (uiState.value.step is AddSupplyUiState.Step.Container
            && (uiState.value.step as AddSupplyUiState.Step.Container).containerStepState is AddSupplyUiState.ContainerStepState.Scan
        ) {
            when (processorState) {
                BarcodeProcessor.State.Sense -> _uiState.update {
                    it.copy(
                        step = AddSupplyUiState.Step.Container(AddSupplyUiState.ContainerStepState.Scan.Sense)
                    )
                }

                is BarcodeProcessor.State.Recognize -> _uiState.update {
                    it.copy(
                        step = AddSupplyUiState.Step.Container(AddSupplyUiState.ContainerStepState.Scan.Recognize)
                    )
                }

                is BarcodeProcessor.State.Communicate -> {
                    val barcode = requireNotNull(processorState.barcode.rawValue)
                    val container = uiState.value.containers.find { it.barcode == barcode }
                    if (container == null) {
                        _uiState.update {
                            it.copy(
                                step = AddSupplyUiState.Step.Container(
                                    AddSupplyUiState.ContainerStepState.Scan.NotFoundScanResult
                                )
                            )
                        }
                    } else {
                        _uiState.update { it.updateSelectedContainer(barcode) }
                    }
                }
            }
        }
    }

    fun clearSelectedContainer() = _uiState.update { it.clearSelectedContainer() }
    fun selectContainer(barcode: String) = _uiState.update { it.updateSelectedContainer(barcode) }

    fun clickScanContainer() {
        _uiState.update {
            it.copy(
                step = AddSupplyUiState.Step.Container(AddSupplyUiState.ContainerStepState.Scan.Sense)
            )
        }
    }

    fun clickChooseContainer() {
        _uiState.update {
            it.copy(step = AddSupplyUiState.Step.Container(AddSupplyUiState.ContainerStepState.Choose))
        }
    }

    private fun collectContainers() {
        viewModelScope.launch {
            getContainers().collectLatest { result ->
                when (result) {
                    is Result.Loading -> {
                        _uiState.update { it.copy(containers = emptyList()) }
                    }

                    is Result.Error -> {
                        _uiState.update { it.copy(containers = emptyList()) }
                    }

                    is Result.Success -> {
                        val containers = result.data
                        _uiState.update { it.copy(containers = containers) }
                    }
                }
            }
        }
    }

    private fun collectSupplyUses() {
        viewModelScope.launch {
            getSupplyUses().collectLatest { result ->
                when (result) {
                    is Result.Loading -> {
                        _uiState.update { it.copy(supplyUses = emptyList()) }
                    }

                    is Result.Error -> {
                        _uiState.update { it.copy(supplyUses = emptyList()) }
                    }

                    is Result.Success -> {
                        val supplyUses = result.data
                        _uiState.update { it.copy(supplyUses = supplyUses) }
                    }
                }
            }
        }
    }

    fun closeContainerScanner() {
        _uiState.update {
            it.copy(step = AddSupplyUiState.Step.Container(AddSupplyUiState.ContainerStepState.Submit))
        }
    }

    fun scanSupply() {
        _uiState.update {
            it.copy(step = AddSupplyUiState.Step.Supply(AddSupplyUiState.SupplyScannerState.Sense))
        }
    }


    fun hideChooseContainerView() {
        _uiState.update {
            it.copy(step = AddSupplyUiState.Step.Container(AddSupplyUiState.ContainerStepState.Submit))
        }
    }

    private fun Result<Supply>.isSupplyAlreadyExists() = this is Result.Success
}