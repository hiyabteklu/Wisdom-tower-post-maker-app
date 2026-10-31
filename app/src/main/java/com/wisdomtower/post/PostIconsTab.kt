package com.wisdomtower.post

import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * The Icons tab (UI_UX_PLAN.md section 6). Groups: My icons (add pictures, tap one to place it), Shapes (starter pack),
 * On this post (pick which placed icon you are editing), Size (uses the shared slider), Color, Layer (front, back, remove).
 * Move and resize on the preview itself: drag to move, pinch to resize.
 */

private const val NO_ICON_HINT = "Add an icon first: open My icons or Shapes."

@Composable
private fun IconChipRow(content: @Composable RowScope.() -> Unit) {
    Row(
        Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}

@Composable
private fun IconHint(text: String) {
    Text(text, fontSize = 12.sp, color = PostPalette.Muted, modifier = Modifier.widthIn(max = 300.dp).padding(horizontal = 4.dp))
}

/** A small picture tile for My icons. 48 dp tap area. */
@Composable
private fun IconTile(path: String, onClick: () -> Unit) {
    val bmp by produceState<Bitmap?>(null, path) { value = withContext(Dispatchers.IO) { PostIconStore.preview(path) } }
    val shape = RoundedCornerShape(10.dp)
    Box(
        Modifier.size(48.dp).clip(shape).background(PostPalette.Surface).border(1.dp, PostPalette.Line, shape).clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        bmp?.let { Image(bitmap = it.asImageBitmap(), contentDescription = "Icon", modifier = Modifier.size(40.dp), contentScale = ContentScale.Fit) }
    }
}

/** What the shared slider controls on the Size group: the size of the icon being edited. Null when there is none. */
internal fun iconSizeSlider(doc: PostDoc, selected: Int, onDoc: (PostDoc) -> Unit): EditorSlider? {
    if (selected !in doc.icons.indices) return null
    val item = doc.icons[selected]
    return EditorSlider("Size", item.size, 0.04f..0.8f, (item.size * 100).toInt().toString() + "%") { v ->
        onDoc(doc.copy(icons = doc.icons.mapIndexed { i, old -> if (i == selected) old.copy(size = v) else old }))
    }
}

@Composable
internal fun IconOptions(
    group: String,
    doc: PostDoc,
    onDoc: (PostDoc) -> Unit,
    selected: Int,
    onSelect: (Int) -> Unit
) {
    val context = LocalContext.current
    var tick by remember { mutableStateOf(0) }
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
        var added = 0
        uris.forEach { if (PostIconStore.import(context, it) != null) added++ }
        tick++
        Toast.makeText(
            context,
            if (added > 0) "Added $added icon(s)" else "Pick PNG, WebP or JPG pictures",
            Toast.LENGTH_LONG
        ).show()
    }

    fun add(source: String) {
        if (doc.icons.size >= MAX_POST_ICONS) {
            Toast.makeText(context, "Up to $MAX_POST_ICONS icons on a post", Toast.LENGTH_SHORT).show()
            return
        }
        val n = doc.icons.size
        val offset = (n * 0.06f).coerceAtMost(0.3f)
        onDoc(doc.copy(icons = doc.icons + PostIconItem(source, cx = 0.5f + offset, cy = 0.5f + offset)))
        onSelect(n)
    }

    val sel = if (doc.icons.isEmpty()) -1 else selected.coerceIn(0, doc.icons.size - 1)

    fun change(edit: (PostIconItem) -> PostIconItem) {
        onDoc(doc.copy(icons = doc.icons.mapIndexed { i, old -> if (i == sel) edit(old) else old }))
    }

    when (group) {
        "mine" -> IconChipRow {
            OutlinedButton(onClick = { picker.launch(arrayOf("image/png", "image/webp", "image/jpeg")) }) {
                Icon(Icons.Outlined.Add, contentDescription = null)
                Text("Add")
            }
            val files = remember(tick) { PostIconStore.list(context) }
            files.forEach { f -> IconTile(f.absolutePath) { add(PostIconStore.FILE + f.absolutePath) } }
            if (files.isEmpty()) IconHint("Tap Add to pick pictures from your phone. They stay here for every post.")
        }

        "shapes" -> Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            val shapes = PostIconStore.shapes
            val rows = (shapes.size + 1) / 2
            shapes.chunked(rows.coerceAtLeast(1)).forEach { rowShapes ->
                IconChipRow {
                    rowShapes.forEach { name ->
                        FilterChip(selected = false, onClick = { add(PostIconStore.SHAPE + name) }, label = { Text(PostIconStore.shapeTitle(name)) })
                    }
                }
            }
        }

        "placed" -> IconChipRow {
            if (doc.icons.isEmpty()) {
                IconHint(NO_ICON_HINT)
            } else {
                doc.icons.forEachIndexed { i, item ->
                    FilterChip(
                        selected = i == sel,
                        onClick = { onSelect(i) },
                        label = { Text((i + 1).toString() + " " + PostIconStore.titleOf(item)) }
                    )
                }
            }
        }

        "size" -> IconChipRow {
            IconHint(if (sel < 0) NO_ICON_HINT else "Drag the slider above, or pinch on the preview. Drag the icon to move it.")
        }

        "color" -> {
            if (sel < 0) {
                IconChipRow { IconHint(NO_ICON_HINT) }
            } else {
                // A = automatic: a shape takes the brand color, a picture keeps its own colors.
                PaletteStrip(doc.icons[sel].tintHex, null, true) { hex -> change { it.copy(tintHex = hex) } }
            }
        }

        "layer" -> IconChipRow {
            if (sel < 0) {
                IconHint(NO_ICON_HINT)
            } else {
                val item = doc.icons[sel]
                FilterChip(selected = item.front, onClick = { change { it.copy(front = true) } }, label = { Text("In front of words") })
                FilterChip(selected = !item.front, onClick = { change { it.copy(front = false) } }, label = { Text("Behind words") })
                FilterChip(
                    selected = false,
                    onClick = {
                        onDoc(doc.copy(icons = doc.icons.filterIndexed { i, _ -> i != sel }))
                        onSelect(0)
                    },
                    label = { Text("Remove") }
                )
            }
        }
    }
}
