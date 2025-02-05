package com.example.finalproject_fittrack

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.finalproject_fittrack.databinding.ActivityProfileSetupBinding
import com.example.finalproject_fittrack.logic.ProfileManager

class ProfileSetupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileSetupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileSetupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.profileBTNSave.setOnClickListener {
            saveProfile()
        }
    }

    private fun saveProfile() {
        val name = binding.profileEDITName.text.toString()
        val age = binding.profileEDITAge.text.toString().toIntOrNull() ?: 0
        val height = binding.profileEDITHeight.text.toString().toFloatOrNull() ?: 0f
        val weight = binding.profileEDITWeight.text.toString().toFloatOrNull() ?: 0f

        if (name.isBlank() || age == 0 || height == 0f || weight == 0f) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        ProfileManager.getInstance().saveProfile(name, age, height, weight)

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
