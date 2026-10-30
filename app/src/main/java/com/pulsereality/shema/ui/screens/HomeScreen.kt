package com.pulsereality.shema.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.NaturePeople
import androidx.compose.material.icons.outlined.VolunteerActivism
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pulsereality.shema.R
import com.pulsereality.shema.data.model.Devotion
import com.pulsereality.shema.data.model.DevotionLocale
import com.pulsereality.shema.data.model.Hymn
import com.pulsereality.shema.ui.theme.ShemaFonts
import com.pulsereality.shema.ui.theme.ShemaTheme
import com.pulsereality.shema.ui.viewmodels.DevotionUiState
import com.pulsereality.shema.ui.viewmodels.HomeViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    onNavigateToDevotionStep: (String) -> Unit = {},
    onPlayHymn: (Hymn) -> Unit = {},
    onNavigateToCommunity: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by homeViewModel.uiState.collectAsState()
    val completedSteps by homeViewModel.completedSteps.collectAsState()
    val streakDays by homeViewModel.streakDays.collectAsState()

    val currentDateFormatted = remember {
        SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
    }

    val devotionCore: Devotion? = (uiState as? DevotionUiState.Success)?.core
    val devotionLocale: DevotionLocale? = (uiState as? DevotionUiState.Success)?.locale

    var openCardIndex by remember { mutableIntStateOf(0) }
    var isBookmarked by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ShemaTheme.colors.background)
            .padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // MARK: - Top User Bar (1:1 with iOS HomeView header)
        item {
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // User Avatar + Full Name & @username pill
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.male_avatar),
                        contentDescription = "User Avatar",
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
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

                // Right Streak Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.streak),
                        contentDescription = "Streak",
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "$streakDays",
                        style = ShemaFonts.app13SemiBold,
                        color = ShemaTheme.colors.textPrimary
                    )
                }
            }
        }

        // MARK: - 7-Day Streak Card (1:1 with iOS StreakCard)
        item {
            HomeStreakCard(streakDays = streakDays)
        }

        // MARK: - App Lock Status Card (if enabled)
        item {
            HomeLockStatusCard(completedCount = completedSteps.size)
        }

        // MARK: - Daily Devotional Section (1:1 with iOS DailyDevotional)
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = currentDateFormatted,
                    style = ShemaFonts.app14Bold,
                    color = ShemaTheme.colors.textSecondary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = devotionLocale?.theme ?: "Daily Devotional",
                    style = ShemaFonts.app16Bold,
                    color = ShemaTheme.colors.textPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "DAILY DEVOTIONAL",
                        style = ShemaFonts.app16SemiBold,
                        color = ShemaTheme.colors.textPrimary
                    )

                    IconButton(
                        onClick = { isBookmarked = !isBookmarked },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (isBookmarked) ShemaTheme.colors.primary else ShemaTheme.colors.textSecondary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        // MARK: - 4 Expandable Devotional Accordion Cards
        item {
            val devotionalCards = listOf(
                DevotionalCardData("Passage", Icons.AutoMirrored.Outlined.MenuBook, devotionCore?.reference ?: "Philippians 4:6-7", "passage"),
                DevotionalCardData("Devotional", Icons.Outlined.ChatBubbleOutline, null, "devotional"),
                DevotionalCardData("Reflection", Icons.Outlined.NaturePeople, null, "reflection"),
                DevotionalCardData("Prayer", Icons.Outlined.VolunteerActivism, null, "prayer")
            )

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                devotionalCards.forEachIndexed { index, card ->
                    val isCompleted = completedSteps.contains(index + 1)
                    val isOpen = openCardIndex == index

                    HomeDevotionalCard(
                        title = card.title,
                        icon = card.icon,
                        verse = card.verse,
                        isCompleted = isCompleted,
                        isOpen = isOpen,
                        onCardClick = {
                            openCardIndex = if (isOpen) -1 else index
                        },
                        onRead = { onNavigateToDevotionStep(card.stepKey) },
                        onListen = { onNavigateToDevotionStep(card.stepKey) }
                    )
                }
            }
        }

        // MARK: - Recently Played Worship Section (1:1 with iOS HomeRecentlyPlayedSection)
        item {
            HomeRecentlyPlayedSection(onPlayHymn = onPlayHymn)
        }

        // MARK: - Home Community Prayer Card (1:1 with iOS HomePrayerCard)
        item {
            HomePrayerCard(onJoinPrayer = onNavigateToCommunity)
        }

        // MARK: - Explore Topics Grid (1:1 with iOS ExploreTopics)
        item {
            ExploreTopicsSection(onViewAll = onNavigateToCommunity)
        }

        item {
            Spacer(modifier = Modifier.height(70.dp))
        }
    }
}

private data class DevotionalCardData(
    val title: String,
    val icon: ImageVector,
    val verse: String?,
    val stepKey: String
)

// MARK: - Subcomponent: 7-Day Streak Card
@Composable
private fun HomeStreakCard(streakDays: Int) {
    val calendar = Calendar.getInstance()
    val todayDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) // 1=Sun, 2=Mon...

    val dayNames = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(78.dp)
    ) {
        val spacing = 6.dp
        val cellWidth = (maxWidth - (spacing * 6)) / 7

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing)
        ) {
            dayNames.forEachIndexed { index, day ->
                val dayCalendarIndex = when (index) {
                    0 -> Calendar.MONDAY
                    1 -> Calendar.TUESDAY
                    2 -> Calendar.WEDNESDAY
                    3 -> Calendar.THURSDAY
                    4 -> Calendar.FRIDAY
                    5 -> Calendar.SATURDAY
                    else -> Calendar.SUNDAY
                }

                val isToday = dayCalendarIndex == todayDayOfWeek
                val isPast = dayCalendarIndex < todayDayOfWeek

                Column(
                    modifier = Modifier.width(cellWidth),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(ShemaTheme.colors.tertiaryFill)
                            .padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (isPast) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Done",
                                tint = ShemaTheme.colors.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        } else if (isToday) {
                            Image(
                                painter = painterResource(id = R.drawable.rings),
                                contentDescription = "Today",
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.rings),
                                contentDescription = "Upcoming",
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Text(
                            text = day,
                            style = ShemaFonts.appMicro,
                            color = ShemaTheme.colors.textPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Active Today Underline Bar
                    Box(
                        modifier = Modifier
                            .width(cellWidth * 0.7f)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(if (isToday) ShemaTheme.colors.primary else Color.Transparent)
                    )
                }
            }
        }
    }
}

// MARK: - Subcomponent: Lock Status Card
@Composable
private fun HomeLockStatusCard(completedCount: Int) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFFFB800).copy(alpha = 0.08f),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFB800).copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFB800).copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Lock",
                    tint = Color(0xFFFFB800),
                    modifier = Modifier.size(14.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = "Apps lock is enabled",
                style = ShemaFonts.appBodyRegular,
                color = ShemaTheme.colors.textPrimary
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "$completedCount/4",
                style = ShemaFonts.appButtonSmall,
                color = ShemaTheme.colors.textPrimary
            )
        }
    }
}

// MARK: - Subcomponent: Accordion Devotional Card
@Composable
private fun HomeDevotionalCard(
    title: String,
    icon: ImageVector,
    verse: String?,
    isCompleted: Boolean,
    isOpen: Boolean,
    onCardClick: () -> Unit,
    onRead: () -> Unit,
    onListen: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onCardClick() }
            .animateContentSize(spring()),
        shape = RoundedCornerShape(16.dp),
        color = ShemaTheme.colors.tertiaryFill
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = ShemaTheme.colors.textPrimary,
                        modifier = Modifier.size(26.dp)
                    )
                    Text(
                        text = title,
                        style = ShemaFonts.appFootnote,
                        color = ShemaTheme.colors.textPrimary
                    )
                }

                if (isCompleted) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Completed",
                        tint = ShemaTheme.colors.primary,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Outlined.Circle,
                        contentDescription = "Not Completed",
                        tint = ShemaTheme.colors.textMuted.copy(alpha = 0.5f),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            AnimatedVisibility(visible = isOpen) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 14.dp)
                ) {
                    if (!verse.isNullOrBlank()) {
                        Text(
                            text = verse,
                            style = ShemaFonts.app18Bold,
                            color = ShemaTheme.colors.textPrimary,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // High Contrast "Listen" button with crown
                        Button(
                            onClick = onListen,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (ShemaTheme.colors.isDark) Color.White else Color.Black,
                                contentColor = if (ShemaTheme.colors.isDark) Color.Black else Color.White
                            )
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFFB800).copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("👑", fontSize = 11.sp)
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Listen",
                                style = ShemaFonts.app16Bold
                            )
                        }

                        // High Contrast "Read" button
                        Button(
                            onClick = onRead,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (ShemaTheme.colors.isDark) Color.White else Color.Black,
                                contentColor = if (ShemaTheme.colors.isDark) Color.Black else Color.White
                            )
                        ) {
                            Text(
                                text = "Read",
                                style = ShemaFonts.app16Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

// MARK: - Subcomponent: Home Recently Played Section
@Composable
private fun HomeRecentlyPlayedSection(onPlayHymn: (Hymn) -> Unit) {
    val sampleHymns = listOf(
        Hymn("1", 1, "Holy, Holy, Holy!", "Reginald Heber", "Worship & Praise", 222.0),
        Hymn("2", 23, "Amazing Grace", "John Newton", "Grace & Mercy", 255.0),
        Hymn("3", 45, "It Is Well With My Soul", "Horatio Spafford", "Peace & Comfort", 290.0)
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recently Played",
                style = ShemaFonts.appHeadingTitle,
                color = ShemaTheme.colors.textPrimary
            )
            Text(
                text = "Show all",
                style = ShemaFonts.app14Bold,
                color = ShemaTheme.colors.textSecondary
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(sampleHymns) { hymn ->
                HomeHymnCard(hymn = hymn, onClick = { onPlayHymn(hymn) })
            }
        }
    }
}

@Composable
fun HomeHymnCard(
    hymn: Hymn,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(145.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(145.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(ShemaTheme.colors.surfaceSecondary)
        ) {
            Image(
                painter = painterResource(id = if (hymn.number % 2 == 1) R.drawable.hymns else R.drawable.psalms),
                contentDescription = hymn.title,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(8.dp))
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "#${hymn.number}  ${hymn.title}",
            style = ShemaFonts.app14Bold,
            color = ShemaTheme.colors.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = hymn.author ?: hymn.category,
            style = ShemaFonts.app13SemiBold,
            color = ShemaTheme.colors.textSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// MARK: - Subcomponent: Home Community Prayer Card
@Composable
private fun HomePrayerCard(onJoinPrayer: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = ShemaTheme.colors.secondaryGroupedBg,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "✨ Community Prayer",
                        style = ShemaFonts.app16SemiBold,
                        color = Color(0xFF6366F1)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFF97316).copy(alpha = 0.12f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "14m left",
                        style = ShemaFonts.appCaptionBold,
                        color = Color(0xFFF97316)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Strength for Today",
                style = ShemaFonts.appHeadingTitle,
                color = ShemaTheme.colors.textPrimary
            )

            Text(
                text = "Requested by Sarah M.",
                style = ShemaFonts.appBodySmall,
                color = ShemaTheme.colors.textSecondary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Please pray for my mother as she undergoes surgery this morning. Asking for God’s peace and healing hands.",
                style = ShemaFonts.appBodyRegular,
                color = ShemaTheme.colors.textSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onJoinPrayer,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6366F1),
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Join in Prayer",
                    style = ShemaFonts.appButtonSmall
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

// MARK: - Subcomponent: Explore Topics 2-Column Grid
@Composable
fun ExploreTopicsSection(onViewAll: () -> Unit) {
    val topics = listOf(
        Pair("Faith", "Trusting in God's plan through seasons"),
        Pair("Anxiety & Peace", "Finding tranquility and resting in Him"),
        Pair("Love", "Reflecting God's unconditional love"),
        Pair("Purpose & Calling", "Walking in your divine identity")
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Explore Topics",
                style = ShemaFonts.app18Bold,
                color = ShemaTheme.colors.textPrimary
            )

            Text(
                text = "VIEW ALL",
                style = ShemaFonts.app14Bold,
                color = ShemaTheme.colors.primary,
                modifier = Modifier.clickable { onViewAll() }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            for (i in topics.indices step 2) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        TopicCard(name = topics[i].first, description = topics[i].second, onClick = onViewAll)
                    }
                    if (i + 1 < topics.size) {
                        Box(modifier = Modifier.weight(1f)) {
                            TopicCard(name = topics[i + 1].first, description = topics[i + 1].second, onClick = onViewAll)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TopicCard(
    name: String,
    description: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(115.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = ShemaTheme.colors.secondaryGroupedBg,
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📖",
                    fontSize = 18.sp
                )
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = ShemaTheme.colors.primary,
                    modifier = Modifier.size(18.dp)
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = name,
                    style = ShemaFonts.app16Bold,
                    color = ShemaTheme.colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = description,
                    style = ShemaFonts.appCaption,
                    color = ShemaTheme.colors.textSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
