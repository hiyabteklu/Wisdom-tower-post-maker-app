package com.wisdomtower.post

/**
 * More photo templates (family "Photo"): Circle portrait, Glass caption, Magazine cover, Testimonial, Bento grid.
 * Same rules as PostTemplatesPhoto.kt: every layout has its own placement for 1:1, 4:3 and 9:16 (4:5 falls into 1:1),
 * text sits between 12% and 88% of the height, and shapes that must show above the photo are marked overPhoto.
 */

private fun <T> PostSize.pqPick(square: T, wide: T, story: T): T = when (this) {
    PostSize.STORY -> story
    PostSize.LANDSCAPE, PostSize.WIDE -> wide
    else -> square
}

private fun pqText(
    slot: String, x: Float, y: Float, w: Float, h: Float, size: Float, font: PostFont, tone: PostTone,
    centered: Boolean = false, caps: Boolean = false
) = SlotBox(slot, x, y, w, h, size, font, tone, centered, caps)

/** Fixed text the person never edits (the quote mark). */
private fun pqMark(text: String, x: Float, y: Float, w: Float, h: Float, size: Float, tone: PostTone, centered: Boolean) =
    SlotBox("", x, y, w, h, size, PostFont.DISPLAY, tone, centered, false, ListStyle.NONE, text)

private fun pqBlock(
    x: Float, y: Float, w: Float, h: Float, radius: Float, tone: PostTone, alpha: Int = 255,
    over: Boolean = false, to: PostTone? = null, alphaTo: Int = -1, angle: Float = 90f, shadow: Boolean = false, frameBack: Int = 0
) = DecorBox(DecorShape.ROUND, x, y, w, h, tone, alpha, radius, over, to, alphaTo, angle, 0f, shadow, frameBack)

private fun pqRect(
    x: Float, y: Float, w: Float, h: Float, tone: PostTone, alpha: Int, to: PostTone?, alphaTo: Int, angle: Float, over: Boolean
) = DecorBox(DecorShape.RECT, x, y, w, h, tone, alpha, 0f, over, to, alphaTo, angle)

/** A circle on canvas fractions (centre cx, cy; radius in pixels of the 1080 base) as a box. */
private fun pqCircleBox(size: PostSize, cx: Float, cy: Float, rPx: Float): List<Float> =
    listOf(cx - rPx / size.width, cy - rPx / size.height, 2f * rPx / size.width, 2f * rPx / size.height)

private fun pqGlow(size: PostSize, cx: Float, cy: Float, rPx: Float, tone: PostTone, to: PostTone, alpha: Int): DecorBox {
    val (x, y, w, h) = pqCircleBox(size, cx, cy, rPx)
    return DecorBox(DecorShape.OVAL, x, y, w, h, tone, alpha, gradTo = to, alphaTo = 0, angle = 45f)
}

private fun pqTemplate(
    id: String, name: String, samples: Map<String, String>, look: TemplateLook, build: (PostSize) -> TemplateLayout
) = PostTemplateSpec(id, name, "Photo", samples, look) { size -> build(size).copy(clean = true) }

internal val MorePhotoTemplates: List<PostTemplateSpec> = listOf(
    pqTemplate(
        "photo-circle", "Circle portrait",
        mapOf(
            PostSlots.EYEBROW to "STUDENT OF THE MONTH",
            PostSlots.HEADLINE to "Selam Bekele",
            PostSlots.BODY to "Grade 12 · Top of her class"
        ),
        TemplateLook(frame = PhotoFrame.CIRCLE),
        ::circleLayout
    ),
    pqTemplate(
        "photo-glass", "Glass caption",
        mapOf(
            PostSlots.EYEBROW to "THIS WEEK",
            PostSlots.HEADLINE to "Science fair day at Wisdom Tower",
            PostSlots.BODY to "Projects, demos and a lot of curiosity."
        ),
        TemplateLook(),
        ::glassLayout
    ),
    pqTemplate(
        "photo-cover", "Magazine cover",
        mapOf(
            PostSlots.EYEBROW to "THIS TERM'S COVER STORY",
            PostSlots.HEADLINE to "Exam ready",
            PostSlots.BODY to "How our grade 12 class prepared, week by week."
        ),
        TemplateLook(grain = true),
        ::coverLayout
    ),
    pqTemplate(
        "photo-testimonial", "Testimonial",
        mapOf(
            PostSlots.EYEBROW to "AMANUEL T., GRADE 12",
            PostSlots.HEADLINE to "The teachers here made me believe I could do it.",
            PostSlots.BODY to "Scored 94% in the national exam"
        ),
        TemplateLook(frame = PhotoFrame.CIRCLE),
        ::testimonialLayout
    ),
    pqTemplate(
        "photo-bento", "Bento grid",
        mapOf(
            PostSlots.EYEBROW to "RESULTS",
            PostSlots.NUMBER to "94%",
            PostSlots.HEADLINE to "Our best exam results yet",
            PostSlots.BODY to "Average score across grade 12 this year."
        ),
        TemplateLook(frame = PhotoFrame.ROUNDED),
        ::bentoLayout
    )
)

private fun circleLayout(size: PostSize): TemplateLayout {
    val cx = size.pqPick(.50f, .27f, .50f)
    val cy = size.pqPick(.35f, .50f, .32f)
    val r = size.pqPick(240f, 270f, 320f)
    val (x, y, w, h) = pqCircleBox(size, cx, cy, r)
    val (dx, dy, dw, dh) = pqCircleBox(size, cx + 28f / size.width, cy + 28f / size.height, r)
    val slots = size.pqPick(
        listOf(
            pqText(PostSlots.EYEBROW, .10f, .66f, .80f, .03f, .024f, PostFont.BOLD, PostTone.ACCENT, centered = true, caps = true),
            pqText(PostSlots.HEADLINE, .10f, .705f, .80f, .09f, .062f, PostFont.DISPLAY, PostTone.TEXT, centered = true),
            pqText(PostSlots.BODY, .10f, .80f, .80f, .06f, .030f, PostFont.REGULAR, PostTone.MUTED, centered = true)
        ),
        listOf(
            pqText(PostSlots.EYEBROW, .56f, .31f, .38f, .04f, .022f, PostFont.BOLD, PostTone.ACCENT, caps = true),
            pqText(PostSlots.HEADLINE, .56f, .37f, .38f, .20f, .060f, PostFont.DISPLAY, PostTone.TEXT),
            pqText(PostSlots.BODY, .56f, .59f, .38f, .14f, .030f, PostFont.REGULAR, PostTone.MUTED)
        ),
        listOf(
            pqText(PostSlots.EYEBROW, .10f, .56f, .80f, .025f, .026f, PostFont.BOLD, PostTone.ACCENT, centered = true, caps = true),
            pqText(PostSlots.HEADLINE, .10f, .60f, .80f, .10f, .074f, PostFont.DISPLAY, PostTone.TEXT, centered = true),
            pqText(PostSlots.BODY, .10f, .71f, .80f, .07f, .034f, PostFont.REGULAR, PostTone.MUTED, centered = true)
        )
    )
    val decor = listOf(
        pqGlow(size, cx, cy, r * 1.5f, PostTone.GLOW, PostTone.TINT, 150),
        DecorBox(DecorShape.OVAL, dx, dy, dw, dh, PostTone.ACCENT, gradTo = PostTone.GLOW, angle = 135f, frameBack = 1),
        DecorBox(DecorShape.OVAL, x, y, w, h, PostTone.LIGHT, frameBack = 1)
    )
    return TemplateLayout(
        PostBase.LIGHT, decor, slots,
        photo = PostPhotoArea(x, y, w, h, 10),
        backdrop = Backdrop(PostTone.TINT, PostTone.SOFT, 150f)
    )
}

private fun glassLayout(size: PostSize): TemplateLayout {
    val card = size.pqPick(listOf(.07f, .52f, .86f, .32f), listOf(.06f, .20f, .46f, .62f), listOf(.07f, .58f, .86f, .28f))
    val slots = size.pqPick(
        listOf(
            pqText(PostSlots.EYEBROW, .11f, .55f, .78f, .035f, .022f, PostFont.BOLD, PostTone.SECONDARY, caps = true),
            pqText(PostSlots.HEADLINE, .11f, .60f, .78f, .15f, .056f, PostFont.DISPLAY, PostTone.LIGHT),
            pqText(PostSlots.BODY, .11f, .76f, .78f, .07f, .028f, PostFont.REGULAR, PostTone.LIGHT_MUTED)
        ),
        listOf(
            pqText(PostSlots.EYEBROW, .10f, .24f, .38f, .04f, .022f, PostFont.BOLD, PostTone.SECONDARY, caps = true),
            pqText(PostSlots.HEADLINE, .10f, .30f, .38f, .28f, .054f, PostFont.DISPLAY, PostTone.LIGHT),
            pqText(PostSlots.BODY, .10f, .60f, .38f, .16f, .028f, PostFont.REGULAR, PostTone.LIGHT_MUTED)
        ),
        listOf(
            pqText(PostSlots.EYEBROW, .11f, .61f, .78f, .025f, .026f, PostFont.BOLD, PostTone.SECONDARY, caps = true),
            pqText(PostSlots.HEADLINE, .11f, .65f, .78f, .13f, .066f, PostFont.DISPLAY, PostTone.LIGHT),
            pqText(PostSlots.BODY, .11f, .79f, .78f, .06f, .032f, PostFont.REGULAR, PostTone.LIGHT_MUTED)
        )
    )
    val decor = listOf(
        pqGlow(size, .85f, .15f, 420f, PostTone.ACCENT, PostTone.GLOW, 190),
        pqRect(0f, 0f, 1f, .25f, PostTone.DARK, 120, PostTone.DARK, 0, 90f, true),
        pqRect(0f, .40f, 1f, .60f, PostTone.DARK, 0, PostTone.DARK, 190, 90f, true),
        pqBlock(card[0], card[1], card[2], card[3], .04f, PostTone.LIGHT, alpha = 62, over = true)
    )
    return TemplateLayout(
        PostBase.DARK, decor, slots,
        photo = PostPhotoArea(0f, 0f, 1f, 1f, 22),
        backdrop = Backdrop(PostTone.DEEP, PostTone.PAGE, 150f)
    )
}

private fun coverLayout(size: PostSize): TemplateLayout {
    val slots = size.pqPick(
        listOf(
            pqText(PostSlots.EYEBROW, .07f, .15f, .60f, .035f, .024f, PostFont.BOLD, PostTone.SECONDARY, caps = true),
            pqText(PostSlots.HEADLINE, .07f, .20f, .86f, .36f, .120f, PostFont.DISPLAY, PostTone.LIGHT, caps = true),
            pqText(PostSlots.BODY, .07f, .72f, .70f, .12f, .034f, PostFont.REGULAR, PostTone.LIGHT)
        ),
        listOf(
            pqText(PostSlots.EYEBROW, .06f, .17f, .50f, .04f, .022f, PostFont.BOLD, PostTone.SECONDARY, caps = true),
            pqText(PostSlots.HEADLINE, .06f, .22f, .70f, .42f, .115f, PostFont.DISPLAY, PostTone.LIGHT, caps = true),
            pqText(PostSlots.BODY, .06f, .70f, .55f, .12f, .032f, PostFont.REGULAR, PostTone.LIGHT)
        ),
        listOf(
            pqText(PostSlots.EYEBROW, .08f, .17f, .84f, .03f, .026f, PostFont.BOLD, PostTone.SECONDARY, caps = true),
            pqText(PostSlots.HEADLINE, .08f, .22f, .84f, .34f, .140f, PostFont.DISPLAY, PostTone.LIGHT, caps = true),
            pqText(PostSlots.BODY, .08f, .74f, .84f, .10f, .038f, PostFont.REGULAR, PostTone.LIGHT)
        )
    )
    val decor = listOf(
        pqGlow(size, .80f, .20f, 480f, PostTone.ACCENT, PostTone.GLOW, 200),
        pqRect(0f, 0f, 1f, .55f, PostTone.DARK, 200, PostTone.DARK, 0, 90f, true),
        pqRect(0f, .55f, 1f, .45f, PostTone.DARK, 0, PostTone.DARK, 190, 90f, true)
    )
    return TemplateLayout(
        PostBase.DARK, decor, slots,
        photo = PostPhotoArea(0f, 0f, 1f, 1f, 18),
        backdrop = Backdrop(PostTone.DEEP, PostTone.PAGE, 160f)
    )
}

private fun testimonialLayout(size: PostSize): TemplateLayout {
    val card = size.pqPick(listOf(.07f, .17f, .86f, .67f), listOf(.06f, .16f, .88f, .68f), listOf(.07f, .25f, .86f, .58f))
    val ax = size.pqPick(.50f, .23f, .50f)
    val ay = size.pqPick(.17f, .50f, .25f)
    val ar = size.pqPick(110f, 170f, 130f)
    val (x, y, w, h) = pqCircleBox(size, ax, ay, ar)
    val (rx, ry, rw, rh) = pqCircleBox(size, ax, ay, ar + 12f)
    val slots = size.pqPick(
        listOf(
            pqMark("“", .35f, .265f, .30f, .13f, .15f, PostTone.ACCENT, true),
            pqText(PostSlots.HEADLINE, .13f, .39f, .74f, .28f, .050f, PostFont.DISPLAY, PostTone.TEXT, centered = true),
            pqText(PostSlots.EYEBROW, .13f, .71f, .74f, .04f, .022f, PostFont.BOLD, PostTone.ACCENT, centered = true, caps = true),
            pqText(PostSlots.BODY, .13f, .76f, .74f, .06f, .028f, PostFont.REGULAR, PostTone.MUTED, centered = true)
        ),
        listOf(
            pqMark("“", .43f, .20f, .14f, .15f, .14f, PostTone.ACCENT, false),
            pqText(PostSlots.HEADLINE, .43f, .32f, .48f, .28f, .050f, PostFont.DISPLAY, PostTone.TEXT),
            pqText(PostSlots.EYEBROW, .43f, .64f, .48f, .04f, .022f, PostFont.BOLD, PostTone.ACCENT, caps = true),
            pqText(PostSlots.BODY, .43f, .70f, .48f, .08f, .028f, PostFont.REGULAR, PostTone.MUTED)
        ),
        listOf(
            pqMark("“", .35f, .33f, .30f, .07f, .13f, PostTone.ACCENT, true),
            pqText(PostSlots.HEADLINE, .13f, .42f, .74f, .21f, .056f, PostFont.DISPLAY, PostTone.TEXT, centered = true),
            pqText(PostSlots.EYEBROW, .13f, .67f, .74f, .025f, .026f, PostFont.BOLD, PostTone.ACCENT, centered = true, caps = true),
            pqText(PostSlots.BODY, .13f, .71f, .74f, .08f, .032f, PostFont.REGULAR, PostTone.MUTED, centered = true)
        )
    )
    val decor = listOf(
        pqBlock(card[0], card[1], card[2], card[3], .04f, PostTone.LIGHT, shadow = true),
        DecorBox(DecorShape.OVAL, rx, ry, rw, rh, PostTone.ACCENT, gradTo = PostTone.GLOW, angle = 135f, frameBack = 1),
        DecorBox(DecorShape.OVAL, x, y, w, h, PostTone.LIGHT, frameBack = 1)
    )
    return TemplateLayout(
        PostBase.LIGHT, decor, slots,
        photo = PostPhotoArea(x, y, w, h, 8),
        backdrop = Backdrop(PostTone.TINT, PostTone.SOFT, 135f)
    )
}

private fun bentoLayout(size: PostSize): TemplateLayout {
    // photo tile, number tile, headline tile, note tile (x, y, w, h)
    val photo = size.pqPick(listOf(.07f, .15f, .56f, .38f), listOf(.05f, .15f, .44f, .70f), listOf(.07f, .15f, .86f, .30f))
    val num = size.pqPick(listOf(.66f, .15f, .27f, .38f), listOf(.52f, .15f, .20f, .34f), listOf(.07f, .48f, .41f, .16f))
    val head = size.pqPick(listOf(.07f, .56f, .56f, .30f), listOf(.52f, .52f, .43f, .33f), listOf(.07f, .66f, .86f, .22f))
    val note = size.pqPick(listOf(.66f, .56f, .27f, .30f), listOf(.75f, .15f, .20f, .34f), listOf(.52f, .48f, .41f, .16f))
    val slots = size.pqPick(
        listOf(
            pqText(PostSlots.NUMBER, .67f, .24f, .25f, .15f, .11f, PostFont.DISPLAY, PostTone.LIGHT, centered = true),
            pqText(PostSlots.EYEBROW, .10f, .585f, .50f, .035f, .022f, PostFont.BOLD, PostTone.ACCENT, caps = true),
            pqText(PostSlots.HEADLINE, .10f, .63f, .50f, .20f, .050f, PostFont.DISPLAY, PostTone.TEXT),
            pqText(PostSlots.BODY, .685f, .585f, .23f, .24f, .026f, PostFont.REGULAR, PostTone.DARK)
        ),
        listOf(
            pqText(PostSlots.NUMBER, .525f, .24f, .19f, .14f, .095f, PostFont.DISPLAY, PostTone.LIGHT, centered = true),
            pqText(PostSlots.EYEBROW, .55f, .55f, .37f, .04f, .022f, PostFont.BOLD, PostTone.ACCENT, caps = true),
            pqText(PostSlots.HEADLINE, .55f, .60f, .37f, .22f, .050f, PostFont.DISPLAY, PostTone.TEXT),
            pqText(PostSlots.BODY, .765f, .19f, .18f, .26f, .024f, PostFont.REGULAR, PostTone.DARK)
        ),
        listOf(
            pqText(PostSlots.NUMBER, .08f, .50f, .39f, .12f, .100f, PostFont.DISPLAY, PostTone.LIGHT, centered = true),
            pqText(PostSlots.EYEBROW, .11f, .685f, .78f, .025f, .026f, PostFont.BOLD, PostTone.ACCENT, caps = true),
            pqText(PostSlots.HEADLINE, .11f, .725f, .78f, .14f, .062f, PostFont.DISPLAY, PostTone.TEXT),
            pqText(PostSlots.BODY, .545f, .50f, .36f, .12f, .028f, PostFont.REGULAR, PostTone.DARK)
        )
    )
    val decor = listOf(
        pqBlock(photo[0], photo[1], photo[2], photo[3], .05f, PostTone.GLOW, alpha = 150, frameBack = 1),
        pqBlock(num[0], num[1], num[2], num[3], .04f, PostTone.ACCENT, to = PostTone.DEEP, angle = 135f),
        pqBlock(head[0], head[1], head[2], head[3], .04f, PostTone.LIGHT, shadow = true),
        pqBlock(note[0], note[1], note[2], note[3], .04f, PostTone.SECONDARY)
    )
    return TemplateLayout(
        PostBase.LIGHT, decor, slots,
        photo = PostPhotoArea(photo[0], photo[1], photo[2], photo[3], 12),
        backdrop = Backdrop(PostTone.TINT, PostTone.SOFT, 135f)
    )
}
