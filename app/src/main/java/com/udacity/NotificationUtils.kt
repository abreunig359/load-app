package com.udacity

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.udacity.MainActivity.Companion.CHANNEL_ID
import com.udacity.MainActivity.Companion.NOTIFICATION_ID

fun NotificationManager.sendNotification(
    fileName: String,
    status: String,
    applicationContext: Context
) {

    val switchActivityIntent = Intent(applicationContext, DetailActivity::class.java)
    switchActivityIntent.putExtra(FILE_NAME_KEY, fileName)
    switchActivityIntent.putExtra(STATUS_KEY, status)

    val switchActivityPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        switchActivityIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or
            PendingIntent.FLAG_IMMUTABLE
    )

    val style = NotificationCompat.BigTextStyle()

    val action = NotificationCompat.Action.Builder(
        R.drawable.ic_baseline_info_24,
        applicationContext.getString(R.string.notification_button),
        switchActivityPendingIntent
    ).build()

    val builder = NotificationCompat.Builder(
        applicationContext,
        CHANNEL_ID
    )
        .setSmallIcon(R.drawable.ic_baseline_cloud_download_24)
        .setContentTitle(applicationContext.getString(R.string.notification_title))
        .setContentText(applicationContext.getString(R.string.notification_description))
        .addAction(action)
        .setStyle(style)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)

    Log.i("NotificationManager", "Send notification")
    notify(NOTIFICATION_ID, builder.build())
}
