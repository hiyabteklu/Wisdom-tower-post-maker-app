package com.wisdomtower.post

import android.content.ContentValues
import android.content.Context
import android.content.ClipData
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun exportDesign(context: Context, document: DesignDocument, format: String, share: Boolean = false) {
    val preset = document.canvasPreset
    val width = preset.width
    val height = preset.height
    val stamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
    val base = "WisdomTower_${preset.name}_$stamp"
    var shareUri: Uri? = null
    val mimeTypeForShare = when (format) { "jpg" -> "image/jpeg"; "png" -> "image/png"; else -> "application/pdf" }
    try {
        if (format == "jpg" || format == "png") {
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            drawDesign(context, Canvas(bitmap), document, width, height)
            val extension = if (format == "png") "png" else "jpg"
            val mimeType = if (format == "png") "image/png" else "image/jpeg"
            val compressFormat = if (format == "png") Bitmap.CompressFormat.PNG else Bitmap.CompressFormat.JPEG
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, "$base.$extension")
                    put(MediaStore.Images.Media.MIME_TYPE, mimeType)
                    put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Wisdom Tower Post")
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }
                val resolver = context.contentResolver
                val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                    ?: throw IllegalStateException("Unable to create image")
                shareUri = uri
                resolver.openOutputStream(uri)?.use { output ->
                    if (!bitmap.compress(compressFormat, if (format == "png") 100 else 96, output)) error("Image encoding failed")
                } ?: error("Unable to open JPG output")
                values.clear()
                values.put(MediaStore.Images.Media.IS_PENDING, 0)
                resolver.update(uri, values, null, null)
            } else {
                val folder = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Wisdom Tower Post")
                folder.mkdirs()
                val file = File(folder, "$base.$extension")
                FileOutputStream(file).use { bitmap.compress(compressFormat, if (format == "png") 100 else 96, it) }
                shareUri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            }
            bitmap.recycle()
            Toast.makeText(context, "${extension.uppercase(Locale.US)} exported: $width × $height", Toast.LENGTH_LONG).show()
        } else {
            val pageWidth = when (preset) {
                CanvasPreset.A4_PORTRAIT -> 595
                CanvasPreset.A4_LANDSCAPE -> 842
                else -> 540
            }
            val pageHeight = (pageWidth.toFloat() * height / width).toInt()
            val pdf = PdfDocument()
            val page = pdf.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create())
            page.canvas.save()
            page.canvas.scale(pageWidth.toFloat() / width, pageHeight.toFloat() / height)
            drawDesign(context, page.canvas, document, width, height)
            page.canvas.restore()
            pdf.finishPage(page)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, "$base.pdf")
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/Wisdom Tower Post")
                    put(MediaStore.MediaColumns.IS_PENDING, 1)
                }
                val resolver = context.contentResolver
                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                    ?: throw IllegalStateException("Unable to create PDF")
                shareUri = uri
                resolver.openOutputStream(uri)?.use { pdf.writeTo(it) } ?: error("Unable to open PDF output")
                values.clear()
                values.put(MediaStore.MediaColumns.IS_PENDING, 0)
                resolver.update(uri, values, null, null)
            } else {
                val folder = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "Wisdom Tower Post")
                folder.mkdirs()
                val file = File(folder, "$base.pdf")
                FileOutputStream(file).use { pdf.writeTo(it) }
                shareUri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            }
            pdf.close()
            Toast.makeText(context, "PDF exported", Toast.LENGTH_LONG).show()
        }
        val uriToShare = shareUri
        if (share && uriToShare != null) {
            val send = Intent(Intent.ACTION_SEND).apply {
                type = mimeTypeForShare
                putExtra(Intent.EXTRA_STREAM, uriToShare)
                clipData = ClipData.newUri(context.contentResolver, "Wisdom Tower design", uriToShare)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(send, "Share your design"))
        }
    } catch (error: Exception) {
        Toast.makeText(context, "Export failed: ${error.localizedMessage ?: "unknown error"}", Toast.LENGTH_LONG).show()
    }
}

private fun drawDesign(context: Context, canvas: Canvas, document: DesignDocument, width: Int, height: Int) {
    val start = android.graphics.Color.parseColor(document.backgroundColor)
    val end = android.graphics.Color.parseColor(document.gradientEnd)
    val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    if (document.useGradient) {
        val angle = Math.toRadians(document.gradientAngle.toDouble())
        bgPaint.shader = LinearGradient(
            0f, 0f,
            width * kotlin.math.cos(angle).toFloat(),
            height * kotlin.math.sin(angle).toFloat(),
            android.graphics.Color.parseColor(document.gradientStart), end, Shader.TileMode.CLAMP
        )
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)
        bgPaint.shader = null
    } else canvas.drawColor(start)

    document.layers.filter { it.visible }.forEach { layer ->
        val left = layer.x * width
        val top = layer.y * height
        val right = left + layer.width * width
        val bottom = top + layer.height * height
        val bounds = RectF(left, top, right, bottom)
        val layerColor = android.graphics.Color.parseColor(layer.colorHex)
        canvas.save()
        canvas.rotate(layer.rotation, bounds.centerX(), bounds.centerY())
        when (layer.type) {
            LayerType.SHAPE -> {
                val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { alpha = (layer.opacity.coerceIn(0f, 1f) * 255).toInt() }
                if (layer.gradientEnabled) {
                    paint.shader = LinearGradient(left, top, right, bottom, layerColor,
                        android.graphics.Color.parseColor(layer.secondaryColorHex), Shader.TileMode.CLAMP)
                } else paint.color = layerColor
                if (layer.shape == "line") {
                    paint.strokeWidth = maxOf(2f, width * 0.006f)
                    canvas.drawLine(left, bounds.centerY(), right, bounds.centerY(), paint)
                }
                else if (layer.shape == "circle") canvas.drawOval(bounds, paint)
                else canvas.drawRoundRect(bounds, width * .008f, width * .008f, paint)
            }
            LayerType.IMAGE -> {
                val image = runCatching {
                    layer.imageUri?.let { uri ->
                        context.contentResolver.openInputStream(Uri.parse(uri))?.use { BitmapFactory.decodeStream(it) }
                    }
                }.getOrNull()
                if (image != null) {
                    val src = Rect(0, 0, image.width, image.height)
                    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { alpha = (layer.opacity.coerceIn(0f, 1f) * 255).toInt() }
                    if (layer.imageFit == "contain") {
                        val scale = minOf(bounds.width() / src.width(), bounds.height() / src.height())
                        val targetW = src.width() * scale
                        val targetH = src.height() * scale
                        val target = RectF(bounds.centerX() - targetW / 2, bounds.centerY() - targetH / 2,
                            bounds.centerX() + targetW / 2, bounds.centerY() + targetH / 2)
                        canvas.drawBitmap(image, src, target, paint)
                    } else {
                        val scale = maxOf(bounds.width() / src.width(), bounds.height() / src.height())
                        val cropW = bounds.width() / scale
                        val cropH = bounds.height() / scale
                        val crop = Rect(
                            ((src.width() - cropW) / 2).toInt(),
                            ((src.height() - cropH) / 2).toInt(),
                            ((src.width() + cropW) / 2).toInt(),
                            ((src.height() + cropH) / 2).toInt()
                        )
                        canvas.drawBitmap(image, crop, bounds, paint)
                    }
                    image.recycle()
                }
            }
            LayerType.TEXT -> {
                val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = layerColor
                    alpha = (layer.opacity.coerceIn(0f, 1f) * 255).toInt()
                    textSize = width * layer.fontSize
                    typeface = Typeface.create(layer.fontFamily, if (layer.fontWeight >= 700) Typeface.BOLD else Typeface.NORMAL)
                }
                val alignment = when (layer.textAlign) {
                    "center" -> Layout.Alignment.ALIGN_CENTER
                    "end" -> Layout.Alignment.ALIGN_OPPOSITE
                    else -> Layout.Alignment.ALIGN_NORMAL
                }
                val layout = StaticLayout.Builder.obtain(layer.text, 0, layer.text.length, textPaint, maxOf(1, bounds.width().toInt()))
                    .setAlignment(alignment)
                    .setIncludePad(false)
                    .setMaxLines(maxOf(1, (bounds.height() / (textPaint.textSize * 1.2f)).toInt()))
                    .build()
                canvas.save()
                canvas.translate(left, top)
                layout.draw(canvas)
                canvas.restore()
            }
        }
        canvas.restore()
    }
}
