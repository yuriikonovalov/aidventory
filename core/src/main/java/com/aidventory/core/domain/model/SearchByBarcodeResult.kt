package com.aidventory.core.domain.model

import com.aidventory.core.domain.entities.Container
import com.aidventory.core.domain.entities.Supply

data class SearchByBarcodeResult(
    val barcode: String,
    val supply: Supply? = null,
    val container: Container? = null
) {
    fun isSupplyResult() = supply != null && container == null
    fun isContainerResult() = supply == null && container != null
    fun isMultipleResult() = supply != null && container != null
}
