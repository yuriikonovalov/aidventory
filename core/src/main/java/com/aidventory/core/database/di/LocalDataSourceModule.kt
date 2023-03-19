package com.aidventory.core.database.di

import com.aidventory.core.data.datasources.ContainerLocalDataSource
import com.aidventory.core.data.datasources.SupplyLocalDataSource
import com.aidventory.core.data.datasources.SupplyUseLocalDataSource
import com.aidventory.core.data.datasources.UserPreferencesDataSource
import com.aidventory.core.database.datasources.ContainerLocalDataSourceImpl
import com.aidventory.core.database.datasources.SupplyLocalDataSourceImpl
import com.aidventory.core.database.datasources.SupplyUseLocalDataSourceImpl
import com.aidventory.core.preferences.UserPreferencesDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface LocalDataSourceModule {
    @Binds
    fun bindsUserPreferencesDataSource(
        impl: UserPreferencesDataSourceImpl
    ): UserPreferencesDataSource

    @Binds
    fun bindsContainerLocalDataSource(
        impl: ContainerLocalDataSourceImpl
    ): ContainerLocalDataSource

    @Binds
    fun bindsSupplyUseLocalDataSource(
        impl: SupplyUseLocalDataSourceImpl
    ): SupplyUseLocalDataSource

    @Binds
    fun bindsSupplyLocalDataSource(
        impl: SupplyLocalDataSourceImpl
    ): SupplyLocalDataSource

}