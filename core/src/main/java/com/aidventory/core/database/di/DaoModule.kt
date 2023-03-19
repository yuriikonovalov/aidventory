package com.aidventory.core.database.di

import com.aidventory.core.database.AidventoryDatabase
import com.aidventory.core.database.dao.ContainerDao
import com.aidventory.core.database.dao.SupplyDao
import com.aidventory.core.database.dao.SupplyUseDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal object DaoModule {
    @Provides
    fun provideSupplyUseDao(database: AidventoryDatabase): SupplyUseDao = database.supplyUseDao()

    @Provides
    fun provideContainerDao(database: AidventoryDatabase): ContainerDao = database.containerDao()

    @Provides
    fun provideSupplyDao(database: AidventoryDatabase): SupplyDao = database.supplyDao()
}