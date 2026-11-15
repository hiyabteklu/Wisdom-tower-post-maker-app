package com.wisdomtower.post

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

/**
 * Bottom editor tabs and strips for the studio (UI_UX_PLAN.md + TEMPLATE_REDESIGN_PLAN.md sections 3.4, 3.8, 3.10).
 * Uses color circles only (no hex code input boxes anywhere).
 */

internal data class EditorSlider(
    val label: String,
    val value: Float,
    val range: ClosedFloatingPointRange<Float>,
    val display: String,
    val onChange: (Float) -> Unit
)

private val StripColors = listOf(
    "#FFFFFF", "#F8FAFC", "#CBD5E1", "#475569", "#1E293B", "#0F172A", "#05070B", "#000000",
    "#EF4444", "#DC2626", "#F97316", "#FACC15", "#FFD84D", "#22C55E", "#10B981", "#14B8A6",
    "#06B6D4", "#0EA5E9", "#3B82F6", "#2563EB", "#6366F1", "#8B5CF6", "#A855F7", "#EC4899"
)

@Composable
internal fun PaletteStrip(
    currentHex: String?,
    brandHex: String?,
    allowAuto: Boolean,
    onPick: (String?) -> Unit
) {
    val cleanBrand = brandHex?.uppercase()?.takeIf { it.matches(Regex("^#[0-9A-F]{6}$")) }
    val cleanCurrent = currentHex?.uppercase()?.takeIf { it.matches(Regex("^#[0-9A-F]{6}$")) }
    val colors = (listOfNotNull(cleanBrand, cleanCurrent) + StripColors).distinct()

    Row(
        Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (allowAuto) {
            PostSwatch(null, currentHex.isNullOrBlank()) { onPick(null) }
        }
        colors.forEach { hex ->
            PostSwatch(hex, hex.equals(currentHex, ignoreCase = true)) { onPick(hex) }
        }
    }
}

@Composable
internal fun TemplateStrip(
    doc: PostDoc,
    onDoc: (PostDoc) -> Unit,
    library: PostLibrary,
    onOpenLibrary: () -> Unit
) {
    val activeSpec = postTemplate(doc.templateId)
    val families = listOf("All") + PostTemplates.map { it.family }.distinct() + listOf("Saved")
    var selectedFamily by rememberSaveable { mutableStateOf("All") }

    val filtered = when (selectedFamily) {
        "All" -> PostTemplates
        "Saved" -> PostTemplates.filter { it.id in library.bookmarks }.ifEmpty { PostTemplates }
        else -> PostTemplates.filter { it.family == selectedFamily }
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // Family filter row + Browse all button
        Row(
            Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilterChip(
                selected = false,
                onClick = onOpenLibrary,
                label = { Text("Gallery", fontSize = 12.sp) },
                leadingIcon = { Icon(Icons.Outlined.GridView, contentDescription = null) }
            )
            families.forEach { fam ->
                FilterChip(
                    selected = selectedFamily == fam,
                    onClick = { selectedFamily = fam },
                    label = { Text(fam, fontSize = 12.sp) }
                )
            }
        }

        // Full 2-line template cards (Section 3.8: never cut template names to 1 word)
        Row(
            Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            filtered.forEach { spec ->
                val isSelected = spec.id == activeSpec.id
                val shape = RoundedCornerShape(12.dp)
                Column(
                    Modifier
                        .width(146.dp)
                        .clip(shape)
                        .background(if (isSelected) PostPalette.Raised else PostPalette.Surface)
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) PostPalette.Mint else PostPalette.Line,
                            shape = shape
                        )
                        .clickable {
                            val nextTexts = PostSlots.withDerivedSlots(doc.texts, spec.slots)
                            onDoc(
                                doc.copy(
                                    templateId = spec.id,
                                    lookIndex = 0,
                                    texts = nextTexts
                                )
                            )
                        }
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = spec.name,
                        color = if (isSelected) PostPalette.Mint else PostPalette.Text,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                        fontSize = 13.sp,
                        maxLines = 2,
                        minLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 16.sp
                    )
                    Text(
                        text = spec.family,
                        color = PostPalette.Muted,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        // Look 1 / 2 / 3 chips when the template has color & mood looks (Section 3.10)
        if (activeSpec.looks.isNotEmpty()) {
            Row(
                Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PostSmallLabel("Look:")
                val totalLooks = activeSpec.looks.size.coerceAtLeast(1)
                for (i in 0 until totalLooks) {
                    val lookName = activeSpec.looks.getOrNull(i)?.name ?: "Look ${i + 1}"
                    FilterChip(
                        selected = doc.lookIndex == i,
                        onClick = {
                            val look = activeSpec.looks.getOrNull(i)
                            onDoc(
                                doc.copy(
                                    lookIndex = i,
                                    accentHex = look?.accentHex,
                                    bgMode = if (look?.bgHex != null) BgMode.SOLID else BgMode.TEMPLATE,
                                    bgStartHex = look?.bgHex ?: doc.bgStartHex
                                )
                            )
                        },
                        label = { Text("Look ${i + 1}: $lookName", fontSize = 12.sp) }
                    )
                }
            }
        }
    }
}

@Composable
internal fun EditorTabs(
    tab: String,
    onTab: (String) -> Unit,
    doc: PostDoc,
    onDoc: (PostDoc) -> Unit,
    brand: PostBrand,
    library: PostLibrary,
    selectedSlot: String?,
    onSelectSlot: (String?) -> Unit,
    selectedIcon: Int,
    onSelectIcon: (Int) -> Unit,
    moveLogo: Boolean,
    onMoveLogo: (Boolean) -> Unit,
    moveOn: Boolean,
    onMove: (Boolean) -> Unit,
    onPickPhoto: () -> Unit,
    onPickBackgroundPhoto: () -> Unit,
    onPickLogo: () -> Unit,
    onBringWords: (() -> Unit)?,
    onOpenLibrary: () -> Unit
) {
    val spec = postTemplate(doc.templateId).withLookIndex(doc.lookIndex)
    val layout = spec.layout(doc.size)
    var styleSub by rememberSaveable { mutableStateOf("colors") }
    var photoSub by rememberSaveable { mutableStateOf("bg") }
    var iconGroup by rememberSaveable { mutableStateOf("mine") }

    val tabs = listOf(
        "template" to "Template",
        "words" to "Words",
        "style" to "Style",
        "photo" to "Photo",
        "icons" to "Icons"
    )

    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // Primary tab row
        Row(
            Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEach { (id, label) ->
                FilterChip(
                    selected = tab == id,
                    onClick = { onTab(id) },
                    label = { Text(label, fontWeight = if (tab == id) FontWeight.Bold else FontWeight.Normal) }
                )
            }
        }

        when (tab) {
            "template" -> Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                TemplateStrip(doc = doc, onDoc = onDoc, library = library, onOpenLibrary = onOpenLibrary)
                Row(
                    Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PostSmallLabel("Format:")
                    PostSize.values().forEach { s ->
                        FilterChip(
                            selected = doc.size == s,
                            onClick = { onDoc(doc.copy(size = s)) },
                            label = { Text(s.label, fontSize = 12.sp) }
                        )
                    }
                }
            }

            "words" -> Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (onBringWords != null) {
                        OutlinedButton(onClick = onBringWords) {
                            Text("Bring last words", fontSize = 12.sp)
                        }
                    }
                    if (doc.hasOffsets()) {
                        OutlinedButton(onClick = { onDoc(doc.withoutOffsets()) }) {
                            Text("Reset moved boxes", fontSize = 12.sp)
                        }
                    }
                }

                // Post-wide size & shadow quick sliders
                PostLabeledSlider(
                    label = "All words size",
                    value = doc.textScale,
                    range = 0.65f..1.45f,
                    display = "${(doc.textScale * 100).roundToInt()}%"
                ) { v ->
                    onDoc(doc.copy(textScale = v))
                }
                PostLabeledSlider(
                    label = "All words shadow",
                    value = doc.shadowAll.strength.toFloat(),
                    range = 0f..100f,
                    display = if (doc.shadowAll.strength == 0) "Template" else "${doc.shadowAll.strength}"
                ) { v ->
                    onDoc(doc.copy(shadowAll = doc.shadowAll.copy(strength = v.roundToInt().coerceIn(0, 100))))
                }

                val slots = spec.slots.ifEmpty {
                    listOf(PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.BODY)
                }
                slots.forEach { slot ->
                    WordBox(
                        slot = slot,
                        doc = doc,
                        onDoc = onDoc,
                        brand = brand,
                        selectedOnCanvas = selectedSlot == slot,
                        onSelectOnCanvas = { onSelectSlot(if (selectedSlot == slot) null else slot) }
                    )
                }

                OtherTextStyles(doc = doc, onDoc = onDoc, brand = brand)
            }

            "style" -> Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        "colors" to "Colors",
                        "backdrop" to "Backdrop & Panels",
                        "fonts" to "Fonts",
                        "logo" to "Logo & Footer"
                    ).forEach { (id, label) ->
                        FilterChip(
                            selected = styleSub == id,
                            onClick = { styleSub = id },
                            label = { Text(label, fontSize = 12.sp) }
                        )
                    }
                }

                when (styleSub) {
                    "colors" -> Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        PostSmallLabel("Accent color (A = brand default)")
                        PaletteStrip(
                            currentHex = doc.accentHex,
                            brandHex = brand.accentHex,
                            allowAuto = true,
                            onPick = { hex -> onDoc(doc.copy(accentHex = hex)) }
                        )
                        PostSmallLabel("Text color (A = template default)")
                        PaletteStrip(
                            currentHex = doc.textHex,
                            brandHex = brand.lightHex,
                            allowAuto = true,
                            onPick = { hex -> onDoc(doc.copy(textHex = hex)) }
                        )
                        PostSmallLabel("Background color (A = template default)")
                        PaletteStrip(
                            currentHex = if (doc.bgMode == BgMode.SOLID) doc.bgStartHex else null,
                            brandHex = brand.darkHex,
                            allowAuto = true,
                            onPick = { hex ->
                                if (hex == null) {
                                    onDoc(doc.copy(bgMode = BgMode.TEMPLATE, backgroundHex = null))
                                } else {
                                    onDoc(doc.copy(bgMode = BgMode.SOLID, bgStartHex = hex, backgroundHex = hex))
                                }
                            }
                        )
                    }

                    "backdrop" -> Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        PostSmallLabel("Background pattern")
                        Row(
                            Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            FilterChip(
                                selected = doc.backdrop == null,
                                onClick = { onDoc(doc.copy(backdrop = null)) },
                                label = { Text("Template", fontSize = 12.sp) }
                            )
                            Backdrop.values().forEach { b ->
                                FilterChip(
                                    selected = doc.backdrop == b,
                                    onClick = { onDoc(doc.copy(backdrop = b)) },
                                    label = { Text(b.name.lowercase().replaceFirstChar { it.uppercase() }, fontSize = 12.sp) }
                                )
                            }
                        }

                        PostLabeledSlider(
                            label = "Cards & panels opacity",
                            value = doc.panelOpacity.toFloat(),
                            range = 0f..100f,
                            display = "${doc.panelOpacity}%"
                        ) { v ->
                            onDoc(doc.copy(panelOpacity = v.roundToInt().coerceIn(0, 100)))
                        }

                        PostSmallLabel("Gradient glow & film grain")
                        Row(
                            Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            MeshStyle.values().forEach { m ->
                                FilterChip(
                                    selected = doc.meshStyle == m,
                                    onClick = { onDoc(doc.copy(meshStyle = m)) },
                                    label = { Text(m.label, fontSize = 12.sp) }
                                )
                            }
                            FilterChip(
                                selected = doc.grain,
                                onClick = { onDoc(doc.copy(grain = !doc.grain)) },
                                label = { Text("Film grain", fontSize = 12.sp) }
                            )
                        }
                    }

                    "fonts" -> FontsSection(doc = doc, onDoc = onDoc)

                    "logo" -> Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        PostSwitchRow("Show logo header", doc.showLogo) { onDoc(doc.copy(showLogo = it)) }
                        PostSwitchRow("Show social footer", doc.showFooter) { onDoc(doc.copy(showFooter = it)) }
                        OutlinedButton(onClick = onPickLogo) {
                            Text("Change brand logo image", fontSize = 12.sp)
                        }
                        if (doc.showLogo) {
                            LogoMoveRow(doc = doc, onDoc = onDoc, moveLogo = moveLogo, onMoveLogo = onMoveLogo)
                        }
                    }
                }
            }

            "photo" -> Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        "bg" to "Background photo",
                        "window" to "Template photo",
                        "cutout" to "Cut-out & Move"
                    ).forEach { (id, label) ->
                        FilterChip(
                            selected = photoSub == id,
                            onClick = { photoSub = id },
                            label = { Text(label, fontSize = 12.sp) }
                        )
                    }
                }

                when (photoSub) {
                    "bg" -> Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            "Any template can have a photo behind it. Blend it with the template colors or dim it so words stay easy to read.",
                            color = PostPalette.Muted,
                            fontSize = 12.sp
                        )
                        Row(
                            Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedButton(onClick = onPickBackgroundPhoto) {
                                Text(if (doc.backgroundPhotoUri == null) "Pick background photo" else "Change background photo", fontSize = 12.sp)
                            }
                            if (!doc.photoUri.isNullOrBlank()) {
                                FilterChip(
                                    selected = doc.photoAsBackground,
                                    onClick = {
                                        val next = !doc.photoAsBackground
                                        val nextOpacity = if (next && doc.backgroundPhotoOpacity <= 0.01f) 0.55f else doc.backgroundPhotoOpacity
                                        onDoc(doc.copy(photoAsBackground = next, backgroundPhotoOpacity = nextOpacity))
                                    },
                                    label = { Text("Use template photo as background", fontSize = 12.sp) }
                                )
                            }
                            if (doc.backgroundPhotoUri != null || doc.photoAsBackground) {
                                OutlinedButton(
                                    onClick = {
                                        onDoc(
                                            doc.copy(
                                                backgroundPhotoUri = null,
                                                photoAsBackground = false,
                                                backgroundPhotoOpacity = 0.35f,
                                                backgroundPhotoDim = 0,
                                                backgroundPhotoBlur = 0,
                                                backgroundPhotoZoom = 1f,
                                                backgroundPhotoTone = BgPhotoTone.NONE
                                            )
                                        )
                                    }
                                ) {
                                    Text("Remove", fontSize = 12.sp)
                                }
                            }
                        }

                        val hasBgPhoto = !doc.backgroundPhotoUri.isNullOrBlank() || (doc.photoAsBackground && !doc.photoUri.isNullOrBlank())
                        if (hasBgPhoto) {
                            PostLabeledSlider(
                                label = "Photo strength",
                                value = doc.backgroundPhotoOpacity,
                                range = 0f..1f,
                                display = "${(doc.backgroundPhotoOpacity * 100).roundToInt()}%"
                            ) { v ->
                                onDoc(doc.copy(backgroundPhotoOpacity = v))
                            }

                            PostLabeledSlider(
                                label = "Darken background",
                                value = doc.backgroundPhotoDim.toFloat(),
                                range = 0f..100f,
                                display = "${doc.backgroundPhotoDim}%"
                            ) { v ->
                                onDoc(doc.copy(backgroundPhotoDim = v.roundToInt().coerceIn(0, 100)))
                            }

                            PostLabeledSlider(
                                label = "Blur",
                                value = doc.backgroundPhotoBlur.toFloat(),
                                range = 0f..100f,
                                display = "${doc.backgroundPhotoBlur}%"
                            ) { v ->
                                onDoc(doc.copy(backgroundPhotoBlur = v.roundToInt().coerceIn(0, 100)))
                            }

                            PostLabeledSlider(
                                label = "Zoom",
                                value = doc.backgroundPhotoZoom,
                                range = 1f..2.5f,
                                display = String.format("%.1fx", doc.backgroundPhotoZoom)
                            ) { v ->
                                onDoc(doc.copy(backgroundPhotoZoom = v))
                            }

                            PostLabeledSlider(
                                label = "Cards & panels opacity",
                                value = doc.panelOpacity.toFloat(),
                                range = 0f..100f,
                                display = "${doc.panelOpacity}%"
                            ) { v ->
                                onDoc(doc.copy(panelOpacity = v.roundToInt().coerceIn(0, 100)))
                            }

                            PostSmallLabel("Photo color tone")
                            Row(
                                Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                BgPhotoTone.values().forEach { t ->
                                    FilterChip(
                                        selected = doc.backgroundPhotoTone == t,
                                        onClick = { onDoc(doc.copy(backgroundPhotoTone = t)) },
                                        label = {
                                            Text(
                                                when (t) {
                                                    BgPhotoTone.NONE -> "Normal"
                                                    BgPhotoTone.MONO -> "Black & white"
                                                    BgPhotoTone.DUOTONE -> "Brand duotone"
                                                },
                                                fontSize = 12.sp
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }

                    "window" -> Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(onClick = onPickPhoto) {
                                Text(if (doc.photoUri == null) "Pick template photo" else "Change template photo", fontSize = 12.sp)
                            }
                            if (doc.photoUri != null) {
                                OutlinedButton(
                                    onClick = {
                                        onDoc(doc.copy(photoUri = null, photoZoom = 1f, photoFocusX = 0.5f, photoFocusY = 0.5f))
                                    }
                                ) {
                                    Text("Remove", fontSize = 12.sp)
                                }
                            }
                        }
                        if (doc.photoUri != null) {
                            PostLabeledSlider(
                                label = "Photo zoom",
                                value = doc.photoZoom,
                                range = 1f..3f,
                                display = String.format("%.1fx", doc.photoZoom)
                            ) { onDoc(doc.copy(photoZoom = it)) }
                            PostLabeledSlider(
                                label = "Focus left / right",
                                value = doc.photoFocusX,
                                range = 0f..1f,
                                display = "${(doc.photoFocusX * 100).roundToInt()}%"
                            ) { onDoc(doc.copy(photoFocusX = it)) }
                            PostLabeledSlider(
                                label = "Focus up / down",
                                value = doc.photoFocusY,
                                range = 0f..1f,
                                display = "${(doc.photoFocusY * 100).roundToInt()}%"
                            ) { onDoc(doc.copy(photoFocusY = it)) }
                        }
                        if (layout.photo != null) {
                            PostSmallLabel("Photo frame shape")
                            Row(
                                Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                FilterChip(
                                    selected = doc.photoFrame == null,
                                    onClick = { onDoc(doc.copy(photoFrame = null)) },
                                    label = { Text("Template", fontSize = 12.sp) }
                                )
                                PhotoFrame.values().forEach { frame ->
                                    FilterChip(
                                        selected = doc.photoFrame == frame,
                                        onClick = { onDoc(doc.copy(photoFrame = frame)) },
                                        label = { Text(frame.label, fontSize = 12.sp) }
                                    )
                                }
                                FilterChip(
                                    selected = doc.frameBorder,
                                    onClick = { onDoc(doc.copy(frameBorder = !doc.frameBorder)) },
                                    label = { Text("Border", fontSize = 12.sp) }
                                )
                            }
                        }
                    }

                    "cutout" -> EngineControls(
                        doc = doc,
                        onDoc = onDoc,
                        slots = spec.slots,
                        hasPhotoWindow = layout.photo != null,
                        moveOn = moveOn,
                        onMove = onMove
                    )
                }
            }

            "icons" -> Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val slider = if (iconGroup == "size") iconSizeSlider(doc, selectedIcon, onDoc) else null
                if (slider != null) {
                    PostLabeledSlider(
                        label = slider.label,
                        value = slider.value,
                        range = slider.range,
                        display = slider.display,
                        onChange = slider.onChange
                    )
                }
                Row(
                    Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        "mine" to "My icons",
                        "shapes" to "Shapes",
                        "placed" to "On this post",
                        "size" to "Size",
                        "color" to "Color",
                        "layer" to "Layer"
                    ).forEach { (id, label) ->
                        FilterChip(
                            selected = iconGroup == id,
                            onClick = { iconGroup = id },
                            label = { Text(label, fontSize = 12.sp) }
                        )
                    }
                }
                IconOptions(
                    group = iconGroup,
                    doc = doc,
                    onDoc = onDoc,
                    selected = selectedIcon,
                    onSelect = onSelectIcon
                )
            }
        }
        Spacer(Modifier.height(8.dp))
    }
}
