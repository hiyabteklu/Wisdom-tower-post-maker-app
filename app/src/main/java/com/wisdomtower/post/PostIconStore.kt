package com.wisdomtower.post

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.RectF
import android.net.Uri
import android.provider.OpenableColumns
import java.io.File
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

/**
 * The icon library (UI_UX_PLAN.md section 6): the user's own pictures copied into filesDir/icons, plus a small pack of flat starter shapes.
 * Pictures are shrunk to at most 1024 px on the longest side and stored as PNG, so they stay small and keep transparency.
 */
object PostIconStore {
    const val SHAPE = "shape:"
    const val FILE = "file:"
    private const val MAX_SIDE = 1024

    val shapes = listOf("diamond", "triangle", "hexagon", "ring", "plus", "arrow", "check")

    fun shapeTitle(name: String): String = name.replaceFirstChar { it.uppercase() }

    private fun dir(context: Context): File = File(context.filesDir, "icons").also { it.mkdirs() }

    /** The user's own pictures, newest first. */
    fun list(context: Context): List<File> =
        (dir(context).listFiles() ?: emptyArray<File>())
            .filter { it.isFile && it.name.endsWith(".png") }
            .sortedByDescending { it.lastModified() }

    /** Copies a PNG, WebP or JPG picked on the phone into the app. Returns the saved file, or null if it is not a readable picture. */
    fun import(context: Context, uri: Uri): File? = runCatching {
        var name = "icon"
        context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { c ->
            if (c.moveToFirst()) name = c.getString(0) ?: name
        }
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return@runCatching null
        var sample = 1
        while (max(bounds.outWidth, bounds.outHeight) / (sample * 2) >= MAX_SIDE) sample *= 2
        val opts = BitmapFactory.Options().apply { inSampleSize = sample }
        val decoded = context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, opts) }
            ?: return@runCatching null
        val longest = max(decoded.width, decoded.height)
        val bitmap = if (longest > MAX_SIDE) {
            val s = MAX_SIDE.toFloat() / longest
            Bitmap.createScaledBitmap(decoded, (decoded.width * s).toInt().coerceAtLeast(1), (decoded.height * s).toInt().coerceAtLeast(1), true)
        } else decoded
        val clean = name.substringBeforeLast('.').replace(Regex("[^A-Za-z0-9_-]"), "_").take(24).ifEmpty { "icon" }
        val target = File(dir(context), System.currentTimeMillis().toString() + "_" + clean + ".png")
        target.outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        target
    }.getOrNull()

    /** Small picture for the chips (about 96 px). */
    fun preview(path: String, target: Int = 96): Bitmap? = runCatching {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(path, bounds)
        var sample = 1
        while (max(bounds.outWidth, bounds.outHeight) / (sample * 2) >= target) sample *= 2
        BitmapFactory.decodeFile(path, BitmapFactory.Options().apply { inSampleSize = sample })
    }.getOrNull()

    fun titleOf(item: PostIconItem): String =
        if (item.source.startsWith(SHAPE)) shapeTitle(item.source.removePrefix(SHAPE)) else "Picture"

    /**
     * Draws one placed icon. The longest side is size * unit. A shape uses its tint, else defaultColor.
     * A picture keeps its own colors unless it has a tint.
     * A picture is decoded through PostImageCache, so dragging or pinching an icon does not read the file again for every redraw.
     */
    fun draw(canvas: Canvas, item: PostIconItem, w: Int, h: Int, unit: Float, defaultColor: Int) {
        val d = item.size * unit
        if (d <= 1f) return
        val cx = item.cx * w
        val cy = item.cy * h
        val tint = item.tintHex?.let { runCatching { Color.parseColor(it) }.getOrNull() }
        if (item.source.startsWith(SHAPE)) {
            drawShape(canvas, item.source.removePrefix(SHAPE), cx, cy, d, tint ?: defaultColor)
            return
        }
        val path = item.source.removePrefix(FILE)
        val file = File(path)
        if (!file.exists()) return
        val target = d.toInt().coerceAtLeast(32) * 2
        val picture = runCatching {
            PostImageCache.decode("icon:" + path + "|" + file.lastModified(), target) { file.inputStream() }
        }.getOrNull() ?: return
        val bmp = picture.bitmap
        val scale = d / max(bmp.width, bmp.height)
        val bw = bmp.width * scale
        val bh = bmp.height * scale
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        if (tint != null) paint.colorFilter = PorterDuffColorFilter(tint, PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bmp, null, RectF(cx - bw / 2f, cy - bh / 2f, cx + bw / 2f, cy + bh / 2f), paint)
        picture.release()
    }

    private fun drawShape(canvas: Canvas, name: String, cx: Float, cy: Float, d: Float, color: Int) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = color
        paint.style = Paint.Style.FILL
        val r = d / 2f
        val path = Path()
        when (name) {
            "diamond" -> {
                path.moveTo(cx, cy - r); path.lineTo(cx + r, cy); path.lineTo(cx, cy + r); path.lineTo(cx - r, cy); path.close()
                canvas.drawPath(path, paint)
            }
            "triangle" -> {
                path.moveTo(cx, cy - r); path.lineTo(cx + r, cy + r * 0.85f); path.lineTo(cx - r, cy + r * 0.85f); path.close()
                canvas.drawPath(path, paint)
            }
            "hexagon" -> {
                for (i in 0 until 6) {
                    val a = Math.toRadians(-90.0 + 60.0 * i)
                    val px = cx + r * cos(a).toFloat()
                    val py = cy + r * sin(a).toFloat()
                    if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
                }
                path.close()
                canvas.drawPath(path, paint)
            }
            "ring" -> {
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = d * 0.16f
                canvas.drawCircle(cx, cy, r - paint.strokeWidth / 2f, paint)
            }
            "plus" -> {
                val t = d * 0.16f
                canvas.drawRect(cx - t, cy - r, cx + t, cy + r, paint)
                canvas.drawRect(cx - r, cy - t, cx + r, cy + t, paint)
            }
            "arrow" -> {
                val t = d * 0.15f
                path.moveTo(cx - r, cy - t)
                path.lineTo(cx + r * 0.1f, cy - t)
                path.lineTo(cx + r * 0.1f, cy - r * 0.8f)
                path.lineTo(cx + r, cy)
                path.lineTo(cx + r * 0.1f, cy + r * 0.8f)
                path.lineTo(cx + r * 0.1f, cy + t)
                path.lineTo(cx - r, cy + t)
                path.close()
                canvas.drawPath(path, paint)
            }
            "check" -> {
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = d * 0.17f
                paint.strokeCap = Paint.Cap.ROUND
                paint.strokeJoin = Paint.Join.ROUND
                path.moveTo(cx - r * 0.75f, cy + r * 0.05f)
                path.lineTo(cx - r * 0.2f, cy + r * 0.6f)
                path.lineTo(cx + r * 0.8f, cy - r * 0.55f)
                canvas.drawPath(path, paint)
            }
        }
    }
}
