package com.aidventory.core.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.aidventory.core.common.di.AppDispatcher
import com.aidventory.core.common.di.Dispatcher
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
internal object DataStoreModule {

    @Provides
    @Singleton
    fun providesPreferenceDataStore(
        @ApplicationContext context: Context,
        @Dispatcher(AppDispatcher.IO) ioDispatcher: CoroutineDispatcher
    ): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() },
            scope = CoroutineScope(ioDispatcher + SupervisorJob())
        ) {
            context.preferencesDataStoreFile("user_preferences")
        }
    }
}