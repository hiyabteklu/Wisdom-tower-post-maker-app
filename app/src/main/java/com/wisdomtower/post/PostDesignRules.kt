package com.wisdomtower.post

import kotlin.math.min

/**
 * Shared design rules (docs/APP_QUALITY_ROADMAP.md, slice Q2). One place for the numbers every template is judged by,
 * so a rule changes once and the Template QA screen reports against it.
 *
 * Sizes are in pixels on the standard export (1080 on the shorter side). A box size in a template is a fraction of the shorter side,
 * so the pixel size of its text is box.size * min(width, height). On a phone feed the 1080 px poster is shown about 3 times smaller,
 * so a body text under about 34 px is under 12 points on screen.
 *
 * First version: the values below are starting estimates, not final. Change them here after looking at the Template QA report.
 * Contrast is not listed here: the renderer's contrast guard already moves any text below about 4:1 to a readable color.
 */
object PostDesignRules {
    /** Smallest wanted size of message and list text, in pixels at 1080. */
    const val MIN_BODY_PX = 34f

    /** Smallest wanted size of the small label and the button text. */
    const val MIN_LABEL_PX = 26f

    /** Smallest wanted size of the headline. */
    const val MIN_HEADLINE_PX = 56f

    /** The renderer shrinks text down to this share of its size before it reports an overflow (see PostRenderer.drawSlot). */
    const val SHRINK_FLOOR = 0.6f

    /** The smallest wanted pixel size for a box, or null for boxes this rule does not judge (big numbers). */
    fun minPxFor(slot: String): Float? = when (slot) {
        PostSlots.BODY, PostSlots.LIST -> MIN_BODY_PX
        PostSlots.EYEBROW, PostSlots.BUTTON -> MIN_LABEL_PX
        PostSlots.HEADLINE -> MIN_HEADLINE_PX
        else -> null
    }

    /**
     * The boxes of this template whose own size is under the rule at this size. Looks at the layout only, nothing is drawn.
     * Each line reads like "Message 29 px (min 34)". Fixed marks (literal text) are skipped.
     */
    fun smallText(spec: PostTemplateSpec, size: PostSize): List<String> {
        val unit = min(size.width, size.height).toFloat()
        return spec.layout(size).slots.mapNotNull { box ->
            if (box.literal != null) return@mapNotNull null
            val wanted = minPxFor(box.slot) ?: return@mapNotNull null
            val px = box.size * unit
            if (px + 0.5f >= wanted) null
            else PostSlots.label(box.slot) + " " + px.toInt() + " px (min " + wanted.toInt() + ")"
        }
    }
}
