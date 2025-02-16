package com.dicoding.courseschedule.ui.add

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.dicoding.courseschedule.R
import com.dicoding.courseschedule.data.DataRepository
import com.dicoding.courseschedule.util.TimePickerFragment
import com.google.android.material.snackbar.Snackbar

class AddCourseActivity : AppCompatActivity(), TimePickerFragment.DialogTimeListener {

    private var selectedDay: Int = 1
    private var startTime: String = "08:00"
    private var endTime: String = "09:00"

    private lateinit var viewModel: AddCourseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_course)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            window.insetsController?.setSystemBarsAppearance(
                0,
                android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = 0
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.purple_700)


        val repository = DataRepository.getInstance(applicationContext)!!
        viewModel = ViewModelProvider(
            this,
            AddCourseViewModelFactory(repository)
        )[AddCourseViewModel::class.java]

        setupSpinner()
        setupTimePicker()

        viewModel.saved.observe(this) { event ->
            event.getContentIfNotHandled()?.let { isSaved ->
                if (isSaved) {
                    Snackbar.make(
                        findViewById(android.R.id.content),
                        "Course Saved",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    finish()
                } else {
                    Snackbar.make(
                        findViewById(android.R.id.content),
                        "Please fill in all fields",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun setupSpinner() {
        val daySpinner = findViewById<Spinner>(R.id.spinner_day)
        val dayAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            resources.getStringArray(R.array.day)
        )
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        daySpinner.adapter = dayAdapter

        daySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: android.view.View?,
                position: Int,
                id: Long
            ) {
                selectedDay = position
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupTimePicker() {
        findViewById<ImageButton>(R.id.ib_start_time).setOnClickListener {
            val dialogFragment = TimePickerFragment()
            dialogFragment.show(supportFragmentManager, "StartTime")
        }

        findViewById<ImageButton>(R.id.ib_end_time).setOnClickListener {
            val dialogFragment = TimePickerFragment()
            dialogFragment.show(supportFragmentManager, "EndTime")
        }
    }

    @SuppressLint("DefaultLocale")
    override fun onDialogTimeSet(tag: String?, hour: Int, minute: Int) {
        val time = String.format("%02d:%02d", hour, minute)
        when (tag) {
            "StartTime" -> {
                startTime = time
                findViewById<TextView>(R.id.tv_start_time).text = startTime
            }

            "EndTime" -> {
                endTime = time
                findViewById<TextView>(R.id.tv_end_time).text = endTime
            }
        }
    }

    private fun saveCourse() {
        val courseName = findViewById<EditText>(R.id.ed_course_name).text.toString().trim()
        val lecturer = findViewById<EditText>(R.id.ed_lecturer).text.toString().trim()
        val note = findViewById<EditText>(R.id.ed_note).text.toString().trim()

        viewModel.insertCourse(courseName, selectedDay, startTime, endTime, lecturer, note)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_insert -> {
                saveCourse()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}
