package com.aidventory.core.domain.interfaces.repositories

import com.aidventory.core.domain.entities.Supply
import com.aidventory.core.domain.entities.SupplyUse
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface SupplyRepository {
    suspend fun insert(
        barcode: String,
        isBarcodeGenerated: Boolean,
        name: String,
        containerBarcode: String? = null,
        expiry: LocalDate? = null,
        uses: List<SupplyUse> = emptyList()
    )

    suspend fun delete(code: String)
    fun getSupplies(
        // sort config
        useSortNameASC: Boolean = false,
        useSortNameDESC: Boolean = false,
        useSortExpiryASC: Boolean = false,
        useSortExpiryDESC: Boolean = false,
        // filter config
        useFilterContainerBarcode: Boolean = false,
        filterContainerBarcode: Set<String> = emptySet(),
        useFilterSupplyUseIds: Boolean = false,
        filterSupplyUseIds: Set<Int> = emptySet()
    ): Flow<List<Supply>>

    suspend fun getSupplyByBarcode(barcode: String): Supply?
    suspend fun isSupplyInContainer(supplyBarcode: String, containerBarcode: String): Boolean
    suspend fun updateContainerOfSupply(supplyBarcode: String, newContainerBarcode: String)
    suspend fun deleteAll()

    fun getSuppliesByName(query: String): Flow<List<Supply>>
    fun getExpiredSupplies(): Flow<List<Supply>>
    suspend fun getExpiredTodaySupplies(): List<Supply>
}