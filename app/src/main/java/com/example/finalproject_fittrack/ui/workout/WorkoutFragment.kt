package com.example.finalproject_fittrack.ui.workout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.finalproject_fittrack.R
import com.example.finalproject_fittrack.databinding.FragmentWorkoutBinding

class WorkoutFragment : Fragment() {

    private var _binding: FragmentWorkoutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkoutBinding.inflate(inflater, container, false)

        binding.btnCardio.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("category", "Cardio")
            findNavController().navigate(R.id.workoutListFragment, bundle)
        }

        binding.btnStrength.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("category", "Strength")
            findNavController().navigate(R.id.workoutListFragment, bundle)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
