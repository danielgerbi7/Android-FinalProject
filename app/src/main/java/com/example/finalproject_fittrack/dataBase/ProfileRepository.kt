package com.example.finalproject_fittrack.dataBase

import android.content.Context
import com.example.finalproject_fittrack.dataBase.FirebaseRepository.getUserReference
import com.example.finalproject_fittrack.utilities.Constants
import com.example.finalproject_fittrack.utilities.DateDetails


class ProfileRepository private constructor() {
    companion object {
        @Volatile
        private var instance: ProfileRepository? = null

        fun init() {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = ProfileRepository()
                    }
                }
            }
        }

        fun getInstance(): ProfileRepository {
            return instance ?: throw IllegalStateException("ProfileRepository not initialized")
        }
    }

    fun saveProfile(name: String, age: Int, height: Float, weight: Float, onComplete: (Boolean) -> Unit) {
        val userRef = getUserReference() ?: return

        userRef.get().addOnSuccessListener { snapshot ->
            val existingData = snapshot.value as? Map<String, Any> ?: emptyMap()

            val updatedData = mutableMapOf<String, Any>(
                "name" to name,
                "age" to age,
                "height" to height,
                "weight" to weight,
                "profile_complete" to true
            )

            existingData["calories_burned"]?.let { updatedData["calories_burned"] = it }
            existingData["daily_goal"]?.let { updatedData["daily_goal"] = it }
            existingData["favorite_workouts"]?.let { updatedData["favorite_workouts"] = it }

            userRef.updateChildren(updatedData)
                .addOnSuccessListener {
                    WorkoutRepository.checkAndSaveDefaultWorkouts()
                    onComplete(true)
                }
                .addOnFailureListener { onComplete(false) }
        }.addOnFailureListener {
            onComplete(false)
        }
    }

    fun getProfile(onComplete: (Map<String, Any>?) -> Unit) {
        val userRef = getUserReference() ?: return

        userRef.get().addOnSuccessListener { snapshot ->
            val profileData = snapshot.value as? Map<String, Any>
            DailyGoalManager.getDailyProgress { progress ->
                val combinedData = (profileData ?: mutableMapOf()).toMutableMap().apply {
                    put("daily_goal", progress.goal)
                    put("calories_burned", progress.caloriesBurned)
                }
                onComplete(combinedData)
            }
        }.addOnFailureListener {
            onComplete(null)
        }
    }

    fun isProfileComplete(onComplete: (Boolean) -> Unit) {
        getProfile { profileData ->
            val isComplete = profileData?.get("profile_complete") as? Boolean ?: false
            onComplete(isComplete)
        }
    }

    fun calculateBMI(onComplete: (Float, String) -> Unit) {
        getProfile { profileData ->
            val height = (profileData?.get("height") as? Number)?.toFloat() ?: 0f
            val weight = (profileData?.get("weight") as? Number)?.toFloat() ?: 0f

            val bmi: Float
            val status: String

            if (height > 0 && weight > 0) {
                bmi = weight / ((height / 100) * (height / 100))
                status = when {
                    bmi < 18.5 -> "Underweight"
                    bmi in 18.5..24.9 -> "Normal"
                    bmi in 25.0..29.9 -> "Overweight"
                    else -> "Obese"
                }
            } else {
                bmi = 0f
                status = "Unknown"
            }
            onComplete(bmi, status)
        }
    }

    object DailyGoalManager {
        private const val DEFAULT_GOAL = 500

        data class DailyProgress(
            val goal: Int = DEFAULT_GOAL,
            val caloriesBurned: Int = 0,
            val lastUpdateDate: String = ""
        )

        fun updateDailyGoal(goal: Int, onComplete: (Boolean) -> Unit) {
            val userRef = getUserReference() ?: return
            val updates = hashMapOf<String, Any>(
                "daily_goal" to goal,
                "calories_burned" to 0
            )

            userRef.updateChildren(updates)
                .addOnSuccessListener { onComplete(true) }
                .addOnFailureListener { onComplete(false) }
        }

        fun updateCaloriesBurned(calories: Int, onComplete: (Boolean) -> Unit) {
            val userRef = getUserReference() ?: return
            userRef.child("calories_burned").setValue(calories)
                .addOnSuccessListener { onComplete(true) }
                .addOnFailureListener { onComplete(false) }
        }

        fun getDailyProgress(onComplete: (DailyProgress) -> Unit) {
            val userRef = getUserReference() ?: return
            userRef.get().addOnSuccessListener { snapshot ->
                val goal = (snapshot.child("daily_goal").value as? Long)?.toInt() ?: DEFAULT_GOAL
                val caloriesBurned = (snapshot.child("calories_burned").value as? Long)?.toInt() ?: 0
                val lastUpdateDate = snapshot.child("last_update_date").value as? String ?: ""

                onComplete(DailyProgress(goal, caloriesBurned, lastUpdateDate))
            }.addOnFailureListener {
                onComplete(DailyProgress())
            }
        }

        fun resetDailyProgress(context: Context, onComplete: (Boolean) -> Unit) {
            val sharedPreferences = context.getSharedPreferences(Constants.SharedPrefs.PREFS_NAME, Context.MODE_PRIVATE)
            val todayDate = DateDetails.getTodayDate()
            val lastLoginDate = sharedPreferences.getString(Constants.SharedPrefs.LAST_LOGIN_DATE, "")

            if (lastLoginDate != todayDate) {
                sharedPreferences.edit()
                    .putInt(Constants.SharedPrefs.CALORIES_BURNED, 0)
                    .putString(Constants.SharedPrefs.LAST_LOGIN_DATE, todayDate)
                    .apply()

                val userRef = getUserReference() ?: return
                val updates = hashMapOf<String, Any>(
                    "calories_burned" to 0,
                    "last_update_date" to todayDate
                )

                userRef.updateChildren(updates)
                    .addOnSuccessListener { onComplete(true) }
                    .addOnFailureListener { onComplete(false) }
            } else {
                onComplete(true)
            }
        }
    }




}
