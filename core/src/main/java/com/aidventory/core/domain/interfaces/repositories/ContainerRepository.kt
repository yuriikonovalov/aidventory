package com.aidventory.core.domain.interfaces.repositories

import com.aidventory.core.domain.entities.Container
import com.aidventory.core.domain.entities.ContainerWithContent
import kotlinx.coroutines.flow.Flow

interface ContainerRepository {
    suspend fun insertContainer(container: Container)
    suspend fun deleteContainer(barcode: String)
    fun getContainersWithContent(): Flow<List<ContainerWithContent>>
    fun getContainers(): Flow<List<Container>>
    suspend fun getContainerByBarcode(barcode: String): ContainerWithContent?
    suspend fun deleteAll()
}