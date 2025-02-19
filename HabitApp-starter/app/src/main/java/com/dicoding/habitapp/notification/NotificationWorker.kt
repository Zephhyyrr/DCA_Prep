package com.dicoding.habitapp.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.suspendCoroutine
import android.util.Log
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.dicoding.habitapp.R
import com.dicoding.habitapp.data.Habit
import com.dicoding.habitapp.data.HabitDatabase
import com.dicoding.habitapp.ui.detail.DetailHabitActivity
import com.dicoding.habitapp.ui.detail.DetailHabitActivity.Companion.EXTRA_HABIT_ID
import com.dicoding.habitapp.utils.HABIT_ID
import com.dicoding.habitapp.utils.NOTIFICATION_CHANNEL_ID
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class NotificationWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {

    private val habitId = inputData.getInt(HABIT_ID, -1)

    override suspend fun doWork(): Result {
        Log.d("NotificationWorker", "doWork() called with habitId: $habitId")
        if (habitId == -1) {
            Log.e("NotificationWorker", "Invalid habitId")
            return Result.failure()
        }

        val database = HabitDatabase.getInstance(applicationContext).habitDao()
        val habit = getHabitById(database.getHabitById(habitId)) ?: return Result.failure()

        Log.d("NotificationWorker", "Habit retrieved: ${habit.title}")
        showNotification(habit)
        return Result.success()
    }

    private suspend fun getHabitById(habitLiveData: LiveData<Habit>): Habit? {
        return withContext(Dispatchers.Main) {
            suspendCoroutine { continuation ->
                val observer = object : Observer<Habit?> {
                    override fun onChanged(value: Habit?) {
                        habitLiveData.removeObserver(this)
                        continuation.resume(value)
                    }
                }
                habitLiveData.observeForever(observer)
            }
        }
    }


    private fun showNotification(habit: Habit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ActivityCompat.checkSelfPermission(
                applicationContext,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e("NotificationWorker", "Notification permission not granted")
            return
        }

        val intent = Intent(applicationContext, DetailHabitActivity::class.java).apply {
            putExtra(EXTRA_HABIT_ID, habit.id)
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            habit.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Habit Reminder",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for habit reminders"
            }
            notificationManager.createNotificationChannel(channel)
        }

        createNotificationChannel(context = applicationContext)

        val notification = NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notifications)
            .setContentTitle(habit.title)
            .setContentText(applicationContext.getString(R.string.notify_content))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        NotificationManagerCompat.from(applicationContext).notify(habit.id, notification)
        Log.d("NotificationWorker", "Notification sent for habit: ${habit.title}")
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Habit Reminder",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for habbit"
            }
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}