package com.wisdomtower.post

import org.json.JSONArray
import org.json.JSONObject
import java.text.BreakIterator
import kotlin.math.roundToInt

enum class PostSize(val label: String, val width: Int, val height: Int) {
    LANDSCAPE("4:3", 1440, 1080),
    STORY("9:16", 1080, 1920),
    WIDE("16:9", 1920, 1080);

    fun <T> pick3(story: T, standard: T, wide: T): T = when (this) {
        STORY -> story
        LANDSCAPE -> standard
        WIDE -> wide
    }

    companion object {
        fun fromKey(key: String?): PostSize = values().firstOrNull {
            it.name.equals(key, ignoreCase = true) || it.label == key
        } ?: LANDSCAPE
    }
}

object PostSlots {
    const val LETTER = "letter"
    const val WORD = "word"
    const val EYEBROW = "eyebrow"
    const val HEADLINE = "headline"
    const val BODY = "body"
    const val NUMBER = "number"
    const val LIST = "list"
    const val BUTTON = "button"
    const val MARKS = "marks"
    const val FOOTER = "footer"

    val ALL = listOf(LETTER, WORD, EYEBROW, HEADLINE, BODY, NUMBER, LIST, BUTTON)

    fun label(slot: String): String = when (slot) {
        LETTER -> "Big letter"
        WORD -> "Big word"
        EYEBROW -> "Small label"
        HEADLINE -> "Headline"
        BODY -> "Message"
        NUMBER -> "Number"
        LIST -> "List"
        BUTTON -> "Button"
        MARKS -> "Page marks"
        FOOTER -> "Footer"
        else -> slot.replaceFirstChar { it.uppercase() }
    }

    fun containsEthiopic(text: String): Boolean {
        for (ch in text) {
            val code = ch.code
            if (code in 0x1200..0x137F || code in 0x1380..0x139F || code in 0x2D80..0x2DDF || code in 0xAB00..0xAB2F) {
                return true
            }
        }
        return false
    }

    fun graphemes(text: String, maxCount: Int = Int.MAX_VALUE): List<String> {
        val clean = text.trim()
        if (clean.isEmpty() || maxCount <= 0) return emptyList()
        val bi = BreakIterator.getCharacterInstance()
        bi.setText(clean)
        val out = mutableListOf<String>()
        var start = bi.first()
        var end = bi.next()
        while (end != BreakIterator.DONE && out.size < maxCount) {
            val g = clean.substring(start, end)
            if (g.isNotBlank()) out.add(g)
            start = end
            end = bi.next()
        }
        return out
    }

    fun firstGraphemes(text: String, maxCount: Int = 1): String =
        graphemes(text, maxCount.coerceAtLeast(1)).joinToString("")

    fun deriveLetter(word: String?, headline: String?): String {
        val source = word?.trim()?.takeIf { it.isNotEmpty() }
            ?: headline?.trim()?.takeIf { it.isNotEmpty() }
            ?: return ""
        val cleaned = source.trimStart { !it.isLetterOrDigit() && Character.getType(it) != Character.OTHER_LETTER.toInt() }
        return firstGraphemes(cleaned.ifEmpty { source }, 1)
    }

    fun deriveWord(headline: String?): String {
        val clean = headline?.trim().orEmpty()
        if (clean.isEmpty()) return ""
        val tokens = clean.split(Regex("\\s+"))
            .map { it.trim { ch -> ch in ".,!?;:\"'()[]{}*«»—–-" } }
            .filter { it.isNotEmpty() }
        if (tokens.isEmpty()) return ""
        val stopWords = setOf("the", "a", "an", "of", "in", "on", "to", "for", "and", "or", "is", "are", "with", "by")
        return tokens.firstOrNull { it.length >= 3 && it.lowercase() !in stopWords } ?: tokens.first()
    }

    fun withDerivedSlots(texts: Map<String, String>, requiredSlots: Iterable<String>): Map<String, String> {
        val out = texts.toMutableMap()
        val req = requiredSlots.toSet()
        if (WORD in req && out[WORD].isNullOrBlank()) {
            val derivedW = deriveWord(out[HEADLINE])
            if (derivedW.isNotBlank()) out[WORD] = derivedW
        }
        if (LETTER in req && out[LETTER].isNullOrBlank()) {
            val derivedL = deriveLetter(out[WORD], out[HEADLINE])
            if (derivedL.isNotBlank()) out[LETTER] = derivedL
        }
        return out
    }
}

enum class FitMode {
    LEGACY,
    FILL,
    FILL_WIDTH,
    STACK,
    TICKER,
    TILES,
    CHIPS
}

enum class VAnchor {
    TOP,
    CENTER,
    BOTTOM
}

enum class TextFx {
    SOLID,
    OUTLINE,
    GRADIENT,
    PHOTO_FILL,
    ECHO,
    EXTRUDE,
    SPLIT_TONE
}

enum class ShadowMode {
    SOFT,
    HARD,
    GLOW
}

data class TextShadow(
    val strength: Int = 0,
    val mode: ShadowMode = ShadowMode.SOFT,
    val colorHex: String? = null,
    val angle: Int = 135,
    val distance: Int = 35,
    val blur: Int = 55
) {
    val angleDeg: Int get() = angle
    val isDefault: Boolean get() = strength <= 0
}

enum class BgPhotoTone {
    NONE,
    MONO,
    DUOTONE
}

enum class BgMode {
    TEMPLATE,
    SOLID,
    GRADIENT
}

enum class TextAlign {
    LEFT,
    CENTER,
    RIGHT
}

data class SlotStyle(
    val colorHex: String? = null,
    val fontEn: String? = null,
    val fontAm: String? = null,
    val scale: Float = 1f,
    val sizeUnit: Float? = null,
    val shadow: TextShadow? = null,
    val fx: TextFx? = null,
    val tracking: Float? = null,
    val leading: Float? = null,
    val rotate: Float? = null,
    val align: TextAlign? = null,
    val caps: Boolean? = null
) {
    val isDefault: Boolean
        get() = colorHex == null &&
            fontEn == null &&
            fontAm == null &&
            scale == 1f &&
            sizeUnit == null &&
            (shadow == null || shadow.strength == 0) &&
            fx == null &&
            tracking == null &&
            leading == null &&
            rotate == null &&
            align == null &&
            caps == null
}

enum class FooterStyle { PILL, LINE }

enum class IconLook { PLAIN, BADGE, OFFICIAL }

data class FooterItem(
    val kind: String,
    val label: String,
    val colorHex: String = ""
)

data class PostBrand(
    val name: String = "Wisdom Tower",
    val handle: String = "@wisdomtower",
    val primaryHex: String = "#22E0FF",
    val secondaryHex: String = "#FFD54A",
    val lightHex: String = "#FFFFFF",
    val darkHex: String = "#060B14",
    val bgLook: Int = 1,
    val logoPath: String? = null,
    val logoStamp: Long = 0L,
    val logoScale: Float = 1f,
    val footerStyle: FooterStyle = FooterStyle.PILL,
    val iconLook: IconLook = IconLook.OFFICIAL,
    val iconScale: Float = 1f,
    val footerItems: List<FooterItem> = listOf(
        FooterItem("telegram", "@wisdomtower"),
        FooterItem("youtube", "Wisdom Tower")
    )
) {
    val accentHex: String get() = primaryHex
    val logoUri: String? get() = logoPath
}

enum class PhotoFrame(val label: String) {
    ROUNDED("Rounded"),
    PILL("Pill"),
    CIRCLE("Circle"),
    ARCH("Arch"),
    STACKED("Stacked")
}

enum class MeshStyle(val label: String) {
    NONE("None"),
    AURORA("Aurora"),
    SPOTLIGHT("Spotlight"),
    DUSK("Dusk");

    companion object {
        val OFF = NONE
    }
}

data class Offset(val dx: Float = 0f, val dy: Float = 0f)

const val MAX_POST_ICONS = 8

data class PostIconItem(
    val source: String,
    val cx: Float = 0.5f,
    val cy: Float = 0.5f,
    val size: Float = 0.14f,
    val tintHex: String? = null,
    val front: Boolean = true
)

data class PostDoc(
    val version: Int = 4,
    val templateId: String = "ltr-window",
    val size: PostSize = PostSize.LANDSCAPE,
    val texts: Map<String, String> = PostSlots.withDerivedSlots(
        postTemplate("ltr-window").samples,
        postTemplate("ltr-window").slots
    ),
    val hiddenTextSlots: Set<String> = emptySet(),
    val textHex: String? = null,
    val accentHex: String? = null,
    val backgroundHex: String? = null,
    val bgMode: BgMode = BgMode.TEMPLATE,
    val bgStartHex: String = "#0B1F3A",
    val bgEndHex: String = "#050910",
    val bgAngle: Float = 90f,
    val backdrop: Backdrop? = null,
    val textScale: Float = 1f,
    val fontEnglish: String? = null,
    val fontAmharic: String? = null,
    val photoUri: String? = null,
    val photoZoom: Float = 1f,
    val photoPanX: Float = 0f,
    val photoPanY: Float = 0f,
    val photoFocusX: Float = 0.5f,
    val photoFocusY: Float = 0.5f,
    val photoDim: Int? = null,
    val photoFrame: PhotoFrame? = null,
    val frameBorder: Boolean = false,
    val backgroundPhotoUri: String? = null,
    val backgroundPhotoOpacity: Float = 0.35f,
    val backgroundPhotoDim: Int = 0,
    val backgroundPhotoBlur: Int = 0,
    val backgroundPhotoZoom: Float = 1f,
    val backgroundPhotoFocusX: Float = 0.5f,
    val backgroundPhotoFocusY: Float = 0.5f,
    val backgroundPhotoTone: BgPhotoTone = BgPhotoTone.NONE,
    val photoAsBackground: Boolean = false,
    val panelOpacity: Int = 100,
    val shadowAll: TextShadow = TextShadow(),
    val lookIndex: Int = 0,
    val meshStyle: MeshStyle = MeshStyle.NONE,
    val grain: Boolean = false,
    val cutoutUri: String? = null,
    val cutoutScale: Float = 1f,
    val cutoutX: Float = 0.5f,
    val cutoutY: Float = 0.94f,
    val textBehind: Set<String> = emptySet(),
    val showLogo: Boolean = true,
    val logoCx: Float? = null,
    val logoCy: Float? = null,
    val logoSize: Float = 1f,
    val showFooter: Boolean = true,
    val offsets: Map<String, Offset> = emptyMap(),
    val slotStyles: Map<String, SlotStyle> = emptyMap(),
    val icons: List<PostIconItem> = emptyList()
) {
    val backgroundPhotoAlpha: Float get() = backgroundPhotoOpacity
    fun hasOffsets(): Boolean = offsets.values.any { it.dx != 0f || it.dy != 0f }
    fun withoutOffsets(): PostDoc = copy(offsets = emptyMap())
    fun offsetOf(slot: String): Offset = offsets[slot] ?: Offset()
    fun withOffset(slot: String, offset: Offset): PostDoc = copy(offsets = offsets + (slot to offset))
}

object PostCodec {
    fun encodeDoc(doc: PostDoc): String {
        val root = JSONObject()
        root.put("version", 4)
        root.put("templateId", doc.templateId)
        root.put("size", doc.size.name)
        val textsObj = JSONObject()
        doc.texts.forEach { (k, v) -> textsObj.put(k, v) }
        root.put("texts", textsObj)
        if (doc.hiddenTextSlots.isNotEmpty()) {
            val hArr = JSONArray()
            doc.hiddenTextSlots.forEach { hArr.put(it) }
            root.put("hiddenTextSlots", hArr)
        }
        doc.textHex?.let { root.put("textHex", it) }
        doc.accentHex?.let { root.put("accentHex", it) }
        doc.backgroundHex?.let { root.put("backgroundHex", it) }
        if (doc.bgMode != BgMode.TEMPLATE) root.put("bgMode", doc.bgMode.name)
        root.put("bgStartHex", doc.bgStartHex)
        root.put("bgEndHex", doc.bgEndHex)
        if (doc.bgAngle != 90f) root.put("bgAngle", doc.bgAngle.toDouble())
        doc.backdrop?.let { b ->
            root.put("backdropFrom", b.from.name)
            root.put("backdropTo", b.to.name)
            root.put("backdropAngle", b.angle.toDouble())
        }
        root.put("textScale", doc.textScale.toDouble())
        doc.fontEnglish?.let { root.put("fontEnglish", it) }
        doc.fontAmharic?.let { root.put("fontAmharic", it) }
        doc.photoUri?.let { root.put("photoUri", it) }
        root.put("photoZoom", doc.photoZoom.toDouble())
        root.put("photoPanX", doc.photoPanX.toDouble())
        root.put("photoPanY", doc.photoPanY.toDouble())
        root.put("photoFocusX", doc.photoFocusX.toDouble())
        root.put("photoFocusY", doc.photoFocusY.toDouble())
        doc.photoDim?.let { root.put("photoDim", it) }
        doc.photoFrame?.let { root.put("photoFrame", it.name) }
        root.put("frameBorder", doc.frameBorder)
        doc.backgroundPhotoUri?.let { root.put("backgroundPhotoUri", it) }
        root.put("backgroundPhotoOpacity", doc.backgroundPhotoOpacity.toDouble())
        if (doc.backgroundPhotoDim != 0) root.put("backgroundPhotoDim", doc.backgroundPhotoDim)
        if (doc.backgroundPhotoBlur != 0) root.put("backgroundPhotoBlur", doc.backgroundPhotoBlur)
        if (doc.backgroundPhotoZoom != 1f) root.put("backgroundPhotoZoom", doc.backgroundPhotoZoom.toDouble())
        if (doc.backgroundPhotoFocusX != 0.5f) root.put("backgroundPhotoFocusX", doc.backgroundPhotoFocusX.toDouble())
        if (doc.backgroundPhotoFocusY != 0.5f) root.put("backgroundPhotoFocusY", doc.backgroundPhotoFocusY.toDouble())
        if (doc.backgroundPhotoTone != BgPhotoTone.NONE) root.put("backgroundPhotoTone", doc.backgroundPhotoTone.name)
        if (doc.photoAsBackground) root.put("photoAsBackground", true)
        if (doc.panelOpacity != 100) root.put("panelOpacity", doc.panelOpacity)
        if (!doc.shadowAll.isDefault) root.put("shadowAll", encodeShadow(doc.shadowAll))
        if (doc.lookIndex != 0) root.put("lookIndex", doc.lookIndex)
        root.put("meshStyle", doc.meshStyle.name)
        root.put("grain", doc.grain)
        doc.cutoutUri?.let { root.put("cutoutUri", it) }
        root.put("cutoutScale", doc.cutoutScale.toDouble())
        root.put("cutoutX", doc.cutoutX.toDouble())
        root.put("cutoutY", doc.cutoutY.toDouble())
        val behindArr = JSONArray()
        doc.textBehind.forEach { behindArr.put(it) }
        root.put("textBehind", behindArr)
        root.put("showLogo", doc.showLogo)
        doc.logoCx?.let { root.put("logoCx", it.toDouble()) }
        doc.logoCy?.let { root.put("logoCy", it.toDouble()) }
        root.put("logoSize", doc.logoSize.toDouble())
        root.put("showFooter", doc.showFooter)

        val offsetsObj = JSONObject()
        doc.offsets.forEach { (k, off) ->
            if (off.dx != 0f || off.dy != 0f) {
                offsetsObj.put(k, JSONObject().put("dx", off.dx.toDouble()).put("dy", off.dy.toDouble()))
            }
        }
        root.put("offsets", offsetsObj)
        root.put("slotStyles", encodeSlotStyles(doc.slotStyles))

        val iconsArr = JSONArray()
        doc.icons.forEach { item ->
            val o = JSONObject()
            o.put("source", item.source)
            o.put("cx", item.cx.toDouble())
            o.put("cy", item.cy.toDouble())
            o.put("size", item.size.toDouble())
            item.tintHex?.let { o.put("tintHex", it) }
            o.put("front", item.front)
            iconsArr.put(o)
        }
        root.put("icons", iconsArr)
        return root.toString()
    }

    fun decodeDoc(json: String): PostDoc {
        val root = JSONObject(json)
        val rawTemplateId = root.optString("templateId", "ltr-window")
        val resolvedId = postTemplate(rawTemplateId).id
        val size = PostSize.fromKey(root.optString("size", "LANDSCAPE"))
        val texts = mutableMapOf<String, String>()
        root.optJSONObject("texts")?.let { obj ->
            obj.keys().forEach { k -> texts[k] = obj.optString(k, "") }
        }
        val specSlots = postTemplate(resolvedId).slots
        val hydratedTexts = PostSlots.withDerivedSlots(texts, specSlots)

        val hidden = mutableSetOf<String>()
        root.optJSONArray("hiddenTextSlots")?.let { arr ->
            for (i in 0 until arr.length()) {
                val s = arr.optString(i)
                if (s.isNotBlank()) hidden.add(s)
            }
        }

        val behind = mutableSetOf<String>()
        root.optJSONArray("textBehind")?.let { arr ->
            for (i in 0 until arr.length()) {
                val s = arr.optString(i)
                if (s.isNotBlank()) behind.add(s)
            }
        }

        val offsets = mutableMapOf<String, Offset>()
        root.optJSONObject("offsets")?.let { obj ->
            obj.keys().forEach { k ->
                obj.optJSONObject(k)?.let { o ->
                    offsets[k] = Offset(
                        o.optDouble("dx", 0.0).toFloat(),
                        o.optDouble("dy", 0.0).toFloat()
                    )
                }
            }
        }

        val icons = mutableListOf<PostIconItem>()
        root.optJSONArray("icons")?.let { arr ->
            for (i in 0 until arr.length()) {
                val o = arr.optJSONObject(i) ?: continue
                icons.add(
                    PostIconItem(
                        source = o.optString("source", ""),
                        cx = o.optDouble("cx", 0.5).toFloat(),
                        cy = o.optDouble("cy", 0.5).toFloat(),
                        size = o.optDouble("size", 0.14).toFloat(),
                        tintHex = o.optString("tintHex", "").takeIf { it.isNotBlank() },
                        front = o.optBoolean("front", true)
                    )
                )
            }
        }

        val backdrop = if (root.has("backdropFrom") && root.has("backdropTo")) {
            val from = runCatching { PostTone.valueOf(root.getString("backdropFrom")) }.getOrNull()
            val to = runCatching { PostTone.valueOf(root.getString("backdropTo")) }.getOrNull()
            if (from != null && to != null) Backdrop(from, to, root.optDouble("backdropAngle", 90.0).toFloat()) else null
        } else null

        val bgOpacity = when {
            root.has("backgroundPhotoOpacity") -> root.optDouble("backgroundPhotoOpacity", 0.35).toFloat()
            root.has("backgroundPhotoAlpha") -> root.optDouble("backgroundPhotoAlpha", 0.35).toFloat()
            else -> 0.35f
        }.coerceIn(0f, 1f)

        val bgDim = when {
            !root.has("backgroundPhotoDim") -> 0
            root.optDouble("backgroundPhotoDim", 0.0) in -1.0..1.0 && root.optInt("backgroundPhotoDim", 0) == 0 ->
                (root.optDouble("backgroundPhotoDim", 0.0) * 100.0).roundToInt().coerceIn(0, 100)
            else -> root.optInt("backgroundPhotoDim", 0).coerceIn(0, 100)
        }

        val bgBlur = when {
            !root.has("backgroundPhotoBlur") -> 0
            root.optDouble("backgroundPhotoBlur", 0.0) in 0.0..1.0 && root.optInt("backgroundPhotoBlur", 0) <= 1 ->
                (root.optDouble("backgroundPhotoBlur", 0.0) * 100.0).roundToInt().coerceIn(0, 100)
            else -> root.optInt("backgroundPhotoBlur", 0).coerceIn(0, 100)
        }

        val pOpacity = when {
            !root.has("panelOpacity") -> 100
            root.optDouble("panelOpacity", 100.0) <= 1.0 ->
                (root.optDouble("panelOpacity", 1.0) * 100.0).roundToInt().coerceIn(0, 100)
            else -> root.optInt("panelOpacity", 100).coerceIn(0, 100)
        }

        val shadowAll = root.optJSONObject("shadowAll")?.let { decodeShadow(it) }
            ?: if (root.has("shadowAll")) TextShadow(strength = root.optInt("shadowAll", 0).coerceIn(0, 100))
            else TextShadow()

        return PostDoc(
            version = 4,
            templateId = resolvedId,
            size = size,
            texts = hydratedTexts,
            hiddenTextSlots = hidden,
            textHex = root.optString("textHex", "").takeIf { it.isNotBlank() },
            accentHex = root.optString("accentHex", "").takeIf { it.isNotBlank() },
            backgroundHex = root.optString("backgroundHex", "").takeIf { it.isNotBlank() },
            bgMode = root.optString("bgMode", "").let { name ->
                BgMode.values().firstOrNull { it.name == name } ?: BgMode.TEMPLATE
            },
            bgStartHex = root.optString("bgStartHex", "#0B1F3A"),
            bgEndHex = root.optString("bgEndHex", "#050910"),
            bgAngle = root.optDouble("bgAngle", 90.0).toFloat(),
            backdrop = backdrop,
            textScale = root.optDouble("textScale", 1.0).toFloat().coerceIn(0.4f, 2.5f),
            fontEnglish = root.optString("fontEnglish", "").takeIf { it.isNotBlank() },
            fontAmharic = root.optString("fontAmharic", "").takeIf { it.isNotBlank() },
            photoUri = root.optString("photoUri", "").takeIf { it.isNotBlank() },
            photoZoom = root.optDouble("photoZoom", 1.0).toFloat().coerceIn(0.5f, 4f),
            photoPanX = root.optDouble("photoPanX", 0.0).toFloat().coerceIn(-1f, 1f),
            photoPanY = root.optDouble("photoPanY", 0.0).toFloat().coerceIn(-1f, 1f),
            photoFocusX = root.optDouble("photoFocusX", 0.5).toFloat().coerceIn(0f, 1f),
            photoFocusY = root.optDouble("photoFocusY", 0.5).toFloat().coerceIn(0f, 1f),
            photoDim = if (root.has("photoDim")) root.optInt("photoDim").coerceIn(0, 255) else null,
            photoFrame = root.optString("photoFrame", "").takeIf { it.isNotBlank() }?.let { name ->
                PhotoFrame.values().firstOrNull { it.name == name }
            },
            frameBorder = root.optBoolean("frameBorder", false),
            backgroundPhotoUri = root.optString("backgroundPhotoUri", "").takeIf { it.isNotBlank() },
            backgroundPhotoOpacity = bgOpacity,
            backgroundPhotoDim = bgDim,
            backgroundPhotoBlur = bgBlur,
            backgroundPhotoZoom = root.optDouble("backgroundPhotoZoom", 1.0).toFloat().coerceIn(1f, 3f),
            backgroundPhotoFocusX = root.optDouble("backgroundPhotoFocusX", 0.5).toFloat().coerceIn(0f, 1f),
            backgroundPhotoFocusY = root.optDouble("backgroundPhotoFocusY", 0.5).toFloat().coerceIn(0f, 1f),
            backgroundPhotoTone = root.optString("backgroundPhotoTone", "").let { name ->
                BgPhotoTone.values().firstOrNull { it.name == name } ?: BgPhotoTone.NONE
            },
            photoAsBackground = root.optBoolean("photoAsBackground", false),
            panelOpacity = pOpacity,
            shadowAll = shadowAll,
            lookIndex = root.optInt("lookIndex", 0).coerceIn(0, 2),
            meshStyle = root.optString("meshStyle", "").let { name ->
                MeshStyle.values().firstOrNull { it.name == name } ?: MeshStyle.NONE
            },
            grain = root.optBoolean("grain", false),
            cutoutUri = root.optString("cutoutUri", "").takeIf { it.isNotBlank() },
            cutoutScale = root.optDouble("cutoutScale", 1.0).toFloat().coerceIn(0.3f, 2.5f),
            cutoutX = root.optDouble("cutoutX", 0.5).toFloat().coerceIn(0f, 1f),
            cutoutY = root.optDouble("cutoutY", 0.94).toFloat().coerceIn(0f, 1.2f),
            textBehind = behind,
            showLogo = root.optBoolean("showLogo", true),
            logoCx = if (root.has("logoCx")) root.optDouble("logoCx").toFloat() else null,
            logoCy = if (root.has("logoCy")) root.optDouble("logoCy").toFloat() else null,
            logoSize = root.optDouble("logoSize", 1.0).toFloat().coerceIn(0.4f, 3f),
            showFooter = root.optBoolean("showFooter", true),
            offsets = offsets,
            slotStyles = decodeSlotStyles(root.optJSONObject("slotStyles")),
            icons = icons
        )
    }

    private fun encodeShadow(sh: TextShadow): JSONObject = JSONObject().apply {
        put("strength", sh.strength)
        put("mode", sh.mode.name)
        sh.colorHex?.let { put("colorHex", it) }
        put("angle", sh.angle)
        put("distance", sh.distance)
        put("blur", sh.blur)
    }

    private fun decodeShadow(so: JSONObject): TextShadow = TextShadow(
        strength = so.optInt("strength", 0).coerceIn(0, 100),
        mode = so.optString("mode", "").let { n ->
            ShadowMode.values().firstOrNull { it.name == n } ?: ShadowMode.SOFT
        },
        colorHex = so.optString("colorHex", "").takeIf { it.isNotBlank() },
        angle = so.optInt("angle", so.optInt("angleDeg", 135)),
        distance = so.optInt("distance", 35).coerceIn(0, 100),
        blur = so.optInt("blur", 55).coerceIn(0, 100)
    )

    fun encodeSlotStyles(styles: Map<String, SlotStyle>): JSONObject {
        val obj = JSONObject()
        styles.forEach { (slot, s) ->
            if (s.isDefault) return@forEach
            val o = JSONObject()
            s.colorHex?.let { o.put("colorHex", it) }
            s.fontEn?.let { o.put("fontEn", it) }
            s.fontAm?.let { o.put("fontAm", it) }
            if (s.scale != 1f) o.put("scale", s.scale.toDouble())
            s.sizeUnit?.let { o.put("sizeUnit", it.toDouble()) }
            s.shadow?.takeIf { it.strength > 0 }?.let { sh ->
                o.put("shadow", encodeShadow(sh))
            }
            s.fx?.let { o.put("fx", it.name) }
            s.tracking?.let { o.put("tracking", it.toDouble()) }
            s.leading?.let { o.put("leading", it.toDouble()) }
            s.rotate?.let { o.put("rotate", it.toDouble()) }
            s.align?.let { o.put("align", it.name) }
            s.caps?.let { o.put("caps", it) }
            obj.put(slot, o)
        }
        return obj
    }

    fun decodeSlotStyles(obj: JSONObject?): Map<String, SlotStyle> {
        if (obj == null) return emptyMap()
        val out = mutableMapOf<String, SlotStyle>()
        obj.keys().forEach { slot ->
            val o = obj.optJSONObject(slot) ?: return@forEach
            val shadow = o.optJSONObject("shadow")?.let { decodeShadow(it) }
            val style = SlotStyle(
                colorHex = o.optString("colorHex", "").takeIf { it.isNotBlank() },
                fontEn = o.optString("fontEn", "").takeIf { it.isNotBlank() },
                fontAm = o.optString("fontAm", "").takeIf { it.isNotBlank() },
                scale = o.optDouble("scale", 1.0).toFloat().coerceIn(0.25f, 4.0f),
                sizeUnit = if (o.has("sizeUnit")) o.optDouble("sizeUnit").toFloat().coerceIn(0.015f, 1.6f) else null,
                shadow = shadow,
                fx = o.optString("fx", "").takeIf { it.isNotBlank() }?.let { n ->
                    TextFx.values().firstOrNull { it.name == n }
                },
                tracking = if (o.has("tracking")) o.optDouble("tracking").toFloat().coerceIn(-0.08f, 0.5f) else null,
                leading = if (o.has("leading")) o.optDouble("leading").toFloat().coerceIn(0.75f, 1.8f) else null,
                rotate = if (o.has("rotate")) o.optDouble("rotate").toFloat().coerceIn(-90f, 90f) else null,
                align = o.optString("align", "").takeIf { it.isNotBlank() }?.let { n ->
                    TextAlign.values().firstOrNull { it.name == n }
                },
                caps = if (o.has("caps")) o.optBoolean("caps") else null
            )
            if (!style.isDefault) out[slot] = style
        }
        return out
    }

    fun encodeBrand(brand: PostBrand): String {
        val o = JSONObject()
        o.put("name", brand.name)
        o.put("handle", brand.handle)
        o.put("primaryHex", brand.primaryHex)
        o.put("secondaryHex", brand.secondaryHex)
        o.put("lightHex", brand.lightHex)
        o.put("darkHex", brand.darkHex)
        o.put("bgLook", brand.bgLook)
        brand.logoPath?.let { o.put("logoPath", it) }
        o.put("logoStamp", brand.logoStamp)
        o.put("logoScale", brand.logoScale.toDouble())
        o.put("footerStyle", brand.footerStyle.name)
        o.put("iconLook", brand.iconLook.name)
        o.put("iconScale", brand.iconScale.toDouble())
        val arr = JSONArray()
        brand.footerItems.forEach { item ->
            arr.put(
                JSONObject()
                    .put("kind", item.kind)
                    .put("label", item.label)
                    .put("colorHex", item.colorHex)
            )
        }
        o.put("footerItems", arr)
        return o.toString()
    }

    fun decodeBrand(json: String): PostBrand {
        val o = JSONObject(json)
        val items = mutableListOf<FooterItem>()
        o.optJSONArray("footerItems")?.let { arr ->
            for (i in 0 until arr.length()) {
                val fi = arr.optJSONObject(i) ?: continue
                items.add(
                    FooterItem(
                        kind = fi.optString("kind", "telegram"),
                        label = fi.optString("label", ""),
                        colorHex = fi.optString("colorHex", "")
                    )
                )
            }
        }
        val primary = o.optString("primaryHex", "").ifBlank { o.optString("accentHex", "#22E0FF") }
        val logoPath = o.optString("logoPath", "").ifBlank { o.optString("logoUri", "") }.takeIf { it.isNotBlank() }
        return PostBrand(
            name = o.optString("name", "Wisdom Tower"),
            handle = o.optString("handle", "@wisdomtower"),
            primaryHex = primary,
            secondaryHex = o.optString("secondaryHex", "#FFD54A"),
            lightHex = o.optString("lightHex", "#FFFFFF"),
            darkHex = o.optString("darkHex", "#060B14"),
            bgLook = o.optInt("bgLook", 1),
            logoPath = logoPath,
            logoStamp = o.optLong("logoStamp", 0L),
            logoScale = o.optDouble("logoScale", 1.0).toFloat(),
            footerStyle = o.optString("footerStyle", "").let { n ->
                FooterStyle.values().firstOrNull { it.name == n } ?: FooterStyle.PILL
            },
            iconLook = o.optString("iconLook", "").let { n ->
                IconLook.values().firstOrNull { it.name == n } ?: IconLook.OFFICIAL
            },
            iconScale = o.optDouble("iconScale", 1.0).toFloat(),
            footerItems = if (items.isNotEmpty()) items else PostBrand().footerItems
        )
    }
}
