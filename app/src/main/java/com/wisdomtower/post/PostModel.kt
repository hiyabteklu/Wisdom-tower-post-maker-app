package com.wisdomtower.post

import org.json.JSONArray
import org.json.JSONObject

/**
 * The export sizes. Everything in the renderer is stored as fractions, so a size is just width and height.
 * PORTRAIT (4:5, Instagram feed) has no layouts of its own yet: every template falls into its 1:1 branch for it.
 * Export order and the order of the size chips follow this order.
 */
enum class PostSize(val label: String, val width: Int, val height: Int) {
    SQUARE("1:1", 1080, 1080),
    PORTRAIT("4:5", 1080, 1350),
    LANDSCAPE("4:3", 1440, 1080),
    STORY("9:16", 1080, 1920)
}

object PostSlots {
    const val EYEBROW = "eyebrow"
    const val HEADLINE = "headline"
    const val BODY = "body"
    const val NUMBER = "number"
    const val LIST = "list"
    const val BUTTON = "button"

    fun label(slot: String): String = when (slot) {
        EYEBROW -> "Small label"
        HEADLINE -> "Headline"
        BODY -> "Message"
        NUMBER -> "Big number or code"
        LIST -> "List (one item per line)"
        BUTTON -> "Button text"
        else -> slot
    }
}

enum class LogoCorner { LEFT, CENTER, RIGHT }

/** LINE: a thin rule with the icons under it. PILL: every icon and handle sits in its own rounded chip. */
enum class FooterStyle { LINE, PILL }

/** PLAIN: just the glyph. BADGE: glyph on a circle in the brand (or item) color. OFFICIAL: glyph on a circle in the platform's own color. */
enum class IconLook { PLAIN, BADGE, OFFICIAL }

/** TEMPLATE keeps the template's own gradient. SOLID and GRADIENT use the colors picked on the post. */
enum class BgMode { TEMPLATE, SOLID, GRADIENT }

/** kind picks the icon. label is the text shown next to it. colorHex is an optional own color for that icon (empty = automatic). */
data class FooterItem(val kind: String, val label: String, val colorHex: String = "")

/**
 * A small graphic placed on one post (UI_UX_PLAN.md section 6).
 * source is "shape:<name>" for a bundled flat shape, or "file:<absolute path>" for a picture copied into app storage.
 * cx and cy are the centre as a fraction of the post width and height. size is the longest side as a fraction of the shorter post side.
 * tintHex null keeps the picture's own colors (a shape then uses the brand color); a color tints the whole icon.
 * front true draws it above the words, false draws it behind them.
 */
data class PostIconItem(
    val source: String,
    val cx: Float = 0.5f,
    val cy: Float = 0.5f,
    val size: Float = 0.18f,
    val tintHex: String? = null,
    val front: Boolean = true
)

const val MAX_POST_ICONS = 6

data class PostBrand(
    val primaryHex: String = "#176B4D",
    val secondaryHex: String = "#C9E7B8",
    val lightHex: String = "#F4F6EF",
    val darkHex: String = "#18221D",
    val logoPath: String? = null,
    val logoStamp: Long = 0L,
    val logoCorner: LogoCorner = LogoCorner.LEFT,
    val logoScale: Float = 1f,
    val footerItems: List<FooterItem> = listOf(FooterItem("website", "www.wisdom-tower-academy.live")),
    val footerStyle: FooterStyle = FooterStyle.PILL,
    val iconLook: IconLook = IconLook.OFFICIAL,
    val iconScale: Float = 1f
)

/**
 * One post: which template, which size, the words and the style choices. No layers.
 * A null color means "use the brand color", so changing the Brand screen restyles every post that has no override.
 * photoDim -1 means "use the template's own darkening".
 */
data class PostDoc(
    val templateId: String = "info-quote",
    val size: PostSize = PostSize.SQUARE,
    val texts: Map<String, String> = mapOf(
        PostSlots.EYEBROW to "TODAY'S THOUGHT",
        PostSlots.HEADLINE to "Small steps still move you forward.",
        PostSlots.BODY to "Progress is built by showing up consistently."
    ),
    val bgMode: BgMode = BgMode.TEMPLATE,
    val bgStartHex: String = "#1FA37A",
    val bgEndHex: String = "#0B3B2C",
    val bgAngle: Int = 135,
    /** Full-canvas photo layer, independent from a template photo slot. */
    val backgroundPhotoUri: String? = null,
    val backgroundPhotoOpacity: Float = 1f,
    val accentHex: String? = null,
    val textHex: String? = null,
    val textScale: Float = 1f,
    val photoUri: String? = null,
    val photoZoom: Float = 1f,
    val photoFocusX: Float = 0.5f,
    val photoFocusY: Float = 0.5f,
    val photoDim: Int = -1,
    val showLogo: Boolean = true,
    val showFooter: Boolean = true,
    /** Free logo position on this post: centre of the logo as a fraction of the post width and height. null = the Brand screen's corner. */
    val logoCx: Float? = null,
    val logoCy: Float? = null,
    /** Multiplies the Brand logo size for this post only. */
    val logoSize: Float = 1f,
    /** Path of the English font picked on this post (an asset path, or a file added from the phone). null = the default font. */
    val fontEnglish: String? = null,
    /** Path of the Amharic font picked on this post. null = the first Amharic font available. */
    val fontAmharic: String? = null,
    /** Small graphics the user placed on this post (at most MAX_POST_ICONS). */
    val icons: List<PostIconItem> = emptyList(),
    /** Engine (document version 4). A photo frame picked on this post; null keeps the template's own photo shape. */
    val photoFrame: PhotoFrame? = null,
    /** A light outline around a framed photo. */
    val frameBorder: Boolean = false,
    /** Soft color blends over the background gradient, plus a light grain so gradients do not band. */
    val meshStyle: MeshStyle = MeshStyle.OFF,
    val grain: Boolean = false,
    /** A transparent PNG of a subject (absolute path in app storage). Drawn between the "behind" and the "front" text. */
    val cutoutUri: String? = null,
    val cutoutScale: Float = 1f,
    /** Horizontal centre and bottom edge of the cut-out, as fractions of the post width and height. */
    val cutoutX: Float = 0.5f,
    val cutoutY: Float = 0.94f,
    /** Text slots drawn behind the cut-out subject. */
    val textBehind: Set<String> = emptySet(),
    /** Dragged text positions, keyed "templateId:SIZE:slot" (see PostEngine.offsetFor). */
    val slotOffsets: Map<String, SlotOffset> = emptyMap()
) {
    fun withText(slot: String, value: String): PostDoc = copy(texts = texts + (slot to value))
}

object PostCodec {
    /**
     * Drafts saved before the Clean/Rich switch was removed still carry a "richness" number. It is not read any more and not written.
     */
    fun encodeDoc(d: PostDoc): String = JSONObject().apply {
        put("version", 4)
        put("templateId", d.templateId)
        put("size", d.size.name)
        put("texts", JSONObject().also { o -> d.texts.forEach { (k, v) -> o.put(k, v) } })
        put("bgMode", d.bgMode.name)
        put("bgStartHex", d.bgStartHex)
        put("bgEndHex", d.bgEndHex)
        put("bgAngle", d.bgAngle)
        put("backgroundPhotoUri", d.backgroundPhotoUri ?: "")
        put("backgroundPhotoOpacity", d.backgroundPhotoOpacity.toDouble())
        put("accentHex", d.accentHex ?: "")
        put("textHex", d.textHex ?: "")
        put("textScale", d.textScale.toDouble())
        put("photoUri", d.photoUri ?: "")
        put("photoZoom", d.photoZoom.toDouble())
        put("photoFocusX", d.photoFocusX.toDouble())
        put("photoFocusY", d.photoFocusY.toDouble())
        put("photoDim", d.photoDim)
        put("showLogo", d.showLogo)
        put("showFooter", d.showFooter)
        put("logoCx", d.logoCx?.toDouble() ?: -1.0)
        put("logoCy", d.logoCy?.toDouble() ?: -1.0)
        put("logoSize", d.logoSize.toDouble())
        put("fontEnglish", d.fontEnglish ?: "")
        put("fontAmharic", d.fontAmharic ?: "")
        put("icons", JSONArray().also { arr ->
            d.icons.forEach {
                arr.put(
                    JSONObject()
                        .put("source", it.source)
                        .put("cx", it.cx.toDouble())
                        .put("cy", it.cy.toDouble())
                        .put("size", it.size.toDouble())
                        .put("tintHex", it.tintHex ?: "")
                        .put("front", it.front)
                )
            }
        })
        put("photoFrame", d.photoFrame?.name ?: "")
        put("frameBorder", d.frameBorder)
        put("meshStyle", d.meshStyle.name)
        put("grain", d.grain)
        put("cutoutUri", d.cutoutUri ?: "")
        put("cutoutScale", d.cutoutScale.toDouble())
        put("cutoutX", d.cutoutX.toDouble())
        put("cutoutY", d.cutoutY.toDouble())
        put("textBehind", JSONArray().also { arr -> d.textBehind.forEach { arr.put(it) } })
        put("slotOffsets", JSONObject().also { o ->
            d.slotOffsets.forEach { (k, v) -> o.put(k, JSONObject().put("dx", v.dx.toDouble()).put("dy", v.dy.toDouble())) }
        })
    }.toString()

    /** The first test build stored the default colors as if they were choices. Treat those as "use the brand color". */
    private fun colorOverride(j: JSONObject, key: String, legacyDefault: String): String? =
        j.optString(key).takeIf { it.isNotBlank() && it != "null" && !it.equals(legacyDefault, ignoreCase = true) }

    private fun decodeIcons(j: JSONObject): List<PostIconItem> {
        val arr = j.optJSONArray("icons") ?: return emptyList()
        return (0 until arr.length()).mapNotNull { i ->
            val o = arr.optJSONObject(i) ?: return@mapNotNull null
            val source = o.optString("source")
            if (source.isBlank()) return@mapNotNull null
            PostIconItem(
                source = source,
                cx = o.optDouble("cx", 0.5).toFloat().coerceIn(0f, 1f),
                cy = o.optDouble("cy", 0.5).toFloat().coerceIn(0f, 1f),
                size = o.optDouble("size", 0.18).toFloat().coerceIn(0.04f, 0.9f),
                tintHex = o.optString("tintHex").takeIf { it.isNotBlank() && it != "null" },
                front = o.optBoolean("front", true)
            )
        }.take(MAX_POST_ICONS)
    }

    private fun decodeTextBehind(j: JSONObject): Set<String> {
        val arr = j.optJSONArray("textBehind") ?: return emptySet()
        return (0 until arr.length()).map { arr.optString(it) }.filter { it.isNotBlank() }.toSet()
    }

    private fun decodeOffsets(j: JSONObject): Map<String, SlotOffset> {
        val o = j.optJSONObject("slotOffsets") ?: return emptyMap()
        val out = LinkedHashMap<String, SlotOffset>()
        o.keys().forEach { key ->
            val v = o.optJSONObject(key) ?: return@forEach
            out[key] = SlotOffset(
                dx = v.optDouble("dx", 0.0).toFloat().coerceIn(-1f, 1f),
                dy = v.optDouble("dy", 0.0).toFloat().coerceIn(-1f, 1f)
            )
        }
        return out
    }

    fun decodeDoc(raw: String): PostDoc {
        val j = JSONObject(raw)
        val base = PostDoc()
        val t = j.optJSONObject("texts")
        val texts = if (t == null) base.texts else t.keys().asSequence().associateWith { t.optString(it) }
        return PostDoc(
            templateId = j.optString("templateId", base.templateId),
            size = runCatching { PostSize.valueOf(j.optString("size", "SQUARE")) }.getOrDefault(PostSize.SQUARE),
            texts = texts,
            bgMode = runCatching { BgMode.valueOf(j.optString("bgMode", "TEMPLATE")) }.getOrDefault(BgMode.TEMPLATE),
            bgStartHex = j.optString("bgStartHex", base.bgStartHex).ifBlank { base.bgStartHex },
            bgEndHex = j.optString("bgEndHex", base.bgEndHex).ifBlank { base.bgEndHex },
            bgAngle = j.optInt("bgAngle", base.bgAngle),
            backgroundPhotoUri = j.optString("backgroundPhotoUri").takeIf { it.isNotBlank() && it != "null" },
            backgroundPhotoOpacity = j.optDouble("backgroundPhotoOpacity", 1.0).toFloat().coerceIn(0f, 1f),
            accentHex = colorOverride(j, "accentHex", "#176B4D"),
            textHex = colorOverride(j, "textHex", "#18221D"),
            textScale = j.optDouble("textScale", 1.0).toFloat(),
            photoUri = j.optString("photoUri").takeIf { it.isNotBlank() && it != "null" },
            photoZoom = j.optDouble("photoZoom", 1.0).toFloat(),
            photoFocusX = j.optDouble("photoFocusX", 0.5).toFloat(),
            photoFocusY = j.optDouble("photoFocusY", 0.5).toFloat(),
            photoDim = j.optInt("photoDim", -1),
            showLogo = j.optBoolean("showLogo", true),
            showFooter = j.optBoolean("showFooter", true),
            logoCx = j.optDouble("logoCx", -1.0).toFloat().takeIf { it >= 0f },
            logoCy = j.optDouble("logoCy", -1.0).toFloat().takeIf { it >= 0f },
            logoSize = j.optDouble("logoSize", 1.0).toFloat().coerceIn(0.4f, 3f),
            fontEnglish = j.optString("fontEnglish").takeIf { it.isNotBlank() && it != "null" },
            fontAmharic = j.optString("fontAmharic").takeIf { it.isNotBlank() && it != "null" },
            icons = decodeIcons(j),
            photoFrame = runCatching { PhotoFrame.valueOf(j.optString("photoFrame", "")) }.getOrNull(),
            frameBorder = j.optBoolean("frameBorder", false),
            meshStyle = runCatching { MeshStyle.valueOf(j.optString("meshStyle", "OFF")) }.getOrDefault(MeshStyle.OFF),
            grain = j.optBoolean("grain", false),
            cutoutUri = j.optString("cutoutUri").takeIf { it.isNotBlank() && it != "null" },
            cutoutScale = j.optDouble("cutoutScale", 1.0).toFloat().coerceIn(0.3f, 2.5f),
            cutoutX = j.optDouble("cutoutX", 0.5).toFloat().coerceIn(0f, 1f),
            cutoutY = j.optDouble("cutoutY", 0.94).toFloat().coerceIn(0.2f, 1.2f),
            textBehind = decodeTextBehind(j),
            slotOffsets = decodeOffsets(j)
        )
    }

    fun encodeBrand(b: PostBrand): String = JSONObject().apply {
        put("primaryHex", b.primaryHex)
        put("secondaryHex", b.secondaryHex)
        put("lightHex", b.lightHex)
        put("darkHex", b.darkHex)
        put("logoPath", b.logoPath ?: "")
        put("logoStamp", b.logoStamp)
        put("logoCorner", b.logoCorner.name)
        put("logoScale", b.logoScale.toDouble())
        put("footerStyle", b.footerStyle.name)
        put("iconLook", b.iconLook.name)
        put("iconScale", b.iconScale.toDouble())
        put("footerItems", JSONArray().also { arr ->
            b.footerItems.forEach { arr.put(JSONObject().put("kind", it.kind).put("label", it.label).put("colorHex", it.colorHex)) }
        })
    }.toString()

    fun decodeBrand(raw: String): PostBrand {
        val j = JSONObject(raw)
        val base = PostBrand()
        val arr = j.optJSONArray("footerItems")
        val items = if (arr == null) base.footerItems else (0 until arr.length()).mapNotNull { i ->
            arr.optJSONObject(i)?.let { FooterItem(it.optString("kind", "website"), it.optString("label", ""), it.optString("colorHex", "")) }
        }
        return PostBrand(
            primaryHex = j.optString("primaryHex", base.primaryHex),
            secondaryHex = j.optString("secondaryHex", base.secondaryHex),
            lightHex = j.optString("lightHex", base.lightHex),
            darkHex = j.optString("darkHex", base.darkHex),
            logoPath = j.optString("logoPath").takeIf { it.isNotBlank() && it != "null" },
            logoStamp = j.optLong("logoStamp", 0L),
            logoCorner = runCatching { LogoCorner.valueOf(j.optString("logoCorner", "LEFT")) }.getOrDefault(LogoCorner.LEFT),
            logoScale = j.optDouble("logoScale", 1.0).toFloat(),
            footerItems = items,
            footerStyle = runCatching { FooterStyle.valueOf(j.optString("footerStyle", "PILL")) }.getOrDefault(FooterStyle.PILL),
            iconLook = runCatching { IconLook.valueOf(j.optString("iconLook", "OFFICIAL")) }.getOrDefault(IconLook.OFFICIAL),
            iconScale = j.optDouble("iconScale", 1.0).toFloat()
        )
    }
}
