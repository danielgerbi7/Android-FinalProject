package com.example.finalproject_fittrack.dataBase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.example.finalproject_fittrack.utilities.Constants

object FirebaseRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val database: DatabaseReference =
        FirebaseDatabase.getInstance().getReference(Constants.Firebase.USERS_REF)

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    fun getUserReference(): DatabaseReference? {
        val userId = getCurrentUserId() ?: return null
        return database.child(userId)
    }

}
