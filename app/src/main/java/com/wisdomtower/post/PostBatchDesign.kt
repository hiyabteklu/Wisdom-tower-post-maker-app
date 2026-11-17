package com.wisdomtower.post

import android.content.Context
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.json.JSONObject

/**
 * Batch design controls (docs/MATH_AND_TEMPLATES_PLAN.md, Step D).
 *
 * The review screen of a batch (PostBatchScreen) shows one Design panel under the preview. A switch at the top of the panel picks
 * the scope: "This page" changes only the post on screen, "Entire batch" changes every post the same way. A design change only
 * touches look fields (template, text color, background, fonts, text size); the words of a post are never read or written here,
 * so switching a template or a color can not lose or replace content, and the LaTeX source stays as typed.
 *
 * Nothing here has been compiled yet (see the plan).
 */

/**
 * Switches template and keeps exactly the words the post has. PostDoc.withTemplate also adds the new template's SAMPLE words for
 * every box the post has no text for; in a batch that put sample text (for example "SQ3R") on real posts, so batches use this instead.
 * Words typed for a box the new template does not draw stay stored, and come back when the template is switched back.
 */
internal fun PostDoc.withTemplateKeepingWords(spec: PostTemplateSpec): PostDoc {
    val next = copy(templateId = spec.id)
    val look = spec.look ?: return next
    return next.withLook(look)
}

/**
 * Designs of an inbox batch, kept on this phone so they survive closing and reopening the batch.
 * One entry per batch: the design of the whole batch and the own design of every page that was edited on its own.
 */
internal object PostDesignStore {
    private const val PREFS = "wt_post_designs"

    class Saved(val batch: PostDoc?, val pages: Map<Int, PostDoc>)

    private fun prefs(context: Context) = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun load(context: Context, batchId: String?): Saved {
        val none = Saved(null, emptyMap())
        if (batchId.isNullOrBlank()) return none
        val raw = prefs(context).getString(batchId, null) ?: return none
        return runCatching {
            val j = JSONObject(raw)
            val batch = j.optString("batch").takeIf { it.isNotBlank() }?.let { PostCodec.decodeDoc(it) }
            val pages = LinkedHashMap<Int, PostDoc>()
            val o = j.optJSONObject("pages")
            if (o != null) {
                for (key in o.keys()) {
                    val number = key.toIntOrNull() ?: continue
                    pages[number] = PostCodec.decodeDoc(o.getString(key))
                }
            }
            Saved(batch, pages)
        }.getOrDefault(none)
    }

    fun save(context: Context, batchId: String, batch: PostDoc, pages: Map<Int, PostDoc>) {
        runCatching {
            val o = JSONObject()
            for ((number, design) in pages) o.put(number.toString(), PostCodec.encodeDoc(design))
            val j = JSONObject().put("batch", PostCodec.encodeDoc(batch)).put("pages", o)
            prefs(context).edit().putString(batchId, j.toString()).apply()
        }
    }
}

private val TEXT_COLORS = listOf("#18221D", "#252B31", "#122A69", "#176B4D", "#D11A26", "#7C3AED", "#FFFFFF", "#F4F6EF")
private val SOLID_BACKGROUNDS = listOf("#FFFFFF", "#F4F6EF", "#FFF4D6", "#E6F0FF", "#18221D", "#0B1F3A", "#122A69", "#176B4D")
private val GRADIENT_BACKGROUNDS = listOf(
    "#1FA37A" to "#0B3B2C",
    "#122A69" to "#0B1F3A",
    "#7C3AED" to "#2E1065",
    "#F59E0B" to "#B45309"
)

@Composable
private fun ScrollRow(content: @Composable RowScope.() -> Unit) {
    Row(
        Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}

/** A font chip drawn in its own font, so the choice can be seen before it is tapped. */
@Composable
private fun FontOption(f: PostFonts.FontChoice, selected: Boolean, onClick: () -> Unit) {
    val context = LocalContext.current
    val family = remember(f.path) { PostFonts.typeface(context, f.path)?.let { FontFamily(it) } }
    FilterChip(selected = selected, onClick = onClick, label = { Text(f.label, fontFamily = family, fontSize = 15.sp) })
}

/**
 * The Design panel of the review step.
 * design is what the post on screen looks like now (used to show what is selected); onChange is called with a function that
 * turns one design into the changed design. The screen applies that function to the page or to the whole batch, by the scope.
 */
@Composable
internal fun BatchDesignPanel(
    postCount: Int,
    pageNumber: Int,
    entireBatch: Boolean,
    onScope: (Boolean) -> Unit,
    design: PostDoc,
    brand: PostBrand,
    onChange: ((PostDoc) -> PostDoc) -> Unit
) {
    val context = LocalContext.current
    val family = remember(design.templateId) { postTemplate(design.templateId).family }
    val templates = remember(family) { PostTemplates.filter { it.family == family } }
    val english = remember { PostFonts.list(context, PostFonts.ENGLISH) }
    val amharic = remember { PostFonts.list(context, PostFonts.AMHARIC) }
    val inkColors = remember(brand) {
        (listOf(brand.primaryHex, brand.secondaryHex, brand.lightHex, brand.darkHex) + TEXT_COLORS).distinctBy { it.uppercase() }
    }

    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Design", color = PostPalette.Text, fontWeight = FontWeight.Bold)
        ScrollRow {
            FilterChip(selected = !entireBatch, onClick = { onScope(false) }, label = { Text("Edit this only · $pageNumber") })
            FilterChip(selected = entireBatch, onClick = { onScope(true) }, label = { Text("Edit all · $postCount") })
        }
        PostSmallLabel(
            if (entireBatch) "Changes apply to all $postCount posts. Words are never changed."
            else "Changes apply to post $pageNumber only. Words are never changed."
        )

        PostSmallLabel(if (entireBatch) "Template · applies to every page" else "Template · this page only")
        ScrollRow {
            templates.forEach { spec ->
                FilterChip(
                    selected = design.templateId == spec.id,
                    onClick = { onChange { it.withTemplateKeepingWords(spec) } },
                    label = { Text(spec.name) }
                )
            }
        }

        PostSmallLabel("Text color")
        ScrollRow {
            PostSwatch(null, design.textHex == null) { onChange { it.copy(textHex = null) } }
            inkColors.forEach { hex ->
                PostSwatch(hex, design.textHex.equals(hex, ignoreCase = true)) { onChange { it.copy(textHex = hex) } }
            }
        }

        PostSmallLabel("Background")
        ScrollRow {
            PostSwatch(null, design.bgMode == BgMode.TEMPLATE) { onChange { it.copy(bgMode = BgMode.TEMPLATE) } }
            SOLID_BACKGROUNDS.forEach { hex ->
                PostSwatch(hex, design.bgMode == BgMode.SOLID && design.bgStartHex.equals(hex, ignoreCase = true)) {
                    onChange { it.copy(bgMode = BgMode.SOLID, bgStartHex = hex) }
                }
            }
        }
        ScrollRow {
            GRADIENT_BACKGROUNDS.forEach { (from, to) ->
                val selected = design.bgMode == BgMode.GRADIENT &&
                    design.bgStartHex.equals(from, ignoreCase = true) && design.bgEndHex.equals(to, ignoreCase = true)
                PostSwatch(from, selected) { onChange { it.copy(bgMode = BgMode.GRADIENT, bgStartHex = from, bgEndHex = to) } }
            }
        }
        PostSmallLabel("A = the template's own look. On a dark background the ink turns light by itself.")

        PostSmallLabel("Font")
        ScrollRow {
            FilterChip(selected = design.fontEnglish == null, onClick = { onChange { it.copy(fontEnglish = null) } }, label = { Text("Template font") })
            english.forEach { f -> FontOption(f, design.fontEnglish == f.path) { onChange { it.copy(fontEnglish = f.path) } } }
        }
        if (amharic.isNotEmpty()) {
            PostSmallLabel("Amharic font")
            ScrollRow {
                FilterChip(selected = design.fontAmharic == null, onClick = { onChange { it.copy(fontAmharic = null) } }, label = { Text("Default") })
                amharic.forEach { f -> FontOption(f, design.fontAmharic == f.path) { onChange { it.copy(fontAmharic = f.path) } } }
            }
        }

        PostLabeledSlider("Text size", design.textScale, 0.6f..1.6f, "${(design.textScale * 100).toInt()}%") { v ->
            onChange { it.copy(textScale = v) }
        }
    }
}
