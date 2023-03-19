package com.aidventory.core.domain.usecases

import com.aidventory.core.domain.interfaces.BarcodeGenerator
import com.aidventory.core.common.result.Result
import com.aidventory.core.domain.entities.SupplyUse
import com.aidventory.core.domain.interfaces.repositories.SupplyRepository
import java.io.IOException
import java.time.LocalDate
import javax.inject.Inject

class AddSupplyUseCase @Inject constructor(
    private val supplyRepository: SupplyRepository,
    private val barcodeGenerator: BarcodeGenerator
) {
    suspend operator fun invoke(
        barcode: String?,
        name: String,
        containerBarcode: String?,
        expiry: LocalDate?,
        uses: List<SupplyUse>
    ): Result<Unit> {
        val isBarcodeGenerated = barcode == null
        return try {
            supplyRepository.insert(
                barcode = barcode ?: barcodeGenerator.generate(),
                isBarcodeGenerated = isBarcodeGenerated,
                name = name,
                containerBarcode = containerBarcode,
                expiry = expiry,
                uses = uses
            )
            Result.Success(Unit)
        } catch (e: IOException) {
            Result.Error(e)
        }
    }
}