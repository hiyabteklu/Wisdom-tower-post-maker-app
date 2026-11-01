package com.wisdomtower.post

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.net.Uri
import android.text.Layout
import android.text.SpannableString
import android.text.Spanned
import android.text.StaticLayout
import android.text.style.MetricAffectingSpan
import android.text.TextPaint
import android.util.LruCache
import java.io.File
import java.util.Locale
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

/**
 * Optional bundled fonts. Put the .ttf files in app/src/main/assets/fonts/ with these exact names.
 * Until they exist the system sans font is used, so nothing breaks.
 */
object PostFonts {
    private val cache = HashMap<PostFont, Typeface>()

    fun get(context: Context, font: PostFont): Typeface = cache.getOrPut(font) {
        val file = when (font) {
            PostFont.DISPLAY -> "fonts/outfit_extrabold.ttf"
            PostFont.BOLD -> "fonts/plus_jakarta_sans_bold.ttf"
            PostFont.REGULAR -> "fonts/plus_jakarta_sans_regular.ttf"
        }
        runCatching { Typeface.createFromAsset(context.assets, file) }.getOrElse {
            if (font == PostFont.REGULAR) Typeface.create("sans-serif", Typeface.NORMAL)
            else Typeface.create("sans-serif", Typeface.BOLD)
        }
    }

    const val ENGLISH = "english"
    const val AMHARIC = "amharic"

    class FontChoice(val path: String, val label: String)

    private val fileCache = HashMap<String, Typeface?>()

    /** The first Amharic font, remembered so a redraw does not list the font folders again. Forgotten when a font is added. */
    private var amharicDefault: String? = null
    private var amharicDefaultKnown = false

    private fun defaultAmharic(context: Context): String? = synchronized(fileCache) {
        if (!amharicDefaultKnown) {
            amharicDefault = list(context, AMHARIC).firstOrNull()?.path
            amharicDefaultKnown = true
        }
        amharicDefault
    }

    /** Fonts bundled in assets/fonts/english or assets/fonts/amharic plus fonts added from the phone with the Add button. */
    fun list(context: Context, language: String): List<FontChoice> {
        val imported = (File(context.filesDir, "fonts/$language").listFiles() ?: emptyArray<File>())
            .filter { it.isFile && (it.name.endsWith(".ttf", true) || it.name.endsWith(".otf", true)) }
            .map { FontChoice(it.absolutePath, labelFor(it.name)) }
        return (listAssets(context, language) + imported).sortedBy { it.label.lowercase() }
    }

    /** Copies a .ttf / .otf picked on the phone into the app so it can be chosen on any post. Returns the saved path, or null if it is not a usable font. */
    fun importFont(context: Context, uri: Uri, language: String): String? {
        var name = "font"
        runCatching {
            context.contentResolver.query(uri, arrayOf(android.provider.OpenableColumns.DISPLAY_NAME), null, null, null)?.use { c ->
                if (c.moveToFirst()) name = c.getString(0) ?: name
            }
        }
        val clean = name.replace(Regex("[^A-Za-z0-9._ -]"), "_")
        if (!(clean.endsWith(".ttf", true) || clean.endsWith(".otf", true))) return null
        val dir = File(context.filesDir, "fonts/$language").apply { mkdirs() }
        val target = File(dir, clean)
        val copied = runCatching {
            context.contentResolver.openInputStream(uri)?.use { input -> target.outputStream().use { out -> input.copyTo(out) } } != null
        }.getOrDefault(false)
        if (!copied) { target.delete(); return null }
        synchronized(fileCache) {
            fileCache.remove(target.absolutePath)
            amharicDefaultKnown = false
        }
        val usable = runCatching { Typeface.createFromFile(target) }.isSuccess
        if (!usable) { target.delete(); return null }
        return target.absolutePath
    }

    private fun listAssets(context: Context, language: String): List<FontChoice> {
        val names = runCatching { context.assets.list("fonts/$language") }.getOrNull() ?: return emptyList()
        return names
            .filter { it.endsWith(".ttf", true) || it.endsWith(".otf", true) }
            .sortedBy { it.lowercase() }
            .map { FontChoice("fonts/$language/$it", labelFor(it)) }
    }

    private fun labelFor(file: String): String =
        file.substringBeforeLast('.').replace('_', ' ').replace('-', ' ').trim()
            .split(' ').filter { it.isNotEmpty() }
            .joinToString(" ") { word -> word.replaceFirstChar { c -> c.uppercase() } }

    private fun load(context: Context, path: String?): Typeface? {
        if (path == null) return null
        return synchronized(fileCache) {
            fileCache.getOrPut(path) {
                runCatching { if (path.startsWith("/")) Typeface.createFromFile(path) else Typeface.createFromAsset(context.assets, path) }.getOrNull()
            }
        }
    }

    /** The typeface of a font path from list() (a bundled asset or a file added from the phone), or null when it cannot be read. Used to draw font previews. */
    fun typeface(context: Context, path: String?): Typeface? = load(context, path)

    /**
     * The typeface for a text role. Order: the font picked on this one text box (own), the English font picked on the post,
     * then the template's bundled default.
     */
    fun resolve(context: Context, font: PostFont, doc: PostDoc, own: String? = null): Typeface =
        load(context, own) ?: load(context, doc.fontEnglish) ?: get(context, font)

    private fun isEthiopic(c: Char): Boolean =
        c.code in 0x1200..0x139F || c.code in 0x2D80..0x2DDF || c.code in 0xAB00..0xAB2F

    private class EthiopicSpan(private val face: Typeface) : MetricAffectingSpan() {
        override fun updateDrawState(p: TextPaint) { p.typeface = face }
        override fun updateMeasureState(p: TextPaint) { p.typeface = face }
    }

    /**
     * Amharic letters are drawn with the Amharic font: the one picked on this text box (amharicOwn), else the one picked on the post,
     * else the first one available, else the phone's own.
     * Everything else keeps the English font, so mixed English and Amharic text works in one box.
     */
    fun styled(context: Context, text: String, doc: PostDoc, amharicOwn: String? = null): CharSequence {
        if (text.none { isEthiopic(it) }) return text
        val face = load(context, amharicOwn)
            ?: load(context, doc.fontAmharic ?: defaultAmharic(context))
            ?: return text
        val out = SpannableString(text)
        var i = 0
        while (i < text.length) {
            if (isEthiopic(text[i])) {
                var j = i
                while (j < text.length && (isEthiopic(text[j]) || (text[j] == ' ' && j + 1 < text.length && isEthiopic(text[j + 1])))) j++
                out.setSpan(EthiopicSpan(face), i, j, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                i = j
            } else {
                i++
            }
        }
        return out
    }

    fun bundled(context: Context): Boolean =
        runCatching { context.assets.open("fonts/outfit_extrabold.ttf").close(); true }.getOrDefault(false)
}

/**
 * The single drawing path. The screen preview, the template thumbnails and the exported file all come from
 * render(). Layout, text fitting and every position are worked out at the real export size; a smaller
 * bitmap (the editor preview) only scales the canvas, so what you see is what you save.
 * Order: background gradient, backdrop look glow and pattern, mesh blend and grain, shapes, photo, shapes marked overPhoto, icons placed "behind",
 * text set "behind the subject", the cut-out subject, the rest of the text, header box with the logo, then the social block or the footer,
 * then icons placed "in front".
 * Colors come from the Brand kit unless the post overrides them. Derived tones (DEEP, GLOW, TINT, SOFT, PAGE)
 * are built from the brand here, so every gradient follows a color change.
 * Text: each word box takes its color from the template's tone unless the post has a style for that box (PostDoc.slotStyles),
 * which can set its color, its English and Amharic fonts and its size. See docs/TEXT_STYLE.md.
 *
 * DARK-FIRST LOOK (docs/BRAND_COLOR_PLAN.md, phase 1). When the brand's Dark color is near-black (darkFirst):
 * - pale (PostBase.LIGHT) templates are drawn on a deep dark surface with light text instead of a pale page;
 * - gradients that run from the accent (ACCENT to DEEP) become dark gradients tinted with the accent, so a bright cyan
 *   Main never sits behind white text;
 * - big accent shapes get a soft glow, big rounded cards get a thin light border, light headlines get a soft glow of the accent,
 *   and the logo box stays dark.
 * BACKDROP LOOKS (docs/BACKDROPS.md). In dark-first, when the brand has a look (PostBrand.bgLook, see PostBackdrops) and the post uses
 * the template background, the look's gradient, glow and pattern replace the template's own backdrop on every template.
 * CONTRAST GUARD (always on). Before a text box is drawn, the pixels under it are sampled. If the template's text color would
 * be below about 4:1 against them, the text switches to whichever of light text, dark, white or black reads best.
 * A color the user picked on the post (box color or Text color) is never changed.
 *
 * SPEED (docs/PERFORMANCE.md). This all runs on the processor, not the graphics chip, so the cost of a render is the cost of its
 * pixels. Thumbnails and previews are drawn straight at their small size. Decoded photos, cut-outs, icons and the logo are
 * kept between renders (PostImageCache and the logo cache below) instead of being read from storage again every time.
 */
object PostRenderer {
    data class Result(val bitmap: Bitmap, val overflow: List<String>)

    private class Colors(
        val accent: Int, val text: Int, val light: Int, val secondary: Int, val dark: Int,
        val deep: Int, val glow: Int, val tint: Int, val soft: Int,
        /** The brand's real light color, always usable as text on dark (light itself becomes a dark surface on pale templates in dark-first). */
        val lightText: Int,
        val darkFirst: Boolean
    ) {
        /** The color at the bottom of the background. Set once the background is known. */
        var page: Int = Color.WHITE

        /** The color picked for the footer and social text on this post, or null for the automatic one. */
        var footerText: Int? = null

        /** The bitmap being drawn, and its scale from base post pixels, so the contrast guard can read what is under a text box. */
        var canvasBitmap: Bitmap? = null
        var canvasScale: Float = 1f
    }

    private class Bg(val from: Int, val mid: Int?, val to: Int, val angle: Float)

    /** The decoded logo and the box around its visible part, kept so a redraw does not decode and scan it again. */
    private class LogoArt(val bitmap: Bitmap, val visible: Rect)

    private val logoCache = LruCache<String, LogoArt>(4)

    private const val SAFE_TOP = 0.12f
    private const val SAFE_BOTTOM = 0.88f

    /**
     * Draws the post.
     * scale: 1 is the standard export size. Below 1 the bitmap is smaller (previews). 2 is the 4K export: the bitmap is twice as large in
     * both directions. Every position, text-fit step and overflow decision is still made at the base size and the canvas is scaled,
     * so the overflow list and the look match at every scale.
     * cancelled: asked between the drawing steps. When it returns true the render stops, frees its bitmap and throws a
     * CancellationException, so a preview that is already out of date does not keep the processor busy.
     */
    fun render(context: Context, doc: PostDoc, brand: PostBrand, scale: Float = 1f, cancelled: () -> Boolean = { false }): Result {
        val w = doc.size.width
        val h = doc.size.height
        val unit = min(w, h).toFloat()
        val s = scale.coerceIn(0.05f, 2f)
        // photos are decoded no bigger than the bitmap needs; near full size (and in 4K) they keep a full-size target
        val decodeTo = if (s >= 0.75f) (max(w, h) * max(1f, s)).toInt() else max(256, (max(w, h) * s * 1.5f).toInt())
        fun stop() {
            if (cancelled()) throw java.util.concurrent.CancellationException("stale render")
        }
        val layout = postTemplate(doc.templateId).layout(doc.size)
        val colors = colorsFor(doc, brand, layout.base)
        // a backdrop look only applies to dark-first brands and to posts that use the template background
        val look = PostBackdrops.byId(brand.bgLook)?.takeIf { colors.darkFirst && doc.bgMode == BgMode.TEMPLATE }
        val bg = backgroundFor(doc, colors, layout, look)
        colors.page = bg.to
        val bitmap = Bitmap.createBitmap(max(1, (w * s).toInt()), max(1, (h * s).toInt()), Bitmap.Config.ARGB_8888)
        colors.canvasBitmap = bitmap
        colors.canvasScale = bitmap.width / w.toFloat()
        try {
            val canvas = Canvas(bitmap)
            canvas.scale(bitmap.width / w.toFloat(), bitmap.height / h.toFloat())
            drawBackground(canvas, bg, w, h)
            if (look != null) PostBackdrops.draw(canvas, look, w, h, colors.accent, colors.secondary)
            PostEngine.drawMesh(canvas, doc.meshStyle, w, h, colors.accent, colors.glow, colors.deep, colors.secondary)
            if (doc.grain) PostEngine.drawGrain(canvas, w, h)
            stop()
            doc.backgroundPhotoUri?.let { drawBackgroundPhoto(context, canvas, it, doc.backgroundPhotoOpacity, w, h, decodeTo) }
            stop()

            val photoArea = layout.photo
            // a frame picked on the post only applies to a photo window that does not fill the whole post
            val frame = doc.photoFrame?.takeIf { photoArea != null && photoArea.w < 0.99f && photoArea.h < 0.99f }
            layout.decor.filter { !it.overPhoto }.forEach { drawDecor(canvas, it, colors, w, h, unit, frame) }
            val photoUri = doc.photoUri
            if (photoArea != null && photoUri != null) drawPhoto(context, canvas, photoUri, photoArea, doc, colors, w, h, unit, decodeTo)
            stop()
            layout.decor.filter { it.overPhoto }.forEach { drawDecor(canvas, it, colors, w, h, unit) }

            // icons the user placed behind the words
            doc.icons.filter { !it.front }.forEach { PostIconStore.draw(canvas, it, w, h, unit, colors.accent) }
            stop()

            val overflow = mutableListOf<String>()

            // behind = true draws the words set "behind the subject", false the rest. Words the user dragged leave the safe band, so they are drawn without its clip.
            fun drawSlots(behind: Boolean) {
                canvas.save()
                canvas.clipRect(0f, h * SAFE_TOP, w.toFloat(), h * SAFE_BOTTOM)
                layout.slots.forEach { box ->
                    stop()
                    val isBehind = box.literal == null && box.slot in doc.textBehind
                    if (isBehind != behind) return@forEach
                    if (box.literal == null && doc.offsetFor(box.slot) != null) return@forEach
                    val text = (box.literal ?: doc.texts[box.slot].orEmpty()).trim()
                    if (text.isEmpty()) return@forEach
                    val fits = drawSlot(context, canvas, box, text, doc, colors, w, h, unit)
                    if (!fits && box.literal == null) overflow.add(box.slot)
                }
                canvas.restore()
                layout.slots.forEach { box ->
                    stop()
                    if (box.literal != null) return@forEach
                    val off = doc.offsetFor(box.slot) ?: return@forEach
                    if ((box.slot in doc.textBehind) != behind) return@forEach
                    val text = doc.texts[box.slot].orEmpty().trim()
                    if (text.isEmpty()) return@forEach
                    val moved = box.copy(x = box.x + off.dx, y = box.y + off.dy)
                    if (!drawSlot(context, canvas, moved, text, doc, colors, w, h, unit)) overflow.add(box.slot)
                }
            }

            drawSlots(true)
            stop()
            doc.cutoutUri?.let { PostEngine.drawCutout(canvas, it, doc, w, h, decodeTo) }
            stop()
            drawSlots(false)
            stop()

            if (doc.showLogo) drawLogo(context, canvas, doc, brand, colors, w, h, unit)
            val social = layout.social
            if (social != null) {
                drawSocial(context, canvas, brand, social, colors, w, h, unit)
            } else if (doc.showFooter) {
                drawFooter(context, canvas, brand, if (layout.footerLight) colors.light else colors.accent, colors, layout.footerLight, w, h, unit)
            }
            stop()

            // icons the user placed in front
            doc.icons.filter { it.front }.forEach { PostIconStore.draw(canvas, it, w, h, unit, colors.accent) }
            colors.canvasBitmap = null
            return Result(bitmap, overflow)
        } catch (e: java.util.concurrent.CancellationException) {
            bitmap.recycle()
            throw e
        }
    }

    /**
     * The template cards and the Library. Drawn straight at targetWidth like a preview, never at full size first and then shrunk,
     * so a card costs a fraction of an export. Pass cancelled so a card that is out of date can stop early (see render).
     */
    fun thumbnail(context: Context, doc: PostDoc, brand: PostBrand, targetWidth: Int, cancelled: () -> Boolean = { false }): Result =
        preview(context, doc, brand, targetWidth, cancelled)

    /**
     * The editor and Brand previews: drawn straight at targetWidth (never above full size), with no full-size bitmap in between.
     * Pass cancelled so a newer change can stop this render early (see render).
     */
    fun preview(context: Context, doc: PostDoc, brand: PostBrand, targetWidth: Int, cancelled: () -> Boolean = { false }): Result =
        render(context, doc, brand, min(1f, targetWidth.toFloat() / doc.size.width), cancelled)

    fun hasLogo(context: Context, brand: PostBrand): Boolean {
        val path = brand.logoPath
        if (path != null && File(path).exists()) return true
        return context.resources.getIdentifier("wt_logo", "drawable", context.packageName) != 0
    }

    // ---- colors and background ----

    /** WCAG relative luminance of a color, 0 (black) to 1 (white). */
    private fun relLum(c: Int): Float {
        fun ch(v: Int): Float {
            val x = v / 255f
            return if (x <= 0.03928f) x / 12.92f else Math.pow(((x + 0.055f) / 1.055f).toDouble(), 2.4).toFloat()
        }
        return 0.2126f * ch(Color.red(c)) + 0.7152f * ch(Color.green(c)) + 0.0722f * ch(Color.blue(c))
    }

    private fun contrastOf(l1: Float, l2: Float): Float = (max(l1, l2) + 0.05f) / (min(l1, l2) + 0.05f)

    /**
     * The contrast guard. Looks at the pixels under the box (left, top, width, height in base post pixels) and returns the wanted color
     * when it reads at about 4:1 or better, else the best of light text, dark, white and black. The alpha of the wanted color is kept.
     */
    private fun readable(c: Colors, want: Int, left: Float, top: Float, width: Float, height: Float): Int {
        val bmp = c.canvasBitmap ?: return want
        val k = c.canvasScale
        var sum = 0f
        var n = 0
        for (iy in 0 until 3) {
            for (ix in 0 until 6) {
                val px = ((left + width * (ix + 0.5f) / 6f) * k).toInt().coerceIn(0, bmp.width - 1)
                val py = ((top + height * (iy + 0.5f) / 3f) * k).toInt().coerceIn(0, bmp.height - 1)
                sum += relLum(bmp.getPixel(px, py))
                n++
            }
        }
        val bgLum = sum / n
        val opaque = Color.rgb(Color.red(want), Color.green(want), Color.blue(want))
        if (contrastOf(relLum(opaque), bgLum) >= 4f) return want
        val best = listOf(c.lightText, c.dark, Color.WHITE, Color.BLACK).maxByOrNull { contrastOf(relLum(it), bgLum) } ?: return want
        return Color.argb(Color.alpha(want), Color.red(best), Color.green(best), Color.blue(best))
    }

    private fun colorsFor(doc: PostDoc, brand: PostBrand, base: PostBase): Colors {
        val accent = parseHex(doc.accentHex ?: brand.primaryHex, Color.BLACK)
        val dark = parseHex(brand.darkHex, Color.BLACK)
        val lightBrand = parseHex(brand.lightHex, Color.WHITE)
        val secondary = parseHex(brand.secondaryHex, Color.WHITE)
        // dark-first: a near-black Dark color means the brand lives on dark screens, so pale templates are drawn dark
        val darkFirst = relLum(dark) < 0.12f
        val paleToDark = darkFirst && base == PostBase.LIGHT
        val surface = mix(dark, accent, 0.10f)
        val light = if (paleToDark) surface else lightBrand
        val colors = Colors(
            accent = accent,
            text = parseHex(doc.textHex ?: (if (paleToDark) brand.lightHex else brand.darkHex), Color.BLACK),
            light = light,
            secondary = secondary,
            dark = dark,
            deep = mix(accent, dark, 0.45f),
            glow = mix(accent, secondary, 0.40f),
            tint = if (paleToDark) mix(surface, accent, 0.16f) else mix(lightBrand, accent, 0.10f),
            soft = if (paleToDark) mix(surface, secondary, 0.20f) else mix(lightBrand, secondary, 0.45f),
            lightText = lightBrand,
            darkFirst = darkFirst
        )
        colors.footerText = parseHexOrNull(doc.slotStyles[PostSlots.FOOTER]?.colorHex)
        return colors
    }

    /** Tones used for backgrounds. In dark-first the accent-based tones become dark tinted tones, so light text always has a dark page under it. */
    private fun bgTone(c: Colors, tone: PostTone): Int {
        if (!c.darkFirst) return toneColor(c, tone)
        return when (tone) {
            PostTone.ACCENT -> mix(c.dark, c.accent, 0.32f)
            PostTone.DEEP -> mix(c.dark, c.accent, 0.14f)
            PostTone.GLOW -> mix(c.dark, c.glow, 0.30f)
            else -> toneColor(c, tone)
        }
    }

    /** look: the brand's backdrop look when it applies (dark-first, template background), else null. */
    private fun backgroundFor(doc: PostDoc, c: Colors, layout: TemplateLayout, look: BackdropLook?): Bg = when (doc.bgMode) {
        BgMode.SOLID -> {
            val solid = parseHex(doc.bgStartHex, c.light)
            Bg(solid, null, solid, 90f)
        }
        BgMode.GRADIENT -> Bg(parseHex(doc.bgStartHex, c.accent), null, parseHex(doc.bgEndHex, c.dark), doc.bgAngle.toFloat())
        BgMode.TEMPLATE -> {
            val backdrop = layout.backdrop
            if (look != null) {
                Bg(
                    parseHex(look.from, c.dark),
                    look.mid?.let { parseHex(it, c.dark) },
                    parseHex(look.to, c.dark),
                    look.angle.toFloat()
                )
            } else if (backdrop != null) {
                Bg(
                    bgTone(c, backdrop.from),
                    backdrop.mid?.let { bgTone(c, it) },
                    bgTone(c, backdrop.to),
                    backdrop.angle
                )
            } else if (c.darkFirst && layout.base == PostBase.LIGHT) {
                Bg(mix(c.dark, c.accent, 0.18f), null, c.dark, 135f)
            } else if (c.darkFirst && layout.base == PostBase.PRIMARY) {
                Bg(mix(c.dark, c.accent, 0.32f), null, mix(c.dark, c.accent, 0.12f), 135f)
            } else {
                val flat = when (layout.base) {
                    PostBase.LIGHT -> c.light
                    PostBase.PRIMARY -> c.accent
                    PostBase.DARK -> c.dark
                }
                Bg(flat, null, flat, 90f)
            }
        }
    }

    private fun drawBackground(canvas: Canvas, bg: Bg, w: Int, h: Int) {
        val mid = bg.mid
        if (bg.from == bg.to && mid == null) {
            canvas.drawColor(bg.from)
            return
        }
        val stops = if (mid != null) intArrayOf(bg.from, mid, bg.to) else intArrayOf(bg.from, bg.to)
        val paint = Paint()
        paint.shader = gradientShader(RectF(0f, 0f, w.toFloat(), h.toFloat()), bg.angle, stops)
        canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), paint)
    }

    /** angle: 0 = left to right, 90 = top to bottom. The gradient line is stretched so it exactly spans the rectangle. */
    private fun gradientShader(r: RectF, angle: Float, stops: IntArray): LinearGradient {
        val rad = Math.toRadians(angle.toDouble())
        val dx = cos(rad).toFloat()
        val dy = sin(rad).toFloat()
        val half = abs(dx) * r.width() / 2f + abs(dy) * r.height() / 2f
        val cx = r.centerX()
        val cy = r.centerY()
        return LinearGradient(cx - dx * half, cy - dy * half, cx + dx * half, cy + dy * half, stops, null, Shader.TileMode.CLAMP)
    }

    private fun toneColor(c: Colors, tone: PostTone): Int = when (tone) {
        PostTone.ACCENT -> c.accent
        PostTone.TEXT -> c.text
        PostTone.MUTED -> withAlpha(c.text, 190)
        PostTone.LIGHT -> c.light
        PostTone.LIGHT_MUTED -> withAlpha(c.light, 205)
        PostTone.SECONDARY -> c.secondary
        PostTone.DARK -> c.dark
        PostTone.BLUE -> Color.rgb(18, 42, 105)
        PostTone.RED -> Color.rgb(211, 25, 38)
        PostTone.DEEP -> c.deep
        PostTone.GLOW -> c.glow
        PostTone.TINT -> c.tint
        PostTone.SOFT -> c.soft
        PostTone.PAGE -> c.page
    }

    private fun isLightTone(tone: PostTone): Boolean =
        tone == PostTone.LIGHT || tone == PostTone.LIGHT_MUTED || tone == PostTone.SECONDARY

    // ---- shapes ----

    /**
     * frame: the photo frame chosen on the post (null when none, or when the photo window fills the whole post).
     * A shape marked frameBack 1 is drawn in that frame's shape instead of its own, so the backing of the photo matches the photo.
     * A shape marked frameBack 2 is a leftover piece of such a backing and is skipped.
     */
    private fun drawDecor(canvas: Canvas, d: DecorBox, c: Colors, w: Int, h: Int, unit: Float, frame: PhotoFrame? = null) {
        if (d.frameBack == 2 && frame != null) return
        val r = RectF(d.x * w, d.y * h, (d.x + d.w) * w, (d.y + d.h) * h)
        if (r.width() <= 0f || r.height() <= 0f) return
        val startColor = withAlpha(toneColor(c, d.tone), d.alpha)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.style = Paint.Style.FILL
        val to = d.gradTo
        if (to != null) {
            val endAlpha = if (d.alphaTo >= 0) d.alphaTo else d.alpha
            paint.shader = gradientShader(r, d.angle, intArrayOf(startColor, withAlpha(toneColor(c, to), endAlpha)))
        } else {
            paint.color = startColor
        }
        if (d.shadow) {
            paint.setShadowLayer(unit * 0.022f, 0f, unit * 0.010f, Color.argb(75, 0, 0, 0))
        } else if (c.darkFirst && d.tone == PostTone.ACCENT && d.alpha >= 120 &&
            (d.shape == DecorShape.ROUND || d.shape == DecorShape.RECT || d.shape == DecorShape.OVAL || d.shape == DecorShape.RING)
        ) {
            // the neon glow of the app: a soft halo of the accent around solid accent shapes
            paint.setShadowLayer(unit * 0.03f, 0f, 0f, withAlpha(c.accent, 120))
        }

        if (d.frameBack == 1 && frame != null) {
            canvas.drawPath(PostEngine.framePath(frame, r, unit), paint)
            return
        }

        when (d.shape) {
            DecorShape.RECT -> canvas.drawRect(r, paint)
            DecorShape.ROUND -> {
                canvas.drawRoundRect(r, d.radius * unit, d.radius * unit, paint)
                if (c.darkFirst && r.width() > unit * 0.2f && r.height() > unit * 0.1f && d.alpha >= 60) {
                    // glassy cards of the app: a thin light border around big rounded cards
                    val edge = Paint(Paint.ANTI_ALIAS_FLAG)
                    edge.style = Paint.Style.STROKE
                    edge.strokeWidth = max(1.5f, unit * 0.0025f)
                    edge.color = withAlpha(Color.WHITE, 46)
                    canvas.drawRoundRect(r, d.radius * unit, d.radius * unit, edge)
                }
            }
            DecorShape.OVAL -> canvas.drawOval(r, paint)
            DecorShape.RING -> {
                val sw = max(1f, d.stroke * unit)
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = sw
                val inset = sw / 2f
                canvas.drawOval(RectF(r.left + inset, r.top + inset, r.right - inset, r.bottom - inset), paint)
            }
            DecorShape.DOTS -> {
                val spacing = unit * 0.03f
                val cols = max(1, (r.width() / spacing).toInt())
                val rows = max(1, (r.height() / spacing).toInt())
                val ox = r.left + (r.width() - (cols - 1) * spacing) / 2f
                val oy = r.top + (r.height() - (rows - 1) * spacing) / 2f
                val radius = spacing * 0.14f
                for (row in 0 until rows) {
                    for (col in 0 until cols) canvas.drawCircle(ox + col * spacing, oy + row * spacing, radius, paint)
                }
            }
            DecorShape.STRIPES -> {
                val spacing = unit * 0.034f
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = spacing * 0.28f
                canvas.save()
                canvas.clipRect(r)
                var x = r.left - r.height()
                while (x < r.right) {
                    canvas.drawLine(x, r.bottom, x + r.height(), r.top, paint)
                    x += spacing
                }
                canvas.restore()
            }
            DecorShape.WAVE -> {
                val path = Path()
                path.moveTo(r.left, r.bottom)
                val steps = 48
                val amp = r.height() * 0.45f
                for (i in 0..steps) {
                    val t = i / steps.toFloat()
                    val y = r.top + r.height() * 0.5f + amp * sin(t * 2.0 * PI * 1.5 + 0.6).toFloat()
                    path.lineTo(r.left + t * r.width(), y)
                }
                path.lineTo(r.right, r.bottom)
                path.close()
                canvas.drawPath(path, paint)
            }
            DecorShape.DIAMOND -> Unit
            DecorShape.TRIANGLE -> Unit
            DecorShape.HEXAGON -> Unit
        }
    }

    // ---- text ----

    /**
     * Draws one slot. Shrinks the text down to 60% of its size to fit; returns false if it still does not fit.
     * A word box takes its color, fonts and size from its style on the post (doc.slotStyles[slot]) when it has one.
     * A fixed mark (box.literal) only takes the color of the "marks" style: its font and size stay as the template placed them,
     * because the template centers it with the font's metrics.
     * In dark-first a light headline gets a soft glow of the accent color.
     */
    private fun drawSlot(
        context: Context, canvas: Canvas, box: SlotBox, rawText: String,
        doc: PostDoc, colors: Colors, w: Int, h: Int, unit: Float
    ): Boolean {
        if (box.slot == PostSlots.LIST || box.list != ListStyle.NONE) return drawList(context, canvas, box, rawText, doc, colors, w, h, unit)
        val own = box.literal == null
        val style = doc.slotStyles[if (own) box.slot else PostSlots.MARKS]
        val text: CharSequence = PostFonts.styled(
            context, if (box.caps) rawText.uppercase(Locale.getDefault()) else rawText, doc, if (own) style?.fontAm else null
        )
        val left = box.x * w
        val top = box.y * h
        val boxW = (box.w * w).toInt().coerceAtLeast(1)
        val boxH = box.h * h
        val paint = TextPaint(Paint.ANTI_ALIAS_FLAG)
        paint.typeface = PostFonts.resolve(context, box.font, doc, if (own) style?.fontEn else null)
        val picked = parseHexOrNull(style?.colorHex)
        paint.color = picked ?: toneColor(colors, box.tone)
        // contrast guard: only for colors the template chose, never for a color the user picked
        val userSet = picked != null || (doc.textHex != null && box.tone == PostTone.TEXT)
        if (!userSet) paint.color = readable(colors, paint.color, left, top, boxW.toFloat(), boxH)
        if (colors.darkFirst && own && box.slot == PostSlots.HEADLINE && relLum(paint.color) > 0.5f) {
            paint.setShadowLayer(unit * 0.022f, 0f, 0f, withAlpha(colors.accent, 100))
        }
        if (box.caps) paint.letterSpacing = 0.12f
        val alignment = if (box.centered) Layout.Alignment.ALIGN_CENTER else Layout.Alignment.ALIGN_NORMAL
        var size = box.size * unit * doc.textScale * (if (own) (style?.scale ?: 1f) else 1f)
        val minSize = size * 0.6f
        var layout = buildLayout(text, paint, size, boxW, alignment)
        while (layout.height > boxH && size > minSize) {
            size *= 0.94f
            layout = buildLayout(text, paint, size, boxW, alignment)
        }
        canvas.save()
        canvas.translate(left, top)
        layout.draw(canvas)
        canvas.restore()
        return layout.height <= boxH + 1f
    }

    /**
     * One line of text per list item, each with a marker (dot, check, letter, number) or a label column. Shrinks as a block.
     * A color picked for the list box colors the text, the markers and the row rules.
     */
    private fun drawList(
        context: Context, canvas: Canvas, box: SlotBox, rawText: String,
        doc: PostDoc, colors: Colors, w: Int, h: Int, unit: Float
    ): Boolean {
        class Row(val mark: String, val body: String)

        val lines = rawText.split('\n').map { it.trim() }.filter { it.isNotEmpty() }
        if (lines.isEmpty()) return true
        val rows = lines.mapIndexed { i, line ->
            when (box.list) {
                ListStyle.DOT -> Row("•", line)
                ListStyle.CHECK -> Row("✓", line)
                ListStyle.LETTER -> Row(('A' + i).toString() + ".", line)
                ListStyle.NUMBER -> Row((i + 1).toString() + ".", line)
                ListStyle.ROW -> {
                    val cut = line.indexOf(':')
                    if (cut > 0) Row(line.substring(0, cut).trim(), line.substring(cut + 1).trim()) else Row("", line)
                }
                ListStyle.NONE -> Row("", line)
            }
        }

        val style = doc.slotStyles[box.slot]
        val ownColor = parseHexOrNull(style?.colorHex)
        val left = box.x * w
        val top = box.y * h
        val boxW = box.w * w
        val boxH = box.h * h
        var markColor = ownColor ?: (if (isLightTone(box.tone)) colors.secondary else colors.accent)
        var textColor = ownColor ?: toneColor(colors, box.tone)
        if (ownColor == null) {
            markColor = readable(colors, markColor, left, top, boxW, boxH)
            if (!(doc.textHex != null && box.tone == PostTone.TEXT)) textColor = readable(colors, textColor, left, top, boxW, boxH)
        }
        val paint = TextPaint(Paint.ANTI_ALIAS_FLAG)
        paint.typeface = PostFonts.resolve(context, box.font, doc, style?.fontEn)
        paint.color = textColor
        val markPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
        markPaint.typeface = PostFonts.get(context, PostFont.BOLD)
        markPaint.color = markColor

        fun indentFor(sz: Float): Float = when (box.list) {
            ListStyle.NONE -> 0f
            ListStyle.DOT, ListStyle.CHECK -> sz * 1.3f
            ListStyle.LETTER, ListStyle.NUMBER -> sz * 1.6f
            ListStyle.ROW -> boxW * 0.3f
        }

        fun gapFor(sz: Float): Float = if (box.list == ListStyle.ROW) sz * 0.55f else sz * 0.45f

        fun build(sz: Float): List<StaticLayout> {
            val width = (boxW - indentFor(sz)).toInt().coerceAtLeast(1)
            return rows.map { buildLayout(PostFonts.styled(context, it.body.ifEmpty { " " }, doc, style?.fontAm), paint, sz, width, Layout.Alignment.ALIGN_NORMAL) }
        }

        var size = box.size * unit * doc.textScale * (style?.scale ?: 1f)
        val minSize = size * 0.6f
        var layouts = build(size)

        fun total(sz: Float): Float = layouts.fold(0f) { acc, l -> acc + l.height } + gapFor(sz) * (layouts.size - 1)

        while (total(size) > boxH && size > minSize) {
            size *= 0.94f
            layouts = build(size)
        }

        paint.textSize = size
        markPaint.textSize = size
        val indent = indentFor(size)
        val gap = gapFor(size)
        val rulePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        rulePaint.color = withAlpha(markColor, 80)
        rulePaint.strokeWidth = max(1.5f, unit * 0.0015f)
        var y = top
        layouts.forEachIndexed { i, layout ->
            val row = rows[i]
            val hasMark = row.mark.isNotEmpty()
            val textLeft = if (box.list == ListStyle.ROW && !hasMark) left else left + indent
            if (hasMark) canvas.drawText(row.mark, left, y - paint.ascent(), markPaint)
            canvas.save()
            canvas.translate(textLeft, y)
            layout.draw(canvas)
            canvas.restore()
            if (box.list == ListStyle.ROW && i < layouts.size - 1) {
                val lineY = y + layout.height + gap / 2f
                canvas.drawLine(left, lineY, left + boxW, lineY, rulePaint)
            }
            y += layout.height + gap
        }
        return total(size) <= boxH + 1f
    }

    private fun buildLayout(text: CharSequence, paint: TextPaint, size: Float, width: Int, alignment: Layout.Alignment): StaticLayout {
        paint.textSize = size
        return StaticLayout.Builder.obtain(text, 0, text.length, paint, width)
            .setAlignment(alignment)
            .setLineSpacing(0f, 1.12f)
            .setIncludePad(false)
            .build()
    }

    // ---- photo and logo ----

    /** Full-canvas photo layer, drawn above the chosen color/gradient and below every template element. decodeTo: the longest side to decode at. */
    private fun drawBackgroundPhoto(context: Context, canvas: Canvas, uri: String, opacity: Float, w: Int, h: Int, decodeTo: Int) {
        val picture = runCatching { decodeSampled(context, Uri.parse(uri), decodeTo) }.getOrNull() ?: return
        val bitmap = picture.bitmap
        val scale = max(w.toFloat() / bitmap.width, h.toFloat() / bitmap.height)
        val cropW = w / scale
        val cropH = h / scale
        val crop = Rect(
            ((bitmap.width - cropW) / 2f).toInt().coerceIn(0, bitmap.width - 1),
            ((bitmap.height - cropH) / 2f).toInt().coerceIn(0, bitmap.height - 1),
            ((bitmap.width + cropW) / 2f).toInt().coerceIn(1, bitmap.width),
            ((bitmap.height + cropH) / 2f).toInt().coerceIn(1, bitmap.height)
        )
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG).apply {
            alpha = (opacity.coerceIn(0f, 1f) * 255).toInt()
        }
        canvas.drawBitmap(bitmap, crop, RectF(0f, 0f, w.toFloat(), h.toFloat()), paint)
        picture.release()
    }

    /**
     * Crops the photo to fill the area (zoom and focus come from the post), then lays a dim layer over it so light text stays readable.
     * A frame picked on the post (rounded, arch, pill, circle, stacked) replaces the template's own shape on windows that do not
     * fill the whole post. The photo is drawn into its own layer and everything OUTSIDE the frame shape is erased from that layer,
     * so the photo really takes the shape (a plain DST_IN mask only changes the pixels the shape itself covers, which left the rectangle).
     */
    private fun drawPhoto(
        context: Context, canvas: Canvas, uri: String, area: PostPhotoArea, doc: PostDoc,
        c: Colors, w: Int, h: Int, unit: Float, decodeTo: Int
    ) {
        val dest = RectF(area.x * w, area.y * h, (area.x + area.w) * w, (area.y + area.h) * h)
        val picture = runCatching { decodeSampled(context, Uri.parse(uri), decodeTo) }.getOrNull() ?: return
        val bmp = picture.bitmap
        val frame = doc.photoFrame?.takeIf { area.w < 0.99f && area.h < 0.99f }
        val framePath = frame?.let { PostEngine.framePath(it, dest, unit) }
        if (frame == PhotoFrame.STACKED) PostEngine.drawStackedBacks(canvas, dest, unit, c.glow, c.secondary)
        if (framePath != null) PostEngine.drawFrameShadow(canvas, framePath, unit)

        val bounds = RectF(dest).apply { inset(-2f, -2f) }
        val layerSave = if (framePath != null) canvas.saveLayer(bounds, null) else canvas.save()
        if (framePath == null && area.mask == PhotoMask.PENTAGON) {
            val cut = dest.height() * 0.16f
            val mask = Path().apply {
                moveTo(dest.left, dest.top)
                lineTo(dest.right, dest.top)
                lineTo(dest.right, dest.bottom - cut)
                lineTo(dest.centerX(), dest.bottom)
                lineTo(dest.left, dest.bottom - cut)
                close()
            }
            canvas.clipPath(mask)
        }
        val zoom = max(1f, doc.photoZoom)
        val scale = max(dest.width() / bmp.width, dest.height() / bmp.height) * zoom
        val cropW = dest.width() / scale
        val cropH = dest.height() / scale
        val left = ((bmp.width - cropW) * doc.photoFocusX.coerceIn(0f, 1f)).coerceIn(0f, max(0f, bmp.width - cropW))
        val top = ((bmp.height - cropH) * doc.photoFocusY.coerceIn(0f, 1f)).coerceIn(0f, max(0f, bmp.height - cropH))
        val src = Rect(
            left.toInt(),
            top.toInt(),
            min(bmp.width, (left + cropW).toInt()),
            min(bmp.height, (top + cropH).toInt())
        )
        canvas.drawBitmap(bmp, src, dest, Paint(Paint.FILTER_BITMAP_FLAG))
        val dimValue = if (doc.photoDim >= 0) doc.photoDim else area.dim
        if (dimValue > 0) {
            val dim = Paint()
            dim.color = Color.argb(dimValue.coerceAtMost(255), 0, 0, 0)
            canvas.drawRect(dest, dim)
        }
        if (framePath != null) {
            // erase everything outside the shape: fill the inverse of the path with DST_OUT (the layer's clip is the photo bounds)
            val outside = Path(framePath).apply { fillType = Path.FillType.INVERSE_WINDING }
            val maskPaint = Paint(Paint.ANTI_ALIAS_FLAG)
            maskPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
            maskPaint.color = Color.BLACK
            canvas.drawPath(outside, maskPaint)
        }
        picture.release()
        canvas.restoreToCount(layerSave)
        if (framePath != null && doc.frameBorder) PostEngine.drawFrameBorder(canvas, framePath, unit, c.light)
    }

    /** A photo decoded no smaller than target on its shorter side. Shared through PostImageCache; call release() on the result when done. */
    private fun decodeSampled(context: Context, uri: Uri, target: Int): PostImageCache.Picture? =
        PostImageCache.decode(uri.toString(), target) { context.contentResolver.openInputStream(uri) }

    /** Logo from the Brand store, or the optional bundled res/drawable-nodpi/wt_logo.png. Null if neither exists. */
    private fun loadLogo(context: Context, brand: PostBrand, targetPx: Int): Bitmap? {
        val path = brand.logoPath
        if (path != null && File(path).exists()) {
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeFile(path, bounds)
            var sample = 1
            while (bounds.outWidth / (sample * 2) >= targetPx * 2 && bounds.outHeight / (sample * 2) >= targetPx * 2) sample *= 2
            val real = BitmapFactory.Options().apply { inSampleSize = sample }
            BitmapFactory.decodeFile(path, real)?.let { return it }
        }
        val id = context.resources.getIdentifier("wt_logo", "drawable", context.packageName)
        if (id != 0) return BitmapFactory.decodeResource(context.resources, id)
        return null
    }

    /**
     * The logo and the box around its visible part, from the cache when the logo file has not changed.
     * Decoding the logo and scanning it for its visible part used to happen on every single redraw.
     */
    private fun logoArt(context: Context, brand: PostBrand, targetPx: Int): LogoArt? {
        val path = brand.logoPath
        val file = if (path != null) File(path) else null
        val source = if (file != null && file.exists()) file.absolutePath + "|" + file.length() + "|" + file.lastModified() else "bundled"
        val key = source + "|" + targetPx
        val hit = logoCache.get(key)
        if (hit != null && !hit.bitmap.isRecycled) return hit
        val bitmap = loadLogo(context, brand, targetPx) ?: return null
        val art = LogoArt(bitmap, visibleBounds(bitmap))
        logoCache.put(key, art)
        return art
    }

    /** The box around the visible part of the logo, so empty transparent margins do not make the header box too big. */
    private fun visibleBounds(bmp: Bitmap): Rect {
        val step = max(1, max(bmp.width, bmp.height) / 256)
        var minX = bmp.width
        var minY = bmp.height
        var maxX = -1
        var maxY = -1
        var y = 0
        while (y < bmp.height) {
            var x = 0
            while (x < bmp.width) {
                if (Color.alpha(bmp.getPixel(x, y)) > 24) {
                    if (x < minX) minX = x
                    if (x > maxX) maxX = x
                    if (y < minY) minY = y
                    if (y > maxY) maxY = y
                }
                x += step
            }
            y += step
        }
        if (maxX < 0) return Rect(0, 0, bmp.width, bmp.height)
        return Rect(
            max(0, minX - step),
            max(0, minY - step),
            min(bmp.width, maxX + step + 1),
            min(bmp.height, maxY + step + 1)
        )
    }

    /**
     * The header: a dark brand-gradient box hanging from the top-right corner with the white logo inside it.
     * Always on the right edge, on every template and size, so the logo reads on light and dark posters alike.
     * On tall posts (9:16) the box is lowered to about 8.5% of the height so Instagram and TikTok top buttons do not cover it.
     * 1:1, 4:5 and 4:3 keep it flush to the top.
     * In dark-first the box is a deep tint of the accent over the dark color, never a bright accent, so the white logo always reads.
     */
    private fun drawLogo(context: Context, canvas: Canvas, doc: PostDoc, brand: PostBrand, c: Colors, w: Int, h: Int, unit: Float) {
        val art = logoArt(context, brand, (unit * 0.12f).toInt()) ?: return
        val bmp = art.bitmap
        val src = art.visible
        val aspect = src.width().toFloat() / max(1, src.height())
        var innerH = unit * 0.056f * brand.logoScale * doc.logoSize
        var innerW = innerH * aspect
        val maxInnerW = unit * 0.34f
        if (innerW > maxInnerW) {
            innerW = maxInnerW
            innerH = innerW / max(0.01f, aspect)
        }
        val padX = unit * 0.034f
        val padY = unit * 0.020f
        val boxW = innerW + padX * 2f
        val boxH = innerH + padY * 2f
        val detached = h >= w * 1.5f
        val top = if (detached) h * 0.085f else 0f
        val bleed = unit * 0.03f
        // the box runs a little past the right (and, when flush, the top) edge so no outline shows there
        val rect = RectF(w - boxW, if (detached) top else top - bleed, w + bleed, top + boxH)
        val r = boxH * 0.32f
        val radii = if (detached) floatArrayOf(r, r, 0f, 0f, 0f, 0f, r, r) else floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, r, r)
        val shape = Path().apply { addRoundRect(rect, radii, Path.Direction.CW) }

        val fill = Paint(Paint.ANTI_ALIAS_FLAG)
        val boxTop = if (c.darkFirst) mix(c.dark, c.accent, 0.22f) else c.deep
        fill.shader = gradientShader(rect, 135f, intArrayOf(boxTop, c.dark))
        fill.setShadowLayer(unit * 0.02f, 0f, unit * 0.008f, Color.argb(90, 0, 0, 0))
        canvas.drawPath(shape, fill)

        val edge = Paint(Paint.ANTI_ALIAS_FLAG)
        edge.style = Paint.Style.STROKE
        edge.strokeWidth = max(1.5f, unit * 0.0028f)
        edge.color = withAlpha(c.secondary, 170)
        canvas.drawPath(shape, edge)

        val left = w - boxW + padX
        val logoTop = top + padY
        canvas.drawBitmap(bmp, src, RectF(left, logoTop, left + innerW, logoTop + innerH), Paint(Paint.FILTER_BITMAP_FLAG or Paint.ANTI_ALIAS_FLAG))
    }

    // ---- icons, footer and social block ----

    /** One icon in the Brand's chosen look. fg is the plain glyph color and the badge ring. An own color on the item wins. */
    private fun drawIconFor(canvas: Canvas, brand: PostBrand, item: FooterItem, cx: Float, cy: Float, d: Float, fg: Int, c: Colors, onDark: Boolean) {
        val custom = parseHexOrNull(item.colorHex)
        when (brand.iconLook) {
            IconLook.PLAIN -> PostIcons.draw(canvas, item.kind, cx, cy, d, custom ?: fg)
            IconLook.BADGE -> {
                val fill = custom ?: if (onDark) c.secondary else c.accent
                PostIcons.drawBadge(canvas, item.kind, cx, cy, d, fill, contrastOn(fill, c), fg, false)
            }
            IconLook.OFFICIAL -> {
                val fill = custom ?: PostIcons.officialColor(item.kind) ?: c.accent
                val insta = custom == null && item.kind == "instagram"
                PostIcons.drawBadge(canvas, item.kind, cx, cy, d, fill, Color.WHITE, fg, insta)
            }
        }
    }

    private fun contrastOn(fill: Int, c: Colors): Int {
        val lum = (0.299f * Color.red(fill) + 0.587f * Color.green(fill) + 0.114f * Color.blue(fill)) / 255f
        return if (lum > 0.6f) c.dark else Color.WHITE
    }

    /**
     * One centered row at the bottom: icon, then label, for each footer item. PILL puts each item in its own chip,
     * LINE draws a thin rule above the row. The row shrinks as a whole to fit the width. fg is the footer color.
     * The label text takes the color picked for the footer text on this post (c.footerText), else fg (checked by the contrast guard).
     */
    private fun drawFooter(
        context: Context, canvas: Canvas, brand: PostBrand, fg: Int, c: Colors, onDark: Boolean,
        w: Int, h: Int, unit: Float
    ) {
        val items = brand.footerItems.filter { it.label.trim().isNotEmpty() }
        if (items.isEmpty()) return
        val margin = unit * 0.06f
        val maxWidth = w - margin * 2
        val pill = brand.footerStyle == FooterStyle.PILL
        val centerY = if (pill) h * 0.935f else h * 0.945f

        if (!pill) {
            val line = Paint(Paint.ANTI_ALIAS_FLAG)
            line.color = withAlpha(fg, 110)
            line.strokeWidth = max(2f, unit * 0.002f)
            canvas.drawLine(margin, h * 0.915f, w - margin, h * 0.915f, line)
        }

        val paint = TextPaint(Paint.ANTI_ALIAS_FLAG)
        paint.typeface = PostFonts.get(context, PostFont.BOLD)
        paint.color = c.footerText ?: readable(c, fg, margin, centerY - unit * 0.04f, maxWidth, unit * 0.08f)

        fun rowWidth(ts: Float): Float {
            paint.textSize = ts
            val icon = ts * 1.5f * brand.iconScale
            val gap = ts * 0.45f
            val pad = if (pill) ts * 0.6f else 0f
            val spacing = if (pill) ts * 0.7f else ts * 1.8f
            var total = 0f
            items.forEach { total += pad * 2f + icon + gap + paint.measureText(it.label.trim()) }
            return total + spacing * (items.size - 1)
        }

        var ts = unit * 0.026f
        val natural = rowWidth(ts)
        if (natural > maxWidth) ts *= maxWidth / natural
        val total = rowWidth(ts)
        paint.textSize = ts
        val icon = ts * 1.5f * brand.iconScale
        val gap = ts * 0.45f
        val pad = if (pill) ts * 0.6f else 0f
        val spacing = if (pill) ts * 0.7f else ts * 1.8f
        val chipH = icon + ts * 0.7f
        val chipFill = Paint(Paint.ANTI_ALIAS_FLAG)
        chipFill.color = withAlpha(fg, if (onDark) 40 else 24)
        val chipEdge = Paint(Paint.ANTI_ALIAS_FLAG)
        chipEdge.style = Paint.Style.STROKE
        chipEdge.strokeWidth = max(1.5f, unit * 0.002f)
        chipEdge.color = withAlpha(fg, 90)

        var x = (w - total) / 2f
        items.forEach { item ->
            val label = item.label.trim()
            val itemW = pad * 2f + icon + gap + paint.measureText(label)
            if (pill) {
                val chip = RectF(x, centerY - chipH / 2f, x + itemW, centerY + chipH / 2f)
                canvas.drawRoundRect(chip, chipH / 2f, chipH / 2f, chipFill)
                canvas.drawRoundRect(chip, chipH / 2f, chipH / 2f, chipEdge)
            }
            drawIconFor(canvas, brand, item, x + pad + icon / 2f, centerY, icon, fg, c, onDark)
            val baseline = centerY - (paint.ascent() + paint.descent()) / 2f
            canvas.drawText(label, x + pad + icon + gap, baseline, paint)
            x += itemW + spacing
        }
    }

    /** The big version of the footer, used by the Social templates: the Brand icons as cards in a grid or a list. */
    private fun drawSocial(
        context: Context, canvas: Canvas, brand: PostBrand, area: SocialArea, c: Colors,
        w: Int, h: Int, unit: Float
    ) {
        val items = brand.footerItems.filter { it.label.trim().isNotEmpty() }
        if (items.isEmpty()) return
        val ax = area.x * w
        val ay = area.y * h
        val aw = area.w * w
        val ah = area.h * h
        val gap = unit * 0.025f
        val n = items.size

        if (area.style == SocialStyle.GRID) {
            val cols = if (n == 1) 1 else 2
            val rows = (n + cols - 1) / cols
            val cellW = (aw - gap * (cols - 1)) / cols
            val cellH = min((ah - gap * (rows - 1)) / rows, unit * 0.17f)
            val gridH = cellH * rows + gap * (rows - 1)
            val top = ay + (ah - gridH) / 2f
            items.forEachIndexed { i, item ->
                val row = i / cols
                val inRow = if (row == rows - 1) n - row * cols else cols
                val rowW = inRow * cellW + gap * (inRow - 1)
                val left = ax + (aw - rowW) / 2f + (i - row * cols) * (cellW + gap)
                val y = top + row * (cellH + gap)
                drawSocialCell(context, canvas, brand, item, RectF(left, y, left + cellW, y + cellH), c, area.onDark, unit)
            }
        } else {
            val rowH = min((ah - gap * (n - 1)) / n, unit * 0.115f)
            val listH = rowH * n + gap * (n - 1)
            val top = ay + (ah - listH) / 2f
            items.forEachIndexed { i, item ->
                val y = top + i * (rowH + gap)
                drawSocialCell(context, canvas, brand, item, RectF(ax, y, ax + aw, y + rowH), c, area.onDark, unit)
            }
        }
    }

    private fun drawSocialCell(
        context: Context, canvas: Canvas, brand: PostBrand, item: FooterItem,
        cell: RectF, c: Colors, onDark: Boolean, unit: Float
    ) {
        val radius = cell.height() * 0.28f
        val fill = Paint(Paint.ANTI_ALIAS_FLAG)
        fill.color = if (onDark) withAlpha(Color.WHITE, 30) else withAlpha(c.accent, 18)
        canvas.drawRoundRect(cell, radius, radius, fill)
        val edge = Paint(Paint.ANTI_ALIAS_FLAG)
        edge.style = Paint.Style.STROKE
        edge.strokeWidth = max(1.5f, unit * 0.002f)
        edge.color = if (onDark) withAlpha(Color.WHITE, 80) else withAlpha(c.accent, 60)
        canvas.drawRoundRect(cell, radius, radius, edge)

        val wanted = c.footerText ?: (if (onDark) c.light else c.text)
        val textColor = c.footerText ?: readable(c, wanted, cell.left, cell.top, cell.width(), cell.height())
        val d = min(cell.height() * 0.60f * brand.iconScale, cell.height() * 0.84f)
        val pad = (cell.height() - d) / 2f
        drawIconFor(canvas, brand, item, cell.left + pad + d / 2f, cell.centerY(), d, if (onDark) c.light else c.accent, c, onDark)

        val textLeft = cell.left + pad * 2f + d
        val avail = (cell.right - pad - textLeft).coerceAtLeast(1f)
        val label = item.label.trim()
        val paint = TextPaint(Paint.ANTI_ALIAS_FLAG)
        paint.typeface = PostFonts.get(context, PostFont.BOLD)
        paint.color = textColor
        var size = cell.height() * 0.27f
        paint.textSize = size
        while (paint.measureText(label) > avail && size > cell.height() * 0.12f) {
            size *= 0.94f
            paint.textSize = size
        }
        val caption = TextPaint(paint)
        caption.typeface = PostFonts.get(context, PostFont.REGULAR)
        caption.color = withAlpha(textColor, 175)
        caption.textSize = size * 0.62f
        val between = size * 0.18f
        val block = (caption.descent() - caption.ascent()) + between + (paint.descent() - paint.ascent())
        val top = cell.centerY() - block / 2f
        canvas.drawText(PostIcons.title(item.kind), textLeft, top - caption.ascent(), caption)
        canvas.drawText(label, textLeft, top + (caption.descent() - caption.ascent()) + between - paint.ascent(), paint)
    }

    // ---- small helpers ----

    private fun mix(a: Int, b: Int, t: Float): Int = Color.rgb(
        (Color.red(a) + (Color.red(b) - Color.red(a)) * t).toInt().coerceIn(0, 255),
        (Color.green(a) + (Color.green(b) - Color.green(a)) * t).toInt().coerceIn(0, 255),
        (Color.blue(a) + (Color.blue(b) - Color.blue(a)) * t).toInt().coerceIn(0, 255)
    )

    private fun parseHex(hex: String, fallback: Int): Int =
        runCatching { Color.parseColor(if (hex.startsWith("#")) hex else "#" + hex) }.getOrDefault(fallback)

    private fun parseHexOrNull(hex: String?): Int? {
        if (hex.isNullOrBlank()) return null
        return runCatching { Color.parseColor(if (hex.startsWith("#")) hex else "#" + hex) }.getOrNull()
    }

    private fun withAlpha(c: Int, alpha: Int): Int = Color.argb(alpha.coerceIn(0, 255), Color.red(c), Color.green(c), Color.blue(c))
}
