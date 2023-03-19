package com.aidventory.core.data.repositories

import com.aidventory.core.data.datasources.UserPreferencesDataSource
import com.aidventory.core.domain.model.Theme
import com.aidventory.core.domain.interfaces.repositories.UserPreferencesRepository
import javax.inject.Inject

internal class UserPreferencesRepositoryImpl @Inject constructor(
    private val userPreferencesDataSource: UserPreferencesDataSource
) : UserPreferencesRepository {
    override val userPreferences = userPreferencesDataSource.userPreferences

    override suspend fun setTheme(theme: Theme) = userPreferencesDataSource.setTheme(theme)

    override suspend fun setShouldHideOnboarding(shouldHideOnboarding: Boolean) =
        userPreferencesDataSource.setShouldHideOnboarding(shouldHideOnboarding)
}