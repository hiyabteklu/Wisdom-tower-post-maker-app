package com.wisdomtower.post

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Disk-backed preview cache. The key changes whenever the draft, brand or output size changes. */
object PostThumbnailCache {
    private fun file(context: android.content.Context, key: String): File {
        val safe = key.map { if (it.isLetterOrDigit() || it == '-') it else '_' }.joinToString("")
        val dir = File(context.cacheDir, "template-thumbnails").apply { mkdirs() }
        return File(dir, "$safe.webp")
    }

    suspend fun getOrRender(
        context: android.content.Context,
        doc: PostDoc,
        brand: PostBrand,
        width: Int,
        version: Long
    ): Bitmap? {
        val key = "${doc.templateId}_${doc.size.name}_${brand.hashCode()}_${version}_${width}"
        val cachedFile = file(context, key)
        val cached = withContext(Dispatchers.IO) {
            if (cachedFile.exists()) BitmapFactory.decodeFile(cachedFile.absolutePath) else null
        }
        if (cached != null) return cached

        val made = withContext(Dispatchers.Default) {
            runCatching { PostRenderer.thumbnail(context, doc, brand, width).bitmap }.getOrNull()
        } ?: return null
        withContext(Dispatchers.IO) {
            runCatching {
                cachedFile.outputStream().use { made.compress(Bitmap.CompressFormat.WEBP, 82, it) }
            }
        }
        return made
    }
}
