package com.dicoding.todoapp.ui.detail

import androidx.lifecycle.*
import com.dicoding.todoapp.data.Task
import com.dicoding.todoapp.data.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailTaskViewModel(private val taskRepository: TaskRepository) : ViewModel() {
    private val _taskId = MutableLiveData<Int?>()
    private val _task = _taskId.switchMap { id ->
        if (id != null) taskRepository.getTaskById(id) else MutableLiveData()
    }
    val task: LiveData<Task> = _task

    fun setTaskId(taskId: Int) {
        if (taskId != _taskId.value) _taskId.value = taskId
    }

    fun deleteTask() {
        viewModelScope.launch(Dispatchers.IO) {
            _task.value?.let { taskRepository.deleteTask(it) }
            _taskId.postValue(null)
        }
    }
    fun updateTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            taskRepository.updateTask(task)
        }
    }
}
