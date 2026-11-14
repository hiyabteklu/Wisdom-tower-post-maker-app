package com.wisdomtower.post

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.Typeface
import android.util.Log
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * A small LaTeX math layout engine for the study sheets (docs/MATH_AND_TEMPLATES_PLAN.md, Step B). No dependency: it parses LaTeX,
 * lays it out in boxes the way TeX does (width, height above the baseline, depth below it) and draws the boxes on a Canvas.
 *
 * Why it exists: PostMath only makes one line of Unicode text. This engine draws stacked fractions, root signs with an index,
 * big operators with limits, integrals, scripts, stretching brackets, matrices, cases, aligned and multi-line equations.
 * The LaTeX source stays in the post text; this only turns it into a picture at draw time.
 *
 * Supported: fractions (frac, dfrac, tfrac, binom, over), sqrt with index, super and subscripts (and primes), Greek letters,
 * about 150 symbols, sum, prod, int (iint, oint), lim, max, min and the other limit operators, sin, cos, log and the other named
 * operators, left and right brackets of any size, matrix, pmatrix, bmatrix, vmatrix, cases, aligned, align, gather, array, substack,
 * accents (hat, bar, vec, dot, ddot, tilde, overline, underline, overbrace, underbrace, cancel), text, mathrm, mathbf, mathbb,
 * color, boxed, phantom, and the spacing commands. A line that is too wide is split at relations and operators, never squeezed.
 *
 * typeset() never throws: it returns null for LaTeX it cannot read, and the caller then shows the formula as plain text.
 * Nothing here has been compiled yet (see the plan).
 */
internal object PostTex {
    /** A laid-out piece of math: width, height above and depth below the baseline, and how to draw it (y is the baseline). */
    internal abstract class Box {
        var w = 0f
        var asc = 0f
        var desc = 0f
        abstract fun draw(c: Canvas, x: Float, y: Float)
    }

    private const val AXIS = 0.25f
    private val LEVEL_SCALE = floatArrayOf(1f, 1f, 0.7f, 0.5f)

    private const val F_AUTO = 0
    private const val F_ROMAN = 1
    private const val F_BOLD = 2
    private const val F_ITALIC = 3
    private const val F_BB = 4
    private const val F_SANS = 5
    private const val F_MONO = 6
    private const val F_BIT = 7

    private val ROMAN: Typeface by lazy { Typeface.create("serif", Typeface.NORMAL) }
    private val ITALIC: Typeface by lazy { Typeface.create("serif", Typeface.ITALIC) }
    private val BOLD: Typeface by lazy { Typeface.create("serif", Typeface.BOLD) }
    private val BOLD_ITALIC: Typeface by lazy { Typeface.create("serif", Typeface.BOLD_ITALIC) }
    private val SANS: Typeface by lazy { Typeface.create("sans-serif", Typeface.NORMAL) }
    private val MONO: Typeface by lazy { Typeface.MONOSPACE }

    private fun rule(px: Float): Float = max(1.2f, px * 0.05f)

    /**
     * Lays out one formula. size is the display font size in pixels, ink the color, avail the width it should fit.
     * Returns one box per line (usually one). A box can still be wider than avail (one huge matrix): the caller scales it.
     */
    fun typeset(latex: String, size: Float, ink: Int, avail: Float): List<Box>? {
        try {
            val src = latex.trim()
            if (src.isEmpty()) return null
            val root = Parser(src).parseTop() ?: return null
            val e = Env(size, 0, ink, F_AUTO)
            val items = (root as? NSeq)?.items
            if (items == null || items.size < 3) return listOf(lay(root, e))
            val a = atoms(items, e)
            val whole = join(a, 0, items.size, e)
            if (whole.w <= avail) return listOf(whole)
            val lines = ArrayList<Box>()
            var start = 0
            while (start < items.size) {
                var best = -1
                var firstCandidate = -1
                for (k in start + 1..items.size) {
                    val candidate = k == items.size || a.cls[k] == Cls.REL || a.cls[k] == Cls.BIN
                    if (!candidate) continue
                    if (firstCandidate < 0) firstCandidate = k
                    if (join(a, start, k, e).w <= avail) best = k else break
                }
                if (best < 0) best = if (firstCandidate > 0) firstCandidate else items.size
                lines.add(join(a, start, best, e))
                start = best
            }
            return lines
        } catch (ex: Throwable) {
            Log.w("PostTex", "typeset failed: " + ex.message)
            return null
        }
    }

    // ---- style and fonts ----

    private class Env(val base: Float, val level: Int, val ink: Int, val font: Int) {
        fun px(): Float = base * LEVEL_SCALE[level]
        fun at(l: Int): Env = Env(base, l.coerceIn(0, 3), ink, font)
        fun sup(): Env = at(if (level <= 1) 2 else 3)
        fun num(): Env = at(min(3, level + 1))
        fun withInk(c: Int): Env = Env(base, level, c, font)
        fun withFont(f: Int): Env = Env(base, level, ink, f)
    }

    private enum class Cls { ORD, OP, BIN, REL, OPEN, CLOSE, PUNCT }

    // ---- boxes ----

    private class TextBox(val s: String, val tf: Typeface, val px: Float, val ink: Int, centerOn: Float? = null) : Box() {
        private var dy = 0f

        init {
            val p = Paint(Paint.ANTI_ALIAS_FLAG)
            p.typeface = tf
            p.textSize = px
            w = p.measureText(s)
            if (s.isNotBlank()) {
                val r = Rect()
                p.getTextBounds(s, 0, s.length, r)
                if (centerOn != null) dy = -centerOn - (r.top + r.bottom) / 2f
                asc = max(0f, -(r.top + dy))
                desc = max(0f, r.bottom + dy)
            }
        }

        override fun draw(c: Canvas, x: Float, y: Float) {
            if (s.isBlank()) return
            val p = Paint(Paint.ANTI_ALIAS_FLAG)
            p.typeface = tf
            p.textSize = px
            p.color = ink
            c.drawText(s, x, y + dy, p)
        }
    }

    private class HBox(val items: List<Box>) : Box() {
        init {
            for (b in items) {
                w += b.w
                asc = max(asc, b.asc)
                desc = max(desc, b.desc)
            }
        }

        override fun draw(c: Canvas, x: Float, y: Float) {
            var cx = x
            for (b in items) {
                b.draw(c, cx, y)
                cx += b.w
            }
        }
    }

    private class Glue(width: Float) : Box() {
        init { w = width }
        override fun draw(c: Canvas, x: Float, y: Float) {}
    }

    private class PhantomBox(val inner: Box) : Box() {
        init {
            w = inner.w
            asc = inner.asc
            desc = inner.desc
        }
        override fun draw(c: Canvas, x: Float, y: Float) {}
    }

    private class BoxedBox(val inner: Box, val px: Float, val ink: Int) : Box() {
        private val pad = px * 0.2f

        init {
            w = inner.w + pad * 2f
            asc = inner.asc + pad
            desc = inner.desc + pad
        }

        override fun draw(c: Canvas, x: Float, y: Float) {
            val p = Paint(Paint.ANTI_ALIAS_FLAG)
            p.style = Paint.Style.STROKE
            p.color = ink
            p.strokeWidth = rule(px)
            c.drawRect(x, y - asc, x + w, y + desc, p)
            inner.draw(c, x + pad, y)
        }
    }

    private class FracBox(val num: Box, val den: Box, px: Float, display: Boolean, bar: Boolean) : Box() {
        private val t = if (bar) rule(px) else 0f
        private val axis = px * AXIS
        private var up = 0f
        private var down = 0f

        init {
            val line = rule(px)
            val gap = if (bar) (if (display) 3f * line else line) else (if (display) 3.5f * line else 1.5f * line)
            up = max(px * (if (display) 0.68f else 0.40f), axis + t / 2f + gap + num.desc)
            down = max(px * (if (display) 0.69f else 0.35f), den.asc + gap + t / 2f - axis)
            w = max(num.w, den.w) + px * 0.2f
            asc = up + num.asc
            desc = down + den.desc
        }

        override fun draw(c: Canvas, x: Float, y: Float) {
            val cx = x + w / 2f
            num.draw(c, cx - num.w / 2f, y - up)
            den.draw(c, cx - den.w / 2f, y + down)
            if (t > 0f) {
                val p = Paint(Paint.ANTI_ALIAS_FLAG)
                p.color = numInk
                c.drawRect(x + w * 0.02f, y - axis - t / 2f, x + w * 0.98f, y - axis + t / 2f, p)
            }
        }

        var numInk: Int = Color.BLACK
    }

    private class ScriptBox(
        val base: Box, val sup: Box?, val sub: Box?, px: Float, spx: Float, display: Boolean,
        baseIsChar: Boolean, val dxSup: Float, val dxSub: Float
    ) : Box() {
        private var u = 0f
        private var v = 0f

        init {
            val t = rule(px)
            val xh = px * 0.43f
            if (sup != null) {
                var up = if (baseIsChar) 0f else max(0f, base.asc - spx * 0.39f)
                up = max(up, px * (if (display) 0.41f else 0.36f))
                up = max(up, sup.desc + xh * 0.25f)
                u = up
            }
            if (sub != null) {
                var dn = if (baseIsChar) 0f else base.desc + spx * 0.05f
                dn = max(dn, px * (if (sup != null) 0.25f else 0.15f))
                dn = max(dn, sub.asc - xh * 0.8f)
                if (sup != null) {
                    val gap = (u - sup.desc) - (sub.asc - dn)
                    if (gap < 4f * t) {
                        dn += 4f * t - gap
                        val psi = xh * 0.8f - (u - sup.desc)
                        if (psi > 0f) {
                            u += psi
                            dn -= psi
                        }
                    }
                }
                v = dn
            }
            val sw = max(if (sup != null) dxSup + sup.w else 0f, if (sub != null) dxSub + sub.w else 0f)
            w = base.w + sw + px * 0.04f
            asc = max(base.asc, if (sup != null) u + sup.asc else 0f)
            desc = max(base.desc, if (sub != null) v + sub.desc else 0f)
        }

        override fun draw(c: Canvas, x: Float, y: Float) {
            base.draw(c, x, y)
            sup?.draw(c, x + base.w + dxSup, y - u)
            sub?.draw(c, x + base.w + dxSub, y + v)
        }
    }

    private class LimitsBox(val base: Box, val over: Box?, val under: Box?, px: Float) : Box() {
        private val gap = px * 0.12f

        init {
            w = max(base.w, max(over?.w ?: 0f, under?.w ?: 0f))
            asc = base.asc + (if (over != null) gap + over.asc + over.desc else 0f)
            desc = base.desc + (if (under != null) gap + under.asc + under.desc else 0f)
        }

        override fun draw(c: Canvas, x: Float, y: Float) {
            base.draw(c, x + (w - base.w) / 2f, y)
            if (over != null) over.draw(c, x + (w - over.w) / 2f, y - base.asc - gap - over.desc)
            if (under != null) under.draw(c, x + (w - under.w) / 2f, y + base.desc + gap + under.asc)
        }
    }

    private class SqrtBox(val inner: Box, val index: Box?, val px: Float, display: Boolean, val ink: Int) : Box() {
        private val t = rule(px)
        private var top = 0f
        private var bottom = 0f
        private var kw = 0f
        private var left = 0f
        private var idxDy = 0f

        init {
            val gap = t * 1.6f + px * (if (display) 0.07f else 0.03f)
            top = max(inner.asc, px * 0.45f) + gap + t
            bottom = inner.desc + t
            val total = top + bottom
            kw = min(px * 0.95f, px * 0.55f + max(0f, total - px) * 0.12f)
            if (index != null) {
                left = max(0f, index.w - kw * 0.45f)
                idxDy = bottom - 0.6f * total
            }
            w = left + kw + inner.w + px * 0.08f
            asc = max(top, if (index != null) index.asc - idxDy else 0f)
            desc = bottom
        }

        override fun draw(c: Canvas, x: Float, y: Float) {
            val x0 = x + left
            val total = top + bottom
            val yTop = y - top + t / 2f
            val yBot = y + bottom
            val yA = yBot - 0.4f * total
            val rise = min(total * 0.1f, px * 0.12f)
            val p = Paint(Paint.ANTI_ALIAS_FLAG)
            p.style = Paint.Style.STROKE
            p.color = ink
            p.strokeWidth = t
            p.strokeJoin = Paint.Join.MITER
            val path = Path()
            path.moveTo(x0 + kw * 0.04f, yA)
            path.lineTo(x0 + kw * 0.15f, yA - rise)
            path.lineTo(x0 + kw * 0.42f, yBot)
            path.lineTo(x0 + kw, yTop)
            path.lineTo(x0 + kw + inner.w + px * 0.06f, yTop)
            c.drawPath(path, p)
            p.strokeWidth = t * 2.1f
            c.drawLine(x0 + kw * 0.15f, yA - rise, x0 + kw * 0.42f, yBot, p)
            inner.draw(c, x0 + kw, y)
            if (index != null) index.draw(c, x0 + kw * 0.45f - index.w, y + idxDy)
        }
    }

    private class AccentBox(val base: Box, val kind: String, val px: Float, val ink: Int) : Box() {
        private val t = rule(px)
        private val under = kind == "underline" || kind == "underbrace"
        private val cancel = kind == "cancel"
        private val ba = max(base.asc, px * 0.43f)
        private val gp = when (kind) {
            "overline", "underline" -> t * 3f
            "overbrace", "underbrace" -> px * 0.1f
            else -> px * 0.08f
        }
        private val ah = when (kind) {
            "hat", "widehat", "check", "vec", "overrightarrow", "overleftarrow", "acute", "grave" -> px * 0.2f
            "tilde", "widetilde" -> px * 0.16f
            "dot", "ddot" -> px * 0.1f
            "breve" -> px * 0.14f
            "overbrace", "underbrace" -> px * 0.28f
            else -> t
        }

        init {
            w = base.w
            if (cancel) {
                asc = base.asc
                desc = base.desc
            } else if (under) {
                asc = base.asc
                desc = base.desc + gp + ah + t
            } else {
                asc = ba + gp + ah + t
                desc = base.desc
            }
        }

        override fun draw(c: Canvas, x: Float, y: Float) {
            base.draw(c, x, y)
            val p = Paint(Paint.ANTI_ALIAS_FLAG)
            p.style = Paint.Style.STROKE
            p.color = ink
            p.strokeWidth = t
            p.strokeCap = Paint.Cap.ROUND
            p.strokeJoin = Paint.Join.ROUND
            val cx = x + w / 2f
            val yb = y - ba - gp
            val wide = kind.startsWith("wide") || kind == "overrightarrow" || kind == "overleftarrow"
            val aw = if (wide) max(w, px * 0.4f) else min(px * 0.42f, max(px * 0.28f, w * 0.8f))
            val l = cx - aw / 2f
            val r = cx + aw / 2f
            when (kind) {
                "hat", "widehat" -> {
                    val q = Path()
                    q.moveTo(l, yb)
                    q.lineTo(cx, yb - ah)
                    q.lineTo(r, yb)
                    c.drawPath(q, p)
                }
                "check" -> {
                    val q = Path()
                    q.moveTo(l, yb - ah)
                    q.lineTo(cx, yb)
                    q.lineTo(r, yb - ah)
                    c.drawPath(q, p)
                }
                "tilde", "widetilde" -> {
                    val ym = yb - ah / 2f
                    val q = Path()
                    q.moveTo(l, ym)
                    q.cubicTo(l + aw * 0.15f, yb - ah * 1.25f, l + aw * 0.35f, yb - ah * 1.25f, cx, ym)
                    q.cubicTo(cx + aw * 0.15f, yb + ah * 0.25f, r - aw * 0.15f, yb + ah * 0.25f, r, ym)
                    c.drawPath(q, p)
                }
                "bar" -> {
                    val bw = max(px * 0.3f, w * 0.85f)
                    c.drawLine(cx - bw / 2f, yb - t / 2f, cx + bw / 2f, yb - t / 2f, p)
                }
                "overline" -> c.drawLine(x, yb - t / 2f, x + w, yb - t / 2f, p)
                "vec", "overrightarrow", "overleftarrow" -> {
                    val yv = yb - ah / 2f
                    val dir = if (kind == "overleftarrow") -1f else 1f
                    val tipX = if (dir > 0f) r else l
                    val tailX = if (dir > 0f) l else r
                    c.drawLine(tailX, yv, tipX, yv, p)
                    val hd = ah * 0.6f
                    val q = Path()
                    q.moveTo(tipX - dir * hd, yv - hd)
                    q.lineTo(tipX, yv)
                    q.lineTo(tipX - dir * hd, yv + hd)
                    c.drawPath(q, p)
                }
                "dot", "ddot" -> {
                    val f = Paint(Paint.ANTI_ALIAS_FLAG)
                    f.color = ink
                    val rad = px * 0.045f
                    if (kind == "dot") {
                        c.drawCircle(cx, yb - px * 0.05f, rad, f)
                    } else {
                        c.drawCircle(cx - px * 0.09f, yb - px * 0.05f, rad, f)
                        c.drawCircle(cx + px * 0.09f, yb - px * 0.05f, rad, f)
                    }
                }
                "breve" -> {
                    val q = Path()
                    q.moveTo(l, yb - ah)
                    q.quadTo(cx, yb + ah * 0.5f, r, yb - ah)
                    c.drawPath(q, p)
                }
                "acute" -> c.drawLine(cx - aw * 0.15f, yb, cx + aw * 0.2f, yb - ah, p)
                "grave" -> c.drawLine(cx + aw * 0.15f, yb, cx - aw * 0.2f, yb - ah, p)
                "underline" -> {
                    val yy = y + base.desc + gp + t / 2f
                    c.drawLine(x, yy, x + w, yy, p)
                }
                "overbrace" -> c.drawPath(bracePath(x, x + w, yb - ah, yb, true), p)
                "underbrace" -> {
                    val yt = y + base.desc + gp
                    c.drawPath(bracePath(x, x + w, yt + ah, yt, true), p)
                }
                "cancel" -> c.drawLine(x, y + base.desc, x + w, y - base.asc, p)
                else -> {}
            }
        }
    }

    private class DelimBox(val lft: String, val rgt: String, val inner: Box, val px: Float, val ink: Int) : Box() {
        private val axis = px * AXIS
        private val gap = px * 0.06f
        private val sw = max(1.1f, px * 0.055f)
        private var dh = 0f
        private var lw = 0f
        private var rw = 0f

        init {
            val half = max(inner.asc - axis, inner.desc + axis)
            dh = max(px * 1.1f, max(2f * half * 0.96f, 2f * half - px * 0.3f))
            lw = widthOf(lft)
            rw = widthOf(rgt)
            w = lw + gap + inner.w + gap + rw
            asc = max(inner.asc, axis + dh / 2f)
            desc = max(inner.desc, dh / 2f - axis)
        }

        private fun widthOf(ch: String): Float = when (ch) {
            "" -> 0f
            "|" -> px * 0.18f
            "‖" -> px * 0.3f
            else -> px * 0.28f + min(px * 0.22f, dh * 0.035f)
        }

        override fun draw(c: Canvas, x: Float, y: Float) {
            val top = y - axis - dh / 2f
            val bot = y - axis + dh / 2f
            drawDelim(c, lft, x, top, bot, lw, false, ink, sw)
            inner.draw(c, x + lw + gap, y)
            drawDelim(c, rgt, x + lw + gap + inner.w + gap, top, bot, rw, true, ink, sw)
        }
    }

    private class GridBox(
        val cells: List<List<Box>>, val aligns: List<Char>, val gaps: FloatArray, val px: Float
    ) : Box() {
        private val cols = cells.maxOfOrNull { it.size } ?: 0
        private val colW = FloatArray(cols)
        private val rowAsc = FloatArray(cells.size)
        private val rowDesc = FloatArray(cells.size)
        private val rowGap = px * 0.15f

        init {
            for ((r, row) in cells.withIndex()) {
                rowAsc[r] = px * 0.7f
                rowDesc[r] = px * 0.3f
                for ((k, b) in row.withIndex()) {
                    colW[k] = max(colW[k], b.w)
                    rowAsc[r] = max(rowAsc[r], b.asc)
                    rowDesc[r] = max(rowDesc[r], b.desc)
                }
            }
            var total = rowGap * max(0, cells.size - 1)
            for (r in cells.indices) total += rowAsc[r] + rowDesc[r]
            var width = 0f
            for (k in 0 until cols) width += colW[k] + (if (k < cols - 1) gapAfter(k) else 0f)
            w = width
            asc = total / 2f + px * AXIS
            desc = total / 2f - px * AXIS
        }

        private fun gapAfter(k: Int): Float = if (k < gaps.size) gaps[k] else px

        override fun draw(c: Canvas, x: Float, y: Float) {
            var yy = y - asc
            for ((r, row) in cells.withIndex()) {
                val base = yy + rowAsc[r]
                var xx = x
                for (k in 0 until cols) {
                    val b = row.getOrNull(k)
                    if (b != null) {
                        val a = if (k < aligns.size) aligns[k] else 'c'
                        val dx = when (a) {
                            'r' -> colW[k] - b.w
                            'l' -> 0f
                            else -> (colW[k] - b.w) / 2f
                        }
                        b.draw(c, xx + dx, base)
                    }
                    xx += colW[k] + gapAfter(k)
                }
                yy += rowAsc[r] + rowDesc[r] + rowGap
            }
        }
    }

    // ---- curly braces and tall brackets drawn as paths ----

    /**
     * A curly brace between a0 and a1 along its long axis. tip is the cross-axis coordinate of its point, edge that of its two ends.
     * horiz true makes a horizontal brace (for overbrace and underbrace), false a vertical one.
     */
    private fun bracePath(a0: Float, a1: Float, tip: Float, edge: Float, horiz: Boolean): Path {
        val p = Path()
        val mid = (a0 + a1) / 2f
        val stem = (tip + edge) / 2f
        val r = min(abs(edge - tip) / 2f, (a1 - a0) / 4f)
        fun mv(a: Float, b: Float) { if (horiz) p.moveTo(a, b) else p.moveTo(b, a) }
        fun ln(a: Float, b: Float) { if (horiz) p.lineTo(a, b) else p.lineTo(b, a) }
        fun qd(ac: Float, bc: Float, a: Float, b: Float) {
            if (horiz) p.quadTo(ac, bc, a, b) else p.quadTo(bc, ac, b, a)
        }
        mv(a0, edge)
        qd(a0, stem, a0 + r, stem)
        ln(mid - r, stem)
        qd(mid, stem, mid, tip)
        qd(mid, stem, mid + r, stem)
        ln(a1 - r, stem)
        qd(a1, stem, a1, edge)
        return p
    }

    /** One bracket of any height, drawn as a path so it stretches without a font. right true for a closing bracket. */
    private fun drawDelim(c: Canvas, ch: String, x: Float, top: Float, bot: Float, wd: Float, right: Boolean, ink: Int, sw: Float) {
        if (ch.isEmpty()) return
        val p = Paint(Paint.ANTI_ALIAS_FLAG)
        p.style = Paint.Style.STROKE
        p.color = ink
        p.strokeWidth = sw
        p.strokeJoin = Paint.Join.ROUND
        val mid = (top + bot) / 2f
        val xs = if (right) x else x + wd
        val xt = if (right) x + wd else x
        val q = Path()
        when (ch) {
            "(", ")" -> {
                q.moveTo(xs, top)
                q.cubicTo(xt, top + (bot - top) * 0.22f, xt, bot - (bot - top) * 0.22f, xs, bot)
            }
            "[", "]" -> {
                q.moveTo(xs, top)
                q.lineTo(xt, top)
                q.lineTo(xt, bot)
                q.lineTo(xs, bot)
            }
            "{", "}" -> q.addPath(bracePath(top, bot, xt, xs, false))
            "|" -> {
                q.moveTo(x + wd / 2f, top)
                q.lineTo(x + wd / 2f, bot)
            }
            "‖" -> {
                q.moveTo(x + wd * 0.25f, top)
                q.lineTo(x + wd * 0.25f, bot)
                q.moveTo(x + wd * 0.75f, top)
                q.lineTo(x + wd * 0.75f, bot)
            }
            "⟨", "⟩" -> {
                q.moveTo(xs, top)
                q.lineTo(xt, mid)
                q.lineTo(xs, bot)
            }
            "⌊", "⌋" -> {
                q.moveTo(xs, bot)
                q.lineTo(xt, bot)
                q.lineTo(xt, top)
            }
            "⌈", "⌉" -> {
                q.moveTo(xs, top)
                q.lineTo(xt, top)
                q.lineTo(xt, bot)
            }
            else -> return
        }
        c.drawPath(q, p)
    }

    // ---- syntax tree ----

    private sealed class N
    private class NSym(val s: String, val cls: Cls, val kind: Int) : N()
    private class NSeq(val items: List<N>) : N()
    private class NFrac(val a: N, val b: N, val bar: Boolean, val force: Int) : N()
    private class NSqrt(val body: N, val index: N?) : N()
    private class NScripts(val base: N, val sub: N?, val sup: N?, val limits: Int) : N()
    private class NOp(val s: String, val kind: Int, val limitsDefault: Boolean) : N()
    private class NLeft(val opening: String, val body: N, val closing: String) : N()
    private class NAccent(val kind: String, val body: N) : N()
    private class NGrid(val rows: List<List<N>>, val env: String, val aligns: String) : N()
    private class NText(val s: String, val kind: Int) : N()
    private class NFont(val kind: Int, val body: N) : N()
    private class NSpace(val em: Float) : N()
    private class NColor(val color: Int, val body: N) : N()
    private class NStyle(val level: Int, val body: N) : N()
    private class NBox(val body: N) : N()
    private class NPhantom(val body: N) : N()

    private fun unwrap(n: N): N {
        var x = n
        while (x is NSeq && x.items.size == 1) x = x.items[0]
        return x
    }

    private fun isBlankN(n: N): Boolean = n is NSeq && n.items.isEmpty()

    // ---- symbol tables ----

    private val GREEK = mapOf(
        "alpha" to "α", "beta" to "β", "gamma" to "γ", "delta" to "δ", "epsilon" to "ε", "varepsilon" to "ε",
        "zeta" to "ζ", "eta" to "η", "theta" to "θ", "vartheta" to "ϑ", "iota" to "ι", "kappa" to "κ",
        "lambda" to "λ", "mu" to "μ", "nu" to "ν", "xi" to "ξ", "omicron" to "ο", "pi" to "π", "varpi" to "ϖ",
        "rho" to "ρ", "varrho" to "ϱ", "sigma" to "σ", "varsigma" to "ς", "tau" to "τ", "upsilon" to "υ",
        "phi" to "φ", "varphi" to "ϕ", "chi" to "χ", "psi" to "ψ", "omega" to "ω",
        "Gamma" to "Γ", "Delta" to "Δ", "Theta" to "Θ", "Lambda" to "Λ", "Xi" to "Ξ", "Pi" to "Π",
        "Sigma" to "Σ", "Upsilon" to "Υ", "Phi" to "Φ", "Psi" to "Ψ", "Omega" to "Ω"
    )

    private val BIN_SYMS = mapOf(
        "times" to "×", "cdot" to "·", "pm" to "±", "mp" to "∓", "div" to "÷", "ast" to "∗", "star" to "⋆",
        "circ" to "∘", "bullet" to "•", "oplus" to "⊕", "otimes" to "⊗", "ominus" to "⊖", "cup" to "∪",
        "cap" to "∩", "wedge" to "∧", "land" to "∧", "vee" to "∨", "lor" to "∨", "setminus" to "∖"
    )

    private val REL_SYMS = mapOf(
        "leq" to "≤", "le" to "≤", "geq" to "≥", "ge" to "≥", "neq" to "≠", "ne" to "≠", "approx" to "≈",
        "equiv" to "≡", "sim" to "∼", "simeq" to "≃", "cong" to "≅", "propto" to "∝", "ll" to "≪", "gg" to "≫",
        "in" to "∈", "notin" to "∉", "ni" to "∋", "subset" to "⊂", "subseteq" to "⊆", "supset" to "⊃",
        "supseteq" to "⊇", "to" to "→", "rightarrow" to "→", "leftarrow" to "←", "gets" to "←",
        "Rightarrow" to "⇒", "Leftarrow" to "⇐", "leftrightarrow" to "↔", "Leftrightarrow" to "⇔",
        "mapsto" to "↦", "longrightarrow" to "⟶", "implies" to "⟹", "iff" to "⟺", "impliedby" to "⟸",
        "uparrow" to "↑", "downarrow" to "↓", "perp" to "⊥", "parallel" to "∥", "mid" to "∣", "vdash" to "⊢",
        "models" to "⊨"
    )

    private val ORD_SYMS = mapOf(
        "infty" to "∞", "partial" to "∂", "nabla" to "∇", "emptyset" to "∅", "varnothing" to "∅",
        "forall" to "∀", "exists" to "∃", "neg" to "¬", "lnot" to "¬", "angle" to "∠", "degree" to "°",
        "prime" to "′", "ldots" to "…", "cdots" to "⋯", "dots" to "…", "vdots" to "⋮", "ddots" to "⋱",
        "therefore" to "∴", "because" to "∵", "hbar" to "ℏ", "ell" to "ℓ", "aleph" to "ℵ", "Re" to "ℜ",
        "Im" to "ℑ", "wp" to "℘", "triangle" to "△", "square" to "□", "checkmark" to "✓", "surd" to "√",
        "top" to "⊤", "bot" to "⊥", "imath" to "ı", "jmath" to "ȷ", "vert" to "|", "Vert" to "‖"
    )

    private val BIG_OPS = mapOf(
        "sum" to "∑", "prod" to "∏", "coprod" to "∐", "bigcup" to "⋃", "bigcap" to "⋂",
        "bigoplus" to "⨁", "bigotimes" to "⨂", "bigvee" to "⋁", "bigwedge" to "⋀"
    )

    private val INTEGRALS = mapOf("int" to "∫", "iint" to "∬", "iiint" to "∭", "oint" to "∮")

    private val NAMED_OPS = setOf(
        "sin", "cos", "tan", "cot", "sec", "csc", "sinh", "cosh", "tanh", "coth", "arcsin", "arccos", "arctan",
        "log", "ln", "lg", "exp", "lim", "limsup", "liminf", "max", "min", "sup", "inf", "det", "dim", "ker",
        "gcd", "deg", "arg", "Pr", "hom"
    )

    private val LIMIT_OPS = setOf("lim", "limsup", "liminf", "max", "min", "sup", "inf", "det", "gcd", "Pr")

    private val ACCENTS = setOf(
        "hat", "widehat", "bar", "overline", "underline", "vec", "overrightarrow", "overleftarrow", "dot", "ddot",
        "tilde", "widetilde", "check", "breve", "acute", "grave", "overbrace", "underbrace", "cancel", "bcancel", "xcancel"
    )

    private fun colorOf(name: String, fallback: Int): Int {
        val k = name.trim()
        val named: Int? = when (k.lowercase()) {
            "red" -> Color.rgb(209, 26, 38)
            "blue" -> Color.rgb(23, 107, 206)
            "green" -> Color.rgb(22, 128, 60)
            "black" -> Color.BLACK
            "white" -> Color.WHITE
            "gray", "grey" -> Color.GRAY
            "orange" -> Color.rgb(230, 126, 34)
            "purple", "violet" -> Color.rgb(124, 58, 237)
            "teal" -> Color.rgb(0, 128, 128)
            "magenta" -> Color.MAGENTA
            "cyan" -> Color.CYAN
            "brown" -> Color.rgb(139, 69, 19)
            "navy" -> Color.rgb(18, 42, 105)
            else -> null
        }
        if (named != null) return named
        return runCatching { Color.parseColor(if (k.startsWith("#")) k else "#" + k) }.getOrDefault(fallback)
    }

    private fun bb(ch: Char): String = when (ch) {
        'C' -> "ℂ"
        'H' -> "ℍ"
        'N' -> "ℕ"
        'P' -> "ℙ"
        'Q' -> "ℚ"
        'R' -> "ℝ"
        'Z' -> "ℤ"
        in 'A'..'Z' -> String(Character.toChars(0x1D538 + (ch - 'A')))
        in 'a'..'z' -> String(Character.toChars(0x1D552 + (ch - 'a')))
        in '0'..'9' -> String(Character.toChars(0x1D7D8 + (ch - '0')))
        else -> ch.toString()
    }

    private fun charNode(c: Char): N = when {
        c.isLetter() -> NSym(c.toString(), Cls.ORD, 0)
        c.isDigit() -> NSym(c.toString(), Cls.ORD, 1)
        c == '+' -> NSym("+", Cls.BIN, 1)
        c == '-' -> NSym("\u2212", Cls.BIN, 1)
        c == '*' -> NSym("\u2217", Cls.BIN, 1)
        c == '=' || c == '<' || c == '>' || c == ':' -> NSym(c.toString(), Cls.REL, 1)
        c == ',' || c == ';' -> NSym(c.toString(), Cls.PUNCT, 1)
        c == '(' || c == '[' -> NSym(c.toString(), Cls.OPEN, 1)
        c == ')' || c == ']' -> NSym(c.toString(), Cls.CLOSE, 1)
        c == '\'' -> NSym("\u2032", Cls.ORD, 1)
        c == '~' -> NSpace(0.33f)
        "×·±∓÷∘∧∨∪∩⊕⊗⋅".indexOf(c) >= 0 -> NSym(c.toString(), Cls.BIN, 1)
        "≤≥≠≈≡∼∝∈∉⊂⊆⊃⊇→←⇒⇔↔≪≫∣∥⊥≅≃⟶".indexOf(c) >= 0 -> NSym(c.toString(), Cls.REL, 1)
        else -> NSym(c.toString(), Cls.ORD, 1)
    }

    // ---- parser ----

    private class Parser(val s: String) {
        var i = 0

        fun skipWs() {
            while (i < s.length && s[i].isWhitespace()) i++
        }

        /** The name of the letter command at the current position (after spaces), or null. Does not consume it. */
        fun peekName(): String? {
            skipWs()
            if (i >= s.length || s[i] != '\\') return null
            var j = i + 1
            while (j < s.length && s[j].isLetter()) j++
            return if (j == i + 1) null else s.substring(i + 1, j)
        }

        private fun balanced(): Boolean {
            var depth = 0
            var k = 0
            while (k < s.length) {
                val c = s[k]
                if (c == '\\') {
                    k += 2
                    continue
                }
                if (c == '{') depth++
                if (c == '}') {
                    depth--
                    if (depth < 0) return false
                }
                k++
            }
            return depth == 0
        }

        /** The whole formula. Several rows (a double backslash) or columns (an ampersand) at the top become an aligned block. */
        fun parseTop(): N? {
            if (!balanced()) return null
            val rows = parseRows()
            if (i < s.length) return null
            if (rows.size == 1 && rows[0].size == 1) return rows[0][0]
            return NGrid(rows, if (rows.any { it.size > 1 }) "aligned" else "gathered", "")
        }

        fun parseRows(): List<List<N>> {
            val rows = ArrayList<List<N>>()
            var row = ArrayList<N>()
            while (true) {
                row.add(parseSeq())
                skipWs()
                if (i >= s.length) break
                if (s[i] == '&') {
                    i++
                    continue
                }
                if (s.startsWith("\\\\", i)) {
                    i += 2
                    skipWs()
                    if (i < s.length && s[i] == '[') {
                        val close = s.indexOf(']', i)
                        if (close > 0) i = close + 1
                    }
                    rows.add(row)
                    row = ArrayList()
                    continue
                }
                break
            }
            rows.add(row)
            while (rows.size > 1 && rows[rows.size - 1].all { isBlankN(it) }) rows.removeAt(rows.size - 1)
            return rows
        }

        fun parseSeq(): N {
            val items = ArrayList<N>()
            while (true) {
                skipWs()
                if (i >= s.length) break
                val c = s[i]
                if (c == '}' || c == '&') break
                if (c == '\\') {
                    if (s.startsWith("\\\\", i)) break
                    val nm = peekName() ?: ""
                    if (nm == "right" || nm == "end") break
                    if (nm == "over" || nm == "choose" || nm == "atop") {
                        i += 1 + nm.length
                        val den = parseSeq()
                        val num: N = if (items.size == 1) items[0] else NSeq(ArrayList(items))
                        val frac = NFrac(num, den, nm == "over", -1)
                        return if (nm == "choose") NLeft("(", NFrac(num, den, false, -1), ")") else frac
                    }
                }
                items.add(parseAtom())
            }
            return if (items.size == 1) items[0] else NSeq(items)
        }

        private fun parseAtom(): N {
            skipWs()
            val c = s[i]
            val base: N = if (c == '^' || c == '_') NSym("", Cls.ORD, 1) else parsePrimary()
            return withScripts(base)
        }

        private fun joinN(a: N?, b: N): N = if (a == null) b else NSeq(listOf<N>(a, b))

        private fun withScripts(base: N): N {
            var sub: N? = null
            var sup: N? = null
            var limits = 0
            while (true) {
                skipWs()
                if (i >= s.length) break
                val c = s[i]
                if (c == '^') {
                    i++
                    sup = joinN(sup, scriptArg())
                } else if (c == '_') {
                    i++
                    sub = joinN(sub, scriptArg())
                } else if (c == '\'') {
                    i++
                    sup = joinN(sup, NSym("\u2032", Cls.ORD, 1))
                } else if (c == '\\') {
                    val nm = peekName() ?: ""
                    if (nm == "limits") {
                        i += 7
                        limits = 1
                    } else if (nm == "nolimits") {
                        i += 9
                        limits = 2
                    } else {
                        break
                    }
                } else {
                    break
                }
            }
            return if (sub == null && sup == null) base else NScripts(base, sub, sup, limits)
        }

        private fun scriptArg(): N {
            skipWs()
            if (i >= s.length || s[i] == '}' || s[i] == '&') return NSeq(emptyList())
            return parsePrimary()
        }

        /** One argument of a command: a {group} or a single token (so a fraction of two digits works without braces). */
        fun arg(): N {
            skipWs()
            if (i >= s.length || s[i] == '}' || s[i] == '&') return NSeq(emptyList())
            return parsePrimary()
        }

        /** The raw text of an argument (for text, color names and environment names). */
        fun rawArg(): String {
            skipWs()
            if (i >= s.length) return ""
            if (s[i] != '{') {
                val ch = s[i]
                i++
                return ch.toString()
            }
            var depth = 0
            var j = i
            while (j < s.length) {
                if (s[j] == '\\') {
                    j += 2
                    continue
                }
                if (s[j] == '{') depth++
                if (s[j] == '}') {
                    depth--
                    if (depth == 0) break
                }
                j++
            }
            val end = min(j, s.length)
            val out = s.substring(i + 1, end)
            i = min(end + 1, s.length)
            return out
        }

        private fun parsePrimary(): N {
            skipWs()
            val c = s[i]
            if (c == '{') {
                i++
                val g = parseSeq()
                skipWs()
                if (i < s.length && s[i] == '}') i++
                return NSeq(listOf<N>(g))
            }
            if (c == '\\') return parseCommand()
            i++
            return charNode(c)
        }

        private fun readDelim(): String {
            skipWs()
            if (i >= s.length) return ""
            val c = s[i]
            if (c == '\\') {
                i++
                if (i >= s.length) return ""
                var j = i
                if (s[i].isLetter()) {
                    while (j < s.length && s[j].isLetter()) j++
                } else {
                    j = i + 1
                }
                val nm = s.substring(i, j)
                i = j
                return when (nm) {
                    "langle" -> "⟨"
                    "rangle" -> "⟩"
                    "{", "lbrace" -> "{"
                    "}", "rbrace" -> "}"
                    "|", "Vert" -> "‖"
                    "vert" -> "|"
                    "lfloor" -> "⌊"
                    "rfloor" -> "⌋"
                    "lceil" -> "⌈"
                    "rceil" -> "⌉"
                    "lbrack" -> "["
                    "rbrack" -> "]"
                    else -> ""
                }
            }
            i++
            return when (c) {
                '(', ')', '[', ']', '|' -> c.toString()
                '<' -> "⟨"
                '>' -> "⟩"
                else -> ""
            }
        }

        private fun parseEnv(): N {
            val name = rawArg().trim()
            var spec = ""
            if (name == "array" || name == "tabular" || name == "alignat" || name == "alignat*") {
                skipWs()
                if (i < s.length && s[i] == '{') spec = rawArg()
            }
            val rows = parseRows()
            if (peekName() == "end") {
                i += 4
                rawArg()
            }
            return when (name) {
                "pmatrix" -> NLeft("(", NGrid(rows, "matrix", ""), ")")
                "bmatrix" -> NLeft("[", NGrid(rows, "matrix", ""), "]")
                "Bmatrix" -> NLeft("{", NGrid(rows, "matrix", ""), "}")
                "vmatrix" -> NLeft("|", NGrid(rows, "matrix", ""), "|")
                "Vmatrix" -> NLeft("‖", NGrid(rows, "matrix", ""), "‖")
                "cases" -> NLeft("{", NGrid(rows, "cases", ""), "")
                "aligned", "align", "align*", "alignat", "alignat*", "split", "eqnarray", "flalign" -> NGrid(rows, "aligned", "")
                "gather", "gathered", "gather*", "equation", "equation*", "displaymath", "multline" -> NGrid(rows, "gathered", "")
                "array", "tabular" -> NGrid(rows, "array", spec)
                else -> NGrid(rows, "matrix", "")
            }
        }

        private fun parseCommand(): N {
            i++
            if (i >= s.length) return NSym("\\", Cls.ORD, 1)
            var j = i
            if (s[i].isLetter()) {
                while (j < s.length && s[j].isLetter()) j++
            } else {
                j = i + 1
            }
            val name = s.substring(i, j)
            i = j
            if (name in ACCENTS) return NAccent(name, arg())
            if (name in NAMED_OPS) return NOp(name, 0, name in LIMIT_OPS)
            GREEK[name]?.let { return NSym(it, Cls.ORD, if (name[0].isUpperCase()) 1 else 0) }
            BIN_SYMS[name]?.let { return NSym(it, Cls.BIN, 1) }
            REL_SYMS[name]?.let { return NSym(it, Cls.REL, 1) }
            ORD_SYMS[name]?.let { return NSym(it, Cls.ORD, 1) }
            BIG_OPS[name]?.let { return NOp(it, 1, true) }
            INTEGRALS[name]?.let { return NOp(it, 2, false) }
            return when (name) {
                "frac", "dfrac", "tfrac", "cfrac" -> {
                    val a = arg()
                    val b = arg()
                    NFrac(a, b, true, if (name == "dfrac" || name == "cfrac") 0 else if (name == "tfrac") 1 else -1)
                }
                "binom", "dbinom", "tbinom" -> {
                    val a = arg()
                    val b = arg()
                    NLeft("(", NFrac(a, b, false, if (name == "dbinom") 0 else if (name == "tbinom") 1 else -1), ")")
                }
                "sqrt" -> {
                    skipWs()
                    var index: N? = null
                    if (i < s.length && s[i] == '[') {
                        val close = s.indexOf(']', i)
                        if (close > i) {
                            index = Parser(s.substring(i + 1, close)).parseSeq()
                            i = close + 1
                        }
                    }
                    NSqrt(arg(), index)
                }
                "left" -> {
                    val opening = readDelim()
                    val body = parseSeq()
                    var closing = ""
                    if (peekName() == "right") {
                        i += 6
                        closing = readDelim()
                    }
                    NLeft(opening, body, closing)
                }
                "middle" -> {
                    readDelim()
                    NSym("|", Cls.ORD, 1)
                }
                "right" -> {
                    readDelim()
                    NSym("", Cls.ORD, 1)
                }
                "begin" -> parseEnv()
                "end" -> NSym("", Cls.ORD, 1)
                "text", "textrm", "mbox", "textnormal", "hbox" -> NText(rawArg(), 0)
                "textit", "emph" -> NText(rawArg(), 1)
                "textbf" -> NText(rawArg(), 2)
                "mathrm", "rm" -> NFont(F_ROMAN, arg())
                "mathbf", "mathbfit" -> NFont(F_BOLD, arg())
                "mathit" -> NFont(F_ITALIC, arg())
                "mathsf" -> NFont(F_SANS, arg())
                "mathtt" -> NFont(F_MONO, arg())
                "mathbb" -> NFont(F_BB, arg())
                "boldsymbol", "bm" -> NFont(F_BIT, arg())
                "mathcal", "mathscr", "mathfrak" -> NFont(F_ITALIC, arg())
                "operatorname", "mathop" -> NOp(rawArg(), 0, false)
                "overset", "stackrel" -> {
                    val a = arg()
                    val b = arg()
                    NScripts(b, null, a, 1)
                }
                "underset" -> {
                    val a = arg()
                    val b = arg()
                    NScripts(b, a, null, 1)
                }
                "substack" -> {
                    skipWs()
                    if (i < s.length && s[i] == '{') i++
                    val rows = parseRows()
                    skipWs()
                    if (i < s.length && s[i] == '}') i++
                    NGrid(rows, "substack", "")
                }
                "color" -> {
                    val color = colorOf(rawArg(), Color.BLACK)
                    NColor(color, parseSeq())
                }
                "textcolor" -> {
                    val color = colorOf(rawArg(), Color.BLACK)
                    NColor(color, arg())
                }
                "boxed", "fbox" -> NBox(arg())
                "phantom", "hphantom", "vphantom" -> NPhantom(arg())
                "displaystyle" -> NStyle(0, parseSeq())
                "textstyle" -> NStyle(1, parseSeq())
                "scriptstyle" -> NStyle(2, parseSeq())
                "scriptscriptstyle" -> NStyle(3, parseSeq())
                "limits", "nolimits" -> NSym("", Cls.ORD, 1)
                "not" -> {
                    skipWs()
                    if (i < s.length && s[i] == '=') {
                        i++
                        NSym("≠", Cls.REL, 1)
                    } else if (peekName() == "in") {
                        i += 3
                        NSym("∉", Cls.REL, 1)
                    } else {
                        NSym("", Cls.ORD, 1)
                    }
                }
                "," , "thinspace" -> NSpace(1f / 6f)
                ":", ">", "medspace" -> NSpace(2f / 9f)
                ";", "thickspace" -> NSpace(5f / 18f)
                "!" -> NSpace(-1f / 6f)
                " " -> NSpace(1f / 3f)
                "quad" -> NSpace(1f)
                "qquad" -> NSpace(2f)
                "enspace" -> NSpace(0.5f)
                "hspace", "hspace*", "vspace", "vspace*", "tag", "label" -> {
                    rawArg()
                    NSpace(0f)
                }
                "{" -> NSym("{", Cls.OPEN, 1)
                "}" -> NSym("}", Cls.CLOSE, 1)
                "|" -> NSym("‖", Cls.ORD, 1)
                "%", "&", "#", "_", "\u0024" -> NSym(name, Cls.ORD, 1)
                "\\" -> NSym("", Cls.ORD, 1)
                else -> NText("\\" + name, 0)
            }
        }
    }

    // ---- layout ----

    private fun faceFor(n: NSym, ch: Char, e: Env): Typeface {
        val letter = n.s.length == 1 && ch.isLetter()
        val upperGreek = ch.code in 0x391..0x3A9
        return when (e.font) {
            F_ROMAN -> ROMAN
            F_BOLD -> BOLD
            F_ITALIC -> if (letter) ITALIC else ROMAN
            F_SANS -> SANS
            F_MONO -> MONO
            F_BIT -> if (letter) BOLD_ITALIC else BOLD
            else -> if (n.kind == 0 && letter && !upperGreek) ITALIC else ROMAN
        }
    }

    private fun laySym(n: NSym, e: Env): Box {
        var str = n.s
        val ch = if (str.length == 1) str[0] else ' '
        if (e.font == F_BB && ch.isLetter()) str = bb(ch)
        return TextBox(str, faceFor(n, ch, e), e.px(), e.ink)
    }

    private fun layOp(n: NOp, e: Env): Box {
        val px = e.px()
        return when (n.kind) {
            0 -> TextBox(n.s, ROMAN, px, e.ink)
            1 -> TextBox(n.s, ROMAN, px * (if (e.level == 0) 1.4f else 1.0f), e.ink, px * AXIS)
            else -> TextBox(n.s, ROMAN, px * (if (e.level == 0) 1.7f else 1.2f), e.ink, px * AXIS)
        }
    }

    private fun layScripts(n: NScripts, e: Env): Box {
        val bn = unwrap(n.base)
        val base = lay(n.base, e)
        val se = e.sup()
        val sup = n.sup?.let { lay(it, se) }
        val sub = n.sub?.let { lay(it, se) }
        val px = e.px()
        val limit = when {
            n.limits == 1 -> true
            n.limits == 2 -> false
            bn is NOp -> e.level == 0 && bn.limitsDefault
            bn is NAccent -> bn.kind == "overbrace" || bn.kind == "underbrace"
            else -> false
        }
        if (limit) return LimitsBox(base, sup, sub, px)
        val isChar = bn is NSym || (bn is NOp && bn.kind == 0)
        val integral = bn is NOp && bn.kind == 2
        return ScriptBox(base, sup, sub, px, se.px(), e.level == 0, isChar, if (integral) px * 0.25f else 0f, 0f)
    }

    private fun layGrid(n: NGrid, e: Env): Box {
        val px = e.px()
        val ce = when (n.env) {
            "matrix", "cases" -> if (e.level == 0) e.at(1) else e
            "substack" -> e.sup()
            else -> e
        }
        val aligned = n.env == "aligned"
        val cells = ArrayList<List<Box>>()
        for (row in n.rows) {
            val boxes = ArrayList<Box>()
            for ((c, cell) in row.withIndex()) {
                val node: N = if (aligned && c % 2 == 1) NSeq(listOf<N>(NSym("", Cls.ORD, 1), cell)) else cell
                boxes.add(lay(node, ce))
            }
            cells.add(boxes)
        }
        val cols = n.rows.maxOfOrNull { it.size } ?: 0
        val spec = n.aligns.filter { it == 'l' || it == 'c' || it == 'r' }
        val aligns = ArrayList<Char>()
        for (c in 0 until cols) {
            aligns.add(
                when (n.env) {
                    "cases" -> 'l'
                    "aligned" -> if (c % 2 == 0) 'r' else 'l'
                    "array" -> spec.getOrElse(c) { 'c' }
                    else -> 'c'
                }
            )
        }
        val gaps = FloatArray(max(1, cols)) { c ->
            if (n.env == "aligned") (if (c % 2 == 0) 0f else px * 2f) else px
        }
        return GridBox(cells, aligns, gaps, px)
    }

    private fun lay(n: N, e: Env): Box = when (n) {
        is NSym -> laySym(n, e)
        is NSeq -> {
            val a = atoms(n.items, e)
            join(a, 0, n.items.size, e)
        }
        is NFrac -> {
            val fe = if (n.force >= 0) e.at(n.force) else e
            val f = FracBox(lay(n.a, fe.num()), lay(n.b, fe.num()), fe.px(), fe.level == 0, n.bar)
            f.numInk = e.ink
            f
        }
        is NSqrt -> SqrtBox(lay(n.body, e), n.index?.let { lay(it, e.at(3)) }, e.px(), e.level == 0, e.ink)
        is NScripts -> layScripts(n, e)
        is NOp -> layOp(n, e)
        is NLeft -> DelimBox(n.opening, n.closing, lay(n.body, e), e.px(), e.ink)
        is NAccent -> AccentBox(lay(n.body, e), n.kind, e.px(), e.ink)
        is NGrid -> layGrid(n, e)
        is NText -> TextBox(n.s, when (n.kind) {
            1 -> ITALIC
            2 -> BOLD
            else -> ROMAN
        }, e.px(), e.ink)
        is NFont -> lay(n.body, e.withFont(n.kind))
        is NSpace -> Glue(n.em * e.px())
        is NColor -> lay(n.body, e.withInk(n.color))
        is NStyle -> lay(n.body, e.at(n.level))
        is NBox -> BoxedBox(lay(n.body, e), e.px(), e.ink)
        is NPhantom -> PhantomBox(lay(n.body, e))
    }

    // ---- spacing between atoms (the TeX table) ----

    private class Atoms(val boxes: List<Box>, val cls: List<Cls?>)

    private fun clsOf(n: N): Cls? = when (n) {
        is NSym -> n.cls
        is NOp -> Cls.OP
        is NScripts -> clsOf(unwrap(n.base)) ?: Cls.ORD
        is NSpace -> null
        else -> Cls.ORD
    }

    /** A binary operator at the start, after another operator, or before a relation or closing bracket is an ordinary symbol (so -x has no gap). */
    private fun normalize(cls: MutableList<Cls?>) {
        var prev: Cls? = null
        for (k in cls.indices) {
            val c = cls[k] ?: continue
            if (c == Cls.BIN && (prev == null || prev == Cls.BIN || prev == Cls.OP || prev == Cls.REL || prev == Cls.OPEN || prev == Cls.PUNCT)) {
                cls[k] = Cls.ORD
            }
            prev = cls[k]
        }
        var next: Cls? = null
        for (k in cls.indices.reversed()) {
            val c = cls[k] ?: continue
            if (c == Cls.BIN && (next == null || next == Cls.REL || next == Cls.CLOSE || next == Cls.PUNCT)) {
                cls[k] = Cls.ORD
            }
            next = cls[k]
        }
    }

    private fun gap(l: Cls, r: Cls): Float {
        val thin = 0.1667f
        val med = 0.2222f
        val thick = 0.2778f
        return when (l) {
            Cls.ORD -> when (r) {
                Cls.OP -> thin
                Cls.BIN -> med
                Cls.REL -> thick
                else -> 0f
            }
            Cls.OP -> when (r) {
                Cls.ORD, Cls.OP -> thin
                Cls.REL -> thick
                else -> 0f
            }
            Cls.BIN -> when (r) {
                Cls.ORD, Cls.OP, Cls.OPEN -> med
                else -> 0f
            }
            Cls.REL -> when (r) {
                Cls.ORD, Cls.OP, Cls.OPEN -> thick
                else -> 0f
            }
            Cls.OPEN -> 0f
            Cls.CLOSE -> when (r) {
                Cls.OP -> thin
                Cls.BIN -> med
                Cls.REL -> thick
                else -> 0f
            }
            Cls.PUNCT -> thin
        }
    }

    private fun atoms(items: List<N>, e: Env): Atoms {
        val boxes = ArrayList<Box>()
        val cls = ArrayList<Cls?>()
        for (n in items) {
            boxes.add(lay(n, e))
            cls.add(clsOf(n))
        }
        normalize(cls)
        return Atoms(boxes, cls)
    }

    /** Boxes from..to-1 side by side, with the gaps between neighbours (no gaps in script styles, none at the start of a line). */
    private fun join(a: Atoms, from: Int, to: Int, e: Env): Box {
        val parts = ArrayList<Box>()
        var prev: Cls? = null
        for (k in from until to) {
            val c = a.cls[k]
            if (c != null) {
                if (prev != null && e.level <= 1) {
                    val g = gap(prev, c)
                    if (g > 0f) parts.add(Glue(g * e.px()))
                }
                prev = c
            }
            parts.add(a.boxes[k])
        }
        return HBox(parts)
    }
}
