package com.pulsereality.shema.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pulsereality.shema.R
import com.pulsereality.shema.data.model.Hymn
import com.pulsereality.shema.ui.theme.ShemaFonts
import com.pulsereality.shema.ui.theme.ShemaTheme
import com.pulsereality.shema.ui.viewmodels.WorshipViewModel

@Composable
fun WorshipScreen(
    viewModel: WorshipViewModel,
    onPlayHymn: (Hymn) -> Unit,
    modifier: Modifier = Modifier
) {
    val sampleHymns = listOf(
        Hymn("1", 1, "Holy, Holy, Holy!", "Reginald Heber", "Worship & Praise", 222.0),
        Hymn("2", 23, "Amazing Grace", "John Newton", "Grace & Mercy", 255.0),
        Hymn("3", 45, "It Is Well With My Soul", "Horatio Spafford", "Peace & Comfort", 290.0),
        Hymn("4", 88, "Great Is Thy Faithfulness", "Thomas Chisholm", "Faith & Trust", 238.0),
        Hymn("5", 102, "Be Thou My Vision", "Eleanor Hull", "Worship & Praise", 210.0),
        Hymn("6", 114, "Blessed Assurance", "Fanny Crosby", "Grace & Mercy", 225.0)
    )

    val samplePsalms = listOf(
        Hymn("p1", 23, "The Lord is My Shepherd", "Psalm 23", "Psalms", 240.0, isHymn = false),
        Hymn("p2", 91, "He Who Dwells in the Shelter", "Psalm 91", "Psalms", 270.0, isHymn = false),
        Hymn("p3", 103, "Bless the Lord, O My Soul", "Psalm 103", "Psalms", 260.0, isHymn = false),
        Hymn("p4", 121, "I Lift Up My Eyes", "Psalm 121", "Psalms", 215.0, isHymn = false)
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ShemaTheme.colors.background)
            .padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(6.dp))
            // MARK: Top Section Hymns / Psalms navigation cards (1:1 with iOS WorshipView)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Hymns Navigation Card
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .height(72.dp)
                        .clickable { onPlayHymn(sampleHymns[0]) },
                    shape = RoundedCornerShape(16.dp),
                    color = ShemaTheme.colors.surfaceSecondary
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.hymns),
                            contentDescription = "Hymns",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(65.dp)
                                .clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
                        )
                        Text(
                            text = "Hymns",
                            style = ShemaFonts.appHeadingTitle,
                            color = ShemaTheme.colors.textPrimary,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                    }
                }

                // Psalms Navigation Card
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .height(72.dp)
                        .clickable { onPlayHymn(samplePsalms[0]) },
                    shape = RoundedCornerShape(16.dp),
                    color = ShemaTheme.colors.surfaceSecondary
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.psalms),
                            contentDescription = "Psalms",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(65.dp)
                                .clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
                        )
                        Text(
                            text = "Psalms",
                            style = ShemaFonts.appHeadingTitle,
                            color = ShemaTheme.colors.textPrimary,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                    }
                }
            }
        }

        // MARK: - Recently Played (1:1 with iOS WorshipView)
        item {
            WorshipSectionHeader(title = "Recently Played", onShowAll = {})
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(sampleHymns.take(4)) { hymn ->
                    HomeHymnCard(hymn = hymn, onClick = { onPlayHymn(hymn) })
                }
            }
        }

        // MARK: - Today's Featured Hymn Banner (1:1 with iOS WorshipView)
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Today Featured",
                    style = ShemaFonts.appHeadingTitle,
                    color = ShemaTheme.colors.textPrimary
                )
                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onPlayHymn(sampleHymns[1]) }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.hymns),
                        contentDescription = "Featured Hymn",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Linear gradient overlay matching iOS
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                                    startY = 100f
                                )
                            )
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomStart)
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "HYMNS",
                                style = ShemaFonts.app16Bold,
                                color = Color.White.copy(alpha = 0.85f),
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Amazing Grace",
                                style = ShemaFonts.appHeadingTitle,
                                color = Color.White,
                                maxLines = 1
                            )
                        }

                        // White Circular Play Button
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Play",
                                tint = Color.Black,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }

        // MARK: - Top Hymns Carousel (1:1 with iOS WorshipView)
        item {
            WorshipSectionHeader(title = "Top Hymns", onShowAll = {})
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(sampleHymns) { hymn ->
                    HomeHymnCard(hymn = hymn, onClick = { onPlayHymn(hymn) })
                }
            }
        }

        // MARK: - Top Psalms Carousel (1:1 with iOS WorshipView)
        item {
            WorshipSectionHeader(title = "Top Psalms", onShowAll = {})
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(samplePsalms) { psalm ->
                    HomeHymnCard(hymn = psalm, onClick = { onPlayHymn(psalm) })
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun WorshipSectionHeader(
    title: String,
    onShowAll: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = ShemaFonts.appHeadingTitle,
            color = ShemaTheme.colors.textPrimary
        )

        if (onShowAll != null) {
            Text(
                text = "Show all",
                style = ShemaFonts.app14Bold,
                color = ShemaTheme.colors.textSecondary,
                modifier = Modifier.clickable { onShowAll() }
            )
        }
    }
}
