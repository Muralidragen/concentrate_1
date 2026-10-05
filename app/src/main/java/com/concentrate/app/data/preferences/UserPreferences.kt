package com.concentrate.app.data.preferences

import android.content.Context
import android.content.SharedPreferences
import com.concentrate.app.data.model.ActivePass
import com.concentrate.app.data.model.JeeSubject
import com.concentrate.app.data.model.SessionMode
import com.concentrate.app.data.model.Wallet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserPreferences(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _isLockdownActive = MutableStateFlow(prefs.getBoolean(KEY_LOCKDOWN_ACTIVE, false))
    val isLockdownActive: StateFlow<Boolean> = _isLockdownActive.asStateFlow()

    private val _lockdownExpiresAt = MutableStateFlow(prefs.getLong(KEY_LOCKDOWN_EXPIRES_AT, 0L))
    val lockdownExpiresAt: StateFlow<Long> = _lockdownExpiresAt.asStateFlow()

    private val _sessionMode = MutableStateFlow(
        SessionMode.valueOf(prefs.getString(KEY_SESSION_MODE, SessionMode.VAULT.name) ?: SessionMode.VAULT.name)
    )
    val sessionMode: StateFlow<SessionMode> = _sessionMode.asStateFlow()

    private val _currentSubject = MutableStateFlow(
        JeeSubject.valueOf(prefs.getString(KEY_CURRENT_SUBJECT, JeeSubject.PHYSICS.name) ?: JeeSubject.PHYSICS.name)
    )
    val currentSubject: StateFlow<JeeSubject> = _currentSubject.asStateFlow()

    private val _whitelist = MutableStateFlow(prefs.getStringSet(KEY_WHITELIST, setOf()) ?: emptySet())
    val whitelist: StateFlow<Set<String>> = _whitelist.asStateFlow()

    private val _blacklist = MutableStateFlow(
        prefs.getStringSet(KEY_BLACKLIST, setOf("com.instagram.android", "com.google.android.youtube", "com.zhiliaoapp.musically")) ?: emptySet()
    )
    val blacklist: StateFlow<Set<String>> = _blacklist.asStateFlow()

    private val _wallet = MutableStateFlow(
        Wallet(
            points = prefs.getInt(KEY_WALLET_POINTS, 250),
            diamonds = prefs.getInt(KEY_WALLET_DIAMONDS, 4),
            streakDays = prefs.getInt(KEY_STREAK_DAYS, 5),
            totalFocusMinutes = prefs.getInt(KEY_TOTAL_FOCUS_MINUTES, 540),
            totalProblemsSolved = prefs.getInt(KEY_TOTAL_PROBLEMS, 72),
            totalSlashedPoints = prefs.getInt(KEY_TOTAL_SLASHED, 0)
        )
    )
    val wallet: StateFlow<Wallet> = _wallet.asStateFlow()

    private val _activePass = MutableStateFlow<ActivePass?>(loadActivePass())
    val activePass: StateFlow<ActivePass?> = _activePass.asStateFlow()

    fun startLockdown(durationMinutes: Int, mode: SessionMode, subject: JeeSubject) {
        val expiresAt = System.currentTimeMillis() + (durationMinutes * 60 * 1000L)
        prefs.edit()
            .putBoolean(KEY_LOCKDOWN_ACTIVE, true)
            .putLong(KEY_LOCKDOWN_EXPIRES_AT, expiresAt)
            .putString(KEY_SESSION_MODE, mode.name)
            .putString(KEY_CURRENT_SUBJECT, subject.name)
            .apply()

        _isLockdownActive.value = true
        _lockdownExpiresAt.value = expiresAt
        _sessionMode.value = mode
        _currentSubject.value = subject
    }

    fun endLockdown(completedSuccessfully: Boolean, durationMinutes: Int = 0, problemsSolved: Int = 0) {
        val currentWallet = _wallet.value
        val updatedWallet = if (completedSuccessfully) {
            val earnedPoints = currentWallet.calculatePayout(durationMinutes, _sessionMode.value)
            val bonusDiamonds = if (durationMinutes >= 45) 1 else 0
            currentWallet.copy(
                points = currentWallet.points + earnedPoints,
                diamonds = currentWallet.diamonds + bonusDiamonds,
                totalFocusMinutes = currentWallet.totalFocusMinutes + durationMinutes,
                totalProblemsSolved = currentWallet.totalProblemsSolved + problemsSolved,
                streakDays = currentWallet.streakDays + 1
            )
        } else {
            // Slashing penalty: Burn 50 points or 20% of balance on failure
            val penalty = (currentWallet.points * 0.20f).toInt().coerceAtLeast(30)
            currentWallet.copy(
                points = (currentWallet.points - penalty).coerceAtLeast(0),
                totalSlashedPoints = currentWallet.totalSlashedPoints + penalty,
                streakDays = 0 // Break streak
            )
        }

        saveWallet(updatedWallet)

        prefs.edit()
            .putBoolean(KEY_LOCKDOWN_ACTIVE, false)
            .putLong(KEY_LOCKDOWN_EXPIRES_AT, 0L)
            .apply()

        _isLockdownActive.value = false
        _lockdownExpiresAt.value = 0L
    }

    fun setSessionMode(mode: SessionMode) {
        prefs.edit().putString(KEY_SESSION_MODE, mode.name).apply()
        _sessionMode.value = mode
    }

    fun setCurrentSubject(subject: JeeSubject) {
        prefs.edit().putString(KEY_CURRENT_SUBJECT, subject.name).apply()
        _currentSubject.value = subject
    }

    /**
     * Strict 4-App Limit on Whitelist
     */
    fun toggleWhitelistApp(packageName: String): Boolean {
        val current = _whitelist.value.toMutableSet()
        if (current.contains(packageName)) {
            current.remove(packageName)
        } else {
            if (current.size >= 4) {
                return false // Rejected! Hard 4-app limit reached.
            }
            current.add(packageName)
        }
        prefs.edit().putStringSet(KEY_WHITELIST, current).apply()
        _whitelist.value = current
        return true
    }

    fun toggleBlacklistApp(packageName: String) {
        val current = _blacklist.value.toMutableSet()
        if (current.contains(packageName)) {
            current.remove(packageName)
        } else {
            current.add(packageName)
        }
        prefs.edit().putStringSet(KEY_BLACKLIST, current).apply()
        _blacklist.value = current
    }

    fun activatePass(pass: ActivePass, pointCost: Int, diamondCost: Int): Boolean {
        val currentWallet = _wallet.value
        if (currentWallet.points < pointCost || currentWallet.diamonds < diamondCost) {
            return false
        }

        val updatedWallet = currentWallet.copy(
            points = currentWallet.points - pointCost,
            diamonds = currentWallet.diamonds - diamondCost
        )
        saveWallet(updatedWallet)

        prefs.edit()
            .putString(KEY_ACTIVE_PASS_ID, pass.templateId)
            .putString(KEY_ACTIVE_PASS_TITLE, pass.title)
            .putString(KEY_ACTIVE_PASS_PKG, pass.targetPackage ?: "")
            .putLong(KEY_ACTIVE_PASS_EXP, pass.expiresAtTimestamp)
            .putInt(KEY_ACTIVE_PASS_DUR, pass.durationMinutes)
            .apply()

        _activePass.value = pass
        return true
    }

    fun clearActivePass() {
        prefs.edit()
            .remove(KEY_ACTIVE_PASS_ID)
            .remove(KEY_ACTIVE_PASS_TITLE)
            .remove(KEY_ACTIVE_PASS_PKG)
            .remove(KEY_ACTIVE_PASS_EXP)
            .remove(KEY_ACTIVE_PASS_DUR)
            .apply()
        _activePass.value = null
    }

    private fun loadActivePass(): ActivePass? {
        val id = prefs.getString(KEY_ACTIVE_PASS_ID, null) ?: return null
        val title = prefs.getString(KEY_ACTIVE_PASS_TITLE, "") ?: ""
        val pkg = prefs.getString(KEY_ACTIVE_PASS_PKG, "")
        val exp = prefs.getLong(KEY_ACTIVE_PASS_EXP, 0L)
        val dur = prefs.getInt(KEY_ACTIVE_PASS_DUR, 15)
        if (System.currentTimeMillis() >= exp) return null
        return ActivePass(
            templateId = id,
            title = title,
            targetPackage = if (pkg.isNullOrEmpty()) null else pkg,
            expiresAtTimestamp = exp,
            durationMinutes = dur
        )
    }

    private fun saveWallet(wallet: Wallet) {
        prefs.edit()
            .putInt(KEY_WALLET_POINTS, wallet.points)
            .putInt(KEY_WALLET_DIAMONDS, wallet.diamonds)
            .putInt(KEY_STREAK_DAYS, wallet.streakDays)
            .putInt(KEY_TOTAL_FOCUS_MINUTES, wallet.totalFocusMinutes)
            .putInt(KEY_TOTAL_PROBLEMS, wallet.totalProblemsSolved)
            .putInt(KEY_TOTAL_SLASHED, wallet.totalSlashedPoints)
            .apply()
        _wallet.value = wallet
    }

    companion object {
        private const val PREFS_NAME = "concentrate_lockdown_prefs"
        private const val KEY_LOCKDOWN_ACTIVE = "lockdown_active"
        private const val KEY_LOCKDOWN_EXPIRES_AT = "lockdown_expires_at"
        private const val KEY_SESSION_MODE = "session_mode"
        private const val KEY_CURRENT_SUBJECT = "current_subject"
        private const val KEY_WHITELIST = "whitelist_packages"
        private const val KEY_BLACKLIST = "blacklist_packages"
        private const val KEY_WALLET_POINTS = "wallet_points"
        private const val KEY_WALLET_DIAMONDS = "wallet_diamonds"
        private const val KEY_STREAK_DAYS = "wallet_streak_days"
        private const val KEY_TOTAL_FOCUS_MINUTES = "total_focus_minutes"
        private const val KEY_TOTAL_PROBLEMS = "total_problems_solved"
        private const val KEY_TOTAL_SLASHED = "total_slashed_points"

        private const val KEY_ACTIVE_PASS_ID = "active_pass_id"
        private const val KEY_ACTIVE_PASS_TITLE = "active_pass_title"
        private const val KEY_ACTIVE_PASS_PKG = "active_pass_pkg"
        private const val KEY_ACTIVE_PASS_EXP = "active_pass_exp"
        private const val KEY_ACTIVE_PASS_DUR = "active_pass_dur"
    }
}
