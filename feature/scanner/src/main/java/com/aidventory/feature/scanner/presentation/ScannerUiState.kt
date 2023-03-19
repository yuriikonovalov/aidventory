package com.aidventory.feature.scanner.presentation

import com.aidventory.core.domain.entities.Container
import com.aidventory.core.domain.entities.Supply

sealed interface ScannerUiState {

    val barcode: String

    object Sense : ScannerUiState {
        override val barcode: String
            get() = throw UnsupportedOperationException("Barcode is unknown during the Sense state")
    }

    object Recognize : ScannerUiState {
        override val barcode: String
            get() = throw UnsupportedOperationException("Barcode is unknown during the Recognize state")
    }

    data class NotFoundResult(override val barcode: String) : ScannerUiState
    data class SupplyResult(override val barcode: String) : ScannerUiState
    data class ContainerResult(override val barcode: String) : ScannerUiState
    data class MultipleResult(val supply: Supply, val container: Container) : ScannerUiState {
        override val barcode: String
            get() = throw UnsupportedOperationException(
                "Reduce MultipleResult state to either SupplyResult or ContainerResult " +
                        "to get a barcode from MultipleResult state."
            )
    }
}