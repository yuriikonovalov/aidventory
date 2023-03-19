package com.aidventory.core.domain.usecases

import com.aidventory.core.common.result.Result
import com.aidventory.core.domain.interfaces.repositories.SupplyUseRepository
import java.io.IOException
import javax.inject.Inject

class AddSupplyUseUseCase @Inject constructor(
    private val supplyUseRepository: SupplyUseRepository
) {
    suspend operator fun invoke(name: String): Result<Unit> {
        return try {
            supplyUseRepository.insertSupplyUse(name.trim())
            Result.Success(Unit)
        } catch (e: IOException) {
            Result.Error(e)
        }
    }
}