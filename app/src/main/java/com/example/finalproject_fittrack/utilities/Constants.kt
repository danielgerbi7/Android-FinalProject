package com.example.finalproject_fittrack.utilities

import com.google.firebase.firestore.SetOptions

class Constants {

    object SharedPrefs {
        const val PREFS_NAME = "FitTrackPrefs"
        const val USER_NAME = "user_name"
        const val USER_AGE = "user_age"
        const val USER_HEIGHT = "user_height"
        const val USER_WEIGHT = "user_weight"
        const val PROFILE_SETUP_COMPLETE = "profile_setup_complete"
        const val CALORIES_BURNED = "calories_burned"
        const val DAILY_GOAL = "daily_goal"
        const val LAST_LOGIN_DATE = "last_login_date"
        const val FAVORITE_WORKOUTS = "favorite_workouts"
    }

    object Firebase {
        const val USERS_REF = "users"
        const val WORKOUTS_REF = "workouts"

        val PROFILE_UPLOAD_DEFAULT_OPTIONS = SetOptions.mergeFields(
            "profilePic",
            "name",
            "age",
            "height",
            "weight",
            "bmi",
            "goal"
        )
    }

    object Activities {
        const val MAIN = "MainActivity"
        const val LOGIN = "LoginActivity"
        const val PROFILE_SETUP = "ProfileSetupActivity"
    }

    object Navigation {
        const val HOME = "navigation_home"
        const val PROFILE = "navigation_profile"
        const val WORKOUT = "navigation_workout"
        const val PROGRESS = "navigation_progress"
    }

    object Messages {
        const val WORKOUT_IN_PROGRESS = "A workout is already in progress. Finish it first!"
        const val WORKOUT_STARTED = "Workout started! Check the progress screen."
    }
}
