package com.example.becure.ui.activities
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

class ReminderWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        // Bildirim gönder
        sendNotification("Don't forget your appointment!")
        return Result.success()
    }

    private fun sendNotification(message: String) {
        val channelId = "reminder_channel"
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Bildirim kanalı oluşturma (Android 8 ve üzeri için)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Reminder Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Bildirim oluşturma
        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle("Reminder")
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        // Bildirimi göster
        notificationManager.notify(1, notification)
    }
}
