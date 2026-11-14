package com.wisdomtower.post

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import kotlin.math.max
import kotlin.math.min

/**
 * The line-grid engine of the Chapter summaries (docs/CHAPTER_SUMMARIES_PLAN.md, sections 5 to 8).
 *
 * The notebook paper (stPaper in PostTemplatesStudy.kt) draws 30 rules from 2.5% to 97.5% of the height. This engine uses the
 * 29 cells between those rules as text lines: every text line sits on its own cell with its baseline a little above the rule, so
 * nothing crosses a rule, nothing overlaps, and two text lines never share one cell. Content starts at the top (cell 0 is shared
 * with the small logo at the right) and runs down to the footer chips in the last cell (cell 28).
 *
 * Text is never shrunk to fit the page. A block that does not fit is not drawn half-way: its slot name is returned in the
 * overflow list and drawing stops, so the review screen can show it and the sheet can be split.
 *
 * Authoring markup (one line per row of the list, also used for the message):
 *   # Title, ## Topic, ### Subtopic    headings (one cell each, colored, underlined)
 *   - text, • text, ★ text, ✓ text       bullets with a marker; two leading spaces per nesting level (max 2)
 *   1. text, 1.1. text                    numbered rows (the number is kept)
 *   > text                                key idea (green box); > NB / WARNING / CAUTION / TRAP / AVOID is a red box
 *   Term: explanation                     in ROW templates the term becomes a bold colored label
 *   ---                                   one blank line
 *   |a|b|c|                               temporary table rows (real tables come in the next slice)
 * Inline marks (**bold**, *italic*, ==highlight==, [[term]], #underline#, [[#HEX;B:word]], $math$) go through PostFonts.styled.
 */
internal object PostStudyGrid {
    private const val FIRST = 0.025f
    private const val LAST = 0.975f
    private const val RULES = 30
    private const val LAST_CELL = RULES - 2

    /** How far above its rule a baseline sits, in cells. Keeps descenders off the rule and ascenders under the rule above. */
    private const val LIFT = 0.22f

    private val NAVY = Color.rgb(18, 42, 105)
    private val BLUE = Color.rgb(23, 107, 206)
    private val RED = Color.rgb(209, 26, 38)
    private val GREEN = Color.rgb(22, 128, 60)
    private val PURPLE = Color.rgb(124, 58, 237)
    private val INK = Color.rgb(37, 43, 49)

    private val MARKS = listOf("★", "✦", "✧", "•", "✓", "✔", "☑", "☐", "→")
    private val NUMBERED = Regex("""^(\d+(?:\.\d+)*[.)])\s+(.+)$""")

    /** What makes one of the six variants different: marker color, default row style, title size in cells and body size. */
    private class Look(val marker: Int, val list: ListStyle, val headCells: Int, val headScale: Float, val bodyScale: Float)

    private fun lookFor(id: String): Look = when (id) {
        "study-cover" -> Look(RED, ListStyle.NONE, 3, 1.9f, 0.72f)
        "study-list" -> Look(RED, ListStyle.NUMBER, 2, 1.45f, 0.66f)
        "study-define" -> Look(PURPLE, ListStyle.NONE, 2, 1.5f, 0.70f)
        "study-checklist" -> Look(GREEN, ListStyle.CHECK, 2, 1.35f, 0.64f)
        "study-table" -> Look(BLUE, ListStyle.ROW, 2, 1.35f, 0.64f)
        "study-mnemonic" -> Look(PURPLE, ListStyle.ROW, 2, 1.35f, 0.66f)
        else -> Look(RED, ListStyle.DOT, 2, 1.4f, 0.66f)
    }

    // ---- handwriting fonts ----

    private var handRegular: Typeface? = null
    private var handBold: Typeface? = null
    private var handLoaded = false

    /**
     * Handwriting fonts: the English font picked on the post wins, then any .ttf/.otf in assets/fonts/hand/
     * (a file with "bold" in its name is the bold face), then the phone's own casual handwriting-style font.
     */
    private fun hand(context: Context, doc: PostDoc, bold: Boolean): Typeface {
        PostFonts.typeface(context, doc.fontEnglish)?.let { return it }
        synchronized(this) {
            if (!handLoaded) {
                handLoaded = true
                val names = (runCatching { context.assets.list("fonts/hand") }.getOrNull() ?: emptyArray<String>())
                    .filter { it.endsWith(".ttf", true) || it.endsWith(".otf", true) }
                    .sorted()
                fun load(name: String): Typeface? =
                    runCatching { Typeface.createFromAsset(context.assets, "fonts/hand/" + name) }.getOrNull()
                val boldName = names.firstOrNull { it.contains("bold", true) }
                val regularName = names.firstOrNull { !it.contains("bold", true) } ?: names.firstOrNull()
                handRegular = regularName?.let { load(it) }
                handBold = boldName?.let { load(it) } ?: handRegular?.let { Typeface.create(it, Typeface.BOLD) }
            }
        }
        val found = if (bold) handBold else handRegular
        return found ?: Typeface.create("casual", if (bold) Typeface.BOLD else Typeface.NORMAL)
    }

    private fun parseColor(hex: String?): Int? {
        if (hex.isNullOrBlank()) return null
        return runCatching { Color.parseColor(if (hex.startsWith("#")) hex else "#" + hex) }.getOrNull()
    }

    private fun hexOf(color: Int): String = String.format("#%06X", color and 0xFFFFFF)

    private fun markColor(symbol: String, look: Look): Int = when (symbol) {
        "✓", "✔", "☑" -> GREEN
        "★" -> RED
        "→" -> BLUE
        "☐" -> INK
        else -> look.marker
    }

    /** "Term: explanation" or "Term — explanation" into its two parts, or null when the line has no label. */
    private fun splitRow(line: String): Pair<String, String>? {
        val colon = line.indexOf(':')
        val markup = line.indexOf("[[")
        if (colon in 1..42 && !(markup in 0 until colon)) {
            return line.substring(0, colon).trim() to line.substring(colon + 1).trim()
        }
        for (separator in listOf(" — ", " – ", " - ")) {
            val at = line.indexOf(separator)
            if (at in 1..42) return line.substring(0, at).trim() to line.substring(at + separator.length).trim()
        }
        return null
    }

    private fun labelled(label: String, rest: String, color: Int): String =
        "[[" + hexOf(color) + ";B:" + label + "]]" + (if (rest.isEmpty()) "" else " – " + rest)

    // ---- the page ----

    private class Page(
        val context: Context,
        val canvas: Canvas,
        val doc: PostDoc,
        val w: Int,
        val h: Int,
        val look: Look,
        val lastCell: Int
    ) {
        val step: Float = h * (LAST - FIRST) / (RULES - 1)
        val left: Float = w * 0.065f
        val width: Float = w * 0.91f
        val bodyInk: Int = parseColor(doc.textHex) ?: INK
        var cell = 0
        val overflow = LinkedHashSet<String>()

        fun ruleY(k: Int): Float = h * FIRST + k * step
        fun baseline(c: Int): Float = ruleY(c + 1) - step * LIFT
        fun full(): Boolean = cell > lastCell
        fun gap() { cell += 1 }

        /** The first cell shares its line with the small logo in the top-right corner, so text there stops before it. */
        fun room(c: Int, avail: Float): Float = if (c == 0 && doc.showLogo) avail - w * 0.15f else avail

        fun paint(bold: Boolean, size: Float, color: Int): TextPaint {
            val p = TextPaint(Paint.ANTI_ALIAS_FLAG)
            p.typeface = hand(context, doc, bold)
            p.textSize = size
            p.color = color
            return p
        }

        fun styled(slot: String, raw: String): CharSequence =
            PostFonts.styled(context, raw, doc, doc.slotStyles[slot]?.fontAm)

        fun layout(text: CharSequence, paint: TextPaint, width: Int): StaticLayout =
            StaticLayout.Builder.obtain(text, 0, text.length, paint, max(1, width))
                .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                .setLineSpacing(0f, 1f)
                .setIncludePad(false)
                .build()

        /**
         * Draws each wrapped line of the layout on its own cell, starting at firstCell. The layout is drawn once per line with a clip
         * to that line, moved so the line's baseline lands exactly on the cell's baseline, whatever the font metrics are.
         */
        fun drawLayout(lay: StaticLayout, x: Float, firstCell: Int) {
            for (i in 0 until lay.lineCount) {
                val c = firstCell + i
                if (c > lastCell) break
                canvas.save()
                canvas.translate(x, baseline(c) - lay.getLineBaseline(i))
                canvas.clipRect(-step, lay.getLineTop(i).toFloat(), lay.width + step, lay.getLineBottom(i).toFloat())
                lay.draw(canvas)
                canvas.restore()
            }
        }

        /** A one-line title that shrinks to the page width (never wraps unless it cannot fit at 45%), bottom-aligned in its cells. */
        fun heading(
            slot: String, raw: String, scale: Float, color: Int, cells: Int, underline: Float,
            underlineColor: Int? = null, caps: Boolean = false, spacing: Float = 0f
        ) {
            if (raw.isBlank()) return
            if (cell + cells - 1 > lastCell) {
                overflow.add(slot)
                cell = lastCell + 1
                return
            }
            val own = doc.slotStyles[slot]
            val ink = parseColor(own?.colorHex) ?: color
            var size = step * scale * (own?.scale ?: 1f)
            val text = styled(slot, if (caps) raw.uppercase() else raw)
            val avail = room(cell, width)
            val p = paint(true, size, ink)
            p.letterSpacing = spacing
            val minSize = size * 0.45f
            while (p.measureText(text, 0, text.length) > avail && size > minSize) {
                size *= 0.94f
                p.textSize = size
            }
            val lay = layout(text, p, avail.toInt())
            val lines = lay.lineCount
            val used = max(cells, lines)
            if (cell + used - 1 > lastCell) {
                overflow.add(slot)
                cell = lastCell + 1
                return
            }
            val first = cell + (used - lines)
            drawLayout(lay, left, first)
            if (underline > 0f) {
                val u = Paint(Paint.ANTI_ALIAS_FLAG)
                u.color = underlineColor ?: ink
                u.alpha = 215
                u.strokeWidth = step * underline
                u.strokeCap = Paint.Cap.ROUND
                val y = ruleY(first + lines) - step * 0.12f
                val len = min(avail, lay.getLineWidth(lines - 1) + step * 0.3f)
                canvas.drawLine(left, y, left + len, y, u)
            }
            cell += used
        }

        /** One row: an optional marker, then the text wrapped over as many cells as it needs, with a hanging indent. */
        fun item(slot: String, marker: String, raw: String, level: Int, markerColor: Int, ink: Int, bold: Boolean = false) {
            if (full()) {
                overflow.add(slot)
                return
            }
            val own = doc.slotStyles[slot]
            val size = step * look.bodyScale * (own?.scale ?: 1f)
            val nest = level * size * 0.9f
            val x0 = left + nest
            val digits = marker.firstOrNull()?.isDigit() == true
            val indent = if (marker.isEmpty()) 0f else if (digits) size * 1.95f else size * 1.2f
            val avail = room(cell, width - nest) - indent
            val lay = layout(styled(slot, raw.ifEmpty { " " }), paint(bold, size, ink), avail.toInt())
            val need = lay.lineCount
            if (cell + need - 1 > lastCell) {
                overflow.add(slot)
                cell = lastCell + 1
                return
            }
            if (marker.isNotEmpty()) canvas.drawText(marker, x0, baseline(cell), paint(true, size, markerColor))
            drawLayout(lay, x0 + indent, cell)
            cell += need
        }

        /** A tinted box over whole cells with a bar at the left: green for a key idea, red for a warning or exam trap. */
        fun callout(slot: String, raw: String, warn: Boolean) {
            if (full()) {
                overflow.add(slot)
                return
            }
            val bar = if (warn) RED else GREEN
            val size = step * look.bodyScale * (doc.slotStyles[slot]?.scale ?: 1f)
            val pad = size * 0.55f
            val indent = size * 1.4f
            val avail = room(cell, width) - pad - indent - size * 0.2f
            val lay = layout(styled(slot, raw), paint(true, size, bodyInk), avail.toInt())
            val need = lay.lineCount
            if (cell + need - 1 > lastCell) {
                overflow.add(slot)
                cell = lastCell + 1
                return
            }
            val top = ruleY(cell) + step * 0.08f
            val bottom = ruleY(cell + need) - step * 0.08f
            val fill = Paint(Paint.ANTI_ALIAS_FLAG)
            fill.color = Color.argb(40, Color.red(bar), Color.green(bar), Color.blue(bar))
            canvas.drawRect(left, top, left + width, bottom, fill)
            val edge = Paint(Paint.ANTI_ALIAS_FLAG)
            edge.color = bar
            canvas.drawRect(left, top, left + size * 0.14f, bottom, edge)
            canvas.drawText(if (warn) "!" else "★", left + pad, baseline(cell), paint(true, size, bar))
            drawLayout(lay, left + pad + indent, cell)
            cell += need
        }
    }

    /**
     * Draws the text slots of a study post on the line grid and returns the slots that did not fit (empty when everything fits).
     * slots: the text boxes the template has; text in other slots is ignored, as for every template.
     */
    fun draw(context: Context, canvas: Canvas, doc: PostDoc, w: Int, h: Int, slots: Set<String>): List<String> {
        val look = lookFor(doc.templateId)
        val page = Page(context, canvas, doc, w, h, look, if (doc.showFooter) LAST_CELL - 1 else LAST_CELL)

        fun text(slot: String): String =
            if (slot in slots && slot !in doc.hiddenTextSlots) doc.texts[slot].orEmpty().trim() else ""

        fun flow(slot: String, raw: String, style: ListStyle) {
            if (raw.isEmpty()) return
            var counter = 0
            var tableRow = 0
            for (rawLine in raw.split('\n')) {
                val line = rawLine.trimEnd()
                if (line.isBlank()) continue
                if (page.full()) {
                    page.overflow.add(slot)
                    return
                }
                val t = line.trim()
                val level = min(2, (line.length - line.trimStart().length) / 2)
                if (!t.startsWith("|")) tableRow = 0

                if (t.startsWith("### ")) {
                    page.heading(slot, t.drop(4), 0.72f, look.marker, 1, 0f)
                    counter = 0
                    continue
                }
                if (t.startsWith("## ")) {
                    page.heading(slot, t.drop(3), 0.80f, BLUE, 1, 0.06f)
                    counter = 0
                    continue
                }
                if (t.startsWith("# ")) {
                    page.heading(slot, t.drop(2), 0.88f, NAVY, 1, 0.07f, RED)
                    counter = 0
                    continue
                }
                if (t == "---") {
                    page.gap()
                    continue
                }
                if (t.startsWith(">")) {
                    val body = t.drop(1).trim()
                    val upper = body.uppercase()
                    val warn = listOf("NB", "WARNING", "CAUTION", "TRAP", "EXAM TRAP", "AVOID").any { upper.startsWith(it) }
                    page.callout(slot, body, warn)
                    continue
                }
                if (t.startsWith("|")) {
                    val cells = t.trim('|').split('|').map { it.trim() }
                    if (cells.all { c -> c.all { ch -> ch == '-' || ch == ':' } }) continue
                    tableRow++
                    if (tableRow == 1) {
                        page.item(slot, "", cells.joinToString("  |  "), 0, look.marker, NAVY, true)
                    } else {
                        page.item(slot, "", labelled(cells.first(), cells.drop(1).joinToString("  •  "), look.marker), 0, look.marker, page.bodyInk)
                    }
                    continue
                }

                var body = t
                var dash = false
                if (body.startsWith("- ") || body.startsWith("* ")) {
                    body = body.drop(2).trim()
                    dash = true
                }
                val symbol = MARKS.firstOrNull { body.startsWith("$it ") }
                val numbered = NUMBERED.matchEntire(body)
                val ink = page.bodyInk
                if (symbol != null) {
                    page.item(slot, symbol, body.drop(symbol.length).trim(), level, markColor(symbol, look), ink)
                } else if (numbered != null) {
                    page.item(slot, numbered.groupValues[1], numbered.groupValues[2], level, look.marker, ink)
                } else if (dash) {
                    page.item(slot, "•", body, level, look.marker, ink)
                } else {
                    when (style) {
                        ListStyle.NUMBER -> {
                            counter++
                            page.item(slot, "$counter.", body, level, look.marker, ink)
                        }
                        ListStyle.CHECK -> page.item(slot, "✓", body, level, GREEN, ink)
                        ListStyle.DOT -> page.item(slot, "•", body, level, look.marker, ink)
                        ListStyle.LETTER -> {
                            counter++
                            page.item(slot, ('A' + counter - 1).toString() + ".", body, level, look.marker, ink)
                        }
                        ListStyle.ROW -> {
                            val row = splitRow(body)
                            if (row != null) page.item(slot, "", labelled(row.first, row.second, look.marker), level, look.marker, ink)
                            else page.item(slot, "", body, level, look.marker, ink)
                        }
                        ListStyle.NONE -> page.item(slot, "", body, level, look.marker, ink)
                    }
                }
            }
        }

        val eyebrow = text(PostSlots.EYEBROW)
        if (eyebrow.isNotEmpty()) page.heading(PostSlots.EYEBROW, eyebrow, 0.46f, RED, 1, 0f, null, true, 0.08f)

        val acronym = text(PostSlots.NUMBER)
        if (acronym.isNotEmpty()) page.heading(PostSlots.NUMBER, acronym, 1.7f, PURPLE, 2, 0f)

        val headline = text(PostSlots.HEADLINE)
        if (headline.isNotEmpty()) {
            if (acronym.isNotEmpty()) page.heading(PostSlots.HEADLINE, headline, 0.95f, NAVY, 1, 0.07f, RED)
            else page.heading(PostSlots.HEADLINE, headline, look.headScale, NAVY, look.headCells, 0.08f, RED)
        }

        flow(PostSlots.BODY, text(PostSlots.BODY), ListStyle.NONE)
        flow(PostSlots.LIST, text(PostSlots.LIST), look.list)

        val button = text(PostSlots.BUTTON)
        if (button.isNotEmpty()) page.callout(PostSlots.BUTTON, button, false)

        return page.overflow.toList()
    }
}
