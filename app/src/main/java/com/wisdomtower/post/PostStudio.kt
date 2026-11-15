package com.wisdomtower.post

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset as ComposeOffset
import androidx.compose.ui.geometry.Size as ComposeSize
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Main Studio screen with interactive canvas preview, Floating HUD (Section 3.3), and tabbed controls.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostStudioScreen(
    doc: PostDoc,
    onDoc: (PostDoc) -> Unit,
    brand: PostBrand,
    library: PostLibrary = PostLibrary(),
    onBrand: (PostBrand) -> Unit = {},
    onPickPhoto: () -> Unit = {},
    onPickBackgroundPhoto: () -> Unit = {},
    onPickLogo: () -> Unit = {},
    onOpenBrand: () -> Unit = {},
    edited: Boolean = false,
    onReset: () -> Unit = {},
    onBringWords: (() -> Unit)? = null,
    bookmarked: Boolean = false,
    onToggleBookmark: () -> Unit = {},
    onOpenLibrary: () -> Unit = {},
    onOpenBatch: () -> Unit = {},
    onOpenInbox: () -> Unit = {},
    embedded: Boolean = false,
    headerTitle: String? = null,
    onHeaderApplyAll: (() -> Unit)? = null,
    onHeaderDone: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var activeTab by rememberSaveable { mutableStateOf("template") }
    var selectedSlot by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedIcon by rememberSaveable { mutableStateOf(-1) }
    var moveLogo by rememberSaveable { mutableStateOf(false) }
    var moveOn by rememberSaveable { mutableStateOf(true) }
    var moreSheetOpen by rememberSaveable { mutableStateOf(false) }
    var exportSheetOpen by rememberSaveable { mutableStateOf(false) }
    var allHudTarget by rememberSaveable { mutableStateOf("size") }

    val spec = postTemplate(doc.templateId).withLookIndex(doc.lookIndex)
    val layout = spec.layout(doc.size)
    if (selectedSlot != null && layout.slots.none { it.slot == selectedSlot }) {
        selectedSlot = null
    }

    val renderResult by produceState<PostRenderer.Result?>(null, doc, brand) {
        value = withContext(Dispatchers.Default) {
            PostRenderer.preview(context, doc, brand) { false }
        }
    }

    val latestDoc by rememberUpdatedState(doc)
    val latestOnDoc by rememberUpdatedState(onDoc)
    val latestSelectedSlot by rememberUpdatedState(selectedSlot)
    val latestSelectedIcon by rememberUpdatedState(selectedIcon)
    val latestMoveLogo by rememberUpdatedState(moveLogo)
    val latestMoveOn by rememberUpdatedState(moveOn)

    var previewPxSize by remember { mutableStateOf(IntSize.Zero) }

    Column(
        Modifier
            .fillMaxSize()
            .background(PostPalette.Ink)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Top header bar
        StudioTopBar(
            doc = doc,
            spec = spec,
            edited = edited,
            bookmarked = bookmarked,
            embedded = embedded,
            headerTitle = headerTitle,
            onToggleBookmark = onToggleBookmark,
            onReset = onReset,
            onOpenLibrary = onOpenLibrary,
            onOpenBatch = onOpenBatch,
            onOpenInbox = onOpenInbox,
            onOpenBrand = onOpenBrand,
            onHeaderApplyAll = onHeaderApplyAll,
            onHeaderDone = onHeaderDone,
            onOpenExport = { exportSheetOpen = true }
        )

        // Interactive Preview Canvas
        val aspect = doc.size.width.toFloat() / doc.size.height.toFloat()
        Box(
            Modifier
                .fillMaxWidth()
                .heightIn(max = 430.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                Modifier
                    .aspectRatio(aspect)
                    .clip(RoundedCornerShape(14.dp))
                    .background(PostPalette.Surface)
                    .border(1.dp, PostPalette.Line, RoundedCornerShape(14.dp))
                    .onSizeChanged { previewPxSize = it }
                    .pointerInput(doc.templateId, doc.size, doc.lookIndex) {
                        detectTapGestures { tapOffset ->
                            val w = previewPxSize.width.toFloat().coerceAtLeast(1f)
                            val h = previewPxSize.height.toFloat().coerceAtLeast(1f)
                            val normX = (tapOffset.x / w).coerceIn(0f, 1f)
                            val normY = (tapOffset.y / h).coerceIn(0f, 1f)
                            val curDoc = latestDoc
                            if (latestMoveLogo && curDoc.showLogo) {
                                latestOnDoc(curDoc.copy(logoCx = normX, logoCy = normY))
                                return@detectTapGestures
                            }
                            val hitIconIdx = PostEngine.hitIcon(curDoc, normX, normY)
                            if (hitIconIdx != null) {
                                selectedIcon = hitIconIdx
                                selectedSlot = null
                                return@detectTapGestures
                            }
                            val curLayout = postTemplate(curDoc.templateId).withLookIndex(curDoc.lookIndex).layout(curDoc.size)
                            val hit = PostEngine.hitSlot(curLayout, curDoc, normX, normY)
                            selectedSlot = hit
                            if (hit != null) selectedIcon = -1
                        }
                    }
                    .pointerInput(doc.templateId, doc.size, doc.lookIndex) {
                        detectTransformGestures { centroid, pan, zoom, _ ->
                            val w = previewPxSize.width.toFloat().coerceAtLeast(1f)
                            val h = previewPxSize.height.toFloat().coerceAtLeast(1f)
                            val dx = pan.x / w
                            val dy = pan.y / h
                            val curDoc = latestDoc
                            val selSlot = latestSelectedSlot
                            val selIcon = latestSelectedIcon

                            // 1. Move logo mode
                            if (latestMoveLogo && curDoc.showLogo) {
                                val cx = ((curDoc.logoCx ?: 0.15f) + dx).coerceIn(0.04f, 0.96f)
                                val cy = ((curDoc.logoCy ?: 0.08f) + dy).coerceIn(0.04f, 0.96f)
                                val nextScale = if (zoom != 1f) (curDoc.logoSize * zoom).coerceIn(0.4f, 3f) else curDoc.logoSize
                                latestOnDoc(curDoc.copy(logoCx = cx, logoCy = cy, logoSize = nextScale))
                                return@detectTransformGestures
                            }

                            // 2. Selected icon move/pinch
                            if (selIcon in curDoc.icons.indices) {
                                val icon = curDoc.icons[selIcon]
                                val nextIcon = icon.copy(
                                    cx = (icon.cx + dx).coerceIn(0.02f, 0.98f),
                                    cy = (icon.cy + dy).coerceIn(0.02f, 0.98f),
                                    size = if (zoom != 1f) (icon.size * zoom).coerceIn(0.04f, 0.8f) else icon.size
                                )
                                latestOnDoc(
                                    curDoc.copy(
                                        icons = curDoc.icons.mapIndexed { idx, old -> if (idx == selIcon) nextIcon else old }
                                    )
                                )
                                return@detectTransformGestures
                            }

                            // 3. Selected text slot move + pinch-to-resize (Section 3.3)
                            if (selSlot != null && latestMoveOn) {
                                val curLayout = postTemplate(curDoc.templateId).withLookIndex(curDoc.lookIndex).layout(curDoc.size)
                                val slotBox = curLayout.slots.firstOrNull { it.slot == selSlot }
                                var updated = curDoc
                                if (dx != 0f || dy != 0f) {
                                    val oldOff = updated.offsetOf(selSlot)
                                    updated = updated.withOffset(
                                        selSlot,
                                        Offset(
                                            (oldOff.dx + dx).coerceIn(-0.85f, 0.85f),
                                            (oldOff.dy + dy).coerceIn(-0.85f, 0.85f)
                                        )
                                    )
                                }
                                if (zoom != 1f && kotlin.math.abs(zoom - 1f) > 0.004f) {
                                    val curStyle = updated.slotStyles[selSlot] ?: SlotStyle()
                                    val rawSlotSize = slotBox?.size ?: 0.032f
                                    val defaultU = (if (rawSlotSize <= 2.0f) rawSlotSize else rawSlotSize / 1080f).coerceIn(0.015f, 1.6f)
                                    val currentU = curStyle.sizeUnit ?: (defaultU * curStyle.scale).coerceIn(0.015f, 1.6f)
                                    val nextU = (currentU * zoom).coerceIn(0.015f, 1.6f)
                                    val nextStyle = curStyle.copy(sizeUnit = nextU)
                                    val nextMap = updated.slotStyles.toMutableMap()
                                    nextMap[selSlot] = nextStyle
                                    updated = updated.copy(slotStyles = nextMap)
                                }
                                latestOnDoc(updated)
                                return@detectTransformGestures
                            }

                            // 4. Cutout subject move/pinch
                            val normX = (centroid.x / w).coerceIn(0f, 1f)
                            val normY = (centroid.y / h).coerceIn(0f, 1f)
                            if (latestMoveOn && PostEngine.hitCutout(curDoc, normX, normY)) {
                                latestOnDoc(
                                    curDoc.copy(
                                        cutoutX = (curDoc.cutoutX + dx).coerceIn(0.05f, 0.95f),
                                        cutoutY = (curDoc.cutoutY + dy).coerceIn(0.1f, 1.05f),
                                        cutoutScale = (curDoc.cutoutScale * zoom).coerceIn(0.3f, 2.5f)
                                    )
                                )
                            }
                        }
                    }
            ) {
                val bmp = renderResult?.bitmap
                if (bmp != null && !bmp.isRecycled) {
                    Image(
                        bitmap = bmp.asImageBitmap(),
                        contentDescription = "Poster preview",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Subtle selection outline around selected text box on the preview
                val activeSlotName = selectedSlot
                if (activeSlotName != null) {
                    val slotBox = layout.slots.firstOrNull { it.slot == activeSlotName }
                    if (slotBox != null) {
                        val rectNorm = PostEngine.slotRect(slotBox, doc)
                        val deg = PostEngine.effectiveRotation(slotBox, doc)
                        Canvas(Modifier.fillMaxSize()) {
                            val left = rectNorm.left * size.width
                            val top = rectNorm.top * size.height
                            val rw = rectNorm.width() * size.width
                            val rh = rectNorm.height() * size.height
                            val cx = left + rw / 2f
                            val cy = top + rh / 2f
                            withTransform({
                                if (deg != 0f) rotate(deg, ComposeOffset(cx, cy))
                            }) {
                                drawRect(
                                    color = Color(0xFF42E6A4),
                                    topLeft = ComposeOffset(left, top),
                                    size = ComposeSize(rw, rh),
                                    style = Stroke(width = 2.dp.toPx())
                                )
                            }
                        }
                    }
                }
            }
        }

        // Overflow warning chip (Section 3.1)
        if (renderResult?.overflow?.isNotEmpty() == true) {
            val isStudy = doc.templateId.startsWith("study-")
            val msg = if (isStudy) {
                "Text shrunk to fit"
            } else {
                "Too long for this template: shorten it or pick a bigger one"
            }
            Surface(
                color = Color(0xFF3B1D1D),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = msg,
                    color = Color(0xFFFCA5A5),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }

        // Floating HUD (Section 3.3): Selected text box HUD or All-text HUD
        FloatingStudioHud(
            doc = doc,
            onDoc = onDoc,
            layout = layout,
            selectedSlot = selectedSlot,
            onClearSlot = { selectedSlot = null },
            onOpenMore = { moreSheetOpen = true },
            allHudTarget = allHudTarget,
            onAllHudTarget = { allHudTarget = it },
            onPickBackgroundPhoto = onPickBackgroundPhoto
        )

        // Editor tabs below the HUD
        EditorTabs(
            tab = activeTab,
            onTab = { activeTab = it },
            doc = doc,
            onDoc = onDoc,
            brand = brand,
            library = library,
            selectedSlot = selectedSlot,
            onSelectSlot = { selectedSlot = it },
            selectedIcon = selectedIcon,
            onSelectIcon = { selectedIcon = it },
            moveLogo = moveLogo,
            onMoveLogo = { moveLogo = it },
            moveOn = moveOn,
            onMove = { moveOn = it },
            onPickPhoto = onPickPhoto,
            onPickBackgroundPhoto = onPickBackgroundPhoto,
            onPickLogo = onPickLogo,
            onBringWords = onBringWords,
            onOpenLibrary = onOpenLibrary
        )

        Spacer(Modifier.height(16.dp))
    }

    // Bottom sheet for "More" per-slot style controls (Section 3.3)
    val sheetSlot = selectedSlot
    if (moreSheetOpen && sheetSlot != null) {
        ModalBottomSheet(
            onDismissRequest = { moreSheetOpen = false },
            containerColor = PostPalette.Surface
        ) {
            val style = doc.slotStyles[sheetSlot] ?: SlotStyle()
            Column(
                Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${PostSlots.label(sheetSlot)} style",
                        color = PostPalette.Text,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    FilterChip(
                        selected = false,
                        onClick = { moreSheetOpen = false },
                        label = { Text("Done") }
                    )
                }
                SlotStyleEditor(
                    slot = sheetSlot,
                    doc = doc,
                    style = style,
                    brandHex = brand.accentHex,
                    onStyle = { nextStyle ->
                        val nextMap = doc.slotStyles.toMutableMap()
                        if (nextStyle.isDefault) nextMap.remove(sheetSlot) else nextMap[sheetSlot] = nextStyle
                        onDoc(doc.copy(slotStyles = nextMap))
                    },
                    onResetOffset = if (doc.offsetOf(sheetSlot) != Offset(0f, 0f)) {
                        {
                            val nextOffsets = doc.offsets.toMutableMap()
                            nextOffsets.remove(sheetSlot)
                            onDoc(doc.copy(offsets = nextOffsets))
                        }
                    } else null
                )
                Spacer(Modifier.height(24.dp))
            }
        }
    }

    // Export bottom sheet
    if (exportSheetOpen) {
        ExportBottomSheet(
            doc = doc,
            brand = brand,
            onDismiss = { exportSheetOpen = false },
            onSave = { sizes, fourK, share ->
                exportSheetOpen = false
                scope.launch {
                    runCatching {
                        withContext(Dispatchers.IO) {
                            PostExport.save(context, doc, brand, sizes, png = true, fourK = fourK)
                        }
                    }.onSuccess { saved ->
                        if (share && saved.uris.isNotEmpty()) {
                            shareUris(context, saved.uris)
                        } else {
                            Toast.makeText(context, "Saved ${saved.uris.size} image(s) to Gallery", Toast.LENGTH_LONG).show()
                        }
                    }.onFailure { err ->
                        Toast.makeText(context, "Save failed: ${err.message}", Toast.LENGTH_LONG).show()
                    }
                }
            },
            onSaveVideoFrame = if (PostFrameExport.isDeviceTemplate(doc.templateId)) {
                { sizes, fourK ->
                    exportSheetOpen = false
                    scope.launch {
                        runCatching {
                            withContext(Dispatchers.IO) {
                                PostFrameExport.save(context, doc, brand, sizes, fourK)
                            }
                        }.onSuccess { uris ->
                            Toast.makeText(
                                context,
                                "Saved ${uris.size} transparent video frame PNG(s). " + PostFrameExport.hint(doc, doc.size),
                                Toast.LENGTH_LONG
                            ).show()
                        }.onFailure { err ->
                            Toast.makeText(context, "Frame export failed: ${err.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            } else null
        )
    }
}

private fun shareUris(context: android.content.Context, uris: List<Uri>) {
    if (uris.isEmpty()) return
    val intent = if (uris.size == 1) {
        Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uris.first())
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    } else {
        Intent(Intent.ACTION_SEND_MULTIPLE).apply {
            type = "image/png"
            putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(uris))
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }
    context.startActivity(Intent.createChooser(intent, "Share poster"))
}

@Composable
private fun FloatingStudioHud(
    doc: PostDoc,
    onDoc: (PostDoc) -> Unit,
    layout: TemplateLayout,
    selectedSlot: String?,
    onClearSlot: () -> Unit,
    onOpenMore: () -> Unit,
    allHudTarget: String,
    onAllHudTarget: (String) -> Unit,
    onPickBackgroundPhoto: () -> Unit
) {
    val shape = RoundedCornerShape(14.dp)
    Column(
        Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(PostPalette.Surface)
            .border(1.dp, PostPalette.Line, shape)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        if (selectedSlot != null) {
            val style = doc.slotStyles[selectedSlot] ?: SlotStyle()
            val slotBox = layout.slots.firstOrNull { it.slot == selectedSlot }
            val shortSide = min(doc.size.width, doc.size.height)
            val rawSlotSize = slotBox?.size ?: 0.032f
            val defaultUnit = (if (rawSlotSize <= 2.0f) rawSlotSize else rawSlotSize / 1080f).coerceIn(0.015f, 1.6f)
            val activeUnit = style.sizeUnit ?: (defaultUnit * style.scale).coerceIn(0.015f, 1.6f)
            val pxVal = (activeUnit * shortSide).roundToInt()
            val shadow = style.shadow ?: TextShadow()

            fun updateStyle(next: SlotStyle) {
                val map = doc.slotStyles.toMutableMap()
                if (next.isDefault) map.remove(selectedSlot) else map[selectedSlot] = next
                onDoc(doc.copy(slotStyles = map))
            }

            Row(
                Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = PostSlots.label(selectedSlot),
                    color = PostPalette.Mint,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(end = 4.dp)
                )
                FilterChip(
                    selected = false,
                    onClick = { updateStyle(style.copy(sizeUnit = (activeUnit / 1.12f).coerceIn(0.015f, 1.6f))) },
                    label = { Text("A-", fontSize = 12.sp) }
                )
                FilterChip(
                    selected = false,
                    onClick = { updateStyle(style.copy(sizeUnit = (activeUnit * 1.12f).coerceIn(0.015f, 1.6f))) },
                    label = { Text("A+", fontSize = 12.sp) }
                )
                FilterChip(
                    selected = style.sizeUnit == null && style.scale == 1f,
                    onClick = { updateStyle(style.copy(sizeUnit = null, scale = 1f)) },
                    label = { Text("Fit", fontSize = 12.sp) }
                )
                FilterChip(
                    selected = false,
                    onClick = onOpenMore,
                    label = { Text("More", fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Outlined.Tune, contentDescription = null) }
                )
                FilterChip(
                    selected = false,
                    onClick = onClearSlot,
                    label = { Text("Done", fontSize = 12.sp) }
                )
            }

            // Size logarithmic slider (16px..1700px) + Shadow slider
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Size $pxVal px", color = PostPalette.Muted, fontSize = 11.sp, modifier = Modifier.weight(0.28f))
                Slider(
                    value = unitToLogSlider(activeUnit),
                    onValueChange = { t -> updateStyle(style.copy(sizeUnit = logSliderToUnit(t))) },
                    valueRange = 0f..1f,
                    modifier = Modifier.weight(0.72f)
                )
            }
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = if (shadow.strength == 0) "Shadow Off" else "Shadow ${shadow.strength}",
                    color = PostPalette.Muted,
                    fontSize = 11.sp,
                    modifier = Modifier.weight(0.28f)
                )
                Slider(
                    value = shadow.strength.toFloat(),
                    onValueChange = { v ->
                        val s = v.roundToInt().coerceIn(0, 100)
                        updateStyle(style.copy(shadow = if (s == 0) null else shadow.copy(strength = s)))
                    },
                    valueRange = 0f..100f,
                    modifier = Modifier.weight(0.72f)
                )
            }
        } else {
            // All-text HUD when no box is selected (Section 3.3)
            Row(
                Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Quick:", color = PostPalette.Muted, fontSize = 12.sp)
                listOf(
                    "size" to "All size",
                    "shadow" to "All shadow",
                    "photo" to "Bg photo",
                    "panels" to "Panels"
                ).forEach { (id, label) ->
                    FilterChip(
                        selected = allHudTarget == id,
                        onClick = { onAllHudTarget(id) },
                        label = { Text(label, fontSize = 12.sp) }
                    )
                }
                Text("Tap any text on preview to resize or style it", color = PostPalette.Muted, fontSize = 11.sp)
            }

            when (allHudTarget) {
                "size" -> Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("${(doc.textScale * 100).roundToInt()}%", color = PostPalette.Muted, fontSize = 11.sp, modifier = Modifier.weight(0.22f))
                    Slider(
                        value = doc.textScale.coerceIn(0.65f, 1.45f),
                        onValueChange = { onDoc(doc.copy(textScale = it)) },
                        valueRange = 0.65f..1.45f,
                        modifier = Modifier.weight(0.78f)
                    )
                }

                "shadow" -> Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        if (doc.shadowAll.strength == 0) "Auto" else "${doc.shadowAll.strength}",
                        color = PostPalette.Muted,
                        fontSize = 11.sp,
                        modifier = Modifier.weight(0.22f)
                    )
                    Slider(
                        value = doc.shadowAll.strength.toFloat(),
                        onValueChange = {
                            onDoc(doc.copy(shadowAll = doc.shadowAll.copy(strength = it.roundToInt().coerceIn(0, 100))))
                        },
                        valueRange = 0f..100f,
                        modifier = Modifier.weight(0.78f)
                    )
                }

                "photo" -> {
                    val hasBg = !doc.backgroundPhotoUri.isNullOrBlank() || (doc.photoAsBackground && !doc.photoUri.isNullOrBlank())
                    if (!hasBg) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedButton(onClick = onPickBackgroundPhoto) {
                                Text("Add background photo", fontSize = 12.sp)
                            }
                            if (!doc.photoUri.isNullOrBlank()) {
                                FilterChip(
                                    selected = doc.photoAsBackground,
                                    onClick = {
                                        onDoc(doc.copy(photoAsBackground = true, backgroundPhotoOpacity = 0.55f))
                                    },
                                    label = { Text("Use template photo", fontSize = 12.sp) }
                                )
                            }
                        }
                    } else {
                        Row(
                            Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("${(doc.backgroundPhotoOpacity * 100).roundToInt()}%", color = PostPalette.Muted, fontSize = 11.sp, modifier = Modifier.weight(0.22f))
                            Slider(
                                value = doc.backgroundPhotoOpacity.coerceIn(0f, 1f),
                                onValueChange = { onDoc(doc.copy(backgroundPhotoOpacity = it)) },
                                valueRange = 0f..1f,
                                modifier = Modifier.weight(0.78f)
                            )
                        }
                    }
                }

                "panels" -> Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("${doc.panelOpacity}%", color = PostPalette.Muted, fontSize = 11.sp, modifier = Modifier.weight(0.22f))
                    Slider(
                        value = doc.panelOpacity.toFloat().coerceIn(0f, 100f),
                        onValueChange = { onDoc(doc.copy(panelOpacity = it.roundToInt().coerceIn(0, 100))) },
                        valueRange = 0f..100f,
                        modifier = Modifier.weight(0.78f)
                    )
                }
            }
        }
    }
}

@Composable
private fun StudioTopBar(
    doc: PostDoc,
    spec: PostTemplateSpec,
    edited: Boolean,
    bookmarked: Boolean,
    embedded: Boolean,
    headerTitle: String?,
    onToggleBookmark: () -> Unit,
    onReset: () -> Unit,
    onOpenLibrary: () -> Unit,
    onOpenBatch: () -> Unit,
    onOpenInbox: () -> Unit,
    onOpenBrand: () -> Unit,
    onHeaderApplyAll: (() -> Unit)?,
    onHeaderDone: (() -> Unit)?,
    onOpenExport: () -> Unit
) {
    Row(
        Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (embedded) {
            if (headerTitle != null) {
                Text(headerTitle, color = PostPalette.Text, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
            if (onHeaderApplyAll != null) {
                OutlinedButton(onClick = onHeaderApplyAll) { Text("Apply to all", fontSize = 12.sp) }
            }
            if (onHeaderDone != null) {
                Button(onClick = onHeaderDone) { Text("Done", fontSize = 12.sp) }
            }
        } else {
            Text(
                text = "${spec.name} · ${spec.family}",
                color = PostPalette.Text,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.clickable(onClick = onOpenLibrary)
            )
            IconButton(onClick = onToggleBookmark) {
                Icon(
                    imageVector = if (bookmarked) Icons.Outlined.Bookmark else Icons.Outlined.BookmarkBorder,
                    contentDescription = "Bookmark template",
                    tint = if (bookmarked) PostPalette.Mint else PostPalette.Muted
                )
            }
            if (edited) {
                FilterChip(selected = false, onClick = onReset, label = { Text("Reset", fontSize = 12.sp) })
            }
            FilterChip(selected = false, onClick = onOpenBatch, label = { Text("Batch", fontSize = 12.sp) })
            FilterChip(selected = false, onClick = onOpenInbox, label = { Text("Inbox", fontSize = 12.sp) })
            FilterChip(selected = false, onClick = onOpenBrand, label = { Text("Brand", fontSize = 12.sp) })
            Button(onClick = onOpenExport) {
                Icon(Icons.Outlined.Share, contentDescription = null)
                Text(" Save / Share", fontSize = 12.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExportBottomSheet(
    doc: PostDoc,
    brand: PostBrand,
    onDismiss: () -> Unit,
    onSave: (sizes: List<PostSize>, fourK: Boolean, share: Boolean) -> Unit,
    onSaveVideoFrame: ((sizes: List<PostSize>, fourK: Boolean) -> Unit)?
) {
    var allSizes by rememberSaveable { mutableStateOf(false) }
    var fourK by rememberSaveable { mutableStateOf(false) }

    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = PostPalette.Surface) {
        Column(
            Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Save or share post", color = PostPalette.Text, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            PostSwitchRow("Export all 3 sizes (4:3, 9:16, 16:9)", allSizes) { allSizes = it }
            PostSwitchRow("Ultra-sharp 2x resolution", fourK) { fourK = it }

            val targetSizes = if (allSizes) PostSize.values().toList() else listOf(doc.size)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(onClick = { onSave(targetSizes, fourK, false) }, modifier = Modifier.weight(1f)) {
                    Text("Save PNG")
                }
                OutlinedButton(onClick = { onSave(targetSizes, fourK, true) }, modifier = Modifier.weight(1f)) {
                    Text("Share")
                }
            }
            if (onSaveVideoFrame != null) {
                OutlinedButton(
                    onClick = { onSaveVideoFrame(targetSizes, fourK) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save transparent frame for video (CapCut)")
                }
                Text(
                    text = PostFrameExport.hint(doc, doc.size),
                    color = PostPalette.Muted,
                    fontSize = 12.sp
                )
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}
