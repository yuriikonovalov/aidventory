package com.aidventory.core.notification

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.net.toUri
import com.aidventory.core.R

private const val CHANNEL_ID = "supply_expiry"

object SupplyExpiryNotificationChannel {
    fun createNotificationChannel(context: Context) {
        val channel = NotificationChannelCompat
            .Builder(CHANNEL_ID, NotificationManagerCompat.IMPORTANCE_DEFAULT)
            .setName(context.getString(R.string.notification_channel_expired_supplies_name))
            .setDescription(context.getString(R.string.notification_channel_expired_supplies_description))
            .build()
        // Register the channel with the system.
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.createNotificationChannel(channel)
    }
}

object SupplyExpiryNotification {

    const val EXPIRED_SCREEN_DEEP_LINK_URI = "https://aidventory.com"

    private fun getPendingIntent(
        context: Context,
        activityClass: Class<ComponentActivity>
    ): PendingIntent? {
        val intent = Intent(
            Intent.ACTION_VIEW,
            "$EXPIRED_SCREEN_DEEP_LINK_URI/expired".toUri(),
            context,
            activityClass
        )
        val pendingIntent = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }
        return pendingIntent
    }

    fun createNotification(
        context: Context,
        activityClass: Class<ComponentActivity>,
        text: String
    ): Notification {
        val pendingIntent = getPendingIntent(context, activityClass)
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_medication)
            .setContentTitle(context.getString(R.string.notification_supply_expiry_title))
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            .build()
    }
}