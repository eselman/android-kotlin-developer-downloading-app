package com.eselman.android.downloadingapp

import android.app.NotificationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.eselman.android.downloadingapp.data.FileDownloaded
import com.eselman.android.downloadingapp.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.cancelAll()

        val binding = ActivityDetailBinding.inflate(layoutInflater)

        val bundle = intent.extras?.getBundle("FILE_DOWNLOADED")
        val fileDownload = bundle?.getParcelable("FILE") as FileDownloaded?
        if (fileDownload != null) {
            binding.fileDownloaded = fileDownload
        }

        binding.btnOk.setOnClickListener {
            super.onBackPressed()
        }

        val view = binding.root
        setContentView(view)
    }
}