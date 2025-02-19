package com.dicoding.habitapp.ui.detail

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.dicoding.habitapp.R
import com.dicoding.habitapp.data.Habit
import com.dicoding.habitapp.ui.ViewModelFactory
import com.dicoding.habitapp.ui.countdown.CountDownActivity
import com.dicoding.habitapp.utils.HABIT
import com.dicoding.habitapp.utils.HABIT_ID
import com.google.android.material.appbar.CollapsingToolbarLayout

class DetailHabitActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_HABIT_ID = "extra_habit_id"
    }

    private lateinit var selectedHabit: Habit
    private val viewModel: DetailHabitViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_habit)
        setSupportActionBar(findViewById(R.id.toolbar))

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val habitId = intent.getIntExtra(EXTRA_HABIT_ID, -1)
        if (habitId == -1) {
            finish() // Jika ID tidak valid, tutup activity
            return
        }

        viewModel.start(habitId) // Ambil data habit berdasarkan ID


        viewModel.habit.observe(this) { habit ->
            habit?.let {
                selectedHabit = it // Simpan objek Habit yang didapat dari ViewModel
                updateUI(it)
            }
        }



        findViewById<Button>(R.id.btn_open_count_down).setOnClickListener {
            val intent = Intent(this, CountDownActivity::class.java)
            intent.putExtra(HABIT, selectedHabit)
            startActivity(intent)
        }
    }

    private fun updateUI(habit: Habit) {
        findViewById<CollapsingToolbarLayout>(R.id.toolbar_layout).title = habit.title
        findViewById<EditText>(R.id.detail_ed_time_minutes).setText(habit.minutesFocus.toString())
        findViewById<EditText>(R.id.detail_ed_start_time).setText(habit.startTime)

        when (habit.priorityLevel) {
            resources.getStringArray(R.array.priority_level)[0] -> {
                findViewById<View>(R.id.detail_priority_level).setBackgroundColor(
                    ContextCompat.getColor(this, R.color.red)
                )
            }
            resources.getStringArray(R.array.priority_level)[1] -> {
                findViewById<View>(R.id.detail_priority_level).setBackgroundColor(
                    ContextCompat.getColor(this, R.color.yellow)
                )
            }
            else -> {
                findViewById<View>(R.id.detail_priority_level).setBackgroundColor(
                    ContextCompat.getColor(this, R.color.green)
                )
            }
        }
    }
}