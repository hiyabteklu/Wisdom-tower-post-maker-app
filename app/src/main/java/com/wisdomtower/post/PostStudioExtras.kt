package com.wisdomtower.post

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.OpenWith
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
    val enScroll = rememberScrollState()
    val amScroll = rememberScrollState()

    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(PostPalette.Card)
            .border(1.dp, PostPalette.Line, RoundedCornerShape(10.dp))
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("English font", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = PostPalette.Text)
            CompactButton(
                label = "Add .ttf",
                icon = Icons.Outlined.Add,
                onClick = { target = PostFonts.ENGLISH; picker.launch(arrayOf("*/*")) }
            )
        }
        Row(
            Modifier.fillMaxWidth().horizontalScrollWithFade(enScroll, PostPalette.Card),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            CompactChip(
                label = "Default",
                selected = doc.fontEnglish == null,
                onClick = { onDoc(doc.copy(fontEnglish = null)) }
            )
            english.forEach { f ->
                CompactChip(
                    label = f.label,
                    selected = doc.fontEnglish == f.path,
                    onClick = { onDoc(doc.copy(fontEnglish = f.path)) }
                )
            }
        }

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Amharic font", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = PostPalette.Text)
            CompactButton(
                label = "Add .ttf",
                icon = Icons.Outlined.Add,
                onClick = { target = PostFonts.AMHARIC; picker.launch(arrayOf("*/*")) }
            )
        }
        Row(
            Modifier.fillMaxWidth().horizontalScrollWithFade(amScroll, PostPalette.Card),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            CompactChip(
                label = "Default",
                selected = doc.fontAmharic == null,
                onClick = { onDoc(doc.copy(fontAmharic = null)) }
            )
            amharic.forEach { f ->
                CompactChip(
                    label = f.label,
                    selected = doc.fontAmharic == f.path,
                    onClick = { onDoc(doc.copy(fontAmharic = f.path)) }
                )
            }
        }
    }
}

/** Move, resize and reset the logo on this post. While Move logo is on, dragging or tapping the preview places the logo. */
@Composable
internal fun LogoMoveRow(doc: PostDoc, onDoc: (PostDoc) -> Unit, moveLogo: Boolean, onMoveLogo: (Boolean) -> Unit) {
    val scroll = rememberScrollState()
    PostSmallLabel("Logo placement on this post")
    Row(
        Modifier.fillMaxWidth().horizontalScrollWithFade(scroll, PostPalette.Surface),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        CompactChip(
            label = if (moveLogo) "Done" else "Move logo",
            icon = Icons.Outlined.OpenWith,
            selected = moveLogo,
            onClick = { onMoveLogo(!moveLogo) }
        )
        CompactChip(
            label = "Logo -",
            selected = false,
            onClick = { onDoc(doc.copy(logoSize = (doc.logoSize * 0.85f).coerceAtLeast(0.4f))) }
        )
        CompactChip(
            label = "Logo +",
            selected = false,
            onClick = { onDoc(doc.copy(logoSize = (doc.logoSize * 1.18f).coerceAtMost(3f))) }
        )
        CompactChip(
            label = "Reset",
            icon = Icons.Outlined.RestartAlt,
            selected = false,
            onClick = { onMoveLogo(false); onDoc(doc.copy(logoCx = null, logoCy = null, logoSize = 1f)) }
        )
    }
    if (moveLogo) PostSmallLabel("Drag or tap the preview to position the logo.")
}
