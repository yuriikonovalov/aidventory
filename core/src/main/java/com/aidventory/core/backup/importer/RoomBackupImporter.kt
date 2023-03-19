package com.aidventory.core.backup.importer

import android.content.Context
import android.net.Uri
import com.aidventory.core.backup.Backup
import com.aidventory.core.backup.hash.BackupHashManager
import com.aidventory.core.backup.json.JsonBackupConverter
import com.aidventory.core.common.di.AppDispatcher
import com.aidventory.core.common.di.Dispatcher
import com.aidventory.core.database.dao.ContainerDao
import com.aidventory.core.database.dao.SupplyDao
import com.aidventory.core.database.dao.SupplyUseDao
import com.aidventory.core.database.di.DatabaseVersion
import com.aidventory.core.domain.model.HashNotEqualException
import com.aidventory.core.domain.model.VersionNotEqualException
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class RoomBackupImporter @Inject constructor(
    @ApplicationContext private val context: Context,
    @Dispatcher(AppDispatcher.IO) private val ioDispatcher: CoroutineDispatcher,
    private val jsonBackupConverter: JsonBackupConverter,
    private val backupHashManager: BackupHashManager,
    private val supplyDao: SupplyDao,
    private val containerDao: ContainerDao,
    private val supplyUseDao: SupplyUseDao,
    @DatabaseVersion private val databaseVersion: Int
) : BackupImporter {

    override suspend fun import(uri: Uri) {
        withContext(ioDispatcher) {
            val backup = readBackupFromUri(uri)
            val hashBody = jsonBackupConverter.toContentJson(backup.content)
            // Check hash
            val hashEqual = backupHashManager.isHashEqual(hashBody, backup.hash)
            if (!hashEqual) throw HashNotEqualException

            // Check version
            val versionEqual = databaseVersion == backup.version
            if (!versionEqual) throw VersionNotEqualException

            insertBackupContentInDb(backup.content)
        }
    }

    private fun readBackupFromUri(uri: Uri): Backup {
        val json = context.contentResolver.openInputStream(uri)!!.use { inputStream ->
            inputStream.reader().use { inputStreamReader ->
                inputStreamReader.readText()
            }
        }
        return jsonBackupConverter.fromJson(json)!!
    }

    private suspend fun insertBackupContentInDb(content: Backup.Content) {
        // Clear DB
        supplyDao.deleteAll()
        containerDao.deleteAll()
        supplyUseDao.deleteNotDefault()
        // Insert data. The order of insert calls depends on DB entities' relations.
        containerDao.insert(content.containers)
        supplyDao.insert(content.supplies)
        supplyUseDao.insert(content.supplyUses)
        supplyDao.insertSupplySupplyUseCrossRefEntities(content.supplySupplyUseCrossRefs)
    }
}