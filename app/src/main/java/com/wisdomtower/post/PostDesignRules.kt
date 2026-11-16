package com.wisdomtower.post

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * Shared design rules (docs/APP_QUALITY_ROADMAP.md, slice Q2). One place for the numbers every template is judged by,
 * so a rule changes once and the Template QA screen reports against it.
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

    /**
     * Checks that every non-bleeding slot in [spec] at [size] stays inside the safe area (4%..96% horizontally,
     * 5.5%..89% vertically above the footer) and does not overlap another unrotated slot in the same column.
     */
    fun safeAreaIssues(spec: PostTemplateSpec, size: PostSize): List<String> {
        val layout = spec.layout(size)
        val doc = PostDrafts.factory(spec.id, size)
        val safeRects = PostEngine.safeNormalizedRects(doc, layout)
        val issues = mutableListOf<String>()

        val slots = layout.slots.filter {
            it.literal == null && !(layout.bleed && (it.slot == PostSlots.LETTER || it.slot == PostSlots.WORD))
        }
        for (b in slots) {
            val r = safeRects[b.slot] ?: continue
            if (abs(b.rotate) <= 0.5f) {
                if (r.left < PostEngine.SAFE_LEFT - 0.005f || r.right > PostEngine.SAFE_RIGHT + 0.005f) {
                    issues += "${PostSlots.label(b.slot)} outside horizontal safe area"
                }
                if (r.top < PostEngine.SAFE_TOP - 0.005f || r.bottom > PostEngine.SAFE_BOTTOM_WITH_FOOTER + 0.005f) {
                    issues += "${PostSlots.label(b.slot)} overlaps footer or top edge"
                }
            }
        }
        val unrotated = slots.filter { abs(it.rotate) <= 0.5f }
        for (i in 0 until unrotated.size) {
            val a = unrotated[i]
            val ra = safeRects[a.slot] ?: continue
            for (j in i + 1 until unrotated.size) {
                val b = unrotated[j]
                val rb = safeRects[b.slot] ?: continue
                val ox = min(ra.right, rb.right) - max(ra.left, rb.left)
                val oy = min(ra.bottom, rb.bottom) - max(ra.top, rb.top)
                if (ox > min(ra.width(), rb.width()) * 0.35f && oy > 0.004f) {
                    issues += "${PostSlots.label(a.slot)} overlaps ${PostSlots.label(b.slot)}"
                }
            }
        }
        return issues
    }
}
