package com.example.finalproject_fittrack.interfaces

import com.example.finalproject_fittrack.models.WorkoutModel

interface WorkoutFavoriteCallback {
    fun onFavoriteClicked(workout: WorkoutModel, position: Int)
}