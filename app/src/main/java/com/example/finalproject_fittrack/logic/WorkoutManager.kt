package com.example.finalproject_fittrack.logic

import WorkoutModel
import com.example.finalproject_fittrack.utilities.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.example.finalproject_fittrack.R

object WorkoutManager {

    private val database: DatabaseReference = FirebaseDatabase.getInstance().getReference(Constants.Firebase.USERS_REF)
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    fun loadWorkouts(category: String, onComplete: (List<WorkoutModel>) -> Unit) {
        val userId = getCurrentUserId() ?: return
        database.child(userId).child("workouts").orderByChild("category").equalTo(category)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val workouts = snapshot.children.mapNotNull { it.getValue(WorkoutModel::class.java) }
                    onComplete(workouts)
                }
                override fun onCancelled(error: DatabaseError) {
                    onComplete(emptyList())
                }
            })
    }

    fun saveDefaultWorkouts() {
        val userId = getCurrentUserId() ?: return
        val defaultWorkouts = listOf(
            WorkoutModel.Builder()
                .name("Chest Workout")
                .description("Exercises to develop the chest muscles")
                .imageRes("chest")
                .category("Strength")
                .caloriesBurned(250)
                .duration(45)
                .build(),

            WorkoutModel.Builder()
                .name("Back Workout")
                .description("Exercises to strengthen the back")
                .imageRes("back")
                .category("Strength")
                .caloriesBurned(200)
                .duration(40)
                .build(),

            WorkoutModel.Builder()
                .name("Shoulder Workout")
                .description("Exercises for stronger shoulders")
                .imageRes("shoulders")
                .category("Strength")
                .caloriesBurned(150)
                .duration(35)
                .build(),

            WorkoutModel.Builder()
                .name("Leg Workout")
                .description("Exercises to strengthen the legs")
                .imageRes("legs")
                .category("Strength")
                .caloriesBurned(300)
                .duration(50)
                .build(),

            WorkoutModel.Builder()
                .name("Abs Workout")
                .description("Exercises to strengthen the core")
                .imageRes("abs")
                .category("Strength")
                .caloriesBurned(100)
                .duration(30)
                .build(),

            WorkoutModel.Builder()
                .name("Arms Workout")
                .description("Exercises for biceps and triceps")
                .imageRes("arms")
                .category("Strength")
                .caloriesBurned(150)
                .duration(30)
                .build(),

            WorkoutModel.Builder()
                .name("Jump Rope")
                .description("High-intensity cardio workout")
                .imageRes("jump_rope")
                .category("Cardio")
                .caloriesBurned(200)
                .duration(20)
                .build(),

            WorkoutModel.Builder()
                .name("Spinning")
                .description("Indoor cycling exercise")
                .imageRes("spinning")
                .category("Cardio")
                .caloriesBurned(300)
                .duration(40)
                .build(),

            WorkoutModel.Builder()
                .name("Ski Machine")
                .description("Simulated skiing exercise")
                .imageRes("ski")
                .category("Cardio")
                .caloriesBurned(250)
                .duration(35)
                .build(),

            WorkoutModel.Builder()
                .name("Running")
                .description("Cardiovascular endurance training")
                .imageRes("running")
                .category("Cardio")
                .caloriesBurned(400)
                .duration(60)
                .build(),

            WorkoutModel.Builder()
                .name("Escalate")
                .description("Simulated stair climbing")
                .imageRes("escalate")
                .category("Cardio")
                .caloriesBurned(350)
                .duration(45)
                .build(),

            WorkoutModel.Builder()
                .name("Rowing")
                .description("Full-body workout using a rowing machine")
                .imageRes("rowing")
                .category("Cardio")
                .caloriesBurned(300)
                .duration(40)
                .build()
        )

        database.child(userId).child("workouts").setValue(defaultWorkouts)
    }

    private fun saveFavoriteWorkouts(favorites: List<WorkoutModel>) {
        val userId = getCurrentUserId() ?: return
        database.child(userId).child("favorite_workouts").setValue(favorites)
    }

    fun loadFavoriteWorkouts(onComplete: (List<WorkoutModel>) -> Unit) {
        val userId = getCurrentUserId() ?: return
        database.child(userId).child("favorite_workouts")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val favorites = snapshot.children.mapNotNull { it.getValue(WorkoutModel::class.java) }
                    onComplete(favorites)
                }
                override fun onCancelled(error: DatabaseError) {
                    onComplete(emptyList())
                }
            })
    }

    fun updateFavoriteStatus(workout: WorkoutModel, onComplete: () -> Unit) {
        val userId = getCurrentUserId() ?: return
        workout.isFavorite = !workout.isFavorite

        database.child(userId).child("favorite_workouts")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val favorites = snapshot.children.mapNotNull { it.getValue(WorkoutModel::class.java) }.toMutableList()

                    if (workout.isFavorite) {
                        favorites.add(workout)
                    } else {
                        favorites.removeAll { it.name == workout.name }
                    }

                    database.child(userId).child("favorite_workouts").setValue(favorites)
                        .addOnCompleteListener { onComplete() }
                }

                override fun onCancelled(error: DatabaseError) {
                    onComplete()
                }
            })
    }

    fun setActiveWorkout(workout: WorkoutModel) {
        val userId = getCurrentUserId() ?: return
        workout.isInProgress = true
        database.child(userId).child("active_workout").setValue(workout)
    }

    fun getActiveWorkout(onComplete: (WorkoutModel?) -> Unit) {
        val userId = getCurrentUserId() ?: return
        database.child(userId).child("active_workout")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val workout = snapshot.getValue(WorkoutModel::class.java)
                    onComplete(workout)
                }
                override fun onCancelled(error: DatabaseError) {
                    onComplete(null)
                }
            })
    }

    fun completeWorkout(workout: WorkoutModel, caloriesBurned: Int) {
        val userId = getCurrentUserId() ?: return
        workout.isInProgress = false

        val workoutData = workout.copy(caloriesBurned = caloriesBurned)
        database.child(userId).child("workout_history").push().setValue(workoutData)

        val userRef = database.child(userId)
        userRef.child("calories_burned").get().addOnSuccessListener { snapshot ->
            val currentCalories = snapshot.getValue(Int::class.java) ?: 0
            val updatedCalories = currentCalories + caloriesBurned
            userRef.child("calories_burned").setValue(updatedCalories)
        }

        database.child(userId).child("active_workout").removeValue()
    }

    fun isWorkoutActive(onComplete: (Boolean) -> Unit) {
        val userId = getCurrentUserId() ?: return
        database.child(userId).child("active_workout")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val isActive = snapshot.exists() && snapshot.getValue(WorkoutModel::class.java) != null
                    onComplete(isActive)
                }

                override fun onCancelled(error: DatabaseError) {
                    onComplete(false)
                }
            })
    }
}
