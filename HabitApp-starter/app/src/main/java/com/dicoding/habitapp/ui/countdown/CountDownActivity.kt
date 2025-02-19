package com.dicoding.habitapp.ui.countdown

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.work.*
import com.dicoding.habitapp.R
import com.dicoding.habitapp.data.Habit
import com.dicoding.habitapp.notification.NotificationWorker
import com.dicoding.habitapp.utils.HABIT
import com.dicoding.habitapp.utils.HABIT_ID
import java.util.UUID

class CountDownActivity : AppCompatActivity() {

    private lateinit var viewModel: CountDownViewModel
    private var habit: Habit? = null
    private var isStopped = false
    private var workRequestId: UUID? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_count_down)
        supportActionBar?.title = "Count Down"

        habit = intent.getParcelableExtra(HABIT)

        habit?.let {
            findViewById<TextView>(R.id.tv_count_down_title).text = it.title

            viewModel = ViewModelProvider(this)[CountDownViewModel::class.java]
            viewModel.setInitialTime(it.minutesFocus.toLong())

            viewModel.currentTimeString.observe(this) { time ->
                findViewById<TextView>(R.id.tv_count_down).text = time
            }

            viewModel.eventCountDownFinish.observe(this) { isFinished ->
                if (isFinished && !isStopped) {
                    startNotificationWorker(it.id)
                }
                updateButtonState(!isFinished)
            }

            findViewById<Button>(R.id.btn_start).setOnClickListener {
                isStopped = false
                viewModel.startTimer()
                updateButtonState(true)
            }

            findViewById<Button>(R.id.btn_stop).setOnClickListener {
                isStopped = true
                viewModel.resetTimer()
                cancelNotificationWorker()
                updateButtonState(false)
            }
        } ?: run {
            Log.e("CountDownActivity", "Habit is null!")
            finish()
        }
    }

    private fun updateButtonState(isRunning: Boolean) {
        findViewById<Button>(R.id.btn_start).isEnabled = !isRunning
        findViewById<Button>(R.id.btn_stop).isEnabled = isRunning
    }

    private fun startNotificationWorker(habitId: Int) {
        val data = Data.Builder().putInt(HABIT_ID, habitId).build()

        val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInputData(data)
            .build()

        workRequestId = workRequest.id

        WorkManager.getInstance(this).enqueue(workRequest)
    }

    private fun cancelNotificationWorker() {
        workRequestId?.let {
            WorkManager.getInstance(this).cancelWorkById(it)
        }
    }
}
