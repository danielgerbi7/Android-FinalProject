package com.example.finalproject_fittrack.logic

import android.content.Context
import com.example.finalproject_fittrack.utilities.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileManager private constructor(context: Context) {

    private val database: DatabaseReference = FirebaseDatabase.getInstance().getReference(Constants.Firebase.USERS_REF)
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    companion object {
        @Volatile
        private var instance: ProfileManager? = null

        fun init(context: Context) {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = ProfileManager(context)
                    }
                }
            }
        }

        fun getInstance(): ProfileManager {
            return instance ?: throw IllegalStateException("ProfileManager not initialized")
        }
    }

    fun checkAndSaveDefaultWorkouts() {
        val userId = getCurrentUserId() ?: return
        database.child(userId).child("workouts")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!snapshot.exists()) {
                        WorkoutManager.saveDefaultWorkouts()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    private fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    fun saveProfile(name: String, age: Int, height: Float, weight: Float, onComplete: (Boolean) -> Unit) {
        val userId = getCurrentUserId() ?: return

        val userProfile = mapOf(
            "name" to name,
            "age" to age,
            "height" to height,
            "weight" to weight,
            "profile_complete" to true
        )

        database.child(userId).setValue(userProfile)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }


    fun getProfile(onComplete: (Map<String, Any>?) -> Unit) {
        val userId = getCurrentUserId() ?: return

        database.child(userId).get().addOnSuccessListener { snapshot ->
            val profileData = snapshot.value as? Map<String, Any>
            onComplete(profileData)
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
}
