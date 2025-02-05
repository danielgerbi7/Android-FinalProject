package com.example.finalproject_fittrack

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.finalproject_fittrack.logic.ProfileManager
import com.example.finalproject_fittrack.utilities.Constants
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

class LoginActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences(Constants.SharedPrefs.PREFS_NAME, MODE_PRIVATE)

        val user = FirebaseAuth.getInstance().currentUser
        val savedUserId = sharedPreferences.getString("user_id", "")

        if (user == null) {
            startLoginProcess()
        } else {
            if (savedUserId.isNullOrEmpty()|| savedUserId != user.uid) {
                saveUserLoginStatus(user.uid)
            }
            checkUserStatusAndRedirect()
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
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                saveUserLoginStatus(user.uid)
                checkUserStatusAndRedirect()
            } else {
                Toast.makeText(this, "Unexpected error, please try again", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(this, "Login failed!", Toast.LENGTH_LONG).show()
            startLoginProcess()
        }
    }

    private fun checkUserStatusAndRedirect() {
        FirebaseAuth.getInstance().currentUser?.reload()?.addOnCompleteListener { task ->
            val lastLoginDate = sharedPreferences.getString(Constants.SharedPrefs.LAST_LOGIN_DATE, "")
            val todayDate = getTodayDate()
            val isProfileSetupComplete = ProfileManager.getInstance().isProfileComplete()

            when {
                !isProfileSetupComplete -> {
                    startActivity(Intent(this, ProfileSetupActivity::class.java))
                }
                lastLoginDate != todayDate -> {
                    sharedPreferences.edit().putString(Constants.SharedPrefs.LAST_LOGIN_DATE, todayDate).apply()
                    startActivity(Intent(this, MainActivity::class.java).apply {
                        putExtra("SHOW_DAILY_GOAL_DIALOG", true)
                    })
                }
                else -> {
                    startActivity(Intent(this, MainActivity::class.java))
                }
            }
            finish()
        }
    }

    private fun saveUserLoginStatus(uid: String) {
        sharedPreferences.edit()
            .putBoolean("isLoggedIn", true)
            .putString("user_id", uid)
            .apply()

    }

    private fun getTodayDate(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }
}
