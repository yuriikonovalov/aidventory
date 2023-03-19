package com.aidventory.core.domain.usecases

import com.aidventory.core.common.result.Result
import com.aidventory.core.common.result.asResult
import com.aidventory.core.domain.entities.Supply
import com.aidventory.core.domain.interfaces.repositories.SupplyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class SearchByNameUseCase @Inject constructor(
    private val supplyRepository: SupplyRepository
) {
    operator fun invoke(query: String): Flow<Result<List<Supply>>> {
        return if (query.isBlank()) {
            flowOf(Result.Success(emptyList()))
        } else {
            supplyRepository.getSuppliesByName(query).asResult()
        }
    }
}