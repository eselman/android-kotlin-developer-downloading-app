package com.eselman.android.downloadingapp.utils

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import com.eselman.android.downloadingapp.DetailActivity
import com.eselman.android.downloadingapp.MainActivity
import com.eselman.android.downloadingapp.R
import com.eselman.android.downloadingapp.data.FileDownloaded

private const val NOTIFICATION_ID = 0

fun NotificationManager.sendNotification(messageBody: String, applicationContext: Context, fileDownloaded: FileDownloaded) {
    val contentIntent = Intent(applicationContext, MainActivity::class.java)
    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_CANCEL_CURRENT
    )

    val statusIntent = Intent(applicationContext, DetailActivity::class.java)
    val bundle = Bundle()
    bundle.putParcelable("FILE", fileDownloaded)
    statusIntent.putExtra("FILE_DOWNLOADED", bundle)
    val statusPendingIntent: PendingIntent? = TaskStackBuilder.create(applicationContext).run {
        addNextIntentWithParentStack(statusIntent)
        getPendingIntent(0, PendingIntent.FLAG_CANCEL_CURRENT)
    }

    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.loading_app_notification_channel_id)
    )
        .setSmallIcon(R.drawable.ic_baseline_cloud_download)
        .setContentTitle(
            applicationContext
                .getString(R.string.loading_app_notification_title)
        )
        .setContentText(messageBody)
        .setContentIntent(contentPendingIntent)
        .setAutoCancel(true)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .addAction( 0,
        applicationContext.getString(R.string.loading_app_notification_status_action),
        statusPendingIntent)

    notify(NOTIFICATION_ID, builder.build())
}