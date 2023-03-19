package com.aidventory.core.domain.interfaces

import android.net.Uri

interface BackupManager {
    suspend fun import(jsonUri: Uri)

    suspend fun export(destination: Uri)

    suspend fun exportInCache(): Uri
}