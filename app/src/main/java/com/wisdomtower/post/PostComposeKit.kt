package com.wisdomtower.post

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BlurMaskFilter
import android.graphics.Camera
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.PorterDuffXfermode
import android.graphics.RadialGradient
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.text.TextPaint
import android.util.LruCache
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Showcase Composition Kit (docs/SHOWCASE_TEMPLATES_PLAN.md Phases P1 & P3):
 * - P1: Transparent brand mark extraction (strips baked dark background from new_logo.png),
 *       auto-contrast luminance sampling, 12 anchor placements, 5 mark styles, keep-out collision avoidance.
 * - P3: 3D tilted device frames, cutout stack with rim glow/sticker edge & default silhouette,
 *       special text FX (GLITCH_SPLIT, CURVE_ARC, VERTICAL, STRIKE_REVEAL, OUTLINE_STACK),
 *       and 3D/abstract showcase decor shapes.
 */
object PostComposeKit {

    private val cleanLogoCache = LruCache<String, Bitmap>(12)
    private val tintedLogoCache = LruCache<String, Bitmap>(32)

    /**
     * Strips baked solid/dark background pixels from a logo bitmap using corner flood/color-distance alpha keying,
     * then trims excess transparent padding so the mark scales predictably.
     */
    @Synchronized
    fun obtainCleanLogo(raw: Bitmap, cacheKey: String): Bitmap {
        cleanLogoCache.get(cacheKey)?.takeIf { !it.isRecycled }?.let { return it }

        if (raw.isRecycled || raw.width <= 4 || raw.height <= 4) return raw

        val maxLogoSide = 320
        val src: Bitmap = if (raw.width > maxLogoSide || raw.height > maxLogoSide || raw.config != Bitmap.Config.ARGB_8888) {
            val scale = min(1f, maxLogoSide.toFloat() / max(raw.width, raw.height).toFloat())
            val sw = (raw.width * scale).roundToInt().coerceAtLeast(8)
            val sh = (raw.height * scale).roundToInt().coerceAtLeast(8)
            val bmp = Bitmap.createBitmap(sw, sh, Bitmap.Config.ARGB_8888)
            val c = Canvas(bmp)
            val p = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
            c.drawBitmap(raw, null, RectF(0f, 0f, sw.toFloat(), sh.toFloat()), p)
            bmp
        } else {
            raw
        }

        val w = src.width
        val h = src.height
        val pixels = IntArray(w * h)
        src.getPixels(pixels, 0, w, 0, 0, w, h)

        // Sample the 4 corners (3x3 patches) to detect baked background color
        val cornerSamples = mutableListOf<Int>()
        val patch = min(4, min(w, h) / 6).coerceAtLeast(1)
        for (cy in 0 until patch) {
            for (cx in 0 until patch) {
                cornerSamples.add(pixels[cy * w + cx])
                cornerSamples.add(pixels[cy * w + (w - 1 - cx)])
                cornerSamples.add(pixels[(h - 1 - cy) * w + cx])
                cornerSamples.add(pixels[(h - 1 - cy) * w + (w - 1 - cx)])
            }
        }

        val avgAlpha = cornerSamples.map { Color.alpha(it) }.average()
        val out = IntArray(w * h)

        if (avgAlpha > 200.0) {
            // The logo has an opaque background box (like new_logo.png's dark square).
            val bgR = cornerSamples.map { Color.red(it) }.average().toFloat()
            val bgG = cornerSamples.map { Color.green(it) }.average().toFloat()
            val bgB = cornerSamples.map { Color.blue(it) }.average().toFloat()

            val lowThresh = 26f
            val highThresh = 78f

            for (i in pixels.indices) {
                val c = pixels[i]
                val a = Color.alpha(c)
                if (a == 0) {
                    out[i] = 0
                    continue
                }
                val r = Color.red(c)
                val g = Color.green(c)
                val b = Color.blue(c)
                val dr = r - bgR
                val dg = g - bgG
                val db = b - bgB
                val dist = sqrt(dr * dr + dg * dg + db * db)

                // Also check brightness lift for glowing gold/cyan marks on dark backgrounds
                val lumDiff = abs((0.299f * r + 0.587f * g + 0.114f * b) - (0.299f * bgR + 0.587f * bgG + 0.114f * bgB))
                val metric = max(dist, lumDiff * 1.35f)

                when {
                    metric <= lowThresh -> out[i] = 0
                    metric >= highThresh -> out[i] = c
                    else -> {
                        val t = ((metric - lowThresh) / (highThresh - lowThresh)).coerceIn(0f, 1f)
                        val smoothA = (a * t * t * (3f - 2f * t)).roundToInt().coerceIn(0, 255)
                        out[i] = Color.argb(smoothA, r, g, b)
                    }
                }
            }
        } else {
            System.arraycopy(pixels, 0, out, 0, pixels.size)
        }

        // Find non-transparent bounding box to trim blank borders
        var minX = w
        var minY = h
        var maxX = -1
        var maxY = -1
        for (y in 0 until h) {
            val row = y * w
            for (x in 0 until w) {
                if (Color.alpha(out[row + x]) > 18) {
                    if (x < minX) minX = x
                    if (x > maxX) maxX = x
                    if (y < minY) minY = y
                    if (y > maxY) maxY = y
                }
            }
        }

        val cleaned = if (maxX > minX && maxY > minY) {
            val pad = max(2, min(w, h) / 40)
            val cropL = (minX - pad).coerceAtLeast(0)
            val cropT = (minY - pad).coerceAtLeast(0)
            val cropR = (maxX + pad).coerceAtMost(w - 1)
            val cropB = (maxY + pad).coerceAtMost(h - 1)
            val cw = (cropR - cropL + 1).coerceAtLeast(1)
            val ch = (cropB - cropT + 1).coerceAtLeast(1)
            val cropped = Bitmap.createBitmap(cw, ch, Bitmap.Config.ARGB_8888)
            for (y in 0 until ch) {
                cropped.setPixels(out, (cropT + y) * w + cropL, cw, 0, y, cw, 1)
            }
            cropped
        } else {
            Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888).apply {
                setPixels(out, 0, w, 0, 0, w, h)
            }
        }

        cleanLogoCache.put(cacheKey, cleaned)
        return cleaned
    }

    /**
     * Produces a crisp monochrome or accent-tinted version of the clean transparent logo while preserving anti-aliased alpha.
     */
    @Synchronized
    fun obtainTintedLogo(clean: Bitmap, baseKey: String, targetColor: Int): Bitmap {
        val key = "$baseKey|${Integer.toHexString(targetColor)}"
        tintedLogoCache.get(key)?.takeIf { !it.isRecycled }?.let { return it }

        val w = clean.width.coerceAtLeast(1)
        val h = clean.height.coerceAtLeast(1)
        val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val c = Canvas(bmp)
        val p = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG).apply {
            colorFilter = PorterDuffColorFilter(targetColor, PorterDuff.Mode.SRC_IN)
        }
        c.drawBitmap(clean, 0f, 0f, p)
        tintedLogoCache.put(key, bmp)
        return bmp
    }

    /**
     * Samples a 5-point grid in [rect] (given in logical canvas coordinates 0..canvasW, 0..canvasH)
     * mapped accurately onto [canvasBitmap] (which may be scaled down in preview).
     */
    fun sampleRectLuminance(
        canvasBitmap: Bitmap,
        rect: RectF,
        canvasW: Float = canvasBitmap.width.toFloat(),
        canvasH: Float = canvasBitmap.height.toFloat()
    ): Float {
        if (canvasBitmap.isRecycled) return 0.1f
        val bw = canvasBitmap.width
        val bh = canvasBitmap.height
        if (bw <= 0 || bh <= 0 || canvasW <= 0f || canvasH <= 0f) return 0.1f

        val sx = bw / canvasW
        val sy = bh / canvasH
        val points = arrayOf(
            rect.centerX() to rect.centerY(),
            (rect.left * 0.7f + rect.right * 0.3f) to (rect.top * 0.7f + rect.bottom * 0.3f),
            (rect.left * 0.3f + rect.right * 0.7f) to (rect.top * 0.7f + rect.bottom * 0.3f),
            (rect.left * 0.7f + rect.right * 0.3f) to (rect.top * 0.3f + rect.bottom * 0.7f),
            (rect.left * 0.3f + rect.right * 0.7f) to (rect.top * 0.3f + rect.bottom * 0.7f)
        )
        var sum = 0f
        var count = 0
        for ((lx, ly) in points) {
            val px = (lx * sx).toInt().coerceIn(0, bw - 1)
            val py = (ly * sy).toInt().coerceIn(0, bh - 1)
            val c = runCatching { canvasBitmap.getPixel(px, py) }.getOrNull() ?: continue
            sum += relativeLuminance(c)
            count++
        }
        return if (count > 0) sum / count else 0.1f
    }

    fun relativeLuminance(color: Int): Float {
        fun ch(v: Int): Float {
            val s = v / 255f
            return if (s <= 0.03928f) s / 12.92f else ((s + 0.055f) / 1.055f).pow(2.4f)
        }
        return 0.2126f * ch(Color.red(color)) + 0.7152f * ch(Color.green(color)) + 0.0722f * ch(Color.blue(color))
    }

    fun resolveLogoAnchor(doc: PostDoc, brand: PostBrand, layout: TemplateLayout): LogoAnchor {
        if (!doc.showLogo) return LogoAnchor.NONE
        if (doc.logoAnchor != LogoAnchor.AUTO) return doc.logoAnchor
        if (brand.logoAnchor != LogoAnchor.AUTO) return brand.logoAnchor
        if (doc.templateId.startsWith("study-")) return LogoAnchor.BOTTOM_RIGHT
        return layout.logoAnchor
    }

    fun resolveLogoStyle(doc: PostDoc, brand: PostBrand): LogoStyle =
        doc.logoStyle ?: brand.logoStyle

    fun computeLogoRect(
        doc: PostDoc,
        brand: PostBrand,
        layout: TemplateLayout,
        w: Float,
        h: Float
    ): RectF {
        val unit = min(w, h)
        val anchor = resolveLogoAnchor(doc, brand, layout)
        val style = resolveLogoStyle(doc, brand)
        val baseScale = (brand.logoScale * doc.logoSize).coerceIn(0.45f, 2.6f)
        val markSize = when (anchor) {
            LogoAnchor.LARGE_WATERMARK -> unit * 0.42f * baseScale
            LogoAnchor.CORNER_WATERMARK -> unit * 0.095f * baseScale
            LogoAnchor.STAMP -> unit * 0.115f * baseScale
            LogoAnchor.INLINE_FOOTER -> unit * 0.044f * baseScale
            else -> unit * 0.072f * baseScale
        }
        val boxW = when (style) {
            LogoStyle.MARK_AND_WORDMARK -> markSize * 2.65f
            LogoStyle.WORDMARK_ONLY -> markSize * 2.25f
            LogoStyle.GLASS_CHIP -> markSize * 1.25f
            else -> markSize
        }
        val boxH = when (style) {
            LogoStyle.WORDMARK_ONLY -> markSize * 0.58f
            else -> markSize
        }

        val isStorySafe = doc.size == PostSize.STORY && brand.footerPosition == FooterPosition.SAFE
        val padX = w * 0.055f
        val padTop = if (isStorySafe) h * 0.075f else h * 0.048f
        val footerBaseY = if (isStorySafe) h * 0.82f else h * 0.945f
        val footerReserve = if (isStorySafe) {
            h - footerBaseY + (if (doc.showFooter && anchor != LogoAnchor.INLINE_FOOTER) h * 0.042f else h * 0.015f)
        } else {
            if (doc.showFooter && anchor != LogoAnchor.INLINE_FOOTER) h * 0.068f else h * 0.038f
        }

        var cx: Float
        var cy: Float
        if (doc.logoCx != null && doc.logoCy != null) {
            cx = doc.logoCx * w
            cy = doc.logoCy * h
        } else {
            when (anchor) {
                LogoAnchor.TOP_LEFT, LogoAnchor.AUTO -> {
                    cx = padX + boxW * 0.5f
                    cy = padTop + boxH * 0.5f
                }
                LogoAnchor.TOP_RIGHT -> {
                    cx = w - padX - boxW * 0.5f
                    cy = padTop + boxH * 0.5f
                }
                LogoAnchor.TOP_CENTER -> {
                    cx = w * 0.5f
                    cy = padTop + boxH * 0.5f
                }
                LogoAnchor.BOTTOM_LEFT -> {
                    cx = padX + boxW * 0.5f
                    cy = h - footerReserve - boxH * 0.5f
                }
                LogoAnchor.BOTTOM_RIGHT, LogoAnchor.CORNER_WATERMARK, LogoAnchor.STAMP -> {
                    cx = w - padX - boxW * 0.5f
                    cy = h - footerReserve - boxH * 0.5f
                }
                LogoAnchor.BOTTOM_CENTER -> {
                    cx = w * 0.5f
                    cy = h - footerReserve - boxH * 0.5f
                }
                LogoAnchor.INLINE_FOOTER -> {
                    cx = padX + boxW * 0.5f
                    cy = footerBaseY
                }
                LogoAnchor.VERTICAL_EDGE -> {
                    cx = w - padX * 0.65f
                    cy = h * 0.5f
                }
                LogoAnchor.LARGE_WATERMARK -> {
                    cx = w * 0.76f
                    cy = h * 0.72f
                }
                LogoAnchor.NONE -> {
                    return RectF(0f, 0f, 0f, 0f)
                }
            }
        }

        var rect = RectF(cx - boxW / 2f, cy - boxH / 2f, cx + boxW / 2f, cy + boxH / 2f)

        // Respect template's logoKeepOut zone when user hasn't manually dragged the logo
        val keepOut = layout.logoKeepOut
        if (keepOut != null && doc.logoCx == null && doc.logoCy == null) {
            val koPx = RectF(keepOut.left * w, keepOut.top * h, keepOut.right * w, keepOut.bottom * h)
            if (RectF.intersects(rect, koPx)) {
                // Shift to opposite horizontal corner first
                val flippedCx = w - cx
                val alt = RectF(flippedCx - boxW / 2f, rect.top, flippedCx + boxW / 2f, rect.bottom)
                rect = if (!RectF.intersects(alt, koPx)) {
                    alt
                } else {
                    val botCy = h - footerReserve - boxH * 0.5f
                    RectF(w - padX - boxW, botCy - boxH / 2f, w - padX, botCy + boxH / 2f)
                }
            }
        }
        return rect
    }

    /**
     * Renders the brand mark according to Phase 1 of SHOWCASE_TEMPLATES_PLAN.md:
     * - Transparent background (no dark box)
     * - Automatic contrast detection against the pixels directly under the mark
     * - Mark only, Wordmark, Mark + Wordmark, Sticker outline, or Glass chip
     */
    fun drawBrandMark(
        context: Context,
        canvas: Canvas,
        canvasBitmap: Bitmap,
        rawLogo: Bitmap?,
        logoCacheKey: String,
        doc: PostDoc,
        brand: PostBrand,
        layout: TemplateLayout,
        accentColor: Int,
        lightColor: Int,
        darkColor: Int,
        w: Float,
        h: Float
    ) {
        val anchor = resolveLogoAnchor(doc, brand, layout)
        if (anchor == LogoAnchor.NONE || !doc.showLogo) return

        val rect = computeLogoRect(doc, brand, layout, w, h)
        if (rect.width() <= 2f || rect.height() <= 2f) return

        val style = resolveLogoStyle(doc, brand)
        val contrastMode = if (doc.logoContrast != LogoContrast.AUTO) doc.logoContrast else brand.logoContrast
        val bgLum = sampleRectLuminance(canvasBitmap, rect, w, h)
        val isDarkBg = bgLum < 0.42f

        val targetTint: Int? = when (contrastMode) {
            LogoContrast.LIGHT -> lightColor
            LogoContrast.DARK -> darkColor
            LogoContrast.ACCENT -> accentColor
            LogoContrast.ORIGINAL -> null
            LogoContrast.AUTO -> {
                // On light backgrounds, tint dark so light/gold marks never vanish; on dark backgrounds, keep original or light
                if (!isDarkBg) darkColor else null
            }
        }

        val baseAlpha = when (anchor) {
            LogoAnchor.LARGE_WATERMARK -> 32
            LogoAnchor.CORNER_WATERMARK -> 150
            else -> (255 * (doc.logoOpacity * brand.logoOpacity).coerceIn(0.1f, 1f)).roundToInt().coerceIn(25, 255)
        }

        val cleanLogo = rawLogo?.let { obtainCleanLogo(it, logoCacheKey) }
        val unit = min(w, h)

        canvas.save()
        if (anchor == LogoAnchor.VERTICAL_EDGE) {
            canvas.rotate(-90f, rect.centerX(), rect.centerY())
        } else if (anchor == LogoAnchor.STAMP) {
            canvas.rotate(-8f, rect.centerX(), rect.centerY())
        }

        // Optional backdrop only if explicitly requested via GLASS_CHIP
        if (style == LogoStyle.GLASS_CHIP) {
            val chipPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = if (isDarkBg) Color.argb((baseAlpha * 0.48f).roundToInt(), 255, 255, 255)
                else Color.argb((baseAlpha * 0.55f).roundToInt(), 10, 14, 24)
            }
            val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                this.style = Paint.Style.STROKE
                strokeWidth = max(1.5f, unit * 0.002f)
                color = withAlpha(if (isDarkBg) lightColor else darkColor, (baseAlpha * 0.4f).roundToInt())
            }
            val r = rect.height() * 0.32f
            canvas.drawRoundRect(rect, r, r, chipPaint)
            canvas.drawRoundRect(rect, r, r, borderPaint)
        }

        // Stamp circular seal ring when anchor == STAMP
        if (anchor == LogoAnchor.STAMP) {
            val ringColor = targetTint ?: if (isDarkBg) accentColor else darkColor
            val ringPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                this.style = Paint.Style.STROKE
                strokeWidth = max(2f, unit * 0.003f)
                color = withAlpha(ringColor, (baseAlpha * 0.75f).roundToInt())
            }
            val rad = max(rect.width(), rect.height()) * 0.56f
            canvas.drawCircle(rect.centerX(), rect.centerY(), rad, ringPaint)
        }

        val wordmarkColor = targetTint ?: if (isDarkBg) lightColor else darkColor

        if (style == LogoStyle.WORDMARK_ONLY || cleanLogo == null) {
            drawWordmarkText(context, canvas, doc, brand, rect, wordmarkColor, baseAlpha)
            canvas.restore()
            return
        }

        val markRect = if (style == LogoStyle.MARK_AND_WORDMARK) {
            val mSize = rect.height()
            RectF(rect.left, rect.top, rect.left + mSize, rect.bottom)
        } else if (style == LogoStyle.GLASS_CHIP) {
            val inset = rect.height() * 0.14f
            RectF(rect.left + inset, rect.top + inset, rect.right - inset, rect.bottom - inset)
        } else {
            rect
        }

        // Fit cleanLogo inside markRect preserving aspect ratio
        val srcRatio = cleanLogo.width.toFloat() / max(1, cleanLogo.height).toFloat()
        val dstW: Float
        val dstH: Float
        if (srcRatio >= 1f) {
            dstW = markRect.width()
            dstH = dstW / srcRatio
        } else {
            dstH = markRect.height()
            dstW = dstH * srcRatio
        }
        val drawRect = RectF(
            markRect.centerX() - dstW / 2f,
            markRect.centerY() - dstH / 2f,
            markRect.centerX() + dstW / 2f,
            markRect.centerY() + dstH / 2f
        )

        // Sticker die-cut white outline when LogoStyle.STICKER is selected
        if (style == LogoStyle.STICKER) {
            val outlinePaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG).apply {
                alpha = baseAlpha
                colorFilter = PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)
            }
            val off = max(2.5f, min(dstW, dstH) * 0.055f)
            for (angleDeg in 0 until 360 step 30) {
                val rad = Math.toRadians(angleDeg.toDouble())
                val dx = (cos(rad) * off).toFloat()
                val dy = (sin(rad) * off).toFloat()
                val r = RectF(drawRect.left + dx, drawRect.top + dy, drawRect.right + dx, drawRect.bottom + dy)
                canvas.drawBitmap(cleanLogo, null, r, outlinePaint)
            }
        } else if (anchor != LogoAnchor.LARGE_WATERMARK && anchor != LogoAnchor.CORNER_WATERMARK) {
            // Subtle contrast halo/drop shadow so the mark is crisp over busy photos without any box
            val shadowColor = if (isDarkBg) Color.argb((baseAlpha * 0.45f).roundToInt(), 0, 0, 0)
            else Color.argb((baseAlpha * 0.28f).roundToInt(), 255, 255, 255)
            val sp = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG).apply {
                colorFilter = PorterDuffColorFilter(shadowColor, PorterDuff.Mode.SRC_IN)
            }
            val dy = max(1.5f, dstH * 0.025f)
            canvas.drawBitmap(cleanLogo, null, RectF(drawRect.left, drawRect.top + dy, drawRect.right, drawRect.bottom + dy), sp)
        }

        val mainPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG).apply {
            alpha = baseAlpha
            if (targetTint != null) {
                colorFilter = PorterDuffColorFilter(targetTint, PorterDuff.Mode.SRC_IN)
            }
        }
        canvas.drawBitmap(cleanLogo, null, drawRect, mainPaint)

        if (style == LogoStyle.MARK_AND_WORDMARK) {
            val textRect = RectF(markRect.right + rect.height() * 0.14f, rect.top, rect.right, rect.bottom)
            drawWordmarkText(context, canvas, doc, brand, textRect, wordmarkColor, baseAlpha, leftAlign = true)
        }

        canvas.restore()
    }

    private fun drawWordmarkText(
        context: Context,
        canvas: Canvas,
        doc: PostDoc,
        brand: PostBrand,
        rect: RectF,
        color: Int,
        alpha: Int,
        leftAlign: Boolean = false
    ) {
        val label = brand.name.ifBlank { "WISDOM TOWER" }.uppercase()
        val tf = PostFonts.get(context, PostFont.BOLD, label, doc)
        val tp = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = tf
            this.color = withAlpha(color, alpha)
            letterSpacing = 0.08f
            textSize = (rect.height() * 0.42f).coerceIn(14f, 64f)
        }
        val measured = tp.measureText(label)
        if (measured > rect.width() && measured > 1f) {
            tp.textSize *= (rect.width() / measured)
        }
        val fm = tp.fontMetrics
        val baseline = rect.centerY() - (fm.ascent + fm.descent) / 2f
        val x = if (leftAlign) rect.left else rect.centerX() - tp.measureText(label) / 2f
        canvas.drawText(label, x, baseline, tp)
    }

    // ========================================================================
    // P3.1 — 3D DEVICE FRAMES (Single 3D, Opposing Pair, Trio Scroll, Tablet+Phone)
    // ========================================================================

    fun drawDevice3D(
        context: Context,
        canvas: Canvas,
        box: DecorBox,
        rect: RectF,
        doc: PostDoc,
        primaryPhoto: Bitmap?,
        secondaryPhoto: Bitmap?,
        tertiaryPhoto: Bitmap?,
        accentColor: Int,
        secondaryColor: Int,
        lightColor: Int,
        darkColor: Int,
        deepColor: Int,
        w: Float,
        h: Float
    ) {
        val slot2Photo = secondaryPhoto ?: if (doc.fillEmptyWithFirst) primaryPhoto else null
        val slot3Photo = tertiaryPhoto ?: if (doc.fillEmptyWithFirst) primaryPhoto else null

        when (box.variant) {
            1 -> {
                // Variant 1: Opposing 3D phone pair (info-twinphones)
                val gap = rect.width() * 0.05f
                val pw = (rect.width() - gap) * 0.48f
                val ph = rect.height() * 0.92f
                val leftR = RectF(rect.left, rect.top + rect.height() * 0.05f, rect.left + pw, rect.top + rect.height() * 0.05f + ph)
                val rightR = RectF(rect.right - pw, rect.top, rect.right, rect.top + ph)

                drawSingle3DDevice(
                    context = context,
                    canvas = canvas,
                    rect = leftR,
                    rotZ = -7.5f,
                    tiltX = 6f,
                    tiltY = 14f,
                    screenPhoto = primaryPhoto,
                    slotIndex = 0,
                    accentColor = accentColor,
                    lightColor = lightColor,
                    darkColor = darkColor,
                    deepColor = deepColor,
                    doc = doc,
                    isTablet = false
                )
                drawSingle3DDevice(
                    context = context,
                    canvas = canvas,
                    rect = rightR,
                    rotZ = 7.5f,
                    tiltX = 6f,
                    tiltY = -14f,
                    screenPhoto = slot2Photo,
                    slotIndex = 1,
                    accentColor = secondaryColor,
                    lightColor = lightColor,
                    darkColor = darkColor,
                    deepColor = deepColor,
                    doc = doc,
                    isTablet = false
                )
            }
            2 -> {
                // Variant 2: Trio of 3D phones in a staggered scroll showcase (device-trio-scroll)
                val pw = rect.width() * 0.36f
                val ph = rect.height() * 0.84f
                val leftR = RectF(rect.left, rect.top + rect.height() * 0.12f, rect.left + pw, rect.top + rect.height() * 0.12f + ph)
                val rightR = RectF(rect.right - pw, rect.top + rect.height() * 0.12f, rect.right, rect.top + rect.height() * 0.12f + ph)
                val centerW = rect.width() * 0.42f
                val centerH = rect.height() * 0.94f
                val centerR = RectF(
                    rect.centerX() - centerW * 0.5f,
                    rect.top,
                    rect.centerX() + centerW * 0.5f,
                    rect.top + centerH
                )

                drawSingle3DDevice(context, canvas, leftR, -11f, 5f, 15f, slot2Photo, 1, secondaryColor, lightColor, darkColor, deepColor, doc, false)
                drawSingle3DDevice(context, canvas, rightR, 11f, 5f, -15f, slot3Photo, 2, secondaryColor, lightColor, darkColor, deepColor, doc, false)
                drawSingle3DDevice(context, canvas, centerR, 0f, 4f, 0f, primaryPhoto, 0, accentColor, lightColor, darkColor, deepColor, doc, false)
            }
            3 -> {
                // Variant 3: Wide 3D Tablet + Foreground 3D Phone pair (device-tablet-duo)
                val tabRect = RectF(
                    rect.left,
                    rect.top,
                    rect.left + rect.width() * 0.82f,
                    rect.top + rect.height() * 0.78f
                )
                val phoneRect = RectF(
                    rect.right - rect.width() * 0.36f,
                    rect.bottom - rect.height() * 0.76f,
                    rect.right,
                    rect.bottom
                )
                drawSingle3DDevice(context, canvas, tabRect, -3.5f, 5f, 8f, slot2Photo, 1, secondaryColor, lightColor, darkColor, deepColor, doc, isTablet = true)
                drawSingle3DDevice(context, canvas, phoneRect, 6.5f, 4f, -10f, primaryPhoto, 0, accentColor, lightColor, darkColor, deepColor, doc, isTablet = false)
            }
            else -> {
                // Variant 0: Single 3D tilted hero phone
                drawSingle3DDevice(
                    context = context,
                    canvas = canvas,
                    rect = rect,
                    rotZ = box.rotate,
                    tiltX = if (box.tiltX != 0f) box.tiltX else 6f,
                    tiltY = if (box.tiltY != 0f) box.tiltY else -12f,
                    screenPhoto = primaryPhoto,
                    slotIndex = 0,
                    accentColor = accentColor,
                    lightColor = lightColor,
                    darkColor = darkColor,
                    deepColor = deepColor,
                    doc = doc,
                    isTablet = false
                )
            }
        }
    }

    private fun drawSingle3DDevice(
        context: Context,
        canvas: Canvas,
        rect: RectF,
        rotZ: Float,
        tiltX: Float,
        tiltY: Float,
        screenPhoto: Bitmap?,
        slotIndex: Int,
        accentColor: Int,
        lightColor: Int,
        darkColor: Int,
        deepColor: Int,
        doc: PostDoc,
        isTablet: Boolean
    ) {
        val w = rect.width()
        val h = rect.height()
        if (w <= 8f || h <= 8f) return

        // Ground contact shadow beneath floating 3D device
        PostDepth.contactShadow(
            canvas = canvas,
            cx = rect.centerX(),
            cy = rect.bottom + min(w, h) * 0.03f,
            rx = w * 0.44f,
            ry = min(w, h) * 0.08f,
            strength = doc.sceneShadowStrength
        )

        canvas.save()
        val cx = rect.centerX()
        val cy = rect.centerY()

        // Apply 3D Camera rotation around device center
        if (tiltX != 0f || tiltY != 0f || rotZ != 0f) {
            val cam = Camera()
            cam.save()
            cam.setLocation(0f, 0f, -24f)
            cam.rotateX(tiltX.coerceIn(-28f, 28f))
            cam.rotateY(tiltY.coerceIn(-28f, 28f))
            val m = Matrix()
            cam.getMatrix(m)
            cam.restore()
            m.preTranslate(-cx, -cy)
            m.postTranslate(cx, cy)
            canvas.concat(m)
            if (rotZ != 0f) {
                canvas.rotate(rotZ, cx, cy)
            }
        }

        val outerR = min(w, h) * (if (isTablet) 0.085f else 0.14f)

        // 1. Ambient & contact floor shadow
        val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(145, 0, 0, 0)
            maskFilter = BlurMaskFilter(max(8f, min(w, h) * 0.09f), BlurMaskFilter.Blur.NORMAL)
        }
        val shOffX = -tiltY * 0.9f
        val shOffY = max(8f, min(w, h) * 0.055f)
        canvas.drawRoundRect(
            RectF(rect.left + shOffX, rect.top + shOffY, rect.right + shOffX, rect.bottom + shOffY),
            outerR, outerR, shadowPaint
        )

        // 2. 3D Extruded metallic side rail thickness
        val extrudeX = (-tiltY * 0.55f).coerceIn(-w * 0.045f, w * 0.045f)
        val extrudeY = (tiltX * 0.45f).coerceIn(-h * 0.03f, h * 0.03f)
        val railPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                rect.left, rect.top, rect.right, rect.bottom,
                intArrayOf(
                    blendColors(accentColor, Color.BLACK, 0.55f),
                    Color.rgb(28, 34, 48),
                    blendColors(accentColor, Color.WHITE, 0.25f)
                ),
                floatArrayOf(0f, 0.55f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        for (step in 6 downTo 1) {
            val f = step / 6f
            val rRail = RectF(
                rect.left + extrudeX * f,
                rect.top + extrudeY * f,
                rect.right + extrudeX * f,
                rect.bottom + extrudeY * f
            )
            canvas.drawRoundRect(rRail, outerR, outerR, railPaint)
        }

        // 3. Chassis body
        val bodyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                rect.left, rect.top, rect.right, rect.bottom,
                intArrayOf(Color.rgb(38, 43, 56), Color.rgb(12, 15, 22), Color.rgb(26, 30, 42)),
                floatArrayOf(0f, 0.5f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRoundRect(rect, outerR, outerR, bodyPaint)

        // Metallic specular bezel rim
        val rimPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = max(2f, min(w, h) * 0.009f)
            shader = LinearGradient(
                rect.left, rect.top, rect.right, rect.bottom,
                intArrayOf(withAlpha(lightColor, 220), withAlpha(accentColor, 180), withAlpha(lightColor, 70)),
                floatArrayOf(0f, 0.5f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRoundRect(rect, outerR, outerR, rimPaint)

        // 4. Inner screen
        val bezel = min(w, h) * (if (isTablet) 0.042f else 0.048f)
        val screenRect = RectF(rect.left + bezel, rect.top + bezel, rect.right - bezel, rect.bottom - bezel)
        val screenR = (outerR - bezel * 0.8f).coerceAtLeast(6f)
        val screenPath = Path().apply { addRoundRect(screenRect, screenR, screenR, Path.Direction.CW) }

        canvas.save()
        canvas.clipPath(screenPath)

        val adj = doc.slotAdjust(slotIndex)
        if (screenPhoto != null && !screenPhoto.isRecycled) {
            drawBitmapCenterCrop(canvas, screenPhoto, screenRect, adj.zoom, adj.focusX, adj.focusY)
        } else {
            // Branded placeholder screen with "+ Photo N" hint (geometry + hint only; never duplicates slot text)
            val bgGrad = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = LinearGradient(
                    screenRect.left, screenRect.top, screenRect.right, screenRect.bottom,
                    intArrayOf(deepColor, darkColor, blendColors(deepColor, accentColor, 0.32f)),
                    floatArrayOf(0f, 0.65f, 1f),
                    Shader.TileMode.CLAMP
                )
            }
            canvas.drawRect(screenRect, bgGrad)

            val orbPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = RadialGradient(
                    screenRect.centerX(), screenRect.top + screenRect.height() * 0.34f,
                    max(12f, screenRect.width() * 0.65f),
                    intArrayOf(withAlpha(accentColor, 135), Color.TRANSPARENT),
                    null, Shader.TileMode.CLAMP
                )
            }
            canvas.drawRect(screenRect, orbPaint)

            val cardMargin = screenRect.width() * 0.09f
            val innerCard = RectF(
                screenRect.left + cardMargin,
                screenRect.top + screenRect.height() * 0.18f,
                screenRect.right - cardMargin,
                screenRect.bottom - screenRect.height() * 0.14f
            )
            val cr = screenRect.width() * 0.06f
            canvas.drawRoundRect(innerCard, cr, cr, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.argb(185, 15, 22, 36) })
            canvas.drawRoundRect(
                innerCard, cr, cr,
                Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    style = Paint.Style.STROKE
                    strokeWidth = max(1.5f, screenRect.width() * 0.006f)
                    color = withAlpha(accentColor, 165)
                }
            )

            // Decorative UI bars + subtle "+ Photo N" pill
            val pillH = (innerCard.height() * 0.14f).coerceIn(14f, 44f)
            val pillRect = RectF(
                innerCard.left + innerCard.width() * 0.14f,
                innerCard.top + innerCard.height() * 0.12f,
                innerCard.right - innerCard.width() * 0.14f,
                innerCard.top + innerCard.height() * 0.12f + pillH
            )
            canvas.drawRoundRect(pillRect, pillH / 2f, pillH / 2f, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = withAlpha(accentColor, 220) })

            val hintLabel = "+ PHOTO ${slotIndex + 1}"
            val hintTf = PostFonts.get(context, PostFont.BOLD, hintLabel, doc)
            val hPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                typeface = hintTf
                color = darkColor
                textSize = (pillH * 0.46f).coerceIn(9f, 22f)
            }
            val hfm = hPaint.fontMetrics
            canvas.drawText(
                hintLabel,
                pillRect.centerX() - hPaint.measureText(hintLabel) / 2f,
                pillRect.centerY() - (hfm.ascent + hfm.descent) / 2f,
                hPaint
            )

            val barPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = withAlpha(lightColor, 195) }
            val mutedBar = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = withAlpha(lightColor, 85) }
            val barL = innerCard.left + innerCard.width() * 0.12f
            var barY = pillRect.bottom + innerCard.height() * 0.12f
            val barH = max(4f, innerCard.height() * 0.045f)
            canvas.drawRoundRect(RectF(barL, barY, innerCard.right - innerCard.width() * 0.14f, barY + barH), barH / 2f, barH / 2f, barPaint)
            barY += barH * 2.1f
            canvas.drawRoundRect(RectF(barL, barY, innerCard.right - innerCard.width() * 0.26f, barY + barH), barH / 2f, barH / 2f, barPaint)
            barY += barH * 2.3f
            canvas.drawRoundRect(RectF(barL, barY, innerCard.right - innerCard.width() * 0.20f, barY + barH * 0.75f), barH / 2f, barH / 2f, mutedBar)
        }

        // 5. Diagonal glass reflection sheen across screen
        val glassPath = Path().apply {
            moveTo(screenRect.left, screenRect.top)
            lineTo(screenRect.left + screenRect.width() * 0.62f, screenRect.top)
            lineTo(screenRect.left + screenRect.width() * 0.18f, screenRect.bottom)
            lineTo(screenRect.left, screenRect.bottom)
            close()
        }
        val glassPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                screenRect.left, screenRect.top,
                screenRect.left + screenRect.width() * 0.6f, screenRect.bottom * 0.6f,
                intArrayOf(Color.argb(46, 255, 255, 255), Color.TRANSPARENT),
                null, Shader.TileMode.CLAMP
            )
        }
        canvas.drawPath(glassPath, glassPaint)
        canvas.restore()

        // 6. Dynamic island / camera punch-hole
        if (!isTablet) {
            val islandW = screenRect.width() * 0.24f
            val islandH = max(5f, screenRect.height() * 0.022f)
            val islandRect = RectF(
                screenRect.centerX() - islandW / 2f,
                screenRect.top + islandH * 0.65f,
                screenRect.centerX() + islandW / 2f,
                screenRect.top + islandH * 1.65f
            )
            canvas.drawRoundRect(islandRect, islandH / 2f, islandH / 2f, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(6, 8, 12) })
        }

        canvas.restore()
    }

    // ========================================================================
    // P3.2 — CUTOUT STACK (Rim Glow, Sticker Edge, Ground Shadow, Default Subject)
    // ========================================================================

    fun drawCutoutSubject(
        canvas: Canvas,
        cutoutBitmap: Bitmap?,
        doc: PostDoc,
        layout: TemplateLayout,
        accentColor: Int,
        lightColor: Int,
        darkColor: Int,
        w: Float,
        h: Float
    ) {
        val style = doc.cutoutStyle
        val defaultCx = when (val c = layout.cutout) {
            is CutoutBox -> c.x + c.w * 0.5f
            is PostPhotoArea -> c.x + c.w * 0.5f
            else -> 0.5f
        }
        val defaultBottomY = when (val c = layout.cutout) {
            is CutoutBox -> c.y + c.h
            is PostPhotoArea -> c.y + c.h
            else -> 0.94f
        }
        val normCx = if (doc.cutoutX == 0.5f && doc.cutoutY == 0.94f) defaultCx else doc.cutoutX
        val normBottomY = if (doc.cutoutX == 0.5f && doc.cutoutY == 0.94f) defaultBottomY else doc.cutoutY

        if (cutoutBitmap == null || cutoutBitmap.isRecycled) {
            if (!layout.cutoutShowDefault && layout.cutout == null) return
            drawDefaultSubjectSilhouette(canvas, doc, normCx, normBottomY, accentColor, lightColor, darkColor, w, h)
            return
        }

        val scale = doc.cutoutScale.coerceIn(0.35f, 2.4f)
        val targetH = h * 0.66f * scale
        val aspect = cutoutBitmap.width.toFloat() / max(1, cutoutBitmap.height).toFloat()
        val targetW = targetH * aspect
        val cx = normCx * w
        val bottomY = normBottomY * h
        val dst = RectF(cx - targetW / 2f, bottomY - targetH, cx + targetW / 2f, bottomY)

        // 1. Ground contact shadow ellipse
        val floorRect = RectF(cx - targetW * 0.42f, bottomY - targetH * 0.06f, cx + targetW * 0.42f, bottomY + targetH * 0.05f)
        val floorPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(
                floorRect.centerX(), floorRect.centerY(),
                max(10f, floorRect.width() * 0.5f),
                intArrayOf(Color.argb(150, 0, 0, 0), Color.TRANSPARENT),
                null, Shader.TileMode.CLAMP
            )
        }
        canvas.drawOval(floorRect, floorPaint)

        // 2. Style treatment (Rim glow or White sticker edge)
        when (style) {
            CutoutStyle.RIM_GLOW -> {
                val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG).apply {
                    colorFilter = PorterDuffColorFilter(withAlpha(accentColor, 195), PorterDuff.Mode.SRC_IN)
                    maskFilter = BlurMaskFilter(max(8f, min(w, h) * 0.022f), BlurMaskFilter.Blur.NORMAL)
                }
                val expand = min(w, h) * 0.012f
                canvas.drawBitmap(
                    cutoutBitmap,
                    null,
                    RectF(dst.left - expand, dst.top - expand, dst.right + expand, dst.bottom),
                    glowPaint
                )
            }
            CutoutStyle.STICKER_EDGE -> {
                val stickerPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG).apply {
                    colorFilter = PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)
                }
                val off = max(4f, min(w, h) * 0.010f)
                for (deg in 0 until 360 step 30) {
                    val rad = Math.toRadians(deg.toDouble())
                    val dx = (cos(rad) * off).toFloat()
                    val dy = (sin(rad) * off).toFloat()
                    canvas.drawBitmap(cutoutBitmap, null, RectF(dst.left + dx, dst.top + dy, dst.right + dx, dst.bottom + dy), stickerPaint)
                }
            }
            CutoutStyle.SILHOUETTE -> {
                val silPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG).apply {
                    colorFilter = PorterDuffColorFilter(darkColor, PorterDuff.Mode.SRC_IN)
                }
                val rimPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG).apply {
                    colorFilter = PorterDuffColorFilter(accentColor, PorterDuff.Mode.SRC_IN)
                }
                val rimOff = max(3f, min(w, h) * 0.006f)
                canvas.drawBitmap(cutoutBitmap, null, RectF(dst.left - rimOff, dst.top - rimOff, dst.right + rimOff, dst.bottom), rimPaint)
                canvas.drawBitmap(cutoutBitmap, null, dst, silPaint)
                return
            }
            CutoutStyle.CLEAN -> Unit
        }

        // 3. Crisp subject foreground
        val mainPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        canvas.drawBitmap(cutoutBitmap, null, dst, mainPaint)
    }

    /**
     * Draws a sculpted editorial silhouette bust with rim lighting when a cutout template is previewed before a user picks a cutout PNG.
     */
    private fun drawDefaultSubjectSilhouette(
        canvas: Canvas,
        doc: PostDoc,
        normCx: Float,
        normBottomY: Float,
        accentColor: Int,
        lightColor: Int,
        darkColor: Int,
        w: Float,
        h: Float
    ) {
        val scale = doc.cutoutScale.coerceIn(0.4f, 2.2f)
        val cx = normCx * w
        val bottomY = normBottomY * h
        val figH = h * 0.50f * scale
        val figW = figH * 0.72f

        val path = Path().apply {
            // Shoulders + torso arch
            moveTo(cx - figW * 0.5f, bottomY)
            cubicTo(
                cx - figW * 0.48f, bottomY - figH * 0.42f,
                cx - figW * 0.22f, bottomY - figH * 0.54f,
                cx, bottomY - figH * 0.54f
            )
            cubicTo(
                cx + figW * 0.22f, bottomY - figH * 0.54f,
                cx + figW * 0.48f, bottomY - figH * 0.42f,
                cx + figW * 0.5f, bottomY
            )
            close()
        }
        val headR = figW * 0.22f
        val headCy = bottomY - figH * 0.74f

        val rimPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = max(4f, min(w, h) * 0.008f)
            color = withAlpha(accentColor, 220)
            maskFilter = BlurMaskFilter(max(6f, min(w, h) * 0.015f), BlurMaskFilter.Blur.NORMAL)
        }
        canvas.drawPath(path, rimPaint)
        canvas.drawCircle(cx, headCy, headR, rimPaint)

        val bodyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                cx, headCy - headR, cx, bottomY,
                intArrayOf(blendColors(darkColor, accentColor, 0.28f), darkColor),
                null, Shader.TileMode.CLAMP
            )
        }
        canvas.drawPath(path, bodyPaint)
        canvas.drawCircle(cx, headCy, headR, bodyPaint)

        val edgePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = max(2f, min(w, h) * 0.0035f)
            color = withAlpha(lightColor, 175)
        }
        canvas.drawPath(path, edgePaint)
        canvas.drawCircle(cx, headCy, headR, edgePaint)
    }

    // ========================================================================
    // P3.3 — SPECIAL TEXT FX (GLITCH_SPLIT, CURVE_ARC, VERTICAL, STRIKE_REVEAL, OUTLINE_STACK)
    // ========================================================================

    fun drawSpecialSingleLineFx(
        canvas: Canvas,
        text: String,
        drawX: Float,
        drawY: Float,
        paint: TextPaint,
        rect: RectF,
        color: Int,
        accentColor: Int,
        glowColor: Int,
        darkColor: Int,
        lightColor: Int,
        fx: TextFx,
        w: Int,
        h: Int
    ) {
        val fontSize = paint.textSize
        when (fx) {
            TextFx.GLITCH_SPLIT -> {
                val midY = rect.centerY()
                val shift = max(4f, fontSize * 0.045f)
                val pTop = TextPaint(paint).apply {
                    style = Paint.Style.FILL
                    this.color = color
                }
                val pBot = TextPaint(paint).apply {
                    style = Paint.Style.FILL
                    this.color = accentColor
                }

                // Upper half shifted left
                canvas.save()
                canvas.clipRect(0f, 0f, w.toFloat(), midY)
                canvas.drawText(text, drawX - shift, drawY, pTop)
                canvas.restore()

                // Lower half shifted right
                canvas.save()
                canvas.clipRect(0f, midY, w.toFloat(), h.toFloat())
                canvas.drawText(text, drawX + shift, drawY, pBot)
                canvas.restore()

                // Glowing razor seam line across split
                val seamPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    this.color = glowColor
                    strokeWidth = max(2.5f, fontSize * 0.018f)
                }
                canvas.drawLine(rect.left - shift, midY, rect.right + shift, midY, seamPaint)
            }

            TextFx.CURVE_ARC -> {
                val arcPath = Path()
                val arcRect = RectF(
                    rect.left + rect.width() * 0.04f,
                    rect.top + rect.height() * 0.12f,
                    rect.right - rect.width() * 0.04f,
                    rect.top + rect.height() * 1.45f
                )
                arcPath.addArc(arcRect, 195f, 150f)
                val p = TextPaint(paint).apply {
                    style = Paint.Style.FILL
                    this.color = color
                    textAlign = Paint.Align.CENTER
                }
                canvas.drawTextOnPath(text, arcPath, 0f, 0f, p)
            }

            TextFx.VERTICAL -> {
                canvas.save()
                canvas.rotate(-90f, rect.centerX(), rect.centerY())
                val p = TextPaint(paint).apply {
                    style = Paint.Style.FILL
                    this.color = color
                }
                val tw = p.measureText(text)
                val fm = p.fontMetrics
                canvas.drawText(text, rect.centerX() - tw / 2f, rect.centerY() - (fm.ascent + fm.descent) / 2f, p)
                canvas.restore()
            }

            TextFx.STRIKE_REVEAL -> {
                val p = TextPaint(paint).apply {
                    style = Paint.Style.FILL
                    this.color = withAlpha(color, 185)
                }
                canvas.drawText(text, drawX, drawY, p)
                val tw = p.measureText(text)
                val strikeY = rect.centerY()
                val slashPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    this.color = Color.rgb(239, 68, 68)
                    strokeWidth = max(4f, fontSize * 0.09f)
                    strokeCap = Paint.Cap.ROUND
                }
                canvas.drawLine(
                    drawX - fontSize * 0.08f,
                    strikeY + fontSize * 0.10f,
                    drawX + tw + fontSize * 0.08f,
                    strikeY - fontSize * 0.10f,
                    slashPaint
                )
            }

            TextFx.OUTLINE_STACK -> {
                val outPaint = TextPaint(paint).apply {
                    style = Paint.Style.STROKE
                    strokeWidth = max(2.5f, fontSize * 0.032f)
                    this.color = withAlpha(accentColor, 155)
                }
                val off = fontSize * 0.065f
                canvas.drawText(text, drawX + off, drawY - off, outPaint)
                val fillPaint = TextPaint(paint).apply {
                    style = Paint.Style.FILL
                    this.color = color
                }
                canvas.drawText(text, drawX, drawY, fillPaint)
            }

            else -> {
                val p = TextPaint(paint).apply {
                    style = Paint.Style.FILL
                    this.color = color
                }
                canvas.drawText(text, drawX, drawY, p)
            }
        }
    }

    // ========================================================================
    // P3.4 — ABSTRACT 3D & EDITORIAL SHOWCASE DECOR SHAPES
    // ========================================================================

    fun drawShowcaseDecor(
        context: Context,
        canvas: Canvas,
        box: DecorBox,
        rect: RectF,
        doc: PostDoc,
        primaryColor: Int,
        secondaryColor: Int,
        accentColor: Int,
        glowColor: Int,
        lightColor: Int,
        darkColor: Int,
        deepColor: Int,
        unit: Float
    ) {
        when (box.shape) {
            DecorShape.PORTAL_ARCH -> drawPortalArch(canvas, rect, primaryColor, accentColor, glowColor, darkColor, unit)
            DecorShape.SPOTLIGHT_CONE -> drawSpotlightCone(canvas, rect, primaryColor, glowColor)
            DecorShape.SPEECH_STAGE -> drawSpeechStage(canvas, rect, primaryColor, accentColor, lightColor, deepColor, unit)
            DecorShape.PEEL_CORNER -> drawPeelCorner(canvas, rect, primaryColor, accentColor, lightColor, darkColor)
            DecorShape.CHECK_CARDS -> drawCheckCards(context, canvas, rect, doc, primaryColor, accentColor, lightColor, darkColor, deepColor, unit)
            DecorShape.CARD_FAN -> drawCardFan(context, canvas, rect, doc, primaryColor, accentColor, secondaryColor, lightColor, darkColor, deepColor, unit)
            DecorShape.RIBBON_PATH -> drawRibbonPath(context, canvas, rect, doc, primaryColor, accentColor, lightColor, darkColor, deepColor, unit)
            DecorShape.DATE_SLAB -> drawDateSlab(canvas, rect, primaryColor, accentColor, lightColor, darkColor, deepColor, unit)
            DecorShape.MONUMENT_SLAB -> drawMonumentSlab(canvas, rect, primaryColor, accentColor, glowColor, lightColor, darkColor, unit)
            DecorShape.TAPE_BAND -> drawTapeBand(canvas, rect, box.rotate, primaryColor, darkColor, unit)
            DecorShape.DOC_STACK -> drawDocStack(canvas, rect, primaryColor, accentColor, lightColor, darkColor, deepColor, unit)
            DecorShape.STARBURST -> drawStarburst(canvas, rect, primaryColor, accentColor, lightColor)
            DecorShape.FLOATING_OBJECT -> drawFloatingObject(canvas, rect, primaryColor, accentColor, glowColor, lightColor, darkColor, unit)
            else -> Unit
        }
    }

    private fun drawPortalArch(
        canvas: Canvas,
        rect: RectF,
        primaryColor: Int,
        accentColor: Int,
        glowColor: Int,
        darkColor: Int,
        unit: Float
    ) {
        val archPath = Path().apply {
            val rx = rect.width() * 0.5f
            moveTo(rect.left, rect.bottom)
            lineTo(rect.left, rect.top + rx)
            arcTo(RectF(rect.left, rect.top, rect.right, rect.top + rx * 2f), 180f, 180f, false)
            lineTo(rect.right, rect.bottom)
            close()
        }

        // Outer neon halo
        val haloPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = max(8f, unit * 0.022f)
            color = withAlpha(glowColor, 140)
            maskFilter = BlurMaskFilter(max(10f, unit * 0.028f), BlurMaskFilter.Blur.NORMAL)
        }
        canvas.drawPath(archPath, haloPaint)

        // Inner portal depth gradient
        val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                rect.centerX(), rect.top, rect.centerX(), rect.bottom,
                intArrayOf(withAlpha(accentColor, 95), withAlpha(primaryColor, 220), darkColor),
                floatArrayOf(0f, 0.55f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawPath(archPath, fillPaint)

        // Crisp double metallic rim
        val rimPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = max(3f, unit * 0.0055f)
            color = accentColor
        }
        canvas.drawPath(archPath, rimPaint)
    }

    private fun drawSpotlightCone(
        canvas: Canvas,
        rect: RectF,
        primaryColor: Int,
        glowColor: Int
    ) {
        val cone = Path().apply {
            moveTo(rect.centerX() - rect.width() * 0.08f, rect.top)
            lineTo(rect.centerX() + rect.width() * 0.08f, rect.top)
            lineTo(rect.right, rect.bottom * 0.92f)
            lineTo(rect.left, rect.bottom * 0.92f)
            close()
        }
        val beamPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                rect.centerX(), rect.top, rect.centerX(), rect.bottom,
                intArrayOf(withAlpha(glowColor, 195), withAlpha(primaryColor, 85), Color.TRANSPARENT),
                floatArrayOf(0f, 0.55f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawPath(cone, beamPaint)

        // Glowing stage floor disc
        val floor = RectF(rect.left + rect.width() * 0.06f, rect.bottom - rect.height() * 0.16f, rect.right - rect.width() * 0.06f, rect.bottom)
        val floorPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(
                floor.centerX(), floor.centerY(),
                max(12f, floor.width() * 0.5f),
                intArrayOf(withAlpha(glowColor, 180), withAlpha(primaryColor, 70), Color.TRANSPARENT),
                floatArrayOf(0f, 0.6f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawOval(floor, floorPaint)
    }

    private fun drawSpeechStage(
        canvas: Canvas,
        rect: RectF,
        primaryColor: Int,
        accentColor: Int,
        lightColor: Int,
        deepColor: Int,
        unit: Float
    ) {
        val r = unit * 0.036f
        // Drop shadow
        val shPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(130, 0, 0, 0)
            maskFilter = BlurMaskFilter(max(8f, unit * 0.022f), BlurMaskFilter.Blur.NORMAL)
        }
        canvas.drawRoundRect(RectF(rect.left, rect.top + unit * 0.014f, rect.right, rect.bottom + unit * 0.014f), r, r, shPaint)

        // Frosted glass card
        val cardPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                rect.left, rect.top, rect.right, rect.bottom,
                intArrayOf(withAlpha(deepColor, 235), withAlpha(primaryColor, 210)),
                null, Shader.TileMode.CLAMP
            )
        }
        canvas.drawRoundRect(rect, r, r, cardPaint)

        // Specular rim
        val rimPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = max(2f, unit * 0.0035f)
            shader = LinearGradient(
                rect.left, rect.top, rect.right, rect.bottom,
                intArrayOf(withAlpha(accentColor, 220), withAlpha(lightColor, 80)),
                null, Shader.TileMode.CLAMP
            )
        }
        canvas.drawRoundRect(rect, r, r, rimPaint)

        // 5 rating stars + waveform bars at bottom of stage
        val starY = rect.bottom - rect.height() * 0.14f
        val starR = min(rect.width(), rect.height()) * 0.035f
        val starPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = accentColor }
        for (i in 0 until 5) {
            val sx = rect.left + rect.width() * 0.10f + i * (starR * 2.6f)
            drawMiniStar(canvas, sx, starY, starR, starPaint)
        }

        // Audio waveform bars on right side
        val wavePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = withAlpha(accentColor, 175)
            strokeCap = Paint.Cap.ROUND
            strokeWidth = max(3f, unit * 0.005f)
        }
        val heights = floatArrayOf(0.35f, 0.75f, 1.0f, 0.55f, 0.85f, 0.45f, 0.90f, 0.60f, 0.30f)
        val maxWaveH = starR * 2.2f
        val startX = rect.right - rect.width() * 0.32f
        val stepX = (rect.width() * 0.22f) / heights.size
        for (i in heights.indices) {
            val wx = startX + i * stepX
            val hh = maxWaveH * heights[i] * 0.5f
            canvas.drawLine(wx, starY - hh, wx, starY + hh, wavePaint)
        }
    }

    private fun drawMiniStar(canvas: Canvas, cx: Float, cy: Float, r: Float, paint: Paint) {
        val path = Path()
        for (i in 0 until 10) {
            val rad = Math.toRadians((i * 36 - 90).toDouble())
            val d = if (i % 2 == 0) r else r * 0.44f
            val x = cx + (cos(rad) * d).toFloat()
            val y = cy + (sin(rad) * d).toFloat()
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()
        canvas.drawPath(path, paint)
    }

    private fun drawPeelCorner(
        canvas: Canvas,
        rect: RectF,
        primaryColor: Int,
        accentColor: Int,
        lightColor: Int,
        darkColor: Int
    ) {
        // Under-peel dark revealed triangle
        val revealPath = Path().apply {
            moveTo(rect.right, rect.top)
            lineTo(rect.left, rect.top)
            lineTo(rect.right, rect.bottom)
            close()
        }
        val underPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                rect.left, rect.bottom, rect.right, rect.top,
                intArrayOf(darkColor, accentColor),
                null, Shader.TileMode.CLAMP
            )
        }
        canvas.drawPath(revealPath, underPaint)

        // Curled flap shadow
        val shadowPath = Path().apply {
            moveTo(rect.left, rect.top)
            quadTo(rect.left + rect.width() * 0.18f, rect.bottom * 0.85f, rect.right, rect.bottom)
            lineTo(rect.left, rect.bottom)
            close()
        }
        val shPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(120, 0, 0, 0)
            maskFilter = BlurMaskFilter(max(8f, rect.width() * 0.08f), BlurMaskFilter.Blur.NORMAL)
        }
        canvas.drawPath(shadowPath, shPaint)

        // Curled 3D paper/foil flap
        val flapPath = Path().apply {
            moveTo(rect.left, rect.top)
            quadTo(rect.left + rect.width() * 0.14f, rect.top + rect.height() * 0.86f, rect.right, rect.bottom)
            quadTo(rect.left + rect.width() * 0.45f, rect.top + rect.height() * 0.55f, rect.left, rect.top)
            close()
        }
        val flapPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                rect.left, rect.bottom, rect.right, rect.top,
                intArrayOf(primaryColor, lightColor, accentColor),
                floatArrayOf(0f, 0.55f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawPath(flapPath, flapPaint)
    }

    data class TextAnchor(
        val cx: Float,
        val cy: Float,
        val w: Float,
        val h: Float,
        val rotateDeg: Float = 0f,
        val tiltSkew: Float = 0f
    )

    fun textAnchors(
        shape: DecorShape,
        rect: RectF,
        lineCount: Int,
        variant: Int = 0
    ): List<TextAnchor> = when (shape) {
        DecorShape.CHECK_CARDS -> {
            val count = lineCount.coerceIn(1, 3)
            val gap = rect.height() * 0.055f
            val cardH = (rect.height() - gap * (count - 1)) / count
            val tilts = floatArrayOf(-2.2f, 1.8f, -1.4f)
            (0 until count).map { idx ->
                val top = rect.top + idx * (cardH + gap)
                val insetX = if (idx % 2 == 1) rect.width() * 0.04f else 0f
                val cr = RectF(rect.left + insetX, top, rect.right - (rect.width() * 0.04f - insetX), top + cardH)
                val orbR = cardH * 0.30f
                val orbCx = cr.left + cardH * 0.52f
                val textLeft = orbCx + orbR + cardH * 0.22f
                val textRight = cr.right - cardH * 0.18f
                val textW = (textRight - textLeft).coerceAtLeast(32f)
                TextAnchor(
                    cx = (textLeft + textRight) * 0.5f,
                    cy = cr.centerY(),
                    w = textW,
                    h = cardH * 0.64f,
                    rotateDeg = tilts[idx % tilts.size]
                )
            }
        }
        DecorShape.RIBBON_PATH -> {
            val count = lineCount.coerceIn(1, 4)
            val nodes = listOf(
                (rect.left + rect.width() * 0.18f) to (rect.top + rect.height() * 0.14f),
                (rect.left + rect.width() * 0.82f) to (rect.top + rect.height() * 0.38f),
                (rect.left + rect.width() * 0.20f) to (rect.top + rect.height() * 0.64f),
                (rect.left + rect.width() * 0.80f) to (rect.top + rect.height() * 0.88f)
            )
            val orbR = min(rect.width(), rect.height()) * 0.068f
            val pillW = rect.width() * 0.52f
            val pillH = orbR * 1.48f
            (0 until count).map { i ->
                val (nx, ny) = nodes[i]
                val pillLeft = if (i % 2 == 0) nx + orbR * 1.35f else nx - orbR * 1.35f - pillW
                val pillRect = RectF(pillLeft, ny - pillH / 2f, pillLeft + pillW, ny + pillH / 2f)
                TextAnchor(
                    cx = pillRect.centerX(),
                    cy = pillRect.centerY(),
                    w = pillRect.width() * 0.86f,
                    h = pillRect.height() * 0.74f,
                    rotateDeg = 0f
                )
            }
        }
        DecorShape.CARD_FAN -> {
            val cardW = rect.width() * 0.78f
            val cardH = rect.height() * 0.84f
            listOf(
                TextAnchor(
                    cx = rect.centerX(),
                    cy = rect.centerY(),
                    w = cardW * 0.84f,
                    h = cardH * 0.72f,
                    rotateDeg = 0f
                )
            )
        }
        else -> {
            val count = lineCount.coerceAtLeast(1)
            val rowH = rect.height() / count
            (0 until count).map { i ->
                TextAnchor(
                    cx = rect.centerX(),
                    cy = rect.top + rowH * (i + 0.5f),
                    w = rect.width() * 0.88f,
                    h = rowH * 0.82f,
                    rotateDeg = 0f
                )
            }
        }
    }

    private fun drawCheckCards(
        context: Context,
        canvas: Canvas,
        rect: RectF,
        doc: PostDoc,
        primaryColor: Int,
        accentColor: Int,
        lightColor: Int,
        darkColor: Int,
        deepColor: Int,
        unit: Float
    ) {
        val rawItems = doc.texts[PostSlots.LIST].orEmpty()
            .split('\n')
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .ifEmpty { listOf("Active Recall", "Spaced Review", "Full Rest") }
            .take(3)

        val count = rawItems.size.coerceAtLeast(1)
        val gap = rect.height() * 0.055f
        val cardH = (rect.height() - gap * (count - 1)) / count
        val tilts = floatArrayOf(-2.2f, 1.8f, -1.4f)

        for (idx in 0 until count) {
            val top = rect.top + idx * (cardH + gap)
            val insetX = if (idx % 2 == 1) rect.width() * 0.04f else 0f
            val cr = RectF(rect.left + insetX, top, rect.right - (rect.width() * 0.04f - insetX), top + cardH)
            val rad = cardH * 0.28f

            canvas.save()
            canvas.rotate(tilts[idx % tilts.size], cr.centerX(), cr.centerY())

            // Shadow
            val sp = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.argb(135, 0, 0, 0)
                maskFilter = BlurMaskFilter(max(6f, unit * 0.016f), BlurMaskFilter.Blur.NORMAL)
            }
            canvas.drawRoundRect(RectF(cr.left, cr.top + unit * 0.010f, cr.right, cr.bottom + unit * 0.010f), rad, rad, sp)

            // Card fill
            val cp = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = LinearGradient(
                    cr.left, cr.top, cr.right, cr.bottom,
                    intArrayOf(withAlpha(deepColor, 240), withAlpha(primaryColor, 225)),
                    null, Shader.TileMode.CLAMP
                )
            }
            canvas.drawRoundRect(cr, rad, rad, cp)

            // Rim
            val rp = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = max(2f, unit * 0.003f)
                color = withAlpha(accentColor, 185)
            }
            canvas.drawRoundRect(cr, rad, rad, rp)

            // Glowing check orb (geometry only — text is rendered once by PostRenderer via textAnchors)
            val orbR = cardH * 0.30f
            val orbCx = cr.left + cardH * 0.52f
            val orbCy = cr.centerY()
            canvas.drawCircle(orbCx, orbCy, orbR, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = accentColor })

            val tickP = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = max(3f, orbR * 0.24f)
                strokeCap = Paint.Cap.ROUND
                strokeJoin = Paint.Join.ROUND
                color = darkColor
            }
            val tick = Path().apply {
                moveTo(orbCx - orbR * 0.42f, orbCy)
                lineTo(orbCx - orbR * 0.08f, orbCy + orbR * 0.34f)
                lineTo(orbCx + orbR * 0.46f, orbCy - orbR * 0.34f)
            }
            canvas.drawPath(tick, tickP)

            canvas.restore()
        }
    }

    private fun drawCardFan(
        context: Context,
        canvas: Canvas,
        rect: RectF,
        doc: PostDoc,
        primaryColor: Int,
        accentColor: Int,
        secondaryColor: Int,
        lightColor: Int,
        darkColor: Int,
        deepColor: Int,
        unit: Float
    ) {
        val cardW = rect.width() * 0.78f
        val cardH = rect.height() * 0.84f
        val baseRect = RectF(
            rect.centerX() - cardW / 2f,
            rect.centerY() - cardH / 2f,
            rect.centerX() + cardW / 2f,
            rect.centerY() + cardH / 2f
        )
        val r = min(cardW, cardH) * 0.08f

        // Back left & back right fanned cards
        val fanConfigs = listOf(
            Triple(-11f, -cardW * 0.09f, secondaryColor),
            Triple(11f, cardW * 0.09f, accentColor)
        )
        for ((angle, dx, rimCol) in fanConfigs) {
            canvas.save()
            canvas.translate(dx, cardH * 0.03f)
            canvas.rotate(angle, baseRect.centerX(), baseRect.bottom)
            val bp = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = withAlpha(deepColor, 215) }
            canvas.drawRoundRect(baseRect, r, r, bp)
            val rp = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = max(2f, unit * 0.003f)
                color = withAlpha(rimCol, 150)
            }
            canvas.drawRoundRect(baseRect, r, r, rp)
            canvas.restore()
        }

        // Front hero card with drop shadow & top accent bar
        val shPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(150, 0, 0, 0)
            maskFilter = BlurMaskFilter(max(10f, unit * 0.025f), BlurMaskFilter.Blur.NORMAL)
        }
        canvas.drawRoundRect(RectF(baseRect.left, baseRect.top + unit * 0.015f, baseRect.right, baseRect.bottom + unit * 0.015f), r, r, shPaint)

        val frontPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                baseRect.left, baseRect.top, baseRect.right, baseRect.bottom,
                intArrayOf(primaryColor, deepColor),
                null, Shader.TileMode.CLAMP
            )
        }
        canvas.drawRoundRect(baseRect, r, r, frontPaint)

        val frontRim = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = max(2.5f, unit * 0.004f)
            color = accentColor
        }
        canvas.drawRoundRect(baseRect, r, r, frontRim)
    }

    private fun drawRibbonPath(
        context: Context,
        canvas: Canvas,
        rect: RectF,
        doc: PostDoc,
        primaryColor: Int,
        accentColor: Int,
        lightColor: Int,
        darkColor: Int,
        deepColor: Int,
        unit: Float
    ) {
        val rawSteps = doc.texts[PostSlots.LIST].orEmpty()
            .split('\n')
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .ifEmpty { listOf("Read", "Recall", "Practice", "Master") }
            .take(4)

        val nodes = listOf(
            (rect.left + rect.width() * 0.18f) to (rect.top + rect.height() * 0.14f),
            (rect.left + rect.width() * 0.82f) to (rect.top + rect.height() * 0.38f),
            (rect.left + rect.width() * 0.20f) to (rect.top + rect.height() * 0.64f),
            (rect.left + rect.width() * 0.80f) to (rect.top + rect.height() * 0.88f)
        )

        val ribbon = Path().apply {
            moveTo(nodes[0].first, nodes[0].second)
            for (i in 0 until nodes.lastIndex) {
                val (x0, y0) = nodes[i]
                val (x1, y1) = nodes[i + 1]
                val midY = (y0 + y1) * 0.5f
                cubicTo(x0, midY, x1, midY, x1, y1)
            }
        }

        // Glow ribbon trail
        val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = max(14f, unit * 0.032f)
            strokeCap = Paint.Cap.ROUND
            color = withAlpha(accentColor, 85)
            maskFilter = BlurMaskFilter(max(8f, unit * 0.02f), BlurMaskFilter.Blur.NORMAL)
        }
        canvas.drawPath(ribbon, glowPaint)

        // Core ribbon path
        val corePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = max(6f, unit * 0.012f)
            strokeCap = Paint.Cap.ROUND
            shader = LinearGradient(
                rect.left, rect.top, rect.right, rect.bottom,
                intArrayOf(accentColor, lightColor, accentColor),
                null, Shader.TileMode.CLAMP
            )
        }
        canvas.drawPath(ribbon, corePaint)

        val tf = PostFonts.get(context, PostFont.BOLD, "01 02 03 04", doc)
        val orbR = min(rect.width(), rect.height()) * 0.068f
        val pillW = rect.width() * 0.52f
        val pillH = orbR * 1.48f

        for (i in 0 until rawSteps.size.coerceIn(1, nodes.size)) {
            val (nx, ny) = nodes[i]
            // Node disc
            canvas.drawCircle(nx, ny, orbR * 1.2f, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = deepColor })
            canvas.drawCircle(nx, ny, orbR, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = accentColor })

            val numStr = "0${i + 1}"
            val np = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                typeface = tf
                color = darkColor
                textSize = orbR * 0.92f
            }
            val nfm = np.fontMetrics
            canvas.drawText(numStr, nx - np.measureText(numStr) / 2f, ny - (nfm.ascent + nfm.descent) / 2f, np)

            // Step pill frame next to node (geometry only — text is rendered once by PostRenderer via textAnchors)
            val pillLeft = if (i % 2 == 0) nx + orbR * 1.35f else nx - orbR * 1.35f - pillW
            val pillRect = RectF(pillLeft, ny - pillH / 2f, pillLeft + pillW, ny + pillH / 2f)

            canvas.drawRoundRect(pillRect, pillH / 2f, pillH / 2f, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = withAlpha(deepColor, 230) })
            canvas.drawRoundRect(pillRect, pillH / 2f, pillH / 2f, Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = max(1.5f, unit * 0.0025f)
                color = withAlpha(accentColor, 160)
            })
        }
    }

    private fun drawDateSlab(
        canvas: Canvas,
        rect: RectF,
        primaryColor: Int,
        accentColor: Int,
        lightColor: Int,
        darkColor: Int,
        deepColor: Int,
        unit: Float
    ) {
        val r = unit * 0.038f
        val depth = unit * 0.018f

        // 3D Extruded slab base
        val basePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = blendColors(accentColor, Color.BLACK, 0.55f) }
        canvas.drawRoundRect(RectF(rect.left + depth, rect.top + depth, rect.right + depth, rect.bottom + depth), r, r, basePaint)

        // Main calendar slab body
        val bodyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                rect.left, rect.top, rect.right, rect.bottom,
                intArrayOf(deepColor, primaryColor),
                null, Shader.TileMode.CLAMP
            )
        }
        canvas.drawRoundRect(rect, r, r, bodyPaint)

        // Top calendar header band
        val headerH = rect.height() * 0.24f
        val headerPath = Path().apply {
            addRoundRect(
                RectF(rect.left, rect.top, rect.right, rect.top + headerH),
                floatArrayOf(r, r, r, r, 0f, 0f, 0f, 0f),
                Path.Direction.CW
            )
        }
        canvas.drawPath(headerPath, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = accentColor })

        // Binder rings at top
        val ringR = headerH * 0.16f
        val ringPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = darkColor }
        canvas.drawCircle(rect.left + rect.width() * 0.25f, rect.top + headerH * 0.35f, ringR, ringPaint)
        canvas.drawCircle(rect.right - rect.width() * 0.25f, rect.top + headerH * 0.35f, ringR, ringPaint)

        // Rim stroke
        val rimPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = max(2.5f, unit * 0.004f)
            color = withAlpha(accentColor, 210)
        }
        canvas.drawRoundRect(rect, r, r, rimPaint)
    }

    private fun drawMonumentSlab(
        canvas: Canvas,
        rect: RectF,
        primaryColor: Int,
        accentColor: Int,
        glowColor: Int,
        lightColor: Int,
        darkColor: Int,
        unit: Float
    ) {
        // Volumetric celebration rays behind pedestal
        val cx = rect.centerX()
        val cy = rect.centerY()
        val rayPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(
                cx, cy, max(16f, max(rect.width(), rect.height()) * 0.65f),
                intArrayOf(withAlpha(glowColor, 150), withAlpha(accentColor, 55), Color.TRANSPARENT),
                floatArrayOf(0f, 0.55f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawOval(rect, rayPaint)

        // 3D Isometric tiered pedestal at bottom of rect
        val pedTop = rect.bottom - rect.height() * 0.26f
        val tier1 = RectF(rect.left + rect.width() * 0.14f, pedTop, rect.right - rect.width() * 0.14f, pedTop + rect.height() * 0.11f)
        val tier2 = RectF(rect.left + rect.width() * 0.06f, tier1.bottom - 4f, rect.right - rect.width() * 0.06f, rect.bottom)

        val tierPaint1 = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                tier1.left, tier1.top, tier1.right, tier1.bottom,
                intArrayOf(accentColor, primaryColor, darkColor),
                floatArrayOf(0f, 0.5f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        val tierPaint2 = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                tier2.left, tier2.top, tier2.right, tier2.bottom,
                intArrayOf(primaryColor, darkColor),
                null, Shader.TileMode.CLAMP
            )
        }
        val r = unit * 0.022f
        canvas.drawRoundRect(tier2, r, r, tierPaint2)
        canvas.drawRoundRect(tier1, r, r, tierPaint1)

        // Floating celebratory confetti sparks
        val sparkPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = accentColor }
        val sparkLight = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = lightColor }
        val sparks = arrayOf(
            0.12f to 0.22f, 0.88f to 0.18f, 0.18f to 0.56f, 0.84f to 0.52f,
            0.28f to 0.12f, 0.74f to 0.14f, 0.08f to 0.38f, 0.92f to 0.36f
        )
        val sr = unit * 0.011f
        for ((i, pair) in sparks.withIndex()) {
            val sx = rect.left + rect.width() * pair.first
            val sy = rect.top + rect.height() * pair.second
            drawMiniStar(canvas, sx, sy, if (i % 2 == 0) sr * 1.4f else sr, if (i % 2 == 0) sparkPaint else sparkLight)
        }
    }

    private fun drawTapeBand(
        canvas: Canvas,
        rect: RectF,
        rotate: Float,
        primaryColor: Int,
        darkColor: Int,
        unit: Float
    ) {
        canvas.save()
        if (rotate != 0f) {
            canvas.rotate(rotate, rect.centerX(), rect.centerY())
        }

        // Cast shadow under tape
        val shPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(140, 0, 0, 0)
            maskFilter = BlurMaskFilter(max(6f, unit * 0.015f), BlurMaskFilter.Blur.NORMAL)
        }
        canvas.drawRect(RectF(rect.left, rect.top + unit * 0.010f, rect.right, rect.bottom + unit * 0.010f), shPaint)

        // Caution tape base fill
        val basePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = primaryColor }
        canvas.drawRect(rect, basePaint)

        // Diagonal hazard stripes along edges
        canvas.save()
        canvas.clipRect(rect)
        val stripePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = withAlpha(darkColor, 48)
            style = Paint.Style.FILL
        }
        val step = rect.height() * 1.15f
        var x = rect.left - rect.height()
        while (x < rect.right + rect.height()) {
            val p = Path().apply {
                moveTo(x, rect.bottom)
                lineTo(x + step * 0.42f, rect.bottom)
                lineTo(x + step * 0.82f, rect.top)
                lineTo(x + step * 0.40f, rect.top)
                close()
            }
            canvas.drawPath(p, stripePaint)
            x += step
        }
        canvas.restore()

        canvas.restore()
    }

    private fun drawDocStack(
        canvas: Canvas,
        rect: RectF,
        primaryColor: Int,
        accentColor: Int,
        lightColor: Int,
        darkColor: Int,
        deepColor: Int,
        unit: Float
    ) {
        val sheetW = rect.width() * 0.74f
        val sheetH = rect.height() * 0.84f
        val base = RectF(
            rect.centerX() - sheetW / 2f,
            rect.centerY() - sheetH / 2f,
            rect.centerX() + sheetW / 2f,
            rect.centerY() + sheetH / 2f
        )
        val r = unit * 0.024f

        // 2 tilted back sheets
        val backSheets = listOf(-9f to (-sheetW * 0.07f), 7f to (sheetW * 0.07f))
        for ((ang, dx) in backSheets) {
            canvas.save()
            canvas.translate(dx, sheetH * 0.02f)
            canvas.rotate(ang, base.centerX(), base.centerY())
            canvas.drawRoundRect(base, r, r, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = withAlpha(deepColor, 225) })
            canvas.drawRoundRect(base, r, r, Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = max(2f, unit * 0.003f)
                color = withAlpha(accentColor, 130)
            })
            canvas.restore()
        }

        // Front hero document sheet
        val shPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(150, 0, 0, 0)
            maskFilter = BlurMaskFilter(max(10f, unit * 0.024f), BlurMaskFilter.Blur.NORMAL)
        }
        canvas.drawRoundRect(RectF(base.left, base.top + unit * 0.014f, base.right, base.bottom + unit * 0.014f), r, r, shPaint)

        val paperPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                base.left, base.top, base.right, base.bottom,
                intArrayOf(lightColor, blendColors(lightColor, accentColor, 0.12f)),
                null, Shader.TileMode.CLAMP
            )
        }
        canvas.drawRoundRect(base, r, r, paperPaint)

        // Document header ribbon + ruled lines preview
        val headerRect = RectF(base.left + sheetW * 0.08f, base.top + sheetH * 0.08f, base.left + sheetW * 0.44f, base.top + sheetH * 0.16f)
        canvas.drawRoundRect(headerRect, headerRect.height() / 2f, headerRect.height() / 2f, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = accentColor })

        val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = withAlpha(darkColor, 45) }
        val lineH = max(4f, sheetH * 0.026f)
        for (i in 0 until 5) {
            val ly = base.top + sheetH * (0.24f + i * 0.08f)
            val rightFrac = if (i % 2 == 0) 0.88f else 0.68f
            canvas.drawRoundRect(
                RectF(base.left + sheetW * 0.08f, ly, base.left + sheetW * rightFrac, ly + lineH),
                lineH / 2f, lineH / 2f, linePaint
            )
        }
    }

    private fun drawStarburst(
        canvas: Canvas,
        rect: RectF,
        primaryColor: Int,
        accentColor: Int,
        lightColor: Int
    ) {
        val cx = rect.centerX()
        val cy = rect.centerY()
        val outerR = min(rect.width(), rect.height()) * 0.5f
        val innerR = outerR * 0.78f
        val points = 24
        val path = Path()
        for (i in 0 until points * 2) {
            val rad = Math.toRadians((i * 180.0 / points))
            val r = if (i % 2 == 0) outerR else innerR
            val x = cx + (cos(rad) * r).toFloat()
            val y = cy + (sin(rad) * r).toFloat()
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()

        val fill = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(
                cx, cy, max(8f, outerR),
                intArrayOf(accentColor, primaryColor),
                null, Shader.TileMode.CLAMP
            )
        }
        canvas.drawPath(path, fill)
        val stroke = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = max(2f, outerR * 0.04f)
            color = withAlpha(lightColor, 180)
        }
        canvas.drawPath(path, stroke)
    }

    private fun drawFloatingObject(
        canvas: Canvas,
        rect: RectF,
        primaryColor: Int,
        accentColor: Int,
        glowColor: Int,
        lightColor: Int,
        darkColor: Int,
        unit: Float
    ) {
        val cx = rect.centerX()
        val cy = rect.centerY()
        val r = min(rect.width(), rect.height()) * 0.36f

        // Ground shadow below floating 3D object
        val floor = RectF(cx - r * 0.95f, rect.bottom - r * 0.28f, cx + r * 0.95f, rect.bottom)
        canvas.drawOval(floor, Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(
                floor.centerX(), floor.centerY(), max(10f, floor.width() * 0.5f),
                intArrayOf(Color.argb(150, 0, 0, 0), Color.TRANSPARENT),
                null, Shader.TileMode.CLAMP
            )
        })

        // Ambient glow
        canvas.drawCircle(cx, cy, r * 1.35f, Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(
                cx, cy, max(12f, r * 1.35f),
                intArrayOf(withAlpha(glowColor, 165), Color.TRANSPARENT),
                null, Shader.TileMode.CLAMP
            )
        })

        // 3D Sphere / Gem core
        val spherePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(
                cx - r * 0.32f, cy - r * 0.32f, max(10f, r * 1.35f),
                intArrayOf(lightColor, accentColor, primaryColor, darkColor),
                floatArrayOf(0f, 0.28f, 0.68f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawCircle(cx, cy, r, spherePaint)

        // Tilted orbital ring around sphere
        canvas.save()
        canvas.rotate(-22f, cx, cy)
        val orbitRect = RectF(cx - r * 1.48f, cy - r * 0.38f, cx + r * 1.48f, cy + r * 0.38f)
        val orbitPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = max(4f, unit * 0.009f)
            shader = LinearGradient(
                orbitRect.left, orbitRect.centerY(), orbitRect.right, orbitRect.centerY(),
                intArrayOf(accentColor, lightColor, accentColor),
                null, Shader.TileMode.CLAMP
            )
        }
        canvas.drawOval(orbitRect, orbitPaint)
        canvas.restore()
    }

    private fun drawBitmapCenterCrop(
        canvas: Canvas,
        bmp: Bitmap,
        dest: RectF,
        zoom: Float,
        focusX: Float,
        focusY: Float
    ) {
        val bw = bmp.width.toFloat()
        val bh = bmp.height.toFloat()
        if (bw <= 1f || bh <= 1f) return
        val scale = max(dest.width() / bw, dest.height() / bh) * zoom.coerceIn(0.5f, 4f)
        val sw = bw * scale
        val sh = bh * scale
        val dx = dest.left + (dest.width() - sw) * focusX.coerceIn(0f, 1f)
        val dy = dest.top + (dest.height() - sh) * focusY.coerceIn(0f, 1f)
        val m = Matrix().apply {
            postScale(scale, scale)
            postTranslate(dx, dy)
        }
        canvas.drawBitmap(bmp, m, Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG))
    }

    private fun withAlpha(color: Int, alpha: Int): Int =
        Color.argb(alpha.coerceIn(0, 255), Color.red(color), Color.green(color), Color.blue(color))

    private fun blendColors(c1: Int, c2: Int, ratio: Float): Int {
        val t = ratio.coerceIn(0f, 1f)
        val r = (Color.red(c1) * (1f - t) + Color.red(c2) * t).roundToInt().coerceIn(0, 255)
        val g = (Color.green(c1) * (1f - t) + Color.green(c2) * t).roundToInt().coerceIn(0, 255)
        val b = (Color.blue(c1) * (1f - t) + Color.blue(c2) * t).roundToInt().coerceIn(0, 255)
        return Color.rgb(r, g, b)
    }
}
