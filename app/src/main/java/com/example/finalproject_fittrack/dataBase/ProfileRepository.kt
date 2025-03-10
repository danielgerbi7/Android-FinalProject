package com.example.finalproject_fittrack.dataBase

import android.content.Context
import com.example.finalproject_fittrack.dataBase.FirebaseRepository.getUserReference
import com.example.finalproject_fittrack.models.DailyGoalManager
import com.example.finalproject_fittrack.models.Profile
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

    fun saveProfile(profile: Profile, onComplete: (Boolean) -> Unit) {
        val userRef = getUserReference() ?: return

        val profileData = mapOf(
            "name" to profile.name,
            "age" to profile.age,
            "height" to profile.height,
            "weight" to profile.weight,
            "profile_complete" to profile.profileComplete,
            "calories_burned" to (profile.caloriesBurned ?: 0),
            "daily_goal" to (profile.dailyGoal ?: 500),
            "favorite_workouts" to (profile.favoriteWorkouts ?: emptyList<String>())
        )

        userRef.updateChildren(profileData)
            .addOnSuccessListener {
                WorkoutRepository.checkAndSaveDefaultWorkouts()
                onComplete(true)
            }
            .addOnFailureListener { onComplete(false) }
    }

    fun getProfile(onComplete: (Profile?) -> Unit) {
        val userRef = getUserReference() ?: return

        userRef.get().addOnSuccessListener { snapshot ->
            val data = snapshot.value as? Map<String, Any> ?: emptyMap()

            DailyGoalManager.getDailyProgress { progress ->
                val profile = Profile.Builder()
                    .name(data["name"] as? String ?: "")
                    .age((data["age"] as? Number)?.toInt() ?: 0)
                    .height((data["height"] as? Number)?.toFloat() ?: 0f)
                    .weight((data["weight"] as? Number)?.toFloat() ?: 0f)
                    .profileComplete(data["profile_complete"] as? Boolean ?: false)
                    .caloriesBurned(progress.caloriesBurned)
                    .dailyGoal(progress.goal)
                    .favoriteWorkouts(data["favorite_workouts"] as? List<String>)
                    .build()

                onComplete(profile)
            }
        }.addOnFailureListener {
            onComplete(null)
        }
    }


    fun isProfileComplete(onComplete: (Boolean) -> Unit) {
        getProfile { profile ->
            onComplete(profile?.profileComplete ?: false)
        }
    }

    fun calculateBMI(onComplete: (Float, String) -> Unit) {

        getProfile { profile ->
            if (profile == null || profile.height <= 0 || profile.weight <= 0) {
                onComplete(0f, "Unknown")
                return@getProfile
            }

            val height = profile.height
            val weight = profile.weight

            if (height > 0 && weight > 0) {
                val bmi = weight / ((height / 100) * (height / 100))
                val status = when {
                    bmi < 18.5 -> "Underweight"
                    bmi in 18.5..24.9 -> "Normal"
                    bmi in 25.0..29.9 -> "Overweight"
                    else -> "Obese"
                }
                onComplete(bmi, status)
            } else {
                onComplete(0f, "Unknown")
            }
        }
    }

    fun updateProfileFields(updatedData: Map<String, Any>, onComplete: (Boolean) -> Unit) {
        val userRef = getUserReference() ?: return

        userRef.updateChildren(updatedData)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

}

