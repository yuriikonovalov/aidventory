package com.aidventory.core.data.repositories

import com.aidventory.core.data.datasources.SupplyUseLocalDataSource
import com.aidventory.core.domain.entities.SupplyUse
import com.aidventory.core.domain.interfaces.repositories.SupplyUseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class SupplyUseRepositoryImpl @Inject constructor(
    private val supplyUseLocalDataSource: SupplyUseLocalDataSource
) : SupplyUseRepository {
    override fun getSupplyUses(): Flow<List<SupplyUse>> {
        return supplyUseLocalDataSource.getSupplyUses()
    }

    override suspend fun insertSupplyUse(name: String) {
        supplyUseLocalDataSource.insertSupplyUse(name)
    }

    override suspend fun deleteSupplyUse(id: Int) {
        supplyUseLocalDataSource.deleteSupplyUse(id)
    }

    override suspend fun deleteNotDefault() {
        supplyUseLocalDataSource.deleteNotDefault()
    }
}