package com.example.nanutakehome.android.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.nanutakehome.model.UserRole

@Composable
fun WhoAreYouScreen(onRoleSelected: (UserRole) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Who are you?", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = { onRoleSelected(UserRole.PATIENT) }, modifier = Modifier.fillMaxWidth()) {
            Text("Patient")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { onRoleSelected(UserRole.EXPERT) }, modifier = Modifier.fillMaxWidth()) {
            Text("Expert")
        }
    }
}
