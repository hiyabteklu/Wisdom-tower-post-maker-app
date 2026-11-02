package com.wisdomtower.post

import android.content.Context
import java.net.HttpURLConnection
import java.net.URL
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
}

object PostChatApi {
    suspend fun testConnection(context: Context, endpoint: String, secret: String): String =
        withContext(Dispatchers.IO) {
            val url = endpoint.trim()
            if (!url.startsWith("https://") || !url.contains(".supabase.co/") ||
                !url.endsWith("/functions/v1/wt-mcp")) {
                throw IllegalArgumentException("Use the Wisdom Tower wt-mcp HTTPS endpoint")
            }
            if (secret.length < 24) throw IllegalArgumentException("Enter the secret key (at least 24 characters)")
            val connection = (URL(url).openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = 12_000
                readTimeout = 12_000
                doOutput = true
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty("Accept", "application/json")
                setRequestProperty("x-wt-key", secret)
            }
            try {
                val body = JSONObject().put("jsonrpc", "2.0").put("id", 1).put("method", "tools/list").toString()
                connection.outputStream.use { it.write(body.toByteArray(Charsets.UTF_8)) }
                val status = connection.responseCode
                if (status == 401) throw IllegalStateException("Wrong key")
                if (status !in 200..299) throw IllegalStateException("Server returned HTTP $status")
                val responseText = connection.inputStream.bufferedReader().use { it.readText() }
                val response = JSONObject(responseText)
                if (response.has("error")) throw IllegalStateException(response.optJSONObject("error")?.optString("message") ?: "Connection failed")
                val tools = response.optJSONObject("result")?.optJSONArray("tools")
                    ?: throw IllegalStateException("Unexpected response from wt-mcp")
                if (tools.length() < 6) throw IllegalStateException("Connected, but backend tools are incomplete")
                PostChatStore.save(context, url, secret)
                "Connected · ${tools.length()} backend tools"
            } catch (e: java.net.UnknownHostException) {
                throw IllegalStateException("No internet or server not found")
            } catch (e: java.net.SocketTimeoutException) {
                throw IllegalStateException("Connection timed out")
            } finally {
                connection.disconnect()
            }
        }
}
