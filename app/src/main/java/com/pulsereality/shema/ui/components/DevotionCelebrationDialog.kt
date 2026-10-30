package com.pulsereality.shema.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.pulsereality.shema.R
import com.pulsereality.shema.ui.theme.ShemaFonts
import com.pulsereality.shema.ui.theme.ShemaTheme

@Composable
fun DevotionCelebrationDialog(
    newStreak: Int,
    gemsEarned: Int,
    xpEarned: Int,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = ShemaTheme.colors.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, ShemaTheme.colors.border),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Streak Flame Artwork (1:1 with iOS streak animation)
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(ShemaTheme.colors.primary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.streak),
                        contentDescription = "Streak Flame",
                        modifier = Modifier.size(54.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "STREAK ACTIVE!",
                    style = ShemaFonts.app14Bold.copy(letterSpacing = 2.5.sp),
                    color = ShemaTheme.colors.gold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "$newStreak Days",
                    style = ShemaFonts.streakCounter,
                    color = ShemaTheme.colors.textPrimary
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Devotion Complete card
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = ShemaTheme.colors.primary.copy(alpha = 0.06f),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        ShemaTheme.colors.primary.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Devotion Complete!",
                            style = ShemaFonts.appHeadingTitle,
                            color = ShemaTheme.colors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "All 4 devotionals completed for today! Keep the flame burning in your heart.",
                            style = ShemaFonts.appSubheadline,
                            color = ShemaTheme.colors.textSecondary,
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ShemaTheme.colors.primary,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Awesome!",
                        style = ShemaFonts.appButtonSecondary
                    )
                }
            }
        }
    }
}
