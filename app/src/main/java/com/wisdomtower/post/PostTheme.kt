package com.wisdomtower.post

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/** The app's own look: deep green ink, mint and gold accents. The posts themselves use the Brand colors, not these. */
object PostPalette {
    val Ink = Color(0xFF0C1410)
    val Surface = Color(0xFF15211B)
    val Raised = Color(0xFF1E3027)
    val Line = Color(0xFF2C4438)
    val Mint = Color(0xFF5DE0A5)
    val Gold = Color(0xFFF4C95D)
    val Text = Color(0xFFE8F2EA)
    val Muted = Color(0xFF8CA697)
    val Warn = Color(0xFFFF8F85)
}

@Composable
fun PostTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = PostPalette.Mint, onPrimary = PostPalette.Ink,
            secondary = PostPalette.Gold, onSecondary = PostPalette.Ink,
            secondaryContainer = PostPalette.Raised, onSecondaryContainer = PostPalette.Text,
            background = PostPalette.Ink, onBackground = PostPalette.Text,
            surface = PostPalette.Surface, onSurface = PostPalette.Text,
            surfaceVariant = PostPalette.Raised, onSurfaceVariant = PostPalette.Muted,
            outline = PostPalette.Line, error = PostPalette.Warn
        ),
        content = content
    )
}
