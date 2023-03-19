package com.aidventory.core.database.dao

import androidx.room.*
import com.aidventory.core.database.model.SupplyUseEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for [SupplyUseEntity] access.
 */
@Dao
internal interface SupplyUseDao {

    /**
     * Inserts [entity] into the db with replacing old one if the entities' IDs match.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: SupplyUseEntity)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entities: List<SupplyUseEntity>)

    /**
     * Deletes an entity from the db by the provided [id].
     */
    @Query(
        """
        DELETE FROM supply_uses
        WHERE id = :id
    """
    )
    suspend fun delete(id: Int)

    @Query(value = " SELECT * FROM supply_uses")
    fun getSupplyUseEntities(): Flow<List<SupplyUseEntity>>

    @Query(
        """
        DELETE FROM supply_uses
        WHERE is_default = 0 
    """
    )
    suspend fun deleteNotDefault()
}