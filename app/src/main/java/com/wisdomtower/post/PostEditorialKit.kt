package com.wisdomtower.post

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
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
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * Senior-designer editorial & photo-manipulation rendering kit.
 * Batch 1 engines:
 * - Suspended Letter Mobiles (hanging sky cords + glossy glass orbs spelling WORD)
 * - Adjustable Rising Mist (multi-stop cubic bottom-up atmospheric gradient)
 * - Clean 16:9 Studio Laptop (minimal anodized frame, real 16:9 user screen, no fake wireframe UI)
 * - Out-of-Bounds Portal Frame Breaker (architectural monolith designed for pop-out cutouts)
 * - Fluted Glass Prism Slice (refractive vertical frosted-glass strips over photo/cutout)
 */
object PostEditorialKit {

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

    private fun parseHex(hex: String?): Int? {
        if (hex.isNullOrBlank()) return null
        return runCatching { Color.parseColor(hex.trim()) }.getOrNull()
    }

    private fun luminance(color: Int): Float =
        (0.299f * Color.red(color) + 0.587f * Color.green(color) + 0.114f * Color.blue(color)) / 255f

    private fun drawCrop(
        canvas: Canvas,
        bmp: Bitmap,
        dest: RectF,
        zoom: Float = 1f,
        focusX: Float = 0.5f,
        focusY: Float = 0.5f,
        paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
    ) {
        if (bmp.isRecycled || bmp.width <= 0 || bmp.height <= 0 || dest.width() <= 1f || dest.height() <= 1f) return
        val bw = bmp.width.toFloat()
        val bh = bmp.height.toFloat()
        val targetAspect = dest.width() / dest.height()
        val srcAspect = bw / bh

        var cropW = bw
        var cropH = bh
        if (srcAspect > targetAspect) {
            cropW = bh * targetAspect
        } else {
            cropH = bw / targetAspect
        }
        val z = zoom.coerceIn(1f, 4f)
        cropW /= z
        cropH /= z

        val maxLeft = max(0f, bw - cropW)
        val maxTop = max(0f, bh - cropH)
        val left = (maxLeft * focusX.coerceIn(0f, 1f)).coerceIn(0f, maxLeft)
        val top = (maxTop * focusY.coerceIn(0f, 1f)).coerceIn(0f, maxTop)

        val src = Rect(
            left.roundToInt(),
            top.roundToInt(),
            (left + cropW).roundToInt().coerceAtMost(bmp.width),
            (top + cropH).roundToInt().coerceAtMost(bmp.height)
        )
        canvas.drawBitmap(bmp, src, dest, paint)
    }

    // ========================================================================
    // 1. SUSPENDED LETTER MOBILES (Hanging sky cords + glossy glass letter orbs)
    // ========================================================================

    fun drawSuspendedMobiles(
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
        w: Float,
        h: Float,
        unit: Float
    ) {
        val rawWord = doc.texts[PostSlots.WORD]?.trim().orEmpty()
            .ifBlank { doc.texts[PostSlots.LETTER]?.trim().orEmpty() }
            .ifBlank { "FOCUS" }
        val letters = PostSlots.graphemes(rawWord.replace(" ", ""), 9)
            .ifEmpty { listOf("W", "I", "S", "D", "O", "M") }
            .map { if (PostSlots.containsEthiopic(it)) it else it.uppercase() }

        val n = letters.size
        val shadowStrength = doc.sceneShadowStrength.coerceIn(0.15f, 1f)

        // Subtle atmospheric sky light pool near top center
        val skyGlow = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(
                rect.centerX(), 0f, max(w, h) * 0.62f,
                intArrayOf(
                    withAlpha(accentColor, if (isLightBase) 42 else 82),
                    withAlpha(secondaryColor, if (isLightBase) 18 else 28),
                    Color.TRANSPARENT
                ),
                floatArrayOf(0f, 0.48f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f, 0f, w, h * 0.72f, skyGlow)

        // Subtle studio floor ambient pool ready for user's movable BG-removed cutout
        val floorPoolY = h * 0.80f
        val floorRx = w * 0.34f
        val floorRy = h * 0.065f
        canvas.save()
        canvas.translate(w * 0.5f, floorPoolY)
        canvas.scale(1f, (floorRy / max(1f, floorRx)).coerceAtLeast(0.12f))
        val floorGlow = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(
                0f, 0f, floorRx,
                intArrayOf(
                    withAlpha(accentColor, if (isLightBase) 36 else 65),
                    Color.TRANSPARENT
                ),
                null,
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawCircle(0f, 0f, floorRx, floorGlow)
        canvas.restore()

        // Compute orb radius and horizontal layout
        val padX = rect.width() * 0.05f
        val usableW = rect.width() - padX * 2f
        val stepX = if (n == 1) 0f else usableW / (n - 1).toFloat()
        val maxRByWidth = if (n == 1) rect.height() * 0.24f else stepX * 0.44f
        val orbR = min(maxRByWidth, min(rect.height() * 0.22f, unit * 0.115f)).coerceAtLeast(unit * 0.042f)

        // Organic staggered catenary + editorial wave heights so orbs feel sculpted in mid-air
        val waveOffsets = floatArrayOf(0.24f, 0.56f, 0.34f, 0.72f, 0.42f, 0.62f, 0.28f, 0.58f, 0.38f)

        for (i in 0 until n) {
            val ch = letters[i]
            val cx = if (n == 1) rect.centerX() else rect.left + padX + stepX * i
            val t = if (n <= 1) 0.5f else i.toFloat() / (n - 1).toFloat()
            val catenary = sin(t * PI).toFloat() * 0.16f
            val stagger = waveOffsets[i % waveOffsets.size]
            val cy = rect.top + orbR + (rect.height() - orbR * 2.2f) * (stagger * 0.72f + catenary).coerceIn(0.08f, 0.90f)

            val cordTopY = -h * 0.02f
            val claspY = cy - orbR - max(3f, orbR * 0.08f)

            // 1. Fine shimmering tension cord from sky
            val cordWidth = max(1.6f, unit * 0.0024f)
            val cordPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = cordWidth
                shader = LinearGradient(
                    cx, cordTopY, cx, claspY,
                    intArrayOf(
                        withAlpha(if (isLightBase) darkColor else lightColor, 35),
                        withAlpha(if (isLightBase) darkColor else lightColor, 175),
                        withAlpha(accentColor, 225)
                    ),
                    floatArrayOf(0f, 0.68f, 1f),
                    Shader.TileMode.CLAMP
                )
            }
            canvas.drawLine(cx, cordTopY, cx, claspY, cordPaint)

            // Subtle cord shadow
            val cordShadow = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = cordWidth
                color = withAlpha(Color.BLACK, (40 * shadowStrength).roundToInt())
            }
            canvas.drawLine(cx + cordWidth * 1.4f, cordTopY, cx + cordWidth * 1.4f, claspY, cordShadow)

            // Tiny precision metallic clasp bead where cord meets orb
            val claspR = max(3.5f, orbR * 0.095f)
            val claspPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = RadialGradient(
                    cx - claspR * 0.3f, claspY - claspR * 0.3f, claspR * 1.6f,
                    intArrayOf(Color.WHITE, accentColor, darkColor),
                    floatArrayOf(0f, 0.55f, 1f),
                    Shader.TileMode.CLAMP
                )
            }
            canvas.drawCircle(cx, claspY, claspR, claspPaint)

            // 2. Soft colored aura halo + cast depth shadow behind the glass bubble
            val auraR = orbR * 1.65f
            val auraPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = RadialGradient(
                    cx, cy, auraR,
                    intArrayOf(
                        withAlpha(accentColor, if (isLightBase) 55 else 95),
                        withAlpha(accentColor, 0)
                    ),
                    floatArrayOf(0.25f, 1f),
                    Shader.TileMode.CLAMP
                )
            }
            canvas.drawCircle(cx, cy, auraR, auraPaint)

            val dropShadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = withAlpha(Color.BLACK, (if (isLightBase) 42f else 110f * shadowStrength).roundToInt())
                maskFilter = BlurMaskFilter(max(8f, orbR * 0.32f), BlurMaskFilter.Blur.NORMAL)
            }
            canvas.drawCircle(cx + orbR * 0.10f, cy + orbR * 0.16f, orbR * 0.94f, dropShadowPaint)

            // 3. Glossy 3D translucent glass/pearl bubble body
            val orbCore = if (isLightBase) {
                blend(Color.WHITE, accentColor, 0.14f)
            } else {
                blend(deepColor, accentColor, 0.30f)
            }
            val orbEdge = if (isLightBase) {
                blend(accentColor, darkColor, 0.22f)
            } else {
                blend(accentColor, Color.WHITE, 0.36f)
            }
            val bodyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = RadialGradient(
                    cx - orbR * 0.30f, cy - orbR * 0.32f, orbR * 1.28f,
                    intArrayOf(
                        withAlpha(Color.WHITE, if (isLightBase) 235 else 155),
                        withAlpha(orbCore, if (isLightBase) 210 else 215),
                        withAlpha(blend(orbCore, darkColor, 0.42f), if (isLightBase) 220 else 235),
                        withAlpha(orbEdge, if (isLightBase) 210 else 205)
                    ),
                    floatArrayOf(0f, 0.34f, 0.78f, 1f),
                    Shader.TileMode.CLAMP
                )
            }
            canvas.drawCircle(cx, cy, orbR, bodyPaint)

            // Bottom internal caustic glow inside the glass sphere
            val causticPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = RadialGradient(
                    cx + orbR * 0.18f, cy + orbR * 0.56f, orbR * 0.65f,
                    intArrayOf(withAlpha(glowColor, if (isLightBase) 110 else 165), Color.TRANSPARENT),
                    null,
                    Shader.TileMode.CLAMP
                )
            }
            canvas.save()
            val clipOrb = Path().apply { addCircle(cx, cy, orbR, Path.Direction.CW) }
            canvas.clipPath(clipOrb)
            canvas.drawCircle(cx + orbR * 0.18f, cy + orbR * 0.56f, orbR * 0.65f, causticPaint)

            // Upper-left glossy specular oval highlight
            canvas.save()
            canvas.rotate(-34f, cx - orbR * 0.34f, cy - orbR * 0.38f)
            val specRect = RectF(
                cx - orbR * 0.56f,
                cy - orbR * 0.54f,
                cx - orbR * 0.10f,
                cy - orbR * 0.24f
            )
            val specPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = LinearGradient(
                    specRect.left, specRect.top, specRect.right, specRect.bottom,
                    intArrayOf(withAlpha(Color.WHITE, 215), withAlpha(Color.WHITE, 12)),
                    null,
                    Shader.TileMode.CLAMP
                )
            }
            canvas.drawOval(specRect, specPaint)
            canvas.restore()
            canvas.restore()

            // Crisp translucent glass rim stroke
            val rimPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = max(1.8f, orbR * 0.036f)
                shader = LinearGradient(
                    cx - orbR, cy - orbR, cx + orbR, cy + orbR,
                    intArrayOf(
                        withAlpha(Color.WHITE, if (isLightBase) 225 else 210),
                        withAlpha(accentColor, 150),
                        withAlpha(glowColor, 200)
                    ),
                    floatArrayOf(0f, 0.55f, 1f),
                    Shader.TileMode.CLAMP
                )
            }
            canvas.drawCircle(cx, cy, orbR - rimPaint.strokeWidth * 0.5f, rimPaint)

            // 4. Bold centered letter/grapheme inside the glass bubble
            val tf = PostFonts.get(context, PostFont.DISPLAY, ch, doc, doc.slotStyles[PostSlots.WORD])
            val letterColor = if (isLightBase) darkColor else lightColor
            val tp = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                typeface = tf
                textSize = orbR * (if (PostSlots.containsEthiopic(ch)) 0.92f else 1.04f)
                color = letterColor
                isSubpixelText = true
            }
            val bounds = Rect()
            tp.getTextBounds(ch, 0, ch.length, bounds)
            val drawX = cx - bounds.exactCenterX()
            val drawY = cy - bounds.exactCenterY()

            // Crisp depth shadow behind glyph inside glass
            val glyphShadow = TextPaint(tp).apply {
                color = withAlpha(if (isLightBase) accentColor else Color.BLACK, if (isLightBase) 80 else 135)
                maskFilter = BlurMaskFilter(max(3f, orbR * 0.08f), BlurMaskFilter.Blur.NORMAL)
            }
            canvas.drawText(ch, drawX, drawY + orbR * 0.05f, glyphShadow)
            canvas.drawText(ch, drawX, drawY, tp)
        }
    }

    // ========================================================================
    // 2. ADJUSTABLE RISING MIST (Height + Color + Opacity atmospheric bottom-up fog)
    // ========================================================================

    fun drawRisingMist(
        canvas: Canvas,
        doc: PostDoc,
        accentColor: Int,
        darkColor: Int,
        deepColor: Int,
        w: Float,
        h: Float
    ) {
        val heightFrac = doc.mistHeight.coerceIn(0.15f, 0.95f)
        val opacity = doc.mistOpacity.coerceIn(0.05f, 1f)
        val baseMist = parseHex(doc.mistColorHex) ?: blend(deepColor, darkColor, 0.45f)
        val crestGlow = blend(baseMist, accentColor, 0.35f)

        val mistTopY = h * (1f - heightFrac)
        val mistRect = RectF(0f, mistTopY, w, h)

        // 7-stop smooth eased cubic ramp — zero harsh horizontal seam
        val stops = floatArrayOf(0f, 0.14f, 0.30f, 0.48f, 0.66f, 0.84f, 1f)
        val alphas = floatArrayOf(0f, 0.06f, 0.22f, 0.48f, 0.76f, 0.94f, 1.0f)
        val colors = IntArray(stops.size) { idx ->
            val a = (alphas[idx] * opacity * 255f).roundToInt().coerceIn(0, 255)
            val tint = if (idx in 1..3) blend(baseMist, crestGlow, 0.28f) else baseMist
            withAlpha(tint, a)
        }

        val mistPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                0f, mistTopY, 0f, h,
                colors,
                stops,
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(mistRect, mistPaint)

        // Subtle atmospheric horizon bloom along the rising mist crest
        val bloomPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(
                w * 0.5f, h * (1f - heightFrac * 0.55f), max(w, h) * 0.62f,
                intArrayOf(
                    withAlpha(crestGlow, (opacity * 62f).roundToInt().coerceIn(0, 120)),
                    Color.TRANSPARENT
                ),
                null,
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f, max(0f, mistTopY - h * 0.12f), w, h, bloomPaint)
    }

    // ========================================================================
    // 3. CLEAN 16:9 STUDIO LAPTOP (Precision minimal aluminum frame, zero fake UI)
    // ========================================================================

    fun drawLaptop16x9(
        context: Context,
        canvas: Canvas,
        rect: RectF,
        doc: PostDoc,
        primaryPhoto: Bitmap?,
        accentColor: Int,
        lightColor: Int,
        darkColor: Int,
        deepColor: Int,
        isLightBase: Boolean,
        unit: Float
    ) {
        val shadowStrength = doc.sceneShadowStrength.coerceIn(0.15f, 1f)

        // Fit true 16:9 laptop proportions cleanly inside rect
        // Lid width = 84% of base width; screen is exact 16:9 inside slim bezel
        val baseRatioH = 0.044f
        val lidBezelPadX = 0.022f
        val lidBezelPadTop = 0.032f
        val lidBezelPadBot = 0.042f

        val maxBaseW = rect.width()
        val baseWFromH = rect.height() / ((0.84f * (9f / 16f) * (1f + lidBezelPadTop + lidBezelPadBot)) + baseRatioH)
        val baseW = min(maxBaseW, baseWFromH)
        val lidW = baseW * 0.84f
        val screenW = lidW * (1f - lidBezelPadX * 2f)
        val screenH = screenW * (9f / 16f)
        val lidH = screenH + lidW * (lidBezelPadTop + lidBezelPadBot)
        val baseH = max(unit * 0.016f, baseW * baseRatioH)
        val totalH = lidH + baseH

        val cx = rect.centerX()
        val topY = rect.centerY() - totalH * 0.5f
        val lidRect = RectF(cx - lidW * 0.5f, topY, cx + lidW * 0.5f, topY + lidH)
        val baseRect = RectF(cx - baseW * 0.5f, lidRect.bottom - baseH * 0.08f, cx + baseW * 0.5f, lidRect.bottom + baseH * 0.92f)

        // 1. Studio floor ambient & tight contact shadow
        PostDepth.contactShadow(
            canvas = canvas,
            cx = cx,
            cy = baseRect.bottom + baseH * 0.12f,
            rx = baseW * 0.54f,
            ry = max(unit * 0.024f, baseH * 0.95f),
            strength = shadowStrength * 0.85f
        )
        PostDepth.contactShadow(
            canvas = canvas,
            cx = cx,
            cy = baseRect.bottom + baseH * 0.02f,
            rx = baseW * 0.48f,
            ry = max(unit * 0.009f, baseH * 0.34f),
            strength = shadowStrength
        )

        // Soft accent backlight halo behind the screen lid (dark mode luxury pop)
        if (!isLightBase) {
            val backGlow = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = RadialGradient(
                    lidRect.centerX(), lidRect.centerY(), lidW * 0.72f,
                    intArrayOf(withAlpha(accentColor, 68), Color.TRANSPARENT),
                    null,
                    Shader.TileMode.CLAMP
                )
            }
            canvas.drawOval(
                RectF(lidRect.left - lidW * 0.16f, lidRect.top - lidH * 0.16f, lidRect.right + lidW * 0.16f, lidRect.bottom + lidH * 0.12f),
                backGlow
            )
        }

        // 2. Machined aluminum lid shell
        val lidRadius = max(10f, lidW * 0.026f)
        val lidPath = Path().apply {
            addRoundRect(
                lidRect,
                floatArrayOf(lidRadius, lidRadius, lidRadius, lidRadius, lidRadius * 0.25f, lidRadius * 0.25f, lidRadius * 0.25f, lidRadius * 0.25f),
                Path.Direction.CW
            )
        }
        val metalLight = if (isLightBase) Color.rgb(218, 224, 234) else Color.rgb(74, 82, 98)
        val metalMid = if (isLightBase) Color.rgb(176, 184, 198) else Color.rgb(42, 47, 58)
        val metalDark = if (isLightBase) Color.rgb(132, 140, 154) else Color.rgb(20, 23, 30)

        val lidShellPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                lidRect.left, lidRect.top, lidRect.right, lidRect.bottom,
                intArrayOf(metalLight, metalMid, metalDark),
                floatArrayOf(0f, 0.45f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawPath(lidPath, lidShellPaint)

        // Jet-black glass bezel inset
        val bezelInset = max(2.5f, lidW * 0.0055f)
        val bezelRect = RectF(
            lidRect.left + bezelInset,
            lidRect.top + bezelInset,
            lidRect.right - bezelInset,
            lidRect.bottom - bezelInset * 0.6f
        )
        val bezelRadius = max(7f, lidRadius - bezelInset)
        val bezelPath = Path().apply {
            addRoundRect(
                bezelRect,
                floatArrayOf(bezelRadius, bezelRadius, bezelRadius, bezelRadius, 3f, 3f, 3f, 3f),
                Path.Direction.CW
            )
        }
        canvas.drawPath(bezelPath, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(10, 12, 16) })

        // Precision 16:9 screen rect
        val screenLeft = cx - screenW * 0.5f
        val screenTop = lidRect.top + lidW * lidBezelPadTop
        val screenRect = RectF(screenLeft, screenTop, screenLeft + screenW, screenTop + screenH)
        val screenRadius = max(3f, lidW * 0.006f)
        val screenPath = Path().apply {
            addRoundRect(screenRect, screenRadius, screenRadius, Path.Direction.CW)
        }

        // 3. Draw real 16:9 user photo/screenshot inside the display
        canvas.save()
        canvas.clipPath(screenPath)
        if (primaryPhoto != null && !primaryPhoto.isRecycled) {
            val adj = doc.slotAdjust(0)
            drawCrop(canvas, primaryPhoto, screenRect, adj.zoom, adj.focusX, adj.focusY)
        } else {
            // Ultra-clean minimal obsidian studio screen placeholder (ZERO fake wireframe UI bars!)
            val emptyBg = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = LinearGradient(
                    screenRect.left, screenRect.top, screenRect.right, screenRect.bottom,
                    intArrayOf(
                        blend(darkColor, accentColor, 0.22f),
                        darkColor,
                        blend(deepColor, accentColor, 0.16f)
                    ),
                    floatArrayOf(0f, 0.55f, 1f),
                    Shader.TileMode.CLAMP
                )
            }
            canvas.drawRect(screenRect, emptyBg)

            val screenAura = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = RadialGradient(
                    screenRect.centerX(), screenRect.centerY(), screenW * 0.55f,
                    intArrayOf(withAlpha(accentColor, 85), Color.TRANSPARENT),
                    null,
                    Shader.TileMode.CLAMP
                )
            }
            canvas.drawRect(screenRect, screenAura)

            // Crisp minimal crosshair & 16:9 indicator pill
            val label = "16:9 SCREEN · ADD PHOTO"
            val tp = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                typeface = PostFonts.get(context, PostFont.BOLD, label, doc)
                textSize = (screenH * 0.056f).coerceIn(12f, 26f)
                color = withAlpha(lightColor, 200)
                letterSpacing = 0.12f
            }
            val tw = tp.measureText(label)
            val pillW = tw + tp.textSize * 2.0f
            val pillH = tp.textSize * 2.1f
            val pillRect = RectF(
                screenRect.centerX() - pillW * 0.5f,
                screenRect.centerY() - pillH * 0.5f,
                screenRect.centerX() + pillW * 0.5f,
                screenRect.centerY() + pillH * 0.5f
            )
            canvas.drawRoundRect(
                pillRect, pillH * 0.5f, pillH * 0.5f,
                Paint(Paint.ANTI_ALIAS_FLAG).apply { color = withAlpha(Color.BLACK, 110) }
            )
            canvas.drawRoundRect(
                pillRect, pillH * 0.5f, pillH * 0.5f,
                Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    style = Paint.Style.STROKE
                    strokeWidth = 1.5f
                    color = withAlpha(accentColor, 165)
                }
            )
            val fm = tp.fontMetrics
            canvas.drawText(
                label,
                pillRect.centerX() - tw * 0.5f,
                pillRect.centerY() - (fm.ascent + fm.descent) * 0.5f,
                tp
            )
        }

        // Subtle anti-glare diagonal glass reflection sheen across screen
        val sheen = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                screenRect.left, screenRect.top,
                screenRect.left + screenW * 0.65f, screenRect.bottom,
                intArrayOf(
                    withAlpha(Color.WHITE, 26),
                    withAlpha(Color.WHITE, 8),
                    Color.TRANSPARENT
                ),
                floatArrayOf(0f, 0.36f, 0.62f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(screenRect, sheen)
        canvas.restore()

        // Tiny precision webcam lens dot in top bezel
        val camY = (bezelRect.top + screenRect.top) * 0.5f
        val camR = max(2.2f, (screenRect.top - bezelRect.top) * 0.22f)
        canvas.drawCircle(cx, camY, camR, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(34, 40, 52) })
        canvas.drawCircle(cx, camY, camR * 0.45f, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(68, 92, 130) })

        // Top metallic lid rim highlight
        val rimHighlight = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = max(1.4f, unit * 0.002f)
            shader = LinearGradient(
                lidRect.left, lidRect.top, lidRect.right, lidRect.bottom,
                intArrayOf(withAlpha(Color.WHITE, 145), withAlpha(Color.WHITE, 25), Color.TRANSPARENT),
                floatArrayOf(0f, 0.35f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawPath(lidPath, rimHighlight)

        // 4. Machined aluminum base deck with tapered bottom edge & thumb-opening recess
        val topDeckH = baseH * 0.54f
        val topDeckRect = RectF(baseRect.left, baseRect.top, baseRect.right, baseRect.top + topDeckH)
        val basePath = Path().apply {
            val bRad = max(4f, baseH * 0.48f)
            moveTo(baseRect.left, baseRect.top + 2f)
            quadTo(baseRect.left, baseRect.top, baseRect.left + 4f, baseRect.top)
            lineTo(baseRect.right - 4f, baseRect.top)
            quadTo(baseRect.right, baseRect.top, baseRect.right, baseRect.top + 2f)
            lineTo(baseRect.right, baseRect.top + topDeckH * 0.65f)
            cubicTo(
                baseRect.right - baseW * 0.02f, baseRect.bottom,
                baseRect.right - baseW * 0.06f, baseRect.bottom,
                baseRect.right - baseW * 0.09f, baseRect.bottom
            )
            lineTo(baseRect.left + baseW * 0.09f, baseRect.bottom)
            cubicTo(
                baseRect.left + baseW * 0.06f, baseRect.bottom,
                baseRect.left + baseW * 0.02f, baseRect.bottom,
                baseRect.left, baseRect.top + topDeckH * 0.65f
            )
            close()
        }

        val deckPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                baseRect.left, baseRect.top, baseRect.right, baseRect.top,
                intArrayOf(
                    metalDark,
                    metalLight,
                    metalMid,
                    metalLight,
                    metalDark
                ),
                floatArrayOf(0f, 0.09f, 0.5f, 0.91f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawPath(basePath, deckPaint)

        // Lower bevel shading on tapered chin
        val chinShade = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                0f, baseRect.top + topDeckH * 0.5f, 0f, baseRect.bottom,
                intArrayOf(Color.TRANSPARENT, withAlpha(Color.BLACK, if (isLightBase) 68 else 135)),
                null,
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawPath(basePath, chinShade)

        // Precision center thumb-opening notch
        val notchW = baseW * 0.145f
        val notchH = max(3f, topDeckH * 0.58f)
        val notchRect = RectF(cx - notchW * 0.5f, baseRect.top, cx + notchW * 0.5f, baseRect.top + notchH)
        val notchPath = Path().apply {
            addRoundRect(
                notchRect,
                floatArrayOf(0f, 0f, 0f, 0f, notchH * 0.75f, notchH * 0.75f, notchH * 0.75f, notchH * 0.75f),
                Path.Direction.CW
            )
        }
        canvas.drawPath(notchPath, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = withAlpha(Color.BLACK, if (isLightBase) 72 else 145) })

        // Crisp top specular edge along aluminum deck
        canvas.drawLine(
            baseRect.left + 4f, baseRect.top + 1f,
            baseRect.right - 4f, baseRect.top + 1f,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                strokeWidth = max(1.2f, unit * 0.0016f)
                color = withAlpha(Color.WHITE, if (isLightBase) 185 else 110)
            }
        )
    }

    // ========================================================================
    // 4. OUT-OF-BOUNDS PORTAL FRAME BREAKER (Geometric monolith for pop-out cutout)
    // ========================================================================

    fun drawPortalFrameBreaker(
        canvas: Canvas,
        rect: RectF,
        doc: PostDoc,
        accentColor: Int,
        lightColor: Int,
        darkColor: Int,
        isLightBase: Boolean,
        unit: Float
    ) {
        val radius = max(14f, unit * 0.030f)
        val shadowStrength = doc.sceneShadowStrength.coerceIn(0.15f, 1f)

        // 1. Offset architectural outer wireframe ghost frame (adds editorial depth)
        val offset = max(10f, unit * 0.020f)
        val ghostRect = RectF(rect.left + offset, rect.top + offset, rect.right + offset, rect.bottom + offset)
        val ghostPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = max(1.6f, unit * 0.0022f)
            color = withAlpha(accentColor, if (isLightBase) 115 else 135)
        }
        canvas.drawRoundRect(ghostRect, radius, radius, ghostPaint)

        // 2. Soft ambient glow & cast depth shadow behind monolith frame
        if (!isLightBase) {
            val halo = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = RadialGradient(
                    rect.centerX(), rect.centerY(), max(rect.width(), rect.height()) * 0.72f,
                    intArrayOf(withAlpha(accentColor, 78), Color.TRANSPARENT),
                    null,
                    Shader.TileMode.CLAMP
                )
            }
            canvas.drawRect(
                rect.left - rect.width() * 0.2f,
                rect.top - rect.height() * 0.2f,
                rect.right + rect.width() * 0.2f,
                rect.bottom + rect.height() * 0.2f,
                halo
            )
        }

        val framePath = Path().apply { addRoundRect(rect, radius, radius, Path.Direction.CW) }
        PostDepth.castShadow(
            canvas = canvas,
            path = framePath,
            lightAngleDeg = doc.sceneLightAngle,
            elevationPx = max(14f, unit * 0.028f),
            strength = shadowStrength
        )

        // 3. Crisp rim border around the portal window
        val rimPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = max(3f, unit * 0.0046f)
            shader = LinearGradient(
                rect.left, rect.top, rect.right, rect.bottom,
                intArrayOf(
                    withAlpha(accentColor, 240),
                    withAlpha(if (isLightBase) darkColor else lightColor, 170),
                    withAlpha(accentColor, 225)
                ),
                floatArrayOf(0f, 0.5f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRoundRect(rect, radius, radius, rimPaint)

        // 4. Precision editorial registration crosshairs (+) at outer top-left & bottom-right
        val tick = max(8f, unit * 0.014f)
        val tickPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = max(1.6f, unit * 0.0022f)
            color = withAlpha(if (isLightBase) darkColor else lightColor, 185)
        }
        val tlx = rect.left - offset * 0.75f
        val tly = rect.top - offset * 0.75f
        canvas.drawLine(tlx - tick * 0.5f, tly, tlx + tick * 0.5f, tly, tickPaint)
        canvas.drawLine(tlx, tly - tick * 0.5f, tlx, tly + tick * 0.5f, tickPaint)

        val brx = ghostRect.right + offset * 0.55f
        val bry = ghostRect.bottom + offset * 0.55f
        canvas.drawLine(brx - tick * 0.5f, bry, brx + tick * 0.5f, bry, tickPaint)
        canvas.drawLine(brx, bry - tick * 0.5f, brx, bry + tick * 0.5f, tickPaint)
    }

    // ========================================================================
    // 5. FLUTED GLASS PRISM SLICE (Refractive vertical frosted-glass panels)
    // ========================================================================

    fun drawPrismGlassStrips(
        canvas: Canvas,
        doc: PostDoc,
        sourcePhoto: Bitmap?,
        accentColor: Int,
        lightColor: Int,
        darkColor: Int,
        w: Float,
        h: Float,
        unit: Float
    ) {
        val panelFactor = (doc.panelOpacity.coerceIn(0, 100) / 100f).coerceIn(0.15f, 1f)
        val blurredPhoto = if (sourcePhoto != null && !sourcePhoto.isRecycled) {
            val key = doc.photoUri ?: doc.backgroundPhotoUri ?: "prism"
            PostImageCache.blurred(key, sourcePhoto, 48)
        } else null

        // Strategic asymmetric vertical glass strips leaving a wide crystal-clear focal window!
        // Each triple: (leftFrac, widthFrac, refractionShiftFrac)
        val strips = if (w > h * 1.3f) {
            listOf(
                Triple(0.06f, 0.11f, -0.045f),
                Triple(0.22f, 0.075f, 0.035f),
                Triple(0.73f, 0.085f, -0.035f),
                Triple(0.86f, 0.09f, 0.045f)
            )
        } else {
            listOf(
                Triple(0.05f, 0.13f, -0.045f),
                Triple(0.22f, 0.075f, 0.035f),
                Triple(0.81f, 0.135f, 0.045f)
            )
        }

        val fullDest = RectF(0f, 0f, w, h)
        val adj = doc.slotAdjust(0)

        for ((xFrac, wFrac, refractShift) in strips) {
            val sr = RectF(xFrac * w, 0f, (xFrac + wFrac) * w, h)

            // 1. Subtle cast shadow to the right of each glass slab
            val slabShadow = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = LinearGradient(
                    sr.right, 0f, sr.right + max(12f, unit * 0.022f), 0f,
                    intArrayOf(withAlpha(Color.BLACK, (95 * panelFactor).roundToInt()), Color.TRANSPARENT),
                    null,
                    Shader.TileMode.CLAMP
                )
            }
            canvas.drawRect(sr.right, 0f, sr.right + max(12f, unit * 0.022f), h, slabShadow)

            canvas.save()
            canvas.clipRect(sr)

            // 2. Blurred + optically refracted (shifted & slightly magnified) photo slice
            if (blurredPhoto != null && !blurredPhoto.isRecycled) {
                val refractPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG).apply {
                    alpha = (235 * panelFactor).roundToInt().coerceIn(0, 255)
                }
                drawCrop(
                    canvas = canvas,
                    bmp = blurredPhoto,
                    dest = fullDest,
                    zoom = (adj.zoom * 1.12f).coerceIn(1f, 4f),
                    focusX = (adj.focusX + refractShift).coerceIn(0f, 1f),
                    focusY = adj.focusY.coerceIn(0f, 1f),
                    paint = refractPaint
                )
            }

            // 3. Fluted / reeded architectural glass vertical ribs (subtle alternating optical columns)
            val ribCount = 5
            val ribW = sr.width() / ribCount.toFloat()
            for (r in 0 until ribCount) {
                val rx0 = sr.left + r * ribW
                val rx1 = rx0 + ribW
                val ribPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    shader = LinearGradient(
                        rx0, 0f, rx1, 0f,
                        intArrayOf(
                            withAlpha(Color.WHITE, (54 * panelFactor).roundToInt()),
                            withAlpha(Color.WHITE, (8 * panelFactor).roundToInt()),
                            withAlpha(darkColor, (44 * panelFactor).roundToInt()),
                            withAlpha(accentColor, (22 * panelFactor).roundToInt())
                        ),
                        floatArrayOf(0f, 0.28f, 0.74f, 1f),
                        Shader.TileMode.CLAMP
                    )
                }
                canvas.drawRect(rx0, 0f, rx1, h, ribPaint)
            }

            // 4. Frosted vertical diagonal glint wash
            val frostWash = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = LinearGradient(
                    sr.left, 0f, sr.right, h * 0.7f,
                    intArrayOf(
                        withAlpha(lightColor, (46 * panelFactor).roundToInt()),
                        withAlpha(accentColor, (18 * panelFactor).roundToInt()),
                        withAlpha(darkColor, (42 * panelFactor).roundToInt())
                    ),
                    floatArrayOf(0f, 0.45f, 1f),
                    Shader.TileMode.CLAMP
                )
            }
            canvas.drawRect(sr, frostWash)
            canvas.restore()

            // 5. Crisp glass slab specular edge highlights (left bright, right cool accent)
            val leftEdge = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                strokeWidth = max(1.8f, unit * 0.0024f)
                color = withAlpha(Color.WHITE, (165 * panelFactor).roundToInt())
            }
            val rightEdge = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                strokeWidth = max(1.4f, unit * 0.0018f)
                color = withAlpha(blend(Color.WHITE, accentColor, 0.4f), (125 * panelFactor).roundToInt())
            }
            canvas.drawLine(sr.left + 1f, 0f, sr.left + 1f, h, leftEdge)
            canvas.drawLine(sr.right - 1f, 0f, sr.right - 1f, h, rightEdge)
        }
    }
}
