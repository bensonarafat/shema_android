package com.pulsereality.shema.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pulsereality.shema.data.model.SubscriptionPlan
import com.pulsereality.shema.ui.theme.ShemaFonts
import com.pulsereality.shema.ui.theme.ShemaTheme

@Composable
fun PricingScreen(
    onDismiss: () -> Unit,
    onSubscribed: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedPlanId by remember { mutableStateOf("com.shema.premium.yearly") }
    var isPurchasing by remember { mutableStateOf(false) }

    val proFeatures = listOf(
        "Daily Bible devotions & readings",
        "Unlimited Hymns & Psalms audio",
        "App lock until devotion is complete",
        "Share & receive prayer requests",
        "Bible study groups & community",
        "Streaks & accountability",
        "Unlimited Prayer request messages",
        "AI-powered devotional insights"
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = ShemaTheme.colors.background,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(ShemaTheme.colors.tertiaryFill)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = ShemaTheme.colors.textSecondary
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            // Header Titles (1:1 with iOS PricingView)
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Unlock your Spiritual Journey",
                    style = ShemaFonts.appHeadingTitle.copy(fontWeight = FontWeight.Bold),
                    color = ShemaTheme.colors.textPrimary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Build a intimate personal relationship with God.",
                    style = ShemaFonts.appSubheadline,
                    color = ShemaTheme.colors.textSecondary
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Pro Features List (1:1 with iOS PricingFeatureRow)
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                proFeatures.forEach { feature ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(ShemaTheme.colors.primary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = ShemaTheme.colors.primary,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                        Text(
                            text = feature,
                            style = ShemaFonts.appBody,
                            color = ShemaTheme.colors.textPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(color = ShemaTheme.colors.border.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(20.dp))

            // Subscription Plan Cards
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SubscriptionPlan.PLANS.forEach { plan ->
                    val isSelected = selectedPlanId == plan.id
                    val borderColor by animateColorAsState(
                        targetValue = if (isSelected) ShemaTheme.colors.primary else ShemaTheme.colors.border,
                        label = "planBorder"
                    )
                    val bgColor by animateColorAsState(
                        targetValue = if (isSelected) ShemaTheme.colors.primary.copy(alpha = 0.08f) else ShemaTheme.colors.surface,
                        label = "planBg"
                    )

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedPlanId = plan.id },
                        shape = RoundedCornerShape(16.dp),
                        color = bgColor,
                        border = androidx.compose.foundation.BorderStroke(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = borderColor
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = plan.title,
                                        style = ShemaFonts.app16Bold,
                                        color = ShemaTheme.colors.textPrimary
                                    )
                                    plan.tag?.let { tagText ->
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(ShemaTheme.colors.gold)
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = tagText,
                                                style = ShemaFonts.appMicro.copy(fontWeight = FontWeight.Bold),
                                                color = Color.Black
                                            )
                                        }
                                    }
                                }

                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        text = plan.price,
                                        style = ShemaFonts.app18Bold,
                                        color = ShemaTheme.colors.primary
                                    )
                                    Text(
                                        text = plan.period,
                                        style = ShemaFonts.appBodySmall,
                                        color = ShemaTheme.colors.textSecondary,
                                        modifier = Modifier.padding(bottom = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = plan.subtitle,
                                style = ShemaFonts.appCaption,
                                color = ShemaTheme.colors.textSecondary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Primary Purchase Button
            val selectedPlan = SubscriptionPlan.PLANS.firstOrNull { it.id == selectedPlanId }
            Button(
                onClick = {
                    isPurchasing = true
                    onSubscribed()
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ShemaTheme.colors.primary)
            ) {
                Text(
                    text = "Get Started",
                    style = ShemaFonts.appButtonSecondary,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Restore Purchases & Terms (1:1 with iOS)
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Restore purchases",
                    style = ShemaFonts.appCaption,
                    color = ShemaTheme.colors.textSecondary,
                    modifier = Modifier.clickable { }
                )

                Text(
                    text = "By continuing, you agree to Shema's Terms of Service and Privacy Policy. Subscriptions renew automatically unless cancelled.",
                    style = ShemaFonts.appSubFootnote,
                    color = ShemaTheme.colors.textMuted,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
