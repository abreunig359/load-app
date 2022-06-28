package com.udacity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.app.DownloadManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

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
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            download_progress_bar.visibility = View.INVISIBLE
            download_progress_text.visibility = View.INVISIBLE
            custom_button.isEnabled = true

            Log.i("MainActivity", "finished download")
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
                R.id.radio_glide -> setDownloadOnClickListener(GLIDE_URL)
                R.id.radio_retrofit -> setDownloadOnClickListener(RETROFIT_URL)
                R.id.radio_udacity -> setDownloadOnClickListener(UDACITY_URL)
            }
        }
    }

    private fun setDownloadOnClickListener(url: String) {
        custom_button.setOnClickListener {
            download_progress_bar.visibility = View.VISIBLE
            download_progress_text.visibility = View.VISIBLE
            val progressAnimator = ObjectAnimator.ofInt(download_progress_bar, "progress", 0, 100)
            progressAnimator.duration = LOADING_ANIMATION_DURATION_MS
            progressAnimator.disableViewOnAnimationStart(it)
            progressAnimator.start()
            download(url)
        }
    }

    private fun download(url: String) {
        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
        Log.i("MainActivity", "download triggered")
    }

    private fun navigateToDetailsActivity(fileName: String, status: String) {
        val switchActivityIntent = Intent(this, DetailActivity::class.java)
        switchActivityIntent.putExtra(FILE_NAME_KEY, fileName)
        switchActivityIntent.putExtra(STATUS_KEY, status)
        startActivity(switchActivityIntent)
    }

    private fun ObjectAnimator.disableViewOnAnimationStart(view: View) {
        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                super.onAnimationStart(animation)
                view.isEnabled = false
            }
        })
    }

    companion object {

        private const val GLIDE_URL = "https://github.com/bumptech/glide/archive/master.zip"
        private const val UDACITY_URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val RETROFIT_URL = "https://github.com/square/retrofit/archive/master.zip"
        private const val CHANNEL_ID = "channelId"

        private const val LOADING_ANIMATION_DURATION_MS = 2000L
    }
}

private enum class DownloadUrl(val url: String) {

}
