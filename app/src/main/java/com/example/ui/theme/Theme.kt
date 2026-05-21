package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = MinimalPrimaryDark,
    secondary = MinimalSecondaryDark,
    tertiary = MinimalTertiaryDark,
    background = MinimalBackgroundDark,
    surface = MinimalSurfaceDark,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color(0xFFE6E0E9),
    onSurface = Color(0xFFE6E0E9),
    outline = MinimalBorderDark
  )

private val LightColorScheme =
  lightColorScheme(
    primary = MinimalPrimary,
    secondary = MinimalSecondary,
    tertiary = MinimalTertiary,
    background = MinimalBackgroundLight,
    surface = MinimalSurfaceLight,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF1D1B20),
    onSurface = Color(0xFF1D1B20),
    outline = MinimalBorderLight
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Set to false to strictly enforce the "Clean Minimalism" custom color design aesthetic
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
