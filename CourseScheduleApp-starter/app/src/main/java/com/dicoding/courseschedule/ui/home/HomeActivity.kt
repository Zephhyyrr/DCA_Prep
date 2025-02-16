package com.dicoding.courseschedule.ui.home

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.courseschedule.R
import com.dicoding.courseschedule.data.Course
import com.dicoding.courseschedule.data.DataRepository
import com.dicoding.courseschedule.ui.add.AddCourseActivity
import com.dicoding.courseschedule.ui.list.ListActivity
import com.dicoding.courseschedule.ui.setting.SettingsActivity
import com.dicoding.courseschedule.util.DayName
import com.dicoding.courseschedule.util.QueryType
import com.dicoding.courseschedule.util.timeDifference
import android.Manifest
import android.annotation.SuppressLint
import android.os.Build

//TODO 15 : Write UI test to validate when user tap Add Course (+) Menu, the AddCourseActivity is displayed
class HomeActivity : AppCompatActivity() {

    //TODO 5 : Show nearest schedule in CardHomeView and implement menu action
    private lateinit var viewModel: HomeViewModel
    private var queryType = QueryType.CURRENT_DAY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        supportActionBar?.title = resources.getString(R.string.today_schedule)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 0
                )
            }
        }

        val repository = DataRepository.getInstance(this)

        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                    return repository?.let { HomeViewModel(it) } as T
                }
                throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        })[HomeViewModel::class.java]

        viewModel.nearestSchedule.observe(this) { course ->
            showNearestSchedule(course)
        }

        viewModel.setQueryType(queryType)
    }

    private fun showNearestSchedule(course: Course?) {
        checkQueryType(course)
        course?.apply {
            val dayName = DayName.getByNumber(day)
            val time = String.format(getString(R.string.time_format), dayName, startTime, endTime)
            val remainingTime = timeDifference(day, startTime)

            val cardHome = findViewById<CardHomeView>(R.id.view_home)
            cardHome.apply {
                setCourseName(courseName)
                setTime(time)
                setRemainingTime(remainingTime)
                setLecturer(lecturer)
                setNote(note)
                visibility = View.VISIBLE
            }
        }

        findViewById<TextView>(R.id.tv_empty_home).visibility =
            if (course == null) View.VISIBLE else View.GONE
    }

    private fun checkQueryType(course: Course?) {
        if (course == null) {
            val newQueryType: QueryType = when (queryType) {
                QueryType.CURRENT_DAY -> QueryType.NEXT_DAY
                QueryType.NEXT_DAY -> QueryType.PAST_DAY
                else -> QueryType.CURRENT_DAY
            }
            viewModel.setQueryType(newQueryType)
            queryType = newQueryType
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val intent = when (item.itemId) {
            R.id.action_settings -> Intent(this, SettingsActivity::class.java)
            R.id.action_add -> Intent(this, AddCourseActivity::class.java)
            R.id.action_list -> Intent(this, ListActivity::class.java)
            else -> return super.onOptionsItemSelected(item)
        }

        startActivity(intent)
        return true
    }
}
