package com.pulsereality.shema.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
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
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pulsereality.shema.R
import com.pulsereality.shema.data.model.HymnStem
import com.pulsereality.shema.ui.theme.ShemaFonts
import com.pulsereality.shema.ui.theme.ShemaTheme
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

    var isScrubbing by remember { mutableFloatStateOf(-1f) }

    val listState = rememberLazyListState()

    // Smooth auto-scroll when active lyric line advances
    LaunchedEffect(activeLyricIndex) {
        if (activeLyricIndex >= 0 && currentHymn?.lyrics?.isNotEmpty() == true) {
            listState.animateScrollToItem(
                index = activeLyricIndex,
                scrollOffset = -180
            )
        }
    }

    var dragOffsetY by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = if (ShemaTheme.colors.isDark) {
                        listOf(Color(0xFF261C14), Color(0xFF0E0D0C), Color(0xFF0E0D0C))
                    } else {
                        listOf(Color(0xFFEEDFCE), Color(0xFFF8F7F5), Color(0xFFF8F7F5))
                    }
                )
            )
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        if (dragOffsetY > 120) {
                            onDismiss()
                        }
                        dragOffsetY = 0f
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        if (dragAmount.y > 0) {
                            dragOffsetY += dragAmount.y
                        }
                    }
                )
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // MARK: Top Header Bar (1:1 with iOS PlayerView)
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Drag handle pill
                Box(
                    modifier = Modifier
                        .size(width = 36.dp, height = 4.dp)
                        .clip(CircleShape)
                        .background(ShemaTheme.colors.textMuted.copy(alpha = 0.4f))
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Circular dismiss button
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(ShemaTheme.colors.tertiaryFill)
                            .clickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Dismiss",
                            tint = ShemaTheme.colors.textPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Playing from hymnal title
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (currentHymn?.isHymn == false) "PLAYING PSALM" else "PLAYING FROM HYMNAL",
                            style = ShemaFonts.appMicro.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.8.sp),
                            color = ShemaTheme.colors.textMuted
                        )
                        Text(
                            text = currentHymn?.category ?: "Worship & Praise",
                            style = ShemaFonts.app13SemiBold,
                            color = ShemaTheme.colors.textPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Circular more options button
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(ShemaTheme.colors.tertiaryFill)
                            .clickable { },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More",
                            tint = ShemaTheme.colors.textPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // MARK: Stem Selection Tabs (Vocal, Instrumental, Congregation)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                val stems = currentHymn?.availableStems ?: listOf(HymnStem.VOCAL, HymnStem.INSTRUMENTAL, HymnStem.CONGREGATION)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    stems.forEach { stem ->
                        val isSelected = currentStem == stem
                        val bg = if (isSelected) ShemaTheme.colors.primary else ShemaTheme.colors.tertiaryFill
                        val fg = if (isSelected) Color.White else ShemaTheme.colors.textSecondary

                        Surface(
                            modifier = Modifier.clickable { viewModel.switchStem(stem) },
                            shape = CircleShape,
                            color = bg
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Icon(
                                    imageVector = when (stem) {
                                        HymnStem.VOCAL -> Icons.Default.Mic
                                        HymnStem.INSTRUMENTAL -> Icons.Default.MusicNote
                                        HymnStem.CONGREGATION -> Icons.Default.People
                                    },
                                    contentDescription = stem.rawValue,
                                    tint = fg,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = stem.rawValue,
                                    style = ShemaFonts.appCaptionBold,
                                    color = fg
                                )
                            }
                        }
                    }
                }
            }

            // MARK: Center Lyrics Experience
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
                        contentPadding = PaddingValues(vertical = 30.dp),
                        verticalArrangement = Arrangement.spacedBy(18.dp)
                    ) {
                        itemsIndexed(lyrics) { index, line ->
                            val isActive = index == activeLyricIndex
                            val isPast = index < activeLyricIndex

                            val targetScale = if (isActive) 1.03f else 1.0f
                            val animatedScale by animateFloatAsState(
                                targetValue = targetScale,
                                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                                label = "lyricScale"
                            )

                            val lyricColor = when {
                                isActive -> ShemaTheme.colors.textPrimary
                                isPast -> ShemaTheme.colors.textSecondary.copy(alpha = 0.5f)
                                else -> ShemaTheme.colors.textMuted.copy(alpha = 0.35f)
                            }

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.seekTo(line.time) }
                                    .padding(vertical = 4.dp),
                                horizontalAlignment = Alignment.Start
                            ) {
                                if (index == 0 || (index > 0 && lyrics[index - 1].verseNumber != line.verseNumber)) {
                                    if (line.verseNumber != null) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(if (isActive) ShemaTheme.colors.primary.copy(alpha = 0.15f) else ShemaTheme.colors.tertiaryFill)
                                                .padding(horizontal = 8.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "VERSE ${line.verseNumber}",
                                                style = ShemaFonts.appMicro.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    letterSpacing = 1.2.sp
                                                ),
                                                color = if (isActive) ShemaTheme.colors.primary else ShemaTheme.colors.textMuted
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                    }
                                }

                                Text(
                                    text = line.text,
                                    style = if (isActive) {
                                        ShemaFonts.appHeadingTitle.copy(fontSize = 24.sp, lineHeight = 30.sp)
                                    } else {
                                        ShemaFonts.appTitle2.copy(fontSize = 20.sp, lineHeight = 26.sp)
                                    },
                                    color = lyricColor,
                                    modifier = Modifier.scale(animatedScale)
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
                            style = ShemaFonts.appFootnote,
                            color = ShemaTheme.colors.textSecondary
                        )
                    }
                }
            }

            // MARK: Bottom Audio Controls (1:1 with iOS PlayerView)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
            ) {
                // Track Metadata Row: Artwork (48x48) + Title + Author + Heart
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.hymns),
                            contentDescription = "Cover",
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(10.dp))
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = currentHymn?.title ?: "Worship Hymn",
                                style = ShemaFonts.appHeadingTitle,
                                color = ShemaTheme.colors.textPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                if (currentHymn?.isHymn != false) {
                                    Text(
                                        text = "#${currentHymn?.number ?: 1}",
                                        style = ShemaFonts.app13SemiBold,
                                        color = ShemaTheme.colors.primary
                                    )
                                }
                                Text(
                                    text = currentHymn?.author ?: "Traditional Hymn",
                                    style = ShemaFonts.appSubFootnote,
                                    color = ShemaTheme.colors.textSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    // Circular heart button
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(if (isFavorite) ShemaTheme.colors.red.copy(alpha = 0.15f) else ShemaTheme.colors.tertiaryFill)
                            .clickable { isFavorite = !isFavorite },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavorite) ShemaTheme.colors.red else ShemaTheme.colors.textSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Progress Bar Scrubber
                val currentProgress = if (isScrubbing >= 0f) isScrubbing else (if (duration > 0) (currentTime / duration).toFloat() else 0f)
                val displayElapsed = if (isScrubbing >= 0f) (isScrubbing.toDouble() * duration) else currentTime

                Slider(
                    value = currentProgress.coerceIn(0f, 1f),
                    onValueChange = { isScrubbing = it },
                    onValueChangeFinished = {
                        viewModel.seekTo(isScrubbing.toDouble() * duration)
                        isScrubbing = -1f
                    },
                    colors = SliderDefaults.colors(
                        thumbColor = Color.White,
                        activeTrackColor = ShemaTheme.colors.primary,
                        inactiveTrackColor = ShemaTheme.colors.border.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Time Elapsed & Negative Remaining
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = formatPlayerTime(displayElapsed),
                        style = ShemaFonts.appMicro,
                        color = ShemaTheme.colors.textMuted
                    )
                    Text(
                        text = "-" + formatPlayerTime((duration - displayElapsed).coerceAtLeast(0.0)),
                        style = ShemaFonts.appMicro,
                        color = ShemaTheme.colors.textMuted
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Main Transport Controls: Shuffle, 10s back, Big Play/Pause, 10s fwd, Repeat
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Shuffle with active dot
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { isShuffle = !isShuffle }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shuffle,
                            contentDescription = "Shuffle",
                            tint = if (isShuffle) ShemaTheme.colors.primary else ShemaTheme.colors.textMuted,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .clip(CircleShape)
                                .background(if (isShuffle) ShemaTheme.colors.primary else Color.Transparent)
                        )
                    }

                    // 10s skip backward
                    IconButton(onClick = { viewModel.seekTo((currentTime - 15.0).coerceAtLeast(0.0)) }) {
                        Icon(
                            imageVector = Icons.Default.Replay10,
                            contentDescription = "Back 15s",
                            tint = ShemaTheme.colors.textPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    // Big Circular Play / Pause Button (66x66)
                    Box(
                        modifier = Modifier
                            .size(66.dp)
                            .clip(CircleShape)
                            .background(if (ShemaTheme.colors.isDark) Color.White else Color(0xFF181716))
                            .clickable { viewModel.togglePlayPause() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = if (ShemaTheme.colors.isDark) Color(0xFF0E0D0C) else Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    // 10s skip forward
                    IconButton(onClick = { viewModel.seekTo((currentTime + 15.0).coerceAtMost(duration)) }) {
                        Icon(
                            imageVector = Icons.Default.Forward10,
                            contentDescription = "Forward 15s",
                            tint = ShemaTheme.colors.textPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    // Repeat with active dot
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { viewModel.cycleRepeatMode() }
                    ) {
                        Icon(
                            imageVector = when (repeatMode) {
                                RepeatMode.OFF -> Icons.Default.Repeat
                                RepeatMode.ALL -> Icons.Default.Repeat
                                RepeatMode.ONE -> Icons.Default.RepeatOne
                            },
                            contentDescription = "Repeat",
                            tint = if (repeatMode != RepeatMode.OFF) ShemaTheme.colors.primary else ShemaTheme.colors.textMuted,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .clip(CircleShape)
                                .background(if (repeatMode != RepeatMode.OFF) ShemaTheme.colors.primary else Color.Transparent)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Bottom Utilities: Speed Badge, AirPlay placeholder, Lyrics Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(ShemaTheme.colors.tertiaryFill)
                            .clickable { viewModel.cyclePlaybackSpeed() }
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${playbackSpeed}x",
                            style = ShemaFonts.app13SemiBold,
                            color = ShemaTheme.colors.textSecondary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(ShemaTheme.colors.primary.copy(alpha = 0.12f))
                            .clickable { }
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChatBubbleOutline,
                                contentDescription = "Lyrics",
                                tint = ShemaTheme.colors.primary,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "Lyrics",
                                style = ShemaFonts.app13SemiBold,
                                color = ShemaTheme.colors.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun formatPlayerTime(seconds: Double): String {
    val totalSeconds = seconds.toLong().coerceAtLeast(0)
    val minutes = totalSeconds / 60
    val secs = totalSeconds % 60
    return String.format(java.util.Locale.US, "%d:%02d", minutes, secs)
}
