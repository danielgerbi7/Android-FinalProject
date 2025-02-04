package com.example.finalproject_fittrack.ui.workout

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalproject_fittrack.MainActivity
import com.example.finalproject_fittrack.adapter.WorkoutAdapter
import com.example.finalproject_fittrack.databinding.FragmentWorkoutListBinding
import com.example.finalproject_fittrack.interfaces.WorkoutCallback
import com.example.finalproject_fittrack.logic.WorkoutManager
import com.example.finalproject_fittrack.models.WorkoutModel
import com.example.finalproject_fittrack.ui.home.HomeFragment

class WorkoutListFragment : Fragment() {

    private var _binding: FragmentWorkoutListBinding? = null
    private val binding get() = _binding!!

    private lateinit var workoutAdapter: WorkoutAdapter
    private lateinit var workoutList: MutableList<WorkoutModel>

    private var activeWorkout: WorkoutModel? = null
    private var workoutTimer: CountDownTimer? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkoutListBinding.inflate(inflater, container, false)

        val category = arguments?.getString("category") ?: "Cardio"
        workoutList = WorkoutManager.getWorkoutsByCategory(category).toMutableList()

        workoutAdapter = WorkoutAdapter(
            workouts = workoutList,
            workoutCallback = object : WorkoutCallback {
                override fun onFavoriteClicked(workout: WorkoutModel, position: Int) {
                    toggleFavorite(workout, position)
                }

                override fun onStartWorkout(workout: WorkoutModel, position: Int) {
                    startWorkout(workout, position)
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
        (activity as? MainActivity)?.let { mainActivity ->
            val homeFragment = mainActivity.supportFragmentManager.findFragmentByTag("HomeFragment") as? HomeFragment
            homeFragment?.updateFavorites()
        }
        workoutAdapter.notifyItemChanged(position)
    }

    private fun startWorkout(workout: WorkoutModel, position: Int) {
        if (activeWorkout != null) {
            return
        }
        activeWorkout = workout

        workoutTimer = object : CountDownTimer(workout.duration * 60000L, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutesLeft = millisUntilFinished / 60000
                //if (_binding != null) {
                  //  binding.FWLRVWorkouts.text = "Workout in Progress: $minutesLeft min left"
                //}
            }

            override fun onFinish() {
                activeWorkout = null
                if (_binding != null) {
                    //binding.FHLBLgoalStatus.text = "Workout Complete!"
                }
                updateCaloriesAfterWorkout(workout)
            }
        }.start()
    }


    private fun updateCaloriesAfterWorkout(workout: WorkoutModel) {
        (activity as? MainActivity)?.let { mainActivity ->
            val homeFragment = mainActivity.supportFragmentManager.findFragmentByTag("HomeFragment") as? HomeFragment
            homeFragment?.addCalories(workout.caloriesBurned)
        }
    }
    private fun navigateToWorkoutDetails(workout: WorkoutModel) {
        // Future implementation for navigating to a workout detail screen
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
