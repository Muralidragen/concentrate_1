package com.concentrate.app.data.model

enum class SessionMode(
    val title: String,
    val subtitle: String,
    val basePointRatePerMin: Int,
    val description: String
) {
    VAULT(
        title = "The Vault",
        subtitle = "Intense Lockdown",
        basePointRatePerMin = 10,
        description = "Total isolation. All apps blocked except system dialer and up to 4 chosen tools. Rubber-band barrier enforced."
    ),
    SNIPER(
        title = "The Sniper",
        subtitle = "Tactical Pomodoro",
        basePointRatePerMin = 5,
        description = "Distraction-targeted. Blacklisted apps are silently eliminated back to home screen without heavy overlays."
    ),
    NTA_EXAM(
        title = "NTA Exam Slot",
        subtitle = "3-Hour Deep Simulation",
        basePointRatePerMin = 15,
        description = "Simulates official JEE Mains & Advanced exam slot (180 mins). Zero exceptions. Auto-DND engaged."
    )
}
