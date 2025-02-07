package com.example.finalproject_fittrack.dataBase

import com.example.finalproject_fittrack.dataBase.FirebaseRepository.getCurrentUserId
import com.example.finalproject_fittrack.dataBase.FirebaseRepository.getUserReference
import com.example.finalproject_fittrack.models.DailyGoalManager
import com.google.firebase.database.*
import com.example.finalproject_fittrack.models.Workout

object WorkoutRepository {

    fun saveDefaultWorkouts() {
        val userId = getCurrentUserId() ?: return
        val defaultWorkouts = listOf(
            Workout.Builder()
                .name("Chest Workout")
                .description("Exercises to develop the chest muscles")
                .imageRes("chest")
                .category("Strength")
                .caloriesBurned(250)
                .duration(45)
                .build(),

            Workout.Builder()
                .name("Back Workout")
                .description("Exercises to strengthen the back")
                .imageRes("back")
                .category("Strength")
                .caloriesBurned(200)
                .duration(40)
                .build(),

            Workout.Builder()
                .name("Shoulder Workout")
                .description("Exercises for stronger shoulders")
                .imageRes("shoulders")
                .category("Strength")
                .caloriesBurned(150)
                .duration(35)
                .build(),

            Workout.Builder()
                .name("Leg Workout")
                .description("Exercises to strengthen the legs")
                .imageRes("legs")
                .category("Strength")
                .caloriesBurned(300)
                .duration(50)
                .build(),

            Workout.Builder()
                .name("Abs Workout")
                .description("Exercises to strengthen the core")
                .imageRes("abs")
                .category("Strength")
                .caloriesBurned(100)
                .duration(30)
                .build(),

            Workout.Builder()
                .name("Arms Workout")
                .description("Exercises for biceps and triceps")
                .imageRes("arms")
                .category("Strength")
                .caloriesBurned(150)
                .duration(30)
                .build(),

            Workout.Builder()
                .name("Jump Rope")
                .description("High-intensity cardio workout")
                .imageRes("jump_rope")
                .category("Cardio")
                .caloriesBurned(200)
                .duration(20)
                .build(),

            Workout.Builder()
                .name("Spinning")
                .description("Indoor cycling exercise")
                .imageRes("spinning")
                .category("Cardio")
                .caloriesBurned(300)
                .duration(40)
                .build(),

            Workout.Builder()
                .name("Ski Machine")
                .description("Simulated skiing exercise")
                .imageRes("ski")
                .category("Cardio")
                .caloriesBurned(250)
                .duration(35)
                .build(),

            Workout.Builder()
                .name("Running")
                .description("Cardiovascular endurance training")
                .imageRes("running")
                .category("Cardio")
                .caloriesBurned(400)
                .duration(60)
                .build(),

            Workout.Builder()
                .name("Escalate")
                .description("Simulated stair climbing")
                .imageRes("escalate")
                .category("Cardio")
                .caloriesBurned(350)
                .duration(45)
                .build(),

            Workout.Builder()
                .name("Rowing")
                .description("Full-body workout using a rowing machine")
                .imageRes("rowing")
                .category("Cardio")
                .caloriesBurned(300)
                .duration(40)
                .build()
        )

        FirebaseRepository.database.child(userId).child("workouts").setValue(defaultWorkouts)
    }

    fun loadWorkouts(category: String, onComplete: (List<Workout>) -> Unit) {
        val userRef = getUserReference() ?: return
        userRef.child("workouts").orderByChild("category").equalTo(category)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val workouts =
                        snapshot.children.mapNotNull { it.getValue(Workout::class.java) }
                    onComplete(workouts)
                }

                override fun onCancelled(error: DatabaseError) {
                    onComplete(emptyList())
                }
            })
    }

    fun loadFavoriteWorkouts(onComplete: (List<Workout>) -> Unit) {
        val userId = getCurrentUserId() ?: return
        FirebaseRepository.database.child(userId).child("favorite_workouts")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val favorites =
                        snapshot.children.mapNotNull { it.getValue(Workout::class.java) }
                    onComplete(favorites)
                }

                override fun onCancelled(error: DatabaseError) {
                    onComplete(emptyList())
                }
            })
    }

    fun updateFavoriteStatus(workout: Workout, onComplete: () -> Unit) {
        val userId = getCurrentUserId() ?: return
        workout.isFavorite = !workout.isFavorite

        FirebaseRepository.database.child(userId).child("favorite_workouts")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val favorites =
                        snapshot.children.mapNotNull { it.getValue(Workout::class.java) }
                            .toMutableList()

                    if (workout.isFavorite) {
                        favorites.add(workout)
                    } else {
                        favorites.removeAll { it.name == workout.name }
                    }

                    FirebaseRepository.database.child(userId).child("favorite_workouts")
                        .setValue(favorites)
                        .addOnCompleteListener { onComplete() }
                }

                override fun onCancelled(error: DatabaseError) {
                    onComplete()
                }
            })
    }

    fun setActiveWorkout(workout: Workout) {
        val userId = getCurrentUserId() ?: return
        workout.isInProgress = true
        FirebaseRepository.database.child(userId).child("active_workout").setValue(workout)
    }

    fun getActiveWorkout(onComplete: (Workout?) -> Unit) {
        val userId = getCurrentUserId() ?: return
        FirebaseRepository.database.child(userId).child("active_workout")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val workout = snapshot.getValue(Workout::class.java)
                    onComplete(workout)
                }

                override fun onCancelled(error: DatabaseError) {
                    onComplete(null)
                }
            })
    }

    fun completeWorkout(workout: Workout, caloriesBurned: Int) {
        val userId = getCurrentUserId() ?: return
        workout.isInProgress = false

        val workoutData = workout.copy(caloriesBurned = caloriesBurned)
        FirebaseRepository.database.child(userId).child("workout_history").push()
            .setValue(workoutData)

            DailyGoalManager.getDailyProgress { currentProgress ->
            val updatedCalories = currentProgress.caloriesBurned + caloriesBurned
            DailyGoalManager.updateCaloriesBurned(updatedCalories) { _ -> }
        }

        FirebaseRepository.database.child(userId).child("active_workout").removeValue()
    }

    fun checkAndSaveDefaultWorkouts() {
        val userRef = getUserReference() ?: return
        userRef.child("workouts")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!snapshot.exists()) {
                        saveDefaultWorkouts()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    fun isWorkoutActive(onComplete: (Boolean) -> Unit) {
        val userId = getCurrentUserId() ?: return
        FirebaseRepository.database.child(userId).child("active_workout")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val isActive =
                        snapshot.exists() && snapshot.getValue(Workout::class.java) != null
                    onComplete(isActive)
                }

                override fun onCancelled(error: DatabaseError) {
                    onComplete(false)
                }
            })
    }
}
