package com.example.finalproject_fittrack.interfaces

import com.example.finalproject_fittrack.models.WorkoutModel

interface WorkoutCallback {
    fun onFavoriteClicked(workout: WorkoutModel, position: Int)
    fun onStartWorkout(workout: WorkoutModel, position: Int)
}