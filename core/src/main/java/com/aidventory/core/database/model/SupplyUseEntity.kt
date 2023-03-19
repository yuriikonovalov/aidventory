package com.aidventory.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.aidventory.core.domain.entities.SupplyUse
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
@Entity(
    tableName = "supply_uses",
    indices = [Index("id")]
)
internal data class SupplyUseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    @Json(name = "is_default")
    @ColumnInfo(name = "is_default")
    val isDefault: Boolean
)

internal fun SupplyUseEntity.toDomain() = SupplyUse(id = id, name = name, isDefault = isDefault)