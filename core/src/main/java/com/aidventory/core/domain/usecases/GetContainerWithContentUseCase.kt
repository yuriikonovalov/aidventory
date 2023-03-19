package com.aidventory.core.domain.usecases

import com.aidventory.core.common.result.Result
import com.aidventory.core.domain.entities.ContainerWithContent
import com.aidventory.core.domain.interfaces.repositories.ContainerRepository
import java.io.IOException
import javax.inject.Inject

class GetContainerWithContentUseCase @Inject constructor(private val containerRepository: ContainerRepository) {
    suspend operator fun invoke(barcode: String): Result<ContainerWithContent> {
        return try {
            containerRepository.getContainerByBarcode(barcode)
                ?.let { Result.Success(it) }
                ?: Result.Error()
        } catch (e: IOException) {
            Result.Error(e)
        }
    }
}