package com.example.rmaprojekt

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.rmaprojekt.ui.theme.RMAprojektTheme
import com.example.rmaprojekt.workers.AlbumReminderWorker
import com.google.firebase.FirebaseApp
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import java.time.Duration

class MainActivity : ComponentActivity() {

    private val requestNotificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val authViewModel : AuthViewModel by viewModels()
        val modelsViewModel : ModelsViewModel by viewModels()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermission.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
        scheduleDailyReminder(
            15,
            0,
            "Start your origami day! Have you added today's photo?"
        )

        scheduleDailyReminder(
            18,
            0,
            "Evening reminder: don't forget to capture your origami progress!"
        )

        scheduleDailyReminder(
            21,
            0,
            "Last chance today! Save your origami memory before the day ends."
        )


        setContent {
            RMAprojektTheme {
                AppBackground {
                    Scaffold(modifier = Modifier.fillMaxSize(),
                            containerColor = Color.Transparent) {
                        innerPadding ->
                        MyAppNavigation(modifier = Modifier.padding(innerPadding)
                            ,authViewModel = authViewModel
                            ,modelsViewModel = modelsViewModel)
                    }
                }
            }
        }
    }

    private fun scheduleDailyReminder(
        hour: Int,
        minute: Int,
        message: String
    ) {
        val now = LocalDateTime.now()
        var target = now.withHour(hour).withMinute(minute).withSecond(0)

        if (target.isBefore(now)) {
            target = target.plusDays(1)
        }

        val delay = Duration.between(now, target).toMillis()

        val workRequest =
            PeriodicWorkRequestBuilder<AlbumReminderWorker>(
                24,
                TimeUnit.HOURS
            )
                .setInputData(
                    androidx.work.workDataOf(
                        "MESSAGE" to message
                    )
                )
                .setInitialDelay(
                    delay,
                    TimeUnit.MILLISECONDS
                )
                .build()

        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(
                "album_reminder_${hour}_${minute}",
                ExistingPeriodicWorkPolicy.UPDATE,
                workRequest
            )
    }


}

@Composable
fun AppBackground(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) {
        Image(
            painter = painterResource(id = R.drawable.background2),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        content()
    }
}




