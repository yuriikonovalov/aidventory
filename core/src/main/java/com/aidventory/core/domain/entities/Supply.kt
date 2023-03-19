package com.aidventory.core.domain.entities

import java.time.LocalDate

data class Supply(
    val barcode: String,
    val isBarcodeGenerated: Boolean,
    val name: String,
    val uses: List<SupplyUse>,
    val expiry: LocalDate?,
    val container: Container?
)
