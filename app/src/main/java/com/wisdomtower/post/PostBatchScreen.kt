package com.wisdomtower.post

import android.content.Intent
import android.graphics.Bitmap
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.ContentPaste
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.SaveAlt
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import java.time.LocalDate
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Batch posts, in two steps.
 * Step 1 (write): paste the list, pick the fill box, add photos.
 * Step 2 (review): swipe through a preview of every post, change the design of one page or of the whole batch (Design panel,
 * PostBatchDesign.kt), edit the words of any post, then press Save to gallery. Nothing is saved to the gallery until that button is pressed.
 * Designs of an inbox batch are remembered on this phone (PostDesignStore), so they are still there when the batch is reopened.
 * Words and captions of an inbox batch reach Supabase only when "Save changes to cloud" is pressed (BatchSaveBar in PostBatchSync.kt,
 * wt-mcp v5 update_batch; docs/URGENT_PLAN.md item 3). Edited words are also kept on this phone (PostWordsStore.kt), so they survive
 * closing the app before they reached the cloud.
 * One post of an inbox batch can be removed (BatchRemovePostBar in PostBatchSync.kt, wt-mcp v6 remove_post; docs/URGENT_PLAN.md item 1).
 * "Mark posted" (inbox batches) uses the same platform picker and post log as the inbox card menu (PostInboxActions.kt).
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PostBatchScreen(doc: PostDoc, brand: PostBrand, initialBatch: PostChatBatch? = null, onBack: () -> Unit) {
    val context = LocalContext.current
    var currentBrand by remember(brand) { mutableStateOf(brand) }
    val clipboard = LocalClipboardManager.current
    val view = LocalView.current
    val scope = rememberCoroutineScope()
    val slots = remember(doc.templateId, doc.size) {
        PostBatch.editableSlots(postTemplate(doc.templateId).layout(doc.size))
    }
    // A batch opened from the Poster inbox fills the list itself (full blocks) and goes straight to the review step.
    var mode by remember(initialBatch?.id) {
        mutableStateOf(if (initialBatch != null) BatchMode.BLOCKS else PostBatchStore.loadMode(context))
    }
    var text by remember(doc.templateId, initialBatch?.id) {
        mutableStateOf(initialBatch?.batchInput() ?: PostBatchStore.loadText(context, doc.templateId))
    }
    var fill by remember(doc.templateId, slots) { mutableStateOf(PostBatchStore.loadFill(context, doc.templateId, slots)) }
    var options by remember(doc.size) { mutableStateOf(PostStore.loadSaveOptions(context, doc.size).copy(sizes = setOf(doc.size))) }
    // The review page owns the output aspect ratio; changing it re-renders the preview and controls export.
    var batchSize by remember(initialBatch?.id) { mutableStateOf(doc.size) }
    var backendStatus by remember(initialBatch?.id) { mutableStateOf(initialBatch?.status.orEmpty()) }
    var backendMessage by remember(initialBatch?.id) { mutableStateOf<String?>(null) }
    var showPostedPicker by remember(initialBatch?.id) { mutableStateOf(false) }
    var postingBusy by remember(initialBatch?.id) { mutableStateOf(false) }

    // Review step
    var reviewing by remember { mutableStateOf(false) }
    val reviewItems = remember { mutableStateListOf<BatchItem>() }
    var originalItems by remember { mutableStateOf<List<BatchItem>>(emptyList()) }
    var editingIndex by remember { mutableStateOf<Int?>(null) }
    var editingDoc by remember { mutableStateOf<PostDoc?>(null) }
    // Designs of an inbox batch come back from this phone: the design of the whole batch, and the own design of every page edited on its own.
    val savedDesigns = remember(initialBatch?.id) { PostDesignStore.load(context, initialBatch?.id) }
    // null = the batch still uses the open post's design (doc).
    var batchDesign by remember(initialBatch?.id) { mutableStateOf<PostDoc?>(savedDesigns.batch) }
    // The Design panel scope: true changes every post, false changes only the post on screen.
    var entireBatch by remember(initialBatch?.id) { mutableStateOf(true) }
    val editedDesigns = remember(initialBatch?.id) { mutableStateMapOf<Int, PostDoc>().apply { putAll(savedDesigns.pages) } }
    // Captions belong to the inbox batch, not to the poster design. Keep edits locally on this phone
    // so they survive leaving and reopening the batch. They reach Supabase when "Save changes to cloud" is pressed
    // (BatchSaveBar, PostBatchSync.kt, wt-mcp v5 update_batch).
    val captionDrafts = remember(initialBatch?.id) {
        mutableStateMapOf<Int, String>().apply {
            val batch = initialBatch
            if (batch != null) {
                for (i in 0 until batch.captions.length()) put(i + 1, batch.captions.optString(i))
                val saved = context.getSharedPreferences("wt_post_captions", 0)
                for (i in 1..batch.posts.length()) {
                    saved.getString("${batch.id}:$i", null)?.let { put(i, it) }
                }
            }
        }
    }
    val pagerState = rememberPagerState(pageCount = { reviewItems.size })

    var checking by remember { mutableStateOf(false) }
    var fitResults by remember { mutableStateOf<List<String>>(emptyList()) }
    var running by remember { mutableStateOf(false) }
    var done by remember { mutableStateOf(0) }
    var total by remember { mutableStateOf(0) }
    var status by remember { mutableStateOf<String?>(null) }
    var resultUris by remember { mutableStateOf<List<android.net.Uri>>(emptyList()) }
    var runJob by remember { mutableStateOf<Job?>(null) }
    var cancelRequested by remember { mutableStateOf(false) }
    var batchPhotos by remember { mutableStateOf<List<String>>(emptyList()) }
    val importedPhotoPaths = remember { mutableListOf<String>() }
    val hasPhotoArea = remember(doc.templateId, doc.size) { postTemplate(doc.templateId).layout(doc.size).photo != null }
    val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
        scope.launch {
            val stored = withContext(Dispatchers.IO) { uris.mapNotNull { PostStore.importPhoto(context, it) } }
            importedPhotoPaths.filter { it != doc.photoUri && it != doc.backgroundPhotoUri }.forEach { PostStore.deleteImportedPhoto(context, it) }
            importedPhotoPaths.clear()
            val limited = stored.take(PostBatch.MAX_POSTS)
            stored.drop(PostBatch.MAX_POSTS).forEach { PostStore.deleteImportedPhoto(context, it) }
            importedPhotoPaths.addAll(limited)
            batchPhotos = limited
        }
    }

    fun saveEditingDoc(updated: PostDoc) {
        editingDoc = updated
        val index = editingIndex
        val current = index?.let { reviewItems.getOrNull(it) }
        if (index != null && current != null) {
            editedDesigns[current.number] = updated
            batchSize = updated.size
            options = options.copy(sizes = setOf(updated.size))
            reviewItems[index] = current.copy(texts = updated.texts)
        }
    }

    val editorPhotoPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            val stored = PostStore.importPhoto(context, uri)
            if (stored != null) editingDoc?.copy(photoUri = stored, photoZoom = 1f, photoFocusX = 0.5f, photoFocusY = 0.5f)?.let(::saveEditingDoc)
            else backendMessage = "That file is not a readable image"
        }
    }
    val editorBackgroundPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            val stored = PostStore.importPhoto(context, uri)
            if (stored != null) editingDoc?.copy(backgroundPhotoUri = stored, backgroundPhotoOpacity = 1f)?.let(::saveEditingDoc)
            else backendMessage = "That file is not a readable image"
        }
    }
    val editorLogoPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            val stored = PostStore.importLogo(context, uri)
            if (stored != null) currentBrand = currentBrand.copy(logoPath = stored, logoStamp = System.currentTimeMillis())
            else backendMessage = "That file is not a readable image"
        }
    }

    val parsed = remember(text, mode, slots, fill) { PostBatch.parse(text, mode, slots, fill) }
    // This image-driven path is for the local studio Batch action; Inbox batches retain their existing text/caption flow.
    val mockupBatch = initialBatch == null && (doc.templateId == "device-hero" || doc.templateId == "device-headline")
    // Phone mockups are image-driven: one imported screenshot creates one review/export item.
    val items = if (mockupBatch) batchPhotos.mapIndexed { index, _ -> BatchItem(index + 1, emptyMap(), emptyList()) } else parsed.items

    // A page's design: its own (edited alone or in the editor), else the design of the whole batch, else the open post.
    // Words: an inbox batch shows exactly the words the post came with (or the words edited in the editor), never the template's
    // SAMPLE words, so a box the post left empty stays empty. A local studio batch keeps the open post's words for boxes it did not fill.
    fun pageDoc(item: BatchItem): PostDoc {
        val design = editedDesigns[item.number] ?: batchDesign ?: doc
        val batchPhoto = batchPhotos.getOrNull(item.number - 1)
        val rawWorded = if (initialBatch != null) design.copy(texts = item.texts) else PostBatch.apply(design, item)
        val specSlots = postTemplate(rawWorded.templateId).slots
        val worded = rawWorded.copy(texts = PostSlots.withDerivedSlots(rawWorded.texts, specSlots))
        return worded.let { page ->
            val withSize = page.copy(size = batchSize)
            if (item.number !in editedDesigns && batchPhoto != null) withSize.copy(photoUri = batchPhoto) else withSize
        }
    }

    // Keeps the designs of an inbox batch on this phone. Only inbox batches have an id to keep them under.
    fun persistDesigns() {
        val id = initialBatch?.id ?: return
        PostDesignStore.save(context, id, batchDesign ?: doc, editedDesigns.toMap())
    }

    // The Design panel calls this. change only touches look fields (see PostBatchDesign.kt), so words are never read or replaced here.
    // Entire batch: the batch design and every page that has its own design get the same change.
    // This page: only that page gets its own design (a copy of the batch design with the change), and keeps its own photo.
    fun changeDesign(pageNumber: Int, change: (PostDoc) -> PostDoc) {
        if (entireBatch) {
            batchDesign = change(batchDesign ?: doc)
            editedDesigns.keys.toList().forEach { number ->
                editedDesigns[number]?.let { editedDesigns[number] = change(it) }
            }
        } else {
            val first = editedDesigns[pageNumber] == null
            val current = editedDesigns[pageNumber] ?: batchDesign ?: doc
            val changed = change(current)
            val ownPhoto = batchPhotos.getOrNull(pageNumber - 1)
            editedDesigns[pageNumber] = if (first && ownPhoto != null) changed.copy(photoUri = ownPhoto) else changed
        }
        persistDesigns()
    }

    // Words that came from the inbox are not stored as the template's own batch draft.
    LaunchedEffect(text, doc.templateId) {
        if (initialBatch != null) return@LaunchedEffect
        delay(500)
        PostBatchStore.saveText(context, doc.templateId, text)
    }
    LaunchedEffect(mode) { if (initialBatch == null) PostBatchStore.saveMode(context, mode) }
    LaunchedEffect(fill, doc.templateId) { PostBatchStore.saveFill(context, doc.templateId, fill) }
    LaunchedEffect(running) { view.keepScreenOn = running }
    // Edited words of an inbox batch are kept on this phone (PostWordsStore.kt); the entry goes away when the words equal the cloud words again.
    val wordsSignature = PostBatchSync.textSignature(reviewItems.toList())
    LaunchedEffect(initialBatch?.id, reviewing, wordsSignature) {
        val id = initialBatch?.id ?: return@LaunchedEffect
        if (!reviewing || reviewItems.isEmpty() || originalItems.isEmpty()) return@LaunchedEffect
        delay(300)
        PostWordsStore.sync(context, id, originalItems, reviewItems.toList())
    }
    val latestJob by rememberUpdatedState(runJob)
    DisposableEffect(Unit) {
        onDispose {
            view.keepScreenOn = false
            latestJob?.cancel()
            importedPhotoPaths.filter { it != doc.photoUri }.forEach { PostStore.deleteImportedPhoto(context, it) }
            importedPhotoPaths.clear()
        }
    }

    // System back from the review step goes back to the list, not out of the screen.
    BackHandler(enabled = reviewing && !running) { reviewing = false }

    fun startReview() {
        if (items.isEmpty() || running) return
        reviewItems.clear()
        // An inbox batch gets back the words that were edited on this phone but never reached the cloud; they then show as unsaved changes.
        val restored = initialBatch?.let { PostWordsStore.restore(context, it.id, items) }
        reviewItems.addAll(restored ?: items)
        originalItems = items
        status = null
        fitResults = emptyList()
        resultUris = emptyList()
        reviewing = true
        scope.launch { pagerState.scrollToPage(0) }
    }

    // Opened from the inbox: show the previews straight away.
    LaunchedEffect(initialBatch?.id) {
        if (initialBatch != null && items.isNotEmpty() && !reviewing) startReview()
    }

    fun startBatch() {
        if (reviewItems.isEmpty() || running) return
        val list = reviewItems.toList()
        PostStore.saveSaveOptions(context, options)
        val chosenSizes = PostSize.values().filter { it in options.sizes }.ifEmpty { listOf(doc.size) }
        val stamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val folder = "Batch " + SimpleDateFormat("yyyy-MM-dd HH-mm-ss", Locale.US).format(Date())
        val width = list.size.toString().length.coerceAtLeast(2)
        total = list.size
        done = 0
        cancelRequested = false
        status = null
        fitResults = emptyList()
        resultUris = emptyList()
        running = true
        runJob = scope.launch {
            val savedUris = mutableListOf<android.net.Uri>()
            val shrunk = mutableListOf<String>()
            try {
                for (item in list) {
                    kotlinx.coroutines.currentCoroutineContext().ensureActive()
                    if (cancelRequested) {
                        status = "Cancelled after $done of $total posts"
                        break
                    }
                    try {
                        val saved = withContext(Dispatchers.Default) {
                            PostExport.saveOne(
                                context, pageDoc(item), currentBrand, chosenSizes,
                                options.png, options.fourK, stamp,
                                item.number.toString().padStart(width, '0'), folder
                            )
                        }
                        savedUris.addAll(saved.uris)
                        saved.overflow.forEach { (size, boxes) ->
                            shrunk.add("Post ${item.number}, ${size.label}: " + boxes.joinToString { PostSlots.label(it) })
                        }

                        done = item.number
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Throwable) {
                        status = "Stopped at post ${item.number}: " + (e.localizedMessage ?: "export failed")
                        break
                    }
                }
            } catch (_: CancellationException) {
                status = "Cancelled after ${done} of ${total} posts"
            } finally {
                resultUris = savedUris
                fitResults = shrunk
                if (status == null) status = "Saved ${savedUris.size} files to your gallery (Pictures/Wisdom Tower Post/$folder)"
                running = false
            }
        }
    }

    fun shareResults() {
        if (resultUris.isEmpty() || resultUris.size > 30) return
        val intent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
            type = "image/*"
            putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(resultUris))
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Share batch"))
    }

    PostTheme {
        Box(Modifier.fillMaxSize()) {
        Scaffold(containerColor = PostPalette.Ink) { padding ->
            Column(
                Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(horizontal = 14.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (!reviewing) {
                    // ---------- Step 1: write the list ----------
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Outlined.ArrowBack, contentDescription = "Back", tint = PostPalette.Text)
                        }
                        Column(Modifier.weight(1f)) {
                            Text(if (mockupBatch) "Phone mockup batch" else "Batch posts", color = PostPalette.Text, fontSize = 19.sp, fontWeight = FontWeight.Bold)
                            Text(postTemplate(doc.templateId).name, color = PostPalette.Muted, fontSize = 12.sp)
                        }
                        OutlinedButton(onClick = {
                            if (mockupBatch) {
                                importedPhotoPaths.filter { it != doc.photoUri && it != doc.backgroundPhotoUri }.forEach { PostStore.deleteImportedPhoto(context, it) }
                                importedPhotoPaths.clear()
                                batchPhotos = emptyList()
                            } else text = ""
                        }) {
                            Icon(Icons.Outlined.Delete, contentDescription = null)
                            Text("Clear")
                        }
                    }
                    if (mockupBatch) {
                        Text("Create phone-frame mockups from a whole gallery of screenshots. Each image stays in its own phone screen; review and edit any one before exporting the set.", color = PostPalette.Muted, fontSize = 13.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Button(onClick = { photoPicker.launch(arrayOf("image/*")) }, modifier = Modifier.height(48.dp)) {
                                Text(if (batchPhotos.isEmpty()) "Select screenshots" else "Replace screenshots")
                            }
                            Text(if (batchPhotos.isEmpty()) "Choose multiple images (up to 50)" else "${batchPhotos.size} screenshots ready", color = PostPalette.Mint, fontSize = 12.sp)
                        }
                        if (batchPhotos.isNotEmpty()) {
                            OutlinedButton(
                                onClick = {
                                    importedPhotoPaths.filter { it != doc.photoUri && it != doc.backgroundPhotoUri }.forEach { PostStore.deleteImportedPhoto(context, it) }
                                    importedPhotoPaths.clear()
                                    batchPhotos = emptyList()
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) { Text("Clear selected screenshots") }
                        }
                        Text("Nothing is downloaded until you review the previews and press Save to gallery.", color = PostPalette.Muted, fontSize = 12.sp)
                        Button(
                            onClick = { startReview() },
                            enabled = batchPhotos.isNotEmpty() && !running,
                            modifier = Modifier.fillMaxWidth().height(52.dp)
                        ) {
                            Text("Review ${batchPhotos.size} mockups")
                        }
                    } else {
                        Text("Every post uses this design, including its colors, photos, logo and fonts. Next you can look at every post, fix any of them, and then save.", color = PostPalette.Muted, fontSize = 12.sp)
                        if (hasPhotoArea) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                OutlinedButton(onClick = { photoPicker.launch(arrayOf("image/*")) }) { Text("Add photos") }
                                Text(if (batchPhotos.isEmpty()) "Optional: one photo per post" else "${batchPhotos.size} photos selected", color = PostPalette.Muted, fontSize = 12.sp)
                                if (batchPhotos.isNotEmpty()) OutlinedButton(onClick = { importedPhotoPaths.filter { it != doc.photoUri && it != doc.backgroundPhotoUri }.forEach { PostStore.deleteImportedPhoto(context, it) }; importedPhotoPaths.clear(); batchPhotos = emptyList() }) { Text("Clear photos") }
                            }
                        }
                        if (doc.photoUri != null || doc.backgroundPhotoUri != null) {
                            Text("The same photo will be used on every post in this batch.", color = PostPalette.Gold, fontSize = 12.sp)
                        }
                        Text("Write your list", color = PostPalette.Text, fontWeight = FontWeight.Bold)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(selected = mode == BatchMode.ONE_LINE, onClick = { mode = BatchMode.ONE_LINE }, label = { Text("One line = one post") })
                            FilterChip(selected = mode == BatchMode.BLOCKS, onClick = { mode = BatchMode.BLOCKS }, label = { Text("Full blocks") })
                        }
                        if (mode == BatchMode.ONE_LINE) {
                            Text("Fill this box. Other boxes keep the words from the open post.", color = PostPalette.Muted, fontSize = 12.sp)
                            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                slots.forEach { slot ->
                                    FilterChip(selected = fill == slot, onClick = { fill = slot }, label = { Text(PostSlots.label(slot)) })
                                }
                            }
                        } else {
                            Text("Separate posts with --- . Use labels such as headline:, message: and list:, or write one box per line.", color = PostPalette.Muted, fontSize = 12.sp)
                        }
                        OutlinedTextField(
                            value = text, onValueChange = { text = it },
                            modifier = Modifier.fillMaxWidth().height(190.dp),
                            placeholder = { Text(if (mode == BatchMode.ONE_LINE) "One post per line..." else "headline: Your headline\nmessage: Your message\n---\nheadline: Next post") },
                            minLines = 8,
                            textStyle = TextStyle(color = PostPalette.Text, fontSize = 14.sp)
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(onClick = { text = clipboard.getText()?.text.orEmpty() }) {
                                Icon(Icons.Outlined.ContentPaste, contentDescription = null)
                                Text("Paste")
                            }
                            OutlinedButton(onClick = { text = PostBatch.exampleFor(slots, mode) }) { Text("Example") }
                            Text("${items.size} posts", color = PostPalette.Muted, fontSize = 12.sp)
                        }
                        if (parsed.notes.isNotEmpty()) {
                            Text(parsed.notes.joinToString("\n"), color = PostPalette.Gold, fontSize = 12.sp)
                        }
                        Button(
                            onClick = { startReview() },
                            enabled = items.isNotEmpty(),
                            modifier = Modifier.fillMaxWidth().height(52.dp)
                        ) {
                            Text("Review ${items.size} posts")
                        }
                    }
                } else {
                    // ---------- Step 2: review ----------
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { reviewing = false }) {
                            Icon(Icons.Outlined.ArrowBack, contentDescription = "Back", tint = PostPalette.Text)
                        }
                        Column(Modifier.weight(1f)) {
                            Text(if (mockupBatch) "Review mockups" else "Review batch", color = PostPalette.Text, fontSize = 19.sp, fontWeight = FontWeight.Bold)
                            Text(if (mockupBatch) "${reviewItems.size} mockups · swipe to preview" else "${reviewItems.size} posts · swipe to check", color = PostPalette.Muted, fontSize = 12.sp)
                        }
                    }
                    if (backendMessage != null) {
                        Text(backendMessage!!, color = PostPalette.Gold, fontSize = 12.sp)
                    }
                    Text("Aspect ratio", color = PostPalette.Text, fontWeight = FontWeight.Bold)
                    Row(
                        Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PostSize.values().forEach { size ->
                            FilterChip(
                                selected = batchSize == size,
                                onClick = {
                                    batchSize = size
                                    options = options.copy(sizes = setOf(size))
                                },
                                label = { Text(size.label) }
                            )
                        }
                    }
                    Text("Preview and Save to gallery use the selected ratio.", color = PostPalette.Muted, fontSize = 12.sp)
                    HorizontalPager(
                        state = pagerState,
                        contentPadding = PaddingValues(horizontal = 24.dp),
                        pageSpacing = 12.dp,
                        modifier = Modifier.fillMaxWidth().height(420.dp)
                    ) { page ->
                        val item = reviewItems[page]
                        BatchPostPage(
                            postDoc = pageDoc(item),
                            brand = currentBrand,
                            number = item.number,
                            edited = item.number in editedDesigns,
                            canEdit = !running,
                            onEdit = {
                                editingIndex = page
                                editingDoc = pageDoc(item)
                            }
                        )
                    }
                    // Caption controls stay in the review flow, directly below the matching poster.
                    val captionItem = reviewItems.getOrNull(pagerState.currentPage)
                    if (initialBatch != null && captionItem != null) {
                        val caption = captionDrafts[captionItem.number].orEmpty()
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Caption for post ${captionItem.number}", color = PostPalette.Text, fontWeight = FontWeight.Bold)
                            OutlinedTextField(
                                value = caption,
                                onValueChange = { updated ->
                                    captionDrafts[captionItem.number] = updated
                                    context.getSharedPreferences("wt_post_captions", 0).edit()
                                        .putString("${initialBatch.id}:${captionItem.number}", updated)
                                        .apply()
                                },
                                modifier = Modifier.fillMaxWidth().heightIn(min = 96.dp),
                                label = { Text("Edit caption") },
                                minLines = 3,
                                textStyle = TextStyle(color = PostPalette.Text, fontSize = 14.sp)
                            )
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                OutlinedButton(
                                    onClick = { clipboard.setText(AnnotatedString(caption)) },
                                    enabled = caption.isNotBlank()
                                ) { Text("Copy caption") }
                            }
                        }
                    }
                    // One clear save state for the words and captions of an inbox batch (PostBatchSync.kt, docs/URGENT_PLAN.md item 3).
                    if (initialBatch != null && !running) {
                        BatchSaveBar(
                            batch = initialBatch,
                            items = reviewItems,
                            originalItems = originalItems,
                            captions = captionDrafts,
                            backendStatus = backendStatus
                        )
                        // Remove the post on screen from the cloud copy of the batch (wt-mcp v6 remove_post, docs/URGENT_PLAN.md item 1).
                        BatchRemovePostBar(
                            batch = initialBatch,
                            pageIndex = pagerState.currentPage,
                            reviewCount = reviewItems.size,
                            backendStatus = backendStatus,
                            onRemoved = onBack
                        )
                    }
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { scope.launch { pagerState.animateScrollToPage((pagerState.currentPage - 1).coerceAtLeast(0)) } }) {
                            Icon(Icons.Outlined.ArrowBack, contentDescription = "Previous")
                        }
                        Text("Post ${pagerState.currentPage + 1} of ${reviewItems.size}", color = PostPalette.Text)
                        IconButton(onClick = { scope.launch { pagerState.animateScrollToPage((pagerState.currentPage + 1).coerceAtMost(reviewItems.lastIndex)) } }) {
                            Icon(Icons.Outlined.ArrowForward, contentDescription = "Next")
                        }
                    }
                    // Design panel: This page / Entire batch (docs/MATH_AND_TEMPLATES_PLAN.md, Step D).
                    val designPage = reviewItems.getOrNull(pagerState.currentPage)
                    if (!running && designPage != null) {
                        Box(
                            Modifier.fillMaxWidth()
                                .background(PostPalette.Surface, RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            BatchDesignPanel(
                                postCount = reviewItems.size,
                                pageNumber = designPage.number,
                                entireBatch = entireBatch,
                                onScope = { entireBatch = it },
                                design = editedDesigns[designPage.number] ?: batchDesign ?: doc,
                                brand = currentBrand,
                                onChange = { change -> changeDesign(designPage.number, change) }
                            )
                        }
                    }
                    // Save options + actions
                    Text("Save options", color = PostPalette.Text, fontWeight = FontWeight.Bold)
                    // (save options UI continues...)
                    if (running) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                            CircularProgressIndicator()
                            Text("Saving $done of $total...", color = PostPalette.Text)
                            OutlinedButton(onClick = { cancelRequested = true }) { Text("Cancel") }
                        }
                    } else {
                        val fileCount = reviewItems.size * PostSize.values().count { it in options.sizes }.coerceAtLeast(1)
                        Button(
                            onClick = { startBatch() },
                            enabled = reviewItems.isNotEmpty(),
                            modifier = Modifier.fillMaxWidth().height(52.dp)
                        ) {
                            Icon(Icons.Outlined.SaveAlt, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text(if (resultUris.isEmpty()) {
                                if (mockupBatch) "Save mockups to gallery ($fileCount files)" else "Save to gallery ($fileCount files)"
                            } else "Save again to a new folder")
                        }
                    }
                    status?.takeIf { it != "All posts fit" }?.let { Text(it, color = PostPalette.Text, fontSize = 13.sp) }
                    if (resultUris.isNotEmpty() && !running && initialBatch != null) {
                        Text("Backend status: $backendStatus", color = PostPalette.Muted, fontSize = 12.sp)
                        OutlinedButton(onClick = {
                            scope.launch {
                                try {
                                    PostChatApi.setBatchStatus(context, initialBatch.id, "approved")
                                    backendStatus = "approved"
                                    backendMessage = "Batch marked approved"
                                } catch (e: Exception) { backendMessage = e.message ?: "Status update failed" }
                            }
                        }, modifier = Modifier.fillMaxWidth(), enabled = backendStatus != "approved" && backendStatus != "published") { Text("Mark approved") }
                        // Same picker and post log as the inbox card menu: every platform used, one log row per post and platform.
                        Button(
                            onClick = { showPostedPicker = true },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = backendStatus != "published" && !postingBusy
                        ) { Text(if (postingBusy) "Marking posted..." else "Mark posted (choose platforms)") }
                    }
                    if (resultUris.isNotEmpty() && resultUris.size <= 30) {
                        OutlinedButton(onClick = { shareResults() }, modifier = Modifier.fillMaxWidth()) {
                            Icon(Icons.Outlined.Share, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Share saved images")
                        }
                    }
                }
            }
        }

        if (showPostedPicker && initialBatch != null) {
            PlatformPickerDialog(
                batchTitle = initialBatch.title,
                onDismiss = { showPostedPicker = false },
                onConfirm = { platforms ->
                    showPostedPicker = false
                    postingBusy = true
                    scope.launch {
                        try {
                            val rows = markBatchPosted(context, initialBatch.id, platforms) { _, _ -> }
                            backendStatus = "published"
                            backendMessage = "Marked posted on " + platforms.joinToString(", ") + " ($rows log rows saved)"
                        } catch (e: Exception) {
                            val message = e.message ?: "Could not mark posted"
                            // markBatchPosted sets the status first, so a half-written log still means the batch is published
                            if (message.startsWith("Marked posted")) backendStatus = "published"
                            backendMessage = message
                        } finally {
                            postingBusy = false
                        }
                    }
                }
            )
        }

        // The editor for one post in the batch.
        // It is NOT a Dialog any more. A Dialog is a second window, and on this phone (gesture navigation, Android 15
        // edge-to-edge) that window was laid out taller than the visible screen, so the pinned tool bar was pushed
        // below the bottom edge. Here the editor is a full-screen layer inside the SAME window as the normal editor,
        // so it gets exactly the same status bar and navigation bar handling and the tool bar always stays visible.
        // The studio owns the two fixed header rows (title row with Apply all / Done, then size row) and the pinned tool bar.
        // Do not add a third header above it.
        val index = editingIndex
        val item = index?.let { reviewItems.getOrNull(it) }
        val activeEditorDoc = editingDoc
        if (index != null && item != null && activeEditorDoc != null) {
            BackHandler { persistDesigns(); editingIndex = null; editingDoc = null }
            Box(
                Modifier.fillMaxSize().background(PostPalette.Ink)
                    // Swallow touches so nothing under the editor can be tapped through it.
                    .pointerInput(Unit) { detectTapGestures { } }
            ) {
                PostStudioScreen(
                    doc = activeEditorDoc,
                    onDoc = { changed -> saveEditingDoc(changed) },
                    brand = currentBrand,
                    library = PostDrafts.loadLibrary(context) ?: PostLibrary(),
                    onBrand = { currentBrand = it },
                    onPickPhoto = { editorPhotoPicker.launch(arrayOf("image/*")) },
                    onPickBackgroundPhoto = { editorBackgroundPicker.launch(arrayOf("image/*")) },
                    onPickLogo = { editorLogoPicker.launch(arrayOf("image/*")) },
                    onOpenBrand = { backendMessage = "Close this editor to manage brand settings from the main studio." },
                    embedded = false,
                    headerTitle = "Editing poster ${item.number}/${reviewItems.size}",
                    onHeaderApplyAll = {
                        val source = editingDoc ?: return@PostStudioScreen
                        reviewItems.forEach { target ->
                            if (mockupBatch) {
                                // Copy the styling/layout edit, but preserve each page's own screenshot and crop.
                                val targetDesign = editedDesigns[target.number] ?: pageDoc(target)
                                editedDesigns[target.number] = source.copy(
                                    photoUri = targetDesign.photoUri,
                                    secondaryPhotoUri = targetDesign.secondaryPhotoUri,
                                    tertiaryPhotoUri = targetDesign.tertiaryPhotoUri,
                                    photoZoom = targetDesign.photoZoom,
                                    photoFocusX = targetDesign.photoFocusX,
                                    photoFocusY = targetDesign.photoFocusY,
                                    photoSlotAdjust = targetDesign.photoSlotAdjust,
                                    texts = targetDesign.texts
                                )
                            } else {
                                editedDesigns[target.number] = source.copy(texts = doc.texts)
                            }
                        }
                        persistDesigns()
                        backendMessage = "Design applied to all ${reviewItems.size} posts"
                    },
                    onHeaderDone = { persistDesigns(); editingIndex = null; editingDoc = null }
                )
            }
        }
        }
    }
}

/**
 * One swipeable page: the preview of one post and its Edit button. Fixed height so swiping never jumps.
 * Chapter summaries (study-*) never shrink their text, so the chip on those pages shows "Lines n / 29" when the sheet fits, or
 * "Does not fit, split this sheet. Left out: ..." naming the first row that was left out (PostStudyGrid.Fit).
 */
@Composable
private fun BatchPostPage(
    postDoc: PostDoc,
    brand: PostBrand,
    number: Int,
    edited: Boolean,
    canEdit: Boolean,
    onEdit: () -> Unit
) {
    val context = LocalContext.current
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var overflow by remember { mutableStateOf<List<String>>(emptyList()) }
    var fit by remember { mutableStateOf<PostStudyGrid.Fit?>(null) }
    LaunchedEffect(postDoc, brand) {
        val result = withContext(Dispatchers.Default) {
            PostRenderer.preview(context, postDoc, brand, 540) { !isActive }
        }
        bitmap = result.bitmap
        overflow = result.overflow
        fit = if (postDoc.templateId.startsWith("study-")) {
            withContext(Dispatchers.Default) { runCatching { PostStudyGrid.measure(context, postDoc) }.getOrNull() }
        } else {
            null
        }
    }
    Column(Modifier.fillMaxWidth()) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(340.dp)
                .background(PostPalette.Surface, RoundedCornerShape(12.dp))
        ) {
            bitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }
            if (edited) {
                Text(
                    "Edited",
                    color = PostPalette.Ink,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .background(PostPalette.Mint, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
            val isStudy = postDoc.templateId.startsWith("study-")
            val studyFit = fit
            if (isStudy && studyFit != null) {
                Text(
                    if (studyFit.fits) studyFit.counter() else studyFit.warning(),
                    color = PostPalette.Ink,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 3,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp)
                        .then(if (studyFit.fits) Modifier else Modifier.fillMaxWidth(0.8f))
                        .background(if (studyFit.fits) PostPalette.Mint else PostPalette.Gold, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                )
            } else if (overflow.isNotEmpty()) {
                Text(
                    if (isStudy) "Does not fit, split this sheet" else "Too long for this template: shorten it or pick a bigger one",
                    color = PostPalette.Ink,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .background(PostPalette.Gold, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        OutlinedButton(
            onClick = onEdit,
            enabled = canEdit,
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Icon(Icons.Outlined.Edit, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(if (postDoc.templateId == "device-hero" || postDoc.templateId == "device-headline") "Edit mockup $number" else "Edit post $number")
        }
    }
}
