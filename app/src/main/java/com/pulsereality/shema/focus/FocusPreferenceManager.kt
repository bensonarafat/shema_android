package com.pulsereality.shema.focus

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Process
import android.provider.Settings
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class FocusPreferenceManager(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("shema_focus_preferences", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_SHIELD_ENABLED = "key_shield_enabled"
        private const val KEY_START_HOUR = "key_start_hour"
        private const val KEY_START_MINUTE = "key_start_minute"
        private const val KEY_END_HOUR = "key_end_hour"
        private const val KEY_END_MINUTE = "key_end_minute"
        private const val KEY_BLOCKED_PACKAGES = "key_blocked_packages"
        private const val KEY_DEVOTION_COMPLETED_DATE = "key_devotion_completed_date"
        private const val KEY_TEMP_UNLOCK_UNTIL = "key_temp_unlock_until"

        val POPULAR_DISTRACTING_APPS = listOf(
            AppInfo("com.instagram.android", "Instagram", "Social"),
            AppInfo("com.zhiliaoapp.musically", "TikTok", "Social"),
            AppInfo("com.twitter.android", "X / Twitter", "Social"),
            AppInfo("com.google.android.youtube", "YouTube", "Video"),
            AppInfo("com.reddit.frontpage", "Reddit", "Social"),
            AppInfo("com.facebook.katana", "Facebook", "Social"),
            AppInfo("com.netflix.mediaclient", "Netflix", "Entertainment"),
            AppInfo("com.android.chrome", "Chrome", "Browser")
        )
    }

    data class AppInfo(
        val packageName: String,
        val appName: String,
        val category: String
    )

    var isShieldEnabled: Boolean
        get() = prefs.getBoolean(KEY_SHIELD_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_SHIELD_ENABLED, value).apply()

    var startHour: Int
        get() = prefs.getInt(KEY_START_HOUR, 7) // 7:00 AM
        set(value) = prefs.edit().putInt(KEY_START_HOUR, value).apply()

    var startMinute: Int
        get() = prefs.getInt(KEY_START_MINUTE, 0)
        set(value) = prefs.edit().putInt(KEY_START_MINUTE, value).apply()

    var endHour: Int
        get() = prefs.getInt(KEY_END_HOUR, 8) // 8:30 AM
        set(value) = prefs.edit().putInt(KEY_END_HOUR, value).apply()

    var endMinute: Int
        get() = prefs.getInt(KEY_END_MINUTE, 30)
        set(value) = prefs.edit().putInt(KEY_END_MINUTE, value).apply()

    var blockedPackages: Set<String>
        get() = prefs.getStringSet(
            KEY_BLOCKED_PACKAGES,
            setOf(
                "com.instagram.android",
                "com.zhiliaoapp.musically",
                "com.twitter.android",
                "com.google.android.youtube",
                "com.reddit.frontpage",
                "com.facebook.katana"
            )
        ) ?: emptySet()
        set(value) = prefs.edit().putStringSet(KEY_BLOCKED_PACKAGES, value).apply()

    fun markDevotionCompleted() {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        prefs.edit().putString(KEY_DEVOTION_COMPLETED_DATE, today).apply()
    }

    fun isDevotionCompletedToday(): Boolean {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        val saved = prefs.getString(KEY_DEVOTION_COMPLETED_DATE, "")
        return today == saved
    }

    fun grantTemporaryUnlock(minutes: Int = 5) {
        val unlockUntil = System.currentTimeMillis() + (minutes * 60 * 1000)
        prefs.edit().putLong(KEY_TEMP_UNLOCK_UNTIL, unlockUntil).apply()
    }

    fun isTemporaryUnlockActive(): Boolean {
        val unlockUntil = prefs.getLong(KEY_TEMP_UNLOCK_UNTIL, 0L)
        return System.currentTimeMillis() < unlockUntil
    }

    /**
     * Checks if the current time falls within the scheduled focus window
     */
    fun isWithinScheduledHours(): Boolean {
        val now = Calendar.getInstance()
        val currentMinutes = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE)
        val startMinutes = startHour * 60 + startMinute
        val endMinutes = endHour * 60 + endMinute

        return if (startMinutes <= endMinutes) {
            currentMinutes in startMinutes..endMinutes
        } else {
            // Over midnight schedule
            currentMinutes >= startMinutes || currentMinutes <= endMinutes
        }
    }

    /**
     * Checks if a target app should be shielded right now:
     * 1. Shield master toggle is enabled
     * 2. Package is in the blocked list
     * 3. Today's devotion has NOT been completed
     * 4. Temporary unlock window has expired
     * 5. Current time is within devotion focus hours
     */
    fun shouldShieldPackage(packageName: String): Boolean {
        if (!isShieldEnabled) return false
        if (!blockedPackages.contains(packageName)) return false
        if (isDevotionCompletedToday()) return false
        if (isTemporaryUnlockActive()) return false
        if (!isWithinScheduledHours()) return false

        return true
    }

    // Permission Checkers
    fun hasUsageStatsPermission(): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as? AppOpsManager ?: return false
        val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            appOps.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(),
                context.packageName
            )
        } else {
            @Suppress("DEPRECATION")
            appOps.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(),
                context.packageName
            )
        }
        return mode == AppOpsManager.MODE_ALLOWED
    }

    fun hasOverlayPermission(): Boolean {
        return Settings.canDrawOverlays(context)
    }
}
