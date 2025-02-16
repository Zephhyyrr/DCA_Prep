package com.dicoding.courseschedule.ui.list

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.courseschedule.data.Course
import com.dicoding.courseschedule.data.DataRepository
import com.dicoding.courseschedule.util.SortType

class ListViewModel(private val repository: DataRepository) : ViewModel() {

    private val _sortParams = MutableLiveData<SortType>()

    init {
        _sortParams.value = SortType.TIME
    }

    val courses: LiveData<PagingData<Course>> = _sortParams.switchMap { sortType ->
        repository.getAllCourse(sortType).cachedIn(viewModelScope)
    }

    fun sort(newValue: SortType) {
        _sortParams.value = newValue
    }

    fun delete(course: Course) {
        repository.delete(course)
    }
}
