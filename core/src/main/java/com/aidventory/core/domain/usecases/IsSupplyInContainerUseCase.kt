package com.aidventory.core.domain.usecases

import com.aidventory.core.common.result.Result
import com.aidventory.core.domain.interfaces.repositories.SupplyRepository
import java.io.IOException
import javax.inject.Inject

class IsSupplyInContainerUseCase @Inject constructor(private val supplyRepository: SupplyRepository) {
    suspend operator fun invoke(supplyBarcode: String, containerBarcode: String): Result<Boolean> {
        return try {
            Result.Success(supplyRepository.isSupplyInContainer(supplyBarcode, containerBarcode))
        } catch (e: IOException) {
            Result.Error(e)
        }
    }
}