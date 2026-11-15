package com.wisdomtower.post

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.TextRange
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Per-box text styling (docs/TEXT_STYLE.md). Every word box of a template gets its own Color, Font and Size,
 * stored in PostDoc.slotStyles under the slot name. Two extra keys cover text that is not a word box:
 * PostSlots.MARKS (fixed marks such as the quote mark, the ANSWER label and badge letters) and PostSlots.FOOTER
 * (the footer and social text). A style with nothing set is removed, so a post with no overrides stays "not edited".
 */

/** Changes the style stored under key. A style that ends up with nothing set is dropped from the map. */
internal fun PostDoc.withSlotStyle(key: String, edit: (SlotStyle) -> SlotStyle): PostDoc {
    val next = edit(slotStyles[key] ?: SlotStyle())
    return copy(slotStyles = if (next.isDefault) slotStyles - key else slotStyles + (key to next))
}

private fun PostDoc.withColorOnAll(slots: List<String>, hex: String?): PostDoc =
    slots.fold(this) { d, s -> d.withSlotStyle(s) { it.copy(colorHex = hex) } }

/** "Montserrat_Black.ttf" or ".../Montserrat_Black.ttf" becomes "Montserrat Black". */
private fun fontShort(path: String?): String {
    if (path == null) return "Font"
    return path.substringAfterLast('/').substringBeforeLast('.').replace('_', ' ').replace('-', ' ').trim().ifEmpty { "Font" }
}

@Composable
private fun ColorDot(hex: String?) {
    val c = remember(hex) { hex?.let { h -> runCatching { Color(android.graphics.Color.parseColor(h)) }.getOrNull() } } ?: Color.Gray
    Box(Modifier.size(14.dp).clip(CircleShape).background(c))
}

/** One strip of colors: A (the template's own color), the four Brand colors, then the shared palette. */
@Composable
private fun StyleColorPicker(selected: String?, brand: PostBrand, onPick: (String?) -> Unit) {
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), verticalAlignment = Alignment.CenterVertically) {
            PostCircle(null, selected == null, "A") { onPick(null) }
            listOf(brand.primaryHex, brand.secondaryHex, brand.lightHex, brand.darkHex).forEach { hex ->
                PostCircle(hex, selected.equals(hex, ignoreCase = true)) { onPick(hex) }
            }
        }
        PaletteStrip(selected, null, false, onPick)
    }
}

/** A font chip drawn in its own font, so the choice can be seen before it is tapped. */
@Composable
private fun FontChip(f: PostFonts.FontChoice, selected: Boolean, onClick: () -> Unit) {
    val context = LocalContext.current
    val family = remember(f.path) { PostFonts.typeface(context, f.path)?.let { FontFamily(it) } }
    FilterChip(selected = selected, onClick = onClick, label = { Text(f.label, fontFamily = family, fontSize = 15.sp) })
}

/** English fonts change the Latin letters of the box, Amharic fonts change the Ethiopic letters of the box. */
@Composable
private fun StyleFontPicker(style: SlotStyle, onChange: (SlotStyle) -> Unit) {
    val context = LocalContext.current
    val english = remember { PostFonts.list(context, PostFonts.ENGLISH) }
    val amharic = remember { PostFonts.list(context, PostFonts.AMHARIC) }
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        PostSmallLabel("English letters")
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = style.fontEn == null, onClick = { onChange(style.copy(fontEn = null)) }, label = { Text("Template font") })
            english.forEach { f -> FontChip(f, style.fontEn == f.path) { onChange(style.copy(fontEn = f.path)) } }
        }
        if (amharic.isNotEmpty()) {
            PostSmallLabel("Amharic letters")
            Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = style.fontAm == null, onClick = { onChange(style.copy(fontAm = null)) }, label = { Text("Default") })
                amharic.forEach { f -> FontChip(f, style.fontAm == f.path) { onChange(style.copy(fontAm = f.path)) } }
            }
        }
        if (english.isEmpty() && amharic.isEmpty()) {
            PostSmallLabel("No fonts yet. Add .ttf or .otf files in the Fonts section.")
        }
    }
}

/** The colors offered for ink on a selected word. Tap a circle; there is no hex code box. */
private val InlineInkColors = listOf(
    "#D11A26", "#EF4444", "#F97316", "#FACC15", "#22C55E", "#14B8A6",
    "#22D3EE", "#3B82F6", "#176BCE", "#6366F1", "#8B5CF6", "#EC4899", "#111827", "#FFFFFF"
)

/**
 * One word box: the text field, then three chips (Color, Font, Size) that open one small panel at a time.
 * Nothing is set until a chip is used, so the template keeps its own look by default. Reset clears this box only.
 */
@Composable
internal fun WordBox(slot: String, doc: PostDoc, onDoc: (PostDoc) -> Unit, brand: PostBrand) {
    var panel by remember(slot) { mutableStateOf("") }
    var field by remember(slot) { mutableStateOf(TextFieldValue(doc.texts[slot].orEmpty())) }
    LaunchedEffect(doc.texts[slot]) {
        val external = doc.texts[slot].orEmpty()
        if (field.text != external) field = TextFieldValue(external, TextRange(external.length))
    }
    fun applyInline(open: String, close: String, placeholder: String = "text") {
        val value = field
        val start = minOf(value.selection.start, value.selection.end).coerceIn(0, value.text.length)
        val end = maxOf(value.selection.start, value.selection.end).coerceIn(0, value.text.length)
        val chosen = value.text.substring(start, end).ifEmpty { placeholder }
        val replacement = open + chosen + close
        val next = value.text.substring(0, start) + replacement + value.text.substring(end)
        val cursor = if (start == end) start + open.length + chosen.length else start + replacement.length
        field = TextFieldValue(next, TextRange(cursor))
        onDoc(doc.withText(slot, next))
    }
    val style = doc.slotStyles[slot] ?: SlotStyle()
    val boxes = remember(doc.templateId, doc.size) {
        postTemplate(doc.templateId).layout(doc.size).slots.filter { it.literal == null }.map { it.slot }.distinct()
    }
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        OutlinedTextField(
            value = field,
            onValueChange = { field = it; onDoc(doc.withText(slot, it.text)) },
            placeholder = { Text(PostSlots.label(slot)) },
            minLines = 1,
            maxLines = if (slot == PostSlots.LIST) 8 else 5,
            modifier = Modifier.fillMaxWidth()
        )
        PostSmallLabel("Select a word or phrase above, then tap a formatting control. With no selection, a placeholder is inserted at the cursor.")
        Row(
            Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilterChip(selected = false, onClick = { applyInline("**", "**", "bold") }, label = { Text("Bold") })
            FilterChip(selected = false, onClick = { applyInline("*", "*", "italic") }, label = { Text("Italic") })
            FilterChip(selected = false, onClick = { applyInline("==", "==", "highlight") }, label = { Text("Highlight") })
            FilterChip(selected = false, onClick = { applyInline("[[", "]]", "key term") }, label = { Text("Key term") })
            FilterChip(selected = false, onClick = { applyInline("#", "#", "underline") }, label = { Text("Underline") })
            FilterChip(selected = false, onClick = { applyInline("\\box{", "}", "important note") }, label = { Text("Note box") })
            FilterChip(selected = false, onClick = { applyInline("\u0024", "\u0024", "formula") }, label = { Text("Formula") })
            FilterChip(selected = false, onClick = { applyInline("\u0024\u0024", "\u0024\u0024", "display formula") }, label = { Text("Display math") })
        }
        // Color of the selected word: tap a circle, the color is applied to the selection at once (no hex code box).
        PostSmallLabel("Color of the selected word")
        Row(
            Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            (listOf(brand.primaryHex, brand.secondaryHex) + InlineInkColors).distinct().forEach { hex ->
                PostSwatch(hex, false) { applyInline("[[${hex.uppercase()}:", "]]", "colored word") }
            }
        }
        Row(
            Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilterChip(
                selected = panel == "color",
                onClick = { panel = if (panel == "color") "" else "color" },
                label = { Text("Color") },
                leadingIcon = if (style.colorHex != null) { { ColorDot(style.colorHex) } } else null
            )
            FilterChip(
                selected = panel == "font",
                onClick = { panel = if (panel == "font") "" else "font" },
                label = { Text(if (style.fontEn == null && style.fontAm == null) "Font" else fontShort(style.fontEn ?: style.fontAm)) }
            )
            FilterChip(
                selected = panel == "size",
                onClick = { panel = if (panel == "size") "" else "size" },
                label = { Text(if (style.scale == 1f) "Size" else "${(style.scale * 100).toInt()}%") }
            )
            if (!style.isDefault) {
                TextButton(onClick = { onDoc(doc.withSlotStyle(slot) { SlotStyle() }); panel = "" }) { Text("Reset") }
            }
        }
        when (panel) {
            "color" -> {
                StyleColorPicker(style.colorHex, brand) { hex -> onDoc(doc.withSlotStyle(slot) { it.copy(colorHex = hex) }) }
                if (boxes.size > 1) {
                    Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
                        FilterChip(
                            selected = false,
                            onClick = { onDoc(doc.withColorOnAll(boxes, style.colorHex)) },
                            label = { Text("Use this color for all words") }
                        )
                    }
                }
                PostSmallLabel("A = the template's own color for this box.")
            }
            "font" -> StyleFontPicker(style) { next -> onDoc(doc.withSlotStyle(slot) { next }) }
            "size" -> PostLabeledSlider("Size of this box", style.scale, 0.6f..1.6f, "${(style.scale * 100).toInt()}%") { v ->
                onDoc(doc.withSlotStyle(slot) { it.copy(scale = v) })
            }
        }
    }
}

/** Colors for the text that is not a word box: the fixed marks of a template (when it has any) and the footer text. */
@Composable
internal fun OtherTextStyles(doc: PostDoc, onDoc: (PostDoc) -> Unit, brand: PostBrand, hasMarks: Boolean) {
    var open by remember { mutableStateOf("") }
    val shown = if (open == PostSlots.MARKS && !hasMarks) "" else open
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text("Other text", color = PostPalette.Text, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (hasMarks) {
                FilterChip(
                    selected = shown == PostSlots.MARKS,
                    onClick = { open = if (open == PostSlots.MARKS) "" else PostSlots.MARKS },
                    label = { Text("Marks and badges") },
                    leadingIcon = if (doc.slotStyles[PostSlots.MARKS]?.colorHex != null) { { ColorDot(doc.slotStyles[PostSlots.MARKS]?.colorHex) } } else null
                )
            }
            FilterChip(
                selected = shown == PostSlots.FOOTER,
                onClick = { open = if (open == PostSlots.FOOTER) "" else PostSlots.FOOTER },
                label = { Text("Footer text") },
                leadingIcon = if (doc.slotStyles[PostSlots.FOOTER]?.colorHex != null) { { ColorDot(doc.slotStyles[PostSlots.FOOTER]?.colorHex) } } else null
            )
        }
        if (shown.isNotEmpty()) {
            val style = doc.slotStyles[shown] ?: SlotStyle()
            StyleColorPicker(style.colorHex, brand) { hex -> onDoc(doc.withSlotStyle(shown) { it.copy(colorHex = hex) }) }
            PostSmallLabel(
                if (shown == PostSlots.MARKS) "Quote marks, the ANSWER label and the letters inside badges. A = the template's own color."
                else "The handles in the footer and the social cards. A = automatic."
            )
        }
    }
}
