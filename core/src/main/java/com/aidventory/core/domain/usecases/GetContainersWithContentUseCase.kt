package com.aidventory.core.domain.usecases

import com.aidventory.core.common.result.Result
import com.aidventory.core.common.result.asResult
import com.aidventory.core.domain.entities.ContainerWithContent
import com.aidventory.core.domain.interfaces.repositories.ContainerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetContainersWithContentUseCase @Inject constructor(
    private val containerRepository: ContainerRepository
) {
    operator fun invoke(): Flow<Result<List<ContainerWithContent>>> {
        return containerRepository.getContainersWithContent().asResult()
    }
}