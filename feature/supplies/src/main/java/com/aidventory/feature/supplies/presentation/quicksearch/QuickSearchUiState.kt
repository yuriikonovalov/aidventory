package com.aidventory.feature.supplies.presentation.quicksearch

import com.aidventory.core.domain.entities.Supply

data class QuickSearchUiState(
    val supplyBarcode: String,
    val supply: Supply? = null,
    val scanState: ScanState
) {
    sealed interface ScanState {
        object Sense : ScanState
        object Recognize : ScanState
        object FoundScanResult : ScanState
        object NotFoundScanResult : ScanState
    }
}

