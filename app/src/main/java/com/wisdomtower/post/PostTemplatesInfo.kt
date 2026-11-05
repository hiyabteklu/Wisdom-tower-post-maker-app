package com.wisdomtower.post

/**
 * Phase 3, batch 2 of REDESIGN_PLAN.md: the eight information templates.
 *
 * Editorial quote, Numbered tip, Quiz (four options), Question and answer, Checklist, Weekly timetable, Definition, Big stat.
 * Each has its own structure (side tab, number beside a gradient card, header block with a floating options card, two stacked panels,
 * badge with a checklist card, dark timetable card, split block, giant number), not a recolor.
 *
 * Design quality pass (5 Nov 2026): richer depth on Definition, Numbered tip, Editorial quote and Big stat
 * (soft accent glows, stronger hierarchy, mesh/grain on key templates). Same clean rules: no DIAMOND/TRIANGLE/HEXAGON,
 * own placement for 1:1 / 4:3 / 9:16, content between 12% and 88% of height (16% start on 9:16).
 *
 * InfoTemplates at the bottom also carries the batch 3 marketing and type templates (PostTemplatesMarketing.kt).
 */

private const val INU = 1080f

private val IN_BACKDROP = Backdrop(PostTone.TINT, PostTone.SOFT, 135f)

/** Picks the value for the current size. Any size that is not 4:3 or 9:16 gets the 1:1 value. */
private fun <T> PostSize.pick(square: T, wide: T, story: T): T = when (this) {
    PostSize.STORY -> story
    PostSize.LANDSCAPE, PostSize.WIDE -> wide
    else -> square
}

private fun infoTemplate(
    id: String,
    name: String,
    samples: Map<String, String>,
    look: TemplateLook? = null,
    build: (PostSize) -> TemplateLayout
) = PostTemplateSpec(id, name, "Information", samples, look) { size -> build(size).copy(clean = true) }

private fun inText(
    slot: String, x: Float, y: Float, w: Float, h: Float, size: Float, font: PostFont, tone: PostTone,
    centered: Boolean = false, caps: Boolean = false
) = SlotBox(slot, x, y, w, h, size, font, tone, centered, caps)

private fun inList(
    slot: String, x: Float, y: Float, w: Float, h: Float, size: Float, font: PostFont, tone: PostTone, style: ListStyle
) = SlotBox(slot, x, y, w, h, size, font, tone, false, false, style)

/** Fixed text the person never edits (a quote mark, the word ANSWER). */
private fun inMark(
    text: String, x: Float, y: Float, w: Float, h: Float, size: Float, font: PostFont, tone: PostTone, centered: Boolean = false
) = SlotBox("", x, y, w, h, size, font, tone, centered, false, ListStyle.NONE, text)

/** A rounded block. radius is a fraction of the shorter side. to turns the fill into a gradient. */
private fun inBlock(
    x: Float, y: Float, w: Float, h: Float, radius: Float, tone: PostTone,
    to: PostTone? = null, angle: Float = 135f, shadow: Boolean = false
) = DecorBox(DecorShape.ROUND, x, y, w, h, tone, 255, radius, false, to, -1, angle, 0f, shadow)

/** A square-cornered panel, flat or a gradient. */
private fun inPanel(x: Float, y: Float, w: Float, h: Float, tone: PostTone, to: PostTone? = null, angle: Float = 135f) =
    DecorBox(DecorShape.RECT, x, y, w, h, tone, 255, 0f, false, to, -1, angle)

private fun inBar(x: Float, y: Float, w: Float, h: Float, tone: PostTone) = DecorBox(DecorShape.RECT, x, y, w, h, tone)

/** A thin horizontal rule. w is a fraction of the width, thickPx is in pixels of the 1080 base. */
private fun inRule(size: PostSize, x: Float, y: Float, w: Float, tone: PostTone, thickPx: Float = 6f) =
    DecorBox(DecorShape.RECT, x, y, w, thickPx / size.height, tone)

/** A flat circle centered on canvas fractions; the radius is in pixels of the 1080 base. */
private fun inDisc(size: PostSize, cx: Float, cy: Float, rPx: Float, tone: PostTone) = DecorBox(
    DecorShape.OVAL, cx - rPx / size.width, cy - rPx / size.height, 2f * rPx / size.width, 2f * rPx / size.height, tone
)

/** Soft radial glow (fades to transparent). Radius in pixels of the 1080 base. */
private fun inGlow(size: PostSize, cx: Float, cy: Float, rPx: Float, tone: PostTone, alpha: Int = 160) = DecorBox(
    DecorShape.OVAL,
    cx - rPx / size.width, cy - rPx / size.height,
    2f * rPx / size.width, 2f * rPx / size.height,
    tone, alpha = alpha, gradTo = tone, alphaTo = 0, angle = 45f
)

/** The top of a one-line box so that its text sits at the vertical centre cy (a canvas fraction). fontPx is the text size in pixels. */
private fun inTopFor(size: PostSize, cy: Float, fontPx: Float): Float = cy - 0.62f * fontPx / size.height

/** A disc with one letter or mark in the middle (Q, A, a tick). Returns the disc and the fixed text to draw over it. */
private fun inBadge(
    size: PostSize, cx: Float, cy: Float, rPx: Float, letter: String, disc: PostTone, ink: PostTone,
    font: PostFont = PostFont.DISPLAY
): Pair<DecorBox, SlotBox> {
    val fontPx = rPx * 1.05f
    val boxW = 2f * rPx / size.width
    val boxH = fontPx * 1.5f / size.height
    return inDisc(size, cx, cy, rPx, disc) to
        inMark(letter, cx - boxW / 2f, inTopFor(size, cy, fontPx), boxW, boxH, fontPx / INU, font, ink, centered = true)
}

private val InfoOnlyTemplates: List<PostTemplateSpec> = listOf(

    // 8. Editorial quote — soft glow behind the tab, stronger presence
    infoTemplate(
        "info-quote", "Editorial quote",
        mapOf(
            PostSlots.EYEBROW to "TODAY'S THOUGHT",
            PostSlots.HEADLINE to "Small steps still move you forward.",
            PostSlots.BODY to "Progress is built by showing up consistently."
        ),
        TemplateLook(mesh = MeshStyle.AURORA, grain = true),
        ::quoteLayout
    ),

    // 9. Numbered tip — glow behind the giant number, tighter card
    infoTemplate(
        "info-tip", "Numbered tip",
        mapOf(
            PostSlots.EYEBROW to "STUDY TIP",
            PostSlots.NUMBER to "01",
            PostSlots.HEADLINE to "Test yourself instead of rereading.",
            PostSlots.BODY to "Close the book and write down what you remember. Then check what you missed."
        ),
        TemplateLook(mesh = MeshStyle.SPOTLIGHT, grain = true),
        ::tipLayout
    ),

    // 10. Quiz, four options
    infoTemplate(
        "info-quiz", "Quiz, four options",
        mapOf(
            PostSlots.EYEBROW to "QUESTION 1",
            PostSlots.HEADLINE to "Which organelle is called the powerhouse of the cell?",
            PostSlots.LIST to "Nucleus\nMitochondrion\nRibosome\nGolgi apparatus"
        ),
        ::quizLayout
    ),

    // 11. Question and answer
    infoTemplate(
        "info-qa", "Question and answer",
        mapOf(
            PostSlots.EYEBROW to "QUICK QUESTION",
            PostSlots.HEADLINE to "What is the derivative of sin(x)?",
            PostSlots.BODY to "cos(x)"
        ),
        ::qaLayout
    ),

    // 12. Checklist
    infoTemplate(
        "info-checklist", "Checklist",
        mapOf(
            PostSlots.EYEBROW to "BEFORE THE EXAM",
            PostSlots.HEADLINE to "Exam day checklist",
            PostSlots.LIST to "ID card and pens\nCalculator checked\nArrive 30 minutes early\nSleep well the night before\nStay calm and read every question"
        ),
        ::checklistLayout
    ),

    // 13. Weekly timetable
    infoTemplate(
        "info-timetable", "Weekly timetable",
        mapOf(
            PostSlots.EYEBROW to "STUDY PLAN",
            PostSlots.HEADLINE to "This week's schedule",
            PostSlots.LIST to "Mon: Mathematics\nTue: Physics\nWed: Chemistry\nThu: English\nFri: Revision\nSat: Mock test"
        ),
        ::timetableLayout
    ),

    // 14. Definition — dramatic term block with soft inner glow, cleaner message area
    infoTemplate(
        "info-definition", "Definition",
        mapOf(
            PostSlots.EYEBROW to "DEFINITION",
            PostSlots.HEADLINE to "Homeostasis",
            PostSlots.BODY to "The way a living body keeps its inner conditions steady, such as temperature and water balance, while the outside keeps changing."
        ),
        TemplateLook(mesh = MeshStyle.DUSK, grain = true),
        ::definitionLayout
    ),

    // 15. Big stat — soft glow behind the giant number
    infoTemplate(
        "info-stat", "Big stat",
        mapOf(
            PostSlots.EYEBROW to "DID YOU KNOW",
            PostSlots.NUMBER to "72%",
            PostSlots.HEADLINE to "of what you read is forgotten in 3 days",
            PostSlots.BODY to "Review the same page again tomorrow and the loss drops sharply."
        ),
        TemplateLook(mesh = MeshStyle.SPOTLIGHT, grain = true),
        ::statLayout
    )
)

/** The eight information templates (batch 2) followed by the ten marketing and type templates (batch 3). */
internal val InfoTemplates: List<PostTemplateSpec> = InfoOnlyTemplates + MarketingTemplates

private fun quoteLayout(size: PostSize): TemplateLayout {
    val glow = size.pick(
        inGlow(size, .14f, .50f, 280f, PostTone.ACCENT, 110),
        inGlow(size, .12f, .50f, 260f, PostTone.ACCENT, 110),
        inGlow(size, .21f, .22f, 220f, PostTone.ACCENT, 100)
    )
    val decor = size.pick(
        listOf(
            glow,
            inBlock(.07f, .16f, .14f, .70f, .05f, PostTone.ACCENT, to = PostTone.DEEP),
            inRule(size, .29f, .715f, .10f, PostTone.ACCENT, 8f)
        ),
        listOf(
            glow,
            inBlock(.06f, .16f, .11f, .70f, .05f, PostTone.ACCENT, to = PostTone.DEEP),
            inRule(size, .24f, .715f, .07f, PostTone.ACCENT, 8f)
        ),
        listOf(
            glow,
            inBlock(.08f, .16f, .26f, .11f, .035f, PostTone.ACCENT, to = PostTone.DEEP),
            inRule(size, .08f, .705f, .12f, PostTone.ACCENT, 8f)
        )
    )
    val slots = size.pick(
        listOf(
            inMark("\u201C", .07f, .17f, .14f, .30f, .20f, PostFont.DISPLAY, PostTone.SECONDARY, centered = true),
            inText(PostSlots.EYEBROW, .29f, .17f, .58f, .04f, .024f, PostFont.BOLD, PostTone.ACCENT, caps = true),
            inText(PostSlots.HEADLINE, .29f, .23f, .62f, .45f, .068f, PostFont.DISPLAY, PostTone.TEXT),
            inText(PostSlots.BODY, .29f, .74f, .62f, .12f, .032f, PostFont.REGULAR, PostTone.MUTED)
        ),
        listOf(
            inMark("\u201C", .06f, .17f, .11f, .30f, .20f, PostFont.DISPLAY, PostTone.SECONDARY, centered = true),
            inText(PostSlots.EYEBROW, .24f, .17f, .60f, .04f, .022f, PostFont.BOLD, PostTone.ACCENT, caps = true),
            inText(PostSlots.HEADLINE, .24f, .23f, .64f, .45f, .068f, PostFont.DISPLAY, PostTone.TEXT),
            inText(PostSlots.BODY, .24f, .74f, .64f, .12f, .032f, PostFont.REGULAR, PostTone.MUTED)
        ),
        listOf(
            inMark("\u201C", .08f, .175f, .26f, .134f, .17f, PostFont.DISPLAY, PostTone.SECONDARY, centered = true),
            inText(PostSlots.EYEBROW, .08f, .31f, .84f, .025f, .026f, PostFont.BOLD, PostTone.ACCENT, caps = true),
            inText(PostSlots.HEADLINE, .08f, .35f, .84f, .32f, .090f, PostFont.DISPLAY, PostTone.TEXT),
            inText(PostSlots.BODY, .08f, .73f, .84f, .12f, .038f, PostFont.REGULAR, PostTone.MUTED)
        )
    )
    return TemplateLayout(PostBase.LIGHT, decor, slots, backdrop = IN_BACKDROP)
}

/**
 * Numbered tip. Glow behind the giant number + tighter, more premium card.
 */
private fun tipLayout(size: PostSize): TemplateLayout {
    val numGlow = size.pick(
        inGlow(size, .22f, .28f, 320f, PostTone.ACCENT, 140),
        inGlow(size, .20f, .40f, 340f, PostTone.ACCENT, 130),
        inGlow(size, .24f, .26f, 280f, PostTone.ACCENT, 130)
    )
    val decor = size.pick(
        listOf(
            numGlow,
            inBlock(.07f, .47f, .86f, .37f, .045f, PostTone.ACCENT, to = PostTone.DEEP, shadow = true),
            inRule(size, .50f, .285f, .10f, PostTone.ACCENT, 8f)
        ),
        listOf(
            numGlow,
            inBlock(.40f, .16f, .54f, .70f, .045f, PostTone.ACCENT, to = PostTone.DEEP, shadow = true),
            inRule(size, .06f, .195f, .08f, PostTone.ACCENT, 8f)
        ),
        listOf(
            numGlow,
            inBlock(.07f, .42f, .86f, .38f, .045f, PostTone.ACCENT, to = PostTone.DEEP, shadow = true),
            inRule(size, .46f, .272f, .12f, PostTone.ACCENT, 8f)
        )
    )
    val slots = size.pick(
        listOf(
            inText(PostSlots.EYEBROW, .50f, .225f, .43f, .04f, .028f, PostFont.BOLD, PostTone.ACCENT, caps = true),
            inText(PostSlots.NUMBER, .06f, .13f, .40f, .33f, .24f, PostFont.DISPLAY, PostTone.ACCENT),
            inText(PostSlots.HEADLINE, .12f, .51f, .76f, .17f, .058f, PostFont.DISPLAY, PostTone.LIGHT),
            inText(PostSlots.BODY, .12f, .66f, .76f, .15f, .034f, PostFont.REGULAR, PostTone.LIGHT)
        ),
        listOf(
            inText(PostSlots.EYEBROW, .06f, .14f, .30f, .04f, .024f, PostFont.BOLD, PostTone.ACCENT, caps = true),
            inText(PostSlots.NUMBER, .06f, .21f, .31f, .50f, .32f, PostFont.DISPLAY, PostTone.ACCENT),
            inText(PostSlots.HEADLINE, .46f, .22f, .42f, .30f, .062f, PostFont.DISPLAY, PostTone.LIGHT),
            inText(PostSlots.BODY, .46f, .56f, .42f, .24f, .032f, PostFont.REGULAR, PostTone.LIGHT)
        ),
        listOf(
            inText(PostSlots.EYEBROW, .46f, .225f, .47f, .035f, .030f, PostFont.BOLD, PostTone.ACCENT, caps = true),
            inText(PostSlots.NUMBER, .07f, .17f, .38f, .19f, .22f, PostFont.DISPLAY, PostTone.ACCENT),
            inText(PostSlots.HEADLINE, .12f, .46f, .76f, .19f, .068f, PostFont.DISPLAY, PostTone.LIGHT),
            inText(PostSlots.BODY, .12f, .63f, .76f, .15f, .040f, PostFont.REGULAR, PostTone.LIGHT)
        )
    )
    return TemplateLayout(PostBase.LIGHT, decor, slots, backdrop = IN_BACKDROP)
}

private fun quizLayout(size: PostSize): TemplateLayout {
    val decor = size.pick(
        listOf(
            inBlock(.06f, .14f, .88f, .34f, .04f, PostTone.ACCENT, to = PostTone.DEEP),
            inBlock(.10f, .43f, .80f, .43f, .035f, PostTone.LIGHT, shadow = true)
        ),
        listOf(
            inBlock(.05f, .16f, .42f, .68f, .04f, PostTone.ACCENT, to = PostTone.DEEP),
            inBlock(.40f, .22f, .54f, .56f, .035f, PostTone.LIGHT.LIGHT, shadow = true)
        ),
        listOf(
            inBlock(.07f, .16f, .86f, .30f, .04f, PostTone.ACCENT, to = PostTone.DEEP),
            inBlock(.10f, .43f, .80f, .42f, .035f, PostTone.LIGHT, shadow = true)
        )
    )
    val slots = size.pick(
        listOf(
            inText(PostSlots.EYEBROW, .11f, .175f, .70f, .035f, .024f, PostFont.BOLD, PostTone.SECONDARY, caps = true),
            inText(PostSlots.HEADLINE, .11f, .225f, .78f, .20f, .054f, PostFont.DISPLAY, PostTone.LIGHT),
            inList(PostSlots.LIST, .15f, .46f, .70f, .37f, .046f, PostFont.BOLD, PostTone.TEXT, ListStyle.LETTER)
        ),
        listOf(
            inText(PostSlots.EYEBROW, .09f, .20f, .28f, .04f, .022f, PostFont.BOLD, PostTone.SECONDARY, caps = true),
            inText(PostSlots.HEADLINE, .09f, .26f, .28f, .52f, .050f, PostFont.DISPLAY, PostTone.LIGHT),
            inList(PostSlots.LIST, .50f, .27f, .40f, .46f, .046f, PostFont.BOLD, PostTone.TEXT, ListStyle.LETTER)
        ),
        listOf(
            inText(PostSlots.EYEBROW, .12f, .19f, .70f, .025f, .026f, PostFont.BOLD, PostTone.SECONDARY, caps = true),
            inText(PostSlots.HEADLINE, .12f, .225f, .76f, .19f, .062f, PostFont.DISPLAY, PostTone.LIGHT),
            inList(PostSlots.LIST, .16f, .47f, .68f, .35f, .050f, PostFont.BOLD, PostTone.TEXT, ListStyle.LETTER)
        )
    )
    return TemplateLayout(PostBase.LIGHT, decor, slots, backdrop = IN_BACKDROP)
}
