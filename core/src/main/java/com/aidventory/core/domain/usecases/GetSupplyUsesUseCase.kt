package com.aidventory.core.domain.usecases

import com.aidventory.core.common.result.Result
import com.aidventory.core.common.result.asResult
import com.aidventory.core.domain.entities.SupplyUse
import com.aidventory.core.domain.interfaces.repositories.SupplyUseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSupplyUsesUseCase @Inject constructor(
    private val supplyUseRepository: SupplyUseRepository
) {
    operator fun invoke(): Flow<Result<List<SupplyUse>>> {
        return supplyUseRepository.getSupplyUses().asResult()
    }
}