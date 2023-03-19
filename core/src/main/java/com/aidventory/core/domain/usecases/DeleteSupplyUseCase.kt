package com.aidventory.core.domain.usecases

import com.aidventory.core.domain.interfaces.repositories.SupplyRepository
import java.io.IOException
import javax.inject.Inject

class DeleteSupplyUseCase @Inject constructor(
    private val supplyRepository: SupplyRepository
) {
    suspend operator fun invoke(barcode: String) {
        try {
            supplyRepository.delete(barcode)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}