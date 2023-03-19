package com.aidventory.core.domain.usecases

import com.aidventory.core.domain.interfaces.repositories.SupplyRepository
import java.io.IOException
import javax.inject.Inject

class MoveSupplyUseCase @Inject constructor(
    private val supplyRepository: SupplyRepository
) {
    suspend operator fun invoke(supplyBarcode: String, newContainerBarcode: String) {
        try {
            supplyRepository.updateContainerOfSupply(supplyBarcode, newContainerBarcode)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}