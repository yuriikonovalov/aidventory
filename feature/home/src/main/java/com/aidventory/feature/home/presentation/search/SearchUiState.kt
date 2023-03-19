package com.aidventory.feature.home.presentation.search

import com.aidventory.core.domain.entities.Supply

internal data class SearchUiState(
    val query: String = "",
    val supplies: List<Supply> = emptyList()
)
