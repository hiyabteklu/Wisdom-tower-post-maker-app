package com.wisdomtower.post

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Small shared controls used by the studio, the brand screen and the footer editor. */

private val SharedHex = Regex("^#[0-9A-F]{6}$")

@Composable
internal fun PostSmallLabel(text: String) {
    Text(text, fontSize = 12.sp, color = PostPalette.Muted)
}

/** A color field that only reports a value once it is a full #RRGGBB code. */
@Composable
internal fun PostHexField(label: String, value: String, modifier: Modifier = Modifier, onValid: (String) -> Unit) {
    var text by remember(value) { mutableStateOf(value) }
    val swatch = runCatching { Color(android.graphics.Color.parseColor(value)) }.getOrDefault(Color.Gray)
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Box(
            Modifier.size(36.dp).clip(RoundedCornerShape(8.dp)).background(swatch)
                .border(1.dp, PostPalette.Line, RoundedCornerShape(8.dp))
        )
        OutlinedTextField(
            value = text,
            onValueChange = { typed ->
                val upper = typed.uppercase()
                text = upper
                if (SharedHex.matches(upper)) onValid(upper)
            },
            label = { Text(label) },
            singleLine = true,
            modifier = Modifier.weight(1f)
        )
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
