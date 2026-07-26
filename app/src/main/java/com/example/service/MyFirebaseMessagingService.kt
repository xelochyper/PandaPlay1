package com.example.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed FCM Registration Token: $token")
        // Store or send token to backend server if needed
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "From: ${remoteMessage.from}")

        var title: String? = null
        var body: String? = null
        var type: String? = "general"
        var itemId: String? = null

        // Check payload from notification object
        remoteMessage.notification?.let {
            title = it.title
            body = it.body
        }

        // Check payload from data map (overrides or complements notification payload)
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
            if (title.isNullOrEmpty()) {
                title = remoteMessage.data["title"] ?: remoteMessage.data["subject"]
            }
            if (body.isNullOrEmpty()) {
                body = remoteMessage.data["body"] ?: remoteMessage.data["message"]
            }
            type = remoteMessage.data["type"] ?: type
            itemId = remoteMessage.data["item_id"]
        }

        val notificationTitle = title ?: when (type) {
            "status_update" -> "Pembaruan Status Akun"
            "content_drop" -> "MOD & Livery Baru Rilis!"
            else -> "Notifikasi Panda Play"
        }

        val notificationBody = body ?: when (type) {
            "status_update" -> "Status akun Panda Play Anda telah diperbarui."
            "content_drop" -> "Koleksi MOD & Livery BUSSID terbaru telah tersedia untuk didownload."
            else -> "Buka aplikasi Panda Play untuk informasi selengkapnya."
        }

        sendNotification(notificationTitle, notificationBody, type, itemId)
    }

    private fun sendNotification(
        title: String,
        body: String,
        type: String?,
        itemId: String?
    ) {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            putExtra("notification_type", type)
            putExtra("item_id", itemId)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            System.currentTimeMillis().toInt(),
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = CHANNEL_ID
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "Notifikasi Panda Play"
            val channelDescription = "Notifikasi status akun dan rilis MOD/Livery baru"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = channelDescription
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentIntent(pendingIntent)

        val notificationId = System.currentTimeMillis().toInt()
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    companion object {
        private const val TAG = "PandaFCMService"
        const val CHANNEL_ID = "panda_play_fcm_channel"

        fun createNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = "Notifikasi Panda Play"
                val descriptionText = "Notifikasi status akun dan rilis MOD/Livery baru"
                val importance = NotificationManager.IMPORTANCE_HIGH
                val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                    description = descriptionText
                    enableVibration(true)
                }
                val notificationManager: NotificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
        }
    }
}
