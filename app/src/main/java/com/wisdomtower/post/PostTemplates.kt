package com.wisdomtower.post

import android.graphics.RectF
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
    HEXAGON,
    DEVICE_3D,
    DOC_STACK,
    PEEL_CORNER,
    TAPE_BAND,
    RIBBON_PATH,
    CARD_FAN,
    MONUMENT_SLAB,
    SPOTLIGHT_CONE,
    PORTAL_ARCH,
    STARBURST,
    SPEECH_STAGE,
    CHECK_CARDS,
    DATE_SLAB,
    FLOATING_OBJECT
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
    val rotate: Float = 0f,
    val tiltX: Float = 0f,
    val tiltY: Float = 0f,
    val variant: Int = 0,
    val secondaryTone: PostTone? = null
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
    val radius: Float = 0.028f,
    val tiltX: Float = 0f,
    val tiltY: Float = 0f,
    val photoIndex: Int = 0,
    val rimTone: PostTone? = null,
    val shadow: Boolean = false
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
    val dim: Int = 0,
    val arch: Boolean = false,
    val rimTone: PostTone? = null,
    val shadow: Boolean = false
) {
    val mask: PhotoFrame?
        get() = when {
            arch -> PhotoFrame.ARCH
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
    val bleed: Boolean = false,
    val logoAnchor: LogoAnchor = LogoAnchor.TOP_LEFT,
    val logoKeepOut: RectF? = null,
    val cutoutShowDefault: Boolean = false,
    val cutoutStyle: CutoutStyle = CutoutStyle.RIM_GLOW
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
    val cutoutScale: Float = 1f,
    val cutoutStyle: CutoutStyle? = null
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
    textBehind = if (look.textBehind.isNotEmpty()) look.textBehind else textBehind,
    cutoutStyle = look.cutoutStyle ?: cutoutStyle
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
 * Legacy template ID mapping (Section 6 of docs/TEMPLATE_REDESIGN_PLAN.md + Section 8 of docs/SHOWCASE_TEMPLATES_PLAN.md).
 * Old drafts and inbox batches saved under any removed template ID resolve cleanly to their new showcase replacement.
 */
val LegacyTemplateIds: Map<String, String> = mapOf(
    // First-generation legacy IDs -> current showcase IDs
    "bold-quote" to "info-pullquote",
    "bold-stat" to "info-stat",
    "bold-tip" to "info-bignumber",
    "bold-words" to "type-spotlightcut",
    "bold-minimal" to "type-auroraword",
    "bold-ribbon" to "ltr-bigword",
    "bold-versus" to "info-strikereveal",
    "bold-callout" to "mkt-alerttape",
    "bold-steps" to "info-bignumber",
    "bold-banner" to "type-ink",

    "split-left" to "ltr-mirrorsplit",
    "split-top" to "ltr-number",
    "split-corner" to "type-grid",
    "split-diagonal" to "ltr-bigword",
    "split-band" to "type-chips",
    "split-pillar" to "ltr-coverline",
    "split-card" to "info-definition",
    "split-frame" to "mkt-poster",
    "split-step" to "info-twinphones",
    "split-arch" to "ltr-portal",

    "photo-full" to "photo-floor",
    "photo-circle" to "photo-voiceframe",
    "photo-card" to "photo-polaroid",
    "photo-left" to "photo-cornerpeel",
    "photo-top" to "photo-cover",
    "photo-badge" to "photo-gueststage",
    "photo-frame" to "photo-cover",
    "photo-strip" to "photo-triptych",
    "photo-arch" to "photo-behind",
    "photo-quote" to "photo-voiceframe",
    "photo-corner" to "photo-cornerpeel",
    "photo-split" to "photo-triptych",
    "photo-spotlight" to "photo-behind",
    "photo-banner" to "photo-floor",

    "info-list" to "info-bignumber",
    "info-compare" to "info-strikereveal",
    "info-fact" to "info-stat",
    "info-glossary" to "info-definition",
    "info-rule" to "info-formula",
    "info-summary" to "info-definition",

    "mkt-announce" to "mkt-poster",
    "mkt-event" to "mkt-ticket",
    "mkt-offer" to "mkt-poster",
    "mkt-cta" to "mkt-docstack",
    "mkt-quote" to "info-pullquote",
    "mkt-stat" to "mkt-monument",
    "mkt-launch" to "mkt-poster",
    "mkt-alert" to "mkt-alerttape",
    "mkt-card" to "mkt-docstack",
    "social-follow" to "mkt-follow",

    // Second-generation 20 replaced IDs -> new showcase IDs (SHOWCASE_TEMPLATES_PLAN.md Section 8)
    "ltr-window" to "ltr-portal",
    "ltr-dropcap" to "ltr-coverline",
    "ltr-split" to "ltr-mirrorsplit",
    "type-aurora" to "type-auroraword",
    "type-spotlight" to "type-spotlightcut",
    "photo-idcard" to "photo-gueststage",
    "photo-testimonial" to "photo-voiceframe",
    "photo-tag" to "photo-cornerpeel",
    "info-quote" to "info-pullquote",
    "info-versus" to "info-strikereveal",
    "info-tip" to "info-bignumber",
    "info-checklist" to "info-triplestack",
    "info-quiz" to "info-questionhero",
    "info-qa" to "info-twinphones",
    "info-flashcard" to "info-cardfan",
    "info-timeline" to "info-ribbonpath",
    "info-timetable" to "info-dateslab",
    "mkt-milestone" to "mkt-monument",
    "mkt-notice" to "mkt-alerttape",
    "mkt-download" to "mkt-docstack"
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
    var mappedId = id
    var hops = 0
    while (LegacyTemplateIds.containsKey(mappedId) && hops < 4) {
        mappedId = LegacyTemplateIds[mappedId] ?: break
        hops++
    }
    return PostTemplates.firstOrNull { it.id == mappedId }
        ?: PostTemplates.firstOrNull { it.id == id }
        ?: PostTemplates.first()
}
