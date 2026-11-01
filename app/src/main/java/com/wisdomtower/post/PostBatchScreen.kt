package com.wisdomtower.post

import android.content.Intent
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.ContentPaste
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.AnnotatedString
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

@Composable
fun PostBatchScreen(doc: PostDoc, brand: PostBrand, onBack: () -> Unit) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val view = LocalView.current
    val scope = rememberCoroutineScope()
    val slots = remember(doc.templateId, doc.size) {
        PostBatch.editableSlots(postTemplate(doc.templateId).layout(doc.size))
    }
    var mode by remember { mutableStateOf(PostBatchStore.loadMode(context)) }
    var text by remember(doc.templateId) { mutableStateOf(PostBatchStore.loadText(context, doc.templateId)) }
    var fill by remember(doc.templateId, slots) { mutableStateOf(PostBatchStore.loadFill(context, doc.templateId, slots)) }
    var options by remember(doc.size) { mutableStateOf(PostStore.loadSaveOptions(context, doc.size)) }
    var previewIndex by remember { mutableStateOf(0) }
    var previewBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var previewOverflow by remember { mutableStateOf<List<String>>(emptyList()) }
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
            importedPhotoPaths.filter { it != doc.photoUri }.forEach { PostStore.deleteImportedPhoto(context, it) }
            importedPhotoPaths.clear()
            importedPhotoPaths.addAll(stored)
            batchPhotos = stored
        }
    }

    val parsed = remember(text, mode, slots, fill) { PostBatch.parse(text, mode, slots, fill) }
    val items = parsed.items
    val safeIndex = previewIndex.coerceIn(0, (items.size - 1).coerceAtLeast(0))
    val itemDoc = remember(doc, items, safeIndex, batchPhotos) {
        val base = items.getOrNull(safeIndex)?.let { PostBatch.apply(doc, it) } ?: doc
        base.copy(photoUri = batchPhotos.getOrNull(safeIndex) ?: base.photoUri)
    }

    LaunchedEffect(text, doc.templateId) {
        delay(500)
        PostBatchStore.saveText(context, doc.templateId, text)
    }
    LaunchedEffect(mode) { PostBatchStore.saveMode(context, mode) }
    LaunchedEffect(fill, doc.templateId) { PostBatchStore.saveFill(context, doc.templateId, fill) }
    LaunchedEffect(itemDoc, brand) {
        val result = withContext(Dispatchers.Default) {
            PostRenderer.preview(context, itemDoc, brand, 540) { !isActive }
        }
        previewBitmap?.takeIf { it !== result.bitmap }?.recycle()
        previewBitmap = result.bitmap
        previewOverflow = result.overflow
    }
    LaunchedEffect(running) { view.keepScreenOn = running }
    val latestJob by rememberUpdatedState(runJob)
    val latestPreview by rememberUpdatedState(previewBitmap)
    DisposableEffect(Unit) {
        onDispose {
            view.keepScreenOn = false
            latestJob?.cancel()
            latestPreview?.recycle()
            importedPhotoPaths.filter { it != doc.photoUri }.forEach { PostStore.deleteImportedPhoto(context, it) }
            importedPhotoPaths.clear()
        }
    }

    fun startBatch() {
        if (items.isEmpty() || running) return
        PostStore.saveSaveOptions(context, options)
        val chosenSizes = PostSize.values().filter { it in options.sizes }.ifEmpty { listOf(doc.size) }
        val stamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val folder = "Batch " + SimpleDateFormat("yyyy-MM-dd HH-mm-ss", Locale.US).format(Date())
        val width = items.size.toString().length.coerceAtLeast(2)
        total = items.size
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
                for (item in items) {
                    kotlinx.coroutines.currentCoroutineContext().ensureActive()
                    if (cancelRequested) {
                        status = "Cancelled after $done of $total posts"
                        break
                    }
                    try {
                        val saved = withContext(Dispatchers.Default) {
                            PostExport.saveOne(
                                context, PostBatch.apply(doc, item).copy(photoUri = batchPhotos.getOrNull(item.number - 1) ?: doc.photoUri), brand, chosenSizes,
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
                if (status == null) status = "Saved ${savedUris.size} files to Pictures/Wisdom Tower Post/$folder"
                importedPhotoPaths.filter { it != doc.photoUri }.forEach { PostStore.deleteImportedPhoto(context, it) }
                importedPhotoPaths.clear()
                batchPhotos = emptyList()
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack, enabled = !running) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = "Back", tint = PostPalette.Text)
                    }
                    Column(Modifier.weight(1f)) {
                        Text("Batch posts", color = PostPalette.Text, fontSize = 19.sp, fontWeight = FontWeight.Bold)
                        Text(postTemplate(doc.templateId).name, color = PostPalette.Muted, fontSize = 12.sp)
                    }
                    OutlinedButton(onClick = { text = "" }, enabled = !running) {
                        Icon(Icons.Outlined.Delete, contentDescription = null)
                        Text("Clear")
                    }
                }
                Text("Every post uses this design, including its colors, photos, logo and fonts.", color = PostPalette.Muted, fontSize = 12.sp)
                if (hasPhotoArea) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        OutlinedButton(onClick = { photoPicker.launch(arrayOf("image/*")) }, enabled = !running) { Text("Add photos") }
                        Text(if (batchPhotos.isEmpty()) "Optional: one photo per post" else "${batchPhotos.size} photos selected", color = PostPalette.Muted, fontSize = 12.sp)
                        if (batchPhotos.isNotEmpty()) OutlinedButton(onClick = { importedPhotoPaths.filter { it != doc.photoUri }.forEach { PostStore.deleteImportedPhoto(context, it) }; importedPhotoPaths.clear(); batchPhotos = emptyList() }, enabled = !running) { Text("Clear photos") }
                    }
                }
                if (doc.photoUri != null || doc.backgroundPhotoUri != null) {
                    Text("The same photo will be used on every post in this batch.", color = PostPalette.Gold, fontSize = 12.sp)
                }
                Text("Sizes", color = PostPalette.Text, fontWeight = FontWeight.Bold)
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
                Text("Write your list", color = PostPalette.Text, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = mode == BatchMode.ONE_LINE, onClick = { mode = BatchMode.ONE_LINE }, label = { Text("One line = one post") }, enabled = !running)
                    FilterChip(selected = mode == BatchMode.BLOCKS, onClick = { mode = BatchMode.BLOCKS }, label = { Text("Full blocks") }, enabled = !running)
                }
                if (mode == BatchMode.ONE_LINE) {
                    Text("Fill this box. Other boxes keep the words from the open post.", color = PostPalette.Muted, fontSize = 12.sp)
                    Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        slots.forEach { slot ->
                            FilterChip(selected = fill == slot, onClick = { fill = slot }, label = { Text(PostSlots.label(slot)) }, enabled = !running)
                        }
                    }
                } else {
                    Text("Separate posts with --- . Use labels such as headline:, message: and list:, or write one box per line.", color = PostPalette.Muted, fontSize = 12.sp)
                }
                OutlinedTextField(
                    value = text, onValueChange = { text = it },
                    modifier = Modifier.fillMaxWidth().height(190.dp),
                    placeholder = { Text(if (mode == BatchMode.ONE_LINE) "One post per line..." else "headline: Your headline\nmessage: Your message\n---\nheadline: Next post") },
                    enabled = !running,
                    minLines = 8,
                    textStyle = androidx.compose.ui.text.TextStyle(color = PostPalette.Text, fontSize = 14.sp)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { text = clipboard.getText()?.text.orEmpty() }, enabled = !running) {
                        Icon(Icons.Outlined.ContentPaste, contentDescription = null)
                        Text("Paste")
                    }
                    OutlinedButton(onClick = { text = PostBatch.exampleFor(slots, mode) }, enabled = !running) { Text("Example") }
                    Text("${items.size} posts", color = PostPalette.Gold, modifier = Modifier.align(Alignment.CenterVertically))
                }
                if (parsed.notes.isNotEmpty()) {
                    Text(parsed.notes.distinct().joinToString("\n"), color = PostPalette.Gold, fontSize = 12.sp)
                }
                Text("Preview", color = PostPalette.Text, fontWeight = FontWeight.Bold)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                    IconButton(onClick = { previewIndex = (safeIndex - 1).coerceAtLeast(0) }, enabled = safeIndex > 0 && !running) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = "Previous post", tint = PostPalette.Text)
                    }
                    Text(if (items.isEmpty()) "No posts" else "Post ${safeIndex + 1} of ${items.size}", color = PostPalette.Text)
                    IconButton(onClick = { previewIndex = (safeIndex + 1).coerceAtMost(items.lastIndex) }, enabled = safeIndex < items.lastIndex && !running) {
                        Icon(Icons.Outlined.ArrowForward, contentDescription = "Next post", tint = PostPalette.Text)
                    }
                }
                previewBitmap?.let { bitmap ->
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Image(bitmap.asImageBitmap(), contentDescription = "Batch post preview",
                            modifier = Modifier.fillMaxWidth(0.72f).aspectRatio(bitmap.width.toFloat() / bitmap.height).background(PostPalette.Surface, RoundedCornerShape(8.dp)))
                    }
                } ?: Box(Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = PostPalette.Mint) }
                if (previewOverflow.isNotEmpty()) {
                    Text("Text shrunk to fit: " + previewOverflow.joinToString { PostSlots.label(it) }, color = PostPalette.Ink, fontSize = 12.sp,
                        modifier = Modifier.background(PostPalette.Gold, RoundedCornerShape(8.dp)).padding(8.dp))
                }
                OutlinedButton(onClick = {
                    checking = true
                    fitResults = emptyList()
                    scope.launch {
                        val found = mutableListOf<String>()
                        items.forEach { item ->
                            val result = withContext(Dispatchers.Default) {
                                PostRenderer.preview(context, PostBatch.apply(doc, item).copy(photoUri = batchPhotos.getOrNull(item.number - 1) ?: doc.photoUri), brand, 360) { !isActive }
                            }
                            if (result.overflow.isNotEmpty()) found.add("Post ${item.number}: " + result.overflow.joinToString { PostSlots.label(it) })
                            result.bitmap.recycle()
                        }
                        fitResults = found
                        status = if (found.isEmpty()) "All posts fit" else null
                        checking = false
                    }
                }, enabled = items.isNotEmpty() && !running && !checking, modifier = Modifier.fillMaxWidth()) {
                    Text(if (checking) "Checking..." else "Check text fit")
                }
                if (checking) Text("Checking every post at preview size...", color = PostPalette.Muted, fontSize = 12.sp)
                if (fitResults.isNotEmpty()) Text(fitResults.joinToString("\n"), color = PostPalette.Gold, fontSize = 12.sp)
                if (!checking && fitResults.isEmpty() && status == "All posts fit") Text(status!!, color = PostPalette.Mint, fontSize = 12.sp)
                if (running) {
                    Text("Keep the app open until it is done.", color = PostPalette.Muted, fontSize = 12.sp)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        CircularProgressIndicator(progress = done.toFloat() / total.coerceAtLeast(1), color = PostPalette.Mint)
                        Text("Saving $done of $total", color = PostPalette.Text)
                        Spacer(Modifier.weight(1f))
                        OutlinedButton(onClick = { cancelRequested = true }) { Text("Cancel") }
                    }
                } else {
                    Button(onClick = { startBatch() }, enabled = items.isNotEmpty(), modifier = Modifier.fillMaxWidth()) {
                        Text("Make ${items.size} posts (${items.size * (PostSize.values().count { it in options.sizes }.coerceAtLeast(1))} files)")
                    }
                }
                status?.let { Text(it, color = PostPalette.Text, fontSize = 13.sp) }
                if (resultUris.isNotEmpty() && !running) {
                    if (resultUris.size <= 30) OutlinedButton(onClick = { shareResults() }, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Outlined.Share, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Share saved files")
                    } else Text("More than 30 files were saved. Open the batch folder to share them.", color = PostPalette.Muted, fontSize = 12.sp)
                    if (fitResults.isNotEmpty()) Text("Text that shrank:\n" + fitResults.joinToString("\n"), color = PostPalette.Gold, fontSize = 12.sp)
                }
                Spacer(Modifier.height(20.dp))
            }
        }
    }
}
