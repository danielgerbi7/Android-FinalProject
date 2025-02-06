package com.example.finalproject_fittrack.ui.workout

import WorkoutModel
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
class WorkoutListFragment : Fragment() {

    private var _binding: FragmentWorkoutListBinding? = null
    private val binding get() = _binding!!

    private lateinit var workoutAdapter: WorkoutAdapter
    private var workoutList: MutableList<WorkoutModel> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkoutListBinding.inflate(inflater, container, false)

        val category = arguments?.getString("category") ?: "Cardio"

        loadWorkoutsFromDB(category)

        return binding.root
    }

    private fun loadWorkoutsFromDB(category: String) {
        WorkoutManager.loadWorkouts(category) { workouts ->
            workoutList.clear()
            workoutList.addAll(workouts)

            WorkoutManager.loadFavoriteWorkouts { favorites ->
                workoutList.forEach { workout ->
                    workout.isFavorite = favorites.any { it.name == workout.name }
                }
                setupRecyclerView()
            }
        }
    }

    private fun setupRecyclerView() {
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
    }

    private fun toggleFavorite(workout: WorkoutModel, position: Int) {
        WorkoutManager.updateFavoriteStatus(workout) {
            WorkoutManager.loadFavoriteWorkouts { favorites ->
                workoutList.forEach { it.isFavorite = favorites.any { fav -> fav.name == it.name } }
                workoutAdapter.notifyItemChanged(position)
            }
        }
    }

    private fun navigateToProgressScreen(workout: WorkoutModel) {
        val bundle = Bundle().apply {
            putString("workout_name", workout.name)
            putInt("workout_duration", workout.duration)
            putInt("workout_calories", workout.caloriesBurned)
        }

        findNavController().navigate(R.id.navigation_progress, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
