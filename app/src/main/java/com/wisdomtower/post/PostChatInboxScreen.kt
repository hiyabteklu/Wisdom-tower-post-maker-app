package com.wisdomtower.post

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.FactCheck
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

/** Folder order in the inbox: the 8 pillars of the Brand Kit PLAYBOOK, then the catch-all folders. Any other name is added after these. */
private val FOLDER_ORDER = listOf(
    "Academic Mastery", "Study Systems", "Commercial Offers", "Community & Retention",
    "Student Culture & Memes", "Skills & Campus Life", "Proof & Celebrations", "Official Notices",
    "Other", "Test samples", "Unsorted"
)

/** Key of the "All batches" shortcut folder (every batch, newest first). */
private const val ALL_KEY = "__all__"

/**
 * The Poster inbox, organised in folders.
 * Level 1: folder list (one folder per pillar, with batch and post counts and a draft badge), plus "All batches".
 * Level 2: the batches of one folder, newest first. When a folder has more than one subfolder (by default the level, for example Freshman or Grade 12), a chip row filters them.
 * The status chips (All, Draft, Approved, Posted) apply on both levels. Back from a folder returns to the folder list.
 * It opens at once from the list kept in memory (or the one saved on disk) and refreshes quietly behind it, so there is no blank wait.
 * Pull down or tap the arrow to refresh. Tap a card to open the batch.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostChatInboxScreen(onBack: () -> Unit, onOpenBatch: (PostChatBatch) -> Unit, onOpenQa: (() -> Unit)? = null) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var batches by remember {
        mutableStateOf(if (PostInboxState.batches.isNotEmpty()) PostInboxState.batches else PostChatStore.loadCachedBatches(context))
    }
    var refreshing by remember { mutableStateOf(false) }
    var opening by remember { mutableStateOf<String?>(null) }
    var notice by remember { mutableStateOf<String?>(null) }
    var filter by rememberSaveable { mutableStateOf("all") }
    var openFolder by rememberSaveable { mutableStateOf<String?>(null) }
    var openSub by rememberSaveable { mutableStateOf<String?>(null) }

    fun refresh() {
        if (refreshing) return
        refreshing = true
        scope.launch {
            try {
                val fresh = PostChatApi.listBatches(context)
                PostInboxState.update(fresh)
                batches = fresh
                notice = null
            } catch (e: Exception) {
                notice = if (batches.isNotEmpty()) "Offline. Showing the last saved list." else (e.message ?: "Could not load batches")
            } finally {
                refreshing = false
            }
        }
    }
    // Refresh on entry only when the list is older than 15 seconds.
    LaunchedEffect(Unit) { if (System.currentTimeMillis() - PostInboxState.loadedAt > 15_000L) refresh() }

    val currentFolder = openFolder
    val currentSub = openSub
    BackHandler(enabled = currentFolder != null) { openFolder = null; openSub = null }

    val byStatus = if (filter == "all") batches else batches.filter { it.status == filter }
    val inFolder = when (currentFolder) {
        null -> emptyList()
        ALL_KEY -> byStatus
        else -> byStatus.filter { it.folder == currentFolder }
    }
    val subCounts = inFolder.groupingBy { it.subfolder }.eachCount()
    val shown = if (currentSub == null) inFolder else inFolder.filter { it.subfolder == currentSub }
    val folderNames = (FOLDER_ORDER + byStatus.map { it.folder }.distinct().filter { it !in FOLDER_ORDER })
        .filter { name -> byStatus.any { it.folder == name } }

    PostTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            when (currentFolder) { null -> "Poster inbox"; ALL_KEY -> "All batches"; else -> currentFolder },
                            fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { if (currentFolder != null) { openFolder = null; openSub = null } else onBack() }) {
                            Icon(Icons.Outlined.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        if (onOpenQa != null) {
                            IconButton(onClick = onOpenQa) { Icon(Icons.Outlined.FactCheck, contentDescription = "Template QA: check every template") }
                        }
                        IconButton(onClick = { refresh() }, enabled = !refreshing) { Icon(Icons.Outlined.Refresh, contentDescription = "Refresh") }
                    }
                )
            },
            containerColor = PostPalette.Ink
        ) { padding ->
            PullToRefreshBox(
                isRefreshing = refreshing,
                onRefresh = { refresh() },
                modifier = Modifier.fillMaxSize().padding(padding)
            ) {
                LazyColumn(
                    Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 4.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item(key = "filters") {
                        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("all" to "All", "draft" to "Draft", "approved" to "Approved", "published" to "Posted").forEach { (key, label) ->
                                val n = if (key == "all") batches.size else batches.count { it.status == key }
                                FilterChip(selected = filter == key, onClick = { filter = key }, label = { Text("$label $n", fontSize = 12.sp) })
                            }
                        }
                    }
                    notice?.let { text ->
                        item(key = "notice") { Text(text, color = PostPalette.Muted, fontSize = 13.sp) }
                    }

                    if (currentFolder == null) {
                        // Level 1: the folder list.
                        if (byStatus.isNotEmpty()) {
                            item(key = "folder_all") {
                                FolderCard("All batches", byStatus.size, byStatus.sumOf { it.postCount }, byStatus.count { it.status == "draft" }) {
                                    openFolder = ALL_KEY; openSub = null
                                }
                            }
                            items(folderNames, key = { "folder_$it" }) { name ->
                                val inThis = byStatus.filter { it.folder == name }
                                FolderCard(name, inThis.size, inThis.sumOf { it.postCount }, inThis.count { it.status == "draft" }) {
                                    openFolder = name; openSub = null
                                }
                            }
                        } else if (!refreshing) {
                            item(key = "empty") {
                                EmptyState(
                                    if (batches.isEmpty()) "Nothing here yet" else "No batches in this filter",
                                    "Ask your AI chat for posters, for example: give me 10 posts about Freshman mistakes. They land here, in the folder of their pillar."
                                )
                            }
                        }
                    } else {
                        // Level 2: the batches of one folder.
                        if (subCounts.size > 1) {
                            item(key = "subfolders") {
                                Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    FilterChip(selected = currentSub == null, onClick = { openSub = null }, label = { Text("Everything ${inFolder.size}", fontSize = 12.sp) })
                                    subCounts.entries.sortedBy { it.key.lowercase() }.forEach { (sub, n) ->
                                        FilterChip(
                                            selected = currentSub == sub,
                                            onClick = { openSub = sub },
                                            label = { Text("${sub.ifBlank { "General" }} $n", fontSize = 12.sp) }
                                        )
                                    }
                                }
                            }
                        }
                        if (shown.isEmpty() && !refreshing) {
                            item(key = "empty") { EmptyState("No batches in this folder", "Try another filter, or pull down to refresh.") }
                        }
                        items(shown, key = { it.id }) { batch ->
                            BatchCard(batch, busy = opening == batch.id, folderLabel = if (currentFolder == ALL_KEY) batch.folder else null) {
                                if (opening != null) return@BatchCard
                                opening = batch.id
                                notice = null
                                scope.launch {
                                    try { onOpenBatch(PostChatApi.getBatch(context, batch.id)) }
                                    catch (e: Exception) { notice = e.message ?: "Could not open batch" }
                                    finally { opening = null }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyState(title: String, hint: String) {
    Column(
        Modifier.fillMaxWidth().padding(top = 48.dp, start = 24.dp, end = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(title, color = PostPalette.Text, fontWeight = FontWeight.Bold, fontSize = 17.sp)
        Text(hint, color = PostPalette.Muted, fontSize = 14.sp, textAlign = TextAlign.Center)
    }
}

@Composable
private fun FolderCard(name: String, batches: Int, posts: Int, drafts: Int, onOpen: () -> Unit) {
    val shape = RoundedCornerShape(16.dp)
    Row(
        Modifier.fillMaxWidth().clip(shape).background(PostPalette.Surface)
            .border(BorderStroke(1.dp, PostPalette.Line), shape)
            .clickable(onClick = onOpen).padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(PostPalette.Mint.copy(alpha = 0.14f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Outlined.Folder, contentDescription = null, tint = PostPalette.Mint)
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(name, color = PostPalette.Text, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(
                "$batches " + (if (batches == 1) "batch" else "batches") + " \u00b7 $posts " + (if (posts == 1) "post" else "posts"),
                color = PostPalette.Muted, fontSize = 13.sp
            )
        }
        if (drafts > 0) {
            Box(Modifier.clip(RoundedCornerShape(8.dp)).background(PostPalette.Gold.copy(alpha = 0.16f)).padding(horizontal = 8.dp, vertical = 2.dp)) {
                Text("$drafts draft", color = PostPalette.Gold, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.width(8.dp))
        }
        Icon(Icons.Outlined.ChevronRight, contentDescription = "Open folder", tint = PostPalette.Muted)
    }
}

@Composable
private fun BatchCard(batch: PostChatBatch, busy: Boolean, folderLabel: String? = null, onOpen: () -> Unit) {
    val shape = RoundedCornerShape(16.dp)
    Row(
        Modifier.fillMaxWidth().clip(shape).background(PostPalette.Surface)
            .border(BorderStroke(1.dp, PostPalette.Line), shape)
            .clickable(onClick = onOpen).padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            Modifier.size(52.dp).clip(RoundedCornerShape(12.dp)).background(PostPalette.Mint.copy(alpha = 0.14f)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(batch.postCount.toString(), color = PostPalette.Mint, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
            Text(if (batch.postCount == 1) "post" else "posts", color = PostPalette.Mint, fontSize = 10.sp)
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(batch.title, color = PostPalette.Text, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Text(
                (if (folderLabel != null) "$folderLabel \u00b7 " else "") + batch.level.ifBlank { "General" } + " \u00b7 " + templateName(batch.template),
                color = PostPalette.Muted, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis
            )
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatusChip(batch.status)
                val age = ago(batch.createdAt)
                if (age.isNotEmpty()) Text(age, color = PostPalette.Muted, fontSize = 12.sp)
            }
        }
        Spacer(Modifier.width(8.dp))
        if (busy) CircularProgressIndicator(Modifier.size(22.dp), color = PostPalette.Mint, strokeWidth = 2.dp)
        else Icon(Icons.Outlined.ChevronRight, contentDescription = "Open batch", tint = PostPalette.Muted)
    }
}

@Composable
private fun StatusChip(status: String) {
    val (label, color) = when (status) {
        "approved" -> "Approved" to PostPalette.Mint
        "published" -> "Posted" to Color(0xFF7BD88F)
        else -> "Draft" to PostPalette.Gold
    }
    Box(Modifier.clip(RoundedCornerShape(8.dp)).background(color.copy(alpha = 0.16f)).padding(horizontal = 8.dp, vertical = 2.dp)) {
        Text(label, color = color, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
    }
}

/** The template's own name when the app knows it, otherwise the raw id. */
private fun templateName(id: String): String {
    val spec = postTemplate(id)
    return if (spec.id == id) spec.name else id
}

/** "5 min ago", "2 h ago", "yesterday"; empty when the date cannot be read. */
private fun ago(iso: String): String = runCatching {
    val then = java.time.OffsetDateTime.parse(iso).toInstant().toEpochMilli()
    val minutes = (System.currentTimeMillis() - then) / 60_000L
    when {
        minutes < 1L -> "just now"
        minutes < 60L -> "$minutes min ago"
        minutes < 1440L -> "${minutes / 60L} h ago"
        minutes < 2880L -> "yesterday"
        else -> "${minutes / 1440L} days ago"
    }
}.getOrDefault("")
