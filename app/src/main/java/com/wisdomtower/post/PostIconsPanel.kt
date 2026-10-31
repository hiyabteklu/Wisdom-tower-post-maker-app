package com.wisdomtower.post

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private val StarterShapes = listOf(
    "diamond" to "◇", "triangle" to "△", "hexagon" to "⬡",
    "ring" to "○", "plus" to "+", "arrow" to "➜", "check" to "✓"
)

@Composable
fun PostIconsPanel(
    doc: PostDoc,
    onDoc: (PostDoc) -> Unit,
    brand: PostBrand,
    selectedIconId: String?,
    onSelectIcon: (String?) -> Unit,
    onPickIcons: () -> Unit
) {
    val selected = doc.placedIcons.firstOrNull { it.id == selectedIconId }
    Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = onPickIcons, enabled = doc.placedIcons.size < 6) {
                Text("+ Add icons")
            }
            Text("${doc.placedIcons.size}/6 on this post", color = PostPalette.Muted, fontSize = 11.sp)
        }
        Text("My icons · tap to select, then drag or pinch on the preview", color = PostPalette.Muted, fontSize = 11.sp)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(doc.placedIcons, key = { it.id }) { icon ->
                IconTile(icon, selected = icon.id == selectedIconId) { onSelectIcon(icon.id) }
            }
        }
        Text("Starter shapes", color = PostPalette.Muted, fontSize = 11.sp)
        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            StarterShapes.forEach { (shape, glyph) ->
                FilterChip(
                    selected = false,
                    onClick = {
                        if (doc.placedIcons.size < 6) {
                            val item = PostPlacedIcon(
                                id = "shape_${System.currentTimeMillis()}_$shape",
                                path = "shape:$shape"
                            )
                            onDoc(doc.copy(placedIcons = doc.placedIcons + item))
                            onSelectIcon(item.id)
                        }
                    },
                    label = { Text(glyph + " " + shape.replaceFirstChar { it.uppercase() }) },
                    enabled = doc.placedIcons.size < 6
                )
            }
        }
        if (selected != null) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = {
                    onDoc(doc.copy(placedIcons = doc.placedIcons.filterNot { it.id == selected.id }))
                    onSelectIcon(null)
                }) { Text("Remove") }
                OutlinedButton(onClick = {
                    onDoc(doc.copy(placedIcons = doc.placedIcons.map {
                        if (it.id == selected.id) it.copy(front = !it.front) else it
                    }))
                }) { Text(if (selected.front) "Send behind text" else "Bring to front") }
            }
            PostLabeledSlider("Size", selected.scale, 0.25f..4f, "${(selected.scale * 100).toInt()}%") { value ->
                onDoc(doc.copy(placedIcons = doc.placedIcons.map { if (it.id == selected.id) it.copy(scale = value) else it }))
            }
            PostSwitchRow("Match Academy brand color", selected.matchBrand) { checked ->
                onDoc(doc.copy(placedIcons = doc.placedIcons.map {
                    if (it.id == selected.id) it.copy(matchBrand = checked, tintHex = if (checked) null else it.tintHex) else it
                }))
            }
            if (!selected.matchBrand) {
                PostColorPicker("Icon tint", selected.tintHex, brand, { color ->
                    onDoc(doc.copy(placedIcons = doc.placedIcons.map {
                        if (it.id == selected.id) it.copy(tintHex = color, matchBrand = false) else it
                    }))
                })
            }
        }
    }
}

@Composable
private fun IconTile(icon: PostPlacedIcon, selected: Boolean, onClick: () -> Unit) {
    val context = LocalContext.current
    val bitmap by produceState<android.graphics.Bitmap?>(null, icon.path) {
        value = if (icon.path.startsWith("shape:")) null else withContext(Dispatchers.IO) {
            runCatching { BitmapFactory.decodeFile(icon.path) }.getOrNull()
        }
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            Modifier.size(66.dp).clip(RoundedCornerShape(12.dp))
                .background(PostPalette.Raised)
                .border(if (selected) 3.dp else 1.dp, if (selected) PostPalette.Mint else PostPalette.Line, RoundedCornerShape(12.dp))
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            if (icon.path.startsWith("shape:")) {
                val glyph = StarterShapes.firstOrNull { icon.path == "shape:" + it.first }?.second ?: "◇"
                Text(glyph, color = PostPalette.Text, fontSize = 30.sp, fontWeight = FontWeight.Bold)
            } else {
                bitmap?.let { Image(it.asImageBitmap(), contentDescription = null, modifier = Modifier.size(48.dp)) }
            }
        }
        Text(if (icon.path.startsWith("shape:")) icon.path.substringAfter(':') else "Icon", color = PostPalette.Muted, fontSize = 10.sp)
    }
}
