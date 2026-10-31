package com.wisdomtower.post

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.delay

/** App navigation and offline state owner. Drafts are isolated per template; brand settings remain global. */
@Composable
fun PostApp() {
    val context = LocalContext.current
    val hasLegacyDoc = remember { PostStore.hasSavedDoc(context) }
    val initialDoc = remember {
        val legacy = PostStore.loadDoc(context)
        if (hasLegacyDoc) PostDraftStore.migrateLegacy(context, legacy)
        val index = PostDraftStore.loadIndex(context)
        val last = PostTemplates.firstOrNull { it.id == index.lastOpenedTemplateId } ?: PostTemplates.first()
        PostDraftStore.loadDraft(context, last.id)
            ?: if (hasLegacyDoc) legacy else PostDoc(templateId = last.id, texts = last.samples)
    }
    val initialIndex = remember { PostDraftStore.loadIndex(context) }
    var doc by remember { mutableStateOf(initialDoc) }
    var docDirty by remember { mutableStateOf(false) }
    var brand by remember { mutableStateOf(PostStore.loadBrand(context)) }
    var projects by remember { mutableStateOf(PostProjects.load(context)) }
    var library by remember { mutableStateOf(initialIndex) }
    var screen by rememberSaveable {
        mutableStateOf(if (hasLegacyDoc || initialIndex.lastEditedAt.isNotEmpty()) "editor" else "home")
    }
    var brandReturn by rememberSaveable { mutableStateOf("home") }

    LaunchedEffect(doc, docDirty, screen) {
        if (!docDirty || screen != "editor") return@LaunchedEffect
        delay(1_000)
        PostDraftStore.saveDraft(context, doc)
        PostStore.save(context, doc, brand)
        library = PostDraftStore.loadIndex(context)
        docDirty = false
    }
    LaunchedEffect(brand) {
        delay(350)
        PostStore.save(context, doc, brand)
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
            if (stored != null) { doc = doc.copy(backgroundPhotoUri = stored, backgroundPhotoOpacity = 1f); docDirty = true }
            else Toast.makeText(context, "That file is not a readable image", Toast.LENGTH_LONG).show()
        }
    }
    val iconPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
        val available = (6 - doc.placedIcons.size).coerceAtLeast(0)
        val imported = uris.take(available).mapIndexedNotNull { index, uri ->
            val path = PostStore.importIcon(context, uri)
            path?.let { PostPlacedIcon(id = "icon_${System.currentTimeMillis()}_$index", path = it) }
        }
        if (imported.isNotEmpty()) { doc = doc.copy(placedIcons = (doc.placedIcons + imported).take(6)); docDirty = true }
        if (uris.size > available) Toast.makeText(context, "A post can contain up to 6 icons", Toast.LENGTH_SHORT).show()
    }

    val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            val stored = PostStore.importPhoto(context, uri)
            if (stored != null) { doc = doc.copy(photoUri = stored, photoZoom = 1f, photoFocusX = 0.5f, photoFocusY = 0.5f); docDirty = true }
            else Toast.makeText(context, "That file is not a readable image", Toast.LENGTH_LONG).show()
        }
    }

    fun openBrand() {
        brandReturn = screen
        screen = "brand"
    }

    BackHandler(enabled = screen != "home") {
        screen = if (screen == "brand") brandReturn else "home"
    }

    when (screen) {
        "brand" -> PostBrandScreen(
            brand = brand,
            onBrand = { brand = it },
            onPickLogo = { logoPicker.launch(arrayOf("image/*")) },
            onBack = { screen = brandReturn }
        )
        "editor" -> PostStudioScreen(
            doc = doc,
            onDoc = { doc = it; docDirty = true },
            brand = brand,
            onBrand = { brand = it },
            onPickPhoto = { photoPicker.launch(arrayOf("image/*")) },
            onPickBackgroundPhoto = { backgroundPhotoPicker.launch(arrayOf("image/*")) },
            onPickLogo = { logoPicker.launch(arrayOf("image/*")) },
            onPickIcons = { iconPicker.launch(arrayOf("image/png", "image/webp", "image/jpeg")) },
            onOpenBrand = { openBrand() },
            onBackHome = { screen = "home" },
            onSaveProject = {
                projects = PostProjects.save(context, doc)
                Toast.makeText(context, "Saved to My posts", Toast.LENGTH_SHORT).show()
            },
            bookmarked = doc.templateId in library.bookmarks,
            onToggleBookmark = {
                PostDraftStore.setBookmarked(context, doc.templateId, doc.templateId !in library.bookmarks)
                library = PostDraftStore.loadIndex(context)
            }
        )
        else -> PostHomeScreen(
            brand = brand,
            projects = projects,
            library = library,
            onChooseTemplate = { spec, size ->
                doc = PostDraftStore.openTemplate(context, spec, size)
                docDirty = false
                library = PostDraftStore.loadIndex(context)
                screen = "editor"
            },
            onOpenProject = { project ->
                doc = project.document
                docDirty = true
                PostDraftStore.markOpened(context, doc.templateId)
                library = PostDraftStore.loadIndex(context)
                screen = "editor"
            },
            onDeleteProject = { id -> projects = PostProjects.delete(context, id) },
            onBookmark = { id, saved ->
                PostDraftStore.setBookmarked(context, id, saved)
                library = PostDraftStore.loadIndex(context)
            },
            onOpenBrand = { openBrand() }
        )
    }
}
