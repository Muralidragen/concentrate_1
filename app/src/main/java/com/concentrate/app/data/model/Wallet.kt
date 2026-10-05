package com.concentrate.app.data.model

data class Wallet(
    val points: Int = 160,
    val diamonds: Int = 5,
    val streakDays: Int = 3,
    val totalFocusMinutes: Int = 420,
    val totalProblemsSolved: Int = 68,
    val totalSlashedPoints: Int = 0
) {
    /**
     * Streak multiplier: Payout = BaseRate * (1 + 0.1 * Streak)
     */
    val streakMultiplier: Float
        get() = 1f + (streakDays * 0.1f)

    fun calculatePayout(minutes: Int, mode: SessionMode): Int {
        val base = minutes * mode.basePointRatePerMin
        return (base * streakMultiplier).toInt()
    }
}
