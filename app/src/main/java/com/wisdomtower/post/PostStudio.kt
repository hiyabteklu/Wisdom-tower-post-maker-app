package com.wisdomtower.post

import android.content.Intent
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.Inbox
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
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
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
 * One-screen studio: title and size controls, pinned preview, pinned template strip, then ONE panel of controls, then the tool bar.
 * The tool bar (PostBottomBar.kt, EditorTool) picks the panel: Text (the message boxes, on start), Color, Font, Photo, Background, More.
 * Text holds one box per text slot with its own Color, Font and Size (docs/TEXT_STYLE.md) and the overall text size.
 * More holds the layer tools, logo, icons and footer as collapsed cards. Nothing was removed; each control moved to the panel that names it.
 * The tool bar is Scaffold's pinned bottom bar; the editing panel scrolls independently above it.
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
    onOpenLibrary: () -> Unit = {},
    onOpenBatch: () -> Unit = {},
    onOpenInbox: () -> Unit = {},
    embedded: Boolean = false
) {
    var moveLogoOn by remember { mutableStateOf(false) }
    var moveOn by remember { mutableStateOf(false) }
    var moveTarget by remember { mutableStateOf<String?>(null) }
    var footerItem by remember { mutableStateOf(0) }
    var selectedIcon by remember { mutableStateOf(0) }
    var showSave by remember { mutableStateOf(false) }
    var tool by rememberSaveable { mutableStateOf(EditorTool.TEXT) }

    val spec = postTemplate(doc.templateId)
    val layout = remember(doc.templateId, doc.size) { spec.layout(doc.size) }
    val slots = remember(doc.templateId, doc.size) {
        PostBatch.editableSlots(layout)
    }
    val hasMarks = remember(layout) { layout.slots.any { it.literal != null } }
    val photoArea = layout.photo

    PostTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize().imePadding(),
            contentWindowInsets = WindowInsets.safeDrawing,
            containerColor = PostPalette.Ink,
            // Use Scaffold's dedicated bottom-bar slot so the editor content is measured
            // above the toolbar instead of relying on manual Box alignment and a guessed height.
            // Always apply safeDrawing + imePadding so the tool bar stays above the system
            // navigation bar (fixes cropping when opened via Inbox → draft edit, which sets embedded=true).
            bottomBar = {
                PostToolBar(
                    selected = tool,
                    onSelect = { tool = it }
                )
            }
        ) { padding ->
            BoxWithConstraints(Modifier.fillMaxSize().padding(padding)) {
                val previewHeight = (maxHeight * 0.39f).coerceAtMost(350.dp)
                // The same options call every panel used before, written once.
                val options: @Composable (EditorTab, String) -> Unit = { tab, group ->
                    EditorOptions(tab, group, doc, onDoc, brand, onBrand,
                        photoArea != null, onPickPhoto, onPickBackgroundPhoto, onPickLogo,
                        moveLogoOn, { moveLogoOn = it }, footerItem, { footerItem = it })
                }

                Box(Modifier.fillMaxSize()) {
                    Column(Modifier.fillMaxSize()) {
                    TitleRow(doc, edited, bookmarked, onToggleBookmark, onOpenLibrary, onOpenBrand, onOpenBatch, onOpenInbox)
                    SizeRow(doc, onDoc, edited, onReset) { showSave = true }
                    PreviewPane(
                        doc, brand, moveLogoOn, onDoc,
                        iconEdit = doc.icons.isNotEmpty() && !moveOn && !moveLogoOn,
                        selectedIcon = selectedIcon,
                        layout = layout,
                        moveOn = moveOn,
                        moveTarget = moveTarget,
                        onTarget = { moveTarget = it },
                        modifier = Modifier.fillMaxWidth().height(previewHeight)
                    )
                    TemplateStrip(doc, brand, library, onDoc)
                    // A new scroll position for each panel, so a panel always opens at its top.
                    key(tool) {
                        Column(
                            Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            when (tool) {
                                EditorTool.TEXT -> {
                                    ToolCard("Type your words. Under each box, tap Color, Font or Size to style just that box.") {
                                        slots.forEach { slot ->
                                            ControlHeading(PostSlots.label(slot))
                                            WordBox(slot, doc, onDoc, brand)
                                        }
                                        OtherTextStyles(doc, onDoc, brand, hasMarks)
                                        if (onBringWords != null) {
                                            OutlinedButton(onClick = onBringWords, modifier = Modifier.fillMaxWidth()) {
                                                Text("Bring my words from the previous template")
                                            }
                                        }
                                        InlineEditorSlider(EditorSlider("Text size, all words", doc.textScale, 0.7f..1.5f,
                                            "${(doc.textScale * 100).toInt()}%") { onDoc(doc.copy(textScale = it)) })
                                    }
                                }

                                EditorTool.COLOR -> {
                                    ToolCard("Color pairs, the accent color and the default dark text color. A single box can have its own color in Text.") {
                                        listOf("pairs" to "Color pairs", "main" to "Accent color", "text" to "Default dark text color").forEach { (group, label) ->
                                            ControlHeading(label)
                                            options(EditorTab.LOOK, group)
                                        }
                                    }
                                }

                                EditorTool.FONT -> {
                                    ToolCard("The fonts for the whole post. To change one box only, use Font under that box in Text. Add your own .ttf or .otf files here.") {
                                        ControlHeading("English")
                                        options(EditorTab.FONT, "en")
                                        ControlHeading("Amharic")
                                        options(EditorTab.FONT, "am")
                                    }
                                }

                                EditorTool.PHOTO -> {
                                    val groups = editorGroups(EditorTab.PHOTO, doc, slots, photoArea != null, false)
                                        .filterNot { it.key == "bgimg" || it.key == "bgop" }
                                    ToolCard("The template photo: add it, zoom, and move it up, down, left or right.") {
                                        if (groups.isEmpty()) {
                                            Text("This template has no photo area. For a full-canvas picture, use Background.", color = PostPalette.Muted, fontSize = 13.sp)
                                        }
                                        groups.forEach { group ->
                                            ControlHeading(group.label)
                                            val slider = editorSlider(EditorTab.PHOTO, group.key, doc, photoArea?.dim, onDoc)
                                            if (slider != null) InlineEditorSlider(slider)
                                            options(EditorTab.PHOTO, group.key)
                                        }
                                    }
                                }

                                EditorTool.BACKGROUND -> {
                                    ToolCard("The template background: a solid color, a gradient, or a full-canvas picture behind everything.") {
                                        listOf("bg" to "Background type", "bgfrom" to "Solid color / gradient start", "bgto" to "Gradient end", "bgdir" to "Gradient direction").forEach { (group, label) ->
                                            if (group != "bgfrom" || doc.bgMode == BgMode.SOLID || doc.bgMode == BgMode.GRADIENT) {
                                                if (group != "bgto" && group != "bgdir" || doc.bgMode == BgMode.GRADIENT) {
                                                    ControlHeading(label)
                                                    options(EditorTab.LOOK, group)
                                                }
                                            }
                                        }

                                        Spacer(Modifier.height(4.dp))
                                        ControlHeading("Full-canvas background image")
                                        Text(
                                            "This image sits behind the entire design. Lower opacity to blend it with the template colors.",
                                            color = PostPalette.Muted,
                                            fontSize = 12.sp
                                        )
                                        OutlinedButton(onClick = onPickBackgroundPhoto, modifier = Modifier.fillMaxWidth()) {
                                            Text(if (doc.backgroundPhotoUri == null) "Add background image" else "Replace background image")
                                        }
                                        if (doc.backgroundPhotoUri != null) {
                                            InlineEditorSlider(
                                                EditorSlider(
                                                    "Background image opacity",
                                                    doc.backgroundPhotoOpacity,
                                                    0f..1f,
                                                    "${(doc.backgroundPhotoOpacity * 100).toInt()}%"
                                                ) { onDoc(doc.copy(backgroundPhotoOpacity = it)) }
                                            )
                                            OutlinedButton(
                                                onClick = { onDoc(doc.copy(backgroundPhotoUri = null, backgroundPhotoOpacity = 1f)) },
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Text("Remove background image")
                                            }
                                        }
                                    }
                                }

                                EditorTool.MORE -> {
                                    SettingsSection("Layers and style", "Photo frames, gradient blends, a cut-out subject and moving words.") {
                                        EngineControls(
                                            doc, onDoc, slots, photoArea != null, moveOn,
                                            { on ->
                                                moveOn = on
                                                if (!on) moveTarget = null
                                            }
                                        )
                                    }

                                    SettingsSection("Layer manager", "Select an element here, then move or resize it directly on the poster preview.") {
                                        ControlHeading("Text layers")
                                        slots.forEach { slot ->
                                            Row(
                                                Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(PostSlots.label(slot) + if (slot in doc.hiddenTextSlots) " · Hidden" else "", color = PostPalette.Text, fontSize = 13.sp, modifier = Modifier.weight(1f))
                                                OutlinedButton(
                                                    onClick = {
                                                        moveLogoOn = false
                                                        moveOn = true
                                                        moveTarget = slot
                                                    }
                                                ) { Text(if (moveOn && moveTarget == slot) "Selected" else "Select") }
                                                OutlinedButton(onClick = {
                                                    val hidden = doc.hiddenTextSlots.toMutableSet()
                                                    if (slot in hidden) hidden.remove(slot) else hidden.add(slot)
                                                    onDoc(doc.copy(hiddenTextSlots = hidden))
                                                    if (slot in hidden && moveTarget == slot) { moveOn = false; moveTarget = null }
                                                }) { Text(if (slot in doc.hiddenTextSlots) "Show" else "Hide") }
                                            }
                                        }

                                        if (doc.icons.isNotEmpty()) {
                                            ControlHeading("Graphic layers")
