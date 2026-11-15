package com.wisdomtower.post

import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

enum class PostBase { DARK, LIGHT }

enum class PostTone {
    ACCENT,
    DEEP,
    GLOW,
    SECONDARY,
    LIGHT,
    DARK,
    TINT,
    SOFT,
    PAGE,
    TEXT,
    MUTED,
    PAPER,
    BLUE,
    RED
}

data class Backdrop(
    val from: PostTone,
    val to: PostTone,
    val angle: Float = 90f,
    val presetName: String = "Custom",
    val mid: PostTone? = null
) {
    val name: String get() = presetName

    companion object {
        val SPOT = Backdrop(PostTone.DEEP, PostTone.DARK, 135f, "Spot")
        val CORNER = Backdrop(PostTone.TINT, PostTone.DARK, 150f, "Corner")
        val RAYS = Backdrop(PostTone.GLOW, PostTone.DEEP, 120f, "Rays")
        val GRID = Backdrop(PostTone.SOFT, PostTone.DEEP, 90f, "Grid")
        val PAPER = Backdrop(PostTone.PAPER, PostTone.PAPER, 90f, "Paper")
        val MESH = Backdrop(PostTone.ACCENT, PostTone.DEEP, 140f, "Mesh")

        private val ALL = listOf(SPOT, CORNER, RAYS, GRID, PAPER, MESH)
        fun values(): Array<Backdrop> = ALL.toTypedArray()
    }
}

enum class PostFont {
    DISPLAY,
    BOLD,
    REGULAR,
    CONDENSED,
    SERIF,
    WIDE
}

/**
 * Must keep the exact 6 entries expected by PostStudyGrid.kt:860 exhaustive `when (style)`.
 */
enum class ListStyle {
    NONE,
    NUMBER,
    CHECK,
    DOT,
    LETTER,
    ROW
}

enum class DecorShape {
    CARD,
    BAR,
    LINE,
    QUOTE,
    PILL,
    BLOCK,
    CORNER,
    HALO,
    ARCH,
    SLANT,
    FRAME,
    PHONE,
    RECT,
    ROUND,
    DISC,
    GLOW,
    RING,
    WAVE,
    DOTS,
    STRIPES,
    GEM,
    DIAMOND,
    TRIANGLE,
    HEXAGON
}

data class DecorBox(
    val shape: DecorShape,
    val x: Float,
    val y: Float,
    val w: Float,
    val h: Float,
    val tone: PostTone,
    val alpha: Int = 255,
    val frameBack: Int = 0,
    val panel: Boolean = false,
    val rotate: Float = 0f
)

class SlotBox(
    val slot: String,
    val x: Float,
    val y: Float,
    val w: Float,
    val h: Float,
    size: Float,
    val font: PostFont,
    val tone: PostTone,
    val centered: Boolean = false,
    val caps: Boolean = false,
    val list: ListStyle = ListStyle.NONE,
    val literal: String? = null,
    val fit: FitMode = FitMode.LEGACY,
    val anchor: VAnchor = VAnchor.TOP,
    val rotate: Float = 0f,
    val tracking: Float = 0f,
    val leading: Float = 1.08f,
    val fx: TextFx = TextFx.SOLID,
    val fxTo: PostTone = PostTone.ACCENT,
    val chip: PostTone? = null,
    val dimOthers: Float = 1f,
    val shadow: TextShadow? = null,
    val maxLines: Int = 0,
    val stackFx: List<TextFx> = emptyList()
) {
    /**
     * Normalized fraction of min(width, height).
     * When the size passed in is > 2f (given in 1080p pixels), converts to a fraction and enforces PostDesignRules minimums.
     */
    val size: Float = if (size > 2.0f) {
        val frac = size / 1080f
        when (slot) {
            PostSlots.HEADLINE -> maxOf(frac, 0.053f)
            PostSlots.BODY, PostSlots.LIST -> maxOf(frac, 0.032f)
            PostSlots.EYEBROW, PostSlots.BUTTON -> maxOf(frac, 0.025f)
            else -> frac
        }
    } else {
        size
    }

    constructor(
        slot: String,
        x: Float,
        y: Float,
        w: Float,
        h: Float,
        size: Float,
        font: PostFont,
        tone: PostTone,
        centered: Boolean = false,
        caps: Boolean = false,
        list: ListStyle = ListStyle.NONE,
        literal: Boolean,
        fit: FitMode = FitMode.LEGACY,
        anchor: VAnchor = VAnchor.TOP,
        rotate: Float = 0f,
        tracking: Float = 0f,
        leading: Float = 1.08f,
        fx: TextFx = TextFx.SOLID,
        fxTo: PostTone = PostTone.ACCENT,
        chip: PostTone? = null,
        dimOthers: Float = 1f,
        shadow: TextShadow? = null,
        maxLines: Int = 0,
        stackFx: List<TextFx> = emptyList()
    ) : this(
        slot = slot,
        x = x,
        y = y,
        w = w,
        h = h,
        size = size,
        font = font,
        tone = tone,
        centered = centered,
        caps = caps,
        list = list,
        literal = if (literal) slot else null,
        fit = fit,
        anchor = anchor,
        rotate = rotate,
        tracking = tracking,
        leading = leading,
        fx = fx,
        fxTo = fxTo,
        chip = chip,
        dimOthers = dimOthers,
        shadow = shadow,
        maxLines = maxLines,
        stackFx = stackFx
    )
}

data class PhotoCell(
    val x: Float,
    val y: Float,
    val w: Float,
    val h: Float,
    val panOffsetX: Float = 0f,
    val panOffsetY: Float = 0f,
    val rotate: Float = 0f,
    val radius: Float = 0.028f
)

data class PostPhotoArea(
    val x: Float,
    val y: Float,
    val w: Float,
    val h: Float,
    val circle: Boolean = false,
    val rounded: Boolean = false,
    val fadeBottom: Boolean = false,
    val fadeTop: Boolean = false,
    val fadeRight: Boolean = false,
    val cells: List<PhotoCell> = emptyList(),
    val rotate: Float = 0f,
    val dim: Int = 0
) {
    val mask: PhotoFrame?
        get() = when {
            circle -> PhotoFrame.CIRCLE
            rounded -> PhotoFrame.ROUNDED
            else -> null
        }
}

data class SocialArea(
    val x: Float,
    val y: Float,
    val w: Float,
    val h: Float,
    val darkCards: Boolean = true
)

data class TemplateLayout(
    val base: PostBase = PostBase.DARK,
    val decor: List<DecorBox> = emptyList(),
    val slots: List<SlotBox> = emptyList(),
    val photo: PostPhotoArea? = null,
    val footerLight: Boolean = true,
    val backdrop: Backdrop? = null,
    val clean: Boolean = false,
    val bare: Boolean = false,
    val social: SocialArea? = null,
    val bleed: Boolean = false
)

data class TemplateLook(
    val name: String = "Default",
    val accentHex: String? = null,
    val bgHex: String? = null,
    val base: PostBase? = null,
    val backdrop: Backdrop? = null,
    val mesh: MeshStyle = MeshStyle.OFF,
    val grain: Boolean = false,
    val frame: PhotoFrame? = null,
    val frameBorder: Boolean = false,
    val textBehind: Set<String> = emptySet(),
    val cutoutX: Float = 0.5f,
    val cutoutY: Float = 0.94f,
    val cutoutScale: Float = 1f
)

class PostTemplateSpec(
    val id: String,
    val name: String,
    val family: String,
    val samples: Map<String, String>,
    val look: TemplateLook? = null,
    val looks: List<TemplateLook> = if (look != null) listOf(look) else emptyList(),
    val slots: List<String> = samples.keys.toList(),
    val layout: (PostSize) -> TemplateLayout
) {
    val sample: Map<String, String> get() = samples

    constructor(
        id: String,
        name: String,
        family: String,
        slots: List<String>,
        sample: Map<String, String>,
        looks: List<TemplateLook> = emptyList(),
        layout: (PostSize) -> TemplateLayout
    ) : this(
        id = id,
        name = name,
        family = family,
        samples = sample,
        look = looks.firstOrNull(),
        looks = looks,
        slots = slots,
        layout = layout
    )
}

fun rotBox(w: Float, h: Float, deg: Float): Pair<Float, Float> {
    val rad = Math.toRadians(abs(deg).toDouble())
    val c = cos(rad).toFloat()
    val s = sin(rad).toFloat()
    return (w * c + h * s) to (w * s + h * c)
}

fun PostDoc.withLook(look: TemplateLook): PostDoc = copy(
    accentHex = look.accentHex ?: accentHex,
    backgroundHex = look.bgHex ?: backgroundHex,
    meshStyle = if (look.mesh != MeshStyle.OFF) look.mesh else meshStyle,
    grain = look.grain || grain,
    photoFrame = look.frame ?: photoFrame,
    frameBorder = look.frameBorder || frameBorder,
    textBehind = if (look.textBehind.isNotEmpty()) look.textBehind else textBehind
)

fun PostDoc.withTemplate(spec: PostTemplateSpec): PostDoc {
    val mergedTexts = PostSlots.withDerivedSlots(spec.samples + texts, spec.slots)
    val base = copy(
        templateId = spec.id,
        lookIndex = 0,
        texts = mergedTexts
    )
    return spec.look?.let { base.withLook(it) } ?: base
}

fun PostTemplateSpec.withLookIndex(index: Int): PostTemplateSpec {
    if (looks.isEmpty()) return this
    val chosen = looks[index.coerceIn(0, looks.lastIndex)]
    return PostTemplateSpec(
        id = id,
        name = name,
        family = family,
        samples = samples,
        look = chosen,
        looks = looks,
        slots = slots
    ) { size ->
        val raw = layout(size)
        raw.copy(
            base = chosen.base ?: raw.base,
            backdrop = chosen.backdrop ?: raw.backdrop
        )
    }
}

/**
 * Legacy template ID mapping (Section 6 of docs/TEMPLATE_REDESIGN_PLAN.md).
 * Old drafts and inbox batches saved under any removed template ID resolve cleanly to their new replacement.
 */
val LegacyTemplateIds: Map<String, String> = mapOf(
    "bold-quote" to "info-quote",
    "bold-stat" to "info-stat",
    "bold-tip" to "info-tip",
    "bold-words" to "type-spotlight",
    "bold-minimal" to "type-aurora",
    "bold-ribbon" to "ltr-bigword",
    "bold-versus" to "info-versus",
    "bold-callout" to "mkt-notice",
    "bold-steps" to "info-tip",
    "bold-banner" to "type-ink",

    "split-left" to "ltr-split",
    "split-top" to "ltr-number",
    "split-corner" to "type-grid",
    "split-diagonal" to "ltr-bigword",
    "split-band" to "type-chips",
    "split-pillar" to "ltr-dropcap",
    "split-card" to "info-definition",
    "split-frame" to "mkt-poster",
    "split-step" to "info-qa",
    "split-arch" to "ltr-window",

    "photo-full" to "photo-floor",
    "photo-circle" to "photo-testimonial",
    "photo-card" to "photo-polaroid",
    "photo-left" to "photo-tag",
    "photo-top" to "photo-cover",
    "photo-badge" to "photo-idcard",
    "photo-frame" to "photo-cover",
    "photo-strip" to "photo-triptych",
    "photo-arch" to "photo-behind",
    "photo-quote" to "photo-testimonial",
    "photo-corner" to "photo-tag",
    "photo-split" to "photo-triptych",
    "photo-spotlight" to "photo-behind",
    "photo-banner" to "photo-floor",

    "info-list" to "info-tip",
    "info-steps" to "info-tip",
    "info-compare" to "info-versus",
    "info-fact" to "info-stat",
    "info-glossary" to "info-definition",
    "info-rule" to "info-formula",
    "info-summary" to "info-definition",

    "mkt-announce" to "mkt-poster",
    "mkt-event" to "mkt-ticket",
    "mkt-offer" to "mkt-poster",
    "mkt-cta" to "mkt-download",
    "mkt-quote" to "info-quote",
    "mkt-stat" to "mkt-milestone",
    "mkt-launch" to "mkt-poster",
    "mkt-alert" to "mkt-notice",
    "mkt-card" to "mkt-download",
    "social-follow" to "mkt-follow"
)

val PostTemplates: List<PostTemplateSpec> by lazy {
    LetterTemplates +
        TypeTemplates +
        PhotoTemplates +
        InfoTemplates +
        MarketingTemplates +
        StudyTemplates +
        DeviceTemplates
}

fun postTemplate(id: String): PostTemplateSpec {
    val mappedId = LegacyTemplateIds[id] ?: id
    return PostTemplates.firstOrNull { it.id == mappedId }
        ?: PostTemplates.firstOrNull { it.id == id }
        ?: PostTemplates.first()
}
