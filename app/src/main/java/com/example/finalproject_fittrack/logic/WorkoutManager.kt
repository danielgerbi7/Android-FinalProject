package com.example.finalproject_fittrack.logic

import com.example.finalproject_fittrack.R
import com.example.finalproject_fittrack.models.WorkoutModel

object WorkoutManager {

    private val workouts = mutableListOf<WorkoutModel>()

    init {
        loadWorkoutData()
    }

    private fun loadWorkoutData() {
        workouts.addAll(
            listOf(
                WorkoutModel.Builder()
                    .name("Chest Workout")
                    .description("Exercises to develop the chest muscles")
                    .imageRes(R.drawable.chest)
                    .category("Strength")
                    .caloriesBurned(250)
                    .duration(45)
                    .build(),

                WorkoutModel.Builder()
                    .name("Back Workout")
                    .description("Exercises to strengthen the back")
                    .imageRes(R.drawable.back)
                    .category("Strength")
                    .caloriesBurned(200)
                    .duration(40)
                    .build(),

                WorkoutModel.Builder()
                    .name("Shoulder Workout")
                    .description("Exercises for stronger shoulders")
                    .imageRes(R.drawable.shoulders)
                    .category("Strength")
                    .caloriesBurned(150)
                    .duration(35)
                    .build(),

                WorkoutModel.Builder()
                    .name("Leg Workout")
                    .description("Exercises to strengthen the legs")
                    .imageRes(R.drawable.legs)
                    .category("Strength")
                    .caloriesBurned(300)
                    .duration(50)
                    .build(),

                WorkoutModel.Builder()
                    .name("Abs Workout")
                    .description("Exercises to strengthen the core")
                    .imageRes(R.drawable.abs)
                    .category("Strength")
                    .caloriesBurned(100)
                    .duration(30)
                    .build(),

                WorkoutModel.Builder()
                    .name("Arms Workout")
                    .description("Exercises for biceps and triceps")
                    .imageRes(R.drawable.arms)
                    .category("Strength")
                    .caloriesBurned(150)
                    .duration(30)
                    .build(),

                WorkoutModel.Builder()
                    .name("Jump Rope")
                    .description("High-intensity cardio workout")
                    .imageRes(R.drawable.jump_rope)
                    .category("Cardio")
                    .caloriesBurned(200)
                    .duration(20)
                    .build(),

                WorkoutModel.Builder()
                    .name("Spinning")
                    .description("Indoor cycling exercise")
                    .imageRes(R.drawable.spinning)
                    .category("Cardio")
                    .caloriesBurned(300)
                    .duration(40)
                    .build(),

                WorkoutModel.Builder()
                    .name("Ski Machine")
                    .description("Simulated skiing exercise")
                    .imageRes(R.drawable.ski)
                    .category("Cardio")
                    .caloriesBurned(250)
                    .duration(35)
                    .build(),

                WorkoutModel.Builder()
                    .name("Running")
                    .description("Cardiovascular endurance training")
                    .imageRes(R.drawable.running)
                    .category("Cardio")
                    .caloriesBurned(400)
                    .duration(60)
                    .build(),

                WorkoutModel.Builder()
                    .name("Escalate")
                    .description("Simulated stair climbing")
                    .imageRes(R.drawable.escalate)
                    .category("Cardio")
                    .caloriesBurned(350)
                    .duration(45)
                    .build(),

                WorkoutModel.Builder()
                    .name("Rowing")
                    .description("Full-body workout using a rowing machine")
                    .imageRes(R.drawable.rowing)
                    .category("Cardio")
                    .caloriesBurned(300)
                    .duration(40)
                    .build()
            )
        )
    }

    fun getWorkoutsByCategory(category: String): List<WorkoutModel> =
        workouts.filter { it.category == category }


    fun getFavoriteWorkouts(): List<WorkoutModel> = workouts.filter { it.isFavorite }

    fun updateFavoriteStatus(workout: WorkoutModel) {
        workout.isFavorite = !workout.isFavorite
    }
}
