package com.pulsereality.shema.ui.screens

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.VolunteerActivism
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pulsereality.shema.R
import com.pulsereality.shema.data.model.PrayerRequest
import com.pulsereality.shema.ui.components.SubmitPrayerBottomSheet
import com.pulsereality.shema.ui.theme.ShemaFonts
import com.pulsereality.shema.ui.theme.ShemaTheme
import com.pulsereality.shema.ui.viewmodels.CommunityViewModel

@Composable
fun CommunityScreen(
    viewModel: CommunityViewModel,
    modifier: Modifier = Modifier
) {
    val activePrayer by viewModel.activePrayer.collectAsState()
    val communityPrayers by viewModel.communityPrayers.collectAsState()
    val prayedRequests by viewModel.prayedRequests.collectAsState()
    val countdownSeconds by viewModel.countdownSeconds.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) } // 0=Prayers, 1=Bible Study, 2=Topics
    var isSubmitSheetOpen by remember { mutableStateOf(false) }

    val tabs = listOf("Prayers", "Bible Study", "Topics")

    val formattedCountdown = remember(countdownSeconds) {
        val minutes = countdownSeconds / 60
        val seconds = countdownSeconds % 60
        String.format(java.util.Locale.US, "%02d:%02d", minutes, seconds)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = ShemaTheme.colors.background,
        floatingActionButton = {
            if (selectedTab != 2) {
                FloatingActionButton(
                    onClick = { isSubmitSheetOpen = true },
                    containerColor = ShemaTheme.colors.primary,
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier.padding(bottom = 60.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Submit Request",
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(6.dp)) }

            // MARK: Currently Praying For Live Card (1:1 with iOS CommunityView)
            if (selectedTab != 2 && activePrayer != null) {
                val active = activePrayer!!
                val hasPrayed = prayedRequests.contains(active.id)

                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🎯 Currently Praying For",
                                style = ShemaFonts.app16SemiBold,
                                color = ShemaTheme.colors.textSecondary
                            )

                            // Orange Countdown Badge
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(Color(0xFFF97316).copy(alpha = 0.15f))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Timer,
                                        contentDescription = null,
                                        tint = Color(0xFFF97316),
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = "$formattedCountdown left",
                                        style = ShemaFonts.appCaptionBold,
                                        color = Color(0xFFF97316)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Premium Card
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(22.dp),
                            color = ShemaTheme.colors.secondaryGroupedBg,
                            border = androidx.compose.foundation.BorderStroke(
                                1.5.dp,
                                ShemaTheme.colors.primary.copy(alpha = 0.25f)
                            ),
                            shadowElevation = 3.dp
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.female_avatar),
                                        contentDescription = "Avatar",
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(CircleShape)
                                    )

                                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                        Text(
                                            text = active.displayName,
                                            style = ShemaFonts.app16Bold,
                                            color = ShemaTheme.colors.textPrimary
                                        )

                                        Box(
                                            modifier = Modifier
                                                .clip(CircleShape)
                                                .background(ShemaTheme.colors.primary.copy(alpha = 0.15f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "PRAYER REQUEST",
                                                style = ShemaFonts.appMicro.copy(fontWeight = FontWeight.Bold),
                                                color = ShemaTheme.colors.primary
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = active.title,
                                    style = ShemaFonts.appHeadingTitle,
                                    color = ShemaTheme.colors.textPrimary
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = active.body,
                                    style = ShemaFonts.appBody,
                                    color = ShemaTheme.colors.textSecondary,
                                    maxLines = 4,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Spacer(modifier = Modifier.height(14.dp))
                                HorizontalDivider(color = ShemaTheme.colors.border.copy(alpha = 0.5f))
                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text("🙏", fontSize = 14.sp)
                                        Text(
                                            text = "${active.prayCount} prayers sent",
                                            style = ShemaFonts.appBodySmall,
                                            color = ShemaTheme.colors.textSecondary
                                        )
                                    }

                                    Button(
                                        onClick = { viewModel.prayForRequest(active.id) },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (hasPrayed) ShemaTheme.colors.green else ShemaTheme.colors.primary,
                                            contentColor = Color.White
                                        )
                                    ) {
                                        Icon(
                                            imageVector = if (hasPrayed) Icons.Default.Check else Icons.Outlined.VolunteerActivism,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (hasPrayed) "Prayed" else "I Prayed",
                                            style = ShemaFonts.appButtonSmall
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // MARK: Segmented Sliding Tabs (Prayers, Bible Study, Topics)
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        tabs.forEachIndexed { index, tabTitle ->
                            val isSelected = selectedTab == index
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedTab = index }
                                    .padding(vertical = 6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = tabTitle,
                                    style = ShemaFonts.app14Bold,
                                    color = if (isSelected) ShemaTheme.colors.primary else ShemaTheme.colors.textSecondary
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(fraction = 0.8f)
                                        .height(3.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) ShemaTheme.colors.primary else Color.Transparent)
                                )
                            }
                        }
                    }
                    HorizontalDivider(color = ShemaTheme.colors.border.copy(alpha = 0.5f))
                }
            }

            // MARK: Feed Cards or Topics View (1:1 with iOS FeedRowView)
            if (selectedTab == 2) {
                item {
                    ExploreTopicsSection(onViewAll = {})
                }
            } else {
                items(communityPrayers, key = { it.id }) { prayer ->
                    val hasPrayed = prayedRequests.contains(prayer.id)

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = ShemaTheme.colors.secondaryGroupedBg,
                        shadowElevation = 1.dp
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.male_avatar),
                                        contentDescription = "Avatar",
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                    )

                                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                        Text(
                                            text = prayer.displayName,
                                            style = ShemaFonts.app16Bold,
                                            color = ShemaTheme.colors.textPrimary
                                        )

                                        Box(
                                            modifier = Modifier
                                                .clip(CircleShape)
                                                .background(ShemaTheme.colors.primary.copy(alpha = 0.12f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = if (selectedTab == 0) "PRAYER" else "BIBLE STUDY",
                                                style = ShemaFonts.appMicro.copy(fontWeight = FontWeight.Bold),
                                                color = ShemaTheme.colors.primary
                                            )
                                        }
                                    }
                                }

                                Text(
                                    text = "2h ago",
                                    style = ShemaFonts.appCaption,
                                    color = ShemaTheme.colors.textSecondary
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = prayer.title,
                                style = ShemaFonts.app16SemiBold,
                                color = ShemaTheme.colors.textPrimary
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = prayer.body,
                                style = ShemaFonts.appBodyRegular,
                                color = ShemaTheme.colors.textSecondary,
                                maxLines = 5,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = ShemaTheme.colors.border.copy(alpha = 0.4f))
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Text(
                                        text = "🙏 ${prayer.prayCount} prayed",
                                        style = ShemaFonts.appCaption,
                                        color = ShemaTheme.colors.textSecondary
                                    )

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.QuestionAnswer,
                                            contentDescription = null,
                                            tint = ShemaTheme.colors.textMuted,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Text(
                                            text = "3 replies",
                                            style = ShemaFonts.appCaptionBold,
                                            color = ShemaTheme.colors.textSecondary
                                        )
                                    }
                                }

                                Surface(
                                    modifier = Modifier.clickable { viewModel.prayForRequest(prayer.id) },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (hasPrayed) ShemaTheme.colors.green.copy(alpha = 0.15f) else ShemaTheme.colors.primary.copy(alpha = 0.1f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (hasPrayed) Icons.Default.Check else Icons.Outlined.VolunteerActivism,
                                            contentDescription = "Pray",
                                            tint = if (hasPrayed) ShemaTheme.colors.green else ShemaTheme.colors.primary,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Text(
                                            text = if (hasPrayed) "Prayed" else "Pray",
                                            style = ShemaFonts.appCaptionBold,
                                            color = if (hasPrayed) ShemaTheme.colors.green else ShemaTheme.colors.primary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    if (isSubmitSheetOpen) {
        SubmitPrayerBottomSheet(
            onSubmit = { title, body, author, type ->
                viewModel.submitPrayer(title, body, author, type)
            },
            onDismiss = { isSubmitSheetOpen = false }
        )
    }
}
