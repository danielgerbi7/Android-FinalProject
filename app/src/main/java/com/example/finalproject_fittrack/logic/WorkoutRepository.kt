package com.example.finalproject_fittrack.logic

import com.example.finalproject_fittrack.R
import com.example.finalproject_fittrack.models.WorkoutModel

object WorkoutRepository {

    private val workouts = mutableListOf<WorkoutModel>()

    init {
        loadWorkoutData()
    }

    private fun loadWorkoutData() {
        workouts.addAll(
            listOf(
                WorkoutModel("Chest Workout", "Exercises to develop the chest muscles", R.drawable.chest, "Strength", 250),
                WorkoutModel("Back Workout", "Exercises to strengthen the back", R.drawable.back, "Strength", 200),
                WorkoutModel("Shoulder Workout", "Exercises for stronger shoulders", R.drawable.shoulders, "Strength", 150),
                WorkoutModel("Leg Workout", "Exercises to strengthen the legs", R.drawable.legs, "Strength", 300),
                WorkoutModel("Abs Workout", "Exercises to strengthen the core", R.drawable.abs, "Strength", 100),
                WorkoutModel("Arms Workout", "Exercises for biceps and triceps", R.drawable.arms, "Strength", 150),

                WorkoutModel("Jump Rope", "High-intensity cardio workout", R.drawable.jump_rope, "Cardio", 200),
                WorkoutModel("Spinning", "Indoor cycling exercise", R.drawable.spinning, "Cardio", 300),
                WorkoutModel("Ski Machine", "Simulated skiing exercise", R.drawable.ski, "Cardio", 250),
                WorkoutModel("Running", "Cardiovascular endurance training", R.drawable.running, "Cardio", 400),
                WorkoutModel("Escalate", "Simulated stair climbing", R.drawable.escalate, "Cardio", 350),
                WorkoutModel("Rowing", "Full-body workout using a rowing machine", R.drawable.rowing, "Cardio", 300)
            )
        )
    }

    fun getWorkoutsByCategory(category: String): List<WorkoutModel> = workouts.filter { it.category == category }

    fun getAllWorkouts(): List<WorkoutModel> = workouts

    fun getFavoriteWorkouts(): List<WorkoutModel> = workouts.filter { it.isFavorite }

    fun updateFavoriteStatus(workout: WorkoutModel) {
        workout.isFavorite = !workout.isFavorite
    }
}
