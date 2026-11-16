package com.wisdomtower.post

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudSync
import androidx.compose.material.icons.outlined.Collections
import androidx.compose.material.icons.outlined.FactCheck
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.coroutines.yield

/**
 * Owns the open post, the brand and the library, and switches between Studio, Library, Batch/Inbox, Brand, and Settings
 * using the persistent 56dp bottom navigation bar (PostAppBottomNav).
 */
@Composable
fun PostApp() {
    val context = LocalContext.current
    val startup = remember { PostDrafts.startup(context) }
    var doc by remember { mutableStateOf(startup.first) }
    var library by remember { mutableStateOf(startup.second) }
    var brand by remember { mutableStateOf(PostStore.loadBrand(context)) }
    var screen by rememberSaveable { mutableStateOf("studio") }
    var remoteBatch by remember { mutableStateOf<PostChatBatch?>(null) }
    var inboxBackTo by rememberSaveable { mutableStateOf("studio") }
    var libSize by remember { mutableStateOf(startup.first.size) }
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val undoStack = remember { mutableStateListOf<PostDoc>() }

    /** The last post that was written to its draft file (or opened), so opening a template does not count as an edit. */
    val persisted = remember { arrayOf(startup.first) }

    // The brand (and, as a fallback, the last post) stays in the old preferences file.
    LaunchedEffect(doc, brand) {
        delay(800)
        PostStore.save(context, doc, brand)
    }

    // Autosave the draft of the open template shortly after the last change. No Save button for drafts.
    LaunchedEffect(doc) {
        if (doc == persisted[0]) return@LaunchedEffect
        delay(400)
        val snapshot = doc
        library = withContext(Dispatchers.IO) { PostDrafts.persist(context, snapshot, library) }
        persisted[0] = snapshot
    }

    // Inbox dot: show the cached count at once (drafts plus approved batches not posted yet), then refresh quietly.
    LaunchedEffect(Unit) {
        PostInboxState.countsFrom(PostChatStore.loadCachedBatches(context))
        delay(1200)
        runCatching { PostInboxState.update(PostChatApi.listBatches(context)) }
    }

    // Warm the Library thumbnails (disk cache) one by one in the background, after the first screen has settled.
    LaunchedEffect(Unit) {
        delay(2500)
        withContext(Dispatchers.IO) {
            PostTemplates.forEach { spec ->
                PostThumbs.get(context, spec, libSize, brand, library.edited[spec.id])
                yield()
            }
        }
    }

    fun recordUndo(previous: PostDoc) {
        if (undoStack.lastOrNull() != previous) {
            undoStack.add(previous)
            if (undoStack.size > 25) {
                undoStack.removeAt(0)
            }
        }
    }

    fun openTemplate(id: String) {
        val old = doc
        recordUndo(old)
        var lib = PostDrafts.persist(context, old, library)
        val next = PostDrafts.loadDraft(context, id) ?: PostDrafts.factory(id, old.size)
        lib = lib.copy(
            lastTemplate = postTemplate(id).id,
            lastWords = if (PostDrafts.isEdited(old)) old.texts else lib.lastWords
        )
        PostDrafts.saveLibrary(context, lib)
        library = lib
        persisted[0] = next
        doc = next
    }

    fun updateDoc(next: PostDoc) {
        if (next.templateId != doc.templateId) {
            openTemplate(next.templateId)
        } else if (next != doc) {
            recordUndo(doc)
            doc = next
        }
    }

    fun performUndo() {
        val previous = undoStack.removeLastOrNull() ?: return
        doc = previous
    }

    /** Writes the open post to its draft right away, so the Library shows it as you left it. */
    fun saveOpenDraft() {
        val lib = PostDrafts.persist(context, doc, library)
        library = lib
        persisted[0] = doc
    }

    fun toggleBookmark(id: String) {
        val marks = if (id in library.bookmarks) library.bookmarks - id else library.bookmarks + id
        val lib = library.copy(bookmarks = marks)
        PostDrafts.saveLibrary(context, lib)
        library = lib
    }

    fun resetCurrent() {
        val before = doc
        recordUndo(before)
        val fresh = PostDrafts.factory(before.templateId, before.size)
        PostDrafts.clearDraft(context, before.templateId)
        val lib = library.copy(edited = library.edited - before.templateId)
        PostDrafts.saveLibrary(context, lib)
        library = lib
        persisted[0] = fresh
        doc = fresh
        snackbar.currentSnackbarData?.dismiss()
        scope.launch {
            val result = withTimeoutOrNull(8000L) {
                snackbar.showSnackbar("Template reset", actionLabel = "Undo", duration = SnackbarDuration.Indefinite)
            }
            if (result == SnackbarResult.ActionPerformed) {
                val restored = withContext(Dispatchers.IO) { PostDrafts.persist(context, before, library) }
                library = restored
                if (doc.templateId == before.templateId) {
                    persisted[0] = before
                    doc = before
                }
            } else {
                val open = doc
                withContext(Dispatchers.IO) { PostDrafts.releaseFiles(context, before, open) }
            }
        }
    }

    val bringWords: () -> Unit = {
        updateDoc(doc.copy(texts = postTemplate(doc.templateId).samples + library.lastWords))
    }

    val logoPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            val path = PostStore.importLogo(context, uri)
            if (path != null) brand = brand.copy(logoPath = path, logoStamp = System.currentTimeMillis())
            else Toast.makeText(context, "That file is not a readable image", Toast.LENGTH_LONG).show()
        }
    }
    val backgroundPhotoPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            val stored = PostStore.importPhoto(context, uri)
            if (stored != null) updateDoc(doc.copy(backgroundPhotoUri = stored, backgroundPhotoOpacity = 1f))
            else Toast.makeText(context, "That file is not a readable image", Toast.LENGTH_LONG).show()
        }
    }

    val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            val stored = PostStore.importPhoto(context, uri)
            if (stored != null) updateDoc(doc.copy(photoUri = stored, photoZoom = 1f, photoFocusX = 0.5f, photoFocusY = 0.5f))
            else Toast.makeText(context, "That file is not a readable image", Toast.LENGTH_LONG).show()
        }
    }

    PostTheme {
        Column(Modifier.fillMaxSize().background(PostPalette.Ink)) {
            Box(Modifier.weight(1f).fillMaxWidth()) {
                when (screen) {
                    "library" -> {
                        BackHandler { screen = "studio" }
                        PostLibraryScreen(
                            brand = brand,
                            library = library,
                            size = libSize,
                            onSize = { libSize = it },
                            onOpen = { id ->
                                val wasEdited = id in library.edited
                                if (id != doc.templateId) openTemplate(id)
                                if (!wasEdited && doc.size != libSize) doc = doc.copy(size = libSize)
                                screen = "studio"
                            },
                            onToggleBookmark = { toggleBookmark(it) },
                            onBack = { screen = "studio" }
                        )
                    }
                    "batch" -> {
                        BackHandler { screen = if (remoteBatch != null) "inbox" else "studio"; remoteBatch = null }
                        val imported = remoteBatch
                        val batchDoc = imported?.let { batch ->
                            doc.copy(templateId = batch.template, texts = postTemplate(batch.template).samples)
                        } ?: doc
                        PostBatchScreen(doc = batchDoc, brand = brand, initialBatch = imported, onBack = {
                            remoteBatch = null
                            screen = if (imported != null) "inbox" else "studio"
                        })
                    }
                    "chat" -> {
                        BackHandler { screen = "settings" }
                        PostChatScreen(onBack = { screen = "settings" }, onOpenInbox = { inboxBackTo = "chat"; screen = "inbox" })
                    }
                    "inbox" -> {
                        BackHandler { screen = inboxBackTo }
                        PostChatInboxScreen(
                            onBack = { screen = inboxBackTo },
                            onOpenBatch = { batch ->
                                if (postTemplate(batch.template).id != batch.template) {
                                    Toast.makeText(context, "This app does not know template ${batch.template}", Toast.LENGTH_LONG).show()
                                } else {
                                    remoteBatch = batch
                                    screen = "batch"
                                }
                            },
                            onOpenQa = { screen = "qa" }
                        )
                    }
                    "qa" -> {
                        BackHandler { screen = "inbox" }
                        PostQaScreen(brand = brand, onBack = { screen = "inbox" })
                    }
                    "brand" -> {
                        BackHandler { screen = "studio" }
                        PostBrandScreen(
                            brand = brand,
                            onBrand = { brand = it },
                            onPickLogo = { logoPicker.launch(arrayOf("image/*")) },
                            onBack = { screen = "studio" },
                            onOpenChat = { screen = "chat" }
                        )
                    }
                    "settings" -> {
                        BackHandler { screen = "studio" }
                        PostSettingsScreen(
                            doc = doc,
                            onDoc = { updateDoc(it) },
                            brand = brand,
                            onBrand = { brand = it },
                            onOpenChat = { screen = "chat" },
                            onOpenQa = { screen = "qa" },
                            onOpenBatch = { screen = "batch" },
                            onOpenInbox = { inboxBackTo = "settings"; screen = "inbox" },
                            onOpenBrand = { screen = "brand" }
                        )
                    }
                    else -> PostStudioScreen(
                        doc = doc,
                        onDoc = { updateDoc(it) },
                        brand = brand,
                        library = library,
                        onBrand = { brand = it },
                        onPickPhoto = { photoPicker.launch(arrayOf("image/*")) },
                        onPickBackgroundPhoto = { backgroundPhotoPicker.launch(arrayOf("image/*")) },
                        onPickLogo = { logoPicker.launch(arrayOf("image/*")) },
                        onOpenBrand = { screen = "brand" },
                        edited = PostDrafts.isEdited(doc),
                        onReset = { resetCurrent() },
                        onBringWords = if (library.lastWords.isNotEmpty()) bringWords else null,
                        bookmarked = doc.templateId in library.bookmarks,
                        onToggleBookmark = { toggleBookmark(doc.templateId) },
                        onOpenLibrary = {
                            saveOpenDraft()
                            libSize = doc.size
                            screen = "library"
                        },
                        onOpenBatch = { screen = "batch" },
                        onOpenInbox = { inboxBackTo = "studio"; screen = "inbox" },
                        onOpenQa = { screen = "qa" },
                        canUndo = undoStack.isNotEmpty(),
                        onUndo = { performUndo() }
                    )
                }
                SnackbarHost(snackbar, Modifier.align(Alignment.BottomCenter).padding(bottom = 8.dp))
            }

            // Persistent 56dp Bottom Nav: Studio, Library, Batch/Inbox, Brand, Settings
            PostAppBottomNav(
                currentScreen = screen,
                inboxBadgeCount = PostInboxState.drafts,
                onSelect = { tab ->
                    when (tab) {
                        AppNavTab.STUDIO -> screen = "studio"
                        AppNavTab.LIBRARY -> {
                            saveOpenDraft()
                            libSize = doc.size
                            screen = "library"
                        }
                        AppNavTab.INBOX -> {
                            inboxBackTo = "studio"
                            screen = "inbox"
                        }
                        AppNavTab.BRAND -> screen = "brand"
                        AppNavTab.SETTINGS -> screen = "settings"
                    }
                }
            )
        }
    }
}

@Composable
private fun PostSettingsScreen(
    doc: PostDoc,
    onDoc: (PostDoc) -> Unit,
    brand: PostBrand,
    onBrand: (PostBrand) -> Unit,
    onOpenChat: () -> Unit,
    onOpenQa: () -> Unit,
    onOpenBatch: () -> Unit,
    onOpenInbox: () -> Unit,
    onOpenBrand: () -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(PostPalette.Ink)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "Settings & Tools",
            color = PostPalette.Text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        // Quick tools card
        Column(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(PostPalette.Surface)
                .border(1.dp, PostPalette.Line, RoundedCornerShape(10.dp))
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PostSmallLabel("Workspace & Quality Tools")
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                CompactButton(
                    label = "Poster Inbox",
                    icon = Icons.Outlined.Inbox,
                    modifier = Modifier.weight(1f),
                    onClick = onOpenInbox
                )
                CompactButton(
                    label = "Batch Maker",
                    icon = Icons.Outlined.Collections,
                    modifier = Modifier.weight(1f),
                    onClick = onOpenBatch
                )
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                CompactButton(
                    label = "Server & Chat Link",
                    icon = Icons.Outlined.CloudSync,
                    modifier = Modifier.weight(1f),
                    onClick = onOpenChat
                )
                CompactButton(
                    label = "Template QA",
                    icon = Icons.Outlined.FactCheck,
                    modifier = Modifier.weight(1f),
                    onClick = onOpenQa
                )
            }
            CompactButton(
                label = "Brand Colors & Logo",
                icon = Icons.Outlined.Palette,
                modifier = Modifier.fillMaxWidth(),
                onClick = onOpenBrand
            )
        }

        // Fonts section
        FontsSection(doc = doc, onDoc = onDoc)

        // Social footer defaults
        Column(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(PostPalette.Surface)
                .border(1.dp, PostPalette.Line, RoundedCornerShape(10.dp))
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Social Footer Bar",
                color = PostPalette.Text,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            PostSwitchRow("Show logo header on this post", doc.showLogo) { onDoc(doc.copy(showLogo = it)) }
            PostSwitchRow("Show social footer on this post", doc.showFooter) { onDoc(doc.copy(showFooter = it)) }
            FooterEditor(brand = brand, onBrand = onBrand)
        }
        Spacer(Modifier.height(12.dp))
    }
}
