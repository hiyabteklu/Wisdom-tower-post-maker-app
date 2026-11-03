package com.wisdomtower.post

import kotlin.math.min

/**
 * Phase 3, batch 1 of REDESIGN_PLAN.md: the seven photo templates.
 *
 * Each one has a signature structure (arch window, floating card, stacked frames, duotone full bleed, giant type behind a cut-out subject,
 * spotlight cut-out, pill portrait) instead of text on a gradient. They do not use PostLooks or PostRich (the layout is marked clean),
 * and they use no DIAMOND, TRIANGLE or HEXAGON shapes because the renderer draws nothing for those.
 *
 * Every layout has its own placement for 1:1, 4:3 and 9:16. Any other size (4:5 later) falls into the 1:1 branch until it gets its own.
 * Anything that must show above the photo is marked overPhoto, because shapes without it are drawn under the photo.
 * The shapes that sit directly behind the photo (halo, card, arch) are marked frameBack, so when a different photo frame is chosen
 * on the post they take that frame's shape and stay matched to the photo (see DecorBox.frameBack).
 */

/**
 * What a template sets on the post when it is picked: photo frame, mesh blend, grain, words behind the subject and where the cut-out sits.
 * Picking a template applies its look (see withTemplate); the studio can still change every one of these afterwards.
 */
data class TemplateLook(
    val frame: PhotoFrame? = null,
    val mesh: MeshStyle = MeshStyle.OFF,
    val grain: Boolean = false,
    val textBehind: Set<String> = emptySet(),
    val cutoutX: Float = 0.5f,
    val cutoutY: Float = 0.94f,
    val cutoutScale: Float = 1f
)

fun PostDoc.withLook(look: TemplateLook): PostDoc = copy(
    photoFrame = look.frame,
    meshStyle = look.mesh,
    grain = look.grain,
    textBehind = look.textBehind,
    cutoutX = look.cutoutX,
    cutoutY = look.cutoutY,
    cutoutScale = look.cutoutScale
)

private const val PHU = 1080f

private val PostSize.phStory: Boolean get() = this == PostSize.STORY
private val PostSize.phWide: Boolean get() = this == PostSize.LANDSCAPE || this == PostSize.WIDE

private fun photoTemplate(
    id: String,
    name: String,
    samples: Map<String, String>,
    look: TemplateLook,
    build: (PostSize) -> TemplateLayout
) = PostTemplateSpec(id, name, "Photo", samples, look) { size -> build(size).copy(clean = true) }

private fun phText(
    slot: String, x: Float, y: Float, w: Float, h: Float, size: Float, font: PostFont, tone: PostTone,
    centered: Boolean = false, caps: Boolean = false
) = SlotBox(slot, x, y, w, h, size, font, tone, centered, caps)

/** A rounded block. radius is a fraction of the shorter side. over = drawn above the photo. frameBack = 1 when it is the backing of the photo. */
private fun phBlock(
    x: Float, y: Float, w: Float, h: Float, radius: Float, tone: PostTone, alpha: Int = 255,
    over: Boolean = false, to: PostTone? = null, alphaTo: Int = -1, angle: Float = 90f, shadow: Boolean = false, frameBack: Int = 0
) = DecorBox(DecorShape.ROUND, x, y, w, h, tone, alpha, radius, over, to, alphaTo, angle, 0f, shadow, frameBack)

private fun phBar(x: Float, y: Float, w: Float, h: Float, tone: PostTone, alpha: Int = 255, over: Boolean = false) =
    DecorBox(DecorShape.RECT, x, y, w, h, tone, alpha, 0f, over)

/** Corner radius (a fraction of the shorter side) that turns a box into a stadium, or into the round top of an arch. */
private fun phHalfSide(size: PostSize, w: Float, h: Float): Float = min(w * size.width, h * size.height) / 2f / PHU

/**
 * An arch (round top, flat bottom) in one flat color. Built from a round block and a rectangle so the bottom stays square.
 * The round block is the backing that follows a chosen frame; the flat lower half is dropped then (frameBack 2).
 */
private fun phArch(size: PostSize, x: Float, y: Float, w: Float, h: Float, tone: PostTone): List<DecorBox> = listOf(
    phBlock(x, y, w, h, phHalfSide(size, w, h), tone, frameBack = 1),
    DecorBox(DecorShape.RECT, x, y + h / 2f, w, h / 2f, tone, frameBack = 2)
)

/** A soft circle of light centered on canvas fractions; r is a fraction of the shorter side. */
private fun phGlow(size: PostSize, cx: Float, cy: Float, r: Float, tone: PostTone, to: PostTone, alpha: Int) = DecorBox(
    DecorShape.OVAL, cx - r * PHU / size.width, cy - r * PHU / size.height, 2f * r * PHU / size.width, 2f * r * PHU / size.height,
    tone, alpha, gradTo = to, alphaTo = 0, angle = 45f
)

private fun phRing(size: PostSize, cx: Float, cy: Float, r: Float, tone: PostTone, alpha: Int, stroke: Float) = DecorBox(
    DecorShape.RING, cx - r * PHU / size.width, cy - r * PHU / size.height, 2f * r * PHU / size.width, 2f * r * PHU / size.height,
    tone, alpha, stroke = stroke
)

internal val PhotoTemplates: List<PostTemplateSpec> = listOf(

    // 1. Arch window: the photo in an arch with a solid offset arch behind it, text beside it (below it on 9:16)
    photoTemplate(
        "photo-arch", "Arch window",
        mapOf(
            PostSlots.EYEBROW to "COURSE SPOTLIGHT",
            PostSlots.HEADLINE to "Learn it. Then teach it.",
            PostSlots.BODY to "The fastest way to remember is to explain."
        ),
        TemplateLook(frame = PhotoFrame.ARCH),
        ::archLayout
    ),

    // 2. Rounded card: a large rounded photo with a floating caption card across its lower edge
    photoTemplate(
        "photo-card", "Rounded card",
        mapOf(
            PostSlots.EYEBROW to "STUDENT STORY",
            PostSlots.HEADLINE to "From 60% to 90% in one term",
            PostSlots.BODY to "What changed? A simple weekly routine."
        ),
        TemplateLook(frame = PhotoFrame.ROUNDED),
        ::cardLayout
    ),

    // 3. Stacked frames: the photo on two tilted cards, on a brand gradient
    photoTemplate(
        "photo-stack", "Stacked frames",
        mapOf(
            PostSlots.EYEBROW to "FIELD NOTES",
            PostSlots.HEADLINE to "Learning looks different in every classroom",
            PostSlots.BODY to "Swipe for the full story."
        ),
        TemplateLook(frame = PhotoFrame.STACKED),
        ::stackLayout
    ),

    // 4. Duotone full-bleed: the photo under a brand-color wash, headline at the bottom
    photoTemplate(
        "photo-duotone", "Duotone full-bleed",
        mapOf(
            PostSlots.EYEBROW to "THIS WEEK AT WISDOM TOWER",
            PostSlots.HEADLINE to "Every great result starts with showing up",
            PostSlots.BODY to "Join the next session."
        ),
        TemplateLook(grain = true),
        ::duotoneLayout
    ),

    // 5. Behind the subject: giant headline behind a cut-out subject, caption in front. Needs a transparent PNG of the subject.
    photoTemplate(
        "photo-behind", "Behind the subject",
        mapOf(
            PostSlots.EYEBROW to "MEET THE TUTOR",
            PostSlots.HEADLINE to "STUDY SMART",
            PostSlots.BODY to "Short lessons that fit your day."
        ),
        TemplateLook(grain = true, textBehind = setOf(PostSlots.HEADLINE), cutoutX = 0.5f, cutoutY = 0.84f, cutoutScale = 0.92f),
        ::behindLayout
    ),

    // 6. Cutout spotlight: the subject on a glowing gradient, headline behind, caption beside it. Needs a transparent PNG too.
    photoTemplate(
        "photo-spotlight", "Cutout spotlight",
        mapOf(
            PostSlots.EYEBROW to "NEW TUTOR",
            PostSlots.HEADLINE to "Ask better questions",
            PostSlots.BODY to "Live Q&A every Thursday. Bring your hardest problem."
        ),
        TemplateLook(mesh = MeshStyle.SPOTLIGHT, grain = true, textBehind = setOf(PostSlots.HEADLINE), cutoutX = 0.62f, cutoutY = 0.84f, cutoutScale = 0.9f),
        ::spotlightLayout
    ),

    // 7. Pill portrait: a stadium-shaped photo inside a gradient halo, name and title
    photoTemplate(
        "photo-pill", "Pill portrait",
        mapOf(
            PostSlots.EYEBROW to "MEET YOUR TUTOR",
            PostSlots.HEADLINE to "Dr. Selam Bekele",
            PostSlots.BODY to "Biology \u00B7 12 years of teaching"
        ),
        TemplateLook(frame = PhotoFrame.PILL),
        ::pillLayout
    )
)

private fun archLayout(size: PostSize): TemplateLayout {
    val story = size.phStory
    val wide = size.phWide
    val (ax, ay, aw, ah) = if (story) listOf(.15f, .15f, .70f, .40f) else if (wide) listOf(.10f, .17f, .31f, .67f) else listOf(.08f, .17f, .42f, .67f)
    val dx = 32f / size.width
    val dy = 32f / size.height
    val slots = if (story) listOf(
        phText(PostSlots.EYEBROW, .10f, .60f, .80f, .03f, .026f, PostFont.BOLD, PostTone.ACCENT, caps = true),
        phText(PostSlots.HEADLINE, .10f, .65f, .80f, .14f, .074f, PostFont.DISPLAY, PostTone.TEXT),
        phText(PostSlots.BODY, .10f, .80f, .80f, .07f, .034f, PostFont.REGULAR, PostTone.MUTED)
    ) else if (wide) listOf(
        phText(PostSlots.EYEBROW, .50f, .26f, .38f, .04f, .024f, PostFont.BOLD, PostTone.ACCENT, caps = true),
        phText(PostSlots.HEADLINE, .50f, .335f, .38f, .31f, .070f, PostFont.DISPLAY, PostTone.TEXT),
        phText(PostSlots.BODY, .50f, .68f, .38f, .16f, .032f, PostFont.REGULAR, PostTone.MUTED)
    ) else listOf(
        phText(PostSlots.EYEBROW, .56f, .24f, .36f, .04f, .024f, PostFont.BOLD, PostTone.ACCENT, caps = true),
        phText(PostSlots.HEADLINE, .56f, .32f, .36f, .32f, .060f, PostFont.DISPLAY, PostTone.TEXT),
        phText(PostSlots.BODY, .56f, .67f, .36f, .17f, .032f, PostFont.REGULAR, PostTone.MUTED)
    )
    val ruleX = if (story) .10f else if (wide) .50f else .56f
    val ruleY = if (story) .635f else if (wide) .312f else .295f
    val decor = phArch(size, ax + dx, ay + dy, aw, ah, PostTone.ACCENT) +
        phArch(size, ax, ay, aw, ah, PostTone.LIGHT) +
        phBar(ruleX, ruleY, .08f, .006f * PHU / size.height, PostTone.ACCENT)
    return TemplateLayout(
        PostBase.LIGHT,
        decor,
        slots,
        photo = PostPhotoArea(ax, ay, aw, ah, 20),
        backdrop = Backdrop(PostTone.TINT, PostTone.SOFT, 135f)
    )
}

private fun cardLayout(size: PostSize): TemplateLayout {
    val story = size.phStory
    val wide = size.phWide
    val (px, py, pw, ph) = if (story) listOf(.07f, .14f, .86f, .46f) else if (wide) listOf(.06f, .16f, .54f, .68f) else listOf(.07f, .15f, .86f, .50f)
    val (cx, cy, cw, ch) = if (story) listOf(.11f, .54f, .78f, .30f) else if (wide) listOf(.46f, .31f, .46f, .46f) else listOf(.12f, .55f, .76f, .31f)
    val slots = if (story) listOf(
        phText(PostSlots.EYEBROW, .15f, .565f, .70f, .025f, .024f, PostFont.BOLD, PostTone.ACCENT, caps = true),
        phText(PostSlots.HEADLINE, .15f, .60f, .70f, .13f, .056f, PostFont.DISPLAY, PostTone.TEXT),
        phText(PostSlots.BODY, .15f, .74f, .70f, .08f, .032f, PostFont.REGULAR, PostTone.MUTED)
    ) else if (wide) listOf(
        phText(PostSlots.EYEBROW, .50f, .35f, .38f, .04f, .022f, PostFont.BOLD, PostTone.ACCENT, caps = true),
        phText(PostSlots.HEADLINE, .50f, .40f, .38f, .20f, .054f, PostFont.DISPLAY, PostTone.TEXT),
        phText(PostSlots.BODY, .50f, .62f, .38f, .11f, .030f, PostFont.REGULAR, PostTone.MUTED)
    ) else listOf(
        phText(PostSlots.EYEBROW, .16f, .575f, .68f, .035f, .022f, PostFont.BOLD, PostTone.ACCENT, caps = true),
        phText(PostSlots.HEADLINE, .16f, .615f, .68f, .15f, .050f, PostFont.DISPLAY, PostTone.TEXT),
        phText(PostSlots.BODY, .16f, .77f, .68f, .075f, .028f, PostFont.REGULAR, PostTone.MUTED)
    )
    return TemplateLayout(
        PostBase.LIGHT,
        listOf(
            phBlock(px, py, pw, ph, .05f, PostTone.GLOW, 150, frameBack = 1),
            phBlock(cx, cy, cw, ch, .035f, PostTone.LIGHT, over = true, shadow = true)
        ),
        slots,
        photo = PostPhotoArea(px, py, pw, ph, 15),
        backdrop = Backdrop(PostTone.TINT, PostTone.SOFT, 135f)
    )
}

private fun stackLayout(size: PostSize): TemplateLayout {
    val story = size.phStory
    val wide = size.phWide
    val (px, py, pw, ph) = if (story) listOf(.14f, .15f, .72f, .32f) else if (wide) listOf(.08f, .20f, .40f, .60f) else listOf(.20f, .16f, .60f, .38f)
    val slots = if (story) listOf(
        phText(PostSlots.EYEBROW, .10f, .56f, .80f, .03f, .026f, PostFont.BOLD, PostTone.SECONDARY, centered = true, caps = true),
        phText(PostSlots.HEADLINE, .10f, .60f, .80f, .17f, .068f, PostFont.DISPLAY, PostTone.LIGHT, centered = true),
        phText(PostSlots.BODY, .10f, .78f, .80f, .07f, .034f, PostFont.REGULAR, PostTone.LIGHT_MUTED, centered = true)
    ) else if (wide) listOf(
        phText(PostSlots.EYEBROW, .56f, .30f, .36f, .04f, .024f, PostFont.BOLD, PostTone.SECONDARY, caps = true),
        phText(PostSlots.HEADLINE, .56f, .36f, .36f, .26f, .058f, PostFont.DISPLAY, PostTone.LIGHT),
        phText(PostSlots.BODY, .56f, .64f, .36f, .12f, .030f, PostFont.REGULAR, PostTone.LIGHT_MUTED)
    ) else listOf(
        phText(PostSlots.EYEBROW, .10f, .62f, .80f, .04f, .024f, PostFont.BOLD, PostTone.SECONDARY, centered = true, caps = true),
        phText(PostSlots.HEADLINE, .10f, .665f, .80f, .15f, .056f, PostFont.DISPLAY, PostTone.LIGHT, centered = true),
        phText(PostSlots.BODY, .10f, .82f, .80f, .05f, .030f, PostFont.REGULAR, PostTone.LIGHT_MUTED, centered = true)
    )
    return TemplateLayout(
        PostBase.PRIMARY,
        listOf(phBlock(px, py, pw, ph, .05f, PostTone.LIGHT, 60, frameBack = 1)),
        slots,
        photo = PostPhotoArea(px, py, pw, ph, 15),
        backdrop = Backdrop(PostTone.ACCENT, PostTone.DEEP, 140f)
    )
}

private fun duotoneLayout(size: PostSize): TemplateLayout {
    val story = size.phStory
    val wide = size.phWide
    val colW = if (wide) .70f else .84f
    val slots = if (story) listOf(
        phText(PostSlots.EYEBROW, .08f, .585f, .84f, .025f, .026f, PostFont.BOLD, PostTone.SECONDARY, caps = true),
        phText(PostSlots.HEADLINE, .08f, .62f, .84f, .19f, .088f, PostFont.DISPLAY, PostTone.LIGHT),
        phText(PostSlots.BODY, .08f, .83f, .84f, .04f, .036f, PostFont.REGULAR, PostTone.LIGHT_MUTED)
    ) else listOf(
        phText(PostSlots.EYEBROW, .08f, .50f, colW, .04f, .024f, PostFont.BOLD, PostTone.SECONDARY, caps = true),
        phText(PostSlots.HEADLINE, .08f, .55f, colW, .25f, .078f, PostFont.DISPLAY, PostTone.LIGHT),
        phText(PostSlots.BODY, .08f, .81f, colW, .06f, .032f, PostFont.REGULAR, PostTone.LIGHT_MUTED)
    )
    val ruleY = if (story) .565f else .485f
    return TemplateLayout(
        PostBase.DARK,
        listOf(
            // the brand-color wash that makes the photo duotone, then a fade so the words stay readable
            DecorBox(DecorShape.RECT, 0f, 0f, 1f, 1f, PostTone.ACCENT, 130, overPhoto = true, gradTo = PostTone.DEEP, alphaTo = 225, angle = 100f),
            DecorBox(DecorShape.RECT, 0f, .40f, 1f, .60f, PostTone.DARK, 0, overPhoto = true, gradTo = PostTone.DARK, alphaTo = 190, angle = 90f),
            phBar(.08f, ruleY, .10f, .006f * PHU / size.height, PostTone.SECONDARY, over = true)
        ),
        slots,
        photo = PostPhotoArea(0f, 0f, 1f, 1f, 40),
        backdrop = Backdrop(PostTone.DEEP, PostTone.PAGE, 150f)
    )
}

private fun behindLayout(size: PostSize): TemplateLayout {
    val story = size.phStory
    val wide = size.phWide
    val slots = if (story) listOf(
        phText(PostSlots.EYEBROW, .06f, .15f, .62f, .03f, .026f, PostFont.BOLD, PostTone.SECONDARY, caps = true),
        phText(PostSlots.HEADLINE, .04f, .19f, .92f, .30f, .20f, PostFont.DISPLAY, PostTone.LIGHT, centered = true, caps = true),
        phText(PostSlots.BODY, .08f, .85f, .84f, .03f, .034f, PostFont.BOLD, PostTone.LIGHT, centered = true)
    ) else listOf(
        phText(PostSlots.EYEBROW, .06f, .125f, if (wide) .50f else .62f, .04f, .024f, PostFont.BOLD, PostTone.SECONDARY, caps = true),
        phText(PostSlots.HEADLINE, .04f, .165f, .92f, .46f, .19f, PostFont.DISPLAY, PostTone.LIGHT, centered = true, caps = true),
        phText(PostSlots.BODY, if (wide) .15f else .10f, .845f, if (wide) .70f else .80f, .04f, .030f, PostFont.BOLD, PostTone.LIGHT, centered = true)
    )
    return TemplateLayout(
        PostBase.DARK,
        listOf(
            phGlow(size, .5f, .50f, .46f, PostTone.ACCENT, PostTone.GLOW, 190),
            phRing(size, .5f, .50f, .30f, PostTone.SECONDARY, 55, .006f)
        ),
        slots,
        backdrop = Backdrop(PostTone.DEEP, PostTone.PAGE, 160f)
    )
}

private fun spotlightLayout(size: PostSize): TemplateLayout {
    val story = size.phStory
    val wide = size.phWide
    val slots = if (story) listOf(
        phText(PostSlots.EYEBROW, .07f, .15f, .60f, .03f, .026f, PostFont.BOLD, PostTone.SECONDARY, caps = true),
        phText(PostSlots.HEADLINE, .07f, .19f, .86f, .20f, .100f, PostFont.DISPLAY, PostTone.LIGHT),
        phText(PostSlots.BODY, .08f, .85f, .84f, .03f, .034f, PostFont.REGULAR, PostTone.LIGHT)
    ) else if (wide) listOf(
        phText(PostSlots.EYEBROW, .07f, .16f, .40f, .04f, .024f, PostFont.BOLD, PostTone.SECONDARY, caps = true),
        phText(PostSlots.HEADLINE, .07f, .21f, .50f, .40f, .092f, PostFont.DISPLAY, PostTone.LIGHT),
        phText(PostSlots.BODY, .07f, .66f, .30f, .18f, .032f, PostFont.REGULAR, PostTone.LIGHT)
    ) else listOf(
        phText(PostSlots.EYEBROW, .07f, .16f, .50f, .04f, .024f, PostFont.BOLD, PostTone.SECONDARY, caps = true),
        phText(PostSlots.HEADLINE, .07f, .21f, .62f, .40f, .088f, PostFont.DISPLAY, PostTone.LIGHT),
        phText(PostSlots.BODY, .07f, .66f, .31f, .18f, .032f, PostFont.REGULAR, PostTone.LIGHT)
    )
    return TemplateLayout(
        PostBase.PRIMARY,
        listOf(
            phGlow(size, .62f, .46f, .40f, PostTone.GLOW, PostTone.ACCENT, 200),
            phRing(size, .62f, .46f, .30f, PostTone.SECONDARY, 70, .006f)
        ),
        slots,
        backdrop = Backdrop(PostTone.ACCENT, PostTone.DEEP, 150f)
    )
}

private fun pillLayout(size: PostSize): TemplateLayout {
    val story = size.phStory
    val wide = size.phWide
    val (px, py, pw, ph) = if (story) listOf(.18f, .15f, .64f, .40f) else if (wide) listOf(.12f, .17f, .28f, .66f) else listOf(.31f, .14f, .38f, .50f)
    val pad = 18f
    val hx = px - pad / size.width
    val hy = py - pad / size.height
    val hw = pw + 2f * pad / size.width
    val hh = ph + 2f * pad / size.height
    val slots = if (story) listOf(
        phText(PostSlots.EYEBROW, .10f, .60f, .80f, .03f, .026f, PostFont.BOLD, PostTone.ACCENT, centered = true, caps = true),
        phText(PostSlots.HEADLINE, .10f, .635f, .80f, .075f, .072f, PostFont.DISPLAY, PostTone.TEXT, centered = true),
        phText(PostSlots.BODY, .10f, .72f, .80f, .07f, .036f, PostFont.REGULAR, PostTone.MUTED, centered = true)
    ) else if (wide) listOf(
        phText(PostSlots.EYEBROW, .50f, .32f, .38f, .04f, .022f, PostFont.BOLD, PostTone.ACCENT, caps = true),
        phText(PostSlots.HEADLINE, .50f, .37f, .38f, .17f, .066f, PostFont.DISPLAY, PostTone.TEXT),
        phText(PostSlots.BODY, .50f, .56f, .38f, .12f, .034f, PostFont.REGULAR, PostTone.MUTED)
    ) else listOf(
        phText(PostSlots.EYEBROW, .10f, .69f, .80f, .035f, .022f, PostFont.BOLD, PostTone.ACCENT, centered = true, caps = true),
        phText(PostSlots.HEADLINE, .10f, .73f, .80f, .085f, .058f, PostFont.DISPLAY, PostTone.TEXT, centered = true),
        phText(PostSlots.BODY, .10f, .82f, .80f, .05f, .032f, PostFont.REGULAR, PostTone.MUTED, centered = true)
    )
    return TemplateLayout(
        PostBase.LIGHT,
        listOf(
            phGlow(size, px + pw / 2f, py + ph / 2f, .42f, PostTone.GLOW, PostTone.TINT, 150),
            phBlock(hx, hy, hw, hh, phHalfSide(size, hw, hh), PostTone.ACCENT, to = PostTone.GLOW, angle = 135f, frameBack = 1),
            phBlock(px, py, pw, ph, phHalfSide(size, pw, ph), PostTone.LIGHT, frameBack = 1)
        ),
        slots,
        photo = PostPhotoArea(px, py, pw, ph, 10),
        backdrop = Backdrop(PostTone.TINT, PostTone.SOFT, 150f)
    )
}
