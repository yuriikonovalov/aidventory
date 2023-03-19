package com.aidventory.core.database.datasources

import com.aidventory.core.data.datasources.SupplyLocalDataSource
import com.aidventory.core.database.dao.SupplyDao
import com.aidventory.core.database.model.SupplyEntity
import com.aidventory.core.database.model.SupplySupplyUseCrossRef
import com.aidventory.core.database.model.toDomain
import com.aidventory.core.domain.entities.Supply
import com.aidventory.core.domain.entities.SupplyUse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

internal class SupplyLocalDataSourceImpl @Inject constructor(
    private val supplyDao: SupplyDao
) : SupplyLocalDataSource {
    override suspend fun insert(
        barcode: String,
        isBarcodeGenerated: Boolean,
        name: String,
        containerBarcode: String?,
        expiry: LocalDate?,
        uses: List<SupplyUse>
    ) {
        val entity = SupplyEntity(
            barcode = barcode,
            isBarcodeGenerated = isBarcodeGenerated,
            name = name,
            expiry = expiry,
            containerBarcode = containerBarcode
        )

        val crossRefs = uses.map { supplyUse ->
            SupplySupplyUseCrossRef(
                supplyId = barcode,
                supplyUseId = supplyUse.id
            )
        }

        supplyDao.upsert(entity)
        supplyDao.insertSupplySupplyUseCrossRefEntities(crossRefs)
    }

    override suspend fun delete(barcode: String) {
        supplyDao.delete(barcode)
    }

    override fun getSupplies(
        useSortNameASC: Boolean,
        useSortNameDESC: Boolean,
        useSortExpiryASC: Boolean,
        useSortExpiryDESC: Boolean,
        useFilterContainerBarcodes: Boolean,
        filterContainerBarcodes: Set<String>,
        useFilterSupplyUseIds: Boolean,
        filterSupplyUseIds: Set<Int>
    ): Flow<List<Supply>> {
        return supplyDao.getSupplies(
            useSortNameASC = useSortNameASC,
            useSortNameDESC = useSortNameDESC,
            useSortExpiryASC = useSortExpiryASC,
            useSortExpiryDESC = useSortExpiryDESC,
            useFilterContainerBarcodes = useFilterContainerBarcodes,
            filterContainerBarcodes = filterContainerBarcodes,
            useFilterSupplyUseIds = useFilterSupplyUseIds,
            filterSupplyUseIds = filterSupplyUseIds
        ).map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun getSupplyByBarcode(barcode: String): Supply? {
        return supplyDao.getPopulatedSupplyByBarcode(barcode)?.toDomain()
    }

    override suspend fun isSupplyInContainer(
        supplyBarcode: String,
        containerBarcode: String
    ): Boolean {
        return supplyDao.getCountSupplyWithContainer(supplyBarcode, containerBarcode) > 0
    }

    override suspend fun updateContainerOfSupply(
        supplyBarcode: String,
        newContainerBarcode: String
    ) {
        supplyDao.updateContainer(supplyBarcode, newContainerBarcode)
    }

    override suspend fun deleteAll() {
        supplyDao.deleteAll()
    }

    override fun getSuppliesByName(query: String): Flow<List<Supply>> {
        return supplyDao.getSuppliesByName(query).map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getExpiredSupplies(): Flow<List<Supply>> {
        return supplyDao.getExpiredSupplies().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun getExpiredTodaySupplies(): List<Supply> {
        return supplyDao.getExpiredTodaySupplies().map { it.toDomain() }
    }
}