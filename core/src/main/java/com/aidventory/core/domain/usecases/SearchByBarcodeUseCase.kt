package com.aidventory.core.domain.usecases

import com.aidventory.core.common.result.Result
import com.aidventory.core.domain.model.SearchByBarcodeResult
import com.aidventory.core.domain.interfaces.repositories.ContainerRepository
import com.aidventory.core.domain.interfaces.repositories.SupplyRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.io.IOException
import javax.inject.Inject

class SearchByBarcodeUseCase @Inject constructor(
    private val supplyRepository: SupplyRepository,
    private val containerRepository: ContainerRepository
) {
    suspend operator fun invoke(barcode: String): Result<SearchByBarcodeResult> {
        return coroutineScope {
            return@coroutineScope try {
                val deferredSupply = async { supplyRepository.getSupplyByBarcode(barcode) }
                val deferredContainer = async { containerRepository.getContainerByBarcode(barcode) }
                Result.Success(
                    SearchByBarcodeResult(
                        barcode = barcode,
                        supply = deferredSupply.await(),
                        container = deferredContainer.await()?.container
                    )
                )
            } catch (e: IOException) {
                Result.Error(e)
            }
        }
    }
}