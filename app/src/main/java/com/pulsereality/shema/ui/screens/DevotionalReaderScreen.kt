package com.pulsereality.shema.ui.screens

import android.content.Intent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeMute
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pulsereality.shema.R
import com.pulsereality.shema.data.model.Devotion
import com.pulsereality.shema.data.model.DevotionLocale
import com.pulsereality.shema.data.model.ScriptureVerse
import com.pulsereality.shema.data.model.VerseTranslation
import com.pulsereality.shema.ui.components.DevotionCelebrationDialog
import com.pulsereality.shema.ui.theme.ShemaFonts
import kotlinx.coroutines.delay

@Composable
fun DevotionalReaderScreen(
    devotion: Devotion,
    locale: DevotionLocale,
    initialStepIndex: Int = 1,
    onBackToHome: () -> Unit,
    onDevotionCompleted: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var currentStepIndex by remember { mutableIntStateOf(initialStepIndex) }
    var isMuted by remember { mutableStateOf(false) }
    var showCelebration by remember { mutableStateOf(false) }

    // Random background image from bg1..bg5 (matching iOS AppConstants.Style.background)
    val backgroundRes = remember {
        listOf(
            R.drawable.bg1,
            R.drawable.bg2,
            R.drawable.bg3,
            R.drawable.bg4,
            R.drawable.bg5
        ).random()
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Fullscreen immersive photography background
        Image(
            painter = painterResource(id = backgroundRes),
            contentDescription = "Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Dark linear gradient overlay (exact iOS colors: 0.7, 0.2, 0.8)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.75f),
                            Color.Black.copy(alpha = 0.25f),
                            Color.Black.copy(alpha = 0.85f)
                        )
                    )
                )
        )

        AnimatedContent(
            targetState = currentStepIndex,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "devotionStep"
        ) { step ->
            when (step) {
                1 -> {
                    PassageStoryView(
                        verses = locale.verses,
                        reference = devotion.reference,
                        isMuted = isMuted,
                        onToggleMute = { isMuted = !isMuted },
                        onClose = onBackToHome,
                        onComplete = {
                            currentStepIndex = 2
                        }
                    )
                }
                2 -> {
                    StandardDevotionSectionView(
                        title = "Devotional",
                        content = locale.reflection,
                        isMuted = isMuted,
                        onToggleMute = { isMuted = !isMuted },
                        onBack = onBackToHome,
                        onNext = { currentStepIndex = 3 }
                    )
                }
                3 -> {
                    StandardDevotionSectionView(
                        title = "Reflection",
                        content = locale.actionStep,
                        isMuted = isMuted,
                        onToggleMute = { isMuted = !isMuted },
                        onBack = onBackToHome,
                        onNext = { currentStepIndex = 4 }
                    )
                }
                4 -> {
                    StandardDevotionSectionView(
                        title = "Prayers",
                        content = locale.prayer,
                        isMuted = isMuted,
                        onToggleMute = { isMuted = !isMuted },
                        onBack = onBackToHome,
                        onNext = {
                            showCelebration = true
                            onDevotionCompleted()
                        }
                    )
                }
            }
        }
    }

    if (showCelebration) {
        DevotionCelebrationDialog(
            newStreak = 8,
            gemsEarned = devotion.gems,
            xpEarned = devotion.xp,
            onDismiss = {
                showCelebration = false
                onBackToHome()
            }
        )
    }
}

// MARK: - Instagram Stories-style Passage Reader (1:1 with iOS PassageView)
@Composable
private fun PassageStoryView(
    verses: List<ScriptureVerse>,
    reference: String,
    isMuted: Boolean,
    onToggleMute: () -> Unit,
    onClose: () -> Unit,
    onComplete: () -> Unit
) {
    val context = LocalContext.current
    var currentVerseIndex by remember { mutableIntStateOf(0) }
    var progress by remember { mutableFloatStateOf(0f) }
    var isPaused by remember { mutableStateOf(false) }

    val safeVerses = if (verses.isEmpty()) {
        listOf(ScriptureVerse(vs = "1", translations = listOf(VerseTranslation(book = "Psalms", text = "Be still, and know that I am God.", reference = "Psalm 46:10"))))
    } else verses

    val currentVerse = safeVerses[currentVerseIndex.coerceIn(0, safeVerses.size - 1)]
    val currentTranslation = currentVerse.translations.firstOrNull()

    val totalVerses = safeVerses.size
    val isLastVerse = currentVerseIndex == totalVerses - 1

    // Timer auto-advancing story
    LaunchedEffect(currentVerseIndex, isPaused) {
        if (isPaused) return@LaunchedEffect
        progress = 0f
        val durationMs = 8000L
        val intervalMs = 50L
        val steps = (durationMs / intervalMs).toInt()
        for (i in 1..steps) {
            delay(intervalMs)
            progress = i.toFloat() / steps
        }
        if (currentVerseIndex < totalVerses - 1) {
            currentVerseIndex++
        } else {
            onComplete()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .pointerInput(totalVerses) {
                detectTapGestures(
                    onPress = {
                        isPaused = true
                        tryAwaitRelease()
                        isPaused = false
                    },
                    onTap = { offset ->
                        val screenWidth = size.width
                        if (offset.x < screenWidth * 0.35f) {
                            if (currentVerseIndex > 0) currentVerseIndex--
                        } else {
                            if (currentVerseIndex < totalVerses - 1) currentVerseIndex++
                            else onComplete()
                        }
                    }
                )
            }
    ) {
        Spacer(modifier = Modifier.height(28.dp))

        // Top Story Progress Bar & Action Icons
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Segmented story bar
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                for (i in 0 until totalVerses) {
                    val fillFraction = when {
                        i < currentVerseIndex -> 1f
                        i == currentVerseIndex -> progress
                        else -> 0f
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(3.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.3f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(fraction = fillFraction)
                                .height(3.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                        )
                    }
                }
            }

            // Mute toggle button
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f))
                    .clickable { onToggleMute() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isMuted) Icons.AutoMirrored.Filled.VolumeMute else Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = "Mute",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Close button
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f))
                    .clickable { onClose() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Verse Label & Text
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "VERSE ${currentVerse.vs}",
                style = ShemaFonts.appSubFootnote.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                ),
                color = Color.White.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = currentTranslation?.text ?: "",
                style = ShemaFonts.appTitle.copy(
                    fontSize = 22.sp,
                    lineHeight = 32.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "— ${currentTranslation?.reference ?: reference}",
                style = ShemaFonts.appBodyRegular,
                color = Color.White.copy(alpha = 0.6f)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Bottom Action Buttons
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Share Verse Button
            Button(
                onClick = {
                    val shareText = "${currentTranslation?.text}\n— ${currentTranslation?.reference}\n\nShared via Shema"
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, shareText)
                        type = "text/plain"
                    }
                    context.startActivity(Intent.createChooser(sendIntent, "Share Verse"))
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White.copy(alpha = 0.15f),
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Share Verse",
                    style = ShemaFonts.appButtonSmall
                )
            }

            // Read Full Bible (visible on last verse)
            if (isLastVerse) {
                Button(
                    onClick = onComplete,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Book,
                        contentDescription = "Bible",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Continue Devotional",
                        style = ShemaFonts.appButtonSmall
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

// MARK: - Fullscreen Section Reader for Devotional, Reflection, Prayer (1:1 with iOS)
@Composable
private fun StandardDevotionSectionView(
    title: String,
    content: String,
    isMuted: Boolean,
    onToggleMute: () -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(38.dp))

        // Top Navigation & Mute Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f))
                    .clickable { onBack() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f))
                    .clickable { onToggleMute() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isMuted) Icons.AutoMirrored.Filled.VolumeMute else Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = "Mute",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = title,
                style = ShemaFonts.appTitle2,
                color = Color.White.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = content,
                style = ShemaFonts.appTitle2.copy(
                    fontSize = 20.sp,
                    lineHeight = 30.sp,
                    fontWeight = FontWeight.Normal
                ),
                color = Color.White
            )

            Spacer(modifier = Modifier.height(40.dp))
        }

        // Bottom Continue / Amen Button
        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            )
        ) {
            Text(
                text = if (title == "Prayers") "Amen & Complete" else "Continue",
                style = ShemaFonts.appButtonSmall
            )
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}
