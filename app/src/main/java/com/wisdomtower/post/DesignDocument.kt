package com.wisdomtower.post

import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

enum class CanvasPreset(val label: String, val width: Int, val height: Int) {
    SQUARE("Square · 1:1", 1080, 1080),
    PORTRAIT_9_16("Story · 9:16", 1080, 1920),
    A4_PORTRAIT("A4 Portrait", 2480, 3508),
    A4_LANDSCAPE("A4 Landscape", 3508, 2480),
    PORTRAIT_4_5("Feed Portrait · 4:5", 1080, 1350),
    LANDSCAPE_16_9("Landscape · 16:9", 1920, 1080)
}

enum class LayerType { TEXT, IMAGE, SHAPE }

data class DesignLayer(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "Text layer",
    val type: LayerType = LayerType.TEXT,
    val text: String = "",
    val x: Float = 0.08f,
    val y: Float = 0.12f,
    val width: Float = 0.84f,
    val height: Float = 0.18f,
    val rotation: Float = 0f,
    val visible: Boolean = true,
    val locked: Boolean = false,
    val colorHex: String = "#18221D",
    val secondaryColorHex: String = "#176B4D",
    val fontSize: Float = 0.055f,
    val fontWeight: Int = 700,
    val fontFamily: String = "sans-serif",
    val textAlign: String = "start",
    val opacity: Float = 1f,
    val imageUri: String? = null,
    val imageFit: String = "cover",
    val shape: String = "rectangle",
    val gradientEnabled: Boolean = false
)

data class DesignDocument(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "Untitled design",
    val canvasPreset: CanvasPreset = CanvasPreset.SQUARE,
    val backgroundColor: String = "#F4F6EF",
    val gradientStart: String = "#F4F6EF",
    val gradientEnd: String = "#D7E8D8",
    val gradientAngle: Int = 45,
    val useGradient: Boolean = false,
    val layers: List<DesignLayer> = emptyList(),
    val selectedLayerId: String? = layers.firstOrNull()?.id,
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun selectedLayer(): DesignLayer? = layers.firstOrNull { it.id == selectedLayerId }

    fun replaceLayer(updated: DesignLayer): DesignDocument = copy(
        layers = layers.map { if (it.id == updated.id) updated else it },
        updatedAt = System.currentTimeMillis()
    )

    fun selectLayer(id: String?): DesignDocument = copy(selectedLayerId = id)

    fun addLayer(layer: DesignLayer): DesignDocument = copy(
        layers = layers + layer,
        selectedLayerId = layer.id,
        updatedAt = System.currentTimeMillis()
    )

    fun deleteSelectedLayer(): DesignDocument {
        val remaining = layers.filterNot { it.id == selectedLayerId }
        return copy(
            layers = remaining,
            selectedLayerId = remaining.lastOrNull()?.id,
            updatedAt = System.currentTimeMillis()
        )
    }

    fun moveLayer(id: String, offset: Int): DesignDocument {
        if (layers.isEmpty()) return this
        val from = layers.indexOfFirst { it.id == id }
        if (from < 0) return this
        val to = (from + offset).coerceIn(0, layers.lastIndex)
        if (to !in layers.indices || from == to) return this
        val reordered = layers.toMutableList()
        val item = reordered.removeAt(from)
        reordered.add(to, item)
        return copy(layers = reordered, updatedAt = System.currentTimeMillis())
    }
}

object DesignDocumentCodec {
    fun encode(document: DesignDocument): String = JSONObject().apply {
        put("id", document.id)
        put("name", document.name)
        put("canvasPreset", document.canvasPreset.name)
        put("backgroundColor", document.backgroundColor)
        put("gradientStart", document.gradientStart)
        put("gradientEnd", document.gradientEnd)
        put("gradientAngle", document.gradientAngle)
        put("useGradient", document.useGradient)
        put("selectedLayerId", document.selectedLayerId)
        put("updatedAt", document.updatedAt)
        put("layers", JSONArray().apply {
            document.layers.forEach { layer ->
                put(JSONObject().apply {
                    put("id", layer.id)
                    put("name", layer.name)
                    put("type", layer.type.name)
                    put("text", layer.text)
                    put("x", layer.x.toDouble())
                    put("y", layer.y.toDouble())
                    put("width", layer.width.toDouble())
                    put("height", layer.height.toDouble())
                    put("rotation", layer.rotation.toDouble())
                    put("visible", layer.visible)
                    put("locked", layer.locked)
                    put("colorHex", layer.colorHex)
                    put("secondaryColorHex", layer.secondaryColorHex)
                    put("fontSize", layer.fontSize.toDouble())
                    put("fontWeight", layer.fontWeight)
                    put("fontFamily", layer.fontFamily)
                    put("textAlign", layer.textAlign)
                    put("opacity", layer.opacity.toDouble())
                    put("imageUri", layer.imageUri)
                    put("imageFit", layer.imageFit)
                    put("shape", layer.shape)
                    put("gradientEnabled", layer.gradientEnabled)
                })
            }
        })
    }.toString()

    fun decode(raw: String): DesignDocument {
        val json = JSONObject(raw)
        val layersJson = json.optJSONArray("layers") ?: JSONArray()
        val layers = (0 until layersJson.length()).mapNotNull { index ->
            val item = layersJson.optJSONObject(index) ?: return@mapNotNull null
            DesignLayer(
                id = item.optString("id", UUID.randomUUID().toString()),
                name = item.optString("name", "Layer"),
                type = runCatching { LayerType.valueOf(item.optString("type", "TEXT")) }.getOrDefault(LayerType.TEXT),
                text = item.optString("text", ""),
                x = item.optDouble("x", 0.08).toFloat(),
                y = item.optDouble("y", 0.12).toFloat(),
                width = item.optDouble("width", 0.84).toFloat(),
                height = item.optDouble("height", 0.18).toFloat(),
                rotation = item.optDouble("rotation", 0.0).toFloat(),
                visible = item.optBoolean("visible", true),
                locked = item.optBoolean("locked", false),
                colorHex = item.optString("colorHex", "#18221D"),
                secondaryColorHex = item.optString("secondaryColorHex", "#176B4D"),
                fontSize = item.optDouble("fontSize", 0.055).toFloat(),
                fontWeight = item.optInt("fontWeight", 700),
                fontFamily = item.optString("fontFamily", "sans-serif"),
                textAlign = item.optString("textAlign", "start"),
                opacity = item.optDouble("opacity", 1.0).toFloat(),
                imageUri = item.optString("imageUri").takeIf { it.isNotBlank() && it != "null" },
                imageFit = item.optString("imageFit", "cover"),
                shape = item.optString("shape", "rectangle"),
                gradientEnabled = item.optBoolean("gradientEnabled", false)
            )
        }
        return DesignDocument(
            id = json.optString("id", UUID.randomUUID().toString()),
            name = json.optString("name", "Untitled design"),
            canvasPreset = runCatching { CanvasPreset.valueOf(json.optString("canvasPreset", "SQUARE")) }.getOrDefault(CanvasPreset.SQUARE),
            backgroundColor = json.optString("backgroundColor", "#F4F6EF"),
            gradientStart = json.optString("gradientStart", "#F4F6EF"),
            gradientEnd = json.optString("gradientEnd", "#D7E8D8"),
            gradientAngle = json.optInt("gradientAngle", 45),
            useGradient = json.optBoolean("useGradient", false),
            layers = layers,
            selectedLayerId = json.optString("selectedLayerId").takeIf { it.isNotBlank() && it != "null" },
            updatedAt = json.optLong("updatedAt", System.currentTimeMillis())
        )
    }
}

/** Fifteen curated starting colors; custom HEX colors can be added by the editor. */
val DesignPalette = listOf(
    "#176B4D", "#0F3D2E", "#8FB996", "#D7E8D8", "#F4F6EF",
    "#18221D", "#FFFFFF", "#172923", "#C9E7B8", "#385D9D",
    "#B65C32", "#FFF4D8", "#E8A23A", "#8D4B70", "#7D8790"
)

data class TemplateDefinition(
    val id: String,
    val name: String,
    val category: String,
    val background: String,
    val accent: String,
    val styleTag: String,
    val eyebrow: String,
    val headline: String,
    val body: String,
    val footer: String,
    val useGradient: Boolean = false
) {
    fun createDocument(preset: CanvasPreset = CanvasPreset.SQUARE): DesignDocument {
        val dark = background.equals("#172923", true) || background.equals("#0F3D2E", true)
        var eyebrowY = 0.19f
        var headlineY = 0.29f
        var headlineH = 0.25f
        var headlineSize = 0.064f
        var bodyY = 0.59f
        var bodyH = 0.22f
        var bodySize = 0.032f
        var centered = false

        when (id) {
            "quote-minimal", "milestone" -> { eyebrowY = 0.23f; headlineY = 0.35f; headlineH = 0.25f; bodyY = 0.65f; bodyH = 0.16f; centered = true }
            "quote-warm" -> { eyebrowY = 0.17f; headlineY = 0.31f; headlineH = 0.28f; bodyY = 0.64f; bodyH = 0.18f }
            "motivation-green" -> { eyebrowY = 0.23f; headlineY = 0.34f; headlineH = 0.29f; headlineSize = 0.073f; bodyY = 0.66f; bodyH = 0.16f }
            "motivation-sunrise" -> { eyebrowY = 0.22f; headlineY = 0.36f; headlineH = 0.25f; headlineSize = 0.071f; bodyY = 0.65f; bodyH = 0.19f }
            "mcq-classic", "mcq-midnight" -> { eyebrowY = 0.17f; headlineY = 0.27f; headlineH = 0.16f; headlineSize = 0.055f; bodyY = 0.48f; bodyH = 0.32f; bodySize = 0.029f }
            "exam-alert" -> { eyebrowY = 0.22f; headlineY = 0.32f; headlineH = 0.24f; bodyY = 0.62f; bodyH = 0.2f }
            "countdown" -> { eyebrowY = 0.25f; headlineY = 0.37f; headlineH = 0.25f; headlineSize = 0.071f; bodyY = 0.66f; bodyH = 0.17f }
            "study-tip" -> { eyebrowY = 0.17f; headlineY = 0.29f; headlineH = 0.21f; bodyY = 0.56f; bodyH = 0.24f }
            "short-note" -> { eyebrowY = 0.2f; headlineY = 0.3f; headlineH = 0.2f; bodyY = 0.53f; bodyH = 0.27f; bodySize = 0.03f }
            "subject-spotlight" -> { eyebrowY = 0.22f; headlineY = 0.31f; headlineH = 0.25f; bodyY = 0.61f; bodyH = 0.19f }
            "course-card" -> { eyebrowY = 0.24f; headlineY = 0.36f; headlineH = 0.23f; bodyY = 0.63f; bodyH = 0.18f }
            "app-feature" -> { eyebrowY = 0.2f; headlineY = 0.32f; headlineH = 0.27f; headlineSize = 0.069f; bodyY = 0.63f; bodyH = 0.2f }
            "app-promo" -> { eyebrowY = 0.23f; headlineY = 0.36f; headlineH = 0.27f; headlineSize = 0.072f; bodyY = 0.66f; bodyH = 0.17f }
            "reminder" -> { eyebrowY = 0.21f; headlineY = 0.33f; headlineH = 0.24f; bodyY = 0.62f; bodyH = 0.2f }
            "schedule" -> { eyebrowY = 0.17f; headlineY = 0.27f; headlineH = 0.16f; headlineSize = 0.056f; bodyY = 0.46f; bodyH = 0.34f; bodySize = 0.03f }
            "announcement-clean" -> { eyebrowY = 0.23f; headlineY = 0.34f; headlineH = 0.25f; bodyY = 0.63f; bodyH = 0.2f }
            "announcement-gradient" -> { eyebrowY = 0.2f; headlineY = 0.34f; headlineH = 0.28f; headlineSize = 0.07f; bodyY = 0.66f; bodyH = 0.18f }
        }

        val decorations = when (id) {
            "motivation-sunrise" -> listOf(DesignLayer(name = "Sun accent", type = LayerType.SHAPE, shape = "circle", x = .68f, y = .1f, width = .23f, height = .23f, colorHex = accent, opacity = .28f))
            "exam-alert" -> listOf(DesignLayer(name = "Alert stripe", type = LayerType.SHAPE, x = .035f, y = .16f, width = .025f, height = .68f, colorHex = accent))
            "countdown" -> listOf(DesignLayer(name = "Countdown accent", type = LayerType.SHAPE, shape = "circle", x = .68f, y = .15f, width = .25f, height = .25f, colorHex = accent, opacity = .22f))
            "mcq-classic", "mcq-midnight" -> listOf(DesignLayer(name = "Answer panel", type = LayerType.SHAPE, x = .06f, y = .46f, width = .88f, height = .35f, colorHex = accent, opacity = .12f))
            "study-tip" -> listOf(DesignLayer(name = "Accent rule", type = LayerType.SHAPE, x = .08f, y = .25f, width = .28f, height = .012f, colorHex = accent))
            "app-feature" -> listOf(DesignLayer(name = "Feature glow", type = LayerType.SHAPE, shape = "circle", x = .69f, y = .12f, width = .23f, height = .23f, colorHex = accent, opacity = .18f))
            "milestone" -> listOf(
                DesignLayer(name = "Celebration dot 1", type = LayerType.SHAPE, shape = "circle", x = .12f, y = .19f, width = .045f, height = .045f, colorHex = accent),
                DesignLayer(name = "Celebration dot 2", type = LayerType.SHAPE, shape = "circle", x = .82f, y = .25f, width = .065f, height = .065f, colorHex = accent),
                DesignLayer(name = "Celebration dot 3", type = LayerType.SHAPE, shape = "circle", x = .18f, y = .73f, width = .035f, height = .035f, colorHex = accent)
            )
            "announcement-gradient" -> listOf(DesignLayer(name = "Gradient ribbon", type = LayerType.SHAPE, x = .08f, y = .12f, width = .84f, height = .025f, colorHex = accent, secondaryColorHex = "#8FB996", gradientEnabled = true))
            else -> emptyList()
        }

        val textLayers = listOf(
            DesignLayer(name = "Brand label", text = "WISDOM TOWER ACADEMY",
                x = .08f, y = .08f, width = .84f, height = .07f, colorHex = accent, fontSize = .018f, fontWeight = 700),
            DesignLayer(name = "Eyebrow", text = eyebrow,
                x = .08f, y = eyebrowY, width = .84f, height = .06f, colorHex = accent, fontSize = .018f, fontWeight = 700,
                textAlign = if (centered) "center" else "start"),
            DesignLayer(name = "Headline", text = headline,
                x = .08f, y = headlineY, width = .84f, height = headlineH,
                colorHex = if (dark) "#FFFFFF" else "#18221D", fontSize = headlineSize, fontWeight = 800,
                textAlign = if (centered) "center" else "start"),
            DesignLayer(name = "Message", text = body,
                x = .08f, y = bodyY, width = .84f, height = bodyH,
                colorHex = if (dark) "#E0E8E2" else "#46514A", fontSize = bodySize, fontWeight = 400,
                textAlign = if (centered) "center" else "start"),
            DesignLayer(name = "Footer", text = footer,
                x = .08f, y = .88f, width = .84f, height = .05f, colorHex = accent, fontSize = .018f, fontWeight = 700,
                textAlign = if (centered) "center" else "start")
        )
        return DesignDocument(
            name = name, canvasPreset = preset, backgroundColor = background,
            gradientStart = background, gradientEnd = "#D7E8D8", useGradient = useGradient,
            layers = decorations + textLayers
        )
    }
}

val StarterTemplates = listOf(
    TemplateDefinition("quote-evergreen", "Evergreen Quote", "Quotes", "#F4F6EF", "#176B4D", "Editorial", "TODAY'S THOUGHT", "Small steps still move you forward.", "Progress is built by showing up consistently.", "LEARN • GROW • ACHIEVE"),
    TemplateDefinition("quote-midnight", "Midnight Quote", "Quotes", "#172923", "#C9E7B8", "Dark", "A THOUGHT TO KEEP", "Your future is shaped by what you practise today.", "Keep going, even when the progress feels quiet.", "STAY CURIOUS", true),
    TemplateDefinition("quote-minimal", "Quiet Words", "Quotes", "#FFFFFF", "#385D9D", "Minimal", "A MOMENT OF CLARITY", "Make room for what matters.", "One idea. One action. One better day.", "ONE IDEA AT A TIME"),
    TemplateDefinition("motivation-green", "Keep Going", "Motivation", "#0F3D2E", "#8FB996", "Bold", "FOR THE DAYS YOU DOUBT", "Keep going. Your effort is adding up.", "Even an imperfect study session counts.", "ONE DAY AT A TIME", true),
    TemplateDefinition("motivation-sunrise", "New Day", "Motivation", "#FFF4D8", "#E8A23A", "Bright", "A NEW CHANCE", "Start before you feel ready.", "Ten focused minutes can break the delay.", "BEGIN AGAIN"),
    TemplateDefinition("mcq-classic", "Daily MCQ", "Questions", "#FFFFFF", "#176B4D", "Question", "QUESTION OF THE DAY", "Can you solve this?", "A. First option\nB. Second option\nC. Third option\nD. Fourth option", "COMMENT YOUR ANSWER"),
    TemplateDefinition("mcq-midnight", "Challenge Mode", "Questions", "#172923", "#C9E7B8", "Dark", "DAILY CHALLENGE", "Test what you know.", "A. Option one\nB. Option two\nC. Option three\nD. Option four", "ANSWER • LEARN • REPEAT", true),
    TemplateDefinition("exam-alert", "Exam Alert", "Exam Prep", "#0F3D2E", "#FFFFFF", "Announcement", "IMPORTANT ANNOUNCEMENT", "Exam preparation starts now.", "Review the key topics and make a realistic plan.", "SAVE • SHARE • PREPARE"),
    TemplateDefinition("countdown", "Countdown", "Exam Prep", "#172923", "#E8A23A", "Countdown", "THE COUNTDOWN IS ON", "Your exam date is getting closer.", "Use today wisely. Revise, practise, review.", "MAKE TODAY COUNT", true),
    TemplateDefinition("study-tip", "Study Tip", "Study Tips", "#F4F6EF", "#176B4D", "Editorial", "STUDY SMARTER", "Test yourself before rereading.", "Retrieval practice helps you discover what you really remember.", "A TIP FROM WISDOM TOWER"),
    TemplateDefinition("short-note", "Quick Note", "Study Tips", "#FFF4D8", "#B65C32", "Notebook", "QUICK REVISION NOTE", "One concept at a time.", "Keep the definition short, the example clear, and the practice active.", "SAVE FOR REVISION"),
    TemplateDefinition("subject-spotlight", "Subject Spotlight", "Subjects", "#FFFFFF", "#385D9D", "Clean", "SUBJECT SPOTLIGHT", "Make Physics make sense.", "Key ideas, worked examples and practice questions in one place.", "EXPLORE • UNDERSTAND"),
    TemplateDefinition("course-card", "Course Focus", "Subjects", "#D7E8D8", "#0F3D2E", "Fresh", "TODAY'S FOCUS", "One chapter. Clear progress.", "Choose a topic, practise questions, and track what improves.", "YOUR NEXT STEP"),
    TemplateDefinition("app-feature", "Feature Spotlight", "App Promotion", "#172923", "#8FB996", "Product", "INSIDE WISDOM TOWER", "Learn with explanations, not just answers.", "Explore worked solutions and guided practice in the app.", "YOUR STUDY COMPANION", true),
    TemplateDefinition("app-promo", "App Launch", "App Promotion", "#176B4D", "#FFFFFF", "Promo", "BUILT FOR YOUR STUDIES", "Your learning toolkit in one place.", "Notes, questions, flashcards and exam preparation.", "DISCOVER WISDOM TOWER"),
    TemplateDefinition("reminder", "Study Reminder", "Reminders", "#FFF4D8", "#B65C32", "Warm", "A GENTLE REMINDER", "Have you done today's review?", "A few focused minutes now can save stress later.", "CHECK YOUR PLAN"),
    TemplateDefinition("schedule", "Study Schedule", "Reminders", "#FFFFFF", "#176B4D", "Planner", "TODAY'S STUDY PLAN", "Protect your focus time.", "1. Review\n2. Practise\n3. Check mistakes", "PLAN • FOCUS • FINISH"),
    TemplateDefinition("milestone", "Progress Milestone", "Achievements", "#D7E8D8", "#176B4D", "Celebration", "PROGRESS WORTH CELEBRATING", "Another step completed.", "Small improvements become meaningful results over time.", "KEEP BUILDING"),
    TemplateDefinition("announcement-clean", "Clean Announcement", "Announcements", "#FFFFFF", "#18221D", "Minimal", "WISDOM TOWER ACADEMY", "A clear message starts here.", "Add your announcement details and call to action.", "OFFICIAL UPDATE"),
    TemplateDefinition("announcement-gradient", "Gradient Broadcast", "Announcements", "#0F3D2E", "#FFFFFF", "Gradient", "PLEASE TAKE NOTE", "Important information, clearly shared.", "Use this space for the details students need to know.", "WISDOM TOWER ACADEMY", true)
)


fun applyTemplateKeepingContent(current: DesignDocument, template: TemplateDefinition): DesignDocument {
    val replacement = template.createDocument(current.canvasPreset)
    val currentText = current.layers.filter { it.type == LayerType.TEXT }.associateBy { it.name }
    val mergedTemplateLayers = replacement.layers.map { newLayer ->
        val oldLayer = currentText[newLayer.name]
        if (oldLayer != null && newLayer.type == LayerType.TEXT) newLayer.copy(
            text = oldLayer.text,
            fontSize = oldLayer.fontSize,
            fontWeight = oldLayer.fontWeight,
            fontFamily = oldLayer.fontFamily,
            textAlign = oldLayer.textAlign
        ) else newLayer
    }
    val templateDecorationNames = setOf(
        "Sun accent", "Alert stripe", "Countdown accent", "Answer panel",
        "Accent rule", "Feature glow", "Celebration dot 1", "Celebration dot 2",
        "Celebration dot 3", "Gradient ribbon"
    )
    val templateTextNames = setOf("Brand label", "Eyebrow", "Headline", "Message", "Footer")
    val customLayers = current.layers.filter { layer ->
        layer.type == LayerType.IMAGE ||
            (layer.type == LayerType.SHAPE && layer.name !in templateDecorationNames) ||
            (layer.type == LayerType.TEXT && layer.name !in templateTextNames)
    }
    val mergedLayers = mergedTemplateLayers + customLayers
    val oldSelectedName = current.selectedLayer()?.name
    val selected = mergedLayers.firstOrNull { it.name == oldSelectedName }?.id ?: mergedLayers.firstOrNull()?.id
    return replacement.copy(
        name = current.name,
        layers = mergedLayers,
        selectedLayerId = selected,
        updatedAt = System.currentTimeMillis()
    )
}
