package com.example.finalproject_fittrack

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.finalproject_fittrack.logic.ProfileManager
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
            checkProfileAndRedirect()
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
            checkProfileAndRedirect()
        } else {
            Toast.makeText(this, "Login failed!", Toast.LENGTH_LONG).show()
            startLoginProcess()
        }
    }

    private fun checkProfileAndRedirect() {
        ProfileManager.getInstance().isProfileComplete { isComplete ->
            redirectUser(isComplete)
        }
    }

    private fun redirectUser(isComplete: Boolean) {
        val intent = if (isComplete) {
            Intent(this, MainActivity::class.java)
        } else {
            Intent(this, ProfileSetupActivity::class.java)
        }
        startActivity(intent)
        finish()
    }
}
