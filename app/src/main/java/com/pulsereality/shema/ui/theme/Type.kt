package com.pulsereality.shema.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.pulsereality.shema.R

val NunitoFontFamily = FontFamily(
    Font(R.font.nunito_regular, FontWeight.Normal),
    Font(R.font.nunito_medium, FontWeight.Medium),
    Font(R.font.nunito_semibold, FontWeight.SemiBold),
    Font(R.font.nunito_bold, FontWeight.Bold),
    Font(R.font.nunito_extrabold, FontWeight.ExtraBold),
    Font(R.font.nunito_black, FontWeight.Black)
)

val FjallaOneFontFamily = FontFamily(
    Font(R.font.fjallaone_regular, FontWeight.Normal)
)

// Exact 1:1 match with iOS Typography.swift
object ShemaFonts {
    val appLargeTitle = TextStyle(fontFamily = NunitoFontFamily, fontWeight = FontWeight.Bold, fontSize = 34.sp)
    val appDisplay = TextStyle(fontFamily = NunitoFontFamily, fontWeight = FontWeight.ExtraBold, fontSize = 28.sp)
    val appTitle = TextStyle(fontFamily = NunitoFontFamily, fontWeight = FontWeight.Medium, fontSize = 24.sp)
    val appTitle2 = TextStyle(fontFamily = NunitoFontFamily, fontWeight = FontWeight.Normal, fontSize = 20.sp)
    val streakCounter = TextStyle(fontFamily = NunitoFontFamily, fontWeight = FontWeight.Black, fontSize = 48.sp)

    val appHeadingTitle = TextStyle(fontFamily = NunitoFontFamily, fontWeight = FontWeight.Bold, fontSize = 20.sp)
    val appHeadingSubTitle = TextStyle(fontFamily = NunitoFontFamily, fontWeight = FontWeight.Medium, fontSize = 12.sp)

    val appBody = TextStyle(fontFamily = NunitoFontFamily, fontWeight = FontWeight.Medium, fontSize = 14.sp)
    val appBodyRegular = TextStyle(fontFamily = NunitoFontFamily, fontWeight = FontWeight.Normal, fontSize = 14.sp)
    val appBodySmall = TextStyle(fontFamily = NunitoFontFamily, fontWeight = FontWeight.Normal, fontSize = 12.sp)

    val appSubheadline = TextStyle(fontFamily = NunitoFontFamily, fontWeight = FontWeight.Normal, fontSize = 14.sp)
    val appFootnote = TextStyle(fontFamily = NunitoFontFamily, fontWeight = FontWeight.Normal, fontSize = 18.sp)
    val appSubFootnote = TextStyle(fontFamily = NunitoFontFamily, fontWeight = FontWeight.Normal, fontSize = 13.sp)
    val appCaption = TextStyle(fontFamily = NunitoFontFamily, fontWeight = FontWeight.Normal, fontSize = 12.sp)
    val appCaptionBold = TextStyle(fontFamily = NunitoFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
    val appMicro = TextStyle(fontFamily = NunitoFontFamily, fontWeight = FontWeight.Normal, fontSize = 10.sp)

    val appButtonPrimary = TextStyle(fontFamily = NunitoFontFamily, fontWeight = FontWeight.ExtraBold, fontSize = 24.sp)
    val appButtonSecondary = TextStyle(fontFamily = NunitoFontFamily, fontWeight = FontWeight.Medium, fontSize = 18.sp)
    val appButtonSmall = TextStyle(fontFamily = NunitoFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)

    val app18Bold = TextStyle(fontFamily = NunitoFontFamily, fontWeight = FontWeight.Bold, fontSize = 18.sp)
    val app16Bold = TextStyle(fontFamily = NunitoFontFamily, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    val app16SemiBold = TextStyle(fontFamily = NunitoFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
    val app14Bold = TextStyle(fontFamily = NunitoFontFamily, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    val app13SemiBold = TextStyle(fontFamily = NunitoFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)

    val bibleReaderFont = TextStyle(fontFamily = NunitoFontFamily, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 26.sp)
    val bibleReaderNumber = TextStyle(fontFamily = NunitoFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
}

val ShemaTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = FjallaOneFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 34.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.5.sp
    ),
    displayMedium = TextStyle(
        fontFamily = FjallaOneFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp,
        lineHeight = 34.sp,
        letterSpacing = 0.5.sp
    ),
    displaySmall = TextStyle(
        fontFamily = FjallaOneFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.5.sp
    ),
    headlineLarge = ShemaFonts.appHeadingTitle,
    headlineMedium = ShemaFonts.appTitle2,
    headlineSmall = ShemaFonts.app18Bold,
    titleLarge = ShemaFonts.app18Bold,
    titleMedium = ShemaFonts.app16SemiBold,
    titleSmall = ShemaFonts.app14Bold,
    bodyLarge = ShemaFonts.bibleReaderFont,
    bodyMedium = ShemaFonts.appBody,
    bodySmall = ShemaFonts.appBodySmall,
    labelLarge = ShemaFonts.appButtonSmall,
    labelMedium = ShemaFonts.app13SemiBold,
    labelSmall = ShemaFonts.appCaptionBold
)
