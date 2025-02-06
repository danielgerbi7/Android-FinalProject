package com.example.finalproject_fittrack.models
import java.io.Serializable

data class WorkoutModel(
    var name: String = "",
    var description: String = "",
    var imageRes: Any = "",
    var category: String = "",
    var caloriesBurned: Int = 0,
    var duration: Int = 0,
    var isFavorite: Boolean = false,
    var isInProgress: Boolean = false
) : Serializable {

    fun getImageResAsString(): String {
        return if (imageRes is Long) {
            (imageRes as Long).toString()
        } else {
            imageRes as String
        }
    }

    class Builder {
        private var name: String = ""
        private var description: String = ""
        private var imageRes: Any = ""
        private var category: String = ""
        private var caloriesBurned: Int = 0
        private var duration: Int = 0
        private var isFavorite: Boolean = false
        private var isInProgress: Boolean = false

        fun name(name: String) = apply { this.name = name }
        fun description(description: String) = apply { this.description = description }
        fun imageRes(imageRes: Any) = apply { this.imageRes = imageRes }
        fun category(category: String) = apply { this.category = category }
        fun caloriesBurned(caloriesBurned: Int) = apply { this.caloriesBurned = caloriesBurned }
        fun duration(duration: Int) = apply { this.duration = duration }

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
