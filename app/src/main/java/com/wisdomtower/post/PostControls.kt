package com.wisdomtower.post

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Small shared controls used by the studio, the brand screen and the footer editor. */

/** The colors offered by every color control. Tap a circle; there is no hex code box any more. */
internal val PickerColors = listOf(
    "#FFFFFF", "#F3F4F6", "#9CA3AF", "#374151", "#111827", "#000000",
    "#EF4444", "#D11A26", "#F97316", "#FACC15", "#FFD84D", "#22C55E",
    "#14B8A6", "#22D3EE", "#3B82F6", "#176BCE", "#6366F1", "#8B5CF6", "#EC4899"
)

@Composable
internal fun PostSmallLabel(text: String) {
    Text(
        text = text,
        fontSize = 11.sp,
        color = PostPalette.Muted,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

/**
 * Horizontal scroll modifier that adds subtle left/right edge fades when additional content exists offscreen,
 * so nothing looks cropped on narrow 360dp phones.
 */
internal fun Modifier.horizontalScrollWithFade(
    scrollState: ScrollState,
    fadeColor: Color = PostPalette.Ink
): Modifier = this
    .drawWithContent {
        drawContent()
        val fadeWidth = 18.dp.toPx()
        if (scrollState.value > 0) {
            drawRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(fadeColor, Color.Transparent),
                    startX = 0f,
                    endX = fadeWidth
                ),
                topLeft = Offset.Zero,
                size = Size(fadeWidth, size.height)
            )
        }
        if (scrollState.value < scrollState.maxValue) {
            drawRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color.Transparent, fadeColor),
                    startX = size.width - fadeWidth,
                    endX = size.width
                ),
                topLeft = Offset(size.width - fadeWidth, 0f),
                size = Size(fadeWidth, size.height)
            )
        }
    }
    .horizontalScroll(scrollState)

/**
 * Compact 32dp-high chip with 12sp text and optional icon.
 * Tested for 360dp phones with large font scaling: single-line with ellipsis so text is never clipped.
 */
@Composable
internal fun CompactChip(
    label: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    accent: Color = PostPalette.Mint,
    onClick: () -> Unit
) {
    val bg = if (selected) accent else PostPalette.Raised
    val fg = if (selected) Color(0xFF051817) else PostPalette.Text
    val iconTint = if (selected) Color(0xFF051817) else PostPalette.Muted
    Row(
        modifier = modifier
            .height(34.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .border(1.dp, if (selected) accent else PostPalette.Line, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(14.dp)
            )
        }
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = fg,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/**
 * Compact 32dp-high action button with 12sp text and optional icon.
 */
@Composable
internal fun CompactButton(
    label: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    primary: Boolean = false,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val bg = when {
        !enabled -> PostPalette.Raised.copy(alpha = 0.45f)
        primary -> PostPalette.Mint
        else -> PostPalette.Raised
    }
    val fg = when {
        !enabled -> PostPalette.Muted.copy(alpha = 0.45f)
        primary -> Color(0xFF062118)
        else -> PostPalette.Text
    }
    Row(
        modifier = modifier
            .height(32.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .border(
                width = 1.dp,
                color = if (primary) PostPalette.Mint else PostPalette.Line,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = fg,
                modifier = Modifier.size(14.dp).padding(end = 4.dp)
            )
        }
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = fg,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/**
 * Small section header that can fold up/down to keep controls organized and compact.
 */
@Composable
internal fun FoldableSection(
    title: String,
    summary: String? = null,
    initiallyOpen: Boolean = false,
    content: @Composable () -> Unit
) {
    var open by rememberSaveable(title) { mutableStateOf(initiallyOpen) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(PostPalette.Card)
            .border(1.dp, PostPalette.Line, RoundedCornerShape(8.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(32.dp)
                .clickable { open = !open }
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = PostPalette.Text,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            if (summary != null) {
                Text(
                    text = summary,
                    fontSize = 11.sp,
                    color = PostPalette.Muted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(horizontal = 6.dp)
                )
            }
            Icon(
                imageVector = if (open) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = if (open) "Collapse" else "Expand",
                tint = PostPalette.Muted,
                modifier = Modifier.size(16.dp)
            )
        }
        AnimatedVisibility(visible = open) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp, bottom = 8.dp, top = 2.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                content()
            }
        }
    }
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
    val scroll = rememberScrollState()
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        if (label.isNotEmpty()) {
            PostSmallLabel(label)
        }
        Row(
            Modifier.fillMaxWidth().horizontalScrollWithFade(scroll, PostPalette.Card),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            choices.forEach { hex -> PostSwatch(hex, hex.equals(current, ignoreCase = true)) { onValid(hex) } }
        }
    }
}

/** A round 28dp color choice. A null hex draws the "automatic" swatch. */
@Composable
internal fun PostSwatch(hex: String?, selected: Boolean, onClick: () -> Unit) {
    val fill = hex?.let { runCatching { Color(android.graphics.Color.parseColor(it)) }.getOrNull() }
    Box(
        Modifier.size(28.dp).clip(CircleShape)
            .background(fill ?: PostPalette.Raised)
            .border(if (selected) 2.5.dp else 1.dp, if (selected) PostPalette.Mint else PostPalette.Line, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (hex == null) Text("A", fontSize = 11.sp, color = PostPalette.Text, fontWeight = FontWeight.Bold)
    }
}

/**
 * Thin compact slider that displays label, slider track, and value in a single compact row.
 */
@Composable
internal fun PostLabeledSlider(
    label: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    display: String,
    onChange: (Float) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().height(28.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = PostPalette.Muted,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.widthIn(min = 52.dp, max = 96.dp)
        )
        Slider(
            value = value.coerceIn(range.start, range.endInclusive),
            onValueChange = onChange,
            valueRange = range,
            modifier = Modifier.weight(1f).height(24.dp)
        )
        Text(
            text = display,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = PostPalette.Text,
            maxLines = 1,
            modifier = Modifier.defaultMinSize(minWidth = 34.dp)
        )
    }
}

@Composable
internal fun PostSwitchRow(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        Modifier.fillMaxWidth().height(32.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = PostPalette.Text,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        Switch(checked = checked, onCheckedChange = onChange)
    }
}
