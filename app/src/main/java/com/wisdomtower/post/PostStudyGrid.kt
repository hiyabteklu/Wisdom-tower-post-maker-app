package com.wisdomtower.post

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Picture
import android.graphics.Typeface
import android.text.Layout
import android.text.Spannable
import android.text.StaticLayout
import android.text.TextPaint
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import kotlin.math.ceil
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
 * (A table is the one block that may be drawn with the rows that fit; the slot is still reported, and everything after the table
 * counts as left out, so a later row never appears above a missing table row.)
 *
 * Step 8 (never lose text silently): drawFit() and measure() also return a Fit: how many cells the sheet uses out of how many it
 * has ("Lines 24 / 29"), how many rows were left out, and the first row that was left out. The review screens show that instead of
 * the old "Text shrunk to fit" wording, which was wrong for these sheets (nothing is shrunk, rows are left out).
 *
 * Authoring markup (one line per row of the list, also used for the message):
 *   # Title, ## Topic, ### Subtopic    headings (one cell each, colored, underlined)
 *   - text, • text, ★ text, ✓ text       bullets with a marker; two leading spaces per nesting level (max 2)
 *   1. text, 1.1. text                    numbered rows (the number is kept)
 *   > text                                key idea (green bar); > NB / WARNING / CAUTION / TRAP / AVOID is a red bar
 *   >> text, >> Label | text              Key Point box: green border, the label (default "Key Point") on its first line, then the text
 *   Term: explanation                     the term becomes a bold colored label on every row style (a short label before a colon)
 *   ---                                   one blank line
 *   |a|b|c|                               a real table: consecutive pipe rows, the first row is the header, |---|---| rows are ignored
 *   $$ ... $$, \[ ... \], \begin{pmatrix} ... \end{pmatrix}
 *                                         a display formula on its own row (it may span several lines), typeset by PostTex:
 *                                         stacked fractions, roots, sums and integrals with limits, matrices, aligned equations
 *   $ ... $ alone on a row                promoted to a display formula when it needs two dimensions (frac, sqrt, sum, int, lim, left, begin)
 * Inline styling (explicit marks AND automatic styling of plain text: colored formulas, real sub and superscripts, Greek,
 * emphasis words) is done by PostStudyInline.
 *
 * Text size and color: the post's overall Text size (textScale) and each box's own scale multiply every size. The post's Text color
 * is used for the body and for headings (a color set on one box wins). On a dark background (a dark solid or gradient) every
 * ink is lifted to a light tone and the chips behind marked words are darkened, so nothing is dark on dark.
 */
internal object PostStudyGrid {
    private const val FIRST = 0.025f
    private const val LAST = 0.975f
    private const val RULES = 30
    private const val LAST_CELL = RULES - 2
    private const val DOLLAR = "\u0024"

    /** How far above its rule a baseline sits, in cells. Keeps descenders off the rule and ascenders under the rule above. */
    private const val LIFT = 0.22f

    private val NAVY = Color.rgb(18, 42, 105)
    private val BLUE = Color.rgb(23, 107, 206)
    private val RED = Color.rgb(209, 26, 38)
    private val GREEN = Color.rgb(22, 128, 60)
    private val PURPLE = Color.rgb(124, 58, 237)
    private val INK = Color.rgb(37, 43, 49)
    private val FORMULA = Color.rgb(0, 102, 153)

    /** Term colors, used in turn on the row styles that do not have one color of their own (checklist, numbered, bullets). */
    private val LABELS = intArrayOf(BLUE, RED, GREEN, PURPLE, NAVY)

    private val MARKS = listOf("★", "✦", "✧", "•", "✓", "✔", "☑", "☐", "→")
    private val NUMBERED = Regex("""^(\d+(?:\.\d+)*[.)])\s+(.+)$""")

    /** A whole row that is one environment, for example \begin{pmatrix} ... \end{pmatrix}. */
    private val BEGIN_ENV = Regex("""^\\begin\{([A-Za-z]+\*?)\}[\s\S]*\\end\{\1\}\z""")

    /** LaTeX that needs two dimensions, so a row that is only such a formula is drawn as a display formula. */
    private val NEEDS_2D = Regex(
        """\\(?:d?frac|tfrac|cfrac|binom|sqrt|sum|prod|iint|int|oint|lim|left|begin|over(?![A-Za-z])|underbrace|overbrace|overset|underset|substack|xrightarrow|xleftarrow|xrightleftharpoons|rightleftharpoons|ce)"""
    )

    /**
     * How well a sheet fits its ruled lines (Step 8).
     * slots: the text boxes that had something left out (the same list draw() returns).
     * used / capacity: cells used out of cells available (29, or 28 when the footer takes the last one).
     * leftOut: how many rows were not drawn. firstLeftOut: the first of them, as plain text, so the owner or the agent knows where to split.
     */
    internal class Fit(
        val slots: List<String>,
        val used: Int,
        val capacity: Int,
        val leftOut: Int,
        val firstLeftOut: String
    ) {
        val fits: Boolean get() = slots.isEmpty() && leftOut == 0

        /** "Lines 24 / 29". */
        fun counter(): String = "Lines " + used + " / " + capacity

        /** The warning for a sheet that does not fit; it names what was left out. */
        fun warning(): String {
            val more = if (leftOut > 1) " and " + (leftOut - 1) + " more" else ""
            val where = if (firstLeftOut.isEmpty()) "" else " Left out: \"" + firstLeftOut + "\"" + more + "."
            return "Does not fit, split this sheet." + where
        }
    }

    /** What makes one of the six variants different: marker color, default row style, title size in cells and body size. */
    private data class Look(val marker: Int, val list: ListStyle, val headCells: Int, val headScale: Float, val bodyScale: Float)

    private fun lookFor(id: String): Look = when (id) {
        "study-cover" -> Look(RED, ListStyle.NONE, 3, 1.9f, 0.72f)
        "study-list" -> Look(RED, ListStyle.NUMBER, 2, 1.45f, 0.66f)
        "study-define" -> Look(PURPLE, ListStyle.NONE, 2, 1.5f, 0.70f)
        "study-checklist" -> Look(GREEN, ListStyle.CHECK, 2, 1.35f, 0.64f)
        "study-table" -> Look(BLUE, ListStyle.ROW, 2, 1.35f, 0.64f)
        "study-mnemonic" -> Look(PURPLE, ListStyle.ROW, 2, 1.35f, 0.66f)
        else -> Look(RED, ListStyle.DOT, 2, 1.4f, 0.66f)
    }

    // ---- dark backgrounds ----

    /** True when the post's own solid or gradient background is dark. The template's own paper is light. */
    private fun isDark(doc: PostDoc): Boolean {
        if (doc.bgMode == BgMode.TEMPLATE) return false
        val a = parseColor(doc.bgStartHex) ?: return false
        val b = if (doc.bgMode == BgMode.GRADIENT) (parseColor(doc.bgEndHex) ?: a) else a
        fun lum(c: Int): Double = (0.299 * Color.red(c) + 0.587 * Color.green(c) + 0.114 * Color.blue(c)) / 255.0
        return (lum(a) + lum(b)) / 2.0 < 0.5
    }

    /** A light tone of a dark ink: same hue, softer, bright enough on a dark page. */
    private fun lift(c: Int): Int {
        val hsv = FloatArray(3)
        Color.colorToHSV(c, hsv)
        hsv[1] = min(hsv[1], 0.55f)
        hsv[2] = max(hsv[2], 0.9f)
        return Color.HSVToColor(Color.alpha(c), hsv)
    }

    /** A deep tone of a light chip color, so a lifted word stays readable on it. */
    private fun deepen(c: Int): Int {
        val hsv = FloatArray(3)
        Color.colorToHSV(c, hsv)
        hsv[2] = hsv[2] * 0.4f
        return Color.HSVToColor(Color.alpha(c), hsv)
    }

    /** Lifts every text color and darkens every chip behind marked words, in place. */
    private fun adaptSpans(text: Spannable) {
        for (s in text.getSpans(0, text.length, ForegroundColorSpan::class.java)) {
            val a = text.getSpanStart(s)
            val b = text.getSpanEnd(s)
            val f = text.getSpanFlags(s)
            text.removeSpan(s)
            text.setSpan(ForegroundColorSpan(lift(s.foregroundColor)), a, b, f)
        }
        for (s in text.getSpans(0, text.length, BackgroundColorSpan::class.java)) {
            val a = text.getSpanStart(s)
            val b = text.getSpanEnd(s)
            val f = text.getSpanFlags(s)
            text.removeSpan(s)
            text.setSpan(BackgroundColorSpan(deepen(s.backgroundColor)), a, b, f)
        }
    }

    // ---- handwriting fonts ----

    private var handRegular: Typeface? = null
    private var handBold: Typeface? = null
    private var handLoaded = false

    /** The handwriting font that ships in assets/fonts/english/ (Patrick Hand has only a regular face, bold is drawn from it). */
    private const val BUNDLED_HAND = "fonts/english/PatrickHand-Regular.ttf"

    /**
     * Handwriting fonts: the font picked on this box (own) wins, then the English font picked on the post, then any .ttf/.otf in
     * assets/fonts/hand/ (a file with "bold" in its name is the bold face), then the bundled Patrick Hand, then the phone's own casual font.
     */
    private fun hand(context: Context, doc: PostDoc, bold: Boolean, own: String? = null): Typeface {
        PostFonts.typeface(context, own ?: doc.fontEnglish)?.let { return it }
        synchronized(this) {
            if (!handLoaded) {
                handLoaded = true
                val names = (runCatching { context.assets.list("fonts/hand") }.getOrNull() ?: emptyArray<String>())
                    .filter { it.endsWith(".ttf", true) || it.endsWith(".otf", true) }
                    .sorted()
                fun load(path: String): Typeface? =
                    runCatching { Typeface.createFromAsset(context.assets, path) }.getOrNull()
                val boldName = names.firstOrNull { it.contains("bold", true) }
                val regularName = names.firstOrNull { !it.contains("bold", true) } ?: names.firstOrNull()
                handRegular = regularName?.let { load("fonts/hand/" + it) } ?: load(BUNDLED_HAND)
                handBold = boldName?.let { load("fonts/hand/" + it) } ?: handRegular?.let { Typeface.create(it, Typeface.BOLD) }
            }
        }
        val found = if (bold) handBold else handRegular
        return found ?: Typeface.create("casual", if (bold) Typeface.BOLD else Typeface.NORMAL)
    }

    private fun parseColor(hex: String?): Int? {
        if (hex.isNullOrBlank()) return null
        return runCatching { Color.parseColor(if (hex.startsWith("#")) hex else "#" + hex) }.getOrNull()
    }

    private fun hexOf(color: Int): String = String.format(java.util.Locale.US, "#%06X", color and 0xFFFFFF)

    private fun markColor(symbol: String, look: Look): Int = when (symbol) {
        "✓", "✔", "☑" -> GREEN
        "★" -> RED
        "→" -> BLUE
        "☐" -> INK
        else -> look.marker
    }

    /** "Term: explanation" or "Term — explanation" into its two parts, or null when the line has no label. */
    private fun splitRow(line: String): Pair<String, String>? {
        splitColon(line)?.let { return it }
        for (separator in listOf(" — ", " – ", " - ")) {
            val at = line.indexOf(separator)
            if (at in 1..42) return line.substring(0, at).trim() to line.substring(at + separator.length).trim()
        }
        return null
    }

    /** "Term: explanation" only (a colon within the first 42 characters, before any [[ mark). */
    private fun splitColon(line: String): Pair<String, String>? {
        val colon = line.indexOf(':')
        val markup = line.indexOf("[[")
        if (colon in 1..42 && !(markup in 0 until colon)) {
            return line.substring(0, colon).trim() to line.substring(colon + 1).trim()
        }
        return null
    }

    /** A real term is short: up to 3 words and no sentence marks, so a sentence that merely contains a colon is left alone. */
    private fun goodLabel(label: String): Boolean =
        label.isNotEmpty() && label.split(' ').filter { it.isNotEmpty() }.size <= 3 &&
            label.none { it == '.' || it == ';' || it == '(' || it == '=' }

    private fun labelled(label: String, rest: String, color: Int): String =
        "[[" + hexOf(color) + ";B:" + label + "]]" + (if (rest.isEmpty()) "" else " – " + rest)

    private val COLOR_MARK = Regex("\\[\\[(?:#[0-9A-Fa-f]{6}(?:[0-9A-Fa-f]{2})?(?:;[BUIH]+)?:)?")
    private val SPACES = Regex("\\s+")

    /** A row of markup as short plain text for the "Left out:" note: no tags, no list marker, at most 34 characters. */
    private fun plainLabel(raw: String): String {
        val noise = "*`\\\u0024"
        val text = raw.replace(COLOR_MARK, "").replace("]]", "").replace("==", "")
            .filterNot { it in noise }
            .replace(SPACES, " ")
            .trim()
            .trimStart { it == '-' || it == ' ' || it == '•' || it == '★' || it == '✓' || it == '→' || it == '>' || it == '#' }
        return if (text.length > 34) text.take(33).trimEnd() + "…" else text
    }

    // ---- display formulas ----

    /** The LaTeX of a row that is a display formula, or null when the row is ordinary text. */
    private fun displayLatex(t: String): String? {
        if (t.length > 4 && t.startsWith(DOLLAR + DOLLAR) && t.endsWith(DOLLAR + DOLLAR)) return t.substring(2, t.length - 2).trim()
        if (t.length > 4 && t.startsWith("\\[") && t.endsWith("\\]")) return t.substring(2, t.length - 2).trim()
        if (BEGIN_ENV.matches(t)) return t
        if (t.length > 2 && t.startsWith(DOLLAR) && t.endsWith(DOLLAR) && t.indexOf(DOLLAR, 1) == t.length - 1) {
            val inner = t.substring(1, t.length - 1).trim()
            if (NEEDS_2D.containsMatchIn(inner)) return inner
        }
        return null
    }

    /** How a multi-line display formula starts: "$$", "\[" or "\begin{...}". Null for any other row. */
    private fun opener(t: String): String? = when {
        t.startsWith(DOLLAR + DOLLAR) -> DOLLAR + DOLLAR
        t.startsWith("\\[") -> "\\["
        t.startsWith("\\begin{") -> "\\begin"
        else -> null
    }

    private fun isClosed(text: String, opener: String): Boolean = when (opener) {
        DOLLAR + DOLLAR -> text.length > 4 && text.endsWith(DOLLAR + DOLLAR)
        "\\[" -> text.length > 3 && text.endsWith("\\]")
        else -> BEGIN_ENV.matches(text)
    }

    /**
     * Joins the rows of a display formula that was written over several lines into one row (so a matrix or an aligned block
     * typed on separate lines is one formula). A formula that never closes within 40 rows is left as it was typed.
     */
    private fun mergeMath(src: List<String>): List<String> {
        val out = ArrayList<String>()
        var i = 0
        while (i < src.size) {
            val t = src[i].trim()
            val open = opener(t)
            val hasClose = when (open) {
                null -> true
                DOLLAR + DOLLAR -> t.indexOf(DOLLAR + DOLLAR, 2) >= 0
                "\\[" -> t.contains("\\]")
                else -> t.contains("\\end{")
            }
            if (open != null && !hasClose) {
                val sb = StringBuilder(t)
                var j = i + 1
                var done = false
                while (j < src.size && j < i + 40) {
                    sb.append(' ').append(src[j].trim())
                    if (isClosed(sb.toString(), open)) {
                        done = true
                        break
                    }
                    j++
                }
                if (done) {
                    out.add(sb.toString())
                    i = j + 1
                    continue
                }
            }
            out.add(src[i])
            i++
        }
        return out
    }

    // ---- the page ----

    private class Page(
        val context: Context,
        val canvas: Canvas?,
        val doc: PostDoc,
        val w: Int,
        val h: Int,
        val look: Look,
        val lastCell: Int,
        val fitScale: Float = 1f,
        val brand: PostBrand = PostBrand()
    ) {
        val step: Float = h * (LAST - FIRST) / (RULES - 1)
        val left: Float = w * 0.065f
        val width: Float = w * 0.91f

        val activeTheme: PostGlobalTheme? = if (!doc.themeLock) PostThemes.byId(brand.globalThemeId) else null

        /** A dark background lifts every ink (see isDark). */
        val dark: Boolean = isDark(doc)

        /** The post's Text color, if one was picked. It colors the body and the headings. */
        val userInk: Int? = parseColor(doc.textHex)
        val bodyInk: Int = userInk ?: tone(activeTheme?.notesInkInt ?: INK)
        var cell = 0
        val overflow = LinkedHashSet<String>()

        /** How many rows were not drawn, and the first of them (Step 8). */
        var leftOut = 0
        var firstLeftOut = ""

        fun tone(c: Int): Int = if (dark) lift(c) else c

        /** The post's overall Text size times this box's own size, clamped to the readable minimum floor (0.54 * step). */
        fun scaleOf(slot: String): Float = doc.textScale.coerceIn(0.6f, 1.8f) * (doc.slotStyles[slot]?.scale ?: 1f) * fitScale

        fun bodyPx(slot: String): Float = max(step * 0.54f, step * look.bodyScale * scaleOf(slot))

        fun ruleY(k: Int): Float = h * FIRST + k * step
        fun baseline(c: Int): Float = ruleY(c + 1) - step * LIFT
        fun full(): Boolean = cell > lastCell
        fun gap() { cell += 1 }

        /** Records that a row (or count rows from it on) did not fit: its slot goes in the overflow list, the first one is remembered by name. */
        fun miss(slot: String, raw: String, count: Int = 1) {
            overflow.add(slot)
            leftOut += max(1, count)
            if (firstLeftOut.isEmpty()) firstLeftOut = plainLabel(raw)
        }

        /** Full width on every line (including cell 0), because the study sheet logo sits at BOTTOM_RIGHT outside the ruled lines. */
        fun room(@Suppress("UNUSED_PARAMETER") c: Int, avail: Float): Float = avail

        fun paint(bold: Boolean, size: Float, color: Int, slot: String = ""): TextPaint {
            val p = TextPaint(Paint.ANTI_ALIAS_FLAG)
            p.typeface = hand(context, doc, bold, doc.slotStyles[slot]?.fontEn)
            p.textSize = size
            p.color = color
            return p
        }

        /** auto: false for titles, so only the explicit marks are applied there (see PostStudyInline). */
        fun styled(slot: String, raw: String, auto: Boolean = true): CharSequence {
            val out = PostStudyInline.render(context, raw, doc, doc.slotStyles[slot]?.fontAm, auto)
            if (dark && out is Spannable) adaptSpans(out)
            return out
        }

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
            val cv = canvas ?: return
            for (i in 0 until lay.lineCount) {
                val c = firstCell + i
                if (c > lastCell) break
                cv.save()
                cv.translate(x, baseline(c) - lay.getLineBaseline(i))
                cv.clipRect(-step, lay.getLineTop(i).toFloat(), lay.width + step, lay.getLineBottom(i).toFloat())
                lay.draw(cv)
                cv.restore()
            }
        }

        /** A one-line title that shrinks to the page width (never wraps unless it cannot fit at 45%), bottom-aligned in its cells. */
        fun heading(
            slot: String, raw: String, scale: Float, color: Int, cells: Int, underline: Float,
            underlineColor: Int? = null, caps: Boolean = false, spacing: Float = 0f
        ) {
            if (raw.isBlank()) return
            if (cell + cells - 1 > lastCell) {
                miss(slot, raw)
                cell = lastCell + 1
                return
            }
            val own = doc.slotStyles[slot]
            val ink = parseColor(own?.colorHex) ?: userInk ?: tone(color)
            var size = step * scale * doc.textScale.coerceIn(0.6f, 1.8f) * (doc.slotStyles[slot]?.scale ?: 1f)
            val text = styled(slot, if (caps) raw.uppercase() else raw, false)
            val avail = room(cell, width)
            val p = paint(true, size, ink, slot)
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
                miss(slot, raw)
                cell = lastCell + 1
                return
            }
            val first = cell + (used - lines)
            drawLayout(lay, left, first)
            if (underline > 0f && canvas != null) {
                val u = Paint(Paint.ANTI_ALIAS_FLAG)
                u.color = underlineColor?.let { tone(it) } ?: ink
                u.alpha = 215
                u.strokeWidth = step * underline
                u.strokeCap = Paint.Cap.ROUND
                val y = ruleY(first + lines) - step * 0.12f
                val len = min(avail, lay.getLineWidth(lines - 1) + step * 0.3f)
                canvas.drawLine(left, y, left + len, y, u)
            }
            cell += used
        }

        /**
         * One row: an optional marker, then the text wrapped over as many cells as it needs, with a hanging indent.
         * The marker "•" is drawn as a small filled dot (no font needed); every other marker is text.
         */
        fun item(slot: String, marker: String, raw: String, level: Int, markerColor: Int, ink: Int, bold: Boolean = false) {
            if (full()) {
                miss(slot, raw)
                return
            }
            val size = bodyPx(slot)
            val nest = level * size * 0.9f
            val x0 = left + nest
            val digits = marker.firstOrNull()?.isDigit() == true
            val indent = if (marker.isEmpty()) 0f else if (digits) size * 1.95f else size * 1.2f
            val avail = room(cell, width - nest) - indent
            val lay = layout(styled(slot, raw.ifEmpty { " " }), paint(bold, size, ink, slot), avail.toInt())
            val need = lay.lineCount
            if (cell + need - 1 > lastCell) {
                miss(slot, raw)
                cell = lastCell + 1
                return
            }
            if (canvas != null) {
                if (marker == "•") {
                    val dot = Paint(Paint.ANTI_ALIAS_FLAG)
                    dot.color = tone(markerColor)
                    canvas.drawCircle(x0 + size * 0.3f, baseline(cell) - size * 0.3f, size * 0.13f, dot)
                } else if (marker.isNotEmpty()) {
                    canvas.drawText(marker, x0, baseline(cell), paint(true, size, tone(markerColor), slot))
                }
            }
            drawLayout(lay, x0 + indent, cell)
            cell += need
        }

        /** A tinted box over whole cells with a bar at the left: green for a key idea, red for a warning or exam trap. */
        fun callout(slot: String, raw: String, warn: Boolean) {
            if (full()) {
                miss(slot, raw)
                return
            }
            val bar = tone(if (warn) RED else GREEN)
            val size = bodyPx(slot)
            val pad = size * 0.55f
            val indent = size * 1.4f
            val avail = room(cell, width) - pad - indent - size * 0.2f
            val lay = layout(styled(slot, raw), paint(true, size, bodyInk, slot), avail.toInt())
            val need = lay.lineCount
            if (cell + need - 1 > lastCell) {
                miss(slot, raw)
                cell = lastCell + 1
                return
            }
            if (canvas != null) {
                val top = ruleY(cell) + step * 0.08f
                val bottom = ruleY(cell + need) - step * 0.08f
                val fill = Paint(Paint.ANTI_ALIAS_FLAG)
                fill.color = Color.argb(40, Color.red(bar), Color.green(bar), Color.blue(bar))
                canvas.drawRect(left, top, left + width, bottom, fill)
                val edge = Paint(Paint.ANTI_ALIAS_FLAG)
                edge.color = bar
                canvas.drawRect(left, top, left + size * 0.14f, bottom, edge)
                canvas.drawText(if (warn) "!" else "★", left + pad, baseline(cell), paint(true, size, bar, slot))
            }
            drawLayout(lay, left + pad + indent, cell)
            cell += need
        }

        /**
         * The Key Point box (">> Label | text"): a rounded green border over whole cells, the label with a star on its first cell,
         * then the text wrapped over the following cells. The box is one label cell plus the text cells high.
         */
        fun keyPoint(slot: String, raw: String) {
            if (full()) {
                miss(slot, raw)
                return
            }
            var label = "Key Point"
            var body = raw.trim()
            val bar = body.indexOf(" | ")
            if (bar in 1..30) {
                label = body.substring(0, bar).trim()
                body = body.substring(bar + 3).trim()
            }
            val green = tone(GREEN)
            val size = bodyPx(slot)
            val pad = size * 0.6f
            val avail = room(cell, width) - pad * 2
            val lay = layout(styled(slot, body.ifEmpty { " " }), paint(false, size, bodyInk, slot), avail.toInt())
            val need = 1 + lay.lineCount
            if (cell + need - 1 > lastCell) {
                miss(slot, raw)
                cell = lastCell + 1
                return
            }
            if (canvas != null) {
                val top = ruleY(cell) + step * 0.06f
                val bottom = ruleY(cell + need) - step * 0.06f
                val radius = step * 0.3f
                val fill = Paint(Paint.ANTI_ALIAS_FLAG)
                fill.color = Color.argb(30, Color.red(green), Color.green(green), Color.blue(green))
                canvas.drawRoundRect(left, top, left + width, bottom, radius, radius, fill)
                val edge = Paint(Paint.ANTI_ALIAS_FLAG)
                edge.style = Paint.Style.STROKE
                edge.color = green
                edge.strokeWidth = max(2.5f, step * 0.07f)
                canvas.drawRoundRect(left, top, left + width, bottom, radius, radius, edge)
                canvas.drawText("★ " + label, left + pad, baseline(cell), paint(true, size, green, slot))
            }
            drawLayout(lay, left + pad, cell + 1)
            cell += need
        }

        /**
         * A display formula on whole cells (Step B). PostTex typesets the LaTeX; a formula wider than the page is split at its relations
         * and operators into several lines, and only a single huge block (a big matrix) is scaled down to the width.
         */
        fun math(slot: String, latex: String) {
            if (full()) {
                miss(slot, "formula " + latex)
                return
            }
            val own = doc.slotStyles[slot]
            val size = bodyPx(slot) * 1.16f
            val ink = parseColor(own?.colorHex) ?: tone(FORMULA)
            val usable = room(cell, width)
            val avail = max(usable - size * 0.9f, size * 4f)
            val boxes = PostTex.typeset(latex, size, ink, avail)
            if (boxes == null || boxes.isEmpty()) {
                item(slot, "", DOLLAR + latex.replace('\n', ' ').trim() + DOLLAR, 0, look.marker, bodyInk)
                return
            }
            val scales = ArrayList<Float>()
            val heights = ArrayList<Int>()
            var need = 0
            for (b in boxes) {
                val k = if (b.w > avail && b.w > 0f) avail / b.w else 1f
                val cells = max(1, ceil(((b.asc + b.desc) * k + step * 0.2f) / step).toInt())
                scales.add(k)
                heights.add(cells)
                need += cells
            }
            if (cell + need - 1 > lastCell) {
                miss(slot, "formula " + latex)
                cell = lastCell + 1
                return
            }
            if (canvas != null) {
                val panel = Paint(Paint.ANTI_ALIAS_FLAG)
                panel.color = Color.argb(24, Color.red(ink), Color.green(ink), Color.blue(ink))
                val radius = step * 0.3f
                canvas.drawRoundRect(left, ruleY(cell) + step * 0.06f, left + usable, ruleY(cell + need) - step * 0.06f, radius, radius, panel)
                var c0 = cell
                for ((n, b) in boxes.withIndex()) {
                    val k = scales[n]
                    val cells = heights[n]
                    val mid = (ruleY(c0) + ruleY(c0 + cells)) / 2f
                    val base = mid + (b.asc - b.desc) * k / 2f
                    val x = left + (usable - b.w * k) / 2f
                    canvas.save()
                    canvas.translate(x, base)
                    canvas.scale(k, k)
                    b.draw(canvas, 0f, 0f)
                    canvas.restore()
                    c0 += cells
                }
            }
            cell += need
        }

        /**
         * A real table on whole cells. Supports 2 to 8+ columns, uneven rows, long cells, and inline formulas inside cells.
         * Dynamically scales font size and horizontal padding when columns >= 4 so wide tables never clip horizontally.
         */
        fun table(slot: String, rows: List<List<String>>) {
            if (rows.isEmpty()) return
            if (full()) {
                miss(slot, rows[0].firstOrNull().orEmpty(), rows.size)
                return
            }
            val columns = max(1, rows.maxOf { it.size })
            val colFactor = when {
                columns >= 7 -> 0.64f
                columns >= 5 -> 0.75f
                columns == 4 -> 0.85f
                else -> 0.94f
            }
            val size = max(step * 0.48f, bodyPx(slot) * colFactor)
            val padX = size * (if (columns >= 5) 0.18f else 0.28f)
            val usable = room(cell, width)
            val weights = FloatArray(columns) { c ->
                val longest = rows.maxOf { r -> r.getOrElse(c) { "" }.length }
                max(4f, min(32f, longest.toFloat()))
            }
            val total = max(1f, weights.sum())
            val minColW = usable / max(2f, columns * 1.65f)
            val widths = FloatArray(columns) { max(minColW, usable * weights[it] / total) }
            val fitScaleW = usable / max(1f, widths.sum())
            for (c in widths.indices) widths[c] *= fitScaleW

            val color = tone(BLUE)
            val laid = ArrayList<Pair<List<StaticLayout>, Int>>()
            for ((r, row) in rows.withIndex()) {
                val header = r == 0
                val lays = (0 until columns).map { c ->
                    val ink = if (header) tone(NAVY) else if (c == 0) tone(look.marker) else bodyInk
                    val cellText = styled(slot, row.getOrElse(c) { "" }.ifEmpty { " " })
                    val cellAvail = max(1, (widths[c] - padX * 2).toInt())
                    var cellPaint = paint(header || c == 0, size, ink, slot)
                    // Shrink cell font slightly if a single word (or header token) is wider than the column width
                    val maxWord = cellText.toString().split(' ').maxOfOrNull { cellPaint.measureText(it) } ?: 0f
                    if (maxWord > cellAvail && size > step * 0.46f) {
                        val shrunk = max(step * 0.46f, size * (cellAvail / maxWord).coerceIn(0.68f, 1f))
                        cellPaint = paint(header || c == 0, shrunk, ink, slot)
                    }
                    layout(cellText, cellPaint, cellAvail)
                }
                laid.add(lays to max(1, lays.maxOf { it.lineCount }))
            }

            var used = 0
            var partial = false
            val shown = ArrayList<Pair<List<StaticLayout>, Int>>()
            for ((index, row) in laid.withIndex()) {
                if (cell + used + row.second - 1 > lastCell) {
                    miss(slot, rows[index].firstOrNull().orEmpty(), laid.size - index)
                    partial = true
                    break
                }
                used += row.second
                shown.add(row)
            }
            if (shown.isEmpty()) {
                cell = lastCell + 1
                return
            }

            if (canvas != null) {
                val top = ruleY(cell)
                val bottom = ruleY(cell + used)
                val right = left + usable

                val headFill = Paint(Paint.ANTI_ALIAS_FLAG)
                headFill.color = Color.argb(42, Color.red(color), Color.green(color), Color.blue(color))
                canvas.drawRect(left, top, right, ruleY(cell + shown[0].second), headFill)

                val line = Paint(Paint.ANTI_ALIAS_FLAG)
                line.color = color
                line.strokeWidth = max(2f, step * 0.05f)
                line.strokeCap = Paint.Cap.SQUARE
                var rowCell = cell
                canvas.drawLine(left, top, right, top, line)
                for (row in shown) {
                    rowCell += row.second
                    canvas.drawLine(left, ruleY(rowCell), right, ruleY(rowCell), line)
                }
                var x = left
                canvas.drawLine(x, top, x, bottom, line)
                for (c in 0 until columns) {
                    x += widths[c]
                    canvas.drawLine(x, top, x, bottom, line)
                }

                rowCell = cell
                for (row in shown) {
                    var cx = left
                    for (c in 0 until columns) {
                        drawLayout(row.first[c], cx + padX, rowCell)
                        cx += widths[c]
                    }
                    rowCell += row.second
                }
            }
            cell += used
            if (partial) cell = lastCell + 1
        }
    }

    /**
     * Draws the text slots of a study post on the line grid and returns the slots that did not fit (empty when everything fits).
     * slots: the text boxes the template has; text in other slots is ignored, as for every template.
     */
    fun draw(context: Context, canvas: Canvas, doc: PostDoc, w: Int, h: Int, slots: Set<String>, brand: PostBrand = PostBrand()): List<String> =
        drawFit(context, canvas, doc, w, h, slots, brand).slots

    /**
     * Counts how many lines a post takes without drawing it anywhere visible.
     * Used by the review screens to show "Lines 24 / 29" or the "Does not fit" note next to the preview.
     */
    fun measure(context: Context, doc: PostDoc, brand: PostBrand = PostBrand()): Fit {
        val w = doc.size.width
        val h = doc.size.height
        val slots = postTemplate(doc.templateId).layout(doc.size).slots
            .filter { it.literal == null }
            .map { it.slot }
            .toSet()
        return drawFit(context, null, doc, w, h, slots, brand)
    }

    /**
     * Same as draw(), and also returns the Fit of the sheet (cells used, rows left out, the first one left out).
     * Shrink-to-readable-minimum overflow policy: if the sheet overflows at 1.0x scale, automatically tries progressively
     * tighter body scales down to the readable floor (0.54 * step) before leaving any rows out.
     */
    fun drawFit(context: Context, canvas: Canvas?, doc: PostDoc, w: Int, h: Int, slots: Set<String>, brand: PostBrand = PostBrand()): Fit {
        val candidateScales = floatArrayOf(1.0f, 0.90f, 0.82f, 0.75f, 0.68f)
        var chosenScale = 1.0f
        var lastProbeFit: Fit? = null
        for (sc in candidateScales) {
            val fit = drawFitOnce(context, null, doc, w, h, slots, sc, brand)
            lastProbeFit = fit
            chosenScale = sc
            if (fit.fits) break
        }
        if (canvas == null && lastProbeFit != null) {
            return lastProbeFit
        }
        return drawFitOnce(context, canvas, doc, w, h, slots, chosenScale, brand)
    }

    private fun drawFitOnce(
        context: Context,
        canvas: Canvas?,
        doc: PostDoc,
        w: Int,
        h: Int,
        slots: Set<String>,
        fitScale: Float,
        brand: PostBrand = PostBrand()
    ): Fit {
        val activeTheme = if (!doc.themeLock) PostThemes.byId(brand.globalThemeId) else null
        val baseLook = lookFor(doc.templateId)
        val look = if (activeTheme != null) {
            baseLook.copy(marker = activeTheme.accentInt)
        } else {
            baseLook
        }
        val headMain = activeTheme?.notesInkInt ?: NAVY
        val headSub = activeTheme?.secondaryInt ?: BLUE
        val headRule = activeTheme?.accentInt ?: RED
        val page = Page(context, canvas, doc, w, h, look, if (doc.showFooter) LAST_CELL - 1 else LAST_CELL, fitScale, brand)

        fun text(slot: String): String =
            if (slot in slots && slot !in doc.hiddenTextSlots) doc.texts[slot].orEmpty().trim() else ""

        var labelIdx = 0

        fun lab(text: String, rowStyle: Boolean): String {
            val row = (if (rowStyle) splitRow(text) else splitColon(text)) ?: return text
            if (!rowStyle && !goodLabel(row.first)) return text
            val color = if (rowStyle) look.marker else LABELS[labelIdx++ % LABELS.size]
            return labelled(row.first, row.second, color)
        }

        fun flow(slot: String, raw: String, style: ListStyle) {
            if (raw.isEmpty()) return
            var counter = 0
            val lines = mergeMath(raw.split('\n').map { it.trimEnd() }.filter { it.isNotBlank() })
            var i = 0
            while (i < lines.size) {
                val line = lines[i]
                i++
                if (page.full()) {
                    val rest = lines.drop(i - 1).filter { it.trim() != "---" }
                    if (rest.isNotEmpty()) page.miss(slot, rest[0], rest.size)
                    return
                }
                val t = line.trim()
                val level = min(2, (line.length - line.trimStart().length) / 2)

                val tex = displayLatex(t)
                if (tex != null) {
                    page.math(slot, tex)
                    continue
                }
                if (t.startsWith("### ")) {
                    page.heading(slot, t.drop(4), 0.72f, look.marker, 1, 0f)
                    counter = 0
                    continue
                }
                if (t.startsWith("## ")) {
                    page.heading(slot, t.drop(3), 0.80f, headSub, 1, 0.06f)
                    counter = 0
                    continue
                }
                if (t.startsWith("# ")) {
                    page.heading(slot, t.drop(2), 0.88f, headMain, 1, 0.07f, headRule)
                    counter = 0
                    continue
                }
                if (t == "---") {
                    page.gap()
                    continue
                }
                if (t.startsWith(">>")) {
                    page.keyPoint(slot, t.drop(2).trim())
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
                    val rows = ArrayList<List<String>>()
                    var current = t
                    while (true) {
                        val cells = current.trim('|').split('|').map { it.trim() }
                        val separator = cells.all { c -> c.isNotEmpty() && c.all { ch -> ch == '-' || ch == ':' } }
                        if (!separator) rows.add(cells)
                        if (i < lines.size && lines[i].trim().startsWith("|")) {
                            current = lines[i].trim()
                            i++
                        } else {
                            break
                        }
                    }
                    page.table(slot, rows)
                    counter = 0
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
                    page.item(slot, symbol, lab(body.drop(symbol.length).trim(), false), level, markColor(symbol, look), ink)
                } else if (numbered != null) {
                    page.item(slot, numbered.groupValues[1], lab(numbered.groupValues[2], false), level, look.marker, ink)
                } else if (dash) {
                    page.item(slot, "•", lab(body, false), level, look.marker, ink)
                } else {
                    when (style) {
                        ListStyle.NUMBER -> {
                            counter++
                            page.item(slot, "$counter.", lab(body, false), level, look.marker, ink)
                        }
                        ListStyle.CHECK -> page.item(slot, "✓", lab(body, false), level, GREEN, ink)
                        ListStyle.DOT -> page.item(slot, "•", lab(body, false), level, look.marker, ink)
                        ListStyle.LETTER -> {
                            counter++
                            page.item(slot, ('A' + counter - 1).toString() + ".", lab(body, false), level, look.marker, ink)
                        }
                        ListStyle.ROW -> page.item(slot, "•", lab(body, true), level, look.marker, ink)
                        ListStyle.NONE -> page.item(slot, "", lab(body, false), level, look.marker, ink)
                    }
                }
            }
        }

        // Title (HEADLINE) is ALWAYS drawn first on line 1 (cell = 0).
        // EYEBROW is accepted by slots for backwards compatibility, but never drawn above the title.
        val acronym = text(PostSlots.NUMBER)
        val headline = text(PostSlots.HEADLINE)
        if (headline.isNotEmpty()) {
            page.heading(PostSlots.HEADLINE, headline, look.headScale, headMain, look.headCells, 0.08f, headRule)
        }
        if (acronym.isNotEmpty()) {
            page.heading(PostSlots.NUMBER, acronym, 1.45f, activeTheme?.secondaryInt ?: PURPLE, 2, 0f)
        }

        flow(PostSlots.BODY, text(PostSlots.BODY), ListStyle.NONE)
        flow(PostSlots.LIST, text(PostSlots.LIST), look.list)

        val button = text(PostSlots.BUTTON)
        if (button.isNotEmpty()) page.callout(PostSlots.BUTTON, button, false)

        val capacity = page.lastCell + 1
        return Fit(page.overflow.toList(), min(page.cell, capacity), capacity, page.leftOut, page.firstLeftOut)
    }
}
