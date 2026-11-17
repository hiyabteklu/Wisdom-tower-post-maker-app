package com.wisdomtower.post

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoFixHigh
import androidx.compose.material.icons.outlined.FormatSize
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined.WbShade
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Per-slot text editor and style controls (TEXT_STYLE.md + TEMPLATE_REDESIGN_PLAN.md sections 3.1, 3.2, 3.3).
 * Compact 32dp chips, 12sp text, 6dp spacing, Size/Shadow/Color together in one row, and foldable section headers.
 * Uses color circles only (no hex code input boxes anywhere).
 */

private val StyleSwatches = listOf(
    "#FFFFFF", "#F8FAFC", "#CBD5E1", "#475569", "#0F172A", "#000000",
    "#EF4444", "#F97316", "#FACC15", "#FFD84D", "#22C55E", "#10B981",
    "#14B8A6", "#06B6D4", "#3B82F6", "#6366F1", "#8B5CF6", "#EC4899"
)

internal fun unitToLogSlider(u: Float): Float {
    val clamped = u.coerceIn(0.015f, 1.6f)
    val lo = ln(0.015f)
    val hi = ln(1.6f)
    return ((ln(clamped) - lo) / (hi - lo)).coerceIn(0f, 1f)
}

internal fun logSliderToUnit(t: Float): Float {
    val lo = ln(0.015f)
    val hi = ln(1.6f)
    return exp(lo + t.coerceIn(0f, 1f) * (hi - lo)).coerceIn(0.015f, 1.6f)
}

@Composable
internal fun WordBox(
    slot: String,
    doc: PostDoc,
    onDoc: (PostDoc) -> Unit,
    brand: PostBrand,
    selectedOnCanvas: Boolean = false,
    onSelectOnCanvas: (() -> Unit)? = null
) {
    var styleOpen by rememberSaveable(slot) { mutableStateOf(false) }
    val text = doc.texts[slot].orEmpty()
    val style = doc.slotStyles[slot] ?: SlotStyle()
    val hasCustomStyle = !style.isDefault
    val multiline = slot == PostSlots.BODY || slot == PostSlots.LIST || slot == PostSlots.HEADLINE

    val shape = RoundedCornerShape(10.dp)
    Column(
        Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(PostPalette.Surface)
            .border(
                width = if (selectedOnCanvas) 1.5.dp else 1.dp,
                color = if (selectedOnCanvas) PostPalette.Mint else PostPalette.Line,
                shape = shape
            )
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .weight(1f)
                    .clickable(enabled = onSelectOnCanvas != null) { onSelectOnCanvas?.invoke() }
            ) {
                Text(
                    text = PostSlots.label(slot),
                    color = PostPalette.Text,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (hasCustomStyle) {
                    Text("• Styled", color = PostPalette.Mint, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                if (slot == PostSlots.LETTER || slot == PostSlots.WORD) {
                    val hl = doc.texts[PostSlots.HEADLINE].orEmpty()
                    if (hl.isNotBlank()) {
                        CompactChip(
                            label = "From title",
                            icon = Icons.Outlined.AutoFixHigh,
                            selected = false,
                            onClick = {
                                val derived = if (slot == PostSlots.LETTER) {
                                    PostSlots.deriveLetter(doc.texts[PostSlots.WORD], hl)
                                } else {
                                    PostSlots.deriveWord(hl)
                                }
                                onDoc(doc.copy(texts = doc.texts + (slot to derived)))
                            }
                        )
                    }
                }
                CompactChip(
                    label = if (styleOpen) "Hide" else "Style",
                    icon = Icons.Outlined.Tune,
                    selected = styleOpen,
                    onClick = { styleOpen = !styleOpen }
                )
            }
        }

        OutlinedTextField(
            value = text,
            onValueChange = { next ->
                val updatedTexts = doc.texts.toMutableMap()
                updatedTexts[slot] = next
                if (slot == PostSlots.HEADLINE) {
                    if (doc.texts[PostSlots.WORD].isNullOrBlank()) {
                        val w = PostSlots.deriveWord(next)
                        if (w.isNotBlank()) updatedTexts[PostSlots.WORD] = w
                    }
                    if (doc.texts[PostSlots.LETTER].isNullOrBlank()) {
                        val l = PostSlots.deriveLetter(updatedTexts[PostSlots.WORD], next)
                        if (l.isNotBlank()) updatedTexts[PostSlots.LETTER] = l
                    }
                }
                onDoc(doc.copy(texts = updatedTexts))
            },
            placeholder = { Text(PostSlots.label(slot), fontSize = 12.sp) },
            textStyle = TextStyle(fontSize = 13.sp, color = PostPalette.Text),
            singleLine = !multiline,
            minLines = 1,
            maxLines = if (multiline) 4 else 2,
            modifier = Modifier.fillMaxWidth()
        )

        if (styleOpen) {
            SlotStyleEditor(
                slot = slot,
                doc = doc,
                style = style,
                brandHex = brand.accentHex,
                onStyle = { nextStyle ->
                    val nextMap = doc.slotStyles.toMutableMap()
                    if (nextStyle.isDefault) nextMap.remove(slot) else nextMap[slot] = nextStyle
                    onDoc(doc.copy(slotStyles = nextMap))
                },
                onResetOffset = if (doc.offsetOf(slot) != Offset(0f, 0f)) {
                    {
                        val nextOffsets = doc.offsets.toMutableMap()
                        nextOffsets.remove(slot)
                        onDoc(doc.copy(offsets = nextOffsets))
                    }
                } else null
            )
        }
    }
}

@Composable
internal fun OtherTextStyles(doc: PostDoc, onDoc: (PostDoc) -> Unit, brand: PostBrand) {
    var openSlot by rememberSaveable { mutableStateOf<String?>(null) }
    val extras = listOf(PostSlots.FOOTER to "Bottom bar", PostSlots.MARKS to "Corner marks")
    val scroll = rememberScrollState()

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        PostSmallLabel("Other text on the post")
        Row(
            Modifier.fillMaxWidth().horizontalScrollWithFade(scroll, PostPalette.Surface),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            extras.forEach { (slot, label) ->
                val styled = doc.slotStyles[slot]?.isDefault == false
                CompactChip(
                    label = if (styled) "$label •" else label,
                    icon = Icons.Outlined.Tune,
                    selected = openSlot == slot,
                    onClick = { openSlot = if (openSlot == slot) null else slot }
                )
            }
        }
        val active = openSlot
        if (active != null) {
            val style = doc.slotStyles[active] ?: SlotStyle()
            Box(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(PostPalette.Surface)
                    .border(1.dp, PostPalette.Line, RoundedCornerShape(10.dp))
                    .padding(8.dp)
            ) {
                SlotStyleEditor(
                    slot = active,
                    doc = doc,
                    style = style,
                    brandHex = brand.accentHex,
                    onStyle = { nextStyle ->
                        val nextMap = doc.slotStyles.toMutableMap()
                        if (nextStyle.isDefault) nextMap.remove(active) else nextMap[active] = nextStyle
                        onDoc(doc.copy(slotStyles = nextMap))
                    },
                    onResetOffset = null
                )
            }
        }
    }
}

@Composable
internal fun SlotStyleEditor(
    slot: String,
    doc: PostDoc,
    style: SlotStyle,
    brandHex: String?,
    onStyle: (SlotStyle) -> Unit,
    onResetOffset: (() -> Unit)?
) {
    val context = LocalContext.current
    val enFonts = remember { PostFonts.list(context, PostFonts.ENGLISH) }
    val amFonts = remember { PostFonts.list(context, PostFonts.AMHARIC) }
    val shortSide = min(doc.size.width, doc.size.height)
    val templateSlot = postTemplate(doc.templateId).withLookIndex(doc.lookIndex).layout(doc.size).slots.firstOrNull { it.slot == slot }
    val rawSlotSize = templateSlot?.size ?: 0.032f
    val defaultUnit = (if (rawSlotSize <= 2.0f) rawSlotSize else rawSlotSize / 1080f).coerceIn(0.015f, 1.6f)
    val activeUnit = style.sizeUnit ?: (defaultUnit * style.scale).coerceIn(0.015f, 1.6f)
    val pxDisplay = (activeUnit * shortSide).roundToInt()
    val shadow = style.shadow ?: TextShadow()
    var primaryTab by rememberSaveable(slot) { mutableStateOf("size") }
    val topRowScroll = rememberScrollState()

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        // 1. Set Size, Shadow, and Color together in one compact row
        Row(
            Modifier.fillMaxWidth().horizontalScrollWithFade(topRowScroll, PostPalette.Surface),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CompactChip(
                label = "Size ${pxDisplay}px",
                icon = Icons.Outlined.FormatSize,
                selected = primaryTab == "size",
                onClick = { primaryTab = "size" }
            )
            CompactChip(
                label = if (shadow.strength == 0) "Shadow Off" else "Shadow ${shadow.strength}",
                icon = Icons.Outlined.WbShade,
                selected = primaryTab == "shadow",
                onClick = { primaryTab = "shadow" }
            )
            CompactChip(
                label = "Color",
                icon = Icons.Outlined.Palette,
                selected = primaryTab == "color",
                onClick = { primaryTab = "color" }
            )
            PostSwatch(style.colorHex, primaryTab == "color") { primaryTab = "color" }
        }

        // Inline control for whichever of Size / Shadow / Color is selected in the row above
        when (primaryTab) {
            "size" -> {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    CompactChip(
                        label = "A-",
                        selected = false,
                        onClick = {
                            val nextU = (activeUnit / 1.12f).coerceIn(0.015f, 1.6f)
                            onStyle(style.copy(sizeUnit = nextU))
                        }
                    )
                    Slider(
                        value = unitToLogSlider(activeUnit),
                        onValueChange = { t -> onStyle(style.copy(sizeUnit = logSliderToUnit(t))) },
                        valueRange = 0f..1f,
                        modifier = Modifier.weight(1f).height(24.dp)
                    )
                    CompactChip(
                        label = "A+",
                        selected = false,
                        onClick = {
                            val nextU = (activeUnit * 1.12f).coerceIn(0.015f, 1.6f)
                            onStyle(style.copy(sizeUnit = nextU))
                        }
                    )
                    CompactChip(
                        label = "Fit",
                        selected = style.sizeUnit == null && style.scale == 1f,
                        onClick = { onStyle(style.copy(sizeUnit = null, scale = 1f)) }
                    )
                }
            }
            "shadow" -> {
                PostLabeledSlider(
                    label = "Shadow",
                    value = shadow.strength.toFloat(),
                    range = 0f..100f,
                    display = if (shadow.strength == 0) "Off" else "${shadow.strength}"
                ) { v ->
                    val s = v.roundToInt().coerceIn(0, 100)
                    onStyle(style.copy(shadow = if (s == 0) null else shadow.copy(strength = s)))
                }
                if (shadow.strength > 0) {
                    val shadowScroll = rememberScrollState()
                    Row(
                        Modifier.fillMaxWidth().horizontalScrollWithFade(shadowScroll, PostPalette.Surface),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ShadowMode.values().forEach { mode ->
                            CompactChip(
                                label = when (mode) {
                                    ShadowMode.SOFT -> "Soft"
                                    ShadowMode.HARD -> "Hard"
                                    ShadowMode.GLOW -> "Glow"
                                },
                                selected = shadow.mode == mode,
                                onClick = { onStyle(style.copy(shadow = shadow.copy(mode = mode))) }
                            )
                        }
                        PostSwatch(null, shadow.colorHex == null) {
                            onStyle(style.copy(shadow = shadow.copy(colorHex = null)))
                        }
                        StyleSwatches.forEach { hex ->
                            PostSwatch(hex, shadow.colorHex.equals(hex, ignoreCase = true)) {
                                onStyle(style.copy(shadow = shadow.copy(colorHex = hex)))
                            }
                        }
                    }
                }
            }
            "color" -> {
                PaletteStrip(
                    currentHex = style.colorHex,
                    brandHex = brandHex,
                    allowAuto = true,
                    onPick = { hex -> onStyle(style.copy(colorHex = hex)) }
                )
            }
        }

        // 2. Foldable section: Text effect (TextFx)
        FoldableSection(
            title = "Text effect",
            summary = style.fx?.name?.lowercase()?.replaceFirstChar { it.uppercase() } ?: "Template"
        ) {
            val fxScroll = rememberScrollState()
            Row(
                Modifier.fillMaxWidth().horizontalScrollWithFade(fxScroll, PostPalette.Card),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                CompactChip(
                    label = "Template",
                    selected = style.fx == null,
                    onClick = { onStyle(style.copy(fx = null)) }
                )
                TextFx.values().forEach { fx ->
                    CompactChip(
                        label = when (fx) {
                            TextFx.SOLID -> "Solid"
                            TextFx.OUTLINE -> "Outline"
                            TextFx.GRADIENT -> "Gradient"
                            TextFx.PHOTO_FILL -> "Photo inside"
                            TextFx.ECHO -> "Echo"
                            TextFx.EXTRUDE -> "3D block"
                            TextFx.SPLIT_TONE -> "Split color"
                            TextFx.GLITCH_SPLIT -> "Glitch split"
                            TextFx.CURVE_ARC -> "Arc curve"
                            TextFx.VERTICAL -> "Vertical"
                            TextFx.STRIKE_REVEAL -> "Strike"
                            TextFx.OUTLINE_STACK -> "Outline stack"
                        },
                        selected = style.fx == fx,
                        onClick = { onStyle(style.copy(fx = fx)) }
                    )
                }
            }
        }

        // 3. Foldable section: Alignment, Case & Rotation
        FoldableSection(
            title = "Alignment, case & angle",
            summary = listOfNotNull(
                style.align?.name?.lowercase()?.replaceFirstChar { it.uppercase() },
                style.rotate?.let { "${it.roundToInt()}°" }
            ).joinToString(" · ").ifEmpty { "Auto" }
        ) {
            val alignScroll = rememberScrollState()
            Row(
                Modifier.fillMaxWidth().horizontalScrollWithFade(alignScroll, PostPalette.Card),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                CompactChip(
                    label = "Auto",
                    selected = style.align == null,
                    onClick = { onStyle(style.copy(align = null)) }
                )
                TextAlign.values().forEach { a ->
                    CompactChip(
                        label = a.name.lowercase().replaceFirstChar { it.uppercase() },
                        selected = style.align == a,
                        onClick = { onStyle(style.copy(align = a)) }
                    )
                }
                CompactChip(
                    label = when (style.caps) {
                        true -> "CAPS"
                        false -> "Normal"
                        null -> "Auto case"
                    },
                    selected = style.caps != null,
                    onClick = {
                        val nextCaps = when (style.caps) {
                            null -> true
                            true -> false
                            false -> null
                        }
                        onStyle(style.copy(caps = nextCaps))
                    }
                )
            }
            val rotScroll = rememberScrollState()
            Row(
                Modifier.fillMaxWidth().horizontalScrollWithFade(rotScroll, PostPalette.Card),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    null to "Template",
                    -90f to "-90°",
                    -45f to "-45°",
                    -8f to "-8°",
                    0f to "0°",
                    8f to "+8°",
                    45f to "+45°",
                    90f to "+90°"
                ).forEach { (deg, label) ->
                    CompactChip(
                        label = label,
                        selected = style.rotate == deg,
                        onClick = { onStyle(style.copy(rotate = deg)) }
                    )
                }
            }
        }

        // 4. Foldable section: Tracking & Line height
        val trackingVal = style.tracking ?: (templateSlot?.tracking ?: 0f)
        val leadingVal = style.leading ?: (templateSlot?.leading ?: 1.08f)
        FoldableSection(
            title = "Letter & line spacing",
            summary = "${(trackingVal * 100).roundToInt()}% · ${String.format("%.2f", leadingVal)}"
        ) {
            PostLabeledSlider(
                label = "Letters",
                value = trackingVal,
                range = -0.06f..0.4f,
                display = "${(trackingVal * 100).roundToInt()}%"
            ) { v ->
                onStyle(style.copy(tracking = v))
            }
            PostLabeledSlider(
                label = "Lines",
                value = leadingVal,
                range = 0.78f..1.6f,
                display = String.format("%.2f", leadingVal)
            ) { v ->
                onStyle(style.copy(leading = v))
            }
        }

        // 5. Foldable section: Per-slot fonts
        if (enFonts.isNotEmpty() || amFonts.isNotEmpty()) {
            FoldableSection(
                title = "Box font",
                summary = if (style.fontEn != null || style.fontAm != null) "Custom" else "Post default"
            ) {
                if (enFonts.isNotEmpty()) {
                    PostSmallLabel("English")
                    val enScroll = rememberScrollState()
                    Row(
                        Modifier.fillMaxWidth().horizontalScrollWithFade(enScroll, PostPalette.Card),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        CompactChip(
                            label = "Default",
                            selected = style.fontEn == null,
                            onClick = { onStyle(style.copy(fontEn = null)) }
                        )
                        enFonts.forEach { f ->
                            CompactChip(
                                label = f.label,
                                selected = style.fontEn == f.path,
                                onClick = { onStyle(style.copy(fontEn = f.path)) }
                            )
                        }
                    }
                }
                if (amFonts.isNotEmpty()) {
                    PostSmallLabel("Amharic")
                    val amScroll = rememberScrollState()
                    Row(
                        Modifier.fillMaxWidth().horizontalScrollWithFade(amScroll, PostPalette.Card),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        CompactChip(
                            label = "Default",
                            selected = style.fontAm == null,
                            onClick = { onStyle(style.copy(fontAm = null)) }
                        )
                        amFonts.forEach { f ->
                            CompactChip(
                                label = f.label,
                                selected = style.fontAm == f.path,
                                onClick = { onStyle(style.copy(fontAm = f.path)) }
                            )
                        }
                    }
                }
            }
        }

        if (!style.isDefault || onResetOffset != null) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                if (!style.isDefault) {
                    CompactButton(
                        label = "Reset style",
                        icon = Icons.Outlined.RestartAlt,
                        onClick = { onStyle(SlotStyle()) }
                    )
                }
                if (onResetOffset != null) {
                    CompactButton(
                        label = "Reset position",
                        icon = Icons.Outlined.RestartAlt,
                        onClick = onResetOffset
                    )
                }
            }
        }
    }
}
