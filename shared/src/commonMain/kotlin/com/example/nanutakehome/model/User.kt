package com.example.nanutakehome.model

enum class UserRole {
    PATIENT,
    EXPERT
}
data class AppUser(
    val uid: String = "",
    val displayName: String = "",
    val email: String = "",
    val role : UserRole = UserRole.PATIENT,
    val settingsTree: List<SettingsNode> = emptyList()
)

val newUser1 = AppUser(uid = "123", email = "a@b.com")
val newUser2 = AppUser(uid = "123", email = "a@b2.com" , role = UserRole.EXPERT)
