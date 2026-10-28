package com.pulsereality.shema

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.pulsereality.shema.focus.FocusMonitorService
import com.pulsereality.shema.focus.FocusPreferenceManager
import com.pulsereality.shema.media.ShemaAudioService
import com.pulsereality.shema.ui.MainScreen
import com.pulsereality.shema.ui.theme.ShemaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Start Media3 Audio Session Service for background playback
        val audioIntent = Intent(this, ShemaAudioService::class.java)
        startService(audioIntent)

        // If focus shield is enabled by the user, ensure monitor service is active
        val focusManager = FocusPreferenceManager(this)
        if (focusManager.isShieldEnabled && focusManager.hasUsageStatsPermission()) {
            FocusMonitorService.start(this)
        }

        setContent {
            ShemaTheme(darkTheme = true) {
                MainScreen()
            }
        }
    }
}
