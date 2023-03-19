package com.aidventory.core.domain.usecases

import android.net.Uri
import com.aidventory.core.domain.interfaces.BackupManager
import com.aidventory.core.common.result.Result
import javax.inject.Inject

class SaveBackupUseCase @Inject constructor(
    private val backupManager: BackupManager
) {
    suspend operator fun invoke(uri: Uri): Result<Unit> {
        return try {
            backupManager.export(uri)
            Result.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(e)
        }
    }
}