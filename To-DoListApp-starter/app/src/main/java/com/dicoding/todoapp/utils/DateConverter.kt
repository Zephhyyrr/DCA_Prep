package com.dicoding.todoapp.utils

import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

object DateConverter {
    fun convertMillisToString(timeMillis: Int): String {
        Log.d("DateConverter", "Millis received: $timeMillis")
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeMillis * 1000L
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(calendar.time)
    }
}
