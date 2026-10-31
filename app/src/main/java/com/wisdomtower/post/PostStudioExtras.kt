package com.wisdomtower.post

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/** The Fonts card: one row of English fonts, one row of Amharic fonts, and Add buttons that import .ttf / .otf files from the phone. */
@Composable
internal fun FontsSection(doc: PostDoc, onDoc: (PostDoc) -> Unit) {
    val context = LocalContext.current
    var tick by remember { mutableStateOf(0) }
    var target by remember { mutableStateOf(PostFonts.ENGLISH) }
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
        var added = 0
        uris.forEach { u -> if (PostFonts.importFont(context, u, target) != null) added++ }
        tick++
        Toast.makeText(context, if (added > 0) "Added $added font(s)" else "Pick .ttf or .otf font files", Toast.LENGTH_LONG).show()
    }
    val english = remember(tick) { PostFonts.list(context, PostFonts.ENGLISH) }
    val amharic = remember(tick) { PostFonts.list(context, PostFonts.AMHARIC) }

    Card(colors = CardDefaults.cardColors(containerColor = PostPalette.Surface), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Fonts", fontWeight = FontWeight.Bold, color = PostPalette.Text)
            PostSmallLabel("English font")
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = doc.fontEnglish == null, onClick = { onDoc(doc.copy(fontEnglish = null)) }, label = { Text("Default") })
                english.forEach { f ->
                    FilterChip(selected = doc.fontEnglish == f.path, onClick = { onDoc(doc.copy(fontEnglish = f.path)) }, label = { Text(f.label) })
                }
            }
            OutlinedButton(onClick = { target = PostFonts.ENGLISH; picker.launch(arrayOf("*/*")) }) { Text("Add English fonts") }

            PostSmallLabel("Amharic font")
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = doc.fontAmharic == null, onClick = { onDoc(doc.copy(fontAmharic = null)) }, label = { Text("Default") })
                amharic.forEach { f ->
                    FilterChip(selected = doc.fontAmharic == f.path, onClick = { onDoc(doc.copy(fontAmharic = f.path)) }, label = { Text(f.label) })
                }
            }
            OutlinedButton(onClick = { target = PostFonts.AMHARIC; picker.launch(arrayOf("*/*")) }) { Text("Add Amharic fonts") }

            if (english.isEmpty() && amharic.isEmpty()) {
                PostSmallLabel("No fonts yet. Tap Add and pick .ttf or .otf files from your phone. You can pick many at once.")
            }
        }
    }
}

/** Move, resize and reset the logo on this post. While Move logo is on, dragging or tapping the preview places the logo. */
@Composable
internal fun LogoMoveRow(doc: PostDoc, onDoc: (PostDoc) -> Unit, moveLogo: Boolean, onMoveLogo: (Boolean) -> Unit) {
    PostSmallLabel("Free logo position on this post")
    Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        FilterChip(selected = moveLogo, onClick = { onMoveLogo(!moveLogo) }, label = { Text(if (moveLogo) "Done moving" else "Move logo") })
        FilterChip(
            selected = false,
            onClick = { onDoc(doc.copy(logoSize = (doc.logoSize * 0.85f).coerceAtLeast(0.4f))) },
            label = { Text("Smaller") }
        )
        FilterChip(
            selected = false,
            onClick = { onDoc(doc.copy(logoSize = (doc.logoSize * 1.18f).coerceAtMost(3f))) },
            label = { Text("Bigger") }
        )
        FilterChip(
            selected = false,
            onClick = { onMoveLogo(false); onDoc(doc.copy(logoCx = null, logoCy = null, logoSize = 1f)) },
            label = { Text("Reset") }
        )
    }
    if (moveLogo) PostSmallLabel("Drag on the preview above, or tap where the logo should go.")
}
