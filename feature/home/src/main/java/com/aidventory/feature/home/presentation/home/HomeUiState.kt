package com.aidventory.feature.home.presentation.home

import com.aidventory.core.domain.entities.Container
import com.aidventory.core.domain.entities.Supply

internal data class HomeUiState(
    val supplies: List<Supply> = emptyList(),
    val isSuppliesLoading: Boolean = true,
    val containers: List<Container> = emptyList(),
    val isContainersLoading: Boolean = true
)
