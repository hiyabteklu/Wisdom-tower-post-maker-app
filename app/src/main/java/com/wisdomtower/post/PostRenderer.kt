package com.wisdomtower.post

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
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
import android.net.Uri
import android.provider.OpenableColumns
import android.text.Layout
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.StaticLayout
import android.text.TextPaint
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.LruCache
import java.io.File
import java.util.Locale
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * Bundled and user-imported fonts, with automatic role fallback (TEMPLATE_REDESIGN_PLAN.md 3.7).
 */
object PostFonts {
    const val ENGLISH = "english"
    const val AMHARIC = "amharic"

    data class FontChoice(val label: String, val path: String)

    private val cache = HashMap<String, Typeface>()

    private val ROLE_CANDIDATES: Map<PostFont, List<String>> = mapOf(
        PostFont.CONDENSED to listOf(
            "fonts/anton_regular.ttf",
            "fonts/english/Anton.ttf",
            "fonts/english/Bebas_Neue.ttf",
            "fonts/english/Staatliches.ttf",
            "fonts/english/Oswald_Bold.ttf"
        ),
        PostFont.SERIF to listOf(
            "fonts/playfair_display_black.ttf",
            "fonts/english/Alfa_Slab.ttf",
            "fonts/amharic/Noto_Serif.ttf",
            "fonts/amharic/Geez_Serif.ttf",
            "fonts/english/Bowlby.ttf"
        ),
        PostFont.WIDE to listOf(
            "fonts/archivo_black.ttf",
            "fonts/english/Archivo_Black.ttf",
            "fonts/english/Montserrat_Black.ttf",
            "fonts/english/Spartan_Black.ttf",
            "fonts/english/Rubik_Mono.ttf"
        ),
        PostFont.DISPLAY to listOf(
            "fonts/english/Outfit_Black.ttf",
            "fonts/english/Montserrat_Black.ttf",
            "fonts/english/Jakarta_ExtraBold.ttf",
            "fonts/english/Spartan_ExtraBold.ttf"
        ),
        PostFont.BOLD to listOf(
            "fonts/english/Jakarta_Bold.ttf",
            "fonts/english/Outfit_Bold.ttf",
            "fonts/english/Montserrat_Bold.ttf",
            "fonts/english/Manrope_Bold.ttf"
        ),
        PostFont.REGULAR to listOf(
            "fonts/english/Jakarta_Medium.ttf",
            "fonts/english/Outfit_Regular.ttf",
            "fonts/english/Manrope_Medium.ttf",
            "fonts/english/Montserrat_Regular.ttf"
        )
    )

    private val AMHARIC_DEFAULTS = listOf(
        "fonts/amharic/Noto_Black.ttf",
        "fonts/amharic/Adwa_Bold.ttf",
        "fonts/amharic/Nokia_Bold.ttf",
        "fonts/amharic/Mulat_Bold.ttf",
        "fonts/amharic/Addis.ttf",
        "fonts/amharic/Ethiopic.ttf"
    )

    private fun cleanLabel(fileName: String): String =
        fileName.substringBeforeLast('.')
            .replace('_', ' ')
            .replace('-', ' ')
            .trim()
            .ifEmpty { "Font" }

    fun list(context: Context, group: String): List<FontChoice> {
        val result = mutableListOf<FontChoice>()
        val assetDir = "fonts/$group"
        val assetFiles = runCatching { context.assets.list(assetDir) }.getOrNull() ?: emptyArray()
        assetFiles
            .filter { it.endsWith(".ttf", ignoreCase = true) || it.endsWith(".otf", ignoreCase = true) }
            .sorted()
            .forEach { name ->
                result.add(FontChoice(cleanLabel(name), "$assetDir/$name"))
            }

        val userDir = File(context.filesDir, "fonts/$group")
        val userFiles = userDir.listFiles() ?: emptyArray()
        userFiles
            .filter { it.isFile && (it.name.endsWith(".ttf", true) || it.name.endsWith(".otf", true)) }
            .sortedBy { it.name }
            .forEach { file ->
                result.add(FontChoice(cleanLabel(file.name), "file:${file.absolutePath}"))
            }
        return result
    }

    fun importFont(context: Context, uri: Uri, group: String): FontChoice? = runCatching {
        var displayName = "font.ttf"
        context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { c ->
            if (c.moveToFirst()) displayName = c.getString(0) ?: displayName
        }
        val lower = displayName.lowercase(Locale.ROOT)
        if (!lower.endsWith(".ttf") && !lower.endsWith(".otf")) return@runCatching null
        val ext = if (lower.endsWith(".otf")) ".otf" else ".ttf"
        val stem = displayName.substringBeforeLast('.').replace(Regex("[^A-Za-z0-9_-]"), "_").take(32).ifEmpty { "font" }
        val dir = File(context.filesDir, "fonts/$group").also { it.mkdirs() }
        val out = File(dir, "$stem$ext")
        context.contentResolver.openInputStream(uri)?.use { input ->
            out.outputStream().use { output -> input.copyTo(output) }
        } ?: return@runCatching null
        val tf = Typeface.createFromFile(out)
        val path = "file:${out.absolutePath}"
        synchronized(cache) { cache[path] = tf }
        FontChoice(cleanLabel(out.name), path)
    }.getOrNull()

    fun typeface(context: Context, path: String?): Typeface? {
        if (path.isNullOrBlank()) return null
        synchronized(cache) { cache[path]?.let { return it } }
        val loaded = runCatching {
            if (path.startsWith("file:")) {
                val file = File(path.removePrefix("file:"))
                if (file.exists()) Typeface.createFromFile(file) else null
            } else {
                Typeface.createFromAsset(context.assets, path)
            }
        }.getOrNull()
        if (loaded != null) {
            synchronized(cache) { cache[path] = loaded }
        }
        return loaded
    }

    fun get(
        context: Context,
        role: PostFont,
        text: String,
        doc: PostDoc,
        slotStyle: SlotStyle? = null
    ): Typeface {
        if (PostSlots.containsEthiopic(text)) {
            typeface(context, slotStyle?.fontAm)?.let { return it }
            typeface(context, doc.fontAmharic)?.let { return it }
            for (candidate in AMHARIC_DEFAULTS) {
                typeface(context, candidate)?.let { return it }
            }
            list(context, AMHARIC).firstOrNull()?.let { choice ->
                typeface(context, choice.path)?.let { return it }
            }
        }
        typeface(context, slotStyle?.fontEn)?.let { return it }
        typeface(context, doc.fontEnglish)?.let { return it }

        val candidates = ROLE_CANDIDATES[role].orEmpty() + ROLE_CANDIDATES[PostFont.DISPLAY].orEmpty()
        for (path in candidates) {
            typeface(context, path)?.let { return it }
        }
        return when (role) {
            PostFont.SERIF -> Typeface.create("serif", Typeface.BOLD)
            PostFont.REGULAR -> Typeface.create("sans-serif", Typeface.NORMAL)
            else -> Typeface.create("sans-serif-black", Typeface.BOLD)
        }
    }
}

/**
 * Full poster renderer implementing TEMPLATE_REDESIGN_PLAN.md section 3:
 * - FitModes: LEGACY, FILL (with ink-bounds cap-height fitting for letters/short words), FILL_WIDTH, STACK, TICKER, TILES, CHIPS
 * - Exact manual sizeUnit with VAnchor (TOP, CENTER, BOTTOM) and bleed support
 * - Proportional TextShadow (SOFT, HARD, GLOW) across all text & list paths
 * - TextFx: SOLID, OUTLINE, GRADIENT, PHOTO_FILL, ECHO, EXTRUDE, SPLIT_TONE, plus chip pills & dimOthers spotlight
 * - Background photo on every template (opacity, dim, blur, zoom/focus, mono/duotone, photoAsBackground, panelOpacity)
 * - Triptych multi-cell photos, rotated slots/decors/photos, and LruCache for fitted text sizes.
 */
object PostRenderer {
    class Result(val bitmap: Bitmap, val overflow: List<String>)

    private val fitCache = LruCache<String, Float>(256)
    private var cachedLogoKey: String? = null
    private var cachedLogoBitmap: Bitmap? = null

    private class TonePalette(
        val accent: Int,
        val secondary: Int,
        val light: Int,
        val dark: Int,
        val deep: Int,
        val glow: Int,
        val tint: Int,
        val soft: Int,
        val page: Int,
        val text: Int,
        val muted: Int,
        val paper: Int,
        val blue: Int,
        val red: Int
    ) {
        fun colorOf(tone: PostTone): Int = when (tone) {
            PostTone.ACCENT -> accent
            PostTone.DEEP -> deep
            PostTone.GLOW -> glow
            PostTone.SECONDARY -> secondary
            PostTone.LIGHT -> light
            PostTone.DARK -> dark
            PostTone.TINT -> tint
            PostTone.SOFT -> soft
            PostTone.PAGE -> page
            PostTone.TEXT -> text
            PostTone.MUTED -> muted
            PostTone.PAPER -> paper
            PostTone.BLUE -> blue
            PostTone.RED -> red
        }
    }

    fun preview(
        context: Context,
        doc: PostDoc,
        brand: PostBrand,
        maxSide: Int = 720,
        cancelled: () -> Boolean = { false }
    ): Result {
        if (cancelled()) {
            return Result(Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888), emptyList())
        }
        val longest = max(doc.size.width, doc.size.height).toFloat()
        val scale = (maxSide.toFloat() / longest).coerceIn(0.15f, 1f)
        return renderInternal(context, doc, brand, scale, cancelled)
    }

    fun thumbnail(
        context: Context,
        doc: PostDoc,
        brand: PostBrand,
        maxSide: Int = 300
    ): Result {
        val longest = max(doc.size.width, doc.size.height).toFloat()
        val scale = (maxSide.toFloat() / longest).coerceIn(0.12f, 0.5f)
        return renderInternal(context, doc, brand, scale) { false }
    }

    fun render(
        context: Context,
        doc: PostDoc,
        brand: PostBrand,
        scale: Float = 1f
    ): Result = renderInternal(context, doc, brand, scale.coerceIn(0.15f, 2f)) { false }

    private fun renderInternal(
        context: Context,
        rawDoc: PostDoc,
        brand: PostBrand,
        scale: Float,
        cancelled: () -> Boolean
    ): Result {
        val spec = postTemplate(rawDoc.templateId)
        val layout = spec.layout(rawDoc.size)
        val requiredSlots = layout.slots.filter { it.literal == null }.map { it.slot }.toSet()
        val doc = if ((PostSlots.LETTER in requiredSlots && rawDoc.texts[PostSlots.LETTER].isNullOrBlank()) ||
            (PostSlots.WORD in requiredSlots && rawDoc.texts[PostSlots.WORD].isNullOrBlank())
        ) {
            rawDoc.copy(texts = PostSlots.withDerivedSlots(rawDoc.texts, requiredSlots))
        } else {
            rawDoc
        }

        val w = doc.size.width
        val h = doc.size.height
        val unit = min(w, h).toFloat()
        val outW = max(1, (w * scale).roundToInt())
        val outH = max(1, (h * scale).roundToInt())
        val bitmap = Bitmap.createBitmap(outW, outH, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.scale(outW / w.toFloat(), outH / h.toFloat())

        val tones = buildPalette(doc, brand, layout.base)

        // 1. Base background (template backdrop, solid, or gradient)
        drawBaseBackground(canvas, doc, brand, layout, tones, w, h)
        if (cancelled()) return Result(bitmap, emptyList())

        // 2. Mesh gradient blend
        if (doc.meshStyle != MeshStyle.NONE) {
            PostEngine.drawMesh(canvas, doc.meshStyle, w, h, tones.accent, tones.secondary, tones.glow)
        }

        // 3. Background photo (or photoAsBackground fallback) with opacity, dim, blur, zoom, focus, tone
        val bgPhotoBitmap = resolveBackgroundPhoto(context, doc, max(outW, outH))
        if (bgPhotoBitmap != null && doc.backgroundPhotoOpacity > 0.005f) {
            drawBackgroundPhoto(canvas, doc, bgPhotoBitmap, tones, w, h)
        }
        if (cancelled()) return Result(bitmap, emptyList())

        // 4. Film grain
        if (doc.grain) {
            PostEngine.drawGrain(canvas, w, h, 22)
        }

        // 5. Template decor shapes (panels respect panelOpacity; rotated decors supported)
        for (decor in layout.decor) {
            drawDecor(canvas, doc, layout, decor, tones, w, h, unit)
        }

        // 6. Template photo window (or multi-cell triptych / rotated polaroid / brand placeholder)
        if (layout.photo != null) {
            drawPhotoArea(context, canvas, doc, layout.photo, tones, w, h, unit, max(outW, outH))
        }
        if (cancelled()) return Result(bitmap, emptyList())

        // 7. Placed icons behind words
        doc.icons.filter { !it.front }.forEach { item ->
            PostIconStore.draw(canvas, item, w, h, unit, tones.accent)
        }

        val overflow = linkedSetOf<String>()

        // 8. Chapter summaries (`study-*`) use PostStudyGrid (untouched per scope rule)
        if (doc.templateId.startsWith("study-")) {
            val studySlots = layout.slots.filter { it.literal == null }.map { it.slot }.toSet()
            overflow.addAll(PostStudyGrid.draw(context, canvas, doc, w, h, studySlots))
        } else {
            val hasCutout = !doc.cutoutUri.isNullOrBlank()
            val behindBoxes = if (hasCutout) {
                layout.slots.filter { it.slot in doc.textBehind }
            } else {
                emptyList()
            }
            val frontBoxes = if (hasCutout) {
                layout.slots.filter { it.slot !in doc.textBehind }
            } else {
                layout.slots
            }

            drawSlotPass(context, canvas, bitmap, doc, layout, behindBoxes, tones, bgPhotoBitmap, w, h, unit, overflow)
            if (hasCutout) {
                drawCutout(context, canvas, doc, w, h, max(outW, outH))
            }
            drawSlotPass(context, canvas, bitmap, doc, layout, frontBoxes, tones, bgPhotoBitmap, w, h, unit, overflow)
        }

        // 9. Social grid cards (`mkt-follow`)
        if (layout.social != null) {
            drawSocial(context, canvas, doc, brand, layout.social, tones, w, h, unit)
        }

        // 10. Placed icons in front of words
        doc.icons.filter { it.front }.forEach { item ->
            PostIconStore.draw(canvas, item, w, h, unit, tones.accent)
        }

        // 11. Furniture: top-right logo header box and bottom footer row (skipped when layout.bare == true)
        if (!layout.bare) {
            if (doc.showLogo) {
                drawLogo(context, canvas, doc, brand, tones, w, h, unit)
            }
            if (doc.showFooter && layout.social == null) {
                drawFooter(context, canvas, doc, brand, layout, tones, w, h, unit)
            }
        }

        return Result(bitmap, overflow.toList())
    }

    private fun buildPalette(doc: PostDoc, brand: PostBrand, base: PostBase): TonePalette {
        val accent = parseColor(doc.accentHex) ?: parseColor(brand.primaryHex) ?: Color.parseColor("#22E0FF")
        val secondary = parseColor(brand.secondaryHex) ?: Color.parseColor("#FFD54A")
        val light = parseColor(brand.lightHex) ?: Color.WHITE
        val dark = parseColor(brand.darkHex) ?: Color.parseColor("#060B14")
        val deep = blend(dark, accent, 0.24f)
        val glow = blend(accent, Color.WHITE, 0.32f)
        val tint = blend(light, accent, 0.18f)
        val soft = Color.parseColor("#F5F2EA")
        val page = if (base == PostBase.LIGHT) soft else dark
        val defaultText = if (base == PostBase.LIGHT) dark else light
        val text = parseColor(doc.textHex) ?: defaultText
        val muted = blend(text, page, 0.36f)
        val paper = Color.parseColor("#FBF9F3")
        val blue = Color.rgb(23, 107, 206)
        val red = Color.rgb(209, 26, 38)
        return TonePalette(
            accent = accent,
            secondary = secondary,
            light = light,
            dark = dark,
            deep = deep,
            glow = glow,
            tint = tint,
            soft = soft,
            page = page,
            text = text,
            muted = muted,
            paper = paper,
            blue = blue,
            red = red
        )
    }

    private fun drawBaseBackground(
        canvas: Canvas,
        doc: PostDoc,
        brand: PostBrand,
        layout: TemplateLayout,
        tones: TonePalette,
        w: Int,
        h: Int
    ) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        when (doc.bgMode) {
            BgMode.SOLID -> {
                paint.color = parseColor(doc.bgStartHex) ?: tones.page
                canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), paint)
            }
            BgMode.GRADIENT -> {
                val c1 = parseColor(doc.bgStartHex) ?: tones.accent
                val c2 = parseColor(doc.bgEndHex) ?: tones.dark
                paint.shader = linearGradientByAngle(w, h, doc.bgAngle, intArrayOf(c1, c2))
                canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), paint)
            }
            BgMode.TEMPLATE -> {
                val look = if (layout.base == PostBase.DARK && !doc.templateId.startsWith("study-")) {
                    PostBackdrops.byId(brand.bgLook)
                } else null
                val bd = layout.backdrop
                if (bd != null && (layout.base == PostBase.LIGHT || look == null)) {
                    val c1 = tones.colorOf(bd.from)
                    val c2 = tones.colorOf(bd.to)
                    val colors = if (bd.mid != null) {
                        intArrayOf(c1, tones.colorOf(bd.mid), c2)
                    } else {
                        intArrayOf(c1, c2)
                    }
                    paint.shader = linearGradientByAngle(w, h, bd.angle, colors)
                    canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), paint)
                } else if (look != null) {
                    val c1 = parseColor(look.from) ?: tones.deep
                    val c2 = parseColor(look.to) ?: tones.dark
                    val colors = if (look.mid != null) {
                        intArrayOf(c1, parseColor(look.mid) ?: tones.deep, c2)
                    } else {
                        intArrayOf(c1, c2)
                    }
                    paint.shader = linearGradientByAngle(w, h, look.angle.toFloat(), colors)
                    canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), paint)
                    PostBackdrops.draw(canvas, look, w, h, tones.accent, tones.secondary)
                } else {
                    paint.color = tones.page
                    canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), paint)
                }
            }
        }
    }

    private fun resolveBackgroundPhoto(context: Context, doc: PostDoc, maxDim: Int): Bitmap? {
        val uri = doc.backgroundPhotoUri?.takeIf { it.isNotBlank() }
            ?: if (doc.photoAsBackground) doc.photoUri?.takeIf { it.isNotBlank() } else null
        if (uri == null) return null
        val raw = PostImageCache.get(context, uri, maxDim) ?: return null
        val blur = if (!doc.backgroundPhotoUri.isNullOrBlank()) {
            doc.backgroundPhotoBlur
        } else if (doc.photoAsBackground && doc.backgroundPhotoBlur == 0) {
            55
        } else {
            doc.backgroundPhotoBlur
        }
        return if (blur > 0) PostImageCache.blurred(uri, raw, blur) else raw
    }

    private fun drawBackgroundPhoto(
        canvas: Canvas,
        doc: PostDoc,
        bmp: Bitmap,
        tones: TonePalette,
        w: Int,
        h: Int
    ) {
        val dest = RectF(0f, 0f, w.toFloat(), h.toFloat())
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        paint.alpha = (doc.backgroundPhotoOpacity.coerceIn(0f, 1f) * 255f).roundToInt().coerceIn(0, 255)
        when (doc.backgroundPhotoTone) {
            BgPhotoTone.NONE -> Unit
            BgPhotoTone.MONO -> {
                val cm = ColorMatrix().apply { setSaturation(0f) }
                paint.colorFilter = ColorMatrixColorFilter(cm)
            }
            BgPhotoTone.DUOTONE -> {
                paint.colorFilter = duotoneFilter(tones.deep, tones.glow)
            }
        }
        drawBitmapCenterCrop(
            canvas = canvas,
            bmp = bmp,
            dest = dest,
            zoom = doc.backgroundPhotoZoom.coerceIn(1f, 3f),
            focusX = doc.backgroundPhotoFocusX.coerceIn(0f, 1f),
            focusY = doc.backgroundPhotoFocusY.coerceIn(0f, 1f),
            paint = paint
        )
        val effectiveDim = if (!doc.backgroundPhotoUri.isNullOrBlank()) {
            doc.backgroundPhotoDim
        } else if (doc.photoAsBackground && doc.backgroundPhotoDim == 0) {
            45
        } else {
            doc.backgroundPhotoDim
        }
        if (effectiveDim > 0) {
            val scrim = Paint(Paint.ANTI_ALIAS_FLAG)
            scrim.color = withAlpha(tones.dark, (effectiveDim.coerceIn(0, 100) * 2.25f).roundToInt())
            canvas.drawRect(dest, scrim)
        }
    }

    private fun duotoneFilter(darkColor: Int, lightColor: Int): ColorMatrixColorFilter {
        val dr = Color.red(darkColor) / 255f
        val dg = Color.green(darkColor) / 255f
        val db = Color.blue(darkColor) / 255f
        val lr = Color.red(lightColor) / 255f
        val lg = Color.green(lightColor) / 255f
        val lb = Color.blue(lightColor) / 255f
        val rDiff = lr - dr
        val gDiff = lg - dg
        val bDiff = lb - db
        val lumR = 0.299f
        val lumG = 0.587f
        val lumB = 0.114f
        val matrix = floatArrayOf(
            lumR * rDiff, lumG * rDiff, lumB * rDiff, 0f, dr * 255f,
            lumR * gDiff, lumG * gDiff, lumB * gDiff, 0f, dg * 255f,
            lumR * bDiff, lumG * bDiff, lumB * bDiff, 0f, db * 255f,
            0f, 0f, 0f, 1f, 0f
        )
        return ColorMatrixColorFilter(ColorMatrix(matrix))
    }

    private fun drawDecor(
        canvas: Canvas,
        doc: PostDoc,
        layout: TemplateLayout,
        decor: DecorBox,
        tones: TonePalette,
        w: Int,
        h: Int,
        unit: Float
    ) {
        val panelFactor = if (decor.panel) doc.panelOpacity.coerceIn(0, 100) / 100f else 1f
        val alpha = (decor.alpha.coerceIn(0, 255) * panelFactor).roundToInt().coerceIn(0, 255)
        if (alpha <= 0) return

        val rect = RectF(
            decor.x * w,
            decor.y * h,
            (decor.x + decor.w) * w,
            (decor.y + decor.h) * h
        )
        val baseColor = tones.colorOf(decor.tone)
        val color = withAlpha(baseColor, alpha)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = color

        val rotated = abs(decor.rotate) > 0.01f
        if (rotated) {
            canvas.save()
            canvas.rotate(decor.rotate, rect.centerX(), rect.centerY())
        }

        if (decor.frameBack == 1) {
            val frame = doc.photoFrame ?: layout.photo?.mask ?: PhotoFrame.ROUNDED
            val path = PostEngine.framePath(frame, rect, unit)
            canvas.drawPath(path, paint)
            if (rotated) canvas.restore()
            return
        }

        when (decor.shape) {
            DecorShape.RECT, DecorShape.BLOCK, DecorShape.BAR, DecorShape.LINE, DecorShape.SLANT -> {
                canvas.drawRect(rect, paint)
            }
            DecorShape.ROUND, DecorShape.CARD -> {
                val r = min(unit * 0.036f, min(rect.width(), rect.height()) * 0.32f)
                if (decor.panel && rect.height() > h * 0.14f) {
                    val sp = Paint(Paint.ANTI_ALIAS_FLAG)
                    sp.color = Color.argb((alpha * 0.24f).roundToInt(), 0, 0, 0)
                    canvas.drawRoundRect(
                        RectF(rect.left, rect.top + unit * 0.012f, rect.right, rect.bottom + unit * 0.012f),
                        r, r, sp
                    )
                }
                canvas.drawRoundRect(rect, r, r, paint)
                if (doc.templateId == "photo-glass" && decor.panel) {
                    val sheen = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        shader = LinearGradient(
                            rect.left, rect.top, rect.right, rect.bottom,
                            intArrayOf(
                                withAlpha(tones.light, 70),
                                withAlpha(tones.light, 18),
                                withAlpha(tones.accent, 28)
                            ),
                            floatArrayOf(0f, 0.5f, 1f),
                            Shader.TileMode.CLAMP
                        )
                    }
                    canvas.drawRoundRect(rect, r, r, sheen)
                    val frostRim = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        style = Paint.Style.STROKE
                        strokeWidth = max(2f, unit * 0.003f)
                        this.color = withAlpha(tones.light, 115)
                    }
                    canvas.drawRoundRect(rect, r, r, frostRim)
                } else if (layout.base == PostBase.DARK && rect.width() > w * 0.25f && rect.height() > h * 0.12f) {
                    val edge = Paint(Paint.ANTI_ALIAS_FLAG)
                    edge.style = Paint.Style.STROKE
                    edge.strokeWidth = max(1.5f, unit * 0.002f)
                    edge.color = withAlpha(tones.light, (alpha * 0.16f).roundToInt())
                    canvas.drawRoundRect(rect, r, r, edge)
                }
            }
            DecorShape.PILL -> {
                val r = min(rect.width(), rect.height()) / 2f
                canvas.drawRoundRect(rect, r, r, paint)
            }
            DecorShape.DISC -> {
                val r = min(rect.width(), rect.height()) / 2f
                canvas.drawCircle(rect.centerX(), rect.centerY(), r, paint)
            }
            DecorShape.GLOW, DecorShape.HALO -> {
                val r = max(rect.width(), rect.height()) / 2f
                if (r > 1f) {
                    paint.shader = RadialGradient(
                        rect.centerX(),
                        rect.centerY(),
                        r,
                        intArrayOf(color, withAlpha(baseColor, 0)),
                        floatArrayOf(0f, 1f),
                        Shader.TileMode.CLAMP
                    )
                    canvas.drawOval(rect, paint)
                }
            }
            DecorShape.FRAME, DecorShape.CORNER -> {
                val r = min(unit * 0.032f, min(rect.width(), rect.height()) * 0.24f)
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = max(2f, unit * 0.004f)
                canvas.drawRoundRect(rect, r, r, paint)
            }
            DecorShape.QUOTE -> {
                val qPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                    typeface = Typeface.create("serif", Typeface.BOLD)
                    textSize = max(rect.width(), rect.height()) * 1.15f
                    this.color = color
                }
                canvas.drawText("\u201C", rect.left, rect.top + qPaint.textSize * 0.78f, qPaint)
            }
            DecorShape.PHONE -> {
                val outerR = unit * 0.062f
                val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    this.color = Color.argb(110, 0, 0, 0)
                }
                canvas.drawRoundRect(
                    RectF(rect.left, rect.top + unit * 0.016f, rect.right, rect.bottom + unit * 0.016f),
                    outerR, outerR, shadowPaint
                )
                canvas.drawRoundRect(rect, outerR, outerR, paint)
                val rim = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    style = Paint.Style.STROKE
                    strokeWidth = max(2.5f, unit * 0.004f)
                    this.color = withAlpha(tones.light, 95)
                }
                canvas.drawRoundRect(rect, outerR, outerR, rim)
            }
            DecorShape.ARCH -> {
                val path = PostEngine.framePath(PhotoFrame.ARCH, rect, unit)
                canvas.drawPath(path, paint)
            }
            DecorShape.RING -> {
                val r = min(rect.width(), rect.height()) / 2f
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = max(2f, unit * 0.006f)
                canvas.drawCircle(rect.centerX(), rect.centerY(), r, paint)
                if (doc.templateId == "mkt-seal") {
                    val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        style = Paint.Style.FILL
                        this.color = color
                    }
                    val dotR = max(2.5f, unit * 0.0045f)
                    for (deg in 0 until 360 step 15) {
                        val rad = Math.toRadians(deg.toDouble())
                        val dx = cos(rad).toFloat() * (r * 0.90f)
                        val dy = sin(rad).toFloat() * (r * 0.90f)
                        canvas.drawCircle(rect.centerX() + dx, rect.centerY() + dy, dotR, dotPaint)
                    }
                }
            }
            DecorShape.DOTS, DecorShape.STRIPES, DecorShape.WAVE, DecorShape.GEM,
            DecorShape.DIAMOND, DecorShape.TRIANGLE, DecorShape.HEXAGON -> Unit
        }

        if (rotated) {
            canvas.restore()
        }
    }

    private fun drawPhotoArea(
        context: Context,
        canvas: Canvas,
        doc: PostDoc,
        area: PostPhotoArea,
        tones: TonePalette,
        w: Int,
        h: Int,
        unit: Float,
        maxDim: Int
    ) {
        val rect = RectF(
            area.x * w,
            area.y * h,
            (area.x + area.w) * w,
            (area.y + area.h) * h
        )
        val bmp = PostImageCache.get(context, doc.photoUri, maxDim)
        val rotated = abs(area.rotate) > 0.01f
        if (rotated) {
            canvas.save()
            canvas.rotate(area.rotate, rect.centerX(), rect.centerY())
        }

        val clipPath = Path()
        val isFullBleed = area.w >= 0.99f && area.h >= 0.99f && area.cells.isEmpty()
        val effectiveFrame = doc.photoFrame ?: area.mask

        if (area.cells.isNotEmpty()) {
            for (cell in area.cells) {
                val cr = RectF(cell.x * w, cell.y * h, (cell.x + cell.w) * w, (cell.y + cell.h) * h)
                val rad = cell.radius * unit
                clipPath.addRoundRect(cr, rad, rad, Path.Direction.CW)
            }
        } else if (!isFullBleed && effectiveFrame != null) {
            if (effectiveFrame == PhotoFrame.STACKED) {
                val backPaint = Paint(Paint.ANTI_ALIAS_FLAG)
                backPaint.color = withAlpha(tones.accent, 95)
                val offset = unit * 0.018f
                val backRect = RectF(rect.left + offset, rect.top + offset, rect.right + offset, rect.bottom + offset)
                canvas.drawPath(PostEngine.framePath(PhotoFrame.ROUNDED, backRect, unit), backPaint)
            }
            clipPath.addPath(PostEngine.framePath(effectiveFrame, rect, unit))
        } else {
            clipPath.addRect(rect, Path.Direction.CW)
        }

        canvas.save()
        canvas.clipPath(clipPath)
        if (doc.templateId == "photo-duo" && area.cells.size == 2) {
            val bmp2 = PostImageCache.get(context, doc.backgroundPhotoUri ?: doc.photoUri, maxDim)
            area.cells.forEachIndexed { idx, cell ->
                val cr = RectF(cell.x * w, cell.y * h, (cell.x + cell.w) * w, (cell.y + cell.h) * h)
                val cellBmp = if (idx == 0) bmp else (bmp2 ?: bmp)
                if (cellBmp != null) {
                    val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
                    val fx = if (idx == 0) (doc.photoFocusX * 0.7f).coerceIn(0f, 1f) else (0.3f + doc.photoFocusX * 0.7f).coerceIn(0f, 1f)
                    drawBitmapCenterCrop(
                        canvas = canvas,
                        bmp = cellBmp,
                        dest = cr,
                        zoom = doc.photoZoom.coerceIn(1f, 4f),
                        focusX = fx,
                        focusY = doc.photoFocusY.coerceIn(0f, 1f),
                        paint = paint
                    )
                } else {
                    val ph = Paint(Paint.ANTI_ALIAS_FLAG)
                    val cA = if (idx == 0) tones.accent else tones.glow
                    ph.shader = LinearGradient(
                        cr.left, cr.top, cr.right, cr.bottom,
                        intArrayOf(tones.deep, blend(cA, tones.dark, 0.42f), tones.dark),
                        floatArrayOf(0f, 0.55f, 1f),
                        Shader.TileMode.CLAMP
                    )
                    canvas.drawRect(cr, ph)
                }
            }
        } else if (bmp != null) {
            val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
            drawBitmapCenterCrop(
                canvas = canvas,
                bmp = bmp,
                dest = rect,
                zoom = doc.photoZoom.coerceIn(1f, 4f),
                focusX = doc.photoFocusX.coerceIn(0f, 1f),
                focusY = doc.photoFocusY.coerceIn(0f, 1f),
                paint = paint
            )
        } else {
            // Brand gradient placeholder for photo templates when no photo is picked yet (Section 3.8)
            val ph = Paint(Paint.ANTI_ALIAS_FLAG)
            ph.shader = LinearGradient(
                rect.left, rect.top, rect.right, rect.bottom,
                intArrayOf(tones.deep, blend(tones.accent, tones.dark, 0.45f), tones.dark),
                floatArrayOf(0f, 0.55f, 1f),
                Shader.TileMode.CLAMP
            )
            canvas.drawRect(rect, ph)
            val orb = Paint(Paint.ANTI_ALIAS_FLAG)
            orb.shader = RadialGradient(
                rect.centerX(),
                rect.top + rect.height() * 0.35f,
                max(1f, max(rect.width(), rect.height()) * 0.6f),
                intArrayOf(withAlpha(tones.glow, 95), withAlpha(tones.glow, 0)),
                floatArrayOf(0f, 1f),
                Shader.TileMode.CLAMP
            )
            canvas.drawRect(rect, orb)
        }

        val dim = (doc.photoDim ?: area.dim).coerceIn(0, 255)
        if (dim > 0) {
            val dimPaint = Paint(Paint.ANTI_ALIAS_FLAG)
            dimPaint.color = withAlpha(tones.dark, dim)
            canvas.drawRect(rect, dimPaint)
        }
        if (area.fadeBottom) {
            val fade = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = LinearGradient(
                    0f, rect.top + rect.height() * 0.35f, 0f, rect.bottom,
                    intArrayOf(withAlpha(tones.dark, 0), withAlpha(tones.dark, 200), withAlpha(tones.dark, 248)),
                    floatArrayOf(0f, 0.65f, 1f),
                    Shader.TileMode.CLAMP
                )
            }
            canvas.drawRect(rect, fade)
        }
        if (area.fadeTop) {
            val fade = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = LinearGradient(
                    0f, rect.top, 0f, rect.top + rect.height() * 0.45f,
                    intArrayOf(withAlpha(tones.dark, 220), withAlpha(tones.dark, 0)),
                    floatArrayOf(0f, 1f),
                    Shader.TileMode.CLAMP
                )
            }
            canvas.drawRect(rect, fade)
        }
        if (area.fadeRight) {
            val fade = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = LinearGradient(
                    rect.left + rect.width() * 0.28f, 0f, rect.right, 0f,
                    intArrayOf(withAlpha(tones.dark, 0), withAlpha(tones.dark, 240)),
                    floatArrayOf(0f, 1f),
                    Shader.TileMode.CLAMP
                )
            }
            canvas.drawRect(rect, fade)
        }
        canvas.restore()

        if (doc.frameBorder && (!isFullBleed || area.cells.isNotEmpty())) {
            val border = Paint(Paint.ANTI_ALIAS_FLAG)
            border.style = Paint.Style.STROKE
            border.strokeWidth = max(3f, unit * 0.005f)
            border.color = tones.light
            canvas.drawPath(clipPath, border)
        }

        if (rotated) {
            canvas.restore()
        }
    }

    private fun drawCutout(
        context: Context,
        canvas: Canvas,
        doc: PostDoc,
        w: Int,
        h: Int,
        maxDim: Int
    ) {
        val bmp = PostImageCache.get(context, doc.cutoutUri, maxDim) ?: return
        val targetH = h * 0.62f * doc.cutoutScale.coerceIn(0.3f, 2.5f)
        val scale = targetH / max(1, bmp.height).toFloat()
        val drawW = bmp.width * scale
        val drawH = bmp.height * scale
        val cx = doc.cutoutX.coerceIn(0f, 1f) * w
        val bottomY = doc.cutoutY.coerceIn(0.1f, 1.05f) * h
        val dest = RectF(cx - drawW / 2f, bottomY - drawH, cx + drawW / 2f, bottomY)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        canvas.drawBitmap(bmp, null, dest, paint)
    }

    private fun drawSlotPass(
        context: Context,
        canvas: Canvas,
        canvasBitmap: Bitmap,
        doc: PostDoc,
        layout: TemplateLayout,
        boxes: List<SlotBox>,
        tones: TonePalette,
        bgPhotoBitmap: Bitmap?,
        w: Int,
        h: Int,
        unit: Float,
        overflow: MutableSet<String>
    ) {
        if (boxes.isEmpty()) return
        val clipSafeBand = !layout.bleed && !layout.bare
        if (clipSafeBand) {
            canvas.save()
            val topSafe = if (doc.size == PostSize.STORY) h * 0.12f else h * 0.05f
            val bottomSafe = if (doc.showFooter && layout.social == null) h * 0.90f else h * 0.96f
            canvas.clipRect(0f, topSafe, w.toFloat(), bottomSafe)
        }

        for (box in boxes) {
            if (box.literal == null && box.slot in doc.hiddenTextSlots) continue
            val slotStyle = doc.slotStyles[box.slot]
            val hasManualSize = slotStyle?.sizeUnit != null
            if (hasManualSize && clipSafeBand) {
                // Manual sizeUnit is allowed to cross the box and safe band (Section 3.1)
                canvas.restore()
                drawSingleBox(context, canvas, canvasBitmap, doc, layout, box, tones, bgPhotoBitmap, w, h, unit, overflow)
                canvas.save()
                val topSafe = if (doc.size == PostSize.STORY) h * 0.12f else h * 0.05f
                val bottomSafe = if (doc.showFooter && layout.social == null) h * 0.90f else h * 0.96f
                canvas.clipRect(0f, topSafe, w.toFloat(), bottomSafe)
            } else {
                drawSingleBox(context, canvas, canvasBitmap, doc, layout, box, tones, bgPhotoBitmap, w, h, unit, overflow)
            }
        }

        if (clipSafeBand) {
            canvas.restore()
        }
    }

    private fun drawSingleBox(
        context: Context,
        canvas: Canvas,
        canvasBitmap: Bitmap,
        doc: PostDoc,
        layout: TemplateLayout,
        box: SlotBox,
        tones: TonePalette,
        bgPhotoBitmap: Bitmap?,
        w: Int,
        h: Int,
        unit: Float,
        overflow: MutableSet<String>
    ) {
        if (box.list != ListStyle.NONE) {
            drawList(context, canvas, canvasBitmap, doc, layout, box, tones, w, h, unit, overflow)
        } else {
            drawSlot(context, canvas, canvasBitmap, doc, layout, box, tones, bgPhotoBitmap, w, h, unit, overflow)
        }
    }

    private fun resolveShadow(doc: PostDoc, box: SlotBox, slotStyle: SlotStyle?): TextShadow? {
        slotStyle?.shadow?.let { return if (it.strength > 0) it else null }
        if (!doc.shadowAll.isDefault) return if (doc.shadowAll.strength > 0) doc.shadowAll else null
        box.shadow?.let { return if (it.strength > 0) it else null }
        return null
    }

    private fun configureShadowPaint(
        paint: Paint,
        shadow: TextShadow,
        fontSize: Float,
        textColor: Int,
        tones: TonePalette
    ) {
        paint.style = Paint.Style.FILL
        val customColor = parseColor(shadow.colorHex)
        val isLightText = luminance(textColor) > 0.45f
        when (shadow.mode) {
            ShadowMode.SOFT -> {
                val base = customColor ?: if (isLightText) Color.BLACK else tones.soft
                val alpha = (shadow.strength.coerceIn(0, 100) / 100f * 0.85f * 255f).roundToInt().coerceIn(0, 255)
                paint.color = withAlpha(base, alpha)
                val radius = max(1f, fontSize * (0.02f + shadow.blur.coerceIn(0, 100) / 100f * 0.25f))
                paint.maskFilter = BlurMaskFilter(radius, BlurMaskFilter.Blur.NORMAL)
            }
            ShadowMode.HARD -> {
                val base = customColor ?: if (isLightText) Color.BLACK else tones.dark
                val alpha = (shadow.strength.coerceIn(0, 100) / 100f * 0.95f * 255f).roundToInt().coerceIn(0, 255)
                paint.color = withAlpha(base, alpha)
                paint.maskFilter = null
            }
            ShadowMode.GLOW -> {
                val base = customColor ?: tones.accent
                val alpha = (shadow.strength.coerceIn(0, 100) / 100f * 0.92f * 255f).roundToInt().coerceIn(0, 255)
                paint.color = withAlpha(base, alpha)
                val radius = max(1.5f, fontSize * (0.03f + shadow.blur.coerceIn(0, 100) / 100f * 0.30f))
                paint.maskFilter = BlurMaskFilter(radius, BlurMaskFilter.Blur.NORMAL)
            }
        }
    }

    private fun shadowOffset(shadow: TextShadow, fontSize: Float): Pair<Float, Float> {
        if (shadow.mode == ShadowMode.GLOW) return 0f to 0f
        val dist = fontSize * (shadow.distance.coerceIn(0, 100) / 100f) * 0.12f
        val rad = Math.toRadians(shadow.angle.toDouble())
        return (cos(rad).toFloat() * dist) to (sin(rad).toFloat() * dist)
    }

    private fun drawSlot(
        context: Context,
        canvas: Canvas,
        canvasBitmap: Bitmap,
        doc: PostDoc,
        layout: TemplateLayout,
        box: SlotBox,
        tones: TonePalette,
        bgPhotoBitmap: Bitmap?,
        w: Int,
        h: Int,
        unit: Float,
        overflow: MutableSet<String>
    ) {
        val isLiteral = box.literal != null
        val raw = (box.literal ?: doc.texts[box.slot]).orEmpty().trim()
        if (raw.isEmpty()) return

        val slotStyle = if (isLiteral) doc.slotStyles[PostSlots.MARKS] else doc.slotStyles[box.slot]
        val caps = if (isLiteral) box.caps else (slotStyle?.caps ?: box.caps)
        val processedMath = if ('$' in raw) {
            raw.replace(Regex("\\$\\$([^$]+)\\$\\$|\\$([^$]+)\\$")) { m ->
                val inner = m.groupValues[1].ifEmpty { m.groupValues[2] }
                PostMath.toText(inner)
            }
        } else {
            raw
        }
        val text = if (caps && !PostSlots.containsEthiopic(processedMath)) {
            processedMath.uppercase(Locale.getDefault())
        } else {
            processedMath
        }

        val tf = PostFonts.get(context, box.font, text, doc, if (isLiteral) null else slotStyle)
        val fx = if (isLiteral) box.fx else (slotStyle?.fx ?: box.fx)
        val tracking = if (isLiteral) box.tracking else (slotStyle?.tracking ?: box.tracking)
        val leading = if (isLiteral) box.leading else (slotStyle?.leading ?: box.leading)
        val rotate = if (isLiteral) box.rotate else (slotStyle?.rotate ?: box.rotate)
        val align = when (if (isLiteral) null else slotStyle?.align) {
            TextAlign.LEFT -> Layout.Alignment.ALIGN_NORMAL
            TextAlign.CENTER -> Layout.Alignment.ALIGN_CENTER
            TextAlign.RIGHT -> Layout.Alignment.ALIGN_OPPOSITE
            null -> if (box.centered) Layout.Alignment.ALIGN_CENTER else Layout.Alignment.ALIGN_NORMAL
        }

        val rect = if (isLiteral) {
            RectF(box.x * w, box.y * h, (box.x + box.w) * w, (box.y + box.h) * h)
        } else {
            PostEngine.safeSlotRect(doc, layout, box, w.toFloat(), h.toFloat())
        }
        val boxW = max(24f, rect.width())
        val boxH = max(18f, rect.height())

        val rawColor = parseColor(slotStyle?.colorHex)
            ?: if (box.tone == PostTone.TEXT && !doc.textHex.isNullOrBlank()) {
                parseColor(doc.textHex) ?: tones.colorOf(box.tone)
            } else {
                tones.colorOf(box.tone)
            }

        val skipContrastGuard = isLiteral || box.chip != null ||
            fx in setOf(TextFx.OUTLINE, TextFx.GRADIENT, TextFx.PHOTO_FILL, TextFx.ECHO, TextFx.EXTRUDE, TextFx.SPLIT_TONE)
        val color = if (skipContrastGuard) {
            rawColor
        } else {
            readable(canvasBitmap, rect, rawColor, tones.light, tones.dark)
        }

        val shadow = resolveShadow(doc, box, if (isLiteral) null else slotStyle)
        val manualSizeUnit = if (isLiteral) null else slotStyle?.sizeUnit
        val postScale = doc.textScale.coerceIn(0.25f, 4.0f)
        val slotScale = (slotStyle?.scale ?: 1f).coerceIn(0.25f, 4.0f)

        val rotated = abs(rotate) > 0.01f
        if (rotated) {
            canvas.save()
            canvas.rotate(rotate, rect.centerX(), rect.centerY())
        }

        when (box.fit) {
            FitMode.STACK -> {
                drawStackSlot(
                    canvas = canvas,
                    text = text,
                    tf = tf,
                    rect = rect,
                    box = box,
                    color = color,
                    tones = tones,
                    fx = fx,
                    tracking = tracking,
                    leading = leading,
                    shadow = shadow,
                    manualSizePx = manualSizeUnit?.let { it * unit * postScale },
                    scaleMultiplier = postScale * slotScale,
                    bgPhotoBitmap = bgPhotoBitmap,
                    doc = doc,
                    w = w,
                    h = h
                )
            }
            FitMode.TICKER -> {
                drawTickerSlot(
                    canvas = canvas,
                    text = text,
                    tf = tf,
                    rect = rect,
                    box = box,
                    color = color,
                    tones = tones,
                    tracking = tracking,
                    shadow = shadow,
                    unit = unit,
                    manualSizePx = manualSizeUnit?.let { it * unit * postScale },
                    scaleMultiplier = postScale * slotScale
                )
            }
            FitMode.TILES -> {
                drawTilesSlot(
                    canvas = canvas,
                    text = text,
                    tf = tf,
                    rect = rect,
                    color = color,
                    tones = tones,
                    shadow = shadow,
                    unit = unit,
                    panelOpacity = doc.panelOpacity
                )
            }
            FitMode.CHIPS -> {
                drawChipsSlot(
                    canvas = canvas,
                    text = text,
                    tf = tf,
                    rect = rect,
                    tones = tones,
                    shadow = shadow,
                    unit = unit,
                    manualSizePx = manualSizeUnit?.let { it * unit * postScale },
                    scaleMultiplier = postScale * slotScale
                )
            }
            FitMode.FILL_WIDTH -> {
                val forceParagraph = box.chip != null ||
                    box.slot == PostSlots.EYEBROW ||
                    box.slot == PostSlots.BUTTON ||
                    box.slot == PostSlots.BODY ||
                    box.slot == PostSlots.LIST
                if (forceParagraph) {
                    drawParagraphSlot(
                        canvas = canvas,
                        rawText = text,
                        tf = tf,
                        rect = rect,
                        boxW = boxW,
                        boxH = boxH,
                        box = box,
                        layout = layout,
                        align = align,
                        color = color,
                        tones = tones,
                        fx = fx,
                        tracking = tracking,
                        leading = leading,
                        shadow = shadow,
                        unit = unit,
                        manualSizePx = manualSizeUnit?.let { it * unit * postScale },
                        postScale = postScale,
                        slotScale = slotScale,
                        bgPhotoBitmap = bgPhotoBitmap,
                        doc = doc,
                        w = w,
                        h = h,
                        isLiteral = isLiteral,
                        overflow = overflow
                    )
                } else {
                    drawFillWidthSlot(
                        canvas = canvas,
                        text = text,
                        tf = tf,
                        rect = rect,
                        box = box,
                        layout = layout,
                        color = color,
                        tones = tones,
                        fx = fx,
                        tracking = tracking,
                        shadow = shadow,
                        manualSizePx = manualSizeUnit?.let { it * unit * postScale },
                        scaleMultiplier = postScale * slotScale,
                        bgPhotoBitmap = bgPhotoBitmap,
                        doc = doc,
                        w = w,
                        h = h
                    )
                }
            }
            FitMode.FILL, FitMode.LEGACY -> {
                val cleanText = stripInlineMarkup(text)
                val isSingleGlyphHero = box.fit == FitMode.FILL &&
                    (box.slot == PostSlots.LETTER || PostSlots.graphemes(cleanText, 4).size <= 2) &&
                    !cleanText.contains(' ') && !cleanText.contains('\n')

                if (isSingleGlyphHero) {
                    drawInkFittedGlyph(
                        canvas = canvas,
                        text = cleanText,
                        tf = tf,
                        rect = rect,
                        box = box,
                        color = color,
                        tones = tones,
                        fx = fx,
                        tracking = tracking,
                        shadow = shadow,
                        manualSizePx = manualSizeUnit?.let { it * unit * postScale },
                        scaleMultiplier = postScale * slotScale,
                        bgPhotoBitmap = bgPhotoBitmap,
                        doc = doc,
                        w = w,
                        h = h
                    )
                } else {
                    drawParagraphSlot(
                        canvas = canvas,
                        rawText = text,
                        tf = tf,
                        rect = rect,
                        boxW = boxW,
                        boxH = boxH,
                        box = box,
                        layout = layout,
                        align = align,
                        color = color,
                        tones = tones,
                        fx = fx,
                        tracking = tracking,
                        leading = leading,
                        shadow = shadow,
                        unit = unit,
                        manualSizePx = manualSizeUnit?.let { it * unit * postScale },
                        postScale = postScale,
                        slotScale = slotScale,
                        bgPhotoBitmap = bgPhotoBitmap,
                        doc = doc,
                        w = w,
                        h = h,
                        isLiteral = isLiteral,
                        overflow = overflow
                    )
                }
            }
        }

        if (rotated) {
            canvas.restore()
        }
    }

    /**
     * Draws a single letter or 2-character token ink-fitted via Paint.getTextBounds so cap-height and side bearings touch the box edges.
     */
    private fun drawInkFittedGlyph(
        canvas: Canvas,
        text: String,
        tf: Typeface,
        rect: RectF,
        box: SlotBox,
        color: Int,
        tones: TonePalette,
        fx: TextFx,
        tracking: Float,
        shadow: TextShadow?,
        manualSizePx: Float?,
        scaleMultiplier: Float,
        bgPhotoBitmap: Bitmap?,
        doc: PostDoc,
        w: Int,
        h: Int
    ) {
        val paint = TextPaint(Paint.ANTI_ALIAS_FLAG)
        paint.typeface = tf
        paint.letterSpacing = tracking
        val bounds = Rect()

        val fittedPx = if (manualSizePx != null) {
            manualSizePx
        } else {
            paint.textSize = 200f
            paint.getTextBounds(text, 0, text.length, bounds)
            val bw = max(1, bounds.width()).toFloat()
            val bh = max(1, bounds.height()).toFloat()
            val scaleW = (rect.width() * 0.94f) / bw
            val scaleH = (rect.height() * 0.94f) / bh
            (200f * min(scaleW, scaleH) * scaleMultiplier).coerceIn(24f, max(w, h) * 1.4f)
        }

        paint.textSize = fittedPx
        paint.getTextBounds(text, 0, text.length, bounds)
        val drawX = rect.centerX() - bounds.exactCenterX()
        val drawY = when (box.anchor) {
            VAnchor.TOP -> rect.top - bounds.top
            VAnchor.BOTTOM -> rect.bottom - bounds.bottom
            VAnchor.CENTER -> rect.centerY() - bounds.exactCenterY()
        }

        drawStyledSingleLine(
            canvas = canvas,
            text = text,
            drawX = drawX,
            drawY = drawY,
            paint = paint,
            rect = rect,
            box = box,
            color = color,
            tones = tones,
            fx = fx,
            shadow = shadow,
            bgPhotoBitmap = bgPhotoBitmap,
            doc = doc,
            w = w,
            h = h
        )
    }

    private fun drawFillWidthSlot(
        canvas: Canvas,
        text: String,
        tf: Typeface,
        rect: RectF,
        box: SlotBox,
        layout: TemplateLayout,
        color: Int,
        tones: TonePalette,
        fx: TextFx,
        tracking: Float,
        shadow: TextShadow?,
        manualSizePx: Float?,
        scaleMultiplier: Float,
        bgPhotoBitmap: Bitmap?,
        doc: PostDoc,
        w: Int,
        h: Int
    ) {
        val line = stripInlineMarkup(text).replace('\n', ' ').trim()
        if (line.isEmpty()) return
        val paint = TextPaint(Paint.ANTI_ALIAS_FLAG)
        paint.typeface = tf
        paint.letterSpacing = tracking
        val bounds = Rect()

        var sizePx = if (manualSizePx != null) {
            manualSizePx
        } else {
            paint.textSize = 160f
            val measuredW = max(1f, paint.measureText(line))
            (160f * (rect.width() / measuredW) * scaleMultiplier).coerceIn(20f, max(w, h) * 1.3f)
        }
        paint.textSize = sizePx
        paint.getTextBounds(line, 0, line.length, bounds)

        val allowBleed = layout.bleed && (box.slot == PostSlots.LETTER || box.slot == PostSlots.WORD)
        if (manualSizePx == null && !allowBleed && bounds.height() > rect.height() * 0.90f) {
            val scaleH = (rect.height() * 0.90f) / max(1f, bounds.height().toFloat())
            sizePx = (sizePx * scaleH).coerceAtLeast(18f)
            paint.textSize = sizePx
            paint.getTextBounds(line, 0, line.length, bounds)
        }

        val drawX = if (box.centered) {
            rect.centerX() - paint.measureText(line) / 2f
        } else {
            rect.left
        }
        val drawY = when (box.anchor) {
            VAnchor.TOP -> rect.top - bounds.top
            VAnchor.BOTTOM -> rect.bottom - bounds.bottom
            VAnchor.CENTER -> rect.centerY() - bounds.exactCenterY()
        }

        drawStyledSingleLine(
            canvas = canvas,
            text = line,
            drawX = drawX,
            drawY = drawY,
            paint = paint,
            rect = rect,
            box = box,
            color = color,
            tones = tones,
            fx = fx,
            shadow = shadow,
            bgPhotoBitmap = bgPhotoBitmap,
            doc = doc,
            w = w,
            h = h
        )
    }

    private fun drawStackSlot(
        canvas: Canvas,
        text: String,
        tf: Typeface,
        rect: RectF,
        box: SlotBox,
        color: Int,
        tones: TonePalette,
        fx: TextFx,
        tracking: Float,
        leading: Float,
        shadow: TextShadow?,
        manualSizePx: Float?,
        scaleMultiplier: Float,
        bgPhotoBitmap: Bitmap?,
        doc: PostDoc,
        w: Int,
        h: Int
    ) {
        val clean = stripInlineMarkup(text)
        val rawParts = if ('\n' in clean) {
            clean.split('\n').map { it.trim() }.filter { it.isNotEmpty() }
        } else {
            clean.split(Regex("\\s+")).map { it.trim() }.filter { it.isNotEmpty() }
        }
        val lines = if (rawParts.size > 6) rawParts.take(6) else rawParts
        if (lines.isEmpty()) return

        val paint = TextPaint(Paint.ANTI_ALIAS_FLAG)
        paint.typeface = tf
        paint.letterSpacing = tracking
        val bounds = Rect()

        val sizes = FloatArray(lines.size)
        val heights = FloatArray(lines.size)
        var totalH = 0f
        for ((i, line) in lines.withIndex()) {
            paint.textSize = 140f
            val mw = max(1f, paint.measureText(line))
            val s = if (manualSizePx != null) manualSizePx else (140f * (rect.width() / mw))
            sizes[i] = s.coerceIn(22f, max(w, h) * 0.65f)
            paint.textSize = sizes[i]
            paint.getTextBounds(line, 0, line.length, bounds)
            val lh = max(sizes[i] * 0.72f, bounds.height().toFloat())
            heights[i] = lh
            totalH += lh * (if (i < lines.lastIndex) leading.coerceIn(0.75f, 1.3f) else 1f)
        }

        val fitRatio = if (manualSizePx == null && totalH > rect.height() && totalH > 1f) {
            (rect.height() / totalH) * scaleMultiplier
        } else {
            scaleMultiplier
        }

        val scaledTotalH = totalH * fitRatio
        var curY = when (box.anchor) {
            VAnchor.TOP -> rect.top
            VAnchor.BOTTOM -> rect.bottom - scaledTotalH
            VAnchor.CENTER -> rect.centerY() - scaledTotalH / 2f
        }

        for ((i, line) in lines.withIndex()) {
            val lineSize = sizes[i] * fitRatio
            paint.textSize = lineSize
            paint.getTextBounds(line, 0, line.length, bounds)
            val lineFx = if (box.stackFx.isNotEmpty()) box.stackFx[i % box.stackFx.size] else fx
            val lineColor = if (box.stackFx.isNotEmpty() && i % 3 == 2) tones.accent else color
            val drawX = rect.centerX() - paint.measureText(line) / 2f
            val baselineY = curY - bounds.top
            drawStyledSingleLine(
                canvas = canvas,
                text = line,
                drawX = drawX,
                drawY = baselineY,
                paint = paint,
                rect = RectF(rect.left, curY, rect.right, curY + heights[i] * fitRatio),
                box = box,
                color = lineColor,
                tones = tones,
                fx = lineFx,
                shadow = shadow,
                bgPhotoBitmap = bgPhotoBitmap,
                doc = doc,
                w = w,
                h = h
            )
            curY += heights[i] * fitRatio * leading.coerceIn(0.75f, 1.3f)
        }
    }

    private fun drawTickerSlot(
        canvas: Canvas,
        text: String,
        tf: Typeface,
        rect: RectF,
        box: SlotBox,
        color: Int,
        tones: TonePalette,
        tracking: Float,
        shadow: TextShadow?,
        unit: Float,
        manualSizePx: Float?,
        scaleMultiplier: Float
    ) {
        val clean = stripInlineMarkup(text).replace('\n', ' ').trim()
        if (clean.isEmpty()) return
        val repeated = buildString {
            repeat(8) { idx ->
                if (idx > 0) append(" \u2022 ")
                append(clean)
            }
        }
        val paint = TextPaint(Paint.ANTI_ALIAS_FLAG)
        paint.typeface = tf
        paint.letterSpacing = tracking
        paint.textSize = (manualSizePx ?: (box.size * unit * scaleMultiplier)).coerceIn(18f, rect.height() * 0.82f)
        paint.color = color

        val fm = paint.fontMetrics
        val baseline = rect.centerY() - (fm.ascent + fm.descent) / 2f
        canvas.save()
        canvas.clipRect(rect)
        val startX = rect.left - unit * 0.04f
        if (shadow != null && shadow.strength > 0) {
            val sp = TextPaint(paint)
            configureShadowPaint(sp, shadow, paint.textSize, color, tones)
            val (dx, dy) = shadowOffset(shadow, paint.textSize)
            canvas.drawText(repeated, startX + dx, baseline + dy, sp)
        }
        canvas.drawText(repeated, startX, baseline, paint)
        canvas.restore()
    }

    private fun drawTilesSlot(
        canvas: Canvas,
        text: String,
        tf: Typeface,
        rect: RectF,
        color: Int,
        tones: TonePalette,
        shadow: TextShadow?,
        unit: Float,
        panelOpacity: Int
    ) {
        val chars = PostSlots.graphemes(stripInlineMarkup(text), 7)
        if (chars.isEmpty()) return
        val count = chars.size
        val gap = unit * 0.014f
        val totalGap = gap * (count - 1)
        val tileW = max(24f, (rect.width() - totalGap) / count)
        val tileH = rect.height()
        val radius = unit * 0.024f
        val panelAlpha = (242 * (panelOpacity.coerceIn(0, 100) / 100f)).roundToInt().coerceIn(0, 255)

        val tilePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = withAlpha(tones.deep, panelAlpha)
        }
        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = max(2f, unit * 0.003f)
            this.color = withAlpha(tones.accent, (panelAlpha * 0.65f).roundToInt())
        }
        val splitPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            strokeWidth = max(2f, unit * 0.0025f)
            this.color = withAlpha(tones.dark, (panelAlpha * 0.85f).roundToInt())
        }

        val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = tf
            this.color = color
        }
        val bounds = Rect()

        for ((i, ch) in chars.withIndex()) {
            val left = rect.left + i * (tileW + gap)
            val tr = RectF(left, rect.top, left + tileW, rect.top + tileH)
            canvas.drawRoundRect(tr, radius, radius, tilePaint)
            canvas.drawRoundRect(tr, radius, radius, borderPaint)

            textPaint.textSize = min(tileW * 0.78f, tileH * 0.68f)
            textPaint.getTextBounds(ch, 0, ch.length, bounds)
            val cx = tr.centerX() - bounds.exactCenterX()
            val cy = tr.centerY() - bounds.exactCenterY()

            if (shadow != null && shadow.strength > 0) {
                val sp = TextPaint(textPaint)
                configureShadowPaint(sp, shadow, textPaint.textSize, color, tones)
                val (dx, dy) = shadowOffset(shadow, textPaint.textSize)
                canvas.drawText(ch, cx + dx, cy + dy, sp)
            }
            canvas.drawText(ch, cx, cy, textPaint)
            canvas.drawLine(tr.left + unit * 0.006f, tr.centerY(), tr.right - unit * 0.006f, tr.centerY(), splitPaint)
        }
    }

    private fun drawChipsSlot(
        canvas: Canvas,
        text: String,
        tf: Typeface,
        rect: RectF,
        tones: TonePalette,
        shadow: TextShadow?,
        unit: Float,
        manualSizePx: Float?,
        scaleMultiplier: Float
    ) {
        val words = stripInlineMarkup(text).split(Regex("\\s+")).map { it.trim() }.filter { it.isNotEmpty() }.take(8)
        if (words.isEmpty()) return

        val count = words.size
        val basePx = manualSizePx ?: ((rect.height() / max(2f, (count + 1) * 0.62f)) * 0.58f * scaleMultiplier)
        val fontSize = basePx.coerceIn(unit * 0.042f, unit * 0.125f)
        val paint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = tf
            textSize = fontSize
        }

        val padX = fontSize * 0.42f
        val padY = fontSize * 0.24f
        val gapX = fontSize * 0.24f
        val gapY = fontSize * 0.26f
        val chipH = fontSize + padY * 2f

        class ChipItem(val word: String, val w: Float)
        val items = words.map { w -> ChipItem(w, paint.measureText(w) + padX * 2f) }

        val rows = mutableListOf<MutableList<ChipItem>>()
        var currentRow = mutableListOf<ChipItem>()
        var rowW = 0f
        for (item in items) {
            val nextW = if (currentRow.isEmpty()) item.w else rowW + gapX + item.w
            if (currentRow.isNotEmpty() && nextW > rect.width()) {
                rows.add(currentRow)
                currentRow = mutableListOf(item)
                rowW = item.w
            } else {
                currentRow.add(item)
                rowW = nextW
            }
        }
        if (currentRow.isNotEmpty()) rows.add(currentRow)

        val totalH = rows.size * chipH + max(0, rows.size - 1) * gapY
        var curY = rect.centerY() - totalH / 2f
        val chipColors = listOf(
            tones.accent to tones.dark,
            tones.light to tones.dark,
            tones.secondary to tones.dark,
            tones.deep to tones.light
        )
        val angles = floatArrayOf(-3.5f, 2.8f, -2.0f, 3.6f, -2.6f, 2.2f)
        val fm = paint.fontMetrics
        var globalIdx = 0

        for (row in rows) {
            val rw = row.sumOf { it.w.toDouble() }.toFloat() + max(0, row.size - 1) * gapX
            var curX = rect.left + (rect.width() - rw).coerceAtLeast(0f) * 0.15f
            for (item in row) {
                val (bgCol, fgCol) = chipColors[globalIdx % chipColors.size]
                val ang = angles[globalIdx % angles.size]
                val cr = RectF(curX, curY, curX + item.w, curY + chipH)
                val rad = chipH * 0.34f

                canvas.save()
                canvas.rotate(ang, cr.centerX(), cr.centerY())

                val sp = Paint(Paint.ANTI_ALIAS_FLAG)
                sp.color = Color.argb(115, 0, 0, 0)
                val off = fontSize * 0.09f
                canvas.drawRoundRect(RectF(cr.left + off, cr.top + off, cr.right + off, cr.bottom + off), rad, rad, sp)

                val bg = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = bgCol }
                canvas.drawRoundRect(cr, rad, rad, bg)

                paint.color = fgCol
                if (shadow != null && shadow.strength > 0) {
                    val tsp = TextPaint(paint)
                    configureShadowPaint(tsp, shadow, fontSize, fgCol, tones)
                    val (dx, dy) = shadowOffset(shadow, fontSize)
                    canvas.drawText(item.word, cr.left + padX + dx, cr.centerY() - (fm.ascent + fm.descent) / 2f + dy, tsp)
                }
                canvas.drawText(item.word, cr.left + padX, cr.centerY() - (fm.ascent + fm.descent) / 2f, paint)
                canvas.restore()

                curX += item.w + gapX
                globalIdx++
            }
            curY += chipH + gapY
        }
    }

    private fun drawStyledSingleLine(
        canvas: Canvas,
        text: String,
        drawX: Float,
        drawY: Float,
        paint: TextPaint,
        rect: RectF,
        box: SlotBox,
        color: Int,
        tones: TonePalette,
        fx: TextFx,
        shadow: TextShadow?,
        bgPhotoBitmap: Bitmap?,
        doc: PostDoc,
        w: Int,
        h: Int
    ) {
        val fontSize = paint.textSize
        if (shadow != null && shadow.strength > 0) {
            val sp = TextPaint(paint)
            configureShadowPaint(sp, shadow, fontSize, color, tones)
            val (dx, dy) = shadowOffset(shadow, fontSize)
            canvas.drawText(text, drawX + dx, drawY + dy, sp)
        }

        when (fx) {
            TextFx.SOLID -> {
                val p = TextPaint(paint).apply {
                    style = Paint.Style.FILL
                    this.color = color
                }
                canvas.drawText(text, drawX, drawY, p)
            }
            TextFx.OUTLINE -> {
                val p = TextPaint(paint).apply {
                    style = Paint.Style.STROKE
                    strokeWidth = max(2f, fontSize * 0.03f)
                    strokeJoin = Paint.Join.ROUND
                    strokeCap = Paint.Cap.ROUND
                    this.color = color
                }
                canvas.drawText(text, drawX, drawY, p)
            }
            TextFx.GRADIENT -> {
                val p = TextPaint(paint).apply {
                    style = Paint.Style.FILL
                    val toColor = box.fxTo?.let { tones.colorOf(it) } ?: tones.accent
                    shader = LinearGradient(
                        rect.left, rect.top, rect.right, rect.bottom,
                        intArrayOf(tones.glow, tones.secondary, toColor),
                        floatArrayOf(0f, 0.5f, 1f),
                        Shader.TileMode.CLAMP
                    )
                }
                canvas.drawText(text, drawX, drawY, p)
            }
            TextFx.PHOTO_FILL -> {
                if (bgPhotoBitmap != null) {
                    val saveCount = canvas.saveLayer(0f, 0f, w.toFloat(), h.toFloat(), null)
                    val maskPaint = TextPaint(paint).apply {
                        style = Paint.Style.FILL
                        this.color = Color.WHITE
                    }
                    canvas.drawText(text, drawX, drawY, maskPaint)
                    val srcInPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG).apply {
                        xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
                    }
                    drawBitmapCenterCrop(
                        canvas = canvas,
                        bmp = bgPhotoBitmap,
                        dest = RectF(0f, 0f, w.toFloat(), h.toFloat()),
                        zoom = doc.backgroundPhotoZoom.coerceIn(1f, 3f),
                        focusX = doc.backgroundPhotoFocusX.coerceIn(0f, 1f),
                        focusY = doc.backgroundPhotoFocusY.coerceIn(0f, 1f),
                        paint = srcInPaint
                    )
                    canvas.restoreToCount(saveCount)
                    val rim = TextPaint(paint).apply {
                        style = Paint.Style.STROKE
                        strokeWidth = max(1.5f, fontSize * 0.012f)
                        this.color = withAlpha(tones.light, 110)
                    }
                    canvas.drawText(text, drawX, drawY, rim)
                } else {
                    val p = TextPaint(paint).apply {
                        style = Paint.Style.FILL
                        shader = LinearGradient(
                            rect.left, rect.top, rect.right, rect.bottom,
                            intArrayOf(tones.glow, tones.accent),
                            null,
                            Shader.TileMode.CLAMP
                        )
                    }
                    canvas.drawText(text, drawX, drawY, p)
                }
            }
            TextFx.ECHO -> {
                val stepY = fontSize * 0.095f
                val strokePaint = TextPaint(paint).apply {
                    style = Paint.Style.STROKE
                    strokeWidth = max(2f, fontSize * 0.024f)
                    strokeJoin = Paint.Join.ROUND
                }
                for (i in 5 downTo 1) {
                    val alpha = (230 - i * 38).coerceIn(35, 230)
                    strokePaint.color = withAlpha(color, alpha)
                    canvas.drawText(text, drawX, drawY - i * stepY, strokePaint)
                }
                val face = TextPaint(paint).apply {
                    style = Paint.Style.FILL
                    this.color = color
                }
                canvas.drawText(text, drawX, drawY, face)
            }
            TextFx.EXTRUDE -> {
                val depthColor = if (luminance(color) > 0.35f) tones.deep else tones.dark
                val depthPaint = TextPaint(paint).apply {
                    style = Paint.Style.FILL
                    this.color = depthColor
                }
                val steps = 24
                val stepPx = max(1f, fontSize * 0.0055f)
                for (i in steps downTo 1) {
                    canvas.drawText(text, drawX + i * stepPx, drawY + i * stepPx, depthPaint)
                }
                val face = TextPaint(paint).apply {
                    style = Paint.Style.FILL
                    this.color = color
                }
                canvas.drawText(text, drawX, drawY, face)
            }
            TextFx.SPLIT_TONE -> {
                val toneA = tones.dark
                val toneB = box.fxTo?.let { tones.colorOf(it) } ?: tones.light
                val p = TextPaint(paint).apply { style = Paint.Style.FILL }
                if (doc.size == PostSize.STORY) {
                    val midY = h * 0.50f
                    canvas.save()
                    canvas.clipRect(0f, 0f, w.toFloat(), midY)
                    p.color = toneA
                    canvas.drawText(text, drawX, drawY, p)
                    canvas.restore()

                    canvas.save()
                    canvas.clipRect(0f, midY, w.toFloat(), h.toFloat())
                    p.color = toneB
                    canvas.drawText(text, drawX, drawY, p)
                    canvas.restore()
                } else {
                    val midX = w * 0.50f
                    canvas.save()
                    canvas.clipRect(0f, 0f, midX, h.toFloat())
                    p.color = toneA
                    canvas.drawText(text, drawX, drawY, p)
                    canvas.restore()

                    canvas.save()
                    canvas.clipRect(midX, 0f, w.toFloat(), h.toFloat())
                    p.color = toneB
                    canvas.drawText(text, drawX, drawY, p)
                    canvas.restore()
                }
            }
        }
    }

    private fun drawParagraphSlot(
        canvas: Canvas,
        rawText: String,
        tf: Typeface,
        rect: RectF,
        boxW: Float,
        boxH: Float,
        box: SlotBox,
        layout: TemplateLayout,
        align: Layout.Alignment,
        color: Int,
        tones: TonePalette,
        fx: TextFx,
        tracking: Float,
        leading: Float,
        shadow: TextShadow?,
        unit: Float,
        manualSizePx: Float?,
        postScale: Float,
        slotScale: Float,
        bgPhotoBitmap: Bitmap?,
        doc: PostDoc,
        w: Int,
        h: Int,
        isLiteral: Boolean,
        overflow: MutableSet<String>
    ) {
        val charSeq = if (box.dimOthers < 0.99f) {
            buildSpotlightSpannable(rawText, color, tones.accent)
        } else {
            stripInlineMarkup(rawText)
        }

        val availW = if (box.chip != null) max(24f, boxW - unit * 0.036f) else boxW
        val basePx = box.size * unit

        var didOverflow = false
        val finalSizePx: Float = when {
            manualSizePx != null -> manualSizePx
            box.fit == FitMode.FILL || box.fit == FitMode.FILL_WIDTH -> {
                val cacheKey = "FILL|${box.slot}|$charSeq|${tf.hashCode()}|${availW.toInt()}x${boxH.toInt()}|$tracking|$leading|${box.maxLines}"
                val cached = fitCache.get(cacheKey)
                val fitted = cached ?: run {
                    val maxRolePx = when (box.slot) {
                        PostSlots.EYEBROW, PostSlots.BUTTON -> min(basePx * 1.08f, boxH * 0.72f)
                        PostSlots.BODY, PostSlots.LIST -> min(basePx * 1.12f, boxH * 0.88f)
                        PostSlots.HEADLINE -> min(basePx * 1.50f, boxH * 0.90f)
                        else -> min(basePx * 3.5f, boxH * 0.94f)
                    }
                    var lo = max(16f, basePx * 0.30f)
                    var hi = max(lo + 2f, maxRolePx)
                    var best = lo
                    repeat(10) {
                        val mid = (lo + hi) * 0.5f
                        val lay = buildStaticLayout(charSeq, tf, mid, availW.toInt(), align, tracking, leading)
                        val maxWordW = maxLineWidth(lay)
                        val linesOk = box.maxLines <= 0 || lay.lineCount <= box.maxLines
                        if (lay.height <= boxH + 1f && maxWordW <= availW + 2f && linesOk) {
                            best = mid
                            lo = mid
                        } else {
                            hi = mid
                        }
                    }
                    fitCache.put(cacheKey, best)
                    best
                }
                val checkLay = buildStaticLayout(charSeq, tf, fitted, availW.toInt(), align, tracking, leading)
                if (checkLay.height > boxH + 4f || maxLineWidth(checkLay) > availW + 4f) {
                    didOverflow = true
                }
                (fitted * postScale * slotScale).coerceIn(16f, max(w, h) * 1.2f)
            }
            else -> {
                // LEGACY mode
                var size = basePx * postScale * slotScale
                val floor = size * PostDesignRules.SHRINK_FLOOR
                var lay = buildStaticLayout(charSeq, tf, size, availW.toInt(), align, tracking, leading)
                while ((lay.height > boxH + 2f || maxLineWidth(lay) > availW + 2f) && size > floor) {
                    size *= 0.93f
                    lay = buildStaticLayout(charSeq, tf, size, availW.toInt(), align, tracking, leading)
                }
                if (lay.height > boxH + 4f || maxLineWidth(lay) > availW + 4f) {
                    didOverflow = true
                }
                size
            }
        }

        if (didOverflow && !isLiteral && manualSizePx == null) {
            overflow.add(box.slot)
        }

        val paint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = tf
            textSize = finalSizePx
            letterSpacing = tracking
            this.color = color
        }
        val lay = buildStaticLayout(charSeq, paint, availW.toInt(), align, leading)
        val topY = when {
            isLiteral -> rect.centerY() - lay.height / 2f
            box.anchor == VAnchor.CENTER -> rect.centerY() - lay.height / 2f
            box.anchor == VAnchor.BOTTOM -> rect.bottom - lay.height
            else -> rect.top
        }

        // Draw hugging pill chip per line when box.chip != null (TEMPLATE_REDESIGN_PLAN.md 3.6)
        if (box.chip != null) {
            val chipBg = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                this.color = tones.colorOf(box.chip)
            }
            val padX = max(12f, finalSizePx * 0.48f)
            val padY = max(6f, finalSizePx * 0.22f)
            for (i in 0 until lay.lineCount) {
                val lw = lay.getLineWidth(i)
                if (lw <= 1f) continue
                val lineLeft = rect.left + lay.getLineLeft(i)
                val lineTop = topY + lay.getLineTop(i) - padY
                val lineBottom = topY + lay.getLineBottom(i) + padY
                val pillRect = RectF(lineLeft - padX, lineTop, lineLeft + lw + padX, lineBottom)
                val r = pillRect.height() / 2f
                canvas.drawRoundRect(pillRect, r, r, chipBg)
            }
        }

        // Shadow pass under the layout
        if (shadow != null && shadow.strength > 0) {
            val sp = TextPaint(paint)
            configureShadowPaint(sp, shadow, finalSizePx, color, tones)
            val (dx, dy) = shadowOffset(shadow, finalSizePx)
            val shadowLay = buildStaticLayout(stripInlineMarkup(rawText), sp, availW.toInt(), align, leading)
            canvas.save()
            canvas.translate(rect.left + dx, topY + dy)
            shadowLay.draw(canvas)
            canvas.restore()
        } else if (luminance(tones.page) < 0.35f && box.slot == PostSlots.HEADLINE && luminance(color) > 0.6f && fx == TextFx.SOLID) {
            // Subtle dark-first headline glow when no custom shadow is configured (Section 3.2)
            val gp = TextPaint(paint).apply {
                this.color = withAlpha(tones.accent, 65)
                maskFilter = BlurMaskFilter(max(2f, finalSizePx * 0.12f), BlurMaskFilter.Blur.NORMAL)
            }
            val glowLay = buildStaticLayout(stripInlineMarkup(rawText), gp, availW.toInt(), align, leading)
            canvas.save()
            canvas.translate(rect.left, topY)
            glowLay.draw(canvas)
            canvas.restore()
        }

        when (fx) {
            TextFx.SOLID -> {
                canvas.save()
                canvas.translate(rect.left, topY)
                lay.draw(canvas)
                canvas.restore()
            }
            TextFx.OUTLINE -> {
                val p = TextPaint(paint).apply {
                    style = Paint.Style.STROKE
                    strokeWidth = max(2f, finalSizePx * 0.03f)
                    strokeJoin = Paint.Join.ROUND
                }
                val outLay = buildStaticLayout(stripInlineMarkup(rawText), p, availW.toInt(), align, leading)
                canvas.save()
                canvas.translate(rect.left, topY)
                outLay.draw(canvas)
                canvas.restore()
            }
            TextFx.GRADIENT -> {
                val toColor = box.fxTo?.let { tones.colorOf(it) } ?: tones.accent
                val p = TextPaint(paint).apply {
                    style = Paint.Style.FILL
                    shader = LinearGradient(
                        0f, 0f, availW, max(1f, lay.height.toFloat()),
                        intArrayOf(tones.glow, tones.secondary, toColor),
                        floatArrayOf(0f, 0.5f, 1f),
                        Shader.TileMode.CLAMP
                    )
                }
                val gradLay = buildStaticLayout(stripInlineMarkup(rawText), p, availW.toInt(), align, leading)
                canvas.save()
                canvas.translate(rect.left, topY)
                gradLay.draw(canvas)
                canvas.restore()
            }
            TextFx.PHOTO_FILL -> {
                if (bgPhotoBitmap != null) {
                    val saveCount = canvas.saveLayer(0f, 0f, w.toFloat(), h.toFloat(), null)
                    val mp = TextPaint(paint).apply { this.color = Color.WHITE }
                    val maskLay = buildStaticLayout(stripInlineMarkup(rawText), mp, availW.toInt(), align, leading)
                    canvas.save()
                    canvas.translate(rect.left, topY)
                    maskLay.draw(canvas)
                    canvas.restore()
                    val srcIn = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG).apply {
                        xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
                    }
                    drawBitmapCenterCrop(
                        canvas = canvas,
                        bmp = bgPhotoBitmap,
                        dest = RectF(0f, 0f, w.toFloat(), h.toFloat()),
                        zoom = doc.backgroundPhotoZoom.coerceIn(1f, 3f),
                        focusX = doc.backgroundPhotoFocusX.coerceIn(0f, 1f),
                        focusY = doc.backgroundPhotoFocusY.coerceIn(0f, 1f),
                        paint = srcIn
                    )
                    canvas.restoreToCount(saveCount)
                } else {
                    val p = TextPaint(paint).apply {
                        shader = LinearGradient(
                            0f, 0f, availW, max(1f, lay.height.toFloat()),
                            intArrayOf(tones.glow, tones.accent),
                            null,
                            Shader.TileMode.CLAMP
                        )
                    }
                    val gradLay = buildStaticLayout(stripInlineMarkup(rawText), p, availW.toInt(), align, leading)
                    canvas.save()
                    canvas.translate(rect.left, topY)
                    gradLay.draw(canvas)
                    canvas.restore()
                }
            }
            TextFx.ECHO -> {
                val stepY = finalSizePx * 0.09f
                val sp = TextPaint(paint).apply {
                    style = Paint.Style.STROKE
                    strokeWidth = max(2f, finalSizePx * 0.024f)
                }
                for (i in 5 downTo 1) {
                    sp.color = withAlpha(color, (230 - i * 38).coerceIn(35, 230))
                    val echoLay = buildStaticLayout(stripInlineMarkup(rawText), sp, availW.toInt(), align, leading)
                    canvas.save()
                    canvas.translate(rect.left, topY - i * stepY)
                    echoLay.draw(canvas)
                    canvas.restore()
                }
                canvas.save()
                canvas.translate(rect.left, topY)
                lay.draw(canvas)
                canvas.restore()
            }
            TextFx.EXTRUDE -> {
                val depthColor = if (luminance(color) > 0.35f) tones.deep else tones.dark
                val dp = TextPaint(paint).apply { this.color = depthColor }
                val depthLay = buildStaticLayout(stripInlineMarkup(rawText), dp, availW.toInt(), align, leading)
                val stepPx = max(1f, finalSizePx * 0.0055f)
                for (i in 22 downTo 1) {
                    canvas.save()
                    canvas.translate(rect.left + i * stepPx, topY + i * stepPx)
                    depthLay.draw(canvas)
                    canvas.restore()
                }
                canvas.save()
                canvas.translate(rect.left, topY)
                lay.draw(canvas)
                canvas.restore()
            }
            TextFx.SPLIT_TONE -> {
                val pA = TextPaint(paint).apply { this.color = tones.dark }
                val pB = TextPaint(paint).apply { this.color = box.fxTo?.let { tones.colorOf(it) } ?: tones.light }
                val layA = buildStaticLayout(stripInlineMarkup(rawText), pA, availW.toInt(), align, leading)
                val layB = buildStaticLayout(stripInlineMarkup(rawText), pB, availW.toInt(), align, leading)
                if (doc.size == PostSize.STORY) {
                    val midY = h * 0.50f
                    canvas.save()
                    canvas.clipRect(0f, 0f, w.toFloat(), midY)
                    canvas.translate(rect.left, topY)
                    layA.draw(canvas)
                    canvas.restore()

                    canvas.save()
                    canvas.clipRect(0f, midY, w.toFloat(), h.toFloat())
                    canvas.translate(rect.left, topY)
                    layB.draw(canvas)
                    canvas.restore()
                } else {
                    val midX = w * 0.50f
                    canvas.save()
                    canvas.clipRect(0f, 0f, midX, h.toFloat())
                    canvas.translate(rect.left, topY)
                    layA.draw(canvas)
                    canvas.restore()

                    canvas.save()
                    canvas.clipRect(midX, 0f, w.toFloat(), h.toFloat())
                    canvas.translate(rect.left, topY)
                    layB.draw(canvas)
                    canvas.restore()
                }
            }
        }
    }

    /**
     * Draws list slots (Quiz, Checklist, Weekly timetable, and Versus 2-panel comparison).
     * Honors manual sizeUnit, scale, colorHex, and TextShadow (TEMPLATE_REDESIGN_PLAN.md 3.1, 3.2, 5.4, 5.6).
     */
    private fun drawList(
        context: Context,
        canvas: Canvas,
        canvasBitmap: Bitmap,
        doc: PostDoc,
        layout: TemplateLayout,
        box: SlotBox,
        tones: TonePalette,
        w: Int,
        h: Int,
        unit: Float,
        overflow: MutableSet<String>
    ) {
        val raw = doc.texts[box.slot].orEmpty().trim()
        if (raw.isEmpty()) return
        val lines = raw.split('\n').map { it.trim() }.filter { it.isNotEmpty() }
        if (lines.isEmpty()) return

        val slotStyle = doc.slotStyles[box.slot]
        val tf = PostFonts.get(context, box.font, raw, doc, slotStyle)
        val boldTf = PostFonts.get(context, PostFont.BOLD, raw, doc, slotStyle)
        val rect = PostEngine.safeSlotRect(doc, layout, box, w.toFloat(), h.toFloat())
        val shadow = resolveShadow(doc, box, slotStyle)
        val userColor = parseColor(slotStyle?.colorHex)
        val baseInk = userColor ?: tones.colorOf(box.tone)
        val accentInk = userColor ?: tones.accent

        // Special case: info-versus (two panels facing each other, each holding one list line FILL)
        if (doc.templateId == "info-versus") {
            val leftLine = lines.getOrElse(0) { "" }
            val rightLine = lines.getOrElse(1) { "" }
            val p1Rect: RectF
            val p2Rect: RectF
            if (doc.size == PostSize.STORY) {
                val gap = rect.height() * 0.14f
                val ph = (rect.height() - gap) / 2f
                p1Rect = RectF(rect.left + unit * 0.04f, rect.top + unit * 0.03f, rect.right - unit * 0.04f, rect.top + ph - unit * 0.03f)
                p2Rect = RectF(rect.left + unit * 0.04f, rect.bottom - ph + unit * 0.03f, rect.right - unit * 0.04f, rect.bottom - unit * 0.03f)
            } else {
                val gap = rect.width() * 0.10f
                val pw = (rect.width() - gap) / 2f
                p1Rect = RectF(rect.left + unit * 0.035f, rect.top + unit * 0.04f, rect.left + pw - unit * 0.035f, rect.bottom - unit * 0.04f)
                p2Rect = RectF(rect.right - pw + unit * 0.035f, rect.top + unit * 0.04f, rect.right - unit * 0.035f, rect.bottom - unit * 0.04f)
            }
            val manualPx = slotStyle?.sizeUnit?.let { it * unit * doc.textScale }
            listOf(leftLine to (p1Rect to tones.light), rightLine to (p2Rect to tones.dark)).forEach { (lineText, pair) ->
                if (lineText.isEmpty()) return@forEach
                val (r, defaultCol) = pair
                val col = userColor ?: defaultCol
                val box1 = SlotBox(
                    slot = box.slot,
                    x = r.left / w,
                    y = r.top / h,
                    w = r.width() / w,
                    h = r.height() / h,
                    size = box.size,
                    font = PostFont.BOLD,
                    tone = box.tone,
                    centered = true,
                    fit = FitMode.FILL,
                    anchor = VAnchor.CENTER,
                    shadow = shadow
                )
                drawParagraphSlot(
                    canvas = canvas,
                    rawText = lineText,
                    tf = boldTf,
                    rect = r,
                    boxW = max(24f, r.width()),
                    boxH = max(24f, r.height()),
                    box = box1,
                    layout = layout,
                    align = Layout.Alignment.ALIGN_CENTER,
                    color = col,
                    tones = tones,
                    fx = TextFx.SOLID,
                    tracking = 0f,
                    leading = 1.08f,
                    shadow = shadow,
                    unit = unit,
                    manualSizePx = manualPx,
                    postScale = doc.textScale,
                    slotScale = slotStyle?.scale ?: 1f,
                    bgPhotoBitmap = null,
                    doc = doc,
                    w = w,
                    h = h,
                    isLiteral = true,
                    overflow = overflow
                )
            }
            return
        }

        val postScale = doc.textScale.coerceIn(0.25f, 4.0f)
        val slotScale = (slotStyle?.scale ?: 1f).coerceIn(0.25f, 4.0f)
        val manualPx = slotStyle?.sizeUnit?.let { it * unit * postScale }

        val rowCount = lines.size.coerceAtLeast(1)
        val maxH = rect.height()
        val maxW = rect.width()
        var textSize = manualPx ?: (box.size * unit * postScale * slotScale)
        val floor = textSize * PostDesignRules.SHRINK_FLOOR

        fun totalHeightFor(sz: Float): Float {
            val gap = sz * 0.45f
            val indent = sz * 1.65f
            val p = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                typeface = tf
                this.textSize = sz
            }
            var sum = 0f
            lines.forEachIndexed { idx, line ->
                val lay = buildStaticLayout(line, p, max(24, (maxW - indent).toInt()), Layout.Alignment.ALIGN_NORMAL, 1.12f)
                sum += max(sz * 1.35f, lay.height.toFloat())
                if (idx < lines.lastIndex) sum += gap
            }
            return sum
        }

        if (manualPx == null) {
            while (totalHeightFor(textSize) > maxH && textSize > floor) {
                textSize *= 0.93f
            }
            if (totalHeightFor(textSize) > maxH + 4f) {
                overflow.add(box.slot)
            }
        }

        val textColor = if (userColor != null) {
            userColor
        } else {
            readable(canvasBitmap, rect, baseInk, tones.light, tones.dark)
        }

        val gap = textSize * 0.44f
        var curY = rect.top
        val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = tf
            this.textSize = textSize
            this.color = textColor
        }
        val markerPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = boldTf
            this.textSize = textSize * 0.86f
        }

        for ((idx, line) in lines.withIndex()) {
            when (box.list) {
                ListStyle.LETTER -> {
                    // Quiz option row with rounded card + letter badge A/B/C/D
                    val badgeD = textSize * 1.45f
                    val indent = badgeD + textSize * 0.55f
                    val lay = buildStaticLayout(line, textPaint, max(24, (maxW - indent - textSize * 0.5f).toInt()), Layout.Alignment.ALIGN_NORMAL, 1.10f)
                    val rowH = max(badgeD + textSize * 0.45f, lay.height + textSize * 0.55f)
                    val rowRect = RectF(rect.left, curY, rect.right, curY + rowH)
                    val r = rowH * 0.28f

                    val cardPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        color = withAlpha(tones.deep, (215 * doc.panelOpacity / 100f).roundToInt())
                    }
                    canvas.drawRoundRect(rowRect, r, r, cardPaint)
                    val edgePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        style = Paint.Style.STROKE
                        strokeWidth = max(1.5f, unit * 0.002f)
                        color = withAlpha(accentInk, 95)
                    }
                    canvas.drawRoundRect(rowRect, r, r, edgePaint)

                    val badgeCx = rowRect.left + textSize * 0.32f + badgeD / 2f
                    val badgeCy = rowRect.centerY()
                    val discPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = accentInk }
                    canvas.drawCircle(badgeCx, badgeCy, badgeD / 2f, discPaint)

                    val letter = ('A' + (idx % 26)).toString()
                    markerPaint.color = if (luminance(accentInk) > 0.45f) tones.dark else tones.light
                    val bBounds = Rect()
                    markerPaint.getTextBounds(letter, 0, letter.length, bBounds)
                    canvas.drawText(letter, badgeCx - bBounds.exactCenterX(), badgeCy - bBounds.exactCenterY(), markerPaint)

                    val textTop = rowRect.centerY() - lay.height / 2f
                    if (shadow != null && shadow.strength > 0) {
                        val sp = TextPaint(textPaint)
                        configureShadowPaint(sp, shadow, textSize, textColor, tones)
                        val (dx, dy) = shadowOffset(shadow, textSize)
                        val sLay = buildStaticLayout(line, sp, max(24, (maxW - indent - textSize * 0.5f).toInt()), Layout.Alignment.ALIGN_NORMAL, 1.10f)
                        canvas.save()
                        canvas.translate(rowRect.left + indent + dx, textTop + dy)
                        sLay.draw(canvas)
                        canvas.restore()
                    }
                    canvas.save()
                    canvas.translate(rowRect.left + indent, textTop)
                    lay.draw(canvas)
                    canvas.restore()
                    curY += rowH + gap * 0.7f
                }
                ListStyle.CHECK -> {
                    val isNumberedCards = doc.templateId == "info-checklist" || doc.templateId == "info-steps"
                    val isTimeline = doc.templateId == "info-timeline"
                    val badgeD = textSize * 1.32f
                    val indent = badgeD + textSize * 0.55f
                    val lay = buildStaticLayout(line, textPaint, max(24, (maxW - indent - textSize * 0.45f).toInt()), Layout.Alignment.ALIGN_NORMAL, 1.10f)
                    val rowH = if (isNumberedCards || isTimeline) {
                        max(badgeD + textSize * 0.40f, lay.height + textSize * 0.48f)
                    } else {
                        max(badgeD, lay.height.toFloat())
                    }

                    val rowRect = RectF(rect.left, curY, rect.right, curY + rowH)
                    val badgeCx = if (isNumberedCards) {
                        rowRect.left + textSize * 0.30f + badgeD / 2f
                    } else {
                        rect.left + badgeD / 2f
                    }
                    val badgeCy = if (isNumberedCards || isTimeline) rowRect.centerY() else curY + textSize * 0.62f

                    if (isNumberedCards) {
                        val r = rowH * 0.26f
                        val cardPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                            color = withAlpha(tones.deep, (215 * doc.panelOpacity / 100f).roundToInt())
                        }
                        canvas.drawRoundRect(rowRect, r, r, cardPaint)
                        val edgePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                            style = Paint.Style.STROKE
                            strokeWidth = max(1.5f, unit * 0.002f)
                            color = withAlpha(accentInk, 95)
                        }
                        canvas.drawRoundRect(rowRect, r, r, edgePaint)
                    } else if (isTimeline) {
                        if (idx < lines.lastIndex) {
                            val nextRowH = rowH
                            val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                                style = Paint.Style.STROKE
                                strokeWidth = max(3f, unit * 0.004f)
                                color = withAlpha(accentInk, 150)
                            }
                            canvas.drawLine(badgeCx, badgeCy + badgeD * 0.5f, badgeCx, curY + rowH + gap * 0.68f + nextRowH * 0.5f, linePaint)
                        }
                        val cardRect = RectF(rect.left + indent - textSize * 0.20f, curY, rect.right, curY + rowH)
                        val r = rowH * 0.24f
                        val cardPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                            color = withAlpha(tones.deep, (195 * doc.panelOpacity / 100f).roundToInt())
                        }
                        canvas.drawRoundRect(cardRect, r, r, cardPaint)
                    }

                    val discPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = accentInk }
                    canvas.drawCircle(badgeCx, badgeCy, badgeD / 2f, discPaint)

                    if (isNumberedCards || isTimeline) {
                        val numLabel = String.format("%02d", idx + 1)
                        markerPaint.color = if (luminance(accentInk) > 0.45f) tones.dark else tones.light
                        val bBounds = Rect()
                        markerPaint.getTextBounds(numLabel, 0, numLabel.length, bBounds)
                        canvas.drawText(numLabel, badgeCx - bBounds.exactCenterX(), badgeCy - bBounds.exactCenterY(), markerPaint)
                    } else {
                        val tickPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                            style = Paint.Style.STROKE
                            strokeWidth = max(2.5f, badgeD * 0.14f)
                            strokeCap = Paint.Cap.ROUND
                            strokeJoin = Paint.Join.ROUND
                            color = if (luminance(accentInk) > 0.45f) tones.dark else tones.light
                        }
                        val tr = badgeD * 0.24f
                        val tick = Path().apply {
                            moveTo(badgeCx - tr, badgeCy)
                            lineTo(badgeCx - tr * 0.2f, badgeCy + tr * 0.75f)
                            lineTo(badgeCx + tr * 1.1f, badgeCy - tr * 0.75f)
                        }
                        canvas.drawPath(tick, tickPaint)
                    }

                    val textLeft = if (isNumberedCards) rowRect.left + indent + textSize * 0.18f else rect.left + indent
                    val textTop = if (isNumberedCards || isTimeline) rowRect.centerY() - lay.height / 2f else curY

                    if (shadow != null && shadow.strength > 0) {
                        val sp = TextPaint(textPaint)
                        configureShadowPaint(sp, shadow, textSize, textColor, tones)
                        val (dx, dy) = shadowOffset(shadow, textSize)
                        val sLay = buildStaticLayout(line, sp, max(24, (maxW - indent - textSize * 0.45f).toInt()), Layout.Alignment.ALIGN_NORMAL, 1.10f)
                        canvas.save()
                        canvas.translate(textLeft + dx, textTop + dy)
                        sLay.draw(canvas)
                        canvas.restore()
                    }
                    canvas.save()
                    canvas.translate(textLeft, textTop)
                    lay.draw(canvas)
                    canvas.restore()
                    curY += rowH + (if (isNumberedCards || isTimeline) gap * 0.68f else gap)
                }
                ListStyle.ROW -> {
                    // Timetable row ("Mon: Physics & Mechanics")
                    val colon = line.indexOf(':')
                    val labelPart = if (colon in 1..14) line.substring(0, colon).trim() else "${idx + 1}"
                    val valPart = if (colon in 1..14) line.substring(colon + 1).trim() else line
                    val labelW = max(textSize * 2.8f, maxW * 0.25f)
                    val lay = buildStaticLayout(valPart, textPaint, max(24, (maxW - labelW).toInt()), Layout.Alignment.ALIGN_NORMAL, 1.10f)
                    val rowH = max(textSize * 1.35f, lay.height.toFloat())

                    markerPaint.color = accentInk
                    val fm = markerPaint.fontMetrics
                    canvas.drawText(labelPart.uppercase(Locale.getDefault()), rect.left, curY - fm.ascent, markerPaint)

                    if (shadow != null && shadow.strength > 0) {
                        val sp = TextPaint(textPaint)
                        configureShadowPaint(sp, shadow, textSize, textColor, tones)
                        val (dx, dy) = shadowOffset(shadow, textSize)
                        val sLay = buildStaticLayout(valPart, sp, max(24, (maxW - labelW).toInt()), Layout.Alignment.ALIGN_NORMAL, 1.10f)
                        canvas.save()
                        canvas.translate(rect.left + labelW + dx, curY + dy)
                        sLay.draw(canvas)
                        canvas.restore()
                    }
                    canvas.save()
                    canvas.translate(rect.left + labelW, curY)
                    lay.draw(canvas)
                    canvas.restore()

                    if (idx < lines.lastIndex) {
                        val ruleY = curY + rowH + gap * 0.45f
                        val rulePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                            color = withAlpha(accentInk, 68)
                            strokeWidth = max(1.5f, unit * 0.0018f)
                        }
                        canvas.drawLine(rect.left, ruleY, rect.right, ruleY, rulePaint)
                    }
                    curY += rowH + gap
                }
                ListStyle.NUMBER, ListStyle.DOT, ListStyle.NONE -> {
                    val indent = if (box.list == ListStyle.NONE) 0f else textSize * 1.45f
                    val lay = buildStaticLayout(line, textPaint, max(24, (maxW - indent).toInt()), Layout.Alignment.ALIGN_NORMAL, 1.12f)
                    val rowH = max(textSize * 1.25f, lay.height.toFloat())
                    if (box.list == ListStyle.NUMBER) {
                        markerPaint.color = accentInk
                        val fm = markerPaint.fontMetrics
                        canvas.drawText("${idx + 1}.", rect.left, curY - fm.ascent, markerPaint)
                    } else if (box.list == ListStyle.DOT) {
                        val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = accentInk }
                        canvas.drawCircle(rect.left + textSize * 0.35f, curY + textSize * 0.55f, textSize * 0.16f, dotPaint)
                    }
                    canvas.save()
                    canvas.translate(rect.left + indent, curY)
                    lay.draw(canvas)
                    canvas.restore()
                    curY += rowH + gap
                }
            }
        }
    }

    private fun drawSocial(
        context: Context,
        canvas: Canvas,
        doc: PostDoc,
        brand: PostBrand,
        social: SocialArea,
        tones: TonePalette,
        w: Int,
        h: Int,
        unit: Float
    ) {
        val items = brand.footerItems.ifEmpty { PostBrand().footerItems }
        val rect = RectF(social.x * w, social.y * h, (social.x + social.w) * w, (social.y + social.h) * h)
        val count = items.size.coerceIn(1, 4)
        val gap = unit * 0.02f
        val cardH = (rect.height() - gap * (count - 1)) / count
        val radius = unit * 0.028f
        val footerOverride = parseColor(doc.slotStyles[PostSlots.FOOTER]?.colorHex)
        val panelAlpha = (225 * (doc.panelOpacity.coerceIn(0, 100) / 100f)).roundToInt().coerceIn(0, 255)
        val tf = PostFonts.get(context, PostFont.BOLD, "Follow", doc, null)

        items.take(4).forEachIndexed { idx, item ->
            val top = rect.top + idx * (cardH + gap)
            val cr = RectF(rect.left, top, rect.right, top + cardH)
            val cardPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = withAlpha(if (social.darkCards) tones.deep else tones.light, panelAlpha)
            }
            canvas.drawRoundRect(cr, radius, radius, cardPaint)
            val border = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = max(1.5f, unit * 0.002f)
                color = withAlpha(tones.accent, 90)
            }
            canvas.drawRoundRect(cr, radius, radius, border)

            val iconD = min(cardH * 0.56f, unit * 0.068f)
            val iconCx = cr.left + cardH * 0.52f
            val iconCy = cr.centerY()
            val customCol = parseColor(item.colorHex)
            val official = PostIcons.officialColor(item.kind)
            val badgeFill = customCol ?: official ?: tones.accent
            PostIcons.drawBadge(
                canvas = canvas,
                kind = item.kind,
                cx = iconCx,
                cy = iconCy,
                d = iconD,
                fill = badgeFill,
                glyph = Color.WHITE,
                ring = tones.light,
                instaGradient = customCol == null && item.kind == "instagram"
            )

            val textX = cr.left + cardH * 0.95f
            val titlePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                typeface = tf
                textSize = min(cardH * 0.28f, unit * 0.032f)
                color = footerOverride ?: if (social.darkCards) tones.light else tones.dark
            }
            val subPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                typeface = tf
                textSize = min(cardH * 0.24f, unit * 0.026f)
                color = footerOverride ?: tones.accent
            }
            canvas.drawText(PostIcons.title(item.kind), textX, cr.centerY() - titlePaint.textSize * 0.22f, titlePaint)
            canvas.drawText(item.label, textX, cr.centerY() + subPaint.textSize * 0.92f, subPaint)
        }
    }

    /**
     * Top-right corner logo header box (docs/URGENT_PLAN.md item 5: untouched corner box design).
     */
    private fun drawLogo(
        context: Context,
        canvas: Canvas,
        doc: PostDoc,
        brand: PostBrand,
        tones: TonePalette,
        w: Int,
        h: Int,
        unit: Float
    ) {
        val bmp = loadLogoBitmap(context, brand) ?: return
        val isStudy = doc.templateId.startsWith("study-")
        val scale = (brand.logoScale * doc.logoSize).coerceIn(0.4f, 2.6f)
        val logoH = if (isStudy) unit * 0.036f * scale else unit * 0.056f * scale
        val ratio = bmp.width.toFloat() / max(1, bmp.height).toFloat()
        val logoW = logoH * ratio
        val padX = if (isStudy) unit * 0.018f else unit * 0.024f
        val padY = if (isStudy) unit * 0.010f else unit * 0.018f
        val boxW = logoW + padX * 2f
        val boxH = logoH + padY * 2f

        val detached = h >= w * 1.5f && !isStudy
        val rightEdge = if (doc.logoCx != null) {
            (doc.logoCx * w + boxW / 2f).coerceIn(boxW, w.toFloat())
        } else {
            w - unit * 0.045f
        }
        val topEdge = when {
            doc.logoCy != null -> (doc.logoCy * h - boxH / 2f).coerceIn(0f, h - boxH)
            isStudy -> h * 0.018f
            detached -> h * 0.085f
            else -> 0f
        }
        val boxRect = RectF(rightEdge - boxW, topEdge, rightEdge, topEdge + boxH)
        val radius = unit * 0.022f

        val boxPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                boxRect.left, boxRect.top, boxRect.right, boxRect.bottom,
                intArrayOf(tones.deep, tones.dark),
                null,
                Shader.TileMode.CLAMP
            )
        }
        if (detached || isStudy || doc.logoCy != null) {
            canvas.drawRoundRect(boxRect, radius, radius, boxPaint)
            val edge = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = max(1.5f, unit * 0.002f)
                color = withAlpha(tones.secondary, 130)
            }
            canvas.drawRoundRect(boxRect, radius, radius, edge)
        } else {
            val path = Path().apply {
                addRoundRect(
                    boxRect,
                    floatArrayOf(0f, 0f, 0f, 0f, radius, radius, radius, radius),
                    Path.Direction.CW
                )
            }
            canvas.drawPath(path, boxPaint)
            val edge = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = max(1.5f, unit * 0.002f)
                color = withAlpha(tones.secondary, 130)
            }
            canvas.drawPath(path, edge)
        }

        val logoRect = RectF(
            boxRect.left + padX,
            boxRect.top + padY,
            boxRect.right - padX,
            boxRect.bottom - padY
        )
        val imgPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        canvas.drawBitmap(bmp, null, logoRect, imgPaint)
    }

    private fun loadLogoBitmap(context: Context, brand: PostBrand): Bitmap? {
        val key = "${brand.logoPath.orEmpty()}@${brand.logoStamp}"
        synchronized(this) {
            if (cachedLogoKey == key && cachedLogoBitmap != null && !cachedLogoBitmap!!.isRecycled) {
                return cachedLogoBitmap
            }
        }
        val custom = brand.logoPath?.takeIf { it.isNotBlank() }?.let { path ->
            runCatching { BitmapFactory.decodeFile(path) }.getOrNull()
        }
        val decoded = custom ?: runCatching {
            val resId = context.resources.getIdentifier("launcher_logo", "drawable", context.packageName)
            if (resId != 0) BitmapFactory.decodeResource(context.resources, resId) else null
        }.getOrNull()
        if (decoded != null) {
            synchronized(this) {
                cachedLogoKey = key
                cachedLogoBitmap = decoded
            }
        }
        return decoded
    }

    private fun drawFooter(
        context: Context,
        canvas: Canvas,
        doc: PostDoc,
        brand: PostBrand,
        layout: TemplateLayout,
        tones: TonePalette,
        w: Int,
        h: Int,
        unit: Float
    ) {
        val items = brand.footerItems.filter { it.label.isNotBlank() }
        if (items.isEmpty()) return

        val isStudy = doc.templateId.startsWith("study-")
        val ruleY = if (isStudy) h * 0.942f else h * 0.912f
        val centerY = if (isStudy) h * 0.962f else h * 0.946f
        val footerOverride = parseColor(doc.slotStyles[PostSlots.FOOTER]?.colorHex)
        val textColor = footerOverride ?: if (layout.footerLight && !isStudy) tones.light else tones.dark

        if (!isStudy) {
            val rulePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = withAlpha(textColor, 55)
                strokeWidth = max(1.5f, unit * 0.0018f)
            }
            canvas.drawLine(w * 0.06f, ruleY, w * 0.94f, ruleY, rulePaint)
        }

        val scale = brand.iconScale.coerceIn(0.6f, 1.6f)
        val textSize = (if (isStudy) unit * 0.020f else unit * 0.022f) * scale
        val iconD = textSize * 1.38f
        val tf = PostFonts.get(context, PostFont.BOLD, "wisdom", doc, null)
        val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = tf
            this.textSize = textSize
            color = textColor
        }
        val fm = textPaint.fontMetrics
        val gapBetweenItems = unit * 0.028f
        val innerGap = textSize * 0.35f
        val pillPadX = textSize * 0.55f
        val pillH = iconD + textSize * 0.42f

        val widths = items.map { item ->
            val tw = textPaint.measureText(item.label)
            iconD + innerGap + tw + (if (brand.footerStyle == FooterStyle.PILL) pillPadX * 2f else 0f)
        }
        val totalW = widths.sum() + gapBetweenItems * max(0, items.size - 1)
        var curX = (w - totalW) / 2f

        items.forEachIndexed { idx, item ->
            val iw = widths[idx]
            val itemColor = parseColor(item.colorHex)
            if (brand.footerStyle == FooterStyle.PILL) {
                val pillRect = RectF(curX, centerY - pillH / 2f, curX + iw, centerY + pillH / 2f)
                val pillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = withAlpha(if (layout.footerLight && !isStudy) tones.deep else tones.dark, 185)
                }
                canvas.drawRoundRect(pillRect, pillH / 2f, pillH / 2f, pillPaint)
            }

            val startX = if (brand.footerStyle == FooterStyle.PILL) curX + pillPadX else curX
            val icx = startX + iconD / 2f
            when (brand.iconLook) {
                IconLook.PLAIN -> {
                    PostIcons.draw(canvas, item.kind, icx, centerY, iconD * 0.86f, itemColor ?: tones.accent)
                }
                IconLook.BADGE -> {
                    PostIcons.drawBadge(
                        canvas, item.kind, icx, centerY, iconD,
                        itemColor ?: tones.accent, tones.dark, tones.light, false
                    )
                }
                IconLook.OFFICIAL -> {
                    val official = PostIcons.officialColor(item.kind)
                    PostIcons.drawBadge(
                        canvas, item.kind, icx, centerY, iconD,
                        itemColor ?: official ?: tones.accent,
                        Color.WHITE,
                        tones.light,
                        itemColor == null && item.kind == "instagram"
                    )
                }
            }

            val labelColor = if (brand.footerStyle == FooterStyle.PILL) {
                footerOverride ?: itemColor ?: tones.light
            } else {
                footerOverride ?: itemColor ?: textColor
            }
            textPaint.color = labelColor
            val baseline = centerY - (fm.ascent + fm.descent) / 2f
            canvas.drawText(item.label, startX + iconD + innerGap, baseline, textPaint)
            curX += iw + gapBetweenItems
        }
    }

    private fun buildSpotlightSpannable(raw: String, baseColor: Int, accentColor: Int): CharSequence {
        val sb = SpannableStringBuilder()
        val dimColor = withAlpha(baseColor, 102)
        val hasExplicitMarkup = raw.contains("**") || raw.contains("==")
        if (hasExplicitMarkup) {
            var i = 0
            while (i < raw.length) {
                if (raw.startsWith("**", i) || raw.startsWith("==", i)) {
                    val marker = raw.substring(i, i + 2)
                    val end = raw.indexOf(marker, i + 2)
                    if (end > i + 2) {
                        val s = sb.length
                        sb.append(raw.substring(i + 2, end))
                        val e = sb.length
                        sb.setSpan(ForegroundColorSpan(accentColor), s, e, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                        sb.setSpan(StyleSpan(Typeface.BOLD), s, e, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                        i = end + 2
                        continue
                    }
                }
                val s = sb.length
                sb.append(raw[i])
                sb.setSpan(ForegroundColorSpan(dimColor), s, s + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                i++
            }
            return sb
        }
        val trimmed = raw.trimEnd()
        val lastSpace = trimmed.indexOfLast { it.isWhitespace() }
        if (lastSpace <= 0) {
            sb.append(trimmed)
            sb.setSpan(ForegroundColorSpan(accentColor), 0, sb.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            return sb
        }
        sb.append(trimmed)
        sb.setSpan(ForegroundColorSpan(dimColor), 0, lastSpace + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        sb.setSpan(ForegroundColorSpan(accentColor), lastSpace + 1, sb.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return sb
    }

    private fun stripInlineMarkup(text: String): String =
        text.replace("**", "").replace("==", "")

    private fun buildStaticLayout(
        text: CharSequence,
        tf: Typeface,
        sizePx: Float,
        widthPx: Int,
        align: Layout.Alignment,
        tracking: Float,
        leading: Float
    ): StaticLayout {
        val paint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = tf
            textSize = sizePx
            letterSpacing = tracking
        }
        return buildStaticLayout(text, paint, widthPx, align, leading)
    }

    private fun buildStaticLayout(
        text: CharSequence,
        paint: TextPaint,
        widthPx: Int,
        align: Layout.Alignment,
        leading: Float
    ): StaticLayout =
        StaticLayout.Builder.obtain(text, 0, text.length, paint, max(16, widthPx))
            .setAlignment(align)
            .setLineSpacing(0f, leading.coerceIn(0.70f, 1.8f))
            .setIncludePad(false)
            .build()

    private fun maxLineWidth(lay: StaticLayout): Float {
        var m = 0f
        for (i in 0 until lay.lineCount) {
            m = max(m, lay.getLineWidth(i))
        }
        return m
    }

    private fun drawBitmapCenterCrop(
        canvas: Canvas,
        bmp: Bitmap,
        dest: RectF,
        zoom: Float,
        focusX: Float,
        focusY: Float,
        paint: Paint
    ) {
        val bw = max(1, bmp.width).toFloat()
        val bh = max(1, bmp.height).toFloat()
        val dw = max(1f, dest.width())
        val dh = max(1f, dest.height())
        val baseScale = max(dw / bw, dh / bh)
        val scale = baseScale * zoom.coerceAtLeast(1f)
        val scaledW = bw * scale
        val scaledH = bh * scale
        val extraW = max(0f, scaledW - dw)
        val extraH = max(0f, scaledH - dh)
        val tx = dest.left - extraW * focusX.coerceIn(0f, 1f)
        val ty = dest.top - extraH * focusY.coerceIn(0f, 1f)
        val matrix = Matrix().apply {
            postScale(scale, scale)
            postTranslate(tx, ty)
        }
        canvas.save()
        canvas.clipRect(dest)
        canvas.drawBitmap(bmp, matrix, paint)
        canvas.restore()
    }

    private fun linearGradientByAngle(w: Int, h: Int, angleDeg: Float, colors: IntArray): LinearGradient {
        val rad = Math.toRadians(angleDeg.toDouble())
        val dx = cos(rad).toFloat()
        val dy = sin(rad).toFloat()
        val cx = w / 2f
        val cy = h / 2f
        val half = max(w, h) / 2f
        return LinearGradient(
            cx - dx * half,
            cy - dy * half,
            cx + dx * half,
            cy + dy * half,
            colors,
            null,
            Shader.TileMode.CLAMP
        )
    }

    private fun readable(bitmap: Bitmap, rect: RectF, color: Int, light: Int, dark: Int): Int {
        val bw = bitmap.width
        val bh = bitmap.height
        if (bw <= 4 || bh <= 4) return color
        val sx = ((rect.centerX() / max(1f, rect.right.coerceAtLeast(bw.toFloat()))) * bw).toInt().coerceIn(0, bw - 1)
        val sy = ((rect.centerY() / max(1f, rect.bottom.coerceAtLeast(bh.toFloat()))) * bh).toInt().coerceIn(0, bh - 1)
        val bgPixel = bitmap.getPixel(sx, sy)
        val ratio = contrastRatio(color, bgPixel)
        if (ratio >= 3.2f) return color
        return if (contrastRatio(light, bgPixel) >= contrastRatio(dark, bgPixel)) light else dark
    }

    private fun luminance(c: Int): Float {
        fun ch(v: Int): Float {
            val s = v / 255f
            return if (s <= 0.03928f) s / 12.92f else ((s + 0.055f) / 1.055f).pow(2.4f)
        }
        return 0.2126f * ch(Color.red(c)) + 0.7152f * ch(Color.green(c)) + 0.0722f * ch(Color.blue(c))
    }

    private fun contrastRatio(a: Int, b: Int): Float {
        val l1 = luminance(a)
        val l2 = luminance(b)
        return (max(l1, l2) + 0.05f) / (min(l1, l2) + 0.05f)
    }

    private fun parseColor(hex: String?): Int? {
        if (hex.isNullOrBlank()) return null
        val clean = hex.trim().let { if (it.startsWith("#")) it else "#$it" }
        return runCatching { Color.parseColor(clean) }.getOrNull()
    }

    private fun withAlpha(c: Int, alpha: Int): Int =
        Color.argb(alpha.coerceIn(0, 255), Color.red(c), Color.green(c), Color.blue(c))

    private fun blend(c1: Int, c2: Int, ratio: Float): Int {
        val t = ratio.coerceIn(0f, 1f)
        val r = (Color.red(c1) * (1f - t) + Color.red(c2) * t).roundToInt().coerceIn(0, 255)
        val g = (Color.green(c1) * (1f - t) + Color.green(c2) * t).roundToInt().coerceIn(0, 255)
        val b = (Color.blue(c1) * (1f - t) + Color.blue(c2) * t).roundToInt().coerceIn(0, 255)
        return Color.rgb(r, g, b)
    }
}
