package com.pulsereality.shema.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.Highlight
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pulsereality.shema.R
import com.pulsereality.shema.ui.theme.ShemaFonts
import com.pulsereality.shema.ui.theme.ShemaTheme

@Composable
fun MeScreen(
    onNavigateToSettings: (String) -> Unit = {},
    onUpgradeClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ShemaTheme.colors.background)
            .padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(6.dp)) }

        // MARK: Profile Section (1:1 with iOS MeView profileSection)
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToSettings("edit_profile") },
                shape = RoundedCornerShape(16.dp),
                color = ShemaTheme.colors.background,
                border = androidx.compose.foundation.BorderStroke(1.dp, ShemaTheme.colors.tertiaryFill)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.male_avatar),
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Benson Arafat",
                            style = ShemaFonts.appFootnote,
                            color = ShemaTheme.colors.textPrimary
                        )

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(ShemaTheme.colors.tertiaryFill)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "@benson",
                                style = ShemaFonts.app13SemiBold,
                                color = ShemaTheme.colors.textSecondary
                            )
                        }
                    }
                }
            }
        }

        // MARK: Permission / Focus Shield Card
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToSettings("focus_settings") },
                shape = RoundedCornerShape(16.dp),
                color = ShemaTheme.colors.tertiaryFill
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(ShemaTheme.colors.primary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.HourglassTop,
                            contentDescription = null,
                            tint = ShemaTheme.colors.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Focus Shield & App Lock",
                            style = ShemaFonts.app16SemiBold,
                            color = ShemaTheme.colors.textPrimary
                        )
                        Text(
                            text = "Pauses distractions until devotion is finished",
                            style = ShemaFonts.appCaption,
                            color = ShemaTheme.colors.textSecondary
                        )
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = ShemaTheme.colors.textMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // MARK: Premium Upsell Card (1:1 with iOS PremiumCard)
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onUpgradeClick() },
                shape = RoundedCornerShape(16.dp),
                color = Color.Transparent
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF1A1035), Color(0xFF2E1760))
                            )
                        )
                        .padding(18.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text("👑", fontSize = 13.sp)
                                Text(
                                    text = "SHEMA PREMIUM",
                                    style = ShemaFonts.appMicro.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    ),
                                    color = ShemaTheme.colors.primary
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(ShemaTheme.colors.primary)
                                    .padding(horizontal = 12.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = "Upgrade",
                                    style = ShemaFonts.app13SemiBold,
                                    color = Color.Black
                                )
                            }
                        }

                        Text(
                            text = "Unlock deeper devotions,\nunlimited prayers & more.",
                            style = ShemaFonts.app14Bold,
                            color = Color.White,
                            lineHeight = 20.sp
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                            FeaturePill(icon = "📖", label = "Audio")
                            FeaturePill(icon = "✨", label = "Community")
                            FeaturePill(icon = "🔔", label = "Scripture Coach")
                        }
                    }
                }
            }
        }

        // MARK: Primary Menu Rows (Profile, Bookmarks, Bible Highlights, Settings)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                MeMenuRow(
                    icon = Icons.Default.Person,
                    title = "Profile",
                    onClick = { onNavigateToSettings("edit_profile") }
                )
                MeMenuRow(
                    icon = Icons.Default.Bookmark,
                    title = "Bookmarks",
                    crownBadge = true,
                    onClick = { onUpgradeClick() }
                )
                MeMenuRow(
                    icon = Icons.Default.Highlight,
                    title = "Bible Highlights",
                    onClick = { onNavigateToSettings("highlights") }
                )
                MeMenuRow(
                    icon = Icons.Default.Settings,
                    title = "Settings",
                    onClick = { onNavigateToSettings("settings") }
                )
            }
        }

        // MARK: Support Card (Help & Support, Send Feedback, Share Shema)
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = ShemaTheme.colors.tertiaryFill.copy(alpha = 0.35f),
                border = androidx.compose.foundation.BorderStroke(1.dp, ShemaTheme.colors.tertiaryFill)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    MeCardRow(
                        icon = Icons.Default.QuestionMark,
                        title = "Help & Support",
                        onClick = {
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:support@tryshema.com?subject=Help%20%26%20Support")
                            }
                            context.startActivity(intent)
                        }
                    )
                    HorizontalDivider(color = ShemaTheme.colors.border.copy(alpha = 0.4f), modifier = Modifier.padding(start = 54.dp))
                    MeCardRow(
                        icon = Icons.Default.Feedback,
                        title = "Send Feedback",
                        onClick = {
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:feedback@tryshema.com?subject=App%20Feedback")
                            }
                            context.startActivity(intent)
                        }
                    )
                    HorizontalDivider(color = ShemaTheme.colors.border.copy(alpha = 0.4f), modifier = Modifier.padding(start = 54.dp))
                    MeCardRow(
                        icon = Icons.Default.Share,
                        title = "Share Shema",
                        onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, "Stay connected with God every day! Check out Shema: https://www.tryshema.com")
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share Shema"))
                        }
                    )
                }
            }
        }

        // MARK: Legal Card (Terms and Conditions, Privacy Policy)
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = ShemaTheme.colors.tertiaryFill.copy(alpha = 0.35f),
                border = androidx.compose.foundation.BorderStroke(1.dp, ShemaTheme.colors.tertiaryFill)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    MeCardRow(
                        icon = Icons.Default.Policy,
                        title = "Terms and Conditions",
                        hasExternalLink = true,
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.tryshema.com/terms.html"))
                            context.startActivity(intent)
                        }
                    )
                    HorizontalDivider(color = ShemaTheme.colors.border.copy(alpha = 0.4f), modifier = Modifier.padding(start = 54.dp))
                    MeCardRow(
                        icon = Icons.Default.Lock,
                        title = "Privacy Policy",
                        hasExternalLink = true,
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.tryshema.com/privacy.html"))
                            context.startActivity(intent)
                        }
                    )
                }
            }
        }

        // Version Label
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "VERSION 1.2.0",
                    style = ShemaFonts.app14Bold,
                    color = ShemaTheme.colors.textMuted
                )
            }
        }

        item { Spacer(modifier = Modifier.height(70.dp)) }
    }
}

@Composable
private fun FeaturePill(icon: String, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(icon, fontSize = 11.sp)
        Text(
            text = label,
            style = ShemaFonts.appMicro,
            color = Color.White.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun MeMenuRow(
    icon: ImageVector,
    title: String,
    crownBadge: Boolean = false,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = ShemaTheme.colors.tertiaryFill.copy(alpha = 0.35f),
        border = androidx.compose.foundation.BorderStroke(1.dp, ShemaTheme.colors.tertiaryFill)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(ShemaTheme.colors.tertiaryFill.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = ShemaTheme.colors.textPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Text(
                text = title,
                style = ShemaFonts.app16SemiBold,
                color = ShemaTheme.colors.textPrimary,
                modifier = Modifier.weight(1f)
            )

            if (crownBadge) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFB800).copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("👑", fontSize = 11.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = ShemaTheme.colors.textMuted,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun MeCardRow(
    icon: ImageVector,
    title: String,
    hasExternalLink: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(ShemaTheme.colors.tertiaryFill.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = ShemaTheme.colors.textPrimary,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Text(
            text = title,
            style = ShemaFonts.app16SemiBold,
            color = ShemaTheme.colors.textPrimary,
            modifier = Modifier.weight(1f)
        )

        if (hasExternalLink) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                contentDescription = "External",
                tint = ShemaTheme.colors.textMuted,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
