package com.aidventory.core.domain.interfaces.repositories

import com.aidventory.core.domain.model.Theme
import com.aidventory.core.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val userPreferences: Flow<UserPreferences>
    suspend fun setTheme(theme: Theme)
    suspend fun setShouldHideOnboarding(shouldHideOnboarding: Boolean)
}