package com.aidventory.core.domain.usecases

import android.net.Uri
import com.aidventory.core.domain.interfaces.BackupManager
import com.aidventory.core.common.result.Result
import javax.inject.Inject

class SendBackupUseCase @Inject constructor(
    private val backupManager: BackupManager
) {
    suspend operator fun invoke(): Result<Uri> {
        return try {
            val uri = backupManager.exportInCache()
            Result.Success(uri)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(e)
        }
    }
}