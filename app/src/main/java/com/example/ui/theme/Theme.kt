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

private val MarianDarkColorScheme = darkColorScheme(
    primary = MarianSkyBlue,
    onPrimary = MarianBlueDark,
    primaryContainer = MarianBlueMedium,
    onPrimaryContainer = MarianSoftBlue,
    secondary = MarianGold,
    onSecondary = MarianBlueDark,
    secondaryContainer = Color(0xFF382F12),
    onSecondaryContainer = MarianGoldLight,
    tertiary = MarianRosePink,
    onTertiary = Color(0xFF4A0022),
    tertiaryContainer = Color(0xFF5A1E35),
    onTertiaryContainer = Color(0xFFFFD8E4),
    background = MarianBlueDark,
    onBackground = MarianIvory,
    surface = MarianSurfaceDark,
    onSurface = MarianIvory,
    surfaceVariant = MarianBlueMedium,
    onSurfaceVariant = MarianTextSecondary
)

private val MarianLightColorScheme = lightColorScheme(
    primary = MarianBlueAccent,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD6E7FF),
    onPrimaryContainer = MarianBlueDark,
    secondary = MarianGoldDeep,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFF3CD),
    onSecondaryContainer = Color(0xFF4B3800),
    tertiary = MarianRoseDark,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFD8E4),
    onTertiaryContainer = Color(0xFF3B0018),
    background = Color(0xFFF4F7FC),
    onBackground = Color(0xFF0F1A2C),
    surface = MarianSurfaceLight,
    onSurface = Color(0xFF0F1A2C),
    surfaceVariant = Color(0xFFE3EDFA),
    onSurfaceVariant = Color(0xFF425570)
)

@Composable
fun VirgenMariaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep intentional Marian celestial palette by default
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> MarianDarkColorScheme
        else -> MarianLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
