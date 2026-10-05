package com.concentrate.app.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import com.concentrate.app.data.model.SessionMode
import com.concentrate.app.data.preferences.UserPreferences
import com.concentrate.app.ui.screens.VaultOverlayActivity

class WardenAccessibility : AccessibilityService() {

    private lateinit var preferences: UserPreferences

    override fun onCreate() {
        super.onCreate()
        preferences = UserPreferences(this)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null || event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return

        if (!preferences.isLockdownActive.value) return

        val pkgName = event.packageName?.toString() ?: return

        if (EmergencyDialerShield.isEmergencyOrDialer(this, pkgName)) return

        val pass = preferences.activePass.value
        if (pass != null && !pass.isExpired && pass.targetPackage == pkgName) {
            return // Temporarily whitelisted by purchased pass
        }

        val mode = preferences.sessionMode.value
        when (mode) {
            SessionMode.VAULT, SessionMode.NTA_EXAM -> {
                val whitelist = preferences.whitelist.value
                if (!whitelist.contains(pkgName)) {
                    val overlayIntent = Intent(this, VaultOverlayActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    }
                    startActivity(overlayIntent)
                }
            }
            SessionMode.SNIPER -> {
                val blacklist = preferences.blacklist.value
                if (blacklist.contains(pkgName)) {
                    // Silent assassination: drop instantly back to home screen
                    performGlobalAction(GLOBAL_ACTION_HOME)
                }
            }
        }
    }

    override fun onInterrupt() {}
}
