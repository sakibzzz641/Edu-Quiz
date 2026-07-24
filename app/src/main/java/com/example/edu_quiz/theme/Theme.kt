package com.example.edu_quiz.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
  primary = AccentPurple,
  secondary = AccentPink,
  tertiary = AccentCyan,
  background = DarkBg,
  surface = DarkSurface,
  onPrimary = OnPrimaryDark,
  onBackground = TextLight,
  onSurface = TextLight
)

private val LightColorScheme = lightColorScheme(
  primary = Purple40,
  secondary = PurpleGrey40,
  tertiary = Pink40,
  background = TextLight,
  surface = OnPrimaryDark
)

@Composable
fun EduQuizTheme(
  darkTheme: Boolean = true, // Default to true for the premium dark aesthetic
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
