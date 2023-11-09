package com.example.todolist

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat


class ForegroundService:Service() {

    private var CHANNEL_ID: String = "1001"

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        when(intent?.getStringExtra("COMMAND")){
            "START" -> {
                Log.e("SERVICE", "COMMAND : ${intent?.getStringExtra("COMMAND")}")
                implementNotificationChannel(intent)
            }
            "STOP" -> {
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }

        return START_NOT_STICKY
    }

    private fun implementNotificationChannel(intent: Intent) {

        val notificationChannel:NotificationChannel = NotificationChannel(
            CHANNEL_ID,
            "ScheduleTasks",
            NotificationManager.IMPORTANCE_HIGH
        )

        notificationChannel.description = "ScheduleTasks"

//        notificationChannel.setSound(
//            Uri.parse("android.resource://${packageName}/${R.raw.schedule_tasks}"), AudioAttributes.Builder()
//                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
//                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
//                .build()
//        )

        val notificationManager:NotificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(notificationChannel)

        // firstly we did startForeground() but since Android 8.0 (API 26) we must set Notification Channel
        val notification:Notification = NotificationCompat.Builder(this,CHANNEL_ID)
            .setContentTitle("Time to schedule tasks")
            .setContentText("Enjoy your day!")
            .setSmallIcon(R.mipmap.app_image)
            .setContentIntent(PendingIntent.getActivity(    // used because, when user click's to the notification than he may be directed to app's activity
                this,
                0,
                Intent(this,TabLayoutActivity::class.java),
                PendingIntent.FLAG_MUTABLE)
            )
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setAutoCancel(true)
            .build()

        startForeground(1001,notification)
    }


    override fun onDestroy() {
        Log.e("SERVICE", "COMMAND: Destroy STOP!")
        super.onDestroy()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}