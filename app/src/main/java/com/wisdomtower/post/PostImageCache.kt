package com.wisdomtower.post

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.LruCache
import java.io.InputStream
import java.util.concurrent.ConcurrentHashMap

/**
 * Keeps recently decoded pictures (the template photo, the background photo, the cut-out subject and placed icons), so the editor
 * preview and the template cards do not decode the same file again for every redraw. Before this, every redraw while dragging a
 * slider or an icon opened and decoded the photo from storage again.
 *
 * A picture that came from this cache is shared: nobody may recycle it or draw into it. Always call Picture.release() when done;
 * it frees a private decode and leaves a shared one alone. A decode that would be very large (a 4K export) is never kept, so
 * exports cannot fill the memory.
 */
object PostImageCache {
    /** A decoded picture larger than this (in bytes) is not kept. */
    private const val MAX_ENTRY_BYTES = 20L * 1024 * 1024

    class Picture(val bitmap: Bitmap, private val shared: Boolean) {
        /** Frees the pixels of a private decode. A shared (cached) picture is left alone. */
        fun release() {
            if (!shared) bitmap.recycle()
        }
    }

    private fun budget(): Int =
        (Runtime.getRuntime().maxMemory() / 8L).coerceIn(8L * 1024 * 1024, 48L * 1024 * 1024).toInt()

    private val cache = object : LruCache<String, Bitmap>(budget()) {
        override fun sizeOf(key: String, value: Bitmap): Int = value.byteCount
    }

    /** Width and height of each picture, so the file header is read only once. */
    private val sizes = ConcurrentHashMap<String, IntArray>()

    /**
     * Decodes the picture behind key, no smaller than target pixels on its shorter side (it is sampled down by whole powers of two).
     * key must change when the file changes. open gives a fresh stream each time it is called. Returns null when it cannot be read.
     */
    fun decode(key: String, target: Int, open: () -> InputStream?): Picture? {
        val dims = sizes[key] ?: run {
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            open()?.use { BitmapFactory.decodeStream(it, null, bounds) }
            if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null
            intArrayOf(bounds.outWidth, bounds.outHeight).also { sizes[key] = it }
        }
        var sample = 1
        while (dims[0] / (sample * 2) >= target && dims[1] / (sample * 2) >= target) sample *= 2
        val bytes = ((dims[0] + sample - 1) / sample).toLong() * ((dims[1] + sample - 1) / sample) * 4L
        val keep = bytes <= MAX_ENTRY_BYTES
        val cacheKey = key + "#" + sample
        if (keep) {
            val hit = cache.get(cacheKey)
            if (hit != null && !hit.isRecycled) return Picture(hit, true)
        }
        val options = BitmapFactory.Options().apply { inSampleSize = sample }
        val bitmap = open()?.use { BitmapFactory.decodeStream(it, null, options) } ?: return null
        if (keep) cache.put(cacheKey, bitmap)
        return Picture(bitmap, keep)
    }
}
