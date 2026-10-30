package com.pulsereality.shema.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Highlight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.pulsereality.shema.ui.theme.ShemaFonts
import com.pulsereality.shema.ui.theme.ShemaTheme
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
            .background(ShemaTheme.colors.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // MARK: BibleTopControls (1:1 with iOS BibleTopControls)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Pill: [ Book & Chapter | Search icon ]
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = ShemaTheme.colors.tertiaryFill
                ) {
                    Row(
                        modifier = Modifier
                            .height(36.dp)
                            .padding(horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${selectedBook.name} $selectedChapter",
                            style = ShemaFonts.app14Bold,
                            color = ShemaTheme.colors.textPrimary,
                            modifier = Modifier
                                .clickable { isBookSheetOpen = true }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        )

                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(18.dp)
                                .background(ShemaTheme.colors.border)
                        )

                        Box(
                            modifier = Modifier
                                .clickable { }
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = ShemaTheme.colors.textPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // Right Pill: [ AI Sparkles | Translation | Font Size | Highlighter ]
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = ShemaTheme.colors.tertiaryFill
                ) {
                    Row(
                        modifier = Modifier
                            .height(36.dp)
                            .padding(horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // AI Sparkles
                        Box(
                            modifier = Modifier
                                .clickable { }
                                .padding(horizontal = 6.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Bible AI",
                                tint = ShemaTheme.colors.textPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(18.dp)
                                .background(ShemaTheme.colors.border)
                        )

                        // Translation code
                        Text(
                            text = selectedTranslation,
                            style = ShemaFonts.app14Bold,
                            color = ShemaTheme.colors.textPrimary,
                            modifier = Modifier
                                .clickable { isTranslationSheetOpen = true }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        )

                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(18.dp)
                                .background(ShemaTheme.colors.border)
                        )

                        // Font size
                        Box(
                            modifier = Modifier
                                .clickable { }
                                .padding(horizontal = 6.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.FormatSize,
                                contentDescription = "Font Size",
                                tint = ShemaTheme.colors.textPrimary,
                                modifier = Modifier.size(17.dp)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(18.dp)
                                .background(ShemaTheme.colors.border)
                        )

                        // Highlighter
                        Box(
                            modifier = Modifier
                                .clickable { }
                                .padding(horizontal = 6.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Highlight,
                                contentDescription = "Highlights",
                                tint = ShemaTheme.colors.textPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // MARK: Chapter Header & Verses Content (1:1 with iOS BibleChapterContent)
            if (isLoading && verses.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = ShemaTheme.colors.primary)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Chapter Title Header
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = selectedBook.name,
                                style = ShemaFonts.appFootnote,
                                color = ShemaTheme.colors.textPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "$selectedChapter",
                                style = ShemaFonts.appLargeTitle,
                                color = ShemaTheme.colors.textPrimary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    // Verses List (1:1 with iOS BibleVerseView)
                    items(verses, key = { it.pk }) { verse ->
                        val isSelected = selectedVerses.contains(verse.pk)
                        val highlightColor = highlights[verse.pk]

                        val leadingBarColor by animateColorAsState(
                            targetValue = when {
                                isSelected -> ShemaTheme.colors.primary
                                highlightColor != null -> highlightColor
                                else -> Color.Transparent
                            },
                            label = "leadingBar"
                        )

                        val verseBgColor by animateColorAsState(
                            targetValue = when {
                                isSelected -> ShemaTheme.colors.primary.copy(alpha = 0.12f)
                                highlightColor != null -> highlightColor.copy(alpha = 0.18f)
                                else -> Color.Transparent
                            },
                            label = "verseBg"
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(verseBgColor)
                                .clickable { viewModel.toggleVerseSelection(verse.pk) }
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            // Left selection/highlight bar (3dp width)
                            Box(
                                modifier = Modifier
                                    .width(3.dp)
                                    .height(22.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(leadingBarColor)
                            )

                            Spacer(modifier = Modifier.width(6.dp))

                            // Verse number
                            Text(
                                text = "${verse.verse}",
                                style = ShemaFonts.bibleReaderNumber,
                                color = if (isSelected) ShemaTheme.colors.primary else ShemaTheme.colors.textSecondary,
                                modifier = Modifier.width(22.dp).padding(top = 2.dp)
                            )

                            // Verse text
                            Text(
                                text = verse.cleanText,
                                style = ShemaFonts.bibleReaderFont,
                                color = ShemaTheme.colors.textPrimary,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(110.dp))
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

        // Modal Sheets
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
