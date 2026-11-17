package com.wisdomtower.post

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

/**
 * Template QA (docs/APP_QUALITY_ROADMAP.md + docs/TEMPLATE_REDESIGN_PLAN.md section 3.9).
 * Tests every template at 4:3, 9:16, and 16:9 under 6 text modes:
 *   - Sample:        the template's own sample words
 *   - 1 short word:  1 short word ("GROW") in headline/word and 1 letter ("G") in letter
 *   - Long 4-line:   1.5x stretched multi-line text in every box
 *   - Amharic short: short Amharic words ("ዕውቀት", "ጥበብ")
 *   - Amharic long:  full Amharic sentences and list items
 *   - Empty box:     optional boxes (eyebrow, body, button) left empty so only the hero slot renders
 * Plus toggles for background photo (Off / 40% / 100%) and shadow (0 / 60).
 */
private enum class QaText(val label: String) {
    SAMPLE("Sample"),
    SHORT_WORD("1 short word"),
    STRESS("Long 4-line"),
    AMHARIC_SHORT("Amharic short"),
    AMHARIC_LONG("Amharic long"),
    EMPTY_BOX("Empty box"),
    CHAPTER_FIXTURE("Notes QA fixture")
}

private val ChapterFixtureBody = """
| Law | Formula | Unit | Domain | Condition |
|---|---|---|---|---|
| Gauss | ${'$'}\oint \vec{E}\cdot d\vec{A} = \frac{Q}{\varepsilon_0}${'$'} | ${'$'}\text{N}\cdot\text{m}^2/\text{C}${'$'} | Electrostatics | Closed surface |
| Faraday | ${'$'}\mathcal{E} = -\frac{d\Phi_B}{dt}${'$'} | ${'$'}\text{V}${'$'} | Induction | Time-varying ${'$'}\vec{B}${'$'} |

${'$'}${'$'}\begin{bmatrix} 2 & -1 & 0 & 4 \\ 0 & 3 & -2 & 1 \\ 1 & 0 & 5 & -3 \\ -1 & 2 & 1 & 6 \end{bmatrix} \quad \left[\begin{array}{ccc|c} 1 & 2 & -1 & 4 \\ 0 & 1 & 3 & -2 \\ 0 & 0 & 1 & 5 \end{array}\right]${'$'}${'$'}

${'$'}${'$'}f(x) = \begin{cases} \frac{\int_0^x e^{-t^2}\,dt}{1 + \frac{x^2}{1 + x}} & x \ge 0 \\ -\sqrt{1 - x^3} & x < 0 \end{cases}${'$'}${'$'}

${'$'}${'$'}\ce{N2(g) + 3H2(g)} \xrightleftharpoons[\text{450}^\circ\text{C}]{\text{Fe cat.}} \ce{2NH3(g)} \xrightarrow[\Delta]{\text{H2O}} \ce{2NH4+(aq) + 2OH-(aq)}${'$'}${'$'}

የኒውተን ሁለተኛ ሕግ እና የኤሌክትሮማግኔቲክ መስክ መርሆዎች በከፍተኛ ትምህርት ፈተናዎች ውስጥ በጣም አስፈላጊ ናቸው።
""".trimIndent()

private val ChapterFixtureList = """
1. Conservation of Energy: ${'$'}\Delta E = W + Q${'$'}
  - Kinetic: ${'$'}K = \frac{1}{2}mv^2${'$'}
  - Potential: ${'$'}U = mgh${'$'}
2. Linear Systems: Augmented matrix row reduction
  - Pivot selection and scaling
  - Back-substitution check
3. Calculus & Integrals: ${'$'}\int_a^b f'(x)\,dx = f(b) - f(a)${'$'}
  - Improper bounds and Gaussian tails
4. Chemical Equilibrium: Le Chatelier shift under pressure
  - Forward catalyst rate vs reverse rate
5. ኢትዮጵያዊ የጥናት ዘዴ: ምዕራፉን ዘግተህ ከትውስታህ ጻፍ
  - የቀድሞ ፈተና ጥያቄዎችን በሰዓት ገደብ መስራት
""".trimIndent()

private fun stretchLine(text: String, factor: Float): String {
    val colon = text.indexOf(':')
    if (colon in 1..8) return text.substring(0, colon + 1) + " " + stretchLine(text.substring(colon + 1).trim(), factor)
    val target = (text.length * factor).toInt()
    if (target <= text.length) return text
    val words = text.split(' ').filter { it.isNotEmpty() }
    if (words.isEmpty()) return text
    val out = StringBuilder(text)
    var i = 0
    while (out.length < target) {
        out.append(' ').append(words[i % words.size])
        i++
    }
    return out.toString()
}

private fun amharicShortFor(slot: String, sample: String): String = when (slot) {
    PostSlots.LETTER -> "ዕ"
    PostSlots.WORD -> "ዕውቀት"
    PostSlots.NUMBER -> "98%"
    PostSlots.EYEBROW -> "የጥናት ምክር"
    PostSlots.HEADLINE -> "ዕውቀት ያድጋል"
    PostSlots.BODY -> "በየቀኑ ትንሽ እርምጃ ውሰድ።"
    PostSlots.BUTTON -> "ዛሬ ጀምር"
    PostSlots.LIST -> {
        val count = sample.split('\n').count { it.isNotBlank() }.coerceIn(1, 4)
        val rows = sample.contains(':')
        val plain = listOf("ምዕራፍ ክለሳ", "የቀድሞ ፈተና", "ስህተት ማስተካከል", "እረፍት እና ክለሳ")
        val days = listOf("ሰኞ", "ማክሰ", "ረቡዕ", "ሐሙስ")
        (0 until count).joinToString("\n") { if (rows) days[it] + ": " + plain[it] else plain[it] }
    }
    else -> sample
}

private fun amharicLongFor(slot: String, sample: String): String = when (slot) {
    PostSlots.LETTER -> "ጥ"
    PostSlots.WORD -> "ትውስታህ"
    PostSlots.NUMBER -> "100%"
    PostSlots.EYEBROW -> "የጥናት ምክር እና መመሪያ"
    PostSlots.HEADLINE -> "ምዕራፉን ዝጋ። ከትውስታህ መልስ።"
    PostSlots.BODY -> "ምዕራፉን ዘግተህ የምታስታውሰውን ጻፍ። ከዚያ ያመለጠህን አረጋግጥ።"
    PostSlots.BUTTON -> "አሁኑኑ ይጀምሩ"
    PostSlots.LIST -> {
        val count = sample.split('\n').count { it.isNotBlank() }.coerceIn(1, 6)
        val rows = sample.contains(':')
        val plain = listOf("ምዕራፍ ክለሳ", "የቀድሞ ፈተና", "ስህተት ማስተካከል", "እረፍት እና ክለሳ", "ሙከራ ፈተና", "ጥያቄ መመለስ")
        val days = listOf("ሰኞ", "ማክሰ", "ረቡዕ", "ሐሙስ", "አርብ", "ቅዳሜ")
        (0 until count).joinToString("\n") { if (rows) days[it] + ": " + plain[it] else plain[it] }
    }
    else -> sample
}

private fun ensureQaBgPhotoUri(context: android.content.Context): String {
    val file = File(context.cacheDir, "qa_test_bg.png")
    if (!file.exists()) {
        val bmp = Bitmap.createBitmap(360, 360, Bitmap.Config.ARGB_8888)
        val c = Canvas(bmp)
        val p = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                0f, 0f, 360f, 360f,
                intArrayOf(Color.parseColor("#0EA5E9"), Color.parseColor("#6366F1"), Color.parseColor("#F59E0B")),
                floatArrayOf(0f, 0.5f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        c.drawRect(0f, 0f, 360f, 360f, p)
        FileOutputStream(file).use { bmp.compress(Bitmap.CompressFormat.PNG, 90, it) }
        bmp.recycle()
    }
    return Uri.fromFile(file).toString()
}

private fun qaDoc(
    context: android.content.Context,
    spec: PostTemplateSpec,
    size: PostSize,
    mode: QaText,
    bgPhotoPct: Int,
    shadowStrength: Int
): PostDoc {
    val base = PostDrafts.factory(spec.id, size)
    val texts = spec.samples.mapValues { (slot, sample) ->
        when (mode) {
            QaText.SAMPLE -> sample
            QaText.SHORT_WORD -> when (slot) {
                PostSlots.LETTER -> "G"
                PostSlots.WORD -> "GROW"
                PostSlots.HEADLINE -> "GROW"
                PostSlots.NUMBER -> "1"
                PostSlots.EYEBROW -> "TIP"
                PostSlots.BODY -> "Start now."
                PostSlots.BUTTON -> "GO"
                else -> sample
            }
            QaText.STRESS -> when (slot) {
                PostSlots.LETTER -> sample
                PostSlots.WORD -> sample + "S"
                else -> sample.split('\n').joinToString("\n") { stretchLine(it, 1.5f) }
            }
            QaText.AMHARIC_SHORT -> amharicShortFor(slot, sample)
            QaText.AMHARIC_LONG -> amharicLongFor(slot, sample)
            QaText.EMPTY_BOX -> when (slot) {
                PostSlots.EYEBROW, PostSlots.BODY, PostSlots.BUTTON -> ""
                else -> sample
            }
            QaText.CHAPTER_FIXTURE -> if (spec.family == CHAPTER_FAMILY || spec.id.startsWith("study-")) {
                when (slot) {
                    PostSlots.EYEBROW -> "IGNORED ABOVE TITLE"
                    PostSlots.HEADLINE -> "Ch. 7 · Fields, Matrices & Equilibrium"
                    PostSlots.NUMBER -> "M-07"
                    PostSlots.BODY -> ChapterFixtureBody
                    PostSlots.LIST -> ChapterFixtureList
                    else -> sample
                }
            } else sample
        }
    }
    val bgUri = if (bgPhotoPct > 0) ensureQaBgPhotoUri(context) else null
    val bgOpacity = (bgPhotoPct / 100f).coerceIn(0f, 1f)
    return base.copy(
        texts = PostSlots.withDerivedSlots(texts, spec.slots),
        backgroundPhotoUri = bgUri,
        backgroundPhotoOpacity = if (bgPhotoPct > 0) bgOpacity else 0.35f,
        shadowAll = TextShadow(strength = shadowStrength.coerceIn(0, 100))
    )
}

@Composable
fun PostQaScreen(brand: PostBrand, onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val clipboard = LocalClipboardManager.current
    var size by remember { mutableStateOf(PostSize.values().first()) }
    var mode by remember { mutableStateOf(QaText.SAMPLE) }
    var bgPhotoPct by remember { mutableStateOf(0) }
    var shadowStrength by remember { mutableStateOf(0) }
    var running by remember { mutableStateOf(false) }
    var progress by remember { mutableStateOf("") }
    var report by remember { mutableStateOf<List<String>?>(null) }
    var checks by remember { mutableStateOf(0) }
    var smallReport by remember { mutableStateOf<List<String>?>(null) }

    fun runAll() {
        if (running) return
        running = true
        report = null
        val chosen = mode
        val chosenBg = bgPhotoPct
        val chosenShadow = shadowStrength
        scope.launch {
            val found = mutableListOf<String>()
            var count = 0
            for (spec in PostTemplates) {
                for (sz in PostSize.values()) {
                    progress = spec.name + ", " + sz.label
                    val result = withContext(Dispatchers.Default) {
                        PostRenderer.preview(
                            context,
                            qaDoc(context, spec, sz, chosen, chosenBg, chosenShadow),
                            brand,
                            240
                        ) { !isActive }
                    }
                    count++
                    if (result.overflow.isNotEmpty()) {
                        found.add(spec.name + " (" + spec.id + "), " + sz.label + ": " + result.overflow.joinToString { PostSlots.label(it) })
                    }
                    result.bitmap.recycle()
                }
            }
            checks = count
            report = found
            running = false
        }
    }

    /** Q2: reads every layout, draws nothing, so it answers at once. */
    fun runSmall() {
        val found = mutableListOf<String>()
        for (spec in PostTemplates) {
            for (sz in PostSize.values()) {
                val small = PostDesignRules.smallText(spec, sz)
                if (small.isNotEmpty()) found.add(spec.name + " (" + spec.id + "), " + sz.label + ": " + small.joinToString())
            }
        }
        smallReport = found
    }

    PostTheme {
        Scaffold(containerColor = PostPalette.Ink) { padding ->
            LazyColumn(
                Modifier.fillMaxSize().padding(padding).padding(horizontal = 14.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = onBack) { Icon(Icons.Outlined.ArrowBack, contentDescription = "Back", tint = PostPalette.Text) }
                            Column(Modifier.weight(1f)) {
                                Text("Template QA", color = PostPalette.Text, fontSize = 19.sp, fontWeight = FontWeight.Bold)
                                Text("${PostTemplates.size} templates, ${PostSize.values().size} sizes", color = PostPalette.Muted, fontSize = 12.sp)
                            }
                        }
                        Text(
                            "Test every template with Sample, 1 short word, Long 4-line, Amharic short/long, or Empty optional boxes, plus background photo and shadow toggles.",
                            color = PostPalette.Muted, fontSize = 12.sp
                        )
                        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            QaText.values().forEach { m ->
                                FilterChip(selected = mode == m, onClick = { mode = m; report = null }, label = { Text(m.label) }, enabled = !running)
                            }
                        }
                        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                            PostSize.values().forEach { s ->
                                FilterChip(selected = size == s, onClick = { size = s }, label = { Text(s.label) })
                            }
                            listOf(0 to "Bg Off", 40 to "Bg 40%", 100 to "Bg 100%").forEach { (pct, label) ->
                                FilterChip(selected = bgPhotoPct == pct, onClick = { bgPhotoPct = pct }, label = { Text(label) })
                            }
                            listOf(0 to "Shadow 0", 60 to "Shadow 60").forEach { (sh, label) ->
                                FilterChip(selected = shadowStrength == sh, onClick = { shadowStrength = sh }, label = { Text(label) })
                            }
                        }
                        OutlinedButton(onClick = { runAll() }, enabled = !running, modifier = Modifier.fillMaxWidth()) {
                            Text(if (running) "Checking..." else "Check all templates, all sizes (${mode.label})")
                        }
                        if (running) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                CircularProgressIndicator(color = PostPalette.Mint)
                                Text(progress, color = PostPalette.Muted, fontSize = 12.sp)
                            }
                        }
                        report?.let { lines ->
                            if (lines.isEmpty()) {
                                Text("All $checks checks fit (${mode.label}).", color = PostPalette.Mint, fontSize = 13.sp)
                            } else {
                                Text("Text shrinks in ${lines.size} of $checks checks (${mode.label}):", color = PostPalette.Gold, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(lines.joinToString("\n"), color = PostPalette.Gold, fontSize = 12.sp)
                                OutlinedButton(
                                    onClick = { clipboard.setText(AnnotatedString("Template QA, ${mode.label}\n" + lines.joinToString("\n"))) },
                                    modifier = Modifier.fillMaxWidth()
                                ) { Text("Copy report") }
                            }
                        }
                        OutlinedButton(onClick = { runSmall() }, modifier = Modifier.fillMaxWidth()) {
                            Text("Check small text (instant, all templates)")
                        }
                        Text(
                            "Small text: a box whose own size is under ${PostDesignRules.MIN_BODY_PX.toInt()} px for message and list, " +
                                "${PostDesignRules.MIN_LABEL_PX.toInt()} px for label and button, ${PostDesignRules.MIN_HEADLINE_PX.toInt()} px for headline (at 1080 wide).",
                            color = PostPalette.Muted, fontSize = 12.sp
                        )
                        smallReport?.let { lines ->
                            if (lines.isEmpty()) {
                                Text("No small text in any template.", color = PostPalette.Mint, fontSize = 13.sp)
                            } else {
                                Text("Small text in ${lines.size} of ${PostTemplates.size * PostSize.values().size} checks:", color = PostPalette.Gold, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(lines.joinToString("\n"), color = PostPalette.Gold, fontSize = 12.sp)
                                OutlinedButton(
                                    onClick = { clipboard.setText(AnnotatedString("Template QA, small text\n" + lines.joinToString("\n"))) },
                                    modifier = Modifier.fillMaxWidth()
                                ) { Text("Copy small text report") }
                            }
                        }
                    }
                }
                items(PostTemplates, key = { it.id }) { spec ->
                    QaCard(
                        spec = spec,
                        size = size,
                        mode = mode,
                        bgPhotoPct = bgPhotoPct,
                        shadowStrength = shadowStrength,
                        brand = brand
                    )
                }
            }
        }
    }
}

@Composable
private fun QaCard(
    spec: PostTemplateSpec,
    size: PostSize,
    mode: QaText,
    bgPhotoPct: Int,
    shadowStrength: Int,
    brand: PostBrand
) {
    val context = LocalContext.current
    var bitmap by remember(spec.id, size, mode, bgPhotoPct, shadowStrength) { mutableStateOf<Bitmap?>(null) }
    var overflow by remember(spec.id, size, mode, bgPhotoPct, shadowStrength) { mutableStateOf<List<String>>(emptyList()) }
    val small = remember(spec.id, size) { PostDesignRules.smallText(spec, size) }
    LaunchedEffect(spec.id, size, mode, bgPhotoPct, shadowStrength, brand) {
        val result = withContext(Dispatchers.Default) {
            PostRenderer.preview(
                context,
                qaDoc(context, spec, size, mode, bgPhotoPct, shadowStrength),
                brand,
                360
            ) { !isActive }
        }
        bitmap = result.bitmap
        overflow = result.overflow
    }
    Column(
        Modifier.fillMaxWidth().background(PostPalette.Surface, RoundedCornerShape(12.dp)).padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(spec.name + "  ·  " + spec.family + "  ·  " + spec.id, color = PostPalette.Text, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Box(Modifier.fillMaxWidth().heightIn(min = 120.dp, max = 320.dp), contentAlignment = Alignment.Center) {
            val shown = bitmap
            if (shown != null) {
                Image(shown.asImageBitmap(), contentDescription = "Preview of " + spec.name, contentScale = ContentScale.Fit, modifier = Modifier.fillMaxWidth().heightIn(max = 320.dp))
            } else {
                CircularProgressIndicator(color = PostPalette.Mint)
            }
        }
        if (overflow.isNotEmpty()) {
            Text("Text shrinks: " + overflow.joinToString { PostSlots.label(it) }, color = PostPalette.Gold, fontSize = 12.sp)
        } else if (bitmap != null) {
            Text("Fits", color = PostPalette.Mint, fontSize = 12.sp)
        }
        if (small.isNotEmpty()) {
            Text("Small text: " + small.joinToString(), color = PostPalette.Gold, fontSize = 12.sp)
        }
    }
}
