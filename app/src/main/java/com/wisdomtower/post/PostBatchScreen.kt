package com.wisdomtower.post

import android.content.Intent
import android.graphics.Bitmap
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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
 * Step 2 (review): swipe through a preview of every post, edit the words of any post,
 * then press Save to gallery. Nothing is saved until that button is pressed.
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
    var options by remember(doc.size) { mutableStateOf(PostStore.loadSaveOptions(context, doc.size)) }
    var backendStatus by remember(initialBatch?.id) { mutableStateOf(initialBatch?.status.orEmpty()) }
    var backendMessage by remember(initialBatch?.id) { mutableStateOf<String?>(null) }
    var platform by remember(initialBatch?.id) { mutableStateOf("Telegram") }

    // Review step
    var reviewing by remember { mutableStateOf(false) }
    val reviewItems = remember { mutableStateListOf<BatchItem>() }
    var originalItems by remember { mutableStateOf<List<BatchItem>>(emptyList()) }
    var editingIndex by remember { mutableStateOf<Int?>(null) }
    var editingDoc by remember { mutableStateOf<PostDoc?>(null) }
    val editedDesigns = remember { mutableStateMapOf<Int, PostDoc>() }
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
            importedPhotoPaths.addAll(stored)
            batchPhotos = stored
        }
    }

    fun saveEditingDoc(updated: PostDoc) {
        editingDoc = updated
        val index = editingIndex
        val current = index?.let { reviewItems.getOrNull(it) }
        if (index != null && current != null) {
            editedDesigns[current.number] = updated
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
    val items = parsed.items

    fun pageDoc(item: BatchItem): PostDoc {
        val design = editedDesigns[item.number] ?: doc
        val batchPhoto = batchPhotos.getOrNull(item.number - 1)
        return PostBatch.apply(design, item).let { page ->
            if (item.number !in editedDesigns && batchPhoto != null) page.copy(photoUri = batchPhoto) else page
        }
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
        reviewItems.addAll(items)
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
                            Text("Batch posts", color = PostPalette.Text, fontSize = 19.sp, fontWeight = FontWeight.Bold)
                            Text(postTemplate(doc.templateId).name, color = PostPalette.Muted, fontSize = 12.sp)
                        }
                        OutlinedButton(onClick = { text = "" }) {
                            Icon(Icons.Outlined.Delete, contentDescription = null)
                            Text("Clear")
                        }
                    }
                    Text("Every post uses this design, including its colors, photos, logo and fonts. Next you can look at every post, fix any of them, and then save.", color = PostPalette.Muted, fontSize = 12.sp)
                    if (hasPhotoArea) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            OutlinedButton(onClick = { photoPicker.launch(arrayOf("image/*")) }) { Text("Add photos") }
                            Text(if (batchPhotos.isEmpty()) "Optional: one photo per post" else "${batchPhotos.size} photos selected", color = PostPalette.Muted, fontSize = 12.sp)
                            if (batchPhotos.isNotEmpty()) OutlinedButton(onClick = { importedPhotoPaths.filter { it != doc.photoUri }.forEach { PostStore.deleteImportedPhoto(context, it) }; importedPhotoPaths.clear(); batchPhotos = emptyList() }) { Text("Clear photos") }
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
                        Text("${items.size} posts", color = PostPalette.Gold, modifier = Modifier.align(Alignment.CenterVertically))
                    }
                    if (parsed.notes.isNotEmpty()) {
                        Text(parsed.notes.distinct().joinToString("\n"), color = PostPalette.Gold, fontSize = 12.sp)
                    }
                    Button(
                        onClick = { startReview() },
                        enabled = items.isNotEmpty(),
                        modifier = Modifier.fillMaxWidth().height(52.dp)
                    ) {
                        Text(if (items.isEmpty()) "Paste or type your words" else "Preview ${items.size} posts")
                    }
                    Spacer(Modifier.height(20.dp))
                } else {
                    // ---------- Step 2: review, edit, save ----------
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { reviewing = false }, enabled = !running) {
                            Icon(Icons.Outlined.ArrowBack, contentDescription = "Back to the list", tint = PostPalette.Text)
                        }
                        Column(Modifier.weight(1f)) {
                            Text("Review your posts", color = PostPalette.Text, fontSize = 19.sp, fontWeight = FontWeight.Bold)
                            Text("Swipe left or right. Fix anything, then save.", color = PostPalette.Muted, fontSize = 12.sp)
                        }
                    }
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 36.dp),
                        pageSpacing = 12.dp,
                        userScrollEnabled = !running
                    ) { page ->
                        val item = reviewItems.getOrNull(page)
                        if (item != null) {
                            val postDoc = remember(doc, item, batchPhotos) { pageDoc(item) }
                            BatchPostPage(
                                postDoc = postDoc,
                                brand = currentBrand,
                                number = item.number,
                                edited = item != originalItems.getOrNull(page),
                                canEdit = !running,
                                onEdit = { editingIndex = page; editingDoc = postDoc }
                            )
                        }
                    }
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                        IconButton(
                            onClick = { scope.launch { pagerState.animateScrollToPage((pagerState.currentPage - 1).coerceAtLeast(0)) } },
                            enabled = pagerState.currentPage > 0 && !running
                        ) {
                            Icon(Icons.Outlined.ArrowBack, contentDescription = "Previous post", tint = PostPalette.Text)
                        }
                        Text("Post ${pagerState.currentPage + 1} of ${reviewItems.size}", color = PostPalette.Text)
                        IconButton(
                            onClick = { scope.launch { pagerState.animateScrollToPage((pagerState.currentPage + 1).coerceAtMost((reviewItems.size - 1).coerceAtLeast(0))) } },
                            enabled = pagerState.currentPage < reviewItems.size - 1 && !running
                        ) {
                            Icon(Icons.Outlined.ArrowForward, contentDescription = "Next post", tint = PostPalette.Text)
                        }
                    }
                    if (initialBatch != null) {
                        val caption = initialBatch.captions.optString(pagerState.currentPage).takeIf { it.isNotBlank() }
                        if (caption != null) {
                            Text("Caption", color = PostPalette.Text, fontWeight = FontWeight.Bold)
                            Text(caption, color = PostPalette.Muted, fontSize = 13.sp)
                            OutlinedButton(onClick = { clipboard.setText(AnnotatedString(caption)) }, modifier = Modifier.fillMaxWidth()) { Text("Copy caption") }
                        }
                    }
                    OutlinedButton(onClick = {
                        checking = true
                        fitResults = emptyList()
                        val list = reviewItems.toList()
                        scope.launch {
                            val found = mutableListOf<String>()
                            list.forEach { item ->
                                val result = withContext(Dispatchers.Default) {
                                    PostRenderer.preview(context, pageDoc(item), currentBrand, 360) { !isActive }
                                }
                                if (result.overflow.isNotEmpty()) found.add("Post ${item.number}: " + result.overflow.joinToString { PostSlots.label(it) })
                                result.bitmap.recycle()
                            }
                            fitResults = found
                            status = if (found.isEmpty()) "All posts fit" else null
                            checking = false
                        }
                    }, enabled = reviewItems.isNotEmpty() && !running && !checking, modifier = Modifier.fillMaxWidth()) {
                        Text(if (checking) "Checking..." else "Check text fit on all posts")
                    }
                    if (fitResults.isNotEmpty() && !running && resultUris.isEmpty()) Text("Text is shrunk on:\n" + fitResults.joinToString("\n"), color = PostPalette.Gold, fontSize = 12.sp)
                    if (!checking && fitResults.isEmpty() && status == "All posts fit") Text(status!!, color = PostPalette.Mint, fontSize = 12.sp)

                    Text("Save as", color = PostPalette.Text, fontWeight = FontWeight.Bold)
                    Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        PostSize.values().forEach { size ->
                            FilterChip(selected = size in options.sizes, onClick = {
                                options = options.copy(sizes = if (size in options.sizes) options.sizes - size else options.sizes + size)
                            }, label = { Text(size.label) }, enabled = !running)
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        FilterChip(selected = options.png, onClick = { options = options.copy(png = true) }, label = { Text("PNG") }, enabled = !running)
                        FilterChip(selected = !options.png, onClick = { options = options.copy(png = false) }, label = { Text("JPG") }, enabled = !running)
                        FilterChip(selected = !options.fourK, onClick = { options = options.copy(fourK = false) }, label = { Text("Standard") }, enabled = !running)
                        FilterChip(selected = options.fourK, onClick = { options = options.copy(fourK = true) }, label = { Text("4K") }, enabled = !running)
                    }
                    if (running) {
                        Text("Saving to your gallery. Keep the app open until it is done.", color = PostPalette.Muted, fontSize = 12.sp)
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            CircularProgressIndicator(progress = { done.toFloat() / total.coerceAtLeast(1) }, color = PostPalette.Mint)
                            Text("Saving $done of $total", color = PostPalette.Text)
                            Spacer(Modifier.weight(1f))
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
                            Text(if (resultUris.isEmpty()) "Save to gallery ($fileCount files)" else "Save again to a new folder")
                        }
                    }
                    status?.takeIf { it != "All posts fit" }?.let { Text(it, color = PostPalette.Text, fontSize = 13.sp) }
                    if (resultUris.isNotEmpty() && !running && initialBatch != null) {
                        Text("Backend status: $backendStatus", color = PostPalette.Muted, fontSize = 12.sp)
                        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("Telegram", "Instagram", "Facebook", "TikTok").forEach { item ->
                                FilterChip(selected = platform == item, onClick = { platform = item }, label = { Text(item) })
                            }
                        }
                        OutlinedButton(onClick = {
                            scope.launch {
                                try {
                                    PostChatApi.setBatchStatus(context, initialBatch.id, "approved")
                                    backendStatus = "approved"
                                    backendMessage = "Batch marked approved"
                                } catch (e: Exception) { backendMessage = e.message ?: "Status update failed" }
                            }
                        }, modifier = Modifier.fillMaxWidth(), enabled = backendStatus != "approved" && backendStatus != "published") { Text("Mark approved") }
                        Button(onClick = {
                            scope.launch {
                                try {
                                    PostChatApi.setBatchStatus(context, initialBatch.id, "published")
                                    backendStatus = "published"
                                    val postedOn = LocalDate.now().toString()
                                    for (index in 0 until initialBatch.posts.length()) {
                                        val post = initialBatch.posts.optJSONObject(index) ?: continue
                                        PostChatApi.logPost(context, org.json.JSONObject()
                                            .put("posted_on", postedOn).put("platform", platform)
                                            .put("pillar", initialBatch.pillar).put("topic", initialBatch.title)
                                            .put("headline", post.optString("headline", initialBatch.title))
                                            .put("format", initialBatch.template).put("batch_id", initialBatch.id))
                                    }
                                    backendMessage = "Marked published and logged posts"
                                } catch (e: Exception) { backendMessage = e.message ?: "Publishing status update failed" }
                            }
                        }, modifier = Modifier.fillMaxWidth(), enabled = backendStatus != "published") { Text("Mark posted and log") }
                        backendMessage?.let { Text(it, color = PostPalette.Mint, fontSize = 12.sp) }
                    }
                    if (resultUris.isNotEmpty() && !running) {
                        if (resultUris.size <= 30) OutlinedButton(onClick = { shareResults() }, modifier = Modifier.fillMaxWidth()) {
                            Icon(Icons.Outlined.Share, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Share saved files")
                        } else Text("More than 30 files were saved. Open the batch folder in your gallery to share them.", color = PostPalette.Muted, fontSize = 12.sp)
                        if (fitResults.isNotEmpty()) Text("Text that shrank:\n" + fitResults.joinToString("\n"), color = PostPalette.Gold, fontSize = 12.sp)
                        OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("Done") }
                    }
                    Spacer(Modifier.height(20.dp))
                }
            }
        }

        // Reuse the complete main-screen editor for every inbox poster.
        val index = editingIndex
        val item = index?.let { reviewItems.getOrNull(it) }
        val activeEditorDoc = editingDoc
        if (index != null && item != null && activeEditorDoc != null) {
            Dialog(
                onDismissRequest = { editingIndex = null; editingDoc = null },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Column(Modifier.fillMaxSize().background(PostPalette.Ink)) {
                    Row(
                        Modifier.fillMaxWidth().background(PostPalette.Surface).padding(horizontal = 10.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Editing post ${item.number}", color = PostPalette.Text, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                        OutlinedButton(onClick = {
                            val source = editingDoc ?: return@OutlinedButton
                            reviewItems.forEach { target ->
                                editedDesigns[target.number] = source.copy(texts = doc.texts)
                            }
                            backendMessage = "Design applied to all ${reviewItems.size} posts"
                        }) { Text("Apply to all") }
                        Button(onClick = { editingIndex = null; editingDoc = null }) { Text("Done") }
                    }
                    Box(Modifier.weight(1f).fillMaxWidth()) {
                        PostStudioScreen(
                            doc = activeEditorDoc,
                            onDoc = { changed -> saveEditingDoc(changed) },
                            brand = currentBrand,
                            library = PostDrafts.loadLibrary(context) ?: PostLibrary(),
                            onBrand = { currentBrand = it },
                            onPickPhoto = { editorPhotoPicker.launch(arrayOf("image/*")) },
                            onPickBackgroundPhoto = { editorBackgroundPicker.launch(arrayOf("image/*")) },
                            onPickLogo = { editorLogoPicker.launch(arrayOf("image/*")) },
                            onOpenBrand = { backendMessage = "Close this editor to manage brand settings from the main studio." }
                        )
                    }
                }
            }
        }
    }
}

/** One swipeable page: the preview of one post and its Edit button. Fixed height so swiping never jumps. */
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
    LaunchedEffect(postDoc, brand) {
        val result = withContext(Dispatchers.Default) {
            PostRenderer.preview(context, postDoc, brand, 540) { !isActive }
        }
        bitmap = result.bitmap
        overflow = result.overflow
    }
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(
            Modifier.fillMaxWidth().height(420.dp).background(PostPalette.Surface, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            val shown = bitmap
            if (shown != null) {
                Image(
                    shown.asImageBitmap(),
                    contentDescription = "Preview of post $number",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize().padding(6.dp)
                )
            } else {
                CircularProgressIndicator(color = PostPalette.Mint)
            }
            if (overflow.isNotEmpty()) {
                Text(
                    "Text shrunk to fit: " + overflow.joinToString { PostSlots.label(it) },
                    color = PostPalette.Ink, fontSize = 12.sp,
                    modifier = Modifier.align(Alignment.BottomCenter).padding(8.dp)
                        .background(PostPalette.Gold, RoundedCornerShape(8.dp)).padding(8.dp)
                )
            }
            if (edited) {
                Text(
                    "Edited", color = PostPalette.Ink, fontSize = 12.sp, fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)
                        .background(PostPalette.Mint, RoundedCornerShape(8.dp)).padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
        OutlinedButton(onClick = onEdit, enabled = canEdit, modifier = Modifier.fillMaxWidth().height(48.dp)) {
            Icon(Icons.Outlined.Edit, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Edit post $number")
        }
    }
}
