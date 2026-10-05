package com.concentrate.app.service

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.telecom.TelecomManager

object EmergencyDialerShield {

    private val knownDialerPackages = setOf(
        "com.google.android.dialer",       // Pixel, Android One, Motorola
        "com.samsung.android.dialer",      // Samsung OneUI
        "com.android.phone",               // AOSP & System Phone
        "com.android.server.telecom",      // Android Telecom
        "com.miui.home",                   // Xiaomi
        "com.oneplus.dialer",              // OnePlus
        "com.oppo.dialer",                 // Oppo
        "com.vivo.dialer"                  // Vivo
    )

    fun isEmergencyOrDialer(context: Context, packageName: String?): Boolean {
        if (packageName == null) return false
        if (knownDialerPackages.contains(packageName)) return true

        // Check if package is default telecom dialer
        val telecomManager = context.getSystemService(Context.TELECOM_SERVICE) as? TelecomManager
        val defaultDialer = telecomManager?.defaultDialerPackage
        if (defaultDialer != null && defaultDialer == packageName) {
            return true
        }

        // Check if package resolves dialer action
        val dialIntent = Intent(Intent.ACTION_DIAL)
        val resolveInfo = context.packageManager.resolveActivity(dialIntent, PackageManager.MATCH_DEFAULT_ONLY)
        if (resolveInfo?.activityInfo?.packageName == packageName) {
            return true
        }

        // Never block our own app or system settings
        if (packageName == context.packageName || packageName == "com.android.settings") {
            return true
        }

        return false
    }
}
