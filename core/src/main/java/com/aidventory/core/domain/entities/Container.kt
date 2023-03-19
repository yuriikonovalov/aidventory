package com.aidventory.core.domain.entities

import java.time.LocalDate

data class Container(
    val barcode: String,
    val name: String,
    val created: LocalDate
)
