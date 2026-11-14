package com.wisdomtower.post

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.text.style.MetricAffectingSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.text.style.TypefaceSpan
import android.text.style.UnderlineSpan
import android.util.Log

/**
 * Inline text styling for the Chapter summaries (study-* templates). Used by PostStudyGrid instead of PostFonts.styled.
 *
 * Why it exists: content agents send plain sentences, and a sheet must still look like a colorful handwritten page.
 * So this does two jobs.
 *
 * 1. Explicit markup (always honored, can be nested):
 *    [[#HEX;FLAGS:text]] or [[#HEX:text]] colored text (FLAGS: B bold, I italic, U underline, H highlight)
 *    [[key term]] bold blue on a light blue chip | **bold** | *italic* | ==highlight== | #underline# | `code` | \box{note}
 *    $math$, $$math$$ formulas (Greek, real sub and superscripts, roots, fractions as one-line text through PostMath)
 *
 * 2. Automatic styling of plain text (auto = true, off for titles):
 *    - formulas such as F=-kx or T=2π√(m/k) (a token with = in it) become colored italic;
 *    - v_max, a_max, vmax, amin, x^2, s^-1 become real baseline-shifted subscripts and superscripts;
 *    - Greek letters and math symbols use a serif italic face so they match each other (the handwriting font has no Greek);
 *    - remarks in brackets with two or more words, for example (within elastic limit), become muted italic;
 *    - not / never / always / only are bold red, and maximum / minimum / zero / constant / conserved / proportional /
 *      opposite are bold.
 *
 * It never throws: on any problem the text is returned as typed (and the reason is logged under PostStudyInline).
 * Nothing here has been compiled yet (see docs/CHAPTER_SUMMARIES_PROGRESS.md).
 */
internal object PostStudyInline {
    private const val EXCL = Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    private const val DOLLAR = "\u0024"
    private const val SYMBOLS = "√±∞≈≠≤≥∝∂∇∫∑∏→⇒⇔×÷"

    private val FORMULA_INK = Color.rgb(0, 102, 153)
    private val NEGATION_INK = Color.rgb(209, 26, 38)
    private val MUTED_INK = Color.rgb(84, 96, 120)
    private val HIGHLIGHT = Color.rgb(255, 235, 105)
    private val HIGHLIGHT_TEXT = Color.rgb(55, 42, 8)
    private val KEY_BG = Color.rgb(222, 240, 255)
    private val KEY_INK = Color.rgb(20, 83, 150)
    private val BOX_BG = Color.rgb(255, 246, 205)
    private val BOX_INK = Color.rgb(110, 68, 12)

    private val MATH_FACE: Typeface by lazy { Typeface.create("serif", Typeface.ITALIC) }
    private val SYMBOL_FACE: Typeface by lazy { Typeface.create("serif", Typeface.NORMAL) }

    private val COLOR_TAG = Regex("(#[0-9A-Fa-f]{6}(?:[0-9A-Fa-f]{2})?)(?:;([BUIH]+))?:([\\s\\S]*)")
    private val FORMULA = Regex("(?:[^\\s=;,]+ = [^\\s;,]+|[^\\s=;,]*=[^\\s;,]*)(?: [A-Za-z]+\\([^)\\s]*\\))?")
    private val SCRIPT = Regex("\\^(\\{[^}]*\\}|[+\\-\u2212]?[0-9]+|[A-Za-z])|_(\\{[^}]*\\}|[0-9]+|[A-Za-z]+)|\\b([A-Za-z])(max|min|rms|avg)\\b")
    private val PAREN = Regex("\\(([^()=]*\\s[^()=]*)\\)")
    private val NEGATION = Regex("\\b(not|never|always|only)\\b", RegexOption.IGNORE_CASE)
    private val KEYWORD = Regex("\\b(maximum|minimum|zero|constant|conserved|proportional|opposite)\\b", RegexOption.IGNORE_CASE)
    private val SPACES = Regex("\\s+")

    // ---- spans ----

    /** Raised (superscript) or lowered (subscript) smaller text that stays inside its line. */
    private class ScriptSpan(private val up: Boolean) : MetricAffectingSpan() {
        override fun updateDrawState(p: TextPaint) { shift(p) }
        override fun updateMeasureState(p: TextPaint) { shift(p) }
        private fun shift(p: TextPaint) {
            val asc = p.ascent()
            if (up) p.baselineShift += (asc * 0.42f).toInt() else p.baselineShift -= (asc * 0.18f).toInt()
            p.textSize = p.textSize * 0.68f
        }
    }

    private class FaceSpan(private val face: Typeface) : MetricAffectingSpan() {
        override fun updateDrawState(p: TextPaint) { p.typeface = face }
        override fun updateMeasureState(p: TextPaint) { p.typeface = face }
    }

    // ---- entry point ----

    fun render(context: Context, raw: String, doc: PostDoc, amharicOwn: String?, auto: Boolean = true): CharSequence =
        try {
            val out = SpannableStringBuilder()
            parse(raw.replace("\\newline", " "), out, auto, 0)
            amharic(context, out, doc, amharicOwn)
            out
        } catch (e: Exception) {
            Log.w("PostStudyInline", "inline markup failed: " + e.message)
            raw
        }

    private fun span(out: SpannableStringBuilder, body: () -> Unit, apply: (Int, Int) -> Unit) {
        val s = out.length
        body()
        val e = out.length
        if (e > s) apply(s, e)
    }

    // ---- explicit markup ----

    private fun parse(src: String, out: SpannableStringBuilder, auto: Boolean, depth: Int) {
        if (depth > 6) {
            out.append(src)
            return
        }
        val plain = StringBuilder()
        fun flush() {
            if (plain.isNotEmpty()) {
                appendPlain(out, plain.toString(), auto)
                plain.setLength(0)
            }
        }
        var i = 0
        while (i < src.length) {
            val c = src[i]
            var consumed = 0
            if (c == '[' && src.startsWith("[[", i)) {
                val end = src.indexOf("]]", i + 2)
                if (end > i + 2) {
                    flush()
                    tag(src.substring(i + 2, end), out, depth)
                    consumed = end + 2 - i
                }
            } else if (c == '\u0024') {
                val double = src.startsWith(DOLLAR + DOLLAR, i)
                val open = if (double) 2 else 1
                val end = src.indexOf(if (double) DOLLAR + DOLLAR else DOLLAR, i + open)
                if (end > i + open && !src[i + open].isWhitespace()) {
                    flush()
                    math(src.substring(i + open, end), out)
                    consumed = end + open - i
                }
            } else if (c == '*' && src.startsWith("**", i)) {
                val end = src.indexOf("**", i + 2)
                if (end > i + 2) {
                    flush()
                    span(out, { parse(src.substring(i + 2, end), out, auto, depth + 1) }) { s, e ->
                        out.setSpan(StyleSpan(Typeface.BOLD), s, e, EXCL)
                    }
                    consumed = end + 2 - i
                }
            } else if (c == '*') {
                val next = src.getOrNull(i + 1)
                val end = src.indexOf('*', i + 1)
                if (next != null && !next.isWhitespace() && next != '*' && end > i + 1 &&
                    !src[end - 1].isWhitespace() && src.getOrNull(end + 1) != '*'
                ) {
                    flush()
                    span(out, { parse(src.substring(i + 1, end), out, auto, depth + 1) }) { s, e ->
                        out.setSpan(StyleSpan(Typeface.ITALIC), s, e, EXCL)
                    }
                    consumed = end + 1 - i
                }
            } else if (c == '=' && src.startsWith("==", i)) {
                val end = src.indexOf("==", i + 2)
                if (end > i + 2) {
                    flush()
                    span(out, { parse(src.substring(i + 2, end), out, false, depth + 1) }) { s, e ->
                        out.setSpan(BackgroundColorSpan(HIGHLIGHT), s, e, EXCL)
                        out.setSpan(ForegroundColorSpan(HIGHLIGHT_TEXT), s, e, EXCL)
                    }
                    consumed = end + 2 - i
                }
            } else if (c == '`') {
                val end = src.indexOf('`', i + 1)
                if (end > i + 1) {
                    flush()
                    val s = out.length
                    out.append(src, i + 1, end)
                    out.setSpan(TypefaceSpan("monospace"), s, out.length, EXCL)
                    out.setSpan(BackgroundColorSpan(Color.rgb(238, 238, 238)), s, out.length, EXCL)
                    consumed = end + 1 - i
                }
            } else if (c == '\\' && src.startsWith("\\box{", i)) {
                val end = src.indexOf('}', i + 5)
                if (end > i + 5) {
                    flush()
                    span(out, { parse(src.substring(i + 5, end), out, false, depth + 1) }) { s, e ->
                        out.setSpan(BackgroundColorSpan(BOX_BG), s, e, EXCL)
                        out.setSpan(ForegroundColorSpan(BOX_INK), s, e, EXCL)
                        out.setSpan(StyleSpan(Typeface.BOLD), s, e, EXCL)
                    }
                    consumed = end + 1 - i
                }
            } else if (c == '#') {
                val next = src.getOrNull(i + 1)
                val end = src.indexOf('#', i + 1)
                if (next != null && !next.isWhitespace() && end > i + 1 && !src[end - 1].isWhitespace()) {
                    flush()
                    span(out, { parse(src.substring(i + 1, end), out, auto, depth + 1) }) { s, e ->
                        out.setSpan(UnderlineSpan(), s, e, EXCL)
                    }
                    consumed = end + 1 - i
                }
            }
            if (consumed > 0) {
                i += consumed
            } else {
                plain.append(c)
                i++
            }
        }
        flush()
    }

    /** The inside of [[...]]: a colored span when it starts with #HEX, otherwise a key term. */
    private fun tag(inner: String, out: SpannableStringBuilder, depth: Int) {
        val m = COLOR_TAG.matchEntire(inner)
        if (m != null) {
            val color = runCatching { Color.parseColor(m.groupValues[1]) }.getOrNull()
            val flags = m.groupValues[2]
            span(out, { parse(m.groupValues[3], out, false, depth + 1) }) { s, e ->
                if (color != null) out.setSpan(ForegroundColorSpan(color), s, e, EXCL)
                if ('B' in flags) out.setSpan(StyleSpan(Typeface.BOLD), s, e, EXCL)
                if ('I' in flags) out.setSpan(StyleSpan(Typeface.ITALIC), s, e, EXCL)
                if ('U' in flags) out.setSpan(UnderlineSpan(), s, e, EXCL)
                if ('H' in flags) out.setSpan(BackgroundColorSpan(HIGHLIGHT), s, e, EXCL)
            }
        } else {
            span(out, { parse(inner, out, false, depth + 1) }) { s, e ->
                out.setSpan(BackgroundColorSpan(KEY_BG), s, e, EXCL)
                out.setSpan(ForegroundColorSpan(KEY_INK), s, e, EXCL)
                out.setSpan(StyleSpan(Typeface.BOLD), s, e, EXCL)
            }
        }
    }

    // ---- math ----

    private fun math(latex: String, out: SpannableStringBuilder) {
        val s = out.length
        appendMath(latex.trim(), out)
        val e = out.length
        if (e > s) {
            out.setSpan(ForegroundColorSpan(FORMULA_INK), s, e, EXCL)
            out.setSpan(StyleSpan(Typeface.ITALIC), s, e, EXCL)
            decorate(out, s, e)
        }
    }

    /** Top-level ^ and _ become real raised or lowered text; everything else goes through PostMath (one-line Unicode text). */
    private fun appendMath(latex: String, out: SpannableStringBuilder) {
        val seg = StringBuilder()
        var depth = 0
        var i = 0
        fun flushSeg() {
            if (seg.isNotEmpty()) {
                out.append(convert(seg.toString()))
                seg.setLength(0)
            }
        }
        while (i < latex.length) {
            val c = latex[i]
            if (c == '{') {
                depth++
                seg.append(c)
                i++
            } else if (c == '}') {
                depth--
                seg.append(c)
                i++
            } else if ((c == '^' || c == '_') && depth == 0) {
                flushSeg()
                val (arg, next) = readArg(latex, i + 1)
                i = next
                val s = out.length
                out.append(PostMath.toText(arg))
                if (out.length > s) out.setSpan(ScriptSpan(c == '^'), s, out.length, EXCL)
            } else {
                seg.append(c)
                i++
            }
        }
        flushSeg()
    }

    /** PostMath trims its result, so a space at the edge of a piece is put back. */
    private fun convert(seg: String): String {
        if (seg.isEmpty()) return ""
        val lead = if (seg.first() == ' ') " " else ""
        val trail = if (seg.length > 1 && seg.last() == ' ') " " else ""
        return lead + PostMath.toText(seg) + trail
    }

    /** One argument after ^ or _: a {group}, a \command, or one character. Returns the text and the index after it. */
    private fun readArg(s: String, start: Int): Pair<String, Int> {
        var i = start
        while (i < s.length && s[i] == ' ') i++
        if (i >= s.length) return Pair("", i)
        val c = s[i]
        if (c == '{') {
            var depth = 0
            var j = i
            while (j < s.length) {
                if (s[j] == '{') depth++
                if (s[j] == '}') {
                    depth--
                    if (depth == 0) break
                }
                j++
            }
            val end = if (j < s.length) j else s.length
            return Pair(s.substring(i + 1, end), minOf(end + 1, s.length))
        }
        if (c == '\\') {
            var j = i + 1
            while (j < s.length && s[j].isLetter()) j++
            val end = if (j == i + 1) minOf(i + 2, s.length) else j
            return Pair(s.substring(i, end), end)
        }
        return Pair(c.toString(), i + 1)
    }

    // ---- plain text with automatic styling ----

    private fun appendPlain(out: SpannableStringBuilder, text: String, auto: Boolean) {
        if (!auto) {
            val s = out.length
            out.append(text)
            decorate(out, s, out.length)
            return
        }
        var cursor = 0
        for (m in FORMULA.findAll(text)) {
            if (m.range.first < cursor) continue
            var token = m.value
            while (token.isNotEmpty() && token.last() in ".:!?") token = token.dropLast(1)
            if (token.length < 3 || token.none { it.isLetter() }) continue
            plainRun(out, text.substring(cursor, m.range.first))
            val s = out.length
            scripts(out, token)
            val e = out.length
            if (e > s) {
                out.setSpan(ForegroundColorSpan(FORMULA_INK), s, e, EXCL)
                out.setSpan(StyleSpan(Typeface.ITALIC), s, e, EXCL)
                decorate(out, s, e)
            }
            cursor = m.range.first + token.length
        }
        plainRun(out, text.substring(cursor))
    }

    private fun plainRun(out: SpannableStringBuilder, text: String) {
        if (text.isEmpty()) return
        val s = out.length
        scripts(out, text)
        val e = out.length
        emphasise(out, s, e)
        decorate(out, s, e)
    }

    /** v_max, vmax, a_{max}, x^2, s^-1 become real sub and superscripts. */
    private fun scripts(out: SpannableStringBuilder, text: String) {
        var cursor = 0
        for (m in SCRIPT.findAll(text)) {
            out.append(text, cursor, m.range.first)
            val sup = m.groups[1]
            val sub = m.groups[2]
            if (sup != null) {
                addScript(out, sup.value.trim('{', '}'), true)
            } else if (sub != null) {
                addScript(out, sub.value.trim('{', '}'), false)
            } else {
                out.append(m.groupValues[3])
                addScript(out, m.groupValues[4], false)
            }
            cursor = m.range.last + 1
        }
        out.append(text, cursor, text.length)
    }

    private fun addScript(out: SpannableStringBuilder, arg: String, up: Boolean) {
        if (arg.isEmpty()) return
        val s = out.length
        out.append(arg.replace('-', '\u2212'))
        out.setSpan(ScriptSpan(up), s, out.length, EXCL)
    }

    private fun emphasise(out: SpannableStringBuilder, s: Int, e: Int) {
        val t = out.subSequence(s, e).toString()
        for (m in PAREN.findAll(t)) {
            val inside = m.groupValues[1]
            if (inside.trim().split(SPACES).size >= 2 && inside.any { it.isLetter() }) {
                out.setSpan(StyleSpan(Typeface.ITALIC), s + m.range.first, s + m.range.last + 1, EXCL)
                out.setSpan(ForegroundColorSpan(MUTED_INK), s + m.range.first, s + m.range.last + 1, EXCL)
            }
        }
        for (m in NEGATION.findAll(t)) {
            out.setSpan(StyleSpan(Typeface.BOLD), s + m.range.first, s + m.range.last + 1, EXCL)
            out.setSpan(ForegroundColorSpan(NEGATION_INK), s + m.range.first, s + m.range.last + 1, EXCL)
        }
        for (m in KEYWORD.findAll(t)) {
            out.setSpan(StyleSpan(Typeface.BOLD), s + m.range.first, s + m.range.last + 1, EXCL)
        }
    }

    /** Greek letters get a serif italic face (the handwriting font has none, so they used to come from another font); math symbols a serif face. */
    private fun decorate(out: SpannableStringBuilder, s: Int, e: Int) {
        for (k in s until e) {
            val ch = out[k]
            if (ch.code in 0x0370..0x03FF) {
                out.setSpan(FaceSpan(MATH_FACE), k, k + 1, EXCL)
                out.setSpan(RelativeSizeSpan(1.12f), k, k + 1, EXCL)
            } else if (ch in SYMBOLS || ch.code in 0x2070..0x209F) {
                out.setSpan(FaceSpan(SYMBOL_FACE), k, k + 1, EXCL)
            }
        }
    }

    // ---- Amharic ----

    private fun isEthiopic(c: Char): Boolean =
        c.code in 0x1200..0x139F || c.code in 0x2D80..0x2DDF || c.code in 0xAB00..0xAB2F

    private fun amharic(context: Context, out: SpannableStringBuilder, doc: PostDoc, own: String?) {
        val text = out.toString()
        if (text.none { isEthiopic(it) }) return
        val face = PostFonts.typeface(context, own)
            ?: PostFonts.typeface(context, doc.fontAmharic ?: PostFonts.list(context, PostFonts.AMHARIC).firstOrNull()?.path)
            ?: return
        var i = 0
        while (i < text.length) {
            if (isEthiopic(text[i])) {
                var j = i
                while (j < text.length && (isEthiopic(text[j]) || (text[j] == ' ' && j + 1 < text.length && isEthiopic(text[j + 1])))) j++
                out.setSpan(FaceSpan(face), i, j, EXCL)
                i = j
            } else {
                i++
            }
        }
    }
}
