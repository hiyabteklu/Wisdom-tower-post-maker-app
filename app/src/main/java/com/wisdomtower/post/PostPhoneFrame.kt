package com.wisdomtower.post

import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * Phone body geometry, as a share of min(width, height) (every post size has a shorter side of 1080 px).
 * Shared by PostTemplatesDevice.kt (which sizes the screen window inside the body) and PostPhoneFrame (which draws the body).
 * Outside to inside: metal rim (7 px), black glass border (23 px), then the screen window, whose corner radius comes from
 * PhotoFrame.ROUNDED (PostEngine.framePath: unit * 0.05 = 54 px). Corners stay concentric, so the body radius is 84 px.
 */
internal const val PHONE_RIM = 0.0065f
internal const val PHONE_BEZEL = 0.0215f
internal const val PHONE_SCREEN_RADIUS = 0.05f

/**
 * Draws the realistic phone for the two device mockup templates (DecorShape.PHONE):
 * - drawBody: ground shadow, soft light behind the phone on dark posts, side buttons, a metal rim with a diagonal sheen and
 *   a specular bevel line (the 3D edge), and the black glass border. Drawn BEFORE the screenshot.
 * - drawGlass: a faint glass sheen, a soft inner shadow where the screen meets the border, and the front camera.
 *   Drawn AFTER the screenshot. "Save frame for video" erases the whole screen window afterwards, so none of this blocks a recording.
 */
object PostPhoneFrame {
    private fun alpha(c: Int, a: Int): Int =
        Color.argb(a.coerceIn(0, 255), Color.red(c), Color.green(c), Color.blue(c))

    private fun mixColor(c1: Int, c2: Int, t: Float): Int {
        val k = t.coerceIn(0f, 1f)
        val r = (Color.red(c1) * (1f - k) + Color.red(c2) * k).roundToInt().coerceIn(0, 255)
        val g = (Color.green(c1) * (1f - k) + Color.green(c2) * k).roundToInt().coerceIn(0, 255)
        val b = (Color.blue(c1) * (1f - k) + Color.blue(c2) * k).roundToInt().coerceIn(0, 255)
        return Color.rgb(r, g, b)
    }

    /** [rect] is the whole phone body (the DecorBox). [accent] gives the metal a faint brand tint. */
    fun drawBody(canvas: Canvas, rect: RectF, unit: Float, accent: Int, darkPage: Boolean) {
        val rim = unit * PHONE_RIM
        val outerR = unit * (PHONE_SCREEN_RADIUS + PHONE_BEZEL + PHONE_RIM)
        val glassR = unit * (PHONE_SCREEN_RADIUS + PHONE_BEZEL)

        val metalLight = mixColor(Color.rgb(226, 229, 236), accent, 0.08f)
        val metalMid = mixColor(Color.rgb(122, 128, 140), accent, 0.10f)
        val metalDark = mixColor(Color.rgb(34, 37, 45), accent, 0.08f)

        // 0. a soft glow behind the phone on dark posts, so the black body does not melt into the background
        if (darkPage) {
            val glowR = max(rect.width(), rect.height()) * 0.80f
            val glow = Paint(Paint.ANTI_ALIAS_FLAG)
            glow.shader = RadialGradient(
                rect.centerX(), rect.centerY(), glowR,
                intArrayOf(alpha(accent, 52), alpha(accent, 0)),
                floatArrayOf(0f, 1f),
                Shader.TileMode.CLAMP
            )
            canvas.drawCircle(rect.centerX(), rect.centerY(), glowR, glow)
        }

        // 1. ground shadow: one wide soft shadow, one tight contact shadow
        val wide = Paint(Paint.ANTI_ALIAS_FLAG)
        wide.color = Color.argb(165, 0, 0, 0)
        wide.maskFilter = BlurMaskFilter(unit * 0.05f, BlurMaskFilter.Blur.NORMAL)
        canvas.drawRoundRect(
            RectF(rect.left, rect.top + unit * 0.030f, rect.right, rect.bottom + unit * 0.034f),
            outerR, outerR, wide
        )
        val tight = Paint(Paint.ANTI_ALIAS_FLAG)
        tight.color = Color.argb(200, 0, 0, 0)
        tight.maskFilter = BlurMaskFilter(unit * 0.011f, BlurMaskFilter.Blur.NORMAL)
        canvas.drawRoundRect(
            RectF(rect.left, rect.top + unit * 0.008f, rect.right, rect.bottom + unit * 0.012f),
            outerR, outerR, tight
        )

        // 2. side buttons on the right edge (volume, then power), half hidden behind the body
        fun button(topShare: Float, lengthShare: Float) {
            val top = rect.top + rect.height() * topShare
            val r = RectF(rect.right - unit * 0.004f, top, rect.right + unit * 0.0045f, top + rect.height() * lengthShare)
            val p = Paint(Paint.ANTI_ALIAS_FLAG)
            p.shader = LinearGradient(
                r.left, 0f, r.right, 0f,
                intArrayOf(metalDark, metalLight, metalMid),
                floatArrayOf(0f, 0.55f, 1f),
                Shader.TileMode.CLAMP
            )
            canvas.drawRoundRect(r, unit * 0.0035f, unit * 0.0035f, p)
        }
        button(0.19f, 0.10f)
        button(0.33f, 0.055f)

        // 3. metal rim with a diagonal sheen
        val metal = Paint(Paint.ANTI_ALIAS_FLAG)
        metal.shader = LinearGradient(
            rect.left, rect.top, rect.right, rect.bottom,
            intArrayOf(metalLight, metalMid, metalDark, metalMid, metalLight),
            floatArrayOf(0f, 0.28f, 0.52f, 0.78f, 1f),
            Shader.TileMode.CLAMP
        )
        canvas.drawRoundRect(rect, outerR, outerR, metal)

        // 4. specular bevel on the outer edge: bright top-left, dim bottom-right (the 3D edge)
        val bevelW = max(1.6f, unit * 0.0022f)
        val bevel = Paint(Paint.ANTI_ALIAS_FLAG)
        bevel.style = Paint.Style.STROKE
        bevel.strokeWidth = bevelW
        bevel.shader = LinearGradient(
            rect.left, rect.top, rect.right, rect.bottom,
            intArrayOf(
                Color.argb(235, 255, 255, 255),
                Color.argb(0, 255, 255, 255),
                Color.argb(0, 255, 255, 255),
                Color.argb(150, 255, 255, 255)
            ),
            floatArrayOf(0f, 0.30f, 0.70f, 1f),
            Shader.TileMode.CLAMP
        )
        val bevelRect = RectF(rect.left + bevelW / 2f, rect.top + bevelW / 2f, rect.right - bevelW / 2f, rect.bottom - bevelW / 2f)
        canvas.drawRoundRect(bevelRect, outerR - bevelW / 2f, outerR - bevelW / 2f, bevel)

        // 5. black glass border inside the rim
        val glassRect = RectF(rect.left + rim, rect.top + rim, rect.right - rim, rect.bottom - rim)
        val glass = Paint(Paint.ANTI_ALIAS_FLAG)
        glass.shader = LinearGradient(
            0f, glassRect.top, 0f, glassRect.bottom,
            intArrayOf(Color.rgb(18, 20, 26), Color.rgb(6, 7, 10), Color.rgb(12, 13, 18)),
            floatArrayOf(0f, 0.5f, 1f),
            Shader.TileMode.CLAMP
        )
        canvas.drawRoundRect(glassRect, glassR, glassR, glass)

        // 6. dark groove between the rim and the glass, and a faint glint on the glass edge
        val groove = Paint(Paint.ANTI_ALIAS_FLAG)
        groove.style = Paint.Style.STROKE
        groove.strokeWidth = max(1.2f, unit * 0.0016f)
        groove.color = Color.argb(190, 0, 0, 0)
        canvas.drawRoundRect(glassRect, glassR, glassR, groove)
        val glintW = max(1f, unit * 0.0012f)
        val glint = Paint(Paint.ANTI_ALIAS_FLAG)
        glint.style = Paint.Style.STROKE
        glint.strokeWidth = glintW
        glint.color = Color.argb(34, 255, 255, 255)
        val glintRect = RectF(
            glassRect.left + glintW * 1.6f, glassRect.top + glintW * 1.6f,
            glassRect.right - glintW * 1.6f, glassRect.bottom - glintW * 1.6f
        )
        canvas.drawRoundRect(glintRect, glassR - glintW * 1.6f, glassR - glintW * 1.6f, glint)
    }

    /** [screen] is the screen window and [screenPath] its shape (the photo frame path). Draw this after the screenshot. */
    fun drawGlass(canvas: Canvas, screen: RectF, screenPath: Path, unit: Float) {
        canvas.save()
        canvas.clipPath(screenPath)

        // faint glass sheen from the top-left corner
        val sheen = Paint(Paint.ANTI_ALIAS_FLAG)
        sheen.shader = LinearGradient(
            screen.left, screen.top, screen.right, screen.bottom,
            intArrayOf(
                Color.argb(30, 255, 255, 255),
                Color.argb(0, 255, 255, 255),
                Color.argb(0, 255, 255, 255),
                Color.argb(14, 255, 255, 255)
            ),
            floatArrayOf(0f, 0.28f, 0.72f, 1f),
            Shader.TileMode.CLAMP
        )
        canvas.drawRect(screen, sheen)

        // soft inner shadow where the screen meets the border (only the inside half shows, because of the clip)
        val shade = Paint(Paint.ANTI_ALIAS_FLAG)
        shade.style = Paint.Style.STROKE
        shade.strokeWidth = unit * 0.012f
        shade.color = Color.argb(120, 0, 0, 0)
        shade.maskFilter = BlurMaskFilter(unit * 0.008f, BlurMaskFilter.Blur.NORMAL)
        canvas.drawPath(screenPath, shade)
        canvas.restore()

        // front camera, a punch hole at the top centre
        val cx = screen.centerX()
        val cy = screen.top + unit * 0.027f
        val r = unit * 0.0082f
        val hole = Paint(Paint.ANTI_ALIAS_FLAG)
        hole.color = Color.rgb(2, 3, 5)
        canvas.drawCircle(cx, cy, r * 1.55f, hole)
        val ring = Paint(Paint.ANTI_ALIAS_FLAG)
        ring.style = Paint.Style.STROKE
        ring.strokeWidth = max(1f, r * 0.14f)
        ring.color = Color.argb(70, 255, 255, 255)
        canvas.drawCircle(cx, cy, r * 1.55f, ring)
        val lens = Paint(Paint.ANTI_ALIAS_FLAG)
        lens.shader = RadialGradient(
            cx - r * 0.25f, cy - r * 0.25f, r,
            intArrayOf(Color.rgb(44, 56, 98), Color.rgb(8, 10, 22)),
            floatArrayOf(0f, 1f),
            Shader.TileMode.CLAMP
        )
        canvas.drawCircle(cx, cy, r, lens)
        val spark = Paint(Paint.ANTI_ALIAS_FLAG)
        spark.color = Color.argb(150, 255, 255, 255)
        canvas.drawCircle(cx - r * 0.35f, cy - r * 0.35f, r * 0.18f, spark)
    }
}
