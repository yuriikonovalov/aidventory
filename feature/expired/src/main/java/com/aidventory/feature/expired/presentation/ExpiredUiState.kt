package com.aidventory.feature.expired.presentation

import com.aidventory.core.domain.entities.Supply

data class ExpiredUiState(
    val isLoading: Boolean = true,
    // key - the barcode of a container, value - a list of supplies in that container.
    val suppliesByContainer: Map<String?, List<Supply>> = mapOf()
)