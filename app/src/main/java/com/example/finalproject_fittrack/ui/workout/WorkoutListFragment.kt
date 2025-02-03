package com.example.finalproject_fittrack.ui.workout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalproject_fittrack.MainActivity
import com.example.finalproject_fittrack.databinding.FragmentWorkoutListBinding
import com.example.finalproject_fittrack.logic.WorkoutRepository
import com.example.finalproject_fittrack.models.WorkoutModel
import com.example.finalproject_fittrack.ui.home.HomeFragment

class WorkoutListFragment : Fragment() {

    private var _binding: FragmentWorkoutListBinding? = null
    private val binding get() = _binding!!

    private lateinit var workoutAdapter: WorkoutAdapter
    private lateinit var workoutList: MutableList<WorkoutModel>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkoutListBinding.inflate(inflater, container, false)

        val category = arguments?.getString("category") ?: "Cardio"
        workoutList = WorkoutRepository.getWorkoutsByCategory(category).toMutableList()

        workoutAdapter = WorkoutAdapter(
            workoutList,
            onFavoriteClick = { workout, position -> toggleFavorite(workout, position) },
            onItemClick = { workout -> navigateToWorkoutDetails(workout) }
        )
        binding.rvWorkouts.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = workoutAdapter
            setHasFixedSize(true)
        }
        return binding.root
    }

    private fun toggleFavorite(workout: WorkoutModel, position: Int) {
        WorkoutRepository.updateFavoriteStatus(workout)

        (activity as? MainActivity)?.let { mainActivity ->
            val homeFragment = mainActivity.supportFragmentManager.findFragmentByTag("HomeFragment") as? HomeFragment
            homeFragment?.updateFavorites()
        }

        workoutAdapter.notifyItemChanged(position)
    }

    private fun navigateToWorkoutDetails(workout: WorkoutModel) {
        // Future implementation for navigating to a workout detail screen
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
