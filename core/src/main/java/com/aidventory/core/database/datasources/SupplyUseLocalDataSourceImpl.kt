package com.aidventory.core.database.datasources

import com.aidventory.core.data.datasources.SupplyUseLocalDataSource
import com.aidventory.core.database.dao.SupplyUseDao
import com.aidventory.core.database.model.SupplyUseEntity
import com.aidventory.core.database.model.toDomain
import com.aidventory.core.domain.entities.SupplyUse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class SupplyUseLocalDataSourceImpl @Inject constructor(
    private val supplyUseDao: SupplyUseDao
) : SupplyUseLocalDataSource {
    override fun getSupplyUses(): Flow<List<SupplyUse>> {
        return supplyUseDao.getSupplyUseEntities().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun insertSupplyUse(name: String) {
        val entity = SupplyUseEntity(
            name = name,
            isDefault = false
        )
        supplyUseDao.insert(entity)
    }

    override suspend fun deleteSupplyUse(id: Int) {
        supplyUseDao.delete(id)
    }

    override suspend fun deleteNotDefault() {
        supplyUseDao.deleteNotDefault()
    }
}