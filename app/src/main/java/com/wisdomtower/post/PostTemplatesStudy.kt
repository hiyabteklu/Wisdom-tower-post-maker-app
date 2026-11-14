package com.wisdomtower.post

/**
 * Study templates (family "Study"): seven dark, TikTok-style slides for education summaries that students save.
 * Built for 9:16 first (the story branch), with one shared layout for 4:3 and 16:9 so every size still works.
 *
 * Cover slide, Numbered list, Definition, Key points checklist, Notes table, Mnemonic, Flashcard.
 * Look: dark page (DEEP to PAGE), soft accent glows, see-through glass cards, a short rule under the headline.
 * Every color is a tone, so the slides follow the Brand screen. For the Wisdom Tower look set the Brand to
 * navy primary, cyan secondary and a very dark navy dark color.
 *
 * Same rules as the other families: every layout is marked clean, nothing uses DIAMOND, TRIANGLE, HEXAGON, DOTS, STRIPES or WAVE,
 * text sits between 12% and 88% of the height (on 9:16 it starts at 17% so the lowered logo box stays clear and it ends by 84%
 * so TikTok's caption area does not cover it), and text sizes respect PostDesignRules (body 34 px, label 26 px, headline 56 px).
 * Registered in PostTemplates.kt, first in the list, so the Library shows the Study row on top.
 */

private const val STU = 1080f

private val ST_BACKDROP = Backdrop(PostTone.DEEP, PostTone.PAGE, 150f)

/** Picks the value for the current size. 9:16 gets the story value; 4:3 and 16:9 share the wide value. */
private fun <T> PostSize.stPick(wide: T, story: T): T = if (this == PostSize.STORY) story else wide

private fun stTemplate(
    id: String,
    name: String,
    samples: Map<String, String>,
    look: TemplateLook? = null,
    build: (PostSize) -> TemplateLayout
) = PostTemplateSpec(id, name, "Study", samples, look) { size -> build(size).copy(clean = true) }

private fun stText(
    slot: String, x: Float, y: Float, w: Float, h: Float, size: Float, font: PostFont, tone: PostTone,
    centered: Boolean = false, caps: Boolean = false
) = SlotBox(slot, x, y, w, h, size, font, tone, centered, caps)

private fun stList(
    slot: String, x: Float, y: Float, w: Float, h: Float, size: Float, font: PostFont, tone: PostTone, style: ListStyle
) = SlotBox(slot, x, y, w, h, size, font, tone, false, false, style)

/** Fixed words the user never edits (the word ANSWER). */
private fun stMark(
    text: String, x: Float, y: Float, w: Float, h: Float, size: Float, font: PostFont, tone: PostTone, centered: Boolean = false
) = SlotBox("", x, y, w, h, size, font, tone, centered, true, ListStyle.NONE, text)

/** The top of a one-line box so that its text sits at the vertical centre cy (a canvas fraction). fontPx is the text size in pixels. */
private fun stTopFor(size: PostSize, cy: Float, fontPx: Float): Float = cy - 0.62f * fontPx / size.height

/** One line of text centred on the point (x + w / 2, cy). Used for the words inside a button. */
private fun stLine(
    size: PostSize, slot: String, x: Float, w: Float, cy: Float, sz: Float, font: PostFont, tone: PostTone, caps: Boolean = false
): SlotBox {
    val px = sz * STU
    return SlotBox(slot, x, stTopFor(size, cy, px), w, px * 1.5f / size.height, sz, font, tone, true, caps)
}

/** A rounded block. radius is a fraction of the shorter side. to turns the fill into a gradient. */
private fun stBlock(
    x: Float, y: Float, w: Float, h: Float, radius: Float, tone: PostTone,
    to: PostTone? = null, angle: Float = 135f, shadow: Boolean = false
) = DecorBox(DecorShape.ROUND, x, y, w, h, tone, 255, radius, false, to, -1, angle, 0f, shadow)

/** A see-through light card for the dark page. */
private fun stGlass(x: Float, y: Float, w: Float, h: Float, radius: Float = .035f, alpha: Int = 34) =
    DecorBox(DecorShape.ROUND, x, y, w, h, PostTone.LIGHT, alpha, radius)

/** A square-cornered panel, flat or a gradient. */
private fun stPanel(x: Float, y: Float, w: Float, h: Float, tone: PostTone, to: PostTone? = null, angle: Float = 135f) =
    DecorBox(DecorShape.RECT, x, y, w, h, tone, 255, 0f, false, to, -1, angle)

/** A thin horizontal rule. thickPx is in pixels of the 1080 base. */
private fun stRule(size: PostSize, x: Float, y: Float, w: Float, tone: PostTone, thickPx: Float = 8f) =
    DecorBox(DecorShape.RECT, x, y, w, thickPx / size.height, tone)

/** A soft circle of light centered on canvas fractions: starts at tone and fades out toward the edge. Radius in pixels of the 1080 base. */
private fun stGlow(size: PostSize, cx: Float, cy: Float, rPx: Float, tone: PostTone, to: PostTone, alpha: Int) = DecorBox(
    DecorShape.OVAL, cx - rPx / size.width, cy - rPx / size.height, 2f * rPx / size.width, 2f * rPx / size.height, tone,
    alpha = alpha, gradTo = to, alphaTo = 0, angle = 45f
)

internal val StudyTemplates: List<PostTemplateSpec> = listOf(

    // 1. Cover slide: the first slide of a carousel. Big title, a short promise, a swipe button.
    stTemplate(
        "study-cover", "Cover slide",
        mapOf(
            PostSlots.EYEBROW to "GRADE 9 \u00B7 GEOGRAPHY",
            PostSlots.HEADLINE to "Climate of Ethiopia",
            PostSlots.BODY to "Short notes you can save and revise in five minutes.",
            PostSlots.BUTTON to "Swipe to study  \u2192"
        ),
        TemplateLook(mesh = MeshStyle.AURORA, grain = true),
        ::stCoverLayout
    ),

    // 2. Numbered list: a headline and a glass card with numbered lines (questions, steps, terms).
    stTemplate(
        "study-list", "Numbered list",
        mapOf(
            PostSlots.EYEBROW to "CCNA \u00B7 INTERVIEW QUESTIONS",
            PostSlots.HEADLINE to "Networking basics",
            PostSlots.LIST to "What is a network?\nWhat is an IP address?\nWhat is a MAC address?\nWhat is DNS?\nWhat is DHCP?"
        ),
        TemplateLook(mesh = MeshStyle.SPOTLIGHT, grain = true),
        ::stListLayout
    ),

    // 3. Definition: a gradient title panel with the term, and the meaning on a glass card below it.
    stTemplate(
        "study-define", "Definition, dark",
        mapOf(
            PostSlots.EYEBROW to "DEFINITION",
            PostSlots.HEADLINE to "SQ3R",
            PostSlots.BODY to "A five-step way to study a chapter: Survey, Question, Read, Recite, Review. Read with questions in mind, then test yourself before moving on."
        ),
        TemplateLook(mesh = MeshStyle.DUSK, grain = true),
        ::stDefineLayout
    ),

    // 4. Key points checklist: a headline and a glass card with tick marks.
    stTemplate(
        "study-checklist", "Key points",
        mapOf(
            PostSlots.EYEBROW to "KEY POINTS",
            PostSlots.HEADLINE to "Effects of climate on life",
            PostSlots.LIST to "Agriculture: crop type and harvest time\nVegetation: where plants grow\nSettlement: where people live\nWater: rivers, lakes, groundwater\nLivelihoods: farming and pastoralism"
        ),
        TemplateLook(mesh = MeshStyle.AURORA, grain = true),
        ::stChecklistLayout
    ),

    // 5. Notes table: label and text rows on a glass card. Type each line as "Label: text".
    stTemplate(
        "study-table", "Notes table",
        mapOf(
            PostSlots.EYEBROW to "GEOGRAPHY \u00B7 CLIMATE ZONES",
            PostSlots.HEADLINE to "Climatic zones of Ethiopia",
            PostSlots.LIST to "Dega: above 2,500 m, cool\nWoina Dega: 1,500\u20132,500 m, moderate\nKolla: 1,000\u20131,500 m, hot\nBereha: 500\u20131,000 m, very hot\nWurch: below 500 m, extremely hot"
        ),
        TemplateLook(mesh = MeshStyle.SPOTLIGHT, grain = true),
        ::stTableLayout
    ),

    // 6. Mnemonic: a giant code or acronym lit from behind, then what each letter means.
    stTemplate(
        "study-mnemonic", "Mnemonic",
        mapOf(
            PostSlots.EYEBROW to "REMEMBER THIS",
            PostSlots.NUMBER to "SQ3R",
            PostSlots.HEADLINE to "Five steps to study a chapter",
            PostSlots.LIST to "S: Survey\nQ: Question\nR: Read\nR: Recite\nR: Review"
        ),
        TemplateLook(mesh = MeshStyle.SPOTLIGHT, grain = true),
        ::stMnemonicLayout
    ),

    // 7. Flashcard: the question on a gradient card, the answer on a glass card under it.
    stTemplate(
        "study-flashcard", "Flashcard",
        mapOf(
            PostSlots.EYEBROW to "FLASHCARD",
            PostSlots.HEADLINE to "What is learning?",
            PostSlots.BODY to "A lasting change in knowledge or behaviour that comes from experience."
        ),
        TemplateLook(mesh = MeshStyle.DUSK, grain = true),
        ::stFlashcardLayout
    )
)

private fun stCoverLayout(size: PostSize): TemplateLayout {
    val decor = size.stPick(
        listOf(
            stGlow(size, .88f, .50f, 520f, PostTone.ACCENT, PostTone.GLOW, 210),
            stGlow(size, .06f, .96f, 380f, PostTone.GLOW, PostTone.DEEP, 130),
            stRule(size, .06f, .64f, .10f, PostTone.SECONDARY, 8f),
            stBlock(.06f, .78f, .40f, .09f, .04f, PostTone.LIGHT, shadow = true)
        ),
        listOf(
            stGlow(size, .90f, .16f, 640f, PostTone.ACCENT, PostTone.GLOW, 220),
            stGlow(size, .08f, .90f, 520f, PostTone.GLOW, PostTone.DEEP, 150),
            stRule(size, .08f, .60f, .14f, PostTone.SECONDARY, 10f),
            stBlock(.08f, .78f, .84f, .075f, .04f, PostTone.LIGHT, shadow = true)
        )
    )
    val slots = size.stPick(
        listOf(
            stText(PostSlots.EYEBROW, .06f, .17f, .60f, .04f, .026f, PostFont.BOLD, PostTone.SECONDARY, caps = true),
            stText(PostSlots.HEADLINE, .06f, .24f, .62f, .36f, .095f, PostFont.DISPLAY, PostTone.LIGHT),
            stText(PostSlots.BODY, .06f, .68f, .55f, .09f, .034f, PostFont.REGULAR, PostTone.LIGHT_MUTED),
            stLine(size, PostSlots.BUTTON, .06f, .40f, .825f, .030f, PostFont.DISPLAY, PostTone.DARK, caps = true)
        ),
        listOf(
            stText(PostSlots.EYEBROW, .08f, .17f, .84f, .03f, .028f, PostFont.BOLD, PostTone.SECONDARY, caps = true),
            stText(PostSlots.HEADLINE, .08f, .23f, .84f, .34f, .118f, PostFont.DISPLAY, PostTone.LIGHT),
            stText(PostSlots.BODY, .08f, .64f, .84f, .13f, .040f, PostFont.REGULAR, PostTone.LIGHT_MUTED),
            stLine(size, PostSlots.BUTTON, .08f, .84f, .8175f, .034f, PostFont.DISPLAY, PostTone.DARK, caps = true)
        )
    )
    return TemplateLayout(PostBase.DARK, decor, slots, backdrop = ST_BACKDROP)
}

private fun stListLayout(size: PostSize): TemplateLayout {
    val decor = size.stPick(
        listOf(
            stGlow(size, .20f, .50f, 460f, PostTone.ACCENT, PostTone.GLOW, 150),
            stRule(size, .06f, .70f, .08f, PostTone.SECONDARY, 8f),
            stGlass(.47f, .15f, .47f, .70f)
        ),
        listOf(
            stGlow(size, .88f, .12f, 560f, PostTone.ACCENT, PostTone.GLOW, 200),
            stGlow(size, .06f, .96f, 420f, PostTone.GLOW, PostTone.DEEP, 120),
            stRule(size, .08f, .30f, .12f, PostTone.SECONDARY, 8f),
            stGlass(.06f, .335f, .88f, .50f)
        )
    )
    val slots = size.stPick(
        listOf(
            stText(PostSlots.EYEBROW, .06f, .20f, .36f, .06f, .026f, PostFont.BOLD, PostTone.SECONDARY, caps = true),
            stText(PostSlots.HEADLINE, .06f, .28f, .36f, .38f, .066f, PostFont.DISPLAY, PostTone.LIGHT),
            stList(PostSlots.LIST, .51f, .19f, .39f, .62f, .038f, PostFont.BOLD, PostTone.LIGHT, ListStyle.NUMBER)
        ),
        listOf(
            stText(PostSlots.EYEBROW, .08f, .17f, .84f, .03f, .026f, PostFont.BOLD, PostTone.SECONDARY, caps = true),
            stText(PostSlots.HEADLINE, .08f, .21f, .84f, .08f, .068f, PostFont.DISPLAY, PostTone.LIGHT),
            stList(PostSlots.LIST, .11f, .36f, .78f, .45f, .042f, PostFont.BOLD, PostTone.LIGHT, ListStyle.NUMBER)
        )
    )
    return TemplateLayout(PostBase.DARK, decor, slots, backdrop = ST_BACKDROP)
}

private fun stDefineLayout(size: PostSize): TemplateLayout {
    val decor = size.stPick(
        listOf(
            stPanel(0f, 0f, .46f, 1f, PostTone.ACCENT, PostTone.DEEP),
            stGlow(size, .22f, .30f, 420f, PostTone.GLOW, PostTone.ACCENT, 120),
            stGlass(.52f, .16f, .42f, .68f)
        ),
        listOf(
            stPanel(0f, 0f, 1f, .38f, PostTone.ACCENT, PostTone.DEEP),
            stGlow(size, .50f, .22f, 560f, PostTone.GLOW, PostTone.ACCENT, 110),
            stRule(size, .08f, .405f, .14f, PostTone.SECONDARY, 10f),
            stGlass(.07f, .45f, .86f, .39f)
        )
    )
    val slots = size.stPick(
        listOf(
            stText(PostSlots.EYEBROW, .06f, .17f, .34f, .04f, .026f, PostFont.BOLD, PostTone.SECONDARY, caps = true),
            stText(PostSlots.HEADLINE, .06f, .24f, .36f, .45f, .090f, PostFont.DISPLAY, PostTone.LIGHT),
            stText(PostSlots.BODY, .56f, .22f, .34f, .56f, .036f, PostFont.REGULAR, PostTone.LIGHT)
        ),
        listOf(
            stText(PostSlots.EYEBROW, .08f, .17f, .84f, .03f, .028f, PostFont.BOLD, PostTone.SECONDARY, caps = true),
            stText(PostSlots.HEADLINE, .08f, .22f, .84f, .15f, .120f, PostFont.DISPLAY, PostTone.LIGHT),
            stText(PostSlots.BODY, .12f, .48f, .76f, .33f, .044f, PostFont.REGULAR, PostTone.LIGHT)
        )
    )
    return TemplateLayout(PostBase.DARK, decor, slots, backdrop = ST_BACKDROP)
}

private fun stChecklistLayout(size: PostSize): TemplateLayout {
    val decor = size.stPick(
        listOf(
            stGlow(size, .20f, .50f, 440f, PostTone.ACCENT, PostTone.GLOW, 140),
            stRule(size, .06f, .68f, .08f, PostTone.SECONDARY, 8f),
            stGlass(.50f, .15f, .44f, .70f)
        ),
        listOf(
            stGlow(size, .90f, .14f, 560f, PostTone.ACCENT, PostTone.GLOW, 200),
            stGlow(size, .04f, .94f, 400f, PostTone.GLOW, PostTone.DEEP, 120),
            stRule(size, .08f, .40f, .12f, PostTone.SECONDARY, 8f),
            stGlass(.07f, .44f, .86f, .40f)
        )
    )
    val slots = size.stPick(
        listOf(
            stText(PostSlots.EYEBROW, .06f, .20f, .38f, .05f, .026f, PostFont.BOLD, PostTone.SECONDARY, caps = true),
            stText(PostSlots.HEADLINE, .06f, .27f, .38f, .38f, .066f, PostFont.DISPLAY, PostTone.LIGHT),
            stList(PostSlots.LIST, .54f, .20f, .36f, .60f, .036f, PostFont.BOLD, PostTone.LIGHT, ListStyle.CHECK)
        ),
        listOf(
            stText(PostSlots.EYEBROW, .08f, .17f, .84f, .03f, .028f, PostFont.BOLD, PostTone.SECONDARY, caps = true),
            stText(PostSlots.HEADLINE, .08f, .215f, .84f, .17f, .078f, PostFont.DISPLAY, PostTone.LIGHT),
            stList(PostSlots.LIST, .12f, .47f, .76f, .34f, .040f, PostFont.BOLD, PostTone.LIGHT, ListStyle.CHECK)
        )
    )
    return TemplateLayout(PostBase.DARK, decor, slots, backdrop = ST_BACKDROP)
}

private fun stTableLayout(size: PostSize): TemplateLayout {
    val decor = size.stPick(
        listOf(
            stGlow(size, .20f, .50f, 440f, PostTone.ACCENT, PostTone.GLOW, 140),
            stRule(size, .06f, .70f, .08f, PostTone.SECONDARY, 8f),
            stGlass(.46f, .15f, .48f, .70f)
        ),
        listOf(
            stGlow(size, .88f, .12f, 560f, PostTone.ACCENT, PostTone.GLOW, 200),
            stGlow(size, .06f, .96f, 420f, PostTone.GLOW, PostTone.DEEP, 120),
            stRule(size, .08f, .365f, .12f, PostTone.SECONDARY, 8f),
            stGlass(.07f, .40f, .86f, .44f)
        )
    )
    val slots = size.stPick(
        listOf(
            stText(PostSlots.EYEBROW, .06f, .20f, .36f, .06f, .026f, PostFont.BOLD, PostTone.SECONDARY, caps = true),
            stText(PostSlots.HEADLINE, .06f, .28f, .36f, .38f, .064f, PostFont.DISPLAY, PostTone.LIGHT),
            stList(PostSlots.LIST, .50f, .19f, .40f, .62f, .034f, PostFont.REGULAR, PostTone.LIGHT, ListStyle.ROW)
        ),
        listOf(
            stText(PostSlots.EYEBROW, .08f, .17f, .84f, .03f, .026f, PostFont.BOLD, PostTone.SECONDARY, caps = true),
            stText(PostSlots.HEADLINE, .08f, .21f, .84f, .14f, .072f, PostFont.DISPLAY, PostTone.LIGHT),
            stList(PostSlots.LIST, .11f, .42f, .78f, .40f, .036f, PostFont.REGULAR, PostTone.LIGHT, ListStyle.ROW)
        )
    )
    return TemplateLayout(PostBase.DARK, decor, slots, backdrop = ST_BACKDROP)
}

private fun stMnemonicLayout(size: PostSize): TemplateLayout {
    val decor = size.stPick(
        listOf(
            stGlow(size, .26f, .42f, 520f, PostTone.ACCENT, PostTone.GLOW, 190),
            stGlass(.52f, .15f, .42f, .70f)
        ),
        listOf(
            stGlow(size, .50f, .31f, 520f, PostTone.ACCENT, PostTone.GLOW, 180),
            stRule(size, .44f, .515f, .12f, PostTone.SECONDARY, 8f),
            stGlass(.07f, .55f, .86f, .29f)
        )
    )
    val slots = size.stPick(
        listOf(
            stText(PostSlots.EYEBROW, .06f, .17f, .40f, .04f, .026f, PostFont.BOLD, PostTone.SECONDARY, caps = true),
            stText(PostSlots.NUMBER, .04f, .26f, .44f, .30f, .210f, PostFont.DISPLAY, PostTone.SECONDARY, centered = true),
            stText(PostSlots.HEADLINE, .06f, .62f, .40f, .20f, .052f, PostFont.DISPLAY, PostTone.LIGHT),
            stList(PostSlots.LIST, .56f, .20f, .34f, .60f, .038f, PostFont.BOLD, PostTone.LIGHT, ListStyle.ROW)
        ),
        listOf(
            stText(PostSlots.EYEBROW, .08f, .17f, .84f, .03f, .028f, PostFont.BOLD, PostTone.SECONDARY, caps = true),
            stText(PostSlots.NUMBER, .04f, .22f, .92f, .17f, .200f, PostFont.DISPLAY, PostTone.SECONDARY, centered = true),
            stText(PostSlots.HEADLINE, .08f, .405f, .84f, .09f, .056f, PostFont.DISPLAY, PostTone.LIGHT, centered = true),
            stList(PostSlots.LIST, .12f, .575f, .76f, .24f, .038f, PostFont.BOLD, PostTone.LIGHT, ListStyle.ROW)
        )
    )
    return TemplateLayout(PostBase.DARK, decor, slots, backdrop = ST_BACKDROP)
}

private fun stFlashcardLayout(size: PostSize): TemplateLayout {
    val decor = size.stPick(
        listOf(
            stBlock(.06f, .17f, .42f, .66f, .04f, PostTone.ACCENT, to = PostTone.DEEP, shadow = true),
            stGlass(.52f, .17f, .42f, .66f)
        ),
        listOf(
            stGlow(size, .86f, .20f, 520f, PostTone.ACCENT, PostTone.GLOW, 150),
            stBlock(.07f, .17f, .86f, .31f, .04f, PostTone.ACCENT, to = PostTone.DEEP, shadow = true),
            stGlass(.07f, .55f, .86f, .29f)
        )
    )
    val slots = size.stPick(
        listOf(
            stText(PostSlots.EYEBROW, .09f, .21f, .34f, .04f, .026f, PostFont.BOLD, PostTone.SECONDARY, caps = true),
            stText(PostSlots.HEADLINE, .09f, .29f, .34f, .46f, .060f, PostFont.DISPLAY, PostTone.LIGHT),
            stMark("ANSWER", .56f, .21f, .30f, .04f, .026f, PostFont.BOLD, PostTone.SECONDARY),
            stText(PostSlots.BODY, .56f, .29f, .34f, .48f, .040f, PostFont.REGULAR, PostTone.LIGHT)
        ),
        listOf(
            stText(PostSlots.EYEBROW, .11f, .195f, .78f, .03f, .026f, PostFont.BOLD, PostTone.SECONDARY, caps = true),
            stText(PostSlots.HEADLINE, .11f, .245f, .78f, .21f, .068f, PostFont.DISPLAY, PostTone.LIGHT),
            stMark("ANSWER", .11f, .575f, .60f, .03f, .026f, PostFont.BOLD, PostTone.SECONDARY),
            stText(PostSlots.BODY, .11f, .62f, .78f, .20f, .042f, PostFont.REGULAR, PostTone.LIGHT)
        )
    )
    return TemplateLayout(PostBase.DARK, decor, slots, backdrop = ST_BACKDROP)
}
