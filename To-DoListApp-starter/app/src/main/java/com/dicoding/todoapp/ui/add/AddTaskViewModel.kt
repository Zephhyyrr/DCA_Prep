package com.dicoding.todoapp.ui.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.todoapp.data.Task
import com.dicoding.todoapp.data.TaskRepository
import kotlinx.coroutines.launch

class AddTaskViewModel(private val taskRepository: TaskRepository) : ViewModel() {

    private val _isTaskInserted = MutableLiveData<Boolean>()
    val isTaskInserted: LiveData<Boolean> get() = _isTaskInserted

    fun insertTask(task: Task) {
        viewModelScope.launch {
            try {
                taskRepository.insertTask(task)
                _isTaskInserted.value = true
            } catch (e: Exception) {
                _isTaskInserted.value = false
            }
        }
    }
}
