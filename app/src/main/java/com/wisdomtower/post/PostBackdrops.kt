package com.wisdomtower.post

import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
import kotlin.math.max
import kotlin.math.min

/**
 * One poster background "look" (docs/BACKDROPS.md). A look replaces the template's own backdrop on every template when the brand is dark-first,
 * so choosing another look changes the whole poster background, not just a tint.
 * from / mid / to: the base gradient (hex), drawn at angle (0 = left to right, 90 = top to bottom).
 * glow: 0 none, 1 top-right, 2 bottom-left, 3 spotlight near the top centre, 4 two opposite corners, 5 rising from the bottom edge.
 * glowA: color of the main glow (null = the brand's Main). glowB: color of the second glow (null = the brand's Second).
 * pattern: 0 none, 1 dot grid, 2 diagonal lines, 3 rings from the top-right corner, 4 fine grid, 5 arcs from the bottom-left corner.
 * Everything is dark enough for white text. The contrast guard in PostRenderer still checks every text box.
 */
class BackdropLook(
    val id: Int,
    val name: String,
    val from: String,
    val mid: String?,
    val to: String,
    val angle: Int,
    val glow: Int,
    val glowA: String?,
    val glowB: String?,
    val pattern: Int
)

object PostBackdrops {
    /** id 0 is "Auto" (no look: the phase 1 dark-first tints). The ids below are stored in the brand, so never renumber them. */
    val all: List<BackdropLook> = listOf(
        BackdropLook(1, "Signature Navy", "#10203A", null, "#050910", 160, 1, null, null, 0),
        BackdropLook(2, "Black Gold", "#221707", null, "#060402", 135, 2, null, null, 2),
        BackdropLook(3, "Deep Petrol", "#06404F", "#04222D", "#020B12", 120, 3, null, null, 3),
        BackdropLook(4, "Warm Amber", "#34230A", null, "#0A0703", 90, 3, null, null, 1),
        BackdropLook(5, "Cyan Grid", "#0F2C45", null, "#060A12", 45, 4, null, "#1E88C8", 4),
        BackdropLook(6, "Obsidian", "#1A1712", null, "#050505", 90, 5, null, null, 0),
        BackdropLook(7, "Midnight Indigo", "#1F3170", "#0E1740", "#050818", 150, 4, null, null, 1),
        BackdropLook(8, "Graphite", "#2D323C", null, "#0F1115", 135, 1, null, null, 4),
        BackdropLook(9, "Bronze", "#41290E", "#1D1309", "#080604", 135, 2, null, null, 5),
        BackdropLook(10, "Neon Night", "#2A1356", "#13152F", "#060814", 160, 4, null, "#FF5CD6", 2)
    )

    fun byId(id: Int): BackdropLook? = all.firstOrNull { it.id == id }

    /** Draws the glows and the pattern of a look over the base gradient. w and h are the full export size (the canvas is already scaled). */
    fun draw(canvas: Canvas, look: BackdropLook, w: Int, h: Int, accent: Int, secondary: Int) {
        val a = look.glowA?.let { parse(it, accent) } ?: accent
        val b = look.glowB?.let { parse(it, secondary) } ?: secondary
        when (look.glow) {
            1 -> blob(canvas, w, h, 1.00f, 0.00f, 0.85f, a, 95)
            2 -> blob(canvas, w, h, 0.00f, 1.00f, 0.85f, a, 95)
            3 -> blob(canvas, w, h, 0.50f, 0.30f, 0.75f, a, 85)
            4 -> {
                blob(canvas, w, h, 0.05f, 0.05f, 0.75f, a, 85)
                blob(canvas, w, h, 0.95f, 0.98f, 0.75f, b, 70)
            }
            5 -> {
                blob(canvas, w, h, 0.50f, 1.05f, 0.90f, a, 90)
                blob(canvas, w, h, 0.95f, 0.05f, 0.50f, b, 45)
            }
            else -> Unit
        }
        drawPattern(canvas, look.pattern, w, h, accent)
    }

    private fun blob(canvas: Canvas, w: Int, h: Int, cx: Float, cy: Float, r: Float, color: Int, alpha: Int) {
        val reach = max(w, h).toFloat()
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.shader = RadialGradient(
            cx * w, cy * h, r * reach,
            intArrayOf(withAlpha(color, alpha), withAlpha(color, 0)),
            floatArrayOf(0f, 1f),
            Shader.TileMode.CLAMP
        )
        canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), paint)
    }

    /**
     * MUTED PATTERNS. Every pattern is now drawn faint, slightly blurred and fading across the poster instead of as crisp hairlines:
     * about half the old opacity, a soft edge (blur), and a fade (linear across the poster, or outward from the corner for rings and arcs).
     */
    private fun softPaint(
        unit: Float, style: Paint.Style, strokeWidth: Float, color: Int, alpha: Int, w: Int, h: Int,
        cx: Float? = null, cy: Float? = null, reach: Float = 0f, blur: Float = 0.004f
    ): Paint {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.style = style
        paint.strokeWidth = strokeWidth
        val near = withAlpha(color, alpha)
        val far = withAlpha(color, alpha / 4)
        paint.shader = if (cx != null && cy != null) {
            RadialGradient(cx, cy, max(1f, reach), intArrayOf(near, far), floatArrayOf(0f, 1f), Shader.TileMode.CLAMP)
        } else {
            LinearGradient(0f, 0f, w.toFloat(), h.toFloat(), near, far, Shader.TileMode.CLAMP)
        }
        paint.maskFilter = BlurMaskFilter(max(1f, unit * blur), BlurMaskFilter.Blur.NORMAL)
        return paint
    }

    private fun drawPattern(canvas: Canvas, pattern: Int, w: Int, h: Int, accent: Int) {
        if (pattern == 0) return
        val unit = min(w, h).toFloat()
        when (pattern) {
            1 -> {
                val paint = softPaint(unit, Paint.Style.FILL, 0f, Color.WHITE, 14, w, h, blur = 0.003f)
                val step = unit * 0.055f
                var y = step / 2f
                while (y < h) {
                    var x = step / 2f
                    while (x < w) {
                        canvas.drawCircle(x, y, unit * 0.003f, paint)
                        x += step
                    }
                    y += step
                }
            }
            2 -> {
                val paint = softPaint(unit, Paint.Style.STROKE, max(2f, unit * 0.003f), accent, 11, w, h)
                val step = unit * 0.085f
                var x = -h.toFloat()
                while (x < w) {
                    canvas.drawLine(x, h.toFloat(), x + h, 0f, paint)
                    x += step
                }
            }
            3, 5 -> {
                val cx = if (pattern == 3) w.toFloat() else 0f
                val cy = if (pattern == 3) 0f else h.toFloat()
                val paint = softPaint(unit, Paint.Style.STROKE, max(2f, unit * 0.004f), accent, 15, w, h, cx, cy, unit * 1.44f)
                for (i in 1..9) canvas.drawCircle(cx, cy, i * unit * 0.16f, paint)
            }
            4 -> {
                val paint = softPaint(unit, Paint.Style.STROKE, max(1.5f, unit * 0.002f), Color.WHITE, 8, w, h, blur = 0.003f)
                val step = unit * 0.10f
                var x = step
                while (x < w) {
                    canvas.drawLine(x, 0f, x, h.toFloat(), paint)
                    x += step
                }
                var y = step
                while (y < h) {
                    canvas.drawLine(0f, y, w.toFloat(), y, paint)
                    y += step
                }
            }
        }
    }

    private fun parse(hex: String, fallback: Int): Int =
        runCatching { Color.parseColor(if (hex.startsWith("#")) hex else "#" + hex) }.getOrDefault(fallback)

    private fun withAlpha(c: Int, alpha: Int): Int = Color.argb(alpha.coerceIn(0, 255), Color.red(c), Color.green(c), Color.blue(c))
}
