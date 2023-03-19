package com.aidventory.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.aidventory.core.database.dao.ContainerDao
import com.aidventory.core.database.dao.SupplyDao
import com.aidventory.core.database.dao.SupplyUseDao
import com.aidventory.core.database.model.ContainerEntity
import com.aidventory.core.database.model.SupplyEntity
import com.aidventory.core.database.model.SupplySupplyUseCrossRef
import com.aidventory.core.database.model.SupplyUseEntity
import com.aidventory.core.database.util.LocalDateConverter

@Database(
    entities = [
        ContainerEntity::class,
        SupplyEntity::class,
        SupplyUseEntity::class,
        SupplySupplyUseCrossRef::class
    ],
    version = AidventoryDatabase.VERSION,
    exportSchema = true
)
@TypeConverters(LocalDateConverter::class)
internal abstract class AidventoryDatabase : RoomDatabase() {
    abstract fun supplyUseDao(): SupplyUseDao
    abstract fun containerDao(): ContainerDao
    abstract fun supplyDao(): SupplyDao

    companion object {
        const val VERSION = 1
    }
}