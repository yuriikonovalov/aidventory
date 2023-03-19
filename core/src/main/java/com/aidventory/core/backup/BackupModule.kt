package com.aidventory.core.backup

import com.aidventory.core.backup.exporter.BackupExporter
import com.aidventory.core.backup.exporter.RoomBackupExporter
import com.aidventory.core.backup.hash.BackupHashManager
import com.aidventory.core.backup.hash.SHA256BackupHashManager
import com.aidventory.core.backup.importer.BackupImporter
import com.aidventory.core.backup.importer.RoomBackupImporter
import com.aidventory.core.backup.json.JsonBackupConverter
import com.aidventory.core.backup.json.MoshiJsonBackupConverter
import com.aidventory.core.domain.interfaces.BackupManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Suppress("unused", "unused")
@Module
@InstallIn(SingletonComponent::class)
internal interface BackupModule {
    @Binds
    fun bindsJsonBackupConverter(impl: MoshiJsonBackupConverter): JsonBackupConverter

    @Binds
    fun bindsBackupMessageDigest(impl: SHA256BackupHashManager): BackupHashManager

    @Binds
    fun bindsBackupManager(impl: DefaultBackupManager): BackupManager

    @Binds
    fun bindsBackupExporter(impl: RoomBackupExporter): BackupExporter

    @Binds
    fun bindsBackupImporter(impl: RoomBackupImporter): BackupImporter
}