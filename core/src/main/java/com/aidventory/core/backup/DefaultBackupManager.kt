package com.aidventory.core.backup

import android.net.Uri
import com.aidventory.core.backup.exporter.BackupExporter
import com.aidventory.core.backup.importer.BackupImporter
import com.aidventory.core.domain.interfaces.BackupManager
import javax.inject.Inject

internal class DefaultBackupManager @Inject constructor(
    private val importer: BackupImporter,
    private val exporter: BackupExporter,
) : BackupManager {
    override suspend fun import(jsonUri: Uri) {
        importer.import(jsonUri)
    }

    override suspend fun export(destination: Uri) {
        exporter.export(destination)
    }

    override suspend fun exportInCache(): Uri {
        return exporter.exportInCache()
    }
}