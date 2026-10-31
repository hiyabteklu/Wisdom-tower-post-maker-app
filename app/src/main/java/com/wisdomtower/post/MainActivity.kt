package com.wisdomtower.post

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas as AndroidCanvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.PictureAsPdf
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val BrandGreen = Color(0xFF176B4D)
private val Ink = Color(0xFF18221D)
private val Paper = Color(0xFFF5F6F1)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PostApp()
        }
    }
}

private data class PostTemplate(
    val id: Int,
    val name: String,
    val background: Color,
    val accent: Color,
    val style: String
)

private val templates = listOf(
    PostTemplate(0, "Evergreen", Color(0xFFF4F6EF), BrandGreen, "Editorial"),
    PostTemplate(1, "Midnight", Color(0xFF172923), Color(0xFFC9E7B8), "Dark"),
    PostTemplate(2, "Study Notes", Color(0xFFFFF4D8), Color(0xFFB65C32), "Warm"),
    PostTemplate(3, "Clean Focus", Color.White, Color(0xFF385D9D), "Minimal")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WisdomTowerPostApp() {
    val context = LocalContext.current
    val draftPrefs = remember {
        context.getSharedPreferences("wisdom_tower_post_draft", Context.MODE_PRIVATE)
    }
    var title by remember {
        mutableStateOf(draftPrefs.getString("title", "A little progress every day") ?: "")
    }
    var body by remember {
        mutableStateOf(draftPrefs.getString(
            "body",
            "You don't need a perfect study day. Start with one page, one question, one small win."
        ) ?: "")
    }
    var subtitle by remember {
        mutableStateOf(draftPrefs.getString("subtitle", "WISDOM TOWER ACADEMY") ?: "")
    }
    var selectedTemplate by remember {
        mutableStateOf(draftPrefs.getInt("selected_template", 0))
    }
    LaunchedEffect(title, body, subtitle, selectedTemplate) {
        draftPrefs.edit()
            .putString("title", title)
            .putString("body", body)
            .putString("subtitle", subtitle)
            .putInt("selected_template", selectedTemplate)
            .apply()
    }
    val template = templates.firstOrNull { it.id == selectedTemplate } ?: templates.first()

    MaterialTheme(colorScheme = lightColorScheme(primary = BrandGreen, background = Paper, surface = Color.White)) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text("Wisdom Tower Post", fontWeight = FontWeight.Bold)
                            Text("Your daily post studio", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                )
            },
            containerColor = Paper
        ) { padding ->
            Column(
                modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Create once. Compare designs.", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text("Your words stay when you switch templates.", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                    Surface(shape = RoundedCornerShape(12.dp), color = Color(0xFFE0EEE4)) {
                        androidx.compose.material3.Icon(Icons.Outlined.Palette, contentDescription = "Templates", tint = BrandGreen, modifier = Modifier.padding(10.dp))
                    }
                }

                PostPreview(template = template, title = title, body = body, subtitle = subtitle)

                Text("Choose a look", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    templates.forEach { item ->
                        TemplateChip(
                            template = item,
                            selected = item.id == selectedTemplate,
                            onClick = { selectedTemplate = item.id }
                        )
                    }
                }

                Text("Post content", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                OutlinedTextField(
                    value = subtitle, onValueChange = { subtitle = it },
                    label = { Text("Brand / series label") },
                    modifier = Modifier.fillMaxWidth(), singleLine = true
                )
                OutlinedTextField(
                    value = title, onValueChange = { title = it },
                    label = { Text("Headline") }, modifier = Modifier.fillMaxWidth(),
                    minLines = 2, maxLines = 3
                )
                OutlinedTextField(
                    value = body, onValueChange = { body = it },
                    label = { Text("Main message") }, modifier = Modifier.fillMaxWidth(),
                    minLines = 3, maxLines = 5
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { exportPoster(context, "jpg", template, title, body, subtitle) },
                        modifier = Modifier.weight(1f)
                    ) {
                        androidx.compose.material3.Icon(Icons.Outlined.Image, contentDescription = null)
                        Spacer(Modifier.size(6.dp))
                        Text("Save JPG")
                    }
                    Button(
                        onClick = { exportPoster(context, "pdf", template, title, body, subtitle) },
                        modifier = Modifier.weight(1f)
                    ) {
                        androidx.compose.material3.Icon(Icons.Outlined.PictureAsPdf, contentDescription = null)
                        Spacer(Modifier.size(6.dp))
                        Text("Save PDF")
                    }
                }
                Text("Draft saved on this device • offline", modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), textAlign = TextAlign.Center, color = Color.Gray, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
private fun TemplateChip(template: PostTemplate, selected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = if (selected) BrandGreen else Color.White,
        border = if (selected) null else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E5DE)),
        shadowElevation = if (selected) 2.dp else 0.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(Modifier.size(22.dp).background(template.background, RoundedCornerShape(6.dp)).border(2.dp, template.accent, RoundedCornerShape(6.dp)))
            Column {
                Text(template.name, color = if (selected) Color.White else Ink, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                Text(template.style, color = if (selected) Color(0xFFD8E8DD) else Color.Gray, fontSize = 10.sp)
            }
        }
    }
}

@Composable
private fun PostPreview(template: PostTemplate, title: String, body: String, subtitle: String) {
    Card(
        modifier = Modifier.fillMaxWidth().aspectRatio(1f),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = template.background),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Box(Modifier.fillMaxSize().padding(22.dp)) {
            Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                Column(verticalArrangement = Arrangement.spacedBy(if (template.id == 3) 18.dp else 14.dp)) {
                    if (template.id == 2) {
                        Surface(color = template.accent, shape = RoundedCornerShape(6.dp)) {
                            Text(
                                "TODAY'S STUDY NOTE",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                color = Color.White, fontWeight = FontWeight.Bold, fontSize = 9.sp,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                    if (template.id == 3) {
                        Text(
                            "A THOUGHT WORTH KEEPING",
                            color = template.accent, fontWeight = FontWeight.Bold,
                            fontSize = 9.sp, letterSpacing = 1.5.sp
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            Modifier.size(if (template.id == 3) 26.dp else 32.dp)
                                .background(template.accent, RoundedCornerShape(if (template.id == 2) 4.dp else 10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("W", color = template.background, fontWeight = FontWeight.Black, fontSize = 18.sp)
                        }
                        Text(
                            subtitle.ifBlank { "WISDOM TOWER ACADEMY" }.uppercase(),
                            color = template.accent, fontWeight = FontWeight.Bold,
                            fontSize = 10.sp, letterSpacing = if (template.id == 3) 0.5.sp else 1.sp
                        )
                    }
                    if (template.id != 3) {
                        Box(Modifier.fillMaxWidth().height(if (template.id == 2) 4.dp else 3.dp)
                            .background(template.accent.copy(alpha = 0.65f), RoundedCornerShape(2.dp)))
                    }
                    Text(
                        title.ifBlank { "Your headline goes here" },
                        color = if (template.id == 1) Color.White else Ink,
                        fontSize = when (template.id) { 2 -> 25.sp; 3 -> 30.sp; else -> 27.sp },
                        lineHeight = when (template.id) { 2 -> 30.sp; 3 -> 35.sp; else -> 32.sp },
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        body.ifBlank { "Your message will appear here as you write." },
                        color = if (template.id == 1) Color(0xFFE0E8E2) else Color(0xFF46514A),
                        fontSize = if (template.id == 3) 13.sp else 14.sp,
                        lineHeight = if (template.id == 3) 20.sp else 21.sp
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(Modifier.fillMaxWidth().height(if (template.id == 2) 2.dp else 1.dp)
                        .background(template.accent.copy(alpha = 0.45f)))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            when (template.id) {
                                1 -> "STAY CURIOUS"
                                2 -> "STUDY • REFLECT • GROW"
                                3 -> "ONE IDEA AT A TIME"
                                else -> "LEARN • GROW • ACHIEVE"
                            },
                            color = template.accent, fontWeight = FontWeight.Bold, fontSize = 9.sp
                        )
                        Text("●  ▶  ◎", color = template.accent, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}


private fun exportPoster(
    context: Context,
    format: String,
    template: PostTemplate,
    title: String,
    body: String,
    subtitle: String
) {
    val stamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
    val baseName = "WisdomTowerPost_$stamp"
    val size = 1080
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    drawPoster(AndroidCanvas(bitmap), size, template, title, body, subtitle)

    try {
        if (format == "jpg") {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, "$baseName.jpg")
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                    put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Wisdom Tower Post")
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }
                val resolver = context.contentResolver
                val uri: Uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                    ?: throw IllegalStateException("Could not create image file")
                resolver.openOutputStream(uri)?.use { output ->
                    if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 95, output)) {
                        throw IllegalStateException("Could not encode JPG")
                    }
                } ?: throw IllegalStateException("Could not open image output")
                values.clear()
                values.put(MediaStore.Images.Media.IS_PENDING, 0)
                resolver.update(uri, values, null, null)
            } else {
                val directory = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Wisdom Tower Post")
                if (!directory.exists()) directory.mkdirs()
                FileOutputStream(File(directory, "$baseName.jpg")).use {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 95, it)
                }
            }
            Toast.makeText(context, "JPG saved • 1080 × 1080", Toast.LENGTH_LONG).show()
        } else {
            val document = PdfDocument()
            val page = document.startPage(PdfDocument.PageInfo.Builder(size, size, 1).create())
            drawPoster(page.canvas, size, template, title, body, subtitle)
            document.finishPage(page)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, "$baseName.pdf")
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/Wisdom Tower Post")
                    put(MediaStore.MediaColumns.IS_PENDING, 1)
                }
                val resolver = context.contentResolver
                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                    ?: throw IllegalStateException("Could not create PDF file")
                resolver.openOutputStream(uri)?.use { document.writeTo(it) }
                    ?: throw IllegalStateException("Could not open PDF output")
                values.clear()
                values.put(MediaStore.MediaColumns.IS_PENDING, 0)
                resolver.update(uri, values, null, null)
            } else {
                val directory = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "Wisdom Tower Post")
                if (!directory.exists()) directory.mkdirs()
                FileOutputStream(File(directory, "$baseName.pdf")).use { document.writeTo(it) }
            }
            document.close()
            Toast.makeText(context, "PDF saved • 1080 × 1080", Toast.LENGTH_LONG).show()
        }
    } catch (error: Exception) {
        Toast.makeText(context, "Export failed: " + (error.localizedMessage ?: "unknown error"), Toast.LENGTH_LONG).show()
    } finally {
        bitmap.recycle()
    }
}

private fun drawPoster(
    canvas: AndroidCanvas,
    size: Int,
    template: PostTemplate,
    title: String,
    body: String,
    subtitle: String
) {
    canvas.save()
    val background = template.background.toArgb()
    val accent = template.accent.toArgb()
    val dark = template.id == 1
    canvas.drawColor(background)
    val margin = 76f
    val maxWidth = 1080f - margin * 2
    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = accent }

    if (template.id == 2) {
        paint.style = Paint.Style.FILL
        canvas.drawRoundRect(RectF(margin, 68f, margin + 300f, 112f), 10f, 10f, paint)
        paint.color = android.graphics.Color.WHITE
        paint.textSize = 19f
        paint.typeface = Typeface.create("sans-serif", Typeface.BOLD)
        canvas.drawText("TODAY'S STUDY NOTE", margin + 15f, 97f, paint)
    }

    var y = if (template.id == 2) 165f else 105f
    if (template.id == 3) {
        paint.color = accent
        paint.textSize = 22f
        paint.typeface = Typeface.create("sans-serif", Typeface.BOLD)
        canvas.drawText("A THOUGHT WORTH KEEPING", margin, y, paint)
        y += 65f
    }

    paint.color = accent
    canvas.drawRoundRect(RectF(margin, y, margin + 62f, y + 62f), 14f, 14f, paint)
    paint.color = background
    paint.textSize = 39f
    paint.typeface = Typeface.create("sans-serif-black", Typeface.NORMAL)
    canvas.drawText("W", margin + 16f, y + 44f, paint)
    paint.color = accent
    paint.textSize = 23f
    paint.typeface = Typeface.create("sans-serif", Typeface.BOLD)
    val label = subtitle.ifBlank { "WISDOM TOWER ACADEMY" }.uppercase(Locale.getDefault())
    canvas.drawText(ellipsize(label, paint, maxWidth - 85f), margin + 82f, y + 38f, paint)
    y += 110f

    if (template.id != 3) {
        canvas.drawRoundRect(
            RectF(margin, y, 1080f - margin, y + if (template.id == 2) 8f else 5f),
            3f, 3f, paint
        )
        y += 55f
    } else {
        y += 15f
    }

    paint.color = if (dark) android.graphics.Color.WHITE else android.graphics.Color.rgb(24, 34, 29)
    paint.textSize = if (template.id == 2) 74f else if (template.id == 3) 82f else 76f
    paint.typeface = Typeface.create("sans-serif", Typeface.BOLD)
    y = drawWrappedText(canvas, title.ifBlank { "Your headline goes here" }, paint, margin, y, maxWidth, 1.13f, 4)
    y += 35f

    paint.color = if (dark) android.graphics.Color.rgb(224, 232, 226) else android.graphics.Color.rgb(70, 81, 74)
    paint.textSize = if (template.id == 3) 38f else 40f
    paint.typeface = Typeface.create("sans-serif", Typeface.NORMAL)
    drawWrappedText(canvas, body.ifBlank { "Your message will appear here as you write." }, paint, margin, y, maxWidth, 1.35f, 8)

    val footerY = 970f
    paint.color = accent
    paint.strokeWidth = 2f
    canvas.drawLine(margin, footerY - 30f, 1080f - margin, footerY - 30f, paint)
    paint.textSize = 23f
    paint.typeface = Typeface.create("sans-serif", Typeface.BOLD)
    val footer = when (template.id) {
        1 -> "STAY CURIOUS"
        2 -> "STUDY • REFLECT • GROW"
        3 -> "ONE IDEA AT A TIME"
        else -> "LEARN • GROW • ACHIEVE"
    }
    canvas.drawText(footer, margin, footerY + 15f, paint)
    paint.textSize = 28f
    canvas.drawText("●  ▶  ◎", 825f, footerY + 15f, paint)
}

private fun drawWrappedText(
    canvas: AndroidCanvas,
    text: String,
    paint: Paint,
    x: Float,
    startY: Float,
    maxWidth: Float,
    lineHeightMultiplier: Float,
    maxLines: Int
): Float {
    var y = startY
    val lineHeight = paint.textSize * lineHeightMultiplier
    var line = StringBuilder()
    var lines = 0
    val words = text.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
    for (word in words) {
        val candidate = if (line.isEmpty()) word else "$line $word"
        if (line.isNotEmpty() && paint.measureText(candidate) > maxWidth) {
            canvas.drawText(line.toString(), x, y, paint)
            y += lineHeight
            lines++
            line = StringBuilder(word)
            if (lines >= maxLines) {
                canvas.drawText(ellipsize(line.toString(), paint, maxWidth), x, y, paint)
                return y + lineHeight
            }
        } else {
            line = StringBuilder(candidate)
        }
    }
    if (line.isNotEmpty() && lines < maxLines) {
        canvas.drawText(ellipsize(line.toString(), paint, maxWidth), x, y, paint)
        y += lineHeight
    }
    return y
}

private fun ellipsize(text: String, paint: Paint, maxWidth: Float): String {
    if (paint.measureText(text) <= maxWidth) return text
    val suffix = "…"
    var shortened = text
    while (shortened.isNotEmpty() && paint.measureText(shortened + suffix) > maxWidth) {
        shortened = shortened.dropLast(1)
    }
    return shortened + suffix
}
