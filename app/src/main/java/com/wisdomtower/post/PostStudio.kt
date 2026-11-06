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
