package com.aidventory.core.data.di

import com.aidventory.core.data.repositories.ContainerRepositoryImpl
import com.aidventory.core.data.repositories.SupplyRepositoryImpl
import com.aidventory.core.data.repositories.SupplyUseRepositoryImpl
import com.aidventory.core.data.repositories.UserPreferencesRepositoryImpl
import com.aidventory.core.domain.interfaces.repositories.ContainerRepository
import com.aidventory.core.domain.interfaces.repositories.SupplyRepository
import com.aidventory.core.domain.interfaces.repositories.SupplyUseRepository
import com.aidventory.core.domain.interfaces.repositories.UserPreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface RepositoryModule {

    @Binds
    fun bindsUserPreferencesRepository(
        impl: UserPreferencesRepositoryImpl
    ): UserPreferencesRepository

    @Binds
    fun bindsContainerRepository(
        impl: ContainerRepositoryImpl
    ): ContainerRepository

    @Binds
    fun bindsSupplyUseRepository(
        impl: SupplyUseRepositoryImpl
    ): SupplyUseRepository


    @Binds
    fun bindsSupplyRepository(
        impl: SupplyRepositoryImpl
    ): SupplyRepository

}