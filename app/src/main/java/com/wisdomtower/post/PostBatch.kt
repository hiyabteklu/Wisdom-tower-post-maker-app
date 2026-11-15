package com.wisdomtower.post

import android.content.Context

enum class BatchMode { ONE_LINE, BLOCKS }
data class BatchItem(val number: Int, val texts: Map<String, String>, val notes: List<String>)
data class BatchParse(val items: List<BatchItem>, val notes: List<String>)

object PostBatch {
    const val MAX_POSTS = 50
    private val keys = mapOf(
        "letter" to PostSlots.LETTER, "big letter" to PostSlots.LETTER,
        "word" to PostSlots.WORD, "big word" to PostSlots.WORD,
        "label" to PostSlots.EYEBROW, "eyebrow" to PostSlots.EYEBROW, "small label" to PostSlots.EYEBROW,
        "headline" to PostSlots.HEADLINE, "title" to PostSlots.HEADLINE,
        "message" to PostSlots.BODY, "body" to PostSlots.BODY,
        "number" to PostSlots.NUMBER, "code" to PostSlots.NUMBER, "big number" to PostSlots.NUMBER,
        "list" to PostSlots.LIST, "button" to PostSlots.BUTTON, "button text" to PostSlots.BUTTON
    )
    private val keyPattern = Regex("^\\s*([A-Za-z ]+)\\s*:(.*)$")
    private val dashLine = Regex("^-{3,}$")

    fun editableSlots(layout: TemplateLayout): List<String> =
        layout.slots.filter { it.literal == null }.map { it.slot }.distinct()

    fun parse(raw: String, mode: BatchMode, slots: List<String>, oneLineSlot: String): BatchParse {
        val normalized = raw.replace("\r\n", "\n").replace('\r', '\n').replace('\t', ' ')
        val notes = mutableListOf<String>()
        val chunks = if (mode == BatchMode.ONE_LINE) {
            normalized.split('\n').filter { it.isNotBlank() && !dashLine.matches(it.trim()) }.map { listOf(it) }
        } else {
            val result = mutableListOf<MutableList<String>>()
            var current = mutableListOf<String>()
            normalized.split('\n').forEach { line ->
                if (dashLine.matches(line.trim())) {
                    if (current.any { it.isNotBlank() }) result.add(current)
                    current = mutableListOf()
                } else current.add(line)
            }
            if (current.any { it.isNotBlank() }) result.add(current)
            result
        }
        val items = mutableListOf<BatchItem>()
        chunks.take(MAX_POSTS).forEachIndexed { index, rawLines ->
            val number = index + 1
            val lines = rawLines.toMutableList()
            while (lines.firstOrNull()?.isBlank() == true) lines.removeAt(0)
            while (lines.lastOrNull()?.isBlank() == true) lines.removeAt(lines.lastIndex)
            val values = linkedMapOf<String, String>()
            val itemNotes = mutableListOf<String>()
            if (mode == BatchMode.ONE_LINE) {
                val line = lines.firstOrNull()?.trim().orEmpty()
                if (oneLineSlot in slots && line.isNotEmpty()) values[oneLineSlot] = line
                else if (oneLineSlot !in slots) itemNotes.add("Fill box is not in this template")
            } else {
                fun knownKey(line: String): Boolean = keyPattern.matchEntire(line)?.groupValues?.get(1)?.trim()?.lowercase() in keys
                val hasKey = lines.any { knownKey(it) }
                if (hasKey) {
                    var currentSlot: String? = null
                    var currentValue = StringBuilder()
                    fun flush() {
                        currentSlot?.let { values[it] = currentValue.toString().trim() }
                    }
                    lines.forEach { line ->
                        val match = keyPattern.matchEntire(line)
                        val key = match?.groupValues?.get(1)?.trim()?.lowercase()
                        if (match != null && key in keys) {
                            flush()
                            val target = keys[key]
                            currentValue = StringBuilder()
                            currentSlot = target?.takeIf { it in slots }
                            if (target != null && target !in slots) itemNotes.add("Post $number: box " + PostSlots.label(target) + " is not in this template")
                            if (currentSlot != null) currentValue.append(match.groupValues[2].trim())
                        } else if (currentSlot != null) {
                            if (currentValue.isNotEmpty()) currentValue.append('\n')
                            currentValue.append(line.trim())
                        }
                    }
                    flush()
                    if (lines.takeWhile { !knownKey(it) }.any { it.isNotBlank() }) {
                        itemNotes.add("Post $number: lines before the first label were ignored")
                    }
                } else {
                    val plain = lines.filter { it.isNotBlank() }
                    var cursor = 0
                    for (slot in slots) {
                        if (cursor >= plain.size) break
                        if (slot == PostSlots.LIST) {
                            val remaining = plain.drop(cursor)
                            if (remaining.firstOrNull()?.trim() == "-") {
                                cursor++
                                val rest = plain.drop(cursor)
                                if (rest.isNotEmpty()) values[slot] = rest.joinToString("\n") { it.trim() }
                            } else if (remaining.isNotEmpty()) values[slot] = remaining.joinToString("\n") { it.trim() }
                            cursor = plain.size
                            break
                        }
                        val line = plain[cursor++].trim()
                        if (line != "-") values[slot] = line
                    }
                    if (cursor < plain.size && slots.isNotEmpty()) {
                        val last = slots.last()
                        val extra = plain.drop(cursor).filter { it.trim() != "-" }.joinToString("\n") { it.trim() }
                        if (extra.isNotEmpty()) values[last] = listOfNotNull(values[last]?.takeIf { it.isNotEmpty() }, extra).joinToString("\n")
                        itemNotes.add("Post $number: extra lines were added to the last box")
                    }
                }
            }
            values.replaceAll { _, value -> value.replace("{n}", number.toString()).trim() }
            val hydrated = PostSlots.withDerivedSlots(values, slots)
            items.add(BatchItem(number, hydrated, itemNotes))
            notes.addAll(itemNotes)
        }
        if (chunks.size > MAX_POSTS) notes.add("Only the first $MAX_POSTS posts were kept")
        return BatchParse(items, notes)
    }

    fun apply(base: PostDoc, item: BatchItem): PostDoc = base.copy(texts = base.texts + item.texts)

    fun exampleFor(slots: List<String>, mode: BatchMode): String {
        if (mode == BatchMode.ONE_LINE) return "Small steps still move you forward.\nRead the question twice.\nSleep is part of studying."
        return slots.map { slot ->
            val key = when (slot) {
                PostSlots.LETTER -> "letter"
                PostSlots.WORD -> "word"
                PostSlots.EYEBROW -> "label"
                PostSlots.HEADLINE -> "headline"
                PostSlots.BODY -> "message"
                PostSlots.NUMBER -> "number"
                PostSlots.LIST -> "list"
                PostSlots.BUTTON -> "button"
                else -> PostSlots.label(slot).lowercase()
            }
            key + ": " + when (slot) {
                PostSlots.LETTER -> "S"
                PostSlots.WORD -> "STEPS"
                PostSlots.EYEBROW -> "STUDY TIP"
                PostSlots.HEADLINE -> "Small steps, every day"
                PostSlots.BODY -> "A little progress adds up."
                PostSlots.LIST -> "Read the notes\nTry a past paper"
                else -> "Your text"
            }
        }.joinToString("\n")
    }
}

object PostBatchStore {
    private const val PREFS = "wisdom_tower_post_batch"
    private fun prefs(context: Context) = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
    fun loadText(context: Context, templateId: String): String = prefs(context).getString("text_$templateId", "").orEmpty()
    fun saveText(context: Context, templateId: String, text: String) { prefs(context).edit().putString("text_$templateId", text).apply() }
    fun clearText(context: Context, templateId: String) = saveText(context, templateId, "")
    fun loadMode(context: Context): BatchMode = runCatching {
        BatchMode.valueOf(prefs(context).getString("mode", BatchMode.ONE_LINE.name)!!)
    }.getOrDefault(BatchMode.ONE_LINE)
    fun saveMode(context: Context, mode: BatchMode) { prefs(context).edit().putString("mode", mode.name).apply() }
    fun loadFill(context: Context, templateId: String, slots: List<String>): String {
        val saved = prefs(context).getString("fill_$templateId", null)
        return saved?.takeIf { it in slots } ?: (PostSlots.HEADLINE.takeIf { it in slots } ?: slots.firstOrNull().orEmpty())
    }
    fun saveFill(context: Context, templateId: String, slot: String) { prefs(context).edit().putString("fill_$templateId", slot).apply() }
}
