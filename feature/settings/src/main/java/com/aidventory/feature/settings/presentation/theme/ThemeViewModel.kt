package com.aidventory.feature.settings.presentation.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aidventory.core.common.result.Result
import com.aidventory.core.domain.model.Theme
import com.aidventory.core.domain.usecases.GetCurrentThemeUseCase
import com.aidventory.core.domain.usecases.SetThemeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    getCurrentTheme: GetCurrentThemeUseCase,
    private val setTheme: SetThemeUseCase
) : ViewModel() {

    val theme = getCurrentTheme()
        .filter { it is Result.Success }
        .map { (it as Result.Success).data }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Theme.LIGHT
        )

    fun changeTheme(theme: Theme) {
        viewModelScope.launch {
            setTheme(theme)
        }
    }
}