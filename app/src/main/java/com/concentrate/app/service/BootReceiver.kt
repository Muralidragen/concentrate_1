package com.concentrate.app.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.concentrate.app.data.preferences.UserPreferences

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED || intent.action == "android.intent.action.QUICKBOOT_POWERON") {
            val prefs = UserPreferences(context)
            if (prefs.isLockdownActive.value && System.currentTimeMillis() < prefs.lockdownExpiresAt.value) {
                // Resume Warden surveillance on boot
                WardenService.start(context)
            }
        }
    }
}
