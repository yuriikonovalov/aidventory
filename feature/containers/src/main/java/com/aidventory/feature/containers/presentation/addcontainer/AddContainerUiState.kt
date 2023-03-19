package com.aidventory.feature.containers.presentation.addcontainer

sealed interface AddContainerUiState {
    data class AddContainer(
        val name: String = "",
        val emptyNameError: Boolean = false
    ) : AddContainerUiState

    data class ShareBarcode(val barcode: String = "") : AddContainerUiState
}