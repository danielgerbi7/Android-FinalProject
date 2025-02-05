package com.example.finalproject_fittrack.logic

import android.content.Context
import android.content.SharedPreferences
import com.example.finalproject_fittrack.utilities.Constants

class ProfileManager private constructor(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(Constants.SharedPrefs.PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private var instance: ProfileManager? = null

        fun init(context: Context) {
            if (instance == null) {
                instance = ProfileManager(context)
            }
        }

        fun getInstance(): ProfileManager {
            return instance ?: throw IllegalStateException("ProfileManager not initialized")
        }
    }

    fun isProfileComplete(): Boolean {
        return sharedPreferences.getBoolean(Constants.SharedPrefs.PREFS_NAME, false)
    }

    fun saveProfile(name: String, age: Int, height: Float, weight: Float) {
        sharedPreferences.edit()
            .putString(Constants.SharedPrefs.USER_NAME, name)
            .putInt(Constants.SharedPrefs.USER_AGE, age)
            .putFloat(Constants.SharedPrefs.USER_HEIGHT, height)
            .putFloat(Constants.SharedPrefs.USER_WEIGHT, weight)
            .putBoolean(Constants.SharedPrefs.PROFILE_SETUP_COMPLETE, true)
            .apply()
    }

    fun calculateBMI(): Pair<Float, String> {
        val height = getUserHeight() / 100
        val weight = getUserWeight()

        return if (height > 0 && weight > 0) {
            val bmi = weight / (height * height)
            val status = when {
                bmi < 18.5 -> "Underweight"
                bmi in 18.5..24.9 -> "Normal"
                bmi in 25.0..29.9 -> "Overweight"
                else -> "Obese"
            }
            Pair(bmi, status)
        } else {
            Pair(0f, "Unknown")
        }
    }

    fun getUserName(): String = sharedPreferences.getString(Constants.SharedPrefs.USER_NAME, "User") ?: "User"
    fun getUserAge(): Int = sharedPreferences.getInt(Constants.SharedPrefs.USER_AGE, 0)
    fun getUserHeight(): Float = sharedPreferences.getFloat(Constants.SharedPrefs.USER_HEIGHT, 0f)
    fun getUserWeight(): Float = sharedPreferences.getFloat(Constants.SharedPrefs.USER_WEIGHT, 0f)
}
