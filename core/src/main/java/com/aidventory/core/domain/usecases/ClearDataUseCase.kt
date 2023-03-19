package com.aidventory.core.domain.usecases

import com.aidventory.core.domain.interfaces.repositories.ContainerRepository
import com.aidventory.core.domain.interfaces.repositories.SupplyRepository
import com.aidventory.core.domain.interfaces.repositories.SupplyUseRepository
import kotlinx.coroutines.coroutineScope
import java.io.IOException
import javax.inject.Inject

class ClearDataUseCase @Inject constructor(
    private val supplyRepository: SupplyRepository,
    private val containerRepository: ContainerRepository,
    private val supplyUseRepository: SupplyUseRepository
) {
    suspend operator fun invoke() {
        try {
            coroutineScope {
                supplyRepository.deleteAll()
                containerRepository.deleteAll()
                supplyUseRepository.deleteNotDefault()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}