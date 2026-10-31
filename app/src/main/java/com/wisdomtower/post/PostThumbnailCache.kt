package com.wisdomtower.post

import android.graphics.Bitmap
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
    ): Bitmap? = withContext(Dispatchers.IO) {
        val key = "${doc.templateId}_${doc.size.name}_${brand.hashCode()}_${version}_${width}"
        val cached = file(context, key)
        if (cached.exists()) {
            val decoded = android.graphics.BitmapFactory.decodeFile(cached.absolutePath)
            if (decoded != null) return@withContext decoded
            cached.delete()
        }
        val made = runCatching {
            PostRenderer.thumbnail(context, doc, brand, width).bitmap
        }.getOrNull() ?: return@withContext null
        runCatching { cached.outputStream().use { made.compress(Bitmap.CompressFormat.WEBP_LOSSY, 82, it) } }
        made
    }
}
