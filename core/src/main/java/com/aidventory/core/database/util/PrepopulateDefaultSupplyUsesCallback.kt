package com.aidventory.core.database.util

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

internal object PrepopulateDefaultSupplyUsesCallback : RoomDatabase.Callback() {

    private val supplyUseNames = listOf(
        "default_supply_use_fever",
        "default_supply_use_inflammation",
        "default_supply_use_vitamins",
        "default_supply_use_cough_relief",
        "default_supply_use_cold_relief",
        "default_supply_use_digestive_disorders",
        "default_supply_use_allergy_relief",
        "default_supply_use_antibiotics",
        "default_supply_use_antidepressants"
    )

    private val queries = supplyUseNames.map { name ->
        "INSERT INTO supply_uses (name, is_default) VALUES (\"$name\", 1)"
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        GlobalScope.launch(Dispatchers.IO) {
            queries.forEach { sql ->
                db.execSQL(sql)
            }
        }
    }
}