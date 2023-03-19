package com.aidventory.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.LocalDate

@JsonClass(generateAdapter = true)
@Entity(
    tableName = "supplies",
    foreignKeys = [
        ForeignKey(
            entity = ContainerEntity::class,
            parentColumns = ["barcode"],
            childColumns = ["container_barcode"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("barcode"), Index("container_barcode")]
)
internal data class SupplyEntity(
    @PrimaryKey
    val barcode: String,
    @Json(name = "is_barcode_generated")
    @ColumnInfo(name = "is_barcode_generated")
    val isBarcodeGenerated: Boolean,
    val name: String,
    val expiry: LocalDate? = null,
    @Json(name = "container_barcode")
    @ColumnInfo(name = "container_barcode")
    val containerBarcode: String? = null
)