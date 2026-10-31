package com.wisdomtower.post

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Local storage for the current post and the brand kit. Logo files are copied into app storage so they never go stale. */
object PostStore {
    private const val PREFS = "wisdom_tower_post_v2"

    private fun prefs(context: Context) = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun hasSavedDoc(context: Context): Boolean = prefs(context).contains("doc")

    fun loadDoc(context: Context): PostDoc =
        runCatching { prefs(context).getString("doc", null)?.let(PostCodec::decodeDoc) }.getOrNull() ?: PostDoc()

    fun loadBrand(context: Context): PostBrand =
        runCatching { prefs(context).getString("brand", null)?.let(PostCodec::decodeBrand) }.getOrNull() ?: PostBrand()

    fun save(context: Context, doc: PostDoc, brand: PostBrand) {
        prefs(context).edit()
            .putString("doc", PostCodec.encodeDoc(doc))
            .putString("brand", PostCodec.encodeBrand(brand))
            .apply()
    }

    /** Copies a picked photo into app storage so it never goes stale. Multiple photos may be kept because a design can use both a full-canvas background and a template photo. */
    fun importPhoto(context: Context, uri: Uri): String? {
        val dir = File(context.filesDir, "photos")
        dir.mkdirs()
        val out = File(dir, "photo_" + System.currentTimeMillis() + "_" + kotlin.random.Random.nextInt(1000, 9999) + ".img")
        val copied = runCatching {
            context.contentResolver.openInputStream(uri)?.use { input ->
                out.outputStream().use { input.copyTo(it) }
            } != null
        }.getOrDefault(false)
        if (!copied) return null
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(out.absolutePath, bounds)
        if (bounds.outWidth <= 0) {
            out.delete()
            return null
        }
        return Uri.fromFile(out).toString()
    }

    /** Imports one custom post icon into app-private storage. Only PNG, WebP and JPEG are accepted. */
    fun importIcon(context: Context, uri: Uri): String? {
        var displayName = "icon.png"
        runCatching {
            context.contentResolver.query(uri, arrayOf(android.provider.OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) displayName = cursor.getString(0) ?: displayName
            }
        }
        val extension = displayName.substringAfterLast('.', "").lowercase()
        if (extension !in setOf("png", "webp", "jpg", "jpeg")) return null
        val directory = File(context.filesDir, "icons").apply { mkdirs() }
        val target = File(directory, "icon_${System.currentTimeMillis()}_${kotlin.random.Random.nextInt(1000, 9999)}.$extension")
        val copied = runCatching {
            context.contentResolver.openInputStream(uri)?.use { input -> target.outputStream().use { output -> input.copyTo(output) } } != null
        }.getOrDefault(false)
        if (!copied) return null
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(target.absolutePath, bounds)
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) {
            target.delete()
            return null
        }
        return target.absolutePath
    }

    /** Copies the picked image into app storage. Returns the stored path, or null if it is not a readable image. */
    fun importLogo(context: Context, uri: Uri): String? {
        val dir = File(context.filesDir, "brand")
        dir.mkdirs()
        val out = File(dir, "logo.img")
        val copied = runCatching {
            context.contentResolver.openInputStream(uri)?.use { input ->
                out.outputStream().use { input.copyTo(it) }
            } != null
        }.getOrDefault(false)
        if (!copied) return null
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(out.absolutePath, bounds)
        if (bounds.outWidth <= 0) {
            out.delete()
            return null
        }
        return out.absolutePath
    }
}

object PostExport {
    data class Saved(val uris: List<Uri>, val overflow: Map<PostSize, List<String>>)

    /** Renders each size from the same document and saves it to Pictures/Wisdom Tower Post. */
    fun save(context: Context, doc: PostDoc, brand: PostBrand, sizes: List<PostSize>, png: Boolean): Saved {
        val stamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val extension = if (png) "png" else "jpg"
        val uris = mutableListOf<Uri>()
        val overflow = linkedMapOf<PostSize, List<String>>()
        for (size in sizes) {
            val result = PostRenderer.render(context, doc.copy(size = size), brand)
            if (result.overflow.isNotEmpty()) overflow[size] = result.overflow
            val name = "WisdomTower_" + size.label.replace(':', 'x') + "_" + stamp + "." + extension
            uris.add(writeImage(context, result.bitmap, name, png))
            result.bitmap.recycle()
        }
        return Saved(uris, overflow)
    }

    private fun writeImage(context: Context, bitmap: Bitmap, name: String, png: Boolean): Uri {
        val format = if (png) Bitmap.CompressFormat.PNG else Bitmap.CompressFormat.JPEG
        val quality = if (png) 100 else 95
        val mime = if (png) "image/png" else "image/jpeg"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, name)
                put(MediaStore.Images.Media.MIME_TYPE, mime)
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Wisdom Tower Post")
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
            val resolver = context.contentResolver
            val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                ?: throw IllegalStateException("Could not create the image file")
            resolver.openOutputStream(uri)?.use { out ->
                if (!bitmap.compress(format, quality, out)) throw IllegalStateException("Image encoding failed")
            } ?: throw IllegalStateException("Could not open the image file")
            values.clear()
            values.put(MediaStore.Images.Media.IS_PENDING, 0)
            resolver.update(uri, values, null, null)
            return uri
        }
        val folder = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Wisdom Tower Post")
        folder.mkdirs()
        val file = File(folder, name)
        FileOutputStream(file).use { bitmap.compress(format, quality, it) }
        return FileProvider.getUriForFile(context, context.packageName + ".fileprovider", file)
    }
}
