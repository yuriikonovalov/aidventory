package com.aidventory.core.domain.model

data class SupplyFilterParams(
    val filterContainerBarcodes: Set<String> = emptySet(),
    val filterSupplyUseIds: Set<Int> = emptySet()
) {
    val useFilterContainerBarcodes: Boolean = filterContainerBarcodes.isNotEmpty()
    val useFilterSupplyUseIds: Boolean = filterSupplyUseIds.isNotEmpty()

    fun takeContainerBarcode(containerBarcode: String): SupplyFilterParams {
        return copy(filterContainerBarcodes = filterContainerBarcodes.addOrRemove(containerBarcode))
    }

    fun takeSupplyUseId(supplyUseId: Int): SupplyFilterParams {
        return copy(filterSupplyUseIds = filterSupplyUseIds.addOrRemove(supplyUseId))
    }
}

private fun <T> Set<T>.addOrRemove(value: T): Set<T> {
    val remove = this.contains(value)
    return if (remove) {
        this.filterNot { it == value }.toSet()
    } else {
        this + value
    }
}