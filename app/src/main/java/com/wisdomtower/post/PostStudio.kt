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
import androidx.compose.foundation.layout.navigationBarsPadding
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
    embedded: Boolean = false,
    headerTitle: String? = null,
    onHeaderApplyAll: (() -> Unit)? = null,
    onHeaderDone: (() -> Unit)? = null
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
            bottomBar = {
                // A full-screen Dialog does not automatically keep custom bottom bars
                // above the gesture/navigation area. The activity's normal editor does.
                PostToolBar(
                    selected = tool,
                    onSelect = { tool = it },
                    modifier = if (embedded) Modifier.navigationBarsPadding() else Modifier
                )
            }
        ) { padding ->
            BoxWithConstraints(Modifier.fillMaxSize().padding(padding)) {
                val previewHeight = (maxHeight * 0.28f).coerceIn(140.dp, 220.dp)
                // The same options call every panel used before, written once.
                val options: @Composable (EditorTab, String) -> Unit = { tab, group ->
                    EditorOptions(tab, group, doc, onDoc, brand, onBrand,
                        photoArea != null, onPickPhoto, onPickBackgroundPhoto, onPickLogo,
                        moveLogoOn, { moveLogoOn = it }, footerItem, { footerItem = it })
                }

                Box(Modifier.fillMaxSize()) {
                    Column(Modifier.fillMaxSize()) {
                        TitleRow(
                            doc, edited, bookmarked, onToggleBookmark, onOpenLibrary,
                            onOpenBrand, onOpenBatch, onOpenInbox,
                            headerTitle, onHeaderApplyAll, onHeaderDone
                        )
                        SizeRow(doc, onDoc, edited, onReset) { showSave = true }

                        // Preview, template strip and editor controls share one scroll area.
                        // The title/size rows and bottom tool bar remain pinned.
                        key(tool) {
                            Column(
                                Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
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
                                            doc.icons.forEachIndexed { index, item ->
                                                Row(
                                                    Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        "${index + 1}. ${PostIconStore.titleOf(item)}" + if (item.visible) "" else " · Hidden",
                                                        color = PostPalette.Text,
                                                        fontSize = 13.sp,
                                                        modifier = Modifier.weight(1f)
                                                    )
                                                    OutlinedButton(
                                                        onClick = {
                                                            selectedIcon = index
                                                            moveOn = false
                                                            moveTarget = null
                                                            moveLogoOn = false
                                                        }
                                                    ) { Text(if (selectedIcon == index) "Selected" else "Select") }
                                                    OutlinedButton(onClick = {
                                                        onDoc(doc.copy(icons = doc.icons.mapIndexed { i, old -> if (i == index) old.copy(visible = !old.visible) else old }))
                                                    }) { Text(if (item.visible) "Hide" else "Show") }
                                                }
                                            }
                                        }

                                        if (doc.cutoutUri != null) {
                                            Row(
                                                Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text("Cut-out subject", color = PostPalette.Text, fontSize = 13.sp)
                                                OutlinedButton(
                                                    onClick = {
                                                        moveLogoOn = false
                                                        moveOn = true
                                                        moveTarget = PostEngine.CUTOUT_TARGET
                                                    }
                                                ) { Text(if (moveOn && moveTarget == PostEngine.CUTOUT_TARGET) "Selected" else "Select") }
                                            }
                                        }

                                        if (doc.showLogo && brand.logoPath != null) {
                                            Row(
                                                Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text("Brand logo", color = PostPalette.Text, fontSize = 13.sp)
                                                OutlinedButton(
                                                    onClick = {
                                                        moveOn = false
                                                        moveTarget = null
                                                        moveLogoOn = true
                                                    }
                                                ) { Text(if (moveLogoOn) "Selected" else "Select") }
                                            }
                                        }

                                        if (doc.photoUri != null) {
                                            Text("Template photo · adjust it in Photo", color = PostPalette.Muted, fontSize = 12.sp)
                                        }
                                        if (doc.backgroundPhotoUri != null) {
                                            Text("Full-canvas background · adjust it in Background", color = PostPalette.Muted, fontSize = 12.sp)
                                        }
                                        OutlinedButton(
                                            onClick = {
                                                moveOn = false
                                                moveTarget = null
                                                moveLogoOn = false
                                            },
                                            modifier = Modifier.fillMaxWidth()
                                        ) { Text("Finish layer selection") }
                                    }

                                    SettingsSection("Logo", "Control the logo on this post.") {
                                        editorGroups(EditorTab.LOGO, doc, slots, photoArea != null, false).forEach { group ->
                                            ControlHeading(group.label)
                                            if (group.key == "size") {
                                                InlineEditorSlider(EditorSlider("Logo size", doc.logoSize, 0.4f..3f,
                                                    "${(doc.logoSize * 100).toInt()}%") { onDoc(doc.copy(logoSize = it)) })
                                            } else options(EditorTab.LOGO, group.key)
                                        }
                                    }

                                    SettingsSection("Icons", "Add graphics, select one, then drag or pinch it on the preview.") {
                                        editorGroups(EditorTab.ICONS, doc, slots, photoArea != null, false).forEach { group ->
                                            ControlHeading(group.label)
                                            if (group.key == "size") {
                                                val slider = iconSizeSlider(doc, selectedIcon, onDoc)
                                                if (slider != null) InlineEditorSlider(slider)
                                            }
                                            IconOptions(group.key, doc, onDoc, selectedIcon) { selectedIcon = it }
                                        }
                                    }

                                    SettingsSection("Footer", "Social icons shown on the post. The footer text color is under Text, Other text.") {
                                        editorGroups(EditorTab.FOOTER, doc, slots, photoArea != null, false).forEach { group ->
                                            ControlHeading(group.label)
                                            if (group.key == "size") {
                                                InlineEditorSlider(EditorSlider("Footer icon size", brand.iconScale, 0.5f..1.8f,
                                                    "${(brand.iconScale * 100).toInt()}%") { onBrand(brand.copy(iconScale = it)) })
                                            } else options(EditorTab.FOOTER, group.key)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    }
                }
            }
        }
        if (showSave) SaveDialog(doc, brand) { showSave = false }
    }
}

/** A card that holds one tool panel. It is always open; the hint line explains the panel. */
@Composable
private fun ToolCard(hint: String?, content: @Composable ColumnScope.() -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = PostPalette.Surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            if (hint != null) Text(hint, color = PostPalette.Muted, fontSize = 12.sp)
            content()
        }
    }
}

/**
 * A card with a tappable header, used in the More panel. Collapsed it is one 48 dp row.
 * Its open or closed state is kept per title, also across a screen turn. defaultOpen is only the state the first time.
 */
@Composable
private fun SettingsSection(
    title: String,
    subtitle: String? = null,
    defaultOpen: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    var open by rememberSaveable(title) { mutableStateOf(defaultOpen) }
    Card(
        colors = CardDefaults.cardColors(containerColor = PostPalette.Surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.fillMaxWidth()) {
            Row(
                Modifier.fillMaxWidth().clickable { open = !open }.padding(horizontal = 14.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, color = PostPalette.Text, fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Icon(
                    if (open) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore,
                    contentDescription = if (open) "Close $title" else "Open $title",
                    tint = PostPalette.Muted
                )
            }
            if (open) {
                Column(
                    Modifier.fillMaxWidth().padding(start = 14.dp, end = 14.dp, bottom = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (subtitle != null) Text(subtitle, color = PostPalette.Muted, fontSize = 12.sp)
                    content()
                }
            }
        }
    }
}

@Composable
private fun ControlHeading(label: String) {
    Text(label, color = PostPalette.Text, fontSize = 13.sp, fontWeight = FontWeight.Bold)
}

@Composable
private fun InlineEditorSlider(slider: EditorSlider) {
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(slider.label, color = PostPalette.Text, fontSize = 13.sp, modifier = Modifier.weight(1f))
            Text(slider.display, color = PostPalette.Muted, fontSize = 13.sp)
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
    onOpenBrand: () -> Unit,
    onOpenBatch: () -> Unit,
    onOpenInbox: () -> Unit,
    headerTitle: String?,
    onHeaderApplyAll: (() -> Unit)?,
    onHeaderDone: (() -> Unit)?
) {
    Row(
        Modifier.fillMaxWidth().padding(start = 10.dp, top = 1.dp, end = 6.dp, bottom = 1.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            headerTitle ?: ((if (edited) "● " else "") + postTemplate(doc.templateId).name),
            fontWeight = FontWeight.Bold,
            fontSize = if (headerTitle != null) 13.sp else 16.sp,
            color = PostPalette.Text,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        if (headerTitle != null) {
            if (onHeaderApplyAll != null) {
                OutlinedButton(
                    onClick = onHeaderApplyAll,
                    contentPadding = PaddingValues(horizontal = 7.dp, vertical = 0.dp)
                ) { Text("Apply all", fontSize = 11.sp, maxLines = 1) }
            }
            if (onHeaderDone != null) {
                Button(
                    onClick = onHeaderDone,
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp)
                ) { Text("Done", fontSize = 11.sp, maxLines = 1) }
            }
        } else {
            IconButton(onClick = onToggleBookmark, modifier = Modifier.size(36.dp)) {
                Icon(
                    if (bookmarked) Icons.Outlined.Bookmark else Icons.Outlined.BookmarkBorder,
                    contentDescription = if (bookmarked) "Remove from Saved" else "Save this template",
                    tint = if (bookmarked) PostPalette.Gold else PostPalette.Muted
                )
            }
            IconButton(onClick = onOpenLibrary, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Outlined.GridView, contentDescription = "Library", tint = PostPalette.Mint)
            }
            IconButton(onClick = onOpenBrand, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Outlined.Palette, contentDescription = "Brand and social icons", tint = PostPalette.Mint)
            }
            IconButton(onClick = onOpenBatch, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Outlined.Layers, contentDescription = "Batch", tint = PostPalette.Mint)
            }
            IconButton(onClick = onOpenInbox, modifier = Modifier.size(36.dp)) {
                Box {
                    Icon(Icons.Outlined.Inbox, contentDescription = "Poster inbox", tint = PostPalette.Mint)
                    if (PostInboxState.drafts > 0) {
                        Box(Modifier.align(Alignment.TopEnd).size(8.dp).clip(CircleShape).background(PostPalette.Gold))
                    }
                }
            }
        }
    }
}

/** The size chips scroll inside the free space, so four sizes plus Reset and Save still fit on a narrow phone. Reset and Save stay pinned on the right. */
@Composable
private fun SizeRow(doc: PostDoc, onDoc: (PostDoc) -> Unit, edited: Boolean, onReset: () -> Unit, onSave: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            Modifier.weight(1f).horizontalScroll(rememberScrollState()),
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
        }
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
    layout: TemplateLayout,
    moveOn: Boolean,
    moveTarget: String?,
    onTarget: (String?) -> Unit,
    modifier: Modifier
) {
    val liveDoc by rememberUpdatedState(doc)
    val liveOnDoc by rememberUpdatedState(onDoc)
    val liveSelected by rememberUpdatedState(selectedIcon)
    val liveLayout by rememberUpdatedState(layout)
    val liveTarget by rememberUpdatedState(moveTarget)
    val liveOnTarget by rememberUpdatedState(onTarget)
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
                            if (moveOn && moveTarget != null) {
                                if (moveTarget == PostEngine.CUTOUT_TARGET) {
                                    drawCircle(
                                        color = Color.White,
                                        radius = 16.dp.toPx(),
                                        center = Offset(doc.cutoutX * size.width, doc.cutoutY * size.height),
                                        style = Stroke(width = 3.dp.toPx())
                                    )
                                } else {
                                    layout.slots.firstOrNull { it.slot == moveTarget && it.literal == null }?.let { b ->
                                        val o = doc.offsetFor(b.slot)
                                        val left = (b.x + (o?.dx ?: 0f)) * size.width
                                        val top = (b.y + (o?.dy ?: 0f)) * size.height
                                        drawRect(
                                            color = Color.White,
                                            topLeft = Offset(left, top),
                                            size = Size(b.w * size.width, b.h * size.height),
                                            style = Stroke(width = 2.dp.toPx())
                                        )
                                    }
                                }
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
                        .pointerInput(moveOn) {
                            if (moveOn) {
                                // A tap picks the words under the finger, or the cut-out subject when no words are there.
                                detectTapGestures { p ->
                                    val d = liveDoc
                                    val hit = PostEngine.hitSlot(liveLayout, d, p.x / size.width, p.y / size.height)
                                        ?: (if (d.cutoutUri != null) PostEngine.CUTOUT_TARGET else null)
                                    liveOnTarget(hit)
                                }
                            }
                        }
                        .pointerInput(moveOn) {
                            if (moveOn) {
                                // Drag moves what was picked; pinch resizes the cut-out subject.
                                var last: PostDoc? = null
                                var lastAt = 0L
                                detectTransformGestures { _, pan, zoom, _ ->
                                    val target = liveTarget
                                    if (target != null) {
                                        val now = System.nanoTime()
                                        val base = if (last != null && now - lastAt < 150_000_000L) last!! else liveDoc
                                        val dx = pan.x / size.width
                                        val dy = pan.y / size.height
                                        val next = if (target == PostEngine.CUTOUT_TARGET) {
                                            base.copy(
                                                cutoutX = (base.cutoutX + dx).coerceIn(0f, 1f),
                                                cutoutY = (base.cutoutY + dy).coerceIn(0.2f, 1.2f),
                                                cutoutScale = (base.cutoutScale * zoom).coerceIn(0.3f, 2.5f)
                                            )
                                        } else {
                                            val cur = base.offsetFor(target) ?: SlotOffset(0f, 0f)
                                            base.withOffset(target, SlotOffset((cur.dx + dx).coerceIn(-1f, 1f), (cur.dy + dy).coerceIn(-1f, 1f)))
                                        }
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

/**
 * The Save sheet: a row of sizes (tick any of them), Standard or 4K, a row of formats, then Save and Save and share.
 * On the two phone mockup templates it also shows "Save frame for video" (PostFrameExport.kt): a PNG with a see-through
 * screen, to lay over a screen recording in a video editor. That button uses the sizes and Standard or 4K chosen above.
 */
@Composable
private fun SaveDialog(doc: PostDoc, brand: PostBrand, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var options by remember(doc.size) { mutableStateOf(PostStore.loadSaveOptions(context, doc.size)) }
    val chosen = options.sizes
    val png = options.png
    val fourK = options.fourK
    var busy by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf<String?>(null) }

    fun runExport(share: Boolean) {
        val sizes = PostSize.values().filter { it in chosen }.ifEmpty { listOf(doc.size) }
        PostStore.saveSaveOptions(context, options)
        busy = true
        status = null
        scope.launch {
            val outcome = runCatching { withContext(Dispatchers.Default) { PostExport.save(context, doc, brand, sizes, png, fourK) } }
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

    fun runFrameExport() {
        val sizes = PostSize.values().filter { it in chosen }.ifEmpty { listOf(doc.size) }
        PostStore.saveSaveOptions(context, options)
        busy = true
        status = null
        scope.launch {
            val outcome = runCatching { withContext(Dispatchers.Default) { PostFrameExport.save(context, doc, brand, sizes, fourK) } }
            busy = false
            val saved = outcome.getOrNull()
            status = if (saved == null) {
                "Export failed: " + (outcome.exceptionOrNull()?.localizedMessage ?: "unknown error")
            } else {
                "Saved " + saved.size + " frame file(s) to Pictures/Wisdom Tower Post\n" + PostFrameExport.hint(doc, sizes.first())
            }
        }
    }

    Dialog(onDismissRequest = { if (!busy) onDismiss() }) {
        Card(colors = CardDefaults.cardColors(containerColor = PostPalette.Surface), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Save", fontWeight = FontWeight.Bold, color = PostPalette.Text)
                PostSmallLabel("Tick the sizes you want. Every size is laid out again, not stretched.")
                Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PostSize.values().forEach { size ->
                        FilterChip(
                            selected = size in chosen,
                            onClick = { options = options.copy(sizes = if (size in chosen) chosen - size else chosen + size) },
                            label = { Text(size.label) }
                        )
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = !fourK, onClick = { options = options.copy(fourK = false) }, label = { Text("Standard") })
                    FilterChip(selected = fourK, onClick = { options = options.copy(fourK = true) }, label = { Text("4K") })
                }
                PostSmallLabel(
                    if (fourK) "4K is twice the pixels: 2160 wide (2880 for 4:3). Bigger files, best for LinkedIn, Telegram files and keeping masters."
                    else "Standard is 1080 wide (1440 for 4:3), the size TikTok and Instagram use."
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = png, onClick = { options = options.copy(png = true) }, label = { Text("PNG") })
                    FilterChip(selected = !png, onClick = { options = options.copy(png = false) }, label = { Text("JPG") })
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
                if (PostFrameExport.isDeviceTemplate(doc.templateId)) {
                    PostSmallLabel("For video: saves only the phone and background as a PNG with a see-through screen, to lay over your screen recording in CapCut.")
                    OutlinedButton(onClick = { runFrameExport() }, enabled = !busy, modifier = Modifier.fillMaxWidth()) {
                        Text("Save frame for video")
                    }
                }
                status?.let { Text(it, fontSize = 12.sp, color = PostPalette.Muted) }
                TextButton(onClick = onDismiss, enabled = !busy, modifier = Modifier.align(Alignment.End)) { Text("Close") }
            }
        }
    }
}
