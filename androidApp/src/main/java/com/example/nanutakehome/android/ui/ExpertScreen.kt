package com.example.nanutakehome.android.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.nanutakehome.android.repository.ExpertRepository
import com.example.nanutakehome.android.ui.component.TopAppBarWithLogout
import com.example.nanutakehome.model.AppUser
import com.example.nanutakehome.model.PatientProfile
import com.example.nanutakehome.model.SettingsNode
import com.example.nanutakehome.model.sampleSettingsTree

@Composable
fun ExpertDashboardContainer(onLogout: () -> Unit) {
    val appUsers = remember { mutableStateListOf<AppUser>() }

    LaunchedEffect(Unit) {
        val fetched = ExpertRepository.fetchAllPatients()
        appUsers.clear()
        appUsers.addAll(fetched)
    }

    val patientProfiles = appUsers.map {
        PatientProfile(
            id = it.uid,
            name = it.displayName,
            settingsTree = it.settingsTree
        )
    }

    ExpertDashboardScreen(patients = patientProfiles, onLogout = onLogout)
}

@Composable
fun ExpertDashboardScreen(patients: List<PatientProfile>,onLogout: () -> Unit = {}) {
    Scaffold(
        topBar = {
            TopAppBarWithLogout(onLogout = onLogout,title = "Expert")
        }
    ) { innerPadding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(innerPadding)
            .padding(16.dp)) {
            items(patients.size, key = { patients[it].id }) { index ->
                val appUser = patients[index]
                PatientCard(appUser)
            }
        }
    }
}

@Composable
fun PatientCard(patient: PatientProfile) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(patient.name, style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))

            IconButton(onClick = { isExpanded = !isExpanded }) {
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null
                )
            }
        }

        AnimatedVisibility(visible = isExpanded) {
            Column {
                patient.settingsTree.forEach { node ->
                    ReadOnlySettingsNodeView(node)
                }
            }
        }
    }
}
@Composable
fun ReadOnlySettingsNodeView(
    node: SettingsNode,
    indent: Int = 0
) {
    val isLeaf = node.children.isEmpty()
    var isExpanded by remember(node.label) { mutableStateOf(false) }

    Column(modifier = Modifier.padding(start = (indent * 16).dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = node.checked, onCheckedChange = null, enabled = false)
            Text(
                text = node.label,
                style = if (isLeaf) MaterialTheme.typography.bodyLarge else MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )
            if (!isLeaf) {
                IconButton(onClick = { isExpanded = !isExpanded }) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null
                    )
                }
            }
        }

        AnimatedVisibility(visible = isExpanded) {
            Column {
                node.children.forEach { child ->
                    ReadOnlySettingsNodeView(child, indent + 1)
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ExpertDashboardPreview() {
    val mockPatients = listOf(
        PatientProfile(
            id = "1",
            name = "Alice Smith",
            settingsTree = sampleSettingsTree
        ),
        PatientProfile(
            id = "2",
            name = "Bob Johnson",
            settingsTree = sampleSettingsTree
        )
    )

    ExpertDashboardScreen(patients = mockPatients)
}
