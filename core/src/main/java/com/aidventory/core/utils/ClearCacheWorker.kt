package com.aidventory.core.utils

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.io.File
import java.util.concurrent.TimeUnit

@HiltWorker
class ClearCacheWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters
) : Worker(context, workerParams) {
    override fun doWork(): Result {
        return try {
            delete(applicationContext.cacheDir)
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private fun delete(file: File) {
        when {
            file.isDirectory -> {
                file.list()?.forEach { child ->
                    delete(File(file, child))
                }
                file.delete()
            }

            file.isFile -> {
                file.delete()
            }
        }
    }

    companion object {
        private const val NAME = "ClearCacheWork"
        fun enqueuePeriodicExecution(context: Context) {
            val workRequest = PeriodicWorkRequestBuilder<ClearCacheWorker>(5, TimeUnit.DAYS)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
        }
    }
}