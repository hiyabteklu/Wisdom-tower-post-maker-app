package com.wisdomtower.post

/**
 * Split templates (family "Split"): half text, half photo, with a stylized cut between the two halves.
 *
 * The photo fills the post (minus the footer strip). A solid panel in the template color is drawn over it (overPhoto) and its edge is the cut:
 * a rounded card, a big arc, a pill, a scalloped (stamp) edge, a curved drop, a circle. Nothing in the renderer had to change.
 * Without a photo the photo half shows a brand gradient placeholder, so the Library cards still look like a split.
 * Every template has its own placement for 1:1, 4:3 and 9:16 (4:5 falls into the 1:1 branch). On 9:16 the split turns horizontal: photo on top, text below.
 */

private enum class SpKind { CARD, ARC, PILL, SCALLOP, DROP, CIRCLE }

private class SpCfg(val kind: SpKind, val left: Boolean, val deep: Boolean)

private fun spOver(
    shape: DecorShape, x: Float, y: Float, w: Float, h: Float, tone: PostTone, radius: Float = 0f, shadow: Boolean = false
) = DecorBox(shape = shape, x = x, y = y, w = w, h = h, tone = tone, radius = radius, overPhoto = true, shadow = shadow)

/** A flat circle on canvas fractions; the radius is in pixels of the 1080 base. */
private fun spDisc(size: PostSize, cx: Float, cy: Float, rPx: Float, tone: PostTone) =
    spOver(DecorShape.OVAL, cx - rPx / size.width, cy - rPx / size.height, 2f * rPx / size.width, 2f * rPx / size.height, tone)

/** The panel (the text half) drawn over the photo. These are the left-hand versions; a right-hand template mirrors them. */
private fun spPanel(kind: SpKind, size: PostSize, bottom: Boolean, t: PostTone): List<DecorBox> {
    val story = size == PostSize.STORY
    val wide = size == PostSize.LANDSCAPE || size == PostSize.WIDE
    if (bottom) {
        val y0 = if (story) .50f else .58f
        return when (kind) {
            SpKind.CARD -> listOf(spOver(DecorShape.ROUND, .05f, y0, .90f, if (story) .46f else .38f, t, .08f, true))
            SpKind.ARC, SpKind.DROP -> listOf(spOver(DecorShape.OVAL, -.40f, y0 - .04f, 1.80f, 1.20f, t, 0f, true))
            SpKind.PILL -> listOf(spOver(DecorShape.ROUND, .04f, y0, .92f, if (story) .44f else .36f, t, .20f, true))
            SpKind.SCALLOP -> listOf(spOver(DecorShape.RECT, 0f, y0 + .04f, 1f, 1f, t)) +
                (0..4).map { spDisc(size, .1f + .2f * it, y0 + .04f, 95f, t) }
            SpKind.CIRCLE -> listOf(spOver(DecorShape.OVAL, -.40f, y0, 1.80f, if (story) 1.0125f else 1.80f, t, 0f, true))
        }
    }
    val edge = if (wide) .50f else .48f
    return when (kind) {
        SpKind.CARD -> listOf(spOver(DecorShape.ROUND, -.05f, .08f, .55f, .80f, t, .07f, true))
        SpKind.ARC, SpKind.DROP -> listOf(spOver(DecorShape.OVAL, if (wide) -.50f else -.55f, -.25f, if (wide) 1.0f else 1.1f, 1.5f, t, 0f, true))
        SpKind.PILL -> listOf(spOver(DecorShape.ROUND, -.30f, .10f, .80f, .78f, t, .37f, true))
        SpKind.SCALLOP -> listOf(spOver(DecorShape.RECT, 0f, 0f, edge, 1f, t)) +
            (0..6).map { spDisc(size, edge, .16f * it, 95f, t) }
        SpKind.CIRCLE ->
            if (wide) listOf(spOver(DecorShape.OVAL, -.28f, 0f, .75f, 1f, t, 0f, true))
            else listOf(spOver(DecorShape.OVAL, -.40f, .05f, .90f, .90f, t, 0f, true))
    }
}

private fun spLayout(size: PostSize, c: SpCfg): TemplateLayout {
    val t = if (c.deep) PostTone.DEEP else PostTone.TINT
    val story = size == PostSize.STORY
    val wide = size == PostSize.LANDSCAPE || size == PostSize.WIDE
    val bottom = story || (c.kind == SpKind.DROP && !wide)
    var panel = spPanel(c.kind, size, bottom, t)
    if (!bottom && !c.left) panel = panel.map { it.copy(x = 1f - it.x - it.w) }

    val eyeTone = if (c.deep) PostTone.SECONDARY else PostTone.ACCENT
    val headTone = if (c.deep) PostTone.LIGHT else PostTone.TEXT
    val bodyTone = if (c.deep) PostTone.LIGHT_MUTED else PostTone.MUTED

    val tx: Float
    val tw: Float
    val eyeY: Float
    val headY: Float
    val headH: Float
    val bodyY: Float
    val bodyH: Float
    val eyeS: Float
    val headS: Float
    val bodyS: Float
    if (bottom) {
        tx = .09f; tw = .82f
        if (story) {
            eyeY = .60f; headY = .64f; headH = .15f; bodyY = .80f; bodyH = .07f
            eyeS = .026f; headS = .072f; bodyS = .032f
        } else {
            eyeY = .65f; headY = .69f; headH = .12f; bodyY = .82f; bodyH = .05f
            eyeS = .022f; headS = .056f; bodyS = .028f
        }
    } else {
        tx = if (wide) (if (c.left) .06f else .58f) else (if (c.left) .07f else .62f)
        tw = if (wide) .34f else .32f
        eyeY = .25f; headY = .31f; headH = .30f; bodyY = .63f; bodyH = .13f
        eyeS = .022f; headS = if (wide) .052f else .054f; bodyS = .027f
    }
    val ruleY = if (bottom) eyeY - .03f else .21f

    val decor = listOf(
        // placeholder for the photo half; the photo covers it
        DecorBox(shape = DecorShape.RECT, x = 0f, y = 0f, w = 1f, h = .88f, tone = PostTone.GLOW, gradTo = PostTone.DEEP, angle = 135f)
    ) + panel + DecorBox(
        shape = DecorShape.RECT, x = tx, y = ruleY, w = .08f, h = 8f / size.height,
        tone = if (c.deep) PostTone.SECONDARY else PostTone.ACCENT, overPhoto = true
    )
    val slots = listOf(
        SlotBox(PostSlots.EYEBROW, tx, eyeY, tw, .04f, eyeS, PostFont.BOLD, eyeTone, false, true),
        SlotBox(PostSlots.HEADLINE, tx, headY, tw, headH, headS, PostFont.DISPLAY, headTone),
        SlotBox(PostSlots.BODY, tx, bodyY, tw, bodyH, bodyS, PostFont.REGULAR, bodyTone)
    )
    return TemplateLayout(
        if (c.deep) PostBase.DARK else PostBase.LIGHT,
        decor,
        slots,
        photo = PostPhotoArea(0f, 0f, 1f, .88f, 12),
        backdrop = Backdrop(t, t, 90f)
    )
}

private fun splitTemplate(id: String, name: String, samples: Map<String, String>, cfg: SpCfg) =
    PostTemplateSpec(id, name, "Split", samples, TemplateLook()) { size -> spLayout(size, cfg).copy(clean = true) }

internal val SplitTemplates: List<PostTemplateSpec> = listOf(
    splitTemplate(
        "split-card", "Card slice",
        mapOf(
            PostSlots.EYEBROW to "OPEN ENROLLMENT",
            PostSlots.HEADLINE to "Your next chapter starts here",
            PostSlots.BODY to "Join Wisdom Tower Academy this term."
        ),
        SpCfg(SpKind.CARD, left = true, deep = false)
    ),
    splitTemplate(
        "split-arc", "Arc slice",
        mapOf(
            PostSlots.EYEBROW to "MEET YOUR TEACHERS",
            PostSlots.HEADLINE to "Learning led by people who care",
            PostSlots.BODY to "Small classes and real attention for every student."
        ),
        SpCfg(SpKind.ARC, left = false, deep = true)
    ),
    splitTemplate(
        "split-pill", "Pill slice",
        mapOf(
            PostSlots.EYEBROW to "WHY WISDOM TOWER",
            PostSlots.HEADLINE to "Small classes. Big results.",
            PostSlots.BODY to "Every student gets the time they need to grow."
        ),
        SpCfg(SpKind.PILL, left = true, deep = true)
    ),
    splitTemplate(
        "split-scallop", "Scallop slice",
        mapOf(
            PostSlots.EYEBROW to "CAMPUS LIFE",
            PostSlots.HEADLINE to "Where curious minds meet",
            PostSlots.BODY to "Clubs, labs and a library that never feels empty."
        ),
        SpCfg(SpKind.SCALLOP, left = false, deep = false)
    ),
    splitTemplate(
        "split-drop", "Curved drop",
        mapOf(
            PostSlots.EYEBROW to "EXAM SEASON",
            PostSlots.HEADLINE to "Prepare with people who know the exam",
            PostSlots.BODY to "Free revision sessions every weekend."
        ),
        SpCfg(SpKind.DROP, left = false, deep = true)
    ),
    splitTemplate(
        "split-circle", "Circle slice",
        mapOf(
            PostSlots.EYEBROW to "SUCCESS STORY",
            PostSlots.HEADLINE to "Top of the class, top of the city",
            PostSlots.BODY to "Congratulations to this year's highest scorers."
        ),
        SpCfg(SpKind.CIRCLE, left = true, deep = false)
    )
)
