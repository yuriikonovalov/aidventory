package com.aidventory

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.aidventory.core.notification.NotifySupplyExpiredWorker
import com.aidventory.core.notification.SupplyExpiryNotificationChannel
import com.aidventory.core.utils.ClearCacheWorker
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject


@HiltAndroidApp
class AidventoryApplication : Application(), Configuration.Provider {
    @Inject
    lateinit var workerFactory: HiltWorkerFactory
    override fun onCreate() {
        super.onCreate()
        SupplyExpiryNotificationChannel.createNotificationChannel(this)
        NotifySupplyExpiredWorker.enqueueNextExecution(this)
        ClearCacheWorker.enqueuePeriodicExecution(this)
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }
}