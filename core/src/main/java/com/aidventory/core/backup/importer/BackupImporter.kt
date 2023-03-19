package com.aidventory.core.backup.importer

import android.net.Uri

internal interface BackupImporter {
    /**
     * Imports data from a file by [uri].
     *
     * @param uri path to a file data should be imported from
     */
    suspend fun import(uri: Uri)
}
