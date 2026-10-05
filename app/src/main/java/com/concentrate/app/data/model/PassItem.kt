package com.concentrate.app.data.model

data class PassTemplate(
    val id: String,
    val title: String,
    val targetPackage: String?,
    val durationMinutes: Int,
    val basePointCost: Int,
    val diamondCost: Int,
    val description: String,
    val isEmergencyAbort: Boolean = false
)

data class ActivePass(
    val templateId: String,
    val title: String,
    val targetPackage: String?,
    val expiresAtTimestamp: Long,
    val durationMinutes: Int
) {
    val isExpired: Boolean
        get() = System.currentTimeMillis() >= expiresAtTimestamp

    val remainingSeconds: Long
        get() = ((expiresAtTimestamp - System.currentTimeMillis()) / 1000).coerceAtLeast(0)
}
