package com.wisdomtower.post

import android.content.Intent
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** Two-speed preview retained from Phase 6. */
private const val PREVIEW_QUICK_WIDTH = 540
private const val PREVIEW_SHARP_WIDTH = 960
private const val PREVIEW_SETTLE_MS = 150L
private const val RAPID_CHANGE_NS = 350_000_000L

/**
 * One-screen studio: title and size controls, pinned preview, pinned template strip,
 * then a single vertically scrolling stack of all editing sections.
 */
@Composable
fun PostStudioScreen(
    doc: PostDoc,
    onDoc: (PostDoc) -> Unit,
    brand: PostBrand,
    library: PostLibrary,
    onBrand: (PostBrand) -> Unit,
    onPickPhoto: () -> Unit,
    onPickBackgroundPhoto: () -> Unit,
    onPickLogo: () -> Unit,
    onOpenBrand: () -> Unit,
    edited: Boolean = false,
    onReset: () -> Unit = {},
    onBringWords: (() -> Unit)? = null,
    bookmarked: Boolean = false,
    onToggleBookmark: () -> Unit = {},
    onOpenLibrary: () -> Unit = {}
) {
    var moveLogoOn by remember { mutableStateOf(false) }
    var footerItem by remember { mutableStateOf(0) }
    var selectedIcon by remember { mutableStateOf(0) }
    var showSave by remember { mutableStateOf(false) }

    val spec = postTemplate(doc.templateId)
    val layout = remember(doc.templateId, doc.size) { spec.layout(doc.size) }
    val slots = remember(doc.templateId, doc.size) {
        layout.slots.filter { it.literal == null }.map { it.slot }.distinct()
    }
    val photoArea = layout.photo

    PostTheme {
        Scaffold(containerColor = PostPalette.Ink) { padding ->
            BoxWithConstraints(Modifier.fillMaxSize().padding(padding).imePadding()) {
                val previewHeight = (maxHeight * 0.39f).coerceAtMost(350.dp)
                Column(Modifier.fillMaxSize()) {
                    TitleRow(doc, edited, bookmarked, onToggleBookmark, onOpenLibrary, onOpenBrand)
                    SizeRow(doc, onDoc, edited, onReset) { showSave = true }
                    PreviewPane(
                        doc, brand, moveLogoOn, onDoc,
                        iconEdit = doc.icons.isNotEmpty(),
                        selectedIcon = selectedIcon,
                        modifier = Modifier.fillMaxWidth().height(previewHeight)
                    )
                    TemplateStrip(doc, brand, library, onDoc)
                    Column(
                        Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        SettingsSection("Words", "Edit every text field directly.") {
                            slots.forEach { slot ->
                                EditorOptions(
                                    EditorTab.WORDS, slot, doc, onDoc, brand, onBrand,
                                    photoArea != null, onPickPhoto, onPickBackgroundPhoto, onPickLogo,
                                    moveLogoOn, { moveLogoOn = it }, footerItem, { footerItem = it }
                                )
                            }
                            if (onBringWords != null) {
                                OutlinedButton(onClick = onBringWords, modifier = Modifier.fillMaxWidth()) {
                                    Text("Bring my words from the previous template")
                                }
                            }
                        }

                        SettingsSection("Colors", "Color pairs, accent and text colors.") {
                            listOf("pairs", "main", "text").forEach { group ->
                                EditorOptions(EditorTab.LOOK, group, doc, onDoc, brand, onBrand,
                                    photoArea != null, onPickPhoto, onPickBackgroundPhoto, onPickLogo,
                                    moveLogoOn, { moveLogoOn = it }, footerItem, { footerItem = it })
                            }
                        }

                        SettingsSection("Background", "Choose the template background, a solid color or a gradient.") {
                            listOf("bg", "bgfrom", "bgto", "bgdir").forEach { group ->
                                if (group != "bgfrom" || doc.bgMode == BgMode.SOLID || doc.bgMode == BgMode.GRADIENT) {
                                    if (group != "bgto" && group != "bgdir" || doc.bgMode == BgMode.GRADIENT) {
                                        EditorOptions(EditorTab.LOOK, group, doc, onDoc, brand, onBrand,
                                            photoArea != null, onPickPhoto, onPickBackgroundPhoto, onPickLogo,
                                            moveLogoOn, { moveLogoOn = it }, footerItem, { footerItem = it })
                                    }
                                }
                            }
                        }

                        SettingsSection("Photo", "Template photo and full-canvas background image.") {
                            val groups = editorGroups(EditorTab.PHOTO, doc, slots, photoArea != null, false)
                            groups.forEach { group ->
                                val slider = editorSlider(EditorTab.PHOTO, group.key, doc, photoArea?.dim, onDoc)
                                if (slider != null) InlineEditorSlider(slider)
                                EditorOptions(EditorTab.PHOTO, group.key, doc, onDoc, brand, onBrand,
                                    photoArea != null, onPickPhoto, onPickBackgroundPhoto, onPickLogo,
                                    moveLogoOn, { moveLogoOn = it }, footerItem, { footerItem = it })
                            }
                        }

                        SettingsSection("Fonts", "English and Amharic font libraries.") {
                            EditorOptions(EditorTab.FONT, "en", doc, onDoc, brand, onBrand,
                                photoArea != null, onPickPhoto, onPickBackgroundPhoto, onPickLogo,
                                moveLogoOn, { moveLogoOn = it }, footerItem, { footerItem = it })
                            EditorOptions(EditorTab.FONT, "am", doc, onDoc, brand, onBrand,
                                photoArea != null, onPickPhoto, onPickBackgroundPhoto, onPickLogo,
                                moveLogoOn, { moveLogoOn = it }, footerItem, { footerItem = it })
                        }

                        SettingsSection("Logo", "Control the logo on this post.") {
                            editorGroups(EditorTab.LOGO, doc, slots, photoArea != null, false).forEach { group ->
                                if (group.key == "size") {
                                    InlineEditorSlider(EditorSlider("Logo size", doc.logoSize, 0.4f..3f,
                                        "${(doc.logoSize * 100).toInt()}%") { onDoc(doc.copy(logoSize = it)) })
                                } else EditorOptions(EditorTab.LOGO, group.key, doc, onDoc, brand, onBrand,
                                    photoArea != null, onPickPhoto, onPickBackgroundPhoto, onPickLogo,
                                    moveLogoOn, { moveLogoOn = it }, footerItem, { footerItem = it })
                            }
                        }

                        SettingsSection("Icons", "Add graphics, select one, then drag or pinch it on the preview.") {
                            editorGroups(EditorTab.ICONS, doc, slots, photoArea != null, false).forEach { group ->
                                if (group.key == "size") {
                                    val slider = iconSizeSlider(doc, selectedIcon, onDoc)
                                    if (slider != null) InlineEditorSlider(slider)
                                }
                                IconOptions(group.key, doc, onDoc, selectedIcon) { selectedIcon = it }
                            }
                        }

                        SettingsSection("Footer", "Social icons shown on the post.") {
                            editorGroups(EditorTab.FOOTER, doc, slots, photoArea != null, false).forEach { group ->
                                if (group.key == "size") {
                                    InlineEditorSlider(EditorSlider("Footer icon size", brand.iconScale, 0.5f..1.8f,
                                        "${(brand.iconScale * 100).toInt()}%") { onBrand(brand.copy(iconScale = it)) })
                                } else EditorOptions(EditorTab.FOOTER, group.key, doc, onDoc, brand, onBrand,
                                    photoArea != null, onPickPhoto, onPickBackgroundPhoto, onPickLogo,
                                    moveLogoOn, { moveLogoOn = it }, footerItem, { footerItem = it })
                            }
                        }

                        SettingsSection("Decoration and text size", "Keep the template clean or add richer decoration.") {
                            EditorOptions(EditorTab.LOOK, "style", doc, onDoc, brand, onBrand,
                                photoArea != null, onPickPhoto, onPickBackgroundPhoto, onPickLogo,
                                moveLogoOn, { moveLogoOn = it }, footerItem, { footerItem = it })
                            InlineEditorSlider(EditorSlider("Text size", doc.textScale, 0.7f..1.5f,
                                "${(doc.textScale * 100).toInt()}%") { onDoc(doc.copy(textScale = it)) })
                        }
                    }
                }
            }
        }
        if (showSave) SaveDialog(doc, brand) { showSave = false }
    }
}

@Composable
private fun SettingsSection(title: String, subtitle: String? = null, content: @Composable ColumnScope.() -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = PostPalette.Surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
            Text(title, color = PostPalette.Text, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            if (subtitle != null) Text(subtitle, color = PostPalette.Muted, fontSize = 11.sp)
            content(this)
        }
    }
}

@Composable
private fun InlineEditorSlider(slider: EditorSlider) {
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(slider.label, color = PostPalette.Text, fontSize = 12.sp, modifier = Modifier.weight(1f))
            Text(slider.display, color = PostPalette.Muted, fontSize = 12.sp)
        }
        androidx.compose.material3.Slider(
            value = slider.value.coerceIn(slider.range.start, slider.range.endInclusive),
            onValueChange = slider.onChange,
            valueRange = slider.range
        )
    }
}

@Composable
private fun TitleRow(
    doc: PostDoc,
    edited: Boolean,
    bookmarked: Boolean,
    onToggleBookmark: () -> Unit,
    onOpenLibrary: () -> Unit,
    onOpenBrand: () -> Unit
) {
    Row(Modifier.fillMaxWidth().padding(start = 12.dp, end = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(
            (if (edited) "\u25CF " else "") + postTemplate(doc.templateId).name,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = PostPalette.Text,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = onToggleBookmark) {
            Icon(
                if (bookmarked) Icons.Outlined.Bookmark else Icons.Outlined.BookmarkBorder,
                contentDescription = if (bookmarked) "Remove from Saved" else "Save this template",
                tint = if (bookmarked) PostPalette.Gold else PostPalette.Muted
            )
        }
        IconButton(onClick = onOpenLibrary) {
            Icon(Icons.Outlined.GridView, contentDescription = "Library", tint = PostPalette.Mint)
        }
        IconButton(onClick = onOpenBrand) {
            Icon(Icons.Outlined.Palette, contentDescription = "Brand and social icons", tint = PostPalette.Mint)
        }
    }
}

@Composable
private fun SizeRow(doc: PostDoc, onDoc: (PostDoc) -> Unit, edited: Boolean, onReset: () -> Unit, onSave: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PostSize.values().forEach { size ->
            FilterChip(
                selected = doc.size == size,
                onClick = { onDoc(doc.copy(size = size)) },
                label = { Text(size.label, fontSize = 12.sp) }
            )
        }
        Spacer(Modifier.weight(1f))
        if (edited) {
            IconButton(onClick = onReset) {
                Icon(Icons.Outlined.Refresh, contentDescription = "Reset this template", tint = PostPalette.Gold)
            }
        }
        Button(onClick = onSave, contentPadding = PaddingValues(horizontal = 16.dp)) {
            Icon(Icons.Outlined.Download, contentDescription = null)
            Spacer(Modifier.width(6.dp))
            Text("Save")
        }
    }
}

@Composable
private fun PreviewPane(
    doc: PostDoc,
    brand: PostBrand,
    moveLogo: Boolean,
    onDoc: (PostDoc) -> Unit,
    iconEdit: Boolean,
    selectedIcon: Int,
    modifier: Modifier
) {
    val liveDoc by rememberUpdatedState(doc)
    val liveOnDoc by rememberUpdatedState(onDoc)
    val liveSelected by rememberUpdatedState(selectedIcon)
    val context = LocalContext.current
    var preview by remember { mutableStateOf<Bitmap?>(null) }
    var overflow by remember { mutableStateOf<List<String>>(emptyList()) }
    val lastChange = remember { longArrayOf(0L) }

    // Two speeds. Changes that follow each other fast (dragging an icon or the logo, moving a slider) draw a small version at once,
    // so the preview keeps up with the finger. About 150 ms after the last change the sharp version replaces it.
    // A single change (a tap on a chip) goes straight to the sharp version. Every render checks isActive as it draws, so a newer
    // change stops the one in progress instead of letting it finish and queue up behind. Export never uses this path: it renders at full size.
    LaunchedEffect(doc, brand) {
        val now = System.nanoTime()
        val rapid = now - lastChange[0] < RAPID_CHANGE_NS
        lastChange[0] = now
        if (rapid || preview == null) {
            val quick = withContext(Dispatchers.Default) {
                PostRenderer.preview(context, doc, brand, PREVIEW_QUICK_WIDTH) { !isActive }
            }
            preview = quick.bitmap
            overflow = quick.overflow
            delay(PREVIEW_SETTLE_MS)
        }
        val sharp = withContext(Dispatchers.Default) {
            PostRenderer.preview(context, doc, brand, PREVIEW_SHARP_WIDTH) { !isActive }
        }
        preview = sharp.bitmap
        overflow = sharp.overflow
    }

    Box(modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp), contentAlignment = Alignment.Center) {
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
                            if (iconEdit && selectedIcon in doc.icons.indices) {
                                val ic = doc.icons[selectedIcon]
                                drawCircle(
                                    color = Color.White,
                                    radius = ic.size * minOf(size.width, size.height) / 2f + 4.dp.toPx(),
                                    center = Offset(ic.cx * size.width, ic.cy * size.height),
                                    style = Stroke(width = 2.dp.toPx())
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
                        .pointerInput(iconEdit) {
                            if (iconEdit) {
                                // Drag moves the selected icon, pinch resizes it. Gestures arrive faster than the screen redraws,
                                // so for a few milliseconds the last post we sent is newer than liveDoc: build on that one.
                                var last: PostDoc? = null
                                var lastAt = 0L
                                detectTransformGestures { _, pan, zoom, _ ->
                                    val now = System.nanoTime()
                                    val base = if (last != null && now - lastAt < 150_000_000L) last!! else liveDoc
                                    val i = liveSelected
                                    if (i in base.icons.indices) {
                                        val ic = base.icons[i]
                                        val moved = ic.copy(
                                            cx = (ic.cx + pan.x / size.width).coerceIn(0f, 1f),
                                            cy = (ic.cy + pan.y / size.height).coerceIn(0f, 1f),
                                            size = (ic.size * zoom).coerceIn(0.04f, 0.9f)
                                        )
                                        val next = base.copy(icons = base.icons.mapIndexed { n, old -> if (n == i) moved else old })
                                        last = next
                                        lastAt = now
                                        liveOnDoc(next)
                                    }
                                }
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

/** The Save sheet: a row of sizes (tick one or all three), a row of formats, then Save and Save and share. */
@Composable
private fun SaveDialog(doc: PostDoc, brand: PostBrand, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var chosen by remember { mutableStateOf(setOf(doc.size)) }
    var png by remember { mutableStateOf(true) }
    var busy by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf<String?>(null) }

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

    Dialog(onDismissRequest = { if (!busy) onDismiss() }) {
        Card(colors = CardDefaults.cardColors(containerColor = PostPalette.Surface), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Save", fontWeight = FontWeight.Bold, color = PostPalette.Text)
                PostSmallLabel("Tick one size or all three. Every size is laid out again, not stretched.")
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
                TextButton(onClick = onDismiss, enabled = !busy, modifier = Modifier.align(Alignment.End)) { Text("Close") }
            }
        }
    }
}
