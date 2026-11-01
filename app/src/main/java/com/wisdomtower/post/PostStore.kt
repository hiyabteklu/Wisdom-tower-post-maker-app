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

    fun loadDoc(context: Context): PostDoc =
        runCatching { prefs(context).getString("doc", null)?.let(PostCodec::decodeDoc) }.getOrNull() ?: PostDoc()

    fun loadBrand(context: Context): PostBrand =
        runCatching { prefs(context).getString("brand", null)?.let(PostCodec::decodeBrand) }.getOrNull() ?: PostBrand()

    fun save(context: Context, doc: PostDoc, brand: PostBrand, sizes: List<PostSize>, png: Boolean, fourK: Boolean = false): Saved {
        val stamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        return saveOne(context, doc, brand, sizes, png, fourK, stamp)
    }

    fun saveOne(
        context: Context, doc: PostDoc, brand: PostBrand, sizes: List<PostSize>,
        png: Boolean, fourK: Boolean, stamp: String, tag: String = "", subfolder: String? = null
    ): Saved {
        val extension = if (png) "png" else "jpg"
        val uris = mutableListOf<Uri>()
        val overflow = linkedMapOf<PostSize, List<String>>()
        for (size in sizes) {
            val result = PostRenderer.render(context, doc.copy(size = size), brand, if (fourK) 2f else 1f)
            try {
                if (result.overflow.isNotEmpty()) overflow[size] = result.overflow
                val name = "WisdomTower_" + size.label.replace(':', 'x') + (if (fourK) "_4K" else "") + "_" + stamp +
                    (if (tag.isNotEmpty()) "_$tag" else "") + "." + extension
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
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, name)
                put(MediaStore.Images.Media.MIME_TYPE, mime)
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Wisdom Tower Post" + (subfolder?.let { "/$it" } ?: ""))
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
        val root = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Wisdom Tower Post")
        val folder = if (subfolder.isNullOrBlank()) root else File(root, subfolder)
        folder.mkdirs()
        val file = File(folder, name)
        FileOutputStream(file).use { bitmap.compress(format, quality, it) }
        return FileProvider.getUriForFile(context, context.packageName + ".fileprovider", file)
    }
}
