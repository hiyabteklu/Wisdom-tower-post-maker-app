package com.wisdomtower.post

/**
 * Phase 3, batch 2 of REDESIGN_PLAN.md: the eight information templates.
 *
 * Editorial quote, Numbered tip, Quiz (four options), Question and answer, Checklist, Weekly timetable, Definition, Big stat.
 * Each has its own structure (side tab, number beside a gradient card, header block with a floating options card, two stacked panels,
 * badge with a checklist card, dark timetable card, split block, giant number), not a recolor.
 *
 * Same rules as the photo templates: every layout is marked clean (PostRich adds nothing, PostLooks is skipped), nothing uses
 * DIAMOND, TRIANGLE or HEXAGON because the renderer draws nothing for those, and every layout has its own placement for 1:1, 4:3 and 9:16.
 * Any other size (4:5 later) falls into the 1:1 branch until it gets its own. Text sits between 12% and 88% of the height;
 * on 9:16 it starts at 16% so the lowered logo box stays clear.
 *
 * InfoTemplates at the bottom of the list also carries the batch 3 marketing and type templates (PostTemplatesMarketing.kt),
 * so PostTemplates.kt did not need to change. Phase 4 will tidy this.
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
    build: (PostSize) -> TemplateLayout
) = PostTemplateSpec(id, name, "Information", samples) { size -> build(size).copy(clean = true) }

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

    // 8. Editorial quote: a tall brand tab with a giant quote mark, the quote in a wide column beside it (a short tab on top on 9:16)
    infoTemplate(
        "info-quote", "Editorial quote",
        mapOf(
            PostSlots.EYEBROW to "TODAY'S THOUGHT",
            PostSlots.HEADLINE to "Small steps still move you forward.",
            PostSlots.BODY to "Progress is built by showing up consistently."
        ),
        ::quoteLayout
    ),

    // 9. Numbered tip: a giant number on the page, the tip on a gradient card below (beside it on 4:3)
    infoTemplate(
        "info-tip", "Numbered tip",
        mapOf(
            PostSlots.EYEBROW to "STUDY TIP",
            PostSlots.NUMBER to "01",
            PostSlots.HEADLINE to "Test yourself instead of rereading.",
            PostSlots.BODY to "Close the book and write down what you remember. Then check what you missed."
        ),
        ::tipLayout
    ),

    // 10. Quiz, four options: the question on a gradient block, the lettered options on a card that overlaps its edge
    infoTemplate(
        "info-quiz", "Quiz, four options",
        mapOf(
            PostSlots.EYEBROW to "QUESTION 1",
            PostSlots.HEADLINE to "Which organelle is called the powerhouse of the cell?",
            PostSlots.LIST to "Nucleus\nMitochondrion\nRibosome\nGolgi apparatus"
        ),
        ::quizLayout
    ),

    // 11. Question and answer: a Q badge and the question on the page, the answer on a dark card with an A badge
    infoTemplate(
        "info-qa", "Question and answer",
        mapOf(
            PostSlots.EYEBROW to "QUICK QUESTION",
            PostSlots.HEADLINE to "What is the derivative of sin(x)?",
            PostSlots.BODY to "cos(x)"
        ),
        ::qaLayout
    ),

    // 12. Checklist: a tick badge beside the title, the items on a floating card
    infoTemplate(
        "info-checklist", "Checklist",
        mapOf(
            PostSlots.EYEBROW to "BEFORE THE EXAM",
            PostSlots.HEADLINE to "Exam day checklist",
            PostSlots.LIST to "ID card and pens\nCalculator checked\nArrive 30 minutes early\nSleep well the night before\nStay calm and read every question"
        ),
        ::checklistLayout
    ),

    // 13. Weekly timetable: day names in one column, subjects in the other, on a dark card with rules between the rows
    infoTemplate(
        "info-timetable", "Weekly timetable",
        mapOf(
            PostSlots.EYEBROW to "STUDY PLAN",
            PostSlots.HEADLINE to "This week's schedule",
            PostSlots.LIST to "Mon: Mathematics\nTue: Physics\nWed: Chemistry\nThu: English\nFri: Revision\nSat: Mock test"
        ),
        ::timetableLayout
    ),

    // 14. Definition: the term on a full-width gradient block, the meaning on the page below
    infoTemplate(
        "info-definition", "Definition",
        mapOf(
            PostSlots.EYEBROW to "DEFINITION",
            PostSlots.HEADLINE to "Homeostasis",
            PostSlots.BODY to "The way a living body keeps its inner conditions steady, such as temperature and water balance, while the outside keeps changing."
        ),
        ::definitionLayout
    ),

    // 15. Big stat: one giant number, a thick rule, the fact, and the note on a small card
    infoTemplate(
        "info-stat", "Big stat",
        mapOf(
            PostSlots.EYEBROW to "DID YOU KNOW",
            PostSlots.NUMBER to "72%",
            PostSlots.HEADLINE to "of what you read is forgotten in 3 days",
            PostSlots.BODY to "Review the same page again tomorrow and the loss drops sharply."
        ),
        ::statLayout
    )
)

/** The eight information templates (batch 2) followed by the ten marketing and type templates (batch 3). */
internal val InfoTemplates: List<PostTemplateSpec> = InfoOnlyTemplates + MarketingTemplates

private fun quoteLayout(size: PostSize): TemplateLayout {
    val decor = size.pick(
        listOf(
            inBlock(.07f, .16f, .14f, .70f, .05f, PostTone.ACCENT, to = PostTone.DEEP),
            inRule(size, .29f, .715f, .10f, PostTone.ACCENT)
        ),
        listOf(
            inBlock(.06f, .16f, .11f, .70f, .05f, PostTone.ACCENT, to = PostTone.DEEP),
            inRule(size, .24f, .715f, .07f, PostTone.ACCENT)
        ),
        listOf(
            inBlock(.08f, .16f, .26f, .11f, .035f, PostTone.ACCENT, to = PostTone.DEEP),
            inRule(size, .08f, .705f, .12f, PostTone.ACCENT)
        )
    )
    val slots = size.pick(
        listOf(
            inMark("\u201C", .07f, .17f, .14f, .30f, .20f, PostFont.DISPLAY, PostTone.SECONDARY, centered = true),
            inText(PostSlots.EYEBROW, .29f, .17f, .58f, .04f, .024f, PostFont.BOLD, PostTone.ACCENT, caps = true),
            inText(PostSlots.HEADLINE, .29f, .23f, .62f, .45f, .066f, PostFont.DISPLAY, PostTone.TEXT),
            inText(PostSlots.BODY, .29f, .74f, .62f, .12f, .030f, PostFont.REGULAR, PostTone.MUTED)
        ),
        listOf(
            inMark("\u201C", .06f, .17f, .11f, .30f, .20f, PostFont.DISPLAY, PostTone.SECONDARY, centered = true),
            inText(PostSlots.EYEBROW, .24f, .17f, .60f, .04f, .022f, PostFont.BOLD, PostTone.ACCENT, caps = true),
            inText(PostSlots.HEADLINE, .24f, .23f, .64f, .45f, .066f, PostFont.DISPLAY, PostTone.TEXT),
            inText(PostSlots.BODY, .24f, .74f, .64f, .12f, .030f, PostFont.REGULAR, PostTone.MUTED)
        ),
        listOf(
            inMark("\u201C", .08f, .175f, .26f, .134f, .17f, PostFont.DISPLAY, PostTone.SECONDARY, centered = true),
            inText(PostSlots.EYEBROW, .08f, .31f, .84f, .025f, .026f, PostFont.BOLD, PostTone.ACCENT, caps = true),
            inText(PostSlots.HEADLINE, .08f, .35f, .84f, .32f, .086f, PostFont.DISPLAY, PostTone.TEXT),
            inText(PostSlots.BODY, .08f, .73f, .84f, .12f, .036f, PostFont.REGULAR, PostTone.MUTED)
        )
    )
    return TemplateLayout(PostBase.LIGHT, decor, slots, backdrop = IN_BACKDROP)
}

/**
 * Numbered tip. Design fix D1 (2 Nov 2026, DESIGN_PLAN.md): the message was small and low contrast (LIGHT_MUTED) with a big gap
 * under the headline, the card was taller than its text, and the small label was tiny beside the giant number.
 * Now the message uses the same tone as the headline and a larger size, sits closer to the headline, the card is shorter,
 * and the small label is larger. 4:3 only changes the message tone (its layout was already tight).
 */
private fun tipLayout(size: PostSize): TemplateLayout {
    val decor = size.pick(
        listOf(
            inBlock(.07f, .47f, .86f, .37f, .04f, PostTone.ACCENT, to = PostTone.DEEP, shadow = true),
            inRule(size, .50f, .285f, .10f, PostTone.ACCENT, 8f)
        ),
        listOf(
            inBlock(.40f, .16f, .54f, .70f, .04f, PostTone.ACCENT, to = PostTone.DEEP, shadow = true),
            inRule(size, .06f, .195f, .08f, PostTone.ACCENT, 8f)
        ),
        listOf(
            inBlock(.07f, .42f, .86f, .38f, .04f, PostTone.ACCENT, to = PostTone.DEEP, shadow = true),
            inRule(size, .46f, .272f, .12f, PostTone.ACCENT, 8f)
        )
    )
    val slots = size.pick(
        listOf(
            inText(PostSlots.EYEBROW, .50f, .225f, .43f, .04f, .028f, PostFont.BOLD, PostTone.ACCENT, caps = true),
            inText(PostSlots.NUMBER, .06f, .13f, .40f, .33f, .22f, PostFont.DISPLAY, PostTone.ACCENT),
            inText(PostSlots.HEADLINE, .12f, .51f, .76f, .17f, .056f, PostFont.DISPLAY, PostTone.LIGHT),
            inText(PostSlots.BODY, .12f, .66f, .76f, .15f, .034f, PostFont.REGULAR, PostTone.LIGHT)
        ),
        listOf(
            inText(PostSlots.EYEBROW, .06f, .14f, .30f, .04f, .024f, PostFont.BOLD, PostTone.ACCENT, caps = true),
            inText(PostSlots.NUMBER, .06f, .21f, .31f, .50f, .30f, PostFont.DISPLAY, PostTone.ACCENT),
            inText(PostSlots.HEADLINE, .46f, .22f, .42f, .30f, .060f, PostFont.DISPLAY, PostTone.LIGHT),
            inText(PostSlots.BODY, .46f, .56f, .42f, .24f, .032f, PostFont.REGULAR, PostTone.LIGHT)
        ),
        listOf(
            inText(PostSlots.EYEBROW, .46f, .225f, .47f, .035f, .030f, PostFont.BOLD, PostTone.ACCENT, caps = true),
            inText(PostSlots.NUMBER, .07f, .17f, .38f, .19f, .21f, PostFont.DISPLAY, PostTone.ACCENT),
            inText(PostSlots.HEADLINE, .12f, .46f, .76f, .19f, .066f, PostFont.DISPLAY, PostTone.LIGHT),
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
            inBlock(.40f, .22f, .54f, .56f, .035f, PostTone.LIGHT, shadow = true)
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

private fun qaLayout(size: PostSize): TemplateLayout {
    // Q badge, A badge and the label beside each one, per size (cx, cy as canvas fractions, radius in pixels)
    val qr = size.pick(34f, 34f, 40f)
    val qcx = size.pick(.105f, .085f, .125f)
    val qcy = size.pick(.19f, .215f, .20f)
    val ar = qr
    val acx = size.pick(.105f, .575f, .125f)
    val acy = size.pick(.605f, .27f, .59f)
    val (qDisc, qLetter) = inBadge(size, qcx, qcy, qr, "Q", PostTone.ACCENT, PostTone.LIGHT)
    val (aDisc, aLetter) = inBadge(size, acx, acy, ar, "A", PostTone.SECONDARY, PostTone.TEXT)
    val card = size.pick(
        inBlock(.06f, .54f, .88f, .32f, .04f, PostTone.TEXT, to = PostTone.DEEP, shadow = true),
        inBlock(.52f, .18f, .42f, .66f, .04f, PostTone.TEXT, to = PostTone.DEEP, shadow = true),
        inBlock(.07f, .54f, .86f, .32f, .04f, PostTone.TEXT, to = PostTone.DEEP, shadow = true)
    )
    val label = size.pick(26f, 26f, 28f)
    val slots = size.pick(
        listOf(
            inText(PostSlots.EYEBROW, .17f, inTopFor(size, qcy, label), .55f, .04f, .024f, PostFont.BOLD, PostTone.ACCENT, caps = true),
            inText(PostSlots.HEADLINE, .07f, .26f, .86f, .25f, .062f, PostFont.DISPLAY, PostTone.TEXT),
            inMark("ANSWER", .17f, inTopFor(size, acy, label), .50f, .04f, .024f, PostFont.BOLD, PostTone.SECONDARY),
            inText(PostSlots.BODY, .11f, .67f, .78f, .17f, .050f, PostFont.DISPLAY, PostTone.LIGHT)
        ),
        listOf(
            inText(PostSlots.EYEBROW, .14f, inTopFor(size, qcy, label), .32f, .04f, .024f, PostFont.BOLD, PostTone.ACCENT, caps = true),
            inText(PostSlots.HEADLINE, .06f, .30f, .40f, .46f, .060f, PostFont.DISPLAY, PostTone.TEXT),
            inMark("ANSWER", .62f, inTopFor(size, acy, label), .28f, .04f, .024f, PostFont.BOLD, PostTone.SECONDARY),
            inText(PostSlots.BODY, .57f, .36f, .34f, .42f, .056f, PostFont.DISPLAY, PostTone.LIGHT)
        ),
        listOf(
            inText(PostSlots.EYEBROW, .21f, inTopFor(size, qcy, label), .60f, .03f, .026f, PostFont.BOLD, PostTone.ACCENT, caps = true),
            inText(PostSlots.HEADLINE, .08f, .27f, .84f, .24f, .074f, PostFont.DISPLAY, PostTone.TEXT),
            inMark("ANSWER", .21f, inTopFor(size, acy, label), .60f, .03f, .026f, PostFont.BOLD, PostTone.SECONDARY),
            inText(PostSlots.BODY, .12f, .65f, .76f, .18f, .060f, PostFont.DISPLAY, PostTone.LIGHT)
        )
    )
    // the A badge sits on the card, so the card comes first
    return TemplateLayout(PostBase.LIGHT, listOf(card, qDisc, aDisc), slots + qLetter + aLetter, backdrop = IN_BACKDROP)
}

private fun checklistLayout(size: PostSize): TemplateLayout {
    val r = size.pick(60f, 56f, 64f)
    val cx = size.pick(.13f, .10f, .14f)
    val cy = size.pick(.235f, .25f, .215f)
    val (disc, tick) = inBadge(size, cx, cy, r, "\u2713", PostTone.ACCENT, PostTone.LIGHT, PostFont.BOLD)
    val card = size.pick(
        inBlock(.07f, .40f, .86f, .46f, .035f, PostTone.LIGHT, shadow = true),
        inBlock(.44f, .15f, .50f, .70f, .035f, PostTone.LIGHT, shadow = true),
        inBlock(.07f, .45f, .86f, .41f, .035f, PostTone.LIGHT, shadow = true)
    )
    val slots = size.pick(
        listOf(
            inText(PostSlots.EYEBROW, .24f, .185f, .64f, .035f, .024f, PostFont.BOLD, PostTone.ACCENT, caps = true),
            inText(PostSlots.HEADLINE, .24f, .23f, .66f, .14f, .056f, PostFont.DISPLAY, PostTone.TEXT),
            inList(PostSlots.LIST, .12f, .435f, .76f, .39f, .040f, PostFont.BOLD, PostTone.TEXT, ListStyle.CHECK)
        ),
        listOf(
            inText(PostSlots.EYEBROW, .06f, .37f, .32f, .04f, .022f, PostFont.BOLD, PostTone.ACCENT, caps = true),
            inText(PostSlots.HEADLINE, .06f, .42f, .32f, .32f, .060f, PostFont.DISPLAY, PostTone.TEXT),
            inList(PostSlots.LIST, .49f, .20f, .40f, .60f, .040f, PostFont.BOLD, PostTone.TEXT, ListStyle.CHECK)
        ),
        listOf(
            inText(PostSlots.EYEBROW, .26f, .205f, .62f, .03f, .024f, PostFont.BOLD, PostTone.ACCENT, caps = true),
            inText(PostSlots.HEADLINE, .08f, .29f, .84f, .14f, .074f, PostFont.DISPLAY, PostTone.TEXT),
            inList(PostSlots.LIST, .12f, .485f, .76f, .34f, .042f, PostFont.BOLD, PostTone.TEXT, ListStyle.CHECK)
        )
    )
    return TemplateLayout(PostBase.LIGHT, listOf(card, disc), slots + tick, backdrop = IN_BACKDROP)
}

private fun timetableLayout(size: PostSize): TemplateLayout {
    val decor = size.pick(
        listOf(inBlock(.07f, .33f, .86f, .53f, .04f, PostTone.TEXT, to = PostTone.DEEP, shadow = true)),
        listOf(
            inBlock(.42f, .14f, .52f, .72f, .04f, PostTone.TEXT, to = PostTone.DEEP, shadow = true),
            inRule(size, .06f, .56f, .08f, PostTone.ACCENT, 8f)
        ),
        listOf(inBlock(.07f, .36f, .86f, .50f, .04f, PostTone.TEXT, to = PostTone.DEEP, shadow = true))
    )
    val slots = size.pick(
        listOf(
            inText(PostSlots.EYEBROW, .08f, .15f, .60f, .035f, .024f, PostFont.BOLD, PostTone.ACCENT, caps = true),
            inText(PostSlots.HEADLINE, .08f, .19f, .84f, .11f, .058f, PostFont.DISPLAY, PostTone.TEXT),
            inList(PostSlots.LIST, .12f, .37f, .76f, .45f, .040f, PostFont.REGULAR, PostTone.LIGHT, ListStyle.ROW)
        ),
        listOf(
            inText(PostSlots.EYEBROW, .06f, .18f, .32f, .04f, .022f, PostFont.BOLD, PostTone.ACCENT, caps = true),
            inText(PostSlots.HEADLINE, .06f, .23f, .32f, .30f, .062f, PostFont.DISPLAY, PostTone.TEXT),
            inList(PostSlots.LIST, .47f, .19f, .42f, .62f, .040f, PostFont.REGULAR, PostTone.LIGHT, ListStyle.ROW)
        ),
        listOf(
            inText(PostSlots.EYEBROW, .08f, .17f, .60f, .025f, .026f, PostFont.BOLD, PostTone.ACCENT, caps = true),
            inText(PostSlots.HEADLINE, .08f, .205f, .84f, .13f, .070f, PostFont.DISPLAY, PostTone.TEXT),
            inList(PostSlots.LIST, .12f, .40f, .76f, .42f, .042f, PostFont.REGULAR, PostTone.LIGHT, ListStyle.ROW)
        )
    )
    return TemplateLayout(PostBase.LIGHT, decor, slots, backdrop = IN_BACKDROP)
}

private fun definitionLayout(size: PostSize): TemplateLayout {
    val decor = size.pick(
        listOf(
            inPanel(0f, 0f, 1f, .47f, PostTone.ACCENT, PostTone.DEEP),
            inBar(0f, .47f, 1f, 6f / size.height, PostTone.SECONDARY),
            inRule(size, .08f, .51f, .10f, PostTone.ACCENT, 8f)
        ),
        listOf(
            inPanel(0f, 0f, 1f, .50f, PostTone.ACCENT, PostTone.DEEP),
            inBar(0f, .50f, 1f, 6f / size.height, PostTone.SECONDARY),
            inRule(size, .06f, .54f, .08f, PostTone.ACCENT, 8f)
        ),
        listOf(
            inPanel(0f, 0f, 1f, .40f, PostTone.ACCENT, PostTone.DEEP),
            inBar(0f, .40f, 1f, 6f / size.height, PostTone.SECONDARY),
            inRule(size, .08f, .435f, .12f, PostTone.ACCENT, 8f)
        )
    )
    val slots = size.pick(
        listOf(
            inText(PostSlots.EYEBROW, .08f, .17f, .60f, .035f, .024f, PostFont.BOLD, PostTone.SECONDARY, caps = true),
            inText(PostSlots.HEADLINE, .08f, .23f, .84f, .20f, .110f, PostFont.DISPLAY, PostTone.LIGHT),
            inText(PostSlots.BODY, .08f, .55f, .84f, .30f, .038f, PostFont.REGULAR, PostTone.TEXT)
        ),
        listOf(
            inText(PostSlots.EYEBROW, .06f, .17f, .60f, .04f, .022f, PostFont.BOLD, PostTone.SECONDARY, caps = true),
            inText(PostSlots.HEADLINE, .06f, .23f, .78f, .24f, .130f, PostFont.DISPLAY, PostTone.LIGHT),
            inText(PostSlots.BODY, .06f, .58f, .80f, .26f, .038f, PostFont.REGULAR, PostTone.TEXT)
        ),
        listOf(
            inText(PostSlots.EYEBROW, .08f, .17f, .60f, .025f, .026f, PostFont.BOLD, PostTone.SECONDARY, caps = true),
            inText(PostSlots.HEADLINE, .08f, .215f, .84f, .16f, .120f, PostFont.DISPLAY, PostTone.LIGHT),
            inText(PostSlots.BODY, .08f, .46f, .84f, .38f, .042f, PostFont.REGULAR, PostTone.TEXT)
        )
    )
    return TemplateLayout(PostBase.LIGHT, decor, slots, backdrop = IN_BACKDROP)
}

private fun statLayout(size: PostSize): TemplateLayout {
    val decor = size.pick(
        listOf(
            inRule(size, .07f, .545f, .16f, PostTone.ACCENT, 10f),
            inBlock(.07f, .735f, .86f, .125f, .03f, PostTone.LIGHT, shadow = true)
        ),
        listOf(inRule(size, .56f, .62f, .10f, PostTone.ACCENT, 10f)),
        listOf(
            inRule(size, .07f, .47f, .16f, PostTone.ACCENT, 10f),
            inBlock(.07f, .69f, .86f, .17f, .03f, PostTone.LIGHT, shadow = true)
        )
    )
    val slots = size.pick(
        listOf(
            inText(PostSlots.EYEBROW, .07f, .15f, .60f, .035f, .024f, PostFont.BOLD, PostTone.ACCENT, caps = true),
            inText(PostSlots.NUMBER, .07f, .19f, .86f, .34f, .240f, PostFont.DISPLAY, PostTone.ACCENT),
            inText(PostSlots.HEADLINE, .07f, .58f, .86f, .14f, .050f, PostFont.DISPLAY, PostTone.TEXT),
            inText(PostSlots.BODY, .11f, .755f, .78f, .09f, .028f, PostFont.REGULAR, PostTone.MUTED)
        ),
        listOf(
            inText(PostSlots.EYEBROW, .56f, .20f, .38f, .04f, .022f, PostFont.BOLD, PostTone.ACCENT, caps = true),
            inText(PostSlots.NUMBER, .06f, .22f, .46f, .52f, .300f, PostFont.DISPLAY, PostTone.ACCENT),
            inText(PostSlots.HEADLINE, .56f, .27f, .38f, .32f, .050f, PostFont.DISPLAY, PostTone.TEXT),
            inText(PostSlots.BODY, .56f, .65f, .38f, .19f, .028f, PostFont.REGULAR, PostTone.MUTED)
        ),
        listOf(
            inText(PostSlots.EYEBROW, .07f, .17f, .60f, .025f, .026f, PostFont.BOLD, PostTone.ACCENT, caps = true),
            inText(PostSlots.NUMBER, .07f, .21f, .86f, .25f, .270f, PostFont.DISPLAY, PostTone.ACCENT),
            inText(PostSlots.HEADLINE, .07f, .51f, .86f, .15f, .070f, PostFont.DISPLAY, PostTone.TEXT),
            inText(PostSlots.BODY, .12f, .715f, .76f, .12f, .034f, PostFont.REGULAR, PostTone.MUTED)
        )
    )
    return TemplateLayout(PostBase.LIGHT, decor, slots, backdrop = IN_BACKDROP)
}
