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

object PostStore {
    private const val PREFS = "wisdom_tower_post_v2"
    private fun prefs(context: Context) = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun loadDoc(context: Context): PostDoc =
        runCatching { prefs(context).getString("doc", null)?.let(PostCodec::decodeDoc) }.getOrNull() ?: PostDoc()

    fun loadBrand(context: Context): PostBrand =
        runCatching { prefs(context).getString("brand", null)?.let(PostCodec::decodeBrand) }.getOrNull() ?: PostBrand()

    fun save(context: Context, doc: PostDoc, brand: PostBrand) {
        prefs(context).edit().putString("doc", PostCodec.encodeDoc(doc))
            .putString("brand", PostCodec.encodeBrand(brand)).apply()
    }

    data class SaveOptions(val sizes: Set<PostSize>, val png: Boolean, val fourK: Boolean)
    private fun batchPrefs(context: Context) = context.getSharedPreferences("wisdom_tower_post_batch", Context.MODE_PRIVATE)

    fun loadSaveOptions(context: Context, defaultSize: PostSize): SaveOptions {
        val p = batchPrefs(context)
        val saved = p.getStringSet("save_sizes", null)
        val sizes = saved?.mapNotNull { runCatching { PostSize.valueOf(it) }.getOrNull() }?.toSet()
        return SaveOptions(sizes?.takeIf { it.isNotEmpty() } ?: setOf(defaultSize),
            p.getBoolean("save_png", true), p.getBoolean("save_4k", false))
    }

    fun saveSaveOptions(context: Context, options: SaveOptions) {
        batchPrefs(context).edit().putStringSet("save_sizes", options.sizes.map { it.name }.toSet())
            .putBoolean("save_png", options.png).putBoolean("save_4k", options.fourK).apply()
    }

    fun importPhoto(context: Context, uri: Uri): String? {
        val dir = File(context.filesDir, "photos")
        dir.mkdirs()
        val out = File(dir, "photo_" + System.currentTimeMillis() + "_" + kotlin.random.Random.nextInt(1000, 9999) + ".img")
        val copied = runCatching {
            context.contentResolver.openInputStream(uri)?.use { input -> out.outputStream().use { input.copyTo(it) } } != null
        }.getOrDefault(false)
        if (!copied) return null
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(out.absolutePath, bounds)
        if (bounds.outWidth <= 0) { out.delete(); return null }
        return Uri.fromFile(out).toString()
    }

    fun deleteImportedPhoto(context: Context, storedUri: String) {
        runCatching {
            val uri = Uri.parse(storedUri)
            if (uri.scheme == "file") File(uri.path.orEmpty()).takeIf {
                it.parentFile == File(context.filesDir, "photos")
            }?.delete()
        }
    }

    fun importLogo(context: Context, uri: Uri): String? {
        val dir = File(context.filesDir, "brand")
        dir.mkdirs()
        val out = File(dir, "logo.img")
        val copied = runCatching {
            context.contentResolver.openInputStream(uri)?.use { input -> out.outputStream().use { input.copyTo(it) } } != null
        }.getOrDefault(false)
        if (!copied) return null
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(out.absolutePath, bounds)
        if (bounds.outWidth <= 0) { out.delete(); return null }
        return out.absolutePath
    }
}

object PostExport {
    data class Saved(val uris: List<Uri>, val overflow: Map<PostSize, List<String>>)

    fun save(context: Context, doc: PostDoc, brand: PostBrand, sizes: List<PostSize>, png: Boolean, fourK: Boolean = false): Saved {
        val stamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        return saveOne(context, doc, brand, sizes, png, fourK, stamp)
    }

    /**
     * A short, clean file name for the post, 1 to 3 words, lower case, joined with hyphens ("exam-tomorrow"), so a person or an AI
     * reading a folder of posters can tell which is which. No app name, no size, no date. The words come from the post's own text:
     * the headline first, then the eyebrow, the number and the body, then any other text, and last the template's name.
     */
    fun fileStem(doc: PostDoc): String {
        val preferred = listOf(PostSlots.HEADLINE, PostSlots.EYEBROW, PostSlots.NUMBER, PostSlots.BODY)
        val source = preferred.asSequence()
            .mapNotNull { slot -> doc.texts[slot]?.trim()?.takeIf { it.isNotEmpty() } }
            .firstOrNull()
            ?: doc.texts.values.map { it.trim() }.firstOrNull { it.isNotEmpty() }
            ?: postTemplate(doc.templateId).name
        val words = Regex("[\\p{L}\\p{N}]+")
            .findAll(source.replace("'", "").replace("\u2019", ""))
            .map { it.value }
            .take(3)
            .toList()
        val stem = words.joinToString("-").lowercase(Locale.ROOT).take(40).trim('-')
        return stem.ifEmpty { "post" }
    }

    /**
     * stamp and tag are no longer part of the file name (see fileStem); the parameters stay so the callers do not change.
     * When more than one size is saved in the same call, the size is added after the name ("exam-tomorrow-9x16") so the files differ.
     * Two different posts with the same words get the same name: on Android 10 and newer the phone adds " (1)" to the second.
     */
    fun saveOne(
        context: Context, doc: PostDoc, brand: PostBrand, sizes: List<PostSize>,
        png: Boolean, fourK: Boolean, stamp: String, tag: String = "", subfolder: String? = null
    ): Saved {
        val extension = if (png) "png" else "jpg"
        val uris = mutableListOf<Uri>()
        val overflow = linkedMapOf<PostSize, List<String>>()
        val stem = fileStem(doc)
        for (size in sizes) {
            val result = PostRenderer.render(context, doc.copy(size = size), brand, if (fourK) 2f else 1f)
            try {
                if (result.overflow.isNotEmpty()) overflow[size] = result.overflow
                val name = stem + (if (sizes.size > 1) "-" + size.label.replace(':', 'x') else "") + "." + extension
                uris.add(writeImage(context, result.bitmap, name, png, subfolder))
            } finally {
                result.bitmap.recycle()
            }
        }
        return Saved(uris, overflow)
    }

    private fun writeImage(context: Context, bitmap: Bitmap, name: String, png: Boolean, subfolder: String? = null): Uri {
        val format = if (png) Bitmap.CompressFormat.PNG else Bitmap.CompressFormat.JPEG
        val quality = if (png) 100 else 95
        val mime = if (png) "image/png" else "image/jpeg"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val relative = Environment.DIRECTORY_PICTURES + "/Wisdom Tower Post" +
                (subfolder?.takeIf { it.isNotBlank() }?.let { "/$it" } ?: "")
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, name)
                put(MediaStore.Images.Media.MIME_TYPE, mime)
                put(MediaStore.Images.Media.RELATIVE_PATH, relative)
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
            val resolver = context.contentResolver
            val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                ?: throw IllegalStateException("Could not create the image file")
            try {
                resolver.openOutputStream(uri)?.use { out ->
                    if (!bitmap.compress(format, quality, out)) throw IllegalStateException("Image encoding failed")
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
        val root = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Wisdom Tower Post")
        val folder = if (subfolder.isNullOrBlank()) root else File(root, subfolder)
        folder.mkdirs()
        val file = File(folder, name)
        FileOutputStream(file).use {
            if (!bitmap.compress(format, quality, it)) throw IllegalStateException("Image encoding failed")
        }
        return FileProvider.getUriForFile(context, context.packageName + ".fileprovider", file)
    }
}
