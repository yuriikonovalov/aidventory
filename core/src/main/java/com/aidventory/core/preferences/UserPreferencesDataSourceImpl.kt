package com.aidventory.core.preferences

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.aidventory.core.data.datasources.UserPreferencesDataSource
import com.aidventory.core.domain.model.Theme
import com.aidventory.core.domain.model.UserPreferences
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

internal class UserPreferencesDataSourceImpl @Inject constructor(
    private val preferencesDataStore: DataStore<Preferences>
) : UserPreferencesDataSource {
    override val userPreferences = preferencesDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            UserPreferences(
                theme = preferences.theme,
                shouldHideOnboarding = preferences.shouldHideOnboarding
            )
        }

    override suspend fun setTheme(theme: Theme) {
        try {
            preferencesDataStore.edit { preferences ->
                preferences[THEME] = theme.name
            }
        } catch (exception: IOException) {
            Log.e("UserPreferences", "Failed to update 'theme'", exception)
        }
    }

    override suspend fun setShouldHideOnboarding(shouldHideOnboarding: Boolean) {
        try {
            preferencesDataStore.edit { preferences ->
                preferences[SHOULD_HIDE_ONBOARDING] = shouldHideOnboarding
            }
        } catch (exception: IOException) {
            Log.e("UserPreferences", "Failed to update 'should hide onboarding'", exception)
        }
    }

    private val Preferences.theme
        get() = this[THEME]?.let { Theme.valueOf(it) } ?: Theme.LIGHT
    private val Preferences.shouldHideOnboarding
        get() = this[SHOULD_HIDE_ONBOARDING] ?: false

    companion object PreferencesKeys {
        private val SHOULD_HIDE_ONBOARDING = booleanPreferencesKey("should_hide_onboarding")
        private val THEME = stringPreferencesKey("theme")
    }
}
