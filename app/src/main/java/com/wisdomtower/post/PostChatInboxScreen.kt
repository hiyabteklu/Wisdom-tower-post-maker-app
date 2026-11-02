package com.wisdomtower.post

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostChatInboxScreen(onBack: () -> Unit, onOpenBatch: (PostChatBatch) -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var batches by remember { mutableStateOf<List<PostChatBatch>>(emptyList()) }
    var busy by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("Loading batches…") }

    fun refresh() {
        if (busy) return
        busy = true
        scope.launch {
            try {
                batches = PostChatApi.listBatches(context)
                message = if (batches.isEmpty()) "No batches yet. Ask your AI chat to save a poster batch." else ""
            } catch (e: Exception) {
                batches = PostChatStore.loadCachedBatches(context)
                message = if (batches.isNotEmpty()) "Offline · showing the last saved inbox" else (e.message ?: "Could not load batches")
            }
            finally { busy = false }
        }
    }
    LaunchedEffect(Unit) { refresh() }

    PostTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Poster inbox", fontWeight = FontWeight.Bold) },
                    navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Outlined.ArrowBack, contentDescription = "Back") } },
                    actions = { IconButton(onClick = { refresh() }, enabled = !busy) { Icon(Icons.Outlined.Refresh, contentDescription = "Refresh") } }
                )
            },
            containerColor = PostPalette.Ink
        ) { padding ->
            Column(Modifier.fillMaxSize().padding(padding).padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (busy) CircularProgressIndicator()
                if (message.isNotBlank()) Text(message, color = PostPalette.Muted)
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(batches, key = { it.id }) { batch ->
                        Card(colors = CardDefaults.cardColors(containerColor = PostPalette.Surface), modifier = Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(batch.title, color = PostPalette.Text, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                                Text("${batch.level.ifBlank { "General" }} · ${batch.template} · ${batch.postCount} posts", color = PostPalette.Muted)
                                Text("Status: ${batch.status}", color = PostPalette.Muted)
                                Button(onClick = {
                                    busy = true
                                    message = "Opening batch…"
                                    scope.launch {
                                        try { onOpenBatch(PostChatApi.getBatch(context, batch.id)) }
                                        catch (e: Exception) { message = e.message ?: "Could not open batch" }
                                        finally { busy = false }
                                    }
                                }, enabled = !busy, modifier = Modifier.fillMaxWidth()) { Text("Open batch") }
                            }
                        }
                    }
                }
            }
        }
    }
}
