package com.example.finalproject_fittrack.dataBase

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.FirebaseDatabase

object LeaderboardRepository {

    private val database = FirebaseDatabase.getInstance().getReference("users")

    fun getLeaderboard(onComplete: (List<Pair<String, Int>>) -> Unit) {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val leaderboard = mutableListOf<Pair<String, Int>>()

                for (userSnapshot in snapshot.children) {
                    val name = userSnapshot.child("name").getValue(String::class.java) ?: "Unknown"
                    val caloriesBurned =
                        userSnapshot.child("calories_burned").getValue(Int::class.java) ?: 0
                    leaderboard.add(Pair(name, caloriesBurned))
                }

                leaderboard.sortByDescending { it.second }

                onComplete(leaderboard)
            }

            override fun onCancelled(error: DatabaseError) {
                onComplete(emptyList())
            }
        })
    }
}
