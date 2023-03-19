package com.aidventory.core.domain.usecases

import com.aidventory.core.common.result.Result
import com.aidventory.core.common.result.asResult
import com.aidventory.core.domain.entities.Supply
import com.aidventory.core.domain.interfaces.repositories.SupplyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetExpiredSuppliesUseCase @Inject constructor(
    private val supplyRepository: SupplyRepository
) {
    operator fun invoke(): Flow<Result<Map<String?, List<Supply>>>> {
        return supplyRepository.getExpiredSupplies()
            .map { supplies ->
                supplies.groupBy { supply -> supply.container?.barcode }
            }
            .asResult()
    }
}