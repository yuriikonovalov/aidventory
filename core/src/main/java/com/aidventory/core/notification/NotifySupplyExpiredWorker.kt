package com.aidventory.core.notification

import android.annotation.SuppressLint
import android.app.Notification
import android.content.Context
import android.icu.util.Calendar
import androidx.activity.ComponentActivity
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.aidventory.core.R
import com.aidventory.core.common.di.MainActivityClass
import com.aidventory.core.domain.entities.Supply
import com.aidventory.core.domain.usecases.GetExpiredTodaySuppliesUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

@HiltWorker
class NotifySupplyExpiredWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    @MainActivityClass private val activity: Class<ComponentActivity>,
    private val getExpiredTodaySupplies: GetExpiredTodaySuppliesUseCase
) : CoroutineWorker(context, workerParams) {
    @SuppressLint("MissingPermission") // Permission is defined in the app module.
    override suspend fun doWork(): Result {
        return try {
            val supplies = getExpiredTodaySupplies()
            if (supplies.isNotEmpty()) {
                val notificationManager = NotificationManagerCompat.from(applicationContext)
                val notificationId = supplies.first().barcode.hashCode()
                val notification = getNotification(supplies)
                notificationManager.notify(notificationId, notification)
                enqueueNextExecution(applicationContext)
            }
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }


    private fun getNotification(supplies: List<Supply>): Notification {
        val text = composeNotificationText(supplies)
        return SupplyExpiryNotification.createNotification(
            applicationContext,
            activity,
            text
        )
    }

    private fun composeNotificationText(supplies: List<Supply>): String {
        val supply = supplies.first()
        return when (supplies.size) {
            1 -> applicationContext.getString(
                R.string.notification_expired_supply_text,
                supply.name
            )

            else -> {
                val other = supplies.size - 1
                "${supply.name} " + applicationContext.resources.getQuantityString(
                    R.plurals.notification_expired_supplies_text,
                    other, other
                )
            }
        }
    }

    companion object {
        private const val NAME = "NotifySupplyExpired"
        private fun getInitialDelay(): Long {
            val currentDate = Calendar.getInstance()
            val dueDate = Calendar.getInstance()
            dueDate.set(Calendar.HOUR_OF_DAY, 8)
            dueDate.set(Calendar.MINUTE, 0)
            dueDate.set(Calendar.SECOND, 0)
            if (dueDate.before(currentDate)) {
                dueDate.add(Calendar.HOUR_OF_DAY, 24)
            }
            return dueDate.timeInMillis - currentDate.timeInMillis
        }

        fun enqueueNextExecution(context: Context) {
            val workRequest = OneTimeWorkRequestBuilder<NotifySupplyExpiredWorker>()
                .setInitialDelay(getInitialDelay(), TimeUnit.MILLISECONDS)
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                NAME,
                ExistingWorkPolicy.KEEP,
                workRequest
            )
        }
    }

}