package com.wisdomtower.post

import android.content.Intent
import android.graphics.Bitmap
import android.util.LruCache
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val thumbCache = LruCache<String, Bitmap>(48)
private val AccentPresets = listOf("#176B4D", "#0F766E", "#0891B2", "#2563EB", "#4338CA", "#7C3AED", "#A21CAF", "#BE185D", "#E11D48", "#DC2626", "#EA580C", "#D97706", "#A16207", "#65A30D", "#334155")

/** One tap sets the main color and the text color together: first is the pop color (bands, rings), second the ink (big words). */
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

/**
 * One screen. The top part never scrolls: size chips, the live preview, and a strip of template cards you swipe
 * sideways. Everything below it scrolls up and down: your words, look, photo, logo, social icons and export.
 */
@Composable
fun PostStudioScreen(
    doc: PostDoc,
    onDoc: (PostDoc) -> Unit,
    brand: PostBrand,
    onBrand: (PostBrand) -> Unit,
    onPickPhoto: () -> Unit,
    onPickBackgroundPhoto: () -> Unit,
    onPickLogo: () -> Unit,
    onOpenBrand: () -> Unit,
    onBackHome: () -> Unit,
    onSaveProject: () -> Unit
) {
    var moveLogo by remember { mutableStateOf(false) }
    PostTheme {
        Scaffold(containerColor = PostPalette.Ink) { padding ->
            BoxWithConstraints(Modifier.fillMaxSize().padding(padding).imePadding()) {
                val previewHeight = maxHeight * 0.34f
                Column(Modifier.fillMaxSize()) {
                    StudioTopRow(doc, onDoc, onOpenBrand, onBackHome, onSaveProject)
                    PreviewPane(doc, brand, previewHeight, moveLogo, onDoc)
                    TemplateStrip(doc, brand, onDoc)
                    Column(
                        Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()).padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StudioDetails(doc, onDoc, brand, onBrand, onPickPhoto, onPickBackgroundPhoto, onPickLogo, onOpenBrand, moveLogo, { moveLogo = it })
                    }
                }
            }
        }
    }
}

@Composable
private fun StudioTopRow(doc: PostDoc, onDoc: (PostDoc) -> Unit, onOpenBrand: () -> Unit, onBackHome: () -> Unit, onSaveProject: () -> Unit) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onBackHome) { Icon(Icons.Outlined.ArrowBack, contentDescription = "Back to templates", tint = PostPalette.Text) }
        Text("Editor", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = PostPalette.Text, modifier = Modifier.weight(1f))
        PostSize.values().forEach { size ->
            FilterChip(
                selected = doc.size == size,
                onClick = { onDoc(doc.copy(size = size)) },
                label = { Text(size.label, fontSize = 11.sp) },
                modifier = Modifier.padding(start = 3.dp)
            )
        }
        IconButton(onClick = onSaveProject) { Icon(Icons.Outlined.Save, contentDescription = "Save to My posts", tint = PostPalette.Mint) }
        IconButton(onClick = onOpenBrand) {
            Icon(Icons.Outlined.Palette, contentDescription = "Brand kit", tint = PostPalette.Mint)
        }
    }
}

@Composable
private fun PreviewPane(doc: PostDoc, brand: PostBrand, height: Dp, moveLogo: Boolean, onDoc: (PostDoc) -> Unit) {
    val liveDoc by rememberUpdatedState(doc)
    val liveOnDoc by rememberUpdatedState(onDoc)
    val context = LocalContext.current
    var preview by remember { mutableStateOf<Bitmap?>(null) }
    var overflow by remember { mutableStateOf<List<String>>(emptyList()) }

    LaunchedEffect(doc, brand) {
        delay(120)
        val result = withContext(Dispatchers.Default) { PostRenderer.thumbnail(context, doc, brand, 960) }
        preview = result.bitmap
        overflow = result.overflow
    }

    Box(Modifier.fillMaxWidth().height(height).padding(horizontal = 12.dp, vertical = 4.dp), contentAlignment = Alignment.Center) {
        val bmp = preview
        if (bmp == null) {
            CircularProgressIndicator(color = PostPalette.Mint)
        } else {
            Box(contentAlignment = Alignment.BottomCenter) {
                Image(
                    bitmap = bmp.asImageBitmap(),
                    contentDescription = "Post preview",
                    modifier = Modifier.aspectRatio(bmp.width.toFloat() / bmp.height).clip(RoundedCornerShape(10.dp))
                        .drawWithContent {
                            drawContent()
                            val cx = doc.logoCx
                            val cy = doc.logoCy
                            if (moveLogo && cx != null && cy != null) {
                                drawCircle(
                                    color = Color.White,
                                    radius = 22.dp.toPx(),
                                    center = Offset(cx * size.width, cy * size.height),
                                    style = Stroke(width = 3.dp.toPx())
                                )
                            }
                        }
                        .pointerInput(moveLogo) {
                            if (moveLogo) {
                                detectTapGestures { p ->
                                    liveOnDoc(liveDoc.copy(logoCx = (p.x / size.width).coerceIn(0f, 1f), logoCy = (p.y / size.height).coerceIn(0f, 1f)))
                                }
                            }
                        }
                        .pointerInput(moveLogo) {
                            if (moveLogo) {
                                detectDragGestures(
                                    onDragStart = { p ->
                                        liveOnDoc(liveDoc.copy(logoCx = (p.x / size.width).coerceIn(0f, 1f), logoCy = (p.y / size.height).coerceIn(0f, 1f)))
                                    },
                                    onDrag = { change, _ ->
                                        change.consume()
                                        liveOnDoc(liveDoc.copy(logoCx = (change.position.x / size.width).coerceIn(0f, 1f), logoCy = (change.position.y / size.height).coerceIn(0f, 1f)))
                                    }
                                )
                            }
                        }
                )
                if (overflow.isNotEmpty()) {
                    Text(
                        "Text shrunk to fit: " + overflow.joinToString { PostSlots.label(it) },
                        fontSize = 10.sp,
                        color = PostPalette.Ink,
                        modifier = Modifier.padding(6.dp).background(PostPalette.Gold, RoundedCornerShape(8.dp)).padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun TemplateStrip(doc: PostDoc, brand: PostBrand, onDoc: (PostDoc) -> Unit) {
    val state = rememberLazyListState()
    val index = PostTemplates.indexOfFirst { it.id == doc.templateId }.coerceAtLeast(0)
    LaunchedEffect(doc.templateId) {
        if (state.layoutInfo.visibleItemsInfo.none { it.index == index }) state.animateScrollToItem(index)
    }
    LazyRow(
        state = state,
        contentPadding = PaddingValues(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
    ) {
        items(PostTemplates, key = { it.id }) { spec ->
            TemplateCard(spec, doc.size, brand, doc.accentHex, doc.textHex, spec.id == doc.templateId) { onDoc(doc.withTemplate(spec)) }
        }
    }
}

@Composable
private fun TemplateCard(
    spec: PostTemplateSpec,
    size: PostSize,
    brand: PostBrand,
    accentHex: String?,
    textHex: String?,
    selected: Boolean,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val ratio = size.width.toFloat() / size.height
    val thumbH = 104.dp
    val thumbW = thumbH * ratio
    val thumb by produceState<Bitmap?>(null, spec.id, size, brand, accentHex, textHex) {
        val key = spec.id + "|" + size.name + "|" + brand.hashCode() + "|" + accentHex + "|" + textHex
        val cached = thumbCache.get(key)
        if (cached != null) {
            value = cached
        } else {
            val made = withContext(Dispatchers.Default) {
                val sample = PostDoc(templateId = spec.id, size = size, texts = spec.samples, accentHex = accentHex, textHex = textHex)
                PostRenderer.thumbnail(context, sample, brand, 300).bitmap
            }
            thumbCache.put(key, made)
            value = made
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
                .border(if (selected) 2.dp else 1.dp, if (selected) PostPalette.Mint else PostPalette.Line, shape)
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
        Spacer(Modifier.height(4.dp))
        Text(
            spec.name,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = if (selected) PostPalette.Mint else PostPalette.Text
        )
        Text(spec.family, fontSize = 9.sp, color = PostPalette.Muted, maxLines = 1)
    }
}

/** A two-color button: left half is the pop color, right half the ink. One tap applies both. */
@Composable
private fun PairSwatch(accentHex: String, textHex: String, selected: Boolean, onClick: () -> Unit) {
    val a = remember(accentHex) { runCatching { Color(android.graphics.Color.parseColor(accentHex)) }.getOrDefault(Color.Gray) }
    val t = remember(textHex) { runCatching { Color(android.graphics.Color.parseColor(textHex)) }.getOrDefault(Color.DarkGray) }
    val shape = RoundedCornerShape(12.dp)
    Row(
        Modifier.width(56.dp).height(40.dp).clip(shape)
            .border(if (selected) 3.dp else 1.dp, if (selected) PostPalette.Mint else PostPalette.Line, shape)
            .clickable(onClick = onClick)
    ) {
        Box(Modifier.weight(1f).fillMaxSize().background(a))
        Box(Modifier.weight(1f).fillMaxSize().background(t))
    }
}

@Composable
private fun StudioSection(title: String, subtitle: String? = null, content: @Composable ColumnScope.() -> Unit) {
    var expanded by rememberSaveable(title) { mutableStateOf(false) }
    Card(colors = CardDefaults.cardColors(containerColor = PostPalette.Surface), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                Modifier.fillMaxWidth().clickable { expanded = !expanded },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(title, fontWeight = FontWeight.Bold, color = PostPalette.Text)
                    if (subtitle != null) PostSmallLabel(subtitle)
                }
                Text(if (expanded) "−" else "+", color = PostPalette.Mint, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }
            if (expanded) content()
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ColumnScope.StudioDetails(
    doc: PostDoc,
    onDoc: (PostDoc) -> Unit,
    brand: PostBrand,
    onBrand: (PostBrand) -> Unit,
    onPickPhoto: () -> Unit,
    onPickBackgroundPhoto: () -> Unit,
    onPickLogo: () -> Unit,
    onOpenBrand: () -> Unit,
    moveLogo: Boolean,
    onMoveLogo: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val spec = postTemplate(doc.templateId)
    val layout = remember(doc.templateId, doc.size) { spec.layout(doc.size) }
    val slots = remember(doc.templateId, doc.size) { layout.slots.filter { it.literal == null }.map { it.slot }.distinct() }

    // ---- Words ----
    StudioSection("Words", spec.name + ". Your words stay when you switch template.") {
        slots.forEach { slot ->
            OutlinedTextField(
                value = doc.texts[slot].orEmpty(),
                onValueChange = { onDoc(doc.withText(slot, it)) },
                label = { Text(PostSlots.label(slot)) },
                minLines = when (slot) {
                    PostSlots.HEADLINE, PostSlots.BODY -> 2
                    PostSlots.LIST -> 4
                    else -> 1
                },
                maxLines = when (slot) {
                    PostSlots.LIST -> 8
                    PostSlots.EYEBROW, PostSlots.NUMBER, PostSlots.BUTTON -> 2
                    else -> 5
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
        if (layout.social != null) PostSmallLabel("The big icons on this poster come from Social icons below.")
    }

    // ---- Fonts ----
    FontsSection(doc, onDoc)

    // ---- Look ----
    StudioSection("Look") {
        RichRow(doc, onDoc)
        PostSmallLabel("Text size")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Small" to 0.9f, "Medium" to 1f, "Large" to 1.12f).forEach { (name, scale) ->
                FilterChip(
                    selected = abs(doc.textScale - scale) < 0.01f,
                    onClick = { onDoc(doc.copy(textScale = scale)) },
                    label = { Text(name) }
                )
            }
        }
        PostSmallLabel("Colors: tap a pair. The first half colors bands and rings, the second half colors the big words. The first button is your brand.")
        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            PairSwatch(brand.primaryHex, brand.darkHex, doc.accentHex == null && doc.textHex == null) {
                onDoc(doc.copy(accentHex = null, textHex = null))
            }
            ColorPairs.forEach { (accent, ink) ->
                PairSwatch(accent, ink, doc.accentHex.equals(accent, ignoreCase = true) && doc.textHex.equals(ink, ignoreCase = true)) {
                    onDoc(doc.copy(accentHex = accent, textHex = ink))
                }
            }
        }
        PostColorPicker("Main color", doc.accentHex, brand, { onDoc(doc.copy(accentHex = it)) })
        PostColorPicker("Text color", doc.textHex, brand, { onDoc(doc.copy(textHex = it)) })
        if (doc.textHex != null) {
            TextButton(onClick = { onDoc(doc.copy(textHex = null)) }) { Text("Use the brand text color") }
        }

        PostSmallLabel("Background")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(BgMode.TEMPLATE to "Template", BgMode.SOLID to "Solid", BgMode.GRADIENT to "Gradient").forEach { (mode, name) ->
                FilterChip(selected = doc.bgMode == mode, onClick = { onDoc(doc.copy(bgMode = mode)) }, label = { Text(name) })
            }
        }
        if (doc.bgMode != BgMode.TEMPLATE) {
            PostColorPicker(
                if (doc.bgMode == BgMode.SOLID) "Background color" else "Gradient from",
                doc.bgStartHex,
                brand,
                { hex -> if (hex != null) onDoc(doc.copy(bgStartHex = hex)) },
                includeBrandChoice = false
            )
        }
        if (doc.bgMode == BgMode.GRADIENT) {
            PostColorPicker("Gradient to", doc.bgEndHex, brand, { hex -> if (hex != null) onDoc(doc.copy(bgEndHex = hex)) }, includeBrandChoice = false)
            PostSmallLabel("Gradient direction")
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(0 to "↑", 45 to "↗", 90 to "→", 135 to "↘", 180 to "↓", 225 to "↙", 270 to "←", 315 to "↖").forEach { (angle, glyph) ->
                    FilterChip(
                        selected = kotlin.math.abs(doc.bgAngle - angle) < 18,
                        onClick = { onDoc(doc.copy(bgAngle = angle)) },
                        label = { Text(glyph, fontSize = 18.sp) }
                    )
                }
            }
        }
        if (doc.bgMode != BgMode.TEMPLATE) {
            PostSmallLabel("Light templates use dark text and dark ones use light text. Pick colors that keep it readable.")
        }
    }

    // ---- Full-canvas background photo ----
    StudioSection("Background image", "Add a photo behind the template design. Text, shapes and template details stay editable above it.") {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedButton(onClick = onPickBackgroundPhoto) {
                Icon(Icons.Outlined.PhotoLibrary, contentDescription = null)
                Spacer(Modifier.width(6.dp))
                Text(if (doc.backgroundPhotoUri != null) "Replace background" else "Add background image")
            }
            if (doc.backgroundPhotoUri != null) {
                TextButton(onClick = { onDoc(doc.copy(backgroundPhotoUri = null, backgroundPhotoOpacity = 1f)) }) { Text("Remove") }
            }
        }
        if (doc.backgroundPhotoUri != null) {
            PostLabeledSlider("Background image opacity", doc.backgroundPhotoOpacity, 0f..1f,
                "${(doc.backgroundPhotoOpacity * 100).toInt()}%") {
                onDoc(doc.copy(backgroundPhotoOpacity = it))
            }
            PostSmallLabel("100% = fully visible · 0% = hidden, revealing the color or gradient beneath.")
        }
    }

    // ---- Photo ----
    val photoArea = layout.photo
    StudioSection(
        "Photo",
        if (photoArea != null) "This template shows your photo."
        else "This template has no photo area. Try Quote on photo, Subject cover or Photo announcement."
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedButton(onClick = onPickPhoto) {
                Icon(Icons.Outlined.PhotoLibrary, contentDescription = null)
                Spacer(Modifier.width(6.dp))
                Text(if (doc.photoUri != null) "Replace photo" else "Pick photo")
            }
            if (doc.photoUri != null) {
                TextButton(onClick = { onDoc(doc.copy(photoUri = null)) }) { Text("Remove") }
            }
        }
        if (doc.photoUri != null && photoArea != null) {
            PostLabeledSlider("Zoom", doc.photoZoom, 1f..3f, "x" + String.format("%.1f", doc.photoZoom)) { onDoc(doc.copy(photoZoom = it)) }
            PostLabeledSlider("Move left or right", doc.photoFocusX, 0f..1f, "") { onDoc(doc.copy(photoFocusX = it)) }
            PostLabeledSlider("Move up or down", doc.photoFocusY, 0f..1f, "") { onDoc(doc.copy(photoFocusY = it)) }
            val dim = if (doc.photoDim >= 0) doc.photoDim else photoArea.dim
            PostLabeledSlider("Darkness", dim.toFloat(), 0f..230f, dim.toString()) { onDoc(doc.copy(photoDim = it.toInt())) }
            if (doc.photoDim >= 0) {
                TextButton(onClick = { onDoc(doc.copy(photoDim = -1)) }) { Text("Use the template darkness") }
            }
        }
    }

    // ---- Logo ----
    StudioSection("Logo and footer") {
        PostSwitchRow("Show logo", doc.showLogo) { onDoc(doc.copy(showLogo = it)) }
        PostSwitchRow("Show footer icons", doc.showFooter) { onDoc(doc.copy(showFooter = it)) }
        PostSmallLabel("Logo corner and size")
        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            LogoCorner.values().forEach { corner ->
                FilterChip(
                    selected = brand.logoCorner == corner,
                    onClick = { onBrand(brand.copy(logoCorner = corner)) },
                    label = { Text(corner.name.lowercase().replaceFirstChar { it.uppercase() }) }
                )
            }
            listOf("S" to 0.8f, "M" to 1f, "L" to 1.25f).forEach { (name, scale) ->
                FilterChip(
                    selected = abs(brand.logoScale - scale) < 0.01f,
                    onClick = { onBrand(brand.copy(logoScale = scale)) },
                    label = { Text(name) }
                )
            }
        }
        OutlinedButton(onClick = onPickLogo, modifier = Modifier.fillMaxWidth()) {
            Text(if (brand.logoPath != null) "Replace logo" else "Pick logo from phone")
        }
        LogoMoveRow(doc, onDoc, moveLogo, onMoveLogo)
        TextButton(onClick = onOpenBrand) { Text("Brand colors and logo settings") }
    }

    // ---- Social icons ----
    StudioSection("Social icons", "Used in the footer of every post and big on the Social posters.") {
        FooterEditor(brand, onBrand)
    }

    // ---- Export ----
    var chosen by remember { mutableStateOf(setOf(doc.size)) }
    var exportOpen by remember { mutableStateOf(false) }
    var png by remember { mutableStateOf(true) }
    var busy by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    fun runExport(share: Boolean) {
        val sizes = PostSize.values().filter { it in chosen }.ifEmpty { listOf(doc.size) }
        busy = true
        status = null
        scope.launch {
            val outcome = runCatching { withContext(Dispatchers.Default) { PostExport.save(context, doc, brand, sizes, png) } }
            busy = false
            val saved = outcome.getOrNull()
            if (saved == null) {
                status = "Export failed: " + (outcome.exceptionOrNull()?.localizedMessage ?: "unknown error")
            } else {
                val warn = saved.overflow.entries.joinToString("\n") { (s, names) ->
                    s.label + ": text had to shrink in " + names.joinToString { PostSlots.label(it) }
                }
                status = "Saved " + saved.uris.size + " file(s) to Pictures/Wisdom Tower Post" + (if (warn.isEmpty()) "" else "\n" + warn)
                if (share) {
                    val intent = Intent(Intent.ACTION_SEND_MULTIPLE)
                    intent.type = "image/*"
                    intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(saved.uris))
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    context.startActivity(Intent.createChooser(intent, "Share post"))
                }
            }
        }
    }

    OutlinedButton(onClick = { exportOpen = true }, modifier = Modifier.fillMaxWidth()) {
        Icon(Icons.Outlined.Download, contentDescription = null)
        Spacer(Modifier.width(8.dp))
        Text("Export post")
    }

    if (exportOpen) {
        ModalBottomSheet(onDismissRequest = { exportOpen = false }, containerColor = PostPalette.Surface) {
            Column(
                Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(bottom = 28.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text("Export your post", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = PostPalette.Text)
                PostSmallLabel("Choose one size or export all three. Each format is laid out independently.")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PostSize.values().forEach { size ->
                        FilterChip(
                            selected = size in chosen,
                            onClick = { chosen = if (size in chosen) chosen - size else chosen + size },
                            label = { Text(size.label) }
                        )
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = png, onClick = { png = true }, label = { Text("PNG") })
                    FilterChip(selected = !png, onClick = { png = false }, label = { Text("JPG") })
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { runExport(false) }, enabled = !busy, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Outlined.Download, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text(if (busy) "Saving..." else "Save")
                    }
                    OutlinedButton(onClick = { runExport(true) }, enabled = !busy, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Outlined.Share, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text("Save and share")
                    }
                }
                status?.let { Text(it, fontSize = 12.sp, color = PostPalette.Muted) }
                PostSmallLabel("Check the preview for any text-fit warning before exporting.")
            }
        }
    }
    Spacer(Modifier.height(24.dp))
}
