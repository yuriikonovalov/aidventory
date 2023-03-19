package com.aidventory.core.backup.exporter

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.aidventory.core.backup.Backup
import com.aidventory.core.backup.hash.BackupHashManager
import com.aidventory.core.backup.json.JsonBackupConverter
import com.aidventory.core.common.di.AppDispatcher
import com.aidventory.core.common.di.Dispatcher
import com.aidventory.core.database.dao.ContainerDao
import com.aidventory.core.database.dao.SupplyDao
import com.aidventory.core.database.dao.SupplyUseDao
import com.aidventory.core.database.di.DatabaseVersion
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

internal class RoomBackupExporter @Inject constructor(
    @ApplicationContext private val context: Context,
    @Dispatcher(AppDispatcher.IO) private val ioDispatcher: CoroutineDispatcher,
    private val jsonBackupConverter: JsonBackupConverter,
    private val backupHashManager: BackupHashManager,
    private val supplyDao: SupplyDao,
    private val containerDao: ContainerDao,
    private val supplyUseDao: SupplyUseDao,
    @DatabaseVersion private val databaseVersion: Int
) : BackupExporter {

    override suspend fun export(destination: Uri) {
        withContext(ioDispatcher) {
            val jsonString = getJsonString()
            context.contentResolver.openOutputStream(destination)!!.use { outputStream ->
                outputStream.writer().use { writer ->
                    writer.write(jsonString)
                }
            }
        }
    }

    override suspend fun exportInCache(): Uri {
        return withContext(ioDispatcher) {
            val jsonString = getJsonString()
            val pathName = "${context.cacheDir}/aidventory-backup${System.currentTimeMillis()}.json"
            val file = File(pathName)
            val uri = context.contentUri(file)

            FileOutputStream(file).use { outputStream ->
                outputStream.writer().use { writer ->
                    writer.write(jsonString)
                }
            }
            uri
        }
    }

    private suspend fun getJsonString(): String {
        val content = getBackupContent()
        val contentJson = jsonBackupConverter.toContentJson(content)
        val hash = backupHashManager.getHash(contentJson)
        val backup = Backup(hash, databaseVersion, content)
        return jsonBackupConverter.toJson(backup)
    }

    private suspend fun getBackupContent(): Backup.Content {
        val supplyEntities = supplyDao.getSupplies().first()
            .map { it.supplyEntity }
        val supplySupplyUseCrossRefs = supplyDao.getSupplySupplyUseCrossRefs()
        val supplyUseEntities = supplyUseDao.getSupplyUseEntities().first()
        val containerEntities = containerDao.getContainerEntities().first()
        return Backup.Content(
            supplies = supplyEntities,
            containers = containerEntities,
            supplyUses = supplyUseEntities,
            supplySupplyUseCrossRefs = supplySupplyUseCrossRefs
        )
    }

    private fun Context.contentUri(file: File): Uri {
        val authority = context.packageName + ".fileprovider"
        return FileProvider.getUriForFile(this, authority, file)
    }
}