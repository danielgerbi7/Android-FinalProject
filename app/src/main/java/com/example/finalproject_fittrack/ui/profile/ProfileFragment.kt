package com.example.finalproject_fittrack.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.finalproject_fittrack.LoginActivity
import com.example.finalproject_fittrack.databinding.FragmentProfileBinding
import com.example.finalproject_fittrack.dataBase.ProfileRepository
import com.example.finalproject_fittrack.models.Profile
import com.firebase.ui.auth.AuthUI
import java.util.Locale

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        loadUserProfile()

        binding.profileBTNEdit.setOnClickListener {
            enableEditing(true)
        }

        binding.profileBTNSave.setOnClickListener {
            saveProfile()
            enableEditing(false)
        }

        binding.profileBTNSignOut.setOnClickListener {
            signOutUser()
        }

        return binding.root
    }

    private fun loadUserProfile() {
        ProfileRepository.getInstance().getProfile { profile ->
            if (profile != null) {
                binding.profileEDITName.setText(profile.name)
                binding.profileEDITAge.setText(String.format(Locale.getDefault(), "%d", profile.age))
                binding.profileEDITHeight.setText(String.format(Locale.getDefault(), "%.1f", profile.height))
                binding.profileEDITWeight.setText(String.format(Locale.getDefault(), "%.1f", profile.weight))


                ProfileRepository.getInstance().calculateBMI { bmi, status ->
                    binding.profileLBLBmi.text =
                        String.format(Locale.getDefault(), "BMI: %.1f", bmi)
                    "Status: $status".also { binding.profileLBLBmiStatus.text = it }
                }
            }
        }

        enableEditing(false)
    }

    private fun saveProfile() {
        val name = binding.profileEDITName.text.toString()
        val age = binding.profileEDITAge.text.toString().toIntOrNull() ?: 0
        val height = binding.profileEDITHeight.text.toString().toFloatOrNull() ?: 0f
        val weight = binding.profileEDITWeight.text.toString().toFloatOrNull() ?: 0f

        val updatedProfile = Profile.Builder()
            .name(name)
            .age(age)
            .height(height)
            .weight(weight)
            .profileComplete(true)
            .build()

        ProfileRepository.getInstance().saveProfile(updatedProfile) { success ->
            if (success) {
                requireActivity().supportFragmentManager
                    .setFragmentResult("update_home", Bundle())
                loadUserProfile()
            }
        }
    }

    private fun enableEditing(enabled: Boolean) {
        binding.profileEDITName.isEnabled = enabled
        binding.profileEDITAge.isEnabled = enabled
        binding.profileEDITHeight.isEnabled = enabled
        binding.profileEDITWeight.isEnabled = enabled

        binding.profileBTNSave.visibility = if (enabled) View.VISIBLE else View.GONE
        binding.profileBTNEdit.visibility = if (enabled) View.GONE else View.VISIBLE
    }

    private fun signOutUser() {
        AuthUI.getInstance().signOut(requireContext()).addOnCompleteListener {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
