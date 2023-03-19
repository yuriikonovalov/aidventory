package com.aidventory.core.database.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.aidventory.core.domain.entities.Container
import com.aidventory.core.domain.entities.Supply

internal data class SupplyWithUses(
    @Embedded
    val supplyEntity: SupplyEntity,

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

internal fun SupplyWithUses.toDomain(container: Container?) = Supply(
    barcode = supplyEntity.barcode,
    isBarcodeGenerated = supplyEntity.isBarcodeGenerated,
    name = supplyEntity.name,
    uses = supplyUseEntities.map(SupplyUseEntity::toDomain),
    expiry = supplyEntity.expiry,
    container = container
)