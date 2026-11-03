package com.wisdomtower.post

import android.content.Context
import android.net.Uri
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

/**
 * What the app remembers outside the current post (UI_UX_PLAN.md section 5).
 * bookmarks: template ids saved by the user (used from Phase 4).
 * lastTemplate: the template to open on launch.
 * edited: template id to the time (ms) it was last edited. A template is in this map only while it has a draft.
 * lastWords: the words of the last template that was edited, for the "Bring my words" button.
 */
data class PostLibrary(
    val bookmarks: List<String> = emptyList(),
    val lastTemplate: String? = null,
    val edited: Map<String, Long> = emptyMap(),
    val lastWords: Map<String, String> = emptyMap()
)

/**
 * One draft file per template: filesDir/drafts/<templateId>.json, written with PostCodec.
 * A template with no draft file is shown in its factory look (sample words, brand colors, and the template's own look).
 * Brand settings are never part of a draft, so a brand change restyles every template that has no color of its own.
 * A draft saved under the id of a removed template opens as the template that replaced it (see PostTemplates.kt).
 */
object PostDrafts {
    private fun draftsDir(context: Context): File = File(context.filesDir, "drafts").also { it.mkdirs() }

    private fun safeName(id: String): String = id.replace(Regex("[^A-Za-z0-9_-]"), "_")

    private fun draftFile(context: Context, id: String) = File(draftsDir(context), safeName(id) + ".json")

    private fun libraryFile(context: Context) = File(context.filesDir, "library.json")

    /**
     * The template's own look: its sample words, the brand colors, and its photo frame, mesh, grain, words-behind and cut-out position.
     * The look has to be part of the factory post. Otherwise opening a template from the strip or the Library would drop it,
     * and every template with a look would count as edited.
     */
    fun factory(templateId: String, size: PostSize): PostDoc {
        val spec = postTemplate(templateId)
        return PostDoc(templateId = spec.id, size = size, texts = spec.samples).withTemplate(spec)
    }

    /** True when the post differs from the template's factory look (the size counts as part of the draft). */
    fun isEdited(doc: PostDoc): Boolean = doc != factory(doc.templateId, doc.size)

    /** A post saved under an old template id moves to the template that replaced it, keeping its words and taking the new look. */
    private fun migrate(doc: PostDoc, spec: PostTemplateSpec): PostDoc =
        if (doc.templateId == spec.id) doc else doc.copy(templateId = spec.id).withTemplate(spec)

    fun loadDraft(context: Context, templateId: String): PostDoc? = runCatching {
        val f = draftFile(context, templateId)
        if (!f.exists()) return@runCatching null
        migrate(PostCodec.decodeDoc(f.readText()), postTemplate(templateId))
    }.getOrNull()

    /** All template ids that currently have a draft file. */
    fun draftIds(context: Context): Set<String> = runCatching {
        draftsDir(context).listFiles { f -> f.name.endsWith(".json") }
            ?.mapNotNull { f ->
                runCatching { PostCodec.decodeDoc(f.readText()).templateId }.getOrNull()
            }?.toSet() ?: emptySet()
    }.getOrDefault(emptySet())

    private fun writeDraft(context: Context, doc: PostDoc) {
        val target = draftFile(context, doc.templateId)
        val temp = File(target.parentFile, target.name + ".tmp")
        temp.writeText(PostCodec.encodeDoc(doc))
        if (!temp.renameTo(target)) {
            target.writeText(PostCodec.encodeDoc(doc))
            temp.delete()
        }
    }

    fun clearDraft(context: Context, templateId: String) {
        runCatching { draftFile(context, templateId).delete() }
    }

    /**
     * Writes the post as the draft of its template, or removes the draft when the post is back to its factory look.
     * Returns the updated library (edited times).
     */
    fun persist(context: Context, doc: PostDoc, library: PostLibrary): PostLibrary {
        val id = doc.templateId
        val updated = if (isEdited(doc)) {
            runCatching { writeDraft(context, doc) }
            library.copy(edited = library.edited + (id to System.currentTimeMillis()))
        } else {
            clearDraft(context, id)
            library.copy(edited = library.edited - id)
        }
        saveLibrary(context, updated)
        return updated
    }

    fun loadLibrary(context: Context): PostLibrary? = runCatching {
        val f = libraryFile(context)
        if (!f.exists()) return@runCatching null
        val j = JSONObject(f.readText())
        val marks = j.optJSONArray("bookmarks")
        val edited = j.optJSONObject("edited")
        val words = j.optJSONObject("lastWords")
        PostLibrary(
            bookmarks = if (marks == null) emptyList() else (0 until marks.length()).map { marks.optString(it) }.filter { it.isNotBlank() },
            lastTemplate = j.optString("lastTemplate").takeIf { it.isNotBlank() && it != "null" },
            edited = if (edited == null) emptyMap() else edited.keys().asSequence().associateWith { edited.optLong(it) },
            lastWords = if (words == null) emptyMap() else words.keys().asSequence().associateWith { words.optString(it) }
        )
    }.getOrNull()

    fun saveLibrary(context: Context, library: PostLibrary) {
        runCatching {
            val j = JSONObject()
            j.put("bookmarks", JSONArray().also { a -> library.bookmarks.forEach { a.put(it) } })
            j.put("lastTemplate", library.lastTemplate ?: "")
            j.put("edited", JSONObject().also { o -> library.edited.forEach { (k, v) -> o.put(k, v) } })
            j.put("lastWords", JSONObject().also { o -> library.lastWords.forEach { (k, v) -> o.put(k, v) } })
            val target = libraryFile(context)
            val temp = File(target.parentFile, target.name + ".tmp")
            temp.writeText(j.toString())
            if (!temp.renameTo(target)) {
                target.writeText(j.toString())
                temp.delete()
            }
        }
    }

    /**
     * Called once at launch. First launch after this update: the single post that was stored before becomes the draft
     * of its template, so nothing is lost. Later launches open the template that was open last, exactly as it was left.
     * A template id from the removed list opens as the template that replaced it.
     */
    fun startup(context: Context): Pair<PostDoc, PostLibrary> {
        val existing = loadLibrary(context)
        if (existing == null) {
            val legacy = PostStore.loadDoc(context)
            val doc = migrate(legacy, postTemplate(legacy.templateId))
            var library = PostLibrary(lastTemplate = doc.templateId)
            if (legacy != PostDoc() && isEdited(doc)) {
                library = persist(context, doc, library)
            } else {
                saveLibrary(context, library)
            }
            return (if (legacy == PostDoc()) factory(doc.templateId, doc.size) else doc) to library
        }
        val id = existing.lastTemplate ?: PostDoc().templateId
        return (loadDraft(context, id) ?: factory(id, PostSize.LANDSCAPE)) to existing
    }

    /** Photo files a post points to, as paths inside app storage. */
    private fun photoPaths(doc: PostDoc): Set<String> =
        listOfNotNull(doc.photoUri, doc.backgroundPhotoUri).mapNotNull { runCatching { Uri.parse(it).path }.getOrNull() }.toSet()

    /**
     * After a Reset that was not undone: deletes the photos of the discarded post that no draft and no open post still uses.
     * Only files inside filesDir/photos are touched.
     */
    fun releaseFiles(context: Context, discarded: PostDoc, openDoc: PostDoc) {
        runCatching {
            val photosDir = File(context.filesDir, "photos").canonicalFile
            val inUse = HashSet<String>()
            inUse.addAll(photoPaths(openDoc))
            draftIds(context).forEach { id -> loadDraft(context, id)?.let { inUse.addAll(photoPaths(it)) } }
            photoPaths(discarded).forEach { path ->
                if (path in inUse) return@forEach
                val file = File(path).canonicalFile
                if (file.parentFile == photosDir && file.exists()) file.delete()
            }
        }
    }
}
