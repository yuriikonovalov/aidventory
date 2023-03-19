package com.aidventory.feature.containers.presentation.addcontainer

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aidventory.core.common.result.Result
import com.aidventory.core.domain.usecases.AddContainerUseCase
import com.aidventory.core.domain.usecases.SendQrUseCase
import com.aidventory.core.domain.usecases.SaveQrUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddContainerViewModel @Inject constructor(
    private val addContainer: AddContainerUseCase,
    private val createQRCodePdfFile: SaveQrUseCase,
    private val createQRCodePdfFileInCache: SendQrUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<AddContainerUiState>(AddContainerUiState.AddContainer())
    val uiState = _uiState.asStateFlow()

    private val _sideEffect = MutableStateFlow<AddContainerSideEffect?>(null)
    val sideEffect = _sideEffect.asStateFlow()

    fun consumeSideEffect() {
        _sideEffect.value = null
    }

    fun inputName(text: String) {
        _uiState.update {
            (it as AddContainerUiState.AddContainer).copy(
                name = text,
                emptyNameError = false  // hide the error when user inputs the name.
            )
        }
    }

    fun addContainer() {
        val state = (uiState.value as AddContainerUiState.AddContainer)
        if (state.name.isBlank()) {
            _uiState.update {
                (it as AddContainerUiState.AddContainer).copy(emptyNameError = true)
            }
            return
        }

        viewModelScope.launch {
            addContainer(state.name).also { result ->
                when (result) {
                    is Result.Success -> {
                        _uiState.value = AddContainerUiState.ShareBarcode(result.data)
                    }

                    is Result.Error -> throw Exception("Error happened during adding a container")
                    Result.Loading -> {}
                }
            }
        }
    }

    fun sendQRCodePdf() {
        val barcode = (uiState.value as AddContainerUiState.ShareBarcode).barcode
        viewModelScope.launch {
            when (val result = createQRCodePdfFileInCache(barcode)) {
                is Result.Success -> {
                    _sideEffect.value = AddContainerSideEffect.SendBarcodeIntent(result.data)
                }

                is Result.Error -> {
                    // Show toast
                }

                Result.Loading -> {
                    // no-op
                }
            }
        }
    }

    fun saveBarcode(uri: Uri) {
        val barcode = (uiState.value as AddContainerUiState.ShareBarcode).barcode
        viewModelScope.launch {
            createQRCodePdfFile(barcode, uri)
        }
    }
}

sealed interface AddContainerSideEffect {
    data class SendBarcodeIntent(val uri: Uri) : AddContainerSideEffect
}
