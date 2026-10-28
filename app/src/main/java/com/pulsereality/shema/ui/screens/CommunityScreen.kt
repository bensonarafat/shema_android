package com.pulsereality.shema.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pulsereality.shema.ui.components.SubmitPrayerBottomSheet
import com.pulsereality.shema.ui.theme.BackgroundDark
import com.pulsereality.shema.ui.theme.BorderDark
import com.pulsereality.shema.ui.theme.BrandGreen
import com.pulsereality.shema.ui.theme.GoldAccent
import com.pulsereality.shema.ui.theme.PrimaryBlue
import com.pulsereality.shema.ui.theme.SurfaceDark
import com.pulsereality.shema.ui.theme.SurfaceSecondaryDark
import com.pulsereality.shema.ui.theme.TextMutedDark
import com.pulsereality.shema.ui.theme.TextPrimaryDark
import com.pulsereality.shema.ui.theme.TextSecondaryDark
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
    val selectedFilter by viewModel.selectedFilter.collectAsState()

    var isSubmitSheetOpen by remember { mutableStateOf(false) }

    val filterCategories = listOf("All", "Prayer Need", "Thanksgiving", "Healing", "Family")

    val formattedCountdown = remember(countdownSeconds) {
        val minutes = countdownSeconds / 60
        val seconds = countdownSeconds % 60
        String.format(java.util.Locale.US, "%02d:%02d", minutes, seconds)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BackgroundDark,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { isSubmitSheetOpen = true },
                containerColor = PrimaryBlue,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.padding(bottom = 60.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Submit Prayer Request"
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Prayer Network",
                    style = MaterialTheme.typography.displayMedium,
                    color = TextPrimaryDark
                )
                Text(
                    text = "Bear one another’s burdens, and so fulfill the law of Christ",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondaryDark
                )
            }

            // Live Rotating Active Prayer Hero Card
            activePrayer?.let { active ->
                val hasPrayed = prayedRequests.contains(active.id)
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(BrandGreen.copy(alpha = 0.15f))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(BrandGreen)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "LIVE INTERCESSION",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = BrandGreen,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Schedule,
                                        contentDescription = null,
                                        tint = TextMutedDark,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Rotates in $formattedCountdown",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextMutedDark
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = active.title,
                                style = MaterialTheme.typography.titleLarge,
                                color = TextPrimaryDark
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Submitted by ${active.displayName}",
                                style = MaterialTheme.typography.bodySmall,
                                color = PrimaryBlue
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = active.body,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondaryDark,
                                lineHeight = 22.sp
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = { viewModel.prayForRequest(active.id) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (hasPrayed) BrandGreen else PrimaryBlue
                                )
                            ) {
                                Icon(
                                    imageVector = if (hasPrayed) Icons.Default.Check else Icons.Default.Favorite,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (hasPrayed) "Prayed with ${active.prayCount} Believers" else "I Prayed for This (${active.prayCount})",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            // Category Filter Chips
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(filterCategories) { category ->
                        val isSelected = category == selectedFilter
                        Surface(
                            modifier = Modifier.clickable { viewModel.setFilter(category) },
                            shape = RoundedCornerShape(18.dp),
                            color = if (isSelected) PrimaryBlue else SurfaceDark,
                            border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, BorderDark) else null
                        ) {
                            Text(
                                text = category,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSelected) Color.White else TextSecondaryDark,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Community Requests",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimaryDark
                )
            }

            // Community Requests Feed
            items(communityPrayers, key = { it.id }) { prayer ->
                val hasPrayed = prayedRequests.contains(prayer.id)
                val prayedBg by animateColorAsState(
                    targetValue = if (hasPrayed) BrandGreen.copy(alpha = 0.15f) else SurfaceSecondaryDark,
                    label = "prayedBg"
                )

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = SurfaceDark,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = prayer.displayName,
                                style = MaterialTheme.typography.labelMedium,
                                color = PrimaryBlue,
                                fontWeight = FontWeight.Bold
                            )

                            // Quick Pray Button Pill
                            Surface(
                                modifier = Modifier.clickable { viewModel.prayForRequest(prayer.id) },
                                shape = RoundedCornerShape(12.dp),
                                color = prayedBg
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (hasPrayed) Icons.Default.Check else Icons.Default.Favorite,
                                        contentDescription = "Pray",
                                        tint = if (hasPrayed) BrandGreen else GoldAccent,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = "${prayer.prayCount}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (hasPrayed) BrandGreen else TextPrimaryDark,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = prayer.title,
                            style = MaterialTheme.typography.titleSmall,
                            color = TextPrimaryDark
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = prayer.body,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondaryDark,
                            lineHeight = 20.sp
                        )
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
