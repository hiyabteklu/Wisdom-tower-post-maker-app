package com.wisdomtower.post

import com.wisdomtower.post.PostTone.ACCENT
import com.wisdomtower.post.PostTone.DARK
import com.wisdomtower.post.PostTone.DEEP
import com.wisdomtower.post.PostTone.GLOW
import com.wisdomtower.post.PostTone.LIGHT
import com.wisdomtower.post.PostTone.PAGE
import com.wisdomtower.post.PostTone.SECONDARY
import com.wisdomtower.post.PostTone.SOFT
import com.wisdomtower.post.PostTone.TINT

/**
 * Gives every template its visual richness: a gradient backdrop, gradient shapes, rings, dots, stripes, waves,
 * sparkles and soft shadows. Text boxes are never touched, so the fit that was already tested still holds.
 * All colors are tones, so they follow the Brand colors and the post's accent override.
 */
internal object PostLooks {

    private class G(size: PostSize) {
        private val u = 1080f
        private val w = size.width.toFloat()
        private val h = size.height.toFloat()
        private fun rx(r: Float) = r * u / w
        private fun ry(r: Float) = r * u / h

        /** Circle centered on canvas fractions cx, cy with radius r (fraction of the shorter side). */
        fun circle(
            cx: Float, cy: Float, r: Float, tone: PostTone, alpha: Int = 255,
            gradTo: PostTone? = null, alphaTo: Int = -1, angle: Float = 90f, over: Boolean = false
        ) = DecorBox(DecorShape.OVAL, cx - rx(r), cy - ry(r), 2f * rx(r), 2f * ry(r), tone, alpha, 0f, over, gradTo, alphaTo, angle)

        fun ring(cx: Float, cy: Float, r: Float, tone: PostTone, alpha: Int = 80, stroke: Float = 0.008f, over: Boolean = false) =
            DecorBox(DecorShape.RING, cx - rx(r), cy - ry(r), 2f * rx(r), 2f * ry(r), tone, alpha, 0f, over, null, -1, 90f, stroke)

        /** A square patch (side is a fraction of the shorter side) with its top-left corner at x, y. */
        fun patch(shape: DecorShape, x: Float, y: Float, side: Float, tone: PostTone, alpha: Int, over: Boolean = false) =
            DecorBox(shape, x, y, rx(side), ry(side), tone, alpha, 0f, over)

        fun spark(cx: Float, cy: Float, r: Float, tone: PostTone, alpha: Int = 230, over: Boolean = false) =
            DecorBox(DecorShape.SPARK, cx - rx(r), cy - ry(r), 2f * rx(r), 2f * ry(r), tone, alpha, 0f, over)

        fun wave(y0: Float, y1: Float, tone: PostTone, alpha: Int = 255, over: Boolean = false) =
            DecorBox(DecorShape.WAVE, 0f, y0, 1f, y1 - y0, tone, alpha, 0f, over)
    }

    /** Final order: behind, the template's own flat shapes, front. Shapes marked overPhoto land above the photo; scrim sits under the template's own overPhoto shapes. */
    private fun TemplateLayout.look(
        backdrop: Backdrop?,
        behind: List<DecorBox> = emptyList(),
        scrim: List<DecorBox> = emptyList(),
        front: List<DecorBox> = emptyList(),
        restyle: (Int, DecorBox) -> DecorBox = { _, d -> d }
    ): TemplateLayout {
        val own = decor.mapIndexed(restyle)
        return copy(
            backdrop = backdrop,
            decor = behind + own.filter { !it.overPhoto } + scrim + own.filter { it.overPhoto } + front
        )
    }

    fun enrich(id: String, size: PostSize, base: TemplateLayout): TemplateLayout {
        val g = G(size)
        return when (id) {

            "quote-center" -> base.look(
                Backdrop(LIGHT, SOFT, 160f),
                behind = listOf(
                    g.circle(1.0f, 0.02f, .36f, GLOW, 90, SOFT, 0, 135f),
                    g.ring(0.0f, 1.0f, .30f, ACCENT, 70, .010f),
                    g.ring(0.0f, 1.0f, .21f, ACCENT, 45, .007f),
                    g.patch(DecorShape.DOTS, .70f, .12f, .22f, ACCENT, 70),
                    g.spark(.89f, .74f, .030f, ACCENT, 190),
                    g.spark(.11f, .17f, .020f, ACCENT, 150)
                )
            )

            "quote-bar" -> base.look(
                Backdrop(LIGHT, TINT, 120f),
                behind = listOf(
                    g.circle(1.0f, 1.0f, .42f, GLOW, 80, TINT, 0, 225f),
                    g.patch(DecorShape.DOTS, .70f, .04f, .24f, ACCENT, 65),
                    g.ring(1.0f, 0.0f, .24f, ACCENT, 50, .008f),
                    g.spark(.88f, .55f, .028f, ACCENT, 170)
                ),
                restyle = { i, d -> if (i == 0) d.copy(gradTo = DEEP, angle = 90f) else d }
            )

            "quote-photo" -> base.look(
                Backdrop(DARK, DEEP, 150f),
                behind = listOf(
                    g.circle(0.1f, 0.08f, .42f, ACCENT, 130, DARK, 0, 45f),
                    g.ring(1.0f, 1.0f, .34f, SECONDARY, 40, .010f)
                ),
                scrim = listOf(DecorBox(DecorShape.RECT, 0f, 0f, 1f, 1f, DARK, 30, gradTo = DARK, alphaTo = 175, angle = 90f, overPhoto = true)),
                front = listOf(
                    g.ring(1.0f, 0.0f, .26f, SECONDARY, 60, .009f, true),
                    g.spark(.88f, .20f, .030f, SECONDARY, 220, true)
                )
            )

            "motivation-bold" -> base.look(
                Backdrop(ACCENT, DEEP, 150f),
                behind = listOf(
                    g.ring(1.0f, 0.0f, .44f, SECONDARY, 45, .012f),
                    g.ring(1.0f, 0.0f, .31f, SECONDARY, 35, .010f),
                    g.circle(0.0f, 1.0f, .30f, GLOW, 70, ACCENT, 0, 45f),
                    g.patch(DecorShape.STRIPES, .0f, .70f, .30f, SECONDARY, 30),
                    g.spark(.90f, .62f, .030f, SECONDARY, 200)
                )
            )

            "motivation-split" -> {
                val split = base.decor.first().y
                base.look(
                    Backdrop(LIGHT, TINT, 100f),
                    behind = listOf(
                        g.wave(split - .04f, split + .004f, ACCENT),
                        g.patch(DecorShape.DOTS, .72f, .02f, .24f, ACCENT, 60),
                        g.circle(1.0f, 0.0f, .30f, GLOW, 80, TINT, 0, 135f)
                    ),
                    front = listOf(g.ring(.95f, 1.0f, .28f, SECONDARY, 55, .010f)),
                    restyle = { i, d -> if (i == 0) d.copy(gradTo = DEEP, angle = 90f) else d }
                )
            }

            "mcq-four" -> base.look(
                Backdrop(LIGHT, SOFT, 120f),
                behind = listOf(
                    g.circle(1.0f, 0.0f, .36f, GLOW, 85, SOFT, 0, 135f),
                    g.patch(DecorShape.DOTS, .72f, .13f, .22f, ACCENT, 60),
                    g.spark(.10f, .40f, .026f, ACCENT, 170)
                ),
                restyle = { i, d -> if (i == 0) d.copy(tone = LIGHT, alpha = 240, shadow = true) else d }
            )

            "question-answer" -> base.look(
                Backdrop(LIGHT, SOFT, 150f),
                behind = listOf(
                    g.circle(1.0f, 0.0f, .36f, GLOW, 85, SOFT, 0, 135f),
                    g.patch(DecorShape.DOTS, .04f, .30f, .20f, ACCENT, 55),
                    g.ring(1.0f, 0.0f, .22f, ACCENT, 50, .008f)
                ),
                front = listOf(g.spark(.90f, .64f, .030f, SECONDARY, 230)),
                restyle = { i, d -> if (i == 0) d.copy(gradTo = DEEP, angle = 135f, shadow = true) else d }
            )

            "exam-date" -> base.look(
                Backdrop(ACCENT, DEEP, 150f),
                behind = listOf(
                    g.circle(.5f, .30f, .30f, SECONDARY, 50, ACCENT, 0, 90f),
                    g.ring(1.0f, 0.0f, .40f, SECONDARY, 45, .012f),
                    g.ring(0.0f, 1.0f, .32f, SECONDARY, 40, .010f),
                    g.patch(DecorShape.DOTS, .04f, .14f, .18f, SECONDARY, 50),
                    g.spark(.12f, .46f, .025f, SECONDARY, 210),
                    g.spark(.88f, .50f, .030f, SECONDARY, 210)
                )
            )

            "exam-countdown" -> {
                val c = base.decor.first()
                base.look(
                    Backdrop(LIGHT, SOFT, 150f),
                    behind = listOf(
                        DecorBox(DecorShape.RING, c.x - c.w * .10f, c.y - c.h * .10f, c.w * 1.2f, c.h * 1.2f, ACCENT, 90, stroke = .008f),
                        DecorBox(DecorShape.RING, c.x - c.w * .20f, c.y - c.h * .20f, c.w * 1.4f, c.h * 1.4f, ACCENT, 45, stroke = .006f),
                        g.patch(DecorShape.DOTS, .74f, .70f, .20f, ACCENT, 55),
                        g.spark(.86f, .22f, .030f, ACCENT, 190),
                        g.spark(.12f, .52f, .022f, ACCENT, 160)
                    ),
                    restyle = { i, d -> if (i == 0) d.copy(gradTo = DEEP, angle = 135f, shadow = true) else d }
                )
            }

            "tip-numbered" -> base.look(
                Backdrop(LIGHT, TINT, 130f),
                behind = listOf(
                    g.ring(.82f, .26f, .30f, ACCENT, 35, .014f),
                    g.ring(.82f, .26f, .22f, ACCENT, 28, .008f),
                    g.circle(0.0f, 1.0f, .34f, GLOW, 70, TINT, 0, 45f),
                    g.patch(DecorShape.DOTS, .70f, .08f, .22f, ACCENT, 55),
                    g.spark(.90f, .50f, .028f, ACCENT, 170)
                )
            )

            "tip-card" -> base.look(
                Backdrop(ACCENT, DEEP, 150f),
                behind = listOf(
                    g.ring(1.0f, 0.0f, .40f, SECONDARY, 45, .012f),
                    g.circle(0.0f, 1.0f, .30f, GLOW, 70, ACCENT, 0, 45f)
                ),
                front = listOf(g.spark(.90f, .20f, .028f, ACCENT, 220)),
                restyle = { i, d -> if (i == 0) d.copy(shadow = true) else d }
            )

            "subject-cover" -> {
                val cover = base.decor.first().h
                base.look(
                    Backdrop(LIGHT, LIGHT, 90f),
                    behind = listOf(
                        g.ring(1.0f, 1.0f, .30f, ACCENT, 45, .010f),
                        g.patch(DecorShape.DOTS, .72f, cover + .06f, .22f, ACCENT, 60)
                    ),
                    scrim = listOf(DecorBox(DecorShape.STRIPES, 0f, 0f, 1f, cover, SECONDARY, 28, overPhoto = true)),
                    front = listOf(g.wave(cover - .05f, cover + .003f, PAGE, 255, true)),
                    restyle = { i, d -> if (i == 0) d.copy(gradTo = DEEP, angle = 135f) else d }
                )
            }

            "subject-badge" -> base.look(
                Backdrop(LIGHT, SOFT, 150f),
                behind = listOf(
                    g.ring(.92f, .88f, .34f, ACCENT, 38, .014f),
                    g.ring(.92f, .88f, .24f, ACCENT, 30, .008f),
                    g.patch(DecorShape.DOTS, .70f, .08f, .24f, ACCENT, 60),
                    g.circle(0.0f, 1.0f, .30f, GLOW, 70, SOFT, 0, 45f),
                    g.spark(.86f, .30f, .030f, ACCENT, 180)
                ),
                restyle = { i, d -> if (i == 0) d.copy(gradTo = DEEP, angle = 0f, shadow = true) else d }
            )

            "promo-feature" -> base.look(
                Backdrop(DARK, DEEP, 140f),
                behind = listOf(
                    g.patch(DecorShape.STRIPES, .60f, .62f, .34f, SECONDARY, 22),
                    g.ring(.90f, .90f, .20f, SECONDARY, 40, .008f)
                ),
                front = listOf(
                    g.spark(.88f, .26f, .030f, SECONDARY, 220),
                    g.spark(.10f, .30f, .020f, SECONDARY, 170)
                ),
                restyle = { i, d -> if (i == 0) d.copy(gradTo = DEEP, alphaTo = 30, angle = 135f) else d }
            )

            "promo-download" -> base.look(
                Backdrop(ACCENT, DEEP, 155f),
                behind = listOf(
                    g.ring(1.0f, .30f, .34f, SECONDARY, 40, .012f),
                    g.ring(0.0f, .90f, .30f, SECONDARY, 40, .012f),
                    g.circle(.5f, .78f, .40f, GLOW, 70, ACCENT, 0, 90f),
                    g.patch(DecorShape.DOTS, .06f, .10f, .20f, SECONDARY, 45)
                ),
                front = listOf(
                    g.spark(.86f, .64f, .030f, SECONDARY, 230),
                    g.spark(.14f, .58f, .020f, SECONDARY, 190)
                ),
                restyle = { i, d -> if (i == 0) d.copy(gradTo = SOFT, angle = 0f, shadow = true) else d }
            )

            "reminder-checklist" -> base.look(
                Backdrop(LIGHT, SOFT, 160f),
                behind = listOf(
                    g.circle(1.0f, 0.0f, .34f, GLOW, 85, SOFT, 0, 135f),
                    g.patch(DecorShape.DOTS, .74f, .74f, .22f, ACCENT, 55),
                    g.ring(0.0f, 1.0f, .26f, ACCENT, 45, .009f)
                ),
                restyle = { i, d -> if (i == 0) d.copy(gradTo = DEEP, angle = 0f, shadow = true) else d }
            )

            "reminder-schedule" -> {
                val list = base.slots.first { it.slot == PostSlots.LIST }
                base.look(
                    Backdrop(LIGHT, TINT, 100f),
                    behind = listOf(
                        g.circle(1.0f, 0.0f, .38f, GLOW, 80, TINT, 0, 135f),
                        g.patch(DecorShape.DOTS, .72f, .06f, .24f, ACCENT, 55),
                        DecorBox(DecorShape.ROUND, list.x - .03f, list.y - .02f, list.w + .06f, list.h + .04f, LIGHT, 215, radius = .035f, shadow = true)
                    )
                )
            }

            "milestone-number" -> base.look(
                Backdrop(DARK, DEEP, 150f),
                behind = listOf(
                    g.circle(.5f, .34f, .34f, ACCENT, 120, DARK, 0, 90f),
                    g.ring(.5f, .34f, .30f, SECONDARY, 45, .008f),
                    g.ring(.5f, .34f, .40f, SECONDARY, 28, .006f),
                    g.patch(DecorShape.DOTS, .04f, .62f, .20f, SECONDARY, 40),
                    g.spark(.14f, .20f, .030f, SECONDARY, 220),
                    g.spark(.86f, .28f, .040f, SECONDARY, 230),
                    g.spark(.78f, .50f, .020f, SECONDARY, 190),
                    g.spark(.20f, .50f, .020f, SECONDARY, 190)
                )
            )

            "announce-plain" -> {
                val band = base.decor.first()
                base.look(
                    Backdrop(LIGHT, TINT, 110f),
                    behind = listOf(
                        g.ring(1.0f, 0.0f, .26f, ACCENT, 50, .009f),
                        g.patch(DecorShape.DOTS, .72f, .72f, .22f, ACCENT, 55)
                    ),
                    front = listOf(
                        DecorBox(DecorShape.RECT, 0f, band.y, .02f, band.h, ACCENT, gradTo = DEEP),
                        g.spark(.90f, .28f, .030f, ACCENT, 170)
                    ),
                    restyle = { i, d -> if (i == 0) d.copy(gradTo = SOFT, angle = 135f) else d }
                )
            }

            "announce-photo" -> base.look(
                Backdrop(DARK, DEEP, 150f),
                behind = listOf(g.circle(.2f, .2f, .45f, ACCENT, 130, DARK, 0, 45f)),
                scrim = listOf(DecorBox(DecorShape.RECT, 0f, .30f, 1f, .70f, DARK, 0, gradTo = DARK, alphaTo = 210, angle = 90f, overPhoto = true)),
                front = listOf(
                    g.ring(1.0f, 0.0f, .28f, SECONDARY, 55, .010f, true),
                    g.spark(.88f, .20f, .030f, SECONDARY, 220, true)
                )
            )

            "social-follow" -> base.look(
                Backdrop(DARK, DEEP, 150f),
                behind = listOf(
                    g.circle(.5f, .62f, .46f, ACCENT, 110, DARK, 0, 90f),
                    g.ring(1.0f, 0.0f, .36f, SECONDARY, 40, .010f),
                    g.ring(0.0f, 1.0f, .30f, SECONDARY, 34, .010f),
                    g.patch(DecorShape.DOTS, .04f, .08f, .20f, SECONDARY, 45)
                ),
                front = listOf(
                    g.spark(.88f, .20f, .030f, SECONDARY, 230),
                    g.spark(.10f, .50f, .022f, SECONDARY, 190)
                )
            )

            "social-links" -> base.look(
                Backdrop(LIGHT, SOFT, 150f),
                behind = listOf(
                    g.circle(1.0f, 0.0f, .36f, GLOW, 85, SOFT, 0, 135f),
                    g.ring(0.0f, 1.0f, .28f, ACCENT, 45, .010f),
                    g.patch(DecorShape.DOTS, .70f, .06f, .22f, ACCENT, 60),
                    g.spark(.90f, .36f, .028f, ACCENT, 180)
                ),
                restyle = { i, d -> if (i == 0) d.copy(shadow = true) else d }
            )

            "event-ticket" -> base.look(
                Backdrop(DEEP, DEEP, 90f),
                behind = listOf(
                    g.circle(0.0f, 0.0f, .40f, GLOW, 120, DEEP, 0, 45f),
                    g.circle(1.0f, 1.0f, .36f, ACCENT, 140, DEEP, 0, 225f),
                    g.ring(1.0f, 0.1f, .30f, SECONDARY, 40, .010f),
                    g.patch(DecorShape.STRIPES, .02f, .74f, .26f, SECONDARY, 24)
                ),
                restyle = { i, d -> if (i == 0) d.copy(shadow = true) else d }
            )

            "event-poster" -> base.look(
                Backdrop(DARK, DEEP, 140f),
                behind = listOf(
                    g.circle(.85f, .20f, .40f, ACCENT, 150, DARK, 0, 200f),
                    g.ring(0.0f, 1.0f, .34f, SECONDARY, 40, .010f),
                    g.patch(DecorShape.STRIPES, .62f, .66f, .34f, SECONDARY, 24)
                ),
                front = listOf(
                    g.spark(.95f, .42f, .024f, SECONDARY, 230),
                    g.spark(.05f, .62f, .020f, SECONDARY, 180)
                ),
                restyle = { i, d -> if (i == 0) d.copy(gradTo = SOFT, angle = 135f, shadow = true) else d }
            )

            else -> base
        }
    }
}
