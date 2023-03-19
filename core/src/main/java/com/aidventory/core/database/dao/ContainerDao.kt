package com.aidventory.core.database.dao

import androidx.room.*
import com.aidventory.core.database.model.ContainerEntity
import com.aidventory.core.database.model.PopulatedContainer
import kotlinx.coroutines.flow.Flow

@Dao
internal interface ContainerDao {
    /**
     * Inserts [entity] if it's not present in the db, otherwise updates this [entity].
     */
    @Upsert
    suspend fun upsert(entity: ContainerEntity)

    @Query(
        value = """
        DELETE FROM containers
        WHERE barcode = :barcode
    """
    )
    suspend fun delete(barcode: String)

    @Transaction
    @Query(value = "SELECT * FROM containers")
    fun getContainerEntities(): Flow<List<ContainerEntity>>

    @Transaction
    @Query(value = "SELECT * FROM containers")
    fun getPopulatedContainers(): Flow<List<PopulatedContainer>>

    @Transaction
    @Query(
        value = """
        SELECT *
        FROM containers
        WHERE barcode = :barcode 
    """
    )
    suspend fun getPopulatedContainerByBarcode(barcode: String): PopulatedContainer?

    @Query("DELETE FROM containers")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entities: List<ContainerEntity>)
}