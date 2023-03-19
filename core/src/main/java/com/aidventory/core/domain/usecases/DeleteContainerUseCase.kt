package com.aidventory.core.domain.usecases

import com.aidventory.core.domain.interfaces.repositories.ContainerRepository
import java.io.IOException
import javax.inject.Inject

class DeleteContainerUseCase @Inject constructor(
    private val containerRepository: ContainerRepository
) {
    suspend operator fun invoke(barcode: String) {
        try {
            containerRepository.deleteContainer(barcode)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}