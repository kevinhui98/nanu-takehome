package com.example.nanutakehome.android.repository

import android.util.Log
import com.example.nanutakehome.model.AppUser
import com.example.nanutakehome.model.SettingsNode
import com.example.nanutakehome.model.UserRole
import com.example.nanutakehome.model.sampleSettingsTree
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object FirebaseUserRepository {

    private val db = Firebase.firestore

    fun createOrUpdateUser(
        firebaseUser: FirebaseUser,
        role: UserRole = UserRole.PATIENT,
        settingsTree: List<SettingsNode>? = null,
        onResult: (Boolean) -> Unit
    ) {
        val userRef = db.collection("users").document(firebaseUser.uid)

        // First, check if user already exists to avoid overwriting settingsTree
        userRef.get().addOnSuccessListener { document ->
            val existingUser = document.toObject(AppUser::class.java)

            val user = AppUser(
                uid = firebaseUser.uid,
                displayName = firebaseUser.displayName.orEmpty(),
                email = firebaseUser.email.orEmpty(),
                role = role,
                settingsTree = settingsTree ?: existingUser?.settingsTree ?: sampleSettingsTree
            )

            userRef.set(user)
                .addOnSuccessListener {
                    Log.d("Firestore", "User document successfully written")
                    onResult(true)
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Error writing user document", e)
                    onResult(false)
                }
        }.addOnFailureListener { e ->
            Log.e("Firestore", "Error checking existing user", e)
            onResult(false)
        }
    }

    fun fetchUser(uid: String, onResult: (AppUser?) -> Unit) {
        db.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val user = document.toObject(AppUser::class.java)
                    onResult(user)
                } else {
                    onResult(null)
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error fetching user", e)
                onResult(null)
            }
    }
    fun updateSettingsTree(userId: String, tree: List<SettingsNode>) {
        val db = com.google.firebase.Firebase.firestore

        db.collection("users")
            .document(userId)
            .update("settingsTree", tree)
            .addOnSuccessListener {
                Log.d("Firestore", "Settings updated successfully")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error updating settings", e)
            }
    }

}