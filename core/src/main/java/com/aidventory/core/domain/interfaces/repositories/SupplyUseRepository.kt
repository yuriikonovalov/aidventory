package com.aidventory.core.domain.interfaces.repositories

import com.aidventory.core.domain.entities.SupplyUse
import kotlinx.coroutines.flow.Flow

interface SupplyUseRepository {
    fun getSupplyUses(): Flow<List<SupplyUse>>
    suspend fun insertSupplyUse(name: String)
    suspend fun deleteSupplyUse(id: Int)
    suspend fun deleteNotDefault()
}