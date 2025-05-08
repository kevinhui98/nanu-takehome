package com.example.nanutakehome.android

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun GoogleSignInButton(onClick: () -> Unit,
) {
    Button(onClick = onClick) {
        Text("Sign in with Google")
    }

}
@Preview(showBackground = true)
@Composable
fun GoogleSignInButtonPreview() {
    MaterialTheme {
        GoogleSignInButton(onClick = {})
    }
}
