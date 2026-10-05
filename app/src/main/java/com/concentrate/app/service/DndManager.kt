package com.concentrate.app.service

import android.app.NotificationManager
import android.content.Context
import android.os.Build

object DndManager {

    fun enableNotificationGuillotine(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        if (notificationManager?.isNotificationPolicyAccessGranted == true) {
            try {
                notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_PRIORITY)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun disableNotificationGuillotine(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        if (notificationManager?.isNotificationPolicyAccessGranted == true) {
            try {
                notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
