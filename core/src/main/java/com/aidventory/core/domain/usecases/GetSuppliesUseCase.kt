package com.aidventory.core.domain.usecases

import com.aidventory.core.common.result.Result
import com.aidventory.core.common.result.asResult
import com.aidventory.core.domain.entities.Supply
import com.aidventory.core.domain.model.SupplyFilterParams
import com.aidventory.core.domain.model.SupplySortingParams
import com.aidventory.core.domain.interfaces.repositories.SupplyRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSuppliesUseCase @Inject constructor(private val supplyRepository: SupplyRepository) {
    operator fun invoke(
        supplySortingParams: SupplySortingParams,
        supplyFilterParams: SupplyFilterParams
    ): Flow<Result<List<Supply>>> {
        return supplyRepository.getSupplies(
            useSortNameASC = supplySortingParams.nameASC,
            useSortNameDESC = supplySortingParams.nameDESC,
            useSortExpiryASC = supplySortingParams.expiryASC,
            useSortExpiryDESC = supplySortingParams.expiryDESC,
            useFilterContainerBarcode = supplyFilterParams.useFilterContainerBarcodes,
            filterContainerBarcode = supplyFilterParams.filterContainerBarcodes,
            useFilterSupplyUseIds = supplyFilterParams.useFilterSupplyUseIds,
            filterSupplyUseIds = supplyFilterParams.filterSupplyUseIds
        ).asResult()
    }
}