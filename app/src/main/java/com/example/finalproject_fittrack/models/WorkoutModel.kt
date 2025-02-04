package com.example.finalproject_fittrack.models

import java.io.Serializable

data class WorkoutModel private constructor(
    val name: String,
    val description: String,
    val imageRes: Int,
    val category: String,
    val caloriesBurned: Int,
    val duration: Int,
    var isFavorite: Boolean = false,
    var isInProgress: Boolean = false
): Serializable {

    class Builder {
        private var name: String = ""
        private var description: String = ""
        private var imageRes: Int = 0
        private var category: String = ""
        private var caloriesBurned: Int = 0
        private var duration: Int = 0
        private var isFavorite: Boolean = false
        private var isInProgress: Boolean = false

        fun name(name: String) = apply { this.name = name }
        fun description(description: String) = apply { this.description = description }
        fun imageRes(imageRes: Int) = apply { this.imageRes = imageRes }
        fun category(category: String) = apply { this.category = category }
        fun caloriesBurned(caloriesBurned: Int) = apply { this.caloriesBurned = caloriesBurned }
        fun duration(duration: Int) = apply { this.duration = duration }
        fun isFavorite(isFavorite: Boolean) = apply { this.isFavorite = isFavorite }
        fun isInProgress(isInProgress: Boolean) = apply { this.isInProgress = isInProgress }

        fun build() = WorkoutModel(
            name,
            description,
            imageRes,
            category,
            caloriesBurned,
            duration,
            isFavorite,
            isInProgress
        )
    }
}
