package com.aidventory.core.backup.json

import com.aidventory.core.backup.Backup

internal interface JsonBackupConverter {
    fun toJson(backup: Backup): String
    fun toContentJson(backupContent: Backup.Content): String
    fun fromJson(json: String): Backup?
}