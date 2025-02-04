package com.example.finalproject_fittrack

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sharedPreferences = getSharedPreferences("FitTrackPrefs", MODE_PRIVATE)

        if (FirebaseAuth.getInstance().currentUser != null || isUserLoggedInBefore()) {
            transactToNextScreen()
        } else {
            signIn()
        }
    }

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract(),
    ) { res ->
        this.onSignInResult(res)
    }

    private fun signIn() {
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

    private fun transactToNextScreen() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        if (result.resultCode == RESULT_OK) {
            saveUserLoginStatus()
            transactToNextScreen()
        } else {
            Toast.makeText(this, "Login failed!", Toast.LENGTH_LONG).show()
            signIn()
        }
    }

    private fun saveUserLoginStatus() {
        sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()
    }

    private fun isUserLoggedInBefore(): Boolean {
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }
}
