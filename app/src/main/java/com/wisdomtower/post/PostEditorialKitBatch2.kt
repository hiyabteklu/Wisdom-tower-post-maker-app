package com.wisdomtower.post

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BlurMaskFilter
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
import android.text.TextPaint
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * Senior-designer editorial & photo-manipulation rendering kit — Batch 2 (Templates 7–12):
 *  7. Orbital Halo Type (3D tilted curved text ring weaving behind + in front of cutout)
 *  8. Wet Obsidian Mirror Stage (glossy studio floor + real live flipped cutout reflection)
 *  9. Kinetic Ribbon Weave (criss-crossing diagonal ticker ribbons behind + over cutout with cast shadow)
 * 10. Duotone Studio Gel Beam (hard architectural snoot light shaft + exact beam polygon for color-inverting type)
 * 11. Typographic Plinth (monumental 3D extruded stand-on letters/numbers for cutout to perch/lean on)
 * 12. Darkroom Spotlight Aperture (luminous arched keyhole lens reveal in velvety negative space)
 */
object PostEditorialKitBatch2 {

    private fun withAlpha(color: Int, alpha: Int): Int =
        Color.argb(alpha.coerceIn(0, 255), Color.red(color), Color.green(color), Color.blue(color))

    private fun blend(a: Int, b: Int, t: Float): Int {
        val f = t.coerceIn(0f, 1f)
        return Color.argb(
            (Color.alpha(a) + (Color.alpha(b) - Color.alpha(a)) * f).roundToInt().coerceIn(0, 255),
            (Color.red(a) + (Color.red(b) - Color.red(a)) * f).roundToInt().coerceIn(0, 255),
            (Color.green(a) + (Color.green(b) - Color.green(a)) * f).roundToInt().coerceIn(0, 255),
            (Color.blue(a) + (Color.blue(b) - Color.blue(a)) * f).roundToInt().coerceIn(0, 255)
        )
    }

    private fun luminance(color: Int): Float =
        (0.299f * Color.red(color) + 0.587f * Color.green(color) + 0.114f * Color.blue(color)) / 255f

    private fun ellipseArcPath(
        cx: Float,
        cy: Float,
        rx: Float,
        ry: Float,
        startDeg: Float,
        sweepDeg: Float,
        steps: Int = 72
    ): Path {
        val path = Path()
        for (i in 0..steps) {
            val t = i.toFloat() / steps.toFloat()
            val rad = Math.toRadians((startDeg + sweepDeg * t).toDouble())
            val px = cx + rx * cos(rad).toFloat()
            val py = cy + ry * sin(rad).toFloat()
            if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
        }
        return path
    }

    // ============================================================================================
    // 7. ORBITAL HALO TYPE (3D Tilted Curved Text Ring: Back Arc Behind Cutout + Front Arc Over)
    // ============================================================================================

    fun drawOrbitalHalo(
        context: Context,
        canvas: Canvas,
        rect: RectF,
        doc: PostDoc,
        accentColor: Int,
        secondaryColor: Int,
        glowColor: Int,
        lightColor: Int,
        darkColor: Int,
        deepColor: Int,
        isLightBase: Boolean,
        isFrontPass: Boolean,
        unit: Float
    ) {
        val panelFactor = (doc.panelOpacity.coerceIn(0, 100) / 100f).coerceAtLeast(0.15f)
        val shadowStrength = doc.sceneShadowStrength.coerceIn(0f, 1f)
        val cx = rect.centerX()
        val cy = rect.top + rect.height() * 0.54f
        val rx = rect.width() * 0.455f
        val ry = min(rect.height() * 0.21f, rx * 0.34f)
        val tiltDeg = -13.5f

        val rawRing = doc.texts[PostSlots.WORD]?.trim().orEmpty().ifBlank { "MASTERCLASS · LIVE · 2026" }
        val isEthiopic = PostSlots.containsEthiopic(rawRing)
        val phrase = if (isEthiopic) "$rawRing  ✦  " else "${rawRing.uppercase()}  ✦  "
        val bandThickness = max(24f, min(rx * 0.145f, unit * 0.052f))

        canvas.save()
        canvas.rotate(tiltDeg, cx, cy)

        if (!isFrontPass) {
            // Back pass: atmospheric halo bloom behind the subject
            val bloomR = max(rx * 1.05f, rect.height() * 0.48f)
            val bloomPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = RadialGradient(
                    cx, cy - ry * 0.25f, bloomR,
                    intArrayOf(
                        withAlpha(accentColor, ((if (isLightBase) 54f else 98f) * panelFactor).roundToInt()),
                        withAlpha(secondaryColor, ((if (isLightBase) 24f else 48f) * panelFactor).roundToInt()),
                        withAlpha(accentColor, 0)
                    ),
                    floatArrayOf(0f, 0.48f, 1f),
                    Shader.TileMode.CLAMP
                )
            }
            canvas.drawCircle(cx, cy - ry * 0.25f, bloomR, bloomPaint)

            // Secondary fine orbital wireframe track behind subject
            val wirePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = max(1.4f, unit * 0.0022f)
                color = withAlpha(if (isLightBase) darkColor else glowColor, (90 * panelFactor).roundToInt())
            }
            val outerWire = ellipseArcPath(cx, cy, rx * 1.12f, ry * 1.24f, 180f, 180f)
            canvas.drawPath(outerWire, wirePaint)

            // Rear half of the 3D orbital band (180° -> 360° = upper/far arc)
            val backArc = ellipseArcPath(cx, cy, rx, ry, 182f, 176f)
            val backBandColor = if (isLightBase) {
                blend(darkColor, accentColor, 0.32f)
            } else {
                blend(deepColor, accentColor, 0.36f)
            }
            val backRibbonPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = bandThickness * 0.86f
                strokeCap = Paint.Cap.BUTT
                shader = LinearGradient(
                    cx - rx, cy, cx + rx, cy,
                    intArrayOf(
                        withAlpha(backBandColor, (140 * panelFactor).roundToInt()),
                        withAlpha(blend(backBandColor, darkColor, 0.35f), (215 * panelFactor).roundToInt()),
                        withAlpha(backBandColor, (140 * panelFactor).roundToInt())
                    ),
                    floatArrayOf(0f, 0.5f, 1f),
                    Shader.TileMode.CLAMP
                )
            }
            canvas.drawPath(backArc, backRibbonPaint)

            // Subtle rear rim lines
            val rimStroke = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = max(1.2f, unit * 0.0018f)
                color = withAlpha(if (isLightBase) accentColor else glowColor, (110 * panelFactor).roundToInt())
            }
            canvas.drawPath(ellipseArcPath(cx, cy, rx, ry - bandThickness * 0.43f, 182f, 176f), rimStroke)
            canvas.drawPath(ellipseArcPath(cx, cy, rx, ry + bandThickness * 0.43f, 182f, 176f), rimStroke)

            // Dimmed rear curved text running along the far side of the orbit
            val tf = PostFonts.get(context, PostFont.CONDENSED, phrase, doc, doc.slotStyles[PostSlots.WORD])
            val backTextSize = bandThickness * (if (isEthiopic) 0.52f else 0.58f)
            val backTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                typeface = tf
                textSize = backTextSize
                letterSpacing = if (isEthiopic) 0.04f else 0.14f
                color = withAlpha(if (isLightBase) lightColor else tintOf(lightColor, accentColor), (135 * panelFactor).roundToInt())
            }
            val backRepeated = repeatToArc(phrase, backTextPaint, (PI * (rx + ry)).toFloat() * 0.7f)
            val vOff = -(backTextPaint.ascent() + backTextPaint.descent()) * 0.5f
            canvas.drawTextOnPath(backRepeated, backArc, 0f, vOff, backTextPaint)
        } else {
            // Front pass (DRAWN OVER CUTOUT!): cast shadow + luminous foreground orbital band + crisp curved type
            val frontArc = ellipseArcPath(cx, cy, rx, ry, 178f, -176f) // Left -> bottom -> right so text reads naturally L->R!

            // 1. Soft shadow cast by the front orbital ring onto the cutout subject
            if (shadowStrength > 0.05f) {
                val castShadow = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    style = Paint.Style.STROKE
                    strokeWidth = bandThickness * 1.18f
                    color = withAlpha(Color.BLACK, (120f * shadowStrength * panelFactor).roundToInt())
                    maskFilter = BlurMaskFilter(max(8f, bandThickness * 0.36f), BlurMaskFilter.Blur.NORMAL)
                }
                canvas.save()
                canvas.translate(0f, bandThickness * 0.24f)
                canvas.drawPath(frontArc, castShadow)
                canvas.restore()
            }

            // 2. Crisp vibrant foreground orbital band with specular studio sweep
            val frontRibbonPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = bandThickness
                strokeCap = Paint.Cap.ROUND
                shader = LinearGradient(
                    cx - rx, cy, cx + rx, cy,
                    intArrayOf(
                        withAlpha(blend(accentColor, darkColor, 0.20f), (238 * panelFactor).roundToInt()),
                        withAlpha(accentColor, (252 * panelFactor).roundToInt()),
                        withAlpha(glowColor, (255 * panelFactor).roundToInt()),
                        withAlpha(accentColor, (252 * panelFactor).roundToInt()),
                        withAlpha(blend(accentColor, darkColor, 0.22f), (238 * panelFactor).roundToInt())
                    ),
                    floatArrayOf(0f, 0.24f, 0.50f, 0.76f, 1f),
                    Shader.TileMode.CLAMP
                )
            }
            canvas.drawPath(frontArc, frontRibbonPaint)

            // 3. Top and bottom metallic chamfer highlight rims
            val topEdge = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = max(1.6f, unit * 0.0024f)
                color = withAlpha(Color.WHITE, (180 * panelFactor).roundToInt())
            }
            canvas.drawPath(ellipseArcPath(cx, cy, rx, ry - bandThickness * 0.46f, 178f, -176f), topEdge)

            val outerFrontWire = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = max(1.5f, unit * 0.0022f)
                color = withAlpha(if (isLightBase) darkColor else Color.WHITE, (145 * panelFactor).roundToInt())
            }
            canvas.drawPath(ellipseArcPath(cx, cy, rx * 1.12f, ry * 1.24f, 180f, -180f), outerFrontWire)

            // 4. High-contrast curved typography wrapping right across the front of the cutout
            val tf = PostFonts.get(context, PostFont.DISPLAY, phrase, doc, doc.slotStyles[PostSlots.WORD])
            val frontTextSize = bandThickness * (if (isEthiopic) 0.56f else 0.62f)
            val inkOnAccent = if (luminance(accentColor) > 0.44f) darkColor else Color.WHITE
            val frontTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                typeface = tf
                textSize = frontTextSize
                letterSpacing = if (isEthiopic) 0.04f else 0.14f
                color = inkOnAccent
                isSubpixelText = true
            }
            val frontRepeated = repeatToArc(phrase, frontTextPaint, (PI * (rx + ry)).toFloat() * 0.74f)
            val vOff = -(frontTextPaint.ascent() + frontTextPaint.descent()) * 0.5f
            canvas.drawTextOnPath(frontRepeated, frontArc, bandThickness * 0.4f, vOff, frontTextPaint)

            // 5. Twin orbital node stars on left/right equator points
            val nodeR = max(4.5f, unit * 0.007f)
            val nodePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE }
            val nodeGlow = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = withAlpha(glowColor, 180)
                maskFilter = BlurMaskFilter(nodeR * 1.8f, BlurMaskFilter.Blur.NORMAL)
            }
            for (sx in listOf(cx - rx * 1.12f, cx + rx * 1.12f)) {
                canvas.drawCircle(sx, cy, nodeR * 1.6f, nodeGlow)
                canvas.drawCircle(sx, cy, nodeR, nodePaint)
            }
        }

        canvas.restore()
    }

    private fun tintOf(lightColor: Int, accentColor: Int): Int = blend(lightColor, accentColor, 0.25f)

    private fun repeatToArc(phrase: String, paint: TextPaint, targetWidth: Float): String {
        val singleW = max(12f, paint.measureText(phrase))
        val count = ((targetWidth / singleW).toInt() + 2).coerceIn(2, 16)
        return buildString {
            repeat(count) { append(phrase) }
        }
    }

    // ============================================================================================
    // 8. WET OBSIDIAN MIRROR STAGE (Glossy Studio Floor + Real Live Flipped Cutout Reflection)
    // ============================================================================================

    fun drawObsidianMirrorStage(
        canvas: Canvas,
        rect: RectF,
        doc: PostDoc,
        accentColor: Int,
        secondaryColor: Int,
        glowColor: Int,
        darkColor: Int,
        deepColor: Int,
        isLightBase: Boolean,
        w: Float,
        h: Float,
        unit: Float
    ) {
        val panelFactor = (doc.panelOpacity.coerceIn(0, 100) / 100f).coerceAtLeast(0.20f)
        val horizonY = rect.top.coerceIn(h * 0.45f, h * 0.85f)

        // 1. Overhead studio cyclorama light dome above the horizon
        val domeCx = rect.centerX()
        val domeR = max(w * 0.62f, h * 0.48f)
        val domePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(
                domeCx, horizonY - domeR * 0.36f, domeR,
                intArrayOf(
                    withAlpha(glowColor, ((if (isLightBase) 64f else 108f) * panelFactor).roundToInt()),
                    withAlpha(accentColor, ((if (isLightBase) 28f else 58f) * panelFactor).roundToInt()),
                    withAlpha(accentColor, 0)
                ),
                floatArrayOf(0f, 0.46f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f, 0f, w, horizonY, domePaint)

        // 2. Glossy wet obsidian / polished marble reflective floor plane
        val floorTopColor = if (isLightBase) {
            blend(Color.WHITE, accentColor, 0.10f)
        } else {
            blend(Color.rgb(4, 6, 12), deepColor, 0.38f)
        }
        val floorMidColor = if (isLightBase) {
            blend(Color.rgb(232, 236, 245), accentColor, 0.14f)
        } else {
            Color.rgb(2, 4, 8)
        }
        val floorBotColor = if (isLightBase) {
            blend(Color.rgb(214, 221, 234), darkColor, 0.08f)
        } else {
            blend(Color.rgb(5, 8, 16), accentColor, 0.10f)
        }

        val floorPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                0f, horizonY, 0f, h,
                intArrayOf(floorTopColor, floorMidColor, floorBotColor),
                floatArrayOf(0f, 0.34f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f, horizonY, w, h, floorPaint)

        // 3. Soft reflected accent light pool across the wet mirror surface
        val poolRect = RectF(
            domeCx - w * 0.46f,
            horizonY - (h - horizonY) * 0.10f,
            domeCx + w * 0.46f,
            horizonY + (h - horizonY) * 0.72f
        )
        val poolPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(
                domeCx, horizonY + (h - horizonY) * 0.12f, max(12f, poolRect.width() * 0.52f),
                intArrayOf(
                    withAlpha(accentColor, ((if (isLightBase) 60f else 118f) * panelFactor).roundToInt()),
                    withAlpha(secondaryColor, ((if (isLightBase) 20f else 42f) * panelFactor).roundToInt()),
                    Color.TRANSPARENT
                ),
                floatArrayOf(0f, 0.45f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.save()
        canvas.clipRect(0f, horizonY, w, h)
        canvas.drawOval(poolRect, poolPaint)

        // 4. Subtle architectural mirror perspective lines receding to the horizon
        val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            strokeWidth = max(1f, unit * 0.0014f)
            color = withAlpha(if (isLightBase) darkColor else glowColor, (32 * panelFactor).roundToInt())
        }
        val rayCount = 9
        for (i in -rayCount..rayCount) {
            val topX = domeCx + i * w * 0.075f
            val botX = domeCx + i * w * 0.24f
            canvas.drawLine(topX, horizonY, botX, h, gridPaint)
        }
        // Horizontal wet-gloss specular streaks
        val streakFracs = floatArrayOf(0.06f, 0.15f, 0.28f, 0.46f, 0.70f)
        for ((idx, frac) in streakFracs.withIndex()) {
            val sy = horizonY + (h - horizonY) * frac
            val streakAlpha = ((44 - idx * 7).coerceAtLeast(10) * panelFactor).roundToInt()
            val streakPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                strokeWidth = max(1f, unit * 0.0014f)
                shader = LinearGradient(
                    w * 0.08f, sy, w * 0.92f, sy,
                    intArrayOf(Color.TRANSPARENT, withAlpha(if (isLightBase) Color.WHITE else glowColor, streakAlpha), Color.TRANSPARENT),
                    floatArrayOf(0f, 0.5f, 1f),
                    Shader.TileMode.CLAMP
                )
            }
            canvas.drawLine(w * 0.08f, sy, w * 0.92f, sy, streakPaint)
        }
        canvas.restore()

        // 5. Razor-sharp illuminated horizon seam
        val seamGlow = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            strokeWidth = max(5f, unit * 0.009f)
            color = withAlpha(accentColor, ((if (isLightBase) 65f else 125f) * panelFactor).roundToInt())
            maskFilter = BlurMaskFilter(max(6f, unit * 0.012f), BlurMaskFilter.Blur.NORMAL)
        }
        canvas.drawLine(0f, horizonY, w, horizonY, seamGlow)

        val seamLine = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            strokeWidth = max(1.6f, unit * 0.0022f)
            shader = LinearGradient(
                0f, horizonY, w, horizonY,
                intArrayOf(
                    Color.TRANSPARENT,
                    withAlpha(if (isLightBase) accentColor else Color.WHITE, (215 * panelFactor).roundToInt()),
                    withAlpha(glowColor, (245 * panelFactor).roundToInt()),
                    withAlpha(if (isLightBase) accentColor else Color.WHITE, (215 * panelFactor).roundToInt()),
                    Color.TRANSPARENT
                ),
                floatArrayOf(0f, 0.22f, 0.5f, 0.78f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawLine(0f, horizonY, w, horizonY, seamLine)
    }

    /**
     * Auto-reflects the user's actual BG-removed cutout PNG (or default subject) onto the wet obsidian mirror floor!
     */
    fun drawObsidianMirrorCutoutReflection(
        canvas: Canvas,
        cutoutBitmap: Bitmap?,
        doc: PostDoc,
        layout: TemplateLayout,
        accentColor: Int,
        w: Float,
        h: Float
    ) {
        val (normCx, normBottomY) = PostEngine.resolvedCutoutPos(doc)
        val cx = normCx * w
        val bottomY = normBottomY * h
        val scale = doc.cutoutScale.coerceIn(0.35f, 2.4f)
        val targetH = h * 0.66f * scale

        if (cutoutBitmap != null && !cutoutBitmap.isRecycled) {
            val aspect = cutoutBitmap.width.toFloat() / max(1, cutoutBitmap.height).toFloat()
            val targetW = targetH * aspect
            val reflH = targetH * 0.56f
            val layerBounds = RectF(cx - targetW * 0.65f, bottomY - 4f, cx + targetW * 0.65f, min(h, bottomY + reflH + 12f))
            if (layerBounds.height() <= 4f || layerBounds.width() <= 4f) return

            val saveId = canvas.saveLayer(layerBounds, null)
            // Vertically flipped & gently perspective-compressed cutout bitmap
            canvas.save()
            canvas.translate(cx, bottomY)
            canvas.scale(1f, -0.56f)
            val dst = RectF(-targetW * 0.5f, 0f, targetW * 0.5f, targetH)
            val reflBmpPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG).apply {
                alpha = 148
            }
            canvas.drawBitmap(cutoutBitmap, null, dst, reflBmpPaint)
            canvas.restore()

            // Smooth glossy alpha fade-out downward using DST_IN
            val fadePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
                shader = LinearGradient(
                    0f, bottomY, 0f, bottomY + reflH * 0.88f,
                    intArrayOf(Color.argb(195, 255, 255, 255), Color.argb(72, 255, 255, 255), Color.TRANSPARENT),
                    floatArrayOf(0f, 0.45f, 1f),
                    Shader.TileMode.CLAMP
                )
            }
            canvas.drawRect(layerBounds, fadePaint)
            canvas.restoreToCount(saveId)
        } else if (layout.cutoutShowDefault || layout.cutout != null) {
            // Subtle glossy mirrored silhouette echo when no cutout PNG is inserted yet
            val reflW = w * 0.24f * scale
            val reflH = min(h - bottomY, h * 0.22f * scale)
            if (reflH > 6f) {
                val echoPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    shader = RadialGradient(
                        cx, bottomY + reflH * 0.16f, max(12f, reflW * 0.95f),
                        intArrayOf(withAlpha(accentColor, 85), withAlpha(accentColor, 26), Color.TRANSPARENT),
                        floatArrayOf(0f, 0.48f, 1f),
                        Shader.TileMode.CLAMP
                    )
                }
                canvas.drawOval(RectF(cx - reflW, bottomY - reflH * 0.10f, cx + reflW, bottomY + reflH), echoPaint)
            }
        }
    }

    // ============================================================================================
    // 9. KINETIC RIBBON WEAVE (Criss-Crossing Diagonal Ticker Bands Behind + Over Cutout)
    // ============================================================================================

    fun drawKineticRibbonWeave(
        context: Context,
        canvas: Canvas,
        rect: RectF,
        doc: PostDoc,
        accentColor: Int,
        secondaryColor: Int,
        glowColor: Int,
        lightColor: Int,
        darkColor: Int,
        deepColor: Int,
        isLightBase: Boolean,
        isFrontPass: Boolean,
        w: Float,
        h: Float,
        unit: Float
    ) {
        val panelFactor = (doc.panelOpacity.coerceIn(0, 100) / 100f).coerceAtLeast(0.20f)
        val shadowStrength = doc.sceneShadowStrength.coerceIn(0f, 1f)
        val rawWord = doc.texts[PostSlots.WORD]?.trim().orEmpty().ifBlank { "WISDOM TOWER · TOP SCHOLARS" }
        val isEthiopic = PostSlots.containsEthiopic(rawWord)
        val tickerText = if (isEthiopic) "$rawWord  ●  " else "${rawWord.uppercase()}  ●  "

        val ribbonH = max(32f, min(unit * 0.068f, rect.height() * 0.135f))
        val spanW = max(w, h) * 1.65f
        val tf = PostFonts.get(context, PostFont.DISPLAY, tickerText, doc, doc.slotStyles[PostSlots.WORD])

        if (!isFrontPass) {
            // Back ribbon: deeper contrasting tone angled -11.5° passing BEHIND the cutout
            val cy = rect.top + rect.height() * 0.38f
            canvas.save()
            canvas.translate(rect.centerX(), cy)
            canvas.rotate(-11.5f)

            val backFill = if (isLightBase) {
                blend(darkColor, deepColor, 0.25f)
            } else {
                blend(deepColor, secondaryColor, 0.28f)
            }
            val backRect = RectF(-spanW * 0.5f, -ribbonH * 0.5f, spanW * 0.5f, ribbonH * 0.5f)
            val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = withAlpha(backFill, (230 * panelFactor).roundToInt())
            }
            canvas.drawRect(backRect, fillPaint)

            // Crisp micro hairline borders
            val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                strokeWidth = max(1.4f, unit * 0.002f)
                color = withAlpha(accentColor, (150 * panelFactor).roundToInt())
            }
            canvas.drawLine(backRect.left, backRect.top + 1f, backRect.right, backRect.top + 1f, borderPaint)
            canvas.drawLine(backRect.left, backRect.bottom - 1f, backRect.right, backRect.bottom - 1f, borderPaint)

            val tp = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                typeface = tf
                textSize = ribbonH * (if (isEthiopic) 0.50f else 0.56f)
                letterSpacing = if (isEthiopic) 0.04f else 0.12f
                color = withAlpha(if (isLightBase) lightColor else glowColor, (185 * panelFactor).roundToInt())
            }
            val repeated = repeatToArc(tickerText, tp, spanW * 1.1f)
            val baseline = -(tp.ascent() + tp.descent()) * 0.5f
            canvas.drawText(repeated, -spanW * 0.48f, baseline, tp)
            canvas.restore()
        } else {
            // Front ribbon: bold vibrant accent angled +8.5° sweeping IN FRONT of the cutout with cast shadow!
            val cy = rect.top + rect.height() * 0.68f
            canvas.save()
            canvas.translate(rect.centerX(), cy)
            canvas.rotate(8.5f)

            val frontRect = RectF(-spanW * 0.5f, -ribbonH * 0.54f, spanW * 0.5f, ribbonH * 0.54f)

            // 1. Cast drop shadow directly onto the cutout behind it
            if (shadowStrength > 0.05f) {
                val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = withAlpha(Color.BLACK, (145f * shadowStrength * panelFactor).roundToInt())
                    maskFilter = BlurMaskFilter(max(10f, ribbonH * 0.34f), BlurMaskFilter.Blur.NORMAL)
                }
                canvas.save()
                canvas.translate(0f, ribbonH * 0.22f)
                canvas.drawRect(frontRect, shadowPaint)
                canvas.restore()
            }

            // 2. Vibrant metallic/editorial accent ticker strip
            val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = LinearGradient(
                    -w * 0.55f, 0f, w * 0.55f, 0f,
                    intArrayOf(
                        withAlpha(blend(accentColor, darkColor, 0.16f), (248 * panelFactor).roundToInt()),
                        withAlpha(accentColor, (255 * panelFactor).roundToInt()),
                        withAlpha(glowColor, (255 * panelFactor).roundToInt()),
                        withAlpha(accentColor, (255 * panelFactor).roundToInt())
                    ),
                    floatArrayOf(0f, 0.32f, 0.62f, 1f),
                    Shader.TileMode.CLAMP
                )
            }
            canvas.drawRect(frontRect, fillPaint)

            // Crisp top specular edge
            val specPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                strokeWidth = max(1.8f, unit * 0.0024f)
                color = withAlpha(Color.WHITE, (185 * panelFactor).roundToInt())
            }
            canvas.drawLine(frontRect.left, frontRect.top + 1f, frontRect.right, frontRect.top + 1f, specPaint)

            // 3. Punchy foreground marquee text
            val ink = if (luminance(accentColor) > 0.44f) darkColor else Color.WHITE
            val tp = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                typeface = tf
                textSize = ribbonH * (if (isEthiopic) 0.54f else 0.60f)
                letterSpacing = if (isEthiopic) 0.04f else 0.12f
                color = ink
                isSubpixelText = true
            }
            val repeated = repeatToArc(tickerText, tp, spanW * 1.1f)
            val baseline = -(tp.ascent() + tp.descent()) * 0.5f
            canvas.drawText(repeated, -spanW * 0.44f, baseline, tp)
            canvas.restore()
        }
    }

    // ============================================================================================
    // 10. DUOTONE STUDIO GEL BEAM (Hard Architectural Snoot Light Shaft + Color-Inverting Split)
    // ============================================================================================

    fun buildDuotoneBeamPath(doc: PostDoc, w: Float, h: Float): Path {
        // Subtle user-steerable beam tilt driven by sceneLightAngle (default 35°)
        val angleDelta = ((doc.sceneLightAngle - 35f) / 180f).coerceIn(-0.18f, 0.18f)
        val topLeftX = (0.36f + angleDelta * 0.6f) * w
        val topRightX = (0.76f + angleDelta * 0.6f) * w
        val botLeftX = (0.04f - angleDelta * 0.7f) * w
        val botRightX = (0.64f - angleDelta * 0.7f) * w

        return Path().apply {
            moveTo(topLeftX, -h * 0.04f)
            lineTo(topRightX, -h * 0.04f)
            lineTo(botRightX, h * 1.04f)
            lineTo(botLeftX, h * 1.04f)
            close()
        }
    }

    fun drawDuotoneGelBeamBack(
        canvas: Canvas,
        doc: PostDoc,
        accentColor: Int,
        secondaryColor: Int,
        glowColor: Int,
        darkColor: Int,
        isLightBase: Boolean,
        w: Float,
        h: Float,
        unit: Float
    ) {
        val panelFactor = (doc.panelOpacity.coerceIn(0, 100) / 100f).coerceAtLeast(0.25f)
        val beamPath = buildDuotoneBeamPath(doc, w, h)

        // 1. Secondary ambient gel wash in the opposite studio corner (true dual-gel contrast!)
        val cornerGlow = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(
                w * 0.88f, h * 0.86f, max(w, h) * 0.68f,
                intArrayOf(
                    withAlpha(secondaryColor, ((if (isLightBase) 55f else 108f) * panelFactor).roundToInt()),
                    withAlpha(secondaryColor, 0)
                ),
                floatArrayOf(0f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f, 0f, w, h, cornerGlow)

        // 2. Soft volumetric outer halo bleeding around the hard snoot shaft
        val outerHalo = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = withAlpha(accentColor, ((if (isLightBase) 42f else 88f) * panelFactor).roundToInt())
            maskFilter = BlurMaskFilter(max(24f, unit * 0.048f), BlurMaskFilter.Blur.NORMAL)
        }
        canvas.drawPath(beamPath, outerHalo)

        // 3. Crisp hard-edged architectural light beam fill
        val beamFill = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                w * 0.56f, 0f, w * 0.30f, h,
                intArrayOf(
                    withAlpha(glowColor, (242 * panelFactor).roundToInt()),
                    withAlpha(accentColor, (232 * panelFactor).roundToInt()),
                    withAlpha(blend(accentColor, secondaryColor, 0.25f), (196 * panelFactor).roundToInt())
                ),
                floatArrayOf(0f, 0.48f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawPath(beamPath, beamFill)

        // 4. Razor-sharp architectural studio light edge highlights
        val edgePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = max(2f, unit * 0.0026f)
            color = withAlpha(if (isLightBase) darkColor else Color.WHITE, (130 * panelFactor).roundToInt())
        }
        canvas.drawPath(beamPath, edgePaint)

        // 5. Fine atmospheric studio motes floating inside the light shaft
        canvas.save()
        canvas.clipPath(beamPath)
        val motePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = withAlpha(Color.WHITE, (125 * panelFactor).roundToInt())
        }
        val motes = arrayOf(
            floatArrayOf(0.46f, 0.18f, 2.2f),
            floatArrayOf(0.58f, 0.29f, 3.1f),
            floatArrayOf(0.39f, 0.44f, 1.9f),
            floatArrayOf(0.52f, 0.56f, 2.6f),
            floatArrayOf(0.31f, 0.67f, 2.1f),
            floatArrayOf(0.44f, 0.78f, 3.0f),
            floatArrayOf(0.24f, 0.86f, 1.8f)
        )
        for (m in motes) {
            canvas.drawCircle(m[0] * w, m[1] * h, m[2] * (unit / 1080f).coerceAtLeast(0.8f), motePaint)
        }
        canvas.restore()
    }

    /**
     * Subtle front color-gel rim tint across the cutout subject where it intersects the studio light beam.
     */
    fun drawDuotoneGelFrontRim(
        canvas: Canvas,
        doc: PostDoc,
        accentColor: Int,
        w: Float,
        h: Float
    ) {
        val beamPath = buildDuotoneBeamPath(doc, w, h)
        val sheen = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                w * 0.55f, 0f, w * 0.25f, h,
                intArrayOf(withAlpha(Color.WHITE, 34), withAlpha(accentColor, 26), Color.TRANSPARENT),
                floatArrayOf(0f, 0.45f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawPath(beamPath, sheen)
    }

    // ============================================================================================
    // 11. TYPOGRAPHIC PLINTH (Monumental 3D Extruded Stand-On Letters/Numbers for Cutout Perch)
    // ============================================================================================

    fun drawTypographicPlinth(
        context: Context,
        canvas: Canvas,
        rect: RectF,
        doc: PostDoc,
        accentColor: Int,
        secondaryColor: Int,
        glowColor: Int,
        lightColor: Int,
        darkColor: Int,
        deepColor: Int,
        isLightBase: Boolean,
        unit: Float
    ) {
        val text = doc.texts[PostSlots.NUMBER]?.trim().orEmpty().ifBlank { "94%" }.uppercase()
        val isEthiopic = PostSlots.containsEthiopic(text)
        val shadowStrength = doc.sceneShadowStrength.coerceIn(0f, 1f)
        val depthScale = doc.sceneDepth.coerceIn(0.2f, 1f)

        val tf = PostFonts.get(context, PostFont.CONDENSED, text, doc, doc.slotStyles[PostSlots.NUMBER])
        val tp = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = tf
            isFakeBoldText = !isEthiopic
            isSubpixelText = true
        }

        // Fit giant sculptural glyphs cleanly inside rect
        var sizePx = rect.height() * 0.88f
        tp.textSize = sizePx
        val bounds = Rect()
        tp.getTextBounds(text, 0, text.length, bounds)
        val measuredW = max(1f, bounds.width().toFloat())
        val measuredH = max(1f, bounds.height().toFloat())
        val fitScale = min((rect.width() * 0.92f) / measuredW, (rect.height() * 0.78f) / measuredH)
        sizePx *= fitScale
        tp.textSize = sizePx
        tp.getTextBounds(text, 0, text.length, bounds)

        val glyphW = bounds.width().toFloat()
        val glyphH = bounds.height().toFloat()
        val drawX = rect.centerX() - glyphW * 0.5f - bounds.left
        val baselineY = rect.bottom - rect.height() * 0.14f - bounds.bottom
        val topLedgeY = baselineY + bounds.top

        // 1. Grounded architectural contact floor shadow beneath the giant 3D letters
        if (shadowStrength > 0.04f) {
            val floorRect = RectF(
                rect.centerX() - glyphW * 0.58f,
                baselineY - glyphH * 0.05f,
                rect.centerX() + glyphW * 0.62f,
                baselineY + glyphH * 0.14f
            )
            val floorPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = RadialGradient(
                    floorRect.centerX(), floorRect.centerY(), max(12f, floorRect.width() * 0.52f),
                    intArrayOf(
                        withAlpha(Color.BLACK, ((if (isLightBase) 72f else 175f) * shadowStrength).roundToInt()),
                        Color.TRANSPARENT
                    ),
                    floatArrayOf(0.18f, 1f),
                    Shader.TileMode.CLAMP
                )
            }
            canvas.drawOval(floorRect, floorPaint)
        }

        // 2. Monumental 3D block extrusion layers (angled down-right into depth)
        val extrudeDepth = max(16f, glyphH * 0.12f * (0.65f + depthScale * 0.55f))
        val steps = 22
        val dxStep = (extrudeDepth * 0.52f) / steps
        val dyStep = (extrudeDepth * 0.78f) / steps
        val extrudeDark = if (isLightBase) {
            blend(darkColor, accentColor, 0.30f)
        } else {
            blend(Color.rgb(6, 8, 16), accentColor, 0.18f)
        }
        val extrudeMid = blend(accentColor, darkColor, if (isLightBase) 0.42f else 0.52f)

        val extPaint = TextPaint(tp)
        for (i in steps downTo 1) {
            val t = i.toFloat() / steps.toFloat()
            extPaint.color = blend(extrudeMid, extrudeDark, t)
            canvas.drawText(text, drawX + dxStep * i, baselineY + dyStep * i, extPaint)
        }

        // 3. Sculpted top sunlit ledge bevel (gives visual shelf for a cutout person/object to sit on!)
        val topBevelPaint = TextPaint(tp).apply {
            color = withAlpha(blend(glowColor, Color.WHITE, 0.55f), 220)
        }
        canvas.drawText(text, drawX - max(2f, unit * 0.0028f), baselineY - max(3f, unit * 0.0042f), topBevelPaint)

        // 4. Primary architectural front face with rich directional studio light gradient
        val facePaint = TextPaint(tp).apply {
            shader = LinearGradient(
                rect.centerX(), topLedgeY,
                rect.centerX(), baselineY,
                intArrayOf(
                    blend(glowColor, Color.WHITE, if (isLightBase) 0.10f else 0.28f),
                    accentColor,
                    blend(accentColor, secondaryColor, 0.24f),
                    blend(accentColor, darkColor, 0.22f)
                ),
                floatArrayOf(0f, 0.34f, 0.74f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawText(text, drawX, baselineY, facePaint)

        // 5. Crisp hairline specular rim tracing the sculptural front glyphs
        val rimPaint = TextPaint(tp).apply {
            style = Paint.Style.STROKE
            strokeWidth = max(1.6f, unit * 0.0022f)
            color = withAlpha(if (isLightBase) darkColor else Color.WHITE, 125)
        }
        canvas.drawText(text, drawX, baselineY, rimPaint)

        // 6. Subtle studio floor horizon line under the plinth for architectural grounding
        val rulePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            strokeWidth = max(1.4f, unit * 0.0018f)
            shader = LinearGradient(
                rect.left, baselineY, rect.right, baselineY,
                intArrayOf(
                    Color.TRANSPARENT,
                    withAlpha(if (isLightBase) darkColor else lightColor, 72),
                    Color.TRANSPARENT
                ),
                floatArrayOf(0f, 0.5f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        val floorLineY = baselineY + extrudeDepth * 0.78f + max(4f, unit * 0.006f)
        canvas.drawLine(rect.left, floorLineY, rect.right, floorLineY, rulePaint)
    }

    // ============================================================================================
    // 12. DARKROOM SPOTLIGHT APERTURE (Velvety Matte + Luminous Keyhole/Arched Lens Cutout)
    // ============================================================================================

    fun drawDarkroomAperture(
        canvas: Canvas,
        rect: RectF,
        doc: PostDoc,
        accentColor: Int,
        glowColor: Int,
        lightColor: Int,
        darkColor: Int,
        deepColor: Int,
        isLightBase: Boolean,
        w: Float,
        h: Float,
        unit: Float
    ) {
        val panelFactor = (doc.panelOpacity.coerceIn(0, 100) / 100f).coerceAtLeast(0.35f)
        val cx = rect.centerX()
        val cy = rect.centerY()

        // Architectural arched/keyhole spotlight aperture path
        val apPath = Path()
        val r = min(rect.width(), rect.height()) * 0.45f
        val headCy = rect.top + r * 1.02f
        val headRect = RectF(cx - r, headCy - r, cx + r, headCy + r)
        val pillarHalfW = r * 0.66f
        val baseBottom = rect.bottom

        // Smooth cinema keyhole / arched lens silhouette
        apPath.moveTo(cx - pillarHalfW, baseBottom)
        apPath.lineTo(cx - pillarHalfW, headCy + r * 0.52f)
        apPath.arcTo(headRect, 128f, 284f, false)
        apPath.lineTo(cx + pillarHalfW, baseBottom)
        apPath.close()

        // 1. Velvety darkroom (or museum paper) outer matte with crisp EVEN_ODD aperture cutout
        val maskPath = Path().apply {
            fillType = Path.FillType.EVEN_ODD
            addRect(0f, 0f, w, h, Path.Direction.CW)
            addPath(apPath)
        }
        val matteTop = if (isLightBase) blend(Color.rgb(248, 245, 238), lightColor, 0.35f) else darkColor
        val matteBot = if (isLightBase) blend(Color.rgb(236, 232, 222), accentColor, 0.06f) else blend(Color.rgb(3, 5, 10), deepColor, 0.32f)
        val mattePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                0f, 0f, 0f, h,
                intArrayOf(
                    withAlpha(matteTop, (248 * panelFactor).roundToInt()),
                    withAlpha(matteBot, (252 * panelFactor).roundToInt())
                ),
                null,
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawPath(maskPath, mattePaint)

        // 2. Soft luminous optical halo glowing around the inside/rim of the aperture lens
        val rimGlow = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = max(8f, unit * 0.015f)
            color = withAlpha(accentColor, ((if (isLightBase) 68f else 135f) * panelFactor).roundToInt())
            maskFilter = BlurMaskFilter(max(12f, unit * 0.022f), BlurMaskFilter.Blur.NORMAL)
        }
        canvas.drawPath(apPath, rimGlow)

        // 3. Precision metallic aperture rim stroke
        val rimPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = max(2.2f, unit * 0.0028f)
            shader = LinearGradient(
                rect.left, rect.top, rect.right, rect.bottom,
                intArrayOf(
                    withAlpha(glowColor, 235),
                    withAlpha(accentColor, 215),
                    withAlpha(if (isLightBase) darkColor else Color.WHITE, 165)
                ),
                floatArrayOf(0f, 0.52f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawPath(apPath, rimPaint)

        // 4. Delicate darkroom optical calibration ring + viewfinder degree ticks around the circular head
        val ringR = r * 1.12f
        val ringPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = max(1.1f, unit * 0.0014f)
            color = withAlpha(if (isLightBase) darkColor else lightColor, (68 * panelFactor).roundToInt())
        }
        val tickArc = RectF(cx - ringR, headCy - ringR, cx + ringR, headCy + ringR)
        canvas.drawArc(tickArc, 145f, 250f, false, ringPaint)

        val tickPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            strokeWidth = max(1.3f, unit * 0.0017f)
            color = withAlpha(accentColor, (155 * panelFactor).roundToInt())
        }
        for (deg in 150..390 step 15) {
            val rad = Math.toRadians(deg.toDouble())
            val isMajor = deg % 45 == 0
            val r0 = ringR - (if (isMajor) unit * 0.006f else unit * 0.003f)
            val r1 = ringR + (if (isMajor) unit * 0.010f else unit * 0.005f)
            canvas.drawLine(
                cx + r0 * cos(rad).toFloat(),
                headCy + r0 * sin(rad).toFloat(),
                cx + r1 * cos(rad).toFloat(),
                headCy + r1 * sin(rad).toFloat(),
                tickPaint
            )
        }

        // 5. Subtle corner darkroom registration crosshairs on outer matte
        val crossPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            strokeWidth = max(1.2f, unit * 0.0015f)
            color = withAlpha(if (isLightBase) darkColor else lightColor, 62)
        }
        val arm = max(7f, unit * 0.011f)
        val crossPts = arrayOf(
            w * 0.06f to h * 0.06f,
            w * 0.94f to h * 0.06f,
            w * 0.06f to h * 0.90f,
            w * 0.94f to h * 0.90f
        )
        for ((px, py) in crossPts) {
            canvas.drawLine(px - arm, py, px + arm, py, crossPaint)
            canvas.drawLine(px, py - arm, px, py + arm, crossPaint)
        }
    }
}
