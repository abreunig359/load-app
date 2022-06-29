package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0
    private var selectedFileName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_button.setOnClickListener {
            Toast.makeText(
                applicationContext,
                R.string.select_download_file_message,
                Toast.LENGTH_SHORT
            ).show()
        }

        handleRadioButtonSelected()
        createChannel()
    }

    private fun createChannel() {
        val notificationChannel =
            NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                enableVibration(true)
                description = CHANNEL_NAME
                setShowBadge(false)
            }

        val notificationManager = getSystemService(
            NotificationManager::class.java
        )
        notificationManager.createNotificationChannel(notificationChannel)
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            val status = getDownloadStatus(id!!)

            custom_button.isEnabled = true
            custom_button.changeButtonState(ButtonState.Completed)

            sendNotification(fileName = selectedFileName, status = status)
        }
    }

    private fun getDownloadStatus(id: Long): String {
        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        val query = DownloadManager.Query()

        query.setFilterById(id)

        val cursor = downloadManager.query(query)

        if (cursor.moveToFirst()) {
            val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
            return when (status) {
                DownloadManager.STATUS_SUCCESSFUL -> getString(R.string.download_status_success)
                DownloadManager.STATUS_FAILED -> getString(R.string.download_status_fail)
                else -> getString(R.string.download_status_unknown)
            }
        }
        return getString(R.string.download_status_unknown)
    }

    private fun handleRadioButtonSelected() {
        download_links_radio.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radio_glide -> setDownloadOnClickListener(
                    GLIDE_URL,
                    getString(R.string.glide_radio_title)
                )
                R.id.radio_retrofit -> setDownloadOnClickListener(
                    RETROFIT_URL,
                    getString(R.string.retrofit_radio_title)
                )
                R.id.radio_udacity -> setDownloadOnClickListener(
                    UDACITY_URL,
                    getString(R.string.load_app_radio_title)
                )
            }
        }
    }

    private fun setDownloadOnClickListener(url: String, fileName: String) {
        custom_button.setOnClickListener {
            download(url)
            custom_button.changeButtonState(ButtonState.Loading)
        }
        selectedFileName = fileName
    }

    private fun download(url: String) {
        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
        Log.i("MainActivity", "download triggered")
    }

    private fun sendNotification(fileName: String, status: String) {
        val notificationManager = ContextCompat.getSystemService(
            applicationContext,
            NotificationManager::class.java
        ) as NotificationManager
        notificationManager.sendNotification(
            fileName = fileName,
            status = status,
            applicationContext
        )
    }

    companion object {

        private const val GLIDE_URL = "https://github.com/bumptech/glide/archive/master.zip"
        private const val UDACITY_URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val RETROFIT_URL = "https://github.com/square/retrofit/archive/master.zip"
        const val CHANNEL_ID = "channelId"
        const val CHANNEL_NAME = "Downloads"
        const val NOTIFICATION_ID = 0
    }
}
