package com.aidventory.core.database.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.aidventory.core.domain.entities.Supply

/**
 * A class that represents a fully populated supply.
 */
internal data class PopulatedSupply(
    @Embedded
    val supplyEntity: SupplyEntity,

    @Relation(
        parentColumn = "container_barcode",
        entityColumn = "barcode"
    )
    val containerEntity: ContainerEntity?,

    @Relation(
        parentColumn = "barcode",
        entityColumn = "id",
        associateBy = Junction(
            value = SupplySupplyUseCrossRef::class,
            parentColumn = "supply_id",
            entityColumn = "supply_use_id"
        )
    )
    val supplyUseEntities: List<SupplyUseEntity>
)

internal fun PopulatedSupply.toDomain(): Supply {
    return Supply(
        barcode = supplyEntity.barcode,
        isBarcodeGenerated = supplyEntity.isBarcodeGenerated,
        name = supplyEntity.name,
        uses = supplyUseEntities.map(SupplyUseEntity::toDomain),
        expiry = supplyEntity.expiry,
        container = containerEntity?.toDomain()
    )
}