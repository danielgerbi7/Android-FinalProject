package com.example.finalproject_fittrack.interfaces

import com.example.finalproject_fittrack.models.Workout


interface WorkoutProgressCallback {
    fun onStartWorkout(workout: Workout, position: Int)
}