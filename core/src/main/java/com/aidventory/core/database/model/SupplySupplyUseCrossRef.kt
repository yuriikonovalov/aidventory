package com.aidventory.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Cross reference for many to many relationship between [SupplyEntity] and [SupplyUseEntity].
 */

@JsonClass(generateAdapter = true)
@Entity(
    tableName = "supplies_supply_uses",
    primaryKeys = ["supply_id", "supply_use_id"],
    foreignKeys = [
        ForeignKey(
            entity = SupplyEntity::class,
            parentColumns = ["barcode"],
            childColumns = ["supply_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = SupplyUseEntity::class,
            parentColumns = ["id"],
            childColumns = ["supply_use_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("supply_id"),
        Index("supply_use_id")
    ]
)
internal data class SupplySupplyUseCrossRef(
    @Json(name = "supply_id")
    @ColumnInfo(name = "supply_id")
    val supplyId: String,
    @Json(name = "supply_use_id")
    @ColumnInfo(name = "supply_use_id")
    val supplyUseId: Int
)