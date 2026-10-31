package com.wisdomtower.post

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

/**
 * Per-template offline memory. Each template owns its own editable document;
 * brand settings remain global in PostStore.
 */
data class PostLibraryIndex(
    val bookmarks: Set<String> = emptySet(),
    val lastOpenedTemplateId: String = PostTemplates.firstOrNull()?.id ?: "quote-center",
    val lastEditedAt: Map<String, Long> = emptyMap(),
    val draftVersions: Map<String, Long> = emptyMap()
)

object PostDraftStore {
    private const val INDEX_FILE = "library.json"
    private const val DRAFT_DIR = "drafts"

    private fun draftFile(context: Context, templateId: String): File {
        val safeId = templateId.filter { it.isLetterOrDigit() || it == '-' || it == '_' }
        return File(File(context.filesDir, DRAFT_DIR).apply { mkdirs() }, "$safeId.json")
    }

    fun loadIndex(context: Context): PostLibraryIndex = runCatching {
        val file = File(context.filesDir, INDEX_FILE)
        if (!file.exists()) return@runCatching PostLibraryIndex()
        val json = JSONObject(file.readText())
        val bookmarksJson = json.optJSONArray("bookmarks") ?: JSONArray()
        val bookmarks = (0 until bookmarksJson.length()).mapNotNull { bookmarksJson.optString(it).takeIf(String::isNotBlank) }.toSet()
        val editedJson = json.optJSONObject("lastEditedAt") ?: JSONObject()
        val edited = editedJson.keys().asSequence().associateWith { editedJson.optLong(it) }
        val versionsJson = json.optJSONObject("draftVersions") ?: JSONObject()
        val versions = versionsJson.keys().asSequence().associateWith { versionsJson.optLong(it) }
        PostLibraryIndex(
            bookmarks = bookmarks,
            lastOpenedTemplateId = json.optString("lastOpenedTemplateId", PostLibraryIndex().lastOpenedTemplateId),
            lastEditedAt = edited,
            draftVersions = versions
        )
    }.getOrDefault(PostLibraryIndex())

    private fun writeIndex(context: Context, index: PostLibraryIndex) {
        val json = JSONObject().apply {
            put("bookmarks", JSONArray(index.bookmarks.sorted()))
            put("lastOpenedTemplateId", index.lastOpenedTemplateId)
            put("lastEditedAt", JSONObject().also { out -> index.lastEditedAt.forEach { (id, time) -> out.put(id, time) } })
            put("draftVersions", JSONObject().also { out -> index.draftVersions.forEach { (id, version) -> out.put(id, version) } })
        }
        atomicWrite(File(context.filesDir, INDEX_FILE), json.toString())
    }

    private fun atomicWrite(file: File, content: String) {
        file.parentFile?.mkdirs()
        val temp = File(file.parentFile, file.name + ".tmp")
        FileOutputStream(temp).use { stream ->
            stream.write(content.toByteArray(Charsets.UTF_8))
            stream.fd.sync()
        }
        if (file.exists() && !file.delete()) throw IllegalStateException("Could not replace ${file.name}")
        if (!temp.renameTo(file)) {
            temp.copyTo(file, overwrite = true)
            temp.delete()
        }
    }

    /** Migrates the old single-document preference exactly once, without overwriting an existing template draft. */
    fun migrateLegacy(context: Context, legacy: PostDoc): PostLibraryIndex {
        val target = draftFile(context, legacy.templateId)
        if (!target.exists()) saveDraft(context, legacy)
        val current = loadIndex(context)
        val updated = current.copy(lastOpenedTemplateId = legacy.templateId)
        writeIndex(context, updated)
        return updated
    }

    fun loadDraft(context: Context, templateId: String): PostDoc? =
        runCatching {
            val file = draftFile(context, templateId)
            if (!file.exists()) null else PostCodec.decodeDoc(file.readText())
        }.getOrNull()

    fun openTemplate(context: Context, template: PostTemplateSpec, size: PostSize): PostDoc {
        val existing = loadDraft(context, template.id)
        val doc = (existing ?: PostDoc(templateId = template.id, texts = template.samples))
            .copy(templateId = template.id, size = size)
        markOpened(context, template.id)
        return doc
    }

    fun saveDraft(context: Context, doc: PostDoc) {
        val file = draftFile(context, doc.templateId)
        atomicWrite(file, PostCodec.encodeDoc(doc))
        val old = loadIndex(context)
        val now = System.currentTimeMillis()
        writeIndex(
            context,
            old.copy(
                lastOpenedTemplateId = doc.templateId,
                lastEditedAt = old.lastEditedAt + (doc.templateId to now),
                draftVersions = old.draftVersions + (doc.templateId to ((old.draftVersions[doc.templateId] ?: 0L) + 1L))
            )
        )
    }

    fun markOpened(context: Context, templateId: String) {
        val old = loadIndex(context)
        writeIndex(context, old.copy(lastOpenedTemplateId = templateId))
    }

    fun setBookmarked(context: Context, templateId: String, bookmarked: Boolean) {
        val old = loadIndex(context)
        val next = if (bookmarked) old.bookmarks + templateId else old.bookmarks - templateId
        writeIndex(context, old.copy(bookmarks = next))
    }

    fun isBookmarked(context: Context, templateId: String): Boolean =
        templateId in loadIndex(context).bookmarks

    fun clearDraft(context: Context, templateId: String) {
        draftFile(context, templateId).delete()
        val old = loadIndex(context)
        writeIndex(
            context,
            old.copy(
                lastEditedAt = old.lastEditedAt - templateId,
                draftVersions = old.draftVersions - templateId,
                lastOpenedTemplateId = templateId
            )
        )
    }

    fun reset(context: Context, templateId: String, size: PostSize): PostDoc {
        val template = postTemplate(templateId)
        clearDraft(context, templateId)
        return PostDoc(templateId = template.id, size = size, texts = template.samples)
    }

    fun isEdited(context: Context, templateId: String): Boolean =
        draftFile(context, templateId).exists()

    fun thumbnailVersion(context: Context, templateId: String): Long =
        loadIndex(context).draftVersions[templateId] ?: 0L
}
