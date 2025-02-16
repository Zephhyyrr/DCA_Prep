package com.dicoding.courseschedule.ui.home

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.courseschedule.data.Course
import com.dicoding.courseschedule.data.DataRepository
import com.dicoding.courseschedule.util.QueryType

class HomeViewModel(private val repository: DataRepository) : ViewModel() {

    private val _queryType = MutableLiveData<QueryType>()
    val nearestSchedule = MediatorLiveData<Course?>()

    init {
        _queryType.value = QueryType.CURRENT_DAY
        nearestSchedule.addSource(_queryType) { queryType ->
            nearestSchedule.addSource(repository.getNearestSchedule(queryType)) { course ->
                nearestSchedule.value = course
            }
        }
    }

    fun setQueryType(queryType: QueryType) {
        _queryType.value = queryType
    }
}

