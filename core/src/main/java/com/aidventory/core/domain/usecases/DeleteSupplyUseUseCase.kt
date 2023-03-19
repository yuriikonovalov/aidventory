package com.aidventory.core.domain.usecases

import com.aidventory.core.common.result.Result
import com.aidventory.core.domain.interfaces.repositories.SupplyUseRepository
import java.io.IOException
import javax.inject.Inject

class DeleteSupplyUseUseCase @Inject constructor(
    private val supplyUseRepository: SupplyUseRepository
) {
    suspend operator fun invoke(id: Int): Result<Unit> {
        return try {
            supplyUseRepository.deleteSupplyUse(id)
            Result.Success(Unit)
        } catch (e: IOException) {
            Result.Error(e)
        }
    }
}