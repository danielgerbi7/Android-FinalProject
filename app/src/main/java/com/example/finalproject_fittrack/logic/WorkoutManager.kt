package com.example.finalproject_fittrack.logic

import android.content.Context
import android.content.SharedPreferences
import com.example.finalproject_fittrack.App
import com.example.finalproject_fittrack.R
import com.example.finalproject_fittrack.models.WorkoutModel
import com.google.common.reflect.TypeToken
import com.google.gson.Gson


object WorkoutManager {

    private val workouts = mutableListOf<WorkoutModel>()
    private var activeWorkout: WorkoutModel? = null
    private lateinit var sharedPreferences: SharedPreferences

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences("FitTrackPrefs", Context.MODE_PRIVATE)
        loadWorkoutData()
        loadFavoriteWorkouts()
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
        saveFavoriteWorkouts()
    }

    private fun saveFavoriteWorkouts() {
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(getFavoriteWorkouts())
        editor.putString("favorite_workouts", json)
        editor.apply()
    }

    private fun loadFavoriteWorkouts() {
        val gson = Gson()
        val json = sharedPreferences.getString("favorite_workouts", null)
        val type = object : TypeToken<List<WorkoutModel>>() {}.type
        val savedFavorites: List<WorkoutModel>? = gson.fromJson(json, type)

        if (savedFavorites != null) {
            for (savedWorkout in savedFavorites) {
                workouts.find { it.name == savedWorkout.name }?.isFavorite = true
            }
        }
    }

    fun setActiveWorkout(workout: WorkoutModel) {
        activeWorkout = workout
        workout.isInProgress = true
    }

    fun completeWorkout(workout: WorkoutModel, caloriesBurned: Int) {
        activeWorkout = null
        workout.isInProgress = false

        val totalCaloriesBurned = sharedPreferences.getInt("calories_burned", 0) + caloriesBurned
        sharedPreferences.edit().putInt("calories_burned", totalCaloriesBurned).apply()
    }

    private fun addCaloriesToTotal(calories: Int) {
        val sharedPreferences = App.getAppContext().getSharedPreferences("FitTrackPrefs", Context.MODE_PRIVATE)
        val currentCalories = sharedPreferences.getInt("calories_burned", 0)
        sharedPreferences.edit().putInt("calories_burned", currentCalories + calories).apply()
    }

    fun isWorkoutActive(): Boolean = activeWorkout != null

    fun getActiveWorkout(): WorkoutModel? = activeWorkout

}
