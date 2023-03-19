package com.aidventory.core.domain.usecases

import android.net.Uri
import com.aidventory.core.domain.interfaces.BackupManager
import com.aidventory.core.common.result.Result
import javax.inject.Inject

class ImportBackupUseCase @Inject constructor(
    private val backupManager: BackupManager
) {
    suspend operator fun invoke(uri: Uri): Result<Unit> {
        return try {
            backupManager.import(uri)
            Result.Success(Unit)
        } catch (e: Throwable) {
            e.printStackTrace()
            Result.Error(e)
        }
    }
}