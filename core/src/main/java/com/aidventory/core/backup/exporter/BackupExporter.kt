package com.aidventory.core.backup.exporter

import android.net.Uri

internal interface BackupExporter {
    suspend fun export(destination: Uri)
    suspend fun exportInCache(): Uri
}
