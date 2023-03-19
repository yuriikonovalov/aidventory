package com.aidventory.core.data.repositories

import com.aidventory.core.data.datasources.ContainerLocalDataSource
import com.aidventory.core.domain.entities.Container
import com.aidventory.core.domain.entities.ContainerWithContent
import com.aidventory.core.domain.interfaces.repositories.ContainerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class ContainerRepositoryImpl @Inject constructor(
    private val containerLocalDataSource: ContainerLocalDataSource
) : ContainerRepository {
    override suspend fun insertContainer(container: Container) {
        containerLocalDataSource.insertContainer(container)
    }

    override suspend fun deleteContainer(barcode: String) {
        containerLocalDataSource.deleteContainer(barcode)
    }

    override fun getContainersWithContent(): Flow<List<ContainerWithContent>> {
        return containerLocalDataSource.getContainersWithContent()
    }

    override fun getContainers(): Flow<List<Container>> {
        return containerLocalDataSource.getContainers()
    }

    override suspend fun getContainerByBarcode(barcode: String): ContainerWithContent? {
        return containerLocalDataSource.getContainerByBarcode(barcode)
    }

    override suspend fun deleteAll() {
        containerLocalDataSource.deleteAll()
    }
}