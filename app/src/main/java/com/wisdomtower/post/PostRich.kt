package com.wisdomtower.post

/**
 * Extra decoration for the "Rich" look. It only adds low-opacity shapes BEHIND the template's own shapes and text,
 * so every text box, photo area and social block stays exactly where the template put it. "Clean" (level 1) leaves the template alone.
 * Redesigned templates (layout.clean) are never decorated: their structure is the design.
 */
internal object PostRich {
    fun apply(layout: TemplateLayout, size: PostSize, level: Int): TemplateLayout {
        if (level < 2 || layout.clean) return layout
        val w = size.width.toFloat()
        val h = size.height.toFloat()
        val u = 1080f
        fun rx(r: Float) = r * u / w
        fun ry(r: Float) = r * u / h
        val onColor = layout.base != PostBase.LIGHT
        val line = if (onColor) PostTone.SECONDARY else PostTone.ACCENT
        val glow = if (onColor) PostTone.SECONDARY else PostTone.GLOW

        val extras = listOf(
            // Soft glow in the lower-left corner, fading out.
            DecorBox(
                DecorShape.OVAL, 0.10f - rx(.42f), 0.98f - ry(.42f), 2f * rx(.42f), 2f * ry(.42f),
                glow, if (onColor) 55 else 80, gradTo = glow, alphaTo = 0, angle = 45f
            ),
            // Two thin rings in the top-right corner.
            DecorBox(DecorShape.RING, 0.98f - rx(.30f), 0.02f - ry(.30f), 2f * rx(.30f), 2f * ry(.30f), line, 38, stroke = .005f),
            DecorBox(DecorShape.RING, 0.98f - rx(.21f), 0.02f - ry(.21f), 2f * rx(.21f), 2f * ry(.21f), line, 28, stroke = .004f),
            // Dot grid on the right edge.
            DecorBox(DecorShape.DOTS, 1f - rx(.20f) - 0.03f, 0.64f, rx(.20f), ry(.20f), line, 42),
            // Diagonal lines in the lower-left corner.
            DecorBox(DecorShape.STRIPES, 0f, 1f - ry(.22f), rx(.22f), ry(.22f), line, 45),
            // Small geometric accents: a diamond, a triangle and a hexagon.
            DecorBox(DecorShape.DIAMOND, 0.90f - rx(.022f), 0.30f - ry(.022f), 2f * rx(.022f), 2f * ry(.022f), line, 170),
            DecorBox(DecorShape.TRIANGLE, 0.95f - rx(.012f), 0.38f - ry(.012f), 2f * rx(.012f), 2f * ry(.012f), line, 130),
            DecorBox(DecorShape.HEXAGON, 0.07f - rx(.016f), 0.58f - ry(.016f), 2f * rx(.016f), 2f * ry(.016f), line, 140)
        )
        return layout.copy(decor = extras + layout.decor)
    }
}
