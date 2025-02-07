package com.example.finalproject_fittrack.interfaces

import com.example.finalproject_fittrack.models.Workout



interface WorkoutFavoriteCallback {
    fun onFavoriteClicked(workout: Workout, position: Int)
}