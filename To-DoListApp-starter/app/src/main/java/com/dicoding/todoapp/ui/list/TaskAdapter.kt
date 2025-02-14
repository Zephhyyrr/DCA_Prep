package com.dicoding.todoapp.ui.list

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.todoapp.R
import com.dicoding.todoapp.data.Task
import com.dicoding.todoapp.ui.detail.DetailTaskActivity
import com.dicoding.todoapp.utils.DateConverter
import com.dicoding.todoapp.utils.TASK_ID

class TaskAdapter(
    private val onCheckedChange: (Task, Boolean) -> Unit
) : PagingDataAdapter<Task, TaskAdapter.TaskViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_item, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        getItem(position)?.let { task ->
            holder.bind(task)

            when {
                task.isCompleted == 1 -> {
                    holder.tvTitle.state = TaskTitleView.DONE
                    holder.cbComplete.isChecked = true
                }

                task.dueDateMillis * 1000L < System.currentTimeMillis() -> {
                    holder.tvTitle.state = TaskTitleView.OVERDUE
                    holder.cbComplete.isChecked = false
                }

                else -> {
                    holder.tvTitle.state = TaskTitleView.NORMAL
                    holder.cbComplete.isChecked = false
                }
            }
        }
    }

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TaskTitleView = itemView.findViewById(R.id.item_tv_title)
        val cbComplete: CheckBox = itemView.findViewById(R.id.item_checkbox)
        private val tvDueDate: TextView = itemView.findViewById(R.id.item_tv_date)

        var currentTask: Task? = null

        fun bind(task: Task) {
            currentTask = task
            tvTitle.text = task.title
            tvDueDate.text = DateConverter.convertMillisToString(task.dueDateMillis)

            itemView.setOnClickListener {
                val detailIntent = Intent(itemView.context, DetailTaskActivity::class.java)
                detailIntent.putExtra(TASK_ID, task.id)
                itemView.context.startActivity(detailIntent)
            }

            cbComplete.setOnCheckedChangeListener(null)
            cbComplete.isChecked = task.isCompleted == 1
            cbComplete.setOnCheckedChangeListener { _, isChecked ->
                onCheckedChange(task, isChecked)
            }
        }

    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Task>() {
            override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
                return oldItem == newItem
            }
        }
    }
}
