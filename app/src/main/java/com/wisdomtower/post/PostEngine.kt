package com.wisdomtower.post

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import android.net.Uri
import java.io.File
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

/**
 * Shared geometry, hit-testing (with inverse rotation), photo frame paths, cut-out import,
 * mesh gradients and film grain (docs/REDESIGN_PLAN.md and docs/TEMPLATE_REDESIGN_PLAN.md 3.1).
 */
object PostEngine {
    private var grainTile: Bitmap? = null

    fun importCutout(context: Context, uri: Uri): String? {
        val dir = File(context.filesDir, "photos").also { it.mkdirs() }
        val out = File(dir, "cutout_${System.currentTimeMillis()}_${kotlin.random.Random.nextInt(1000, 9999)}.png")
        val copied = runCatching {
            context.contentResolver.openInputStream(uri)?.use { input ->
                out.outputStream().use { input.copyTo(it) }
            } != null
        }.getOrDefault(false)
        if (!copied) return null
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(out.absolutePath, bounds)
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) {
            out.delete()
            return null
        }
        return Uri.fromFile(out).toString()
    }

    fun hasTransparency(storedUri: String): Boolean = runCatching {
        val path = Uri.parse(storedUri).path ?: return@runCatching false
        val opts = BitmapFactory.Options().apply { inSampleSize = 4 }
        val bmp = BitmapFactory.decodeFile(path, opts) ?: return@runCatching false
        try {
            if (!bmp.hasAlpha()) return@runCatching false
            val w = bmp.width
            val h = bmp.height
            val stepX = max(1, w / 24)
            val stepY = max(1, h / 24)
            for (y in 0 until h step stepY) {
                for (x in 0 until w step stepX) {
                    if (Color.alpha(bmp.getPixel(x, y)) < 230) return@runCatching true
                }
            }
            false
        } finally {
            bmp.recycle()
        }
    }.getOrDefault(false)

    /**
     * Builds the clipping path for a photo window shaped by [frame].
     * ROUNDED uses unit * 0.05 (54 px at 1080) as required by DEVICE_MOCKUP_PLAN.md.
     */
    fun framePath(frame: PhotoFrame, rect: RectF, unit: Float): Path {
        val path = Path()
        when (frame) {
            PhotoFrame.ROUNDED, PhotoFrame.STACKED -> {
                val r = unit * 0.05f
                path.addRoundRect(rect, r, r, Path.Direction.CW)
            }
            PhotoFrame.PILL -> {
                val r = min(rect.width(), rect.height()) / 2f
                path.addRoundRect(rect, r, r, Path.Direction.CW)
            }
            PhotoFrame.CIRCLE -> {
                val r = min(rect.width(), rect.height()) / 2f
                path.addCircle(rect.centerX(), rect.centerY(), r, Path.Direction.CW)
            }
            PhotoFrame.ARCH -> {
                val r = rect.width() / 2f
                val archTop = rect.top + min(r, rect.height() * 0.48f)
                path.moveTo(rect.left, rect.bottom)
                path.lineTo(rect.left, archTop)
                path.arcTo(RectF(rect.left, rect.top, rect.right, rect.top + (archTop - rect.top) * 2f), 180f, 180f, false)
                path.lineTo(rect.right, rect.bottom)
                path.close()
            }
        }
        return path
    }

    fun slotRect(doc: PostDoc, box: SlotBox, canvasW: Float, canvasH: Float): RectF {
        val (dx, dy) = doc.offsetOf(box.slot)
        val left = (box.x + dx) * canvasW
        val top = (box.y + dy) * canvasH
        return RectF(left, top, left + box.w * canvasW, top + box.h * canvasH)
    }

    fun slotRect(box: SlotBox, doc: PostDoc): RectF =
        slotRect(doc, box, 1f, 1f)

    fun effectiveRotation(doc: PostDoc, box: SlotBox): Float =
        doc.slotStyles[box.slot]?.rotate ?: box.rotate

    fun effectiveRotation(box: SlotBox, doc: PostDoc): Float =
        effectiveRotation(doc, box)

    /**
     * Hit-tests word slots on the preview in reverse draw order (topmost first).
     * Inverse-rotates the touch point around the slot box centre when `rotate != 0` (TEMPLATE_REDESIGN_PLAN.md 3.1).
     */
    fun hitSlot(doc: PostDoc, layout: TemplateLayout, normX: Float, normY: Float): String? {
        val w = doc.size.width.toFloat()
        val h = doc.size.height.toFloat()
        val px = normX * w
        val py = normY * h

        val editableSlots = layout.slots.filter { it.literal == null && it.slot !in doc.hiddenTextSlots }
        for (box in editableSlots.asReversed()) {
            val rect = slotRect(doc, box, w, h)
            val angleDeg = effectiveRotation(doc, box)
            val (testX, testY) = if (abs(angleDeg) > 0.01f) {
                val cx = rect.centerX()
                val cy = rect.centerY()
                val rad = Math.toRadians((-angleDeg).toDouble())
                val cosA = cos(rad).toFloat()
                val sinA = sin(rad).toFloat()
                val vx = px - cx
                val vy = py - cy
                (cx + vx * cosA - vy * sinA) to (cy + vx * sinA + vy * cosA)
            } else {
                px to py
            }
            val padX = max(14f, rect.width() * 0.04f)
            val padY = max(14f, rect.height() * 0.06f)
            if (testX in (rect.left - padX)..(rect.right + padX) &&
                testY in (rect.top - padY)..(rect.bottom + padY)
            ) {
                return box.slot
            }
        }
        return null
    }

    fun hitSlot(layout: TemplateLayout, doc: PostDoc, normX: Float, normY: Float): String? =
        hitSlot(doc, layout, normX, normY)

    fun hitCutout(doc: PostDoc, normX: Float, normY: Float): Boolean {
        if (doc.cutoutUri.isNullOrBlank()) return false
        val halfW = 0.24f * doc.cutoutScale.coerceIn(0.3f, 2.5f)
        val height = 0.56f * doc.cutoutScale.coerceIn(0.3f, 2.5f)
        val left = doc.cutoutX - halfW
        val right = doc.cutoutX + halfW
        val bottom = doc.cutoutY
        val top = bottom - height
        return normX in left..right && normY in top..bottom
    }

    fun hitIcon(doc: PostDoc, normX: Float, normY: Float): Int? {
        for (i in doc.icons.indices.reversed()) {
            val item = doc.icons[i]
            val r = max(0.05f, item.size * 0.6f)
            if (abs(normX - item.cx) <= r && abs(normY - item.cy) <= r) {
                return i
            }
        }
        return null
    }

    fun drawMesh(canvas: Canvas, style: MeshStyle, w: Int, h: Int, accent: Int, secondary: Int, glow: Int) {
        if (style == MeshStyle.NONE) return
        val reach = max(w, h).toFloat()
        fun orb(cx: Float, cy: Float, radiusFrac: Float, color: Int, alpha: Int) {
            val p = Paint(Paint.ANTI_ALIAS_FLAG)
            p.shader = RadialGradient(
                cx * w,
                cy * h,
                max(1f, reach * radiusFrac),
                intArrayOf(
                    Color.argb(alpha.coerceIn(0, 255), Color.red(color), Color.green(color), Color.blue(color)),
                    Color.argb(0, Color.red(color), Color.green(color), Color.blue(color))
                ),
                floatArrayOf(0f, 1f),
                Shader.TileMode.CLAMP
            )
            canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), p)
        }
        when (style) {
            MeshStyle.NONE -> Unit
            MeshStyle.AURORA -> {
                orb(0.16f, 0.20f, 0.78f, glow, 92)
                orb(0.84f, 0.78f, 0.75f, secondary, 76)
                orb(0.54f, 0.46f, 0.64f, accent, 64)
            }
            MeshStyle.SPOTLIGHT -> {
                orb(0.50f, 0.38f, 0.74f, glow, 100)
                orb(0.50f, 0.62f, 0.52f, accent, 58)
            }
            MeshStyle.DUSK -> {
                val p = Paint(Paint.ANTI_ALIAS_FLAG)
                p.shader = LinearGradient(
                    0f, 0f, w.toFloat(), h.toFloat(),
                    intArrayOf(
                        Color.argb(80, Color.red(accent), Color.green(accent), Color.blue(accent)),
                        Color.argb(0, 0, 0, 0),
                        Color.argb(74, Color.red(secondary), Color.green(secondary), Color.blue(secondary))
                    ),
                    floatArrayOf(0f, 0.5f, 1f),
                    Shader.TileMode.CLAMP
                )
                canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), p)
                orb(0.85f, 0.18f, 0.65f, glow, 68)
            }
        }
    }

    fun drawGrain(canvas: Canvas, w: Int, h: Int, strength: Int = 22) {
        val tile = synchronized(this) {
            grainTile?.takeIf { !it.isRecycled } ?: run {
                val size = 96
                val bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
                val pixels = IntArray(size * size)
                var seed = 0x5DEECE66DL
                for (i in pixels.indices) {
                    seed = (seed * 25214903917L + 11L) and ((1L shl 48) - 1)
                    val v = ((seed ushr 24) and 0xFF).toInt()
                    val lum = if (v > 128) 255 else 0
                    val a = (v and 0x3F)
                    pixels[i] = Color.argb(a, lum, lum, lum)
                }
                bmp.setPixels(pixels, 0, size, 0, 0, size, size)
                grainTile = bmp
                bmp
            }
        }
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.alpha = strength.coerceIn(8, 90)
        var y = 0
        while (y < h) {
            var x = 0
            while (x < w) {
                canvas.drawBitmap(tile, x.toFloat(), y.toFloat(), paint)
                x += tile.width
            }
            y += tile.height
        }
    }
}
