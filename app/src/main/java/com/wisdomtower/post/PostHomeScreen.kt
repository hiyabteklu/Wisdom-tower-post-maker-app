package com.wisdomtower.post

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
    onChooseTemplate: (PostTemplateSpec, PostSize) -> Unit,
    onOpenProject: (PostProject) -> Unit,
    onDeleteProject: (String) -> Unit,
    onOpenBrand: () -> Unit
) {
    var tab by rememberSaveable { mutableStateOf("templates") }
    var family by rememberSaveable { mutableStateOf("All") }
    var search by rememberSaveable { mutableStateOf("") }
    var selectedSizeName by rememberSaveable { mutableStateOf(PostSize.SQUARE.name) }
    val selectedSize = PostSize.valueOf(selectedSizeName)
    val families = listOf("All") + PostTemplates.map { it.family }.distinct()
    val visible = PostTemplates.filter {
        (family == "All" || it.family == family) &&
            (search.isBlank() || it.name.contains(search, true) || it.family.contains(search, true))
    }

    PostTheme {
        Scaffold(
            containerColor = PostPalette.Ink,
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text("Wisdom Tower Post", fontWeight = FontWeight.Bold)
                            Text("A focused studio for Academy posts", fontSize = 11.sp, color = PostPalette.Muted)
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
                Row(Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = tab == "templates", onClick = { tab = "templates" }, label = { Text("Templates · 20") })
                    FilterChip(selected = tab == "posts", onClick = { tab = "posts" }, label = { Text("My posts · ${projects.size}") })
                }
                if (tab == "templates") {
                    Row(
                        Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 6.dp),
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
                        value = search, onValueChange = { search = it },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp),
                        singleLine = true, label = { Text("Find a template") }
                    )
                    Row(
                        Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 8.dp).horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(7.dp)
                    ) {
                        families.forEach { item ->
                            FilterChip(selected = family == item, onClick = { family = item }, label = { Text(item) })
                        }
                    }
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(start = 12.dp, end = 12.dp, top = 4.dp, bottom = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(visible, key = { it.id }) { spec ->
                            TemplateHomeCard(spec, brand, selectedSize) { onChooseTemplate(spec, selectedSize) }
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
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(14.dp),
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
private fun TemplateHomeCard(spec: PostTemplateSpec, brand: PostBrand, size: PostSize, onClick: () -> Unit) {
    val context = LocalContext.current
    val bitmap by produceState<Bitmap?>(null, spec.id, brand, size) {
        value = withContext(Dispatchers.Default) {
            PostRenderer.thumbnail(
                context,
                PostDoc(templateId = spec.id, size = size, texts = spec.samples),
                brand,
                480
            ).bitmap
        }
    }
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = PostPalette.Surface)
    ) {
        Box(Modifier.fillMaxWidth().aspectRatio(size.width.toFloat() / size.height).background(PostPalette.Surface)) {
            bitmap?.let {
                Image(it.asImageBitmap(), contentDescription = spec.name, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            }
        }
        Column(Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
            Text(spec.name, color = PostPalette.Text, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(spec.family, color = PostPalette.Muted, fontSize = 11.sp)
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
