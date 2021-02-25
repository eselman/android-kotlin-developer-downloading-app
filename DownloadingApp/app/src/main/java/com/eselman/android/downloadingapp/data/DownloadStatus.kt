package com.eselman.android.downloadingapp.data


sealed class DownloadStatus(val status: String) {
    object Successful:DownloadStatus("Successful")
    object Failed:DownloadStatus("Failed")
    object Unknown:DownloadStatus("Unknown")
}