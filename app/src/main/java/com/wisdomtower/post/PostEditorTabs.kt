package com.wisdomtower.post

import android.graphics.Bitmap
import android.util.LruCache
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * The tabs of the editor (UI_UX_PLAN.md section 4.2). Every tab shows ONE horizontal row of options at the bottom, so the
 * editor never scrolls vertically. A tab with several controls (Look, Photo, Icons, Logo, Footer, Font) has a small row of
 * group chips above its options; tapping a group swaps the options row. The area has a fixed height so the preview never jumps.
 * The Icons tab draws its own options (see PostIconsTab.kt); EditorOptions does nothing for it.
 */
enum class EditorTab(val title: String) {
    TEMPLATES("Templates"),
    WORDS("Words"),
    LOOK("Look"),
    PHOTO("Photo"),
    ICONS("Icons"),
    LOGO("Logo"),
    FOOTER("Footer"),
    FONT("Font")
}

internal class EditorGroup(val key: String, val label: String)

/** What the one shared slider above the tabs controls right now. */
internal class EditorSlider(
    val label: String,
    val value: Float,
    val range: ClosedFloatingPointRange<Float>,
    val display: String,
    val onChange: (Float) -> Unit
)

private const val MAX_FOOTER_ICONS = 4

private fun shortSlot(slot: String): String = when (slot) {
    PostSlots.EYEBROW -> "Label"
    PostSlots.HEADLINE -> "Headline"
    PostSlots.BODY -> "Message"
    PostSlots.NUMBER -> "Number"
    PostSlots.LIST -> "List"
    PostSlots.BUTTON -> "Button"
    else -> slot
}

/** The group chips of a tab. Never empty. */
internal fun editorGroups(
    tab: EditorTab,
    doc: PostDoc,
    slots: List<String>,
    hasPhotoArea: Boolean,
    canBringWords: Boolean
): List<EditorGroup> = when (tab) {
    EditorTab.TEMPLATES -> listOf(EditorGroup("all", "Templates"))
    EditorTab.WORDS -> {
        val list = slots.map { EditorGroup(it, shortSlot(it)) }.ifEmpty { listOf(EditorGroup("none", "Words")) }
        if (canBringWords) list + EditorGroup("bring", "Bring my words") else list
    }
    EditorTab.LOOK -> buildList {
        add(EditorGroup("style", "Style"))
        add(EditorGroup("size", "Text size"))
        add(EditorGroup("pairs", "Color pairs"))
        add(EditorGroup("main", "Main color"))
        add(EditorGroup("text", "Text color"))
        add(EditorGroup("bg", "Background"))
        if (doc.bgMode == BgMode.SOLID) add(EditorGroup("bgfrom", "Solid color"))
        if (doc.bgMode == BgMode.GRADIENT) {
            add(EditorGroup("bgfrom", "From"))
            add(EditorGroup("bgto", "To"))
            add(EditorGroup("bgdir", "Direction"))
        }
    }
    EditorTab.PHOTO -> buildList {
        add(EditorGroup("photo", "Photo"))
        if (doc.photoUri != null && hasPhotoArea) {
            add(EditorGroup("zoom", "Zoom"))
            add(EditorGroup("x", "Left-right"))
            add(EditorGroup("y", "Up-down"))
            add(EditorGroup("dim", "Darkness"))
        }
        add(EditorGroup("bgimg", "Background image"))
        if (doc.backgroundPhotoUri != null) add(EditorGroup("bgop", "Opacity"))
    }
    EditorTab.ICONS -> listOf(
        EditorGroup("mine", "My icons"),
        EditorGroup("shapes", "Shapes"),
        EditorGroup("placed", "On this post"),
        EditorGroup("size", "Size"),
        EditorGroup("color", "Color"),
        EditorGroup("layer", "Layer")
    )
    EditorTab.LOGO -> listOf(
        EditorGroup("show", "Show"),
        EditorGroup("corner", "Corner"),
        EditorGroup("size", "Size"),
        EditorGroup("pic", "Picture"),
        EditorGroup("move", "Move")
    )
    EditorTab.FOOTER -> listOf(
        EditorGroup("show", "Show"),
        EditorGroup("style", "Style"),
        EditorGroup("look", "Icon look"),
        EditorGroup("size", "Icon size"),
        EditorGroup("items", "Icons"),
        EditorGroup("kind", "Platform"),
        EditorGroup("handle", "Handle"),
        EditorGroup("color", "Icon color")
    )
    EditorTab.FONT -> listOf(EditorGroup("en", "English"), EditorGroup("am", "Amharic"))
}

internal fun editorSlider(tab: EditorTab, group: String, doc: PostDoc, templateDim: Int?, onDoc: (PostDoc) -> Unit): EditorSlider? {
    if (tab != EditorTab.PHOTO) return null
    return when (group) {
        "zoom" -> EditorSlider("Zoom", doc.photoZoom, 1f..3f, "x" + String.format("%.1f", doc.photoZoom)) { onDoc(doc.copy(photoZoom = it)) }
        "x" -> EditorSlider("Left-right", doc.photoFocusX, 0f..1f, (doc.photoFocusX * 100).toInt().toString() + "%") { onDoc(doc.copy(photoFocusX = it)) }
        "y" -> EditorSlider("Up-down", doc.photoFocusY, 0f..1f, (doc.photoFocusY * 100).toInt().toString() + "%") { onDoc(doc.copy(photoFocusY = it)) }
        "dim" -> {
            val dim = if (doc.photoDim >= 0) doc.photoDim else (templateDim ?: 0)
            EditorSlider("Darkness", dim.toFloat(), 0f..230f, dim.toString()) { onDoc(doc.copy(photoDim = it.toInt())) }
        }
        "bgop" -> EditorSlider("Opacity", doc.backgroundPhotoOpacity, 0f..1f, (doc.backgroundPhotoOpacity * 100).toInt().toString() + "%") {
            onDoc(doc.copy(backgroundPhotoOpacity = it))
        }
        else -> null
    }
}

// ---------------------------------------------------------------------------------------------------------------------
// Colors: circles only, never a typed code. Stored values stay hex strings so old drafts keep working.
// ---------------------------------------------------------------------------------------------------------------------

internal object PostColors {
    val bright = listOf("#EF4444", "#F97316", "#F59E0B", "#EAB308", "#22C55E", "#14B8A6", "#0EA5E9", "#6366F1", "#A855F7", "#EC4899")
    val soft = listOf("#FECACA", "#FED7AA", "#FDE68A", "#BBF7D0", "#A7F3D0", "#BAE6FD", "#C7D2FE", "#E9D5FF", "#FBCFE8")
    val dark = listOf("#7F1D1D", "#7C2D12", "#78350F", "#14532D", "#134E4A", "#0C4A6E", "#1E1B4B", "#4C1D95", "#831843")
    val neutral = listOf("#FFFFFF", "#F4F6EF", "#D1D5DB", "#9CA3AF", "#4B5563", "#1F2937", "#111827", "#000000")
}

/** First is the pop color (bands, rings), second the ink (big words). One tap applies both. */
private val ColorPairs = listOf(
    "#D31926" to "#122A69",
    "#1D4ED8" to "#0F1E4A",
    "#F97316" to "#1F2937",
    "#7C3AED" to "#2E1065",
    "#0D9488" to "#0F2F2C",
    "#DB2777" to "#4A0D2B",
    "#D97706" to "#3B1F0A",
    "#176B4D" to "#18221D"
)

/** A round color choice with a 48 dp tap area. Selected = a ring. label (B or A) marks the "use the brand or automatic color" circle. */
@Composable
internal fun PostCircle(hex: String?, selected: Boolean, label: String? = null, onClick: () -> Unit) {
    val fill = hex?.let { runCatching { Color(android.graphics.Color.parseColor(it)) }.getOrNull() }
    Box(Modifier.size(48.dp).clickable(onClick = onClick), contentAlignment = Alignment.Center) {
        Box(
            Modifier.size(36.dp).clip(CircleShape)
                .background(fill ?: PostPalette.Raised)
                .border(if (selected) 3.dp else 1.dp, if (selected) PostPalette.Mint else PostPalette.Line, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (label != null) {
                val ink = if (fill != null && fill.luminance() > 0.6f) Color.Black else Color.White
                Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (fill == null) PostPalette.Text else ink)
            }
        }
    }
}

/** One horizontal strip: the brand or automatic circle first (when showAuto), then Bright, Soft, Dark and Neutral. */
@Composable
internal fun PaletteStrip(selected: String?, autoHex: String?, showAuto: Boolean, onPick: (String?) -> Unit) {
    Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), verticalAlignment = Alignment.CenterVertically) {
        if (showAuto) {
            PostCircle(autoHex, selected == null, if (autoHex != null) "B" else "A") { onPick(null) }
            Spacer(Modifier.width(10.dp))
        }
        listOf(PostColors.bright, PostColors.soft, PostColors.dark, PostColors.neutral).forEachIndexed { index, colors ->
            if (index > 0) Spacer(Modifier.width(10.dp))
            colors.forEach { hex -> PostCircle(hex, selected.equals(hex, ignoreCase = true)) { onPick(hex) } }
        }
    }
}

/** A two-color button: left half is the pop color, right half the ink. */
@Composable
private fun PairSwatch(accentHex: String, textHex: String, selected: Boolean, onClick: () -> Unit) {
    val a = remember(accentHex) { runCatching { Color(android.graphics.Color.parseColor(accentHex)) }.getOrDefault(Color.Gray) }
    val t = remember(textHex) { runCatching { Color(android.graphics.Color.parseColor(textHex)) }.getOrDefault(Color.DarkGray) }
    val shape = RoundedCornerShape(12.dp)
    Row(
        Modifier.width(60.dp).height(44.dp).clip(shape)
            .border(if (selected) 3.dp else 1.dp, if (selected) PostPalette.Mint else PostPalette.Line, shape)
            .clickable(onClick = onClick)
    ) {
        Box(Modifier.weight(1f).fillMaxSize().background(a))
        Box(Modifier.weight(1f).fillMaxSize().background(t))
    }
}

// ---------------------------------------------------------------------------------------------------------------------
// Template strip (a tab of its own until the Library home exists)
// ---------------------------------------------------------------------------------------------------------------------

private val thumbCache = LruCache<String, Bitmap>(48)

@Composable
internal fun TemplateStrip(doc: PostDoc, brand: PostBrand, library: PostLibrary, onDoc: (PostDoc) -> Unit) {
    val state = rememberLazyListState()
    val index = PostTemplates.indexOfFirst { it.id == doc.templateId }.coerceAtLeast(0)
    LaunchedEffect(doc.templateId) {
        if (state.layoutInfo.visibleItemsInfo.none { it.index == index }) state.animateScrollToItem(index)
    }
    LazyRow(
        state = state,
        contentPadding = PaddingValues(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(PostTemplates, key = { it.id }) { spec ->
            TemplateCard(spec, doc.size, brand, library.edited[spec.id], doc.accentHex, doc.textHex, spec.id == doc.templateId) { onDoc(doc.withTemplate(spec)) }
        }
    }
}

@Composable
private fun TemplateCard(
    spec: PostTemplateSpec,
    size: PostSize,
    brand: PostBrand,
    editedAt: Long?,
    accentHex: String?,
    textHex: String?,
    selected: Boolean,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val ratio = size.width.toFloat() / size.height
    val thumbH = 84.dp
    val thumbW = thumbH * ratio
    val thumb by produceState<Bitmap?>(null, spec.id, size, brand, editedAt, accentHex, textHex) {
        value = withContext(Dispatchers.IO) {
            val cached = PostThumbs.get(context, spec, size, brand, editedAt)
            if (cached != null && (accentHex == null && textHex == null)) cached else {
                val draft = if (editedAt != null) PostDrafts.loadDraft(context, spec.id) else null
                val sample = draft ?: PostDoc(templateId = spec.id, size = size, texts = spec.samples)
                PostRenderer.thumbnail(context, sample.copy(accentHex = accentHex, textHex = textHex), brand, 300).bitmap
            }
        }
    }
    val shape = RoundedCornerShape(10.dp)
    Column(
        Modifier.width(maxOf(thumbW, 84.dp)).clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            Modifier.width(thumbW).height(thumbH).clip(shape)
                .background(PostPalette.Surface)
                .border(if (selected) 3.dp else 1.dp, if (selected) PostPalette.Mint else PostPalette.Line, shape)
        ) {
            thumb?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = spec.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }
        Text(
            spec.name,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = if (selected) PostPalette.Mint else PostPalette.Text,
            modifier = Modifier.padding(top = 4.dp)
        )
        Text(spec.family, fontSize = 9.sp, color = PostPalette.Muted, maxLines = 1)
    }
}

// ---------------------------------------------------------------------------------------------------------------------
// The options row of each tab
// ---------------------------------------------------------------------------------------------------------------------

@Composable
private fun ChipRow(content: @Composable RowScope.() -> Unit) {
    Row(
        Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}

@Composable
private fun Hint(text: String) {
    Text(text, fontSize = 12.sp, color = PostPalette.Muted, modifier = Modifier.widthIn(max = 300.dp).padding(horizontal = 4.dp))
}

@Composable
internal fun EditorOptions(
    tab: EditorTab,
    group: String,
    doc: PostDoc,
    onDoc: (PostDoc) -> Unit,
    brand: PostBrand,
    onBrand: (PostBrand) -> Unit,
    hasPhotoArea: Boolean,
    onPickPhoto: () -> Unit,
    onPickBackgroundPhoto: () -> Unit,
    onPickLogo: () -> Unit,
    moveLogo: Boolean,
    onMoveLogo: (Boolean) -> Unit,
    footerItem: Int,
    onFooterItem: (Int) -> Unit
) {
    when (tab) {
        EditorTab.TEMPLATES -> TemplateStrip(doc, brand, onDoc)
        EditorTab.WORDS -> WordsOptions(doc, onDoc, group)
        EditorTab.LOOK -> LookOptions(doc, onDoc, brand, group)
        EditorTab.PHOTO -> PhotoOptions(doc, onDoc, group, hasPhotoArea, onPickPhoto, onPickBackgroundPhoto)
        EditorTab.ICONS -> Unit // drawn by IconOptions in PostIconsTab.kt, called from the studio
        EditorTab.LOGO -> LogoOptions(doc, onDoc, brand, onBrand, group, onPickLogo, moveLogo, onMoveLogo)
        EditorTab.FOOTER -> FooterOptions(doc, onDoc, brand, onBrand, group, footerItem, onFooterItem)
        EditorTab.FONT -> FontRow(doc, onDoc, amharic = group == "am")
    }
}

@Composable
private fun WordsOptions(doc: PostDoc, onDoc: (PostDoc) -> Unit, slot: String) {
    if (slot == "none") {
        ChipRow { Hint("This template has no editable words.") }
        return
    }
    OutlinedTextField(
        value = doc.texts[slot].orEmpty(),
        onValueChange = { onDoc(doc.withText(slot, it)) },
        placeholder = { Text(PostSlots.label(slot)) },
        maxLines = 3,
        modifier = Modifier.fillMaxWidth().height(72.dp).padding(horizontal = 12.dp)
    )
}

@Composable
private fun LookOptions(doc: PostDoc, onDoc: (PostDoc) -> Unit, brand: PostBrand, group: String) {
    when (group) {
        "style" -> ChipRow {
            FilterChip(selected = doc.richness < 2, onClick = { onDoc(doc.copy(richness = 1)) }, label = { Text("Clean") })
            FilterChip(selected = doc.richness >= 2, onClick = { onDoc(doc.copy(richness = 2)) }, label = { Text("Rich") })
        }
        "size" -> ChipRow {
            listOf("Small" to 0.9f, "Medium" to 1f, "Large" to 1.12f).forEach { (name, scale) ->
                FilterChip(selected = abs(doc.textScale - scale) < 0.01f, onClick = { onDoc(doc.copy(textScale = scale)) }, label = { Text(name) })
            }
        }
        "pairs" -> ChipRow {
            PairSwatch(brand.primaryHex, brand.darkHex, doc.accentHex == null && doc.textHex == null) {
                onDoc(doc.copy(accentHex = null, textHex = null))
            }
            ColorPairs.forEach { (accent, ink) ->
                PairSwatch(accent, ink, doc.accentHex.equals(accent, ignoreCase = true) && doc.textHex.equals(ink, ignoreCase = true)) {
                    onDoc(doc.copy(accentHex = accent, textHex = ink))
                }
            }
        }
        "main" -> PaletteStrip(doc.accentHex, brand.primaryHex, true) { onDoc(doc.copy(accentHex = it)) }
        "text" -> PaletteStrip(doc.textHex, brand.darkHex, true) { onDoc(doc.copy(textHex = it)) }
        "bg" -> ChipRow {
            listOf(BgMode.TEMPLATE to "Template", BgMode.SOLID to "Solid", BgMode.GRADIENT to "Gradient").forEach { (mode, name) ->
                FilterChip(selected = doc.bgMode == mode, onClick = { onDoc(doc.copy(bgMode = mode)) }, label = { Text(name) })
            }
        }
        "bgfrom" -> PaletteStrip(doc.bgStartHex, null, false) { hex -> if (hex != null) onDoc(doc.copy(bgStartHex = hex)) }
        "bgto" -> PaletteStrip(doc.bgEndHex, null, false) { hex -> if (hex != null) onDoc(doc.copy(bgEndHex = hex)) }
        "bgdir" -> ChipRow {
            // angle 0 = left to right, 90 = top to bottom (see PostRenderer.gradientShader)
            listOf(
                0 to "\u2192", 45 to "\u2198", 90 to "\u2193", 135 to "\u2199",
                180 to "\u2190", 225 to "\u2196", 270 to "\u2191", 315 to "\u2197"
            ).forEach { (angle, arrow) ->
                FilterChip(selected = doc.bgAngle == angle, onClick = { onDoc(doc.copy(bgAngle = angle)) }, label = { Text(arrow, fontSize = 18.sp) })
            }
        }
    }
}

@Composable
private fun PhotoOptions(
    doc: PostDoc,
    onDoc: (PostDoc) -> Unit,
    group: String,
    hasPhotoArea: Boolean,
    onPickPhoto: () -> Unit,
    onPickBackgroundPhoto: () -> Unit
) {
    when (group) {
        "photo" -> ChipRow {
            OutlinedButton(onClick = onPickPhoto) {
                Icon(Icons.Outlined.PhotoLibrary, contentDescription = null)
                Spacer(Modifier.width(6.dp))
                Text(if (doc.photoUri != null) "Replace photo" else "Pick photo")
            }
            if (doc.photoUri != null) TextButton(onClick = { onDoc(doc.copy(photoUri = null)) }) { Text("Remove") }
            if (!hasPhotoArea) Hint("This template has no photo area. Try Quote on photo, Subject cover or Photo announcement.")
        }
        "dim" -> ChipRow {
            Hint("Drag the slider above.")
            if (doc.photoDim >= 0) {
                FilterChip(selected = false, onClick = { onDoc(doc.copy(photoDim = -1)) }, label = { Text("Template darkness") })
            }
        }
        "bgimg" -> ChipRow {
            OutlinedButton(onClick = onPickBackgroundPhoto) {
                Icon(Icons.Outlined.PhotoLibrary, contentDescription = null)
                Spacer(Modifier.width(6.dp))
                Text(if (doc.backgroundPhotoUri != null) "Replace background" else "Add background image")
            }
            if (doc.backgroundPhotoUri != null) {
                TextButton(onClick = { onDoc(doc.copy(backgroundPhotoUri = null, backgroundPhotoOpacity = 1f)) }) { Text("Remove") }
            }
        }
        else -> ChipRow { Hint("Drag the slider above.") }
    }
}

@Composable
private fun LogoOptions(
    doc: PostDoc,
    onDoc: (PostDoc) -> Unit,
    brand: PostBrand,
    onBrand: (PostBrand) -> Unit,
    group: String,
    onPickLogo: () -> Unit,
    moveLogo: Boolean,
    onMoveLogo: (Boolean) -> Unit
) {
    when (group) {
        "show" -> ChipRow {
            FilterChip(selected = doc.showLogo, onClick = { onDoc(doc.copy(showLogo = !doc.showLogo)) }, label = { Text("Show logo") })
        }
        "corner" -> ChipRow {
            LogoCorner.values().forEach { corner ->
                FilterChip(
                    selected = brand.logoCorner == corner,
                    onClick = { onBrand(brand.copy(logoCorner = corner)) },
                    label = { Text(corner.name.lowercase().replaceFirstChar { it.uppercase() }) }
                )
            }
        }
        "size" -> ChipRow {
            listOf("S" to 0.8f, "M" to 1f, "L" to 1.25f).forEach { (name, scale) ->
                FilterChip(selected = abs(brand.logoScale - scale) < 0.01f, onClick = { onBrand(brand.copy(logoScale = scale)) }, label = { Text(name) })
            }
        }
        "pic" -> ChipRow {
            OutlinedButton(onClick = onPickLogo) { Text(if (brand.logoPath != null) "Replace logo" else "Pick logo from phone") }
        }
        "move" -> ChipRow {
            FilterChip(selected = moveLogo, onClick = { onMoveLogo(!moveLogo) }, label = { Text(if (moveLogo) "Done moving" else "Move logo") })
            FilterChip(
                selected = false,
                onClick = { onDoc(doc.copy(logoSize = (doc.logoSize * 0.85f).coerceAtLeast(0.4f))) },
                label = { Text("Smaller") }
            )
            FilterChip(
                selected = false,
                onClick = { onDoc(doc.copy(logoSize = (doc.logoSize * 1.18f).coerceAtMost(3f))) },
                label = { Text("Bigger") }
            )
            FilterChip(
                selected = false,
                onClick = { onMoveLogo(false); onDoc(doc.copy(logoCx = null, logoCy = null, logoSize = 1f)) },
                label = { Text("Reset logo") }
            )
            if (moveLogo) Hint("Drag on the preview, or tap where the logo should go.")
        }
    }
}

@Composable
private fun FooterOptions(
    doc: PostDoc,
    onDoc: (PostDoc) -> Unit,
    brand: PostBrand,
    onBrand: (PostBrand) -> Unit,
    group: String,
    footerItem: Int,
    onFooterItem: (Int) -> Unit
) {
    val items = brand.footerItems
    val sel = if (items.isEmpty()) -1 else footerItem.coerceIn(0, items.size - 1)
    fun change(edit: (FooterItem) -> FooterItem) {
        onBrand(brand.copy(footerItems = items.mapIndexed { i, old -> if (i == sel) edit(old) else old }))
    }
    when (group) {
        "show" -> ChipRow {
            FilterChip(selected = doc.showFooter, onClick = { onDoc(doc.copy(showFooter = !doc.showFooter)) }, label = { Text("Show footer icons") })
        }
        "style" -> ChipRow {
            FooterStyle.values().forEach { style ->
                FilterChip(
                    selected = brand.footerStyle == style,
                    onClick = { onBrand(brand.copy(footerStyle = style)) },
                    label = { Text(if (style == FooterStyle.PILL) "Pills" else "Line") }
                )
            }
        }
        "look" -> ChipRow {
            IconLook.values().forEach { look ->
                FilterChip(
                    selected = brand.iconLook == look,
                    onClick = { onBrand(brand.copy(iconLook = look)) },
                    label = { Text(when (look) { IconLook.PLAIN -> "Plain"; IconLook.BADGE -> "Badge"; IconLook.OFFICIAL -> "Platform colors" }) }
                )
            }
        }
        "size" -> ChipRow {
            listOf("Small" to 0.85f, "Medium" to 1f, "Large" to 1.25f).forEach { (name, scale) ->
                FilterChip(selected = abs(brand.iconScale - scale) < 0.01f, onClick = { onBrand(brand.copy(iconScale = scale)) }, label = { Text(name) })
            }
        }
        "items" -> ChipRow {
            items.forEachIndexed { i, item ->
                FilterChip(selected = i == sel, onClick = { onFooterItem(i) }, label = { Text((i + 1).toString() + " " + PostIcons.title(item.kind)) })
            }
            if (items.size < MAX_FOOTER_ICONS) {
                FilterChip(
                    selected = false,
                    onClick = {
                        onBrand(brand.copy(footerItems = items + FooterItem("telegram", "@")))
                        onFooterItem(items.size)
                    },
                    label = { Text("+ Add") }
                )
            }
            if (sel >= 0) {
                FilterChip(
                    selected = false,
                    onClick = {
                        onBrand(brand.copy(footerItems = items.filterIndexed { i, _ -> i != sel }))
                        onFooterItem(0)
                    },
                    label = { Text("Remove") }
                )
            }
        }
        else -> {
            if (sel < 0) {
                ChipRow { Hint("No icons yet. Open Icons and tap + Add.") }
                return
            }
            val item = items[sel]
            when (group) {
                "kind" -> ChipRow {
                    PostIcons.kinds.forEach { kind ->
                        FilterChip(selected = item.kind == kind, onClick = { change { it.copy(kind = kind) } }, label = { Text(PostIcons.title(kind)) })
                    }
                }
                "handle" -> OutlinedTextField(
                    value = item.label,
                    onValueChange = { text -> change { it.copy(label = text) } },
                    placeholder = { Text("Handle or link") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp)
                )
                "color" -> PaletteStrip(item.colorHex.ifBlank { null }, null, true) { hex -> change { it.copy(colorHex = hex ?: "") } }
            }
        }
    }
}

/** One row of fonts for one language, plus an Add button that imports .ttf or .otf files from the phone. */
@Composable
private fun FontRow(doc: PostDoc, onDoc: (PostDoc) -> Unit, amharic: Boolean) {
    val context = LocalContext.current
    var tick by remember { mutableStateOf(0) }
    val kind = if (amharic) PostFonts.AMHARIC else PostFonts.ENGLISH
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
        var added = 0
        uris.forEach { u -> if (PostFonts.importFont(context, u, kind) != null) added++ }
        tick++
        Toast.makeText(context, if (added > 0) "Added $added font(s)" else "Pick .ttf or .otf font files", Toast.LENGTH_LONG).show()
    }
    val fonts = remember(tick, amharic) { PostFonts.list(context, kind) }
    val current = if (amharic) doc.fontAmharic else doc.fontEnglish
    fun pick(path: String?) = onDoc(if (amharic) doc.copy(fontAmharic = path) else doc.copy(fontEnglish = path))
    ChipRow {
        FilterChip(selected = current == null, onClick = { pick(null) }, label = { Text("Default") })
        fonts.forEach { f ->
            FilterChip(selected = current == f.path, onClick = { pick(f.path) }, label = { Text(f.label) })
        }
        OutlinedButton(onClick = { picker.launch(arrayOf("*/*")) }) { Text("Add fonts") }
    }
}
