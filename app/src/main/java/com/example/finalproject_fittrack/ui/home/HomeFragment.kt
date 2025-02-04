package com.example.finalproject_fittrack.ui.home

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalproject_fittrack.R
import com.example.finalproject_fittrack.databinding.FragmentHomeBinding
import com.example.finalproject_fittrack.logic.WorkoutManager
import com.example.finalproject_fittrack.models.WorkoutModel
import com.example.finalproject_fittrack.adapter.WorkoutAdapter
import com.example.finalproject_fittrack.interfaces.WorkoutFavoriteCallback
import com.example.finalproject_fittrack.interfaces.WorkoutProgressCallback
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var favoriteAdapter: WorkoutAdapter
    private var favoriteWorkouts: MutableList<WorkoutModel> = mutableListOf()

    private lateinit var sharedPreferences: SharedPreferences
    private var dailyGoal = 500
    private var currentCaloriesBurned = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        sharedPreferences =
            requireContext().getSharedPreferences("FitTrackPrefs", Context.MODE_PRIVATE)

        val user = FirebaseAuth.getInstance().currentUser
        "Welcome, ${user?.displayName ?: "User"}!".also { binding.FHLBLWelcome.text = it }

        dailyGoal = sharedPreferences.getInt("daily_goal", 500)
        currentCaloriesBurned = sharedPreferences.getInt("calories_burned", 0)

        updateProgressBar()
        setupFavoritesRecyclerView()

        checkDailyGoalAfterLogin()

        binding.FHBTNStartWorkout.setOnClickListener {
            findNavController().navigate(R.id.navigation_workout)
        }

        return binding.root
    }

    private fun setupFavoritesRecyclerView() {
        favoriteWorkouts = WorkoutManager.getFavoriteWorkouts().toMutableList()
        favoriteAdapter = WorkoutAdapter(
            favoriteWorkouts,
            object : WorkoutFavoriteCallback {
                override fun onFavoriteClicked(workout: WorkoutModel, position: Int) {
                    toggleFavorite(workout, position)
                }
            },
            object : WorkoutProgressCallback {
                override fun onStartWorkout(workout: WorkoutModel, position: Int) {
                    val navController = findNavController()
                    navController.navigate(R.id.navigation_progress)
                }
            }
        )

        binding.FHRVFavoritesList.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = favoriteAdapter
            setHasFixedSize(true)
        }
    }

    private fun checkDailyGoalAfterLogin() {
        val todayDate = getTodayDate()
        val lastLoginDate = sharedPreferences.getString("last_login_date", "")

        if (lastLoginDate != todayDate) {
            askUserForDailyGoal()
            sharedPreferences.edit().putString("last_login_date", todayDate).apply()
        }
    }

    private fun updateProgressBar() {
        val progress = if (dailyGoal > 0) (currentCaloriesBurned * 100 / dailyGoal) else 0
        binding.FHPRDailyGoal.progress = progress
        "$progress% of daily goal achieved!".also { binding.FHLBLGoalStatus.text = it }
        "Calories Burned: $currentCaloriesBurned kcal".also {
            binding.FHLBLCaloriesBurned.text = it
        }
    }

    private fun askUserForDailyGoal() {
        val editText = EditText(requireContext())
        editText.hint = "Enter calorie goal (e.g. 500 kcal)"

        AlertDialog.Builder(requireContext())
            .setTitle("Set Your Daily Goal")
            .setView(editText)
            .setPositiveButton("Save") { _, _ ->
                val inputGoal = editText.text.toString().toIntOrNull()
                if (inputGoal != null && inputGoal > 0) {
                    dailyGoal = inputGoal
                    sharedPreferences.edit().putInt("daily_goal", dailyGoal).apply()
                    updateProgressBar()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Please enter a valid number!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .setCancelable(false)
            .show()
    }

    private fun updateFavorites() {
        favoriteWorkouts.clear()
        favoriteWorkouts.addAll(WorkoutManager.getFavoriteWorkouts())
        favoriteAdapter.notifyDataSetChanged()
    }

    private fun toggleFavorite(workout: WorkoutModel, position: Int) {
        WorkoutManager.updateFavoriteStatus(workout)
        updateFavorites()
    }

    fun addCalories(calories: Int) {
        currentCaloriesBurned += calories
        sharedPreferences.edit().putInt("calories_burned", currentCaloriesBurned).apply()
        updateProgressBar()
    }

    private fun getTodayDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
