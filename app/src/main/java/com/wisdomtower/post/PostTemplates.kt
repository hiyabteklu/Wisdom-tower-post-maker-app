package com.wisdomtower.post

enum class PostFont { DISPLAY, BOLD, REGULAR }

/**
 * Where a color comes from. ACCENT is the brand primary (or the post's accent override), TEXT the dark brand color.
 * LIGHT and SECONDARY are for text and shapes that sit on a primary or dark background.
 */
enum class PostTone {
    ACCENT, TEXT, MUTED, LIGHT, LIGHT_MUTED, SECONDARY, DARK, BLUE, RED,

    /** Derived colors, built from the brand so every gradient follows a color change: DEEP is the accent pulled toward dark,
     * GLOW the accent pulled toward the second color, TINT a whisper of accent on the light color, SOFT the light color
     * pulled toward the second color, PAGE the color at the bottom of the background. */
    DEEP, GLOW, TINT, SOFT, PAGE
}

/** The flat color under everything else. PRIMARY and DARK templates use LIGHT / SECONDARY tones for their text. */
enum class PostBase { LIGHT, PRIMARY, DARK }

/**
 * RING is an outline circle, DOTS a dot grid, STRIPES diagonal lines, WAVE a rolling edge,
 * DIAMOND a tilted square, TRIANGLE a triangle pointing up, HEXAGON a six-sided shape.
 */
enum class DecorShape { RECT, ROUND, OVAL, RING, DOTS, STRIPES, WAVE, DIAMOND, TRIANGLE, HEXAGON }

/** How a list slot marks each line. ROW splits "Mon: Physics" into a bold label column and a text column with a rule between rows. */
enum class ListStyle { NONE, DOT, LETTER, NUMBER, CHECK, ROW }

/**
 * One text slot of a template. x, y, w, h are fractions of the canvas width and height.
 * size is a fraction of the shorter canvas side, so type looks the same weight on every ratio.
 * literal is fixed text (a quote mark, the word ANSWER) that the user never edits.
 * Content must stay between 12% and 88% of the height: the logo and the footer own the rest.
 */
data class SlotBox(
    val slot: String,
    val x: Float,
    val y: Float,
    val w: Float,
    val h: Float,
    val size: Float,
    val font: PostFont,
    val tone: PostTone,
    val centered: Boolean = false,
    val caps: Boolean = false,
    val list: ListStyle = ListStyle.NONE,
    val literal: String? = null
)

/** A flat shape. x, y, w, h are canvas fractions. radius is a fraction of the shorter side (ROUND only). */
data class DecorBox(
    val shape: DecorShape,
    val x: Float,
    val y: Float,
    val w: Float,
    val h: Float,
    val tone: PostTone,
    val alpha: Int = 255,
    val radius: Float = 0f,
    val overPhoto: Boolean = false,
    /** When set, the shape fades from tone/alpha to gradTo/alphaTo (alphaTo -1 keeps alpha) along angle (0 = left to right, 90 = top to bottom). */
    val gradTo: PostTone? = null,
    val alphaTo: Int = -1,
    val angle: Float = 90f,
    /** Line width for RING, as a fraction of the shorter side. */
    val stroke: Float = 0f,
    val shadow: Boolean = false
)

/** Where the post photo is drawn (canvas fractions) and how dark the dim layer on top of it is, 0 to 255. */
enum class PhotoMask { RECT, PENTAGON }

data class PostPhotoArea(
    val x: Float, val y: Float, val w: Float, val h: Float, val dim: Int,
    val mask: PhotoMask = PhotoMask.RECT
)

/** The gradient behind everything. angle: 0 = left to right, 90 = top to bottom. */
data class Backdrop(val from: PostTone, val to: PostTone, val angle: Float = 135f, val mid: PostTone? = null)

data class TemplateLayout(
    val base: PostBase,
    val decor: List<DecorBox>,
    val slots: List<SlotBox>,
    val photo: PostPhotoArea? = null,
    val footerLight: Boolean = base != PostBase.LIGHT,
    val backdrop: Backdrop? = null,
    /** When set, the Brand's social icons are drawn big in this area, and the small footer is left out. */
    val social: SocialArea? = null
)

/** GRID: icon cards in up to two columns. LIST: one wide row per icon. */
enum class SocialStyle { GRID, LIST }

/** Where the Brand's footer items are drawn as large icon cards (canvas fractions). onDark picks light text and glass cards. */
data class SocialArea(
    val x: Float,
    val y: Float,
    val w: Float,
    val h: Float,
    val style: SocialStyle,
    val onDark: Boolean
)

/** A template is data: a layout function per size, plus sample text for every slot it uses. */
data class PostTemplateSpec(
    val id: String,
    val name: String,
    val family: String,
    val samples: Map<String, String>,
    val layout: (PostSize) -> TemplateLayout
)

fun postTemplate(id: String): PostTemplateSpec = PostTemplates.firstOrNull { it.id == id } ?: PostTemplates.first()

/** Switches template and keeps every word already typed. Slots the new template needs that are still missing get its sample text. */
fun PostDoc.withTemplate(spec: PostTemplateSpec): PostDoc = copy(templateId = spec.id, texts = spec.samples + texts)

/**
 * Helper that lays a template out in a content band between the logo and the footer.
 * x and w are fractions of a centered column, y and h are fractions of the band, so one description works on all three sizes.
 * abox and the DecorBox helpers that say "canvas" take real canvas fractions when a template needs an exact position.
 */
private class Frame(val size: PostSize) {
    val story = size == PostSize.STORY
    val wide = size == PostSize.LANDSCAPE
    val W = size.width.toFloat()
    val H = size.height.toFloat()
    val U = 1080f
    val top = if (story) 0.14f else 0.18f
    val bottom = if (story) 0.87f else 0.86f
    private val colX = if (wide) 0.14f else 0.08f
    private val colW = if (wide) 0.72f else 0.84f
    private val k = if (story) 1.12f else 1f

    fun fx(v: Float) = colX + colW * v
    fun fw(v: Float) = colW * v
    fun fy(v: Float) = top + (bottom - top) * v
    fun fh(v: Float) = (bottom - top) * v

    fun box(
        slot: String, x: Float, y: Float, w: Float, h: Float, sz: Float, font: PostFont, tone: PostTone,
        centered: Boolean = false, caps: Boolean = false, list: ListStyle = ListStyle.NONE
    ) = SlotBox(slot, fx(x), fy(y), fw(w), fh(h), sz * k, font, tone, centered, caps, list)

    /** Same as box, but y and h are canvas fractions. */
    fun abox(
        slot: String, x: Float, ay: Float, w: Float, ah: Float, sz: Float, font: PostFont, tone: PostTone,
        centered: Boolean = false, caps: Boolean = false, list: ListStyle = ListStyle.NONE
    ) = SlotBox(slot, fx(x), ay, fw(w), ah, sz * k, font, tone, centered, caps, list)

    fun mark(text: String, x: Float, y: Float, w: Float, h: Float, sz: Float, font: PostFont, tone: PostTone, centered: Boolean = false) =
        SlotBox("", fx(x), fy(y), fw(w), fh(h), sz * k, font, tone, centered, false, ListStyle.NONE, text)

    fun rect(x: Float, y: Float, w: Float, h: Float, tone: PostTone, alpha: Int = 255, over: Boolean = false) =
        DecorBox(DecorShape.RECT, fx(x), fy(y), fw(w), fh(h), tone, alpha, 0f, over)

    fun card(x: Float, y: Float, w: Float, h: Float, tone: PostTone, alpha: Int = 255, radius: Float = 0.03f, over: Boolean = false) =
        DecorBox(DecorShape.ROUND, fx(x), fy(y), fw(w), fh(h), tone, alpha, radius, over)

    /** A card that floats: white-ish fill with a soft shadow. */
    fun lift(x: Float, y: Float, w: Float, h: Float, tone: PostTone = PostTone.LIGHT, radius: Float = 0.03f) =
        DecorBox(DecorShape.ROUND, fx(x), fy(y), fw(w), fh(h), tone, 255, radius, shadow = true)

    /** Thin horizontal rule. thick is a fraction of the shorter side. */
    fun rule(x: Float, y: Float, w: Float, tone: PostTone, thick: Float = 0.007f) =
        DecorBox(DecorShape.RECT, fx(x), fy(y), fw(w), thick * U / H, tone)

    /** Thin vertical bar. thick is a fraction of the shorter side. */
    fun vbar(x: Float, y: Float, h: Float, tone: PostTone, thick: Float = 0.012f) =
        DecorBox(DecorShape.RECT, fx(x), fy(y), thick * U / W, fh(h), tone)

    /** Full-width horizontal band between two canvas heights. */
    fun band(y0: Float, y1: Float, tone: PostTone, alpha: Int = 255) =
        DecorBox(DecorShape.RECT, 0f, y0, 1f, y1 - y0, tone, alpha)

    /** A circle centered on column/band coordinates; r is a fraction of the shorter side. */
    fun disc(cx: Float, cy: Float, r: Float, tone: PostTone, alpha: Int = 255): DecorBox {
        val rx = r * U / W
        val ry = r * U / H
        return DecorBox(DecorShape.OVAL, fx(cx) - rx, fy(cy) - ry, 2f * rx, 2f * ry, tone, alpha)
    }

    /** An outline circle centered on canvas fractions; r and stroke are fractions of the shorter side. */
    fun ringAt(cx: Float, cy: Float, r: Float, tone: PostTone, alpha: Int = 255, stroke: Float = 0.008f, over: Boolean = false) =
        DecorBox(DecorShape.RING, cx - r * U / W, cy - r * U / H, 2f * r * U / W, 2f * r * U / H, tone, alpha, overPhoto = over, stroke = stroke)

    /** A soft circle of light centered on canvas fractions: starts at tone and fades out toward the edge color. */
    fun glow(cx: Float, cy: Float, r: Float, tone: PostTone, to: PostTone, alpha: Int = 200) =
        DecorBox(DecorShape.OVAL, cx - r * U / W, cy - r * U / H, 2f * r * U / W, 2f * r * U / H, tone, alpha, gradTo = to, alphaTo = 0, angle = 45f)

    /** A full-width fade over a photo, so text stays readable. y and h are canvas fractions. */
    fun fade(y: Float, h: Float, tone: PostTone, from: Int, to: Int) =
        DecorBox(DecorShape.RECT, 0f, y, 1f, h, tone, from, overPhoto = true, gradTo = tone, alphaTo = to, angle = 90f)

    private fun gem(shape: DecorShape, cx: Float, cy: Float, size: Float, tone: PostTone, alpha: Int) =
        DecorBox(shape, cx - size * U / W / 2f, cy - size * U / H / 2f, size * U / W, size * U / H, tone, alpha)

    /** A small diamond at canvas fractions; size is a fraction of the shorter side. */
    fun dia(cx: Float, cy: Float, size: Float, tone: PostTone, alpha: Int = 255) = gem(DecorShape.DIAMOND, cx, cy, size, tone, alpha)

    /** A small triangle at canvas fractions; size is a fraction of the shorter side. */
    fun tri(cx: Float, cy: Float, size: Float, tone: PostTone, alpha: Int = 255) = gem(DecorShape.TRIANGLE, cx, cy, size, tone, alpha)

    /** A small hexagon at canvas fractions; size is a fraction of the shorter side. */
    fun hex(cx: Float, cy: Float, size: Float, tone: PostTone, alpha: Int = 255) = gem(DecorShape.HEXAGON, cx, cy, size, tone, alpha)
}

private fun template(
    id: String,
    name: String,
    family: String,
    samples: Map<String, String>,
    build: Frame.() -> TemplateLayout
) = PostTemplateSpec(id, name, family, samples) { size -> PostLooks.enrich(id, size, Frame(size).build()) }

/**
 * Layouts in families. Two templates of one family differ in structure, not only in color.
 * Decoration hangs on fixed things (the label row, the number, the card) and never on the end of a headline,
 * because a headline can run one line or four.
 * The bold two-color templates at the end use TEXT as the ink (big words) and ACCENT as the pop (bands, rings),
 * so the Color pairs buttons in the studio restyle them in one tap.
 */
val PostTemplates: List<PostTemplateSpec> = listOf(

    // 1. Quote: centered, ring behind, rule above
    template(
        "quote-center", "Centered quote", "Quote",
        mapOf(
            PostSlots.EYEBROW to "TODAY'S THOUGHT",
            PostSlots.HEADLINE to "Small steps still move you forward.",
            PostSlots.BODY to "Progress is built by showing up consistently."
        )
    ) {
        fun eyebrow(y: Float, h: Float) = SlotBox(PostSlots.EYEBROW, .08f, y, .84f, h, .028f, PostFont.BOLD, PostTone.ACCENT, centered = true, caps = true)
        fun headline(y: Float, h: Float, sz: Float) = SlotBox(PostSlots.HEADLINE, .08f, y, .84f, h, sz, PostFont.DISPLAY, PostTone.TEXT, centered = true)
        fun body(y: Float, h: Float, sz: Float) = SlotBox(PostSlots.BODY, .08f, y, .84f, h, sz, PostFont.REGULAR, PostTone.MUTED, centered = true)
        val slots = when (size) {
            PostSize.SQUARE -> listOf(eyebrow(.22f, .05f), headline(.30f, .32f, .084f), body(.65f, .18f, .04f))
            PostSize.LANDSCAPE -> listOf(eyebrow(.21f, .06f), headline(.31f, .32f, .084f), body(.66f, .17f, .038f))
            PostSize.STORY -> listOf(eyebrow(.28f, .03f), headline(.34f, .24f, .09f), body(.62f, .14f, .042f))
        }
        val ruleY = slots.first().y - .03f
        TemplateLayout(
            PostBase.LIGHT,
            listOf(
                ringAt(.5f, .5f, .36f, PostTone.ACCENT, 38, .008f),
                ringAt(.5f, .5f, .43f, PostTone.ACCENT, 20, .004f),
                DecorBox(DecorShape.RECT, .46f, ruleY, .08f, .006f * U / H, PostTone.ACCENT)
            ),
            slots,
            backdrop = Backdrop(PostTone.TINT, PostTone.SOFT, 135f)
        )
    },

    // 2. Quote: big mark, left aligned with a vertical bar
    template(
        "quote-bar", "Quote with side bar", "Quote",
        mapOf(
            PostSlots.EYEBROW to "STUDY QUOTE",
            PostSlots.HEADLINE to "Learn something new every single day.",
            PostSlots.BODY to "A little each day adds up to a lot."
        )
    ) {
        TemplateLayout(
            PostBase.LIGHT,
            listOf(
                vbar(0f, .02f, .94f, PostTone.ACCENT, .016f),
                DecorBox(DecorShape.STRIPES, fx(.86f), fy(0f), fw(.12f), fw(.12f) * W / H, PostTone.ACCENT, 35)
            ),
            listOf(
                mark("\u201C", .07f, -.01f, .4f, .21f, .2f, PostFont.DISPLAY, PostTone.ACCENT),
                box(PostSlots.EYEBROW, .07f, .20f, .93f, .06f, .028f, PostFont.BOLD, PostTone.ACCENT, caps = true),
                box(PostSlots.HEADLINE, .07f, .27f, .93f, .45f, .076f, PostFont.DISPLAY, PostTone.TEXT),
                box(PostSlots.BODY, .07f, .75f, .93f, .22f, .04f, PostFont.REGULAR, PostTone.MUTED)
            ),
            backdrop = Backdrop(PostTone.TINT, PostTone.SOFT, 120f)
        )
    },

    // 3. Quote: big quote mark over a dimmed photo, fade under the words
    template(
        "quote-photo", "Quote on photo", "Quote",
        mapOf(
            PostSlots.EYEBROW to "FROM THE CLASSROOM",
            PostSlots.HEADLINE to "The expert in anything was once a beginner.",
            PostSlots.BODY to "Keep going."
        )
    ) {
        TemplateLayout(
            PostBase.DARK,
            listOf(fade(.30f, .70f, PostTone.DARK, 0, 200)),
            listOf(
                mark("\u201C", 0f, 0f, .4f, .24f, .2f, PostFont.DISPLAY, PostTone.SECONDARY),
                box(PostSlots.EYEBROW, 0f, .24f, 1f, .06f, .028f, PostFont.BOLD, PostTone.SECONDARY, caps = true),
                box(PostSlots.HEADLINE, 0f, .32f, 1f, .42f, .074f, PostFont.DISPLAY, PostTone.LIGHT),
                box(PostSlots.BODY, 0f, .77f, 1f, .20f, .038f, PostFont.REGULAR, PostTone.LIGHT_MUTED)
            ),
            PostPhotoArea(0f, 0f, 1f, 1f, 120)
        )
    },

    // 4. Motivation: one loud statement on a brand gradient
    template(
        "motivation-bold", "Bold statement", "Motivation",
        mapOf(
            PostSlots.EYEBROW to "MOTIVATION",
            PostSlots.HEADLINE to "Discipline beats motivation every time.",
            PostSlots.BODY to "Show up today, even for twenty minutes."
        )
    ) {
        TemplateLayout(
            PostBase.PRIMARY,
            listOf(
                ringAt(.92f, .80f, .42f, PostTone.SECONDARY, 45, .02f),
                dia(fx(.93f), fy(.02f), .06f, PostTone.SECONDARY),
                rule(0f, .075f, .18f, PostTone.SECONDARY, .010f)
            ),
            listOf(
                box(PostSlots.EYEBROW, 0f, 0f, .8f, .06f, .028f, PostFont.BOLD, PostTone.SECONDARY, caps = true),
                box(PostSlots.HEADLINE, 0f, .11f, 1f, .62f, .104f, PostFont.DISPLAY, PostTone.LIGHT),
                box(PostSlots.BODY, 0f, .80f, 1f, .18f, .04f, PostFont.REGULAR, PostTone.LIGHT_MUTED)
            ),
            backdrop = Backdrop(PostTone.ACCENT, PostTone.DEEP, 140f)
        )
    },

    // 5. Motivation: light top for the headline, rolling wave of brand color for the message
    template(
        "motivation-split", "Split color", "Motivation",
        mapOf(
            PostSlots.EYEBROW to "KEEP GOING",
            PostSlots.HEADLINE to "Hard work today. Results tomorrow.",
            PostSlots.BODY to "Every page you read and every question you solve moves you closer to your goal."
        )
    ) {
        val split = if (story) .55f else .56f
        val eyY = top + .01f
        val eyH = if (story) .03f else .05f
        val headY = top + (if (story) .05f else .075f)
        val bodyY = split + (if (story) .06f else .07f)
        TemplateLayout(
            PostBase.LIGHT,
            listOf(
                DecorBox(DecorShape.WAVE, 0f, split - .03f, 1f, 1f - split + .03f, PostTone.GLOW, 120),
                DecorBox(DecorShape.WAVE, 0f, split, 1f, 1f - split, PostTone.ACCENT, 255, gradTo = PostTone.DEEP, angle = 90f)
            ),
            listOf(
                abox(PostSlots.EYEBROW, 0f, eyY, 1f, eyH, .028f, PostFont.BOLD, PostTone.ACCENT, caps = true),
                abox(PostSlots.HEADLINE, 0f, headY, 1f, split - .04f - headY, .084f, PostFont.DISPLAY, PostTone.TEXT),
                abox(PostSlots.BODY, 0f, bodyY, 1f, bottom - bodyY, .042f, PostFont.REGULAR, PostTone.LIGHT)
            ),
            footerLight = true
        )
    },

    // 6. Question: question on top, four lettered options on a floating card
    template(
        "mcq-four", "MCQ, four options", "Question",
        mapOf(
            PostSlots.EYEBROW to "QUESTION 1",
            PostSlots.HEADLINE to "Which organelle is called the powerhouse of the cell?",
            PostSlots.LIST to "Nucleus\nMitochondrion\nRibosome\nGolgi apparatus"
        )
    ) {
        TemplateLayout(
            PostBase.LIGHT,
            listOf(
                glow(.95f, .15f, .35f, PostTone.ACCENT, PostTone.GLOW, 70),
                lift(0f, .41f, 1f, .57f, radius = .035f),
                vbar(0f, .41f, .57f, PostTone.ACCENT, .014f)
            ),
            listOf(
                box(PostSlots.EYEBROW, 0f, 0f, 1f, .06f, .028f, PostFont.BOLD, PostTone.ACCENT, caps = true),
                box(PostSlots.HEADLINE, 0f, .08f, 1f, .30f, .058f, PostFont.DISPLAY, PostTone.TEXT),
                box(PostSlots.LIST, .06f, .46f, .88f, .48f, .044f, PostFont.BOLD, PostTone.TEXT, list = ListStyle.LETTER)
            ),
            backdrop = Backdrop(PostTone.TINT, PostTone.SOFT, 135f)
        )
    },

    // 7. Question: question above, answer on a gradient card
    template(
        "question-answer", "Question and answer", "Question",
        mapOf(
            PostSlots.EYEBROW to "QUICK QUESTION",
            PostSlots.HEADLINE to "What is the derivative of sin(x)?",
            PostSlots.BODY to "cos(x)"
        )
    ) {
        TemplateLayout(
            PostBase.LIGHT,
            listOf(
                DecorBox(DecorShape.ROUND, fx(0f), fy(.56f), fw(1f), fh(.42f), PostTone.ACCENT, 255, .035f, gradTo = PostTone.DEEP, angle = 125f, shadow = true),
                dia(fx(.92f), fy(.63f), .05f, PostTone.SECONDARY, 200),
                tri(fx(.06f), fy(.90f), .03f, PostTone.SECONDARY, 140)
            ),
            listOf(
                box(PostSlots.EYEBROW, 0f, 0f, 1f, .06f, .028f, PostFont.BOLD, PostTone.ACCENT, centered = true, caps = true),
                box(PostSlots.HEADLINE, 0f, .09f, 1f, .43f, .066f, PostFont.DISPLAY, PostTone.TEXT, centered = true),
                mark("ANSWER", .06f, .61f, .88f, .06f, .026f, PostFont.BOLD, PostTone.SECONDARY, centered = true),
                box(PostSlots.BODY, .06f, .69f, .88f, .26f, .048f, PostFont.DISPLAY, PostTone.LIGHT, centered = true)
            ),
            footerLight = false,
            backdrop = Backdrop(PostTone.TINT, PostTone.SOFT, 135f)
        )
    },

    // 8. Exam: the date is the post, inside a ring
    template(
        "exam-date", "Exam date banner", "Exam",
        mapOf(
            PostSlots.EYEBROW to "EXAM DATE",
            PostSlots.NUMBER to "12 NOV",
            PostSlots.HEADLINE to "Final exam: General Psychology",
            PostSlots.BODY to "Revise the chapters, practise past questions and sleep well."
        )
    ) {
        TemplateLayout(
            PostBase.PRIMARY,
            listOf(
                ringAt(.5f, fy(.27f), .30f, PostTone.SECONDARY, 55, .006f),
                ringAt(.5f, fy(.27f), .36f, PostTone.SECONDARY, 28, .004f),
                rule(.4f, .50f, .2f, PostTone.SECONDARY, .008f)
            ),
            listOf(
                box(PostSlots.EYEBROW, 0f, 0f, 1f, .06f, .028f, PostFont.BOLD, PostTone.SECONDARY, centered = true, caps = true),
                box(PostSlots.NUMBER, 0f, .08f, 1f, .38f, .18f, PostFont.DISPLAY, PostTone.LIGHT, centered = true),
                box(PostSlots.HEADLINE, 0f, .56f, 1f, .22f, .064f, PostFont.DISPLAY, PostTone.LIGHT, centered = true),
                box(PostSlots.BODY, 0f, .80f, 1f, .18f, .038f, PostFont.REGULAR, PostTone.LIGHT_MUTED, centered = true)
            ),
            backdrop = Backdrop(PostTone.ACCENT, PostTone.DEEP, 140f)
        )
    },

    // 9. Exam: days left inside a big gradient circle
    template(
        "exam-countdown", "Countdown number", "Exam",
        mapOf(
            PostSlots.NUMBER to "45",
            PostSlots.EYEBROW to "DAYS LEFT",
            PostSlots.HEADLINE to "Until your final exam",
            PostSlots.BODY to "Plan every day, one topic at a time."
        )
    ) {
        val r = if (story) .27f else .235f
        val cx = fx(.5f)
        val cy = fy(if (story) .27f else .32f)
        val rx = r * U / W
        val ry = r * U / H
        val ns = if (story) .24f else .22f
        val numberH = ns * U * 1.6f / H
        val below = cy + ry
        val eyY = below + .02f
        val eyH = if (story) .03f else .045f
        val headY = eyY + eyH + .012f
        val headH = if (story) .19f else bottom - headY
        val slots = mutableListOf(
            SlotBox(PostSlots.NUMBER, cx - rx, cy - numberH / 2f, 2f * rx, numberH, ns, PostFont.DISPLAY, PostTone.LIGHT, centered = true),
            abox(PostSlots.EYEBROW, 0f, eyY, 1f, eyH, .032f, PostFont.BOLD, PostTone.ACCENT, centered = true, caps = true),
            abox(PostSlots.HEADLINE, 0f, headY, 1f, headH, .058f, PostFont.DISPLAY, PostTone.TEXT, centered = true)
        )
        if (story) {
            val bodyY = headY + headH + .01f
            slots.add(abox(PostSlots.BODY, 0f, bodyY, 1f, bottom - bodyY, .038f, PostFont.REGULAR, PostTone.MUTED, centered = true))
        }
        TemplateLayout(
            PostBase.LIGHT,
            listOf(
                ringAt(cx, cy, r * 1.16f, PostTone.ACCENT, 110, .006f),
                ringAt(cx, cy, r * 1.30f, PostTone.ACCENT, 50, .004f),
                DecorBox(DecorShape.OVAL, cx - rx, cy - ry, 2f * rx, 2f * ry, PostTone.ACCENT, 255, gradTo = PostTone.DEEP, angle = 135f, shadow = true),
                dia(cx + rx * 1.25f, cy - ry * 0.95f, .04f, PostTone.ACCENT),
                tri(cx - rx * 1.30f, cy + ry * 0.70f, .025f, PostTone.ACCENT, 160)
            ),
            slots,
            backdrop = Backdrop(PostTone.TINT, PostTone.SOFT, 135f)
        )
    },

    // 10. Study tip: giant tip number, stripe block, then the tip
    template(
        "tip-numbered", "Numbered tip", "Study tip",
        mapOf(
            PostSlots.EYEBROW to "STUDY TIP",
            PostSlots.NUMBER to "01",
            PostSlots.HEADLINE to "Test yourself instead of rereading.",
            PostSlots.BODY to "Close the book and write down what you remember. Then check what you missed."
        )
    ) {
        TemplateLayout(
            PostBase.LIGHT,
            listOf(
                DecorBox(DecorShape.STRIPES, fx(.74f), fy(0f), fw(.26f), fw(.26f) * W / H, PostTone.ACCENT, 90),
                rule(0f, .50f, .14f, PostTone.ACCENT, .008f)
            ),
            listOf(
                box(PostSlots.EYEBROW, 0f, 0f, .7f, .06f, .028f, PostFont.BOLD, PostTone.ACCENT, caps = true),
                box(PostSlots.NUMBER, 0f, .06f, 1f, .42f, .21f, PostFont.DISPLAY, PostTone.ACCENT),
                box(PostSlots.HEADLINE, 0f, .54f, 1f, .26f, .066f, PostFont.DISPLAY, PostTone.TEXT),
                box(PostSlots.BODY, 0f, .81f, 1f, .17f, .036f, PostFont.REGULAR, PostTone.MUTED)
            ),
            backdrop = Backdrop(PostTone.TINT, PostTone.SOFT, 135f)
        )
    },

    // 11. Study tip: a floating note card with key points on a brand gradient
    template(
        "tip-card", "Note card", "Study tip",
        mapOf(
            PostSlots.EYEBROW to "QUICK NOTES",
            PostSlots.HEADLINE to "Three ways to remember more",
            PostSlots.LIST to "Teach it to someone else\nSpace your revision over days\nSleep after studying"
        )
    ) {
        TemplateLayout(
            PostBase.PRIMARY,
            listOf(
                lift(0f, .02f, 1f, .94f, radius = .035f),
                hex(fx(.90f), fy(.095f), .05f, PostTone.ACCENT),
                rule(.07f, .36f, .12f, PostTone.ACCENT, .008f)
            ),
            listOf(
                box(PostSlots.EYEBROW, .07f, .07f, .70f, .05f, .026f, PostFont.BOLD, PostTone.ACCENT, caps = true),
                box(PostSlots.HEADLINE, .07f, .13f, .86f, .21f, .06f, PostFont.DISPLAY, PostTone.TEXT),
                box(PostSlots.LIST, .07f, .40f, .86f, .52f, .04f, PostFont.REGULAR, PostTone.TEXT, list = ListStyle.DOT)
            ),
            footerLight = true,
            backdrop = Backdrop(PostTone.ACCENT, PostTone.DEEP, 140f)
        )
    },

    // 12. Subject: photo across the top with a wavy edge, title below
    template(
        "subject-cover", "Subject cover, photo on top", "Subject",
        mapOf(
            PostSlots.EYEBROW to "COURSE NOTES",
            PostSlots.HEADLINE to "General Psychology",
            PostSlots.BODY to "Short notes, flashcards and practice questions."
        )
    ) {
        val cover = if (story) .40f else .45f
        val eyY = cover + (if (story) .04f else .05f)
        val eyH = if (story) .03f else .045f
        val headY = eyY + eyH + .01f
        val headH = if (story) .22f else .22f
        val bodyY = headY + headH + .01f
        TemplateLayout(
            PostBase.LIGHT,
            listOf(
                DecorBox(DecorShape.RECT, 0f, 0f, 1f, cover, PostTone.ACCENT, 255, gradTo = PostTone.DEEP, angle = 135f),
                ringAt(.86f, cover * .45f, .16f, PostTone.LIGHT, 70, .008f, over = true),
                DecorBox(DecorShape.WAVE, 0f, cover - .045f, 1f, .06f, PostTone.LIGHT, overPhoto = true)
            ),
            listOf(
                abox(PostSlots.EYEBROW, 0f, eyY, 1f, eyH, .028f, PostFont.BOLD, PostTone.ACCENT, caps = true),
                abox(PostSlots.HEADLINE, 0f, headY, 1f, headH, .08f, PostFont.DISPLAY, PostTone.TEXT),
                abox(PostSlots.BODY, 0f, bodyY, 1f, bottom - bodyY, .036f, PostFont.REGULAR, PostTone.MUTED)
            ),
            PostPhotoArea(0f, 0f, 1f, cover, 60, PhotoMask.PENTAGON)
        )
    },

    // 13. Subject: asymmetric editorial title with a pentagonal photo window
    template(
        "subject-badge", "Course feature with photo", "Subject",
        mapOf(
            PostSlots.NUMBER to "PSYC 1011",
            PostSlots.HEADLINE to "General Psychology",
            PostSlots.BODY to "Chapter 1: The Essence of Psychology"
        )
    ) {
        val badgeY = fy(.02f)
        TemplateLayout(
            PostBase.LIGHT,
            listOf(
                glow(.92f, .82f, .42f, PostTone.ACCENT, PostTone.GLOW, 70),
                DecorBox(DecorShape.ROUND, fx(0f), badgeY, fw(.48f), 68f / H, PostTone.ACCENT, 255, .045f, gradTo = PostTone.DEEP, angle = 0f),
                DecorBox(DecorShape.WAVE, 0f, fy(.60f), 1f, fh(.28f), PostTone.TINT, 170),
                ringAt(.76f, .37f, .22f, PostTone.ACCENT, 45, .006f)
            ),
            listOf(
                abox(PostSlots.NUMBER, 0f, badgeY + 13f / H, .48f, 48f / H, .030f, PostFont.BOLD, PostTone.LIGHT, centered = true, caps = true),
                box(PostSlots.HEADLINE, 0f, .18f, .50f, .43f, .078f, PostFont.DISPLAY, PostTone.TEXT),
                box(PostSlots.BODY, 0f, .67f, 1f, .24f, .036f, PostFont.REGULAR, PostTone.MUTED)
            ),
            photo = PostPhotoArea(fx(.57f), fy(.18f), fw(.38f), fh(.40f), 35, PhotoMask.PENTAGON),
            backdrop = Backdrop(PostTone.TINT, PostTone.SOFT, 135f)
        )
    },

    // 14. Promotion: dark, with glowing light and a feature checklist
    template(
        "promo-feature", "Feature highlight", "Promotion",
        mapOf(
            PostSlots.EYEBROW to "NEW IN THE APP",
            PostSlots.HEADLINE to "Study smarter, anywhere",
            PostSlots.LIST to "Short notes for every chapter\nFlashcards that stick\nExam practice with solutions"
        )
    ) {
        TemplateLayout(
            PostBase.DARK,
            listOf(
                glow(.95f, .08f, .42f, PostTone.ACCENT, PostTone.GLOW, 220),
                glow(.02f, .98f, .30f, PostTone.GLOW, PostTone.DEEP, 130),
                rule(0f, .42f, .12f, PostTone.SECONDARY, .008f)
            ),
            listOf(
                box(PostSlots.EYEBROW, 0f, .02f, .8f, .06f, .028f, PostFont.BOLD, PostTone.SECONDARY, caps = true),
                box(PostSlots.HEADLINE, 0f, .10f, 1f, .30f, .086f, PostFont.DISPLAY, PostTone.LIGHT),
                box(PostSlots.LIST, 0f, .47f, 1f, .50f, .042f, PostFont.BOLD, PostTone.LIGHT, list = ListStyle.CHECK)
            ),
            backdrop = Backdrop(PostTone.DEEP, PostTone.PAGE, 150f)
        )
    },

    // 15. Promotion: headline and a big button
    template(
        "promo-download", "Download call to action", "Promotion",
        mapOf(
            PostSlots.EYEBROW to "GET THE APP",
            PostSlots.HEADLINE to "Your whole course in your pocket",
            PostSlots.BODY to "Free to start. Made for exam season.",
            PostSlots.BUTTON to "Download on Telegram"
        )
    ) {
        TemplateLayout(
            PostBase.PRIMARY,
            listOf(
                glow(.90f, .12f, .38f, PostTone.GLOW, PostTone.ACCENT, 160),
                ringAt(.08f, .62f, .26f, PostTone.SECONDARY, 40, .01f),
                lift(.08f, .76f, .84f, .17f, radius = .05f)
            ),
            listOf(
                box(PostSlots.EYEBROW, 0f, .04f, 1f, .06f, .028f, PostFont.BOLD, PostTone.SECONDARY, centered = true, caps = true),
                box(PostSlots.HEADLINE, 0f, .12f, 1f, .46f, .094f, PostFont.DISPLAY, PostTone.LIGHT, centered = true),
                box(PostSlots.BODY, 0f, .60f, 1f, .12f, .038f, PostFont.REGULAR, PostTone.LIGHT_MUTED, centered = true),
                box(PostSlots.BUTTON, .10f, .80f, .80f, .09f, .044f, PostFont.DISPLAY, PostTone.DARK, centered = true)
            ),
            backdrop = Backdrop(PostTone.ACCENT, PostTone.DEEP, 140f)
        )
    },

    // 16. Reminder: label pill, headline, checklist on a floating card
    template(
        "reminder-checklist", "Checklist", "Reminder",
        mapOf(
            PostSlots.EYEBROW to "BEFORE THE EXAM",
            PostSlots.HEADLINE to "Exam day checklist",
            PostSlots.LIST to "ID card and pens\nCalculator checked\nArrive 30 minutes early\nSleep well the night before\nStay calm and read every question"
        )
    ) {
        val pillY = fy(0f)
        TemplateLayout(
            PostBase.LIGHT,
            listOf(
                DecorBox(DecorShape.ROUND, fx(0f), pillY, fw(.46f), 64f / H, PostTone.ACCENT, 255, .04f, gradTo = PostTone.DEEP, angle = 0f),
                lift(0f, .34f, 1f, .64f, radius = .03f),
                hex(fx(.93f), fy(.04f), .05f, PostTone.ACCENT)
            ),
            listOf(
                abox(PostSlots.EYEBROW, 0f, pillY + 16f / H, .46f, 36f / H, .024f, PostFont.BOLD, PostTone.LIGHT, centered = true, caps = true),
                box(PostSlots.HEADLINE, 0f, .11f, 1f, .21f, .066f, PostFont.DISPLAY, PostTone.TEXT),
                box(PostSlots.LIST, .05f, .38f, .90f, .56f, .042f, PostFont.BOLD, PostTone.TEXT, list = ListStyle.CHECK)
            ),
            backdrop = Backdrop(PostTone.TINT, PostTone.SOFT, 135f)
        )
    },

    // 17. Reminder: weekly schedule, brand header and a floating card of rows
    template(
        "reminder-schedule", "Weekly schedule", "Reminder",
        mapOf(
            PostSlots.EYEBROW to "STUDY PLAN",
            PostSlots.HEADLINE to "This week's schedule",
            PostSlots.LIST to "Mon: Mathematics\nTue: Physics\nWed: Chemistry\nThu: English\nFri: Revision\nSat: Mock test"
        )
    ) {
        TemplateLayout(
            PostBase.LIGHT,
            listOf(
                DecorBox(DecorShape.ROUND, fx(0f), fy(0f), fw(1f), fh(.23f), PostTone.ACCENT, 255, .03f, gradTo = PostTone.DEEP, angle = 135f),
                ringAt(fx(.92f), fy(.10f), .09f, PostTone.LIGHT, 70, .006f),
                lift(0f, .27f, 1f, .71f, radius = .03f)
            ),
            listOf(
                box(PostSlots.EYEBROW, .05f, .02f, .7f, .06f, .024f, PostFont.BOLD, PostTone.SECONDARY, caps = true),
                box(PostSlots.HEADLINE, .05f, .08f, .8f, .14f, .062f, PostFont.DISPLAY, PostTone.LIGHT),
                box(PostSlots.LIST, .05f, .29f, .90f, .67f, .042f, PostFont.REGULAR, PostTone.TEXT, list = ListStyle.ROW)
            ),
            backdrop = Backdrop(PostTone.TINT, PostTone.SOFT, 135f)
        )
    },

    // 18. Achievement: giant number on dark, lit from behind
    template(
        "milestone-number", "Milestone number", "Achievement",
        mapOf(
            PostSlots.EYEBROW to "MILESTONE",
            PostSlots.NUMBER to "10K",
            PostSlots.HEADLINE to "Students learning with us",
            PostSlots.BODY to "Thank you for being part of the journey."
        )
    ) {
        TemplateLayout(
            PostBase.DARK,
            listOf(
                glow(.5f, .36f, .48f, PostTone.ACCENT, PostTone.GLOW, 170),
                ringAt(.5f, .36f, .30f, PostTone.SECONDARY, 55, .006f),
                dia(fx(.90f), fy(.08f), .05f, PostTone.SECONDARY),
                tri(fx(.08f), fy(.50f), .03f, PostTone.SECONDARY, 160),
                rule(0f, .58f, 1f, PostTone.SECONDARY, .008f)
            ),
            listOf(
                box(PostSlots.EYEBROW, 0f, 0f, .8f, .05f, .028f, PostFont.BOLD, PostTone.LIGHT, caps = true),
                box(PostSlots.NUMBER, 0f, .06f, 1f, .50f, .25f, PostFont.DISPLAY, PostTone.SECONDARY),
                box(PostSlots.HEADLINE, 0f, .62f, 1f, .20f, .064f, PostFont.DISPLAY, PostTone.LIGHT),
                box(PostSlots.BODY, 0f, .84f, 1f, .14f, .036f, PostFont.REGULAR, PostTone.LIGHT_MUTED)
            ),
            backdrop = Backdrop(PostTone.DEEP, PostTone.PAGE, 150f)
        )
    },

    // 19. Announcement: a sculpted wave panel creates a distinct text stage
    template(
        "announce-plain", "Wave-cut announcement", "Announcement",
        mapOf(
            PostSlots.EYEBROW to "ANNOUNCEMENT",
            PostSlots.HEADLINE to "New study notes are now live",
            PostSlots.BODY to "Open the app to read the latest chapters. Share it with a friend who is preparing for the same exam."
        )
    ) {
        val waveY = if (story) .53f else .55f
        TemplateLayout(
            PostBase.LIGHT,
            listOf(
                DecorBox(DecorShape.OVAL, .68f, -.15f, .52f, .42f, PostTone.ACCENT, 30),
                DecorBox(DecorShape.WAVE, 0f, waveY - .035f, 1f, 1f - waveY + .035f, PostTone.GLOW, 105),
                DecorBox(DecorShape.WAVE, 0f, waveY, 1f, 1f - waveY, PostTone.ACCENT, 245, gradTo = PostTone.DEEP, angle = 90f),
                ringAt(.94f, .20f, .25f, PostTone.ACCENT, 32, .008f),
                DecorBox(DecorShape.RECT, .07f, .12f, .12f, .008f, PostTone.ACCENT)
            ),
            listOf(
                box(PostSlots.EYEBROW, .02f, .06f, .8f, .06f, .026f, PostFont.BOLD, PostTone.ACCENT, caps = true),
                box(PostSlots.HEADLINE, .02f, .15f, .92f, .34f, .078f, PostFont.DISPLAY, PostTone.TEXT),
                mark("↗", .84f, waveY + .005f, .10f, .075f, .070f, PostFont.DISPLAY, PostTone.LIGHT),
                abox(PostSlots.BODY, .04f, waveY + .09f, .92f, .87f - waveY, .038f, PostFont.REGULAR, PostTone.LIGHT)
            ),
            footerLight = true,
            backdrop = Backdrop(PostTone.TINT, PostTone.SOFT, 135f)
        )
    },

    // 20. Announcement: photo behind, fade under the words, label pill
    template(
        "announce-photo", "Photo announcement", "Announcement",
        mapOf(
            PostSlots.EYEBROW to "JOIN US",
            PostSlots.HEADLINE to "Free revision session this Saturday",
            PostSlots.BODY to "Bring your questions. Seats are limited."
        )
    ) {
        val pillY = fy(.52f)
        TemplateLayout(
            PostBase.DARK,
            listOf(
                fade(.40f, .60f, PostTone.DARK, 0, 215),
                DecorBox(DecorShape.ROUND, fx(0f), pillY, fw(.46f), 64f / H, PostTone.ACCENT, 255, .04f, overPhoto = true)
            ),
            listOf(
                abox(PostSlots.EYEBROW, 0f, pillY + 16f / H, .46f, 36f / H, .024f, PostFont.BOLD, PostTone.LIGHT, centered = true, caps = true),
                box(PostSlots.HEADLINE, 0f, .62f, 1f, .24f, .074f, PostFont.DISPLAY, PostTone.LIGHT),
                box(PostSlots.BODY, 0f, .87f, 1f, .11f, .036f, PostFont.REGULAR, PostTone.LIGHT_MUTED)
            ),
            PostPhotoArea(0f, 0f, 1f, 1f, 110)
        )
    },

    // 21. Social: the Brand icons as big glass cards on a glowing dark gradient
    template(
        "social-follow", "Follow us", "Social",
        mapOf(
            PostSlots.EYEBROW to "STAY CONNECTED",
            PostSlots.HEADLINE to "Follow us everywhere you study",
            PostSlots.BODY to "Daily notes, tips and exam updates on every channel."
        )
    ) {
        TemplateLayout(
            PostBase.DARK,
            listOf(
                glow(.92f, .10f, .40f, PostTone.ACCENT, PostTone.GLOW, 200),
                glow(.05f, .55f, .26f, PostTone.GLOW, PostTone.DEEP, 110)
            ),
            listOf(
                box(PostSlots.EYEBROW, 0f, 0f, 1f, .06f, .028f, PostFont.BOLD, PostTone.SECONDARY, centered = true, caps = true),
                box(PostSlots.HEADLINE, 0f, .08f, 1f, .30f, .078f, PostFont.DISPLAY, PostTone.LIGHT, centered = true),
                box(PostSlots.BODY, 0f, .40f, 1f, .12f, .036f, PostFont.REGULAR, PostTone.LIGHT_MUTED, centered = true)
            ),
            backdrop = Backdrop(PostTone.DEEP, PostTone.PAGE, 150f),
            social = SocialArea(fx(0f), fy(.56f), fw(1f), fh(.42f), SocialStyle.GRID, true)
        )
    },

    // 22. Social: a floating card with one wide row per icon
    template(
        "social-links", "Social link list", "Social",
        mapOf(
            PostSlots.EYEBROW to "FIND US ONLINE",
            PostSlots.HEADLINE to "All our channels in one place",
            PostSlots.BODY to "Tap, follow and share with a friend."
        )
    ) {
        TemplateLayout(
            PostBase.LIGHT,
            listOf(
                glow(.92f, .12f, .34f, PostTone.ACCENT, PostTone.GLOW, 70),
                lift(0f, .44f, 1f, .54f, radius = .035f)
            ),
            listOf(
                box(PostSlots.EYEBROW, 0f, 0f, 1f, .06f, .028f, PostFont.BOLD, PostTone.ACCENT, caps = true),
                box(PostSlots.HEADLINE, 0f, .08f, 1f, .24f, .066f, PostFont.DISPLAY, PostTone.TEXT),
                box(PostSlots.BODY, 0f, .33f, 1f, .08f, .034f, PostFont.REGULAR, PostTone.MUTED)
            ),
            backdrop = Backdrop(PostTone.TINT, PostTone.SOFT, 135f),
            social = SocialArea(fx(.04f), fy(.47f), fw(.92f), fh(.48f), SocialStyle.LIST, false)
        )
    },

    // 23. Exam alert: bold ink-and-pop hierarchy inspired by a weekly exam notice. Ink is the text color, pop the accent.
    template(
        "exam-tomorrow", "Weekly exam tomorrow", "Exam",
        mapOf(
            PostSlots.EYEBROW to "HAVAN ACADEMY  \u2022  WEEKLY NOTICE",
            PostSlots.HEADLINE to "WEEKLY",
            PostSlots.NUMBER to "EXAM",
            PostSlots.BODY to "TOMORROW"
        )
    ) {
        val storyTop = if (story) .19f else .20f
        val headY = if (story) .29f else .28f
        val headH = if (story) .17f else .19f
        val examY = if (story) .47f else .48f
        val examH = if (story) .19f else .20f
        val tomorrowY = if (story) .70f else .71f
        val tomorrowH = if (story) .10f else .11f
        val blue = PostTone.TEXT
        val red = PostTone.ACCENT
        TemplateLayout(
            PostBase.LIGHT,
            listOf(
                // Quiet corner geometry and fine rules frame the typography.
                DecorBox(DecorShape.OVAL, .78f, -.17f, .42f, .34f, blue, 245),
                DecorBox(DecorShape.OVAL, .83f, -.13f, .34f, .27f, PostTone.TINT, 255),
                DecorBox(DecorShape.RECT, .10f, .255f, .80f, .0045f, blue, 210),
                DecorBox(DecorShape.ROUND, .13f, examY, .74f, examH, red, 255, .018f, shadow = true),
                DecorBox(DecorShape.RING, .17f, tomorrowY - .005f, .66f, tomorrowH + .025f, red, 235, stroke = .006f),
                DecorBox(DecorShape.RECT, .08f, .78f, .07f, .009f, blue, 255),
                DecorBox(DecorShape.RECT, .85f, .78f, .07f, .009f, blue, 255)
            ),
            listOf(
                abox(PostSlots.EYEBROW, .08f, storyTop, .84f, .045f, .022f, PostFont.BOLD, blue, centered = true, caps = true),
                abox(PostSlots.HEADLINE, .08f, headY, .84f, headH, if (story) .108f else .105f, PostFont.DISPLAY, blue, centered = true, caps = true),
                abox(PostSlots.NUMBER, .16f, examY + .005f, .68f, examH - .01f, if (story) .145f else .14f, PostFont.DISPLAY, PostTone.LIGHT, centered = true, caps = true),
                abox(PostSlots.BODY, .12f, tomorrowY, .76f, tomorrowH, if (story) .066f else .064f, PostFont.DISPLAY, blue, centered = true, caps = true)
            ),
            backdrop = Backdrop(PostTone.TINT, PostTone.SOFT, 135f)
        )
    },

    // Editorial announcement: a high-contrast notice with a clear title panel and action strip.
    template(
        "notice-editorial", "Editorial announcement", "Announcement",
        mapOf(
            PostSlots.EYEBROW to "IMPORTANT UPDATE",
            PostSlots.HEADLINE to "Something worth knowing",
            PostSlots.BODY to "A clear message with the details people need to remember.",
            PostSlots.BUTTON to "READ THE UPDATE"
        )
    ) {
        val panelY = if (story) .22f else .21f
        val panelH = if (story) .34f else .36f
        TemplateLayout(
            PostBase.LIGHT,
            listOf(
                DecorBox(DecorShape.RECT, 0f, panelY, 1f, panelH, PostTone.TEXT),
                DecorBox(DecorShape.RECT, 0f, panelY, .025f, panelH, PostTone.ACCENT),
                DecorBox(DecorShape.ROUND, .08f, .72f, .84f, .105f, PostTone.ACCENT, radius = .025f, shadow = true),
                DecorBox(DecorShape.RECT, .08f, .65f, .18f, .006f, PostTone.TEXT)
            ),
            listOf(
                abox(PostSlots.EYEBROW, .09f, panelY + .035f, .82f, .055f, .023f, PostFont.BOLD, PostTone.LIGHT_MUTED, caps = true),
                abox(PostSlots.HEADLINE, .09f, panelY + .105f, .82f, panelH - .12f, .073f, PostFont.DISPLAY, PostTone.LIGHT),
                abox(PostSlots.BODY, .09f, .61f, .82f, .10f, .034f, PostFont.REGULAR, PostTone.TEXT),
                abox(PostSlots.BUTTON, .10f, .735f, .80f, .075f, .034f, PostFont.BOLD, PostTone.LIGHT, centered = true, caps = true)
            )
        )
    },

    // Study tip: editorial numbered layout with an oversized marker and a strong reading column.
    template(
        "study-tip-editorial", "Editorial study tip", "Study tip",
        mapOf(
            PostSlots.EYEBROW to "THE STUDY METHOD",
            PostSlots.NUMBER to "01",
            PostSlots.HEADLINE to "Recall beats rereading",
            PostSlots.BODY to "Close your notes. Write what you remember. Then reopen the chapter and fill the gaps."
        )
    ) {
        TemplateLayout(
            PostBase.LIGHT,
            listOf(
                DecorBox(DecorShape.RECT, .08f, .23f, .018f, .49f, PostTone.ACCENT),
                DecorBox(DecorShape.RECT, .08f, .20f, .18f, .006f, PostTone.TEXT),
                DecorBox(DecorShape.ROUND, .68f, .20f, .23f, .16f, PostTone.TEXT, radius = .025f),
                DecorBox(DecorShape.RING, .70f, .22f, .19f, .12f, PostTone.ACCENT, 235, stroke = .004f)
            ),
            listOf(
                abox(PostSlots.EYEBROW, .08f, .16f, .84f, .04f, .022f, PostFont.BOLD, PostTone.ACCENT, caps = true),
                abox(PostSlots.NUMBER, .12f, .25f, .48f, .25f, .18f, PostFont.DISPLAY, PostTone.TEXT),
                abox(PostSlots.HEADLINE, .12f, .51f, .78f, .18f, .062f, PostFont.DISPLAY, PostTone.TEXT),
                abox(PostSlots.BODY, .12f, .70f, .78f, .14f, .032f, PostFont.REGULAR, PostTone.MUTED)
            )
        )
    },

    // 23. Event: a ticket with notches and a dotted tear line, date below
    template(
        "event-ticket", "Event ticket", "Event",
        mapOf(
            PostSlots.EYEBROW to "ADMIT ONE",
            PostSlots.HEADLINE to "Free exam revision session",
            PostSlots.NUMBER to "SAT \u00B7 12 NOV",
            PostSlots.BODY to "9:00 AM at the main hall. Bring your questions."
        )
    ) {
        val notchY = fy(.62f)
        TemplateLayout(
            PostBase.PRIMARY,
            listOf(
                card(0f, 0f, 1f, .98f, PostTone.LIGHT, radius = .04f),
                disc(0f, .62f, .045f, PostTone.PAGE),
                disc(1f, .62f, .045f, PostTone.PAGE),
                DecorBox(DecorShape.DOTS, fx(.08f), notchY - .015f * U / H, fw(.84f), .03f * U / H, PostTone.ACCENT, 130)
            ),
            listOf(
                box(PostSlots.EYEBROW, .07f, .05f, .86f, .05f, .024f, PostFont.BOLD, PostTone.ACCENT, caps = true),
                box(PostSlots.HEADLINE, .07f, .12f, .86f, .42f, .07f, PostFont.DISPLAY, PostTone.TEXT),
                box(PostSlots.NUMBER, .07f, .67f, .86f, .14f, .085f, PostFont.DISPLAY, PostTone.ACCENT),
                box(PostSlots.BODY, .07f, .83f, .86f, .12f, .032f, PostFont.REGULAR, PostTone.MUTED)
            )
        )
    },

    // 24. Event: a date card top left, a loud headline under it
    template(
        "event-poster", "Event poster", "Event",
        mapOf(
            PostSlots.EYEBROW to "REVISION CAMP \u00B7 FREE ENTRY",
            PostSlots.NUMBER to "12 NOV",
            PostSlots.HEADLINE to "Exam revision weekend",
            PostSlots.BODY to "Saturday, 9:00 AM at the main hall. Bring your questions."
        )
    ) {
        TemplateLayout(
            PostBase.DARK,
            listOf(
                glow(.90f, .78f, .42f, PostTone.ACCENT, PostTone.GLOW, 170),
                DecorBox(DecorShape.ROUND, fx(0f), fy(0f), fw(.5f), fh(.30f), PostTone.LIGHT, 255, .04f, shadow = true)
            ),
            listOf(
                box(PostSlots.NUMBER, .03f, .04f, .44f, .22f, .085f, PostFont.DISPLAY, PostTone.ACCENT, centered = true),
                box(PostSlots.EYEBROW, .56f, .02f, .44f, .26f, .03f, PostFont.BOLD, PostTone.SECONDARY, caps = true),
                box(PostSlots.HEADLINE, 0f, .38f, 1f, .40f, .086f, PostFont.DISPLAY, PostTone.LIGHT),
                box(PostSlots.BODY, 0f, .80f, 1f, .18f, .038f, PostFont.REGULAR, PostTone.LIGHT_MUTED)
            ),
            backdrop = Backdrop(PostTone.DEEP, PostTone.PAGE, 150f)
        )
    },

    // 25. Aurora: dark gradient, two soft glows, one loud statement
    template(
        "aurora-statement", "Aurora statement", "Motivation",
        mapOf(
            PostSlots.EYEBROW to "REMEMBER THIS",
            PostSlots.HEADLINE to "You do not need more time. You need a start.",
            PostSlots.BODY to "Open the book for ten minutes. The rest follows."
        )
    ) {
        TemplateLayout(
            PostBase.DARK,
            listOf(
                DecorBox(DecorShape.OVAL, .50f, -.10f, .85f, .85f * W / H, PostTone.ACCENT, 210, gradTo = PostTone.GLOW, alphaTo = 0, angle = 45f),
                DecorBox(DecorShape.OVAL, -.35f, .62f, .80f, .80f * W / H, PostTone.GLOW, 150, gradTo = PostTone.DEEP, alphaTo = 0, angle = 135f),
                DecorBox(DecorShape.TRIANGLE, fx(.88f), fy(0f), .07f, .07f * W / H, PostTone.SECONDARY)
            ),
            listOf(
                box(PostSlots.EYEBROW, 0f, 0f, .8f, .06f, .024f, PostFont.BOLD, PostTone.SECONDARY, caps = true),
                box(PostSlots.HEADLINE, 0f, .10f, 1f, .62f, .092f, PostFont.DISPLAY, PostTone.LIGHT),
                box(PostSlots.BODY, 0f, .78f, .9f, .20f, .036f, PostFont.REGULAR, PostTone.LIGHT_MUTED)
            ),
            backdrop = Backdrop(PostTone.DEEP, PostTone.PAGE, 130f)
        )
    },

    // 26. Stat hero: one number inside a double ring, a fact under it
    template(
        "stat-hero", "Number in a ring", "Fact",
        mapOf(
            PostSlots.NUMBER to "72%",
            PostSlots.HEADLINE to "of what you read is forgotten in 3 days",
            PostSlots.BODY to "Review the same page again tomorrow and the loss drops sharply."
        )
    ) {
        val r = if (story) .26f else .22f
        val cx = fx(.5f)
        val cy = fy(.32f)
        val rx = r * U / W
        val ry = r * U / H
        val ns = if (story) .19f else .17f
        val numberH = ns * U * 1.6f / H
        val headY = cy + ry + .03f
        val headH = if (story) .15f else .11f
        val bodyY = headY + headH
        TemplateLayout(
            PostBase.LIGHT,
            listOf(
                DecorBox(DecorShape.OVAL, cx - rx, cy - ry, 2f * rx, 2f * ry, PostTone.ACCENT, 255, gradTo = PostTone.DEEP, angle = 135f),
                DecorBox(DecorShape.RING, cx - rx * 1.14f, cy - ry * 1.14f, 2.28f * rx, 2.28f * ry, PostTone.ACCENT, 120, stroke = .006f),
                DecorBox(DecorShape.STRIPES, .05f, cy - .06f, .14f, .14f * W / H, PostTone.ACCENT, 30),
                DecorBox(DecorShape.HEXAGON, .83f, cy - .02f, .08f, .08f * W / H, PostTone.ACCENT)
            ),
            listOf(
                SlotBox(PostSlots.NUMBER, cx - rx, cy - numberH / 2f, 2f * rx, numberH, ns, PostFont.DISPLAY, PostTone.LIGHT, centered = true),
                abox(PostSlots.HEADLINE, 0f, headY, 1f, headH, .05f, PostFont.DISPLAY, PostTone.TEXT, centered = true),
                abox(PostSlots.BODY, .06f, bodyY, .88f, bottom - bodyY, .032f, PostFont.REGULAR, PostTone.MUTED, centered = true)
            ),
            backdrop = Backdrop(PostTone.TINT, PostTone.SOFT, 135f)
        )
    },

    // 27. Stripe poster: hazard-style stripe edge, label pill, shouting headline
    template(
        "stripe-poster", "Stripe poster", "Announcement",
        mapOf(
            PostSlots.EYEBROW to "NOTICE",
            PostSlots.HEADLINE to "Registration closes Friday",
            PostSlots.BODY to "Send your name and student ID before 5:00 PM. Late forms are not accepted."
        )
    ) {
        val pillY = fy(0f)
        TemplateLayout(
            PostBase.PRIMARY,
            listOf(
                DecorBox(DecorShape.STRIPES, 0f, 0f, .055f, 1f, PostTone.SECONDARY, 150),
                DecorBox(DecorShape.ROUND, fx(.04f), pillY, fw(.46f), 64f / H, PostTone.SECONDARY, radius = .04f)
            ),
            listOf(
                abox(PostSlots.EYEBROW, .04f, pillY + 16f / H, .46f, 36f / H, .022f, PostFont.BOLD, PostTone.DARK, centered = true, caps = true),
                box(PostSlots.HEADLINE, .04f, .12f, .96f, .56f, .098f, PostFont.DISPLAY, PostTone.LIGHT, caps = true),
                box(PostSlots.BODY, .04f, .74f, .96f, .24f, .036f, PostFont.REGULAR, PostTone.LIGHT_MUTED)
            )
        )
    },

    // 28. Wave: headline on light, message on a rolling brand-color wave
    template(
        "wave-announce", "Wave announcement", "Announcement",
        mapOf(
            PostSlots.EYEBROW to "NEW THIS WEEK",
            PostSlots.HEADLINE to "Chapter 4 notes are ready",
            PostSlots.BODY to "Open the app, pick your course and start with the summary page."
        )
    ) {
        val waveTop = if (story) .60f else .58f
        val bodyY = waveTop + .09f
        TemplateLayout(
            PostBase.LIGHT,
            listOf(
                DecorBox(DecorShape.WAVE, 0f, waveTop - .04f, 1f, 1f - waveTop + .04f, PostTone.GLOW, 140),
                DecorBox(DecorShape.WAVE, 0f, waveTop, 1f, 1f - waveTop, PostTone.ACCENT, 255, gradTo = PostTone.DEEP, angle = 90f)
            ),
            listOf(
                box(PostSlots.EYEBROW, 0f, 0f, 1f, .06f, .024f, PostFont.BOLD, PostTone.ACCENT, caps = true),
                box(PostSlots.HEADLINE, 0f, .09f, 1f, .38f, .08f, PostFont.DISPLAY, PostTone.TEXT),
                abox(PostSlots.BODY, 0f, bodyY, 1f, bottom - bodyY, .038f, PostFont.REGULAR, PostTone.LIGHT)
            ),
            footerLight = true
        )
    },

    // 29. Definition: a term on a floating card over a gradient
    template(
        "definition-card", "Definition card", "Study tip",
        mapOf(
            PostSlots.EYEBROW to "DEFINITION",
            PostSlots.HEADLINE to "Homeostasis",
            PostSlots.BODY to "The way a living body keeps its inner conditions steady, such as temperature and water balance, while the outside keeps changing."
        )
    ) {
        val tagY = fy(.07f)
        TemplateLayout(
            PostBase.PRIMARY,
            listOf(
                DecorBox(DecorShape.ROUND, fx(0f), fy(.02f), fw(1f), fh(.94f), PostTone.LIGHT, 255, .035f, shadow = true),
                DecorBox(DecorShape.ROUND, fx(.06f), tagY, fw(.40f), 56f / H, PostTone.ACCENT, radius = .04f),
                DecorBox(DecorShape.DIAMOND, fx(.84f), fy(.06f), .085f, .085f * W / H, PostTone.ACCENT)
            ),
            listOf(
                abox(PostSlots.EYEBROW, .06f, tagY + 12f / H, .40f, 32f / H, .020f, PostFont.BOLD, PostTone.LIGHT, centered = true, caps = true),
                box(PostSlots.HEADLINE, .06f, .18f, .88f, .24f, .104f, PostFont.DISPLAY, PostTone.ACCENT),
                box(PostSlots.BODY, .06f, .46f, .88f, .46f, .04f, PostFont.REGULAR, PostTone.TEXT)
            ),
            backdrop = Backdrop(PostTone.DEEP, PostTone.ACCENT, 150f)
        )
    },

    // 30. Glass list: key points on a frosted card over a dark gradient
    template(
        "tip-glass", "Glass checklist", "Study tip",
        mapOf(
            PostSlots.EYEBROW to "BEFORE YOU SLEEP",
            PostSlots.HEADLINE to "Four things that help you remember",
            PostSlots.LIST to "Review today's notes once\nWrite three questions for tomorrow\nPhone away from the bed\nSleep at least seven hours"
        )
    ) {
        TemplateLayout(
            PostBase.DARK,
            listOf(
                DecorBox(DecorShape.OVAL, .55f, -.08f, .60f, .60f * W / H, PostTone.ACCENT, 200, gradTo = PostTone.GLOW, alphaTo = 0, angle = 45f),
                DecorBox(DecorShape.ROUND, fx(0f), fy(.36f), fw(1f), fh(.62f), PostTone.LIGHT, 34, .035f)
            ),
            listOf(
                box(PostSlots.EYEBROW, 0f, 0f, 1f, .06f, .024f, PostFont.BOLD, PostTone.SECONDARY, caps = true),
                box(PostSlots.HEADLINE, 0f, .08f, 1f, .25f, .064f, PostFont.DISPLAY, PostTone.LIGHT),
                box(PostSlots.LIST, .06f, .41f, .88f, .52f, .038f, PostFont.BOLD, PostTone.LIGHT, list = ListStyle.CHECK)
            ),
            backdrop = Backdrop(PostTone.DEEP, PostTone.PAGE, 160f)
        )
    },

    // 31. Bold date: ink day name, pop band with the date, ringed time. Pair buttons restyle it.
    template(
        "bold-date", "Bold date", "Exam",
        mapOf(
            PostSlots.EYEBROW to "FINAL EXAM",
            PostSlots.HEADLINE to "SATURDAY",
            PostSlots.NUMBER to "12 NOV",
            PostSlots.BODY to "9:00 AM"
        )
    ) {
        val eyY = if (story) .19f else .20f
        val headY = if (story) .29f else .28f
        val headH = if (story) .17f else .19f
        val bandY = if (story) .47f else .48f
        val bandH = if (story) .19f else .20f
        val ringY = if (story) .70f else .71f
        val ringH = if (story) .10f else .11f
        val ink = PostTone.TEXT
        val pop = PostTone.ACCENT
        TemplateLayout(
            PostBase.LIGHT,
            listOf(
                DecorBox(DecorShape.OVAL, -.20f, -.17f, .42f, .34f, ink, 245),
                DecorBox(DecorShape.OVAL, -.17f, -.13f, .34f, .27f, PostTone.TINT, 255),
                DecorBox(DecorShape.RECT, .10f, .257f, .33f, .0045f, ink, 210),
                DecorBox(DecorShape.RECT, .57f, .257f, .33f, .0045f, ink, 210),
                DecorBox(DecorShape.DIAMOND, .47f, .227f, .06f, .06f * W / H, pop),
                DecorBox(DecorShape.ROUND, .13f, bandY, .74f, bandH, pop, 255, .018f, shadow = true),
                DecorBox(DecorShape.RING, .17f, ringY - .005f, .66f, ringH + .025f, pop, 235, stroke = .006f)
            ),
            listOf(
                SlotBox(PostSlots.EYEBROW, .08f, eyY, .84f, .045f, .024f, PostFont.BOLD, ink, centered = true, caps = true),
                SlotBox(PostSlots.HEADLINE, .08f, headY, .84f, headH, if (story) .108f else .105f, PostFont.DISPLAY, ink, centered = true, caps = true),
                SlotBox(PostSlots.NUMBER, .16f, bandY + .005f, .68f, bandH - .01f, if (story) .145f else .14f, PostFont.DISPLAY, PostTone.LIGHT, centered = true, caps = true),
                SlotBox(PostSlots.BODY, .12f, ringY, .76f, ringH, if (story) .066f else .064f, PostFont.DISPLAY, ink, centered = true, caps = true)
            ),
            backdrop = Backdrop(PostTone.TINT, PostTone.SOFT, 135f)
        )
    },

    // 32. Bold question: ink panel with the question, pop pill label, big lettered options
    template(
        "bold-question", "Bold question", "Question",
        mapOf(
            PostSlots.EYEBROW to "QUESTION 4",
            PostSlots.HEADLINE to "Which organelle is called the powerhouse of the cell?",
            PostSlots.LIST to "Nucleus\nMitochondrion\nRibosome\nGolgi apparatus"
        )
    ) {
        val panelY = if (story) .24f else .25f
        val panelH = if (story) .30f else .29f
        val listY = panelY + panelH + .04f
        val ink = PostTone.TEXT
        val pop = PostTone.ACCENT
        TemplateLayout(
            PostBase.LIGHT,
            listOf(
                DecorBox(DecorShape.RECT, 0f, panelY, 1f, panelH, ink),
                DecorBox(DecorShape.RECT, 0f, panelY, .03f, panelH, pop),
                DecorBox(DecorShape.DIAMOND, .88f, panelY + panelH - .075f, .05f, .05f * W / H, PostTone.LIGHT, 150),
                DecorBox(DecorShape.ROUND, .09f, panelY - .03f, .36f, .06f, pop, 255, .03f, shadow = true),
                DecorBox(DecorShape.RECT, .09f, listY - .012f, .12f, .006f, pop)
            ),
            listOf(
                SlotBox(PostSlots.EYEBROW, .09f, panelY - .03f + .013f, .36f, .036f, .024f, PostFont.BOLD, PostTone.LIGHT, centered = true, caps = true),
                SlotBox(PostSlots.HEADLINE, .09f, panelY + .045f, .82f, panelH - .06f, .06f, PostFont.DISPLAY, PostTone.LIGHT),
                SlotBox(PostSlots.LIST, .09f, listY + .01f, .82f, .85f - listY - .01f, .048f, PostFont.BOLD, ink, list = ListStyle.LETTER)
            ),
            backdrop = Backdrop(PostTone.TINT, PostTone.SOFT, 135f)
        )
    },

    // 33. Bold call to action: ink headline, pop band with the message, ringed button words
    template(
        "bold-promo", "Bold call to action", "Promotion",
        mapOf(
            PostSlots.EYEBROW to "NEW ON THE APP",
            PostSlots.HEADLINE to "FREE NOTES",
            PostSlots.BODY to "ALL CHAPTERS",
            PostSlots.BUTTON to "JOIN NOW"
        )
    ) {
        val eyY = if (story) .19f else .20f
        val headY = if (story) .29f else .28f
        val headH = if (story) .17f else .19f
        val bandY = if (story) .47f else .48f
        val bandH = if (story) .19f else .20f
        val ringY = if (story) .70f else .71f
        val ringH = if (story) .10f else .11f
        val ink = PostTone.TEXT
        val pop = PostTone.ACCENT
        TemplateLayout(
            PostBase.LIGHT,
            listOf(
                DecorBox(DecorShape.OVAL, .78f, -.17f, .42f, .34f, ink, 245),
                DecorBox(DecorShape.OVAL, .83f, -.13f, .34f, .27f, PostTone.TINT, 255),
                DecorBox(DecorShape.RECT, .10f, .257f, .80f, .0045f, ink, 210),
                DecorBox(DecorShape.HEXAGON, .88f, headY + .01f, .06f, .06f * W / H, pop),
                DecorBox(DecorShape.ROUND, .13f, bandY, .74f, bandH, pop, 255, .018f, shadow = true),
                DecorBox(DecorShape.RING, .17f, ringY - .005f, .66f, ringH + .025f, pop, 235, stroke = .006f)
            ),
            listOf(
                SlotBox(PostSlots.EYEBROW, .08f, eyY, .84f, .045f, .024f, PostFont.BOLD, ink, centered = true, caps = true),
                SlotBox(PostSlots.HEADLINE, .08f, headY, .84f, headH, if (story) .108f else .105f, PostFont.DISPLAY, ink, centered = true, caps = true),
                SlotBox(PostSlots.BODY, .16f, bandY + .005f, .68f, bandH - .01f, if (story) .105f else .10f, PostFont.DISPLAY, PostTone.LIGHT, centered = true, caps = true),
                SlotBox(PostSlots.BUTTON, .12f, ringY, .76f, ringH, if (story) .066f else .064f, PostFont.DISPLAY, ink, centered = true, caps = true)
            ),
            backdrop = Backdrop(PostTone.TINT, PostTone.SOFT, 135f)
        )
    }
)
