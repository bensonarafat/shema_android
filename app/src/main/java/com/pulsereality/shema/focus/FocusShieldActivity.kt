package com.pulsereality.shema.focus

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pulsereality.shema.MainActivity
import com.pulsereality.shema.ui.theme.BackgroundDark
import com.pulsereality.shema.ui.theme.BorderDark
import com.pulsereality.shema.ui.theme.BrandGreen
import com.pulsereality.shema.ui.theme.FjallaOneFontFamily
import com.pulsereality.shema.ui.theme.GoldAccent
import com.pulsereality.shema.ui.theme.PrimaryBlue
import com.pulsereality.shema.ui.theme.ShemaTheme
import com.pulsereality.shema.ui.theme.SurfaceDark
import com.pulsereality.shema.ui.theme.SurfaceSecondaryDark
import com.pulsereality.shema.ui.theme.TextMutedDark
import com.pulsereality.shema.ui.theme.TextPrimaryDark
import com.pulsereality.shema.ui.theme.TextSecondaryDark

class FocusShieldActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val blockedPackage = intent.getStringExtra("EXTRA_BLOCKED_PACKAGE") ?: "Social App"
        val focusManager = FocusPreferenceManager(this)

        setContent {
            ShemaTheme(darkTheme = true) {
                FocusShieldContent(
                    blockedPackage = blockedPackage,
                    onOpenShema = {
                        val mainIntent = Intent(this, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                            putExtra("EXTRA_START_DESTINATION", "devotion_reader/passage")
                        }
                        startActivity(mainIntent)
                        finish()
                    },
                    onUnlockTemporary = {
                        focusManager.grantTemporaryUnlock(minutes = 5)
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
fun FocusShieldContent(
    blockedPackage: String,
    onOpenShema: () -> Unit,
    onUnlockTemporary: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        PrimaryBlue.copy(alpha = 0.25f),
                        BackgroundDark,
                        BackgroundDark
                    )
                )
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            border = androidx.compose.foundation.BorderStroke(1.2.dp, BorderDark)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Shield Glowing Icon
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .clip(CircleShape)
                        .background(PrimaryBlue.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.HourglassTop,
                        contentDescription = "Shield Active",
                        tint = PrimaryBlue,
                        modifier = Modifier.size(44.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(PrimaryBlue.copy(alpha = 0.18f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "SHEMA FOCUS SHIELD",
                        style = MaterialTheme.typography.labelSmall,
                        color = PrimaryBlue,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Headline matching iOS ShieldConfigurationExtension
                Text(
                    text = "Time for the Word",
                    fontFamily = FjallaOneFontFamily,
                    fontSize = 32.sp,
                    color = TextPrimaryDark,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Your daily devotional is waiting. Spend a few intentional moments in Scripture and prayer before opening other apps.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondaryDark,
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Blocked App indicator pill
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SurfaceSecondaryDark
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = GoldAccent,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "$blockedPackage is paused",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondaryDark
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Primary Action Button ("Open Shema")
                Button(
                    onClick = onOpenShema,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.MenuBook,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Open Shema Devotion",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Secondary Action Button ("Unlock for 5 Minutes")
                OutlinedButton(
                    onClick = onUnlockTemporary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
                ) {
                    Text(
                        text = "Unlock for 5 Minutes",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondaryDark
                    )
                }
            }
        }
    }
}
