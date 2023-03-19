package com.aidventory.feature.supplies.presentation.supplies

import com.aidventory.core.domain.entities.Container
import com.aidventory.core.domain.entities.Supply
import com.aidventory.core.domain.entities.SupplyUse
import com.aidventory.core.domain.model.SupplyFilterParams
import com.aidventory.core.domain.model.SupplySortingParams

data class SuppliesUiState(
    val isDetailOpen: Boolean = false,
    val isSuppliesLoading: Boolean = false,
    val supplies: List<Supply> = emptyList(),
    val containers: List<Container> = emptyList(),
    val supplyUses: List<SupplyUse> = emptyList(),
    val supplyFilterParams: SupplyFilterParams = SupplyFilterParams(),
    val supplySortingParams: SupplySortingParams = SupplySortingParams.Default,
    val selectedSupplyBarcode: String? = null,
    val modalContentType: ModalContentType = ModalContentType.SORT,
    val isModalContentVisible: Boolean = false
) {

    val selectedSupply: Supply? = selectedSupplyBarcode?.let { barcode ->
        supplies.find { supply -> supply.barcode == barcode }
    }

    fun updateContainerFilterParams(containerBarcode: String): SuppliesUiState {
        return copy(supplyFilterParams = supplyFilterParams.takeContainerBarcode(containerBarcode))
    }

    fun updateSupplyUsesFilterParams(supplyUseId: Int): SuppliesUiState {
        return copy(supplyFilterParams = supplyFilterParams.takeSupplyUseId(supplyUseId))
    }

    fun updateSupplySortingParams(supplySortingParams: SupplySortingParams): SuppliesUiState {
        return copy(supplySortingParams = supplySortingParams)
    }

    fun updateModalContentType(modalContentType: ModalContentType): SuppliesUiState {
        return copy(modalContentType = modalContentType, isModalContentVisible = true)
    }

    fun clearFilters(): SuppliesUiState {
        return copy(supplyFilterParams = SupplyFilterParams())
    }

    fun updateModalContentVisibility(isVisible: Boolean): SuppliesUiState {
        return copy(isModalContentVisible = isVisible)
    }
}