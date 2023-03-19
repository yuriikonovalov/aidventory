package com.aidventory.core.data.repositories

import com.aidventory.core.data.datasources.SupplyLocalDataSource
import com.aidventory.core.domain.entities.Supply
import com.aidventory.core.domain.entities.SupplyUse
import com.aidventory.core.domain.interfaces.repositories.SupplyRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

internal class SupplyRepositoryImpl @Inject constructor(
    private val supplyLocalDataSource: SupplyLocalDataSource
) : SupplyRepository {
    override suspend fun insert(
        barcode: String,
        isBarcodeGenerated: Boolean,
        name: String,
        containerBarcode: String?,
        expiry: LocalDate?,
        uses: List<SupplyUse>
    ) {
        supplyLocalDataSource.insert(
            barcode = barcode,
            isBarcodeGenerated = isBarcodeGenerated,
            name = name,
            containerBarcode = containerBarcode,
            expiry = expiry,
            uses = uses
        )
    }

    override suspend fun delete(code: String) {
        supplyLocalDataSource.delete(code)
    }

    override fun getSupplies(
        useSortNameASC: Boolean,
        useSortNameDESC: Boolean,
        useSortExpiryASC: Boolean,
        useSortExpiryDESC: Boolean,
        useFilterContainerBarcode: Boolean,
        filterContainerBarcode: Set<String>,
        useFilterSupplyUseIds: Boolean,
        filterSupplyUseIds: Set<Int>
    ): Flow<List<Supply>> {
        return supplyLocalDataSource.getSupplies(
            useSortNameASC = useSortNameASC,
            useSortNameDESC = useSortNameDESC,
            useSortExpiryASC = useSortExpiryASC,
            useSortExpiryDESC = useSortExpiryDESC,
            useFilterContainerBarcodes = useFilterContainerBarcode,
            filterContainerBarcodes = filterContainerBarcode,
            useFilterSupplyUseIds = useFilterSupplyUseIds,
            filterSupplyUseIds = filterSupplyUseIds
        )
    }

    override suspend fun getSupplyByBarcode(barcode: String): Supply? {
        return supplyLocalDataSource.getSupplyByBarcode(barcode)
    }

    override suspend fun isSupplyInContainer(
        supplyBarcode: String,
        containerBarcode: String
    ): Boolean {
        return supplyLocalDataSource.isSupplyInContainer(supplyBarcode, containerBarcode)
    }

    override suspend fun updateContainerOfSupply(
        supplyBarcode: String,
        newContainerBarcode: String
    ) {
        supplyLocalDataSource.updateContainerOfSupply(supplyBarcode, newContainerBarcode)
    }

    override suspend fun deleteAll() {
        supplyLocalDataSource.deleteAll()
    }

    override fun getSuppliesByName(query: String): Flow<List<Supply>> {
        return supplyLocalDataSource.getSuppliesByName(query)
    }

    override fun getExpiredSupplies(): Flow<List<Supply>> {
        return supplyLocalDataSource.getExpiredSupplies()
    }

    override suspend fun getExpiredTodaySupplies(): List<Supply> {
        return supplyLocalDataSource.getExpiredTodaySupplies()
    }
}