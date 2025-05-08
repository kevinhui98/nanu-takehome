package com.example.nanutakehome.android

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.nanutakehome.android.repository.FirebaseUserRepository
import com.example.nanutakehome.android.ui.ExpertDashboardContainer
import com.example.nanutakehome.android.ui.WhoAreYouScreen
import com.example.nanutakehome.model.UserRole
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.example.nanutakehome.android.ui.PatientPortalScreen

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        val googleSignInClient = getGoogleSignInClient(this)
        setContent {
            MaterialTheme {
                // State to control what screen is shown
                var currentScreen by remember { mutableStateOf("login") }
                var userRole by remember { mutableStateOf<UserRole?>(null) }
                var userId by remember { mutableStateOf<String?>(null) }

                val launcher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartActivityForResult()
                ) { result ->
                    val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    try {
                        val account = task.getResult(ApiException::class.java)
                        val credential = GoogleAuthProvider.getCredential(account.idToken, null)

                        auth.signInWithCredential(credential)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val user = auth.currentUser
                                    if (user != null) {
                                        userId = user.uid
                                        FirebaseUserRepository.fetchUser(user.uid) { fetchedUser ->
                                            if (fetchedUser?.role != null) {
                                                userRole = fetchedUser.role
                                                currentScreen = fetchedUser.role.name.lowercase()
                                            } else {
                                                currentScreen = "who"
                                            }
                                        }
                                    }
                                } else {
                                    Log.e("SignIn", "Firebase sign-in failed", task.exception)
                                }
                            }
                    } catch (e: ApiException) {
                        Log.e("SignIn", "Google sign-in failed", e)
                    }
                }

                when (currentScreen) {
                    "login" -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            GoogleSignInButton {
                                launcher.launch(googleSignInClient.signInIntent)
                            }
                        }
                    }

                    "who" -> {
                        WhoAreYouScreen { selectedRole ->
                            auth.currentUser?.let { user ->
                                userId = user.uid
                                FirebaseUserRepository.fetchUser(user.uid) { fetchedUser ->
                                    val finalRole = selectedRole
                                    val existingTree = fetchedUser?.settingsTree
                                    FirebaseUserRepository.createOrUpdateUser(
                                        firebaseUser = user,
                                        role = finalRole,
                                        settingsTree = existingTree // ✅ Preserve if exists
                                    ) { updated ->
                                        if (updated) {
                                            userRole = finalRole
                                            currentScreen = finalRole.name.lowercase()
                                        }
                                    }
                                }

                            }
                        }
                    }

                    "patient" -> {
                        userId?.let { uid ->
                            PatientPortalScreen(
                                userId = uid,
                                onLogout = {
                                    logout(this) {
                                        userId = null
                                        userRole = null
                                        currentScreen = "login"
                                    }
                                }
                            )
                        }
                    }

                    "expert" -> {
                        ExpertDashboardContainer(
                            onLogout = {
                                logout(this) {
                                    currentScreen = "login"
                                }
                            }
                        )
                    }

                    else -> {
                        Text("Loading...")
                    }
                }
            }
        }
    }
}
