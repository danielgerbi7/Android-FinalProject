package com.example.finalproject_fittrack.ui.workout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalproject_fittrack.R
import com.example.finalproject_fittrack.adapter.WorkoutAdapter
import com.example.finalproject_fittrack.databinding.FragmentWorkoutListBinding
import com.example.finalproject_fittrack.interfaces.WorkoutFavoriteCallback
import com.example.finalproject_fittrack.interfaces.WorkoutProgressCallback
import com.example.finalproject_fittrack.logic.WorkoutManager
import com.example.finalproject_fittrack.models.WorkoutModel

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
        workoutList = WorkoutManager.getWorkoutsByCategory(category).toMutableList()

        workoutAdapter = WorkoutAdapter(
            workouts = workoutList,
            workoutFavoriteCallback = object : WorkoutFavoriteCallback {
                override fun onFavoriteClicked(workout: WorkoutModel, position: Int) {
                    toggleFavorite(workout, position)
                }
            },
            workoutProgressCallback = object : WorkoutProgressCallback {
                override fun onStartWorkout(workout: WorkoutModel, position: Int) {
                    navigateToProgressScreen(workout)
                }
            }
        )

        binding.FWLRVWorkouts.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = workoutAdapter
            setHasFixedSize(true)
        }
        return binding.root
    }

    private fun toggleFavorite(workout: WorkoutModel, position: Int) {
        WorkoutManager.updateFavoriteStatus(workout)
        workoutAdapter.notifyItemChanged(position)
    }

    private fun navigateToProgressScreen(workout: WorkoutModel) {
        val bundle = Bundle()
        bundle.putString("workout_name", workout.name)
        bundle.putInt("workout_duration", workout.duration)
        bundle.putInt("workout_calories", workout.caloriesBurned)

        findNavController().navigate(R.id.navigation_progress, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
