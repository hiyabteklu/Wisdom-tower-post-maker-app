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
 * The current templates only use RECT, ROUND, OVAL and a few thin RING lines. The renderer draws nothing for DIAMOND, TRIANGLE and HEXAGON.
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
    val social: SocialArea? = null,
    /** True for the redesigned templates, which is all of them now: their structure is the design, nothing is added behind them. */
    val clean: Boolean = false
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

/**
 * A template is data: a layout function per size, plus sample text for every slot it uses.
 * look is what picking the template sets on the post (photo frame, mesh, grain, words behind the subject, cut-out position).
 */
data class PostTemplateSpec(
    val id: String,
    val name: String,
    val family: String,
    val samples: Map<String, String>,
    val look: TemplateLook? = null,
    val layout: (PostSize) -> TemplateLayout
)

/**
 * The 25 templates, in the order the editor strip and the Library show them: photo (PostTemplatesPhoto.kt),
 * then information, marketing and type (InfoTemplates in PostTemplatesInfo.kt, which already includes the marketing and type file).
 * The old 36-template list is gone.
 */
val PostTemplates: List<PostTemplateSpec> = PhotoTemplates + InfoTemplates

/**
 * Ids of the removed templates, each pointing at the nearest new one, so a saved draft or an old library entry still opens.
 * Words stay with the post; the new template's look and any missing sample words are added when the draft is opened (see PostDrafts).
 */
private val LegacyTemplateIds: Map<String, String> = mapOf(
    "quote-center" to "info-quote",
    "quote-bar" to "info-quote",
    "quote-photo" to "photo-duotone",
    "motivation-bold" to "type-aurora",
    "aurora-statement" to "type-aurora",
    "motivation-split" to "type-split",
    "mcq-four" to "info-quiz",
    "bold-question" to "info-quiz",
    "question-answer" to "info-qa",
    "exam-date" to "mkt-poster",
    "bold-date" to "mkt-poster",
    "exam-tomorrow" to "mkt-poster",
    "event-poster" to "mkt-poster",
    "exam-countdown" to "info-stat",
    "stat-hero" to "info-stat",
    "tip-numbered" to "info-tip",
    "study-tip-editorial" to "info-tip",
    "tip-card" to "info-checklist",
    "tip-glass" to "info-checklist",
    "reminder-checklist" to "info-checklist",
    "reminder-schedule" to "info-timetable",
    "definition-card" to "info-definition",
    "subject-cover" to "photo-card",
    "subject-badge" to "photo-card",
    "announce-photo" to "photo-duotone",
    "promo-feature" to "mkt-feature",
    "promo-download" to "mkt-download",
    "bold-promo" to "mkt-download",
    "milestone-number" to "mkt-milestone",
    "announce-plain" to "mkt-notice",
    "wave-announce" to "mkt-notice",
    "stripe-poster" to "mkt-notice",
    "notice-editorial" to "mkt-notice",
    "social-follow" to "mkt-follow",
    "social-links" to "mkt-follow",
    "event-ticket" to "mkt-ticket"
)

/** The template with this id. An id from the old list gives the template that replaced it; an unknown id gives the first template. */
fun postTemplate(id: String): PostTemplateSpec {
    val target = LegacyTemplateIds[id] ?: id
    return PostTemplates.firstOrNull { it.id == target } ?: PostTemplates.first { it.id == "info-quote" }
}

/**
 * Switches template and keeps every word already typed. Slots the new template needs that are still missing get its sample text.
 * A template with a look also sets its frame, mesh, grain, words-behind and cut-out position.
 */
fun PostDoc.withTemplate(spec: PostTemplateSpec): PostDoc {
    val next = copy(templateId = spec.id, texts = spec.samples + texts)
    val look = spec.look ?: return next
    return next.withLook(look)
}
