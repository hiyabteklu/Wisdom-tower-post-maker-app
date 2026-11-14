package com.wisdomtower.post

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.json.JSONObject
import java.time.LocalDate

/**
 * Inbox actions (docs/URGENT_PLAN.md, U1).
 *
 * Mark posted: the batch is set to "published" in Supabase and one row per post and platform goes into wt_post.post_log.
 * Delete draft: a DRAFT batch is deleted in Supabase (wt-mcp delete_batch, v4). The server refuses approved and posted batches.
 * Remove a posted batch: it is only hidden on this phone. Supabase (the batch and its post log rows) is not touched.
 *
 * Nothing here has been compiled yet (see the plan).
 */

/** The platforms of the "Mark posted" picker, in the order they are shown. */
val POST_PLATFORMS = listOf("Telegram", "TikTok", "LinkedIn", "Instagram", "Facebook")

/** Posted batches the owner removed from the inbox of this phone. They stay in Supabase and in the post log. */
internal object PostInboxHidden {
    private const val PREFS = "wt_post_inbox_hidden"
    private const val KEY = "ids"

    fun ids(context: Context): Set<String> =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getStringSet(KEY, emptySet()).orEmpty().toSet()

    fun hide(context: Context, id: String) {
        val next = ids(context) + id
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putStringSet(KEY, next).apply()
    }
}

private fun JSONObject.text(key: String): String = if (isNull(key)) "" else optString(key).trim()

/** The line that names a post in the log: its headline, else its small label, message or number, else the batch title. */
private fun postName(post: JSONObject?, fallback: String): String {
    if (post == null) return fallback
    return listOf("headline", "eyebrow", "message", "number").map { post.text(it) }.firstOrNull { it.isNotBlank() }?.take(300) ?: fallback
}

/**
 * Sets the batch to published, then logs one row per post and platform. Returns how many rows were logged.
 * The status is set first, so a batch is never left looking unposted after the owner said it was posted; if the log fails half way,
 * the error message says so and the rows already written stay.
 */
internal suspend fun markBatchPosted(
    context: Context,
    batchId: String,
    platforms: List<String>,
    onProgress: (done: Int, total: Int) -> Unit
): Int {
    val batch = PostChatApi.getBatch(context, batchId)
    PostChatApi.setBatchStatus(context, batchId, "published")
    val postedOn = LocalDate.now().toString()
    val count = batch.posts.length().coerceAtLeast(1)
    val total = count * platforms.size
    var done = 0
    try {
        for (i in 0 until count) {
            val name = postName(batch.posts.optJSONObject(i), batch.title)
            for (platform in platforms) {
                PostChatApi.logPost(
                    context,
                    JSONObject()
                        .put("posted_on", postedOn)
                        .put("platform", platform)
                        .put("pillar", batch.pillar)
                        .put("topic", batch.title)
                        .put("headline", name)
                        .put("format", batch.template)
                        .put("batch_id", batchId)
                )
                done++
                onProgress(done, total)
            }
        }
    } catch (e: Exception) {
        throw IllegalStateException("Marked posted, but only $done of $total log rows were saved: " + (e.message ?: "log failed"))
    }
    return done
}

/** Pick every platform the batch was posted on. */
@Composable
internal fun PlatformPickerDialog(batchTitle: String, onDismiss: () -> Unit, onConfirm: (List<String>) -> Unit) {
    var picked by remember { mutableStateOf(setOf<String>()) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Mark as posted") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    "Where did you post \"$batchTitle\"? Pick every platform. One post log row is saved per post and platform.",
                    fontSize = 13.sp
                )
                POST_PLATFORMS.chunked(2).forEach { line ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        line.forEach { platform ->
                            FilterChip(
                                selected = platform in picked,
                                onClick = { picked = if (platform in picked) picked - platform else picked + platform },
                                label = { Text(platform) }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(enabled = picked.isNotEmpty(), onClick = { onConfirm(POST_PLATFORMS.filter { it in picked }) }) { Text("Mark posted") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

/** A plain yes / no question with one clear action word. */
@Composable
internal fun ConfirmDialog(title: String, message: String, confirmLabel: String, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(message, fontSize = 14.sp) },
        confirmButton = { TextButton(onClick = onConfirm) { Text(confirmLabel) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
