package com.example.finalproject_fittrack.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.finalproject_fittrack.databinding.FragmentProfileBinding
import com.example.finalproject_fittrack.logic.ProfileManager
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
        val profileManager = ProfileManager.getInstance()
        binding.profileEDITName.setText(profileManager.getUserName())
        binding.profileEDITAge.setText(
            String.format(
                Locale.getDefault(),
                "%d",
                profileManager.getUserAge()
            )
        )
        binding.profileEDITHeight.setText(
            String.format(
                Locale.getDefault(),
                "%.1f",
                profileManager.getUserHeight()
            )
        )
        binding.profileEDITWeight.setText(
            String.format(
                Locale.getDefault(),
                "%.1f",
                profileManager.getUserWeight()
            )
        )

        val (bmi, status) = profileManager.calculateBMI()
        "BMI: %.1f".format(bmi).also { binding.profileLBLBmi.text = it }
        "Status: $status".also { binding.profileLBLBmiStatus.text = it }

        enableEditing(false)
    }

    private fun saveProfile() {
        ProfileManager.getInstance().saveProfile(
            binding.profileEDITName.text.toString(),
            binding.profileEDITAge.text.toString().toIntOrNull() ?: 0,
            binding.profileEDITHeight.text.toString().toFloatOrNull() ?: 0f,
            binding.profileEDITWeight.text.toString().toFloatOrNull() ?: 0f
        )
        requireActivity().supportFragmentManager
            .setFragmentResult("update_home", Bundle())

        loadUserProfile()
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
