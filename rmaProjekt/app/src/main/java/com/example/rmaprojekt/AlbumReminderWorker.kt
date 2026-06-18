package com.example.rmaprojekt.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.rmaprojekt.R
import com.example.rmaprojekt.storage.AlbumStorage
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth


class AlbumReminderWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {

        val context = applicationContext

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return Result.success()
        val albums = AlbumStorage.loadAlbums(context, userId)

        val today = LocalDate.now()

        albums.forEach { album ->

            val start = Instant.ofEpochMilli(album.startDate)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()

            val dayIndex = ChronoUnit.DAYS.between(start, today).toInt()

            if (dayIndex in 0 until album.durationDays) {
                val hasPhoto = album.photos.any { it.dayIndex == dayIndex }

                if (!hasPhoto) {

                    val message =
                        inputData.getString("MESSAGE")
                            ?: "Don't forget your origami photo today!"

                    sendNotification(message)

                }
            }
        }

        return Result.success()
    }

    private fun sendNotification(message: String) {
        val context = applicationContext

        createNotificationChannel(context)

        val notification = NotificationCompat.Builder(context, "album_reminders")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Origami Album Reminder")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context)
            .notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "album_reminders",
                "Album Reminders",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}
