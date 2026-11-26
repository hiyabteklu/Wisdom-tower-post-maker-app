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
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import android.text.TextPaint
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * 3D Educational, Study Material & EdTech Mockups (Variants 6..33 of DecorShape.DEVICE_3D).
 * Built specifically for promoting an educational mobile app, study materials, exam prep,
 * textbooks, notebooks, flashcards, and student milestones across 9:16, 4:3, and 16:9.
 */
object PostEduMockups {

    private data class EduSlot(val bitmap: Bitmap?, val slotIndex: Int)

    fun draw(
        context: Context,
        canvas: Canvas,
        box: DecorBox,
        rect: RectF,
        doc: PostDoc,
        primaryPhoto: Bitmap?,
        secondaryPhoto: Bitmap?,
        tertiaryPhoto: Bitmap?,
        quaternaryPhoto: Bitmap?,
        accentColor: Int,
        secondaryColor: Int,
        lightColor: Int,
        darkColor: Int,
        deepColor: Int
    ) {
        when (box.variant) {
            6 -> drawOpenTextbook3D(
                context, canvas, rect, doc,
                primaryPhoto, secondaryPhoto,
                accentColor, secondaryColor, lightColor, darkColor, deepColor
            )
            7 -> drawSpiralNotebook45(
                context, canvas, rect, doc,
                primaryPhoto, secondaryPhoto, tertiaryPhoto, quaternaryPhoto,
                accentColor, secondaryColor, lightColor, darkColor, deepColor
            )
            8 -> drawFlashcardDeck3D(
                context, canvas, rect, doc,
                primaryPhoto, secondaryPhoto, tertiaryPhoto, quaternaryPhoto,
                accentColor, secondaryColor, lightColor, darkColor, deepColor
            )
            9 -> drawExamClipboard45(
                context, canvas, rect, doc,
                primaryPhoto, secondaryPhoto, tertiaryPhoto, quaternaryPhoto,
                accentColor, secondaryColor, lightColor, darkColor, deepColor
            )
            10 -> drawSubjectBinder45(
                context, canvas, rect, doc,
                primaryPhoto, secondaryPhoto, tertiaryPhoto, quaternaryPhoto,
                accentColor, secondaryColor, lightColor, darkColor, deepColor
            )
            11 -> drawTextbookStack3D(
                context, canvas, rect, doc,
                primaryPhoto, secondaryPhoto, tertiaryPhoto,
                accentColor, secondaryColor, lightColor, darkColor, deepColor
            )
            12 -> drawExamQuizCard3D(
                context, canvas, rect, doc,
                primaryPhoto, secondaryPhoto,
                accentColor, secondaryColor, lightColor, darkColor, deepColor
            )
            13 -> drawOpticalAnswerSheet3D(
                context, canvas, rect, doc,
                primaryPhoto, secondaryPhoto,
                accentColor, secondaryColor, lightColor, darkColor, deepColor
            )
            14 -> drawGraduationCapPedestal3D(
                context, canvas, rect, doc,
                primaryPhoto, secondaryPhoto,
                accentColor, secondaryColor, lightColor, darkColor, deepColor
            )
            15 -> drawTrifoldBrochure3D(
                context, canvas, rect, doc,
                primaryPhoto, secondaryPhoto, tertiaryPhoto,
                accentColor, secondaryColor, lightColor, darkColor, deepColor
            )
            16 -> drawCourseCollectorBox3D(
                context, canvas, rect, doc,
                primaryPhoto, secondaryPhoto, tertiaryPhoto,
                accentColor, secondaryColor, lightColor, darkColor, deepColor
            )
            17 -> drawStudyStreakXpCard3D(
                context, canvas, rect, doc,
                primaryPhoto, secondaryPhoto,
                accentColor, secondaryColor, lightColor, darkColor, deepColor
            )
            18 -> drawScholarIdLanyard3D(
                context, canvas, rect, doc,
                primaryPhoto, secondaryPhoto,
                accentColor, secondaryColor, lightColor, darkColor, deepColor
            )
            19 -> drawFramedCertificateSeal3D(
                context, canvas, rect, doc,
                primaryPhoto, secondaryPhoto,
                accentColor, secondaryColor, lightColor, darkColor, deepColor
            )
            20 -> drawVideoLecturePlayer3D(
                context, canvas, rect, doc,
                primaryPhoto, secondaryPhoto,
                accentColor, secondaryColor, lightColor, darkColor, deepColor
            )
            21 -> drawTutorSpotlightCard3D(
                context, canvas, rect, doc,
                primaryPhoto, secondaryPhoto,
                accentColor, secondaryColor, lightColor, darkColor, deepColor
            )
            22 -> drawMagnifyingLensSheet3D(
                context, canvas, rect, doc,
                primaryPhoto, secondaryPhoto,
                accentColor, secondaryColor, lightColor, darkColor, deepColor
            )
            23 -> drawGlassFormulaBoard3D(
                context, canvas, rect, doc,
                primaryPhoto, secondaryPhoto,
                accentColor, secondaryColor, lightColor, darkColor, deepColor
            )
            24 -> drawRevisionPinboard3D(
                context, canvas, rect, doc,
                primaryPhoto, secondaryPhoto, tertiaryPhoto, quaternaryPhoto,
                accentColor, secondaryColor, lightColor, darkColor, deepColor
            )
            25 -> drawLaptopAndStudyNotes3D(
                context, canvas, rect, doc,
                primaryPhoto, secondaryPhoto,
                accentColor, secondaryColor, lightColor, darkColor, deepColor
            )
            26 -> drawLearningRoadmapSteps3D(
                context, canvas, rect, doc,
                primaryPhoto, secondaryPhoto, tertiaryPhoto,
                accentColor, secondaryColor, lightColor, darkColor, deepColor
            )
            27 -> drawKnowledgeCubeCard3D(
                context, canvas, rect, doc,
                primaryPhoto, secondaryPhoto,
                accentColor, secondaryColor, lightColor, darkColor, deepColor
            )
            28 -> drawBeforeAfterStudySplit3D(
                context, canvas, rect, doc,
                primaryPhoto, secondaryPhoto,
                accentColor, secondaryColor, lightColor, darkColor, deepColor
            )
            29 -> drawLeaderboardPodium3D(
                context, canvas, rect, doc,
                primaryPhoto, secondaryPhoto, tertiaryPhoto,
                accentColor, secondaryColor, lightColor, darkColor, deepColor
            )
            30 -> drawExamCountdownFlipClock3D(
                context, canvas, rect, doc,
                primaryPhoto, secondaryPhoto,
                accentColor, secondaryColor, lightColor, darkColor, deepColor
            )
            31 -> drawHolographicMasteryPoster3D(
                context, canvas, rect, doc,
                primaryPhoto, secondaryPhoto,
                accentColor, secondaryColor, lightColor, darkColor, deepColor
            )
            32 -> drawSubjectBentoGridPoster3D(
                context, canvas, rect, doc,
                primaryPhoto, secondaryPhoto, tertiaryPhoto, quaternaryPhoto,
                accentColor, secondaryColor, lightColor, darkColor, deepColor
            )
            33 -> drawAcademicJournalCoverPoster3D(
                context, canvas, rect, doc,
                primaryPhoto, secondaryPhoto, tertiaryPhoto,
                accentColor, secondaryColor, lightColor, darkColor, deepColor
            )
        }
    }

    private fun resolveActiveSlots(
        doc: PostDoc,
        maxCount: Int,
        p1: Bitmap?,
        p2: Bitmap?,
        p3: Bitmap? = null,
        p4: Bitmap? = null
    ): List<EduSlot> {
        val raw = listOf(
            EduSlot(p1?.takeIf { !it.isRecycled }, 0),
            EduSlot(p2?.takeIf { !it.isRecycled }, 1),
            EduSlot(p3?.takeIf { !it.isRecycled }, 2),
            EduSlot(p4?.takeIf { !it.isRecycled }, 3)
        ).take(maxCount)
        val loaded = raw.filter { it.bitmap != null }
        return when {
            doc.fillEmptyWithFirst && loaded.isNotEmpty() -> {
                val firstBmp = loaded.first().bitmap
                val firstIdx = loaded.first().slotIndex
                raw.map { if (it.bitmap != null) it else EduSlot(firstBmp, firstIdx) }
            }
            loaded.isNotEmpty() -> loaded
            else -> raw
        }
    }

    private fun withAlpha(color: Int, alpha: Int): Int {
        val a = alpha.coerceIn(0, 255)
        return Color.argb(a, Color.red(color), Color.green(color), Color.blue(color))
    }

    private fun blendColors(c1: Int, c2: Int, ratio: Float): Int {
        val r = ratio.coerceIn(0f, 1f)
        val ir = 1f - r
        return Color.argb(
            (Color.alpha(c1) * ir + Color.alpha(c2) * r).roundToInt(),
            (Color.red(c1) * ir + Color.red(c2) * r).roundToInt(),
            (Color.green(c1) * ir + Color.green(c2) * r).roundToInt(),
            (Color.blue(c1) * ir + Color.blue(c2) * r).roundToInt()
        )
    }

    private fun drawBitmapCenterCrop(
        canvas: Canvas,
        bmp: Bitmap,
        dst: RectF,
        zoom: Float = 1f,
        focusX: Float = 0f,
        focusY: Float = 0f
    ) {
        if (bmp.isRecycled || bmp.width <= 0 || bmp.height <= 0) return
        val bw = bmp.width.toFloat()
        val bh = bmp.height.toFloat()
        val dw = dst.width()
        val dh = dst.height()
        val baseScale = max(dw / bw, dh / bh)
        val scale = baseScale * zoom.coerceIn(1f, 3f)
        val sw = bw * scale
        val sh = bh * scale
        val maxDx = (sw - dw) * 0.5f
        val maxDy = (sh - dh) * 0.5f
        val cx = dst.centerX() - focusX.coerceIn(-1f, 1f) * maxDx
        val cy = dst.centerY() - focusY.coerceIn(-1f, 1f) * maxDy
        val target = RectF(cx - sw * 0.5f, cy - sh * 0.5f, cx + sw * 0.5f, cy + sh * 0.5f)
        canvas.drawBitmap(bmp, null, target, Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG))
    }

    private fun drawStudyPagePlaceholder(
        context: Context,
        canvas: Canvas,
        pageRect: RectF,
        slotIndex: Int,
        layerIdx: Int,
        isOverlapped: Boolean,
        subjectTag: String,
        doc: PostDoc,
        accentColor: Int,
        warmPaper: Boolean = true
    ) {
        val pw = pageRect.width()
        val ph = pageRect.height()
        val bgCol = if (warmPaper) Color.rgb(251, 249, 243) else Color.rgb(246, 248, 253)
        canvas.drawRect(pageRect, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = bgCol })

        // Subtle notebook horizontal ruled lines
        val rulePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(26, 59, 130, 246)
            strokeWidth = 1.5f
        }
        var ry = pageRect.top + ph * 0.18f
        while (ry < pageRect.bottom - ph * 0.06f) {
            canvas.drawLine(pageRect.left + pw * 0.07f, ry, pageRect.right - pw * 0.06f, ry, rulePaint)
            ry += ph * 0.055f
        }

        // Top chapter header bar
        val headerH = ph * 0.135f
        val headerCol = when (layerIdx % 4) {
            0 -> blendColors(Color.rgb(16, 78, 188), accentColor, 0.30f)
            1 -> Color.rgb(15, 38, 84)
            2 -> Color.rgb(13, 118, 110)
            else -> Color.rgb(30, 92, 208)
        }
        val headerRect = RectF(pageRect.left, pageRect.top, pageRect.right, pageRect.top + headerH)
        canvas.drawRect(
            headerRect,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = LinearGradient(
                    headerRect.left, headerRect.top, headerRect.right, headerRect.bottom,
                    intArrayOf(headerCol, PostDepth.darken(headerCol, 0.20f)),
                    null, Shader.TileMode.CLAMP
                )
            }
        )

        // Subject tag in header
        val tagTf = PostFonts.get(context, PostFont.BOLD, subjectTag, doc)
        val tagPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = tagTf
            color = Color.argb(230, 255, 255, 255)
            textSize = (headerH * 0.30f).coerceIn(13f, 24f)
        }
        canvas.drawText(
            subjectTag,
            pageRect.left + pw * 0.09f,
            pageRect.top + headerH * 0.62f,
            tagPaint
        )

        // "+ PHOTO N" pill badge in visible region
        val badgeCenterX = pageRect.left + if (isOverlapped) pw * 0.28f else pw * 0.52f
        val badgeW = if (isOverlapped) pw * 0.38f else pw * 0.52f
        val badgeH = ph * 0.056f
        val badgeTop = pageRect.top + ph * 0.175f
        val badgeRect = RectF(
            badgeCenterX - badgeW * 0.5f,
            badgeTop,
            badgeCenterX + badgeW * 0.5f,
            badgeTop + badgeH
        )
        canvas.drawRoundRect(
            badgeRect,
            badgeH * 0.5f,
            badgeH * 0.5f,
            Paint(Paint.ANTI_ALIAS_FLAG).apply { color = headerCol }
        )

        val hintLabel = "+ PHOTO ${slotIndex + 1}"
        val hintTf = PostFonts.get(context, PostFont.BOLD, hintLabel, doc)
        val hPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = hintTf
            color = Color.WHITE
            textSize = (badgeH * 0.42f).coerceIn(13f, 24f)
        }
        val hfm = hPaint.fontMetrics
        canvas.drawText(
            hintLabel,
            badgeRect.centerX() - hPaint.measureText(hintLabel) * 0.5f,
            badgeRect.centerY() - (hfm.ascent + hfm.descent) * 0.5f,
            hPaint
        )

        // Structured study summary cards below badge
        val padL = pageRect.left + pw * 0.09f
        val padR = pageRect.right - pw * 0.08f
        var curY = badgeRect.bottom + ph * 0.038f
        val inkPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(30, 41, 59) }
        val mutedPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(148, 163, 184) }

        for (i in 0 until 3) {
            val cardH = ph * 0.185f
            val cardR = RectF(padL, curY, padR, curY + cardH)
            canvas.drawRoundRect(cardR, 16f, 16f, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE })
            canvas.drawRoundRect(
                cardR, 16f, 16f,
                Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    style = Paint.Style.STROKE
                    strokeWidth = 1.6f
                    color = Color.argb(32, 15, 23, 42)
                }
            )
            // Left accent highlight strip
            canvas.drawRoundRect(
                RectF(cardR.left + pw * 0.022f, cardR.top + cardH * 0.16f, cardR.left + pw * 0.038f, cardR.bottom - cardH * 0.16f),
                6f, 6f,
                Paint(Paint.ANTI_ALIAS_FLAG).apply { color = withAlpha(headerCol, 220) }
            )
            val txL = cardR.left + pw * 0.065f
            canvas.drawRoundRect(RectF(txL, cardR.top + cardH * 0.22f, cardR.right - pw * 0.14f, cardR.top + cardH * 0.34f), 6f, 6f, inkPaint)
            canvas.drawRoundRect(RectF(txL, cardR.top + cardH * 0.46f, cardR.right - pw * 0.08f, cardR.top + cardH * 0.56f), 6f, 6f, mutedPaint)
            canvas.drawRoundRect(RectF(txL, cardR.top + cardH * 0.66f, cardR.right - pw * 0.22f, cardR.top + cardH * 0.76f), 6f, 6f, mutedPaint)
            curY += cardH + ph * 0.028f
        }
    }

    // ========================================================================
    // VARIANT 6: 3D OPEN TEXTBOOK SPREAD (ISOMETRIC OPEN HARDCOVER BOOK)
    // Photo 1 -> Left curved textbook page, Photo 2 -> Right curved textbook page
    // ========================================================================
    private fun drawOpenTextbook3D(
        context: Context,
        canvas: Canvas,
        rect: RectF,
        doc: PostDoc,
        p1: Bitmap?,
        p2: Bitmap?,
        accentColor: Int,
        secondaryColor: Int,
        lightColor: Int,
        darkColor: Int,
        deepColor: Int
    ) {
        if (rect.width() <= 16f || rect.height() <= 16f) return

        val leftBmp = p1?.takeIf { !it.isRecycled }
        val rightBmp = (p2 ?: if (doc.fillEmptyWithFirst) p1 else null)?.takeIf { !it.isRecycled }

        // Canonical open book spread space: width 1400 (-700..+700), height 940 (-470..+470)
        val pageW = 640f
        val pageH = 860f

        canvas.save()
        val cx = rect.centerX()
        val cy = rect.centerY()

        // Apply subtle 3D isometric book tilt using Android Camera
        val cam = Camera()
        cam.save()
        cam.setLocation(0f, 0f, -28f)
        cam.rotateX(16f)
        cam.rotateY(-5f)
        cam.rotateZ(-3.5f)
        val camMatrix = Matrix()
        cam.getMatrix(camMatrix)
        cam.restore()

        val scale = min(rect.width() / 1460f, rect.height() / 1040f)
        val totalMatrix = Matrix().apply {
            postScale(scale, scale)
            postConcat(camMatrix)
            postTranslate(cx, cy - rect.height() * 0.02f)
        }
        canvas.concat(totalMatrix)

        val coverBoardCol = blendColors(Color.rgb(16, 30, 58), accentColor, 0.25f)
        val coverDarkCol = PostDepth.darken(coverBoardCol, 0.45f)
        val ribbonCol = blendColors(Color.rgb(245, 158, 11), accentColor, 0.40f)

        // 1. Deep floor drop shadow under the open book
        val floorShadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb((175 * doc.sceneShadowStrength.coerceIn(0.2f, 1f)).roundToInt(), 0, 0, 0)
            maskFilter = BlurMaskFilter(46f, BlurMaskFilter.Blur.NORMAL)
        }
        val boardRect = RectF(-pageW - 36f, -pageH * 0.5f - 20f, pageW + 36f, pageH * 0.5f + 46f)
        canvas.drawRoundRect(boardRect, 26f, 26f, floorShadowPaint)

        // 2. Hardcover Exterior Binding Board (3D extruded slices)
        val boardPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                boardRect.left, boardRect.top, boardRect.right, boardRect.bottom,
                intArrayOf(coverDarkCol, coverBoardCol, coverDarkCol),
                floatArrayOf(0f, 0.5f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        val outerBoardRect = RectF(-pageW - 28f, -pageH * 0.5f - 16f, pageW + 28f, pageH * 0.5f + 22f)
        for (s in 12 downTo 1) {
            val dy = s * 2.4f
            val dx = -s * 1.1f
            canvas.drawRoundRect(
                RectF(outerBoardRect.left + dx, outerBoardRect.top + dy, outerBoardRect.right + dx, outerBoardRect.bottom + dy),
                22f, 22f, boardPaint
            )
        }
        // Gold/accent foil trim rim on hardcover board
        canvas.drawRoundRect(
            outerBoardRect, 22f, 22f,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = 3f
                color = withAlpha(ribbonCol, 175)
            }
        )

        // Helper to build a curved 3D page wing path (leftSign = -1 for left page, +1 for right page)
        fun buildWingPath(isRight: Boolean, offsetX: Float, offsetY: Float): Path {
            val topY = -pageH * 0.5f + offsetY
            val botY = pageH * 0.5f + offsetY
            val archRise = 26f
            return Path().apply {
                if (!isRight) {
                    val x0 = offsetX
                    val xOut = -pageW + offsetX
                    moveTo(x0, topY + 12f)
                    cubicTo(
                        x0 - pageW * 0.28f, topY - archRise,
                        x0 - pageW * 0.72f, topY - archRise * 0.45f,
                        xOut, topY + 4f
                    )
                    lineTo(xOut, botY + 4f)
                    cubicTo(
                        x0 - pageW * 0.72f, botY - archRise * 0.45f,
                        x0 - pageW * 0.28f, botY - archRise,
                        x0, botY + 12f
                    )
                    close()
                } else {
                    val x0 = offsetX
                    val xOut = pageW + offsetX
                    moveTo(x0, topY + 12f)
                    cubicTo(
                        x0 + pageW * 0.28f, topY - archRise,
                        x0 + pageW * 0.72f, topY - archRise * 0.45f,
                        xOut, topY + 4f
                    )
                    lineTo(xOut, botY + 4f)
                    cubicTo(
                        x0 + pageW * 0.72f, botY - archRise * 0.45f,
                        x0 + pageW * 0.28f, botY - archRise,
                        x0, botY + 12f
                    )
                    close()
                }
            }
        }

        // 3. Thick 3D Stacked Paper Page Blocks under Left and Right Wings
        for (s in 16 downTo 1) {
            val f = s / 16f
            val riffle = if (s % 2 == 0) 0 else -12
            val rCol = (238 + riffle).coerceIn(185, 248)
            val gCol = (233 + riffle).coerceIn(180, 244)
            val bCol = (220 + riffle).coerceIn(168, 232)
            val stackPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(rCol, gCol, bCol) }

            val leftStepPath = buildWingPath(isRight = false, offsetX = -f * 14f, offsetY = f * 24f)
            val rightStepPath = buildWingPath(isRight = true, offsetX = f * 14f, offsetY = f * 24f)
            canvas.drawPath(leftStepPath, stackPaint)
            canvas.drawPath(rightStepPath, stackPaint)
        }

        // 4. Left Page Top Face (Photo 1)
        val leftWingPath = buildWingPath(isRight = false, offsetX = 0f, offsetY = 0f)
        val leftPageRect = RectF(-pageW, -pageH * 0.5f - 18f, 0f, pageH * 0.5f + 14f)
        canvas.save()
        canvas.clipPath(leftWingPath)
        if (leftBmp != null) {
            val adj0 = doc.slotAdjust(0)
            canvas.drawRect(leftPageRect, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(251, 249, 243) })
            drawBitmapCenterCrop(canvas, leftBmp, leftPageRect, adj0.zoom, adj0.focusX, adj0.focusY)
        } else {
            drawStudyPagePlaceholder(
                context = context,
                canvas = canvas,
                pageRect = leftPageRect,
                slotIndex = 0,
                layerIdx = 0,
                isOverlapped = false,
                subjectTag = "CHAPTER 01 · CORE NOTES",
                doc = doc,
                accentColor = accentColor,
                warmPaper = true
            )
        }
        // 3D curved page lighting & spine gutter shadow on left wing
        val leftCurveLighting = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                -pageW, 0f, 0f, 0f,
                intArrayOf(
                    Color.argb(38, 0, 0, 0),
                    Color.argb(42, 255, 255, 255),
                    Color.argb(18, 0, 0, 0),
                    Color.argb(150, 0, 0, 0)
                ),
                floatArrayOf(0f, 0.34f, 0.78f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(leftPageRect, leftCurveLighting)
        canvas.restore()

        // 5. Right Page Top Face (Photo 2)
        val rightWingPath = buildWingPath(isRight = true, offsetX = 0f, offsetY = 0f)
        val rightPageRect = RectF(0f, -pageH * 0.5f - 18f, pageW, pageH * 0.5f + 14f)
        canvas.save()
        canvas.clipPath(rightWingPath)
        if (rightBmp != null) {
            val adj1 = doc.slotAdjust(if (p2 != null) 1 else 0)
            canvas.drawRect(rightPageRect, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(251, 249, 243) })
            drawBitmapCenterCrop(canvas, rightBmp, rightPageRect, adj1.zoom, adj1.focusX, adj1.focusY)
        } else {
            drawStudyPagePlaceholder(
                context = context,
                canvas = canvas,
                pageRect = rightPageRect,
                slotIndex = 1,
                layerIdx = 2,
                isOverlapped = false,
                subjectTag = "CHAPTER 02 · PRACTICE",
                doc = doc,
                accentColor = secondaryColor,
                warmPaper = true
            )
        }
        // 3D curved page lighting & spine gutter shadow on right wing
        val rightCurveLighting = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                0f, 0f, pageW, 0f,
                intArrayOf(
                    Color.argb(155, 0, 0, 0),
                    Color.argb(20, 0, 0, 0),
                    Color.argb(48, 255, 255, 255),
                    Color.argb(32, 0, 0, 0)
                ),
                floatArrayOf(0f, 0.20f, 0.62f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(rightPageRect, rightCurveLighting)
        canvas.restore()

        // Crisp page rim outline on both curved wings
        val pageBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 2f
            color = Color.argb(120, 255, 255, 255)
        }
        canvas.drawPath(leftWingPath, pageBorderPaint)
        canvas.drawPath(rightWingPath, pageBorderPaint)

        // 6. Silk Bookmark Ribbon draping from the bottom center spine
        val ribbonPath = Path().apply {
            moveTo(-14f, pageH * 0.5f + 10f)
            lineTo(14f, pageH * 0.5f + 10f)
            cubicTo(22f, pageH * 0.5f + 46f, 34f, pageH * 0.5f + 78f, 28f, pageH * 0.5f + 112f)
            lineTo(8f, pageH * 0.5f + 94f)
            lineTo(-10f, pageH * 0.5f + 114f)
            cubicTo(-14f, pageH * 0.5f + 76f, -18f, pageH * 0.5f + 44f, -14f, pageH * 0.5f + 10f)
            close()
        }
        canvas.drawPath(
            ribbonPath,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = LinearGradient(
                    -14f, pageH * 0.5f + 10f, 28f, pageH * 0.5f + 114f,
                    intArrayOf(ribbonCol, PostDepth.darken(ribbonCol, 0.28f)),
                    null, Shader.TileMode.CLAMP
                )
            }
        )

        canvas.restore()
    }

    // ========================================================================
    // VARIANT 7: 3D SPIRAL RING NOTEBOOK + LAYERED TEAR-OUT SHEETS (1..4 LAYERS)
    // Same 45° axonometric angle and dynamic 1..4 layer count as Phone & Book layers
    // ========================================================================
    private fun drawSpiralNotebook45(
        context: Context,
        canvas: Canvas,
        rect: RectF,
        doc: PostDoc,
        p1: Bitmap?,
        p2: Bitmap?,
        p3: Bitmap?,
        p4: Bitmap?,
        accentColor: Int,
        secondaryColor: Int,
        lightColor: Int,
        darkColor: Int,
        deepColor: Int
    ) {
        if (rect.width() <= 12f || rect.height() <= 12f) return

        val activeLayers = resolveActiveSlots(doc, 4, p1, p2, p3, p4)
        val count = activeLayers.size.coerceIn(1, 4)

        val nh = 1000f
        val nw = 700f
        val pageRadii = floatArrayOf(8f, 8f, 26f, 26f, 26f, 26f, 8f, 8f)

        val ux = 0.82f
        val uy = 0.072f
        val vx = 0.235f
        val vy = 0.92f

        val projW = nw * ux
        val projH = nh * vy
        val stepX = projW * 0.49f
        val stepY = -projH * 0.123f
        val extX = -38f
        val extY = 25f

        var minX = Float.POSITIVE_INFINITY
        var minY = Float.POSITIVE_INFINITY
        var maxX = Float.NEGATIVE_INFINITY
        var maxY = Float.NEGATIVE_INFINITY

        fun includePt(layerIdx: Int, cx: Float, cy: Float, edx: Float = 0f, edy: Float = 0f) {
            val px = layerIdx * stepX + cx * ux + cy * vx + edx
            val py = layerIdx * stepY + cx * uy + cy * vy + edy
            if (px < minX) minX = px
            if (px > maxX) maxX = px
            if (py < minY) minY = py
            if (py > maxY) maxY = py
        }

        for ((cx, cy) in arrayOf(-32f to -8f, nw + 8f to -8f, nw + 8f to nh + 10f, -32f to nh + 10f)) {
            includePt(0, cx, cy, 0f, 0f)
            includePt(0, cx, cy, extX * 1.15f, extY * 1.15f)
        }
        for (i in 1 until count) {
            for ((cx, cy) in arrayOf(0f to 0f, nw to 0f, nw to nh, 0f to nh)) {
                includePt(i, cx, cy, 0f, 0f)
            }
        }

        val fitScale = min(rect.width() / (maxX - minX).coerceAtLeast(1f), rect.height() / (maxY - minY).coerceAtLeast(1f))
        val fitTx = rect.centerX() - (minX + maxX) * 0.5f * fitScale
        val fitTy = rect.centerY() - (minY + maxY) * 0.5f * fitScale

        fun buildLayerMatrix(layerIdx: Int, edx: Float = 0f, edy: Float = 0f): Matrix {
            return Matrix().apply {
                setValues(
                    floatArrayOf(
                        ux, vx, layerIdx * stepX + edx,
                        uy, vy, layerIdx * stepY + edy,
                        0f, 0f, 1f
                    )
                )
                postScale(fitScale, fitScale)
                postTranslate(fitTx, fitTy)
            }
        }

        val pageRect = RectF(0f, 0f, nw, nh)
        val pagePath = Path().apply { addRoundRect(pageRect, pageRadii, Path.Direction.CW) }
        val subjects = arrayOf("LECTURE NOTES · 01", "KEY FORMULAS · 02", "EXAM SUMMARY · 03", "PRACTICE SET · 04")

        for (layerIdx in 0 until count) {
            val item = activeLayers[layerIdx]
            if (layerIdx == 0) {
                // 1. Floor shadow under 3D spiral notebook
                canvas.save()
                canvas.concat(buildLayerMatrix(0, extX * 1.65f, extY * 1.85f))
                val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.argb((175 * doc.sceneShadowStrength.coerceIn(0.2f, 1f)).roundToInt(), 0, 0, 0)
                    maskFilter = BlurMaskFilter(36f, BlurMaskFilter.Blur.NORMAL)
                }
                canvas.drawPath(pagePath, shadowPaint)
                canvas.restore()

                // 2. Heavy Kraft/Dark Chipboard Back Cover at full extrusion
                val backBoardCol = blendColors(Color.rgb(24, 32, 48), accentColor, 0.18f)
                val backPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = backBoardCol }
                for (s in 22 downTo 19) {
                    val f = s / 22f
                    canvas.save()
                    canvas.concat(buildLayerMatrix(0, extX * f, extY * f))
                    canvas.drawPath(pagePath, backPaint)
                    canvas.restore()
                }

                // 3. Stacked Notebook Paper Block along the bottom & left edge
                for (s in 18 downTo 2) {
                    val f = s / 22f
                    val riffle = if (s % 2 == 0) 0 else -10
                    val col = Color.rgb((242 + riffle).coerceIn(190, 250), (238 + riffle).coerceIn(185, 246), (226 + riffle).coerceIn(175, 236))
                    canvas.save()
                    canvas.concat(buildLayerMatrix(0, extX * f, extY * f))
                    canvas.drawPath(pagePath, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = col })
                    canvas.restore()
                }

                // 4. Front Notebook Sheet / Cover (Layer 0)
                canvas.save()
                canvas.concat(buildLayerMatrix(0))
                canvas.save()
                canvas.clipPath(pagePath)
                val bmp = item.bitmap
                val adj = doc.slotAdjust(item.slotIndex)
                if (bmp != null && !bmp.isRecycled) {
                    canvas.drawRect(pageRect, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE })
                    drawBitmapCenterCrop(canvas, bmp, pageRect, adj.zoom, adj.focusX, adj.focusY)
                } else {
                    drawStudyPagePlaceholder(
                        context = context,
                        canvas = canvas,
                        pageRect = pageRect,
                        slotIndex = item.slotIndex,
                        layerIdx = 0,
                        isOverlapped = count > 1,
                        subjectTag = subjects[0],
                        doc = doc,
                        accentColor = accentColor
                    )
                }
                canvas.restore()

                // Border around notebook front sheet
                canvas.drawPath(
                    pagePath,
                    Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        style = Paint.Style.STROKE
                        strokeWidth = 2.2f
                        color = Color.argb(190, 255, 255, 255)
                    }
                )

                // 5. 14 Realistic 3D Metallic Twin-Loop Spiral Wire Rings along the Left Spine
                val holePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(14, 18, 28) }
                val holeRimPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    style = Paint.Style.STROKE
                    strokeWidth = 1.6f
                    color = Color.argb(90, 0, 0, 0)
                }
                val ringCount = 14
                for (r in 0 until ringCount) {
                    val ry = nh * (0.065f + r * (0.87f / (ringCount - 1)))
                    val holeRect = RectF(16f, ry - 9f, 34f, ry + 9f)
                    canvas.drawRoundRect(holeRect, 4f, 4f, holePaint)
                    canvas.drawRoundRect(holeRect, 4f, 4f, holeRimPaint)

                    // Draw twin-loop metallic wire coils wrapping from the hole out over the 3D left spine edge
                    for (wireOffset in floatArrayOf(-4.5f, 4.5f)) {
                        val wy = ry + wireOffset
                        val wireShadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                            style = Paint.Style.STROKE
                            strokeWidth = 5.2f
                            strokeCap = Paint.Cap.ROUND
                            color = Color.argb(135, 0, 0, 0)
                        }
                        val wirePath = Path().apply {
                            moveTo(25f, wy)
                            cubicTo(2f, wy - 10f, -26f, wy - 4f, -24f, wy + 13f)
                        }
                        canvas.drawPath(wirePath, wireShadowPaint)

                        val wireMetalPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                            style = Paint.Style.STROKE
                            strokeWidth = 4.4f
                            strokeCap = Paint.Cap.ROUND
                            shader = LinearGradient(
                                -26f, wy - 10f, 25f, wy + 10f,
                                intArrayOf(
                                    Color.rgb(90, 102, 120),
                                    Color.rgb(242, 246, 252),
                                    Color.rgb(70, 80, 96),
                                    Color.rgb(210, 220, 235)
                                ),
                                floatArrayOf(0f, 0.38f, 0.72f, 1f),
                                Shader.TileMode.CLAMP
                            )
                        }
                        canvas.drawPath(wirePath, wireMetalPaint)
                    }
                }
                canvas.restore()
            } else {
                // Floating Tear-Out Study Sheets (Layers 1..3)
                canvas.save()
                canvas.concat(buildLayerMatrix(layerIdx, -24f, 17f))
                canvas.drawPath(
                    pagePath,
                    Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        color = Color.argb((155 * doc.sceneShadowStrength.coerceIn(0.2f, 1f)).roundToInt(), 0, 0, 0)
                        maskFilter = BlurMaskFilter(28f, BlurMaskFilter.Blur.NORMAL)
                    }
                )
                canvas.restore()

                // Paper thickness edge
                canvas.save()
                canvas.concat(buildLayerMatrix(layerIdx, -3.5f, 2.5f))
                canvas.drawPath(pagePath, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(220, 215, 204) })
                canvas.restore()

                // Floating sheet face
                canvas.save()
                canvas.concat(buildLayerMatrix(layerIdx))
                canvas.save()
                canvas.clipPath(pagePath)
                val bmp = item.bitmap
                val adj = doc.slotAdjust(item.slotIndex)
                if (bmp != null && !bmp.isRecycled) {
                    canvas.drawRect(pageRect, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE })
                    drawBitmapCenterCrop(canvas, bmp, pageRect, adj.zoom, adj.focusX, adj.focusY)
                } else {
                    drawStudyPagePlaceholder(
                        context = context,
                        canvas = canvas,
                        pageRect = pageRect,
                        slotIndex = item.slotIndex,
                        layerIdx = layerIdx,
                        isOverlapped = layerIdx < count - 1,
                        subjectTag = subjects[layerIdx % subjects.size],
                        doc = doc,
                        accentColor = accentColor
                    )
                }
                // Perforated punch-hole notches along the left tear-out margin
                val notchPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.argb(65, 15, 23, 42) }
                val ringCount = 14
                for (r in 0 until ringCount) {
                    val ry = nh * (0.065f + r * (0.87f / (ringCount - 1)))
                    canvas.drawCircle(16f, ry, 5.5f, notchPaint)
                }
                canvas.restore()

                canvas.drawPath(
                    pagePath,
                    Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        style = Paint.Style.STROKE
                        strokeWidth = 2f
                        color = Color.argb(185, 255, 255, 255)
                    }
                )
                canvas.restore()
            }
        }
    }

    // ========================================================================
    // VARIANT 8: 3D FANNED FLASHCARD DECK (1..4 STUDY CARDS WITH BINDER RING)
    // ========================================================================
    private fun drawFlashcardDeck3D(
        context: Context,
        canvas: Canvas,
        rect: RectF,
        doc: PostDoc,
        p1: Bitmap?,
        p2: Bitmap?,
        p3: Bitmap?,
        p4: Bitmap?,
        accentColor: Int,
        secondaryColor: Int,
        lightColor: Int,
        darkColor: Int,
        deepColor: Int
    ) {
        if (rect.width() <= 12f || rect.height() <= 12f) return

        val activeCards = resolveActiveSlots(doc, 4, p1, p2, p3, p4)
        val count = activeCards.size.coerceIn(1, 4)

        val cw = 620f
        val ch = 880f
        val cardR = 46f

        // Fan angles around the bottom-left binder ring pivot depending on how many cards are active (1..4)
        val fanAngles: FloatArray = when (count) {
            1 -> floatArrayOf(-3f)
            2 -> floatArrayOf(-11f, 7f)
            3 -> floatArrayOf(-16f, -1f, 14f)
            else -> floatArrayOf(-19f, -6.5f, 6f, 18.5f)
        }
        val stepOffsetsX: FloatArray = when (count) {
            1 -> floatArrayOf(0f)
            2 -> floatArrayOf(-85f, 85f)
            3 -> floatArrayOf(-145f, 0f, 145f)
            else -> floatArrayOf(-195f, -65f, 65f, 195f)
        }
        val stepOffsetsY: FloatArray = when (count) {
            1 -> floatArrayOf(0f)
            2 -> floatArrayOf(18f, -12f)
            3 -> floatArrayOf(34f, 0f, -20f)
            else -> floatArrayOf(46f, 14f, -12f, -34f)
        }

        val scale = min(rect.width() / 1180f, rect.height() / 1080f)
        canvas.save()
        canvas.translate(rect.centerX(), rect.centerY() + rect.height() * 0.03f)
        canvas.scale(scale, scale)

        // Slight 3D perspective camera tilt for the entire flashcard fan
        val cam = Camera()
        cam.save()
        cam.setLocation(0f, 0f, -32f)
        cam.rotateX(11f)
        cam.rotateY(-6f)
        val camM = Matrix()
        cam.getMatrix(camM)
        cam.restore()
        canvas.concat(camM)

        val tabColors = intArrayOf(
            blendColors(Color.rgb(16, 185, 129), accentColor, 0.25f),
            blendColors(Color.rgb(37, 99, 235), accentColor, 0.25f),
            blendColors(Color.rgb(245, 158, 11), accentColor, 0.25f),
            blendColors(Color.rgb(168, 85, 247), accentColor, 0.25f)
        )
        val tabLabels = arrayOf("BIOLOGY", "PHYSICS", "MATH", "CHEMISTRY")

        val cardRect = RectF(-cw * 0.5f, -ch * 0.5f, cw * 0.5f, ch * 0.5f)

        // Draw cards from back (index 0) to front (index count - 1)
        for (i in 0 until count) {
            val item = activeCards[i]
            val angle = fanAngles[i]
            val ox = stepOffsetsX[i]
            val oy = stepOffsetsY[i]
            val tabCol = tabColors[i % tabColors.size]

            canvas.save()
            canvas.translate(ox, oy)
            canvas.rotate(angle, -cw * 0.28f, ch * 0.34f)

            // 1. Cast drop shadow onto card below
            val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.argb((160 * doc.sceneShadowStrength.coerceIn(0.2f, 1f)).roundToInt(), 0, 0, 0)
                maskFilter = BlurMaskFilter(34f, BlurMaskFilter.Blur.NORMAL)
            }
            canvas.drawRoundRect(
                RectF(cardRect.left - 18f, cardRect.top + 22f, cardRect.right - 18f, cardRect.bottom + 22f),
                cardR, cardR, shadowPaint
            )

            // 2. Protruding top color-coded subject tab
            val tabW = cw * 0.34f
            val tabH = 46f
            val tabLeft = cardRect.left + 44f + (i % 2) * (cw * 0.38f)
            val tabRect = RectF(tabLeft, cardRect.top - tabH * 0.72f, tabLeft + tabW, cardRect.top + 18f)
            canvas.drawRoundRect(tabRect, 18f, 18f, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = tabCol })
            val tabTf = PostFonts.get(context, PostFont.BOLD, tabLabels[i % tabLabels.size], doc)
            val tabTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                typeface = tabTf
                color = Color.WHITE
                textSize = 20f
            }
            canvas.drawText(
                tabLabels[i % tabLabels.size],
                tabRect.centerX() - tabTextPaint.measureText(tabLabels[i % tabLabels.size]) * 0.5f,
                tabRect.top + 25f,
                tabTextPaint
            )

            // 3. Thick 3D Flashcard Cardstock Edge
            val edgePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(218, 224, 235)
            }
            for (s in 6 downTo 1) {
                val f = s * 1.6f
                canvas.drawRoundRect(
                    RectF(cardRect.left - f, cardRect.top + f, cardRect.right - f, cardRect.bottom + f),
                    cardR, cardR, edgePaint
                )
            }

            // 4. Flashcard Face
            val clipPath = Path().apply { addRoundRect(cardRect, cardR, cardR, Path.Direction.CW) }
            canvas.save()
            canvas.clipPath(clipPath)
            val bmp = item.bitmap
            val adj = doc.slotAdjust(item.slotIndex)
            if (bmp != null && !bmp.isRecycled) {
                canvas.drawRect(cardRect, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE })
                drawBitmapCenterCrop(canvas, bmp, cardRect, adj.zoom, adj.focusX, adj.focusY)
            } else {
                drawStudyPagePlaceholder(
                    context = context,
                    canvas = canvas,
                    pageRect = cardRect,
                    slotIndex = item.slotIndex,
                    layerIdx = i,
                    isOverlapped = false,
                    subjectTag = "FLASHCARD #0${i + 1} · ${tabLabels[i % tabLabels.size]}",
                    doc = doc,
                    accentColor = tabCol,
                    warmPaper = false
                )
            }
            canvas.restore()

            // Card border rim with colored top accent bar
            canvas.drawRoundRect(
                cardRect, cardR, cardR,
                Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    style = Paint.Style.STROKE
                    strokeWidth = 3.5f
                    color = tabCol
                }
            )

            // Metallic grommet punch-hole at bottom-left corner of each flashcard
            val grommetCx = cardRect.left + 56f
            val grommetCy = cardRect.bottom - 56f
            canvas.drawCircle(grommetCx, grommetCy, 20f, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(180, 190, 206) })
            canvas.drawCircle(grommetCx, grommetCy, 13f, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(15, 22, 34) })

            // On the topmost card, draw the 3D metallic binder ring looping through the corner grommet
            if (i == count - 1) {
                val ringRect = RectF(grommetCx - 52f, grommetCy - 26f, grommetCx + 16f, grommetCy + 42f)
                canvas.drawArc(
                    ringRect,
                    30f,
                    295f,
                    false,
                    Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        style = Paint.Style.STROKE
                        strokeWidth = 8.5f
                        strokeCap = Paint.Cap.ROUND
                        shader = LinearGradient(
                            ringRect.left, ringRect.top, ringRect.right, ringRect.bottom,
                            intArrayOf(Color.rgb(240, 245, 252), Color.rgb(90, 102, 120), Color.rgb(220, 228, 240)),
                            null, Shader.TileMode.CLAMP
                        )
                    }
                )
            }

            canvas.restore()
        }

        canvas.restore()
    }

    // ========================================================================
    // VARIANT 9: 3D EXAM CLIPBOARD + STAMPED A4 PAPER STACK (1..4 LAYERS AT 45°)
    // ========================================================================
    private fun drawExamClipboard45(
        context: Context,
        canvas: Canvas,
        rect: RectF,
        doc: PostDoc,
        p1: Bitmap?,
        p2: Bitmap?,
        p3: Bitmap?,
        p4: Bitmap?,
        accentColor: Int,
        secondaryColor: Int,
        lightColor: Int,
        darkColor: Int,
        deepColor: Int
    ) {
        if (rect.width() <= 12f || rect.height() <= 12f) return

        val activeLayers = resolveActiveSlots(doc, 4, p1, p2, p3, p4)
        val count = activeLayers.size.coerceIn(1, 4)

        val ph = 980f
        val pw = 690f
        val boardPadX = 38f
        val boardPadTop = 58f
        val boardPadBot = 38f

        val ux = 0.82f
        val uy = 0.072f
        val vx = 0.235f
        val vy = 0.92f

        val projW = pw * ux
        val projH = ph * vy
        val stepX = projW * 0.49f
        val stepY = -projH * 0.123f
        val extX = -32f
        val extY = 22f

        var minX = Float.POSITIVE_INFINITY
        var minY = Float.POSITIVE_INFINITY
        var maxX = Float.NEGATIVE_INFINITY
        var maxY = Float.NEGATIVE_INFINITY

        fun includePt(layerIdx: Int, cx: Float, cy: Float, edx: Float = 0f, edy: Float = 0f) {
            val px = layerIdx * stepX + cx * ux + cy * vx + edx
            val py = layerIdx * stepY + cx * uy + cy * vy + edy
            if (px < minX) minX = px
            if (px > maxX) maxX = px
            if (py < minY) minY = py
            if (py > maxY) maxY = py
        }

        for ((cx, cy) in arrayOf(
            -boardPadX to -boardPadTop - 25f,
            pw + boardPadX to -boardPadTop - 25f,
            pw + boardPadX to ph + boardPadBot,
            -boardPadX to ph + boardPadBot
        )) {
            includePt(0, cx, cy, 0f, 0f)
            includePt(0, cx, cy, extX * 1.15f, extY * 1.15f)
        }
        for (i in 1 until count) {
            for ((cx, cy) in arrayOf(0f to 0f, pw to 0f, pw to ph, 0f to ph)) {
                includePt(i, cx, cy, 0f, 0f)
            }
        }

        val fitScale = min(rect.width() / (maxX - minX).coerceAtLeast(1f), rect.height() / (maxY - minY).coerceAtLeast(1f))
        val fitTx = rect.centerX() - (minX + maxX) * 0.5f * fitScale
        val fitTy = rect.centerY() - (minY + maxY) * 0.5f * fitScale

        fun buildLayerMatrix(layerIdx: Int, edx: Float = 0f, edy: Float = 0f): Matrix {
            return Matrix().apply {
                setValues(
                    floatArrayOf(
                        ux, vx, layerIdx * stepX + edx,
                        uy, vy, layerIdx * stepY + edy,
                        0f, 0f, 1f
                    )
                )
                postScale(fitScale, fitScale)
                postTranslate(fitTx, fitTy)
            }
        }

        val boardRect = RectF(-boardPadX, -boardPadTop, pw + boardPadX, ph + boardPadBot)
        val boardR = 38f
        val paperRect = RectF(0f, 0f, pw, ph)
        val paperR = 14f
        val paperPath = Path().apply { addRoundRect(paperRect, paperR, paperR, Path.Direction.CW) }

        val boardWoodLight = blendColors(Color.rgb(142, 86, 42), accentColor, 0.22f)
        val boardWoodDark = PostDepth.darken(boardWoodLight, 0.45f)

        for (layerIdx in 0 until count) {
            val item = activeLayers[layerIdx]
            if (layerIdx == 0) {
                // 1. Floor drop shadow under clipboard
                canvas.save()
                canvas.concat(buildLayerMatrix(0, extX * 1.7f, extY * 1.9f))
                canvas.drawRoundRect(
                    boardRect, boardR, boardR,
                    Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        color = Color.argb((180 * doc.sceneShadowStrength.coerceIn(0.2f, 1f)).roundToInt(), 0, 0, 0)
                        maskFilter = BlurMaskFilter(38f, BlurMaskFilter.Blur.NORMAL)
                    }
                )
                canvas.restore()

                // 2. 3D Extruded Clipboard Board Edge
                val edgePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    shader = LinearGradient(
                        boardRect.left, boardRect.top, boardRect.right, boardRect.bottom,
                        intArrayOf(boardWoodDark, PostDepth.darken(boardWoodDark, 0.35f), boardWoodLight),
                        floatArrayOf(0f, 0.6f, 1f),
                        Shader.TileMode.CLAMP
                    )
                }
                for (s in 18 downTo 1) {
                    val f = s / 18f
                    canvas.save()
                    canvas.concat(buildLayerMatrix(0, extX * f, extY * f))
                    canvas.drawRoundRect(boardRect, boardR, boardR, edgePaint)
                    canvas.restore()
                }

                // 3. Clipboard Front Face + Clamped A4 Exam Sheet 1 + Metallic Clip
                canvas.save()
                canvas.concat(buildLayerMatrix(0))
                val facePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    shader = LinearGradient(
                        boardRect.left, boardRect.top, boardRect.right, boardRect.bottom,
                        intArrayOf(boardWoodLight, PostDepth.darken(boardWoodLight, 0.22f), boardWoodDark),
                        floatArrayOf(0f, 0.55f, 1f),
                        Shader.TileMode.CLAMP
                    )
                }
                canvas.drawRoundRect(boardRect, boardR, boardR, facePaint)
                canvas.drawRoundRect(
                    boardRect, boardR, boardR,
                    Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        style = Paint.Style.STROKE
                        strokeWidth = 2.5f
                        color = Color.argb(135, 255, 255, 255)
                    }
                )

                // Subtle paper stack under Sheet 1 on the clipboard
                for (ps in 3 downTo 1) {
                    val off = ps * 3.5f
                    canvas.drawRoundRect(
                        RectF(paperRect.left - off, paperRect.top + off, paperRect.right - off, paperRect.bottom + off),
                        paperR, paperR,
                        Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(226 - ps * 8, 222 - ps * 8, 212 - ps * 8) }
                    )
                }

                // Clamped A4 Exam Sheet 1
                canvas.save()
                canvas.clipPath(paperPath)
                val bmp = item.bitmap
                val adj = doc.slotAdjust(item.slotIndex)
                if (bmp != null && !bmp.isRecycled) {
                    canvas.drawRect(paperRect, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE })
                    drawBitmapCenterCrop(canvas, bmp, paperRect, adj.zoom, adj.focusX, adj.focusY)
                } else {
                    drawStudyPagePlaceholder(
                        context = context,
                        canvas = canvas,
                        pageRect = paperRect,
                        slotIndex = item.slotIndex,
                        layerIdx = 0,
                        isOverlapped = count > 1,
                        subjectTag = "NATIONAL EXAM · PAPER 01",
                        doc = doc,
                        accentColor = accentColor
                    )
                }
                canvas.restore()

                // 4. 3D Brushed-Steel Clipboard Spring Clip at top-center
                val clipBaseRect = RectF(pw * 0.26f, -boardPadTop - 6f, pw * 0.74f, 36f)
                // Cast shadow under metal clip onto paper
                canvas.drawRoundRect(
                    RectF(clipBaseRect.left - 8f, clipBaseRect.top + 12f, clipBaseRect.right - 8f, clipBaseRect.bottom + 14f),
                    18f, 18f,
                    Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        color = Color.argb(125, 0, 0, 0)
                        maskFilter = BlurMaskFilter(12f, BlurMaskFilter.Blur.NORMAL)
                    }
                )
                // Top metal hanging loop
                val loopRect = RectF(pw * 0.43f, -boardPadTop - 28f, pw * 0.57f, -boardPadTop + 12f)
                canvas.drawRoundRect(
                    loopRect, 20f, 20f,
                    Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        style = Paint.Style.STROKE
                        strokeWidth = 9f
                        color = Color.rgb(200, 210, 225)
                    }
                )
                // Brushed steel clamp plate
                val metalGrad = LinearGradient(
                    clipBaseRect.left, clipBaseRect.top, clipBaseRect.right, clipBaseRect.bottom,
                    intArrayOf(
                        Color.rgb(235, 240, 248),
                        Color.rgb(110, 122, 140),
                        Color.rgb(245, 248, 255),
                        Color.rgb(80, 90, 108)
                    ),
                    floatArrayOf(0f, 0.42f, 0.72f, 1f),
                    Shader.TileMode.CLAMP
                )
                canvas.drawRoundRect(clipBaseRect, 16f, 16f, Paint(Paint.ANTI_ALIAS_FLAG).apply { shader = metalGrad })
                // Two metallic rivets on clip
                canvas.drawCircle(pw * 0.33f, clipBaseRect.centerY(), 7f, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(55, 65, 81) })
                canvas.drawCircle(pw * 0.67f, clipBaseRect.centerY(), 7f, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(55, 65, 81) })

                canvas.restore()
            } else {
                // Floating A4 Exam Sheets (Layers 1..3)
                canvas.save()
                canvas.concat(buildLayerMatrix(layerIdx, -24f, 17f))
                canvas.drawPath(
                    paperPath,
                    Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        color = Color.argb((155 * doc.sceneShadowStrength.coerceIn(0.2f, 1f)).roundToInt(), 0, 0, 0)
                        maskFilter = BlurMaskFilter(28f, BlurMaskFilter.Blur.NORMAL)
                    }
                )
                canvas.restore()

                canvas.save()
                canvas.concat(buildLayerMatrix(layerIdx, -3.5f, 2.5f))
                canvas.drawPath(paperPath, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(218, 214, 204) })
                canvas.restore()

                canvas.save()
                canvas.concat(buildLayerMatrix(layerIdx))
                canvas.save()
                canvas.clipPath(paperPath)
                val bmp = item.bitmap
                val adj = doc.slotAdjust(item.slotIndex)
                if (bmp != null && !bmp.isRecycled) {
                    canvas.drawRect(paperRect, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE })
                    drawBitmapCenterCrop(canvas, bmp, paperRect, adj.zoom, adj.focusX, adj.focusY)
                } else {
                    drawStudyPagePlaceholder(
                        context = context,
                        canvas = canvas,
                        pageRect = paperRect,
                        slotIndex = item.slotIndex,
                        layerIdx = layerIdx,
                        isOverlapped = layerIdx < count - 1,
                        subjectTag = "NATIONAL EXAM · PAPER 0${layerIdx + 1}",
                        doc = doc,
                        accentColor = accentColor
                    )
                }
                canvas.restore()

                canvas.drawPath(
                    paperPath,
                    Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        style = Paint.Style.STROKE
                        strokeWidth = 2f
                        color = Color.argb(190, 255, 255, 255)
                    }
                )
                canvas.restore()
            }
        }
    }

    // ========================================================================
    // VARIANT 10: 3D SUBJECT RING-BINDER FOLDER WITH COLOR DIVIDER TABS (1..4 LAYERS)
    // ========================================================================
    private fun drawSubjectBinder45(
        context: Context,
        canvas: Canvas,
        rect: RectF,
        doc: PostDoc,
        p1: Bitmap?,
        p2: Bitmap?,
        p3: Bitmap?,
        p4: Bitmap?,
        accentColor: Int,
        secondaryColor: Int,
        lightColor: Int,
        darkColor: Int,
        deepColor: Int
    ) {
        if (rect.width() <= 12f || rect.height() <= 12f) return

        val activeLayers = resolveActiveSlots(doc, 4, p1, p2, p3, p4)
        val count = activeLayers.size.coerceIn(1, 4)

        val bh = 1000f
        val bw = 690f
        val tabW = 44f
        val ux = 0.82f
        val uy = 0.072f
        val vx = 0.235f
        val vy = 0.92f

        val projW = bw * ux
        val projH = bh * vy
        val stepX = projW * 0.49f
        val stepY = -projH * 0.123f
        val extX = -46f
        val extY = 30f

        var minX = Float.POSITIVE_INFINITY
        var minY = Float.POSITIVE_INFINITY
        var maxX = Float.NEGATIVE_INFINITY
        var maxY = Float.NEGATIVE_INFINITY

        fun includePt(layerIdx: Int, cx: Float, cy: Float, edx: Float = 0f, edy: Float = 0f) {
            val px = layerIdx * stepX + cx * ux + cy * vx + edx
            val py = layerIdx * stepY + cx * uy + cy * vy + edy
            if (px < minX) minX = px
            if (px > maxX) maxX = px
            if (py < minY) minY = py
            if (py > maxY) maxY = py
        }

        for ((cx, cy) in arrayOf(-10f to -6f, bw + tabW + 8f to -6f, bw + tabW + 8f to bh + 8f, -10f to bh + 8f)) {
            includePt(0, cx, cy, 0f, 0f)
            includePt(0, cx, cy, extX * 1.15f, extY * 1.15f)
        }
        for (i in 1 until count) {
            for ((cx, cy) in arrayOf(0f to 0f, bw + tabW to 0f, bw + tabW to bh, 0f to bh)) {
                includePt(i, cx, cy, 0f, 0f)
            }
        }

        val fitScale = min(rect.width() / (maxX - minX).coerceAtLeast(1f), rect.height() / (maxY - minY).coerceAtLeast(1f))
        val fitTx = rect.centerX() - (minX + maxX) * 0.5f * fitScale
        val fitTy = rect.centerY() - (minY + maxY) * 0.5f * fitScale

        fun buildLayerMatrix(layerIdx: Int, edx: Float = 0f, edy: Float = 0f): Matrix {
            return Matrix().apply {
                setValues(
                    floatArrayOf(
                        ux, vx, layerIdx * stepX + edx,
                        uy, vy, layerIdx * stepY + edy,
                        0f, 0f, 1f
                    )
                )
                postScale(fitScale, fitScale)
                postTranslate(fitTx, fitTy)
            }
        }

        val coverRect = RectF(0f, 0f, bw, bh)
        val coverRadii = floatArrayOf(10f, 10f, 24f, 24f, 24f, 24f, 10f, 10f)
        val coverPath = Path().apply { addRoundRect(coverRect, coverRadii, Path.Direction.CW) }

        val tabColors = intArrayOf(
            Color.rgb(16, 185, 129),
            Color.rgb(56, 189, 248),
            Color.rgb(245, 158, 11),
            Color.rgb(244, 63, 94)
        )
        val tabNames = arrayOf("CH 1", "CH 2", "CH 3", "CH 4")
        val binderBase = blendColors(Color.rgb(15, 25, 46), accentColor, 0.25f)
        val binderDark = PostDepth.darken(binderBase, 0.45f)
        val binderLight = PostDepth.lighten(binderBase, 0.28f)

        for (layerIdx in 0 until count) {
            val item = activeLayers[layerIdx]
            if (layerIdx == 0) {
                // 1. Floor shadow
                canvas.save()
                canvas.concat(buildLayerMatrix(0, extX * 1.65f, extY * 1.85f))
                canvas.drawPath(
                    coverPath,
                    Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        color = Color.argb((175 * doc.sceneShadowStrength.coerceIn(0.2f, 1f)).roundToInt(), 0, 0, 0)
                        maskFilter = BlurMaskFilter(36f, BlurMaskFilter.Blur.NORMAL)
                    }
                )
                canvas.restore()

                // 2. Back Binder Board + Protruding Plastic Chapter Divider Tabs inside the binder block
                for (s in 24 downTo 20) {
                    val f = s / 24f
                    canvas.save()
                    canvas.concat(buildLayerMatrix(0, extX * f, extY * f))
                    canvas.drawPath(coverPath, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = binderDark })
                    canvas.restore()
                }

                // Draw the 4 color divider tabs tucked inside the binder fore-edge (at f = 0.25..0.75)
                for (tIdx in 3 downTo 0) {
                    val tf = 0.22f + tIdx * 0.16f
                    val tabTop = bh * (0.12f + tIdx * 0.19f)
                    val tabRect = RectF(bw - 12f, tabTop, bw + tabW, tabTop + bh * 0.16f)
                    canvas.save()
                    canvas.concat(buildLayerMatrix(0, extX * tf, extY * tf))
                    canvas.drawRoundRect(
                        tabRect, 14f, 14f,
                        Paint(Paint.ANTI_ALIAS_FLAG).apply { color = tabColors[tIdx] }
                    )
                    canvas.restore()
                }

                // 3. Binder Paper Stack along bottom edge
                for (s in 19 downTo 3) {
                    val f = s / 24f
                    val riffle = if (s % 2 == 0) 0 else -10
                    val col = Color.rgb(242 + riffle, 238 + riffle, 228 + riffle)
                    canvas.save()
                    canvas.concat(buildLayerMatrix(0, extX * f, extY * f))
                    canvas.drawRoundRect(
                        RectF(bw * 0.05f, 12f, bw - 8f, bh - 8f),
                        16f, 16f,
                        Paint(Paint.ANTI_ALIAS_FLAG).apply { color = col }
                    )
                    canvas.restore()
                }

                // 4. Heavy 3D Ring-Binder Spine along Left Wall (with metallic spine label slot & rivets)
                val spineRect = RectF(-6f, 0f, bw * 0.075f, bh)
                for (s in 24 downTo 1) {
                    val f = s / 24f
                    val col = when {
                        f > 0.72f -> binderDark
                        f > 0.28f -> binderBase
                        else -> binderLight
                    }
                    canvas.save()
                    canvas.concat(buildLayerMatrix(0, extX * f, extY * f))
                    canvas.drawRoundRect(spineRect, 12f, 12f, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = col })
                    canvas.restore()
                }
                // Metallic spine finger-hole grommet on the 3D spine wall
                canvas.save()
                canvas.concat(buildLayerMatrix(0, extX * 0.52f, extY * 0.52f))
                canvas.drawCircle(bw * 0.02f, bh * 0.82f, 16f, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(210, 220, 235) })
                canvas.drawCircle(bw * 0.02f, bh * 0.82f, 10f, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(12, 16, 24) })
                canvas.restore()

                // 5. Front Binder Cover / Sheet 1
                canvas.save()
                canvas.concat(buildLayerMatrix(0))
                canvas.save()
                canvas.clipPath(coverPath)
                val bmp = item.bitmap
                val adj = doc.slotAdjust(item.slotIndex)
                if (bmp != null && !bmp.isRecycled) {
                    canvas.drawRect(coverRect, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE })
                    drawBitmapCenterCrop(canvas, bmp, coverRect, adj.zoom, adj.focusX, adj.focusY)
                } else {
                    drawStudyPagePlaceholder(
                        context = context,
                        canvas = canvas,
                        pageRect = coverRect,
                        slotIndex = item.slotIndex,
                        layerIdx = 0,
                        isOverlapped = count > 1,
                        subjectTag = "SUBJECT BINDER · UNIT 01",
                        doc = doc,
                        accentColor = accentColor
                    )
                }
                // Binder hinge groove & 2 metallic binder ring clasps on left margin
                val hingeX = bw * 0.068f
                canvas.drawRect(
                    RectF(0f, 0f, hingeX, bh),
                    Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        shader = LinearGradient(
                            0f, 0f, hingeX, 0f,
                            intArrayOf(Color.argb(120, 0, 0, 0), Color.argb(45, 255, 255, 255), Color.argb(145, 0, 0, 0)),
                            null, Shader.TileMode.CLAMP
                        )
                    }
                )
                canvas.restore()

                canvas.drawPath(
                    coverPath,
                    Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        style = Paint.Style.STROKE
                        strokeWidth = 2.4f
                        color = Color.argb(200, 255, 255, 255)
                    }
                )
                canvas.restore()
            } else {
                // Floating Tabbed Chapter Sheets (Layers 1..3)
                canvas.save()
                canvas.concat(buildLayerMatrix(layerIdx, -24f, 17f))
                canvas.drawPath(
                    coverPath,
                    Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        color = Color.argb((155 * doc.sceneShadowStrength.coerceIn(0.2f, 1f)).roundToInt(), 0, 0, 0)
                        maskFilter = BlurMaskFilter(28f, BlurMaskFilter.Blur.NORMAL)
                    }
                )
                canvas.restore()

                canvas.save()
                canvas.concat(buildLayerMatrix(layerIdx))
                // Protruding color chapter tab on the right edge of this floating sheet
                val tCol = tabColors[layerIdx % tabColors.size]
                val tabTop = bh * (0.14f + (layerIdx % 4) * 0.19f)
                val tabRect = RectF(bw - 10f, tabTop, bw + tabW, tabTop + bh * 0.15f)
                canvas.drawRoundRect(tabRect, 14f, 14f, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = tCol })

                canvas.save()
                canvas.clipPath(coverPath)
                val bmp = item.bitmap
                val adj = doc.slotAdjust(item.slotIndex)
                if (bmp != null && !bmp.isRecycled) {
                    canvas.drawRect(coverRect, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE })
                    drawBitmapCenterCrop(canvas, bmp, coverRect, adj.zoom, adj.focusX, adj.focusY)
                } else {
                    drawStudyPagePlaceholder(
                        context = context,
                        canvas = canvas,
                        pageRect = coverRect,
                        slotIndex = item.slotIndex,
                        layerIdx = layerIdx,
                        isOverlapped = layerIdx < count - 1,
                        subjectTag = "SUBJECT BINDER · UNIT 0${layerIdx + 1}",
                        doc = doc,
                        accentColor = tCol
                    )
                }
                // 3 binder punch holes on left edge
                val holePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(18, 24, 38) }
                for (hy in floatArrayOf(0.20f, 0.50f, 0.80f)) {
                    canvas.drawCircle(24f, bh * hy, 9f, holePaint)
                }
                canvas.restore()

                canvas.drawPath(
                    coverPath,
                    Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        style = Paint.Style.STROKE
                        strokeWidth = 2f
                        color = Color.argb(190, 255, 255, 255)
                    }
                )
                canvas.restore()
            }
        }
    }

    // ========================================================================
    // VARIANT 11: 3D STACKED TEXTBOOKS PEDESTAL + STANDING COVERS (1..3 PHOTOS)
    // ========================================================================
    private fun drawTextbookStack3D(
        context: Context,
        canvas: Canvas,
        rect: RectF,
        doc: PostDoc,
        p1: Bitmap?,
        p2: Bitmap?,
        p3: Bitmap?,
        accentColor: Int,
        secondaryColor: Int,
        lightColor: Int,
        darkColor: Int,
        deepColor: Int
    ) {
        if (rect.width() <= 12f || rect.height() <= 12f) return

        val activeBooks = resolveActiveSlots(doc, 3, p1, p2, p3)
        val count = activeBooks.size.coerceIn(1, 3)

        val scale = min(rect.width() / 1160f, rect.height() / 1060f)
        canvas.save()
        canvas.translate(rect.centerX(), rect.centerY())
        canvas.scale(scale, scale)

        // 1. Horizontal 3D Stacked Hardcover Textbooks Base Pedestal at the bottom
        val baseShadow = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb((180 * doc.sceneShadowStrength.coerceIn(0.2f, 1f)).roundToInt(), 0, 0, 0)
            maskFilter = BlurMaskFilter(44f, BlurMaskFilter.Blur.NORMAL)
        }
        canvas.drawOval(RectF(-500f, 350f, 500f, 470f), baseShadow)

        val spineColors = intArrayOf(
            blendColors(Color.rgb(15, 42, 96), accentColor, 0.25f),
            blendColors(Color.rgb(14, 116, 104), accentColor, 0.25f)
        )
        val spineTitles = arrayOf("GRADE 12 · NATIONAL EXAM", "GRADE 11 · COMPLETE SYLLABUS")

        for (b in 0 until 2) {
            val by = 385f - b * 74f
            val bxShift = if (b == 0) -14f else 22f
            val spineCol = spineColors[b]
            val spineDark = PostDepth.darken(spineCol, 0.40f)
            val spineLight = PostDepth.lighten(spineCol, 0.25f)

            // Left 3D Spine face of horizontal book
            val spineRect = RectF(-430f + bxShift, by - 34f, 120f + bxShift, by + 34f)
            canvas.drawRoundRect(
                spineRect, 18f, 18f,
                Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    shader = LinearGradient(
                        spineRect.left, spineRect.top, spineRect.left, spineRect.bottom,
                        intArrayOf(spineLight, spineCol, spineDark),
                        floatArrayOf(0f, 0.45f, 1f),
                        Shader.TileMode.CLAMP
                    )
                }
            )
            // Gold foil bands on horizontal book spine
            val foilCol = blendColors(Color.rgb(245, 198, 92), accentColor, 0.35f)
            val foilPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = foilCol }
            canvas.drawRect(RectF(spineRect.left + 32f, spineRect.top, spineRect.left + 44f, spineRect.bottom), foilPaint)
            canvas.drawRect(RectF(spineRect.right - 44f, spineRect.top, spineRect.right - 32f, spineRect.bottom), foilPaint)

            val stTf = PostFonts.get(context, PostFont.BOLD, spineTitles[b], doc)
            val stPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                typeface = stTf
                color = Color.argb(230, 255, 255, 255)
                textSize = 21f
            }
            val sfm = stPaint.fontMetrics
            canvas.drawText(
                spineTitles[b],
                spineRect.centerX() - stPaint.measureText(spineTitles[b]) * 0.5f,
                spineRect.centerY() - (sfm.ascent + sfm.descent) * 0.5f,
                stPaint
            )

            // Right 3D Cream Paper Page Block face of horizontal book
            val pagesRect = RectF(108f + bxShift, by - 28f, 430f + bxShift, by + 28f)
            canvas.drawRoundRect(
                pagesRect, 10f, 10f,
                Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    shader = LinearGradient(
                        pagesRect.left, pagesRect.top, pagesRect.right, pagesRect.bottom,
                        intArrayOf(Color.rgb(245, 240, 228), Color.rgb(225, 218, 204)),
                        null, Shader.TileMode.CLAMP
                    )
                }
            )
            // Paper riffle lines
            val rifflePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.argb(55, 80, 70, 55)
                strokeWidth = 1.6f
            }
            for (rl in -18..18 step 6) {
                canvas.drawLine(pagesRect.left + 10f, by + rl, pagesRect.right - 10f, by + rl, rifflePaint)
            }
            // Top & bottom hardcover board lips over the page block
            canvas.drawRoundRect(
                RectF(104f + bxShift, by - 34f, 438f + bxShift, by - 25f),
                5f, 5f, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = spineLight }
            )
            canvas.drawRoundRect(
                RectF(104f + bxShift, by + 25f, 438f + bxShift, by + 34f),
                5f, 5f, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = spineDark }
            )
        }

        // 2. Upright Standing 3D Hardcover Books above the stack (1, 2, or 3 books)
        val bw = 470f
        val bh = 680f
        val bookRect = RectF(-bw * 0.5f, -bh * 0.5f, bw * 0.5f, bh * 0.5f)
        val bookRadii = floatArrayOf(10f, 10f, 22f, 22f, 22f, 22f, 10f, 10f)
        val bookPath = Path().apply { addRoundRect(bookRect, bookRadii, Path.Direction.CW) }

        data class StandingPose(val x: Float, val y: Float, val rotZ: Float, val tiltY: Float, val scale: Float, val slot: EduSlot)

        val poses: List<StandingPose> = when (count) {
            1 -> listOf(
                StandingPose(0f, -85f, -2f, 12f, 1.04f, activeBooks[0])
            )
            2 -> listOf(
                StandingPose(-170f, -95f, -7f, 14f, 0.92f, activeBooks[1]),
                StandingPose(135f, -70f, 4f, -12f, 1.0f, activeBooks[0])
            )
            else -> listOf(
                StandingPose(-245f, -100f, -10f, 16f, 0.86f, activeBooks[1]),
                StandingPose(245f, -100f, 10f, -16f, 0.86f, activeBooks[2]),
                StandingPose(0f, -65f, 0f, 10f, 1.0f, activeBooks[0])
            )
        }

        for ((pIdx, pose) in poses.withIndex()) {
            canvas.save()
            canvas.translate(pose.x, pose.y)
            canvas.scale(pose.scale, pose.scale)

            val cam = Camera()
            cam.save()
            cam.setLocation(0f, 0f, -28f)
            cam.rotateX(5f)
            cam.rotateY(pose.tiltY)
            val m = Matrix()
            cam.getMatrix(m)
            cam.restore()
            canvas.concat(m)
            canvas.rotate(pose.rotZ)

            // Drop shadow
            canvas.drawPath(
                bookPath,
                Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.argb(155, 0, 0, 0)
                    maskFilter = BlurMaskFilter(32f, BlurMaskFilter.Blur.NORMAL)
                }
            )

            // 3D Spine extrusion on standing book
            val extSign = if (pose.tiltY >= 0f) -1f else 1f
            val spineCol = blendColors(Color.rgb(20, 34, 62), accentColor, 0.28f)
            for (s in 12 downTo 1) {
                val f = s / 12f
                canvas.save()
                canvas.translate(extSign * 26f * f, 12f * f)
                canvas.drawPath(
                    bookPath,
                    Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        color = if (s % 2 == 0) spineCol else PostDepth.darken(spineCol, 0.30f)
                    }
                )
                canvas.restore()
            }

            // Front cover of standing book
            canvas.save()
            canvas.clipPath(bookPath)
            val bmp = pose.slot.bitmap
            val adj = doc.slotAdjust(pose.slot.slotIndex)
            if (bmp != null && !bmp.isRecycled) {
                canvas.drawRect(bookRect, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE })
                drawBitmapCenterCrop(canvas, bmp, bookRect, adj.zoom, adj.focusX, adj.focusY)
            } else {
                drawStudyPagePlaceholder(
                    context = context,
                    canvas = canvas,
                    pageRect = bookRect,
                    slotIndex = pose.slot.slotIndex,
                    layerIdx = pIdx,
                    isOverlapped = false,
                    subjectTag = "TEXTBOOK · VOL 0${pose.slot.slotIndex + 1}",
                    doc = doc,
                    accentColor = accentColor
                )
            }
            // Spine hinge groove on left of cover
            val hx = bookRect.left + bw * 0.068f
            canvas.drawRect(
                RectF(bookRect.left, bookRect.top, hx + 10f, bookRect.bottom),
                Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    shader = LinearGradient(
                        bookRect.left, 0f, hx + 10f, 0f,
                        intArrayOf(Color.argb(115, 0, 0, 0), Color.argb(40, 255, 255, 255), Color.argb(140, 0, 0, 0), Color.TRANSPARENT),
                        floatArrayOf(0f, 0.45f, 0.78f, 1f),
                        Shader.TileMode.CLAMP
                    )
                }
            )
            canvas.restore()

            canvas.drawPath(
                bookPath,
                Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    style = Paint.Style.STROKE
                    strokeWidth = 2.5f
                    color = Color.argb(200, 255, 255, 255)
                }
            )
            canvas.restore()
        }

        canvas.restore()
    }

    // ========================================================================
    // VARIANT 12: 3D FLOATING MULTIPLE-CHOICE EXAM CARD + EXPLANATION CARD
    // Photo 1 -> Question Diagram/Photo on Main 3D Exam Card
    // Photo 2 -> Instant Solution / Notes Card floating on the right
    // ========================================================================
    private fun drawExamQuizCard3D(
        context: Context,
        canvas: Canvas,
        rect: RectF,
        doc: PostDoc,
        p1: Bitmap?,
        p2: Bitmap?,
        accentColor: Int,
        secondaryColor: Int,
        lightColor: Int,
        darkColor: Int,
        deepColor: Int
    ) {
        if (rect.width() <= 12f || rect.height() <= 12f) return

        val qBmp = p1?.takeIf { !it.isRecycled }
        val solBmp = (p2 ?: if (doc.fillEmptyWithFirst) p1 else null)?.takeIf { !it.isRecycled }

        val scale = min(rect.width() / 1180f, rect.height() / 1060f)
        canvas.save()
        canvas.translate(rect.centerX(), rect.centerY())
        canvas.scale(scale, scale)

        // 1. Main 3D Exam Question Card (Left / Center)
        canvas.save()
        canvas.translate(-125f, -15f)
        val camLeft = Camera()
        camLeft.save()
        camLeft.setLocation(0f, 0f, -30f)
        camLeft.rotateX(8f)
        camLeft.rotateY(11f)
        camLeft.rotateZ(-2.5f)
        val mLeft = Matrix()
        camLeft.getMatrix(mLeft)
        camLeft.restore()
        canvas.concat(mLeft)

        val qCardRect = RectF(-370f, -450f, 370f, 450f)
        val qCardR = 42f

        // Drop shadow
        canvas.drawRoundRect(
            RectF(qCardRect.left - 22f, qCardRect.top + 28f, qCardRect.right - 22f, qCardRect.bottom + 28f),
            qCardR, qCardR,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.argb((175 * doc.sceneShadowStrength.coerceIn(0.2f, 1f)).roundToInt(), 0, 0, 0)
                maskFilter = BlurMaskFilter(38f, BlurMaskFilter.Blur.NORMAL)
            }
        )

        // 3D Card thickness
        for (s in 8 downTo 1) {
            val f = s * 1.8f
            canvas.drawRoundRect(
                RectF(qCardRect.left - f, qCardRect.top + f, qCardRect.right - f, qCardRect.bottom + f),
                qCardR, qCardR,
                Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(210, 218, 232) }
            )
        }

        // Question card surface
        val qClip = Path().apply { addRoundRect(qCardRect, qCardR, qCardR, Path.Direction.CW) }
        canvas.save()
        canvas.clipPath(qClip)
        canvas.drawRect(qCardRect, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(248, 250, 254) })

        // Top exam header banner
        val headerCol = blendColors(Color.rgb(20, 84, 196), accentColor, 0.25f)
        val headerRect = RectF(qCardRect.left, qCardRect.top, qCardRect.right, qCardRect.top + 106f)
        canvas.drawRect(
            headerRect,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = LinearGradient(
                    headerRect.left, headerRect.top, headerRect.right, headerRect.bottom,
                    intArrayOf(headerCol, PostDepth.darken(headerCol, 0.18f)),
                    null, Shader.TileMode.CLAMP
                )
            }
        )
        val qHeaderTf = PostFonts.get(context, PostFont.BOLD, "NATIONAL EXAM · PRACTICE Q24", doc)
        val qHeaderPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = qHeaderTf
            color = Color.WHITE
            textSize = 23f
        }
        canvas.drawText("NATIONAL EXAM · PRACTICE Q24", qCardRect.left + 38f, qCardRect.top + 64f, qHeaderPaint)

        // Photo 1 Window (Question Diagram / Image)
        val diagramRect = RectF(qCardRect.left + 34f, qCardRect.top + 128f, qCardRect.right - 34f, qCardRect.top + 455f)
        val diagramPath = Path().apply { addRoundRect(diagramRect, 24f, 24f, Path.Direction.CW) }
        canvas.save()
        canvas.clipPath(diagramPath)
        if (qBmp != null) {
            val adj0 = doc.slotAdjust(0)
            canvas.drawRect(diagramRect, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE })
            drawBitmapCenterCrop(canvas, qBmp, diagramRect, adj0.zoom, adj0.focusX, adj0.focusY)
        } else {
            canvas.drawRect(diagramRect, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(238, 243, 252) })
            // Physics/Math vector diagram placeholder + "+ PHOTO 1" pill
            val axisPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(100, 116, 139)
                strokeWidth = 3f
            }
            canvas.drawLine(diagramRect.left + 60f, diagramRect.bottom - 55f, diagramRect.right - 60f, diagramRect.bottom - 55f, axisPaint)
            canvas.drawLine(diagramRect.centerX(), diagramRect.bottom - 35f, diagramRect.centerX(), diagramRect.top + 45f, axisPaint)

            val pillRect = RectF(diagramRect.centerX() - 120f, diagramRect.centerY() - 26f, diagramRect.centerX() + 120f, diagramRect.centerY() + 26f)
            canvas.drawRoundRect(pillRect, 26f, 26f, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = headerCol })
            val p1Paint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                typeface = qHeaderTf
                color = Color.WHITE
                textSize = 22f
            }
            val pfm = p1Paint.fontMetrics
            canvas.drawText(
                "+ PHOTO 1",
                pillRect.centerX() - p1Paint.measureText("+ PHOTO 1") * 0.5f,
                pillRect.centerY() - (pfm.ascent + pfm.descent) * 0.5f,
                p1Paint
            )
        }
        canvas.restore()
        canvas.drawPath(
            diagramPath,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = 2f
                color = Color.argb(45, 15, 23, 42)
            }
        )

        // 4 Tactile 3D Multiple-Choice Option Pills (A, B, C, D) — Option B is highlighted as Correct!
        val optLetters = arrayOf("A", "B", "C", "D")
        val emeraldCol = Color.rgb(16, 185, 129)
        var optY = diagramRect.bottom + 24f
        for (o in 0 until 4) {
            val isCorrect = (o == 1)
            val optH = 78f
            val optRect = RectF(qCardRect.left + 34f, optY, qCardRect.right - 34f, optY + optH)
            val fillCol = if (isCorrect) Color.rgb(220, 252, 231) else Color.WHITE
            val strokeCol = if (isCorrect) emeraldCol else Color.argb(45, 15, 23, 42)

            canvas.drawRoundRect(optRect, 20f, 20f, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = fillCol })
            canvas.drawRoundRect(
                optRect, 20f, 20f,
                Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    style = Paint.Style.STROKE
                    strokeWidth = if (isCorrect) 3.2f else 1.8f
                    color = strokeCol
                }
            )
            // Circle badge A/B/C/D
            val badgeCx = optRect.left + 44f
            val badgeCy = optRect.centerY()
            canvas.drawCircle(
                badgeCx, badgeCy, 22f,
                Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = if (isCorrect) emeraldCol else Color.rgb(226, 232, 240)
                }
            )
            val lPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                typeface = qHeaderTf
                color = if (isCorrect) Color.WHITE else Color.rgb(51, 65, 85)
                textSize = 20f
            }
            val lfm = lPaint.fontMetrics
            canvas.drawText(
                optLetters[o],
                badgeCx - lPaint.measureText(optLetters[o]) * 0.5f,
                badgeCy - (lfm.ascent + lfm.descent) * 0.5f,
                lPaint
            )
            // Simulated answer line
            val barW = when (o) {
                0 -> 280f
                1 -> 340f
                2 -> 240f
                else -> 310f
            }
            canvas.drawRoundRect(
                RectF(badgeCx + 42f, badgeCy - 8f, badgeCx + 42f + barW, badgeCy + 8f),
                8f, 8f,
                Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = if (isCorrect) Color.rgb(6, 95, 70) else Color.rgb(100, 116, 139)
                }
            )
            optY += optH + 14f
        }
        canvas.restore()

        canvas.drawRoundRect(
            qCardRect, qCardR, qCardR,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = 2.5f
                color = Color.argb(210, 255, 255, 255)
            }
        )
        canvas.restore()

        // 2. Overlapping Floating 3D "INSTANT EXPLANATION / SOLUTION" Card on the Right (Photo 2)
        canvas.save()
        canvas.translate(250f, 115f)
        val camRight = Camera()
        camRight.save()
        camRight.setLocation(0f, 0f, -28f)
        camRight.rotateX(6f)
        camRight.rotateY(-12f)
        camRight.rotateZ(3.5f)
        val mRight = Matrix()
        camRight.getMatrix(mRight)
        camRight.restore()
        canvas.concat(mRight)

        val sCardRect = RectF(-265f, -310f, 265f, 310f)
        val sCardR = 36f

        // Deep cast shadow onto main exam card
        canvas.drawRoundRect(
            RectF(sCardRect.left - 24f, sCardRect.top + 22f, sCardRect.right - 24f, sCardRect.bottom + 22f),
            sCardR, sCardR,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.argb(180, 0, 0, 0)
                maskFilter = BlurMaskFilter(32f, BlurMaskFilter.Blur.NORMAL)
            }
        )

        val sClip = Path().apply { addRoundRect(sCardRect, sCardR, sCardR, Path.Direction.CW) }
        canvas.save()
        canvas.clipPath(sClip)
        if (solBmp != null) {
            val adj1 = doc.slotAdjust(if (p2 != null) 1 else 0)
            canvas.drawRect(sCardRect, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE })
            drawBitmapCenterCrop(canvas, solBmp, sCardRect, adj1.zoom, adj1.focusX, adj1.focusY)
        } else {
            drawStudyPagePlaceholder(
                context = context,
                canvas = canvas,
                pageRect = sCardRect,
                slotIndex = 1,
                layerIdx = 2,
                isOverlapped = false,
                subjectTag = "INSTANT EXPLANATION",
                doc = doc,
                accentColor = Color.rgb(16, 185, 129),
                warmPaper = false
            )
        }
        canvas.restore()

        canvas.drawRoundRect(
            sCardRect, sCardR, sCardR,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = 3f
                color = Color.rgb(16, 185, 129)
            }
        )

        // Glowing 3D "CORRECT · +15 XP" pill floating at top-right of solution card
        val xpRect = RectF(20f, sCardRect.top - 26f, 240f, sCardRect.top + 28f)
        canvas.drawRoundRect(xpRect, 27f, 27f, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(16, 185, 129) })
        val xpPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = qHeaderTf
            color = Color.WHITE
            textSize = 20f
        }
        val xfm = xpPaint.fontMetrics
        canvas.drawText(
            "✓ SOLVED · +15 XP",
            xpRect.centerX() - xpPaint.measureText("✓ SOLVED · +15 XP") * 0.5f,
            xpRect.centerY() - (xfm.ascent + xfm.descent) * 0.5f,
            xpPaint
        )

        canvas.restore()
        canvas.restore()
    }

    // ========================================================================
    // VARIANT 13: 3D OPTICAL ANSWER SHEET (BUBBLE SCANTRON) + SCORE TROPHY CARD (2 PHOTO SLOTS)
    // ========================================================================
    private fun drawOpticalAnswerSheet3D(
        context: Context,
        canvas: Canvas,
        rect: RectF,
        doc: PostDoc,
        p1: Bitmap?,
        p2: Bitmap?,
        accentColor: Int,
        secondaryColor: Int,
        lightColor: Int,
        darkColor: Int,
        deepColor: Int
    ) {
        if (rect.width() <= 12f || rect.height() <= 12f) return

        val mainBmp = p1?.takeIf { !it.isRecycled }
        val badgeBmp = p2?.takeIf { !it.isRecycled }
            ?: if (doc.fillEmptyWithFirst) mainBmp else null

        val refW = 1080f
        val refH = 920f
        val scale = min(rect.width() / refW, rect.height() / refH) * 0.95f

        canvas.save()
        canvas.translate(rect.centerX(), rect.centerY())
        canvas.scale(scale, scale)

        // Ambient floor shadow
        canvas.drawOval(
            RectF(-470f, 340f, 470f, 445f),
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.argb(145, 0, 0, 0)
                maskFilter = BlurMaskFilter(45f, BlurMaskFilter.Blur.NORMAL)
            }
        )

        // 1. Main 3D Tilted Optical Scantron Sheet on the Left (Photo 1 + Bubble Grid)
        canvas.save()
        canvas.translate(-125f, -15f)
        val camLeft = Camera()
        camLeft.save()
        camLeft.setLocation(0f, 0f, -28f)
        camLeft.rotateX(11f)
        camLeft.rotateY(12f)
        camLeft.rotateZ(-4.5f)
        val mLeft = Matrix()
        camLeft.getMatrix(mLeft)
        camLeft.restore()
        canvas.concat(mLeft)

        val sheetRect = RectF(-340f, -400f, 340f, 400f)
        val sheetR = 28f

        // 3D Paper stack extrusion on bottom-left
        val stackEdgePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(214, 222, 234) }
        for (s in 9 downTo 1) {
            val f = s * 1.9f
            canvas.drawRoundRect(
                RectF(sheetRect.left - f, sheetRect.top + f, sheetRect.right - f, sheetRect.bottom + f),
                sheetR, sheetR, stackEdgePaint
            )
        }

        // Scantron cream-white paper surface
        val sheetClip = Path().apply { addRoundRect(sheetRect, sheetR, sheetR, Path.Direction.CW) }
        canvas.save()
        canvas.clipPath(sheetClip)
        canvas.drawColor(Color.rgb(252, 250, 245))

        // Top Scantron Rose/Emerald Official Header Band
        val scantronColor = Color.rgb(225, 29, 72)
        val headerBand = RectF(sheetRect.left, sheetRect.top, sheetRect.right, sheetRect.top + 78f)
        canvas.drawRect(
            headerBand,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = LinearGradient(
                    headerBand.left, headerBand.top, headerBand.right, headerBand.bottom,
                    blendColors(accentColor, scantronColor, 0.45f),
                    blendColors(accentColor, Color.rgb(15, 23, 42), 0.35f),
                    Shader.TileMode.CLAMP
                )
            }
        )
        val boldTf = PostFonts.get(context, PostFont.BOLD, "NATIONAL EXAM · OPTICAL ANSWER SHEET", doc)
        val headerPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = boldTf
            color = Color.WHITE
            textSize = 21f
            letterSpacing = 0.08f
        }
        canvas.drawText("NATIONAL EXAM · OPTICAL ANSWER SHEET", sheetRect.left + 44f, sheetRect.top + 47f, headerPaint)

        // Optical scanner black timing track marks along the left margin
        val trackPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(30, 41, 59) }
        var ty = sheetRect.top + 100f
        while (ty < sheetRect.bottom - 30f) {
            canvas.drawRect(RectF(sheetRect.left + 12f, ty, sheetRect.left + 30f, ty + 11f), trackPaint)
            ty += 28f
        }

        // Top Photo 1 Window inside the Scantron Sheet (Exam Diagram / Student ID Box)
        val photoBox = RectF(sheetRect.left + 46f, sheetRect.top + 96f, sheetRect.right - 32f, sheetRect.top + 380f)
        val photoClip = Path().apply { addRoundRect(photoBox, 18f, 18f, Path.Direction.CW) }
        canvas.save()
        canvas.clipPath(photoClip)
        if (mainBmp != null) {
            val adj0 = doc.slotAdjust(0)
            canvas.drawRect(photoBox, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE })
            drawBitmapCenterCrop(canvas, mainBmp, photoBox, adj0.zoom, adj0.focusX, adj0.focusY)
        } else {
            drawStudyPagePlaceholder(
                context = context,
                canvas = canvas,
                pageRect = photoBox,
                slotIndex = 0,
                layerIdx = 0,
                isOverlapped = false,
                subjectTag = "EXAM SHEET · PHOTO 1",
                doc = doc,
                accentColor = accentColor,
                warmPaper = true
            )
        }
        canvas.restore()
        canvas.drawRoundRect(
            photoBox, 18f, 18f,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = 2.5f
                color = withAlpha(accentColor, 180)
            }
        )

        // Two Columns of Optical Answer Bubbles (Q1..Q5 on Left, Q6..Q10 on Right)
        val bubbleStroke = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 2.2f
            color = Color.rgb(225, 29, 72)
        }
        val correctFill = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(16, 185, 129) }
        val graphiteFill = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(30, 41, 59) }
        val numPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = boldTf
            color = Color.rgb(71, 85, 105)
            textSize = 18f
        }
        val bubLetterPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = boldTf
            color = Color.rgb(225, 29, 72)
            textSize = 15f
        }
        val letters = arrayOf("A", "B", "C", "D")
        val selectedCol1 = intArrayOf(1, 3, 0, 2, 1)
        val selectedCol2 = intArrayOf(2, 0, 1, 3, 1)

        for (col in 0..1) {
            val colLeft = if (col == 0) sheetRect.left + 52f else sheetRect.left + 365f
            // Pink column box
            val colBox = RectF(colLeft - 8f, sheetRect.top + 402f, colLeft + 286f, sheetRect.bottom - 26f)
            canvas.drawRoundRect(
                colBox, 14f, 14f,
                Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.argb(18, 225, 29, 72)
                }
            )
            for (row in 0..4) {
                val qNum = col * 5 + row + 1
                val cy = sheetRect.top + 438f + row * 66f
                canvas.drawText(String.format("%02d", qNum), colLeft + 4f, cy + 6f, numPaint)
                val chosen = if (col == 0) selectedCol1[row] else selectedCol2[row]
                for (b in 0..3) {
                    val cx = colLeft + 68f + b * 54f
                    val oval = RectF(cx - 18f, cy - 13f, cx + 18f, cy + 13f)
                    if (b == chosen) {
                        val isGreen = (row % 2 == 0)
                        canvas.drawOval(oval, if (isGreen) correctFill else graphiteFill)
                        val wPaint = TextPaint(bubLetterPaint).apply { color = Color.WHITE }
                        canvas.drawText(letters[b], cx - wPaint.measureText(letters[b]) * 0.5f, cy + 5f, wPaint)
                    } else {
                        canvas.drawOval(oval, bubbleStroke)
                        canvas.drawText(
                            letters[b],
                            cx - bubLetterPaint.measureText(letters[b]) * 0.5f,
                            cy + 5f,
                            bubLetterPaint
                        )
                    }
                }
            }
        }
        canvas.restore() // end sheetClip

        canvas.drawRoundRect(
            sheetRect, sheetR, sheetR,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = 3f
                color = Color.WHITE
            }
        )
        canvas.restore()

        // 2. Overlapping Floating 3D "98% A+ SCORE & ANALYTICS" Trophy Card on the Right (Photo 2)
        canvas.save()
        canvas.translate(255f, 95f)
        val camRight = Camera()
        camRight.save()
        camRight.setLocation(0f, 0f, -28f)
        camRight.rotateX(7f)
        camRight.rotateY(-13f)
        camRight.rotateZ(4f)
        val mRight = Matrix()
        camRight.getMatrix(mRight)
        camRight.restore()
        canvas.concat(mRight)

        val trophyRect = RectF(-250f, -295f, 250f, 295f)
        val trophyR = 34f

        // Deep drop shadow onto scantron sheet
        canvas.drawRoundRect(
            RectF(trophyRect.left - 24f, trophyRect.top + 24f, trophyRect.right - 24f, trophyRect.bottom + 24f),
            trophyR, trophyR,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.argb(185, 0, 0, 0)
                maskFilter = BlurMaskFilter(34f, BlurMaskFilter.Blur.NORMAL)
            }
        )

        // 3D Gold/Emerald rimmed card
        val tClip = Path().apply { addRoundRect(trophyRect, trophyR, trophyR, Path.Direction.CW) }
        canvas.save()
        canvas.clipPath(tClip)
        if (badgeBmp != null) {
            val adj1 = doc.slotAdjust(if (p2 != null) 1 else 0)
            canvas.drawRect(trophyRect, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE })
            drawBitmapCenterCrop(canvas, badgeBmp, trophyRect, adj1.zoom, adj1.focusX, adj1.focusY)
        } else {
            drawStudyPagePlaceholder(
                context = context,
                canvas = canvas,
                pageRect = trophyRect,
                slotIndex = 1,
                layerIdx = 1,
                isOverlapped = false,
                subjectTag = "SCORE REPORT · 98% A+",
                doc = doc,
                accentColor = Color.rgb(245, 158, 11),
                warmPaper = false
            )
        }
        canvas.restore()

        canvas.drawRoundRect(
            trophyRect, trophyR, trophyR,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = 4f
                color = Color.rgb(245, 158, 11)
            }
        )

        // Floating 3D Gold Medal / A+ Pill at top of Trophy Card
        val badgePill = RectF(-190f, trophyRect.top - 28f, 190f, trophyRect.top + 28f)
        canvas.drawRoundRect(
            badgePill, 28f, 28f,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = LinearGradient(
                    badgePill.left, badgePill.top, badgePill.right, badgePill.bottom,
                    Color.rgb(245, 158, 11), Color.rgb(217, 119, 6), Shader.TileMode.CLAMP
                )
            }
        )
        val pillPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = boldTf
            color = Color.rgb(15, 23, 42)
            textSize = 20f
        }
        val pfm = pillPaint.fontMetrics
        val label = "★ TOP 1% · SCORE 98% A+"
        canvas.drawText(
            label,
            badgePill.centerX() - pillPaint.measureText(label) * 0.5f,
            badgePill.centerY() - (pfm.ascent + pfm.descent) * 0.5f,
            pillPaint
        )

        canvas.restore()
        canvas.restore()
    }

    // ========================================================================
    // VARIANT 14: 3D GRADUATION CAP (MORTARBOARD) ON HARDCOVER BOOK PEDESTAL + PHOTO FRAME (2 PHOTO SLOTS)
    // ========================================================================
    private fun drawGraduationCapPedestal3D(
        context: Context,
        canvas: Canvas,
        rect: RectF,
        doc: PostDoc,
        p1: Bitmap?,
        p2: Bitmap?,
        accentColor: Int,
        secondaryColor: Int,
        lightColor: Int,
        darkColor: Int,
        deepColor: Int
    ) {
        if (rect.width() <= 12f || rect.height() <= 12f) return

        val mainBmp = p1?.takeIf { !it.isRecycled }
        val secondBmp = p2?.takeIf { !it.isRecycled }
            ?: if (doc.fillEmptyWithFirst) mainBmp else null

        val refW = 1080f
        val refH = 940f
        val scale = min(rect.width() / refW, rect.height() / refH) * 0.95f

        canvas.save()
        canvas.translate(rect.centerX(), rect.centerY())
        canvas.scale(scale, scale)

        // Floor shadow
        canvas.drawOval(
            RectF(-470f, 340f, 470f, 450f),
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.argb(150, 0, 0, 0)
                maskFilter = BlurMaskFilter(45f, BlurMaskFilter.Blur.NORMAL)
            }
        )

        // 1. Standing 3D Honor Frame on the Left/Back (Photo 1)
        canvas.save()
        canvas.translate(-155f, -35f)
        val camFrame = Camera()
        camFrame.save()
        camFrame.setLocation(0f, 0f, -28f)
        camFrame.rotateX(8f)
        camFrame.rotateY(13f)
        camFrame.rotateZ(-3.5f)
        val mFrame = Matrix()
        camFrame.getMatrix(mFrame)
        camFrame.restore()
        canvas.concat(mFrame)

        val frameOuter = RectF(-290f, -370f, 290f, 370f)
        val frameR = 32f
        // 3D Gold/Navy Frame Bezel Extrusion
        val bezelCol = blendColors(accentColor, Color.rgb(245, 158, 11), 0.45f)
        for (s in 10 downTo 1) {
            val f = s * 2.2f
            canvas.drawRoundRect(
                RectF(frameOuter.left - f, frameOuter.top + f, frameOuter.right - f, frameOuter.bottom + f),
                frameR, frameR,
                Paint(Paint.ANTI_ALIAS_FLAG).apply { color = blendColors(bezelCol, Color.BLACK, 0.55f) }
            )
        }
        canvas.drawRoundRect(
            frameOuter, frameR, frameR,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = LinearGradient(
                    frameOuter.left, frameOuter.top, frameOuter.right, frameOuter.bottom,
                    bezelCol, blendColors(bezelCol, Color.rgb(15, 23, 42), 0.65f), Shader.TileMode.CLAMP
                )
            }
        )

        val innerPhoto = RectF(frameOuter.left + 22f, frameOuter.top + 22f, frameOuter.right - 22f, frameOuter.bottom - 22f)
        val innerClip = Path().apply { addRoundRect(innerPhoto, 20f, 20f, Path.Direction.CW) }
        canvas.save()
        canvas.clipPath(innerClip)
        if (mainBmp != null) {
            val adj0 = doc.slotAdjust(0)
            canvas.drawRect(innerPhoto, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE })
            drawBitmapCenterCrop(canvas, mainBmp, innerPhoto, adj0.zoom, adj0.focusX, adj0.focusY)
        } else {
            drawStudyPagePlaceholder(
                context = context,
                canvas = canvas,
                pageRect = innerPhoto,
                slotIndex = 0,
                layerIdx = 0,
                isOverlapped = false,
                subjectTag = "HONOR SHOWCASE · PHOTO 1",
                doc = doc,
                accentColor = accentColor,
                warmPaper = false
            )
        }
        canvas.restore()
        canvas.restore()

        // 2. Foreground 3D Hardcover Book Card on the Right (Photo 2) + 3D Graduation Cap Crowned on Top
        canvas.save()
        canvas.translate(215f, 110f)
        val camRight = Camera()
        camRight.save()
        camRight.setLocation(0f, 0f, -28f)
        camRight.rotateX(7f)
        camRight.rotateY(-12f)
        camRight.rotateZ(3f)
        val mRight = Matrix()
        camRight.getMatrix(mRight)
        camRight.restore()
        canvas.concat(mRight)

        val bookRect = RectF(-240f, -230f, 240f, 275f)
        val bookR = 26f

        // Cast shadow from right book onto left frame
        canvas.drawRoundRect(
            RectF(bookRect.left - 28f, bookRect.top + 26f, bookRect.right - 28f, bookRect.bottom + 26f),
            bookR, bookR,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.argb(185, 0, 0, 0)
                maskFilter = BlurMaskFilter(34f, BlurMaskFilter.Blur.NORMAL)
            }
        )

        // 3D Book Spine & Page Block thickness on left
        val spinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = blendColors(accentColor, Color.rgb(15, 23, 42), 0.45f)
        }
        for (s in 12 downTo 1) {
            val f = s * 2.1f
            canvas.drawRoundRect(
                RectF(bookRect.left - f, bookRect.top + f * 0.65f, bookRect.right - f, bookRect.bottom + f * 0.65f),
                bookR, bookR, spinePaint
            )
        }

        val bClip = Path().apply { addRoundRect(bookRect, bookR, bookR, Path.Direction.CW) }
        canvas.save()
        canvas.clipPath(bClip)
        if (secondBmp != null) {
            val adj1 = doc.slotAdjust(if (p2 != null) 1 else 0)
            canvas.drawRect(bookRect, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE })
            drawBitmapCenterCrop(canvas, secondBmp, bookRect, adj1.zoom, adj1.focusX, adj1.focusY)
        } else {
            drawStudyPagePlaceholder(
                context = context,
                canvas = canvas,
                pageRect = bookRect,
                slotIndex = 1,
                layerIdx = 2,
                isOverlapped = false,
                subjectTag = "CLASS OF EXCELLENCE",
                doc = doc,
                accentColor = Color.rgb(245, 158, 11),
                warmPaper = true
            )
        }
        canvas.restore()

        canvas.drawRoundRect(
            bookRect, bookR, bookR,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = 3.5f
                color = Color.rgb(245, 158, 11)
            }
        )
        canvas.restore()

        // 3. Realistic 3D Graduation Mortarboard Cap perched atop the right book pedestal
        canvas.save()
        canvas.translate(205f, -155f)

        // Skullcap base under the mortarboard
        val skullPath = Path().apply {
            moveTo(-125f, 15f)
            quadTo(0f, -15f, 125f, 15f)
            lineTo(105f, 78f)
            quadTo(0f, 108f, -105f, 78f)
            close()
        }
        canvas.drawPath(
            skullPath,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = LinearGradient(
                    -125f, 0f, 125f, 95f,
                    Color.rgb(30, 41, 59), Color.rgb(10, 15, 28), Shader.TileMode.CLAMP
                )
            }
        )
        // Gold trim band along skullcap rim
        canvas.drawPath(
            skullPath,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = 3f
                color = Color.rgb(245, 158, 11)
            }
        )

        // 3D Mortarboard Diamond Top Board thickness (bottom bevel)
        val boardThick = Path().apply {
            moveTo(0f, -74f)
            lineTo(250f, -2f)
            lineTo(250f, 10f)
            lineTo(0f, 84f)
            lineTo(-250f, 10f)
            lineTo(-250f, -2f)
            close()
        }
        canvas.drawPath(
            boardThick,
            Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(245, 158, 11) }
        )

        // 3D Mortarboard Diamond Top Surface
        val boardTop = Path().apply {
            moveTo(0f, -82f)
            lineTo(250f, -6f)
            lineTo(0f, 72f)
            lineTo(-250f, -6f)
            close()
        }
        canvas.drawPath(
            boardTop,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = LinearGradient(
                    -220f, -75f, 220f, 70f,
                    Color.rgb(38, 52, 74), Color.rgb(15, 23, 42), Shader.TileMode.CLAMP
                )
            }
        )
        canvas.drawPath(
            boardTop,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = 2.5f
                color = withAlpha(accentColor, 200)
            }
        )

        // Center Gold Button & Draped Silk Tassel
        canvas.drawCircle(0f, -5f, 14f, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(245, 158, 11) })
        val tasselCord = Path().apply {
            moveTo(0f, -5f)
            quadTo(95f, 12f, 148f, 44f)
            quadTo(162f, 95f, 156f, 152f)
        }
        canvas.drawPath(
            tasselCord,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = 7f
                strokeCap = Paint.Cap.ROUND
                color = Color.rgb(251, 191, 36)
            }
        )
        // Gold clasp ring & flared tassel fringe
        canvas.drawRoundRect(
            RectF(144f, 142f, 168f, 154f),
            4f, 4f,
            Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(217, 119, 6) }
        )
        val fringePath = Path().apply {
            moveTo(145f, 154f)
            lineTo(167f, 154f)
            lineTo(176f, 208f)
            lineTo(136f, 208f)
            close()
        }
        canvas.drawPath(
            fringePath,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = LinearGradient(
                    136f, 154f, 176f, 208f,
                    Color.rgb(251, 191, 36), Color.rgb(180, 83, 9), Shader.TileMode.CLAMP
                )
            }
        )

        canvas.restore()
        canvas.restore()
    }

    // ========================================================================
    // VARIANT 15: 3D FOLDED STUDY BROCHURE TRIFOLD (1..3 DYNAMIC PANELS)
    // ========================================================================
    private fun drawTrifoldBrochure3D(
        context: Context,
        canvas: Canvas,
        rect: RectF,
        doc: PostDoc,
        p1: Bitmap?,
        p2: Bitmap?,
        p3: Bitmap?,
        accentColor: Int,
        secondaryColor: Int,
        lightColor: Int,
        darkColor: Int,
        deepColor: Int
    ) {
        if (rect.width() <= 12f || rect.height() <= 12f) return

        val activePanels = resolveActiveSlots(doc, 3, p1, p2, p3)
        val count = activePanels.size.coerceIn(1, 3)

        val refW = 1080f
        val refH = 920f
        val scale = min(rect.width() / refW, rect.height() / refH) * 0.95f

        canvas.save()
        canvas.translate(rect.centerX(), rect.centerY())
        canvas.scale(scale, scale)

        // Floor shadow under the trifold brochure
        canvas.drawOval(
            RectF(-460f, 345f, 460f, 445f),
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.argb(150, 0, 0, 0)
                maskFilter = BlurMaskFilter(45f, BlurMaskFilter.Blur.NORMAL)
            }
        )

        val panelH = 690f
        val panelW = when (count) {
            1 -> 520f
            2 -> 410f
            else -> 305f
        }
        val totalW = panelW * count
        val startX = -totalW * 0.5f

        for (i in 0 until count) {
            val item = activePanels[i]
            val pLeft = startX + i * panelW
            val pRight = pLeft + panelW
            // Z-fold perspective skew: even panels angle slightly up-right, odd panels angle down-right
            val skewDy = when {
                count == 1 -> -24f
                i % 2 == 0 -> -34f
                else -> 34f
            }
            val yOffsetLeft = when {
                count == 1 -> 12f
                i == 0 -> 18f
                i == 1 -> -16f
                else -> 18f
            }
            val yOffsetRight = yOffsetLeft + skewDy

            canvas.save()
            // Build a 2D affine shear matrix mapping RectF(0, -panelH/2, panelW, panelH/2) to the Z-folded panel quad
            val m = Matrix()
            val slopeY = (yOffsetRight - yOffsetLeft) / panelW
            m.setValues(
                floatArrayOf(
                    1f, 0f, pLeft,
                    slopeY, 1f, yOffsetLeft,
                    0f, 0f, 1f
                )
            )
            canvas.concat(m)

            val localRect = RectF(0f, -panelH * 0.5f, panelW, panelH * 0.5f)

            // 3D Bottom cardstock thickness edge
            val edgePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = if (i % 2 == 0) Color.rgb(218, 226, 238) else Color.rgb(186, 198, 214)
            }
            canvas.drawRect(
                RectF(0f, localRect.bottom, panelW, localRect.bottom + 14f),
                edgePaint
            )

            // Panel Photo or Study Guide Placeholder
            canvas.save()
            canvas.clipRect(localRect)
            val bmp = item.bitmap
            val adj = doc.slotAdjust(item.slotIndex)
            if (bmp != null && !bmp.isRecycled) {
                canvas.drawRect(localRect, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE })
                drawBitmapCenterCrop(canvas, bmp, localRect, adj.zoom, adj.focusX, adj.focusY)
            } else {
                drawStudyPagePlaceholder(
                    context = context,
                    canvas = canvas,
                    pageRect = localRect,
                    slotIndex = item.slotIndex,
                    layerIdx = i,
                    isOverlapped = false,
                    subjectTag = "PANEL 0${i + 1} · CHEAT SHEET",
                    doc = doc,
                    accentColor = accentColor,
                    warmPaper = (i % 2 == 1)
                )
            }

            // Directional Z-fold lighting & crease shadow overlay
            if (i % 2 == 1) {
                // Recessed fold panel gets soft directional shade from the left crease
                canvas.drawRect(
                    localRect,
                    Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        shader = LinearGradient(
                            0f, 0f, panelW, 0f,
                            intArrayOf(
                                Color.argb(85, 0, 0, 0),
                                Color.argb(28, 0, 0, 0),
                                Color.argb(10, 0, 0, 0)
                            ),
                            floatArrayOf(0f, 0.35f, 1f),
                            Shader.TileMode.CLAMP
                        )
                    }
                )
            } else {
                // Forward-facing panel gets subtle highlight near the crease
                canvas.drawRect(
                    localRect,
                    Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        shader = LinearGradient(
                            0f, 0f, panelW, 0f,
                            intArrayOf(
                                Color.argb(24, 255, 255, 255),
                                Color.argb(0, 255, 255, 255),
                                Color.argb(38, 0, 0, 0)
                            ),
                            floatArrayOf(0f, 0.7f, 1f),
                            Shader.TileMode.CLAMP
                        )
                    }
                )
            }
            canvas.restore()

            // Crisp border & fold crease line
            canvas.drawRect(
                localRect,
                Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    style = Paint.Style.STROKE
                    strokeWidth = 2.5f
                    color = Color.argb(210, 255, 255, 255)
                }
            )

            canvas.restore()
        }

        canvas.restore()
    }

    // ========================================================================
    // VARIANT 16: 3D COURSE / SUBJECT COLLECTOR'S BOX SET (1..3 DYNAMIC SLOTS)
    // ========================================================================
    private fun drawCourseCollectorBox3D(
        context: Context,
        canvas: Canvas,
        rect: RectF,
        doc: PostDoc,
        p1: Bitmap?,
        p2: Bitmap?,
        p3: Bitmap?,
        accentColor: Int,
        secondaryColor: Int,
        lightColor: Int,
        darkColor: Int,
        deepColor: Int
    ) {
        if (rect.width() <= 12f || rect.height() <= 12f) return

        val activeSlots = resolveActiveSlots(doc, 3, p1, p2, p3)
        val count = activeSlots.size.coerceIn(1, 3)

        val refW = 1080f
        val refH = 920f
        val scale = min(rect.width() / refW, rect.height() / refH) * 0.95f

        canvas.save()
        canvas.translate(rect.centerX(), rect.centerY())
        canvas.scale(scale, scale)

        // Floor shadow
        canvas.drawOval(
            RectF(-460f, 345f, 460f, 445f),
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.argb(155, 0, 0, 0)
                maskFilter = BlurMaskFilter(45f, BlurMaskFilter.Blur.NORMAL)
            }
        )

        // Shift box slightly left when bonus workbook inserts slide out on the right
        val boxShiftX = when (count) {
            1 -> 25f
            2 -> -95f
            else -> -155f
        }

        // Draw sliding bonus workbook/card inserts (Slots 2 and 3) behind/right of the main 3D box
        for (idx in (count - 1) downTo 1) {
            val item = activeSlots[idx]
            val slideX = boxShiftX + 195f + idx * 145f
            val slideY = -10f + idx * 24f
            canvas.save()
            canvas.translate(slideX, slideY)
            canvas.rotate(4f * idx)

            val insertRect = RectF(-195f, -310f, 195f, 310f)
            val insertR = 24f

            // Drop shadow
            canvas.drawRoundRect(
                RectF(insertRect.left - 18f, insertRect.top + 20f, insertRect.right - 18f, insertRect.bottom + 20f),
                insertR, insertR,
                Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.argb(150, 0, 0, 0)
                    maskFilter = BlurMaskFilter(28f, BlurMaskFilter.Blur.NORMAL)
                }
            )

            // 3D Cardstock edge
            for (s in 6 downTo 1) {
                val f = s * 1.6f
                canvas.drawRoundRect(
                    RectF(insertRect.left - f, insertRect.top + f, insertRect.right - f, insertRect.bottom + f),
                    insertR, insertR,
                    Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(216, 224, 236) }
                )
            }

            val iClip = Path().apply { addRoundRect(insertRect, insertR, insertR, Path.Direction.CW) }
            canvas.save()
            canvas.clipPath(iClip)
            val bmp = item.bitmap
            val adj = doc.slotAdjust(item.slotIndex)
            if (bmp != null && !bmp.isRecycled) {
                canvas.drawRect(insertRect, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE })
                drawBitmapCenterCrop(canvas, bmp, insertRect, adj.zoom, adj.focusX, adj.focusY)
            } else {
                drawStudyPagePlaceholder(
                    context = context,
                    canvas = canvas,
                    pageRect = insertRect,
                    slotIndex = item.slotIndex,
                    layerIdx = idx,
                    isOverlapped = false,
                    subjectTag = if (idx == 1) "WORKBOOK VOL. 1" else "FORMULA DECK VOL. 2",
                    doc = doc,
                    accentColor = accentColor,
                    warmPaper = (idx == 2)
                )
            }
            canvas.restore()

            canvas.drawRoundRect(
                insertRect, insertR, insertR,
                Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    style = Paint.Style.STROKE
                    strokeWidth = 3f
                    color = accentColor
                }
            )
            canvas.restore()
        }

        // Main 3D Isometric Collector's Box Set (Photo 1)
        canvas.save()
        canvas.translate(boxShiftX, 0f)
        val camBox = Camera()
        camBox.save()
        camBox.setLocation(0f, 0f, -30f)
        camBox.rotateX(6f)
        camBox.rotateY(14f)
        camBox.rotateZ(-2.5f)
        val mBox = Matrix()
        camBox.getMatrix(mBox)
        camBox.restore()
        canvas.concat(mBox)

        val frontRect = RectF(-245f, -355f, 245f, 355f)
        val spineW = 92f
        val spineDx = -spineW
        val spineDy = -34f

        // 1. Thick 3D Left Box Spine Polygon
        val spinePath = Path().apply {
            moveTo(frontRect.left, frontRect.top)
            lineTo(frontRect.left + spineDx, frontRect.top + spineDy)
            lineTo(frontRect.left + spineDx, frontRect.bottom + spineDy)
            lineTo(frontRect.left, frontRect.bottom)
            close()
        }
        val spineDark = blendColors(accentColor, Color.rgb(10, 16, 30), 0.72f)
        val spineMid = blendColors(accentColor, Color.rgb(20, 32, 56), 0.45f)
        canvas.drawPath(
            spinePath,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = LinearGradient(
                    frontRect.left + spineDx, 0f, frontRect.left, 0f,
                    spineDark, spineMid, Shader.TileMode.CLAMP
                )
            }
        )
        // Gold/Accent decorative bands on the 3D Box Spine
        val bandPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = accentColor
            strokeWidth = 5f
            style = Paint.Style.STROKE
        }
        canvas.drawLine(
            frontRect.left + spineDx + 10f, frontRect.top + spineDy + 58f,
            frontRect.left - 10f, frontRect.top + 58f, bandPaint
        )
        canvas.drawLine(
            frontRect.left + spineDx + 10f, frontRect.bottom + spineDy - 58f,
            frontRect.left - 10f, frontRect.bottom - 58f, bandPaint
        )

        // Vertical Title on the 3D Box Spine
        val boldTf = PostFonts.get(context, PostFont.BOLD, "COMPLETE STUDY KIT", doc)
        canvas.save()
        val spineCenterX = frontRect.left + spineDx * 0.5f
        val spineCenterY = (frontRect.top + frontRect.bottom + spineDy) * 0.5f
        canvas.translate(spineCenterX, spineCenterY)
        canvas.rotate(-90f)
        val spineTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = boldTf
            color = Color.WHITE
            textSize = 22f
            letterSpacing = 0.14f
        }
        val stLabel = "COMPLETE EXAM PREP KIT"
        canvas.drawText(stLabel, -spineTextPaint.measureText(stLabel) * 0.5f, 8f, spineTextPaint)
        canvas.restore()

        // 2. Top 3D Box Lid Thickness Polygon
        val topLidPath = Path().apply {
            moveTo(frontRect.left, frontRect.top)
            lineTo(frontRect.left + spineDx, frontRect.top + spineDy)
            lineTo(frontRect.right + spineDx, frontRect.top + spineDy)
            lineTo(frontRect.right, frontRect.top)
            close()
        }
        canvas.drawPath(
            topLidPath,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = LinearGradient(
                    frontRect.left, frontRect.top + spineDy, frontRect.right, frontRect.top,
                    blendColors(accentColor, Color.WHITE, 0.35f),
                    spineMid,
                    Shader.TileMode.CLAMP
                )
            }
        )

        // 3. Front Cover Face of the Collector's Box (Photo 1)
        canvas.save()
        canvas.clipRect(frontRect)
        val mainItem = activeSlots[0]
        val mainBmp = mainItem.bitmap
        val adj0 = doc.slotAdjust(mainItem.slotIndex)
        if (mainBmp != null && !mainBmp.isRecycled) {
            canvas.drawRect(frontRect, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE })
            drawBitmapCenterCrop(canvas, mainBmp, frontRect, adj0.zoom, adj0.focusX, adj0.focusY)
        } else {
            drawStudyPagePlaceholder(
                context = context,
                canvas = canvas,
                pageRect = frontRect,
                slotIndex = 0,
                layerIdx = 0,
                isOverlapped = false,
                subjectTag = "COLLECTOR'S EDITION BOX",
                doc = doc,
                accentColor = accentColor,
                warmPaper = false
            )
        }
        // Subtle glossy box spine crease highlight along left edge of front cover
        canvas.drawRect(
            RectF(frontRect.left, frontRect.top, frontRect.left + 26f, frontRect.bottom),
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = LinearGradient(
                    frontRect.left, 0f, frontRect.left + 26f, 0f,
                    Color.argb(95, 255, 255, 255), Color.TRANSPARENT, Shader.TileMode.CLAMP
                )
            }
        )
        canvas.restore()

        canvas.drawRect(
            frontRect,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = 3f
                color = Color.argb(215, 255, 255, 255)
            }
        )

        canvas.restore()
        canvas.restore()
    }

    // ========================================================================
    // VARIANT 17: 3D STUDY STREAK & XP LEVEL-UP COHORT LAUNCH CARD (2 PHOTO SLOTS)
    // ========================================================================
    private fun drawStudyStreakXpCard3D(
        context: Context,
        canvas: Canvas,
        rect: RectF,
        doc: PostDoc,
        p1: Bitmap?,
        p2: Bitmap?,
        accentColor: Int,
        secondaryColor: Int,
        lightColor: Int,
        darkColor: Int,
        deepColor: Int
    ) {
        if (rect.width() <= 12f || rect.height() <= 12f) return

        val mainBmp = p1?.takeIf { !it.isRecycled }
        val badgeBmp = p2?.takeIf { !it.isRecycled }
            ?: if (doc.fillEmptyWithFirst) mainBmp else null

        val refW = 1080f
        val refH = 920f
        val scale = min(rect.width() / refW, rect.height() / refH) * 0.95f

        canvas.save()
        canvas.translate(rect.centerX(), rect.centerY())
        canvas.scale(scale, scale)

        // Floor shadow
        canvas.drawOval(
            RectF(-470f, 340f, 470f, 445f),
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.argb(150, 0, 0, 0)
                maskFilter = BlurMaskFilter(45f, BlurMaskFilter.Blur.NORMAL)
            }
        )

        val boldTf = PostFonts.get(context, PostFont.BOLD, "STREAK & XP", doc)

        // 1. Main 3D Study Streak & XP Dashboard Card on the Left (Photo 1)
        canvas.save()
        canvas.translate(-120f, -15f)
        val camLeft = Camera()
        camLeft.save()
        camLeft.setLocation(0f, 0f, -28f)
        camLeft.rotateX(9f)
        camLeft.rotateY(12f)
        camLeft.rotateZ(-3.5f)
        val mLeft = Matrix()
        camLeft.getMatrix(mLeft)
        camLeft.restore()
        canvas.concat(mLeft)

        val cardRect = RectF(-345f, -395f, 345f, 395f)
        val cardR = 36f

        // 3D Metallic/Glass Extrusion
        val extPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = blendColors(accentColor, Color.rgb(15, 23, 42), 0.55f)
        }
        for (s in 10 downTo 1) {
            val f = s * 2.0f
            canvas.drawRoundRect(
                RectF(cardRect.left - f, cardRect.top + f, cardRect.right - f, cardRect.bottom + f),
                cardR, cardR, extPaint
            )
        }

        val cClip = Path().apply { addRoundRect(cardRect, cardR, cardR, Path.Direction.CW) }
        canvas.save()
        canvas.clipPath(cClip)
        canvas.drawColor(Color.rgb(15, 23, 42))

        // Top 7-Day Study Streak Header Bar (M T W T F S S)
        val streakHeader = RectF(cardRect.left, cardRect.top, cardRect.right, cardRect.top + 135f)
        canvas.drawRect(
            streakHeader,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = LinearGradient(
                    streakHeader.left, streakHeader.top, streakHeader.right, streakHeader.bottom,
                    blendColors(accentColor, Color.rgb(245, 158, 11), 0.4f),
                    Color.rgb(15, 23, 42),
                    Shader.TileMode.CLAMP
                )
            }
        )
        val titlePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = boldTf
            color = Color.WHITE
            textSize = 22f
            letterSpacing = 0.08f
        }
        canvas.drawText("🔥 21-DAY STUDY STREAK · COHORT 2026", cardRect.left + 32f, cardRect.top + 42f, titlePaint)

        val days = arrayOf("M", "T", "W", "T", "F", "S", "S")
        val dayStartX = cardRect.left + 58f
        val dayStepX = 95f
        for (d in 0..6) {
            val dcx = dayStartX + d * dayStepX
            val dcy = cardRect.top + 92f
            val activeDay = d < 6
            canvas.drawCircle(
                dcx, dcy, 24f,
                Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = if (activeDay) Color.rgb(245, 158, 11) else Color.argb(70, 255, 255, 255)
                }
            )
            val dPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                typeface = boldTf
                color = if (activeDay) Color.rgb(15, 23, 42) else Color.WHITE
                textSize = 19f
            }
            val lbl = if (activeDay) "✓" else days[d]
            canvas.drawText(lbl, dcx - dPaint.measureText(lbl) * 0.5f, dcy + 7f, dPaint)
        }

        // Center Photo 1 Preview Window
        val photoRect = RectF(cardRect.left + 26f, cardRect.top + 148f, cardRect.right - 26f, cardRect.bottom - 102f)
        val pClip = Path().apply { addRoundRect(photoRect, 22f, 22f, Path.Direction.CW) }
        canvas.save()
        canvas.clipPath(pClip)
        if (mainBmp != null) {
            val adj0 = doc.slotAdjust(0)
            canvas.drawRect(photoRect, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE })
            drawBitmapCenterCrop(canvas, mainBmp, photoRect, adj0.zoom, adj0.focusX, adj0.focusY)
        } else {
            drawStudyPagePlaceholder(
                context = context,
                canvas = canvas,
                pageRect = photoRect,
                slotIndex = 0,
                layerIdx = 0,
                isOverlapped = false,
                subjectTag = "COHORT PREVIEW · PHOTO 1",
                doc = doc,
                accentColor = accentColor,
                warmPaper = false
            )
        }
        canvas.restore()

        // Bottom XP Level Progress Bar inside the main card
        val xpLabelPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = boldTf
            color = Color.WHITE
            textSize = 19f
        }
        canvas.drawText("LEVEL 12 SCHOLAR", cardRect.left + 32f, cardRect.bottom - 58f, xpLabelPaint)
        val xpValPaint = TextPaint(xpLabelPaint).apply { color = Color.rgb(16, 185, 129) }
        val xpText = "2,450 / 2,500 XP"
        canvas.drawText(xpText, cardRect.right - 32f - xpValPaint.measureText(xpText), cardRect.bottom - 58f, xpValPaint)

        val trackBar = RectF(cardRect.left + 32f, cardRect.bottom - 42f, cardRect.right - 32f, cardRect.bottom - 22f)
        canvas.drawRoundRect(trackBar, 10f, 10f, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(30, 41, 59) })
        val fillBar = RectF(trackBar.left, trackBar.top, trackBar.left + trackBar.width() * 0.88f, trackBar.bottom)
        canvas.drawRoundRect(
            fillBar, 10f, 10f,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = LinearGradient(
                    fillBar.left, 0f, fillBar.right, 0f,
                    accentColor, Color.rgb(16, 185, 129), Shader.TileMode.CLAMP
                )
            }
        )
        canvas.restore()

        canvas.drawRoundRect(
            cardRect, cardR, cardR,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = 3f
                color = withAlpha(accentColor, 220)
            }
        )
        canvas.restore()

        // 2. Overlapping Floating 3D Achievement / Bonus Photo Card on the Right (Photo 2)
        canvas.save()
        canvas.translate(255f, 105f)
        val camRight = Camera()
        camRight.save()
        camRight.setLocation(0f, 0f, -28f)
        camRight.rotateX(6f)
        camRight.rotateY(-13f)
        camRight.rotateZ(3.5f)
        val mRight = Matrix()
        camRight.getMatrix(mRight)
        camRight.restore()
        canvas.concat(mRight)

        val subRect = RectF(-245f, -285f, 245f, 285f)
        val subR = 34f
        canvas.drawRoundRect(
            RectF(subRect.left - 24f, subRect.top + 24f, subRect.right - 24f, subRect.bottom + 24f),
            subR, subR,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.argb(185, 0, 0, 0)
                maskFilter = BlurMaskFilter(32f, BlurMaskFilter.Blur.NORMAL)
            }
        )

        val sClip = Path().apply { addRoundRect(subRect, subR, subR, Path.Direction.CW) }
        canvas.save()
        canvas.clipPath(sClip)
        if (badgeBmp != null) {
            val adj1 = doc.slotAdjust(if (p2 != null) 1 else 0)
            canvas.drawRect(subRect, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE })
            drawBitmapCenterCrop(canvas, badgeBmp, subRect, adj1.zoom, adj1.focusX, adj1.focusY)
        } else {
            drawStudyPagePlaceholder(
                context = context,
                canvas = canvas,
                pageRect = subRect,
                slotIndex = 1,
                layerIdx = 3,
                isOverlapped = false,
                subjectTag = "COHORT BADGE · PHOTO 2",
                doc = doc,
                accentColor = Color.rgb(16, 185, 129),
                warmPaper = false
            )
        }
        canvas.restore()

        canvas.drawRoundRect(
            subRect, subR, subR,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = 3.5f
                color = Color.rgb(16, 185, 129)
            }
        )

        val pillRect = RectF(-175f, subRect.top - 26f, 175f, subRect.top + 26f)
        canvas.drawRoundRect(pillRect, 26f, 26f, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(16, 185, 129) })
        val pillPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = boldTf
            color = Color.WHITE
            textSize = 19f
        }
        val pfm = pillPaint.fontMetrics
        val pLbl = "⚡ LEVEL UP · +250 XP"
        canvas.drawText(
            pLbl,
            pillRect.centerX() - pillPaint.measureText(pLbl) * 0.5f,
            pillRect.centerY() - (pfm.ascent + pfm.descent) * 0.5f,
            pillPaint
        )

        canvas.restore()
        canvas.restore()
    }

    // ========================================================================
    // VARIANT 18: 3D STUDENT SCHOLAR ID & VIP LANYARD PASS (2 PHOTO SLOTS)
    // ========================================================================
    private fun drawScholarIdLanyard3D(
        context: Context,
        canvas: Canvas,
        rect: RectF,
        doc: PostDoc,
        p1: Bitmap?,
        p2: Bitmap?,
        accentColor: Int,
        secondaryColor: Int,
        lightColor: Int,
        darkColor: Int,
        deepColor: Int
    ) {
        if (rect.width() <= 12f || rect.height() <= 12f) return

        val mainBmp = p1?.takeIf { !it.isRecycled }
        val secondBmp = p2?.takeIf { !it.isRecycled }
            ?: if (doc.fillEmptyWithFirst) mainBmp else null

        val refW = 1080f
        val refH = 960f
        val scale = min(rect.width() / refW, rect.height() / refH) * 0.95f

        canvas.save()
        canvas.translate(rect.centerX(), rect.centerY())
        canvas.scale(scale, scale)

        // Floor shadow
        canvas.drawOval(
            RectF(-440f, 365f, 440f, 460f),
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.argb(150, 0, 0, 0)
                maskFilter = BlurMaskFilter(45f, BlurMaskFilter.Blur.NORMAL)
            }
        )

        val boldTf = PostFonts.get(context, PostFont.BOLD, "SCHOLAR ID PASS", doc)

        // 1. Background/Companion VIP Pass Card tilted on the Right (Photo 2)
        canvas.save()
        canvas.translate(220f, 75f)
        val camBack = Camera()
        camBack.save()
        camBack.setLocation(0f, 0f, -28f)
        camBack.rotateX(6f)
        camBack.rotateY(-14f)
        camBack.rotateZ(8f)
        val mBack = Matrix()
        camBack.getMatrix(mBack)
        camBack.restore()
        canvas.concat(mBack)

        val backCard = RectF(-235f, -330f, 235f, 330f)
        val backR = 32f
        for (s in 7 downTo 1) {
            val f = s * 1.8f
            canvas.drawRoundRect(
                RectF(backCard.left + f, backCard.top + f, backCard.right + f, backCard.bottom + f),
                backR, backR,
                Paint(Paint.ANTI_ALIAS_FLAG).apply { color = blendColors(accentColor, Color.BLACK, 0.55f) }
            )
        }
        val bClip = Path().apply { addRoundRect(backCard, backR, backR, Path.Direction.CW) }
        canvas.save()
        canvas.clipPath(bClip)
        if (secondBmp != null) {
            val adj1 = doc.slotAdjust(if (p2 != null) 1 else 0)
            canvas.drawRect(backCard, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE })
            drawBitmapCenterCrop(canvas, secondBmp, backCard, adj1.zoom, adj1.focusX, adj1.focusY)
        } else {
            drawStudyPagePlaceholder(
                context = context,
                canvas = canvas,
                pageRect = backCard,
                slotIndex = 1,
                layerIdx = 1,
                isOverlapped = false,
                subjectTag = "VIP SEMINAR ACCESS · PHOTO 2",
                doc = doc,
                accentColor = accentColor,
                warmPaper = false
            )
        }
        canvas.restore()
        canvas.drawRoundRect(
            backCard, backR, backR,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = 3f
                color = accentColor
            }
        )
        canvas.restore()

        // 2. Primary 3D Acrylic Student Scholar ID Badge + Woven Neck Lanyard & Metallic Clasp (Photo 1)
        canvas.save()
        canvas.translate(-110f, 25f)
        val camFront = Camera()
        camFront.save()
        camFront.setLocation(0f, 0f, -28f)
        camFront.rotateX(8f)
        camFront.rotateY(11f)
        camFront.rotateZ(-4f)
        val mFront = Matrix()
        camFront.getMatrix(mFront)
        camFront.restore()
        canvas.concat(mFront)

        // Woven V-Neck Lanyard Ribbons rising from top clasp
        val lanyardLeft = Path().apply {
            moveTo(-140f, -495f)
            lineTo(-82f, -495f)
            lineTo(-14f, -365f)
            lineTo(-64f, -365f)
            close()
        }
        val lanyardRight = Path().apply {
            moveTo(140f, -495f)
            lineTo(82f, -495f)
            lineTo(14f, -365f)
            lineTo(64f, -365f)
            close()
        }
        canvas.drawPath(
            lanyardLeft,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = LinearGradient(
                    -140f, -495f, -14f, -365f,
                    blendColors(accentColor, Color.rgb(15, 23, 42), 0.35f),
                    accentColor,
                    Shader.TileMode.CLAMP
                )
            }
        )
        canvas.drawPath(
            lanyardRight,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = LinearGradient(
                    140f, -495f, 14f, -365f,
                    blendColors(accentColor, Color.rgb(15, 23, 42), 0.55f),
                    blendColors(accentColor, Color.WHITE, 0.15f),
                    Shader.TileMode.CLAMP
                )
            }
        )

        // Brushed-Steel Lobster Clasp & Badge Slot Bracket
        val claspPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                -35f, -370f, 35f, -310f,
                intArrayOf(Color.rgb(226, 232, 240), Color.rgb(100, 116, 139), Color.rgb(241, 245, 249)),
                null, Shader.TileMode.CLAMP
            )
        }
        canvas.drawRoundRect(RectF(-34f, -372f, 34f, -346f), 8f, 8f, claspPaint)
        canvas.drawRoundRect(RectF(-14f, -348f, 14f, -314f), 7f, 7f, claspPaint)

        val idRect = RectF(-295f, -335f, 295f, 385f)
        val idR = 36f

        // Cast shadow onto companion pass
        canvas.drawRoundRect(
            RectF(idRect.left + 22f, idRect.top + 24f, idRect.right + 22f, idRect.bottom + 24f),
            idR, idR,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.argb(180, 0, 0, 0)
                maskFilter = BlurMaskFilter(34f, BlurMaskFilter.Blur.NORMAL)
            }
        )

        // 3D PVC Card Thickness Extrusion
        for (s in 9 downTo 1) {
            val f = s * 1.9f
            canvas.drawRoundRect(
                RectF(idRect.left - f, idRect.top + f, idRect.right - f, idRect.bottom + f),
                idR, idR,
                Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(203, 213, 225) }
            )
        }

        val idClip = Path().apply { addRoundRect(idRect, idR, idR, Path.Direction.CW) }
        canvas.save()
        canvas.clipPath(idClip)
        canvas.drawColor(Color.rgb(248, 250, 252))

        // Top Colored Badge Header Band with Lanyard Slot Punch
        val topBand = RectF(idRect.left, idRect.top, idRect.right, idRect.top + 102f)
        canvas.drawRect(
            topBand,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = LinearGradient(
                    topBand.left, topBand.top, topBand.right, topBand.bottom,
                    accentColor, blendColors(accentColor, Color.rgb(15, 23, 42), 0.45f), Shader.TileMode.CLAMP
                )
            }
        )
        // Lanyard slot hole punch
        canvas.drawRoundRect(
            RectF(-52f, idRect.top + 18f, 52f, idRect.top + 36f),
            9f, 9f,
            Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(15, 23, 42) }
        )

        val badgeTitlePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = boldTf
            color = Color.WHITE
            textSize = 21f
            letterSpacing = 0.12f
        }
        val bTitle = "VERIFIED SCHOLAR PASS · 2026"
        canvas.drawText(bTitle, -badgeTitlePaint.measureText(bTitle) * 0.5f, idRect.top + 78f, badgeTitlePaint)

        // Main Photo 1 Window inside the Scholar ID Card
        val photoBox = RectF(idRect.left + 28f, idRect.top + 122f, idRect.right - 28f, idRect.bottom - 118f)
        val pClip = Path().apply { addRoundRect(photoBox, 22f, 22f, Path.Direction.CW) }
        canvas.save()
        canvas.clipPath(pClip)
        if (mainBmp != null) {
            val adj0 = doc.slotAdjust(0)
            canvas.drawRect(photoBox, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE })
            drawBitmapCenterCrop(canvas, mainBmp, photoBox, adj0.zoom, adj0.focusX, adj0.focusY)
        } else {
            drawStudyPagePlaceholder(
                context = context,
                canvas = canvas,
                pageRect = photoBox,
                slotIndex = 0,
                layerIdx = 0,
                isOverlapped = false,
                subjectTag = "SCHOLAR ID · PHOTO 1",
                doc = doc,
                accentColor = accentColor,
                warmPaper = false
            )
        }
        canvas.restore()

        // Bottom Holographic Seal & Barcode Strip
        canvas.drawCircle(
            idRect.left + 72f, idRect.bottom - 60f, 34f,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = RadialGradient(
                    idRect.left + 64f, idRect.bottom - 68f, 40f,
                    intArrayOf(Color.rgb(253, 224, 71), Color.rgb(245, 158, 11), Color.rgb(180, 83, 9)),
                    null, Shader.TileMode.CLAMP
                )
            }
        )
        // Simulated Barcode Lines on bottom-right
        val barPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(15, 23, 42) }
        var bx = idRect.left + 135f
        var stepIdx = 0
        val widths = floatArrayOf(5f, 3f, 8f, 4f, 6f, 3f, 9f, 4f)
        while (bx < idRect.right - 36f) {
            val bw = widths[stepIdx % widths.size]
            canvas.drawRect(RectF(bx, idRect.bottom - 86f, bx + bw, idRect.bottom - 34f), barPaint)
            bx += bw + 6f
            stepIdx++
        }

        canvas.restore() // end idClip

        canvas.drawRoundRect(
            idRect, idR, idR,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = 3f
                color = Color.WHITE
            }
        )

        canvas.restore()
        canvas.restore()
    }

    // ========================================================================
    // VARIANT 19: 3D FRAMED CERTIFICATE OF EXCELLENCE & WAX SEAL PLAQUE (2 PHOTO SLOTS)
    // ========================================================================
    private fun drawFramedCertificateSeal3D(
        context: Context,
        canvas: Canvas,
        rect: RectF,
        doc: PostDoc,
        p1: Bitmap?,
        p2: Bitmap?,
        accentColor: Int,
        secondaryColor: Int,
        lightColor: Int,
        darkColor: Int,
        deepColor: Int
    ) {
        if (rect.width() <= 12f || rect.height() <= 12f) return

        val mainBmp = p1?.takeIf { !it.isRecycled }
        val secondBmp = p2?.takeIf { !it.isRecycled }
            ?: if (doc.fillEmptyWithFirst) mainBmp else null

        val refW = 1080f
        val refH = 920f
        val scale = min(rect.width() / refW, rect.height() / refH) * 0.95f

        canvas.save()
        canvas.translate(rect.centerX(), rect.centerY())
        canvas.scale(scale, scale)

        // Floor shadow
        canvas.drawOval(
            RectF(-475f, 340f, 475f, 445f),
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.argb(155, 0, 0, 0)
                maskFilter = BlurMaskFilter(45f, BlurMaskFilter.Blur.NORMAL)
            }
        )

        val boldTf = PostFonts.get(context, PostFont.BOLD, "HONOUR ROLL CERTIFICATE", doc)

        // 1. Main 3D Mahogany & Brushed-Gold Certificate Plaque on the Left (Photo 1)
        canvas.save()
        canvas.translate(-110f, -20f)
        val camPlaque = Camera()
        camPlaque.save()
        camPlaque.setLocation(0f, 0f, -28f)
        camPlaque.rotateX(9f)
        camPlaque.rotateY(11f)
        camPlaque.rotateZ(-3f)
        val mPlaque = Matrix()
        camPlaque.getMatrix(mPlaque)
        camPlaque.restore()
        canvas.concat(mPlaque)

        val frameOuter = RectF(-365f, -380f, 365f, 380f)
        val frameR = 28f

        // 3D Hardwood Plaque Extrusion
        for (s in 11 downTo 1) {
            val f = s * 2.1f
            canvas.drawRoundRect(
                RectF(frameOuter.left - f, frameOuter.top + f, frameOuter.right - f, frameOuter.bottom + f),
                frameR, frameR,
                Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(28, 19, 14) }
            )
        }

        // Mahogany & Brushed-Gold Outer Frame
        canvas.drawRoundRect(
            frameOuter, frameR, frameR,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = LinearGradient(
                    frameOuter.left, frameOuter.top, frameOuter.right, frameOuter.bottom,
                    Color.rgb(68, 40, 24), Color.rgb(34, 20, 12), Shader.TileMode.CLAMP
                )
            }
        )
        // Inner Gold Fillet Trim
        val goldTrimRect = RectF(frameOuter.left + 20f, frameOuter.top + 20f, frameOuter.right - 20f, frameOuter.bottom - 20f)
        canvas.drawRoundRect(
            goldTrimRect, 20f, 20f,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = 6f
                shader = LinearGradient(
                    goldTrimRect.left, goldTrimRect.top, goldTrimRect.right, goldTrimRect.bottom,
                    intArrayOf(Color.rgb(253, 224, 71), Color.rgb(217, 119, 6), Color.rgb(253, 224, 71)),
                    null, Shader.TileMode.CLAMP
                )
            }
        )

        // Certificate Parchment & Photo 1 Window
        val certRect = RectF(frameOuter.left + 32f, frameOuter.top + 32f, frameOuter.right - 32f, frameOuter.bottom - 32f)
        val cClip = Path().apply { addRoundRect(certRect, 14f, 14f, Path.Direction.CW) }
        canvas.save()
        canvas.clipPath(cClip)
        if (mainBmp != null) {
            val adj0 = doc.slotAdjust(0)
            canvas.drawRect(certRect, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE })
            drawBitmapCenterCrop(canvas, mainBmp, certRect, adj0.zoom, adj0.focusX, adj0.focusY)
        } else {
            drawStudyPagePlaceholder(
                context = context,
                canvas = canvas,
                pageRect = certRect,
                slotIndex = 0,
                layerIdx = 0,
                isOverlapped = false,
                subjectTag = "CERTIFICATE OF EXCELLENCE",
                doc = doc,
                accentColor = Color.rgb(217, 119, 6),
                warmPaper = true
            )
        }
        canvas.restore()

        // 3D Embossed Gold Foil Seal & Twin Silk Ribbons at Bottom-Left of Certificate
        val sealCx = certRect.left + 110f
        val sealCy = certRect.bottom - 88f
        val ribbonPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(225, 29, 72) }
        val leftRib = Path().apply {
            moveTo(sealCx - 18f, sealCy + 24f)
            lineTo(sealCx - 48f, sealCy + 135f)
            lineTo(sealCx - 22f, sealCy + 118f)
            lineTo(sealCx - 4f, sealCy + 138f)
            lineTo(sealCx + 6f, sealCy + 28f)
            close()
        }
        val rightRib = Path().apply {
            moveTo(sealCx - 4f, sealCy + 28f)
            lineTo(sealCx + 10f, sealCy + 138f)
            lineTo(sealCx + 28f, sealCy + 118f)
            lineTo(sealCx + 52f, sealCy + 132f)
            lineTo(sealCx + 22f, sealCy + 24f)
            close()
        }
        canvas.drawPath(leftRib, ribbonPaint)
        canvas.drawPath(rightRib, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(190, 18, 60) })

        // Starburst serrated gold foil seal
        val sealPath = Path()
        val teeth = 24
        val outerR = 64f
        val innerR = 54f
        for (t in 0 until teeth * 2) {
            val r = if (t % 2 == 0) outerR else innerR
            val ang = Math.toRadians((t * 360.0 / (teeth * 2)))
            val px = sealCx + (r * cos(ang)).toFloat()
            val py = sealCy + (r * sin(ang)).toFloat()
            if (t == 0) sealPath.moveTo(px, py) else sealPath.lineTo(px, py)
        }
        sealPath.close()
        canvas.drawPath(
            sealPath,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = RadialGradient(
                    sealCx - 16f, sealCy - 16f, outerR * 1.2f,
                    intArrayOf(Color.rgb(254, 240, 138), Color.rgb(245, 158, 11), Color.rgb(180, 83, 9)),
                    null, Shader.TileMode.CLAMP
                )
            }
        )
        canvas.drawCircle(
            sealCx, sealCy, 42f,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = 2.5f
                color = Color.rgb(120, 53, 15)
            }
        )
        val sealTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = boldTf
            color = Color.rgb(120, 53, 15)
            textSize = 20f
        }
        canvas.drawText("★ A+ ★", sealCx - sealTextPaint.measureText("★ A+ ★") * 0.5f, sealCy + 7f, sealTextPaint)

        canvas.restore()

        // 2. Overlapping Floating 3D Honour Roll / Secondary Photo Card on the Right (Photo 2)
        canvas.save()
        canvas.translate(260f, 105f)
        val camRight = Camera()
        camRight.save()
        camRight.setLocation(0f, 0f, -28f)
        camRight.rotateX(6f)
        camRight.rotateY(-13f)
        camRight.rotateZ(4f)
        val mRight = Matrix()
        camRight.getMatrix(mRight)
        camRight.restore()
        canvas.concat(mRight)

        val subRect = RectF(-240f, -280f, 240f, 280f)
        val subR = 32f
        canvas.drawRoundRect(
            RectF(subRect.left - 24f, subRect.top + 24f, subRect.right - 24f, subRect.bottom + 24f),
            subR, subR,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.argb(185, 0, 0, 0)
                maskFilter = BlurMaskFilter(32f, BlurMaskFilter.Blur.NORMAL)
            }
        )

        val sClip = Path().apply { addRoundRect(subRect, subR, subR, Path.Direction.CW) }
        canvas.save()
        canvas.clipPath(sClip)
        if (secondBmp != null) {
            val adj1 = doc.slotAdjust(if (p2 != null) 1 else 0)
            canvas.drawRect(subRect, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE })
            drawBitmapCenterCrop(canvas, secondBmp, subRect, adj1.zoom, adj1.focusX, adj1.focusY)
        } else {
            drawStudyPagePlaceholder(
                context = context,
                canvas = canvas,
                pageRect = subRect,
                slotIndex = 1,
                layerIdx = 1,
                isOverlapped = false,
                subjectTag = "HONOUR ROLL · PHOTO 2",
                doc = doc,
                accentColor = Color.rgb(245, 158, 11),
                warmPaper = false
            )
        }
        canvas.restore()

        canvas.drawRoundRect(
            subRect, subR, subR,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = 3.5f
                color = Color.rgb(245, 158, 11)
            }
        )
        canvas.restore()
        canvas.restore()
    }

    // ═══════════════════════════════════════════════════════════════════════
    // VARIANT 20 — 3D VIDEO LECTURE & AUDIO LESSON PLAYER (2 Photo Slots)
    // Tilted 3D media lecture player deck with Photo 1 video screen, play
    // button badge, audio waveform & chapter timeline, plus floating 3D
    // key-takeaway / transcript card with Photo 2.
    // ═══════════════════════════════════════════════════════════════════════
    private fun drawVideoLecturePlayer3D(
        context: Context,
        canvas: Canvas,
        rect: RectF,
        doc: PostDoc,
        p1: Bitmap?,
        p2: Bitmap?,
        accentColor: Int,
        secondaryColor: Int,
        lightColor: Int,
        darkColor: Int,
        deepColor: Int
    ) {
        val w = rect.width()
        val h = rect.height()
        if (w < 40f || h < 40f) return

        val cx = rect.centerX()
        val cy = rect.centerY()

        val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(
                cx, cy, max(w, h) * 0.56f,
                intArrayOf(
                    withAlpha(accentColor, 100),
                    withAlpha(secondaryColor, 45),
                    Color.TRANSPARENT
                ),
                floatArrayOf(0f, 0.58f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawCircle(cx, cy, max(w, h) * 0.56f, glowPaint)

        val baseW = 1000f
        val baseH = 980f
        val fitScale = min(w / baseW, h / baseH) * 0.94f

        canvas.save()
        canvas.translate(cx, cy)
        canvas.scale(fitScale, fitScale)

        val camera = Camera()
        val matrix = Matrix()
        camera.save()
        camera.setLocation(0f, 0f, -34f)
        camera.rotateX(11f)
        camera.rotateY(-13f)
        camera.rotateZ(3f)
        camera.getMatrix(matrix)
        camera.restore()

        val pRect = RectF(-450f, -380f, 340f, 330f)
        val pR = 42f

        canvas.save()
        canvas.concat(matrix)

        // Deep drop shadow
        canvas.drawRoundRect(
            RectF(pRect.left + 26f, pRect.top + 38f, pRect.right + 26f, pRect.bottom + 38f),
            pR, pR,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.argb(190, 0, 0, 0)
                maskFilter = BlurMaskFilter(44f, BlurMaskFilter.Blur.NORMAL)
            }
        )

        // 3D extruded glass-slate thickness
        val depthLayers = 13
        val edgePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        for (d in depthLayers downTo 1) {
            val t = d.toFloat() / depthLayers
            edgePaint.color = blendColors(blendColors(deepColor, Color.BLACK, 0.45f), accentColor, (1f - t) * 0.35f)
            canvas.drawRoundRect(
                RectF(pRect.left - d * 1.4f, pRect.top + d * 1.9f, pRect.right - d * 1.4f, pRect.bottom + d * 1.9f),
                pR, pR,
                edgePaint
            )
        }

        // Main player deck surface
        canvas.drawRoundRect(
            pRect, pR, pR,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = LinearGradient(
                    pRect.left, pRect.top, pRect.right, pRect.bottom,
                    intArrayOf(
                        blendColors(darkColor, Color.rgb(18, 26, 46), 0.55f),
                        blendColors(deepColor, Color.rgb(10, 14, 28), 0.72f)
                    ),
                    null,
                    Shader.TileMode.CLAMP
                )
            }
        )

        // Top header bar with LIVE HD LECTURE chip & speed badge
        val chipRect = RectF(pRect.left + 32f, pRect.top + 26f, pRect.left + 335f, pRect.top + 74f)
        canvas.drawRoundRect(
            chipRect, 24f, 24f,
            Paint(Paint.ANTI_ALIAS_FLAG).apply { color = withAlpha(accentColor, 42) }
        )
        canvas.drawCircle(
            chipRect.left + 24f, chipRect.centerY(), 7.5f,
            Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(239, 68, 68) }
        )
        canvas.drawText(
            "HD VIDEO LECTURE · CH. 03",
            chipRect.left + 44f, chipRect.centerY() + 7f,
            TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.WHITE
                textSize = 18.5f
                typeface = PostFonts.get(context, PostFont.BOLD, "HD", doc)
            }
        )

        val speedRect = RectF(pRect.right - 155f, pRect.top + 26f, pRect.right - 32f, pRect.top + 74f)
        canvas.drawRoundRect(
            speedRect, 24f, 24f,
            Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.argb(40, 255, 255, 255) }
        )
        canvas.drawText(
            "1.5x · CC",
            speedRect.centerX(), speedRect.centerY() + 7f,
            TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                color = accentColor
                textSize = 19f
                textAlign = Paint.Align.CENTER
                typeface = PostFonts.get(context, PostFont.BOLD, "1.5x", doc)
            }
        )

        // Video screen frame (Photo 1)
        val screenRect = RectF(pRect.left + 32f, pRect.top + 92f, pRect.right - 32f, pRect.bottom - 165f)
        val screenClip = Path().apply { addRoundRect(screenRect, 26f, 26f, Path.Direction.CW) }
        canvas.save()
        canvas.clipPath(screenClip)
        if (p1 != null) {
            val adj0 = doc.slotAdjust(0)
            canvas.drawRect(screenRect, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.BLACK })
            drawBitmapCenterCrop(canvas, p1, screenRect, adj0.zoom, adj0.focusX, adj0.focusY)
        } else {
            drawStudyPagePlaceholder(
                context = context,
                canvas = canvas,
                pageRect = screenRect,
                slotIndex = 0,
                layerIdx = 0,
                isOverlapped = false,
                subjectTag = "VIDEO LESSON · PHOTO 1",
                doc = doc,
                accentColor = accentColor,
                warmPaper = false
            )
        }

        // Translucent play button overlay in bottom-left of video frame
        val playCx = screenRect.left + 64f
        val playCy = screenRect.bottom - 56f
        canvas.drawCircle(
            playCx, playCy, 34f,
            Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.argb(185, 10, 16, 30) }
        )
        canvas.drawCircle(
            playCx, playCy, 34f,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = 3f
                color = accentColor
            }
        )
        val playTri = Path().apply {
            moveTo(playCx - 9f, playCy - 14f)
            lineTo(playCx + 15f, playCy)
            lineTo(playCx - 9f, playCy + 14f)
            close()
        }
        canvas.drawPath(playTri, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE })
        canvas.restore()

        // Screen border
        canvas.drawRoundRect(
            screenRect, 26f, 26f,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = 2.8f
                color = withAlpha(accentColor, 170)
            }
        )

        // Audio waveform & timeline scrub bar below screen
        val waveTop = screenRect.bottom + 24f
        val waveBottom = screenRect.bottom + 76f
        val barCount = 34
        val totalBarW = screenRect.width()
        val stepW = totalBarW / barCount
        val wavePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        for (i in 0 until barCount) {
            val bx = screenRect.left + i * stepW + stepW * 0.25f
            val frac = 0.28f + 0.72f * kotlin.math.abs(sin(i * 0.55f))
            val bh = (waveBottom - waveTop) * frac
            val byMid = (waveTop + waveBottom) * 0.5f
            wavePaint.color = if (i < 21) accentColor else Color.argb(65, 255, 255, 255)
            canvas.drawRoundRect(
                RectF(bx, byMid - bh * 0.5f, bx + stepW * 0.52f, byMid + bh * 0.5f),
                5f, 5f,
                wavePaint
            )
        }

        // Chapter timeline text
        canvas.drawText(
            "18:42 / 32:15 · EXAM TOPIC WALKTHROUGH",
            screenRect.left, pRect.bottom - 36f,
            TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.argb(215, 255, 255, 255)
                textSize = 20f
                typeface = PostFonts.get(context, PostFont.BOLD, "18:42", doc)
            }
        )

        canvas.drawRoundRect(
            pRect, pR, pR,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = 3.5f
                color = withAlpha(accentColor, 195)
            }
        )
        canvas.restore()

        // Floating 3D Key Takeaway / Lesson Notes Card (Photo 2)
        val secondBmp = p2 ?: p1
        canvas.save()
        val cam2 = Camera()
        val mat2 = Matrix()
        cam2.save()
        cam2.setLocation(0f, 0f, -34f)
        cam2.rotateX(8f)
        cam2.rotateY(-20f)
        cam2.rotateZ(-5f)
        cam2.getMatrix(mat2)
        cam2.restore()
        canvas.concat(mat2)

        val noteRect = RectF(95f, -75f, 475f, 425f)
        val noteR = 32f
        canvas.drawRoundRect(
            RectF(noteRect.left - 24f, noteRect.top + 26f, noteRect.right - 24f, noteRect.bottom + 26f),
            noteR, noteR,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.argb(185, 0, 0, 0)
                maskFilter = BlurMaskFilter(34f, BlurMaskFilter.Blur.NORMAL)
            }
        )

        val nClip = Path().apply { addRoundRect(noteRect, noteR, noteR, Path.Direction.CW) }
        canvas.save()
        canvas.clipPath(nClip)
        if (secondBmp != null) {
            val adj1 = doc.slotAdjust(if (p2 != null) 1 else 0)
            canvas.drawRect(noteRect, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE })
            drawBitmapCenterCrop(canvas, secondBmp, noteRect, adj1.zoom, adj1.focusX, adj1.focusY)
        } else {
            drawStudyPagePlaceholder(
                context = context,
                canvas = canvas,
                pageRect = noteRect,
                slotIndex = 1,
                layerIdx = 1,
                isOverlapped = false,
                subjectTag = "LECTURE NOTES · PHOTO 2",
                doc = doc,
                accentColor = secondaryColor,
                warmPaper = true
            )
        }
        canvas.restore()

        canvas.drawRoundRect(
            noteRect, noteR, noteR,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = 4f
                color = accentColor
            }
        )
        canvas.restore()

        canvas.restore()
    }

    // ═══════════════════════════════════════════════════════════════════════
    // VARIANT 21 — 3D TUTOR SPOTLIGHT & LIVE MASTERCLASS CARD (2 Photo Slots)
    // 3D angled Instructor / Keynote Tutor stage card with Photo 1 tutor/session
    // portrait, gold verified mentor badge, 5-star rating pill, and overlapping
    // 3D floating syllabus / class preview card with Photo 2.
    // ═══════════════════════════════════════════════════════════════════════
    private fun drawTutorSpotlightCard3D(
        context: Context,
        canvas: Canvas,
        rect: RectF,
        doc: PostDoc,
        p1: Bitmap?,
        p2: Bitmap?,
        accentColor: Int,
        secondaryColor: Int,
        lightColor: Int,
        darkColor: Int,
        deepColor: Int
    ) {
        val w = rect.width()
        val h = rect.height()
        if (w < 40f || h < 40f) return

        val cx = rect.centerX()
        val cy = rect.centerY()

        val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(
                cx, cy, max(w, h) * 0.56f,
                intArrayOf(
                    withAlpha(accentColor, 105),
                    withAlpha(secondaryColor, 48),
                    Color.TRANSPARENT
                ),
                floatArrayOf(0f, 0.6f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawCircle(cx, cy, max(w, h) * 0.56f, glowPaint)

        val baseW = 1000f
        val baseH = 1000f
        val fitScale = min(w / baseW, h / baseH) * 0.94f

        canvas.save()
        canvas.translate(cx, cy)
        canvas.scale(fitScale, fitScale)

        val camera = Camera()
        val matrix = Matrix()
        camera.save()
        camera.setLocation(0f, 0f, -34f)
        camera.rotateX(10f)
        camera.rotateY(13f)
        camera.rotateZ(-3f)
        camera.getMatrix(matrix)
        camera.restore()

        val cardRect = RectF(-430f, -410f, 250f, 390f)
        val cardR = 44f

        canvas.save()
        canvas.concat(matrix)

        canvas.drawRoundRect(
            RectF(cardRect.left + 26f, cardRect.top + 38f, cardRect.right + 26f, cardRect.bottom + 38f),
            cardR, cardR,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.argb(185, 0, 0, 0)
                maskFilter = BlurMaskFilter(44f, BlurMaskFilter.Blur.NORMAL)
            }
        )

        // 3D gold-and-slate extruded rim
        val goldMid = Color.rgb(245, 158, 11)
        val depthSteps = 12
        val edgePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        for (d in depthSteps downTo 1) {
            val t = d.toFloat() / depthSteps
            edgePaint.color = blendColors(deepColor, goldMid, (1f - t) * 0.5f)
            canvas.drawRoundRect(
                RectF(cardRect.left - d * 1.4f, cardRect.top + d * 1.8f, cardRect.right - d * 1.4f, cardRect.bottom + d * 1.8f),
                cardR, cardR,
                edgePaint
            )
        }

        // Main stage card body
        canvas.drawRoundRect(
            cardRect, cardR, cardR,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = LinearGradient(
                    cardRect.left, cardRect.top, cardRect.right, cardRect.bottom,
                    intArrayOf(
                        blendColors(darkColor, Color.rgb(22, 30, 52), 0.55f),
                        blendColors(deepColor, Color.rgb(10, 14, 28), 0.72f)
                    ),
                    null,
                    Shader.TileMode.CLAMP
                )
            }
        )

        // Verified Mentor Header Pill
        val badgeRect = RectF(cardRect.left + 32f, cardRect.top + 28f, cardRect.right - 32f, cardRect.top + 78f)
        canvas.drawRoundRect(
            badgeRect, 25f, 25f,
            Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.argb(45, 245, 158, 11) }
        )
        canvas.drawText(
            "★ VERIFIED MASTERCLASS TUTOR",
            badgeRect.centerX(), badgeRect.centerY() + 8f,
            TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(253, 224, 71)
                textSize = 20.5f
                textAlign = Paint.Align.CENTER
                typeface = PostFonts.get(context, PostFont.BOLD, "TUTOR", doc)
            }
        )

        // Main Tutor / Session Photo Window (Photo 1)
        val photoRect = RectF(cardRect.left + 32f, cardRect.top + 98f, cardRect.right - 32f, cardRect.bottom - 125f)
        val photoClip = Path().apply { addRoundRect(photoRect, 28f, 28f, Path.Direction.CW) }
        canvas.save()
        canvas.clipPath(photoClip)
        if (p1 != null) {
            val adj0 = doc.slotAdjust(0)
            canvas.drawRect(photoRect, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE })
            drawBitmapCenterCrop(canvas, p1, photoRect, adj0.zoom, adj0.focusX, adj0.focusY)
        } else {
            drawStudyPagePlaceholder(
                context = context,
                canvas = canvas,
                pageRect = photoRect,
                slotIndex = 0,
                layerIdx = 0,
                isOverlapped = false,
                subjectTag = "TUTOR SPOTLIGHT · PHOTO 1",
                doc = doc,
                accentColor = accentColor,
                warmPaper = false
            )
        }
        canvas.restore()

        canvas.drawRoundRect(
            photoRect, 28f, 28f,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = 3f
                color = goldMid
            }
        )

        // Bottom rating & live students bar
        val ratingRect = RectF(cardRect.left + 32f, cardRect.bottom - 102f, cardRect.right - 32f, cardRect.bottom - 28f)
        canvas.drawRoundRect(
            ratingRect, 22f, 22f,
            Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.argb(38, 255, 255, 255) }
        )
        canvas.drawText(
            "★★★★★ 4.98  ·  LIVE Q&A + EXAM PREP",
            ratingRect.centerX(), ratingRect.centerY() + 8f,
            TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.WHITE
                textSize = 21f
                textAlign = Paint.Align.CENTER
                typeface = PostFonts.get(context, PostFont.BOLD, "4.98", doc)
            }
        )

        canvas.drawRoundRect(
            cardRect, cardR, cardR,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = 3.5f
                color = withAlpha(accentColor, 200)
            }
        )
        canvas.restore()

        // Floating 3D Syllabus / Session Material Card (Photo 2)
        val secondBmp = p2 ?: p1
        canvas.save()
        val cam2 = Camera()
        val mat2 = Matrix()
        cam2.save()
        cam2.setLocation(0f, 0f, -34f)
        cam2.rotateX(8f)
        cam2.rotateY(-18f)
        cam2.rotateZ(5f)
        cam2.getMatrix(mat2)
        cam2.restore()
        canvas.concat(mat2)

        val subRect = RectF(70f, -90f, 465f, 415f)
        val subR = 32f
        canvas.drawRoundRect(
            RectF(subRect.left - 24f, subRect.top + 26f, subRect.right - 24f, subRect.bottom + 26f),
            subR, subR,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.argb(185, 0, 0, 0)
                maskFilter = BlurMaskFilter(34f, BlurMaskFilter.Blur.NORMAL)
            }
        )

        val sClip = Path().apply { addRoundRect(subRect, subR, subR, Path.Direction.CW) }
        canvas.save()
        canvas.clipPath(sClip)
        if (secondBmp != null) {
            val adj1 = doc.slotAdjust(if (p2 != null) 1 else 0)
            canvas.drawRect(subRect, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE })
            drawBitmapCenterCrop(canvas, secondBmp, subRect, adj1.zoom, adj1.focusX, adj1.focusY)
        } else {
            drawStudyPagePlaceholder(
                context = context,
                canvas = canvas,
                pageRect = subRect,
                slotIndex = 1,
                layerIdx = 1,
                isOverlapped = false,
                subjectTag = "SESSION SYLLABUS · PHOTO 2",
                doc = doc,
                accentColor = goldMid,
                warmPaper = true
            )
        }
        canvas.restore()

        canvas.drawRoundRect(
            subRect, subR, subR,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = 4f
                color = goldMid
            }
        )
        canvas.restore()

        canvas.restore()
    }

    // ═══════════════════════════════════════════════════════════════════════
    // VARIANT 22 — 3D MAGNIFYING LENS OVER STUDY SHEET (2 Photo Slots)
    // 3D angled A4 study sheet / exam diagram on the base with Photo 1, and
    // a 3D metallic-and-glass magnifying lens hovering above showing Photo 2
    // (or zoomed detail of Photo 1) inside a crisp circular optical frame.
    // ═══════════════════════════════════════════════════════════════════════
    private fun drawMagnifyingLensSheet3D(
        context: Context,
        canvas: Canvas,
        rect: RectF,
        doc: PostDoc,
        p1: Bitmap?,
        p2: Bitmap?,
        accentColor: Int,
        secondaryColor: Int,
        lightColor: Int,
        darkColor: Int,
        deepColor: Int
    ) {
        val w = rect.width()
        val h = rect.height()
        if (w < 40f || h < 40f) return

        val cx = rect.centerX()
        val cy = rect.centerY()

        val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(
                cx, cy, max(w, h) * 0.56f,
                intArrayOf(
                    withAlpha(accentColor, 95),
                    withAlpha(secondaryColor, 40),
                    Color.TRANSPARENT
                ),
                floatArrayOf(0f, 0.6f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawCircle(cx, cy, max(w, h) * 0.56f, glowPaint)

        val baseW = 1000f
        val baseH = 980f
        val fitScale = min(w / baseW, h / baseH) * 0.94f

        canvas.save()
        canvas.translate(cx, cy)
        canvas.scale(fitScale, fitScale)

        // Base 3D Study Sheet (Photo 1)
        val camera = Camera()
        val matrix = Matrix()
        camera.save()
        camera.setLocation(0f, 0f, -34f)
        camera.rotateX(16f)
        camera.rotateY(-12f)
        camera.rotateZ(-6f)
        camera.getMatrix(matrix)
        camera.restore()

        val sheetRect = RectF(-420f, -390f, 290f, 410f)
        val sheetR = 26f

        canvas.save()
        canvas.concat(matrix)

        canvas.drawRoundRect(
            RectF(sheetRect.left + 26f, sheetRect.top + 38f, sheetRect.right + 26f, sheetRect.bottom + 38f),
            sheetR, sheetR,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.argb(180, 0, 0, 0)
                maskFilter = BlurMaskFilter(42f, BlurMaskFilter.Blur.NORMAL)
            }
        )

        // Stacked paper thickness under main sheet
        for (d in 10 downTo 1) {
            canvas.drawRoundRect(
                RectF(sheetRect.left - d * 1.3f, sheetRect.top + d * 1.9f, sheetRect.right - d * 1.3f, sheetRect.bottom + d * 1.9f),
                sheetR, sheetR,
                Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = if (d % 2 == 0) Color.rgb(218, 224, 236) else Color.rgb(198, 206, 222)
                }
            )
        }

        val sheetClip = Path().apply { addRoundRect(sheetRect, sheetR, sheetR, Path.Direction.CW) }
        canvas.save()
        canvas.clipPath(sheetClip)
        if (p1 != null) {
            val adj0 = doc.slotAdjust(0)
            canvas.drawRect(sheetRect, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE })
            drawBitmapCenterCrop(canvas, p1, sheetRect, adj0.zoom, adj0.focusX, adj0.focusY)
        } else {
            drawStudyPagePlaceholder(
                context = context,
                canvas = canvas,
                pageRect = sheetRect,
                slotIndex = 0,
                layerIdx = 0,
                isOverlapped = false,
                subjectTag = "EXAM STUDY SHEET · PHOTO 1",
                doc = doc,
                accentColor = accentColor,
                warmPaper = true
            )
        }
        canvas.restore()

        canvas.drawRoundRect(
            sheetRect, sheetR, sheetR,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = 3f
                color = withAlpha(accentColor, 175)
            }
        )
        canvas.restore()

        // 3D Hovering Magnifying Glass Lens (Photo 2 or zoomed Photo 1)
        val lensCx = 155f
        val lensCy = -25f
        val lensR = 235f

        // Cast shadow of lens onto study sheet
        canvas.drawCircle(
            lensCx - 28f, lensCy + 48f, lensR * 1.02f,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.argb(155, 0, 0, 0)
                maskFilter = BlurMaskFilter(38f, BlurMaskFilter.Blur.NORMAL)
            }
        )

        // 3D angled ergonomic handle extending to bottom-right
        canvas.save()
        canvas.translate(lensCx, lensCy)
        canvas.rotate(42f)
        val handleRect = RectF(lensR - 12f, -26f, lensR + 235f, 26f)
        // Handle shadow
        canvas.drawRoundRect(
            RectF(handleRect.left, handleRect.top + 18f, handleRect.right, handleRect.bottom + 18f),
            24f, 24f,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.argb(145, 0, 0, 0)
                maskFilter = BlurMaskFilter(22f, BlurMaskFilter.Blur.NORMAL)
            }
        )
        // Metallic collar
        canvas.drawRoundRect(
            RectF(lensR - 14f, -22f, lensR + 48f, 22f),
            8f, 8f,
            Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(212, 220, 232) }
        )
        // Grip body
        canvas.drawRoundRect(
            RectF(lensR + 36f, -26f, handleRect.right, 26f),
            24f, 24f,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = LinearGradient(
                    lensR + 36f, -26f, lensR + 36f, 26f,
                    intArrayOf(
                        blendColors(accentColor, Color.WHITE, 0.25f),
                        accentColor,
                        blendColors(deepColor, Color.BLACK, 0.45f)
                    ),
                    floatArrayOf(0f, 0.45f, 1f),
                    Shader.TileMode.CLAMP
                )
            }
        )
        canvas.restore()

        // 3D cylindrical rim depth of magnifying lens
        for (d in 10 downTo 1) {
            canvas.drawCircle(
                lensCx - d * 1.2f, lensCy + d * 1.5f, lensR + 14f,
                Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = blendColors(Color.rgb(30, 41, 59), accentColor, (10 - d) / 20f)
                }
            )
        }

        // Clip inside circular lens for Photo 2 (or zoomed Photo 1)
        val lensRect = RectF(lensCx - lensR, lensCy - lensR, lensCx + lensR, lensCy + lensR)
        val lensClip = Path().apply { addCircle(lensCx, lensCy, lensR, Path.Direction.CW) }
        val secondBmp = p2 ?: p1
        canvas.save()
        canvas.clipPath(lensClip)
        if (secondBmp != null) {
            val adj1 = doc.slotAdjust(if (p2 != null) 1 else 0)
            val zoomFactor = if (p2 != null) adj1.zoom else (adj1.zoom * 1.32f)
            canvas.drawRect(lensRect, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE })
            drawBitmapCenterCrop(canvas, secondBmp, lensRect, zoomFactor, adj1.focusX, adj1.focusY)
        } else {
            drawStudyPagePlaceholder(
                context = context,
                canvas = canvas,
                pageRect = lensRect,
                slotIndex = 1,
                layerIdx = 1,
                isOverlapped = false,
                subjectTag = "DEEP FOCUS · PHOTO 2",
                doc = doc,
                accentColor = secondaryColor,
                warmPaper = false
            )
        }

        // Optical glass glare reflection arc across top-left of lens
        val glarePath = Path().apply {
            addOval(RectF(lensCx - lensR * 0.85f, lensCy - lensR * 0.88f, lensCx + lensR * 0.55f, lensCy - lensR * 0.12f), Path.Direction.CW)
        }
        canvas.drawPath(
            glarePath,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = LinearGradient(
                    lensCx, lensCy - lensR, lensCx, lensCy,
                    intArrayOf(Color.argb(75, 255, 255, 255), Color.TRANSPARENT),
                    null,
                    Shader.TileMode.CLAMP
                )
            }
        )
        canvas.restore()

        // Outer metallic & accent bezel rings
        canvas.drawCircle(
            lensCx, lensCy, lensR + 7f,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = 16f
                shader = LinearGradient(
                    lensCx - lensR, lensCy - lensR, lensCx + lensR, lensCy + lensR,
                    intArrayOf(Color.WHITE, accentColor, Color.rgb(148, 163, 184)),
                    null,
                    Shader.TileMode.CLAMP
                )
            }
        )
        canvas.drawCircle(
            lensCx, lensCy, lensR,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = 3.5f
                color = Color.WHITE
            }
        )

        canvas.restore()
    }

    // ═══════════════════════════════════════════════════════════════════════
    // VARIANT 23 — 3D FROSTED GLASS FORMULA & LAB BOARD (2 Photo Slots)
    // 3D isometric frosted-glass scientific theorem & formula board with
    // brushed-aluminum corner standoffs, glowing grid accents, Photo 1 main
    // formula/diagram canvas, and floating 3D derivation card with Photo 2.
    // ═══════════════════════════════════════════════════════════════════════
    private fun drawGlassFormulaBoard3D(
        context: Context,
        canvas: Canvas,
        rect: RectF,
        doc: PostDoc,
        p1: Bitmap?,
        p2: Bitmap?,
        accentColor: Int,
        secondaryColor: Int,
        lightColor: Int,
        darkColor: Int,
        deepColor: Int
    ) {
        val w = rect.width()
        val h = rect.height()
        if (w < 40f || h < 40f) return

        val cx = rect.centerX()
        val cy = rect.centerY()

        val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(
                cx, cy, max(w, h) * 0.58f,
                intArrayOf(
                    withAlpha(accentColor, 110),
                    withAlpha(secondaryColor, 50),
                    Color.TRANSPARENT
                ),
                floatArrayOf(0f, 0.58f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawCircle(cx, cy, max(w, h) * 0.58f, glowPaint)

        val baseW = 1000f
        val baseH = 980f
        val fitScale = min(w / baseW, h / baseH) * 0.94f

        canvas.save()
        canvas.translate(cx, cy)
        canvas.scale(fitScale, fitScale)

        val camera = Camera()
        val matrix = Matrix()
        camera.save()
        camera.setLocation(0f, 0f, -34f)
        camera.rotateX(11f)
        camera.rotateY(-14f)
        camera.rotateZ(2.5f)
        camera.getMatrix(matrix)
        camera.restore()

        val boardRect = RectF(-450f, -390f, 340f, 350f)
        val boardR = 36f

        canvas.save()
        canvas.concat(matrix)

        // Drop shadow
        canvas.drawRoundRect(
            RectF(boardRect.left + 26f, boardRect.top + 38f, boardRect.right + 26f, boardRect.bottom + 38f),
            boardR, boardR,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.argb(185, 0, 0, 0)
                maskFilter = BlurMaskFilter(44f, BlurMaskFilter.Blur.NORMAL)
            }
        )

        // 3D acrylic/cyan glass edge thickness
        for (d in 11 downTo 1) {
            val t = d / 11f
            canvas.drawRoundRect(
                RectF(boardRect.left - d * 1.4f, boardRect.top + d * 1.8f, boardRect.right - d * 1.4f, boardRect.bottom + d * 1.8f),
                boardR, boardR,
                Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = blendColors(accentColor, Color.rgb(15, 23, 42), t * 0.7f)
                }
            )
        }

        // Frosted glass board base
        canvas.drawRoundRect(
            boardRect, boardR, boardR,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = LinearGradient(
                    boardRect.left, boardRect.top, boardRect.right, boardRect.bottom,
                    intArrayOf(
                        blendColors(darkColor, Color.rgb(18, 32, 58), 0.55f),
                        blendColors(deepColor, Color.rgb(10, 18, 36), 0.75f)
                    ),
                    null,
                    Shader.TileMode.CLAMP
                )
            }
        )

        // Subtle scientific coordinate grid lines on the glass board
        val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = withAlpha(accentColor, 35)
            strokeWidth = 1.5f
        }
        var gx = boardRect.left + 55f
        while (gx < boardRect.right - 30f) {
            canvas.drawLine(gx, boardRect.top + 20f, gx, boardRect.bottom - 20f, gridPaint)
            gx += 55f
        }
        var gy = boardRect.top + 55f
        while (gy < boardRect.bottom - 30f) {
            canvas.drawLine(boardRect.left + 20f, gy, boardRect.right - 20f, gy, gridPaint)
            gy += 55f
        }

        // 4 Brushed-Aluminum Corner Standoff Bolts
        val boltPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(203, 213, 225) }
        val boltRingPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 3f
            color = accentColor
        }
        val boltOffsets = listOf(
            Pair(boardRect.left + 28f, boardRect.top + 28f),
            Pair(boardRect.right - 28f, boardRect.top + 28f),
            Pair(boardRect.left + 28f, boardRect.bottom - 28f),
            Pair(boardRect.right - 28f, boardRect.bottom - 28f)
        )
        for ((bx, by) in boltOffsets) {
            canvas.drawCircle(bx, by, 10f, boltPaint)
            canvas.drawCircle(bx, by, 13f, boltRingPaint)
        }

        // Top formula lab header badge
        canvas.drawText(
            "∑  STEM FORMULA & THEOREM BOARD",
            boardRect.left + 64f, boardRect.top + 54f,
            TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                color = accentColor
                textSize = 21f
                typeface = PostFonts.get(context, PostFont.BOLD, "STEM", doc)
            }
        )

        // Main Formula / Diagram Canvas (Photo 1)
        val mainCanvasRect = RectF(boardRect.left + 36f, boardRect.top + 78f, boardRect.right - 36f, boardRect.bottom - 44f)
        val mainClip = Path().apply { addRoundRect(mainCanvasRect, 24f, 24f, Path.Direction.CW) }
        canvas.save()
        canvas.clipPath(mainClip)
        if (p1 != null) {
            val adj0 = doc.slotAdjust(0)
            canvas.drawRect(mainCanvasRect, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE })
            drawBitmapCenterCrop(canvas, p1, mainCanvasRect, adj0.zoom, adj0.focusX, adj0.focusY)
        } else {
            drawStudyPagePlaceholder(
                context = context,
                canvas = canvas,
                pageRect = mainCanvasRect,
                slotIndex = 0,
                layerIdx = 0,
                isOverlapped = false,
                subjectTag = "FORMULA SHEET · PHOTO 1",
                doc = doc,
                accentColor = accentColor,
                warmPaper = false
            )
        }
        canvas.restore()

        canvas.drawRoundRect(
            mainCanvasRect, 24f, 24f,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = 2.8f
                color = withAlpha(accentColor, 185)
            }
        )

        canvas.drawRoundRect(
            boardRect, boardR, boardR,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = 3.5f
                color = withAlpha(accentColor, 215)
            }
        )
        canvas.restore()

        // Floating 3D Key Proof / Worked Example Card (Photo 2)
        val secondBmp = p2 ?: p1
        canvas.save()
        val cam2 = Camera()
        val mat2 = Matrix()
        cam2.save()
        cam2.setLocation(0f, 0f, -34f)
        cam2.rotateX(8f)
        cam2.rotateY(-20f)
        cam2.rotateZ(-5f)
        cam2.getMatrix(mat2)
        cam2.restore()
        canvas.concat(mat2)

        val proofRect = RectF(95f, -65f, 475f, 425f)
        val proofR = 30f
        canvas.drawRoundRect(
            RectF(proofRect.left - 24f, proofRect.top + 26f, proofRect.right - 24f, proofRect.bottom + 26f),
            proofR, proofR,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.argb(185, 0, 0, 0)
                maskFilter = BlurMaskFilter(34f, BlurMaskFilter.Blur.NORMAL)
            }
        )

        val pClip = Path().apply { addRoundRect(proofRect, proofR, proofR, Path.Direction.CW) }
        canvas.save()
        canvas.clipPath(pClip)
        if (secondBmp != null) {
            val adj1 = doc.slotAdjust(if (p2 != null) 1 else 0)
            canvas.drawRect(proofRect, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE })
            drawBitmapCenterCrop(canvas, secondBmp, proofRect, adj1.zoom, adj1.focusX, adj1.focusY)
        } else {
            drawStudyPagePlaceholder(
                context = context,
                canvas = canvas,
                pageRect = proofRect,
                slotIndex = 1,
                layerIdx = 1,
                isOverlapped = false,
                subjectTag = "WORKED PROOF · PHOTO 2",
                doc = doc,
                accentColor = secondaryColor,
                warmPaper = true
            )
        }
        canvas.restore()

        canvas.drawRoundRect(
            proofRect, proofR, proofR,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = 4f
                color = accentColor
            }
        )
        canvas.restore()

        canvas.restore()
    }

    // ═══════════════════════════════════════════════════════════════════════
    // VARIANT 24 — 3D REVISION PINBOARD & STICKY NOTES (1–4 Dynamic Photos)
    // 3D angled wood-and-slate revision pinboard where inserting 1, 2, 3, or 4
    // images dynamically pins 1–4 tilted 3D revision cards with glossy 3D
    // push-pins and lifted drop shadows.
    // ═══════════════════════════════════════════════════════════════════════
    private fun drawRevisionPinboard3D(
        context: Context,
        canvas: Canvas,
        rect: RectF,
        doc: PostDoc,
        p1: Bitmap?,
        p2: Bitmap?,
        p3: Bitmap?,
        p4: Bitmap?,
        accentColor: Int,
        secondaryColor: Int,
        lightColor: Int,
        darkColor: Int,
        deepColor: Int
    ) {
        val w = rect.width()
        val h = rect.height()
        if (w < 40f || h < 40f) return

        val slots = resolveActiveSlots(doc, 4, p1, p2, p3, p4)
        val count = slots.size.coerceIn(1, 4)

        val cx = rect.centerX()
        val cy = rect.centerY()

        val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(
                cx, cy, max(w, h) * 0.56f,
                intArrayOf(
                    withAlpha(accentColor, 95),
                    withAlpha(secondaryColor, 42),
                    Color.TRANSPARENT
                ),
                floatArrayOf(0f, 0.6f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawCircle(cx, cy, max(w, h) * 0.56f, glowPaint)

        val baseW = 1000f
        val baseH = 980f
        val fitScale = min(w / baseW, h / baseH) * 0.94f

        canvas.save()
        canvas.translate(cx, cy)
        canvas.scale(fitScale, fitScale)

        val camera = Camera()
        val matrix = Matrix()
        camera.save()
        camera.setLocation(0f, 0f, -34f)
        camera.rotateX(11f)
        camera.rotateY(-11f)
        camera.rotateZ(1.5f)
        camera.getMatrix(matrix)
        camera.restore()

        canvas.save()
        canvas.concat(matrix)

        val boardRect = RectF(-460f, -420f, 460f, 420f)
        val boardR = 38f

        // Board shadow
        canvas.drawRoundRect(
            RectF(boardRect.left + 26f, boardRect.top + 38f, boardRect.right + 26f, boardRect.bottom + 38f),
            boardR, boardR,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.argb(190, 0, 0, 0)
                maskFilter = BlurMaskFilter(44f, BlurMaskFilter.Blur.NORMAL)
            }
        )

        // 3D warm oak/walnut frame depth
        val frameDark = Color.rgb(120, 72, 32)
        val frameLight = Color.rgb(217, 155, 82)
        for (d in 12 downTo 1) {
            canvas.drawRoundRect(
                RectF(boardRect.left - d * 1.4f, boardRect.top + d * 1.8f, boardRect.right - d * 1.4f, boardRect.bottom + d * 1.8f),
                boardR, boardR,
                Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = blendColors(frameDark, frameLight, (12 - d) / 12f)
                }
            )
        }

        // Wood frame border
        canvas.drawRoundRect(
            boardRect, boardR, boardR,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = LinearGradient(
                    boardRect.left, boardRect.top, boardRect.right, boardRect.bottom,
                    intArrayOf(frameLight, Color.rgb(165, 105, 48), frameDark),
                    null,
                    Shader.TileMode.CLAMP
                )
            }
        )

        // Inner dark slate/cork pinboard surface
        val innerBoard = RectF(boardRect.left + 26f, boardRect.top + 26f, boardRect.right - 26f, boardRect.bottom - 26f)
        canvas.drawRoundRect(
            innerBoard, 24f, 24f,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = LinearGradient(
                    innerBoard.left, innerBoard.top, innerBoard.right, innerBoard.bottom,
                    intArrayOf(
                        blendColors(darkColor, Color.rgb(26, 34, 52), 0.6f),
                        blendColors(deepColor, Color.rgb(14, 20, 34), 0.78f)
                    ),
                    null,
                    Shader.TileMode.CLAMP
                )
            }
        )

        // Determine dynamic card rectangles and tilt angles based on count (1..4)
        data class PinCardSpec(val rect: RectF, val angle: Float, val pinColor: Int, val label: String)
        val pinColors = intArrayOf(
            Color.rgb(239, 68, 68),
            Color.rgb(245, 158, 11),
            Color.rgb(16, 185, 129),
            Color.rgb(59, 130, 246)
        )

        val specs: List<PinCardSpec> = when (count) {
            1 -> listOf(
                PinCardSpec(RectF(-330f, -330f, 330f, 340f), -2.5f, pinColors[0], "KEY REVISION SHEET")
            )
            2 -> listOf(
                PinCardSpec(RectF(-400f, -310f, 20f, 320f), -5f, pinColors[0], "REVISION NOTE 01"),
                PinCardSpec(RectF(-10f, -290f, 405f, 340f), 4.5f, pinColors[1], "REVISION NOTE 02")
            )
            3 -> listOf(
                PinCardSpec(RectF(-405f, -365f, -15f, 55f), -4.5f, pinColors[0], "NOTE 01"),
                PinCardSpec(RectF(15f, -365f, 405f, 55f), 4f, pinColors[1], "NOTE 02"),
                PinCardSpec(RectF(-220f, -30f, 220f, 375f), -2f, pinColors[2], "NOTE 03")
            )
            else -> listOf(
                PinCardSpec(RectF(-405f, -370f, -20f, -12f), -4.5f, pinColors[0], "NOTE 01"),
                PinCardSpec(RectF(20f, -370f, 405f, -12f), 3.8f, pinColors[1], "NOTE 02"),
                PinCardSpec(RectF(-405f, 18f, -20f, 375f), 3.2f, pinColors[2], "NOTE 03"),
                PinCardSpec(RectF(20f, 18f, 405f, 375f), -3.8f, pinColors[3], "NOTE 04")
            )
        }

        for (i in 0 until count) {
            val spec = specs[i]
            val slot = slots[i]
            val cRect = spec.rect
            canvas.save()
            canvas.rotate(spec.angle, cRect.centerX(), cRect.centerY())

            // Lifted card shadow
            canvas.drawRoundRect(
                RectF(cRect.left + 12f, cRect.top + 20f, cRect.right + 12f, cRect.bottom + 20f),
                20f, 20f,
                Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.argb(165, 0, 0, 0)
                    maskFilter = BlurMaskFilter(24f, BlurMaskFilter.Blur.NORMAL)
                }
            )

            val clip = Path().apply { addRoundRect(cRect, 20f, 20f, Path.Direction.CW) }
            canvas.save()
            canvas.clipPath(clip)
            val bmp = slot.bitmap
            if (bmp != null) {
                val adj = doc.slotAdjust(slot.slotIndex)
                canvas.drawRect(cRect, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE })
                drawBitmapCenterCrop(canvas, bmp, cRect, adj.zoom, adj.focusX, adj.focusY)
            } else {
                drawStudyPagePlaceholder(
                    context = context,
                    canvas = canvas,
                    pageRect = cRect,
                    slotIndex = slot.slotIndex,
                    layerIdx = i,
                    isOverlapped = false,
                    subjectTag = spec.label,
                    doc = doc,
                    accentColor = spec.pinColor,
                    warmPaper = true
                )
            }
            canvas.restore()

            // Card border
            canvas.drawRoundRect(
                cRect, 20f, 20f,
                Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    style = Paint.Style.STROKE
                    strokeWidth = 3f
                    color = Color.WHITE
                }
            )

            // 3D Glossy Push-Pin at top center of card
            val pinX = cRect.centerX()
            val pinY = cRect.top + 18f
            canvas.drawCircle(
                pinX + 5f, pinY + 8f, 14f,
                Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.argb(120, 0, 0, 0) }
            )
            canvas.drawCircle(
                pinX, pinY, 15f,
                Paint(Paint.ANTI_ALIAS_FLAG).apply { color = spec.pinColor }
            )
            canvas.drawCircle(
                pinX - 4f, pinY - 4f, 5.5f,
                Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.argb(180, 255, 255, 255) }
            )

            canvas.restore()
        }

        canvas.restore()
        canvas.restore()
    }

    // ═══════════════════════════════════════════════════════════════════════
    // VARIANT 25 — 3D LAPTOP & FLOATING STUDY SHEET (2 Photo Slots)
    // 3D angled aluminum ultrabook / laptop mockup with keyboard deck, trackpad,
    // and 16:10 display showing Photo 1, paired with a floating 3D study notes
    // sheet hovering on the right showing Photo 2.
    // ═══════════════════════════════════════════════════════════════════════
    private fun drawLaptopAndStudyNotes3D(
        context: Context,
        canvas: Canvas,
        rect: RectF,
        doc: PostDoc,
        p1: Bitmap?,
        p2: Bitmap?,
        accentColor: Int,
        secondaryColor: Int,
        lightColor: Int,
        darkColor: Int,
        deepColor: Int
    ) {
        val w = rect.width()
        val h = rect.height()
        if (w < 40f || h < 40f) return

        val cx = rect.centerX()
        val cy = rect.centerY()

        val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(
                cx, cy, max(w, h) * 0.58f,
                intArrayOf(
                    withAlpha(accentColor, 105),
                    withAlpha(secondaryColor, 45),
                    Color.TRANSPARENT
                ),
                floatArrayOf(0f, 0.58f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawCircle(cx, cy, max(w, h) * 0.58f, glowPaint)

        val baseW = 1040f
        val baseH = 980f
        val fitScale = min(w / baseW, h / baseH) * 0.94f

        canvas.save()
        canvas.translate(cx, cy)
        canvas.scale(fitScale, fitScale)

        // 3D Ultrabook Display & Keyboard Base
        val camera = Camera()
        val matrix = Matrix()
        camera.save()
        camera.setLocation(0f, 0f, -36f)
        camera.rotateX(10f)
        camera.rotateY(-13f)
        camera.rotateZ(1.5f)
        camera.getMatrix(matrix)
        camera.restore()

        canvas.save()
        canvas.concat(matrix)

        val lidRect = RectF(-470f, -370f, 300f, 155f)
        val lidR = 28f

        // Laptop shadow
        canvas.drawRoundRect(
            RectF(lidRect.left + 20f, lidRect.bottom - 10f, lidRect.right + 120f, lidRect.bottom + 215f),
            36f, 36f,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.argb(195, 0, 0, 0)
                maskFilter = BlurMaskFilter(48f, BlurMaskFilter.Blur.NORMAL)
            }
        )

        // 3D Aluminum Lid extrusion
        for (d in 8 downTo 1) {
            canvas.drawRoundRect(
                RectF(lidRect.left - d * 1.2f, lidRect.top - d * 0.8f, lidRect.right - d * 1.2f, lidRect.bottom),
                lidR, lidR,
                Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.rgb(148 + d * 4, 163 + d * 4, 184 + d * 3)
                }
            )
        }

        // Outer dark glass bezel
        canvas.drawRoundRect(
            lidRect, lidR, lidR,
            Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(15, 23, 42) }
        )

        // Screen display area (Photo 1)
        val dispRect = RectF(lidRect.left + 22f, lidRect.top + 24f, lidRect.right - 22f, lidRect.bottom - 24f)
        val dispClip = Path().apply { addRoundRect(dispRect, 14f, 14f, Path.Direction.CW) }
        canvas.save()
        canvas.clipPath(dispClip)
        if (p1 != null) {
            val adj0 = doc.slotAdjust(0)
            canvas.drawRect(dispRect, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE })
            drawBitmapCenterCrop(canvas, p1, dispRect, adj0.zoom, adj0.focusX, adj0.focusY)
        } else {
            drawStudyPagePlaceholder(
                context = context,
                canvas = canvas,
                pageRect = dispRect,
                slotIndex = 0,
                layerIdx = 0,
                isOverlapped = false,
                subjectTag = "WEB & DESKTOP PORTAL · PHOTO 1",
                doc = doc,
                accentColor = accentColor,
                warmPaper = false
            )
        }
        canvas.restore()

        // 3D Isometric Keyboard Base Deck projecting forward from bottom of lid
        val deckTopL = lidRect.left
        val deckTopR = lidRect.right
        val deckTopY = lidRect.bottom - 4f
        val deckBotL = lidRect.left - 85f
        val deckBotR = lidRect.right + 95f
        val deckBotY = lidRect.bottom + 165f

        // Keyboard deck thickness lip
        val lipPath = Path().apply {
            moveTo(deckBotL, deckBotY)
            lineTo(deckBotR, deckBotY)
            lineTo(deckBotR, deckBotY + 22f)
            lineTo(deckBotL, deckBotY + 22f)
            close()
        }
        canvas.drawPath(
            lipPath,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = LinearGradient(
                    deckBotL, deckBotY, deckBotR, deckBotY,
                    intArrayOf(Color.rgb(100, 116, 139), Color.rgb(203, 213, 225), Color.rgb(100, 116, 139)),
                    null,
                    Shader.TileMode.CLAMP
                )
            }
        )

        // Aluminum keyboard top surface
        val deckPath = Path().apply {
            moveTo(deckTopL, deckTopY)
            lineTo(deckTopR, deckTopY)
            lineTo(deckBotR, deckBotY)
            lineTo(deckBotL, deckBotY)
            close()
        }
        canvas.drawPath(
            deckPath,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = LinearGradient(
                    deckTopL, deckTopY, deckBotL, deckBotY,
                    intArrayOf(Color.rgb(226, 232, 240), Color.rgb(148, 163, 184)),
                    null,
                    Shader.TileMode.CLAMP
                )
            }
        )

        // Recessed dark keyboard well
        val kbPath = Path().apply {
            moveTo(deckTopL + 42f, deckTopY + 18f)
            lineTo(deckTopR - 42f, deckTopY + 18f)
            lineTo(deckBotR - 64f, deckBotY - 58f)
            lineTo(deckBotL + 64f, deckBotY - 58f)
            close()
        }
        canvas.drawPath(kbPath, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(30, 41, 59) })

        // Trackpad
        val tpPath = Path().apply {
            moveTo(-140f, deckBotY - 46f)
            lineTo(10f, deckBotY - 46f)
            lineTo(15f, deckBotY - 10f)
            lineTo(-150f, deckBotY - 10f)
            close()
        }
        canvas.drawPath(
            tpPath,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = 2f
                color = Color.rgb(100, 116, 139)
            }
        )

        canvas.restore()

        // Floating 3D Companion Study Notes / Mobile Sync Card (Photo 2)
        val secondBmp = p2 ?: p1
        canvas.save()
        val cam2 = Camera()
        val mat2 = Matrix()
        cam2.save()
        cam2.setLocation(0f, 0f, -34f)
        cam2.rotateX(8f)
        cam2.rotateY(-20f)
        cam2.rotateZ(-5f)
        cam2.getMatrix(mat2)
        cam2.restore()
        canvas.concat(mat2)

        val sheetRect = RectF(105f, -155f, 485f, 395f)
        val sheetR = 32f
        canvas.drawRoundRect(
            RectF(sheetRect.left - 24f, sheetRect.top + 26f, sheetRect.right - 24f, sheetRect.bottom + 26f),
            sheetR, sheetR,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.argb(185, 0, 0, 0)
                maskFilter = BlurMaskFilter(34f, BlurMaskFilter.Blur.NORMAL)
            }
        )

        val sClip = Path().apply { addRoundRect(sheetRect, sheetR, sheetR, Path.Direction.CW) }
        canvas.save()
        canvas.clipPath(sClip)
        if (secondBmp != null) {
            val adj1 = doc.slotAdjust(if (p2 != null) 1 else 0)
            canvas.drawRect(sheetRect, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE })
            drawBitmapCenterCrop(canvas, secondBmp, sheetRect, adj1.zoom, adj1.focusX, adj1.focusY)
        } else {
            drawStudyPagePlaceholder(
                context = context,
                canvas = canvas,
                pageRect = sheetRect,
                slotIndex = 1,
                layerIdx = 1,
                isOverlapped = false,
                subjectTag = "SYNCED NOTES · PHOTO 2",
                doc = doc,
                accentColor = secondaryColor,
                warmPaper = true
            )
        }
        canvas.restore()

        canvas.drawRoundRect(
            sheetRect, sheetR, sheetR,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = 4f
                color = accentColor
            }
        )
        canvas.restore()

        canvas.restore()
    }

    // ═══════════════════════════════════════════════════════════════════════
    // VARIANT 26 — 3D ISOMETRIC LEARNING ROADMAP STEPS (1–3 Dynamic Photos)
    // 3D ascending isometric milestone platforms ("STEP 01", "STEP 02", "STEP 03")
    // with upright 3D study cards standing on each platform. Adapts dynamically
    // to 1, 2, or 3 inserted photos.
    // ═══════════════════════════════════════════════════════════════════════
    private fun drawLearningRoadmapSteps3D(
        context: Context,
        canvas: Canvas,
        rect: RectF,
        doc: PostDoc,
        p1: Bitmap?,
        p2: Bitmap?,
        p3: Bitmap?,
        accentColor: Int,
        secondaryColor: Int,
        lightColor: Int,
        darkColor: Int,
        deepColor: Int
    ) {
        val w = rect.width()
        val h = rect.height()
        if (w < 40f || h < 40f) return

        val slots = resolveActiveSlots(doc, 3, p1, p2, p3)
        val count = slots.size.coerceIn(1, 3)

        val cx = rect.centerX()
        val cy = rect.centerY()

        val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(
                cx, cy, max(w, h) * 0.58f,
                intArrayOf(
                    withAlpha(accentColor, 100),
                    withAlpha(secondaryColor, 45),
                    Color.TRANSPARENT
                ),
                floatArrayOf(0f, 0.6f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawCircle(cx, cy, max(w, h) * 0.58f, glowPaint)

        val baseW = 1040f
        val baseH = 980f
        val fitScale = min(w / baseW, h / baseH) * 0.94f

        canvas.save()
        canvas.translate(cx, cy)
        canvas.scale(fitScale, fitScale)

        data class StepSpec(
            val cx: Float,
            val cy: Float,
            val cardW: Float,
            val cardH: Float,
            val badge: String,
            val color: Int
        )

        val stepColors = intArrayOf(
            accentColor,
            secondaryColor,
            Color.rgb(245, 158, 11)
        )

        val specs: List<StepSpec> = when (count) {
            1 -> listOf(
                StepSpec(0f, -20f, 540f, 680f, "STEP 01 · MASTER", stepColors[0])
            )
            2 -> listOf(
                StepSpec(-215f, 55f, 410f, 560f, "STEP 01 · LEARN", stepColors[0]),
                StepSpec(215f, -55f, 410f, 560f, "STEP 02 · PRACTICE", stepColors[1])
            )
            else -> listOf(
                StepSpec(-315f, 115f, 305f, 460f, "STEP 01", stepColors[0]),
                StepSpec(0f, 0f, 305f, 460f, "STEP 02", stepColors[1]),
                StepSpec(315f, -115f, 305f, 460f, "STEP 03", stepColors[2])
            )
        }

        // Draw glowing connecting roadmap path between steps
        if (count > 1) {
            val pathPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = 10f
                color = withAlpha(accentColor, 140)
            }
            for (i in 0 until count - 1) {
                val a = specs[i]
                val b = specs[i + 1]
                canvas.drawLine(
                    a.cx, a.cy + a.cardH * 0.5f + 25f,
                    b.cx, b.cy + b.cardH * 0.5f + 25f,
                    pathPaint
                )
            }
        }

        for (i in 0 until count) {
            val spec = specs[i]
            val slot = slots[i]

            canvas.save()
            canvas.translate(spec.cx, spec.cy)

            val camera = Camera()
            val matrix = Matrix()
            camera.save()
            camera.setLocation(0f, 0f, -34f)
            camera.rotateX(10f)
            camera.rotateY(-12f)
            camera.rotateZ(1.5f)
            camera.getMatrix(matrix)
            camera.restore()
            canvas.concat(matrix)

            val hw = spec.cardW * 0.5f
            val hh = spec.cardH * 0.5f
            val cRect = RectF(-hw, -hh, hw, hh)

            // 3D Isometric Pedestal Platform underneath card
            val platRect = RectF(-hw - 22f, hh - 10f, hw + 22f, hh + 64f)
            canvas.drawRoundRect(
                RectF(platRect.left + 14f, platRect.top + 24f, platRect.right + 14f, platRect.bottom + 24f),
                22f, 22f,
                Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.argb(175, 0, 0, 0)
                    maskFilter = BlurMaskFilter(28f, BlurMaskFilter.Blur.NORMAL)
                }
            )
            canvas.drawRoundRect(
                platRect, 22f, 22f,
                Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    shader = LinearGradient(
                        platRect.left, platRect.top, platRect.right, platRect.bottom,
                        intArrayOf(
                            blendColors(spec.color, Color.WHITE, 0.15f),
                            blendColors(spec.color, Color.BLACK, 0.45f)
                        ),
                        null,
                        Shader.TileMode.CLAMP
                    )
                }
            )
            canvas.drawText(
                spec.badge,
                platRect.centerX(), platRect.centerY() + 10f,
                TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.WHITE
                    textSize = 22f
                    textAlign = Paint.Align.CENTER
                    typeface = PostFonts.get(context, PostFont.BOLD, spec.badge, doc)
                }
            )

            // Standing 3D Card thickness
            for (d in 8 downTo 1) {
                canvas.drawRoundRect(
                    RectF(cRect.left - d * 1.3f, cRect.top + d * 1.6f, cRect.right - d * 1.3f, cRect.bottom + d * 1.6f),
                    26f, 26f,
                    Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        color = blendColors(deepColor, spec.color, (8 - d) / 14f)
                    }
                )
            }

            val clip = Path().apply { addRoundRect(cRect, 26f, 26f, Path.Direction.CW) }
            canvas.save()
            canvas.clipPath(clip)
            val bmp = slot.bitmap
            if (bmp != null) {
                val adj = doc.slotAdjust(slot.slotIndex)
                canvas.drawRect(cRect, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE })
                drawBitmapCenterCrop(canvas, bmp, cRect, adj.zoom, adj.focusX, adj.focusY)
            } else {
                drawStudyPagePlaceholder(
                    context = context,
                    canvas = canvas,
                    pageRect = cRect,
                    slotIndex = slot.slotIndex,
                    layerIdx = i,
                    isOverlapped = false,
                    subjectTag = "${spec.badge} · PHOTO ${i + 1}",
                    doc = doc,
                    accentColor = spec.color,
                    warmPaper = i % 2 == 1
                )
            }
            canvas.restore()

            canvas.drawRoundRect(
                cRect, 26f, 26f,
                Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    style = Paint.Style.STROKE
                    strokeWidth = 3.5f
                    color = spec.color
                }
            )

            canvas.restore()
        }

        canvas.restore()
    }

    // ═══════════════════════════════════════════════════════════════════════
    // VARIANT 27 — 3D KNOWLEDGE CUBE & VOCABULARY BLOCK (2 Photo Slots)
    // 3D isometric Dual-Face Knowledge & Vocabulary Cube where the left 3D
    // perspective face showcases Photo 1 (Concept / Term) and the right 3D
    // perspective face showcases Photo 2 (Definition / Example), crowned by
    // an illuminated top knowledge cap.
    // ═══════════════════════════════════════════════════════════════════════
    private fun drawKnowledgeCubeCard3D(
        context: Context,
        canvas: Canvas,
        rect: RectF,
        doc: PostDoc,
        p1: Bitmap?,
        p2: Bitmap?,
        accentColor: Int,
        secondaryColor: Int,
        lightColor: Int,
        darkColor: Int,
        deepColor: Int
    ) {
        val w = rect.width()
        val h = rect.height()
        if (w < 40f || h < 40f) return

        val cx = rect.centerX()
        val cy = rect.centerY()

        val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(
                cx, cy, max(w, h) * 0.58f,
                intArrayOf(
                    withAlpha(accentColor, 105),
                    withAlpha(secondaryColor, 48),
                    Color.TRANSPARENT
                ),
                floatArrayOf(0f, 0.58f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawCircle(cx, cy, max(w, h) * 0.58f, glowPaint)

        val baseW = 1040f
        val baseH = 980f
        val fitScale = min(w / baseW, h / baseH) * 0.94f

        canvas.save()
        canvas.translate(cx, cy)
        canvas.scale(fitScale, fitScale)

        // Floor shadow under cube
        canvas.drawOval(
            RectF(-410f, 340f, 410f, 445f),
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.argb(175, 0, 0, 0)
                maskFilter = BlurMaskFilter(45f, BlurMaskFilter.Blur.NORMAL)
            }
        )

        // Top 3D Isometric Diamond Cap of the Knowledge Cube
        val topDiamond = Path().apply {
            moveTo(0f, -430f)
            lineTo(395f, -275f)
            lineTo(0f, -135f)
            lineTo(-395f, -275f)
            close()
        }
        canvas.drawPath(
            topDiamond,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = LinearGradient(
                    -395f, -430f, 395f, -135f,
                    intArrayOf(
                        blendColors(accentColor, Color.WHITE, 0.28f),
                        accentColor,
                        blendColors(deepColor, accentColor, 0.45f)
                    ),
                    null,
                    Shader.TileMode.CLAMP
                )
            }
        )
        canvas.drawPath(
            topDiamond,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = 3.5f
                color = Color.WHITE
            }
        )
        canvas.drawText(
            "3D KNOWLEDGE CUBE · CONCEPT & DEFINITION",
            0f, -272f,
            TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(15, 23, 42)
                textSize = 21f
                textAlign = Paint.Align.CENTER
                typeface = PostFonts.get(context, PostFont.BOLD, "KNOWLEDGE", doc)
            }
        )

        // Left 3D Cube Face (Photo 1)
        canvas.save()
        canvas.translate(-205f, 105f)
        val camL = Camera()
        val matL = Matrix()
        camL.save()
        camL.setLocation(0f, 0f, -32f)
        camL.rotateX(6f)
        camL.rotateY(22f)
        camL.rotateZ(-6f)
        camL.getMatrix(matL)
        camL.restore()
        canvas.concat(matL)

        val leftRect = RectF(-215f, -285f, 200f, 285f)
        val faceR = 28f
        for (d in 10 downTo 1) {
            canvas.drawRoundRect(
                RectF(leftRect.left - d * 1.4f, leftRect.top + d * 1.5f, leftRect.right - d * 1.4f, leftRect.bottom + d * 1.5f),
                faceR, faceR,
                Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = blendColors(deepColor, accentColor, (10 - d) / 18f)
                }
            )
        }
        val lClip = Path().apply { addRoundRect(leftRect, faceR, faceR, Path.Direction.CW) }
        canvas.save()
        canvas.clipPath(lClip)
        if (p1 != null) {
            val adj0 = doc.slotAdjust(0)
            canvas.drawRect(leftRect, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE })
            drawBitmapCenterCrop(canvas, p1, leftRect, adj0.zoom, adj0.focusX, adj0.focusY)
        } else {
            drawStudyPagePlaceholder(
                context = context,
                canvas = canvas,
                pageRect = leftRect,
                slotIndex = 0,
                layerIdx = 0,
                isOverlapped = false,
                subjectTag = "CONCEPT FACE · PHOTO 1",
                doc = doc,
                accentColor = accentColor,
                warmPaper = false
            )
        }
        canvas.restore()
        canvas.drawRoundRect(
            leftRect, faceR, faceR,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = 4f
                color = accentColor
            }
        )
        canvas.restore()

        // Right 3D Cube Face (Photo 2)
        val secondBmp = p2 ?: p1
        canvas.save()
        canvas.translate(205f, 105f)
        val camR = Camera()
        val matR = Matrix()
        camR.save()
        camR.setLocation(0f, 0f, -32f)
        camR.rotateX(6f)
        camR.rotateY(-22f)
        camR.rotateZ(6f)
        camR.getMatrix(matR)
        camR.restore()
        canvas.concat(matR)

        val rightRect = RectF(-200f, -285f, 215f, 285f)
        for (d in 10 downTo 1) {
            canvas.drawRoundRect(
                RectF(rightRect.left + d * 1.4f, rightRect.top + d * 1.5f, rightRect.right + d * 1.4f, rightRect.bottom + d * 1.5f),
                faceR, faceR,
                Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = blendColors(deepColor, secondaryColor, (10 - d) / 18f)
                }
            )
        }
        val rClip = Path().apply { addRoundRect(rightRect, faceR, faceR, Path.Direction.CW) }
        canvas.save()
        canvas.clipPath(rClip)
        if (secondBmp != null) {
            val adj1 = doc.slotAdjust(if (p2 != null) 1 else 0)
            canvas.drawRect(rightRect, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE })
            drawBitmapCenterCrop(canvas, secondBmp, rightRect, adj1.zoom, adj1.focusX, adj1.focusY)
        } else {
            drawStudyPagePlaceholder(
                context = context,
                canvas = canvas,
                pageRect = rightRect,
                slotIndex = 1,
                layerIdx = 1,
                isOverlapped = false,
                subjectTag = "DEFINITION FACE · PHOTO 2",
                doc = doc,
                accentColor = secondaryColor,
                warmPaper = true
            )
        }
        canvas.restore()
        canvas.drawRoundRect(
            rightRect, faceR, faceR,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = 4f
                color = Color.WHITE
            }
        )
        canvas.restore()

        canvas.restore()
    }

    // ═══════════════════════════════════════════════════════════════════════
    // VARIANT 28 — 3D BEFORE & AFTER STUDY COMPARISON SPLIT (2 Photo Slots)
    // Dual angled 3D comparison slates ("BEFORE / PROBLEM" vs "AFTER / SOLVED")
    // separated by a glowing 3D lightning/VS badge in the center.
    // ═══════════════════════════════════════════════════════════════════════
    private fun drawBeforeAfterStudySplit3D(
        context: Context,
        canvas: Canvas,
        rect: RectF,
        doc: PostDoc,
        p1: Bitmap?,
        p2: Bitmap?,
        accentColor: Int,
        secondaryColor: Int,
        lightColor: Int,
        darkColor: Int,
        deepColor: Int
    ) {
        val w = rect.width()
        val h = rect.height()
        if (w < 40f || h < 40f) return

        val cx = rect.centerX()
        val cy = rect.centerY()

        val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(
                cx, cy, max(w, h) * 0.58f,
                intArrayOf(
                    withAlpha(accentColor, 100),
                    withAlpha(secondaryColor, 45),
                    Color.TRANSPARENT
                ),
                floatArrayOf(0f, 0.58f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawCircle(cx, cy, max(w, h) * 0.58f, glowPaint)

        val baseW = 1040f
        val baseH = 980f
        val fitScale = min(w / baseW, h / baseH) * 0.94f

        canvas.save()
        canvas.translate(cx, cy)
        canvas.scale(fitScale, fitScale)

        // Left 3D Slate — BEFORE / QUESTION (Photo 1)
        canvas.save()
        canvas.translate(-225f, 25f)
        val camL = Camera()
        val matL = Matrix()
        camL.save()
        camL.setLocation(0f, 0f, -34f)
        camL.rotateX(9f)
        camL.rotateY(14f)
        camL.rotateZ(-4f)
        camL.getMatrix(matL)
        camL.restore()
        canvas.concat(matL)

        val leftCard = RectF(-235f, -360f, 225f, 360f)
        val cardR = 34f
        canvas.drawRoundRect(
            RectF(leftCard.left + 20f, leftCard.top + 32f, leftCard.right + 20f, leftCard.bottom + 32f),
            cardR, cardR,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.argb(180, 0, 0, 0)
                maskFilter = BlurMaskFilter(36f, BlurMaskFilter.Blur.NORMAL)
            }
        )
        for (d in 9 downTo 1) {
            canvas.drawRoundRect(
                RectF(leftCard.left - d * 1.4f, leftCard.top + d * 1.7f, leftCard.right - d * 1.4f, leftCard.bottom + d * 1.7f),
                cardR, cardR,
                Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(30, 41, 59) }
            )
        }
        val lClip = Path().apply { addRoundRect(leftCard, cardR, cardR, Path.Direction.CW) }
        canvas.save()
        canvas.clipPath(lClip)
        if (p1 != null) {
            val adj0 = doc.slotAdjust(0)
            canvas.drawRect(leftCard, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE })
            drawBitmapCenterCrop(canvas, p1, leftCard, adj0.zoom, adj0.focusX, adj0.focusY)
        } else {
            drawStudyPagePlaceholder(
                context = context,
                canvas = canvas,
                pageRect = leftCard,
                slotIndex = 0,
                layerIdx = 0,
                isOverlapped = false,
                subjectTag = "BEFORE · QUESTION",
                doc = doc,
                accentColor = secondaryColor,
                warmPaper = false
            )
        }
        // Top header ribbon
        val lHeader = RectF(leftCard.left, leftCard.top, leftCard.right, leftCard.top + 72f)
        canvas.drawRect(lHeader, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.argb(220, 15, 23, 42) })
        canvas.drawText(
            "01 · BEFORE / PROBLEM",
            lHeader.centerX(), lHeader.centerY() + 8f,
            TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.WHITE
                textSize = 21f
                textAlign = Paint.Align.CENTER
                typeface = PostFonts.get(context, PostFont.BOLD, "BEFORE", doc)
            }
        )
        canvas.restore()
        canvas.drawRoundRect(
            leftCard, cardR, cardR,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = 3.5f
                color = Color.rgb(148, 163, 184)
            }
        )
        canvas.restore()

        // Right 3D Elevated Slate — AFTER / SOLVED (Photo 2)
        val secondBmp = p2 ?: p1
        canvas.save()
        canvas.translate(225f, -25f)
        val camR = Camera()
        val matR = Matrix()
        camR.save()
        camR.setLocation(0f, 0f, -34f)
        camR.rotateX(9f)
        camR.rotateY(-16f)
        camR.rotateZ(4f)
        camR.getMatrix(matR)
        camR.restore()
        canvas.concat(matR)

        val rightCard = RectF(-225f, -360f, 235f, 360f)
        canvas.drawRoundRect(
            RectF(rightCard.left - 22f, rightCard.top + 34f, rightCard.right - 22f, rightCard.bottom + 34f),
            cardR, cardR,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.argb(190, 0, 0, 0)
                maskFilter = BlurMaskFilter(38f, BlurMaskFilter.Blur.NORMAL)
            }
        )
        for (d in 10 downTo 1) {
            canvas.drawRoundRect(
                RectF(rightCard.left + d * 1.4f, rightCard.top + d * 1.7f, rightCard.right + d * 1.4f, rightCard.bottom + d * 1.7f),
                cardR, cardR,
                Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = blendColors(deepColor, accentColor, (10 - d) / 14f)
                }
            )
        }
        val rClip = Path().apply { addRoundRect(rightCard, cardR, cardR, Path.Direction.CW) }
        canvas.save()
        canvas.clipPath(rClip)
        if (secondBmp != null) {
            val adj1 = doc.slotAdjust(if (p2 != null) 1 else 0)
            canvas.drawRect(rightCard, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE })
            drawBitmapCenterCrop(canvas, secondBmp, rightCard, adj1.zoom, adj1.focusX, adj1.focusY)
        } else {
            drawStudyPagePlaceholder(
                context = context,
                canvas = canvas,
                pageRect = rightCard,
                slotIndex = 1,
                layerIdx = 1,
                isOverlapped = false,
                subjectTag = "AFTER · SOLVED STEP-BY-STEP",
                doc = doc,
                accentColor = accentColor,
                warmPaper = true
            )
        }
        val rHeader = RectF(rightCard.left, rightCard.top, rightCard.right, rightCard.top + 72f)
        canvas.drawRect(rHeader, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = accentColor })
        canvas.drawText(
            "02 · AFTER / SOLVED ✓",
            rHeader.centerX(), rHeader.centerY() + 8f,
            TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(15, 23, 42)
                textSize = 21f
                textAlign = Paint.Align.CENTER
                typeface = PostFonts.get(context, PostFont.BOLD, "AFTER", doc)
            }
        )
        canvas.restore()
        canvas.drawRoundRect(
            rightCard, cardR, cardR,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = 4.5f
                color = accentColor
            }
        )
        canvas.restore()

        // Center 3D VS / Transformation Medallion
        canvas.drawCircle(
            0f, 12f, 62f,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.argb(180, 0, 0, 0)
                maskFilter = BlurMaskFilter(22f, BlurMaskFilter.Blur.NORMAL)
            }
        )
        canvas.drawCircle(
            0f, 0f, 58f,
            Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(15, 23, 42) }
        )
        canvas.drawCircle(
            0f, 0f, 58f,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = 5f
                color = accentColor
            }
        )
        canvas.drawText(
            "VS",
            0f, 13f,
            TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                color = accentColor
                textSize = 36f
                textAlign = Paint.Align.CENTER
                typeface = PostFonts.get(context, PostFont.BOLD, "VS", doc)
            }
        )

        canvas.restore()
    }

    // ═══════════════════════════════════════════════════════════════════════
    // VARIANT 29 — 3D TOP SCHOLARS LEADERBOARD PODIUM (1–3 Dynamic Photos)
    // 3D isometric 1st, 2nd, and 3rd place champion podium pedestals with
    // gold/silver/bronze rank badges and upright 3D student/score cards.
    // ═══════════════════════════════════════════════════════════════════════
    private fun drawLeaderboardPodium3D(
        context: Context,
        canvas: Canvas,
        rect: RectF,
        doc: PostDoc,
        p1: Bitmap?,
        p2: Bitmap?,
        p3: Bitmap?,
        accentColor: Int,
        secondaryColor: Int,
        lightColor: Int,
        darkColor: Int,
        deepColor: Int
    ) {
        val w = rect.width()
        val h = rect.height()
        if (w < 40f || h < 40f) return

        val slots = resolveActiveSlots(doc, 3, p1, p2, p3)
        val count = slots.size.coerceIn(1, 3)

        val cx = rect.centerX()
        val cy = rect.centerY()

        val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(
                cx, cy, max(w, h) * 0.58f,
                intArrayOf(
                    withAlpha(Color.rgb(245, 158, 11), 115),
                    withAlpha(accentColor, 48),
                    Color.TRANSPARENT
                ),
                floatArrayOf(0f, 0.58f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawCircle(cx, cy, max(w, h) * 0.58f, glowPaint)

        val baseW = 1040f
        val baseH = 980f
        val fitScale = min(w / baseW, h / baseH) * 0.94f

        canvas.save()
        canvas.translate(cx, cy)
        canvas.scale(fitScale, fitScale)

        data class PodiumSlotSpec(
            val slotIdx: Int,
            val x: Float,
            val cardTop: Float,
            val cardW: Float,
            val cardH: Float,
            val podiumH: Float,
            val rankText: String,
            val medalColor: Int
        )

        val gold = Color.rgb(245, 158, 11)
        val silver = Color.rgb(148, 163, 184)
        val bronze = Color.rgb(217, 119, 6)

        val podiumSpecs: List<PodiumSlotSpec> = when (count) {
            1 -> listOf(
                PodiumSlotSpec(0, 0f, -380f, 470f, 520f, 185f, "★ #1 TOP SCHOLAR", gold)
            )
            2 -> listOf(
                PodiumSlotSpec(1, -225f, -290f, 380f, 450f, 145f, "#2 RUNNER-UP", silver),
                PodiumSlotSpec(0, 215f, -370f, 405f, 490f, 185f, "★ #1 CHAMPION", gold)
            )
            else -> listOf(
                PodiumSlotSpec(1, -320f, -260f, 290f, 410f, 145f, "#2 SILVER", silver),
                PodiumSlotSpec(2, 320f, -215f, 290f, 390f, 120f, "#3 BRONZE", bronze),
                PodiumSlotSpec(0, 0f, -380f, 325f, 465f, 195f, "★ #1 GOLD", gold)
            )
        }

        for (spec in podiumSpecs) {
            val slot = slots[spec.slotIdx]
            canvas.save()
            canvas.translate(spec.x, 0f)

            val hw = spec.cardW * 0.5f
            val cRect = RectF(-hw, spec.cardTop, hw, spec.cardTop + spec.cardH)
            val podTop = cRect.bottom + 14f
            val podRect = RectF(-hw - 18f, podTop, hw + 18f, podTop + spec.podiumH)

            // 3D Podium Block shadow & body
            canvas.drawRoundRect(
                RectF(podRect.left + 14f, podRect.top + 22f, podRect.right + 14f, podRect.bottom + 22f),
                24f, 24f,
                Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.argb(180, 0, 0, 0)
                    maskFilter = BlurMaskFilter(28f, BlurMaskFilter.Blur.NORMAL)
                }
            )
            canvas.drawRoundRect(
                podRect, 24f, 24f,
                Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    shader = LinearGradient(
                        podRect.left, podRect.top, podRect.right, podRect.bottom,
                        intArrayOf(
                            blendColors(spec.medalColor, Color.WHITE, 0.2f),
                            spec.medalColor,
                            blendColors(spec.medalColor, Color.BLACK, 0.55f)
                        ),
                        null,
                        Shader.TileMode.CLAMP
                    )
                }
            )
            canvas.drawText(
                spec.rankText,
                0f, podRect.centerY() + 10f,
                TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.rgb(15, 23, 42)
                    textSize = 24f
                    textAlign = Paint.Align.CENTER
                    typeface = PostFonts.get(context, PostFont.BOLD, spec.rankText, doc)
                }
            )

            // 3D Standing Card above podium
            for (d in 8 downTo 1) {
                canvas.drawRoundRect(
                    RectF(cRect.left - d * 1.2f, cRect.top + d * 1.5f, cRect.right - d * 1.2f, cRect.bottom + d * 1.5f),
                    28f, 28f,
                    Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        color = blendColors(deepColor, spec.medalColor, (8 - d) / 14f)
                    }
                )
            }

            val clip = Path().apply { addRoundRect(cRect, 28f, 28f, Path.Direction.CW) }
            canvas.save()
            canvas.clipPath(clip)
            val bmp = slot.bitmap
            if (bmp != null) {
                val adj = doc.slotAdjust(slot.slotIndex)
                canvas.drawRect(cRect, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE })
                drawBitmapCenterCrop(canvas, bmp, cRect, adj.zoom, adj.focusX, adj.focusY)
            } else {
                drawStudyPagePlaceholder(
                    context = context,
                    canvas = canvas,
                    pageRect = cRect,
                    slotIndex = slot.slotIndex,
                    layerIdx = spec.slotIdx,
                    isOverlapped = false,
                    subjectTag = "${spec.rankText} · PHOTO ${spec.slotIdx + 1}",
                    doc = doc,
                    accentColor = spec.medalColor,
                    warmPaper = false
                )
            }
            canvas.restore()

            canvas.drawRoundRect(
                cRect, 28f, 28f,
                Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    style = Paint.Style.STROKE
                    strokeWidth = 4f
                    color = spec.medalColor
                }
            )
            canvas.restore()
        }

        canvas.restore()
    }

    // ═══════════════════════════════════════════════════════════════════════
    // VARIANT 30 — 3D EXAM COUNTDOWN FLIP-CLOCK & PLANNER (2 Photo Slots)
    // 3D mechanical split-flap exam countdown housing at the top ("DAYS / HRS")
    // with Photo 1 main study schedule display and overlapping 3D exam
    // checklist card with Photo 2.
    // ═══════════════════════════════════════════════════════════════════════
    private fun drawExamCountdownFlipClock3D(
        context: Context,
        canvas: Canvas,
        rect: RectF,
        doc: PostDoc,
        p1: Bitmap?,
        p2: Bitmap?,
        accentColor: Int,
        secondaryColor: Int,
        lightColor: Int,
        darkColor: Int,
        deepColor: Int
    ) {
        val w = rect.width()
        val h = rect.height()
        if (w < 40f || h < 40f) return

        val cx = rect.centerX()
        val cy = rect.centerY()

        val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(
                cx, cy, max(w, h) * 0.56f,
                intArrayOf(
                    withAlpha(accentColor, 100),
                    withAlpha(secondaryColor, 45),
                    Color.TRANSPARENT
                ),
                floatArrayOf(0f, 0.58f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawCircle(cx, cy, max(w, h) * 0.56f, glowPaint)

        val baseW = 1000f
        val baseH = 980f
        val fitScale = min(w / baseW, h / baseH) * 0.94f

        canvas.save()
        canvas.translate(cx, cy)
        canvas.scale(fitScale, fitScale)

        val camera = Camera()
        val matrix = Matrix()
        camera.save()
        camera.setLocation(0f, 0f, -34f)
        camera.rotateX(11f)
        camera.rotateY(-13f)
        camera.rotateZ(2.5f)
        camera.getMatrix(matrix)
        camera.restore()

        val deckRect = RectF(-450f, -400f, 340f, 350f)
        val deckR = 40f

        canvas.save()
        canvas.concat(matrix)

        canvas.drawRoundRect(
            RectF(deckRect.left + 26f, deckRect.top + 38f, deckRect.right + 26f, deckRect.bottom + 38f),
            deckR, deckR,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.argb(190, 0, 0, 0)
                maskFilter = BlurMaskFilter(44f, BlurMaskFilter.Blur.NORMAL)
            }
        )

        for (d in 12 downTo 1) {
            canvas.drawRoundRect(
                RectF(deckRect.left - d * 1.4f, deckRect.top + d * 1.8f, deckRect.right - d * 1.4f, deckRect.bottom + d * 1.8f),
                deckR, deckR,
                Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = blendColors(deepColor, accentColor, (12 - d) / 20f)
                }
            )
        }

        canvas.drawRoundRect(
            deckRect, deckR, deckR,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = LinearGradient(
                    deckRect.left, deckRect.top, deckRect.right, deckRect.bottom,
                    intArrayOf(
                        blendColors(darkColor, Color.rgb(20, 28, 48), 0.55f),
                        blendColors(deepColor, Color.rgb(10, 14, 26), 0.75f)
                    ),
                    null,
                    Shader.TileMode.CLAMP
                )
            }
        )

        // Top 3D Split-Flap Countdown Digits Row
        val flapLabels = listOf("14" to "DAYS", "08" to "HOURS", "45" to "MINS")
        val flapStartX = deckRect.left + 36f
        val flapW = 215f
        val flapGap = 32f
        val flapTop = deckRect.top + 28f
        val flapBottom = flapTop + 128f

        for (idx in flapLabels.indices) {
            val (numStr, unitStr) = flapLabels[idx]
            val fx = flapStartX + idx * (flapW + flapGap)
            val fRect = RectF(fx, flapTop, fx + flapW, flapBottom)
            canvas.drawRoundRect(
                fRect, 18f, 18f,
                Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(12, 18, 32) }
            )
            canvas.drawRoundRect(
                fRect, 18f, 18f,
                Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    style = Paint.Style.STROKE
                    strokeWidth = 2.5f
                    color = withAlpha(accentColor, 175)
                }
            )
            // Split-flap mechanical horizontal seam
            canvas.drawLine(
                fRect.left + 6f, fRect.centerY() - 8f, fRect.right - 6f, fRect.centerY() - 8f,
                Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.argb(180, 0, 0, 0)
                    strokeWidth = 3f
                }
            )
            canvas.drawText(
                numStr,
                fRect.centerX(), fRect.centerY() + 14f,
                TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = accentColor
                    textSize = 58f
                    textAlign = Paint.Align.CENTER
                    typeface = PostFonts.get(context, PostFont.BOLD, numStr, doc)
                }
            )
            canvas.drawText(
                unitStr,
                fRect.centerX(), fRect.bottom - 12f,
                TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.argb(190, 255, 255, 255)
                    textSize = 15.5f
                    textAlign = Paint.Align.CENTER
                    typeface = PostFonts.get(context, PostFont.BOLD, unitStr, doc)
                }
            )
        }

        // Main Exam Study Schedule / Timetable Window (Photo 1)
        val schedRect = RectF(deckRect.left + 36f, flapBottom + 24f, deckRect.right - 36f, deckRect.bottom - 36f)
        val schedClip = Path().apply { addRoundRect(schedRect, 24f, 24f, Path.Direction.CW) }
        canvas.save()
        canvas.clipPath(schedClip)
        if (p1 != null) {
            val adj0 = doc.slotAdjust(0)
            canvas.drawRect(schedRect, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE })
            drawBitmapCenterCrop(canvas, p1, schedRect, adj0.zoom, adj0.focusX, adj0.focusY)
        } else {
            drawStudyPagePlaceholder(
                context = context,
                canvas = canvas,
                pageRect = schedRect,
                slotIndex = 0,
                layerIdx = 0,
                isOverlapped = false,
                subjectTag = "EXAM COUNTDOWN · PHOTO 1",
                doc = doc,
                accentColor = accentColor,
                warmPaper = false
            )
        }
        canvas.restore()
        canvas.drawRoundRect(
            schedRect, 24f, 24f,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = 2.8f
                color = withAlpha(accentColor, 185)
            }
        )

        canvas.drawRoundRect(
            deckRect, deckR, deckR,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = 3.5f
                color = withAlpha(accentColor, 200)
            }
        )
        canvas.restore()

        // Floating 3D Exam Checklist Card (Photo 2)
        val secondBmp = p2 ?: p1
        canvas.save()
        val cam2 = Camera()
        val mat2 = Matrix()
        cam2.save()
        cam2.setLocation(0f, 0f, -34f)
        cam2.rotateX(8f)
        cam2.rotateY(-20f)
        cam2.rotateZ(-5f)
        cam2.getMatrix(mat2)
        cam2.restore()
        canvas.concat(mat2)

        val checkRect = RectF(95f, -55f, 475f, 425f)
        val checkR = 30f
        canvas.drawRoundRect(
            RectF(checkRect.left - 24f, checkRect.top + 26f, checkRect.right - 24f, checkRect.bottom + 26f),
            checkR, checkR,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.argb(185, 0, 0, 0)
                maskFilter = BlurMaskFilter(34f, BlurMaskFilter.Blur.NORMAL)
            }
        )
        val cClip = Path().apply { addRoundRect(checkRect, checkR, checkR, Path.Direction.CW) }
        canvas.save()
        canvas.clipPath(cClip)
        if (secondBmp != null) {
            val adj1 = doc.slotAdjust(if (p2 != null) 1 else 0)
            canvas.drawRect(checkRect, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE })
            drawBitmapCenterCrop(canvas, secondBmp, checkRect, adj1.zoom, adj1.focusX, adj1.focusY)
        } else {
            drawStudyPagePlaceholder(
                context = context,
                canvas = canvas,
                pageRect = checkRect,
                slotIndex = 1,
                layerIdx = 1,
                isOverlapped = false,
                subjectTag = "STUDY CHECKLIST · PHOTO 2",
                doc = doc,
                accentColor = secondaryColor,
                warmPaper = true
            )
        }
        canvas.restore()
        canvas.drawRoundRect(
            checkRect, checkR, checkR,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = 4f
                color = accentColor
            }
        )
        canvas.restore()

        canvas.restore()
    }

    // ═══════════════════════════════════════════════════════════════════════
    // VARIANT 31 — 3D HOLOGRAPHIC MASTERY POSTER SHOWCASE (2 Photo Slots)
    // Creative 3D EdTech text-and-photo poster featuring a hovering 3D
    // holographic prism frame for Photo 1, luminous aurora rim rings, and
    // an overlapping 3D floating achievement card for Photo 2.
    // ═══════════════════════════════════════════════════════════════════════
    private fun drawHolographicMasteryPoster3D(
        context: Context,
        canvas: Canvas,
        rect: RectF,
        doc: PostDoc,
        p1: Bitmap?,
        p2: Bitmap?,
        accentColor: Int,
        secondaryColor: Int,
        lightColor: Int,
        darkColor: Int,
        deepColor: Int
    ) {
        val w = rect.width()
        val h = rect.height()
        if (w < 40f || h < 40f) return

        val cx = rect.centerX()
        val cy = rect.centerY()

        val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(
                cx, cy, max(w, h) * 0.60f,
                intArrayOf(
                    withAlpha(accentColor, 120),
                    withAlpha(secondaryColor, 55),
                    Color.TRANSPARENT
                ),
                floatArrayOf(0f, 0.58f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawCircle(cx, cy, max(w, h) * 0.60f, glowPaint)

        val baseW = 1000f
        val baseH = 980f
        val fitScale = min(w / baseW, h / baseH) * 0.94f

        canvas.save()
        canvas.translate(cx, cy)
        canvas.scale(fitScale, fitScale)

        // Glowing 3D orbital halo ring behind the holographic frame
        canvas.save()
        canvas.rotate(-14f)
        canvas.drawOval(
            RectF(-470f, -210f, 470f, 210f),
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = 5f
                shader = LinearGradient(
                    -470f, 0f, 470f, 0f,
                    intArrayOf(accentColor, Color.WHITE, secondaryColor),
                    null,
                    Shader.TileMode.CLAMP
                )
            }
        )
        canvas.restore()

        val camera = Camera()
        val matrix = Matrix()
        camera.save()
        camera.setLocation(0f, 0f, -34f)
        camera.rotateX(10f)
        camera.rotateY(-13f)
        camera.rotateZ(2.5f)
        camera.getMatrix(matrix)
        camera.restore()

        val holoRect = RectF(-430f, -395f, 300f, 375f)
        val holoR = 42f

        canvas.save()
        canvas.concat(matrix)

        canvas.drawRoundRect(
            RectF(holoRect.left + 26f, holoRect.top + 38f, holoRect.right + 26f, holoRect.bottom + 38f),
            holoR, holoR,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.argb(185, 0, 0, 0)
                maskFilter = BlurMaskFilter(44f, BlurMaskFilter.Blur.NORMAL)
            }
        )

        for (d in 12 downTo 1) {
            canvas.drawRoundRect(
                RectF(holoRect.left - d * 1.4f, holoRect.top + d * 1.8f, holoRect.right - d * 1.4f, holoRect.bottom + d * 1.8f),
                holoR, holoR,
                Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = blendColors(accentColor, secondaryColor, d / 12f)
                }
            )
        }

        val hClip = Path().apply { addRoundRect(holoRect, holoR, holoR, Path.Direction.CW) }
        canvas.save()
        canvas.clipPath(hClip)
        if (p1 != null) {
            val adj0 = doc.slotAdjust(0)
            canvas.drawRect(holoRect, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE })
            drawBitmapCenterCrop(canvas, p1, holoRect, adj0.zoom, adj0.focusX, adj0.focusY)
        } else {
            drawStudyPagePlaceholder(
                context = context,
                canvas = canvas,
                pageRect = holoRect,
                slotIndex = 0,
                layerIdx = 0,
                isOverlapped = false,
                subjectTag = "HOLOGRAPHIC SHOWCASE · PHOTO 1",
                doc = doc,
                accentColor = accentColor,
                warmPaper = false
            )
        }
        canvas.restore()

        canvas.drawRoundRect(
            holoRect, holoR, holoR,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = 4.5f
                shader = LinearGradient(
                    holoRect.left, holoRect.top, holoRect.right, holoRect.bottom,
                    intArrayOf(Color.WHITE, accentColor, secondaryColor),
                    null,
                    Shader.TileMode.CLAMP
                )
            }
        )
        canvas.restore()

        // Floating 3D Companion Mastery Card (Photo 2)
        val secondBmp = p2 ?: p1
        canvas.save()
        val cam2 = Camera()
        val mat2 = Matrix()
        cam2.save()
        cam2.setLocation(0f, 0f, -34f)
        cam2.rotateX(8f)
        cam2.rotateY(-20f)
        cam2.rotateZ(-5f)
        cam2.getMatrix(mat2)
        cam2.restore()
        canvas.concat(mat2)

        val subRect = RectF(85f, -75f, 470f, 420f)
        val subR = 32f
        canvas.drawRoundRect(
            RectF(subRect.left - 24f, subRect.top + 26f, subRect.right - 24f, subRect.bottom + 26f),
            subR, subR,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.argb(185, 0, 0, 0)
                maskFilter = BlurMaskFilter(34f, BlurMaskFilter.Blur.NORMAL)
            }
        )
        val sClip = Path().apply { addRoundRect(subRect, subR, subR, Path.Direction.CW) }
        canvas.save()
        canvas.clipPath(sClip)
        if (secondBmp != null) {
            val adj1 = doc.slotAdjust(if (p2 != null) 1 else 0)
            canvas.drawRect(subRect, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE })
            drawBitmapCenterCrop(canvas, secondBmp, subRect, adj1.zoom, adj1.focusX, adj1.focusY)
        } else {
            drawStudyPagePlaceholder(
                context = context,
                canvas = canvas,
                pageRect = subRect,
                slotIndex = 1,
                layerIdx = 1,
                isOverlapped = false,
                subjectTag = "MASTERY HIGHLIGHT · PHOTO 2",
                doc = doc,
                accentColor = secondaryColor,
                warmPaper = true
            )
        }
        canvas.restore()
        canvas.drawRoundRect(
            subRect, subR, subR,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = 4f
                color = Color.WHITE
            }
        )
        canvas.restore()

        canvas.restore()
    }

    // ═══════════════════════════════════════════════════════════════════════
    // VARIANT 32 — 3D SUBJECT BENTO GRID POSTER (1–4 Dynamic Photos)
    // Creative 3D EdTech text-and-photo bento showcase where inserting 1, 2, 3,
    // or 4 photos dynamically arranges 1–4 3D extruded modular bento tiles
    // with subject chips in perspective.
    // ═══════════════════════════════════════════════════════════════════════
    private fun drawSubjectBentoGridPoster3D(
        context: Context,
        canvas: Canvas,
        rect: RectF,
        doc: PostDoc,
        p1: Bitmap?,
        p2: Bitmap?,
        p3: Bitmap?,
        p4: Bitmap?,
        accentColor: Int,
        secondaryColor: Int,
        lightColor: Int,
        darkColor: Int,
        deepColor: Int
    ) {
        val w = rect.width()
        val h = rect.height()
        if (w < 40f || h < 40f) return

        val slots = resolveActiveSlots(doc, 4, p1, p2, p3, p4)
        val count = slots.size.coerceIn(1, 4)

        val cx = rect.centerX()
        val cy = rect.centerY()

        val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(
                cx, cy, max(w, h) * 0.58f,
                intArrayOf(
                    withAlpha(accentColor, 100),
                    withAlpha(secondaryColor, 45),
                    Color.TRANSPARENT
                ),
                floatArrayOf(0f, 0.58f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawCircle(cx, cy, max(w, h) * 0.58f, glowPaint)

        val baseW = 1000f
        val baseH = 980f
        val fitScale = min(w / baseW, h / baseH) * 0.94f

        canvas.save()
        canvas.translate(cx, cy)
        canvas.scale(fitScale, fitScale)

        val camera = Camera()
        val matrix = Matrix()
        camera.save()
        camera.setLocation(0f, 0f, -34f)
        camera.rotateX(11f)
        camera.rotateY(-12f)
        camera.rotateZ(2f)
        camera.getMatrix(matrix)
        camera.restore()

        canvas.save()
        canvas.concat(matrix)

        val tileColors = intArrayOf(
            accentColor,
            secondaryColor,
            Color.rgb(245, 158, 11),
            Color.rgb(16, 185, 129)
        )

        val tileRects: List<RectF> = when (count) {
            1 -> listOf(
                RectF(-420f, -390f, 420f, 390f)
            )
            2 -> listOf(
                RectF(-440f, -390f, -14f, 390f),
                RectF(14f, -390f, 440f, 390f)
            )
            3 -> listOf(
                RectF(-440f, -395f, 10f, 395f),
                RectF(36f, -395f, 440f, -12f),
                RectF(36f, 14f, 440f, 395f)
            )
            else -> listOf(
                RectF(-440f, -395f, -14f, -14f),
                RectF(14f, -395f, 440f, -14f),
                RectF(-440f, 14f, -14f, 395f),
                RectF(14f, 14f, 440f, 395f)
            )
        }

        for (i in 0 until count) {
            val tRect = tileRects[i]
            val slot = slots[i]
            val tColor = tileColors[i % tileColors.size]
            val tR = 30f

            // Drop shadow
            canvas.drawRoundRect(
                RectF(tRect.left + 18f, tRect.top + 26f, tRect.right + 18f, tRect.bottom + 26f),
                tR, tR,
                Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.argb(175, 0, 0, 0)
                    maskFilter = BlurMaskFilter(28f, BlurMaskFilter.Blur.NORMAL)
                }
            )

            // 3D tile extrusion
            for (d in 8 downTo 1) {
                canvas.drawRoundRect(
                    RectF(tRect.left - d * 1.3f, tRect.top + d * 1.6f, tRect.right - d * 1.3f, tRect.bottom + d * 1.6f),
                    tR, tR,
                    Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        color = blendColors(deepColor, tColor, (8 - d) / 14f)
                    }
                )
            }

            val clip = Path().apply { addRoundRect(tRect, tR, tR, Path.Direction.CW) }
            canvas.save()
            canvas.clipPath(clip)
            val bmp = slot.bitmap
            if (bmp != null) {
                val adj = doc.slotAdjust(slot.slotIndex)
                canvas.drawRect(tRect, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE })
                drawBitmapCenterCrop(canvas, bmp, tRect, adj.zoom, adj.focusX, adj.focusY)
            } else {
                drawStudyPagePlaceholder(
                    context = context,
                    canvas = canvas,
                    pageRect = tRect,
                    slotIndex = slot.slotIndex,
                    layerIdx = i,
                    isOverlapped = false,
                    subjectTag = "BENTO MODULE 0${i + 1}",
                    doc = doc,
                    accentColor = tColor,
                    warmPaper = i % 2 == 1
                )
            }
            canvas.restore()

            canvas.drawRoundRect(
                tRect, tR, tR,
                Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    style = Paint.Style.STROKE
                    strokeWidth = 3.5f
                    color = tColor
                }
            )
        }

        canvas.restore()
        canvas.restore()
    }

    // ═══════════════════════════════════════════════════════════════════════
    // VARIANT 33 — 3D ACADEMIC JOURNAL & STUDY EDITION COVER (1–3 Dynamic Photos)
    // Creative 3D EdTech editorial journal / syllabus issue showcase where
    // inserting 1, 2, or 3 images fans 1–3 glossy 3D academic publication
    // issues with realistic page-block edges and gold edition masthead trim.
    // ═══════════════════════════════════════════════════════════════════════
    private fun drawAcademicJournalCoverPoster3D(
        context: Context,
        canvas: Canvas,
        rect: RectF,
        doc: PostDoc,
        p1: Bitmap?,
        p2: Bitmap?,
        p3: Bitmap?,
        accentColor: Int,
        secondaryColor: Int,
        lightColor: Int,
        darkColor: Int,
        deepColor: Int
    ) {
        val w = rect.width()
        val h = rect.height()
        if (w < 40f || h < 40f) return

        val slots = resolveActiveSlots(doc, 3, p1, p2, p3)
        val count = slots.size.coerceIn(1, 3)

        val cx = rect.centerX()
        val cy = rect.centerY()

        val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(
                cx, cy, max(w, h) * 0.58f,
                intArrayOf(
                    withAlpha(accentColor, 105),
                    withAlpha(secondaryColor, 48),
                    Color.TRANSPARENT
                ),
                floatArrayOf(0f, 0.58f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawCircle(cx, cy, max(w, h) * 0.58f, glowPaint)

        val baseW = 1020f
        val baseH = 980f
        val fitScale = min(w / baseW, h / baseH) * 0.94f

        canvas.save()
        canvas.translate(cx, cy)
        canvas.scale(fitScale, fitScale)

        data class IssuePose(val dx: Float, val dy: Float, val rotY: Float, val rotZ: Float, val label: String)
        val poses: List<IssuePose> = when (count) {
            1 -> listOf(
                IssuePose(0f, 0f, -14f, -3f, "EDITION 01 · SPECIAL ISSUE")
            )
            2 -> listOf(
                IssuePose(145f, -30f, -16f, 6f, "EDITION 02 · STUDY GUIDE"),
                IssuePose(-135f, 30f, -14f, -5f, "EDITION 01 · SPECIAL ISSUE")
            )
            else -> listOf(
                IssuePose(215f, -55f, -18f, 9f, "EDITION 03 · EXAM ARCHIVE"),
                IssuePose(15f, -10f, -15f, 2f, "EDITION 02 · STUDY GUIDE"),
                IssuePose(-195f, 45f, -13f, -6f, "EDITION 01 · SPECIAL ISSUE")
            )
        }

        // Draw back-to-front so PoseLast (Photo 1) sits hero in the foreground
        for (pIdx in 0 until count) {
            val slotOrder = count - 1 - pIdx
            val slot = slots[slotOrder]
            val pose = poses[pIdx]

            canvas.save()
            canvas.translate(pose.dx, pose.dy)

            val camera = Camera()
            val matrix = Matrix()
            camera.save()
            camera.setLocation(0f, 0f, -34f)
            camera.rotateX(10f)
            camera.rotateY(pose.rotY)
            camera.rotateZ(pose.rotZ)
            camera.getMatrix(matrix)
            camera.restore()
            canvas.concat(matrix)

            val jRect = RectF(-275f, -380f, 275f, 380f)
            val jR = 24f

            // Drop shadow
            canvas.drawRoundRect(
                RectF(jRect.left + 24f, jRect.top + 36f, jRect.right + 24f, jRect.bottom + 36f),
                jR, jR,
                Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.argb(185, 0, 0, 0)
                    maskFilter = BlurMaskFilter(38f, BlurMaskFilter.Blur.NORMAL)
                }
            )

            // 3D Journal Spine & Glossy Page Block Thickness on Right Edge
            for (d in 14 downTo 1) {
                canvas.drawRoundRect(
                    RectF(jRect.left + d * 1.5f, jRect.top + d * 1.2f, jRect.right + d * 1.5f, jRect.bottom + d * 1.2f),
                    jR, jR,
                    Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        color = if (d % 2 == 0) Color.rgb(241, 245, 249) else Color.rgb(203, 213, 225)
                    }
                )
            }

            val clip = Path().apply { addRoundRect(jRect, jR, jR, Path.Direction.CW) }
            canvas.save()
            canvas.clipPath(clip)
            val bmp = slot.bitmap
            if (bmp != null) {
                val adj = doc.slotAdjust(slot.slotIndex)
                canvas.drawRect(jRect, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE })
                drawBitmapCenterCrop(canvas, bmp, jRect, adj.zoom, adj.focusX, adj.focusY)
            } else {
                drawStudyPagePlaceholder(
                    context = context,
                    canvas = canvas,
                    pageRect = jRect,
                    slotIndex = slot.slotIndex,
                    layerIdx = slotOrder,
                    isOverlapped = pIdx < count - 1,
                    subjectTag = pose.label,
                    doc = doc,
                    accentColor = if (slotOrder == 0) accentColor else secondaryColor,
                    warmPaper = slotOrder % 2 == 1
                )
            }

            // Left Spine Crease Highlight
            val spineRect = RectF(jRect.left, jRect.top, jRect.left + 26f, jRect.bottom)
            canvas.drawRect(
                spineRect,
                Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    shader = LinearGradient(
                        spineRect.left, spineRect.top, spineRect.right, spineRect.top,
                        intArrayOf(
                            Color.argb(140, 0, 0, 0),
                            Color.argb(95, 255, 255, 255),
                            Color.TRANSPARENT
                        ),
                        floatArrayOf(0f, 0.5f, 1f),
                        Shader.TileMode.CLAMP
                    )
                }
            )
            canvas.restore()

            canvas.drawRoundRect(
                jRect, jR, jR,
                Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    style = Paint.Style.STROKE
                    strokeWidth = 3.5f
                    color = if (slotOrder == 0) accentColor else Color.WHITE
                }
            )

            canvas.restore()
        }

        canvas.restore()
    }
}


