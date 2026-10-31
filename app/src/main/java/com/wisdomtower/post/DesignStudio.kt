package com.wisdomtower.post

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.UUID
import kotlinx.coroutines.delay

private val StudioGreen = Color(0xFF176B4D)
private val StudioPaper = Color(0xFFF2F4EF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DesignStudioApp() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("wisdom_tower_design_studio", Context.MODE_PRIVATE) }
    var doc by remember {
        mutableStateOf(runCatching { prefs.getString("active_document", null)?.let(DesignDocumentCodec::decode) }
            .getOrNull() ?: StarterTemplates.first().createDocument())
    }
    val undoHistory = remember { mutableStateListOf<DesignDocument>() }
    val redoHistory = remember { mutableStateListOf<DesignDocument>() }
    var applyingHistory by remember { mutableStateOf(false) }
    var templateId by rememberSaveable { mutableStateOf(StarterTemplates.first().id) }
    var categoryFilter by rememberSaveable { mutableStateOf("All") }
    var customHex by rememberSaveable { mutableStateOf("#176B4D") }
    var startHex by rememberSaveable { mutableStateOf(doc.gradientStart) }
    var endHex by rememberSaveable { mutableStateOf(doc.gradientEnd) }
    var brandPrimary by remember { mutableStateOf(prefs.getString("brand_primary", "#176B4D") ?: "#176B4D") }
    var brandSecondary by remember { mutableStateOf(prefs.getString("brand_secondary", "#C9E7B8") ?: "#C9E7B8") }
    var brandLogoUri by remember { mutableStateOf(prefs.getString("brand_logo_uri", null)) }
    var brandSocialUri by remember { mutableStateOf(prefs.getString("brand_social_uri", null)) }
    var brandFooter by remember { mutableStateOf(prefs.getString("brand_footer", "LEARN • GROW • ACHIEVE") ?: "LEARN • GROW • ACHIEVE") }
    var brandKitExpanded by rememberSaveable { mutableStateOf(false) }
    var canvasMenu by rememberSaveable { mutableStateOf(false) }
    var showLayers by rememberSaveable { mutableStateOf(true) }
    var savedDesigns by remember { mutableStateOf(loadSavedDesigns(prefs)) }
    var savedMenu by rememberSaveable { mutableStateOf(false) }
    var pickingLogo by remember { mutableStateOf(false) }
    var pickingSocial by remember { mutableStateOf(false) }
    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            runCatching { context.contentResolver.takePersistableUriPermission(uri, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION) }
            if (pickingLogo) {
                brandLogoUri = uri.toString()
                prefs.edit().putString("brand_logo_uri", brandLogoUri).apply()
                doc = doc.copy(layers = doc.layers.filterNot { it.name == "Academy logo" })
                    .addLayer(DesignLayer(name = "Academy logo", type = LayerType.IMAGE, imageUri = uri.toString(),
                        imageFit = "contain", x = 0.78f, y = 0.055f, width = 0.14f, height = 0.1f))
                pickingLogo = false
            } else if (pickingSocial) {
                brandSocialUri = uri.toString()
                prefs.edit().putString("brand_social_uri", brandSocialUri).apply()
                doc = doc.copy(layers = doc.layers.filterNot { it.name == "Social footer icons" })
                    .addLayer(DesignLayer(name = "Social footer icons", type = LayerType.IMAGE, imageUri = uri.toString(),
                        imageFit = "contain", x = 0.65f, y = 0.875f, width = 0.27f, height = 0.06f))
                doc = doc.copy(layers = doc.layers.map { if (it.name == "Footer") it.copy(width = 0.5f) else it })
                pickingSocial = false
            } else {
                doc = doc.addLayer(DesignLayer(name = "Imported image", type = LayerType.IMAGE, imageUri = uri.toString(),
                    x = 0.12f, y = 0.16f, width = 0.76f, height = 0.42f))
            }
        } else {
            pickingLogo = false
            pickingSocial = false
        }
    }
    LaunchedEffect(doc) {
        prefs.edit().putString("active_document", DesignDocumentCodec.encode(doc)).apply()
    }
    LaunchedEffect(doc) {
        delay(350)
        if (applyingHistory) {
            applyingHistory = false
        } else if (undoHistory.lastOrNull() != doc) {
            undoHistory.add(doc)
            if (undoHistory.size > 40) undoHistory.removeAt(0)
            redoHistory.clear()
        }
    }

    MaterialTheme(colorScheme = lightColorScheme(primary = StudioGreen, background = StudioPaper)) {
        Scaffold(topBar = {
            TopAppBar(title = {
                Column {
                    Text("Wisdom Tower Post", fontWeight = FontWeight.Bold)
                    Text("Personal design studio · autosaved", fontSize = 12.sp, color = Color.Gray)
                }
            }, actions = {
                IconButton(enabled = undoHistory.size > 1, onClick = {
                    if (undoHistory.size > 1) {
                        redoHistory.add(doc)
                        undoHistory.removeAt(undoHistory.lastIndex)
                        applyingHistory = true
                        doc = undoHistory.last()
                    }
                }) { Icon(Icons.Outlined.Undo, contentDescription = "Undo") }
                IconButton(enabled = redoHistory.isNotEmpty(), onClick = {
                    if (redoHistory.isNotEmpty()) {
                        val next = redoHistory.removeAt(redoHistory.lastIndex)
                        undoHistory.add(next)
                        applyingHistory = true
                        doc = next
                    }
                }) { Icon(Icons.Outlined.Redo, contentDescription = "Redo") }
                IconButton(onClick = { showLayers = !showLayers }) { Icon(Icons.Outlined.Layers, contentDescription = "Toggle layers") }
            })
        }, containerColor = StudioPaper) { padding ->
            Column(
                Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(horizontal = 14.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedTextField(
                        value = doc.name,
                        onValueChange = { doc = doc.copy(name = it, updatedAt = System.currentTimeMillis()) },
                        label = { Text("Design name") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    Button(onClick = {
                        savedDesigns = saveDesignSnapshot(savedDesigns, doc)
                        prefs.edit().putString("saved_projects", encodeSavedDesigns(savedDesigns)).apply()
                    }) { Text("Save") }
                    Box {
                        IconButton(onClick = { savedMenu = true }) { Icon(Icons.Outlined.FolderOpen, contentDescription = "Open saved design") }
                        DropdownMenu(expanded = savedMenu, onDismissRequest = { savedMenu = false }) {
                            if (savedDesigns.isEmpty()) {
                                DropdownMenuItem(text = { Text("No saved designs yet") }, onClick = { savedMenu = false })
                            } else {
                                savedDesigns.forEach { saved ->
                                    DropdownMenuItem(text = { Text(saved.name) }, onClick = {
                                        doc = runCatching { DesignDocumentCodec.decode(saved.encoded) }.getOrDefault(doc)
                                        savedMenu = false
                                    })
                                }
                            }
                        }
                    }
                }
                OutlinedButton(onClick = {
                    savedDesigns = saveDesignSnapshot(savedDesigns, doc)
                    prefs.edit().putString("saved_projects", encodeSavedDesigns(savedDesigns)).apply()
                    val starter = StarterTemplates.firstOrNull { it.id == templateId } ?: StarterTemplates.first()
                    val freshDocument = withBrandKit(starter.createDocument(doc.canvasPreset), brandPrimary, brandSecondary, brandFooter, replaceFooter = true)
                    doc = withBrandAssets(freshDocument, brandLogoUri, brandSocialUri)
                }, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Outlined.Add, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text("New design from selected template")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Canvas", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width(8.dp))
                    Box {
                        FilterChip(selected = false, onClick = { canvasMenu = true }, label = { Text(doc.canvasPreset.label) })
                        DropdownMenu(expanded = canvasMenu, onDismissRequest = { canvasMenu = false }) {
                            CanvasPreset.entries.forEach { preset ->
                                DropdownMenuItem(text = { Text(preset.label) }, onClick = {
                                    doc = doc.copy(canvasPreset = preset, updatedAt = System.currentTimeMillis())
                                    canvasMenu = false
                                })
                            }
                        }
                    }
                    Spacer(Modifier.weight(1f))
                    Text("${doc.layers.size} layers", color = Color.Gray, fontSize = 12.sp)
                }
                Text("Template library · ${StarterTemplates.size}", fontWeight = FontWeight.Bold)
                Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    (listOf("All") + StarterTemplates.map { it.category }.distinct()).forEach { category ->
                        FilterChip(selected = categoryFilter == category, onClick = { categoryFilter = category }, label = { Text(category) })
                    }
                }
                Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                    StarterTemplates.filter { categoryFilter == "All" || it.category == categoryFilter }.forEach { template ->
                        val isSelected = templateId == template.id
                        Surface(
                            onClick = {
                                templateId = template.id
                                startHex = template.background
                                endHex = template.accent
                                doc = withBrandAssets(withBrandKit(applyTemplateKeepingContent(doc, template), brandPrimary, brandSecondary, brandFooter, replaceFooter = false), brandLogoUri, brandSocialUri)
                            },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) Color(0xFFE0EEE4) else Color.White,
                            border = BorderStroke(if (isSelected) 2.dp else 1.dp, if (isSelected) StudioGreen else Color(0xFFE0E5DE))
                        ) {
                            Column(Modifier.padding(7.dp).width(112.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                DesignCanvas(
                                    doc = template.createDocument(doc.canvasPreset),
                                    onSelect = {},
                                    onMove = { _, _, _ -> },
                                    modifier = Modifier.fillMaxWidth().aspectRatio(
                                        (doc.canvasPreset.width.toFloat() / doc.canvasPreset.height.toFloat()).coerceIn(0.6f, 1.4f)
                                    )
                                )
                                Text(template.name, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
                                Text(template.category, fontSize = 10.sp, color = Color.Gray, maxLines = 1)
                            }
                        }
                    }
                }
                DesignCanvas(doc, onSelect = { doc = doc.selectLayer(it) }, onMove = { id, dx, dy ->
                    val layer = doc.layers.firstOrNull { it.id == id }
                    if (layer != null && !layer.locked) doc = doc.replaceLayer(layer.copy(
                        x = (layer.x + dx).coerceIn(0f, 1f - layer.width),
                        y = (layer.y + dy).coerceIn(0f, 1f - layer.height)
                    ))
                }, modifier = Modifier.fillMaxWidth().aspectRatio(doc.canvasPreset.width.toFloat() / doc.canvasPreset.height.toFloat()))

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                    Button(onClick = { doc = doc.addLayer(DesignLayer(name = "New text", text = "Edit this text")) }, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Outlined.TextFields, null); Spacer(Modifier.width(4.dp)); Text("Text")
                    }
                    Button(onClick = { imagePicker.launch(arrayOf("image/*")) }, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Outlined.Image, null); Spacer(Modifier.width(4.dp)); Text("Image")
                    }
                    Button(onClick = { doc = doc.addLayer(DesignLayer(name = "Shape", type = LayerType.SHAPE, x = .15f, y = .2f, width = .7f, height = .08f, colorHex = brandPrimary)) }, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Outlined.Add, null); Spacer(Modifier.width(4.dp)); Text("Shape")
                    }
                }
                OutlinedButton(onClick = { pickingLogo = true; imagePicker.launch(arrayOf("image/*")) }, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Outlined.AddPhotoAlternate, null)
                    Spacer(Modifier.width(6.dp))
                    Text(if (doc.layers.any { it.name == "Academy logo" }) "Replace Academy logo" else "Add Academy logo to this design")
                }

                if (showLayers) {
                    Text("Layer stack · select, hide or lock", fontWeight = FontWeight.Bold)
                    doc.layers.asReversed().forEach { layer ->
                        Surface(onClick = { doc = doc.selectLayer(layer.id) }, shape = RoundedCornerShape(8.dp),
                            color = if (doc.selectedLayerId == layer.id) Color(0xFFE0EEE4) else Color.White,
                            border = BorderStroke(1.dp, if (doc.selectedLayerId == layer.id) StudioGreen else Color(0xFFE0E5DE))) {
                            Row(Modifier.fillMaxWidth().padding(start = 10.dp, end = 3.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text(layer.name, Modifier.weight(1f), fontSize = 12.sp)
                                IconButton(onClick = { doc = doc.replaceLayer(layer.copy(visible = !layer.visible)) }) {
                                    Icon(if (layer.visible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff, null)
                                }
                                IconButton(onClick = { doc = doc.replaceLayer(layer.copy(locked = !layer.locked)) }) {
                                    Icon(if (layer.locked) Icons.Outlined.Lock else Icons.Outlined.LockOpen, null)
                                }
                            }
                        }
                    }
                }

                doc.selectedLayer()?.let { layer ->
                    Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
                        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                            OutlinedTextField(
                                value = layer.name,
                                onValueChange = { doc = doc.replaceLayer(layer.copy(name = it)) },
                                label = { Text("Layer name") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Selected layer", Modifier.weight(1f), fontWeight = FontWeight.Bold)
                                IconButton(onClick = { doc = doc.moveLayer(layer.id, 1) }) { Icon(Icons.Outlined.ArrowUpward, "Move up") }
                                IconButton(onClick = { doc = doc.moveLayer(layer.id, -1) }) { Icon(Icons.Outlined.ArrowDownward, "Move down") }
                                IconButton(onClick = { doc = doc.addLayer(layer.copy(id = UUID.randomUUID().toString(), name = layer.name + " copy", x = (layer.x + .025f).coerceAtMost(.8f), y = (layer.y + .025f).coerceAtMost(.8f))) }) { Icon(Icons.Outlined.ContentCopy, "Duplicate") }
                                IconButton(onClick = { doc = doc.deleteSelectedLayer() }) { Icon(Icons.Outlined.Delete, "Delete") }
                            }
                            if (layer.type == LayerType.TEXT) {
                                OutlinedTextField(value = layer.text, onValueChange = { doc = doc.replaceLayer(layer.copy(text = it)) },
                                    label = { Text("Text content") }, modifier = Modifier.fillMaxWidth(), minLines = 1, maxLines = 4)
                                Text("Text size", fontSize = 12.sp)
                                Slider(value = layer.fontSize, onValueChange = { doc = doc.replaceLayer(layer.copy(fontSize = it)) }, valueRange = .015f.. .09f)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Bold", Modifier.weight(1f))
                                    Switch(checked = layer.fontWeight >= 700, onCheckedChange = { doc = doc.replaceLayer(layer.copy(fontWeight = if (it) 700 else 400)) })
                                }
                                Text("Font family", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                                    listOf("sans-serif", "serif", "monospace").forEach { family ->
                                        FilterChip(
                                            selected = layer.fontFamily == family,
                                            onClick = { doc = doc.replaceLayer(layer.copy(fontFamily = family)) },
                                            label = { Text(when (family) { "sans-serif" -> "Sans"; "serif" -> "Serif"; else -> "Mono" }) }
                                        )
                                    }
                                }
                                Text("Text alignment", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                                    listOf("start", "center", "end").forEach { alignment ->
                                        FilterChip(
                                            selected = layer.textAlign == alignment,
                                            onClick = { doc = doc.replaceLayer(layer.copy(textAlign = alignment)) },
                                            label = { Text(alignment.replaceFirstChar { it.uppercase() }) }
                                        )
                                    }
                                }
                            }
                            if (layer.type == LayerType.IMAGE) {
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    FilterChip(selected = layer.imageFit == "cover", onClick = { doc = doc.replaceLayer(layer.copy(imageFit = "cover")) }, label = { Text("Fill frame") })
                                    FilterChip(selected = layer.imageFit == "contain", onClick = { doc = doc.replaceLayer(layer.copy(imageFit = "contain")) }, label = { Text("Fit whole image") })
                                }
                            }
                            if (layer.type == LayerType.SHAPE) {
                                Row {
                                    FilterChip(selected = layer.shape == "rectangle", onClick = { doc = doc.replaceLayer(layer.copy(shape = "rectangle")) }, label = { Text("Rectangle") })
                                    Spacer(Modifier.width(6.dp))
                                    FilterChip(selected = layer.shape == "circle", onClick = { doc = doc.replaceLayer(layer.copy(shape = "circle")) }, label = { Text("Circle") })
                                    FilterChip(selected = layer.shape == "line", onClick = { doc = doc.replaceLayer(layer.copy(shape = "line", height = maxOf(layer.height, .006f))) }, label = { Text("Line") })
                                    FilterChip(selected = layer.gradientEnabled, onClick = { doc = doc.replaceLayer(layer.copy(gradientEnabled = !layer.gradientEnabled)) }, label = { Text("Gradient fill") })
                                }
                                if (layer.gradientEnabled) {
                                    Text("Gradient end color", fontSize = 12.sp)
                                    ColorPalette(layer.secondaryColorHex) { hex -> doc = doc.replaceLayer(layer.copy(secondaryColorHex = hex)) }
                                }
                            }
                            Text("Layer color", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            ColorPalette(layer.colorHex) { hex -> doc = doc.replaceLayer(layer.copy(colorHex = hex)); customHex = hex }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                OutlinedTextField(value = customHex, onValueChange = { customHex = it.uppercase() }, label = { Text("Custom HEX") }, modifier = Modifier.weight(1f), singleLine = true)
                                Button(onClick = { if (customHex.matches(Regex("^#[0-9A-Fa-f]{6}$"))) doc = doc.replaceLayer(layer.copy(colorHex = customHex)) }) { Text("Apply") }
                            }
                            Text("Horizontal position · ${(layer.x * 100).toInt()}%", fontSize = 12.sp)
                            Slider(value = layer.x, onValueChange = {
                                doc = doc.replaceLayer(layer.copy(x = it.coerceIn(0f, 1f - layer.width)))
                            }, valueRange = 0f..(1f - layer.width).coerceAtLeast(0f))
                            Text("Vertical position · ${(layer.y * 100).toInt()}%", fontSize = 12.sp)
                            Slider(value = layer.y, onValueChange = {
                                doc = doc.replaceLayer(layer.copy(y = it.coerceIn(0f, 1f - layer.height)))
                            }, valueRange = 0f..(1f - layer.height).coerceAtLeast(0f))
                            Text("Width · ${(layer.width * 100).toInt()}%", fontSize = 12.sp)
                            Slider(value = layer.width, onValueChange = {
                                val w = it.coerceIn(.05f, 1f - layer.x)
                                doc = doc.replaceLayer(layer.copy(width = w))
                            }, valueRange = .05f..1f)
                            Text("Height · ${(layer.height * 100).toInt()}%", fontSize = 12.sp)
                            Slider(value = layer.height, onValueChange = {
                                val h = it.coerceIn(.03f, 1f - layer.y)
                                doc = doc.replaceLayer(layer.copy(height = h))
                            }, valueRange = .03f..1f)
                            Text("Rotation · ${layer.rotation.toInt()}°", fontSize = 12.sp)
                            Slider(value = layer.rotation, onValueChange = { doc = doc.replaceLayer(layer.copy(rotation = it)) }, valueRange = -180f..180f)
                            Text("Opacity · ${(layer.opacity * 100).toInt()}%", fontSize = 12.sp)
                            Slider(value = layer.opacity, onValueChange = { doc = doc.replaceLayer(layer.copy(opacity = it)) }, valueRange = .1f..1f)
                        }
                    }
                }

                Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Brand kit", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                            TextButton(onClick = { brandKitExpanded = !brandKitExpanded }) {
                                Text(if (brandKitExpanded) "Hide" else "Edit")
                            }
                        }
                        if (brandKitExpanded) {
                            Text("Primary brand color", fontSize = 12.sp)
                            ColorPalette(brandPrimary) { brandPrimary = it }
                            Text("Secondary / gradient color", fontSize = 12.sp)
                            ColorPalette(brandSecondary) { brandSecondary = it }
                            OutlinedTextField(
                                value = brandFooter, onValueChange = { brandFooter = it },
                                label = { Text("Default brand footer") }, modifier = Modifier.fillMaxWidth(), singleLine = true
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(onClick = {
                                    prefs.edit().putString("brand_primary", brandPrimary)
                                        .putString("brand_secondary", brandSecondary)
                                        .putString("brand_footer", brandFooter).apply()
                                }, modifier = Modifier.weight(1f)) { Text("Save defaults") }
                                Button(onClick = {
                                    doc = withBrandKit(doc, brandPrimary, brandSecondary, brandFooter, replaceFooter = true)
                                    prefs.edit().putString("brand_primary", brandPrimary)
                                        .putString("brand_secondary", brandSecondary)
                                        .putString("brand_footer", brandFooter).apply()
                                }, modifier = Modifier.weight(1f)) { Text("Apply to design") }
                            }
                            OutlinedButton(onClick = { pickingSocial = true; imagePicker.launch(arrayOf("image/*")) }, modifier = Modifier.fillMaxWidth()) {
                                Icon(Icons.Outlined.Share, contentDescription = null)
                                Spacer(Modifier.width(6.dp))
                                Text(if (brandSocialUri == null) "Import social icon strip" else "Replace social icon strip")
                            }
                            Text("Logo and social strip are editable image layers. Import a transparent PNG for the cleanest result.", fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Palette, null, tint = StudioGreen)
                    Text("Background", Modifier.padding(start = 7.dp), fontWeight = FontWeight.Bold)
                    Spacer(Modifier.weight(1f))
                    Text("Gradient", fontSize = 12.sp)
                    Switch(checked = doc.useGradient, onCheckedChange = { doc = doc.copy(useGradient = it) })
                }
                ColorPalette(doc.backgroundColor) { doc = doc.copy(backgroundColor = it) }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedTextField(value = customHex, onValueChange = { customHex = it.uppercase() }, label = { Text("Background HEX") }, modifier = Modifier.weight(1f), singleLine = true)
                    Button(onClick = { if (customHex.matches(Regex("^#[0-9A-Fa-f]{6}$"))) doc = doc.copy(backgroundColor = customHex) }) { Text("Apply") }
                }
                if (doc.useGradient) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedTextField(value = startHex, onValueChange = { startHex = it.uppercase() }, label = { Text("Start HEX") }, modifier = Modifier.weight(1f), singleLine = true)
                        OutlinedTextField(value = endHex, onValueChange = { endHex = it.uppercase() }, label = { Text("End HEX") }, modifier = Modifier.weight(1f), singleLine = true)
                    }
                    OutlinedButton(
                        onClick = {
                            val valid = Regex("^#[0-9A-Fa-f]{6}$")
                            if (startHex.matches(valid) && endHex.matches(valid)) {
                                doc = doc.copy(gradientStart = startHex, gradientEnd = endHex, useGradient = true)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Apply gradient colors") }
                    Text("Gradient angle: ${doc.gradientAngle}°", fontSize = 12.sp)
                    Slider(value = doc.gradientAngle.toFloat(), onValueChange = { doc = doc.copy(gradientAngle = it.toInt()) }, valueRange = 0f..360f)
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button(onClick = { exportDesign(context, doc, "jpg") }, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Outlined.Image, contentDescription = null)
                        Spacer(Modifier.width(3.dp))
                        Text("JPG", fontSize = 12.sp)
                    }
                    Button(onClick = { exportDesign(context, doc, "png") }, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Outlined.Image, contentDescription = null)
                        Spacer(Modifier.width(3.dp))
                        Text("PNG", fontSize = 12.sp)
                    }
                    Button(onClick = { exportDesign(context, doc, "pdf") }, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Outlined.PictureAsPdf, contentDescription = null)
                        Spacer(Modifier.width(3.dp))
                        Text("PDF", fontSize = 12.sp)
                    }
                }
                OutlinedButton(
                    onClick = { exportDesign(context, doc, "png", share = true) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Outlined.Share, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text("Export and share PNG")
                }
                Text("Offline draft · autosaved on this device", color = Color.Gray, fontSize = 11.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
            }
        }
    }
}

@Composable
private fun DesignCanvas(doc: DesignDocument, onSelect: (String) -> Unit, onMove: (String, Float, Float) -> Unit, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    BoxWithConstraints(modifier.clip(RoundedCornerShape(14.dp)).border(1.dp, Color(0xFFDDE2DA), RoundedCornerShape(14.dp))) {
        val cw = maxWidth
        val ch = maxHeight
        val bgStart = parseStudioColor(if (doc.useGradient) doc.gradientStart else doc.backgroundColor)
        val bgEnd = parseStudioColor(doc.gradientEnd)
        val angle = Math.toRadians(doc.gradientAngle.toDouble())
        val density = androidx.compose.ui.platform.LocalDensity.current.density
        val brush = if (doc.useGradient) Brush.linearGradient(
            listOf(bgStart, bgEnd),
            start = androidx.compose.ui.geometry.Offset.Zero,
            end = androidx.compose.ui.geometry.Offset(
                cw.value * density * kotlin.math.cos(angle).toFloat(),
                ch.value * density * kotlin.math.sin(angle).toFloat()
            )
        ) else Brush.linearGradient(listOf(bgStart, bgStart))
        Box(Modifier.fillMaxSize().background(brush)) {
            doc.layers.filter { it.visible }.forEach { layer ->
                val selected = layer.id == doc.selectedLayerId
                Box(
                    Modifier.offset(cw * layer.x, ch * layer.y).width(cw * layer.width).height(ch * layer.height)
                        .rotate(layer.rotation)
                        .then(if (selected) Modifier.border(1.5.dp, StudioGreen, RoundedCornerShape(3.dp)) else Modifier)
                        .pointerInput(layer.id, cw, ch, layer.locked) {
                            detectDragGestures(onDragStart = { onSelect(layer.id) }) { change, drag ->
                                change.consume()
                                if (!layer.locked && cw.value > 0 && ch.value > 0) {
                                    onMove(layer.id, drag.x / (cw.value * density), drag.y / (ch.value * density))
                                }
                            }
                        }.clickable { onSelect(layer.id) },
                    contentAlignment = Alignment.CenterStart
                ) {
                    when (layer.type) {
                        LayerType.TEXT -> Text(layer.text,
                            color = parseStudioColor(layer.colorHex).copy(alpha = layer.opacity.coerceIn(0f, 1f)),
                            fontSize = (cw.value * layer.fontSize).coerceIn(8f, 60f).sp,
                            lineHeight = (cw.value * layer.fontSize * 1.2f).coerceIn(10f, 72f).sp,
                            fontWeight = if (layer.fontWeight >= 700) FontWeight.Bold else if (layer.fontWeight >= 500) FontWeight.Medium else FontWeight.Normal,
                            fontFamily = when (layer.fontFamily) { "serif" -> FontFamily.Serif; "monospace" -> FontFamily.Monospace; else -> FontFamily.SansSerif },
                            textAlign = when (layer.textAlign) { "center" -> TextAlign.Center; "end" -> TextAlign.End; else -> TextAlign.Start })
                        LayerType.SHAPE -> {
                            if (layer.shape == "line") {
                                Canvas(Modifier.fillMaxSize()) {
                                    drawLine(
                                        color = parseStudioColor(layer.colorHex).copy(alpha = layer.opacity.coerceIn(0f, 1f)),
                                        start = androidx.compose.ui.geometry.Offset(0f, size.height / 2),
                                        end = androidx.compose.ui.geometry.Offset(size.width, size.height / 2),
                                        strokeWidth = (size.width * 0.008f).coerceIn(2f, 12f)
                                    )
                                }
                            } else {
                                val shape = if (layer.shape == "circle") CircleShape else RoundedCornerShape(5.dp)
                                val shapeModifier = if (layer.gradientEnabled) {
                                    Modifier.fillMaxSize().background(
                                        Brush.horizontalGradient(listOf(
                                            parseStudioColor(layer.colorHex).copy(alpha = layer.opacity.coerceIn(0f, 1f)),
                                            parseStudioColor(layer.secondaryColorHex).copy(alpha = layer.opacity.coerceIn(0f, 1f))
                                        )),
                                        shape
                                    )
                                } else {
                                    Modifier.fillMaxSize().background(
                                        parseStudioColor(layer.colorHex).copy(alpha = layer.opacity.coerceIn(0f, 1f)),
                                        shape
                                    )
                                }
                                Box(shapeModifier)
                            }
                        }
                        LayerType.IMAGE -> {
                            val bitmap = remember(layer.imageUri) {
                                runCatching { layer.imageUri?.let { uri ->
                                    context.contentResolver.openInputStream(Uri.parse(uri))?.use { BitmapFactory.decodeStream(it)?.asImageBitmap() }
                                } }.getOrNull()
                            }
                            if (bitmap != null) Image(bitmap, layer.name, Modifier.fillMaxSize(), contentScale = if (layer.imageFit == "contain") ContentScale.Fit else ContentScale.Crop)
                            else Box(Modifier.fillMaxSize().background(Color.LightGray), contentAlignment = Alignment.Center) { Text("Image unavailable", fontSize = 10.sp) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ColorPalette(selected: String, onPick: (String) -> Unit) {
    Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
        DesignPalette.forEach { hex ->
            Box(Modifier.size(29.dp).clip(CircleShape).background(parseStudioColor(hex))
                .then(if (hex.equals(selected, true)) Modifier.border(3.dp, StudioGreen, CircleShape) else Modifier.border(1.dp, Color.LightGray, CircleShape))
                .clickable { onPick(hex) })
        }
    }
}

private fun parseStudioColor(hex: String): Color = runCatching {
    Color(android.graphics.Color.parseColor(if (hex.startsWith("#")) hex else "#$hex"))
}.getOrDefault(StudioGreen)


private data class SavedDesign(val name: String, val encoded: String, val updatedAt: Long)

private fun loadSavedDesigns(prefs: android.content.SharedPreferences): List<SavedDesign> {
    return runCatching {
        val array = org.json.JSONArray(prefs.getString("saved_projects", "[]") ?: "[]")
        (0 until array.length()).mapNotNull { index ->
            val item = array.optJSONObject(index) ?: return@mapNotNull null
            SavedDesign(
                name = item.optString("name", "Untitled design"),
                encoded = item.optString("encoded", ""),
                updatedAt = item.optLong("updatedAt", 0L)
            ).takeIf { it.encoded.isNotBlank() }
        }.sortedByDescending { it.updatedAt }
    }.getOrDefault(emptyList())
}

private fun saveDesignSnapshot(existing: List<SavedDesign>, document: DesignDocument): List<SavedDesign> {
    val snapshot = SavedDesign(document.name.ifBlank { "Untitled design" }, DesignDocumentCodec.encode(document), System.currentTimeMillis())
    return (listOf(snapshot) + existing.filterNot { it.name.equals(snapshot.name, ignoreCase = true) })
        .take(30)
}

private fun encodeSavedDesigns(projects: List<SavedDesign>): String = org.json.JSONArray().apply {
    projects.forEach { project ->
        put(org.json.JSONObject().apply {
            put("name", project.name)
            put("encoded", project.encoded)
            put("updatedAt", project.updatedAt)
        })
    }
}.toString()


private fun withBrandKit(
    document: DesignDocument,
    primary: String,
    secondary: String,
    footer: String,
    replaceFooter: Boolean
): DesignDocument = document.copy(
    gradientEnd = secondary,
    layers = document.layers.map { layer ->
        when (layer.name) {
            "Brand label", "Eyebrow" -> layer.copy(colorHex = primary)
            "Footer" -> layer.copy(colorHex = primary, text = if (replaceFooter) footer else layer.text)
            else -> layer
        }
    },
    updatedAt = System.currentTimeMillis()
)


private fun withBrandAssets(document: DesignDocument, logoUri: String?, socialUri: String?): DesignDocument {
    var result = document
    if (logoUri != null && result.layers.none { it.name == "Academy logo" }) {
        result = result.addLayer(DesignLayer(name = "Academy logo", type = LayerType.IMAGE, imageUri = logoUri,
            imageFit = "contain", x = .78f, y = .055f, width = .14f, height = .1f))
    }
    if (socialUri != null && result.layers.none { it.name == "Social footer icons" }) {
        result = result.addLayer(DesignLayer(name = "Social footer icons", type = LayerType.IMAGE, imageUri = socialUri,
            imageFit = "contain", x = .65f, y = .875f, width = .27f, height = .06f))
        result = result.copy(layers = result.layers.map { if (it.name == "Footer") it.copy(width = .5f) else it })
    }
    return result
}
