package com.aidventory.feature.supplies.presentation.movesupply

import com.aidventory.core.domain.entities.Container

data class MoveSupplyUiState(
    val supplyBarcode: String = "",
    val containers: List<Container> = emptyList(),
    val mode: Mode = Mode.Choose,
    val isDone: Boolean = false
) {
    sealed interface Mode {
        object Choose : Mode
        sealed interface Scan : Mode {
            object Sense : Scan
            object Recognize : Scan
            object NotFoundScanResult : Scan
            data class FoundScanResult(val container: Container) : Scan
        }
    }
}