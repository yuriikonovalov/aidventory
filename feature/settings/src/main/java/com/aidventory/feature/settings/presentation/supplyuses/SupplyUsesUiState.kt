package com.aidventory.feature.settings.presentation.supplyuses

import com.aidventory.core.domain.entities.SupplyUse

data class SupplyUsesUiState(
    val isAddSupplyUseDialogOpen: Boolean = false,
    val supplyUses: List<SupplyUse> = emptyList(),
    val isLoading: Boolean = false,
    val name: String = "",
    val isNameError: Boolean = false
)
