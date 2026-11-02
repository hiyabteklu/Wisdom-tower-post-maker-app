package com.wisdomtower.post

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

/**
 * Owns the open post, the brand and the library, and switches between the studio, the Library home and the brand screen.
 * Every template remembers how it was left: each one has its own draft file (see PostDrafts). Switching template saves the
 * one you leave and opens the other exactly as it was last edited, or in its factory look the first time.
 * The app opens on the template you edited last; the Library is one tap away (grid button in the editor).
 * The old layer studio has been removed.
 */
@Composable
fun PostApp() {
    val context = LocalContext.current
    val startup = remember { PostDrafts.startup(context) }
    var doc by remember { mutableStateOf(startup.first) }
    var library by remember { mutableStateOf(startup.second) }
    var brand by remember { mutableStateOf(PostStore.loadBrand(context)) }
    var screen by rememberSaveable { mutableStateOf("studio") }\n    var remoteBatch by remember { mutableStateOf<PostChatBatch?>(null) }
    var libSize by remember { mutableStateOf(startup.first.size) }
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
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

    fun openTemplate(id: String) {
        val old = doc
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
        doc = doc.copy(texts = postTemplate(doc.templateId).samples + library.lastWords)
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
            if (stored != null) doc = doc.copy(backgroundPhotoUri = stored, backgroundPhotoOpacity = 1f)
            else Toast.makeText(context, "That file is not a readable image", Toast.LENGTH_LONG).show()
        }
    }

    val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            val stored = PostStore.importPhoto(context, uri)
            if (stored != null) doc = doc.copy(photoUri = stored, photoZoom = 1f, photoFocusX = 0.5f, photoFocusY = 0.5f)
            else Toast.makeText(context, "That file is not a readable image", Toast.LENGTH_LONG).show()
        }
    }

    Box(Modifier.fillMaxSize()) {
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
                        // a template with no draft opens at the size chosen in the Library
                        if (!wasEdited && doc.size != libSize) doc = doc.copy(size = libSize)
                        screen = "studio"
                    },
                    onToggleBookmark = { toggleBookmark(it) },
                    onBack = { screen = "studio" }
                )
            }
            "batch" -> {
                BackHandler { screen = "studio" }
                val imported = remoteBatch\n                val batchDoc = imported?.let { batch ->\n                    doc.copy(templateId = batch.template, texts = postTemplate(batch.template).samples)\n                } ?: doc\n                PostBatchScreen(doc = batchDoc, brand = brand, initialBatch = imported, onBack = {\n                    remoteBatch = null\n                    screen = if (imported != null) "inbox" else "studio"\n                })
            }
            "chat" -> {\n                BackHandler { screen = "brand" }\n                PostChatScreen(onBack = { screen = "brand" }, onOpenInbox = { screen = "inbox" })\n            }\n            "inbox" -> {\n                BackHandler { screen = "chat" }\n                PostChatInboxScreen(onBack = { screen = "chat" }, onOpenBatch = { batch ->\n                    if (postTemplate(batch.template).id != batch.template) {\n                        Toast.makeText(context, "This app does not know template ${batch.template}", Toast.LENGTH_LONG).show()\n                    } else {\n                        remoteBatch = batch\n                        screen = "batch"\n                    }\n                })\n            }\n            "brand" -> {
                BackHandler { screen = "studio" }
                PostBrandScreen(
                    brand = brand,
                    onBrand = { brand = it },
                    onPickLogo = { logoPicker.launch(arrayOf("image/*")) },
                    onBack = { screen = "studio" }
                )
            }
            else -> PostStudioScreen(
                doc = doc,
                onDoc = { new -> if (new.templateId != doc.templateId) openTemplate(new.templateId) else doc = new },
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
                onOpenBatch = { screen = "batch" }
            )
        }
        SnackbarHost(snackbar, Modifier.align(Alignment.BottomCenter).navigationBarsPadding().padding(bottom = 12.dp))
    }
}
