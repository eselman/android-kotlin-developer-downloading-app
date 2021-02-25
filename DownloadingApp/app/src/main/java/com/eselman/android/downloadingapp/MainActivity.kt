package com.eselman.android.downloadingapp

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.webkit.URLUtil
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.eselman.android.downloadingapp.data.DownloadStatus
import com.eselman.android.downloadingapp.data.FileDownloaded
import com.eselman.android.downloadingapp.utils.sendNotification
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private var downloadID: Long = 0

    private lateinit var downloadManager: DownloadManager
    private lateinit var notificationManager: NotificationManager

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            notificationManager = ContextCompat.getSystemService(
                applicationContext,
                NotificationManager::class.java
            ) as NotificationManager

            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            if (id != null && id >= 0) {
                customButton.buttonState = ButtonState.Completed

                val query = DownloadManager.Query()
                query.setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL or DownloadManager.STATUS_FAILED)

                val cursor = downloadManager.query(query)
                if (cursor.moveToFirst()) {
                    val downloadStatus =
                        when (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
                            DownloadManager.STATUS_SUCCESSFUL -> DownloadStatus.Successful
                            DownloadManager.STATUS_FAILED -> DownloadStatus.Failed
                            else -> DownloadStatus.Unknown
                        }

                    val fileTitle =
                        cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE))

                    val uri =
                        cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))

                    notificationManager.sendNotification(
                        fileTitle,
                        applicationContext,
                        FileDownloaded(uri, fileTitle, downloadStatus.status)
                    )
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        customButton.setOnClickListener {
            download()
        }

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.other -> otherUrl.isVisible = true
                else -> otherUrl.isVisible = false
            }
        }

        createChannel(
            getString(R.string.loading_app_notification_channel_id),
            getString(R.string.loading_app_notification_channel_name)
        )
    }

    private fun download() {
        val selectedItem = radioGroup.checkedRadioButtonId
        var url = ""
        if (selectedItem < 0) {
            showToast()
        } else {
            url = when (selectedItem) {
                R.id.glide -> GLIDE
                R.id.udacity -> UDACITY
                R.id.retrofit -> RETROFIT
                else -> otherUrl.text.toString()
            }
        }

        if (url.isNotEmpty() && URLUtil.isValidUrl(url)) {
            val request =
                DownloadManager.Request(Uri.parse(url))
                    .setTitle(getFileTitle(url))
                    .setDescription(getString(R.string.app_description))
                    .setRequiresCharging(false)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)
                    .setDestinationInExternalPublicDir(
                        Environment.DIRECTORY_DOWNLOADS,
                        "DownloadApp"
                    )

            downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager

            downloadID =
                downloadManager.enqueue(request)// enqueue puts the download request in the queue.
        } else {
            showToast()
        }
    }

    private fun showToast() {
        Toast.makeText(this, "Please select the file to download", Toast.LENGTH_SHORT).show()
        Handler(mainLooper).postDelayed({
            customButton.buttonState = ButtonState.Completed
        }, 2500)
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.enableLights(true)
            notificationChannel.enableVibration(true)

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun getFileTitle(url:String): String {
        return when (url) {
            GLIDE -> getString(R.string.glide)
            UDACITY -> getString(R.string.loadApp)
            RETROFIT -> getString(R.string.retrofit)
            else -> getString(R.string.other) + url
        }
    }

    companion object {
        private const val GLIDE = "https://github.com/bumptech/glide/archive/master.zip"
        private const val UDACITY =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val RETROFIT = "https://github.com/square/retrofit/archive/master.zip"
    }
}