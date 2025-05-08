package com.example.nanutakehome.model

data class PatientProfile(
    val id: String,
    val name: String,
    val settingsTree: List<SettingsNode> = sampleSettingsTree,
    var isExpanded: Boolean = false
)
