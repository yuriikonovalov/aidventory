package com.aidventory.core.backup

import com.aidventory.core.database.model.ContainerEntity
import com.aidventory.core.database.model.SupplyEntity
import com.aidventory.core.database.model.SupplySupplyUseCrossRef
import com.aidventory.core.database.model.SupplyUseEntity
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Structure of a JSON file that is used for storing backup data of the application.
 */
@JsonClass(generateAdapter = true)
internal data class Backup(
    val hash: String,
    val version: Int,
    val content: Content
) {
    @JsonClass(generateAdapter = true)
    data class Content(
        val supplies: List<SupplyEntity>,
        val containers: List<ContainerEntity>,
        @Json(name = "supply_uses")
        val supplyUses: List<SupplyUseEntity>,
        @Json(name = "supplies_supply_uses")
        val supplySupplyUseCrossRefs: List<SupplySupplyUseCrossRef>
    )
}


