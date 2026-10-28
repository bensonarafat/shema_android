package com.pulsereality.shema.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pulsereality.shema.ui.components.BibleVerseActionPalette
import com.pulsereality.shema.ui.components.BookSelectorBottomSheet
import com.pulsereality.shema.ui.components.TranslationSelectorBottomSheet
import com.pulsereality.shema.ui.theme.BackgroundDark
import com.pulsereality.shema.ui.theme.BorderDark
import com.pulsereality.shema.ui.theme.FjallaOneFontFamily
import com.pulsereality.shema.ui.theme.PrimaryBlue
import com.pulsereality.shema.ui.theme.SurfaceDark
import com.pulsereality.shema.ui.theme.SurfaceSecondaryDark
import com.pulsereality.shema.ui.theme.TextMutedDark
import com.pulsereality.shema.ui.theme.TextPrimaryDark
import com.pulsereality.shema.ui.theme.TextSecondaryDark
import com.pulsereality.shema.ui.viewmodels.BibleViewModel

@Composable
fun BibleScreen(
    viewModel: BibleViewModel,
    modifier: Modifier = Modifier
) {
    val books by viewModel.books.collectAsState()
    val selectedBook by viewModel.selectedBook.collectAsState()
    val selectedChapter by viewModel.selectedChapter.collectAsState()
    val selectedTranslation by viewModel.selectedTranslation.collectAsState()
    val verses by viewModel.verses.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val selectedVerses by viewModel.selectedVerses.collectAsState()
    val highlights by viewModel.highlights.collectAsState()

    var isBookSheetOpen by remember { mutableStateOf(false) }
    var isTranslationSheetOpen by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // MARK: - Top Navigation Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Book & Chapter Selector Button (Genesis 1)
                Surface(
                    modifier = Modifier.clickable { isBookSheetOpen = true },
                    shape = RoundedCornerShape(14.dp),
                    color = SurfaceDark,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${selectedBook.name} $selectedChapter",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimaryDark,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Select Book & Chapter",
                            tint = PrimaryBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Quick Previous / Next Chapter Buttons + Translation Badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { viewModel.previousChapter() },
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(SurfaceDark)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronLeft,
                            contentDescription = "Previous Chapter",
                            tint = TextSecondaryDark,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = { viewModel.nextChapter() },
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(SurfaceDark)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Next Chapter",
                            tint = TextSecondaryDark,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // Translation Badge Pill (ESV / KJV / NLT)
                    Surface(
                        modifier = Modifier.clickable { isTranslationSheetOpen = true },
                        shape = RoundedCornerShape(10.dp),
                        color = PrimaryBlue.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = selectedTranslation,
                            style = MaterialTheme.typography.labelMedium,
                            color = PrimaryBlue,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // MARK: - Scripture Chapter Reader Pane
            if (isLoading && verses.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryBlue)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Chapter Title Header
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = selectedBook.name.uppercase(),
                                style = MaterialTheme.typography.labelSmall,
                                color = PrimaryBlue,
                                letterSpacing = 2.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$selectedChapter",
                                fontFamily = FjallaOneFontFamily,
                                fontSize = 42.sp,
                                color = TextPrimaryDark,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    // Verses
                    items(verses, key = { it.pk }) { verse ->
                        val isSelected = selectedVerses.contains(verse.pk)
                        val highlightColor = highlights[verse.pk]

                        val leadingBarColor by animateColorAsState(
                            targetValue = when {
                                isSelected -> PrimaryBlue
                                highlightColor != null -> highlightColor
                                else -> Color.Transparent
                            },
                            label = "leadingBar"
                        )

                        val verseBgColor by animateColorAsState(
                            targetValue = when {
                                isSelected -> PrimaryBlue.copy(alpha = 0.12f)
                                highlightColor != null -> highlightColor.copy(alpha = 0.15f)
                                else -> Color.Transparent
                            },
                            label = "verseBg"
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(verseBgColor)
                                .clickable { viewModel.toggleVerseSelection(verse.pk) }
                                .padding(vertical = 6.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            // Highlight / Selection Leading Bar
                            Box(
                                modifier = Modifier
                                    .width(3.dp)
                                    .height(24.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(leadingBarColor)
                            )

                            Spacer(modifier = Modifier.width(6.dp))

                            // Verse Number
                            Text(
                                text = "${verse.verse}",
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isSelected) PrimaryBlue else TextMutedDark,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .width(26.dp)
                                    .padding(top = 2.dp)
                            )

                            // Verse Text
                            Text(
                                text = verse.cleanText,
                                style = MaterialTheme.typography.bodyLarge,
                                color = TextPrimaryDark,
                                lineHeight = 28.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }
            }
        }

        // Floating Action Palette for Selected Verses
        BibleVerseActionPalette(
            selectedCount = selectedVerses.size,
            onHighlightColor = { color -> viewModel.highlightSelected(color) },
            onRemoveHighlight = { viewModel.removeHighlightFromSelected() },
            onClearSelection = { viewModel.clearSelection() },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 70.dp)
        )

        // Book Selector Modal Bottom Sheet
        if (isBookSheetOpen) {
            BookSelectorBottomSheet(
                books = books,
                currentBook = selectedBook,
                currentChapter = selectedChapter,
                onSelectChapter = { book, chap ->
                    viewModel.loadChapter(book, chap)
                },
                onDismiss = { isBookSheetOpen = false }
            )
        }

        // Translation Selector Modal Bottom Sheet
        if (isTranslationSheetOpen) {
            TranslationSelectorBottomSheet(
                currentTranslation = selectedTranslation,
                onSelectTranslation = { code ->
                    viewModel.setTranslation(code)
                },
                onDismiss = { isTranslationSheetOpen = false }
            )
        }
    }
}
