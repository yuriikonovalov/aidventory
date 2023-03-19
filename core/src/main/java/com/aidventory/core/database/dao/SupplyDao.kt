package com.aidventory.core.database.dao

import androidx.room.*
import com.aidventory.core.database.model.PopulatedSupply
import com.aidventory.core.database.model.SupplyEntity
import com.aidventory.core.database.model.SupplySupplyUseCrossRef
import kotlinx.coroutines.flow.Flow

@Dao
internal interface SupplyDao {
    /**
     * Inserts [entity] if it's not present in the db, otherwise updates this [entity].
     */
    @Upsert
    suspend fun upsert(entity: SupplyEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entities: List<SupplyEntity>)

    @Query("DELETE FROM supplies WHERE barcode = :barcode")
    suspend fun delete(barcode: String)

    /**
     * Returns a flow of lists of [PopulatedSupply] based on the provided sort and filter arguments.
     */
    @Transaction
    @Query(
        value = """
        SELECT * FROM supplies
        WHERE
            CASE WHEN :useFilterContainerBarcodes
                THEN container_barcode IN (:filterContainerBarcodes)
                ELSE 1
            END
        AND
            CASE WHEN :useFilterSupplyUseIds
                THEN barcode IN
                    (
                        SELECT supply_id FROM supplies_supply_uses
                        WHERE supply_use_id IN (:filterSupplyUseIds)
                    )
                ELSE 1
            END

        ORDER BY
            CASE
                WHEN :useSortNameASC
                THEN name
            END ASC,

            CASE
                WHEN :useSortNameDESC
                THEN name
            END DESC,

            CASE
                WHEN :useSortExpiryASC
                THEN date(expiry)
            END ASC,

            CASE
                WHEN :useSortExpiryDESC
                THEN date(expiry)
            END DESC
    """
    )
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
    ): Flow<List<PopulatedSupply>>


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSupplySupplyUseCrossRefEntities(entities: List<SupplySupplyUseCrossRef>)

    @Transaction
    @Query(
        """
        SELECT * 
        FROM supplies
        WHERE barcode = :barcode
    """
    )
    suspend fun getPopulatedSupplyByBarcode(barcode: String): PopulatedSupply?

    @Query(
        """
        SELECT COUNT(*) 
        FROM supplies
        WHERE barcode = :supplyBarcode AND container_barcode = :containerBarcode
    """
    )
    suspend fun getCountSupplyWithContainer(supplyBarcode: String, containerBarcode: String): Int

    @Query(
        """
        UPDATE supplies 
        SET container_barcode = :newContainerBarcode
        WHERE barcode = :supplyBarcode
    """
    )
    suspend fun updateContainer(supplyBarcode: String, newContainerBarcode: String)

    @Query("DELETE FROM supplies")
    suspend fun deleteAll()

    @Query("SELECT * FROM supplies_supply_uses")
    suspend fun getSupplySupplyUseCrossRefs(): List<SupplySupplyUseCrossRef>

    @Transaction
    @Query(
        """
         SELECT *
         FROM supplies
         WHERE name LIKE :query || '%'
    """
    )
    fun getSuppliesByName(query: String): Flow<List<PopulatedSupply>>

    @Transaction
    @Query(
        """
        SELECT *
        FROM supplies
        WHERE date(expiry) <= date('now')
    """
    )
    fun getExpiredSupplies(): Flow<List<PopulatedSupply>>

    @Transaction
    @Query(
        """
        SELECT *
        FROM supplies
        WHERE date(expiry) == date('now')
    """
    )
    suspend fun getExpiredTodaySupplies(): List<PopulatedSupply>
}

