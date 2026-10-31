package com.wisdomtower.post

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class PostProject(
    val id: String,
    val title: String,
    val updatedAt: Long,
    val document: PostDoc
)

/** A small offline project shelf. It stores editable documents, never flattened preview images. */
object PostProjects {
    private const val PREFS = "wisdom_tower_post_projects"
    private const val KEY = "items"
    private const val LIMIT = 40

    fun load(context: Context): List<PostProject> = runCatching {
        val raw = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY, "[]") ?: "[]"
        val array = JSONArray(raw)
        (0 until array.length()).mapNotNull { index ->
            val item = array.optJSONObject(index) ?: return@mapNotNull null
            val encoded = item.optString("document")
            if (encoded.isBlank()) return@mapNotNull null
            PostProject(
                id = item.optString("id"),
                title = item.optString("title", "Untitled post"),
                updatedAt = item.optLong("updatedAt"),
                document = PostCodec.decodeDoc(encoded)
            )
        }.sortedByDescending { it.updatedAt }
    }.getOrDefault(emptyList())

    fun save(context: Context, document: PostDoc): List<PostProject> {
        val now = System.currentTimeMillis()
        val headline = document.texts[PostSlots.HEADLINE].orEmpty().trim()
            .replace("\n", " ").take(48).ifBlank { postTemplate(document.templateId).name }
        val current = load(context).toMutableList()
        current.add(0, PostProject(now.toString(), headline, now, document))
        val trimmed = current.take(LIMIT)
        persist(context, trimmed)
        return trimmed
    }

    fun delete(context: Context, id: String): List<PostProject> {
        val updated = load(context).filterNot { it.id == id }
        persist(context, updated)
        return updated
    }

    private fun persist(context: Context, projects: List<PostProject>) {
        val array = JSONArray()
        projects.forEach { project ->
            array.put(JSONObject()
                .put("id", project.id)
                .put("title", project.title)
                .put("updatedAt", project.updatedAt)
                .put("document", PostCodec.encodeDoc(project.document)))
        }
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putString(KEY, array.toString()).apply()
    }
}
