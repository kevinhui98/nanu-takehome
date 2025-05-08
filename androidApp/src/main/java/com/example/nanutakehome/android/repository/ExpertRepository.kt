package com.example.nanutakehome.android.repository

import android.util.Log
import com.example.nanutakehome.model.AppUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

object ExpertRepository {
    private val db = Firebase.firestore

    suspend fun fetchAllPatients(): List<AppUser> {
        return try {
            val snapshot = db.collection("users")
                .whereEqualTo("role", "PATIENT")
                .get()
                .await()

            snapshot.documents.mapNotNull { it.toObject(AppUser::class.java) }
        } catch (e: Exception) {
            Log.e("Firestore", "Error fetching patients", e)
            emptyList()
        }
    }
}
