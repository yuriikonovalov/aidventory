package com.aidventory.feature.containers.presentation.containers

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aidventory.core.common.result.Result
import com.aidventory.core.domain.usecases.SendQrUseCase
import com.aidventory.core.domain.usecases.SaveQrUseCase
import com.aidventory.core.domain.usecases.DeleteContainerUseCase
import com.aidventory.core.domain.usecases.GetContainersWithContentUseCase
import com.aidventory.feature.containers.navigation.ContainersScreenNavigation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContainersViewModel @Inject constructor(
    getContainersWithContent: GetContainersWithContentUseCase,
    private val deleteContainer: DeleteContainerUseCase,
    private val sendQr: SendQrUseCase,
    private val saveQr: SaveQrUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _uiState = MutableStateFlow(ContainersUiState())
    internal var uiState = _uiState.asStateFlow()

    private val _sideEffect = MutableStateFlow<ContainersSideEffect?>(null)
    val sideEffect = _sideEffect.asStateFlow()


    init {
        val barcode: String? = savedStateHandle[ContainersScreenNavigation.ROUTE_ARGUMENT]

        viewModelScope.launch {
            getContainersWithContent()
                .collectLatest { result ->
                    when (result) {
                        Result.Loading -> {
                            _uiState.update { it.copy(isLoading = true) }
                        }

                        is Result.Error -> {
                            _uiState.update {
                                it.copy(isLoading = false, containersWithContent = emptyList())
                            }
                        }

                        is Result.Success -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    containersWithContent = result.data,
                                    selectedContainerBarcode = barcode,
                                    isDetailOpen = barcode != null
                                )
                            }
                        }
                    }
                }
        }
    }

    fun updateIsDetailOpen(isDetailOpen: Boolean) {
        _uiState.update { it.copy(isDetailOpen = isDetailOpen) }
    }

    fun updateSelectedContainerBarcode(barcode: String?) {
        _uiState.update { it.copy(selectedContainerBarcode = barcode) }
    }

    fun updateIsDeleteDialogOpen(isDeleteDialogOpen: Boolean) {
        _uiState.update { it.copy(isDeleteDialogOpen = isDeleteDialogOpen) }
    }

    fun deleteContainer() {
        val barcode = requireNotNull(uiState.value.selectedContainerBarcode)
        viewModelScope.launch {
            deleteContainer(barcode)
            // Reset selected container index after deleting
            _uiState.update { it.copy(selectedContainerBarcode = null, isDeleteDialogOpen = false) }
        }
    }

    fun saveBarcode(uri: Uri) {
        viewModelScope.launch {
            saveQr(uiState.value.selectedContainerBarcode!!, uri)
        }
    }

    fun consumeSideEffect() {
        _sideEffect.value = null
    }

    fun closeShareQrErrorDialog() {
        _uiState.update { it.copy(isShareQrErrorDialogOpen = false) }
    }

    fun sendBarcode() {
        viewModelScope.launch {
            when (val result = sendQr(uiState.value.selectedContainerBarcode!!)) {
                is Result.Success -> {
                    _sideEffect.value = ContainersSideEffect.SendBarcodeIntent(result.data)
                }

                is Result.Error -> {
                    _uiState.update { it.copy(isShareQrErrorDialogOpen = true) }
                }

                Result.Loading -> {}
            }
        }
    }

}

sealed interface ContainersSideEffect {
    data class SendBarcodeIntent(val uri: Uri) : ContainersSideEffect
}
