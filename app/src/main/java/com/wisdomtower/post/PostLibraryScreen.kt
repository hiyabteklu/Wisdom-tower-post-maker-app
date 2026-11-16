package com.wisdomtower.post

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.LruCache
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.math.abs

/**
 * Small preview images for the Library, kept on disk (UI_UX_PLAN.md section 8).
 * A card is rendered once per (template, size, brand, draft version) and saved as a small JPEG in the cache folder,
 * so the next launch shows it instantly. It is rendered again only when its draft or the brand changes.
 * THUMB_VERSION goes into the cache name: raise it when the drawing changes, so old saved cards are not reused.
 */
object PostThumbs {
    private const val THUMB_VERSION = "v7"
    private val memory = LruCache<String, Bitmap>(64)

    private fun safe(text: String): String = text.replace(Regex("[^A-Za-z0-9_-]"), "_")

    /** The thumbnail of a template: the edited draft when editedAt is set, otherwise the factory look (with the template's own look) at libSize. */
    fun get(context: Context, spec: PostTemplateSpec, libSize: PostSize, brand: PostBrand, editedAt: Long?): Bitmap? = runCatching {
        val draft = if (editedAt != null) PostDrafts.loadDraft(context, spec.id) else null
        val doc = draft ?: PostDrafts.factory(spec.id, libSize)
        val stamp = (THUMB_VERSION + "|" + brand.toString() + "|" + (if (draft != null) editedAt else 0L)).hashCode()
        val prefix = safe(spec.id) + "_" + doc.size.name + "_"
        val name = prefix + abs(stamp) + (if (stamp < 0) "n" else "p") + ".jpg"
        memory.get(name) ?: run {
            val dir = File(context.cacheDir, "thumbs").also { it.mkdirs() }
            val file = File(dir, name)
            val fromDisk = if (file.exists()) BitmapFactory.decodeFile(file.path) else null
            val bitmap = fromDisk ?: PostRenderer.thumbnail(context, doc, brand, 300).bitmap.also { made ->
                runCatching {
                    // drop the older versions of this card, then save the new one
                    dir.listFiles { f -> f.name.startsWith(prefix) && f.name != name }?.forEach { it.delete() }
                    file.outputStream().use { made.compress(Bitmap.CompressFormat.JPEG, 88, it) }
                }
            }
            memory.put(name, bitmap)
            bitmap
        }
    }.getOrNull()
}

/**
 * The Library home (UI_UX_PLAN.md section 4.1): a page of horizontal rows. Only the page scrolls up and down.
 * The page is a LazyColumn, so only the rows on screen are built; the rest are built as you scroll (this is what makes it open fast).
 * Rows: Continue (templates you edited, newest first), Saved (bookmarked), then one row per family.
 * A bookmarked template moves into Saved. An edited template shows your edited version with a small dot.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostLibraryScreen(
    brand: PostBrand,
    library: PostLibrary,
    size: PostSize,
    onSize: (PostSize) -> Unit,
    onOpen: (String) -> Unit,
    onToggleBookmark: (String) -> Unit,
    onBack: () -> Unit
) {
    val byId = remember { PostTemplates.associateBy { it.id } }
    val continueList = library.edited.entries.sortedByDescending { it.value }.mapNotNull { byId[it.key] }
    val savedList = library.bookmarks.mapNotNull { byId[it] }
    val families = remember(library.bookmarks) { PostTemplates.filter { it.id !in library.bookmarks }.groupBy { it.family }.entries.toList() }

    PostTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Library", fontWeight = FontWeight.Bold) },
                    navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Outlined.ArrowBack, contentDescription = "Back to editor") } }
                )
            },
            containerColor = PostPalette.Ink
        ) { padding ->
            Column(Modifier.fillMaxSize().padding(padding)) {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PostSize.values().forEach { s ->
                        FilterChip(selected = size == s, onClick = { onSize(s) }, label = { Text(s.label, fontSize = 12.sp) })
                    }
                }
                LazyColumn(
                    Modifier.weight(1f).fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    if (continueList.isNotEmpty()) {
                        item(key = "row-continue") {
                            LibraryRow("Continue", continueList, brand, library, size, onOpen, onToggleBookmark)
                        }
                    }
                    if (savedList.isNotEmpty()) {
                        item(key = "row-saved") {
                            LibraryRow("Saved", savedList, brand, library, size, onOpen, onToggleBookmark)
                        }
                    }
                    items(families, key = { "family-" + it.key }) { entry ->
                        LibraryRow(entry.key, entry.value, brand, library, size, onOpen, onToggleBookmark)
                    }
                }
            }
        }
    }
}

@Composable
private fun LibraryRow(
    title: String,
    list: List<PostTemplateSpec>,
    brand: PostBrand,
    library: PostLibrary,
    size: PostSize,
    onOpen: (String) -> Unit,
    onToggleBookmark: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            title,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = PostPalette.Text,
            modifier = Modifier.padding(horizontal = 12.dp)
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(list, key = { title + "/" + it.id }) { spec ->
                LibraryCard(
                    spec = spec,
                    brand = brand,
                    size = size,
                    editedAt = library.edited[spec.id],
                    bookmarked = spec.id in library.bookmarks,
                    onOpen = { onOpen(spec.id) },
                    onToggleBookmark = { onToggleBookmark(spec.id) }
                )
            }
        }
    }
}

@Composable
private fun LibraryCard(
    spec: PostTemplateSpec,
    brand: PostBrand,
    size: PostSize,
    editedAt: Long?,
    bookmarked: Boolean,
    onOpen: () -> Unit,
    onToggleBookmark: () -> Unit
) {
    val context = LocalContext.current
    val thumb by produceState<Bitmap?>(null, spec.id, size, brand, editedAt) {
        value = withContext(Dispatchers.IO) { PostThumbs.get(context, spec, size, brand, editedAt) }
    }
    val bmp = thumb
    val thumbH = 150.dp
    val ratio = if (bmp != null) bmp.width.toFloat() / bmp.height else size.width.toFloat() / size.height
    val shape = RoundedCornerShape(12.dp)
    Column(Modifier.width(thumbH * ratio).clickable(onClick = onOpen)) {
        Box(
            Modifier.width(thumbH * ratio).height(thumbH).clip(shape)
                .background(PostPalette.Surface)
                .border(1.dp, PostPalette.Line, shape)
        ) {
            if (bmp != null) {
                Image(
                    bitmap = bmp.asImageBitmap(),
                    contentDescription = spec.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            if (editedAt != null) {
                Box(
                    Modifier.align(Alignment.TopStart).padding(8.dp).size(10.dp)
                        .clip(CircleShape).background(PostPalette.Gold)
                )
            }
            IconButton(onClick = onToggleBookmark, modifier = Modifier.align(Alignment.TopEnd).size(44.dp)) {
                Icon(
                    if (bookmarked) Icons.Outlined.Bookmark else Icons.Outlined.BookmarkBorder,
                    contentDescription = if (bookmarked) "Remove from Saved" else "Save template",
                    tint = if (bookmarked) PostPalette.Gold else PostPalette.Text
                )
            }
        }
        Text(
            spec.name,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = PostPalette.Text,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
