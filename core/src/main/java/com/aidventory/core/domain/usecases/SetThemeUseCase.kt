package com.aidventory.core.domain.usecases

import com.aidventory.core.domain.model.Theme
import com.aidventory.core.domain.interfaces.repositories.UserPreferencesRepository
import javax.inject.Inject

class SetThemeUseCase @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(theme: Theme) {
        userPreferencesRepository.setTheme(theme)
    }
}