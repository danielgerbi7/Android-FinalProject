package com.example.finalproject_fittrack.interfaces

import WorkoutModel


interface WorkoutFavoriteCallback {
    fun onFavoriteClicked(workout: WorkoutModel, position: Int)
}