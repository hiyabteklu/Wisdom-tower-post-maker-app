package com.wisdomtower.post

import android.content.Intent
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateMapOf
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** Fixed height of the bottom options area, so switching tabs never moves the preview. */
private val OptionsAreaHeight = 124.dp

/**
 * The editor (UI_UX_PLAN.md section 4.2). Nothing here scrolls vertically.
 * From the top: title row, size row (with Reset and Save), the live preview (takes all spare height and shrinks when the keyboard opens),
 * one shared slider, the category tabs, and one options row. Every category is one horizontal row.
 * edited: this template differs from its factory look (shows a dot and the Reset button).
 * onBringWords: copies the words of the last template you edited into this one; null when there is nothing to bring.
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
    onOpenOldStudio: () -> Unit,
    edited: Boolean = false,
    onReset: () -> Unit = {},
    onBringWords: (() -> Unit)? = null
) {
    var tab by remember { mutableStateOf(EditorTab.WORDS) }
    val groupChoice = remember { mutableStateMapOf<EditorTab, String>() }
    var moveLogoOn by remember { mutableStateOf(false) }
    var footerItem by remember { mutableStateOf(0) }
    var showSave by remember { mutableStateOf(false) }

    val spec = postTemplate(doc.templateId)
    val layout = remember(doc.templateId, doc.size) { spec.layout(doc.size) }
    val slots = remember(doc.templateId, doc.size) { layout.slots.filter { it.literal == null }.map { it.slot }.distinct() }
    val photoArea = layout.photo
    val groups = editorGroups(tab, doc, slots, photoArea != null, onBringWords != null)
    val group = groupChoice[tab]?.takeIf { key -> key != "bring" && groups.any { it.key == key } } ?: groups.first().key
    val slider = editorSlider(tab, group, doc, photoArea?.dim, onDoc)
    val moving = moveLogoOn && tab == EditorTab.LOGO

    PostTheme {
        Scaffold(containerColor = PostPalette.Ink) { padding ->
            Column(Modifier.fillMaxSize().padding(padding).imePadding()) {
                TitleRow(doc, edited, onOpenBrand, onOpenOldStudio)
                SizeRow(doc, onDoc, edited, onReset) { showSave = true }
                PreviewPane(doc, brand, moving, onDoc, Modifier.weight(1f))
                SliderRow(slider)
                Row(
                    Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    EditorTab.values().forEach { t ->
                        FilterChip(selected = t == tab, onClick = { tab = t }, label = { Text(t.title, fontWeight = FontWeight.SemiBold) })
                    }
                }
                Column(Modifier.fillMaxWidth().height(OptionsAreaHeight)) {
                    if (groups.size > 1) {
                        Row(
                            Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            groups.forEach { g ->
                                FilterChip(
                                    selected = g.key == group,
                                    onClick = {
                                        if (g.key == "bring") onBringWords?.invoke() else groupChoice[tab] = g.key
                                    },
                                    label = { Text(g.label, fontSize = 12.sp) }
                                )
                            }
                        }
                    }
                    Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
                        key(tab, group) {
                            EditorOptions(
                                tab, group, doc, onDoc, brand, onBrand,
                                hasPhotoArea = photoArea != null,
                                onPickPhoto = onPickPhoto,
                                onPickBackgroundPhoto = onPickBackgroundPhoto,
                                onPickLogo = onPickLogo,
                                moveLogo = moveLogoOn,
                                onMoveLogo = { moveLogoOn = it },
                                footerItem = footerItem,
                                onFooterItem = { footerItem = it }
                            )
                        }
                    }
                }
            }
        }
        if (showSave) SaveDialog(doc, brand) { showSave = false }
    }
}

@Composable
private fun TitleRow(doc: PostDoc, edited: Boolean, onOpenBrand: () -> Unit, onOpenOldStudio: () -> Unit) {
    var menu by remember { mutableStateOf(false) }
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
        IconButton(onClick = onOpenBrand) {
            Icon(Icons.Outlined.Palette, contentDescription = "Brand and social icons", tint = PostPalette.Mint)
        }
        Box {
            IconButton(onClick = { menu = true }) {
                Icon(Icons.Outlined.MoreVert, contentDescription = "More", tint = PostPalette.Muted)
            }
            DropdownMenu(expanded = menu, onDismissRequest = { menu = false }) {
                DropdownMenuItem(text = { Text("Old layer studio") }, onClick = { menu = false; onOpenOldStudio() })
            }
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

/** Always takes the same height, so the preview does not jump when a control needs the slider. */
@Composable
private fun SliderRow(slider: EditorSlider?) {
    Row(Modifier.fillMaxWidth().height(44.dp).padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        if (slider != null) {
            Text(slider.label, fontSize = 12.sp, color = PostPalette.Muted, modifier = Modifier.width(76.dp))
            Slider(
                value = slider.value.coerceIn(slider.range.start, slider.range.endInclusive),
                onValueChange = slider.onChange,
                valueRange = slider.range,
                modifier = Modifier.weight(1f)
            )
            Text(slider.display, fontSize = 12.sp, color = PostPalette.Muted, textAlign = TextAlign.End, modifier = Modifier.width(48.dp))
        }
    }
}

@Composable
private fun PreviewPane(doc: PostDoc, brand: PostBrand, moveLogo: Boolean, onDoc: (PostDoc) -> Unit, modifier: Modifier) {
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
