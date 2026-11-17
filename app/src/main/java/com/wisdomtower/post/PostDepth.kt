package com.wisdomtower.post

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

object PostSafeArea {
    fun safeRect(size: PostSize): RectF = when (size) {
        PostSize.STORY -> RectF(0.09f, 0.13f, 0.91f, 0.79f)
        PostSize.LANDSCAPE -> RectF(0.05f, 0.05f, 0.95f, 0.95f)
        PostSize.WIDE -> RectF(0.05f, 0.05f, 0.95f, 0.95f)
    }

    fun computeHeroBounds(layout: TemplateLayout, doc: PostDoc? = null): RectF? {
        var minX = 1f
        var minY = 1f
        var maxX = 0f
        var maxY = 0f
        var found = false

        fun include(x: Float, y: Float, w: Float, h: Float) {
            if (w <= 0f || h <= 0f) return
            minX = min(minX, x)
            minY = min(minY, y)
            maxX = max(maxX, x + w)
            maxY = max(maxY, y + h)
            found = true
        }

        layout.photo?.let { area ->
            val fullBleed = area.cells.isEmpty() &&
                area.x <= 0.02f &&
                area.y <= 0.02f &&
                area.w >= 0.96f &&
                area.h >= 0.96f &&
                area.clipShape == null
            if (!fullBleed) {
                if (area.cells.isNotEmpty()) {
                    for (cell in area.cells) {
                        include(cell.x, cell.y, cell.w, cell.h)
                    }
                } else {
                    include(area.x, area.y, area.w, area.h)
                }
            }
        }

        for (d in layout.decor) {
            val isHeroDecor = when (d.shape) {
                DecorShape.PHONE,
                DecorShape.DEVICE_3D,
                DecorShape.CHECK_CARDS,
                DecorShape.RIBBON_PATH,
                DecorShape.CARD_FAN,
                DecorShape.DATE_SLAB,
                DecorShape.MONUMENT_SLAB,
                DecorShape.DOC_STACK,
                DecorShape.SPEECH_STAGE,
                DecorShape.PORTAL_ARCH,
                DecorShape.PEEL_CORNER,
                DecorShape.TAPE_BAND,
                DecorShape.STARBURST,
                DecorShape.FLOATING_OBJECT,
                DecorShape.SCENE_ESCKEY,
                DecorShape.SCENE_BOOKPOOL,
                DecorShape.SCENE_HOUSEDOOR,
                DecorShape.SCENE_DESKFLAT,
                DecorShape.SCENE_FACELIGHT,
                DecorShape.SCENE_PENCILCITY,
                DecorShape.SCENE_BACKPACKPORTAL,
                DecorShape.SCENE_CLOCKHANG,
                DecorShape.SCENE_EXAMCLIFF,
                DecorShape.SCENE_CAPUFO,
                DecorShape.SCENE_PAPERBOAT -> true
                DecorShape.CARD -> !(d.x <= 0.02f && d.y <= 0.02f && d.w >= 0.96f && d.h >= 0.96f)
                else -> false
            }
            if (isHeroDecor) {
                include(d.x, d.y, d.w, d.h)
            }
        }

        for (slot in layout.slots) {
            if (slot.drawnByDecor) continue
            val text = slot.literal ?: doc?.text(slot.slot).orEmpty()
            if (doc != null && text.isBlank()) continue
            include(slot.x, slot.y, slot.w, slot.h)
        }

        if (!found) return null
        return RectF(minX, minY, maxX, maxY)
    }

    fun computeSafeMatrix(
        size: PostSize,
        layout: TemplateLayout,
        doc: PostDoc?,
        w: Float,
        h: Float
    ): Matrix? {
        if (size != PostSize.STORY || !layout.safeFit) return null
        val bounds = computeHeroBounds(layout, doc) ?: return null
        val safe = safeRect(size)
        val bw = bounds.width().coerceAtLeast(0.01f)
        val bh = bounds.height().coerceAtLeast(0.01f)
        val sw = safe.width()
        val sh = safe.height()

        val fitsX = bounds.left >= safe.left - 0.005f && bounds.right <= safe.right + 0.005f
        val fitsY = bounds.top >= safe.top - 0.005f && bounds.bottom <= safe.bottom + 0.005f
        if (fitsX && fitsY) return null

        val scale = min(1f, min(sw / bw, sh / bh)).coerceIn(0.68f, 1f)
        val scaledW = bw * scale
        val scaledH = bh * scale

        val targetLeft = when {
            bounds.left * scale < safe.left -> safe.left
            bounds.left * scale + scaledW > safe.right -> safe.right - scaledW
            else -> safe.centerX() - scaledW * 0.5f
        }
        val targetTop = when {
            bounds.top * scale < safe.top -> safe.top
            bounds.top * scale + scaledH > safe.bottom -> safe.bottom - scaledH
            else -> safe.centerY() - scaledH * 0.5f
        }

        val matrix = Matrix()
        matrix.postTranslate(-bounds.left * w, -bounds.top * h)
        matrix.postScale(scale, scale)
        matrix.postTranslate(targetLeft * w, targetTop * h)
        return matrix
    }

    fun isWithinStorySafeArea(spec: PostTemplateSpec): Boolean {
        val layout = spec.layout(PostSize.STORY)
        if (layout.safeFit) return true
        val bounds = computeHeroBounds(layout, null) ?: return true
        val safe = safeRect(PostSize.STORY)
        return bounds.left >= safe.left - 0.02f &&
            bounds.right <= safe.right + 0.02f &&
            bounds.top >= safe.top - 0.02f &&
            bounds.bottom <= safe.bottom + 0.02f
    }
}

object PostDepth {
    fun lightVector(angleDeg: Float): Pair<Float, Float> {
        val rad = Math.toRadians(angleDeg.toDouble())
        return Pair((-cos(rad)).toFloat(), (-sin(rad)).toFloat())
    }

    fun contactShadow(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        rx: Float,
        ry: Float,
        strength: Float = 0.65f
    ) {
        if (rx <= 1f || ry <= 1f || strength <= 0.01f) return
        val s = strength.coerceIn(0f, 1f)
        canvas.save()
        canvas.translate(cx, cy)
        canvas.scale(1f, (ry / rx).coerceAtLeast(0.08f))
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(
                0f,
                0f,
                rx,
                intArrayOf(
                    PostTheme.alpha(Color.BLACK, 0.56f * s),
                    PostTheme.alpha(Color.BLACK, 0.24f * s),
                    Color.TRANSPARENT
                ),
                floatArrayOf(0f, 0.48f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawCircle(0f, 0f, rx, paint)
        canvas.restore()
    }

    fun castShadow(
        canvas: Canvas,
        path: Path,
        lightAngleDeg: Float = 135f,
        elevationPx: Float = 24f,
        strength: Float = 0.65f
    ) {
        if (elevationPx <= 0.5f || strength <= 0.01f) return
        val s = strength.coerceIn(0f, 1f)
        val (vx, vy) = lightVector(lightAngleDeg)
        val dx = -vx * elevationPx * 0.65f
        val dy = -vy * elevationPx * 0.65f + elevationPx * 0.35f

        val penumbra = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            color = PostTheme.alpha(Color.BLACK, 0.22f * s)
            setShadowLayer(elevationPx * 1.15f, dx, dy, PostTheme.alpha(Color.BLACK, 0.38f * s))
        }
        canvas.drawPath(path, penumbra)

        val umbra = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            color = PostTheme.alpha(Color.BLACK, 0.30f * s)
        }
        canvas.save()
        canvas.translate(dx * 0.28f, dy * 0.28f)
        canvas.drawPath(path, umbra)
        canvas.restore()
    }

    fun bevel(
        canvas: Canvas,
        rect: RectF,
        radius: Float,
        thicknessPx: Float,
        baseColor: Int,
        lightAngleDeg: Float = 135f
    ) {
        if (thicknessPx <= 0.5f) return
        val (vx, vy) = lightVector(lightAngleDeg)
        val sideColor = PostTheme.darken(baseColor, 0.34f)
        val bottomColor = PostTheme.darken(baseColor, 0.46f)
        val highlightColor = PostTheme.lighten(baseColor, 0.32f)

        val extrude = RectF(
            rect.left + (-vx * thicknessPx * 0.45f),
            rect.top + thicknessPx * 0.85f,
            rect.right + (-vx * thicknessPx * 0.45f),
            rect.bottom + thicknessPx * 0.85f
        )
        val extrudePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            shader = LinearGradient(
                extrude.left,
                extrude.top,
                extrude.right,
                extrude.bottom,
                sideColor,
                bottomColor,
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRoundRect(extrude, radius, radius, extrudePaint)

        val facePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            shader = LinearGradient(
                rect.left,
                rect.top,
                rect.right,
                rect.bottom,
                PostTheme.lighten(baseColor, 0.08f),
                PostTheme.darken(baseColor, 0.06f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRoundRect(rect, radius, radius, facePaint)

        val rimStroke = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = max(1.5f, thicknessPx * 0.14f)
            shader = LinearGradient(
                rect.left,
                rect.top,
                rect.right,
                rect.bottom,
                PostTheme.alpha(highlightColor, 0.72f),
                PostTheme.alpha(bottomColor, 0.45f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRoundRect(rect, radius, radius, rimStroke)
    }

    fun specularStrip(
        canvas: Canvas,
        rect: RectF,
        radius: Float = 0f,
        angleDeg: Float = 135f,
        alpha: Float = 0.24f
    ) {
        if (alpha <= 0.01f) return
        val rad = Math.toRadians(angleDeg.toDouble())
        val cx = rect.centerX()
        val cy = rect.centerY()
        val span = max(rect.width(), rect.height()) * 0.65f
        val x0 = cx - cos(rad).toFloat() * span
        val y0 = cy - sin(rad).toFloat() * span
        val x1 = cx + cos(rad).toFloat() * span
        val y1 = cy + sin(rad).toFloat() * span

        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            shader = LinearGradient(
                x0,
                y0,
                x1,
                y1,
                intArrayOf(
                    Color.TRANSPARENT,
                    PostTheme.alpha(Color.WHITE, alpha * 0.35f),
                    PostTheme.alpha(Color.WHITE, alpha),
                    PostTheme.alpha(Color.WHITE, alpha * 0.25f),
                    Color.TRANSPARENT
                ),
                floatArrayOf(0f, 0.38f, 0.48f, 0.58f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        if (radius > 0f) {
            canvas.drawRoundRect(rect, radius, radius, paint)
        } else {
            canvas.drawRect(rect, paint)
        }
    }

    fun ambientOcclusion(
        canvas: Canvas,
        rect: RectF,
        depthPx: Float = 18f,
        strength: Float = 0.55f
    ) {
        if (depthPx <= 1f || strength <= 0.01f) return
        val s = strength.coerceIn(0f, 1f)
        val topPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                rect.left,
                rect.top,
                rect.left,
                rect.top + depthPx,
                PostTheme.alpha(Color.BLACK, 0.42f * s),
                Color.TRANSPARENT,
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(rect.left, rect.top, rect.right, min(rect.bottom, rect.top + depthPx), topPaint)

        val leftPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                rect.left,
                rect.top,
                rect.left + depthPx,
                rect.top,
                PostTheme.alpha(Color.BLACK, 0.34f * s),
                Color.TRANSPARENT,
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(rect.left, rect.top, min(rect.right, rect.left + depthPx), rect.bottom, leftPaint)
    }

    fun rimLight(
        canvas: Canvas,
        path: Path,
        bounds: RectF,
        lightAngleDeg: Float = 135f,
        color: Int = Color.WHITE,
        widthPx: Float = 2.5f,
        alpha: Float = 0.62f
    ) {
        val (vx, vy) = lightVector(lightAngleDeg)
        val x0 = bounds.centerX() + vx * bounds.width() * 0.6f
        val y0 = bounds.centerY() + vy * bounds.height() * 0.6f
        val x1 = bounds.centerX() - vx * bounds.width() * 0.6f
        val y1 = bounds.centerY() - vy * bounds.height() * 0.6f

        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = widthPx
            shader = LinearGradient(
                x0,
                y0,
                x1,
                y1,
                intArrayOf(
                    PostTheme.alpha(color, alpha),
                    PostTheme.alpha(color, alpha * 0.25f),
                    Color.TRANSPARENT
                ),
                floatArrayOf(0f, 0.45f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawPath(path, paint)
    }

    fun paperCurl(
        canvas: Canvas,
        cornerRect: RectF,
        paperColor: Int,
        underColor: Int,
        accentColor: Int
    ) {
        val underPath = Path().apply {
            moveTo(cornerRect.right, cornerRect.top)
            lineTo(cornerRect.right, cornerRect.bottom)
            lineTo(cornerRect.left, cornerRect.bottom)
            close()
        }
        canvas.drawPath(
            underPath,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.FILL
                color = underColor
            }
        )

        val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            shader = LinearGradient(
                cornerRect.left,
                cornerRect.top,
                cornerRect.right,
                cornerRect.bottom,
                PostTheme.alpha(Color.BLACK, 0.35f),
                Color.TRANSPARENT,
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawPath(underPath, shadowPaint)

        val curlFlap = Path().apply {
            moveTo(cornerRect.right, cornerRect.top)
            quadTo(
                cornerRect.left + cornerRect.width() * 0.16f,
                cornerRect.top + cornerRect.height() * 0.18f,
                cornerRect.left,
                cornerRect.bottom
            )
            lineTo(cornerRect.right - cornerRect.width() * 0.72f, cornerRect.top)
            close()
        }
        val flapPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            shader = LinearGradient(
                cornerRect.left,
                cornerRect.top,
                cornerRect.right,
                cornerRect.bottom,
                PostTheme.lighten(paperColor, 0.14f),
                PostTheme.mix(paperColor, accentColor, 0.22f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawPath(curlFlap, flapPaint)
    }

    fun innerShadowAndSpill(
        canvas: Canvas,
        clipPath: Path,
        bounds: RectF,
        accentColor: Int,
        lightAngleDeg: Float = 135f,
        shadowStrength: Float = 0.65f,
        warmth: Float = 0f,
        drawInnerShadow: Boolean = true,
        drawLightSpill: Boolean = true
    ) {
        canvas.save()
        canvas.clipPath(clipPath)

        if (warmth != 0f) {
            val tintColor = if (warmth > 0f) Color.rgb(255, 184, 108) else Color.rgb(100, 178, 255)
            val tintAlpha = ( kotlin.math.abs(warmth) * 0.18f ).coerceIn(0f, 0.22f)
            canvas.drawRect(
                bounds,
                Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = PostTheme.alpha(tintColor, tintAlpha)
                }
            )
        }

        val (vx, vy) = lightVector(lightAngleDeg)
        if (drawLightSpill) {
            val spillCx = bounds.centerX() + vx * bounds.width() * 0.42f
            val spillCy = bounds.centerY() + vy * bounds.height() * 0.42f
            val spillRadius = max(bounds.width(), bounds.height()) * 0.72f
            val spillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = RadialGradient(
                    spillCx,
                    spillCy,
                    spillRadius,
                    intArrayOf(
                        PostTheme.alpha(PostTheme.lighten(accentColor, 0.28f), 0.32f),
                        PostTheme.alpha(accentColor, 0.10f),
                        Color.TRANSPARENT
                    ),
                    floatArrayOf(0f, 0.5f, 1f),
                    Shader.TileMode.CLAMP
                )
            }
            canvas.drawRect(bounds, spillPaint)
        }

        if (drawInnerShadow && shadowStrength > 0.01f) {
            val s = shadowStrength.coerceIn(0f, 1f)
            val depth = min(bounds.width(), bounds.height()) * 0.12f
            ambientOcclusion(canvas, bounds, depth, s * 0.75f)
            val rimShadow = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = max(4f, depth * 0.35f)
                color = PostTheme.alpha(Color.BLACK, 0.42f * s)
            }
            canvas.drawPath(clipPath, rimShadow)
        }

        canvas.restore()
    }

    fun perspectiveText(
        canvas: Canvas,
        quadPx: FloatArray,
        sourceWidth: Int,
        sourceHeight: Int,
        drawBlock: (Canvas, RectF) -> Unit
    ) {
        if (quadPx.size < 8 || sourceWidth <= 4 || sourceHeight <= 4) return
        val w = sourceWidth.coerceIn(32, 1600)
        val h = sourceHeight.coerceIn(32, 1600)
        val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        try {
            val offCanvas = Canvas(bmp)
            drawBlock(offCanvas, RectF(0f, 0f, w.toFloat(), h.toFloat()))
            val src = floatArrayOf(
                0f, 0f,
                w.toFloat(), 0f,
                w.toFloat(), h.toFloat(),
                0f, h.toFloat()
            )
            val matrix = Matrix()
            if (matrix.setPolyToPoly(src, 0, quadPx, 0, 4)) {
                val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
                canvas.drawBitmap(bmp, matrix, paint)
            }
        } finally {
            bmp.recycle()
        }
    }
}

enum class StudentPose {
    CLIMB,
    CANNONBALL,
    WALK,
    SIT,
    HANG,
    FLOAT
}

object PostScenery {
    fun drawStudent(
        canvas: Canvas,
        rect: RectF,
        pose: StudentPose,
        hoodieColor: Int,
        pantsColor: Int = Color.rgb(32, 42, 62),
        packColor: Int = Color.rgb(235, 168, 52),
        lightAngleDeg: Float = 135f,
        shadowStrength: Float = 0.65f
    ) {
        val cx = rect.centerX()
        val cy = rect.centerY()
        val u = min(rect.width(), rect.height())
        if (u <= 4f) return

        // Ground or air shadow
        if (pose == StudentPose.WALK || pose == StudentPose.SIT || pose == StudentPose.CLIMB) {
            PostDepth.contactShadow(
                canvas = canvas,
                cx = cx,
                cy = rect.bottom - u * 0.05f,
                rx = u * 0.28f,
                ry = u * 0.08f,
                strength = shadowStrength
            )
        }

        val skinBase = Color.rgb(214, 158, 118)
        val skinLight = PostTheme.lighten(skinBase, 0.18f)
        val hairColor = Color.rgb(26, 24, 30)

        fun limbPaint(color: Int, width: Float): Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = width
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
            this.color = color
        }

        canvas.save()
        when (pose) {
            StudentPose.CANNONBALL -> canvas.rotate(-18f, cx, cy)
            StudentPose.FLOAT -> canvas.rotate(14f, cx, cy)
            else -> Unit
        }

        val torsoRect = RectF(
            cx - u * 0.16f,
            cy - u * 0.12f,
            cx + u * 0.16f,
            cy + u * 0.20f
        )
        val packRect = RectF(
            torsoRect.left - u * 0.11f,
            torsoRect.top + u * 0.03f,
            torsoRect.left + u * 0.05f,
            torsoRect.bottom - u * 0.02f
        )

        // Backpack
        PostDepth.bevel(canvas, packRect, u * 0.06f, u * 0.03f, packColor, lightAngleDeg)

        // Legs
        val legStroke = limbPaint(pantsColor, u * 0.095f)
        val shoePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE }
        when (pose) {
            StudentPose.WALK -> {
                canvas.drawLine(cx - u * 0.06f, torsoRect.bottom, cx - u * 0.16f, rect.bottom - u * 0.08f, legStroke)
                canvas.drawLine(cx + u * 0.06f, torsoRect.bottom, cx + u * 0.18f, rect.bottom - u * 0.08f, legStroke)
                canvas.drawRoundRect(RectF(cx - u * 0.22f, rect.bottom - u * 0.11f, cx - u * 0.10f, rect.bottom - u * 0.04f), u * 0.03f, u * 0.03f, shoePaint)
                canvas.drawRoundRect(RectF(cx + u * 0.13f, rect.bottom - u * 0.11f, cx + u * 0.25f, rect.bottom - u * 0.04f), u * 0.03f, u * 0.03f, shoePaint)
            }
            StudentPose.SIT -> {
                canvas.drawLine(cx - u * 0.06f, torsoRect.bottom - u * 0.02f, cx + u * 0.18f, torsoRect.bottom + u * 0.04f, legStroke)
                canvas.drawLine(cx + u * 0.18f, torsoRect.bottom + u * 0.04f, cx + u * 0.20f, rect.bottom - u * 0.08f, legStroke)
                canvas.drawRoundRect(RectF(cx + u * 0.15f, rect.bottom - u * 0.11f, cx + u * 0.28f, rect.bottom - u * 0.04f), u * 0.03f, u * 0.03f, shoePaint)
            }
            StudentPose.CANNONBALL -> {
                canvas.drawLine(cx - u * 0.06f, torsoRect.bottom, cx - u * 0.18f, cy + u * 0.06f, legStroke)
                canvas.drawLine(cx + u * 0.06f, torsoRect.bottom, cx + u * 0.18f, cy + u * 0.08f, legStroke)
            }
            StudentPose.HANG -> {
                canvas.drawLine(cx - u * 0.06f, torsoRect.bottom, cx - u * 0.10f, rect.bottom - u * 0.06f, legStroke)
                canvas.drawLine(cx + u * 0.06f, torsoRect.bottom, cx + u * 0.04f, rect.bottom - u * 0.12f, legStroke)
            }
            StudentPose.CLIMB -> {
                canvas.drawLine(cx - u * 0.06f, torsoRect.bottom, cx - u * 0.16f, rect.bottom - u * 0.06f, legStroke)
                canvas.drawLine(cx + u * 0.06f, torsoRect.bottom, cx + u * 0.14f, torsoRect.bottom + u * 0.12f, legStroke)
            }
            StudentPose.FLOAT -> {
                canvas.drawLine(cx - u * 0.06f, torsoRect.bottom, cx - u * 0.18f, rect.bottom - u * 0.08f, legStroke)
                canvas.drawLine(cx + u * 0.06f, torsoRect.bottom, cx + u * 0.14f, rect.bottom - u * 0.12f, legStroke)
            }
        }

        // Hoodie torso
        PostDepth.bevel(canvas, torsoRect, u * 0.10f, u * 0.035f, hoodieColor, lightAngleDeg)

        // Arms
        val armStroke = limbPaint(PostTheme.lighten(hoodieColor, 0.06f), u * 0.082f)
        when (pose) {
            StudentPose.HANG -> {
                canvas.drawLine(torsoRect.left + u * 0.03f, torsoRect.top + u * 0.04f, cx - u * 0.10f, rect.top + u * 0.05f, armStroke)
                canvas.drawLine(torsoRect.right - u * 0.03f, torsoRect.top + u * 0.04f, cx + u * 0.10f, rect.top + u * 0.05f, armStroke)
            }
            StudentPose.CLIMB -> {
                canvas.drawLine(torsoRect.left + u * 0.02f, torsoRect.top + u * 0.04f, cx - u * 0.20f, torsoRect.top - u * 0.12f, armStroke)
                canvas.drawLine(torsoRect.right - u * 0.02f, torsoRect.top + u * 0.04f, cx + u * 0.22f, torsoRect.top - u * 0.04f, armStroke)
            }
            StudentPose.FLOAT -> {
                canvas.drawLine(torsoRect.left + u * 0.02f, torsoRect.top + u * 0.05f, cx - u * 0.26f, torsoRect.top - u * 0.08f, armStroke)
                canvas.drawLine(torsoRect.right - u * 0.02f, torsoRect.top + u * 0.05f, cx + u * 0.26f, torsoRect.top - u * 0.04f, armStroke)
            }
            else -> {
                canvas.drawLine(torsoRect.left + u * 0.02f, torsoRect.top + u * 0.05f, cx - u * 0.22f, torsoRect.bottom - u * 0.04f, armStroke)
                canvas.drawLine(torsoRect.right - u * 0.02f, torsoRect.top + u * 0.05f, cx + u * 0.22f, torsoRect.bottom - u * 0.06f, armStroke)
            }
        }

        // Head (clay sphere, faceless)
        val headR = u * 0.135f
        val headCy = torsoRect.top - headR * 0.72f
        val headPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(
                cx - headR * 0.3f,
                headCy - headR * 0.3f,
                headR * 1.35f,
                skinLight,
                PostTheme.darken(skinBase, 0.22f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawCircle(cx, headCy, headR, headPaint)

        // Hair cap
        val hairRect = RectF(cx - headR * 1.02f, headCy - headR * 1.08f, cx + headR * 1.02f, headCy + headR * 0.3f)
        canvas.drawArc(hairRect, 185f, 170f, true, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = hairColor })

        canvas.restore()
    }

    // Custom clip paths for Scene photo areas
    val escKeyWellClip: (RectF) -> Path = { r ->
        Path().apply {
            val insetX = r.width() * 0.08f
            val insetTop = r.height() * 0.10f
            val insetBottom = r.height() * 0.05f
            moveTo(r.left + insetX, r.top + insetTop)
            lineTo(r.right - insetX, r.top + insetTop)
            lineTo(r.right - insetX * 0.35f, r.bottom - insetBottom)
            lineTo(r.left + insetX * 0.35f, r.bottom - insetBottom)
            close()
        }
    }

    val bookPoolClip: (RectF) -> Path = { r ->
        Path().apply {
            val cr = min(r.width(), r.height()) * 0.08f
            addRoundRect(r, cr, cr, Path.Direction.CW)
        }
    }

    val houseDoorClip: (RectF) -> Path = { r ->
        Path().apply {
            moveTo(r.left + r.width() * 0.14f, r.top + r.height() * 0.06f)
            lineTo(r.right - r.width() * 0.08f, r.top)
            lineTo(r.right - r.width() * 0.08f, r.bottom)
            lineTo(r.left + r.width() * 0.14f, r.bottom - r.height() * 0.06f)
            close()
        }
    }

    val faceSilhouetteClip: (RectF) -> Path = { r ->
        Path().apply {
            val w = r.width()
            val h = r.height()
            moveTo(r.left + w * 0.25f, r.top + h * 0.05f)
            cubicTo(
                r.left + w * 0.72f, r.top + h * 0.02f,
                r.left + w * 0.85f, r.top + h * 0.24f,
                r.left + w * 0.78f, r.top + h * 0.40f
            )
            // Nose profile
            lineTo(r.left + w * 0.92f, r.top + h * 0.52f)
            lineTo(r.left + w * 0.78f, r.top + h * 0.58f)
            // Lips & chin
            cubicTo(
                r.left + w * 0.82f, r.top + h * 0.68f,
                r.left + w * 0.76f, r.top + h * 0.84f,
                r.left + w * 0.52f, r.top + h * 0.92f
            )
            lineTo(r.left + w * 0.20f, r.top + h * 0.95f)
            cubicTo(
                r.left + w * 0.06f, r.top + h * 0.70f,
                r.left + w * 0.06f, r.top + h * 0.25f,
                r.left + w * 0.25f, r.top + h * 0.05f
            )
            close()
        }
    }

    val backpackPortalClip: (RectF) -> Path = { r ->
        Path().apply {
            val w = r.width()
            val h = r.height()
            moveTo(r.left + w * 0.18f, r.top + h * 0.08f)
            quadTo(r.centerX(), r.top - h * 0.04f, r.right - w * 0.18f, r.top + h * 0.08f)
            quadTo(r.right + w * 0.04f, r.centerY(), r.right - w * 0.12f, r.bottom - h * 0.06f)
            quadTo(r.centerX(), r.bottom + h * 0.02f, r.left + w * 0.12f, r.bottom - h * 0.06f)
            quadTo(r.left - w * 0.04f, r.centerY(), r.left + w * 0.18f, r.top + h * 0.08f)
            close()
        }
    }

    val ufoBeamClip: (RectF) -> Path = { r ->
        Path().apply {
            moveTo(r.left + r.width() * 0.30f, r.top)
            lineTo(r.right - r.width() * 0.30f, r.top)
            lineTo(r.right, r.bottom)
            lineTo(r.left, r.bottom)
            close()
        }
    }

    fun drawSceneDecor(
        canvas: Canvas,
        shape: DecorShape,
        rect: RectF,
        layer: DecorLayer,
        accentColor: Int,
        secondaryColor: Int,
        inkColor: Int,
        doc: PostDoc
    ) {
        val lightAngle = doc.sceneLightAngle
        val shadowStrength = doc.sceneShadowStrength * (0.6f + doc.sceneDepth * 0.5f)
        val depthScale = doc.sceneDepth.coerceIn(0.2f, 1.5f)

        when (shape) {
            DecorShape.SCENE_ESCKEY -> drawEscKeyScene(canvas, rect, layer, accentColor, secondaryColor, lightAngle, shadowStrength, depthScale)
            DecorShape.SCENE_BOOKPOOL -> drawBookPoolScene(canvas, rect, layer, accentColor, secondaryColor, lightAngle, shadowStrength, depthScale)
            DecorShape.SCENE_HOUSEDOOR -> drawHouseDoorScene(canvas, rect, layer, accentColor, secondaryColor, lightAngle, shadowStrength, depthScale)
            DecorShape.SCENE_DESKFLAT -> drawDeskFlatScene(canvas, rect, layer, accentColor, secondaryColor, inkColor, lightAngle, shadowStrength, depthScale)
            DecorShape.SCENE_FACELIGHT -> drawFaceLightScene(canvas, rect, layer, accentColor, secondaryColor, lightAngle, shadowStrength)
            DecorShape.SCENE_PENCILCITY -> drawPencilCityScene(canvas, rect, layer, accentColor, secondaryColor, lightAngle, shadowStrength, depthScale)
            DecorShape.SCENE_BACKPACKPORTAL -> drawBackpackPortalScene(canvas, rect, layer, accentColor, secondaryColor, lightAngle, shadowStrength, depthScale)
            DecorShape.SCENE_CLOCKHANG -> drawClockHangScene(canvas, rect, layer, accentColor, secondaryColor, inkColor, lightAngle, shadowStrength, depthScale)
            DecorShape.SCENE_EXAMCLIFF -> drawExamCliffScene(canvas, rect, layer, accentColor, secondaryColor, lightAngle, shadowStrength, depthScale)
            DecorShape.SCENE_CAPUFO -> drawCapUfoScene(canvas, rect, layer, accentColor, secondaryColor, lightAngle, shadowStrength, depthScale)
            DecorShape.SCENE_PAPERBOAT -> drawPaperBoatScene(canvas, rect, layer, accentColor, secondaryColor, lightAngle, shadowStrength, depthScale)
            else -> Unit
        }
    }

    private fun drawEscKeyScene(
        canvas: Canvas,
        rect: RectF,
        layer: DecorLayer,
        accent: Int,
        secondary: Int,
        lightAngle: Float,
        shadowStrength: Float,
        depthScale: Float
    ) {
        val u = min(rect.width(), rect.height())
        if (layer == DecorLayer.BEHIND_PHOTO) {
            PostDepth.contactShadow(canvas, rect.centerX(), rect.bottom - u * 0.08f, rect.width() * 0.44f, u * 0.12f, shadowStrength)
            val baseWell = RectF(
                rect.left + rect.width() * 0.08f,
                rect.top + rect.height() * 0.30f,
                rect.right - rect.width() * 0.08f,
                rect.bottom - rect.height() * 0.08f
            )
            PostDepth.bevel(canvas, baseWell, u * 0.07f, u * 0.06f * depthScale, Color.rgb(24, 30, 44), lightAngle)
            // Glowing switch housing
            val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = RadialGradient(
                    baseWell.centerX(),
                    baseWell.centerY(),
                    baseWell.width() * 0.55f,
                    PostTheme.alpha(accent, 0.52f),
                    Color.TRANSPARENT,
                    Shader.TileMode.CLAMP
                )
            }
            canvas.drawRoundRect(baseWell, u * 0.06f, u * 0.06f, glowPaint)
        } else {
            // Lifted ESC keycap hovering above and student climbing out
            val capRect = RectF(
                rect.left + rect.width() * 0.10f,
                rect.top + rect.height() * 0.03f,
                rect.right - rect.width() * 0.14f,
                rect.top + rect.height() * 0.34f
            )
            canvas.save()
            canvas.rotate(-11f, capRect.centerX(), capRect.centerY())
            PostDepth.bevel(canvas, capRect, u * 0.06f, u * 0.055f * depthScale, PostTheme.lighten(secondary, 0.15f), lightAngle)
            PostDepth.specularStrip(canvas, capRect, u * 0.06f, lightAngle, 0.22f)
            canvas.restore()

            val studentRect = RectF(
                rect.centerX() - u * 0.22f,
                rect.bottom - u * 0.46f,
                rect.centerX() + u * 0.22f,
                rect.bottom - u * 0.04f
            )
            drawStudent(canvas, studentRect, StudentPose.CLIMB, accent, packColor = secondary, lightAngleDeg = lightAngle, shadowStrength = shadowStrength)
        }
    }

    private fun drawBookPoolScene(
        canvas: Canvas,
        rect: RectF,
        layer: DecorLayer,
        accent: Int,
        secondary: Int,
        lightAngle: Float,
        shadowStrength: Float,
        depthScale: Float
    ) {
        val u = min(rect.width(), rect.height())
        if (layer == DecorLayer.BEHIND_PHOTO) {
            PostDepth.contactShadow(canvas, rect.centerX(), rect.bottom - u * 0.06f, rect.width() * 0.46f, u * 0.11f, shadowStrength)
            // Thick open hardcover book base
            val coverRect = RectF(rect.left + u * 0.03f, rect.top + u * 0.14f, rect.right - u * 0.03f, rect.bottom - u * 0.06f)
            PostDepth.bevel(canvas, coverRect, u * 0.04f, u * 0.065f * depthScale, secondary, lightAngle)
            val pagesRect = RectF(coverRect.left + u * 0.03f, coverRect.top + u * 0.025f, coverRect.right - u * 0.03f, coverRect.bottom - u * 0.03f)
            PostDepth.bevel(canvas, pagesRect, u * 0.03f, u * 0.04f * depthScale, Color.rgb(245, 240, 228), lightAngle)
        } else {
            // Water ripples + diving board + jumping student
            val poolCenterX = rect.centerX() + rect.width() * 0.14f
            val poolCenterY = rect.centerY() + rect.height() * 0.06f
            val ripplePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = max(2f, u * 0.008f)
                color = PostTheme.alpha(Color.WHITE, 0.55f)
            }
            for (i in 1..3) {
                val rx = u * (0.08f * i)
                val ry = rx * 0.52f
                canvas.drawOval(RectF(poolCenterX - rx, poolCenterY - ry, poolCenterX + rx, poolCenterY + ry), ripplePaint)
            }

            val studentRect = RectF(
                poolCenterX - u * 0.18f,
                rect.top + u * 0.02f,
                poolCenterX + u * 0.18f,
                rect.top + u * 0.36f
            )
            drawStudent(canvas, studentRect, StudentPose.CANNONBALL, accent, packColor = secondary, lightAngleDeg = lightAngle, shadowStrength = shadowStrength)
        }
    }

    private fun drawHouseDoorScene(
        canvas: Canvas,
        rect: RectF,
        layer: DecorLayer,
        accent: Int,
        secondary: Int,
        lightAngle: Float,
        shadowStrength: Float,
        depthScale: Float
    ) {
        val u = min(rect.width(), rect.height())
        if (layer == DecorLayer.BEHIND_PHOTO) {
            PostDepth.contactShadow(canvas, rect.centerX(), rect.bottom - u * 0.05f, rect.width() * 0.42f, u * 0.10f, shadowStrength)
            val wallRect = RectF(rect.left + rect.width() * 0.10f, rect.top + rect.height() * 0.08f, rect.right - rect.width() * 0.10f, rect.bottom - rect.height() * 0.08f)
            PostDepth.bevel(canvas, wallRect, u * 0.03f, u * 0.05f * depthScale, secondary, lightAngle)
        } else {
            // Swung-open door slab + warm floor beam + student silhouette
            val doorLeaf = Path().apply {
                moveTo(rect.left + rect.width() * 0.18f, rect.top + rect.height() * 0.10f)
                lineTo(rect.left + rect.width() * 0.38f, rect.top + rect.height() * 0.04f)
                lineTo(rect.left + rect.width() * 0.38f, rect.bottom - rect.height() * 0.04f)
                lineTo(rect.left + rect.width() * 0.18f, rect.bottom - rect.height() * 0.10f)
                close()
            }
            PostDepth.castShadow(canvas, doorLeaf, lightAngle, u * 0.05f * depthScale, shadowStrength)
            canvas.drawPath(
                doorLeaf,
                Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    style = Paint.Style.FILL
                    shader = LinearGradient(
                        rect.left,
                        rect.top,
                        rect.centerX(),
                        rect.bottom,
                        PostTheme.lighten(accent, 0.10f),
                        PostTheme.darken(accent, 0.28f),
                        Shader.TileMode.CLAMP
                    )
                }
            )
            // Light beam on floor
            val beam = Path().apply {
                moveTo(rect.left + rect.width() * 0.34f, rect.bottom - rect.height() * 0.16f)
                lineTo(rect.right - rect.width() * 0.16f, rect.bottom - rect.height() * 0.16f)
                lineTo(rect.right - rect.width() * 0.04f, rect.bottom)
                lineTo(rect.left + rect.width() * 0.18f, rect.bottom)
                close()
            }
            canvas.drawPath(
                beam,
                Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    shader = LinearGradient(
                        rect.centerX(),
                        rect.bottom - rect.height() * 0.18f,
                        rect.centerX(),
                        rect.bottom,
                        PostTheme.alpha(accent, 0.42f),
                        Color.TRANSPARENT,
                        Shader.TileMode.CLAMP
                    )
                }
            )

            val studentRect = RectF(
                rect.centerX() - u * 0.14f,
                rect.bottom - u * 0.44f,
                rect.centerX() + u * 0.18f,
                rect.bottom - u * 0.06f
            )
            drawStudent(canvas, studentRect, StudentPose.WALK, accent, packColor = secondary, lightAngleDeg = lightAngle, shadowStrength = shadowStrength)
        }
    }

    private fun drawDeskFlatScene(
        canvas: Canvas,
        rect: RectF,
        layer: DecorLayer,
        accent: Int,
        secondary: Int,
        ink: Int,
        lightAngle: Float,
        shadowStrength: Float,
        depthScale: Float
    ) {
        val u = min(rect.width(), rect.height())
        if (layer == DecorLayer.BEHIND_PHOTO) {
            // Wood grain bands
            val grainPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = max(1.5f, u * 0.004f)
                color = PostTheme.alpha(ink, 0.07f)
            }
            for (i in 1..7) {
                val y = rect.top + rect.height() * (i / 8f)
                canvas.drawLine(rect.left, y, rect.right, y, grainPaint)
            }
            // Notebook under photos
            val notebook = RectF(rect.left + u * 0.06f, rect.top + u * 0.08f, rect.right - u * 0.06f, rect.bottom - u * 0.08f)
            PostDepth.bevel(canvas, notebook, u * 0.03f, u * 0.03f * depthScale, Color.rgb(248, 244, 234), lightAngle)
        } else {
            // Coffee mug top-right
            val mugCx = rect.right - u * 0.16f
            val mugCy = rect.top + u * 0.16f
            val mugR = u * 0.095f
            PostDepth.contactShadow(canvas, mugCx + u * 0.02f, mugCy + u * 0.03f, mugR * 1.15f, mugR * 1.05f, shadowStrength)
            canvas.drawCircle(mugCx, mugCy, mugR, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE })
            canvas.drawCircle(mugCx, mugCy, mugR * 0.76f, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(74, 44, 26) })

            // Highlighter pen bottom-left
            val penRect = RectF(rect.left + u * 0.08f, rect.bottom - u * 0.14f, rect.left + u * 0.46f, rect.bottom - u * 0.09f)
            canvas.save()
            canvas.rotate(-14f, penRect.centerX(), penRect.centerY())
            PostDepth.bevel(canvas, penRect, u * 0.02f, u * 0.015f * depthScale, accent, lightAngle)
            canvas.restore()

            // Sticky note
            val stickyRect = RectF(rect.right - u * 0.34f, rect.bottom - u * 0.32f, rect.right - u * 0.08f, rect.bottom - u * 0.08f)
            PostDepth.bevel(canvas, stickyRect, u * 0.015f, u * 0.018f * depthScale, PostTheme.lighten(accent, 0.25f), lightAngle)
            val curlBox = RectF(stickyRect.right - u * 0.07f, stickyRect.bottom - u * 0.07f, stickyRect.right, stickyRect.bottom)
            PostDepth.paperCurl(canvas, curlBox, PostTheme.lighten(accent, 0.25f), secondary, accent)
        }
    }

    private fun drawFaceLightScene(
        canvas: Canvas,
        rect: RectF,
        layer: DecorLayer,
        accent: Int,
        secondary: Int,
        lightAngle: Float,
        shadowStrength: Float
    ) {
        val u = min(rect.width(), rect.height())
        if (layer == DecorLayer.BEHIND_PHOTO) {
            val halo = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = RadialGradient(
                    rect.centerX(),
                    rect.centerY(),
                    u * 0.56f,
                    PostTheme.alpha(accent, 0.38f),
                    Color.TRANSPARENT,
                    Shader.TileMode.CLAMP
                )
            }
            canvas.drawCircle(rect.centerX(), rect.centerY(), u * 0.56f, halo)
        } else {
            val profilePath = faceSilhouetteClip(rect)
            PostDepth.rimLight(canvas, profilePath, rect, lightAngle, accent, max(3f, u * 0.010f), 0.85f)
            // Floating light motes around forehead/mind
            val motePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = PostTheme.alpha(PostTheme.lighten(accent, 0.35f), 0.78f) }
            val spots = arrayOf(
                0.78f to 0.18f,
                0.86f to 0.28f,
                0.72f to 0.10f,
                0.88f to 0.42f
            )
            for ((sx, sy) in spots) {
                canvas.drawCircle(rect.left + rect.width() * sx, rect.top + rect.height() * sy, u * 0.014f, motePaint)
            }
        }
    }

    private fun drawPencilCityScene(
        canvas: Canvas,
        rect: RectF,
        layer: DecorLayer,
        accent: Int,
        secondary: Int,
        lightAngle: Float,
        shadowStrength: Float,
        depthScale: Float
    ) {
        val u = min(rect.width(), rect.height())
        val towers = arrayOf(
            Triple(0.08f, 0.38f, 0.13f),
            Triple(0.23f, 0.24f, 0.14f),
            Triple(0.40f, 0.44f, 0.13f),
            Triple(0.72f, 0.30f, 0.15f)
        )
        if (layer == DecorLayer.BEHIND_PHOTO) {
            for ((i, t) in towers.withIndex()) {
                val tx = rect.left + rect.width() * t.first
                val ty = rect.top + rect.height() * t.second
                val tw = rect.width() * t.third
                val tb = rect.bottom - rect.height() * 0.06f
                val bodyColor = if (i % 2 == 0) accent else secondary
                val bodyRect = RectF(tx, ty + tw * 0.7f, tx + tw, tb)
                PostDepth.bevel(canvas, bodyRect, u * 0.015f, u * 0.025f * depthScale, bodyColor, lightAngle)

                // Wood cone + graphite tip
                val cone = Path().apply {
                    moveTo(tx, ty + tw * 0.7f)
                    lineTo(tx + tw * 0.5f, ty)
                    lineTo(tx + tw, ty + tw * 0.7f)
                    close()
                }
                canvas.drawPath(cone, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(232, 198, 152) })
                val tip = Path().apply {
                    moveTo(tx + tw * 0.34f, ty + tw * 0.24f)
                    lineTo(tx + tw * 0.5f, ty)
                    lineTo(tx + tw * 0.66f, ty + tw * 0.24f)
                    close()
                }
                canvas.drawPath(tip, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(38, 42, 52) })
            }
        } else {
            // Book bridge + tiny walking student
            val bridgeRect = RectF(
                rect.left + rect.width() * 0.18f,
                rect.bottom - rect.height() * 0.18f,
                rect.right - rect.width() * 0.14f,
                rect.bottom - rect.height() * 0.11f
            )
            PostDepth.bevel(canvas, bridgeRect, u * 0.02f, u * 0.025f * depthScale, PostTheme.lighten(accent, 0.12f), lightAngle)
            val studentRect = RectF(
                bridgeRect.centerX() - u * 0.10f,
                bridgeRect.top - u * 0.22f,
                bridgeRect.centerX() + u * 0.10f,
                bridgeRect.top + u * 0.02f
            )
            drawStudent(canvas, studentRect, StudentPose.WALK, accent, packColor = secondary, lightAngleDeg = lightAngle, shadowStrength = shadowStrength)
        }
    }

    private fun drawBackpackPortalScene(
        canvas: Canvas,
        rect: RectF,
        layer: DecorLayer,
        accent: Int,
        secondary: Int,
        lightAngle: Float,
        shadowStrength: Float,
        depthScale: Float
    ) {
        val u = min(rect.width(), rect.height())
        if (layer == DecorLayer.BEHIND_PHOTO) {
            PostDepth.contactShadow(canvas, rect.centerX(), rect.bottom - u * 0.05f, rect.width() * 0.42f, u * 0.10f, shadowStrength)
            val packBody = RectF(
                rect.left + rect.width() * 0.08f,
                rect.top + rect.height() * 0.08f,
                rect.right - rect.width() * 0.08f,
                rect.bottom - rect.height() * 0.06f
            )
            PostDepth.bevel(canvas, packBody, u * 0.14f, u * 0.06f * depthScale, secondary, lightAngle)
        } else {
            // Zipper teeth rim around portal
            val portalPath = backpackPortalClip(rect)
            PostDepth.rimLight(canvas, portalPath, rect, lightAngle, accent, max(4f, u * 0.015f), 0.90f)
            val dashPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = max(3f, u * 0.012f)
                color = PostTheme.lighten(accent, 0.32f)
                pathEffect = android.graphics.DashPathEffect(floatArrayOf(u * 0.025f, u * 0.018f), 0f)
            }
            canvas.drawPath(portalPath, dashPaint)
        }
    }

    private fun drawClockHangScene(
        canvas: Canvas,
        rect: RectF,
        layer: DecorLayer,
        accent: Int,
        secondary: Int,
        ink: Int,
        lightAngle: Float,
        shadowStrength: Float,
        depthScale: Float
    ) {
        val u = min(rect.width(), rect.height())
        val cx = rect.centerX()
        val cy = rect.top + rect.height() * 0.42f
        val r = u * 0.34f
        if (layer == DecorLayer.BEHIND_PHOTO) {
            PostDepth.contactShadow(canvas, cx, rect.bottom - u * 0.04f, r * 0.95f, u * 0.09f, shadowStrength)
            val rimBox = RectF(cx - r, cy - r, cx + r, cy + r)
            PostDepth.bevel(canvas, rimBox, r, u * 0.05f * depthScale, accent, lightAngle)
            canvas.drawCircle(cx, cy, r * 0.86f, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(250, 247, 238) })
            // Tick marks
            val tickPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = max(2f, u * 0.008f)
                color = PostTheme.alpha(ink, 0.65f)
            }
            for (i in 0 until 12) {
                val a = Math.toRadians((i * 30).toDouble())
                val x0 = cx + cos(a).toFloat() * r * 0.72f
                val y0 = cy + sin(a).toFloat() * r * 0.72f
                val x1 = cx + cos(a).toFloat() * r * 0.82f
                val y1 = cy + sin(a).toFloat() * r * 0.82f
                canvas.drawLine(x0, y0, x1, y1, tickPaint)
            }
        } else {
            // Clock hands + hanging student
            val handPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = max(5f, u * 0.022f)
                strokeCap = Paint.Cap.ROUND
                color = Color.rgb(28, 34, 48)
            }
            val minuteEndX = cx + r * 0.56f
            val minuteEndY = cy + r * 0.24f
            canvas.drawLine(cx, cy, cx - r * 0.28f, cy - r * 0.34f, handPaint)
            canvas.drawLine(cx, cy, minuteEndX, minuteEndY, handPaint)
            canvas.drawCircle(cx, cy, u * 0.03f, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = accent })

            val studentRect = RectF(
                minuteEndX - u * 0.14f,
                minuteEndY - u * 0.02f,
                minuteEndX + u * 0.14f,
                minuteEndY + u * 0.34f
            )
            drawStudent(canvas, studentRect, StudentPose.HANG, accent, packColor = secondary, lightAngleDeg = lightAngle, shadowStrength = shadowStrength)
        }
    }

    private fun drawExamCliffScene(
        canvas: Canvas,
        rect: RectF,
        layer: DecorLayer,
        accent: Int,
        secondary: Int,
        lightAngle: Float,
        shadowStrength: Float,
        depthScale: Float
    ) {
        val u = min(rect.width(), rect.height())
        if (layer == DecorLayer.BEHIND_PHOTO) {
            // Stacked paper cliffs left and right
            val leftCliff = RectF(rect.left, rect.centerY() - u * 0.04f, rect.left + rect.width() * 0.38f, rect.bottom)
            val rightCliff = RectF(rect.right - rect.width() * 0.38f, rect.centerY() - u * 0.10f, rect.right, rect.bottom)
            for (i in 4 downTo 0) {
                val dy = i * u * 0.03f
                val lSheet = RectF(leftCliff.left, leftCliff.top + dy, leftCliff.right - i * u * 0.015f, leftCliff.bottom)
                val rSheet = RectF(rightCliff.left + i * u * 0.015f, rightCliff.top + dy, rightCliff.right, rightCliff.bottom)
                val c = if (i % 2 == 0) Color.rgb(246, 242, 232) else PostTheme.lighten(secondary, 0.55f)
                PostDepth.bevel(canvas, lSheet, u * 0.02f, u * 0.02f * depthScale, c, lightAngle)
                PostDepth.bevel(canvas, rSheet, u * 0.02f, u * 0.02f * depthScale, c, lightAngle)
            }
        } else {
            // Giant yellow #2 pencil bridge across the canyon
            val bridgeY = rect.centerY() - u * 0.06f
            val bridgeH = u * 0.075f
            val pencilBody = RectF(rect.left + rect.width() * 0.16f, bridgeY, rect.right - rect.width() * 0.18f, bridgeY + bridgeH)
            PostDepth.bevel(canvas, pencilBody, bridgeH * 0.3f, u * 0.025f * depthScale, accent, lightAngle)
            // Pink eraser on left end
            val eraser = RectF(pencilBody.left - u * 0.06f, bridgeY, pencilBody.left + u * 0.02f, bridgeY + bridgeH)
            PostDepth.bevel(canvas, eraser, bridgeH * 0.3f, u * 0.02f * depthScale, Color.rgb(238, 128, 148), lightAngle)

            val studentRect = RectF(
                rect.centerX() - u * 0.13f,
                bridgeY - u * 0.28f,
                rect.centerX() + u * 0.13f,
                bridgeY + u * 0.02f
            )
            drawStudent(canvas, studentRect, StudentPose.WALK, secondary, packColor = accent, lightAngleDeg = lightAngle, shadowStrength = shadowStrength)
        }
    }

    private fun drawCapUfoScene(
        canvas: Canvas,
        rect: RectF,
        layer: DecorLayer,
        accent: Int,
        secondary: Int,
        lightAngle: Float,
        shadowStrength: Float,
        depthScale: Float
    ) {
        val u = min(rect.width(), rect.height())
        val cx = rect.centerX()
        if (layer == DecorLayer.BEHIND_PHOTO) {
            // Glowing cone beam behind/around photo
            val beamPath = Path().apply {
                moveTo(cx - rect.width() * 0.16f, rect.top + rect.height() * 0.22f)
                lineTo(cx + rect.width() * 0.16f, rect.top + rect.height() * 0.22f)
                lineTo(rect.right - rect.width() * 0.08f, rect.bottom - rect.height() * 0.06f)
                lineTo(rect.left + rect.width() * 0.08f, rect.bottom - rect.height() * 0.06f)
                close()
            }
            val beamPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = LinearGradient(
                    cx,
                    rect.top + rect.height() * 0.22f,
                    cx,
                    rect.bottom,
                    PostTheme.alpha(accent, 0.55f),
                    Color.TRANSPARENT,
                    Shader.TileMode.CLAMP
                )
            }
            canvas.drawPath(beamPath, beamPaint)
        } else {
            // Giant mortarboard graduation cap hovering at top
            val capCy = rect.top + rect.height() * 0.17f
            val capW = rect.width() * 0.42f
            val capH = u * 0.11f
            val diamond = Path().apply {
                moveTo(cx, capCy - capH)
                lineTo(cx + capW, capCy)
                lineTo(cx, capCy + capH)
                lineTo(cx - capW, capCy)
                close()
            }
            PostDepth.castShadow(canvas, diamond, lightAngle, u * 0.04f * depthScale, shadowStrength)
            canvas.drawPath(diamond, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = PostTheme.darken(secondary, 0.25f) })
            PostDepth.rimLight(canvas, diamond, RectF(cx - capW, capCy - capH, cx + capW, capCy + capH), lightAngle, accent, 3f, 0.8f)

            // Gold tassel
            val tasselPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = max(3f, u * 0.012f)
                color = accent
            }
            canvas.drawLine(cx, capCy, cx + capW * 0.55f, capCy + capH * 1.35f, tasselPaint)

            // Floating student in beam
            val studentRect = RectF(
                cx - u * 0.15f,
                rect.bottom - u * 0.42f,
                cx + u * 0.15f,
                rect.bottom - u * 0.08f
            )
            drawStudent(canvas, studentRect, StudentPose.FLOAT, accent, packColor = secondary, lightAngleDeg = lightAngle, shadowStrength = shadowStrength)
        }
    }

    private fun drawPaperBoatScene(
        canvas: Canvas,
        rect: RectF,
        layer: DecorLayer,
        accent: Int,
        secondary: Int,
        lightAngle: Float,
        shadowStrength: Float,
        depthScale: Float
    ) {
        val u = min(rect.width(), rect.height())
        val cx = rect.centerX()
        val boatBottom = rect.bottom - rect.height() * 0.16f
        val boatTop = boatBottom - u * 0.24f
        val boatHalfW = rect.width() * 0.34f

        if (layer == DecorLayer.BEHIND_PHOTO) {
            PostDepth.contactShadow(canvas, cx, boatBottom + u * 0.03f, boatHalfW * 0.95f, u * 0.08f, shadowStrength)
            // Back sail peak
            val sail = Path().apply {
                moveTo(cx - boatHalfW * 0.45f, boatTop + u * 0.06f)
                lineTo(cx, boatTop - u * 0.16f)
                lineTo(cx + boatHalfW * 0.45f, boatTop + u * 0.06f)
                close()
            }
            canvas.drawPath(sail, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(238, 234, 224) })
        } else {
            // Origami paper boat hull
            val leftFacet = Path().apply {
                moveTo(cx - boatHalfW, boatTop)
                lineTo(cx, boatTop + u * 0.05f)
                lineTo(cx, boatBottom)
                lineTo(cx - boatHalfW * 0.68f, boatBottom)
                close()
            }
            val rightFacet = Path().apply {
                moveTo(cx + boatHalfW, boatTop)
                lineTo(cx, boatTop + u * 0.05f)
                lineTo(cx, boatBottom)
                lineTo(cx + boatHalfW * 0.68f, boatBottom)
                close()
            }
            canvas.drawPath(leftFacet, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(250, 248, 242) })
            canvas.drawPath(rightFacet, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(226, 220, 208) })

            val studentRect = RectF(
                cx - u * 0.14f,
                boatTop - u * 0.22f,
                cx + u * 0.14f,
                boatTop + u * 0.06f
            )
            drawStudent(canvas, studentRect, StudentPose.SIT, accent, packColor = secondary, lightAngleDeg = lightAngle, shadowStrength = shadowStrength)
        }
    }
}
