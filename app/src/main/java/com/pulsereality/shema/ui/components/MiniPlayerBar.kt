package com.pulsereality.shema.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pulsereality.shema.R
import com.pulsereality.shema.ui.theme.ShemaFonts
import com.pulsereality.shema.ui.theme.ShemaTheme

@Composable
fun MiniPlayerBar(
    visible: Boolean,
    title: String,
    subtitle: String,
    currentStem: String,
    isPlaying: Boolean,
    progress: Float,
    isLiked: Boolean = false,
    onLikeClick: () -> Unit = {},
    onPlayPauseClick: () -> Unit,
    onNextClick: () -> Unit = {},
    onPreviousClick: () -> Unit = {},
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically { it } + fadeIn(),
        exit = slideOutVertically { it } + fadeOut(),
        modifier = modifier
    ) {
        var totalDragY by remember { mutableStateOf(0f) }
        var totalDragX by remember { mutableStateOf(0f) }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 4.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(0.8.dp, ShemaTheme.colors.border.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .clickable { onClick() }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragEnd = {
                            if (totalDragY < -40) {
                                onClick() // Swipe up opens full player
                            } else if (totalDragX < -60) {
                                onNextClick()
                            } else if (totalDragX > 60) {
                                onPreviousClick()
                            }
                            totalDragY = 0f
                            totalDragX = 0f
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            totalDragY += dragAmount.y
                            totalDragX += dragAmount.x
                        }
                    )
                },
            shape = RoundedCornerShape(12.dp),
            color = if (ShemaTheme.colors.isDark) Color(0xFF161514).copy(alpha = 0.96f) else ShemaTheme.colors.surface.copy(alpha = 0.96f),
            shadowElevation = 8.dp
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Artwork Thumbnail
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(ShemaTheme.colors.surfaceSecondary),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.hymns),
                            contentDescription = "Artwork",
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // Title & Author + Stem
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = title,
                                style = ShemaFonts.app14Bold,
                                color = ShemaTheme.colors.textPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f, fill = false)
                            )

                            if (isPlaying) {
                                AnimatedEqualizerBars()
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Text(
                                text = subtitle,
                                style = ShemaFonts.appBodySmall,
                                color = ShemaTheme.colors.textSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            if (currentStem.isNotBlank()) {
                                Text(
                                    text = "•",
                                    fontSize = 9.sp,
                                    color = ShemaTheme.colors.textMuted
                                )
                                Text(
                                    text = currentStem.uppercase(),
                                    style = ShemaFonts.appMicro.copy(
                                        color = ShemaTheme.colors.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }

                    // Heart Button
                    IconButton(
                        onClick = onLikeClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isLiked) ShemaTheme.colors.red else ShemaTheme.colors.textSecondary,
                            modifier = Modifier.size(19.dp)
                        )
                    }

                    // Play / Pause Button
                    IconButton(
                        onClick = onPlayPauseClick,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = ShemaTheme.colors.textPrimary,
                            modifier = Modifier.size(23.dp)
                        )
                    }

                    // Next Track Button
                    IconButton(
                        onClick = onNextClick,
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipNext,
                            contentDescription = "Next Track",
                            tint = ShemaTheme.colors.textSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Bottom Progress Line (Spotify-style 2.5dp)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.5.dp)
                        .background(ShemaTheme.colors.border.copy(alpha = 0.3f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fraction = progress.coerceIn(0f, 1f))
                            .height(2.5.dp)
                            .background(ShemaTheme.colors.primary)
                    )
                }
            }
        }
    }
}

@Composable
private fun AnimatedEqualizerBars() {
    val infiniteTransition = rememberInfiniteTransition(label = "equalizer")
    val bar1Height by infiniteTransition.animateFloat(
        initialValue = 4f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(tween(350), RepeatMode.Reverse),
        label = "bar1"
    )
    val bar2Height by infiniteTransition.animateFloat(
        initialValue = 13f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(tween(400), RepeatMode.Reverse),
        label = "bar2"
    )
    val bar3Height by infiniteTransition.animateFloat(
        initialValue = 5f,
        targetValue = 11f,
        animationSpec = infiniteRepeatable(tween(300), RepeatMode.Reverse),
        label = "bar3"
    )

    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        modifier = Modifier.height(14.dp)
    ) {
        Box(
            modifier = Modifier
                .width(2.2.dp)
                .height(bar1Height.dp)
                .clip(RoundedCornerShape(1.dp))
                .background(ShemaTheme.colors.primary)
        )
        Box(
            modifier = Modifier
                .width(2.2.dp)
                .height(bar2Height.dp)
                .clip(RoundedCornerShape(1.dp))
                .background(ShemaTheme.colors.primary)
        )
        Box(
            modifier = Modifier
                .width(2.2.dp)
                .height(bar3Height.dp)
                .clip(RoundedCornerShape(1.dp))
                .background(ShemaTheme.colors.primary)
        )
    }
}
