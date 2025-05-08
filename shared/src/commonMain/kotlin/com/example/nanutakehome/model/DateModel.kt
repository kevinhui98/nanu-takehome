package com.example.nanutakehome.model

data class SettingsNode(
    val label: String ="",
    val checked: Boolean = false,
    val children: List<SettingsNode> = emptyList(),
    val isExpanded: Boolean = true
)

val sampleSettingsTree = listOf(
    SettingsNode("settings", children = listOf(
        SettingsNode("notifications", children = listOf(
            SettingsNode("email", checked = true),
            SettingsNode("sms", checked = false),
            SettingsNode("push", children = listOf(
                SettingsNode("android", checked = false),
                SettingsNode("ios", checked = true)
            ))
        )),
        SettingsNode("privacy", children = listOf(
            SettingsNode("location", checked = false),
            SettingsNode("camera", checked = true),
            SettingsNode("microphone", checked = false)
        )),
        SettingsNode("security", children = listOf(
            SettingsNode("twoFactorAuth", checked = false),
            SettingsNode("backupCodes", checked = true)
        ))
    )),
    SettingsNode("preferences", children = listOf(
        SettingsNode("theme", children = listOf(
            SettingsNode("darkMode", checked = false),
            SettingsNode("highContrast", checked = false)
        )),
        SettingsNode("language", children = listOf(
            SettingsNode("english", checked = true),
            SettingsNode("spanish", checked = false),
            SettingsNode("nested", children = listOf(
                SettingsNode("regionalDialects", children = listOf(
                    SettingsNode("catalan", checked = true),
                    SettingsNode("quechua", checked = false)
                ))
            ))
        ))
    )),
    SettingsNode("integrations", children = listOf(
        SettingsNode("slack", checked = false),
        SettingsNode("github", children = listOf(
            SettingsNode("issues", checked = true),
            SettingsNode("pullRequests", checked = false)
        )),
        SettingsNode("jira", children = listOf(
            SettingsNode("basic", checked = false),
            SettingsNode("advanced", children = listOf(
                SettingsNode("workflows", checked = true),
                SettingsNode("automations", checked = false)
            ))
        ))
    ))
)
