package com.pulsereality.shema.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pulsereality.shema.data.model.HymnStem
import com.pulsereality.shema.data.model.LyricLine
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
import com.pulsereality.shema.ui.viewmodels.RepeatMode
import com.pulsereality.shema.ui.viewmodels.WorshipViewModel

@Composable
fun WorshipPlayerScreen(
    viewModel: WorshipViewModel,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentHymn by viewModel.currentHymn.collectAsState()
    val currentStem by viewModel.currentStem.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val currentTime by viewModel.currentTime.collectAsState()
    val duration by viewModel.duration.collectAsState()
    val activeLyricIndex by viewModel.activeLyricIndex.collectAsState()
    val repeatMode by viewModel.repeatMode.collectAsState()
    val playbackSpeed by viewModel.playbackSpeed.collectAsState()

    var isFavorite by remember { mutableStateOf(false) }
    var isShuffle by remember { mutableStateOf(false) }

    // Local scrubbing state
    var isScrubbing by remember { mutableStateOf(false) }
    var scrubProgress by remember { mutableFloatStateOf(0f) }

    val listState = rememberLazyListState()

    // Smooth auto-scroll when active lyric line advances
    LaunchedEffect(activeLyricIndex) {
        if (activeLyricIndex >= 0 && currentHymn?.lyrics?.isNotEmpty() == true) {
            listState.animateScrollToItem(
                index = activeLyricIndex,
                scrollOffset = -220
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        PrimaryBlue.copy(alpha = 0.22f),
                        BackgroundDark,
                        BackgroundDark
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
            // MARK: - Drag indicator pill & Top Header Bar
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 38.dp, height = 4.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.25f))
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(SurfaceDark)
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Dismiss Player",
                            tint = TextPrimaryDark
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (currentHymn?.isHymn == false) "PLAYING PSALM" else "PLAYING FROM HYMNAL",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = TextSecondaryDark,
                            letterSpacing = 1.8.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = currentHymn?.category ?: "Worship & Praise",
                            style = MaterialTheme.typography.titleSmall,
                            color = TextPrimaryDark,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    IconButton(
                        onClick = {},
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(SurfaceDark)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More Options",
                            tint = TextSecondaryDark
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // MARK: - Stems Selection Tabs (Vocal, Instrumental, Congregation)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                val stems = currentHymn?.availableStems ?: listOf(HymnStem.VOCAL, HymnStem.INSTRUMENTAL, HymnStem.CONGREGATION)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(SurfaceDark)
                        .padding(4.dp)
                ) {
                    stems.forEach { stem ->
                        val isSelected = currentStem == stem
                        val bgColor by animateColorAsState(
                            targetValue = if (isSelected) PrimaryBlue else Color.Transparent,
                            label = "stemBg"
                        )
                        val textColor by animateColorAsState(
                            targetValue = if (isSelected) Color.White else TextSecondaryDark,
                            label = "stemText"
                        )

                        Surface(
                            modifier = Modifier.clickable { viewModel.switchStem(stem) },
                            shape = RoundedCornerShape(20.dp),
                            color = bgColor
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = when (stem) {
                                        HymnStem.VOCAL -> Icons.Default.Mic
                                        HymnStem.INSTRUMENTAL -> Icons.Default.MusicNote
                                        HymnStem.CONGREGATION -> Icons.Default.People
                                    },
                                    contentDescription = stem.rawValue,
                                    tint = textColor,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = stem.rawValue,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = textColor,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // MARK: - Center: Synchronized Lyrics Experience
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                val lyrics = currentHymn?.lyrics ?: emptyList()
                if (lyrics.isNotEmpty()) {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 40.dp),
                        verticalArrangement = Arrangement.spacedBy(22.dp)
                    ) {
                        itemsIndexed(lyrics) { index, line ->
                            val isActive = index == activeLyricIndex
                            val isPast = index < activeLyricIndex

                            val targetScale = if (isActive) 1.05f else 1.0f
                            val animatedScale by animateFloatAsState(
                                targetValue = targetScale,
                                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                                label = "lyricScale"
                            )

                            val lyricColor = when {
                                isActive -> Color.White
                                isPast -> TextSecondaryDark.copy(alpha = 0.6f)
                                else -> TextMutedDark.copy(alpha = 0.35f)
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.seekTo(line.time) }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (isActive) {
                                    Icon(
                                        imageVector = Icons.Default.GraphicEq,
                                        contentDescription = "Active Lyric",
                                        tint = PrimaryBlue,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                }

                                Text(
                                    text = line.text,
                                    style = if (isActive) {
                                        MaterialTheme.typography.headlineMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            lineHeight = 32.sp
                                        )
                                    } else {
                                        MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Normal,
                                            lineHeight = 28.sp
                                        )
                                    },
                                    color = lyricColor,
                                    modifier = Modifier
                                        .weight(1f)
                                        .scale(animatedScale)
                                )
                            }
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Instrumental Praise Sanctuary",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextSecondaryDark
                        )
                    }
                }
            }

            // MARK: - Bottom Audio Controls
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                // Title, Author & Favorite Heart
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = currentHymn?.title ?: "Worship Hymn",
                            style = MaterialTheme.typography.titleLarge,
                            color = TextPrimaryDark,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = currentHymn?.author ?: "Traditional",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondaryDark,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    IconButton(onClick = { isFavorite = !isFavorite }) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavorite) GoldAccent else TextSecondaryDark,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Timeline Scrubbing Slider
                val currentProgress = if (isScrubbing) scrubProgress else (if (duration > 0) (currentTime / duration).toFloat() else 0f)

                Slider(
                    value = currentProgress.coerceIn(0f, 1f),
                    onValueChange = {
                        isScrubbing = true
                        scrubProgress = it
                    },
                    onValueChangeFinished = {
                        viewModel.seekTo(scrubProgress.toDouble() * duration)
                        isScrubbing = false
                    },
                    colors = SliderDefaults.colors(
                        thumbColor = PrimaryBlue,
                        activeTrackColor = PrimaryBlue,
                        inactiveTrackColor = BorderDark
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Time Elapsed & Remaining
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val displayElapsed = if (isScrubbing) (scrubProgress.toDouble() * duration) else currentTime
                    Text(
                        text = formatTime(displayElapsed),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondaryDark
                    )
                    Text(
                        text = formatTime(duration),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMutedDark
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Transport Controls: Shuffle, Previous, Play/Pause, Next, Repeat
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { isShuffle = !isShuffle }) {
                        Icon(
                            imageVector = Icons.Default.Shuffle,
                            contentDescription = "Shuffle",
                            tint = if (isShuffle) PrimaryBlue else TextMutedDark,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    IconButton(onClick = { viewModel.seekTo(0.0) }) {
                        Icon(
                            imageVector = Icons.Default.SkipPrevious,
                            contentDescription = "Previous",
                            tint = TextPrimaryDark,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    // Main Circular Play / Pause Button
                    IconButton(
                        onClick = { viewModel.togglePlayPause() },
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(PrimaryBlue)
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    IconButton(onClick = { viewModel.seekTo(0.0) }) {
                        Icon(
                            imageVector = Icons.Default.SkipNext,
                            contentDescription = "Next",
                            tint = TextPrimaryDark,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    IconButton(onClick = { viewModel.cycleRepeatMode() }) {
                        Icon(
                            imageVector = when (repeatMode) {
                                RepeatMode.OFF -> Icons.Default.Repeat
                                RepeatMode.ALL -> Icons.Default.Repeat
                                RepeatMode.ONE -> Icons.Default.RepeatOne
                            },
                            contentDescription = "Repeat",
                            tint = if (repeatMode != RepeatMode.OFF) PrimaryBlue else TextMutedDark,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Bottom Utilities: Speed Badge & Stem Label
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.clickable { viewModel.cyclePlaybackSpeed() },
                        shape = RoundedCornerShape(12.dp),
                        color = SurfaceDark
                    ) {
                        Text(
                            text = "${playbackSpeed}× Speed",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondaryDark,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

private fun formatTime(seconds: Double): String {
    val totalSeconds = seconds.toLong().coerceAtLeast(0)
    val minutes = totalSeconds / 60
    val remainingSeconds = totalSeconds % 60
    return String.format(java.util.Locale.US, "%d:%02d", minutes, remainingSeconds)
}
