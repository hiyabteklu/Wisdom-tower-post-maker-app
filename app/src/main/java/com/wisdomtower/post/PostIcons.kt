package com.wisdomtower.post

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface

/**
 * Footer icon set, drawn in code so there are no image files to manage. draw() paints a single-color glyph;
 * drawBadge() puts that glyph on a filled circle, optionally in the platform's own color.
 */
object PostIcons {
    val kinds = listOf(
        "website", "telegram", "tiktok", "youtube", "instagram", "facebook",
        "whatsapp", "x", "linkedin", "email", "phone"
    )

    fun title(kind: String): String = when (kind) {
        "website" -> "Website"
        "telegram" -> "Telegram"
        "tiktok" -> "TikTok"
        "youtube" -> "YouTube"
        "instagram" -> "Instagram"
        "facebook" -> "Facebook"
        "whatsapp" -> "WhatsApp"
        "x" -> "X"
        "linkedin" -> "LinkedIn"
        "email" -> "Email"
        "phone" -> "Phone"
        else -> "Link"
    }

    /** The platform's own color, or null when it has none (website, email, phone use the brand color). Instagram is a gradient, drawn separately. */
    fun officialColor(kind: String): Int? = when (kind) {
        "telegram" -> Color.parseColor("#229ED9")
        "tiktok" -> Color.parseColor("#141414")
        "youtube" -> Color.parseColor("#FF0000")
        "instagram" -> Color.parseColor("#E1306C")
        "facebook" -> Color.parseColor("#1877F2")
        "whatsapp" -> Color.parseColor("#25D366")
        "x" -> Color.parseColor("#141414")
        "linkedin" -> Color.parseColor("#0A66C2")
        else -> null
    }

    /** Glyph on a filled circle of diameter d. fill is the circle color, glyph the icon color, ring the soft outline color. */
    fun drawBadge(canvas: Canvas, kind: String, cx: Float, cy: Float, d: Float, fill: Int, glyph: Int, ring: Int, instaGradient: Boolean) {
        val p = Paint(Paint.ANTI_ALIAS_FLAG)
        p.style = Paint.Style.FILL
        if (instaGradient) {
            p.shader = LinearGradient(
                cx - d / 2f, cy + d / 2f, cx + d / 2f, cy - d / 2f,
                intArrayOf(Color.parseColor("#F9A03F"), Color.parseColor("#E1306C"), Color.parseColor("#7B3FE4")),
                null, Shader.TileMode.CLAMP
            )
        } else {
            p.color = fill
        }
        canvas.drawCircle(cx, cy, d / 2f, p)
        val edge = Paint(Paint.ANTI_ALIAS_FLAG)
        edge.style = Paint.Style.STROKE
        edge.strokeWidth = d * 0.045f
        edge.color = Color.argb(110, Color.red(ring), Color.green(ring), Color.blue(ring))
        canvas.drawCircle(cx, cy, d / 2f - edge.strokeWidth / 2f, edge)
        draw(canvas, kind, cx, cy, d * 0.58f, glyph)
    }

    fun draw(canvas: Canvas, kind: String, cx: Float, cy: Float, size: Float, color: Int) {
        val r = size / 2f
        val stroke = Paint(Paint.ANTI_ALIAS_FLAG)
        stroke.color = color
        stroke.style = Paint.Style.STROKE
        stroke.strokeWidth = size * 0.085f
        stroke.strokeCap = Paint.Cap.ROUND
        stroke.strokeJoin = Paint.Join.ROUND
        val fill = Paint(Paint.ANTI_ALIAS_FLAG)
        fill.color = color
        fill.style = Paint.Style.FILL

        when (kind) {
            "website" -> {
                canvas.drawCircle(cx, cy, r * 0.88f, stroke)
                canvas.drawOval(RectF(cx - r * 0.38f, cy - r * 0.88f, cx + r * 0.38f, cy + r * 0.88f), stroke)
                canvas.drawLine(cx - r * 0.88f, cy, cx + r * 0.88f, cy, stroke)
            }
            "telegram" -> {
                val plane = Path()
                plane.moveTo(cx - r * 0.95f, cy - r * 0.05f)
                plane.lineTo(cx + r * 0.95f, cy - r * 0.85f)
                plane.lineTo(cx + r * 0.4f, cy + r * 0.85f)
                plane.lineTo(cx + r * 0.02f, cy + r * 0.2f)
                plane.close()
                canvas.drawPath(plane, fill)
            }
            "tiktok" -> {
                canvas.drawCircle(cx - r * 0.22f, cy + r * 0.52f, r * 0.36f, fill)
                canvas.drawLine(cx + r * 0.14f, cy - r * 0.85f, cx + r * 0.14f, cy + r * 0.52f, stroke)
                val flag = Path()
                flag.moveTo(cx + r * 0.14f, cy - r * 0.85f)
                flag.cubicTo(cx + r * 0.2f, cy - r * 0.4f, cx + r * 0.5f, cy - r * 0.3f, cx + r * 0.9f, cy - r * 0.3f)
                canvas.drawPath(flag, stroke)
            }
            "youtube" -> {
                canvas.drawRoundRect(RectF(cx - r * 0.95f, cy - r * 0.66f, cx + r * 0.95f, cy + r * 0.66f), r * 0.35f, r * 0.35f, stroke)
                val play = Path()
                play.moveTo(cx - r * 0.2f, cy - r * 0.28f)
                play.lineTo(cx + r * 0.32f, cy)
                play.lineTo(cx - r * 0.2f, cy + r * 0.28f)
                play.close()
                canvas.drawPath(play, fill)
            }
            "instagram" -> {
                canvas.drawRoundRect(RectF(cx - r * 0.85f, cy - r * 0.85f, cx + r * 0.85f, cy + r * 0.85f), r * 0.45f, r * 0.45f, stroke)
                canvas.drawCircle(cx, cy, r * 0.38f, stroke)
                canvas.drawCircle(cx + r * 0.52f, cy - r * 0.52f, r * 0.08f, fill)
            }
            "facebook" -> {
                val letter = Paint(Paint.ANTI_ALIAS_FLAG)
                letter.color = color
                letter.textSize = r * 2.1f
                letter.typeface = Typeface.create("sans-serif", Typeface.BOLD)
                letter.textAlign = Paint.Align.CENTER
                canvas.drawText("f", cx + r * 0.08f, cy + r * 0.78f, letter)
            }
            "whatsapp" -> {
                canvas.drawCircle(cx, cy - r * 0.04f, r * 0.84f, stroke)
                val tail = Path()
                tail.moveTo(cx - r * 0.72f, cy + r * 0.55f)
                tail.lineTo(cx - r * 0.92f, cy + r * 0.95f)
                tail.lineTo(cx - r * 0.40f, cy + r * 0.78f)
                canvas.drawPath(tail, stroke)
                val hand = Paint(stroke)
                hand.strokeWidth = size * 0.12f
                val h = Path()
                h.moveTo(cx - r * 0.30f, cy - r * 0.34f)
                h.cubicTo(cx - r * 0.40f, cy + r * 0.10f, cx + r * 0.05f, cy + r * 0.42f, cx + r * 0.36f, cy + r * 0.30f)
                canvas.drawPath(h, hand)
            }
            "x" -> {
                val thick = Paint(stroke)
                thick.strokeWidth = size * 0.14f
                canvas.drawLine(cx - r * 0.62f, cy - r * 0.72f, cx + r * 0.62f, cy + r * 0.72f, thick)
                val thin = Paint(stroke)
                thin.strokeWidth = size * 0.07f
                canvas.drawLine(cx + r * 0.68f, cy - r * 0.72f, cx - r * 0.68f, cy + r * 0.72f, thin)
            }
            "linkedin" -> {
                val letter = Paint(Paint.ANTI_ALIAS_FLAG)
                letter.color = color
                letter.textSize = r * 1.7f
                letter.typeface = Typeface.create("sans-serif", Typeface.BOLD)
                letter.textAlign = Paint.Align.CENTER
                canvas.drawText("in", cx, cy + r * 0.58f, letter)
            }
            "email" -> {
                canvas.drawRoundRect(RectF(cx - r * 0.92f, cy - r * 0.64f, cx + r * 0.92f, cy + r * 0.64f), r * 0.2f, r * 0.2f, stroke)
                val v = Path()
                v.moveTo(cx - r * 0.82f, cy - r * 0.5f)
                v.lineTo(cx, cy + r * 0.12f)
                v.lineTo(cx + r * 0.82f, cy - r * 0.5f)
                canvas.drawPath(v, stroke)
            }
            "phone" -> {
                canvas.drawRoundRect(RectF(cx - r * 0.45f, cy - r * 0.9f, cx + r * 0.45f, cy + r * 0.9f), r * 0.2f, r * 0.2f, stroke)
                canvas.drawCircle(cx, cy + r * 0.62f, r * 0.07f, fill)
            }
            else -> canvas.drawCircle(cx, cy, r * 0.3f, fill)
        }
    }
}
