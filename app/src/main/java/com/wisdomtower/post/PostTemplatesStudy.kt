package com.wisdomtower.post

/**
 * Chapter summaries (family CHAPTER_FAMILY): six notebook-paper study sheets, docs/CHAPTER_SUMMARIES_PLAN.md.
 * The paper (30 ruled lines and the red margin) is the only decoration. All the text, headings, tables and boxes are laid on the
 * rules by PostStudyGrid.kt, so the slot boxes below only say WHICH words a variant has, not where they go.
 *
 * Every variant declares EVERY text box (stPaperLayout adds the ones a variant does not use itself, after its own). The grid engine
 * ignores box positions and draws whatever words exist, so switching variants never hides content (docs/MATH_AND_TEMPLATES_PLAN.md).
 */

/** The family name. The editor's template strip shows only this family for a chapter-summaries draft, and never shows it for a poster draft. */
internal const val CHAPTER_FAMILY = "Chapter summaries"

private const val STU = 1080f
private val ST_BACKDROP = Backdrop(PostTone.PAPER, PostTone.PAPER, 90f)

private fun <T> PostSize.stPick(wide: T, story: T): T = if (this == PostSize.STORY) story else wide

private fun stTemplate(
    id: String,
    name: String,
    samples: Map<String, String>,
    build: (PostSize) -> TemplateLayout
) = PostTemplateSpec(id, name, CHAPTER_FAMILY, samples, null) { size -> build(size).copy(clean = true) }

private fun stText(
    slot: String, x: Float, y: Float, w: Float, h: Float, size: Float, font: PostFont, tone: PostTone,
    centered: Boolean = false, caps: Boolean = false
) = SlotBox(slot, x, y, w, h, when (slot) {
    PostSlots.HEADLINE -> minOf(size, .044f)
    PostSlots.EYEBROW -> minOf(size, .024f)
    PostSlots.NUMBER -> minOf(size, .068f)
    PostSlots.BODY -> minOf(size, .030f)
    PostSlots.BUTTON -> minOf(size, .025f)
    else -> size
}, font, tone, centered, caps)

private fun stList(
    slot: String, x: Float, y: Float, w: Float, h: Float, size: Float, font: PostFont, tone: PostTone, style: ListStyle
) = SlotBox(slot, x, y, w, h, minOf(size, .030f), font, tone, false, false, style)

private fun stRule(size: PostSize, x: Float, y: Float, w: Float, tone: PostTone, thickPx: Float = 2.5f, alpha: Int = 160) =
    DecorBox(DecorShape.RECT, x, y, w, thickPx / size.height, tone, alpha)

private fun stPaper(size: PostSize, marginX: Float = .04f): List<DecorBox> {
    // Exactly 30 evenly spaced notebook rules, spanning 95% of the paper.
    // The red margin is inset only 4%; ruling reaches nearly edge-to-edge.
    val rules = mutableListOf<DecorBox>()
    val first = .025f
    val last = .975f
    val step = (last - first) / 29f
    repeat(30) { index ->
        val y = first + index * step
        rules += stRule(size, .02f, y, .96f, PostTone.BLUE, 1.8f, 85)
    }
    rules += DecorBox(DecorShape.RECT, marginX, .025f, 2.0f / size.width, .95f, PostTone.RED, 150)
    return rules
}

private fun stInkUnderline(size: PostSize, x: Float, y: Float, w: Float, tone: PostTone = PostTone.RED) =
    stRule(size, x, y, w, tone, 5f, 235)

/**
 * The order in which the boxes a variant does not declare itself are added. LIST comes first so that, when a batch is pasted as plain
 * lines, every extra line lands in the list.
 */
private val ST_SLOT_ORDER = listOf(
    PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.LIST, PostSlots.BODY, PostSlots.BUTTON, PostSlots.NUMBER
)

/** A box for a text slot the variant does not use itself. Its position and size are never read by PostStudyGrid. */
private fun stSpareSlot(slot: String): SlotBox =
    if (slot == PostSlots.LIST) {
        stList(slot, .065f, .30f, .91f, .50f, .035f, PostFont.REGULAR, PostTone.TEXT, ListStyle.NONE)
    } else {
        stText(slot, .055f, .30f, .92f, .10f, .035f, PostFont.REGULAR, PostTone.TEXT)
    }

/**
 * The paper only. The decor list the variants still pass is ignored on purpose: those underlines and rules sat at fixed heights
 * and would now cut through the text that PostStudyGrid lays on the rules (it draws its own underlines, tables and boxes).
 * Text boxes the variant does not declare are appended, so the six variants all accept the same words.
 */
private fun stPaperLayout(size: PostSize, @Suppress("UNUSED_PARAMETER") decor: List<DecorBox>, slots: List<SlotBox>): TemplateLayout {
    val spare = ST_SLOT_ORDER.filter { name -> slots.none { it.slot == name } }.map { stSpareSlot(it) }
    return TemplateLayout(
        base = PostBase.LIGHT,
        decor = stPaper(size),
        slots = slots + spare,
        footerLight = false,
        backdrop = ST_BACKDROP,
        logoAnchor = LogoAnchor.BOTTOM_RIGHT
    )
}

internal val StudyTemplates: List<PostTemplateSpec> = listOf(
    stTemplate("study-cover", "Notebook cover", mapOf(
        PostSlots.EYEBROW to "GRADE 9 • GEOGRAPHY",
        PostSlots.HEADLINE to "Climate of Ethiopia",
        PostSlots.BODY to "Short notes • Save and revise",
        PostSlots.BUTTON to "CHAPTER 2  —  QUICK REVISION"
    ), ::stCoverLayout),

    stTemplate("study-list", "Handwritten numbered list", mapOf(
        PostSlots.EYEBROW to "QUICK REVIEW",
        PostSlots.HEADLINE to "Networking basics",
        PostSlots.LIST to "What is a network?\nWhat is an IP address?\nWhat is a MAC address?\nWhat is DNS?\nWhat is DHCP?"
    ), ::stListLayout),

    stTemplate("study-define", "Definition notes", mapOf(
        PostSlots.EYEBROW to "KEY DEFINITION",
        PostSlots.HEADLINE to "SQ3R",
        PostSlots.BODY to "A five-step study method: Survey, Question, Read, Recite and Review. Read actively, then recall the ideas without looking.",
        PostSlots.BUTTON to "REMEMBER: active recall > rereading"
    ), ::stDefineLayout),

    stTemplate("study-checklist", "Exam key-points sheet", mapOf(
        PostSlots.EYEBROW to "CHAPTER SUMMARY",
        PostSlots.HEADLINE to "Effects of climate on life",
        PostSlots.LIST to "Agriculture — crop type and harvest time\nVegetation — plant distribution\nSettlement — where people live\nWater — rivers, lakes, groundwater\nLivelihoods — farming and pastoralism"
    ), ::stChecklistLayout),

    stTemplate("study-table", "Handwritten notes table", mapOf(
        PostSlots.EYEBROW to "GEOGRAPHY • ETHIOPIA",
        PostSlots.HEADLINE to "Climatic zones",
        PostSlots.LIST to "|Zone|Altitude|Climate|\n|Dega|Above 2,500 m|Cool|\n|Woina Dega|1,500–2,500 m|Moderate|\n|Kolla|1,000–1,500 m|Hot|\n|Bereha|500–1,000 m|Very hot|\n|Wurch|Below 500 m|Extremely hot|\n>> Key Point | Altitude decides the zone more than latitude does."
    ), ::stTableLayout),

    stTemplate("study-mnemonic", "Mnemonic study sheet", mapOf(
        PostSlots.EYEBROW to "MEMORY TRICK",
        PostSlots.NUMBER to "SQ3R",
        PostSlots.HEADLINE to "Five steps to study a chapter",
        PostSlots.LIST to "S — Survey\nQ — Question\nR — Read\nR — Recite\nR — Review"
    ), ::stMnemonicLayout)
)

private fun stCoverLayout(size: PostSize): TemplateLayout {
    val slots = size.stPick(
        listOf(
            stText(PostSlots.EYEBROW, .055f, .16f, .92f, .045f, .030f, PostFont.BOLD, PostTone.RED, caps = true),
            stText(PostSlots.HEADLINE, .055f, .23f, .92f, .22f, .090f, PostFont.DISPLAY, PostTone.BLUE),
            stText(PostSlots.BODY, .055f, .49f, .92f, .10f, .040f, PostFont.REGULAR, PostTone.DARK),
            stText(PostSlots.BUTTON, .055f, .66f, .92f, .08f, .032f, PostFont.BOLD, PostTone.RED)
        ),
        listOf(
            stText(PostSlots.EYEBROW, .055f, .17f, .92f, .035f, .027f, PostFont.BOLD, PostTone.RED, caps = true),
            stText(PostSlots.HEADLINE, .055f, .23f, .92f, .20f, .100f, PostFont.DISPLAY, PostTone.BLUE),
            stText(PostSlots.BODY, .055f, .48f, .92f, .07f, .036f, PostFont.REGULAR, PostTone.TEXT),
            stText(PostSlots.BUTTON, .055f, .64f, .92f, .055f, .029f, PostFont.BOLD, PostTone.ACCENT)
        )
    )
    val decor = listOf(stInkUnderline(size, .15f, .455f, .48f), stInkUnderline(size, .15f, .61f, .25f, PostTone.ACCENT))
    return stPaperLayout(size, decor, slots)
}

private fun stListLayout(size: PostSize): TemplateLayout {
    val slots = size.stPick(
        listOf(
            stText(PostSlots.EYEBROW, .055f, .16f, .92f, .04f, .026f, PostFont.BOLD, PostTone.RED, caps = true),
            stText(PostSlots.HEADLINE, .055f, .22f, .92f, .065f, .070f, PostFont.DISPLAY, PostTone.BLUE),
            stList(PostSlots.LIST, .065f, .38f, .91f, .43f, .040f, PostFont.REGULAR, PostTone.TEXT, ListStyle.NUMBER)
        ),
        listOf(
            stText(PostSlots.EYEBROW, .055f, .15f, .92f, .03f, .026f, PostFont.BOLD, PostTone.RED, caps = true),
            stText(PostSlots.HEADLINE, .055f, .20f, .92f, .08f, .074f, PostFont.DISPLAY, PostTone.BLUE),
            stList(PostSlots.LIST, .065f, .32f, .91f, .51f, .041f, PostFont.REGULAR, PostTone.TEXT, ListStyle.NUMBER)
        )
    )
    return stPaperLayout(size, listOf(stInkUnderline(size, .15f, .355f, .30f), stInkUnderline(size, .78f, .29f, .055f, PostTone.ACCENT)), slots)
}

private fun stDefineLayout(size: PostSize): TemplateLayout {
    val slots = size.stPick(
        listOf(
            stText(PostSlots.EYEBROW, .055f, .16f, .92f, .04f, .027f, PostFont.BOLD, PostTone.RED, caps = true),
            stText(PostSlots.HEADLINE, .055f, .23f, .92f, .14f, .092f, PostFont.DISPLAY, PostTone.BLUE),
            stText(PostSlots.BODY, .055f, .40f, .92f, .26f, .040f, PostFont.REGULAR, PostTone.TEXT),
            stText(PostSlots.BUTTON, .055f, .70f, .92f, .09f, .033f, PostFont.BOLD, PostTone.ACCENT)
        ),
        listOf(
            stText(PostSlots.EYEBROW, .055f, .15f, .92f, .03f, .027f, PostFont.BOLD, PostTone.RED, caps = true),
            stText(PostSlots.HEADLINE, .055f, .20f, .92f, .10f, .100f, PostFont.DISPLAY, PostTone.BLUE),
            stText(PostSlots.BODY, .055f, .34f, .92f, .29f, .041f, PostFont.REGULAR, PostTone.TEXT),
            stText(PostSlots.BUTTON, .055f, .68f, .92f, .06f, .030f, PostFont.BOLD, PostTone.ACCENT)
        )
    )
    val decor = listOf(stInkUnderline(size, .15f, .375f, .26f), DecorBox(DecorShape.RECT, .065f, .685f, .91f, .002f, PostTone.ACCENT, 150), stInkUnderline(size, .15f, .795f, .50f, PostTone.RED))
    return stPaperLayout(size, decor, slots)
}

private fun stChecklistLayout(size: PostSize): TemplateLayout {
    val slots = size.stPick(
        listOf(
            stText(PostSlots.EYEBROW, .055f, .15f, .92f, .04f, .027f, PostFont.BOLD, PostTone.RED, caps = true),
            stText(PostSlots.HEADLINE, .055f, .21f, .92f, .16f, .074f, PostFont.DISPLAY, PostTone.BLUE),
            stList(PostSlots.LIST, .065f, .40f, .91f, .43f, .038f, PostFont.BOLD, PostTone.TEXT, ListStyle.CHECK)
        ),
        listOf(
            stText(PostSlots.EYEBROW, .055f, .14f, .92f, .03f, .027f, PostFont.BOLD, PostTone.RED, caps = true),
            stText(PostSlots.HEADLINE, .055f, .19f, .92f, .11f, .082f, PostFont.DISPLAY, PostTone.BLUE),
            stList(PostSlots.LIST, .065f, .34f, .91f, .49f, .039f, PostFont.BOLD, PostTone.TEXT, ListStyle.CHECK)
        )
    )
    val decor = listOf(stInkUnderline(size, .15f, .375f, .43f), stRule(size, .15f, .39f, .77f, PostTone.ACCENT, 1.6f, 125), stInkUnderline(size, .80f, .84f, .10f, PostTone.ACCENT))
    return stPaperLayout(size, decor, slots)
}

private fun stTableLayout(size: PostSize): TemplateLayout {
    val slots = size.stPick(
        listOf(
            stText(PostSlots.EYEBROW, .055f, .15f, .92f, .04f, .026f, PostFont.BOLD, PostTone.RED, caps = true),
            stText(PostSlots.HEADLINE, .055f, .21f, .92f, .055f, .072f, PostFont.DISPLAY, PostTone.BLUE),
            stList(PostSlots.LIST, .065f, .37f, .91f, .46f, .034f, PostFont.REGULAR, PostTone.TEXT, ListStyle.ROW)
        ),
        listOf(
            stText(PostSlots.EYEBROW, .055f, .14f, .92f, .03f, .026f, PostFont.BOLD, PostTone.RED, caps = true),
            stText(PostSlots.HEADLINE, .055f, .19f, .92f, .09f, .078f, PostFont.DISPLAY, PostTone.BLUE),
            stList(PostSlots.LIST, .065f, .31f, .91f, .52f, .035f, PostFont.REGULAR, PostTone.TEXT, ListStyle.ROW)
        )
    )
    val decor = listOf(stInkUnderline(size, .15f, .335f, .34f), stRule(size, .15f, .365f, .77f, PostTone.BLUE, 3f, 180), stRule(size, .15f, .92f, .77f, PostTone.RED, 3f, 150))
    return stPaperLayout(size, decor, slots)
}

private fun stMnemonicLayout(size: PostSize): TemplateLayout {
    val slots = size.stPick(
        listOf(
            stText(PostSlots.EYEBROW, .055f, .15f, .92f, .04f, .026f, PostFont.BOLD, PostTone.RED, caps = true),
            stText(PostSlots.NUMBER, .055f, .21f, .92f, .14f, .115f, PostFont.DISPLAY, PostTone.ACCENT),
            stText(PostSlots.HEADLINE, .055f, .38f, .92f, .10f, .050f, PostFont.BOLD, PostTone.BLUE),
            stList(PostSlots.LIST, .065f, .51f, .91f, .32f, .038f, PostFont.REGULAR, PostTone.TEXT, ListStyle.ROW)
        ),
        listOf(
            stText(PostSlots.EYEBROW, .055f, .14f, .92f, .03f, .026f, PostFont.BOLD, PostTone.RED, caps = true),
            stText(PostSlots.NUMBER, .055f, .19f, .92f, .055f, .120f, PostFont.DISPLAY, PostTone.ACCENT),
            stText(PostSlots.HEADLINE, .055f, .33f, .92f, .07f, .052f, PostFont.BOLD, PostTone.BLUE),
            stList(PostSlots.LIST, .065f, .44f, .91f, .39f, .039f, PostFont.REGULAR, PostTone.TEXT, ListStyle.ROW)
        )
    )
    val decor = listOf(stInkUnderline(size, .15f, .355f, .25f, PostTone.ACCENT), DecorBox(DecorShape.RECT, .15f, .48f, .006f, .33f, PostTone.RED, 135), stRule(size, .15f, .84f, .77f, PostTone.ACCENT, 3f, 170))
    return stPaperLayout(size, decor, slots)
}
