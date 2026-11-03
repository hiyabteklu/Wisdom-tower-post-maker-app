package com.wisdomtower.post

/**
 * Phase 3, batch 3 of REDESIGN_PLAN.md: the ten marketing and type-led templates.
 *
 * Marketing (7): Feature spotlight, Download call to action, Event ticket, Event poster, Notice, Milestone, Follow us.
 * Type-led (3): Aurora statement, Giant ink type, Split block.
 * Each has its own structure (glass card on a spotlight, big button, ticket and stub, date block, title panel with action strip,
 * number between rules, icon cards, soft glows, giant stacked words, two-tone split), not a recolor.
 *
 * Same rules as the photo and information templates: every layout is marked clean (PostRich adds nothing, PostLooks is skipped),
 * nothing uses DIAMOND, TRIANGLE, HEXAGON, RING, DOTS, STRIPES or WAVE, and every layout has its own placement for 1:1, 4:3 and 9:16.
 * Any other size (4:5 later) falls into the 1:1 branch until it gets its own. Text sits between 12% and 88% of the height;
 * on 9:16 it starts at 16% to 17% so the lowered logo box stays clear.
 *
 * Split block uses a straight vertical cut (1:1 and 4:3) or a straight horizontal cut (9:16), because the renderer has no diagonal shape.
 * Registered through InfoTemplates in PostTemplatesInfo.kt, so PostTemplates.kt did not need to change. Phase 4 will tidy this.
 */

private const val MKU = 1080f

private val MK_LIGHT = Backdrop(PostTone.TINT, PostTone.SOFT, 135f)
private val MK_DARK = Backdrop(PostTone.DEEP, PostTone.PAGE, 150f)

/** Picks the value for the current size. Any size that is not 4:3 or 9:16 gets the 1:1 value. */
private fun <T> PostSize.mkPick(square: T, wide: T, story: T): T = when (this) {
    PostSize.STORY -> story
    PostSize.LANDSCAPE, PostSize.WIDE -> wide
    else -> square
}

private fun mkTemplate(
    id: String,
    name: String,
    family: String,
    samples: Map<String, String>,
    build: (PostSize) -> TemplateLayout
) = PostTemplateSpec(id, name, family, samples) { size -> build(size).copy(clean = true) }

private fun mkText(
    slot: String, x: Float, y: Float, w: Float, h: Float, size: Float, font: PostFont, tone: PostTone,
    centered: Boolean = false, caps: Boolean = false
) = SlotBox(slot, x, y, w, h, size, font, tone, centered, caps)

private fun mkList(
    slot: String, x: Float, y: Float, w: Float, h: Float, size: Float, font: PostFont, tone: PostTone, style: ListStyle
) = SlotBox(slot, x, y, w, h, size, font, tone, false, false, style)

/** The top of a one-line box so that its text sits at the vertical centre cy (a canvas fraction). fontPx is the text size in pixels. */
private fun mkTopFor(size: PostSize, cy: Float, fontPx: Float): Float = cy - 0.62f * fontPx / size.height

/** One line of text centred on the point (x + w / 2, cy). Used for words inside a button, a tab or a date block. */
private fun mkLine(
    size: PostSize, slot: String, x: Float, w: Float, cy: Float, sz: Float, font: PostFont, tone: PostTone, caps: Boolean = false
): SlotBox {
    val px = sz * MKU
    return SlotBox(slot, x, mkTopFor(size, cy, px), w, px * 1.5f / size.height, sz, font, tone, true, caps)
}

/** A rounded block. radius is a fraction of the shorter side. to turns the fill into a gradient. */
private fun mkBlock(
    x: Float, y: Float, w: Float, h: Float, radius: Float, tone: PostTone,
    to: PostTone? = null, angle: Float = 135f, shadow: Boolean = false
) = DecorBox(DecorShape.ROUND, x, y, w, h, tone, 255, radius, false, to, -1, angle, 0f, shadow)

/** A see-through light card for dark backgrounds. */
private fun mkGlass(x: Float, y: Float, w: Float, h: Float, radius: Float, alpha: Int = 30) =
    DecorBox(DecorShape.ROUND, x, y, w, h, PostTone.LIGHT, alpha, radius)

/** A square-cornered panel, flat or a gradient. */
private fun mkPanel(x: Float, y: Float, w: Float, h: Float, tone: PostTone, to: PostTone? = null, angle: Float = 135f) =
    DecorBox(DecorShape.RECT, x, y, w, h, tone, 255, 0f, false, to, -1, angle)

/** A thin horizontal rule. w is a fraction of the width, thickPx is in pixels of the 1080 base. */
private fun mkRule(size: PostSize, x: Float, y: Float, w: Float, tone: PostTone, thickPx: Float = 6f) =
    DecorBox(DecorShape.RECT, x, y, w, thickPx / size.height, tone)

/** A thin vertical rule. h is a fraction of the height, thickPx is in pixels of the 1080 base. */
private fun mkVRule(size: PostSize, x: Float, y: Float, h: Float, tone: PostTone, thickPx: Float = 6f) =
    DecorBox(DecorShape.RECT, x, y, thickPx / size.width, h, tone)

/** A flat circle centered on canvas fractions; the radius is in pixels of the 1080 base. */
private fun mkDisc(size: PostSize, cx: Float, cy: Float, rPx: Float, tone: PostTone, alpha: Int = 255) = DecorBox(
    DecorShape.OVAL, cx - rPx / size.width, cy - rPx / size.height, 2f * rPx / size.width, 2f * rPx / size.height, tone, alpha
)

/** A soft circle of light centered on canvas fractions: starts at tone and fades out toward the edge. Radius in pixels of the 1080 base. */
private fun mkGlow(size: PostSize, cx: Float, cy: Float, rPx: Float, tone: PostTone, to: PostTone, alpha: Int) = DecorBox(
    DecorShape.OVAL, cx - rPx / size.width, cy - rPx / size.height, 2f * rPx / size.width, 2f * rPx / size.height, tone,
    alpha = alpha, gradTo = to, alphaTo = 0, angle = 45f
)

internal val MarketingTemplates: List<PostTemplateSpec> = listOf(

    // 16. Feature spotlight: dark page, a spotlight glow, the headline, then the features on a glass card
    mkTemplate(
        "mkt-feature", "Feature spotlight", "Marketing",
        mapOf(
            PostSlots.EYEBROW to "NEW IN THE APP",
            PostSlots.HEADLINE to "Study smarter, anywhere",
            PostSlots.LIST to "Short notes for every chapter\nFlashcards that stick\nExam practice with solutions"
        ),
        ::mkFeatureLayout
    ),

    // 17. Download call to action: loud headline, one big button
    mkTemplate(
        "mkt-download", "Download call to action", "Marketing",
        mapOf(
            PostSlots.EYEBROW to "GET THE APP",
            PostSlots.HEADLINE to "Your whole course in your pocket",
            PostSlots.BODY to "Free to start. Made for exam season.",
            PostSlots.BUTTON to "Download on Telegram"
        ),
        ::mkDownloadLayout
    ),

    // 18. Event ticket: a gradient ticket and a separate stub with the date, a thin tear line between them
    mkTemplate(
        "mkt-ticket", "Event ticket", "Marketing",
        mapOf(
            PostSlots.EYEBROW to "ADMIT ONE",
            PostSlots.HEADLINE to "Free exam revision session",
            PostSlots.NUMBER to "12 NOV",
            PostSlots.BODY to "Saturday, 9:00 AM at the main hall. Bring your questions."
        ),
        ::mkTicketLayout
    ),

    // 19. Event poster: a white date block, a loud headline under it
    mkTemplate(
        "mkt-poster", "Event poster", "Marketing",
        mapOf(
            PostSlots.EYEBROW to "REVISION CAMP, FREE ENTRY",
            PostSlots.NUMBER to "12 NOV",
            PostSlots.HEADLINE to "Exam revision weekend",
            PostSlots.BODY to "Saturday, 9:00 AM at the main hall. Bring your questions."
        ),
        ::mkPosterLayout
    ),

    // 20. Notice: a dark title panel with a label tab, the message, and an action strip
    mkTemplate(
        "mkt-notice", "Notice", "Marketing",
        mapOf(
            PostSlots.EYEBROW to "IMPORTANT",
            PostSlots.HEADLINE to "Registration closes on Friday",
            PostSlots.BODY to "Send your name and student ID before 5:00 PM. Late forms are not accepted.",
            PostSlots.BUTTON to "Register now"
        ),
        ::mkNoticeLayout
    ),

    // 21. Milestone: a giant number between two thin rules, lit from behind
    mkTemplate(
        "mkt-milestone", "Milestone", "Marketing",
        mapOf(
            PostSlots.EYEBROW to "MILESTONE",
            PostSlots.NUMBER to "10K",
            PostSlots.HEADLINE to "Students learning with us",
            PostSlots.BODY to "Thank you for being part of the journey."
        ),
        ::mkMilestoneLayout
    ),

    // 22. Follow us: a gradient title card and the Brand icons as big cards
    mkTemplate(
        "mkt-follow", "Follow us", "Marketing",
        mapOf(
            PostSlots.EYEBROW to "STAY CONNECTED",
            PostSlots.HEADLINE to "Follow us everywhere you study",
            PostSlots.BODY to "Daily notes, tips and exam updates on every channel."
        ),
        ::mkFollowLayout
    ),

    // 23. Aurora statement: two soft glows and one big statement
    mkTemplate(
        "type-aurora", "Aurora statement", "Type",
        mapOf(
            PostSlots.EYEBROW to "REMEMBER THIS",
            PostSlots.HEADLINE to "You do not need more time. You need a start.",
            PostSlots.BODY to "Open the book for ten minutes. The rest follows."
        ),
        ::mkAuroraLayout
    ),

    // 24. Giant ink type: huge stacked words, one per line, with a slim brand tab on the left
    mkTemplate(
        "type-ink", "Giant ink type", "Type",
        mapOf(
            PostSlots.EYEBROW to "THE ROUTINE",
            PostSlots.HEADLINE to "Study. Test. Repeat.",
            PostSlots.BODY to "Three words that carry a whole exam season."
        ),
        ::mkInkLayout
    ),

    // 25. Split block: two tones, a straight cut, the statement on one side and the explanation on the other
    mkTemplate(
        "type-split", "Split block", "Type",
        mapOf(
            PostSlots.EYEBROW to "THE BIG IDEA",
            PostSlots.HEADLINE to "Learn it once, review it often",
            PostSlots.BODY to "A short review within a day, then again after a week, keeps the lesson from fading. Ten minutes beats a late night."
        ),
        ::mkSplitLayout
    )
)

private fun mkFeatureLayout(size: PostSize): TemplateLayout {
    val decor = size.mkPick(
        listOf(
            mkGlow(size, .90f, .10f, 430f, PostTone.ACCENT, PostTone.GLOW, 210),
            mkGlow(size, .04f, .96f, 300f, PostTone.GLOW, PostTone.DEEP, 120),
            mkRule(size, .08f, .465f, .12f, PostTone.SECONDARY, 8f),
            mkGlass(.07f, .52f, .86f, .34f, .035f)
        ),
        listOf(
            mkGlow(size, .90f, .10f, 430f, PostTone.ACCENT, PostTone.GLOW, 210),
            mkGlow(size, .04f, .96f, 300f, PostTone.GLOW, PostTone.DEEP, 120),
            mkRule(size, .06f, .70f, .12f, PostTone.SECONDARY, 8f),
            mkGlass(.48f, .17f, .46f, .66f, .035f)
        ),
        listOf(
            mkGlow(size, .90f, .12f, 430f, PostTone.ACCENT, PostTone.GLOW, 210),
            mkGlow(size, .04f, .96f, 320f, PostTone.GLOW, PostTone.DEEP, 120),
            mkRule(size, .08f, .46f, .12f, PostTone.SECONDARY, 8f),
            mkGlass(.07f, .51f, .86f, .34f, .035f)
        )
    )
    val slots = size.mkPick(
        listOf(
            mkText(PostSlots.EYEBROW, .08f, .15f, .70f, .035f, .024f, PostFont.BOLD, PostTone.SECONDARY, caps = true),
            mkText(PostSlots.HEADLINE, .08f, .20f, .84f, .26f, .078f, PostFont.DISPLAY, PostTone.LIGHT),
            mkList(PostSlots.LIST, .12f, .55f, .76f, .29f, .038f, PostFont.BOLD, PostTone.LIGHT, ListStyle.CHECK)
        ),
        listOf(
            mkText(PostSlots.EYEBROW, .06f, .19f, .38f, .04f, .022f, PostFont.BOLD, PostTone.SECONDARY, caps = true),
            mkText(PostSlots.HEADLINE, .06f, .25f, .38f, .43f, .066f, PostFont.DISPLAY, PostTone.LIGHT),
            mkList(PostSlots.LIST, .53f, .22f, .36f, .56f, .038f, PostFont.BOLD, PostTone.LIGHT, ListStyle.CHECK)
        ),
        listOf(
            mkText(PostSlots.EYEBROW, .08f, .17f, .84f, .03f, .026f, PostFont.BOLD, PostTone.SECONDARY, caps = true),
            mkText(PostSlots.HEADLINE, .08f, .21f, .84f, .23f, .092f, PostFont.DISPLAY, PostTone.LIGHT),
            mkList(PostSlots.LIST, .12f, .54f, .76f, .28f, .042f, PostFont.BOLD, PostTone.LIGHT, ListStyle.CHECK)
        )
    )
    return TemplateLayout(PostBase.DARK, decor, slots, backdrop = MK_DARK)
}

private fun mkDownloadLayout(size: PostSize): TemplateLayout {
    val decor = size.mkPick(
        listOf(
            mkDisc(size, .88f, .14f, 330f, PostTone.SECONDARY, 24),
            mkBlock(.07f, .72f, .86f, .12f, .06f, PostTone.LIGHT, shadow = true)
        ),
        listOf(
            mkDisc(size, .84f, .50f, 390f, PostTone.SECONDARY, 24),
            mkBlock(.06f, .74f, .46f, .12f, .06f, PostTone.LIGHT, shadow = true)
        ),
        listOf(
            mkDisc(size, .90f, .22f, 400f, PostTone.SECONDARY, 24),
            mkBlock(.08f, .73f, .84f, .10f, .06f, PostTone.LIGHT, shadow = true)
        )
    )
    val slots = size.mkPick(
        listOf(
            mkText(PostSlots.EYEBROW, .07f, .15f, .70f, .035f, .024f, PostFont.BOLD, PostTone.SECONDARY, caps = true),
            mkText(PostSlots.HEADLINE, .07f, .20f, .86f, .33f, .094f, PostFont.DISPLAY, PostTone.LIGHT),
            mkText(PostSlots.BODY, .07f, .56f, .80f, .11f, .034f, PostFont.REGULAR, PostTone.LIGHT_MUTED),
            mkLine(size, PostSlots.BUTTON, .10f, .80f, .78f, .040f, PostFont.DISPLAY, PostTone.DARK, caps = true)
        ),
        listOf(
            mkText(PostSlots.EYEBROW, .06f, .15f, .50f, .04f, .022f, PostFont.BOLD, PostTone.SECONDARY, caps = true),
            mkText(PostSlots.HEADLINE, .06f, .20f, .60f, .39f, .084f, PostFont.DISPLAY, PostTone.LIGHT),
            mkText(PostSlots.BODY, .06f, .61f, .50f, .11f, .032f, PostFont.REGULAR, PostTone.LIGHT_MUTED),
            mkLine(size, PostSlots.BUTTON, .08f, .42f, .80f, .036f, PostFont.DISPLAY, PostTone.DARK, caps = true)
        ),
        listOf(
            mkText(PostSlots.EYEBROW, .08f, .17f, .84f, .03f, .026f, PostFont.BOLD, PostTone.SECONDARY, caps = true),
            mkText(PostSlots.HEADLINE, .08f, .22f, .84f, .32f, .108f, PostFont.DISPLAY, PostTone.LIGHT),
            mkText(PostSlots.BODY, .08f, .57f, .84f, .12f, .038f, PostFont.REGULAR, PostTone.LIGHT_MUTED),
            mkLine(size, PostSlots.BUTTON, .10f, .80f, .78f, .042f, PostFont.DISPLAY, PostTone.DARK, caps = true)
        )
    )
    return TemplateLayout(PostBase.PRIMARY, decor, slots, backdrop = Backdrop(PostTone.ACCENT, PostTone.DEEP, 140f))
}

private fun mkTicketLayout(size: PostSize): TemplateLayout {
    val decor = size.mkPick(
        listOf(
            mkBlock(.07f, .15f, .86f, .47f, .035f, PostTone.ACCENT, to = PostTone.DEEP, shadow = true),
            mkRule(size, .12f, .633f, .76f, PostTone.ACCENT, 4f),
            mkBlock(.07f, .65f, .86f, .21f, .035f, PostTone.LIGHT, shadow = true)
        ),
        listOf(
            mkBlock(.06f, .15f, .58f, .70f, .035f, PostTone.ACCENT, to = PostTone.DEEP, shadow = true),
            mkVRule(size, .6486f, .20f, .60f, PostTone.ACCENT, 4f),
            mkBlock(.66f, .15f, .28f, .70f, .035f, PostTone.LIGHT, shadow = true)
        ),
        listOf(
            mkBlock(.07f, .17f, .86f, .43f, .035f, PostTone.ACCENT, to = PostTone.DEEP, shadow = true),
            mkRule(size, .12f, .613f, .76f, PostTone.ACCENT, 4f),
            mkBlock(.07f, .63f, .86f, .20f, .035f, PostTone.LIGHT, shadow = true)
        )
    )
    val slots = size.mkPick(
        listOf(
            mkText(PostSlots.EYEBROW, .12f, .19f, .76f, .035f, .024f, PostFont.BOLD, PostTone.SECONDARY, caps = true),
            mkText(PostSlots.HEADLINE, .12f, .25f, .76f, .34f, .068f, PostFont.DISPLAY, PostTone.LIGHT),
            mkText(PostSlots.NUMBER, .12f, .665f, .76f, .09f, .070f, PostFont.DISPLAY, PostTone.ACCENT),
            mkText(PostSlots.BODY, .12f, .765f, .76f, .085f, .028f, PostFont.REGULAR, PostTone.MUTED)
        ),
        listOf(
            mkText(PostSlots.EYEBROW, .10f, .20f, .50f, .04f, .022f, PostFont.BOLD, PostTone.SECONDARY, caps = true),
            mkText(PostSlots.HEADLINE, .10f, .26f, .50f, .52f, .062f, PostFont.DISPLAY, PostTone.LIGHT),
            mkText(PostSlots.NUMBER, .68f, .30f, .24f, .14f, .060f, PostFont.DISPLAY, PostTone.ACCENT),
            mkText(PostSlots.BODY, .68f, .48f, .24f, .28f, .026f, PostFont.REGULAR, PostTone.MUTED)
        ),
        listOf(
            mkText(PostSlots.EYEBROW, .12f, .21f, .76f, .03f, .026f, PostFont.BOLD, PostTone.SECONDARY, caps = true),
            mkText(PostSlots.HEADLINE, .12f, .26f, .76f, .32f, .074f, PostFont.DISPLAY, PostTone.LIGHT),
            mkText(PostSlots.NUMBER, .12f, .645f, .76f, .08f, .074f, PostFont.DISPLAY, PostTone.ACCENT),
            mkText(PostSlots.BODY, .12f, .735f, .76f, .08f, .030f, PostFont.REGULAR, PostTone.MUTED)
        )
    )
    return TemplateLayout(PostBase.LIGHT, decor, slots, backdrop = MK_LIGHT)
}

private fun mkPosterLayout(size: PostSize): TemplateLayout {
    val decor = size.mkPick(
        listOf(
            mkGlow(size, .92f, .86f, 440f, PostTone.ACCENT, PostTone.GLOW, 170),
            mkBlock(.07f, .15f, .40f, .22f, .04f, PostTone.LIGHT, shadow = true),
            mkRule(size, .07f, .745f, .12f, PostTone.SECONDARY, 8f)
        ),
        listOf(
            mkGlow(size, .90f, .80f, 440f, PostTone.ACCENT, PostTone.GLOW, 170),
            mkBlock(.06f, .17f, .30f, .28f, .04f, PostTone.LIGHT, shadow = true),
            mkRule(size, .42f, .70f, .10f, PostTone.SECONDARY, 8f)
        ),
        listOf(
            mkGlow(size, .90f, .88f, 460f, PostTone.ACCENT, PostTone.GLOW, 170),
            mkBlock(.08f, .17f, .50f, .15f, .04f, PostTone.LIGHT, shadow = true),
            mkRule(size, .08f, .71f, .12f, PostTone.SECONDARY, 8f)
        )
    )
    val slots = size.mkPick(
        listOf(
            mkLine(size, PostSlots.NUMBER, .07f, .40f, .26f, .078f, PostFont.DISPLAY, PostTone.ACCENT, caps = true),
            mkText(PostSlots.EYEBROW, .52f, .16f, .41f, .20f, .026f, PostFont.BOLD, PostTone.SECONDARY, caps = true),
            mkText(PostSlots.HEADLINE, .07f, .41f, .86f, .31f, .090f, PostFont.DISPLAY, PostTone.LIGHT),
            mkText(PostSlots.BODY, .07f, .77f, .86f, .11f, .034f, PostFont.REGULAR, PostTone.LIGHT_MUTED)
        ),
        listOf(
            mkLine(size, PostSlots.NUMBER, .06f, .30f, .31f, .062f, PostFont.DISPLAY, PostTone.ACCENT, caps = true),
            mkText(PostSlots.EYEBROW, .06f, .50f, .30f, .20f, .024f, PostFont.BOLD, PostTone.SECONDARY, caps = true),
            mkText(PostSlots.HEADLINE, .42f, .17f, .52f, .50f, .078f, PostFont.DISPLAY, PostTone.LIGHT),
            mkText(PostSlots.BODY, .42f, .73f, .52f, .13f, .032f, PostFont.REGULAR, PostTone.LIGHT_MUTED)
        ),
        listOf(
            mkLine(size, PostSlots.NUMBER, .08f, .50f, .245f, .074f, PostFont.DISPLAY, PostTone.ACCENT, caps = true),
            mkText(PostSlots.EYEBROW, .64f, .175f, .28f, .14f, .022f, PostFont.BOLD, PostTone.SECONDARY, caps = true),
            mkText(PostSlots.HEADLINE, .08f, .38f, .84f, .30f, .104f, PostFont.DISPLAY, PostTone.LIGHT),
            mkText(PostSlots.BODY, .08f, .74f, .84f, .12f, .038f, PostFont.REGULAR, PostTone.LIGHT_MUTED)
        )
    )
    return TemplateLayout(PostBase.DARK, decor, slots, backdrop = MK_DARK)
}

private fun mkNoticeLayout(size: PostSize): TemplateLayout {
    val decor = size.mkPick(
        listOf(
            mkPanel(0f, .205f, 1f, .36f, PostTone.TEXT, PostTone.DEEP),
            mkPanel(0f, .205f, .02f, .36f, PostTone.ACCENT),
            mkBlock(.07f, .15f, .30f, .055f, .02f, PostTone.ACCENT, shadow = true),
            mkBlock(.08f, .75f, .84f, .10f, .03f, PostTone.ACCENT, to = PostTone.DEEP, shadow = true)
        ),
        listOf(
            mkPanel(0f, .195f, 1f, .38f, PostTone.TEXT, PostTone.DEEP),
            mkPanel(0f, .195f, .015f, .38f, PostTone.ACCENT),
            mkBlock(.06f, .14f, .24f, .055f, .02f, PostTone.ACCENT, shadow = true),
            mkBlock(.06f, .76f, .42f, .09f, .03f, PostTone.ACCENT, to = PostTone.DEEP, shadow = true)
        ),
        listOf(
            mkPanel(0f, .215f, 1f, .34f, PostTone.TEXT, PostTone.DEEP),
            mkPanel(0f, .215f, .02f, .34f, PostTone.ACCENT),
            mkBlock(.08f, .17f, .34f, .035f, .015f, PostTone.ACCENT, shadow = true),
            mkBlock(.08f, .76f, .84f, .08f, .03f, PostTone.ACCENT, to = PostTone.DEEP, shadow = true)
        )
    )
    val slots = size.mkPick(
        listOf(
            mkLine(size, PostSlots.EYEBROW, .07f, .30f, .1775f, .022f, PostFont.BOLD, PostTone.LIGHT, caps = true),
            mkText(PostSlots.HEADLINE, .09f, .25f, .82f, .29f, .070f, PostFont.DISPLAY, PostTone.LIGHT),
            mkText(PostSlots.BODY, .08f, .61f, .84f, .12f, .032f, PostFont.REGULAR, PostTone.TEXT),
            mkLine(size, PostSlots.BUTTON, .10f, .80f, .80f, .030f, PostFont.BOLD, PostTone.LIGHT, caps = true)
        ),
        listOf(
            mkLine(size, PostSlots.EYEBROW, .06f, .24f, .1675f, .022f, PostFont.BOLD, PostTone.LIGHT, caps = true),
            mkText(PostSlots.HEADLINE, .08f, .24f, .84f, .30f, .062f, PostFont.DISPLAY, PostTone.LIGHT),
            mkText(PostSlots.BODY, .06f, .62f, .60f, .12f, .030f, PostFont.REGULAR, PostTone.TEXT),
            mkLine(size, PostSlots.BUTTON, .08f, .38f, .805f, .028f, PostFont.BOLD, PostTone.LIGHT, caps = true)
        ),
        listOf(
            mkLine(size, PostSlots.EYEBROW, .08f, .34f, .1875f, .022f, PostFont.BOLD, PostTone.LIGHT, caps = true),
            mkText(PostSlots.HEADLINE, .08f, .255f, .84f, .27f, .082f, PostFont.DISPLAY, PostTone.LIGHT),
            mkText(PostSlots.BODY, .08f, .60f, .84f, .13f, .036f, PostFont.REGULAR, PostTone.TEXT),
            mkLine(size, PostSlots.BUTTON, .10f, .80f, .80f, .032f, PostFont.BOLD, PostTone.LIGHT, caps = true)
        )
    )
    return TemplateLayout(PostBase.LIGHT, decor, slots, backdrop = MK_LIGHT)
}

private fun mkMilestoneLayout(size: PostSize): TemplateLayout {
    val decor = size.mkPick(
        listOf(
            mkGlow(size, .50f, .40f, 470f, PostTone.ACCENT, PostTone.GLOW, 170),
            mkRule(size, .10f, .168f, .22f, PostTone.SECONDARY, 4f),
            mkRule(size, .68f, .168f, .22f, PostTone.SECONDARY, 4f),
            mkRule(size, .44f, .585f, .12f, PostTone.SECONDARY, 8f)
        ),
        listOf(
            mkGlow(size, .50f, .42f, 470f, PostTone.ACCENT, PostTone.GLOW, 170),
            mkRule(size, .12f, .168f, .26f, PostTone.SECONDARY, 4f),
            mkRule(size, .62f, .168f, .26f, PostTone.SECONDARY, 4f),
            mkRule(size, .45f, .585f, .10f, PostTone.SECONDARY, 8f)
        ),
        listOf(
            mkGlow(size, .50f, .34f, 470f, PostTone.ACCENT, PostTone.GLOW, 170),
            mkRule(size, .10f, .178f, .22f, PostTone.SECONDARY, 4f),
            mkRule(size, .68f, .178f, .22f, PostTone.SECONDARY, 4f),
            mkRule(size, .44f, .47f, .12f, PostTone.SECONDARY, 8f)
        )
    )
    val slots = size.mkPick(
        listOf(
            mkLine(size, PostSlots.EYEBROW, .32f, .36f, .17f, .024f, PostFont.BOLD, PostTone.LIGHT, caps = true),
            mkText(PostSlots.NUMBER, .06f, .22f, .88f, .34f, .260f, PostFont.DISPLAY, PostTone.SECONDARY, centered = true),
            mkText(PostSlots.HEADLINE, .08f, .62f, .84f, .14f, .062f, PostFont.DISPLAY, PostTone.LIGHT, centered = true),
            mkText(PostSlots.BODY, .12f, .77f, .76f, .10f, .032f, PostFont.REGULAR, PostTone.LIGHT_MUTED, centered = true)
        ),
        listOf(
            mkLine(size, PostSlots.EYEBROW, .38f, .24f, .17f, .024f, PostFont.BOLD, PostTone.LIGHT, caps = true),
            mkText(PostSlots.NUMBER, .10f, .22f, .80f, .34f, .270f, PostFont.DISPLAY, PostTone.SECONDARY, centered = true),
            mkText(PostSlots.HEADLINE, .10f, .62f, .80f, .14f, .062f, PostFont.DISPLAY, PostTone.LIGHT, centered = true),
            mkText(PostSlots.BODY, .15f, .77f, .70f, .10f, .032f, PostFont.REGULAR, PostTone.LIGHT_MUTED, centered = true)
        ),
        listOf(
            mkLine(size, PostSlots.EYEBROW, .32f, .36f, .18f, .026f, PostFont.BOLD, PostTone.LIGHT, caps = true),
            mkText(PostSlots.NUMBER, .04f, .24f, .92f, .20f, .300f, PostFont.DISPLAY, PostTone.SECONDARY, centered = true),
            mkText(PostSlots.HEADLINE, .08f, .52f, .84f, .14f, .078f, PostFont.DISPLAY, PostTone.LIGHT, centered = true),
            mkText(PostSlots.BODY, .10f, .68f, .80f, .14f, .038f, PostFont.REGULAR, PostTone.LIGHT_MUTED, centered = true)
        )
    )
    return TemplateLayout(PostBase.DARK, decor, slots, backdrop = MK_DARK)
}

private fun mkFollowLayout(size: PostSize): TemplateLayout {
    val decor = size.mkPick(
        listOf(mkBlock(.06f, .14f, .88f, .33f, .04f, PostTone.ACCENT, to = PostTone.DEEP, shadow = true)),
        listOf(mkBlock(.05f, .15f, .42f, .70f, .04f, PostTone.ACCENT, to = PostTone.DEEP, shadow = true)),
        listOf(mkBlock(.07f, .17f, .86f, .30f, .04f, PostTone.ACCENT, to = PostTone.DEEP, shadow = true))
    )
    val slots = size.mkPick(
        listOf(
            mkText(PostSlots.EYEBROW, .10f, .18f, .80f, .035f, .024f, PostFont.BOLD, PostTone.SECONDARY, caps = true),
            mkText(PostSlots.HEADLINE, .10f, .23f, .80f, .15f, .056f, PostFont.DISPLAY, PostTone.LIGHT),
            mkText(PostSlots.BODY, .10f, .385f, .80f, .07f, .028f, PostFont.REGULAR, PostTone.LIGHT_MUTED)
        ),
        listOf(
            mkText(PostSlots.EYEBROW, .09f, .20f, .34f, .04f, .022f, PostFont.BOLD, PostTone.SECONDARY, caps = true),
            mkText(PostSlots.HEADLINE, .09f, .26f, .34f, .34f, .056f, PostFont.DISPLAY, PostTone.LIGHT),
            mkText(PostSlots.BODY, .09f, .62f, .34f, .18f, .030f, PostFont.REGULAR, PostTone.LIGHT_MUTED)
        ),
        listOf(
            mkText(PostSlots.EYEBROW, .11f, .21f, .78f, .03f, .026f, PostFont.BOLD, PostTone.SECONDARY, caps = true),
            mkText(PostSlots.HEADLINE, .11f, .26f, .78f, .12f, .060f, PostFont.DISPLAY, PostTone.LIGHT),
            mkText(PostSlots.BODY, .11f, .39f, .78f, .06f, .030f, PostFont.REGULAR, PostTone.LIGHT_MUTED)
        )
    )
    val social = size.mkPick(
        SocialArea(.06f, .52f, .88f, .34f, SocialStyle.GRID, false),
        SocialArea(.52f, .15f, .43f, .70f, SocialStyle.GRID, false),
        SocialArea(.07f, .52f, .86f, .33f, SocialStyle.GRID, false)
    )
    return TemplateLayout(PostBase.LIGHT, decor, slots, backdrop = MK_LIGHT, social = social)
}

private fun mkAuroraLayout(size: PostSize): TemplateLayout {
    val decor = size.mkPick(
        listOf(
            mkGlow(size, .80f, .16f, 560f, PostTone.ACCENT, PostTone.GLOW, 230),
            mkGlow(size, .10f, .90f, 440f, PostTone.GLOW, PostTone.DEEP, 160),
            mkRule(size, .08f, .775f, .12f, PostTone.SECONDARY, 8f)
        ),
        listOf(
            mkGlow(size, .88f, .20f, 540f, PostTone.ACCENT, PostTone.GLOW, 230),
            mkGlow(size, .10f, .90f, 420f, PostTone.GLOW, PostTone.DEEP, 160),
            mkRule(size, .06f, .775f, .10f, PostTone.SECONDARY, 8f)
        ),
        listOf(
            mkGlow(size, .85f, .20f, 600f, PostTone.ACCENT, PostTone.GLOW, 230),
            mkGlow(size, .10f, .85f, 500f, PostTone.GLOW, PostTone.DEEP, 160),
            mkRule(size, .08f, .76f, .12f, PostTone.SECONDARY, 8f)
        )
    )
    val slots = size.mkPick(
        listOf(
            mkText(PostSlots.EYEBROW, .08f, .15f, .70f, .035f, .024f, PostFont.BOLD, PostTone.SECONDARY, caps = true),
            mkText(PostSlots.HEADLINE, .08f, .21f, .84f, .55f, .098f, PostFont.DISPLAY, PostTone.LIGHT),
            mkText(PostSlots.BODY, .08f, .80f, .84f, .08f, .030f, PostFont.REGULAR, PostTone.LIGHT_MUTED)
        ),
        listOf(
            mkText(PostSlots.EYEBROW, .06f, .15f, .60f, .04f, .022f, PostFont.BOLD, PostTone.SECONDARY, caps = true),
            mkText(PostSlots.HEADLINE, .06f, .21f, .74f, .54f, .094f, PostFont.DISPLAY, PostTone.LIGHT),
            mkText(PostSlots.BODY, .06f, .80f, .70f, .08f, .030f, PostFont.REGULAR, PostTone.LIGHT_MUTED)
        ),
        listOf(
            mkText(PostSlots.EYEBROW, .08f, .17f, .70f, .03f, .026f, PostFont.BOLD, PostTone.SECONDARY, caps = true),
            mkText(PostSlots.HEADLINE, .08f, .22f, .84f, .50f, .116f, PostFont.DISPLAY, PostTone.LIGHT),
            mkText(PostSlots.BODY, .08f, .78f, .84f, .09f, .036f, PostFont.REGULAR, PostTone.LIGHT_MUTED)
        )
    )
    return TemplateLayout(PostBase.DARK, decor, slots, backdrop = Backdrop(PostTone.DEEP, PostTone.PAGE, 130f))
}

private fun mkInkLayout(size: PostSize): TemplateLayout {
    val decor = size.mkPick(
        listOf(
            mkPanel(0f, .12f, .04f, .76f, PostTone.ACCENT, PostTone.DEEP, 90f),
            mkRule(size, .09f, .72f, .12f, PostTone.ACCENT, 10f)
        ),
        listOf(
            mkPanel(0f, .12f, .03f, .76f, PostTone.ACCENT, PostTone.DEEP, 90f),
            mkRule(size, .10f, .72f, .10f, PostTone.ACCENT, 10f)
        ),
        listOf(
            mkPanel(0f, .15f, .04f, .70f, PostTone.ACCENT, PostTone.DEEP, 90f),
            mkRule(size, .09f, .64f, .12f, PostTone.ACCENT, 10f)
        )
    )
    val slots = size.mkPick(
        listOf(
            mkText(PostSlots.EYEBROW, .09f, .14f, .80f, .035f, .024f, PostFont.BOLD, PostTone.ACCENT, caps = true),
            mkText(PostSlots.HEADLINE, .09f, .19f, .84f, .51f, .150f, PostFont.DISPLAY, PostTone.TEXT, caps = true),
            mkText(PostSlots.BODY, .09f, .745f, .80f, .12f, .032f, PostFont.REGULAR, PostTone.MUTED)
        ),
        listOf(
            mkText(PostSlots.EYEBROW, .10f, .14f, .80f, .035f, .022f, PostFont.BOLD, PostTone.ACCENT, caps = true),
            mkText(PostSlots.HEADLINE, .10f, .19f, .80f, .51f, .150f, PostFont.DISPLAY, PostTone.TEXT, caps = true),
            mkText(PostSlots.BODY, .10f, .745f, .76f, .12f, .032f, PostFont.REGULAR, PostTone.MUTED)
        ),
        listOf(
            mkText(PostSlots.EYEBROW, .09f, .17f, .80f, .03f, .026f, PostFont.BOLD, PostTone.ACCENT, caps = true),
            mkText(PostSlots.HEADLINE, .09f, .215f, .84f, .38f, .170f, PostFont.DISPLAY, PostTone.TEXT, caps = true),
            mkText(PostSlots.BODY, .09f, .665f, .82f, .15f, .036f, PostFont.REGULAR, PostTone.MUTED)
        )
    )
    return TemplateLayout(PostBase.LIGHT, decor, slots, backdrop = MK_LIGHT)
}

private fun mkSplitLayout(size: PostSize): TemplateLayout {
    val decor = size.mkPick(
        listOf(
            mkPanel(0f, 0f, .52f, .88f, PostTone.ACCENT, PostTone.DEEP, 135f),
            mkRule(size, .07f, .76f, .12f, PostTone.SECONDARY, 8f),
            mkRule(size, .58f, .235f, .10f, PostTone.ACCENT, 8f)
        ),
        listOf(
            mkPanel(0f, 0f, .50f, .88f, PostTone.ACCENT, PostTone.DEEP, 135f),
            mkRule(size, .06f, .76f, .10f, PostTone.SECONDARY, 8f),
            mkRule(size, .56f, .235f, .08f, PostTone.ACCENT, 8f)
        ),
        listOf(
            mkPanel(0f, 0f, 1f, .52f, PostTone.ACCENT, PostTone.DEEP, 135f),
            mkRule(size, .08f, .60f, .12f, PostTone.ACCENT, 8f)
        )
    )
    val slots = size.mkPick(
        listOf(
            mkText(PostSlots.HEADLINE, .07f, .19f, .40f, .55f, .074f, PostFont.DISPLAY, PostTone.LIGHT),
            mkText(PostSlots.EYEBROW, .58f, .17f, .34f, .04f, .022f, PostFont.BOLD, PostTone.ACCENT, caps = true),
            mkText(PostSlots.BODY, .58f, .27f, .34f, .50f, .032f, PostFont.REGULAR, PostTone.TEXT)
        ),
        listOf(
            mkText(PostSlots.HEADLINE, .06f, .19f, .38f, .55f, .070f, PostFont.DISPLAY, PostTone.LIGHT),
            mkText(PostSlots.EYEBROW, .56f, .17f, .36f, .04f, .022f, PostFont.BOLD, PostTone.ACCENT, caps = true),
            mkText(PostSlots.BODY, .56f, .27f, .36f, .50f, .032f, PostFont.REGULAR, PostTone.TEXT)
        ),
        listOf(
            mkText(PostSlots.EYEBROW, .08f, .17f, .84f, .03f, .026f, PostFont.BOLD, PostTone.SECONDARY, caps = true),
            mkText(PostSlots.HEADLINE, .08f, .22f, .84f, .28f, .090f, PostFont.DISPLAY, PostTone.LIGHT),
            mkText(PostSlots.BODY, .08f, .63f, .84f, .22f, .040f, PostFont.REGULAR, PostTone.TEXT)
        )
    )
    return TemplateLayout(PostBase.LIGHT, decor, slots, backdrop = MK_LIGHT)
}
