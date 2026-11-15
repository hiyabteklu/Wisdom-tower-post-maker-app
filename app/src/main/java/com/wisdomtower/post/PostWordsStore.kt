package com.wisdomtower.post

import android.content.Context
import org.json.JSONObject

/**
 * Keeps the words edited in the review step of an inbox batch on this phone, so they are not lost when the app is closed before they
 * reached Supabase (docs/URGENT_PLAN.md item 3).
 *
 * One entry per batch in the preferences file "wt_post_words": {"base": <signature of the cloud words>, "words": {"<post number>": {"<box>": "<text>"}}}.
 * The entry is only laid over a batch when its "base" still equals the words the cloud copy has now. If the cloud copy changed in the
 * meantime (edited elsewhere, a post removed, saved from this phone), the entry is ignored and thrown away, so old words can never
 * overwrite newer ones. When the edited words equal the cloud words again, the entry is removed.
 * After a restart the restored words count as unsaved changes, so the save bar sends them (a draft saves itself 2 seconds later).
 * Not compiled when written.
 */
internal object PostWordsStore {
    private const val PREFS = "wt_post_words"

    private fun prefs(context: Context) = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    /** Writes the edited words, or removes the entry when they are the same as the cloud words. [cloud] are the items as parsed from the cloud copy. */
    fun sync(context: Context, batchId: String, cloud: List<BatchItem>, edited: List<BatchItem>) {
        val base = PostBatchSync.textSignature(cloud)
        if (PostBatchSync.textSignature(edited) == base) {
            prefs(context).edit().remove(batchId).apply()
            return
        }
        runCatching {
            val words = JSONObject()
            for (item in edited) {
                val boxes = JSONObject()
                item.texts.forEach { (box, text) -> boxes.put(box, text) }
                words.put(item.number.toString(), boxes)
            }
            val entry = JSONObject().put("base", base).put("words", words)
            prefs(context).edit().putString(batchId, entry.toString()).apply()
        }
    }

    /** The cloud items with the kept words laid over them, or null when nothing usable is kept (then the cloud items are used as they are). */
    fun restore(context: Context, batchId: String, cloud: List<BatchItem>): List<BatchItem>? {
        val raw = prefs(context).getString(batchId, null) ?: return null
        return runCatching {
            val entry = JSONObject(raw)
            if (entry.optString("base") != PostBatchSync.textSignature(cloud)) {
                prefs(context).edit().remove(batchId).apply()
                return@runCatching null
            }
            val words = entry.getJSONObject("words")
            cloud.map { item ->
                val boxes = words.optJSONObject(item.number.toString()) ?: return@map item
                val texts = LinkedHashMap<String, String>()
                for (key in boxes.keys()) texts[key] = boxes.getString(key)
                item.copy(texts = texts)
            }
        }.getOrNull()
    }
}
