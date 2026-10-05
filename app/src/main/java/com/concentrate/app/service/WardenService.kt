package com.concentrate.app.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.concentrate.app.MainActivity
import com.concentrate.app.R
import com.concentrate.app.data.model.SessionMode
import com.concentrate.app.data.preferences.UserPreferences
import com.concentrate.app.ui.screens.VaultOverlayActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class WardenService : Service() {

    private val serviceJob = Job()
    private val scope = CoroutineScope(Dispatchers.Default + serviceJob)

    private lateinit var preferences: UserPreferences
    private lateinit var usageStatsManager: UsageStatsManager

    override fun onCreate() {
        super.onCreate()
        preferences = UserPreferences(this)
        usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = buildForegroundNotification("The Warden Armed", "Focus lockdown active.")
        startForeground(NOTIFICATION_ID, notification)

        DndManager.enableNotificationGuillotine(this)

        startSurveillanceLoop()

        return START_STICKY
    }

    private fun startSurveillanceLoop() {
        scope.launch {
            while (isActive) {
                if (!preferences.isLockdownActive.value) {
                    stopSelf()
                    break
                }

                val now = System.currentTimeMillis()
                val expiresAt = preferences.lockdownExpiresAt.value

                // Session completion check
                if (now >= expiresAt) {
                    preferences.endLockdown(completedSuccessfully = true, durationMinutes = 25)
                    DndManager.disableNotificationGuillotine(this@WardenService)
                    stopSelf()
                    break
                }

                // Check active pass guillotine drop
                val pass = preferences.activePass.value
                if (pass != null && pass.isExpired) {
                    preferences.clearActivePass()
                    dropToHomeScreen() // Guillotine Drop!
                }

                // Surveillance check
                val currentPkg = getForegroundPackage()
                if (currentPkg != null && !EmergencyDialerShield.isEmergencyOrDialer(this@WardenService, currentPkg)) {
                    val isTemporarilyAllowedByPass = pass != null && !pass.isExpired && pass.targetPackage == currentPkg

                    if (!isTemporarilyAllowedByPass) {
                        val mode = preferences.sessionMode.value
                        when (mode) {
                            SessionMode.VAULT, SessionMode.NTA_EXAM -> {
                                val whitelist = preferences.whitelist.value
                                if (!whitelist.contains(currentPkg)) {
                                    triggerVaultBarrier()
                                }
                            }
                            SessionMode.SNIPER -> {
                                val blacklist = preferences.blacklist.value
                                if (blacklist.contains(currentPkg)) {
                                    dropToHomeScreen() // Silent Assassination
                                }
                            }
                        }
                    }
                }

                delay(200) // 200ms reactive polling loop
            }
        }
    }

    private fun getForegroundPackage(): String? {
        val time = System.currentTimeMillis()
        val usageEvents = usageStatsManager.queryEvents(time - 1000 * 5, time)
        val event = UsageEvents.Event()
        var lastForegroundApp: String? = null

        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(event)
            if (event.eventType == UsageEvents.Event.ACTIVITY_RESUMED) {
                lastForegroundApp = event.packageName
            }
        }
        return lastForegroundApp
    }

    private fun triggerVaultBarrier() {
        val intent = Intent(this, VaultOverlayActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        startActivity(intent)
    }

    private fun dropToHomeScreen() {
        val homeIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(homeIntent)
    }

    private fun buildForegroundNotification(title: String, content: String): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.channel_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = getString(R.string.channel_description)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        DndManager.disableNotificationGuillotine(this)
        serviceJob.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        private const val CHANNEL_ID = "warden_channel"
        private const val NOTIFICATION_ID = 9001

        fun start(context: Context) {
            val intent = Intent(context, WardenService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, WardenService::class.java))
        }
    }
}
