package com.wisdomtower.post

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * The app's own look: deep midnight blue ink with a turquoise accent (taken from the logo dot) and a soft gold.
 * The posts themselves use the Brand colors, not these.
 * The names Ink, Surface, Raised, Line, Mint, Gold, Text, Muted and Warn are kept so no other file has to change.
 * Mint is now the turquoise accent. Ink is also set as the window color in res/values/themes.xml.
 */
object PostPalette {
    val Ink = Color(0xFF080D16)
    val Surface = Color(0xFF101827)
    val Card = Color(0xFF182235)
    val Raised = Color(0xFF22304A)
    val Line = Color(0xFF334566)
    val Mint = Color(0xFF2DE0CF)
    val Gold = Color(0xFFF5C451)
    val Text = Color(0xFFF3F6FC)
    val Muted = Color(0xFFA3B1C9)
    val Warn = Color(0xFFFF8B8B)
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
