package com.example.finalproject_fittrack.models

data class WorkoutModel(
    val name: String,
    val description: String,
    val imageRes: Int,
    val category: String,
    val caloriesBurned: Int,
    var isFavorite: Boolean = false
)
