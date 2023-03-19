package com.aidventory.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.aidventory.core.domain.entities.Container
import com.squareup.moshi.JsonClass
import java.time.LocalDate


@JsonClass(generateAdapter = true)
@Entity(
    tableName = "containers",
    indices = [Index("barcode")]
)
internal data class ContainerEntity(
    @PrimaryKey
    @ColumnInfo(name = "barcode")
    val barcode: String,
    val name: String,
    val created: LocalDate
) {
    companion object {
        fun fromDomain(domain: Container) = ContainerEntity(
            barcode = domain.barcode,
            name = domain.name,
            created = domain.created
        )
    }
}

internal fun ContainerEntity.toDomain() =
    Container(barcode = barcode, name = name, created = created)