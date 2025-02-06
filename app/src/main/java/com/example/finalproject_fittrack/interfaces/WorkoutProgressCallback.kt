package com.example.finalproject_fittrack.interfaces

import WorkoutModel


interface WorkoutProgressCallback {
    fun onStartWorkout(workout: WorkoutModel, position: Int)
}