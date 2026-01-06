package com.example.bookexplorer.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF8D6E63),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD7CCC8),
    onPrimaryContainer = Color(0xFF3E2723),
    secondary = Color(0xFFBCAAA4),
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFFEFEBE9),
    onSecondaryContainer = Color(0xFF4E342E),
    surface = Color(0xFFFFFBFA),
    background = Color(0xFFFFFBFA),
    surfaceVariant = Color(0xFFE7E0DE),
    onSurfaceVariant = Color(0xFF494545)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFBCAAA4),
    onPrimary = Color(0xFF4E342E),
    primaryContainer = Color(0xFF5D4037),
    onPrimaryContainer = Color(0xFFD7CCC8),
    secondary = Color(0xFF8D6E63),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF4E342E),
    onSecondaryContainer = Color(0xFFD7CCC8),
    surface = Color(0xFF1c1b1a),
    background = Color(0xFF1c1b1a),
    surfaceVariant = Color(0xFF4A4442),
    onSurfaceVariant = Color(0xFFCEC4BF)
)

@Composable
fun BookExplorerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
