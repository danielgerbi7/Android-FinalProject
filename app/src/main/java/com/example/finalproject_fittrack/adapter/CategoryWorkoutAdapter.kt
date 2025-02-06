package com.example.finalproject_fittrack.adapter

import android.widget.Toast
import com.example.finalproject_fittrack.dataBase.WorkoutRepository
import com.example.finalproject_fittrack.interfaces.WorkoutFavoriteCallback
import com.example.finalproject_fittrack.interfaces.WorkoutProgressCallback
import com.example.finalproject_fittrack.models.WorkoutModel

class CategoryWorkoutAdapter(
    workouts: MutableList<WorkoutModel>,
    private val workoutFavoriteCallback: WorkoutFavoriteCallback,
    private val workoutProgressCallback: WorkoutProgressCallback
) : BaseWorkoutAdapter(workouts) {

    override fun onBindViewHolder(holder: BaseWorkoutViewHolder, position: Int) {
        val workout = getItem(position)
        bindCommonWorkoutData(holder, workout)

        with(holder.binding) {
            workoutIMGFavorite.setOnClickListener {
                workoutFavoriteCallback.onFavoriteClicked(workout, position)
            }

            workoutIMGStart.setOnClickListener {
                WorkoutRepository.isWorkoutActive { isActive ->
                    if (isActive) {
                        Toast.makeText(
                            root.context,
                            "A workout is already in progress!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        WorkoutRepository.setActiveWorkout(workout)
                        workoutProgressCallback.onStartWorkout(workout, position)
                    }
                }
            }
        }
    }
}