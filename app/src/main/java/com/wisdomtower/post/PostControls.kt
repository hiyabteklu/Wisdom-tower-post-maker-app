package com.wisdomtower.post

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Small shared controls used by the studio, the brand screen and the footer editor. */

/** The colors offered by every color control. Tap a circle; there is no hex code box any more. */
private val PickerColors = listOf(
    "#FFFFFF", "#F3F4F6", "#9CA3AF", "#374151", "#111827", "#000000",
    "#EF4444", "#D11A26", "#F97316", "#FACC15", "#FFD84D", "#22C55E",
    "#14B8A6", "#22D3EE", "#3B82F6", "#176BCE", "#6366F1", "#8B5CF6", "#EC4899"
)

@Composable
internal fun PostSmallLabel(text: String) {
    Text(text, fontSize = 12.sp, color = PostPalette.Muted)
}

/**
 * A color control made of tappable color circles (no hex code box). It keeps its old name and parameters so every screen that used the
 * hex box now shows circles. The current color is shown first (and selected) even when it is not in the standard list, so a brand color
 * is never lost. Tapping a circle reports the color as #RRGGBB.
 */
@Composable
internal fun PostHexField(label: String, value: String, modifier: Modifier = Modifier, onValid: (String) -> Unit) {
    val current = value.uppercase()
    val choices = (listOf(current).filter { it.matches(Regex("^#[0-9A-F]{6}$")) } + PickerColors).distinct()
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        PostSmallLabel(label)
        Row(
            Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            choices.forEach { hex -> PostSwatch(hex, hex.equals(current, ignoreCase = true)) { onValid(hex) } }
        }
    }
}

/** A round color choice. A null hex draws the "automatic" swatch. */
@Composable
internal fun PostSwatch(hex: String?, selected: Boolean, onClick: () -> Unit) {
    val fill = hex?.let { runCatching { Color(android.graphics.Color.parseColor(it)) }.getOrNull() }
    Box(
        Modifier.size(34.dp).clip(CircleShape)
            .background(fill ?: PostPalette.Raised)
            .border(if (selected) 3.dp else 1.dp, if (selected) PostPalette.Mint else PostPalette.Line, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (hex == null) Text("A", fontSize = 12.sp, color = PostPalette.Text, fontWeight = FontWeight.Bold)
    }
}

@Composable
internal fun PostLabeledSlider(
    label: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    display: String,
    onChange: (Float) -> Unit
) {
    Column {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            PostSmallLabel(label)
            PostSmallLabel(display)
        }
        Slider(value = value.coerceIn(range.start, range.endInclusive), onValueChange = onChange, valueRange = range)
    }
}

@Composable
internal fun PostSwitchRow(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = PostPalette.Text, modifier = Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = onChange)
    }
}
