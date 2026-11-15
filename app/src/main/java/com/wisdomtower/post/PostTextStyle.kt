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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Per-slot text editor and style controls (TEXT_STYLE.md + TEMPLATE_REDESIGN_PLAN.md sections 3.1, 3.2, 3.3).
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

    val shape = RoundedCornerShape(12.dp)
    Column(
        Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(PostPalette.Surface)
            .border(
                width = if (selectedOnCanvas) 2.dp else 1.dp,
                color = if (selectedOnCanvas) PostPalette.Mint else PostPalette.Line,
                shape = shape
            )
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.clickable(enabled = onSelectOnCanvas != null) { onSelectOnCanvas?.invoke() }
            ) {
                Text(PostSlots.label(slot), color = PostPalette.Text, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                if (hasCustomStyle) {
                    Text("Styled", color = PostPalette.Mint, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                if (slot == PostSlots.LETTER || slot == PostSlots.WORD) {
                    val hl = doc.texts[PostSlots.HEADLINE].orEmpty()
                    if (hl.isNotBlank()) {
                        FilterChip(
                            selected = false,
                            onClick = {
                                val derived = if (slot == PostSlots.LETTER) {
                                    PostSlots.deriveLetter(doc.texts[PostSlots.WORD], hl)
                                } else {
                                    PostSlots.deriveWord(hl)
                                }
                                onDoc(doc.copy(texts = doc.texts + (slot to derived)))
                            },
                            label = { Text("From title", fontSize = 11.sp) }
                        )
                    }
                }
                FilterChip(
                    selected = styleOpen,
                    onClick = { styleOpen = !styleOpen },
                    label = { Text(if (styleOpen) "Hide style" else "Style", fontSize = 12.sp) }
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
            placeholder = { Text(PostSlots.label(slot)) },
            singleLine = !multiline,
            minLines = if (multiline) 2 else 1,
            maxLines = if (multiline) 6 else 2,
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

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        PostSmallLabel("Other text on the post")
        Row(
            Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            extras.forEach { (slot, label) ->
                val styled = doc.slotStyles[slot]?.isDefault == false
                FilterChip(
                    selected = openSlot == slot,
                    onClick = { openSlot = if (openSlot == slot) null else slot },
                    label = { Text(if (styled) "$label *" else label, fontSize = 12.sp) }
                )
            }
        }
        val active = openSlot
        if (active != null) {
            val style = doc.slotStyles[active] ?: SlotStyle()
            Box(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(PostPalette.Surface)
                    .border(1.dp, PostPalette.Line, RoundedCornerShape(12.dp))
                    .padding(10.dp)
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

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // 1. Direct size (logarithmic 16px..1700px) + Fit toggle
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            PostSmallLabel("Text size ($pxDisplay px)")
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                FilterChip(
                    selected = false,
                    onClick = {
                        val nextU = (activeUnit / 1.12f).coerceIn(0.015f, 1.6f)
                        onStyle(style.copy(sizeUnit = nextU))
                    },
                    label = { Text("A-", fontSize = 12.sp) }
                )
                FilterChip(
                    selected = false,
                    onClick = {
                        val nextU = (activeUnit * 1.12f).coerceIn(0.015f, 1.6f)
                        onStyle(style.copy(sizeUnit = nextU))
                    },
                    label = { Text("A+", fontSize = 12.sp) }
                )
                FilterChip(
                    selected = style.sizeUnit == null && style.scale == 1f,
                    onClick = { onStyle(style.copy(sizeUnit = null, scale = 1f)) },
                    label = { Text("Fit box", fontSize = 12.sp) }
                )
            }
        }
        Slider(
            value = unitToLogSlider(activeUnit),
            onValueChange = { t -> onStyle(style.copy(sizeUnit = logSliderToUnit(t))) },
            valueRange = 0f..1f
        )

        // 2. Color circles (no hex input)
        PostSmallLabel("Color")
        PaletteStrip(
            currentHex = style.colorHex,
            brandHex = brandHex,
            allowAuto = true,
            onPick = { hex -> onStyle(style.copy(colorHex = hex)) }
        )

        // 3. Text shadow
        val shadow = style.shadow ?: TextShadow()
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
            Row(
                Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ShadowMode.values().forEach { mode ->
                    FilterChip(
                        selected = shadow.mode == mode,
                        onClick = { onStyle(style.copy(shadow = shadow.copy(mode = mode))) },
                        label = {
                            Text(
                                when (mode) {
                                    ShadowMode.SOFT -> "Soft"
                                    ShadowMode.HARD -> "Hard"
                                    ShadowMode.GLOW -> "Glow"
                                },
                                fontSize = 12.sp
                            )
                        }
                    )
                }
            }
            PostSmallLabel("Shadow color")
            Row(
                Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
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

        // 4. Text effect (TextFx)
        PostSmallLabel("Text effect")
        Row(
            Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FilterChip(
                selected = style.fx == null,
                onClick = { onStyle(style.copy(fx = null)) },
                label = { Text("Template", fontSize = 12.sp) }
            )
            TextFx.values().forEach { fx ->
                FilterChip(
                    selected = style.fx == fx,
                    onClick = { onStyle(style.copy(fx = fx)) },
                    label = {
                        Text(
                            when (fx) {
                                TextFx.SOLID -> "Solid"
                                TextFx.OUTLINE -> "Outline"
                                TextFx.GRADIENT -> "Gradient"
                                TextFx.PHOTO_FILL -> "Photo inside"
                                TextFx.ECHO -> "Echo"
                                TextFx.EXTRUDE -> "3D block"
                                TextFx.SPLIT_TONE -> "Split color"
                            },
                            fontSize = 12.sp
                        )
                    }
                )
            }
        }

        // 5. Alignment, Case, Rotation
        PostSmallLabel("Alignment & Case")
        Row(
            Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FilterChip(
                selected = style.align == null,
                onClick = { onStyle(style.copy(align = null)) },
                label = { Text("Auto", fontSize = 12.sp) }
            )
            TextAlign.values().forEach { a ->
                FilterChip(
                    selected = style.align == a,
                    onClick = { onStyle(style.copy(align = a)) },
                    label = { Text(a.name.lowercase().replaceFirstChar { it.uppercase() }, fontSize = 12.sp) }
                )
            }
            FilterChip(
                selected = style.caps == true,
                onClick = {
                    val nextCaps = when (style.caps) {
                        null -> true
                        true -> false
                        false -> null
                    }
                    onStyle(style.copy(caps = nextCaps))
                },
                label = {
                    Text(
                        when (style.caps) {
                            true -> "ALL CAPS"
                            false -> "Normal case"
                            null -> "Auto case"
                        },
                        fontSize = 12.sp
                    )
                }
            )
        }

        PostSmallLabel("Rotation")
        Row(
            Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf(null to "Template", -90f to "-90°", -45f to "-45°", -8f to "-8°", 0f to "0°", 8f to "+8°", 45f to "+45°", 90f to "+90°").forEach { (deg, label) ->
                FilterChip(
                    selected = style.rotate == deg,
                    onClick = { onStyle(style.copy(rotate = deg)) },
                    label = { Text(label, fontSize = 12.sp) }
                )
            }
        }

        // 6. Tracking & Line spacing
        val trackingVal = style.tracking ?: (templateSlot?.tracking ?: 0f)
        PostLabeledSlider(
            label = "Letter spacing",
            value = trackingVal,
            range = -0.06f..0.4f,
            display = "${(trackingVal * 100).roundToInt()}%"
        ) { v ->
            onStyle(style.copy(tracking = v))
        }

        val leadingVal = style.leading ?: (templateSlot?.leading ?: 1.08f)
        PostLabeledSlider(
            label = "Line height",
            value = leadingVal,
            range = 0.78f..1.6f,
            display = String.format("%.2f", leadingVal)
        ) { v ->
            onStyle(style.copy(leading = v))
        }

        // 7. Per-slot fonts
        if (enFonts.isNotEmpty()) {
            PostSmallLabel("English font for this box")
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                FilterChip(
                    selected = style.fontEn == null,
                    onClick = { onStyle(style.copy(fontEn = null)) },
                    label = { Text("Post default", fontSize = 12.sp) }
                )
                enFonts.forEach { f ->
                    FilterChip(
                        selected = style.fontEn == f.path,
                        onClick = { onStyle(style.copy(fontEn = f.path)) },
                        label = { Text(f.label, fontSize = 12.sp) }
                    )
                }
            }
        }

        if (amFonts.isNotEmpty()) {
            PostSmallLabel("Amharic font for this box")
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                FilterChip(
                    selected = style.fontAm == null,
                    onClick = { onStyle(style.copy(fontAm = null)) },
                    label = { Text("Post default", fontSize = 12.sp) }
                )
                amFonts.forEach { f ->
                    FilterChip(
                        selected = style.fontAm == f.path,
                        onClick = { onStyle(style.copy(fontAm = f.path)) },
                        label = { Text(f.label, fontSize = 12.sp) }
                    )
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (!style.isDefault) {
                OutlinedButton(onClick = { onStyle(SlotStyle()) }) {
                    Text("Reset style", fontSize = 12.sp)
                }
            }
            if (onResetOffset != null) {
                OutlinedButton(onClick = onResetOffset) {
                    Text("Reset position", fontSize = 12.sp)
                }
            }
        }
    }
}
