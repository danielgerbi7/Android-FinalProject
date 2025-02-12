package com.example.finalproject_fittrack.ui.progress

import com.example.finalproject_fittrack.models.Workout
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.finalproject_fittrack.MainActivity
import com.example.finalproject_fittrack.databinding.FragmentProgressBinding
import com.example.finalproject_fittrack.dataBase.WorkoutRepository

class ProgressFragment : Fragment() {

    private var _binding: FragmentProgressBinding? = null
    private val binding get() = _binding!!
    private var workoutTimer: CountDownTimer? = null
    private var activeWorkout: Workout? = null
    private var startTimeMillis: Long = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        (requireActivity() as MainActivity).binding.navView.visibility = View.VISIBLE
        _binding = FragmentProgressBinding.inflate(inflater, container, false)
        checkActiveWorkout()
        binding.progressBTNFinish.setOnClickListener {
            finishWorkout()
        }
        return binding.root
    }

    private fun checkActiveWorkout() {
        WorkoutRepository.getActiveWorkout { workout ->
            if (workout != null) {
                startWorkout(workout)
            } else {
                defaultValues()
            }
        }
    }

    private fun startWorkout(workout: Workout) {
        activeWorkout = workout
        binding.progressLBLWorkoutName.text = workout.name
        "${workout.duration} min".also { binding.progressLBLDurationWorkout.text = it }

        startTimeMillis = System.currentTimeMillis()

        workoutTimer = object : CountDownTimer(workout.duration * 60000L, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutesLeft = millisUntilFinished / 60000
                "$minutesLeft min left".also { binding.progressLBLStatus.text = it }
                val progress =
                    ((workout.duration * 60000L - millisUntilFinished) * 100 / (workout.duration * 60000L)).toInt()
                binding.progressBarWorkout.progress = progress
            }

            override fun onFinish() {
                completeWorkout()
            }
        }.start()
    }

    private fun defaultValues() {
        "No active workout".also { binding.progressLBLWorkoutName.text = it }
        "0 min".also { binding.progressLBLDurationWorkout.text = it }
        "No workout in progress".also { binding.progressLBLStatus.text = it }
        binding.progressBarWorkout.progress = 0
    }

    private fun completeWorkout() {
        activeWorkout?.let { workout ->
            val caloriesBurned = calculateCaloriesBurned(workout)
            WorkoutRepository.completeWorkout(workout, caloriesBurned)
            Toast.makeText(
                requireContext(),
                "Workout complete! You burned $caloriesBurned kcal.",
                Toast.LENGTH_SHORT
            ).show()
        }
        resetUI()
    }

    private fun finishWorkout() {
        activeWorkout?.let { workout ->
            workoutTimer?.cancel()
            val caloriesBurned = calculateCaloriesBurned(workout)
            Toast.makeText(
                requireContext(),
                "Workout ended early! You burned $caloriesBurned kcal.",
                Toast.LENGTH_SHORT
            ).show()

            WorkoutRepository.completeWorkout(workout, caloriesBurned)
        }
        resetUI()
    }

    private fun calculateCaloriesBurned(workout: Workout): Int {
        val elapsedMillis = System.currentTimeMillis() - startTimeMillis
        val elapsedMinutes = elapsedMillis / 60000.0
        val caloriesPerMinute = workout.caloriesBurned.toDouble() / workout.duration
        return (caloriesPerMinute * elapsedMinutes).toInt()
    }


    private fun resetUI() {
        activeWorkout = null
        workoutTimer?.cancel()
        defaultValues()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        workoutTimer?.cancel()
        _binding = null
    }
}
