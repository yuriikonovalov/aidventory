package com.aidventory.core.domain.usecases

import com.aidventory.core.common.result.Result
import com.aidventory.core.domain.entities.Supply
import com.aidventory.core.domain.interfaces.repositories.SupplyRepository
import java.io.IOException
import javax.inject.Inject

class GetSupplyUseCase @Inject constructor(private val supplyRepository: SupplyRepository) {
    suspend operator fun invoke(barcode: String): Result<Supply> {
        return try {
            supplyRepository.getSupplyByBarcode(barcode)
                ?.let { Result.Success(it) }
                ?: Result.Error()
        } catch (e: IOException) {
            Result.Error(e)
        }
    }
}