package com.example.nanutakehome.android

import android.content.Context
import android.util.Log
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

fun getGoogleSignInClient(context: Context): GoogleSignInClient {
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(BuildConfig.WEB_CLIENT_ID)
        .requestEmail()
        .build()

    return GoogleSignIn.getClient(context, gso)
}
fun logout(context: Context, onLoggedOut: () -> Unit) {
    val googleSignInClient = getGoogleSignInClient(context)

    FirebaseAuth.getInstance().signOut()
    googleSignInClient.signOut().addOnCompleteListener {
        onLoggedOut()
    }
}
