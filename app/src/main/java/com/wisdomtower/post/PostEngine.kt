package com.wisdomtower.post

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import android.net.Uri
import java.io.File
import java.util.Random
import kotlin.math.max
import kotlin.math.min

/**
 * Shapes for the template photo (REDESIGN_PLAN.md section 4). A post with no frame chosen keeps the template's own shape.
 * STACKED is a rounded photo with two tilted cards behind it.
 */
enum class PhotoFrame(val label: String) {
    ROUNDED("Rounded"),
    ARCH("Arch"),
    PILL("Pill"),
    CIRCLE("Circle"),
    STACKED("Stacked")
}

/** Soft color blends laid over the background gradient. Built from the brand colors, so they follow a color change. */
enum class MeshStyle(val label: String) {
    OFF("Off"),
    AURORA("Aurora"),
    SPOTLIGHT("Spotlight"),
    DUSK("Dusk")
}

/** How far a text slot was dragged away from where the template put it, as fractions of the post width and height. */
data class SlotOffset(val dx: Float, val dy: Float)

/** Saved offsets are per template and per size, because a slot sits somewhere different on every layout. */
private fun offsetKey(doc: PostDoc, slot: String): String = doc.templateId + ":" + doc.size.name + ":" + slot

fun PostDoc.offsetFor(slot: String): SlotOffset? =
    slotOffsets[offsetKey(this, slot)]?.takeIf { it.dx != 0f || it.dy != 0f }

fun PostDoc.withOffset(slot: String, offset: SlotOffset): PostDoc =
    copy(slotOffsets = slotOffsets + (offsetKey(this, slot) to offset))

/** Puts every slot of the current template and size back where the template designed it. */
fun PostDoc.withoutOffsets(): PostDoc {
    val prefix = templateId + ":" + size.name + ":"
    return copy(slotOffsets = slotOffsets.filterKeys { !it.startsWith(prefix) })
}

fun PostDoc.hasOffsets(): Boolean {
    val prefix = templateId + ":" + size.name + ":"
    return slotOffsets.any { it.key.startsWith(prefix) && (it.value.dx != 0f || it.value.dy != 0f) }
}

/**
 * Drawing helpers that sit beside PostRenderer: photo frames, mesh gradients, grain, the cut-out subject layer
 * and hit testing for dragging text on the preview.
 */
object PostEngine {
    /** The name used for the cut-out subject when it is the thing being dragged on the preview. */
    const val CUTOUT_TARGET = "@cutout"

    // ---- photo frames ----

    fun framePath(frame: PhotoFrame, r: RectF, unit: Float): Path {
        val path = Path()
        when (frame) {
            PhotoFrame.ROUNDED, PhotoFrame.STACKED -> {
                val rad = unit * 0.05f
                path.addRoundRect(r, rad, rad, Path.Direction.CW)
            }
            PhotoFrame.PILL -> {
                val rad = min(r.width(), r.height()) / 2f
                path.addRoundRect(r, rad, rad, Path.Direction.CW)
            }
            PhotoFrame.CIRCLE -> {
                val d = min(r.width(), r.height())
                path.addOval(RectF(r.centerX() - d / 2f, r.centerY() - d / 2f, r.centerX() + d / 2f, r.centerY() + d / 2f), Path.Direction.CW)
            }
            PhotoFrame.ARCH -> {
                val rad = min(r.width() / 2f, r.height() / 2f)
                path.moveTo(r.left, r.bottom)
                path.lineTo(r.left, r.top + rad)
                path.arcTo(RectF(r.left, r.top, r.right, r.top + 2f * rad), 180f, 180f, false)
                path.lineTo(r.right, r.bottom)
                path.close()
            }
        }
        return path
    }

    /** Two tilted cards behind a STACKED photo. far is the back card, near the one just behind the photo. */
    fun drawStackedBacks(canvas: Canvas, dest: RectF, unit: Float, far: Int, near: Int) {
        val rad = unit * 0.05f
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.setShadowLayer(unit * 0.02f, 0f, unit * 0.008f, Color.argb(70, 0, 0, 0))
        canvas.save()
        canvas.rotate(-6f, dest.centerX(), dest.centerY())
        paint.color = far
        canvas.drawRoundRect(dest, rad, rad, paint)
        canvas.restore()
        canvas.save()
        canvas.rotate(4f, dest.centerX(), dest.centerY())
        paint.color = near
        canvas.drawRoundRect(dest, rad, rad, paint)
        canvas.restore()
    }

    /** A soft shadow under the frame. The photo is drawn over the fill, so only the shadow stays visible. */
    fun drawFrameShadow(canvas: Canvas, path: Path, unit: Float) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = Color.BLACK
        paint.setShadowLayer(unit * 0.025f, 0f, unit * 0.010f, Color.argb(80, 0, 0, 0))
        canvas.drawPath(path, paint)
    }

    fun drawFrameBorder(canvas: Canvas, path: Path, unit: Float, color: Int) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = max(2f, unit * 0.012f)
        paint.color = color
        canvas.drawPath(path, paint)
    }

    // ---- mesh gradient and grain ----

    private class Blob(val cx: Float, val cy: Float, val r: Float, val color: Int, val alpha: Int)

    /**
     * Two or three large soft color areas over the background, so the gradient looks blended rather than a straight ramp.
     * w and h are the full export size (the canvas is already scaled for previews).
     */
    fun drawMesh(canvas: Canvas, style: MeshStyle, w: Int, h: Int, accent: Int, glow: Int, deep: Int, secondary: Int) {
        val blobs: List<Blob> = when (style) {
            MeshStyle.OFF -> emptyList()
            MeshStyle.AURORA -> listOf(
                Blob(0.12f, 0.08f, 0.80f, glow, 175),
                Blob(0.95f, 0.52f, 0.75f, accent, 150),
                Blob(0.25f, 1.00f, 0.70f, secondary, 105)
            )
            MeshStyle.SPOTLIGHT -> listOf(
                Blob(0.50f, 0.38f, 0.85f, glow, 200),
                Blob(0.00f, 1.00f, 0.70f, deep, 140),
                Blob(1.00f, 0.00f, 0.55f, accent, 90)
            )
            MeshStyle.DUSK -> listOf(
                Blob(0.90f, 0.10f, 0.70f, secondary, 140),
                Blob(0.05f, 0.60f, 0.80f, accent, 150),
                Blob(0.70f, 1.00f, 0.65f, glow, 110)
            )
        }
        val reach = max(w, h).toFloat()
        blobs.forEach { b ->
            val paint = Paint(Paint.ANTI_ALIAS_FLAG)
            paint.shader = RadialGradient(
                b.cx * w, b.cy * h, b.r * reach,
                intArrayOf(withAlpha(b.color, b.alpha), withAlpha(b.color, 0)),
                floatArrayOf(0f, 1f),
                Shader.TileMode.CLAMP
            )
            canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), paint)
        }
    }

    private var grainTile: Bitmap? = null

    private fun grain(): Bitmap {
        grainTile?.let { return it }
        val size = 128
        val rnd = Random(7L)
        val px = IntArray(size * size) {
            val v = if (rnd.nextBoolean()) 255 else 0
            Color.argb(12, v, v, v)
        }
        val bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        bmp.setPixels(px, 0, size, 0, 0, size, size)
        grainTile = bmp
        return bmp
    }

    /** A very light noise over the background, so smooth gradients do not show bands at large export sizes. */
    fun drawGrain(canvas: Canvas, w: Int, h: Int) {
        val shader = BitmapShader(grain(), Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
        val s = max(1f, w / 1080f)
        val m = Matrix()
        m.setScale(s, s)
        shader.setLocalMatrix(m)
        val paint = Paint()
        paint.shader = shader
        canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), paint)
    }

    // ---- cut-out subject ----

    /** Copies a picture picked on the phone into app storage so it survives. Returns the saved path, or null if it cannot be read. */
    fun importCutout(context: Context, uri: Uri): String? {
        val dir = File(context.filesDir, "cutouts").apply { mkdirs() }
        val target = File(dir, "cutout_" + System.currentTimeMillis() + ".png")
        val copied = runCatching {
            context.contentResolver.openInputStream(uri)?.use { input -> target.outputStream().use { out -> input.copyTo(out) } } != null
        }.getOrDefault(false)
        if (!copied) {
            target.delete()
            return null
        }
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(target.absolutePath, bounds)
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) {
            target.delete()
            return null
        }
        return target.absolutePath
    }

    /** True when the picture has a transparent channel, which a cut-out needs. A JPG, or a PNG with a solid background, returns false. */
    fun hasTransparency(path: String): Boolean {
        val opts = BitmapFactory.Options().apply { inSampleSize = 8 }
        val bmp = runCatching { BitmapFactory.decodeFile(path, opts) }.getOrNull() ?: return false
        val has = bmp.hasAlpha()
        bmp.recycle()
        return has
    }

    private fun decode(path: String, target: Int): Bitmap? {
        if (!File(path).exists()) return null
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(path, bounds)
        var sample = 1
        while (bounds.outWidth / (sample * 2) >= target && bounds.outHeight / (sample * 2) >= target) sample *= 2
        val real = BitmapFactory.Options().apply { inSampleSize = sample }
        return runCatching { BitmapFactory.decodeFile(path, real) }.getOrNull()
    }

    /**
     * Draws the cut-out subject. cutoutX is the horizontal centre and cutoutY the bottom edge, both as fractions of the post;
     * cutoutScale multiplies a height of 72% of the post. Drawn between the "behind" text and the "front" text.
     */
    fun drawCutout(canvas: Canvas, path: String, doc: PostDoc, w: Int, h: Int, decodeTo: Int) {
        val bmp = decode(path, decodeTo) ?: return
        var th = h * 0.72f * doc.cutoutScale
        var tw = th * bmp.width / max(1, bmp.height).toFloat()
        val cap = w * 1.1f
        if (tw > cap) {
            th *= cap / tw
            tw = cap
        }
        val cx = doc.cutoutX * w
        val bottom = doc.cutoutY * h
        val dest = RectF(cx - tw / 2f, bottom - th, cx + tw / 2f, bottom)
        canvas.drawBitmap(bmp, null, dest, Paint(Paint.FILTER_BITMAP_FLAG or Paint.ANTI_ALIAS_FLAG))
        bmp.recycle()
    }

    // ---- dragging text on the preview ----

    /**
     * The text slot under a touch point (fx and fy are fractions of the preview), counting any offset already applied.
     * Later slots win, so a slot drawn on top is picked first. Returns null when the touch lands on no text.
     */
    fun hitSlot(layout: TemplateLayout, doc: PostDoc, fx: Float, fy: Float): String? {
        var found: String? = null
        layout.slots.forEach { box ->
            if (box.literal != null || box.slot.isEmpty()) return@forEach
            val off = doc.offsetFor(box.slot)
            val left = box.x + (off?.dx ?: 0f)
            val top = box.y + (off?.dy ?: 0f)
            if (fx >= left && fx <= left + box.w && fy >= top && fy <= top + box.h) found = box.slot
        }
        return found
    }

    private fun withAlpha(c: Int, alpha: Int): Int = Color.argb(alpha.coerceIn(0, 255), Color.red(c), Color.green(c), Color.blue(c))
}
