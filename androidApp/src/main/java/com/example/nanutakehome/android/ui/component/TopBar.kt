package com.example.nanutakehome.android.ui.component

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarWithLogout(onLogout: () -> Unit, title:String) {
    TopAppBar(
        title = { Text("$title") },
        actions = {
            TextButton(onClick = onLogout) {
                Text("Log out", color = MaterialTheme.colorScheme.primary)
            }
        }
    )
}