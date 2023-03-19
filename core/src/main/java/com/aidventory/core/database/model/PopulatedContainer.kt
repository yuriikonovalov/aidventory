package com.aidventory.core.database.model

import androidx.room.Embedded
import androidx.room.Relation
import com.aidventory.core.domain.entities.ContainerWithContent

/**
 * A class that represents a fully populated container.
 */
internal data class PopulatedContainer(
    @Embedded
    val containerEntity: ContainerEntity,

    // Nested relationship: Container -> list(Supply -> list(SupplyUse)).
    @Relation(
        entity = SupplyEntity::class,
        parentColumn = "barcode",
        entityColumn = "container_barcode",
    )
    val suppliesWithUses: List<SupplyWithUses>
)

internal fun PopulatedContainer.toDomain(): ContainerWithContent {
    val container = containerEntity.toDomain()
    return ContainerWithContent(
        container = container,
        content = suppliesWithUses.map { it.toDomain(container) }
    )
}
