package com.wisdomtower.post

/**
 * Phone mockup templates (family "Photo"): a raw phone screenshot inside a phone frame, ready to post.
 * Plan and open questions: docs/DEVICE_MOCKUP_PLAN.md (plan A: a fixed screen window).
 *
 * The owner's phone is a TECNO CAMON 30 whose screenshots are 1080 x 2436 (ratio 0.4433, about 9:20.3), so the screen window uses that ratio.
 * device-hero is BARE (TemplateLayout.bare): no logo box, no footer, no links, no text, just the phone on a plain dark backdrop,
 * like a product shot. device-headline keeps the header logo and the footer and adds a short headline.
 *
 * The phone is drawn from shapes: a thin light rim, a dark body (the bezel is the gap between body and screen) and side buttons.
 * The screenshot goes in the photo window, which has no dim layer. The screen corner comes from PhotoFrame.ROUNDED
 * (radius 5% of the shorter side = 54 px), so the template look sets that frame and the body radius is 54 px + the bezel.
 * If the person turns the frame off, the screen corners become square. No other renderer change is needed.
 * Same rules as the other template files: own placement for 1:1, 4:3 and 9:16 (4:5 falls into 1:1), text between 12% and 88%
 * of the height, every slot has a sample text, no DIAMOND, TRIANGLE or HEXAGON.
 */

private const val DV_BEZEL = 28f
private const val DV_RIM = 3f

/** Screenshot ratio of the TECNO CAMON 30: 1080 / 2436. */
private const val DV_SCREEN_RATIO = 1080f / 2436f

/** Screen corner radius is unit * 0.05 (PostEngine.framePath, ROUNDED); the body adds the bezel: (54 + 28) / 1080. */
private const val DV_SCREEN_RADIUS = 0.05f
private const val DV_BODY_RADIUS = 0.0759f
private const val DV_RIM_RADIUS = 0.0787f

private enum class DvShape { STORY, SQUARE, WIDE }

private fun PostSize.dvShape(): DvShape = when (this) {
    PostSize.STORY -> DvShape.STORY
    PostSize.LANDSCAPE, PostSize.WIDE -> DvShape.WIDE
    else -> DvShape.SQUARE
}

/** Body and screen rectangles as canvas fractions, plus the phone's centre. */
private class DvPhone(
    val bx: Float, val by: Float, val bw: Float, val bh: Float,
    val sx: Float, val sy: Float, val sw: Float, val sh: Float,
    val cx: Float, val cy: Float
)

/**
 * A phone whose body runs from topFrac to bottomFrac of the height, centred on cxFrac of the width.
 * The screen is the body minus the bezel on every side, and has exactly the screenshot ratio in pixels.
 */
private fun dvPhone(size: PostSize, cxFrac: Float, topFrac: Float, bottomFrac: Float): DvPhone {
    val w = size.width.toFloat()
    val h = size.height.toFloat()
    val top = topFrac * h
    val bodyH = bottomFrac * h - top
    val screenH = bodyH - 2f * DV_BEZEL
    val screenW = screenH * DV_SCREEN_RATIO
    val bodyW = screenW + 2f * DV_BEZEL
    val left = cxFrac * w - bodyW / 2f
    return DvPhone(
        left / w, top / h, bodyW / w, bodyH / h,
        (left + DV_BEZEL) / w, (top + DV_BEZEL) / h, screenW / w, screenH / h,
        cxFrac, (top + bodyH / 2f) / h
    )
}

private fun dvText(
    slot: String, x: Float, y: Float, w: Float, h: Float, size: Float, font: PostFont, tone: PostTone,
    centered: Boolean = false, caps: Boolean = false
) = SlotBox(slot, x, y, w, h, size, font, tone, centered, caps)

private fun dvBlock(
    x: Float, y: Float, w: Float, h: Float, radius: Float, tone: PostTone, alpha: Int = 255,
    to: PostTone? = null, shadow: Boolean = false, frameBack: Int = 0
) = DecorBox(DecorShape.ROUND, x, y, w, h, tone, alpha, radius, false, to, -1, 90f, 0f, shadow, frameBack)

private fun dvGlow(size: PostSize, cx: Float, cy: Float, rPx: Float, alpha: Int): DecorBox =
    DecorBox(
        DecorShape.OVAL,
        cx - rPx / size.width, cy - rPx / size.height, 2f * rPx / size.width, 2f * rPx / size.height,
        PostTone.ACCENT, alpha, gradTo = PostTone.GLOW, alphaTo = 0, angle = 45f
    )

/** Two buttons on the right edge (volume and power, like the owner's phone), half hidden by the body. */
private fun dvButtons(size: PostSize, p: DvPhone): List<DecorBox> {
    val w = size.width.toFloat()
    fun button(topShare: Float, lengthShare: Float): DecorBox =
        dvBlock(p.bx + p.bw - 3f / w, p.by + p.bh * topShare, 9f / w, p.bh * lengthShare, 0.004f, PostTone.SECONDARY, alpha = 80)
    return listOf(button(.20f, .06f), button(.30f, .11f))
}

/** glow = true adds a soft accent glow behind the phone; bare = true removes the logo box and the footer. */
private fun dvLayout(size: PostSize, p: DvPhone, slots: List<SlotBox>, glow: Boolean, bare: Boolean): TemplateLayout {
    val glowR = if (size.dvShape() == DvShape.STORY) 560f else 420f
    val w = size.width.toFloat()
    val h = size.height.toFloat()
    val rim = dvBlock(
        p.bx - DV_RIM / w, p.by - DV_RIM / h, p.bw + 2f * DV_RIM / w, p.bh + 2f * DV_RIM / h,
        DV_RIM_RADIUS, PostTone.SECONDARY, alpha = 70
    )
    val decor = (if (glow) listOf(dvGlow(size, p.cx, p.cy, glowR, 150)) else emptyList()) +
        dvButtons(size, p) +
        listOf(
            rim,
            dvBlock(p.bx, p.by, p.bw, p.bh, DV_BODY_RADIUS, PostTone.DARK, shadow = true),
            // what shows through while no screenshot is loaded; follows the chosen photo frame
            dvBlock(p.sx, p.sy, p.sw, p.sh, DV_SCREEN_RADIUS, PostTone.DEEP, to = PostTone.DARK, frameBack = 1)
        )
    return TemplateLayout(
        PostBase.DARK, decor, slots,
        photo = PostPhotoArea(p.sx, p.sy, p.sw, p.sh, 0),
        backdrop = Backdrop(PostTone.DEEP, PostTone.DARK, 120f),
        bare = bare
    )
}

private fun dvTemplate(
    id: String, name: String, samples: Map<String, String>, build: (PostSize) -> TemplateLayout
) = PostTemplateSpec(id, name, "Photo", samples, TemplateLook(frame = PhotoFrame.ROUNDED)) { size ->
    build(size).copy(clean = true)
}

internal val DeviceTemplates: List<PostTemplateSpec> = listOf(
    dvTemplate("device-hero", "Phone mockup", emptyMap(), ::deviceHeroLayout),
    dvTemplate(
        "device-headline", "Phone and headline",
        mapOf(
            PostSlots.EYEBROW to "NEW IN THE APP",
            PostSlots.HEADLINE to "Every ECE course, one tap away"
        ),
        ::deviceHeadlineLayout
    )
)

/** Hero: the biggest phone that fits, centred, nothing else on the post (no logo, no footer, no text). */
private fun deviceHeroLayout(size: PostSize): TemplateLayout {
    val p = if (size.dvShape() == DvShape.STORY) dvPhone(size, .50f, .09f, .91f) else dvPhone(size, .50f, .08f, .92f)
    return dvLayout(size, p, emptyList(), glow = false, bare = true)
}

/** Headline: on 9:16 the words sit above a smaller phone; on 1:1, 4:5 and 4:3 the words sit left and the phone right. */
private fun deviceHeadlineLayout(size: PostSize): TemplateLayout = when (size.dvShape()) {
    DvShape.STORY -> dvLayout(
        size, dvPhone(size, .50f, .292f, .88f),
        listOf(
            dvText(PostSlots.EYEBROW, .08f, .172f, .84f, .028f, .025f, PostFont.BOLD, PostTone.SECONDARY, centered = true, caps = true),
            dvText(PostSlots.HEADLINE, .08f, .205f, .84f, .082f, .056f, PostFont.DISPLAY, PostTone.LIGHT, centered = true)
        ),
        glow = true, bare = false
    )
    DvShape.SQUARE -> dvLayout(
        size, dvPhone(size, .735f, .20f, .84f),
        listOf(
            dvText(PostSlots.EYEBROW, .07f, .33f, .46f, .035f, .024f, PostFont.BOLD, PostTone.SECONDARY, caps = true),
            dvText(PostSlots.HEADLINE, .07f, .375f, .46f, .34f, .060f, PostFont.DISPLAY, PostTone.LIGHT)
        ),
        glow = true, bare = false
    )
    DvShape.WIDE -> dvLayout(
        size, dvPhone(size, .74f, .16f, .86f),
        listOf(
            dvText(PostSlots.EYEBROW, .07f, .30f, .40f, .04f, .024f, PostFont.BOLD, PostTone.SECONDARY, caps = true),
            dvText(PostSlots.HEADLINE, .07f, .36f, .40f, .34f, .062f, PostFont.DISPLAY, PostTone.LIGHT)
        ),
        glow = true, bare = false
    )
}
