package com.example.finalproject_fittrack.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject_fittrack.R
import com.example.finalproject_fittrack.databinding.ItemWorkoutBinding
import com.example.finalproject_fittrack.interfaces.WorkoutFavoriteCallback
import com.example.finalproject_fittrack.interfaces.WorkoutProgressCallback
import com.example.finalproject_fittrack.logic.WorkoutManager
import com.example.finalproject_fittrack.models.WorkoutModel

class WorkoutAdapter(

    private var workouts: MutableList<WorkoutModel>,
    private val workoutFavoriteCallback: WorkoutFavoriteCallback,
    private val workoutProgressCallback: WorkoutProgressCallback
) : RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val binding = ItemWorkoutBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return WorkoutViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        val workout = getItem(position)
        with(holder) {
            with(workout) {
                binding.workoutLBLName.text = name
                binding.workoutLBLDescription.text = description
                "$caloriesBurned kcal".also { binding.workoutLBLCalories.text = it }
                "$duration min".also { binding.workoutLBLDuration.text = it }

                binding.workoutIMGImage.setImageResource(workout.imageRes)

                binding.workoutIMGFavorite.setImageResource(
                    if (isFavorite) R.drawable.heart else R.drawable.empty_heart
                )

                binding.workoutIMGFavorite.setOnClickListener {
                    workoutFavoriteCallback.onFavoriteClicked(getItem(adapterPosition), adapterPosition)
                }

                binding.workoutIMGStart.setOnClickListener {
                    if (WorkoutManager.isWorkoutActive()) {
                        Toast.makeText(binding.root.context, "A workout is already in progress. Finish it first!", Toast.LENGTH_SHORT).show()
                    } else {
                        WorkoutManager.setActiveWorkout(workout)
                        workoutProgressCallback.onStartWorkout(getItem(adapterPosition), adapterPosition)
                        Toast.makeText(binding.root.context, "Workout started! Check the progress screen.", Toast.LENGTH_SHORT).show()
                        val navController = Navigation.findNavController(binding.root)
                        navController.navigate(R.id.navigation_progress)
                    }
                }

            }
        }
    }

    override fun getItemCount(): Int {
        return workouts.size
    }

    private fun getItem(position: Int) = workouts[position]

    inner class WorkoutViewHolder(val binding: ItemWorkoutBinding) :
        RecyclerView.ViewHolder(binding.root)
}
