package com.aidventory.core.data.datasources

import com.aidventory.core.domain.entities.Supply
import com.aidventory.core.domain.entities.SupplyUse
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

internal interface SupplyLocalDataSource {
    suspend fun insert(
        barcode: String,
        isBarcodeGenerated: Boolean,
        name: String,
        containerBarcode: String?,
        expiry: LocalDate?,
        uses: List<SupplyUse>
    )

    suspend fun delete(barcode: String)
    fun getSupplies(
        // sort config
        useSortNameASC: Boolean = false,
        useSortNameDESC: Boolean = false,
        useSortExpiryASC: Boolean = false,
        useSortExpiryDESC: Boolean = false,
        // filter config
        useFilterContainerBarcodes: Boolean = false,
        filterContainerBarcodes: Set<String> = emptySet(),
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