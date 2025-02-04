package com.example.finalproject_fittrack.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject_fittrack.MainActivity
import com.example.finalproject_fittrack.R
import com.example.finalproject_fittrack.databinding.ItemWorkoutBinding
import com.example.finalproject_fittrack.interfaces.WorkoutCallback
import com.example.finalproject_fittrack.models.WorkoutModel
import com.example.finalproject_fittrack.ui.home.HomeFragment

class WorkoutAdapter(
    private var workouts: MutableList<WorkoutModel>,
    private val workoutCallback: WorkoutCallback
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
                    workoutCallback.onFavoriteClicked(getItem(adapterPosition), adapterPosition)
                }

                binding.workoutIMGStart.setOnClickListener {
                    workoutCallback.onStartWorkout(getItem(adapterPosition), adapterPosition)

                    (binding.root.context as? MainActivity)?.let { mainActivity ->
                        val homeFragment = mainActivity.supportFragmentManager
                            .findFragmentByTag("HomeFragment") as? HomeFragment
                        homeFragment?.addCalories(workout.caloriesBurned)
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
