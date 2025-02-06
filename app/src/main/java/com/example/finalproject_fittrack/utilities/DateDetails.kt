package com.example.finalproject_fittrack.utilities

import java.text.SimpleDateFormat
import java.util.*

object DateDetails {
    fun getTodayDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }
}
