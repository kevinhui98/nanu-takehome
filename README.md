# Nanu Take-Home App

This is a Kotlin Multiplatform Mobile (KMM) application designed to support two types of users: **patients** and **experts**. 
Patients can log daily notes and customize a nested settings tree. 
Experts have access to a dashboard where they can view all patients and inspect each patient's current settings.

---

## Data Structure and Approach

### AppUser (Firestore Document)
Each user is stored in Firestore with the following schema:

```kotlin
data class AppUser(
    val uid: String,
    val displayName: String,
    val email: String,
    val role: UserRole,
    val settingsTree: List<SettingsNode> = sampleSettingsTree,
    val logs: Map<String, String> = emptyMap()
)
```
Settings Tree
- A recursive data structure (SettingsNode) supports parent-child relationships for user settings:
```kotlin
data class SettingsNode(
    val label: String,
    val checked: Boolean = false,
    val isExpanded: Boolean = false,
    val children: List<SettingsNode> = emptyList()
)
```
Settings are:
- Toggleable: Toggling a parent updates all children.
- Upward reactive: Changing any child re-evaluates the parent's state.
- Expandable/collapsible: With animated visibility.

## Logs
- Logs are stored per day using LocalDate.toString() as keys:
```kotlin
"logs": {
  "2025-05-08": "Today I felt good after therapy.",
  "2025-05-07": "Stressed about work."
}
```
## Feature Overview
### Patient Portal
- Calendar with Logs: Users can select a date, write notes, and submit them to Firestore.
- Settings Tree: Users configure their settings using nested checkboxes. Updates are saved to Firestore.

### Expert Dashboard
- Experts can view all patients.
- Each patient expands to show their full settingsTree in read-only mode.

### Role Assignment
- After Google Sign-In, users who are new must choose their role.
- Returning users are automatically routed based on the saved role in Firestore.

### Performance Notes & Enhancements
- State Management: All UI state is remembered and properly scoped with LaunchedEffect and recomposition-safe practices.
- Efficient Firestore Usage:
  - The app avoids overwriting user data (e.g., keeps settingsTree intact unless updated).
  - Firestore update() is used to reduce the document overwrite size.
- Settings Caching: The settingsTree loads from Firestore only once per session.
- Animated Visibility: Used in the settings UI and expert view for a smooth UX without performance cost.

## Technologies
- Kotlin Multiplatform (KMM)
- Jetpack Compose (Android)
- Firebase Auth + Firestore
- Google Sign-In
- Material 3 UI

### App Walkthough GIF

<img src="walkthrough.webm" width=250><br>
https://imgur.com/a/kxvs83y
## Notes

### Challenges Faced

- **Google Sign-In Errors (`ApiException 10` & `12502`)**  
  Solved by ensuring the correct Web Client ID was used, SHA-1 was added in Firebase, and the OAuth consent screen was properly configured.

- **Firestore Deserialization Errors**  
  Resolved issues like `no-arg constructor` for `SettingsNode` by using Kotlin data classes with default values and ensuring the Firestore SDK could deserialize nested structures.

- **State Sync Across Sessions**  
  Encountered bugs where `settingsTree` was reset after logout. Fixed by checking Firestore before setting a default tree.

- **Nested Checkbox Logic**  
  Handling parent/child syncing (e.g. toggling one child should uncheck the parent) was tricky. Achieved correctness by recursively updating the state in both directions.

- **UI Animation & Layout Overlap**  
  Managed Compose `AnimatedVisibility` transitions and spacing issues to keep the interface clean and accessible.

---

### Features To Add Next

- **Firestore Sync Status**  
  Add visual indicators (e.g., a Snackbar or icon) when a user’s settings or log is successfully saved or fails to save.

- **Role Switching from UI**  
  Enable switching between "Patient" and "Expert" from within the app rather than only at onboarding.

- **Cross-Platform iOS UI**  
  The shared logic is in place; add SwiftUI frontend to support iOS using Kotlin Multiplatform.

---



