package com.aidventory.core.barcode.generator

import com.aidventory.core.domain.interfaces.BarcodeGenerator
import java.util.UUID
import javax.inject.Inject

internal class BarcodeGeneratorImpl @Inject constructor() : BarcodeGenerator {
    override fun generate(): String {
        val uuid = UUID.randomUUID()
        return System.nanoTime().toString() + uuid.toString()
    }
}