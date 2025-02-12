package com.example.finalproject_fittrack.ui.home

import com.example.finalproject_fittrack.models.Workout
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalproject_fittrack.R
import com.example.finalproject_fittrack.adapter.FavoriteWorkoutAdapter
import com.example.finalproject_fittrack.databinding.FragmentHomeBinding
import com.example.finalproject_fittrack.dataBase.WorkoutRepository
import com.example.finalproject_fittrack.interfaces.WorkoutFavoriteCallback
import com.example.finalproject_fittrack.interfaces.WorkoutProgressCallback
import com.example.finalproject_fittrack.dataBase.ProfileRepository
import com.example.finalproject_fittrack.models.DailyGoalManager
import com.example.finalproject_fittrack.utilities.Constants

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var favoriteWorkouts: MutableList<Workout> = mutableListOf()
    private lateinit var favoriteAdapter: FavoriteWorkoutAdapter
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        sharedPreferences =
            requireContext().getSharedPreferences(
                Constants.SharedPrefs.PREFS_NAME,
                Context.MODE_PRIVATE
            )

        loadUserProfile()
        updateProgressBar()
        setupFavoritesRecyclerView()

        binding.FHBTNStartWorkout.setOnClickListener {
            findNavController().navigate(R.id.navigation_workout)
        }

        return binding.root
    }

    private fun loadUserProfile() {
        ProfileRepository.getInstance().getProfile { profile ->
            if (profile != null) {
                "Welcome, ${profile.name}!".also { binding.FHLBLWelcome.text = it }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupFavoritesRecyclerView() {
        favoriteWorkouts = mutableListOf()
        favoriteAdapter = FavoriteWorkoutAdapter(
            favoriteWorkouts,
            object : WorkoutFavoriteCallback {
                override fun onFavoriteClicked(workout: Workout, position: Int) {
                    toggleFavorite(workout)
                }
            },
            object : WorkoutProgressCallback {
                override fun onStartWorkout(workout: Workout, position: Int) {
                    findNavController().navigate(R.id.navigation_progress)
                }
            }
        )

        binding.FHRVFavoritesList.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = favoriteAdapter
            setHasFixedSize(true)
        }
        WorkoutRepository.loadFavoriteWorkouts { favorites ->
            favoriteWorkouts.clear()
            favoriteWorkouts.addAll(favorites)
            favoriteAdapter.notifyDataSetChanged()
        }
    }

    private fun updateProgressBar() {
        DailyGoalManager.getDailyProgress { progress ->
            val progressPercentage = if (progress.goal > 0) {
                (progress.caloriesBurned * 100 / progress.goal)
            } else 0

            binding.apply {
                FHPRDailyGoal.progress = progressPercentage
                "$progressPercentage% of daily goal achieved!".also { FHLBLGoalStatus.text = it }
                "Calories Burned: ${progress.caloriesBurned} kcal".also {
                    FHLBLCaloriesBurned.text = it
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateFavorites() {
        WorkoutRepository.loadFavoriteWorkouts { favorites ->
            favoriteWorkouts.clear()
            favoriteWorkouts.addAll(favorites)
            favoriteAdapter.notifyDataSetChanged()
        }
    }

    private fun toggleFavorite(workout: Workout) {
        WorkoutRepository.updateFavoriteStatus(workout) {
            updateFavorites()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parentFragmentManager.setFragmentResultListener("update_home", this) { _, _ ->
            loadUserProfile()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
