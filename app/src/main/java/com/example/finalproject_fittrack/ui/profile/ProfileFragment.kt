package com.example.finalproject_fittrack.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.finalproject_fittrack.databinding.FragmentProfileBinding
import com.example.finalproject_fittrack.dataBase.ProfileRepository
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

        return binding.root
    }

    private fun loadUserProfile() {
        ProfileRepository.getInstance().getProfile { profileData ->
            if (profileData != null) {
                binding.profileEDITName.setText(profileData["name"] as? String ?: "")
                binding.profileEDITAge.setText((profileData["age"] as? Long)?.toString() ?: "0")
                binding.profileEDITHeight.setText(
                    (profileData["height"] as? Long)?.toString() ?: "0.0"
                )
                binding.profileEDITWeight.setText(
                    (profileData["weight"] as? Long)?.toString() ?: "0.0"
                )

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

        ProfileRepository.getInstance().saveProfile(name, age, height, weight) { success ->
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
}
