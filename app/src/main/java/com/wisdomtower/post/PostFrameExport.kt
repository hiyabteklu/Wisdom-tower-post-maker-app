package com.wisdomtower.post

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * "Save frame for video" for the two phone mockup templates (PostTemplatesDevice.kt: device-hero, device-headline).
 *
 * It saves the same design as a normal export, but as a PNG with the phone's screen cut out (fully see-through) and no screenshot.
 * In a video editor (CapCut) the screen recording goes on the bottom layer and this PNG goes above it as an overlay,
 * so the recording shows through the screen window.
 *
 * It does not touch PostRenderer or the photo export: it calls PostRenderer.render with the photo left out,
 * then erases the screen window from the finished bitmap. The window is the template's photo area, in the same shape the
 * photo uses (the rounded frame), so the cut-out matches where a screenshot would sit.
 */
object PostFrameExport {
    /** True for the templates this export is offered on. */
    fun isDeviceTemplate(templateId: String): Boolean = DeviceTemplates.any { it.id == templateId }

    /** Renders each size with the screen cut out and saves it as a PNG. Returns the saved files. */
    fun save(context: Context, doc: PostDoc, brand: PostBrand, sizes: List<PostSize>, fourK: Boolean): List<Uri> {
        val uris = mutableListOf<Uri>()
        val stem = PostExport.fileStem(doc) + "-frame"
        for (size in sizes) {
            val sized = doc.copy(size = size, photoUri = null)
            val result = PostRenderer.render(context, sized, brand, if (fourK) 2f else 1f)
            try {
                punchScreen(result.bitmap, sized)
                val name = stem + (if (sizes.size > 1) "-" + size.label.replace(':', 'x') else "") + ".png"
                uris.add(writePng(context, result.bitmap, name))
            } finally {
                result.bitmap.recycle()
            }
        }
        return uris
    }

    /**
     * Erases the screen window from the bitmap, in the shape the photo would take (same rule as PostRenderer.drawPhotoArea:
     * the chosen photo frame, else the template's own mask, which is the rounded screen of the phone mockups).
     */
    private fun punchScreen(bitmap: Bitmap, doc: PostDoc) {
        val area = postTemplate(doc.templateId).layout(doc.size).photo ?: return
        val w = doc.size.width
        val h = doc.size.height
        val unit = min(w, h).toFloat()
        val dest = RectF(area.x * w, area.y * h, (area.x + area.w) * w, (area.y + area.h) * h)
        val frame = (doc.photoFrame ?: area.mask)?.takeIf { area.w < 0.99f && area.h < 0.99f }
        val path = frame?.let { PostEngine.framePath(it, dest, unit) }
            ?: Path().apply { addRect(dest, Path.Direction.CW) }
        val canvas = Canvas(bitmap)
        canvas.scale(bitmap.width / w.toFloat(), bitmap.height / h.toFloat())
        val clear = Paint(Paint.ANTI_ALIAS_FLAG)
        clear.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        canvas.drawPath(path, clear)
    }

    /**
     * A short how-to for placing the recording in CapCut, with numbers worked out from the template.
     * Numbers are only given for 9:16, where a recording fitted to the full height is the reference size.
     */
    fun hint(doc: PostDoc, size: PostSize): String {
        val base = "In CapCut: put your screen recording on the bottom layer and this PNG above it (Overlay)."
        if (size != PostSize.STORY) return base + " Scale and move the recording until it fills the see-through screen."
        val area = postTemplate(doc.templateId).layout(size).photo ?: return base
        // a bit over the exact size, so the edge of the recording hides under the phone's bezel
        val scalePct = (area.h * 100f + 1f).roundToInt()
        val centerPct = ((area.y + area.h / 2f) * 100f).roundToInt()
        val place = if (centerPct in 49..51) "keep it centered"
        else "move its middle to about $centerPct% of the way down the screen"
        return base + " With the recording fitted to the full height, scale it to about $scalePct% and $place."
    }

    private fun writePng(context: Context, bitmap: Bitmap, name: String): Uri {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, name)
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Wisdom Tower Post")
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
            val resolver = context.contentResolver
            val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                ?: throw IllegalStateException("Could not create the image file")
            try {
                resolver.openOutputStream(uri)?.use { out ->
                    if (!bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)) throw IllegalStateException("Image encoding failed")
                } ?: throw IllegalStateException("Could not open the image file")
                values.clear()
                values.put(MediaStore.Images.Media.IS_PENDING, 0)
                resolver.update(uri, values, null, null)
            } catch (error: Throwable) {
                resolver.delete(uri, null, null)
                throw error
            }
            return uri
        }
        val folder = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Wisdom Tower Post")
        folder.mkdirs()
        val file = File(folder, name)
        FileOutputStream(file).use {
            if (!bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)) throw IllegalStateException("Image encoding failed")
        }
        return FileProvider.getUriForFile(context, context.packageName + ".fileprovider", file)
    }
}
