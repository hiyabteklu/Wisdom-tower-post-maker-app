package com.wisdomtower.post

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * The engine controls of the studio (REDESIGN_PLAN.md section 4): photo frames, gradient blends and grain,
 * a cut-out subject with words behind it, and moving words and the subject on the preview.
 * hasPhotoWindow is true when the current template has a photo window that a frame can shape.
 * moveOn turns on tap-and-drag on the preview.
 */
@Composable
fun EngineControls(
    doc: PostDoc,
    onDoc: (PostDoc) -> Unit,
    slots: List<String>,
    hasPhotoWindow: Boolean,
    moveOn: Boolean,
    onMove: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            val saved = PostEngine.importCutout(context, uri)
            if (saved == null) {
                Toast.makeText(context, "That picture could not be read.", Toast.LENGTH_LONG).show()
            } else {
                if (!PostEngine.hasTransparency(saved)) {
                    Toast.makeText(
                        context,
                        "This picture has a solid background. Use a PNG with the background already removed.",
                        Toast.LENGTH_LONG
                    ).show()
                }
                onDoc(doc.copy(cutoutUri = saved, cutoutScale = 1f, cutoutX = 0.5f, cutoutY = 0.94f))
            }
        }
    }

    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Heading("Photo frame")
        if (hasPhotoWindow) {
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                FilterChip(
                    selected = doc.photoFrame == null,
                    onClick = { onDoc(doc.copy(photoFrame = null)) },
                    label = { Text("Template", fontSize = 13.sp) }
                )
                PhotoFrame.values().forEach { frame ->
                    FilterChip(
                        selected = doc.photoFrame == frame,
                        onClick = { onDoc(doc.copy(photoFrame = frame)) },
                        label = { Text(frame.label, fontSize = 13.sp) }
                    )
                }
                FilterChip(
                    selected = doc.frameBorder,
                    onClick = { onDoc(doc.copy(frameBorder = !doc.frameBorder)) },
                    label = { Text("Border", fontSize = 13.sp) }
                )
            }
        } else {
            Text("This template has no photo window. Frames shape the small photo in templates that have one.", color = PostPalette.Muted, fontSize = 12.sp)
        }

        Heading("Gradient blend")
        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
            MeshStyle.values().forEach { style ->
                FilterChip(
                    selected = doc.meshStyle == style,
                    onClick = { onDoc(doc.copy(meshStyle = style)) },
                    label = { Text(style.label, fontSize = 13.sp) }
                )
            }
            FilterChip(
                selected = doc.grain,
                onClick = { onDoc(doc.copy(grain = !doc.grain)) },
                label = { Text("Grain", fontSize = 13.sp) }
            )
        }

        Heading("Cut-out subject")
        Text(
            "Add a PNG of a person or object with the background removed, or style the template's subject.",
            color = PostPalette.Muted,
            fontSize = 12.sp
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = { picker.launch("image/*") }) {
                Text(if (doc.cutoutUri == null) "Add cut-out PNG" else "Change cut-out", fontSize = 13.sp)
            }
            if (doc.cutoutUri != null) {
                OutlinedButton(onClick = { onDoc(doc.copy(cutoutUri = null, textBehind = emptySet())) }) {
                    Text("Remove", fontSize = 13.sp)
                }
            }
        }
        Row(
            Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CompactChip(
                label = "Template style",
                selected = doc.cutoutStyle == null,
                onClick = { onDoc(doc.copy(cutoutStyle = null)) }
            )
            CutoutStyle.values().forEach { cs ->
                CompactChip(
                    label = cs.label,
                    selected = doc.cutoutStyle == cs,
                    onClick = { onDoc(doc.copy(cutoutStyle = cs)) }
                )
            }
        }
        val hasActiveCutout = doc.cutoutUri != null || postTemplate(doc.templateId).layout(doc.size).cutoutShowDefault
        if (hasActiveCutout) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("Subject size", color = PostPalette.Text, fontSize = 13.sp, modifier = Modifier.weight(1f))
                Text("${(doc.cutoutScale * 100).toInt()}%", color = PostPalette.Muted, fontSize = 13.sp)
            }
            Slider(
                value = doc.cutoutScale.coerceIn(0.3f, 2.5f),
                onValueChange = { onDoc(doc.copy(cutoutScale = it)) },
                valueRange = 0.3f..2.5f
            )
            Heading("Words behind the subject")
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                slots.forEach { slot ->
                    FilterChip(
                        selected = slot in doc.textBehind,
                        onClick = {
                            onDoc(doc.copy(textBehind = if (slot in doc.textBehind) doc.textBehind - slot else doc.textBehind + slot))
                        },
                        label = { Text(PostSlots.label(slot), fontSize = 13.sp) }
                    )
                }
            }
        }

        Heading("Move on the preview")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            FilterChip(
                selected = moveOn,
                onClick = { onMove(!moveOn) },
                label = { Text(if (moveOn) "Moving is on" else "Move words and subject", fontSize = 13.sp) }
            )
            if (doc.hasOffsets()) {
                OutlinedButton(onClick = { onDoc(doc.withoutOffsets()) }) {
                    Text("Put words back", fontSize = 13.sp)
                }
            }
        }
        if (moveOn) {
            Text(
                "Tap words (or the subject) on the preview, then drag. Pinch changes the subject's size.",
                color = PostPalette.Muted,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun Heading(label: String) {
    Text(label, color = PostPalette.Text, fontSize = 13.sp, fontWeight = FontWeight.Bold)
}
