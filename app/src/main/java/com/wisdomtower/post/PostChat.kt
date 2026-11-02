package com.wisdomtower.post

import android.content.Context
import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONArray
import org.json.JSONObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object PostChatStore {
    private const val PREFS = "wisdom_tower_post_chat"
    const val DEFAULT_URL = "https://zfgrgbrrqclugszsotfo.supabase.co/functions/v1/wt-mcp"

    private fun prefs(context: Context) = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
    fun loadUrl(context: Context): String = prefs(context).getString("url", DEFAULT_URL).orEmpty().ifBlank { DEFAULT_URL }
    fun loadKey(context: Context): String = prefs(context).getString("key", "").orEmpty()
    fun save(context: Context, url: String, key: String) {
        prefs(context).edit().putString("url", url.trim()).putString("key", key).apply()
    }

    fun saveCachedBatches(context: Context, batches: List<PostChatBatch>) {
        val rows = JSONArray()
        batches.forEach { batch ->
            rows.put(JSONObject().put("id", batch.id).put("title", batch.title).put("template", batch.template)
                .put("level", batch.level).put("pillar", batch.pillar).put("status", batch.status)
                .put("post_count", batch.postCount).put("created_at", batch.createdAt))
        }
        prefs(context).edit().putString("batches_cache", rows.toString()).apply()
    }

    fun loadCachedBatches(context: Context): List<PostChatBatch> = runCatching {
        val rows = JSONArray(prefs(context).getString("batches_cache", "[]"))
        (0 until rows.length()).mapNotNull { index ->
            val row = rows.optJSONObject(index) ?: return@mapNotNull null
            PostChatBatch(row.optString("id"), row.optString("title"), row.optString("template"), row.optString("level"),
                row.optString("pillar"), row.optString("status"), row.optInt("post_count"), row.optString("created_at"),
                JSONArray(), JSONArray())
        }
    }.getOrDefault(emptyList())
}

data class PostChatBatch(
    val id: String,
    val title: String,
    val template: String,
    val level: String,
    val pillar: String,
    val status: String,
    val postCount: Int,
    val createdAt: String,
    val posts: JSONArray,
    val captions: JSONArray
) {
    fun batchInput(): String {
        val keys = linkedMapOf(
            "eyebrow" to "label", "headline" to "headline", "message" to "message",
            "number" to "number", "list" to "list", "button" to "button"
        )
        val blocks = mutableListOf<String>()
        for (i in 0 until posts.length()) {
            val post = posts.optJSONObject(i) ?: continue
            val lines = mutableListOf<String>()
            keys.forEach { (slot, key) ->
                if (!post.has(slot) || post.isNull(slot)) return@forEach
                val value = post.opt(slot)
                val text = when (value) {
                    is JSONArray -> (0 until value.length()).mapNotNull { value.optString(it).takeIf(String::isNotBlank) }.joinToString("\n")
                    else -> value.toString()
                }
                if (text.isNotBlank()) lines.add("$key: $text")
            }
            if (lines.isNotEmpty()) blocks.add(lines.joinToString("\n"))
        }
        return blocks.joinToString("\n---\n")
    }
}

object PostChatApi {
    private suspend fun rpc(context: Context, method: String, params: JSONObject = JSONObject()): JSONObject =
        withContext(Dispatchers.IO) {
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
                val body = JSONObject().put("jsonrpc", "2.0").put("id", 1).put("method", method).put("params", params).toString()
                connection.outputStream.use { it.write(body.toByteArray(Charsets.UTF_8)) }
                val status = connection.responseCode
                if (status == 401) throw IllegalStateException("Wrong key")
                if (status !in 200..299) throw IllegalStateException("Server returned HTTP $status")
                val response = JSONObject(connection.inputStream.bufferedReader().use { it.readText() })
                if (response.has("error")) throw IllegalStateException(response.optJSONObject("error")?.optString("message") ?: "Backend error")
                response.optJSONObject("result") ?: throw IllegalStateException("Unexpected backend response")
            } catch (e: java.net.UnknownHostException) {
                throw IllegalStateException("No internet or server not found")
            } catch (e: java.net.SocketTimeoutException) {
                throw IllegalStateException("Connection timed out")
            } finally { connection.disconnect() }
        }

    suspend fun testConnection(context: Context, endpoint: String, secret: String): String =
        withContext(Dispatchers.IO) {
            val url = endpoint.trim()
            if (!url.startsWith("https://") || !url.contains(".supabase.co/") ||
                !url.endsWith("/functions/v1/wt-mcp")) throw IllegalArgumentException("Use the Wisdom Tower wt-mcp HTTPS endpoint")
            if (secret.length < 24) throw IllegalArgumentException("Enter the secret key (at least 24 characters)")
            val connection = (URL(url).openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"; connectTimeout = 12_000; readTimeout = 12_000; doOutput = true
                setRequestProperty("Content-Type", "application/json"); setRequestProperty("Accept", "application/json")
                setRequestProperty("x-wt-key", secret)
            }
            try {
                val body = JSONObject().put("jsonrpc", "2.0").put("id", 1).put("method", "tools/list").toString()
                connection.outputStream.use { it.write(body.toByteArray(Charsets.UTF_8)) }
                val status = connection.responseCode
                if (status == 401) throw IllegalStateException("Wrong key")
                if (status !in 200..299) throw IllegalStateException("Server returned HTTP $status")
                val response = JSONObject(connection.inputStream.bufferedReader().use { it.readText() })
                if (response.has("error")) throw IllegalStateException(response.optJSONObject("error")?.optString("message") ?: "Connection failed")
                val tools = response.optJSONObject("result")?.optJSONArray("tools") ?: throw IllegalStateException("Unexpected response from wt-mcp")
                if (tools.length() < 6) throw IllegalStateException("Connected, but backend tools are incomplete")
                PostChatStore.save(context, url, secret)
                "Connected · ${tools.length()} backend tools"
            } catch (e: java.net.UnknownHostException) { throw IllegalStateException("No internet or server not found")
            } catch (e: java.net.SocketTimeoutException) { throw IllegalStateException("Connection timed out")
            } finally { connection.disconnect() }
        }

    suspend fun listBatches(context: Context): List<PostChatBatch> {
        val result = rpc(context, "tools/call", JSONObject().put("name", "list_batches").put("arguments", JSONObject().put("limit", 50)))
        if (result.optBoolean("isError")) throw IllegalStateException(result.optJSONArray("content")?.optJSONObject(0)?.optString("text") ?: "Could not load batches")
        val payload = result.optJSONArray("content")?.optJSONObject(0)?.optString("text")
            ?: throw IllegalStateException("Unexpected list response")
        val rows = JSONArray(payload)
        val batches = (0 until rows.length()).mapNotNull { i ->
            val row = rows.optJSONObject(i) ?: return@mapNotNull null
            PostChatBatch(
                id = row.optString("id"), title = row.optString("title").ifBlank { "Untitled batch" },
                template = row.optString("template"), level = row.optString("level"), pillar = row.optString("pillar"),
                status = row.optString("status"), postCount = row.optInt("post_count"),
                createdAt = row.optString("created_at"), posts = JSONArray(), captions = JSONArray()
            )
        }
        PostChatStore.saveCachedBatches(context, batches)
        batches
    }

    suspend fun setBatchStatus(context: Context, id: String, status: String) {
        val result = rpc(context, "tools/call", JSONObject().put("name", "set_batch_status").put("arguments", JSONObject().put("id", id).put("status", status)))
        if (result.optBoolean("isError")) throw IllegalStateException(result.optJSONArray("content")?.optJSONObject(0)?.optString("text") ?: "Could not update status")
    }

    suspend fun logPost(context: Context, values: JSONObject) {
        val result = rpc(context, "tools/call", JSONObject().put("name", "log_post").put("arguments", values))
        if (result.optBoolean("isError")) throw IllegalStateException(result.optJSONArray("content")?.optJSONObject(0)?.optString("text") ?: "Could not log post")
    }

    suspend fun getBatch(context: Context, id: String): PostChatBatch {
        val result = rpc(context, "tools/call", JSONObject().put("name", "get_batch").put("arguments", JSONObject().put("id", id)))
        if (result.optBoolean("isError")) throw IllegalStateException(result.optJSONArray("content")?.optJSONObject(0)?.optString("text") ?: "Could not load batch")
        val payload = result.optJSONArray("content")?.optJSONObject(0)?.optString("text")
            ?: throw IllegalStateException("Unexpected batch response")
        val row = JSONObject(payload)
        val posts = row.optJSONArray("posts") ?: JSONArray()
        val captions = row.optJSONArray("captions") ?: JSONArray()
        return PostChatBatch(
            id = row.optString("id"), title = row.optString("title").ifBlank { "Untitled batch" },
            template = row.optString("template"), level = row.optString("level"), pillar = row.optString("pillar"),
            status = row.optString("status"), postCount = posts.length(), createdAt = row.optString("created_at"),
            posts = posts, captions = captions
        )
    }
}
