package com.example.nanutakehome.android.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.nanutakehome.android.ui.component.TopAppBarWithLogout

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientPortalScreen(onLogout: () -> Unit,userId: String) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Settings", "Calendar")
    Scaffold(
        topBar = {
            TopAppBarWithLogout(onLogout = onLogout, title = "Patient")
        }
    ) { padding ->
    Column(modifier = Modifier.fillMaxSize()
        .padding(padding) ) {
        TabRow(
            selectedTabIndex = selectedTabIndex,

            modifier = Modifier.fillMaxWidth()
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (selectedTabIndex) {
            0 -> SettingsScreen(userId = userId)
            1 -> CalendarScreen()
        }
    }
    }
}


@Preview(showBackground = true)
@Composable
fun PatientPortalPreview() {
    MaterialTheme {
        PatientPortalScreen(userId = "hello", onLogout = {})
    }
}
