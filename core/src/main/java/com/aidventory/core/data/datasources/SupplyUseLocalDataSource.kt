package com.aidventory.core.data.datasources

import com.aidventory.core.domain.entities.SupplyUse
import kotlinx.coroutines.flow.Flow

internal interface SupplyUseLocalDataSource {
    fun getSupplyUses(): Flow<List<SupplyUse>>
    suspend fun insertSupplyUse(name: String)
    suspend fun deleteSupplyUse(id: Int)
    suspend fun deleteNotDefault()
}