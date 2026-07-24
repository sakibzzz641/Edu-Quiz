package com.example.edu_quiz.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.edu_quiz.R

// Font families definition
val NotoSansBengaliFontFamily = FontFamily(
  Font(R.font.noto_sans_bengali_regular, FontWeight.Normal),
  Font(R.font.noto_sans_bengali_bold, FontWeight.Bold)
)

val OutfitFontFamily = FontFamily(
  Font(R.font.outfit_regular, FontWeight.Normal),
  Font(R.font.outfit_bold, FontWeight.Bold)
)

// Default Material 3 typography
val Typography = Typography(
  displayLarge = TextStyle(
    fontFamily = OutfitFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 48.sp,
    lineHeight = 56.sp,
    letterSpacing = (-0.25).sp
  ),
  headlineLarge = TextStyle(
    fontFamily = OutfitFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 32.sp,
    lineHeight = 40.sp,
    letterSpacing = 0.sp
  ),
  headlineMedium = TextStyle(
    fontFamily = OutfitFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 24.sp,
    lineHeight = 32.sp,
    letterSpacing = 0.sp
  ),
  titleLarge = TextStyle(
    fontFamily = NotoSansBengaliFontFamily, // Mixed English + Bangla
    fontWeight = FontWeight.Bold,
    fontSize = 20.sp,
    lineHeight = 30.sp, // Taller line height for Bangla script (1.5x)
    letterSpacing = 0.sp
  ),
  titleMedium = TextStyle(
    fontFamily = NotoSansBengaliFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 16.sp,
    lineHeight = 24.sp, // Taller line height (1.5x)
    letterSpacing = 0.15.sp
  ),
  bodyLarge = TextStyle(
    fontFamily = NotoSansBengaliFontFamily, // Mixed content
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp,
    lineHeight = 25.sp, // Taller line height for Bengali matras/conjuncts
    letterSpacing = 0.5.sp
  ),
  bodyMedium = TextStyle(
    fontFamily = NotoSansBengaliFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 14.sp,
    lineHeight = 22.sp, // Taller line height for Bengali
    letterSpacing = 0.25.sp
  ),
  labelLarge = TextStyle(
    fontFamily = OutfitFontFamily, // Standard UI labels in Outfit
    fontWeight = FontWeight.Medium,
    fontSize = 14.sp,
    lineHeight = 20.sp,
    letterSpacing = 0.1.sp
  ),
  labelMedium = TextStyle(
    fontFamily = OutfitFontFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 12.sp,
    lineHeight = 16.sp,
    letterSpacing = 0.5.sp
  ),
  labelSmall = TextStyle(
    fontFamily = OutfitFontFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 11.sp,
    lineHeight = 16.sp,
    letterSpacing = 0.5.sp
  )
)
