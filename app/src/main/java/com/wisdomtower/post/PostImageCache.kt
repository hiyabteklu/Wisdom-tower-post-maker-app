package com.wisdomtower.post

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.LruCache
import java.io.ByteArrayInputStream
import java.io.InputStream
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * In-memory LRU bitmap cache so photo, icon and cutout bitmaps are decoded once instead of on every slider drag.
 * Also provides fast downscaled 2-pass box blur cached by (key, blur) without RenderScript (TEMPLATE_REDESIGN_PLAN.md 3.4).
 */
object PostImageCache {
    class Lease(val bitmap: Bitmap) {
        fun release() {
            // Kept in LRU cache; no-op on release so callers never use a recycled bitmap.
        }
    }

    private val cache: LruCache<String, Bitmap> = object : LruCache<String, Bitmap>(24 * 1024) {
        override fun sizeOf(key: String, value: Bitmap): Int = max(1, value.byteCount / 1024)
    }

    fun decode(key: String, maxSide: Int, open: () -> InputStream?): Lease? {
        val target = maxSide.coerceAtLeast(32)
        val cacheKey = "$key@$target"
        synchronized(this) {
            cache.get(cacheKey)?.let { if (!it.isRecycled) return Lease(it) }
        }
        return try {
            val bytes = open()?.use { it.readBytes() } ?: return null
            if (bytes.isEmpty()) return null
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeStream(ByteArrayInputStream(bytes), null, bounds)
            if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null
            var sample = 1
            while (max(bounds.outWidth, bounds.outHeight) / (sample * 2) >= target) {
                sample *= 2
            }
            val opts = BitmapFactory.Options().apply { inSampleSize = sample }
            val decoded = BitmapFactory.decodeStream(ByteArrayInputStream(bytes), null, opts) ?: return null
            synchronized(this) {
                cache.put(cacheKey, decoded)
            }
            Lease(decoded)
        } catch (_: Throwable) {
            null
        }
    }

    fun get(context: Context, uriString: String?, maxDim: Int = 1600): Bitmap? {
        if (uriString.isNullOrBlank()) return null
        val target = maxDim.coerceAtLeast(64)
        val key = "$uriString@$target"
        synchronized(this) {
            cache.get(key)?.let { if (!it.isRecycled) return it }
        }
        return try {
            val uri = Uri.parse(uriString)
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            context.contentResolver.openInputStream(uri)?.use {
                BitmapFactory.decodeStream(it, null, bounds)
            }
            if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null
            var sample = 1
            while (bounds.outWidth / sample > target || bounds.outHeight / sample > target) {
                sample *= 2
            }
            val opts = BitmapFactory.Options().apply { inSampleSize = sample }
            val decoded = context.contentResolver.openInputStream(uri)?.use {
                BitmapFactory.decodeStream(it, null, opts)
            }
            if (decoded != null) {
                synchronized(this) {
                    cache.put(key, decoded)
                }
            }
            decoded
        } catch (_: Throwable) {
            null
        }
    }

    /**
     * Fast downscaled 2-pass sliding box blur cached by (key, blur).
     * Works identically on all API levels without RenderScript.
     */
    fun blurred(key: String, src: Bitmap, blur: Int): Bitmap {
        val clamped = blur.coerceIn(0, 100)
        if (clamped <= 0) return src
        val cacheKey = "blur:$key@$clamped@${src.width}x${src.height}"
        synchronized(this) {
            cache.get(cacheKey)?.let { if (!it.isRecycled) return it }
        }

        val scale = 4
        val sw = max(16, (src.width / scale.toFloat()).roundToInt())
        val sh = max(16, (src.height / scale.toFloat()).roundToInt())
        val small = Bitmap.createScaledBitmap(src, sw, sh, true).copy(Bitmap.Config.ARGB_8888, true)
        val radius = max(1, (clamped * 0.22f).roundToInt())

        boxBlurPass(small, radius)
        boxBlurPass(small, radius)

        synchronized(this) {
            cache.put(cacheKey, small)
        }
        return small
    }

    private fun boxBlurPass(bitmap: Bitmap, radius: Int) {
        val w = bitmap.width
        val h = bitmap.height
        if (w <= 2 || h <= 2 || radius <= 0) return
        val pixels = IntArray(w * h)
        val temp = IntArray(w * h)
        bitmap.getPixels(pixels, 0, w, 0, 0, w, h)
        val div = 2 * radius + 1

        for (y in 0 until h) {
            var aSum = 0
            var rSum = 0
            var gSum = 0
            var bSum = 0
            val rowOffset = y * w
            for (i in -radius..radius) {
                val p = pixels[rowOffset + i.coerceIn(0, w - 1)]
                aSum += (p ushr 24) and 0xFF
                rSum += (p ushr 16) and 0xFF
                gSum += (p ushr 8) and 0xFF
                bSum += p and 0xFF
            }
            for (x in 0 until w) {
                temp[rowOffset + x] =
                    ((aSum / div) shl 24) or ((rSum / div) shl 16) or ((gSum / div) shl 8) or (bSum / div)
                val outIdx = (x - radius).coerceIn(0, w - 1)
                val inIdx = (x + radius + 1).coerceIn(0, w - 1)
                val pOut = pixels[rowOffset + outIdx]
                val pIn = pixels[rowOffset + inIdx]
                aSum += ((pIn ushr 24) and 0xFF) - ((pOut ushr 24) and 0xFF)
                rSum += ((pIn ushr 16) and 0xFF) - ((pOut ushr 16) and 0xFF)
                gSum += ((pIn ushr 8) and 0xFF) - ((pOut ushr 8) and 0xFF)
                bSum += (pIn and 0xFF) - (pOut and 0xFF)
            }
        }

        for (x in 0 until w) {
            var aSum = 0
            var rSum = 0
            var gSum = 0
            var bSum = 0
            for (i in -radius..radius) {
                val p = temp[i.coerceIn(0, h - 1) * w + x]
                aSum += (p ushr 24) and 0xFF
                rSum += (p ushr 16) and 0xFF
                gSum += (p ushr 8) and 0xFF
                bSum += p and 0xFF
            }
            for (y in 0 until h) {
                pixels[y * w + x] =
                    ((aSum / div) shl 24) or ((rSum / div) shl 16) or ((gSum / div) shl 8) or (bSum / div)
                val outIdx = (y - radius).coerceIn(0, h - 1)
                val inIdx = (y + radius + 1).coerceIn(0, h - 1)
                val pOut = temp[outIdx * w + x]
                val pIn = temp[inIdx * w + x]
                aSum += ((pIn ushr 24) and 0xFF) - ((pOut ushr 24) and 0xFF)
                rSum += ((pIn ushr 16) and 0xFF) - ((pOut ushr 16) and 0xFF)
                gSum += ((pIn ushr 8) and 0xFF) - ((pOut ushr 8) and 0xFF)
                bSum += (pIn and 0xFF) - (pOut and 0xFF)
            }
        }

        bitmap.setPixels(pixels, 0, w, 0, 0, w, h)
    }

    fun clear() {
        synchronized(this) {
            cache.evictAll()
        }
    }
}
