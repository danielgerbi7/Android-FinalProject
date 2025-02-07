package com.example.finalproject_fittrack.models
import android.content.Context
import com.example.finalproject_fittrack.dataBase.FirebaseRepository.getUserReference
import com.example.finalproject_fittrack.utilities.Constants
import com.example.finalproject_fittrack.utilities.Constants.DefaultNutrition.DEFAULT_GOAL
import com.example.finalproject_fittrack.utilities.DateDetails

object DailyGoalManager {


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
            val caloriesBurned =
                (snapshot.child("calories_burned").value as? Long)?.toInt() ?: 0
            val lastUpdateDate = snapshot.child("last_update_date").value as? String ?: ""

            onComplete(DailyProgress(goal, caloriesBurned, lastUpdateDate))
        }.addOnFailureListener {
            onComplete(DailyProgress())
        }
    }

    fun resetDailyProgress(context: Context, onComplete: (Boolean) -> Unit) {
        val sharedPreferences =
            context.getSharedPreferences(Constants.SharedPrefs.PREFS_NAME, Context.MODE_PRIVATE)
        val todayDate = DateDetails.getTodayDate()
        val lastLoginDate =
            sharedPreferences.getString(Constants.SharedPrefs.LAST_LOGIN_DATE, "")

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