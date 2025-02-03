package com.example.finalproject_fittrack.ui.workout

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject_fittrack.R
import com.example.finalproject_fittrack.databinding.ItemWorkoutBinding
import com.example.finalproject_fittrack.models.WorkoutModel

class WorkoutAdapter(
    private var workoutList: MutableList<WorkoutModel>,
    private val onFavoriteClick: (WorkoutModel, Int) -> Unit,
    private val onItemClick: (WorkoutModel) -> Unit
) : RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder>() {

    inner class WorkoutViewHolder(val binding: ItemWorkoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val binding = ItemWorkoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WorkoutViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        val workout = workoutList[position]

        with(holder.binding) {
            workoutLBLName.text = workout.name
            workoutLBLDescription.text = workout.description
            workoutIMG.setImageResource(workout.imageRes)
            workoutLBLCalories.text = "${workout.caloriesBurned} kcal"

            workoutIMGFavorite.setImageResource(
                if (workout.isFavorite) R.drawable.heart else R.drawable.empty_heart
            )

            workoutIMGFavorite.setOnClickListener {
                workout.isFavorite = !workout.isFavorite
                onFavoriteClick(workout, position)
                notifyItemChanged(position)
            }
            root.setOnClickListener { onItemClick(workout) }
        }
    }

    override fun getItemCount(): Int = workoutList.size
}
