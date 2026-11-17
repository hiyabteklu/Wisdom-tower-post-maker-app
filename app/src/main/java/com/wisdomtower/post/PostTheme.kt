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

/**
 * Global color theme that restyles every template (backgrounds, 3D showcase objects, cards, chips, notes ink/paper,
 * phone rims, and accents) from a single palette while keeping contrast readable.
 */
data class PostGlobalTheme(
    val id: String,
    val name: String,
    val accent: String,
    val secondary: String,
    val light: String,
    val dark: String,
    val deep: String,
    val bgFrom: String,
    val bgMid: String,
    val bgTo: String,
    val paper: String,
    val soft: String,
    val text: String,
    val notesInk: String,
    val notesPaper: String
) {
    val accentHex: String get() = accent
    val secondaryHex: String get() = secondary
    val darkBgHex: String get() = dark
    val lightBgHex: String get() = light

    val accentInt: Int get() = android.graphics.Color.parseColor(accent)
    val secondaryInt: Int get() = android.graphics.Color.parseColor(secondary)
    val textOnDarkInt: Int get() = android.graphics.Color.parseColor(text)
    val bgDarkStartInt: Int get() = android.graphics.Color.parseColor(dark)
    val bgDarkEndInt: Int get() = android.graphics.Color.parseColor(deep)
    val bgLightInt: Int get() = android.graphics.Color.parseColor(soft)
    val surfaceLightInt: Int get() = android.graphics.Color.parseColor(paper)
    val textOnLightInt: Int get() = android.graphics.Color.parseColor(notesInk)
    val notesInkInt: Int get() = android.graphics.Color.parseColor(notesInk)

    val darkBgColor: Color get() = Color(bgDarkStartInt)
    val accentColor: Color get() = Color(accentInt)
}

object PostThemes {
    const val TEMPLATE_DEFAULT_ID = "template-default"
    const val DEFAULT_ID = "wisdom-gold"

    val presets: List<PostGlobalTheme> = listOf(
        PostGlobalTheme(
            id = "wisdom-gold",
            name = "Wisdom Gold",
            accent = "#F59E0B",
            secondary = "#FBBF24",
            light = "#FFFBEB",
            dark = "#07090F",
            deep = "#1A1408",
            bgFrom = "#1F1608",
            bgMid = "#100D08",
            bgTo = "#06080E",
            paper = "#FBF7EE",
            soft = "#F4EFE2",
            text = "#FFFDF7",
            notesInk = "#231C12",
            notesPaper = "#FBF7EE"
        ),
        PostGlobalTheme(
            id = "emerald-scholar",
            name = "Emerald Scholar",
            accent = "#10B981",
            secondary = "#34D399",
            light = "#ECFDF5",
            dark = "#04110C",
            deep = "#08241B",
            bgFrom = "#092C20",
            bgMid = "#061B14",
            bgTo = "#030C09",
            paper = "#F4FBF7",
            soft = "#E9F5EE",
            text = "#F2FDF9",
            notesInk = "#0F291E",
            notesPaper = "#F4FBF7"
        ),
        PostGlobalTheme(
            id = "ocean-cobalt",
            name = "Ocean Cobalt",
            accent = "#2563EB",
            secondary = "#22E0FF",
            light = "#F0F9FF",
            dark = "#050B18",
            deep = "#0D1E3A",
            bgFrom = "#0F264A",
            bgMid = "#09172E",
            bgTo = "#040914",
            paper = "#F4F8FD",
            soft = "#E8F0FA",
            text = "#F5FAFF",
            notesInk = "#102342",
            notesPaper = "#F4F8FD"
        ),
        PostGlobalTheme(
            id = "royal-violet",
            name = "Royal Violet",
            accent = "#8B5CF6",
            secondary = "#EC4899",
            light = "#FAF5FF",
            dark = "#090616",
            deep = "#1C1136",
            bgFrom = "#241445",
            bgMid = "#140B29",
            bgTo = "#070412",
            paper = "#F9F5FF",
            soft = "#F1E9FC",
            text = "#FCF9FF",
            notesInk = "#22133D",
            notesPaper = "#F9F5FF"
        ),
        PostGlobalTheme(
            id = "crimson-ink",
            name = "Crimson Ink",
            accent = "#EF4444",
            secondary = "#F59E0B",
            light = "#FEF2F2",
            dark = "#0D0608",
            deep = "#290D12",
            bgFrom = "#331016",
            bgMid = "#1B090D",
            bgTo = "#0A0406",
            paper = "#FCF6F6",
            soft = "#F6EAEA",
            text = "#FFF7F7",
            notesInk = "#2E1216",
            notesPaper = "#FCF6F6"
        ),
        PostGlobalTheme(
            id = "sunset-coral",
            name = "Sunset Coral",
            accent = "#F97316",
            secondary = "#FB7185",
            light = "#FFF7ED",
            dark = "#0E070C",
            deep = "#2B1218",
            bgFrom = "#36161C",
            bgMid = "#1E0C13",
            bgTo = "#0B0509",
            paper = "#FDF7F2",
            soft = "#F7ECE3",
            text = "#FFF9F5",
            notesInk = "#2E1712",
            notesPaper = "#FDF7F2"
        )
    )

    val all: List<PostGlobalTheme> get() = presets

    fun byId(id: String?): PostGlobalTheme? {
        if (id.isNullOrBlank() || id == TEMPLATE_DEFAULT_ID) return null
        return presets.firstOrNull { it.id.equals(id, ignoreCase = true) }
    }
}

