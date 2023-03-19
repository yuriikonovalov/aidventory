package com.aidventory.core.database.datasources

import com.aidventory.core.data.datasources.ContainerLocalDataSource
import com.aidventory.core.database.dao.ContainerDao
import com.aidventory.core.database.model.ContainerEntity
import com.aidventory.core.database.model.toDomain
import com.aidventory.core.domain.entities.Container
import com.aidventory.core.domain.entities.ContainerWithContent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class ContainerLocalDataSourceImpl @Inject constructor(
    private val containerDao: ContainerDao
) : ContainerLocalDataSource {
    override suspend fun insertContainer(container: Container) {
        containerDao.upsert(ContainerEntity.fromDomain(container))
    }

    override suspend fun deleteContainer(barcode: String) {
        containerDao.delete(barcode)
    }

    override fun getContainersWithContent(): Flow<List<ContainerWithContent>> {
        return containerDao.getPopulatedContainers().map { list ->
            list.map {
                it.toDomain()
            }
        }
    }

    override fun getContainers(): Flow<List<Container>> {
        return containerDao.getContainerEntities().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun getContainerByBarcode(barcode: String): ContainerWithContent? {
        return containerDao.getPopulatedContainerByBarcode(barcode)?.toDomain()
    }

    override suspend fun deleteAll() {
        containerDao.deleteAll()
    }
}