package com.wisdomtower.post

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

/**
 * Saving edits of an inbox batch back to Supabase (docs/URGENT_PLAN.md item 3, slices 3a and 3b).
 * Uses the wt-mcp v5 tool `update_batch` (live since 15 Nov 2026): replaces posts and/or captions of a DRAFT or APPROVED batch.
 * A published batch is refused by the server. Status, folder and the post log are never touched.
 * Kept in its own file so PostChat.kt (which holds the built-in key) did not have to be rewritten.
 */
object PostBatchSync {
    /** Sends only what is not null. posts replaces the whole posts list of the batch on the server. */
    suspend fun updateBatch(context: Context, id: String, posts: JSONArray?, captions: JSONArray?, title: String? = null) {
        val args = JSONObject().put("id", id)
        if (posts != null) args.put("posts", posts)
        if (captions != null) args.put("captions", captions)
        if (title != null) args.put("title", title)
        callTool(context, "update_batch", args)
    }

    private suspend fun callTool(context: Context, tool: String, args: JSONObject): String = withContext(Dispatchers.IO) {
        val endpoint = PostChatStore.loadUrl(context).trim()
        val secret = PostChatStore.loadKey(context)
        if (!endpoint.startsWith("https://") || !endpoint.contains(".supabase.co/") ||
            !endpoint.endsWith("/functions/v1/wt-mcp")) throw IllegalStateException("Configure the wt-mcp HTTPS endpoint")
        if (secret.length < 24) throw IllegalStateException("Open Chat link settings and enter the secret key")
        val connection = (URL(endpoint).openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"; connectTimeout = 12_000; readTimeout = 12_000; doOutput = true
            setRequestProperty("Content-Type", "application/json"); setRequestProperty("Accept", "application/json")
            setRequestProperty("x-wt-key", secret)
        }
        try {
            val params = JSONObject().put("name", tool).put("arguments", args)
            val body = JSONObject().put("jsonrpc", "2.0").put("id", 1).put("method", "tools/call").put("params", params).toString()
            connection.outputStream.use { it.write(body.toByteArray(Charsets.UTF_8)) }
            val status = connection.responseCode
            if (status == 401) throw IllegalStateException("Wrong key")
            if (status !in 200..299) throw IllegalStateException("Server returned HTTP $status")
            val response = JSONObject(connection.inputStream.bufferedReader().use { it.readText() })
            if (response.has("error")) throw IllegalStateException(response.optJSONObject("error")?.optString("message") ?: "Backend error")
            val result = response.optJSONObject("result") ?: throw IllegalStateException("Unexpected backend response")
            val text = result.optJSONArray("content")?.optJSONObject(0)?.optString("text").orEmpty()
            if (result.optBoolean("isError")) throw IllegalStateException(text.ifBlank { "Could not save changes" })
            text
        } catch (e: java.net.UnknownHostException) {
            throw IllegalStateException("No internet or server not found")
        } catch (e: java.net.SocketTimeoutException) {
            throw IllegalStateException("Connection timed out")
        } finally { connection.disconnect() }
    }

    /** The wt-mcp box name for a PostDoc slot. Note the message box is called BODY in the app and "message" on the server. */
    private fun backendKey(slot: String): String? = when (slot) {
        PostSlots.EYEBROW -> "eyebrow"
        PostSlots.HEADLINE -> "headline"
        PostSlots.BODY -> "message"
        PostSlots.NUMBER -> "number"
        PostSlots.LIST -> "list"
        PostSlots.BUTTON -> "button"
        else -> null
    }

    /**
     * The cloud posts with the edited words laid over them. Every other field of a post is kept as it came from the server.
     * Returns null when the number of posts on the phone differs from the cloud copy (then the words are NOT synced, so a post can never
     * be written to the wrong place).
     */
    fun mergePosts(original: JSONArray, items: List<BatchItem>): JSONArray? {
        if (items.size != original.length()) return null
        val out = JSONArray()
        for (i in 0 until original.length()) {
            val base = original.optJSONObject(i) ?: return null
            val item = items.firstOrNull { it.number == i + 1 } ?: return null
            val post = JSONObject(base.toString())
            item.texts.forEach { (slot, value) ->
                val key = backendKey(slot) ?: return@forEach
                val clean = value.trim()
                if (clean.isEmpty()) {
                    post.remove(key)
                } else if (key == "list") {
                    post.put(key, JSONArray(clean.split('\n').map { it.trim() }.filter { it.isNotEmpty() }))
                } else {
                    post.put(key, clean)
                }
            }
            out.put(post)
        }
        return out
    }

    fun textSignature(items: List<BatchItem>): String =
        items.joinToString("\u0001") { item ->
            item.number.toString() + "\u0002" + item.texts.toSortedMap().entries.joinToString("\u0003") { it.key + "=" + it.value }
        }

    fun captionSignature(count: Int, caption: (Int) -> String): String =
        (1..count).joinToString("\u0001") { caption(it) }
}

private enum class SavePhase { IDLE, SAVING, OFFLINE, ERROR }

/** A draft saves itself this long after the last edit. */
private const val AUTOSAVE_DELAY_MS = 2_000L
/** While offline, the unsent edits are tried again at this interval. */
private const val OFFLINE_RETRY_MS = 15_000L

/**
 * One clear save state for an inbox batch in the review step: "Saved to the cloud", "Unsaved changes", "Saving...",
 * "Waiting for internet" or the server's error.
 * DRAFT batches save themselves about 2 seconds after the last edit (the state line always shows what is happening), and while the phone
 * is offline the unsent edits are retried every 15 seconds while this screen is open. Captions typed on the phone are also kept in
 * preferences (PostBatchScreen), so after the app is closed offline they are found again and sent when the batch is reopened.
 * Words are not kept across a restart yet (docs/URGENT_PLAN.md item 3).
 * An APPROVED (planned) batch never saves by itself: the button asks first. A published batch cannot be edited online.
 * A server error (for example a rejected post) is NOT retried automatically; press Save after fixing it.
 * Not compiled when written (see docs/URGENT_PLAN.md).
 */
@Composable
fun BatchSaveBar(
    batch: PostChatBatch,
    items: List<BatchItem>,
    originalItems: List<BatchItem>,
    captions: Map<Int, String>,
    backendStatus: String
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val count = maxOf(batch.posts.length(), batch.captions.length())
    var savedTexts by remember(batch.id) { mutableStateOf(PostBatchSync.textSignature(originalItems)) }
    var savedCaps by remember(batch.id) { mutableStateOf(PostBatchSync.captionSignature(count) { batch.captions.optString(it - 1) }) }
    var phase by remember(batch.id) { mutableStateOf(SavePhase.IDLE) }
    var error by remember(batch.id) { mutableStateOf<String?>(null) }
    var confirmApproved by remember(batch.id) { mutableStateOf(false) }

    val nowTexts = PostBatchSync.textSignature(items)
    val nowCaps = PostBatchSync.captionSignature(count) { captions[it].orEmpty() }
    val textsDirty = nowTexts != savedTexts
    val capsDirty = nowCaps != savedCaps
    val dirty = textsDirty || capsDirty
    val published = backendStatus == "published"
    val approved = backendStatus == "approved"
    // Words can only be written back when the phone and the cloud agree on how many posts the batch has.
    val wordsBlocked = textsDirty && items.size != batch.posts.length()

    fun doSave() {
        if (phase == SavePhase.SAVING) return
        val posts = if (textsDirty) PostBatchSync.mergePosts(batch.posts, items.toList()) else null
        val capsArray = if (capsDirty) JSONArray().also { arr -> for (n in 1..count) arr.put(captions[n].orEmpty()) } else null
        if (posts == null && capsArray == null) {
            phase = SavePhase.ERROR
            error = "The edited words cannot be matched to the cloud copy (the post count differs). Nothing was saved."
            return
        }
        val textsToMark = nowTexts
        val capsToMark = nowCaps
        phase = SavePhase.SAVING
        error = null
        scope.launch {
            try {
                PostBatchSync.updateBatch(context, batch.id, posts, capsArray)
                if (posts != null) savedTexts = textsToMark
                if (capsArray != null) savedCaps = capsToMark
                phase = SavePhase.IDLE
            } catch (e: Exception) {
                val message = e.message ?: "Could not save"
                error = message
                phase = if (message.startsWith("No internet") || message.startsWith("Connection timed out")) SavePhase.OFFLINE else SavePhase.ERROR
            }
        }
    }

    // Autosave for drafts, and the retry while offline. Restarts on every edit and every state change; a server error stops it.
    LaunchedEffect(batch.id, nowTexts, nowCaps, phase, backendStatus) {
        if (!dirty || published || approved) return@LaunchedEffect
        if (phase == SavePhase.SAVING || phase == SavePhase.ERROR) return@LaunchedEffect
        // Words that cannot be matched to the cloud copy must not loop: only captions are auto-saved then.
        if (textsDirty && !capsDirty && wordsBlocked) return@LaunchedEffect
        delay(if (phase == SavePhase.OFFLINE) OFFLINE_RETRY_MS else AUTOSAVE_DELAY_MS)
        doSave()
    }

    val label: String
    val color: androidx.compose.ui.graphics.Color
    when {
        published -> { label = "This batch is posted. Posted batches cannot be edited online."; color = PostPalette.Muted }
        phase == SavePhase.SAVING -> { label = "Saving..."; color = PostPalette.Text }
        !dirty -> { label = "Saved: same as the cloud copy"; color = PostPalette.Mint }
        phase == SavePhase.OFFLINE -> { label = "Waiting for internet. Your edits will be sent when you are online again."; color = PostPalette.Gold }
        phase == SavePhase.ERROR -> { label = "Could not save: " + (error ?: "unknown error"); color = PostPalette.Gold }
        approved -> { label = "Unsaved changes. This batch is approved, so press Save to confirm."; color = PostPalette.Gold }
        else -> { label = "Unsaved changes, saving in a moment"; color = PostPalette.Gold }
    }

    Column(
        Modifier.fillMaxWidth().background(PostPalette.Surface, RoundedCornerShape(12.dp)).padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Cloud copy", color = PostPalette.Text, fontWeight = FontWeight.Bold)
        Text(label, color = color, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        if (wordsBlocked && !published) {
            Text("Edited words cannot be saved to the cloud for this batch (post count differs). Captions still save.", color = PostPalette.Gold, fontSize = 12.sp)
        }
        if (!published) {
            Button(
                onClick = { if (approved) confirmApproved = true else doSave() },
                enabled = dirty && phase != SavePhase.SAVING,
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) { Text("Save now") }
        }
    }

    if (confirmApproved) {
        AlertDialog(
            onDismissRequest = { confirmApproved = false },
            title = { Text("Save over an approved batch?") },
            text = { Text("This batch is approved (planned). Your changes will replace the words and captions saved in the cloud. The old version is not kept.") },
            confirmButton = { TextButton(onClick = { confirmApproved = false; doSave() }) { Text("Save changes") } },
            dismissButton = { TextButton(onClick = { confirmApproved = false }) { Text("Cancel") } }
        )
    }
}
