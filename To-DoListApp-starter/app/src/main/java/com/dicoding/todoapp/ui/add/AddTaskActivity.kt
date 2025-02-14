package com.dicoding.todoapp.ui.add

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.todoapp.R
import com.dicoding.todoapp.data.Task
import com.dicoding.todoapp.ui.ViewModelFactory
import com.dicoding.todoapp.utils.DatePickerFragment
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*

class AddTaskActivity : AppCompatActivity(), DatePickerFragment.DialogDateListener {

    private val viewModel: AddTaskViewModel by viewModels {
        ViewModelFactory.getInstance(applicationContext)
    }

    private var dueDateMillis: Long = System.currentTimeMillis()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        viewModel.isTaskInserted.observe(this, { isInserted ->
            if (isInserted) {
                Snackbar.make(findViewById(R.id.add_layout), "Task added", Snackbar.LENGTH_SHORT)
                    .show()
                finish()
            } else {
                Snackbar.make(
                    findViewById(R.id.add_layout),
                    "Error adding task",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        })

        supportActionBar?.title = getString(R.string.add_task)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_add, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_save -> {
                saveTask()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    fun showDatePicker(view: View) {
        val dialogFragment = DatePickerFragment()
        dialogFragment.show(supportFragmentManager, "datePicker")
    }

    override fun onDialogDateSet(tag: String?, year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        findViewById<TextView>(R.id.add_tv_due_date).text = dateFormat.format(calendar.time)

        dueDateMillis = calendar.timeInMillis
    }

    private fun saveTask() {
        val title = findViewById<EditText>(R.id.add_ed_title).text.toString()
        val description = findViewById<EditText>(R.id.add_ed_description).text.toString()

        if (title.isNotEmpty() && description.isNotEmpty()) {
            val newTask = Task(
                id = 0,
                title = title,
                description = description,
                dueDateMillis = (dueDateMillis / 1000).toInt(),
                isCompleted = 0
            )

            viewModel.insertTask(newTask)

            Snackbar.make(findViewById(R.id.add_layout), "Task added", Snackbar.LENGTH_SHORT).show()

            finish()
        } else {
            Snackbar.make(
                findViewById(R.id.add_layout),
                "Title and description cannot be empty",
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }
}
