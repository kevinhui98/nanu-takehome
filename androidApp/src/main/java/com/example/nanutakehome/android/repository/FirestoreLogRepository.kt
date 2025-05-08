package com.example.nanutakehome.android.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.LocalDate

object FirestoreLogRepository {
    private val db = Firebase.firestore
    private val user get() = FirebaseAuth.getInstance().currentUser

    fun uploadLog(date: LocalDate, note: String, onResult: (Boolean) -> Unit) {
        val uid = user?.uid
        if (uid == null) {
            onResult(false)
            return
        }

        val logData = mapOf(
            "date" to date.toString(),
            "note" to note
        )

        db.collection("users")
            .document(uid)
            .collection("calendarLogs")
            .document(date.toString())
            .set(logData)
            .addOnSuccessListener {
                Log.d("Firestore", "Log uploaded for $date")
                onResult(true)
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Failed to upload log for $date", e)
                onResult(false)
            }
    }
}
