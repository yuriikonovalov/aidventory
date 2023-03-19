package com.aidventory.core.data.datasources

import com.aidventory.core.domain.model.Theme
import com.aidventory.core.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow

internal interface UserPreferencesDataSource {
    val userPreferences: Flow<UserPreferences>
    suspend fun setTheme(theme: Theme)
    suspend fun setShouldHideOnboarding(shouldHideOnboarding: Boolean)
}