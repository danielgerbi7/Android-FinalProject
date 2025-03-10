package com.example.finalproject_fittrack.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject_fittrack.R
import com.example.finalproject_fittrack.databinding.ItemWorkoutBinding
import com.example.finalproject_fittrack.models.Workout

abstract class BaseWorkoutAdapter(
    private var workouts: MutableList<Workout>
) : RecyclerView.Adapter<BaseWorkoutAdapter.BaseWorkoutViewHolder>() {

    abstract override fun onBindViewHolder(holder: BaseWorkoutViewHolder, position: Int)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseWorkoutViewHolder {
        val binding = ItemWorkoutBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return BaseWorkoutViewHolder(binding)
    }

    override fun getItemCount(): Int = workouts.size

    protected fun getItem(position: Int) = workouts[position]

    class BaseWorkoutViewHolder(val binding: ItemWorkoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    protected fun bindCommonWorkoutData(holder: BaseWorkoutViewHolder, workout: Workout) {
        with(holder.binding) {

            val params = holder.itemView.layoutParams as RecyclerView.LayoutParams
            when (holder.adapterPosition) {
                workouts.size - 1 -> {
                    params.bottomMargin = 150
                }
                else -> {
                    params.bottomMargin = 0
                }
            }

            workoutLBLName.text = workout.name
            workoutLBLDescription.text = workout.description
            "${workout.caloriesBurned} kcal".also { workoutLBLCalories.text = it }
            "${workout.duration} min".also { workoutLBLDuration.text = it }

            val context = root.context
            val imageResource = context.resources.getIdentifier(
                workout.getImageResAsString(),
                "drawable",
                context.packageName
            )

            workoutIMGImage.setImageResource(
                if (imageResource != 0) imageResource else R.drawable.ic_workout_place
            )

            workoutIMGFavorite.setImageResource(
                if (workout.isFavorite) R.drawable.heart else R.drawable.empty_heart
            )
        }
    }
}