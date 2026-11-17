package com.wisdomtower.post

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.util.LruCache
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Undo
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.outlined.AspectRatio
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Collections
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.FactCheck
import androidx.compose.material.icons.outlined.FormatSize
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material.icons.outlined.IosShare
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material.icons.outlined.TextFields
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined.WbShade
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset as ComposeOffset
import androidx.compose.ui.geometry.Size as ComposeSize
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Low-resolution LRU cache for neighbouring template pages in the HorizontalPager so swiping stays smooth.
 */
private object NeighborPreviewCache {
    private val cache = object : LruCache<String, Bitmap>(36) {
        override fun sizeOf(key: String, value: Bitmap): Int = 1
    }

    @Synchronized
    fun get(key: String): Bitmap? {
        val b = cache.get(key)
        return if (b != null && !b.isRecycled) b else null
    }

    @Synchronized
    fun put(key: String, bitmap: Bitmap) {
        if (!bitmap.isRecycled) {
            cache.put(key, bitmap)
        }
    }
}

/**
 * Main Studio screen with:
 * - 48dp Top Bar (Template name + bookmark on left; icon-only Reset, Batch, Undo, Export, More on right).
 * - Fixed centered Preview (~62%+ of screen height) that never scrolls away, inside a smooth HorizontalPager
 *   with snap, neighbouring edges visible, page indicator, and template name underneath.
 * - Compact 1-row "Quick + Ratio" chip bar.
 * - 3-snap-state bottom sheet (PEEK = tab row + thumbnail template strip; HALF = 220dp controls; FULL = 360dp controls).
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
    onOpenQa: (() -> Unit)? = null,
    canUndo: Boolean = false,
    onUndo: (() -> Unit)? = null,
    embedded: Boolean = false,
    headerTitle: String? = null,
    onHeaderApplyAll: (() -> Unit)? = null,
    onHeaderDone: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var activeTab by rememberSaveable { mutableStateOf("template") }
    // 0 = PEEK (tab bar + template strip), 1 = HALF (220dp), 2 = FULL (360dp)
    var sheetSnap by rememberSaveable { mutableStateOf(0) }
    var selectedFamily by rememberSaveable { mutableStateOf("All") }
    var selectedSlot by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedIcon by rememberSaveable { mutableStateOf(-1) }
    var moveLogo by rememberSaveable { mutableStateOf(false) }
    var moveOn by rememberSaveable { mutableStateOf(true) }
    var moreSheetOpen by rememberSaveable { mutableStateOf(false) }
    var exportSheetOpen by rememberSaveable { mutableStateOf(false) }
    var quickTarget by rememberSaveable { mutableStateOf<String?>(null) }
    var showSafeZones by rememberSaveable { mutableStateOf(false) }

    val spec = postTemplate(doc.templateId).withLookIndex(doc.lookIndex)
    val layout = spec.layout(doc.size)
    if (selectedSlot != null && layout.slots.none { it.slot == selectedSlot }) {
        selectedSlot = null
    }

    // Filtered template list for the swipe pager and bottom template strip
    val filteredTemplates = remember(selectedFamily, library.bookmarks) {
        filterTemplatesByFamily(selectedFamily, library)
    }

    // Ensure if current template isn't in the newly picked family filter, we switch to the first template in that family
    LaunchedEffect(selectedFamily) {
        val list = filterTemplatesByFamily(selectedFamily, library)
        if (list.isNotEmpty() && list.none { it.id == doc.templateId }) {
            val first = list.first()
            val nextTexts = PostSlots.withDerivedSlots(doc.texts, first.slots)
            onDoc(doc.copy(templateId = first.id, lookIndex = 0, texts = nextTexts))
        }
    }

    val currentIndex = remember(doc.templateId, filteredTemplates) {
        filteredTemplates.indexOfFirst { it.id == doc.templateId }.coerceAtLeast(0)
    }

    val pagerState = rememberPagerState(
        initialPage = currentIndex,
        pageCount = { filteredTemplates.size.coerceAtLeast(1) }
    )

    val latestDoc by rememberUpdatedState(doc)
    val latestOnDoc by rememberUpdatedState(onDoc)
    val latestFiltered by rememberUpdatedState(filteredTemplates)

    // Sync external template changes (e.g., tapping a card in TemplateStrip or Library) -> Pager
    LaunchedEffect(doc.templateId, filteredTemplates) {
        val target = filteredTemplates.indexOfFirst { it.id == doc.templateId }
        if (target >= 0 && target != pagerState.currentPage && !pagerState.isScrollInProgress) {
            runCatching {
                if (abs(target - pagerState.currentPage) > 2) {
                    pagerState.scrollToPage(target)
                } else {
                    pagerState.animateScrollToPage(target)
                }
            }
        }
    }

    // Sync user swipe on HorizontalPager -> update doc.templateId while keeping words, photo, and brand
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }
            .distinctUntilChanged()
            .collect { page ->
                val list = latestFiltered
                val nextSpec = list.getOrNull(page) ?: return@collect
                val cur = latestDoc
                if (nextSpec.id != cur.templateId) {
                    val nextTexts = PostSlots.withDerivedSlots(cur.texts, nextSpec.slots)
                    latestOnDoc(
                        cur.copy(
                            templateId = nextSpec.id,
                            lookIndex = 0,
                            texts = nextTexts
                        )
                    )
                }
            }
    }

    // Two-stage render for the active page: instant 380px pass during slider drags, then crisp 720px on settle
    val renderResult by produceState<PostRenderer.Result?>(null, doc, brand) {
        val fast = withContext(Dispatchers.Default) {
            PostRenderer.preview(context, doc, brand, maxSide = 380) { false }
        }
        value = fast
        delay(90)
        val full = withContext(Dispatchers.Default) {
            PostRenderer.preview(context, doc, brand, maxSide = 720) { false }
        }
        value = full
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(PostPalette.Ink)
            .let { if (embedded) it else it.statusBarsPadding() }
    ) {
        // 1. Top bar, 48dp high
        StudioTopBar(
            spec = spec,
            edited = edited,
            bookmarked = bookmarked,
            canUndo = canUndo,
            embedded = embedded,
            headerTitle = headerTitle,
            hasOffsets = doc.hasOffsets(),
            onToggleBookmark = onToggleBookmark,
            onReset = onReset,
            onUndo = onUndo,
            onBringWords = onBringWords,
            onResetOffsets = { onDoc(doc.withoutOffsets()) },
            onOpenLibrary = onOpenLibrary,
            onOpenBatch = onOpenBatch,
            onOpenInbox = onOpenInbox,
            onOpenBrand = onOpenBrand,
            onOpenQa = onOpenQa,
            onHeaderApplyAll = onHeaderApplyAll,
            onHeaderDone = onHeaderDone,
            onOpenExport = { exportSheetOpen = true }
        )

        // 3. Always-visible centered Preview (~55%+ of screen height) with HorizontalPager swipe
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 2.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            HorizontalPager(
                state = pagerState,
                contentPadding = PaddingValues(horizontal = 26.dp),
                pageSpacing = 10.dp,
                beyondViewportPageCount = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) { page ->
                val pageSpec = filteredTemplates.getOrNull(page) ?: spec
                val isCurrentPage = pageSpec.id == doc.templateId
                if (isCurrentPage) {
                    ActivePreviewPage(
                        doc = doc,
                        onDoc = onDoc,
                        layout = layout,
                        bitmap = renderResult?.bitmap,
                        selectedSlot = selectedSlot,
                        onSelectSlot = { selectedSlot = it },
                        selectedIcon = selectedIcon,
                        onSelectIcon = { selectedIcon = it },
                        moveLogo = moveLogo,
                        moveOn = moveOn,
                        showSafeZones = showSafeZones
                    )
                } else {
                    NeighborPreviewPage(
                        baseDoc = doc,
                        brand = brand,
                        neighborSpec = pageSpec,
                        onClick = {
                            scope.launch { pagerState.animateScrollToPage(page) }
                        }
                    )
                }
            }

            // Template name, family, page indicator, and overflow badge directly under the preview
            val settledSpec = filteredTemplates.getOrNull(pagerState.currentPage) ?: spec
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = settledSpec.name,
                        color = PostPalette.Text,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "· ${settledSpec.family}",
                        color = PostPalette.Muted,
                        fontSize = 11.sp,
                        maxLines = 1
                    )
                    if (renderResult?.overflow?.isNotEmpty() == true) {
                        Text(
                            text = if (doc.templateId.startsWith("study-")) "• Fitted" else "• Long text",
                            color = Color(0xFFFCA5A5),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1
                        )
                    }
                }

                // Compact page dots + counter
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val total = filteredTemplates.size.coerceAtLeast(1)
                    val cur = pagerState.currentPage.coerceIn(0, total - 1)
                    val dotWindow = 5
                    val start = (cur - dotWindow / 2).coerceAtLeast(0)
                    val end = (start + dotWindow).coerceAtMost(total)
                    for (i in start until end) {
                        Box(
                            Modifier
                                .size(if (i == cur) 6.dp else 4.dp)
                                .clip(CircleShape)
                                .background(if (i == cur) PostPalette.Mint else PostPalette.Line)
                        )
                    }
                    Text(
                        text = "${cur + 1}/$total",
                        color = PostPalette.Muted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(start = 2.dp)
                    )
                }
            }
        }

        // 4. Compact 1-row "Quick" strip (or selected slot inline bar)
        CompactQuickStrip(
            doc = doc,
            onDoc = onDoc,
            brand = brand,
            layout = layout,
            selectedSlot = selectedSlot,
            onClearSlot = { selectedSlot = null },
            onOpenMore = { moreSheetOpen = true },
            quickTarget = quickTarget,
            onQuickTarget = { quickTarget = if (quickTarget == it) null else it },
            onPickBackgroundPhoto = onPickBackgroundPhoto,
            showSafeZones = showSafeZones,
            onToggleSafeZones = { showSafeZones = !showSafeZones }
        )

        // 5. 3-snap-state Bottom Sheet (0 = PEEK, 1 = HALF, 2 = FULL) above the Bottom Nav
        CollapsibleEditorSheet(
            snapState = sheetSnap,
            onSnapChange = { sheetSnap = it.coerceIn(0, 2) },
            activeTab = activeTab,
            onTabSelect = { tapped ->
                if (tapped == "template") {
                    activeTab = "template"
                    sheetSnap = if (sheetSnap == 0) 1 else 0
                } else if (activeTab == tapped && sheetSnap > 0) {
                    sheetSnap = if (sheetSnap == 1) 2 else 0
                } else {
                    activeTab = tapped
                    if (sheetSnap == 0) sheetSnap = 1
                }
            },
            doc = doc,
            onDoc = onDoc,
            brand = brand,
            onBrand = onBrand,
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
            onOpenLibrary = onOpenLibrary,
            selectedFamily = selectedFamily,
            onSelectFamily = { selectedFamily = it }
        )
    }

    // Bottom sheet for "More" per-slot style controls
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
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
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
                        fontSize = 14.sp
                    )
                    CompactChip(
                        label = "Done",
                        selected = false,
                        onClick = { moreSheetOpen = false }
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
                Spacer(Modifier.height(16.dp))
            }
        }
    }

    // Export bottom sheet
    if (exportSheetOpen) {
        ExportBottomSheet(
            doc = doc,
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

@Composable
private fun ActivePreviewPage(
    doc: PostDoc,
    onDoc: (PostDoc) -> Unit,
    layout: TemplateLayout,
    bitmap: Bitmap?,
    selectedSlot: String?,
    onSelectSlot: (String?) -> Unit,
    selectedIcon: Int,
    onSelectIcon: (Int) -> Unit,
    moveLogo: Boolean,
    moveOn: Boolean,
    showSafeZones: Boolean = false
) {
    val latestDoc by rememberUpdatedState(doc)
    val latestOnDoc by rememberUpdatedState(onDoc)
    val latestSelectedSlot by rememberUpdatedState(selectedSlot)
    val latestSelectedIcon by rememberUpdatedState(selectedIcon)
    val latestMoveLogo by rememberUpdatedState(moveLogo)
    val latestMoveOn by rememberUpdatedState(moveOn)

    var previewPxSize by remember { mutableStateOf(IntSize.Zero) }
    val aspect = doc.size.width.toFloat() / doc.size.height.toFloat()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            Modifier
                .aspectRatio(aspect)
                .clip(RoundedCornerShape(12.dp))
                .background(PostPalette.Surface)
                .border(1.dp, PostPalette.Line, RoundedCornerShape(12.dp))
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
                            onSelectIcon(hitIconIdx)
                            onSelectSlot(null)
                            return@detectTapGestures
                        }
                        val curLayout = postTemplate(curDoc.templateId).withLookIndex(curDoc.lookIndex).layout(curDoc.size)
                        val hit = PostEngine.hitSlot(curLayout, curDoc, normX, normY)
                        onSelectSlot(hit)
                        if (hit != null) onSelectIcon(-1)
                    }
                }
                .pointerInput(doc.templateId, doc.size, doc.lookIndex, selectedSlot, selectedIcon, moveLogo) {
                    if (latestSelectedSlot == null && latestSelectedIcon !in latestDoc.icons.indices && !latestMoveLogo) {
                        // Let HorizontalPager handle horizontal swipe gestures when no box/icon/logo is actively grabbed
                        return@pointerInput
                    }
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

                        // 3. Selected text slot move + pinch-to-resize
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
            if (bitmap != null && !bitmap.isRecycled) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Poster preview",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Editor-only TikTok / Reels / Shorts safe-zone overlay (never drawn in exports)
            if (showSafeZones) {
                Canvas(Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val dangerColor = Color(0x55EF4444)
                    // Top status/search bar zone (0..13%)
                    drawRect(
                        color = dangerColor,
                        topLeft = ComposeOffset(0f, 0f),
                        size = ComposeSize(w, h * PostSafeArea.STORY_SAFE_TOP)
                    )
                    // Bottom caption/scrub bar zone (79..100%)
                    drawRect(
                        color = dangerColor,
                        topLeft = ComposeOffset(0f, h * PostSafeArea.STORY_SAFE_BOTTOM),
                        size = ComposeSize(w, h * (1f - PostSafeArea.STORY_SAFE_BOTTOM))
                    )
                    // Right icon rail zone (91..100% between top and bottom)
                    drawRect(
                        color = dangerColor,
                        topLeft = ComposeOffset(w * PostSafeArea.STORY_SAFE_RIGHT, h * PostSafeArea.STORY_SAFE_TOP),
                        size = ComposeSize(w * (1f - PostSafeArea.STORY_SAFE_RIGHT), h * (PostSafeArea.STORY_SAFE_BOTTOM - PostSafeArea.STORY_SAFE_TOP))
                    )
                    // Left margin zone (0..9% between top and bottom)
                    drawRect(
                        color = Color(0x33EF4444),
                        topLeft = ComposeOffset(0f, h * PostSafeArea.STORY_SAFE_TOP),
                        size = ComposeSize(w * PostSafeArea.STORY_SAFE_LEFT, h * (PostSafeArea.STORY_SAFE_BOTTOM - PostSafeArea.STORY_SAFE_TOP))
                    )
                    // Safe box outline
                    drawRect(
                        color = Color(0xFF42E6A4),
                        topLeft = ComposeOffset(w * PostSafeArea.STORY_SAFE_LEFT, h * PostSafeArea.STORY_SAFE_TOP),
                        size = ComposeSize(
                            w * (PostSafeArea.STORY_SAFE_RIGHT - PostSafeArea.STORY_SAFE_LEFT),
                            h * (PostSafeArea.STORY_SAFE_BOTTOM - PostSafeArea.STORY_SAFE_TOP)
                        ),
                        style = Stroke(width = 1.5.dp.toPx())
                    )
                }
            }

            // Subtle selection outline around selected text box on the preview
            if (selectedSlot != null) {
                val slotBox = layout.slots.firstOrNull { it.slot == selectedSlot }
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
}

@Composable
private fun NeighborPreviewPage(
    baseDoc: PostDoc,
    brand: PostBrand,
    neighborSpec: PostTemplateSpec,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val aspect = baseDoc.size.width.toFloat() / baseDoc.size.height.toFloat()
    val cacheKey = remember(
        neighborSpec.id,
        baseDoc.size,
        baseDoc.texts.hashCode(),
        baseDoc.photoUri,
        baseDoc.secondaryPhotoUri,
        baseDoc.tertiaryPhotoUri,
        baseDoc.backgroundPhotoUri,
        brand.hashCode()
    ) {
        "${neighborSpec.id}:${baseDoc.size.name}:${baseDoc.texts.hashCode()}:${baseDoc.photoUri}:${baseDoc.secondaryPhotoUri}:${baseDoc.tertiaryPhotoUri}:${baseDoc.backgroundPhotoUri}:${brand.hashCode()}"
    }

    val bmp by produceState<Bitmap?>(NeighborPreviewCache.get(cacheKey), cacheKey) {
        if (value != null) return@produceState
        delay(45)
        val rendered = withContext(Dispatchers.Default) {
            runCatching {
                val neighborDoc = baseDoc.copy(
                    templateId = neighborSpec.id,
                    lookIndex = 0,
                    texts = PostSlots.withDerivedSlots(baseDoc.texts, neighborSpec.slots)
                )
                PostRenderer.preview(context, neighborDoc, brand, maxSide = 300) { false }?.bitmap
            }.getOrNull()
        }
        if (rendered != null) {
            NeighborPreviewCache.put(cacheKey, rendered)
            value = rendered
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            Modifier
                .fillMaxHeight(0.92f)
                .aspectRatio(aspect)
                .clip(RoundedCornerShape(10.dp))
                .background(PostPalette.Surface)
                .border(1.dp, PostPalette.Line, RoundedCornerShape(10.dp))
                .clickable(onClick = onClick)
        ) {
            val b = bmp
            if (b != null && !b.isRecycled) {
                Image(
                    bitmap = b.asImageBitmap(),
                    contentDescription = neighborSpec.name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

/**
 * 1. Top bar, 48dp high:
 *    - Template name on the left, with a small bookmark.
 *    - On the right, icon-only buttons for Reset, Batch, Undo, Export and More.
 *    - Nothing may be cut off at the right edge.
 *    - Extra actions go in the More menu.
 */
@Composable
private fun StudioTopBar(
    spec: PostTemplateSpec,
    edited: Boolean,
    bookmarked: Boolean,
    canUndo: Boolean,
    embedded: Boolean,
    headerTitle: String?,
    hasOffsets: Boolean,
    onToggleBookmark: () -> Unit,
    onReset: () -> Unit,
    onUndo: (() -> Unit)?,
    onBringWords: (() -> Unit)?,
    onResetOffsets: () -> Unit,
    onOpenLibrary: () -> Unit,
    onOpenBatch: () -> Unit,
    onOpenInbox: () -> Unit,
    onOpenBrand: () -> Unit,
    onOpenQa: (() -> Unit)?,
    onHeaderApplyAll: (() -> Unit)?,
    onHeaderDone: (() -> Unit)?,
    onOpenExport: () -> Unit
) {
    var moreMenuOpen by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(PostPalette.Surface)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (embedded) {
            Text(
                text = headerTitle ?: spec.name,
                color = PostPalette.Text,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                if (onHeaderApplyAll != null) {
                    CompactButton(label = "Apply all", onClick = onHeaderApplyAll)
                }
                if (onHeaderDone != null) {
                    CompactButton(label = "Done", primary = true, onClick = onHeaderDone)
                }
            }
        } else {
            // Left: Template name + small bookmark icon
            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .clickable(onClick = onToggleBookmark),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (bookmarked) Icons.Outlined.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = "Bookmark template",
                        tint = if (bookmarked) PostPalette.Mint else PostPalette.Muted,
                        modifier = Modifier.size(17.dp)
                    )
                }
                Text(
                    text = spec.name,
                    color = PostPalette.Text,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .clickable(onClick = onOpenLibrary)
                )
            }

            // Right: icon-only buttons for Reset, Batch, Undo, Export, and More (never clipped)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                TopBarIconButton(
                    icon = Icons.Outlined.RestartAlt,
                    contentDescription = "Reset template",
                    enabled = edited,
                    onClick = onReset
                )
                TopBarIconButton(
                    icon = Icons.Outlined.Collections,
                    contentDescription = "Batch",
                    onClick = onOpenBatch
                )
                TopBarIconButton(
                    icon = Icons.AutoMirrored.Outlined.Undo,
                    contentDescription = "Undo",
                    enabled = canUndo && onUndo != null,
                    onClick = { onUndo?.invoke() }
                )
                TopBarIconButton(
                    icon = Icons.Outlined.IosShare,
                    contentDescription = "Export",
                    highlighted = true,
                    onClick = onOpenExport
                )
                Box {
                    TopBarIconButton(
                        icon = Icons.Outlined.MoreVert,
                        contentDescription = "More actions",
                        onClick = { moreMenuOpen = true }
                    )
                    DropdownMenu(
                        expanded = moreMenuOpen,
                        onDismissRequest = { moreMenuOpen = false },
                        modifier = Modifier.background(PostPalette.Card)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Template Gallery", fontSize = 13.sp, color = PostPalette.Text) },
                            leadingIcon = { Icon(Icons.Outlined.GridView, null, tint = PostPalette.Text, modifier = Modifier.size(18.dp)) },
                            onClick = { moreMenuOpen = false; onOpenLibrary() }
                        )
                        DropdownMenuItem(
                            text = { Text("Poster Inbox", fontSize = 13.sp, color = PostPalette.Text) },
                            leadingIcon = { Icon(Icons.Outlined.Inbox, null, tint = PostPalette.Text, modifier = Modifier.size(18.dp)) },
                            onClick = { moreMenuOpen = false; onOpenInbox() }
                        )
                        DropdownMenuItem(
                            text = { Text("Brand & Logo", fontSize = 13.sp, color = PostPalette.Text) },
                            leadingIcon = { Icon(Icons.Outlined.Palette, null, tint = PostPalette.Text, modifier = Modifier.size(18.dp)) },
                            onClick = { moreMenuOpen = false; onOpenBrand() }
                        )
                        if (onOpenQa != null) {
                            DropdownMenuItem(
                                text = { Text("Template QA Check", fontSize = 13.sp, color = PostPalette.Text) },
                                leadingIcon = { Icon(Icons.Outlined.FactCheck, null, tint = PostPalette.Text, modifier = Modifier.size(18.dp)) },
                                onClick = { moreMenuOpen = false; onOpenQa() }
                            )
                        }
                        if (onBringWords != null) {
                            DropdownMenuItem(
                                text = { Text("Bring last words", fontSize = 13.sp, color = PostPalette.Text) },
                                leadingIcon = { Icon(Icons.Outlined.History, null, tint = PostPalette.Text, modifier = Modifier.size(18.dp)) },
                                onClick = { moreMenuOpen = false; onBringWords() }
                            )
                        }
                        if (hasOffsets) {
                            DropdownMenuItem(
                                text = { Text("Reset moved boxes", fontSize = 13.sp, color = PostPalette.Text) },
                                leadingIcon = { Icon(Icons.Outlined.RestartAlt, null, tint = PostPalette.Text, modifier = Modifier.size(18.dp)) },
                                onClick = { moreMenuOpen = false; onResetOffsets() }
                            )
                        }
                        DropdownMenuItem(
                            text = { Text("Reset template", fontSize = 13.sp, color = PostPalette.Text) },
                            leadingIcon = { Icon(Icons.Outlined.RestartAlt, null, tint = PostPalette.Text, modifier = Modifier.size(18.dp)) },
                            onClick = { moreMenuOpen = false; onReset() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TopBarIconButton(
    icon: ImageVector,
    contentDescription: String,
    enabled: Boolean = true,
    highlighted: Boolean = false,
    onClick: () -> Unit
) {
    val bg = when {
        highlighted -> PostPalette.Mint
        else -> PostPalette.Raised
    }
    val tint = when {
        !enabled -> PostPalette.Muted.copy(alpha = 0.35f)
        highlighted -> Color(0xFF062118)
        else -> PostPalette.Text
    }
    Box(
        modifier = Modifier
            .size(30.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .border(1.dp, if (highlighted) PostPalette.Mint else PostPalette.Line, RoundedCornerShape(8.dp))
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(16.dp)
        )
    }
}

/**
 * The "Quick" row becomes one scrollable row of small 32dp chips (with optional thin inline slider/swatches when tapped).
 */
@Composable
private fun CompactQuickStrip(
    doc: PostDoc,
    onDoc: (PostDoc) -> Unit,
    brand: PostBrand,
    layout: TemplateLayout,
    selectedSlot: String?,
    onClearSlot: () -> Unit,
    onOpenMore: () -> Unit,
    quickTarget: String?,
    onQuickTarget: (String) -> Unit,
    onPickBackgroundPhoto: () -> Unit,
    showSafeZones: Boolean = false,
    onToggleSafeZones: () -> Unit = {}
) {
    val scroll = rememberScrollState()
    Column(
        Modifier
            .fillMaxWidth()
            .background(PostPalette.Ink)
            .padding(horizontal = 8.dp, vertical = 2.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
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

            // One compact row setting Size, Shadow, and Color together for the tapped slot
            Row(
                Modifier.fillMaxWidth().horizontalScrollWithFade(scroll, PostPalette.Ink),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CompactChip(
                    label = PostSlots.label(selectedSlot),
                    selected = true,
                    onClick = onClearSlot
                )
                CompactChip(
                    label = "A-",
                    selected = false,
                    onClick = { updateStyle(style.copy(sizeUnit = (activeUnit / 1.12f).coerceIn(0.015f, 1.6f))) }
                )
                CompactChip(
                    label = "${pxVal}px",
                    icon = Icons.Outlined.FormatSize,
                    selected = quickTarget == "slot_size",
                    onClick = { onQuickTarget("slot_size") }
                )
                CompactChip(
                    label = "A+",
                    selected = false,
                    onClick = { updateStyle(style.copy(sizeUnit = (activeUnit * 1.12f).coerceIn(0.015f, 1.6f))) }
                )
                CompactChip(
                    label = "Shadow",
                    icon = Icons.Outlined.WbShade,
                    selected = quickTarget == "slot_shadow",
                    onClick = { onQuickTarget("slot_shadow") }
                )
                CompactChip(
                    label = "Color",
                    icon = Icons.Outlined.Palette,
                    selected = quickTarget == "slot_color",
                    onClick = { onQuickTarget("slot_color") }
                )
                CompactChip(
                    label = "Fit",
                    selected = style.sizeUnit == null && style.scale == 1f,
                    onClick = { updateStyle(style.copy(sizeUnit = null, scale = 1f)) }
                )
                CompactChip(
                    label = "Style",
                    icon = Icons.Outlined.Tune,
                    selected = false,
                    onClick = onOpenMore
                )
                CompactChip(
                    label = "Done",
                    icon = Icons.Outlined.Close,
                    selected = false,
                    onClick = onClearSlot
                )
            }

            when (quickTarget) {
                "slot_size" -> PostLabeledSlider(
                    label = "Size",
                    value = unitToLogSlider(activeUnit),
                    range = 0f..1f,
                    display = "${pxVal}px"
                ) { t -> updateStyle(style.copy(sizeUnit = logSliderToUnit(t))) }

                "slot_shadow" -> PostLabeledSlider(
                    label = "Shadow",
                    value = shadow.strength.toFloat(),
                    range = 0f..100f,
                    display = if (shadow.strength == 0) "Off" else "${shadow.strength}"
                ) { v ->
                    val s = v.roundToInt().coerceIn(0, 100)
                    updateStyle(style.copy(shadow = if (s == 0) null else shadow.copy(strength = s)))
                }

                "slot_color" -> PaletteStrip(
                    currentHex = style.colorHex,
                    brandHex = brand.accentHex,
                    allowAuto = true,
                    onPick = { hex -> updateStyle(style.copy(colorHex = hex)) }
                )
            }
        } else {
            // One scrollable row of Ratio + Quick chips (Size, Shadow, Color, Bg photo, Panels, Safe zones)
            Row(
                Modifier.fillMaxWidth().horizontalScrollWithFade(scroll, PostPalette.Ink),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PostSize.values().forEach { s ->
                    CompactChip(
                        label = s.ratio,
                        selected = doc.size == s,
                        onClick = { onDoc(doc.copy(size = s)) }
                    )
                }
                Box(
                    Modifier
                        .width(1.dp)
                        .height(18.dp)
                        .background(PostPalette.Line)
                )
                CompactChip(
                    label = "Safe zones",
                    icon = Icons.Outlined.AspectRatio,
                    selected = showSafeZones,
                    onClick = onToggleSafeZones
                )
                CompactChip(
                    label = "Size ${(doc.textScale * 100).roundToInt()}%",
                    icon = Icons.Outlined.FormatSize,
                    selected = quickTarget == "size",
                    onClick = { onQuickTarget("size") }
                )
                CompactChip(
                    label = if (doc.shadowAll.strength == 0) "Shadow" else "Shadow ${doc.shadowAll.strength}",
                    icon = Icons.Outlined.WbShade,
                    selected = quickTarget == "shadow",
                    onClick = { onQuickTarget("shadow") }
                )
                CompactChip(
                    label = "Color",
                    icon = Icons.Outlined.Palette,
                    selected = quickTarget == "color",
                    onClick = { onQuickTarget("color") }
                )
                CompactChip(
                    label = "Bg photo",
                    icon = Icons.Outlined.Image,
                    selected = quickTarget == "photo",
                    onClick = { onQuickTarget("photo") }
                )
                CompactChip(
                    label = "Panels ${doc.panelOpacity}%",
                    icon = Icons.Outlined.Layers,
                    selected = quickTarget == "panels",
                    onClick = { onQuickTarget("panels") }
                )
            }

            when (quickTarget) {
                "size" -> PostLabeledSlider(
                    label = "All size",
                    value = doc.textScale.coerceIn(0.65f, 1.45f),
                    range = 0.65f..1.45f,
                    display = "${(doc.textScale * 100).roundToInt()}%"
                ) { onDoc(doc.copy(textScale = it)) }

                "shadow" -> PostLabeledSlider(
                    label = "Shadow",
                    value = doc.shadowAll.strength.toFloat(),
                    range = 0f..100f,
                    display = if (doc.shadowAll.strength == 0) "Auto" else "${doc.shadowAll.strength}"
                ) { onDoc(doc.copy(shadowAll = doc.shadowAll.copy(strength = it.roundToInt().coerceIn(0, 100)))) }

                "color" -> PaletteStrip(
                    currentHex = if (doc.manualAccent) doc.accentHex else null,
                    brandHex = brand.accentHex,
                    allowAuto = true,
                    onPick = { hex -> onDoc(doc.copy(accentHex = hex, manualAccent = hex != null)) }
                )

                "photo" -> {
                    val hasBg = !doc.backgroundPhotoUri.isNullOrBlank() || (doc.photoAsBackground && !doc.photoUri.isNullOrBlank())
                    if (!hasBg) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CompactButton(
                                label = "Add bg photo",
                                icon = Icons.Outlined.Image,
                                onClick = onPickBackgroundPhoto
                            )
                            if (!doc.photoUri.isNullOrBlank()) {
                                CompactChip(
                                    label = "Use frame photo",
                                    selected = doc.photoAsBackground,
                                    onClick = { onDoc(doc.copy(photoAsBackground = true, backgroundPhotoOpacity = 0.55f)) }
                                )
                            }
                        }
                    } else {
                        PostLabeledSlider(
                            label = "Bg photo",
                            value = doc.backgroundPhotoOpacity.coerceIn(0f, 1f),
                            range = 0f..1f,
                            display = "${(doc.backgroundPhotoOpacity * 100).roundToInt()}%"
                        ) { onDoc(doc.copy(backgroundPhotoOpacity = it)) }
                    }
                }

                "panels" -> PostLabeledSlider(
                    label = "Panels",
                    value = doc.panelOpacity.toFloat().coerceIn(0f, 100f),
                    range = 0f..100f,
                    display = "${doc.panelOpacity}%"
                ) { onDoc(doc.copy(panelOpacity = it.roundToInt().coerceIn(0, 100))) }
            }
        }
    }
}

/**
 * 3-snap-state bottom sheet above the bottom nav:
 * - 0 = PEEK: shows the tab row + thumbnail template strip.
 * - 1 = HALF: opens the selected tool tab in a 220dp panel so the preview stays large.
 * - 2 = FULL: opens the selected tool tab in a 360dp panel for multi-field editing.
 */
@Composable
private fun CollapsibleEditorSheet(
    snapState: Int,
    onSnapChange: (Int) -> Unit,
    activeTab: String,
    onTabSelect: (String) -> Unit,
    doc: PostDoc,
    onDoc: (PostDoc) -> Unit,
    brand: PostBrand,
    onBrand: (PostBrand) -> Unit = {},
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
    onOpenLibrary: () -> Unit,
    selectedFamily: String,
    onSelectFamily: (String) -> Unit,
    waitForPreviousTools: Boolean = false
) {
    val tabScroll = rememberScrollState()
    val tabs = listOf(
        Triple("template", "Template", Icons.Outlined.Dashboard),
        Triple("words", "Words", Icons.Outlined.TextFields),
        Triple("style", "Style", Icons.Outlined.Palette),
        Triple("photo", "Photo", Icons.Outlined.Image),
        Triple("icons", "Icons", Icons.Outlined.Category)
    )

    Surface(
        color = PostPalette.Surface,
        shape = RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, PostPalette.Line, RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp))
    ) {
        Column(Modifier.fillMaxWidth()) {
            // Drag handle + single row of tabs with horizontal edge fade
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .pointerInput(snapState) {
                        var totalDy = 0f
                        detectVerticalDragGestures(
                            onDragStart = { totalDy = 0f },
                            onVerticalDrag = { _, dragAmount ->
                                totalDy += dragAmount
                                if (totalDy < -24f && snapState < 2) {
                                    totalDy = 0f
                                    onSnapChange(snapState + 1)
                                } else if (totalDy > 24f && snapState > 0) {
                                    totalDy = 0f
                                    onSnapChange(snapState - 1)
                                }
                            }
                        )
                    }
                    .padding(top = 4.dp, bottom = 4.dp, start = 8.dp, end = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    Modifier
                        .width(36.dp)
                        .height(4.dp)
                        .clip(CircleShape)
                        .background(PostPalette.Line)
                        .clickable { onSnapChange((snapState + 1) % 3) }
                )
                Spacer(Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .horizontalScrollWithFade(tabScroll, PostPalette.Surface),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        tabs.forEach { (id, label, icon) ->
                            val isSelected = if (snapState == 0) id == "template" else activeTab == id
                            CompactChip(
                                label = label,
                                icon = icon,
                                selected = isSelected,
                                onClick = { onTabSelect(id) }
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(PostPalette.Raised)
                            .clickable { onSnapChange((snapState + 1) % 3) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (snapState == 2) Icons.Default.ExpandMore else Icons.Default.ExpandLess,
                            contentDescription = "Cycle sheet height",
                            tint = PostPalette.Text,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // PEEK state (0): shows the thumbnail TemplateStrip directly under the tab row
            if (snapState == 0) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    TemplateStrip(
                        doc = doc,
                        onDoc = onDoc,
                        library = library,
                        onOpenLibrary = onOpenLibrary,
                        brand = brand,
                        selectedFamily = selectedFamily,
                        onSelectFamily = onSelectFamily
                    )
                }
            } else {
                // HALF (1 = 220dp) or FULL (2 = 360dp) state
                val maxSheetH = if (snapState >= 2) 360.dp else 220.dp
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = maxSheetH)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    EditorTabs(
                        tab = activeTab,
                        onTab = onTabSelect,
                        doc = doc,
                        onDoc = onDoc,
                        brand = brand,
                        onBrand = onBrand,
                        library = library,
                        selectedSlot = selectedSlot,
                        onSelectSlot = onSelectSlot,
                        selectedIcon = selectedIcon,
                        onSelectIcon = onSelectIcon,
                        moveLogo = moveLogo,
                        onMoveLogo = onMoveLogo,
                        moveOn = moveOn,
                        onMove = onMove,
                        onPickPhoto = onPickPhoto,
                        onPickBackgroundPhoto = onPickBackgroundPhoto,
                        onPickLogo = onPickLogo,
                        onBringWords = onBringWords,
                        onOpenLibrary = onOpenLibrary,
                        selectedFamily = selectedFamily,
                        onSelectFamily = onSelectFamily,
                        showTabBar = false
                    )
                }
            }
        }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExportBottomSheet(
    doc: PostDoc,
    onDismiss: () -> Unit,
    onSave: (sizes: List<PostSize>, fourK: Boolean, share: Boolean) -> Unit,
    onSaveVideoFrame: ((sizes: List<PostSize>, fourK: Boolean) -> Unit)?
) {
    var allSizes by rememberSaveable { mutableStateOf(false) }
    var fourK by rememberSaveable { mutableStateOf(false) }

    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = PostPalette.Surface) {
        Column(
            Modifier.fillMaxWidth().padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("Save or share post", color = PostPalette.Text, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            PostSwitchRow("Export all 3 sizes (4:3, 9:16, 16:9)", allSizes) { allSizes = it }
            PostSwitchRow("Ultra-sharp 2x resolution", fourK) { fourK = it }

            val targetSizes = if (allSizes) PostSize.values().toList() else listOf(doc.size)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CompactButton(
                    label = "Save PNG",
                    primary = true,
                    modifier = Modifier.weight(1f),
                    onClick = { onSave(targetSizes, fourK, false) }
                )
                CompactButton(
                    label = "Share",
                    icon = Icons.Outlined.IosShare,
                    modifier = Modifier.weight(1f),
                    onClick = { onSave(targetSizes, fourK, true) }
                )
            }
            if (onSaveVideoFrame != null) {
                CompactButton(
                    label = "Save transparent video frame (CapCut)",
                    icon = Icons.Outlined.AspectRatio,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onSaveVideoFrame(targetSizes, fourK) }
                )
                Text(
                    text = PostFrameExport.hint(doc, doc.size),
                    color = PostPalette.Muted,
                    fontSize = 11.sp
                )
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}
