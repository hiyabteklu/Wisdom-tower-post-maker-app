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
import androidx.compose.foundation.shape.RoundedCornerShape
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

/** Shared thumb-friendly controls. Colors are always selected visually; hex values stay in storage only. */

@Composable
internal fun PostSmallLabel(text: String) {
    Text(text, fontSize = 12.sp, color = PostPalette.Muted)
}

/** A round color choice. A null hex draws the automatic swatch. */
@Composable
internal fun PostSwatch(hex: String?, selected: Boolean, onClick: () -> Unit) {
    val fill = hex?.let { runCatching { Color(android.graphics.Color.parseColor(it)) }.getOrNull() }
    Box(
        Modifier.size(38.dp).clip(CircleShape)
            .background(fill ?: PostPalette.Raised)
            .border(if (selected) 3.dp else 1.dp, if (selected) PostPalette.Mint else PostPalette.Line, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (hex == null) Text("A", fontSize = 12.sp, color = PostPalette.Text, fontWeight = FontWeight.Bold)
    }
}

/** The shared visual palette used by the editor, footer and Brand screen. */
@Composable
internal fun PostColorPicker(
    label: String,
    value: String?,
    brand: PostBrand,
    onPick: (String?) -> Unit,
    includeBrandChoice: Boolean = true
) {
    var palette by androidx.compose.runtime.rememberSaveable(label) { androidx.compose.runtime.mutableStateOf("Brand") }
    val bright = listOf("#F43F5E", "#EF4444", "#F97316", "#F59E0B", "#84CC16", "#22C55E", "#06B6D4", "#3B82F6")
    val soft = listOf("#FBCFE8", "#FECACA", "#FED7AA", "#FEF3C7", "#D9F99D", "#BBF7D0", "#A5F3FC", "#BFDBFE")
    val dark = listOf("#881337", "#991B1B", "#9A3412", "#92400E", "#3F6212", "#166534", "#155E75", "#1E40AF")
    val neutral = listOf("#FFFFFF", "#E5E7EB", "#9CA3AF", "#6B7280", "#4B5563", "#374151", "#1F2937", "#111827")
    val colors = when (palette) {
        "Bright" -> bright
        "Soft" -> soft
        "Dark" -> dark
        "Neutral" -> neutral
        else -> listOf(brand.primaryHex, brand.secondaryHex, brand.lightHex, brand.darkHex)
    }
    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        PostSmallLabel(label)
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf("Brand", "Bright", "Soft", "Dark", "Neutral").forEach { name ->
                androidx.compose.material3.FilterChip(
                    selected = palette == name,
                    onClick = { palette = name },
                    label = { Text(name, fontSize = 10.sp) }
                )
            }
        }
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (includeBrandChoice) PostSwatch(brand.primaryHex, value == null) { onPick(null) }
            colors.distinct().forEach { hex ->
                PostSwatch(hex, value.equals(hex, ignoreCase = true)) { onPick(hex) }
            }
        }
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
