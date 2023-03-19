package com.aidventory.core.domain.usecases

import com.aidventory.core.common.result.Result
import com.aidventory.core.common.result.asResult
import com.aidventory.core.domain.model.Theme
import com.aidventory.core.domain.interfaces.repositories.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetCurrentThemeUseCase @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    operator fun invoke(): Flow<Result<Theme>> {
        return userPreferencesRepository.userPreferences
            .map { it.theme }
            .asResult()
    }
}