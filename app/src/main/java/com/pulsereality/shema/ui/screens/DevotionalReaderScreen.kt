package com.pulsereality.shema.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pulsereality.shema.data.model.Devotion
import com.pulsereality.shema.data.model.DevotionLocale
import com.pulsereality.shema.media.DevotionSpeechManager
import com.pulsereality.shema.ui.components.DevotionCelebrationDialog
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

enum class DevotionStep(val index: Int, val title: String) {
    PASSAGE(1, "Passage"),
    COMMENTARY(2, "Commentary"),
    REFLECTION(3, "Reflection"),
    PRAYER(4, "Prayer");

    companion object {
        fun fromIndex(index: Int): DevotionStep = entries.firstOrNull { it.index == index } ?: PASSAGE
    }
}

@Composable
fun DevotionalReaderScreen(
    devotion: Devotion,
    locale: DevotionLocale,
    initialStepIndex: Int = 1,
    onBackToHome: () -> Unit,
    onDevotionCompleted: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val speechManager = remember { DevotionSpeechManager(context) }
    val isSpeaking by speechManager.isPlaying.collectAsState()
    val activeCharRange by speechManager.activeRange.collectAsState()
    val playbackSpeed by speechManager.playbackSpeed.collectAsState()

    var currentStep by remember { mutableStateOf(DevotionStep.fromIndex(initialStepIndex)) }
    var selectedTranslation by remember { mutableStateOf("ESV") }
    var showCelebrationDialog by remember { mutableStateOf(false) }

    // Stop speech when disposing screen
    DisposableEffect(Unit) {
        onDispose {
            speechManager.shutdown()
        }
    }

    // Stop speech when changing steps
    fun changeStep(newStep: DevotionStep) {
        speechManager.stop()
        currentStep = newStep
    }

    // Extract current section text for audio narration
    val currentSectionSpokenText: String = when (currentStep) {
        DevotionStep.PASSAGE -> {
            locale.verses.mapNotNull { v ->
                v.translations.firstOrNull { it.book == selectedTranslation }?.text
                    ?: v.translations.firstOrNull()?.text
            }.joinToString(" ")
        }
        DevotionStep.COMMENTARY -> locale.reflection
        DevotionStep.REFLECTION -> "${locale.theme}. ${locale.actionStep}"
        DevotionStep.PRAYER -> locale.prayer
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BackgroundDark,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackToHome) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimaryDark
                        )
                    }

                    Text(
                        text = "Step ${currentStep.index} of 4: ${currentStep.title}",
                        style = MaterialTheme.typography.titleSmall,
                        color = PrimaryBlue,
                        fontWeight = FontWeight.Bold
                    )

                    // Spoken audio speed button
                    Surface(
                        modifier = Modifier.clickable { speechManager.cycleSpeed() },
                        shape = RoundedCornerShape(10.dp),
                        color = SurfaceSecondaryDark
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Speed,
                                contentDescription = "Speed",
                                tint = TextSecondaryDark,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${playbackSpeed}×",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextPrimaryDark
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Progress Bar
                LinearProgressIndicator(
                    progress = { currentStep.index / 4.0f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = PrimaryBlue,
                    trackColor = SurfaceSecondaryDark
                )
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BackgroundDark)
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                // Audio Narration Floating Control Card
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = SurfaceDark,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { speechManager.togglePlayPause(currentSectionSpokenText) },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(if (isSpeaking) BrandGreen else PrimaryBlue)
                        ) {
                            Icon(
                                imageVector = if (isSpeaking) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Toggle Spoken Audio",
                                tint = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isSpeaking) "Speaking Audio Narration..." else "Listen to ${currentStep.title}",
                                style = MaterialTheme.typography.titleSmall,
                                color = TextPrimaryDark
                            )
                            Text(
                                text = "Real-time word highlighting active",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondaryDark
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Previous & Next Navigation Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (currentStep.index > 1) {
                        OutlinedButton(
                            onClick = { changeStep(DevotionStep.fromIndex(currentStep.index - 1)) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
                        ) {
                            Text("Previous", color = TextSecondaryDark)
                        }
                    }

                    Button(
                        onClick = {
                            if (currentStep.index < 4) {
                                changeStep(DevotionStep.fromIndex(currentStep.index + 1))
                            } else {
                                speechManager.stop()
                                showCelebrationDialog = true
                            }
                        },
                        modifier = Modifier.weight(if (currentStep.index > 1) 1.5f else 1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (currentStep == DevotionStep.PRAYER) BrandGreen else PrimaryBlue
                        )
                    ) {
                        Text(
                            text = if (currentStep == DevotionStep.PRAYER) "Amen & Complete" else "Continue",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = if (currentStep == DevotionStep.PRAYER) Icons.Default.Check else Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentStep,
                transitionSpec = {
                    if (targetState.index > initialState.index) {
                        (slideInHorizontally { it } + fadeIn()).togetherWith(slideOutHorizontally { -it } + fadeOut())
                    } else {
                        (slideInHorizontally { -it } + fadeIn()).togetherWith(slideOutHorizontally { it } + fadeOut())
                    }
                },
                label = "DevotionStepTransition"
            ) { step ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    when (step) {
                        DevotionStep.PASSAGE -> {
                            PassageContent(
                                reference = devotion.reference,
                                verses = locale.verses,
                                selectedTranslation = selectedTranslation,
                                onSelectTranslation = { selectedTranslation = it },
                                activeCharRange = if (isSpeaking) activeCharRange else null
                            )
                        }
                        DevotionStep.COMMENTARY -> {
                            CommentaryContent(
                                theme = locale.theme,
                                commentary = locale.reflection,
                                activeCharRange = if (isSpeaking) activeCharRange else null
                            )
                        }
                        DevotionStep.REFLECTION -> {
                            ReflectionContent(
                                theme = locale.theme,
                                actionStep = locale.actionStep,
                                activeCharRange = if (isSpeaking) activeCharRange else null
                            )
                        }
                        DevotionStep.PRAYER -> {
                            PrayerContent(
                                prayerText = locale.prayer,
                                activeCharRange = if (isSpeaking) activeCharRange else null
                            )
                        }
                    }
                }
            }
        }
    }

    if (showCelebrationDialog) {
        DevotionCelebrationDialog(
            newStreak = 8,
            gemsEarned = devotion.gems,
            xpEarned = devotion.xp,
            onDismiss = {
                showCelebrationDialog = false
                onDevotionCompleted()
                onBackToHome()
            }
        )
    }
}

// MARK: - Step 1: Passage Content
@Composable
private fun PassageContent(
    reference: String,
    verses: List<com.pulsereality.shema.data.model.ScriptureVerse>,
    selectedTranslation: String,
    onSelectTranslation: (String) -> Unit,
    activeCharRange: IntRange?
) {
    val availableTranslations = listOf("ESV", "KJV", "NLT")

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = reference,
                style = MaterialTheme.typography.headlineLarge,
                color = TextPrimaryDark
            )

            // Translation Selector Pills
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                availableTranslations.forEach { translation ->
                    val isSelected = translation == selectedTranslation
                    Surface(
                        modifier = Modifier.clickable { onSelectTranslation(translation) },
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) PrimaryBlue else SurfaceSecondaryDark
                    ) {
                        Text(
                            text = translation,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) Color.White else TextSecondaryDark,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                verses.forEach { verse ->
                    val verseText = verse.translations.firstOrNull { it.book == selectedTranslation }?.text
                        ?: verse.translations.firstOrNull()?.text ?: ""

                    Row(verticalAlignment = Alignment.Top) {
                        Text(
                            text = verse.vs,
                            style = MaterialTheme.typography.labelMedium,
                            color = PrimaryBlue,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(28.dp).padding(top = 2.dp)
                        )

                        Text(
                            text = buildHighlightedText(verseText, activeCharRange),
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextPrimaryDark,
                            lineHeight = 28.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

// MARK: - Step 2: Commentary Content
@Composable
private fun CommentaryContent(
    theme: String,
    commentary: String,
    activeCharRange: IntRange?
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(PrimaryBlue.copy(alpha = 0.15f))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                text = "COMMENTARY & TRUTH",
                style = MaterialTheme.typography.labelSmall,
                color = PrimaryBlue,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = theme,
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimaryDark
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = buildHighlightedText(commentary, activeCharRange),
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextPrimaryDark,
                    lineHeight = 28.sp
                )
            }
        }
    }
}

// MARK: - Step 3: Reflection Content
@Composable
private fun ReflectionContent(
    theme: String,
    actionStep: String,
    activeCharRange: IntRange?
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(GoldAccent.copy(alpha = 0.15f))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                text = "PERSONAL APPLICATION",
                style = MaterialTheme.typography.labelSmall,
                color = GoldAccent,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = "Living Out the Word",
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimaryDark
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Today's Action Step",
                    style = MaterialTheme.typography.titleMedium,
                    color = GoldAccent
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = buildHighlightedText(actionStep, activeCharRange),
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextPrimaryDark,
                    lineHeight = 26.sp
                )
            }
        }
    }
}

// MARK: - Step 4: Prayer Content
@Composable
private fun PrayerContent(
    prayerText: String,
    activeCharRange: IntRange?
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(BrandGreen.copy(alpha = 0.15f))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                text = "COMMUNING IN PRAYER",
                style = MaterialTheme.typography.labelSmall,
                color = BrandGreen,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = "Prayer of Peace",
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimaryDark
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = buildHighlightedText(prayerText, activeCharRange),
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextPrimaryDark,
                    lineHeight = 28.sp
                )
            }
        }
    }
}

/**
 * Builds an AnnotatedString highlighting the active spoken character range
 * in PrimaryBlue background and bold text.
 */
private fun buildHighlightedText(text: String, activeRange: IntRange?): AnnotatedString {
    return buildAnnotatedString {
        append(text)
        if (activeRange != null && activeRange.first in text.indices && activeRange.last <= text.length) {
            addStyle(
                style = SpanStyle(
                    color = PrimaryBlue,
                    background = PrimaryBlue.copy(alpha = 0.25f),
                    fontWeight = FontWeight.Bold
                ),
                start = activeRange.first,
                end = activeRange.last
            )
        }
    }
}
