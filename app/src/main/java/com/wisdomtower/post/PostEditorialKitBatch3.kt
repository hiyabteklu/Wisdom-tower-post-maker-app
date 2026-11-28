package com.wisdomtower.post

import android.graphics.Bitmap
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RadialGradient
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * Senior-designer editorial & photo-manipulation rendering kit — Batch 3 (Templates 13–18):
 * 13. Torn-Edge Zine Collage (procedural ripped paper lip pocket where cutout rises out of the tear)
 * 14. Architectural Blueprint Stage (millimeter cyanotype grid, precision reticle, angled technical callout lines)
 * 15. Perspective Horizon Runway (one-point perspective receding grid & glowing runway floor)
 * 16. Kinetic Sliced Louvers (7 staggered vertical photo strips with optical parallax behind sharp foreground)
 * 17. 35mm Contact-Sheet Pop-Out (angled perforated cinema filmstrip holding up to 3 photos behind pop-out cutout)
 * 18. Luminous Eclipse Halo Stage (radiant backlit solar eclipse corona + astronomical rings behind cutout)
 */
object PostEditorialKitBatch3 {

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

    // ============================================================================================
    // 13. TORN-EDGE ZINE COLLAGE (Procedural Ripped Paper Pocket: Back Risograph + Front Rip Lip)
    // ============================================================================================

    private fun buildHorizontalTearPath(
        w: Float,
        h: Float,
        tearY: Float,
        amp: Float,
        seedPhase: Float,
        closeBottom: Boolean
    ): Path {
        val steps = 84
        val path = Path()
        for (i in 0..steps) {
            val t = i.toFloat() / steps.toFloat()
            val x = -w * 0.02f + (w * 1.04f) * t
            // Multi-octave deterministic organic paper rip profile
            val wave =
                sin(t * 21.0 + seedPhase).toFloat() * 0.42f +
                    sin(t * 57.0 + seedPhase * 2.3).toFloat() * 0.30f +
                    cos(t * 119.0 - seedPhase * 1.7).toFloat() * 0.18f +
                    sin(t * 211.0 + seedPhase * 0.9).toFloat() * 0.10f
            val slope = (t - 0.5f) * amp * 1.15f // gentle editorial diagonal rip drift
            val y = tearY + slope + wave * amp
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        if (closeBottom) {
            path.lineTo(w * 1.03f, h * 1.04f)
            path.lineTo(-w * 0.03f, h * 1.04f)
            path.close()
        }
        return path
    }

    private fun buildVerticalTearPath(
        w: Float,
        h: Float,
        tearX: Float,
        amp: Float,
        seedPhase: Float,
        closeRight: Boolean
    ): Path {
        val steps = 84
        val path = Path()
        for (i in 0..steps) {
            val t = i.toFloat() / steps.toFloat()
            val y = -h * 0.02f + (h * 1.04f) * t
            val wave =
                sin(t * 23.0 + seedPhase).toFloat() * 0.42f +
                    sin(t * 61.0 + seedPhase * 2.1).toFloat() * 0.30f +
                    cos(t * 127.0 - seedPhase * 1.5).toFloat() * 0.18f +
                    sin(t * 223.0 + seedPhase * 0.8).toFloat() * 0.10f
            val slope = (t - 0.5f) * amp * 0.95f
            val x = tearX + slope + wave * amp
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        if (closeRight) {
            path.lineTo(w * 1.04f, h * 1.03f)
            path.lineTo(w * 1.04f, -h * 0.03f)
            path.close()
        }
        return path
    }

    fun drawTornZineCollage(
        canvas: Canvas,
        rect: RectF,
        doc: PostDoc,
        accentColor: Int,
        lightColor: Int,
        darkColor: Int,
        deepColor: Int,
        paperColor: Int,
        isLightBase: Boolean,
        isFrontPass: Boolean,
        isVerticalSplit: Boolean,
        w: Float,
        h: Float,
        unit: Float
    ) {
        val panelFactor = (doc.panelOpacity.coerceIn(0, 100) / 100f).coerceAtLeast(0.25f)
        val shadowStrength = doc.sceneShadowStrength.coerceIn(0f, 1f)
        val tearAmp = max(10f, unit * 0.017f)

        if (!isFrontPass) {
            // Back pass: rich risograph halftone atmosphere in the torn-away window behind the cutout
            val backArea = if (isVerticalSplit) {
                RectF(0f, 0f, rect.left + tearAmp * 3f, h)
            } else {
                RectF(0f, 0f, w, rect.top + tearAmp * 3f)
            }
            val gradBack = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = LinearGradient(
                    backArea.left, backArea.top, backArea.right, backArea.bottom,
                    intArrayOf(
                        withAlpha(blend(accentColor, darkColor, if (isLightBase) 0.18f else 0.35f), (215 * panelFactor).roundToInt()),
                        withAlpha(if (isLightBase) blend(accentColor, darkColor, 0.45f) else deepColor, (235 * panelFactor).roundToInt())
                    ),
                    null,
                    Shader.TileMode.CLAMP
                )
            }
            canvas.drawRect(backArea, gradBack)

            // Tactile risograph halftone dot field in the upper/left pocket
            val dotSpacing = max(12f, unit * 0.018f)
            val dotR = max(1.6f, unit * 0.0024f)
            val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = withAlpha(if (isLightBase) Color.WHITE else lightColor, (44 * panelFactor).roundToInt())
            }
            var yPos = dotSpacing * 0.5f
            var row = 0
            while (yPos < backArea.bottom) {
                var xPos = if (row % 2 == 0) dotSpacing * 0.5f else dotSpacing
                while (xPos < backArea.right) {
                    canvas.drawCircle(xPos, yPos, dotR, dotPaint)
                    xPos += dotSpacing
                }
                yPos += dotSpacing
                row++
            }
        } else {
            // Front pass (DRAWN OVER CUTOUT BASE!): ripped archival paper sheet with fiber deckle edge & cast shadow!
            val outerFiberPath = if (isVerticalSplit) {
                buildVerticalTearPath(w, h, rect.left - tearAmp * 0.55f, tearAmp * 1.05f, 1.35f, closeRight = true)
            } else {
                buildHorizontalTearPath(w, h, rect.top - tearAmp * 0.55f, tearAmp * 1.05f, 1.35f, closeBottom = true)
            }
            val mainPaperPath = if (isVerticalSplit) {
                buildVerticalTearPath(w, h, rect.left, tearAmp, 0.72f, closeRight = true)
            } else {
                buildHorizontalTearPath(w, h, rect.top, tearAmp, 0.72f, closeBottom = true)
            }

            // 1. Cast shadow falling from the ripped paper sheet onto the cutout & background behind it
            if (shadowStrength > 0.04f) {
                val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = withAlpha(Color.BLACK, (135f * shadowStrength * panelFactor).roundToInt())
                    maskFilter = BlurMaskFilter(max(12f, unit * 0.022f), BlurMaskFilter.Blur.NORMAL)
                }
                canvas.save()
                if (isVerticalSplit) canvas.translate(-tearAmp * 0.45f, 0f) else canvas.translate(0f, -tearAmp * 0.45f)
                canvas.drawPath(outerFiberPath, shadowPaint)
                canvas.restore()
            }

            // 2. Exposed raw white/cream paper pulp fiber lip sticking slightly past the print surface
            val fiberColor = if (isLightBase) Color.rgb(252, 249, 242) else blend(lightColor, Color.WHITE, 0.72f)
            val fiberPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = withAlpha(fiberColor, (242 * panelFactor).roundToInt())
            }
            canvas.drawPath(outerFiberPath, fiberPaint)

            // 3. Main zine archival sheet body
            val sheetTop = if (isLightBase) paperColor else blend(darkColor, deepColor, 0.45f)
            val sheetBot = if (isLightBase) blend(paperColor, accentColor, 0.06f) else darkColor
            val sheetPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = LinearGradient(
                    0f, rect.top, 0f, h,
                    intArrayOf(
                        withAlpha(sheetTop, (252 * panelFactor).roundToInt()),
                        withAlpha(sheetBot, (255 * panelFactor).roundToInt())
                    ),
                    null,
                    Shader.TileMode.CLAMP
                )
            }
            canvas.drawPath(mainPaperPath, sheetPaint)

            // 4. Subtle translucent masking-tape corner strip accent on the torn edge
            canvas.save()
            val tapeW = unit * 0.14f
            val tapeH = unit * 0.036f
            val tx = if (isVerticalSplit) rect.left else w * 0.80f
            val ty = if (isVerticalSplit) h * 0.14f else rect.top
            canvas.translate(tx, ty)
            canvas.rotate(if (isVerticalSplit) -14f else 11f)
            val tapePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = withAlpha(accentColor, ((if (isLightBase) 115f else 145f) * panelFactor).roundToInt())
            }
            canvas.drawRect(-tapeW * 0.5f, -tapeH * 0.5f, tapeW * 0.5f, tapeH * 0.5f, tapePaint)
            canvas.restore()
        }
    }

    // ============================================================================================
    // 14. ARCHITECTURAL BLUEPRINT STAGE (Millimeter Grid, Reticle, Technical Callout Leader Lines)
    // ============================================================================================

    fun drawArchitecturalBlueprint(
        canvas: Canvas,
        rect: RectF,
        doc: PostDoc,
        accentColor: Int,
        glowColor: Int,
        lightColor: Int,
        darkColor: Int,
        isLightBase: Boolean,
        w: Float,
        h: Float,
        unit: Float
    ) {
        val panelFactor = (doc.panelOpacity.coerceIn(0, 100) / 100f).coerceAtLeast(0.25f)
        val lineBase = if (isLightBase) darkColor else glowColor

        // 1. Millimeter minor drafting grid + major 5-cell engineering grid
        val minorStep = max(20f, unit * 0.026f)
        val minorPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            strokeWidth = max(0.8f, unit * 0.0010f)
            color = withAlpha(lineBase, ((if (isLightBase) 22f else 28f) * panelFactor).roundToInt())
        }
        val majorPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            strokeWidth = max(1.3f, unit * 0.0017f)
            color = withAlpha(accentColor, ((if (isLightBase) 55f else 72f) * panelFactor).roundToInt())
        }

        var idx = 0
        var gx = 0f
        while (gx <= w) {
            canvas.drawLine(gx, 0f, gx, h, if (idx % 5 == 0) majorPaint else minorPaint)
            gx += minorStep
            idx++
        }
        idx = 0
        var gy = 0f
        while (gy <= h) {
            canvas.drawLine(0f, gy, w, gy, if (idx % 5 == 0) majorPaint else minorPaint)
            gy += minorStep
            idx++
        }

        // 2. Architectural perimeter blueprint drafting frame + ruler tick marks
        val inset = unit * 0.036f
        val frameRect = RectF(inset, inset, w - inset, h - inset)
        val framePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = max(1.6f, unit * 0.0022f)
            color = withAlpha(accentColor, (150 * panelFactor).roundToInt())
        }
        canvas.drawRect(frameRect, framePaint)

        // Inner corner L-ticks
        val tickLen = unit * 0.022f
        val tickPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            strokeWidth = max(2.2f, unit * 0.003f)
            color = withAlpha(if (isLightBase) darkColor else Color.WHITE, (185 * panelFactor).roundToInt())
        }
        val corners = arrayOf(
            floatArrayOf(frameRect.left, frameRect.top, 1f, 1f),
            floatArrayOf(frameRect.right, frameRect.top, -1f, 1f),
            floatArrayOf(frameRect.left, frameRect.bottom, 1f, -1f),
            floatArrayOf(frameRect.right, frameRect.bottom, -1f, -1f)
        )
        for (c in corners) {
            canvas.drawLine(c[0], c[1], c[0] + c[2] * tickLen, c[1], tickPaint)
            canvas.drawLine(c[0], c[1], c[0], c[1] + c[3] * tickLen, tickPaint)
        }

        // 3. Precision engineering target reticle centered in the cutout stage rect
        val cx = rect.centerX()
        val cy = rect.centerY()
        val rMain = min(rect.width(), rect.height()) * 0.40f
        val reticleGlow = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(
                cx, cy, rMain * 1.45f,
                intArrayOf(
                    withAlpha(accentColor, ((if (isLightBase) 36f else 68f) * panelFactor).roundToInt()),
                    Color.TRANSPARENT
                ),
                floatArrayOf(0f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawCircle(cx, cy, rMain * 1.45f, reticleGlow)

        val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = max(1.5f, unit * 0.002f)
            color = withAlpha(accentColor, (175 * panelFactor).roundToInt())
        }
        canvas.drawCircle(cx, cy, rMain, circlePaint)

        val dashedPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = max(1.2f, unit * 0.0016f)
            color = withAlpha(lineBase, (130 * panelFactor).roundToInt())
            pathEffect = DashPathEffect(floatArrayOf(unit * 0.014f, unit * 0.010f), 0f)
        }
        canvas.drawCircle(cx, cy, rMain * 1.18f, dashedPaint)
        canvas.drawCircle(cx, cy, rMain * 0.62f, dashedPaint)

        // Axis crosshairs extending through the reticle
        val axisPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            strokeWidth = max(1.1f, unit * 0.0014f)
            color = withAlpha(lineBase, (115 * panelFactor).roundToInt())
        }
        canvas.drawLine(cx - rMain * 1.38f, cy, cx + rMain * 1.38f, cy, axisPaint)
        canvas.drawLine(cx, cy - rMain * 1.32f, cx, cy + rMain * 1.32f, axisPaint)

        // 4. Technical callout leader lines with hollow precision rings + technical metadata stamps
        val leaderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = max(1.5f, unit * 0.002f)
            color = withAlpha(accentColor, (200 * panelFactor).roundToInt())
        }
        val nodeFill = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = if (isLightBase) Color.WHITE else darkColor
        }
        val monoPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = Typeface.MONOSPACE
            textSize = max(11f, unit * 0.0155f)
            color = withAlpha(if (isLightBase) darkColor else glowColor, (195 * panelFactor).roundToInt())
        }

        val callouts = listOf(
            Triple(-0.68f, -0.58f, "FIG.01 // AXIS"),
            Triple(0.68f, -0.44f, "SCALE 1:1"),
            Triple(-0.62f, 0.56f, "REF // 90.0°")
        )
        val nodeR = max(4.5f, unit * 0.006f)
        for ((dxFrac, dyFrac, label) in callouts) {
            val sx = cx + rMain * dxFrac
            val sy = cy + rMain * dyFrac
            val dir = if (dxFrac < 0f) -1f else 1f
            val ex = sx + dir * unit * 0.055f
            val ey = sy + (if (dyFrac < 0f) -1f else 1f) * unit * 0.036f
            val tx = ex + dir * unit * 0.095f

            val lp = Path().apply {
                moveTo(sx, sy)
                lineTo(ex, ey)
                lineTo(tx, ey)
            }
            canvas.drawPath(lp, leaderPaint)
            canvas.drawCircle(sx, sy, nodeR, nodeFill)
            canvas.drawCircle(sx, sy, nodeR, leaderPaint)

            val labelX = if (dir < 0f) tx else ex + unit * 0.008f
            canvas.drawText(label, labelX, ey - max(5f, unit * 0.006f), monoPaint)
        }
    }

    // ============================================================================================
    // 15. PERSPECTIVE HORIZON RUNWAY (One-Point Receding Grid & Glowing Architectural Runway Floor)
    // ============================================================================================

    fun drawPerspectiveRunway(
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
        val panelFactor = (doc.panelOpacity.coerceIn(0, 100) / 100f).coerceAtLeast(0.25f)
        val horizonY = rect.top.coerceIn(h * 0.36f, h * 0.72f)
        val vanishX = rect.centerX()

        // 1. Radiant atmospheric vanishing-point bloom sitting right on the horizon
        val sunR = max(w * 0.54f, h * 0.34f)
        val bloomPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(
                vanishX, horizonY, sunR,
                intArrayOf(
                    withAlpha(glowColor, ((if (isLightBase) 110f else 165f) * panelFactor).roundToInt()),
                    withAlpha(accentColor, ((if (isLightBase) 55f else 95f) * panelFactor).roundToInt()),
                    withAlpha(secondaryColor, ((if (isLightBase) 20f else 42f) * panelFactor).roundToInt()),
                    Color.TRANSPARENT
                ),
                floatArrayOf(0f, 0.28f, 0.62f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawCircle(vanishX, horizonY, sunR, bloomPaint)

        // 2. Runway floor plane fill below the horizon
        val floorPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                0f, horizonY, 0f, h,
                intArrayOf(
                    withAlpha(if (isLightBase) blend(Color.WHITE, accentColor, 0.18f) else blend(deepColor, accentColor, 0.25f), (235 * panelFactor).roundToInt()),
                    withAlpha(if (isLightBase) blend(Color.rgb(232, 238, 248), accentColor, 0.08f) else darkColor, (252 * panelFactor).roundToInt())
                ),
                null,
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f, horizonY, w, h, floorPaint)

        canvas.save()
        canvas.clipRect(0f, horizonY, w, h)

        // 3. Illuminated central runway trapezoid corridor plunging to the vanishing point
        val runwayPath = Path().apply {
            moveTo(vanishX - w * 0.025f, horizonY)
            lineTo(vanishX + w * 0.025f, horizonY)
            lineTo(vanishX + w * 0.34f, h)
            lineTo(vanishX - w * 0.34f, h)
            close()
        }
        val runwayFill = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                vanishX, horizonY, vanishX, h,
                intArrayOf(
                    withAlpha(glowColor, ((if (isLightBase) 95f else 135f) * panelFactor).roundToInt()),
                    withAlpha(accentColor, ((if (isLightBase) 42f else 68f) * panelFactor).roundToInt()),
                    withAlpha(accentColor, ((if (isLightBase) 12f else 22f) * panelFactor).roundToInt())
                ),
                floatArrayOf(0f, 0.42f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawPath(runwayPath, runwayFill)

        // 4. Converging one-point perspective floor rails
        val railPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            strokeWidth = max(1.3f, unit * 0.0018f)
            color = withAlpha(accentColor, ((if (isLightBase) 125f else 155f) * panelFactor).roundToInt())
        }
        val laneEdgePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            strokeWidth = max(2.4f, unit * 0.0032f)
            color = withAlpha(if (isLightBase) accentColor else Color.WHITE, (215 * panelFactor).roundToInt())
        }
        for (i in -8..8) {
            val botX = vanishX + i * w * 0.17f
            val p = if (i == -2 || i == 2) laneEdgePaint else railPaint
            canvas.drawLine(vanishX, horizonY, botX, h, p)
        }

        // 5. Logarithmically spaced horizontal perspective depth cross-bars (authentic 3D foreshortening!)
        val barCount = 13
        val floorH = h - horizonY
        for (i in 1..barCount) {
            val u = i.toFloat() / barCount.toFloat()
            val depthFrac = u.pow(2.35f)
            val yBar = horizonY + floorH * depthFrac
            val alpha = ((45f + 160f * depthFrac) * panelFactor).roundToInt().coerceIn(0, 255)
            val barPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                strokeWidth = max(1.0f, unit * (0.0011f + 0.0022f * depthFrac))
                color = withAlpha(if (isLightBase) darkColor else glowColor, alpha)
            }
            canvas.drawLine(0f, yBar, w, yBar, barPaint)
        }

        // 6. Soft horizon atmospheric fog veil blending distant grid cleanly into light
        val fogH = floorH * 0.24f
        val fogPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                0f, horizonY, 0f, horizonY + fogH,
                intArrayOf(withAlpha(glowColor, ((if (isLightBase) 130f else 150f) * panelFactor).roundToInt()), Color.TRANSPARENT),
                null,
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f, horizonY, w, horizonY + fogH, fogPaint)
        canvas.restore()

        // 7. Crisp illuminated horizon line
        val horizonLine = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            strokeWidth = max(2f, unit * 0.0028f)
            shader = LinearGradient(
                0f, horizonY, w, horizonY,
                intArrayOf(
                    withAlpha(accentColor, 60),
                    withAlpha(if (isLightBase) accentColor else Color.WHITE, 240),
                    withAlpha(accentColor, 60)
                ),
                floatArrayOf(0f, 0.5f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawLine(0f, horizonY, w, horizonY, horizonLine)
    }

    // ============================================================================================
    // 16. KINETIC SLICED LOUVERS (7 Staggered Vertical Photo Strips with Optical Parallax)
    // ============================================================================================

    fun drawKineticSlicedLouvers(
        canvas: Canvas,
        rect: RectF,
        doc: PostDoc,
        sourcePhoto: Bitmap?,
        accentColor: Int,
        secondaryColor: Int,
        glowColor: Int,
        lightColor: Int,
        darkColor: Int,
        deepColor: Int,
        isLightBase: Boolean,
        unit: Float
    ) {
        val panelFactor = (doc.panelOpacity.coerceIn(0, 100) / 100f).coerceAtLeast(0.25f)
        val shadowStrength = doc.sceneShadowStrength.coerceIn(0f, 1f)
        val count = 7
        val gap = max(6f, rect.width() * 0.018f)
        val stripW = (rect.width() - gap * (count - 1)) / count.toFloat()
        val cornerR = max(8f, stripW * 0.14f)

        // Rhythmic architectural stagger profile (top inset, bottom inset, optical parallax shift)
        val staggerTop = floatArrayOf(0.11f, 0.04f, 0.00f, 0.05f, 0.01f, 0.06f, 0.13f)
        val staggerBot = floatArrayOf(0.08f, 0.02f, 0.05f, 0.00f, 0.04f, 0.01f, 0.10f)
        val parallaxShift = floatArrayOf(-0.045f, -0.022f, 0.018f, 0.0f, -0.018f, 0.026f, 0.048f)

        val adj = doc.slotAdjust(0)

        for (i in 0 until count) {
            val sx = rect.left + i * (stripW + gap)
            val sy = rect.top + rect.height() * staggerTop[i]
            val sb = rect.bottom - rect.height() * staggerBot[i]
            val sr = RectF(sx, sy, sx + stripW, sb)
            val clipPath = Path().apply { addRoundRect(sr, cornerR, cornerR, Path.Direction.CW) }

            // 1. Cast architectural drop shadow per louver
            if (shadowStrength > 0.04f) {
                val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = withAlpha(Color.BLACK, ((if (isLightBase) 68f else 150f) * shadowStrength * panelFactor).roundToInt())
                    maskFilter = BlurMaskFilter(max(10f, unit * 0.018f), BlurMaskFilter.Blur.NORMAL)
                }
                canvas.save()
                canvas.translate(unit * 0.008f, unit * 0.014f)
                canvas.drawPath(clipPath, shadowPaint)
                canvas.restore()
            }

            canvas.save()
            canvas.clipPath(clipPath)

            if (sourcePhoto != null && !sourcePhoto.isRecycled) {
                // Draw the user's photo across the full louver envelope with subtle optical parallax shift per louver!
                val photoPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
                drawCrop(
                    canvas = canvas,
                    bmp = sourcePhoto,
                    dest = rect,
                    zoom = (adj.zoom * 1.08f).coerceIn(1f, 4f),
                    focusX = (adj.focusX + parallaxShift[i]).coerceIn(0f, 1f),
                    focusY = (adj.focusY + (if (i % 2 == 0) -0.02f else 0.02f)).coerceIn(0f, 1f),
                    paint = photoPaint
                )
                // Subtle architectural louver glass sheen
                val sheenPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    shader = LinearGradient(
                        sr.left, sr.top, sr.right, sr.top,
                        intArrayOf(
                            withAlpha(Color.WHITE, 46),
                            Color.TRANSPARENT,
                            withAlpha(Color.BLACK, 64)
                        ),
                        floatArrayOf(0f, 0.35f, 1f),
                        Shader.TileMode.CLAMP
                    )
                }
                canvas.drawRect(sr, sheenPaint)
            } else {
                // Vibrant sculptural optical gradient ribbons when no photo is loaded yet
                val t = i.toFloat() / (count - 1).toFloat()
                val topCol = blend(accentColor, glowColor, 0.25f + 0.45f * sin(t * PI).toFloat())
                val midCol = blend(deepColor, secondaryColor, 0.35f + 0.35f * t)
                val botCol = blend(darkColor, accentColor, 0.18f + 0.24f * (1f - t))
                val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    shader = LinearGradient(
                        sr.left, sr.top, sr.right, sr.bottom,
                        intArrayOf(
                            withAlpha(topCol, (238 * panelFactor).roundToInt()),
                            withAlpha(midCol, (245 * panelFactor).roundToInt()),
                            withAlpha(botCol, (250 * panelFactor).roundToInt())
                        ),
                        floatArrayOf(0f, 0.52f, 1f),
                        Shader.TileMode.CLAMP
                    )
                }
                canvas.drawRect(sr, fillPaint)

                // Subtle specular rib highlight inside each strip
                val glossPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    shader = LinearGradient(
                        sr.left, sr.top, sr.left + stripW * 0.45f, sr.top,
                        intArrayOf(withAlpha(Color.WHITE, 90), Color.TRANSPARENT),
                        null,
                        Shader.TileMode.CLAMP
                    )
                }
                canvas.drawRect(sr, glossPaint)
            }
            canvas.restore()

            // 2. Crisp beveled rim border on each louver
            val rimPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = max(1.4f, unit * 0.0020f)
                shader = LinearGradient(
                    sr.left, sr.top, sr.right, sr.bottom,
                    intArrayOf(
                        withAlpha(if (isLightBase) darkColor else Color.WHITE, (145 * panelFactor).roundToInt()),
                        withAlpha(accentColor, (125 * panelFactor).roundToInt())
                    ),
                    null,
                    Shader.TileMode.CLAMP
                )
            }
            canvas.drawPath(clipPath, rimPaint)
        }
    }

    // ============================================================================================
    // 17. 35MM CONTACT-SHEET POP-OUT (Angled Perforated Cinema Filmstrip Holding Up To 3 Photos)
    // ============================================================================================

    fun drawContactSheetFilm(
        canvas: Canvas,
        rect: RectF,
        doc: PostDoc,
        photos: List<Bitmap?>,
        accentColor: Int,
        glowColor: Int,
        lightColor: Int,
        darkColor: Int,
        isLightBase: Boolean,
        unit: Float
    ) {
        val panelFactor = (doc.panelOpacity.coerceIn(0, 100) / 100f).coerceAtLeast(0.25f)
        val shadowStrength = doc.sceneShadowStrength.coerceIn(0f, 1f)
        val isVertical = rect.height() > rect.width() * 1.15f
        val tiltDeg = if (isVertical) -6.5f else -7.5f

        canvas.save()
        canvas.rotate(tiltDeg, rect.centerX(), rect.centerY())

        val stripRect = RectF(rect)
        val stripCorner = max(6f, unit * 0.008f)

        // 1. Cast drop shadow beneath the celluloid filmstrip
        if (shadowStrength > 0.04f) {
            val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = withAlpha(Color.BLACK, ((if (isLightBase) 85f else 175f) * shadowStrength * panelFactor).roundToInt())
                maskFilter = BlurMaskFilter(max(14f, unit * 0.024f), BlurMaskFilter.Blur.NORMAL)
            }
            canvas.save()
            canvas.translate(unit * 0.012f, unit * 0.020f)
            canvas.drawRoundRect(stripRect, stripCorner, stripCorner, shadowPaint)
            canvas.restore()
        }

        // 2. Authentic matte dark celluloid film stock body
        val filmBodyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                stripRect.left, stripRect.top, stripRect.right, stripRect.bottom,
                intArrayOf(
                    withAlpha(Color.rgb(18, 20, 26), (252 * panelFactor).roundToInt()),
                    withAlpha(Color.rgb(9, 11, 15), (255 * panelFactor).roundToInt()),
                    withAlpha(Color.rgb(22, 24, 31), (252 * panelFactor).roundToInt())
                ),
                floatArrayOf(0f, 0.5f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRoundRect(stripRect, stripCorner, stripCorner, filmBodyPaint)

        // 3. Precision 35mm sprocket perforations along both rails
        val railBand = if (isVertical) stripRect.width() * 0.135f else stripRect.height() * 0.145f
        val holeColor = if (isLightBase) Color.rgb(245, 242, 234) else darkColor
        val holePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = withAlpha(holeColor, 230)
        }
        val holeBorder = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = max(1f, unit * 0.0012f)
            color = withAlpha(Color.WHITE, 44)
        }

        if (isVertical) {
            val holeW = railBand * 0.56f
            val holeH = holeW * 0.72f
            val step = holeH * 1.75f
            val leftRailCx = stripRect.left + railBand * 0.52f
            val rightRailCx = stripRect.right - railBand * 0.52f
            var y = stripRect.top + step * 0.75f
            while (y < stripRect.bottom - holeH * 0.6f) {
                for (cx in listOf(leftRailCx, rightRailCx)) {
                    val hr = RectF(cx - holeW * 0.5f, y - holeH * 0.5f, cx + holeW * 0.5f, y + holeH * 0.5f)
                    canvas.drawRoundRect(hr, holeH * 0.22f, holeH * 0.22f, holePaint)
                    canvas.drawRoundRect(hr, holeH * 0.22f, holeH * 0.22f, holeBorder)
                }
                y += step
            }
        } else {
            val holeH = railBand * 0.56f
            val holeW = holeH * 0.76f
            val step = holeW * 1.85f
            val topRailCy = stripRect.top + railBand * 0.52f
            val botRailCy = stripRect.bottom - railBand * 0.52f
            var x = stripRect.left + step * 0.75f
            while (x < stripRect.right - holeW * 0.6f) {
                for (cy in listOf(topRailCy, botRailCy)) {
                    val hr = RectF(x - holeW * 0.5f, cy - holeH * 0.5f, x + holeW * 0.5f, cy + holeH * 0.5f)
                    canvas.drawRoundRect(hr, holeW * 0.22f, holeW * 0.22f, holePaint)
                    canvas.drawRoundRect(hr, holeW * 0.22f, holeW * 0.22f, holeBorder)
                }
                x += step
            }
        }

        // 4. Three 35mm contact-sheet photo windows + authentic amber/gold film rebate edge codes
        val frameCount = 3
        val frameGap = max(8f, unit * 0.012f)
        val innerPad = max(10f, unit * 0.014f)
        val stampPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = Typeface.MONOSPACE
            textSize = max(9.5f, unit * 0.0125f)
            color = withAlpha(glowColor, 205)
        }
        val frameLabels = listOf("01A · 35MM", "02A · 400TX", "03A · ARCHIVE")

        for (i in 0 until frameCount) {
            val fr = if (isVertical) {
                val totalH = stripRect.height() - innerPad * 2f - frameGap * (frameCount - 1)
                val fh = totalH / frameCount.toFloat()
                val top = stripRect.top + innerPad + i * (fh + frameGap)
                RectF(stripRect.left + railBand, top, stripRect.right - railBand, top + fh)
            } else {
                val totalW = stripRect.width() - innerPad * 2f - frameGap * (frameCount - 1)
                val fw = totalW / frameCount.toFloat()
                val left = stripRect.left + innerPad + i * (fw + frameGap)
                RectF(left, stripRect.top + railBand, left + fw, stripRect.bottom - railBand)
            }

            val frameCorner = max(4f, unit * 0.005f)
            val frameClip = Path().apply { addRoundRect(fr, frameCorner, frameCorner, Path.Direction.CW) }
            val bmp = photos.getOrNull(i) ?: if (doc.fillEmptyWithFirst) photos.firstOrNull() else null
            val adj = doc.slotAdjust(i)

            canvas.save()
            canvas.clipPath(frameClip)
            if (bmp != null && !bmp.isRecycled) {
                drawCrop(
                    canvas = canvas,
                    bmp = bmp,
                    dest = fr,
                    zoom = adj.zoom.coerceIn(1f, 3f),
                    focusX = adj.focusX.coerceIn(0f, 1f),
                    focusY = adj.focusY.coerceIn(0f, 1f)
                )
            } else {
                // Cinematic warm darkroom negative frame placeholder (no fake UI bars!)
                val negPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    shader = LinearGradient(
                        fr.left, fr.top, fr.right, fr.bottom,
                        intArrayOf(
                            blend(Color.rgb(26, 30, 42), accentColor, 0.24f + i * 0.08f),
                            blend(Color.rgb(12, 14, 20), accentColor, 0.12f)
                        ),
                        null,
                        Shader.TileMode.CLAMP
                    )
                }
                canvas.drawRect(fr, negPaint)
                // Fine camera focus bracket in center of empty frame
                val brkPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    style = Paint.Style.STROKE
                    strokeWidth = max(1.2f, unit * 0.0016f)
                    color = withAlpha(lightColor, 72)
                }
                val brkR = min(fr.width(), fr.height()) * 0.14f
                canvas.drawCircle(fr.centerX(), fr.centerY(), brkR, brkPaint)
                canvas.drawLine(fr.centerX() - brkR * 1.4f, fr.centerY(), fr.centerX() + brkR * 1.4f, fr.centerY(), brkPaint)
                canvas.drawLine(fr.centerX(), fr.centerY() - brkR * 1.4f, fr.centerX(), fr.centerY() + brkR * 1.4f, brkPaint)
            }
            canvas.restore()

            // Crisp frame aperture border
            val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = max(1.2f, unit * 0.0015f)
                color = withAlpha(Color.WHITE, 55)
            }
            canvas.drawPath(frameClip, borderPaint)

            // Film stock index stamp
            if (!isVertical) {
                canvas.drawText(frameLabels[i], fr.left + 4f, fr.top - max(3f, railBand * 0.14f), stampPaint)
            } else {
                canvas.drawText(frameLabels[i], fr.left + 4f, fr.top - max(2f, frameGap * 0.20f), stampPaint)
            }
        }

        // 5. Subtle diagonal acetate sheen across the celluloid strip
        val glossPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                stripRect.left, stripRect.top, stripRect.right, stripRect.bottom,
                intArrayOf(withAlpha(Color.WHITE, 26), Color.TRANSPARENT, withAlpha(accentColor, 20)),
                floatArrayOf(0f, 0.45f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRoundRect(stripRect, stripCorner, stripCorner, glossPaint)
        canvas.restore()
    }

    // ============================================================================================
    // 18. LUMINOUS ECLIPSE HALO STAGE (Radiant Backlit Solar Corona + Astronomical Rings)
    // ============================================================================================

    fun drawEclipseHaloStage(
        canvas: Canvas,
        rect: RectF,
        doc: PostDoc,
        accentColor: Int,
        secondaryColor: Int,
        glowColor: Int,
        lightColor: Int,
        darkColor: Int,
        isLightBase: Boolean,
        w: Float,
        h: Float,
        unit: Float
    ) {
        val panelFactor = (doc.panelOpacity.coerceIn(0, 100) / 100f).coerceAtLeast(0.25f)
        val cx = rect.centerX()
        val cy = rect.centerY()
        val discR = min(rect.width(), rect.height()) * 0.34f
        val lightRad = Math.toRadians(doc.sceneLightAngle.toDouble())

        // 1. Wide celestial atmospheric corona bloom behind the eclipse disc
        val outerCoronaR = discR * 2.55f
        val outerCorona = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(
                cx, cy, outerCoronaR,
                intArrayOf(
                    withAlpha(glowColor, ((if (isLightBase) 125f else 190f) * panelFactor).roundToInt()),
                    withAlpha(accentColor, ((if (isLightBase) 72f else 135f) * panelFactor).roundToInt()),
                    withAlpha(secondaryColor, ((if (isLightBase) 22f else 54f) * panelFactor).roundToInt()),
                    Color.TRANSPARENT
                ),
                floatArrayOf(0.28f, 0.46f, 0.72f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawCircle(cx, cy, outerCoronaR, outerCorona)

        // 2. Delicate fine astronomical concentric orbital rings & compass needles
        val ringLineColor = if (isLightBase) darkColor else lightColor
        val ringPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = max(1.1f, unit * 0.0014f)
            color = withAlpha(ringLineColor, ((if (isLightBase) 46f else 62f) * panelFactor).roundToInt())
        }
        canvas.drawCircle(cx, cy, discR * 1.38f, ringPaint)
        canvas.drawCircle(cx, cy, discR * 1.82f, ringPaint)

        val dashedOrbit = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = max(1.1f, unit * 0.0015f)
            color = withAlpha(accentColor, ((if (isLightBase) 95f else 130f) * panelFactor).roundToInt())
            pathEffect = DashPathEffect(floatArrayOf(unit * 0.010f, unit * 0.012f), 0f)
        }
        canvas.drawCircle(cx, cy, discR * 1.60f, dashedOrbit)

        // Fine astronomical degree tick marks around the 1.38x orbit
        val tickPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            strokeWidth = max(1.2f, unit * 0.0016f)
            color = withAlpha(glowColor, (145 * panelFactor).roundToInt())
        }
        for (deg in 0 until 360 step 15) {
            val rad = Math.toRadians(deg.toDouble())
            val isCardinal = deg % 90 == 0
            val r0 = discR * (if (isCardinal) 1.28f else 1.34f)
            val r1 = discR * (if (isCardinal) 1.52f else 1.42f)
            canvas.drawLine(
                cx + r0 * cos(rad).toFloat(),
                cy + r0 * sin(rad).toFloat(),
                cx + r1 * cos(rad).toFloat(),
                cy + r1 * sin(rad).toFloat(),
                tickPaint
            )
        }

        // 3. Incandescent tight solar limb ring right behind the dark eclipse disc
        val tightGlow = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = max(10f, discR * 0.14f)
            color = withAlpha(glowColor, (225 * panelFactor).roundToInt())
            maskFilter = BlurMaskFilter(max(12f, discR * 0.18f), BlurMaskFilter.Blur.NORMAL)
        }
        canvas.drawCircle(cx, cy, discR * 1.01f, tightGlow)

        // 4. Velvety eclipse occluder disc (creates dramatic backlit contrast behind any cutout!)
        val discCore = if (isLightBase) blend(darkColor, Color.rgb(18, 22, 34), 0.5f) else Color.rgb(4, 6, 12)
        val discPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(
                cx, cy, discR,
                intArrayOf(discCore, blend(discCore, accentColor, 0.14f)),
                floatArrayOf(0.75f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawCircle(cx, cy, discR, discPaint)

        // 5. Razor-crisp bright limb ring hugging the eclipse silhouette
        val limbStroke = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = max(2.2f, unit * 0.0032f)
            shader = LinearGradient(
                cx - discR, cy - discR, cx + discR, cy + discR,
                intArrayOf(Color.WHITE, glowColor, accentColor, Color.WHITE),
                floatArrayOf(0f, 0.35f, 0.72f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawCircle(cx, cy, discR, limbStroke)

        // 6. Signature Diamond-Ring solar flare burst positioned along the limb (steerable by Light Angle!)
        val fx = cx + discR * cos(lightRad - PI * 0.75).toFloat()
        val fy = cy + discR * sin(lightRad - PI * 0.75).toFloat()
        val flareR = discR * 0.52f
        val flareGlow = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(
                fx, fy, flareR,
                intArrayOf(
                    Color.WHITE,
                    withAlpha(glowColor, 225),
                    withAlpha(accentColor, 95),
                    Color.TRANSPARENT
                ),
                floatArrayOf(0f, 0.18f, 0.48f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawCircle(fx, fy, flareR, flareGlow)

        // Crisp 4-point optical anamorphic star needle on the diamond ring
        val needlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            strokeWidth = max(1.8f, unit * 0.0024f)
            shader = LinearGradient(
                fx - flareR * 1.1f, fy, fx + flareR * 1.1f, fy,
                intArrayOf(Color.TRANSPARENT, Color.WHITE, Color.TRANSPARENT),
                floatArrayOf(0f, 0.5f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawLine(fx - flareR * 1.1f, fy, fx + flareR * 1.1f, fy, needlePaint)
        val vNeedlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            strokeWidth = max(1.4f, unit * 0.0018f)
            shader = LinearGradient(
                fx, fy - flareR * 0.65f, fx, fy + flareR * 0.65f,
                intArrayOf(Color.TRANSPARENT, Color.WHITE, Color.TRANSPARENT),
                floatArrayOf(0f, 0.5f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawLine(fx, fy - flareR * 0.65f, fx, fy + flareR * 0.65f, vNeedlePaint)

        // 7. Delicate celestial micro-stars scattered around the negative space
        val starPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = withAlpha(if (isLightBase) accentColor else Color.WHITE, (135 * panelFactor).roundToInt())
        }
        val starPts = arrayOf(
            floatArrayOf(0.14f, 0.18f, 1.8f),
            floatArrayOf(0.84f, 0.22f, 2.3f),
            floatArrayOf(0.22f, 0.72f, 1.6f),
            floatArrayOf(0.88f, 0.64f, 2.0f),
            floatArrayOf(0.76f, 0.14f, 1.4f),
            floatArrayOf(0.12f, 0.44f, 1.9f),
            floatArrayOf(0.91f, 0.38f, 1.5f)
        )
        val sUnit = (unit / 1080f).coerceAtLeast(0.8f)
        for (sp in starPts) {
            canvas.drawCircle(sp[0] * w, sp[1] * h, sp[2] * sUnit, starPaint)
        }
    }
}
