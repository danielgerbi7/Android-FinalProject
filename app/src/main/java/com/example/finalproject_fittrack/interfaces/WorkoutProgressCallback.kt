package com.example.finalproject_fittrack.interfaces

import com.example.finalproject_fittrack.models.WorkoutModel


interface WorkoutProgressCallback {
    fun onStartWorkout(workout: WorkoutModel, position: Int)
}