package com.example.finalproject_fittrack.models


data class Profile(
    val name: String,
    val age: Int,
    val height: Float,
    val weight: Float,
    val profileComplete: Boolean,
    val caloriesBurned: Int? = null,
    val dailyGoal: Int? = null,
    val favoriteWorkouts: List<String>? = null
) {
    data class Builder(
        private var name: String = "",
        private var age: Int = 0,
        private var height: Float = 0f,
        private var weight: Float = 0f,
        private var profileComplete: Boolean = false,
        private var caloriesBurned: Int? = null,
        private var dailyGoal: Int? = null,
        private var favoriteWorkouts: List<String>? = null
    ) {
        fun name(name: String) = apply { this.name = name }
        fun age(age: Int) = apply { this.age = age }
        fun height(height: Float) = apply { this.height = height }
        fun weight(weight: Float) = apply { this.weight = weight }
        fun profileComplete(profileComplete: Boolean) = apply { this.profileComplete = profileComplete }
        fun caloriesBurned(caloriesBurned: Int?) = apply { this.caloriesBurned = caloriesBurned }
        fun dailyGoal(dailyGoal: Int?) = apply { this.dailyGoal = dailyGoal }
        fun favoriteWorkouts(favoriteWorkouts: List<String>?) = apply { this.favoriteWorkouts = favoriteWorkouts }
        fun build() = Profile(name, age, height, weight, profileComplete, caloriesBurned, dailyGoal, favoriteWorkouts)
    }
}
