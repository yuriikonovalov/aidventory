package com.aidventory.core.domain.interfaces

interface BarcodeGenerator {
    /**
     * Generates a unique barcode value.
     */
    fun generate(): String
}

