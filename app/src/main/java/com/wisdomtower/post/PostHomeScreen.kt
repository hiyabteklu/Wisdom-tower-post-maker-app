package com.wisdomtower.post

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun PostHomeScreen(
    brand: PostBrand,
    projects: List<PostProject>,
    library: PostLibraryIndex,
    onChooseTemplate: (PostTemplateSpec, PostSize) -> Unit,
    onOpenProject: (PostProject) -> Unit,
    onDeleteProject: (String) -> Unit,
    onBookmark: (String, Boolean) -> Unit,
    onOpenBrand: () -> Unit
) {
    var tab by rememberSaveable { mutableStateOf("library") }
    var search by rememberSaveable { mutableStateOf("") }
    var selectedSizeName by rememberSaveable { mutableStateOf(PostSize.SQUARE.name) }
    val selectedSize = PostSize.valueOf(selectedSizeName)
    val templates = PostTemplates.filter { search.isBlank() || it.name.contains(search, true) || it.family.contains(search, true) }
    val preferredFamilies = listOf("Quote", "Motivation", "Question", "Exam", "Study tip", "Subject", "Promotion", "Reminder", "Achievement", "Announcement", "Social", "Event", "Fact")
    val actualFamilies = PostTemplates.map { it.family }.distinct()
    val families = preferredFamilies.filter { preferred -> actualFamilies.any { it.equals(preferred, true) } } +
        actualFamilies.filterNot { actual -> preferredFamilies.any { it.equals(actual, true) } }
    val continueTemplates = library.lastEditedAt.entries
        .sortedByDescending { it.value }
        .mapNotNull { entry -> PostTemplates.firstOrNull { it.id == entry.key } }
        .filter { search.isBlank() || it.name.contains(search, true) || it.family.contains(search, true) }
    val savedTemplates = templates.filter { it.id in library.bookmarks }

    PostTheme {
        Scaffold(
            containerColor = PostPalette.Ink,
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text("Wisdom Tower Post", fontWeight = FontWeight.Bold)
                            Text("Your offline design library", fontSize = 11.sp, color = PostPalette.Muted)
                        }
                    },
                    actions = {
                        IconButton(onClick = onOpenBrand) {
                            Icon(Icons.Outlined.Palette, contentDescription = "Brand kit", tint = PostPalette.Mint)
                        }
                    }
                )
            }
        ) { padding ->
            Column(Modifier.fillMaxSize().padding(padding)) {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(selected = tab == "library", onClick = { tab = "library" }, label = { Text("Library") })
                    FilterChip(selected = tab == "posts", onClick = { tab = "posts" }, label = { Text("My posts · ${projects.size}") })
                }
                if (tab == "library") {
                    Row(
                        Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(7.dp)
                    ) {
                        PostSize.values().forEach { size ->
                            FilterChip(
                                selected = selectedSize == size,
                                onClick = { selectedSizeName = size.name },
                                label = { Text(size.label) }
                            )
                        }
                    }
                    OutlinedTextField(
                        value = search,
                        onValueChange = { search = it },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 4.dp),
                        singleLine = true,
                        label = { Text("Find a template") }
                    )
                    LazyColumn(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        contentPadding = PaddingValues(top = 8.dp, bottom = 28.dp),
                        verticalArrangement = Arrangement.spacedBy(18.dp)
                    ) {
                        if (continueTemplates.isNotEmpty()) {
                            item { TemplateShelf("Continue", "Pick up exactly where you left off", continueTemplates, brand, selectedSize, library, onChooseTemplate, onBookmark) }
                        }
                        if (savedTemplates.isNotEmpty()) {
                            item { TemplateShelf("Saved", "Your bookmarked templates", savedTemplates, brand, selectedSize, library, onChooseTemplate, onBookmark) }
                        }
                        families.forEach { family ->
                            val familyTemplates = templates.filter { it.family.equals(family, true) && it.id !in library.bookmarks }
                            if (familyTemplates.isNotEmpty()) {
                                item(key = "family-$family") {
                                    TemplateShelf(family, null, familyTemplates, brand, selectedSize, library, onChooseTemplate, onBookmark)
                                }
                            }
                        }
                    }
                } else {
                    if (projects.isEmpty()) {
                        Column(
                            Modifier.fillMaxSize().padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Outlined.Add, contentDescription = null, tint = PostPalette.Mint, modifier = Modifier.size(42.dp))
                            Text("Your saved designs will appear here", color = PostPalette.Text, fontWeight = FontWeight.Bold)
                            Text("Open a template, make it yours, then tap Save to My posts.", color = PostPalette.Muted, fontSize = 12.sp)
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(projects, key = { it.id }) { project ->
                                ProjectRow(project, brand, onOpen = { onOpenProject(project) }, onDelete = { onDeleteProject(project.id) })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TemplateShelf(
    title: String,
    subtitle: String?,
    templates: List<PostTemplateSpec>,
    brand: PostBrand,
    size: PostSize,
    library: PostLibraryIndex,
    onChoose: (PostTemplateSpec, PostSize) -> Unit,
    onBookmark: (String, Boolean) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
        Column(Modifier.padding(horizontal = 14.dp)) {
            Text(title, color = PostPalette.Text, fontWeight = FontWeight.Bold, fontSize = 17.sp)
            if (subtitle != null) Text(subtitle, color = PostPalette.Muted, fontSize = 11.sp)
        }
        LazyRow(
            contentPadding = PaddingValues(horizontal = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(11.dp)
        ) {
            items(templates, key = { it.id }) { spec ->
                TemplateHomeCard(
                    spec = spec,
                    brand = brand,
                    size = size,
                    version = library.draftVersions[spec.id] ?: 0L,
                    edited = spec.id in library.lastEditedAt,
                    bookmarked = spec.id in library.bookmarks,
                    onClick = { onChoose(spec, size) },
                    onBookmark = { onBookmark(spec.id, !bookmarked) }
                )
            }
        }
    }
}

@Composable
private fun TemplateHomeCard(
    spec: PostTemplateSpec,
    brand: PostBrand,
    size: PostSize,
    version: Long,
    edited: Boolean,
    bookmarked: Boolean,
    onClick: () -> Unit,
    onBookmark: () -> Unit
) {
    val context = LocalContext.current
    val draft = remember(spec.id, version, size) {
        PostDraftStore.loadDraft(context, spec.id)
            ?: PostDoc(templateId = spec.id, size = size, texts = spec.samples)
    }.copy(size = size)
    val bitmap by produceState<Bitmap?>(null, spec.id, brand, size, version, draft) {
        value = PostThumbnailCache.getOrRender(context, draft, brand, 480, version)
    }
    val ratio = size.width.toFloat() / size.height
    val thumbHeight = 142.dp
    val thumbWidth = thumbHeight * ratio
    Card(
        modifier = Modifier.width(thumbWidth.coerceIn(118.dp, 210.dp)).clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = PostPalette.Surface)
    ) {
        Box(Modifier.fillMaxWidth().aspectRatio(ratio).background(PostPalette.Surface)) {
            bitmap?.let {
                Image(it.asImageBitmap(), contentDescription = spec.name, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            }
            Box(
                Modifier.align(Alignment.TopStart).padding(7.dp).size(8.dp).clip(CircleShape)
                    .background(if (edited) PostPalette.Mint else androidx.compose.ui.graphics.Color.Transparent)
                    .border(1.dp, if (edited) PostPalette.Mint else PostPalette.Line, CircleShape)
            )
            IconButton(onClick = onBookmark, modifier = Modifier.align(Alignment.TopEnd).padding(1.dp).size(42.dp)) {
                Icon(
                    if (bookmarked) Icons.Outlined.Bookmark else Icons.Outlined.BookmarkBorder,
                    contentDescription = if (bookmarked) "Remove bookmark" else "Save template",
                    tint = if (bookmarked) PostPalette.Gold else androidx.compose.ui.graphics.Color.White
                )
            }
        }
        Column(Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
            Text(spec.name, color = PostPalette.Text, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis, fontSize = 12.sp)
            Text(spec.family, color = PostPalette.Muted, fontSize = 10.sp)
        }
    }
}

@Composable
private fun ProjectRow(project: PostProject, brand: PostBrand, onOpen: () -> Unit, onDelete: () -> Unit) {
    val context = LocalContext.current
    val bitmap by produceState<Bitmap?>(null, project.id, project.document, brand) {
        value = withContext(Dispatchers.Default) {
            PostRenderer.thumbnail(context, project.document, brand, 220).bitmap
        }
    }
    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(PostPalette.Surface)
            .clickable(onClick = onOpen).padding(9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(width = 76.dp, height = 84.dp).clip(RoundedCornerShape(8.dp)).background(PostPalette.Ink)) {
            bitmap?.let { Image(it.asImageBitmap(), contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop) }
        }
        Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
            Text(project.title, color = PostPalette.Text, fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Text(postTemplate(project.document.templateId).name + " · " + project.document.size.label, color = PostPalette.Muted, fontSize = 11.sp)
            Text(SimpleDateFormat("MMM d, yyyy · HH:mm", Locale.getDefault()).format(Date(project.updatedAt)), color = PostPalette.Muted, fontSize = 10.sp)
        }
        IconButton(onClick = onDelete) {
            Icon(Icons.Outlined.DeleteOutline, contentDescription = "Delete saved post", tint = PostPalette.Muted)
        }
    }
}
