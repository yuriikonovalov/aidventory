package com.aidventory.feature.home.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aidventory.core.common.result.Result
import com.aidventory.core.domain.usecases.SearchByNameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class SearchViewModel @Inject constructor(
    private val searchByName: SearchByNameUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            uiState
                .map { it.query }
                .distinctUntilChanged()
                .collectLatest { query ->
                    searchByName(query).collectLatest { result ->
                        when (result) {
                            is Result.Success -> {
                                _uiState.update { it.copy(supplies = result.data) }
                            }

                            is Result.Error -> {
                                _uiState.update { it.copy(supplies = emptyList()) }
                            }

                            Result.Loading -> {}
                        }
                    }
                }
        }
    }

    fun inputQuery(query: String) {
        _uiState.update { it.copy(query = query) }
    }
}