package com.example.finalproject_fittrack

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.finalproject_fittrack.dataBase.ProfileRepository
import com.example.finalproject_fittrack.dataBase.WorkoutRepository
import com.example.finalproject_fittrack.models.DailyGoalManager
import com.example.finalproject_fittrack.utilities.Constants
import com.example.finalproject_fittrack.utilities.DateDetails
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val user = FirebaseAuth.getInstance().currentUser

        if (user == null) {
            startLoginProcess()
        } else {
            checkProfileStatus()
        }
    }

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res -> onSignInResult(res) }

    private fun startLoginProcess() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.PhoneBuilder().build()
        )

        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setTheme(R.style.Theme_FinalProjectFitTrack)
            .setLogo(R.drawable.login_icon)
            .build()
        signInLauncher.launch(signInIntent)
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        if (result.resultCode == RESULT_OK) {
            checkProfileStatus()
        } else {
            Toast.makeText(this, "Login failed!", Toast.LENGTH_LONG).show()
            startLoginProcess()
        }
    }

    private fun checkProfileStatus() {
        ProfileRepository.getInstance().isProfileComplete { isComplete ->
            if (isComplete) {
                checkDailyGoal()
            } else {
                askUserForDailyGoal(isNewUser = true)
            }
        }
    }

    private fun checkDailyGoal() {
        val todayDate = DateDetails.getTodayDate()
        val sharedPreferences = getSharedPreferences(Constants.SharedPrefs.PREFS_NAME, MODE_PRIVATE)
        val lastLoginDate = sharedPreferences.getString(Constants.SharedPrefs.LAST_LOGIN_DATE, "")

        DailyGoalManager.getDailyProgress { progress ->
            if (progress.goal <= 0) {
                askUserForDailyGoal(isNewUser = false)
            } else {
                if (lastLoginDate != todayDate) {
                    DailyGoalManager.resetDailyProgress(this) { _ -> }
                    sharedPreferences.edit()
                        .putString(Constants.SharedPrefs.LAST_LOGIN_DATE, todayDate)
                        .apply()
                }
                redirectToMain()
            }
        }
    }

    private fun askUserForDailyGoal(isNewUser: Boolean) {
        val editText = EditText(this)
        editText.hint = "Enter calorie goal (e.g. 500 kcal)"

        AlertDialog.Builder(this)
            .setTitle("Set Your Daily Goal")
            .setView(editText)
            .setPositiveButton("Save") { _, _ ->
                val inputGoal = editText.text.toString().toIntOrNull()
                if (inputGoal != null && inputGoal > 0) {
                    DailyGoalManager.updateDailyGoal(inputGoal) { success ->
                        if (success) {
                            if (isNewUser) {
                                redirectToProfileSetup()
                            } else {
                                redirectToMain()
                            }
                        } else {
                            Toast.makeText(
                                this,
                                "Failed to save goal. Try again!",
                                Toast.LENGTH_SHORT
                            ).show()
                            askUserForDailyGoal(isNewUser)
                        }
                    }
                } else {
                    Toast.makeText(this, "Please enter a valid number!", Toast.LENGTH_SHORT).show()
                    askUserForDailyGoal(isNewUser)
                }
            }
            .setCancelable(false)
            .show()
    }

    private fun redirectToProfileSetup() {
        val intent = Intent(this, ProfileSetupActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun redirectToMain() {
        WorkoutRepository.checkAndSaveDefaultWorkouts()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}