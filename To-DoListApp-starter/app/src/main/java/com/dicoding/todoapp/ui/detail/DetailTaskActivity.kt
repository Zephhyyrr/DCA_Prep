package com.dicoding.todoapp.ui.detail

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.todoapp.R
import com.dicoding.todoapp.data.Task
import com.dicoding.todoapp.ui.ViewModelFactory
import com.dicoding.todoapp.utils.DateConverter
import com.dicoding.todoapp.utils.DatePickerFragment
import com.dicoding.todoapp.utils.TASK_ID
import com.google.android.material.snackbar.Snackbar

class DetailTaskActivity : AppCompatActivity(), DatePickerFragment.DialogDateListener {

    private val viewModel: DetailTaskViewModel by viewModels {
        ViewModelFactory.getInstance(application)
    }

    private var task: Task? = null
    private lateinit var dueDateEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_detail)

        dueDateEditText = findViewById(R.id.detail_ed_due_date)
        dueDateEditText.isFocusable = false
        dueDateEditText.setOnClickListener {
            val datePickerFragment = DatePickerFragment()
            datePickerFragment.show(supportFragmentManager, "datePicker")
        }

        val taskId = intent.getIntExtra(TASK_ID, -1)
        if (taskId != -1) viewModel.setTaskId(taskId)

        viewModel.task.observe(this) { taskData ->
            if (taskData == null) {
                finish()
            } else {
                task = taskData
                findViewById<EditText>(R.id.detail_ed_title).setText(taskData.title)
                findViewById<EditText>(R.id.detail_ed_description).setText(taskData.description)
                dueDateEditText.setText(
                    DateConverter.convertMillisToString(taskData.dueDateMillis)
                )
            }
        }

        // Pindahkan setOnClickListener di sini
        findViewById<Button>(R.id.btn_delete_task).setOnClickListener {
            task?.let {
                viewModel.deleteTask()
                Snackbar.make(
                    findViewById(R.id.detail_layout),
                    "Task deleted",
                    Snackbar.LENGTH_SHORT
                ).show()
                finish()
            } ?: Snackbar.make(
                findViewById(R.id.detail_layout),
                "No task to delete",
                Snackbar.LENGTH_SHORT
            ).show()
        }
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

    private fun saveTask() {
        val title = findViewById<EditText>(R.id.detail_ed_title).text.toString()
        val description = findViewById<EditText>(R.id.detail_ed_description).text.toString()
        val dueDateString = dueDateEditText.text.toString()

        if (task != null && title.isNotEmpty() && description.isNotEmpty() && dueDateString.isNotEmpty()) {
            val updatedTask = task!!.copy(
                title = title,
                description = description,
                dueDateMillis = convertDateToMillis(dueDateString)
            )
            viewModel.updateTask(updatedTask)

            Snackbar.make(
                findViewById(R.id.detail_layout),
                "Task updated",
                Snackbar.LENGTH_SHORT
            ).show()

            finish()
        } else {
            Snackbar.make(
                findViewById(R.id.detail_layout),
                "All fields must be filled",
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    private fun convertDateToMillis(dateString: String): Int {
        val sdf = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
        val date = sdf.parse(dateString)
        return (date?.time?.div(1000L))?.toInt() ?: 0
    }

    override fun onDialogDateSet(tag: String?, year: Int, month: Int, dayOfMonth: Int) {
        val calendar = java.util.Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)
        val sdf = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
        dueDateEditText.setText(sdf.format(calendar.time))
    }
}
