package com.aidventory.core.domain.usecases

import com.aidventory.core.domain.entities.Supply
import com.aidventory.core.domain.interfaces.repositories.SupplyRepository
import java.io.IOException
import javax.inject.Inject

class GetExpiredTodaySuppliesUseCase @Inject constructor(
    private val supplyRepository: SupplyRepository
) {
    suspend operator fun invoke(): List<Supply> {
        return try {
            supplyRepository.getExpiredTodaySupplies()
        } catch (e: IOException) {
            emptyList()
        }
    }
}