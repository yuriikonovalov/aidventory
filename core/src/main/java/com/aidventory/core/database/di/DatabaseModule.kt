package com.aidventory.core.database.di

import android.content.Context
import androidx.room.Room
import com.aidventory.core.database.AidventoryDatabase
import com.aidventory.core.database.util.PrepopulateDefaultSupplyUsesCallback
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class DatabaseVersion

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {
    @Provides
    @Singleton
    fun providesAidventoryDatabase(@ApplicationContext context: Context): AidventoryDatabase =
        Room.databaseBuilder(
            context,
            AidventoryDatabase::class.java,
            "aidventory_database"
        )
            .fallbackToDestructiveMigration() //TODO: Delete this line before production or https://stackoverflow.com/questions/44197309/room-cannot-verify-the-data-integrity
            .addCallback(PrepopulateDefaultSupplyUsesCallback)
            .build()

    @Provides
    @DatabaseVersion
    fun providesDbVersion(): Int = AidventoryDatabase.VERSION
}