package com.wisdomtower.post

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

/**
 * Template QA (docs/APP_QUALITY_ROADMAP.md, slice Q1).
 * Draws every template at every size with test words and lists the boxes whose text had to shrink.
 *   Sample:  the template's own sample words (should never shrink).
 *   Stress:  every box 1.5 times longer than its sample (finds layouts with no room to spare).
 *   Amharic: fixed Amharic words in every box (finds layouts that break with wider letters; needs an Amharic font to be exact).
 * The check uses the same renderer as export, so what it reports is what a saved poster would do.
 * Reached from the Poster inbox ("Template QA").
 */
private enum class QaText(val label: String) { SAMPLE("Sample"), STRESS("Stress 1.5x"), AMHARIC("Amharic") }

private fun stretchLine(text: String, factor: Float): String {
    val colon = text.indexOf(':')
    // a timetable row "Mon: Physics": only the part after the label grows
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

private fun amharicFor(slot: String, sample: String): String = when (slot) {
    PostSlots.EYEBROW -> "የጥናት ምክር"
    PostSlots.HEADLINE -> "ምዕራፉን ዝጋ። ከትውስታህ መልስ።"
    PostSlots.BODY -> "ምዕራፉን ዘግተህ የምታስታውሰውን ጻፍ። ከዚያ ያመለጠህን አረጋግጥ።"
    PostSlots.LIST -> {
        val count = sample.split('\n').count { it.isNotBlank() }.coerceIn(1, 6)
        val rows = sample.contains(':')
        val plain = listOf("ምዕራፍ ክለሳ", "የቀድሞ ፈተና", "ስህተት ማስተካከል", "እረፍት እና ክለሳ", "ሙከራ ፈተና", "ጥያቄ መመለስ")
        val days = listOf("ሰኞ", "ማክሰ", "ረቡዕ", "ሐሙስ", "አርብ", "ቅዳሜ")
        (0 until count).joinToString("\n") { if (rows) days[it] + ": " + plain[it] else plain[it] }
    }
    else -> sample
}

private fun qaDoc(spec: PostTemplateSpec, size: PostSize, mode: QaText): PostDoc {
    val base = PostDrafts.factory(spec.id, size)
    val texts = spec.samples.mapValues { (slot, sample) ->
        when (mode) {
            QaText.SAMPLE -> sample
            QaText.STRESS -> sample.split('\n').joinToString("\n") { stretchLine(it, 1.5f) }
            QaText.AMHARIC -> amharicFor(slot, sample)
        }
    }
    return base.copy(texts = texts)
}

@Composable
fun PostQaScreen(brand: PostBrand, onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val clipboard = LocalClipboardManager.current
    var size by remember { mutableStateOf(PostSize.values().first()) }
    var mode by remember { mutableStateOf(QaText.STRESS) }
    var running by remember { mutableStateOf(false) }
    var progress by remember { mutableStateOf("") }
    var report by remember { mutableStateOf<List<String>?>(null) }
    var checks by remember { mutableStateOf(0) }

    fun runAll() {
        if (running) return
        running = true
        report = null
        val chosen = mode
        scope.launch {
            val found = mutableListOf<String>()
            var count = 0
            for (spec in PostTemplates) {
                for (sz in PostSize.values()) {
                    progress = spec.name + ", " + sz.label
                    val result = withContext(Dispatchers.Default) {
                        PostRenderer.preview(context, qaDoc(spec, sz, chosen), brand, 240) { !isActive }
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
                            "Sample: the template's own words. Stress: every box 1.5 times longer. Amharic: fixed Amharic words. " +
                                "A box is listed when its text had to shrink.",
                            color = PostPalette.Muted, fontSize = 12.sp
                        )
                        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            QaText.values().forEach { m ->
                                FilterChip(selected = mode == m, onClick = { mode = m; report = null }, label = { Text(m.label) }, enabled = !running)
                            }
                        }
                        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            PostSize.values().forEach { s ->
                                FilterChip(selected = size == s, onClick = { size = s }, label = { Text(s.label) })
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
                    }
                }
                items(PostTemplates, key = { it.id }) { spec ->
                    QaCard(spec = spec, size = size, mode = mode, brand = brand)
                }
            }
        }
    }
}

@Composable
private fun QaCard(spec: PostTemplateSpec, size: PostSize, mode: QaText, brand: PostBrand) {
    val context = LocalContext.current
    var bitmap by remember(spec.id, size, mode) { mutableStateOf<android.graphics.Bitmap?>(null) }
    var overflow by remember(spec.id, size, mode) { mutableStateOf<List<String>>(emptyList()) }
    LaunchedEffect(spec.id, size, mode, brand) {
        val result = withContext(Dispatchers.Default) {
            PostRenderer.preview(context, qaDoc(spec, size, mode), brand, 360) { !isActive }
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
    }
}
