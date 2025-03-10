package com.example.finalproject_fittrack.dataBase

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.FirebaseDatabase

object LeaderboardRepository {
    private val database = FirebaseDatabase.getInstance().getReference("users")

    fun getLeaderboard(onComplete: (List<Triple<String, String, Int>>) -> Unit) {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val leaderboard = mutableListOf<Triple<String, String, Int>>()

                for (userSnapshot in snapshot.children) {
                    val uid = userSnapshot.key ?: continue
                    val name = userSnapshot.child("name").getValue(String::class.java) ?: "Unknown"
                    val caloriesBurned = userSnapshot.child("calories_burned").getValue(Int::class.java) ?: 0

                    leaderboard.add(Triple(uid, name, caloriesBurned))
                }

                leaderboard.sortByDescending { it.third }
                onComplete(leaderboard)
            }

            override fun onCancelled(error: DatabaseError) {
                onComplete(emptyList())
            }
        })
    }
}


