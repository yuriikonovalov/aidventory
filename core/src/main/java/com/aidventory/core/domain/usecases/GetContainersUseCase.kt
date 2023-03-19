package com.aidventory.core.domain.usecases

import com.aidventory.core.common.result.Result
import com.aidventory.core.common.result.asResult
import com.aidventory.core.domain.entities.Container
import com.aidventory.core.domain.interfaces.repositories.ContainerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetContainersUseCase @Inject constructor(
    private val containerRepository: ContainerRepository
) {
    operator fun invoke(): Flow<Result<List<Container>>> {
        return containerRepository.getContainers().asResult()
    }
}