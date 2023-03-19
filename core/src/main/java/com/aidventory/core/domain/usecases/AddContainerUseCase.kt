package com.aidventory.core.domain.usecases

import com.aidventory.core.domain.interfaces.BarcodeGenerator
import com.aidventory.core.common.result.Result
import com.aidventory.core.domain.entities.Container
import com.aidventory.core.domain.interfaces.repositories.ContainerRepository
import java.io.IOException
import java.time.LocalDate
import javax.inject.Inject

/**
 * Creates a container with the provided name and returns a barcode generated for the container.
 */
class AddContainerUseCase @Inject constructor(
    private val containerRepository: ContainerRepository,
    private val barcodeGenerator: BarcodeGenerator,
) {
    suspend operator fun invoke(name: String): Result<String> {
        return try {
            val barcode = barcodeGenerator.generate()
            val container = Container(
                barcode = barcode,
                name = name.trim(),
                created = LocalDate.now()
            )
            containerRepository.insertContainer(container)
            Result.Success(barcode)
        } catch (e: IOException) {
            Result.Error(e)
        }
    }
}