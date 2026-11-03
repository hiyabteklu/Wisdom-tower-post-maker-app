package com.wisdomtower.post

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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.FactCheck
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

/**
 * The Poster inbox, the first tab of the bottom bar.
 * It opens at once from the list kept in memory (or the one saved on disk) and refreshes quietly behind it, so there is no blank wait.
 * Pull down or tap the arrow to refresh. Tap a card to open the batch. Filters: All, Draft, Approved, Posted.
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
    // Refresh on entry only when the list is older than 15 seconds, so switching tabs back and forth costs nothing.
    LaunchedEffect(Unit) { if (System.currentTimeMillis() - PostInboxState.loadedAt > 15_000L) refresh() }

    val shown = if (filter == "all") batches else batches.filter { it.status == filter }

    PostTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Inbox", fontWeight = FontWeight.Bold) },
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
                    if (shown.isEmpty() && !refreshing) {
                        item(key = "empty") {
                            Column(
                                Modifier.fillMaxWidth().padding(top = 48.dp, start = 24.dp, end = 24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(if (batches.isEmpty()) "Nothing here yet" else "No batches in this filter", color = PostPalette.Text, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                                Text(
                                    "Ask your AI chat for posters, for example: give me 10 posts about Freshman mistakes. They land here.",
                                    color = PostPalette.Muted, fontSize = 14.sp, textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                    items(shown, key = { it.id }) { batch ->
                        BatchCard(batch, busy = opening == batch.id) {
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

@Composable
private fun BatchCard(batch: PostChatBatch, busy: Boolean, onOpen: () -> Unit) {
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
                batch.level.ifBlank { "General" } + " · " + templateName(batch.template),
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
