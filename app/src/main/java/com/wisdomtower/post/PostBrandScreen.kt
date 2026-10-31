package com.wisdomtower.post

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostBrandScreen(
    brand: PostBrand,
    onBrand: (PostBrand) -> Unit,
    onPickLogo: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var activeTab by rememberSaveable { mutableStateOf("Footer") }
    var preview by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(brand) {
        val spec = postTemplate("social-follow")
        val sample = PostDoc(templateId = spec.id, texts = spec.samples)
        preview = withContext(Dispatchers.Default) { PostRenderer.thumbnail(context, sample, brand, 640).bitmap }
    }

    PostTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Brand", fontWeight = FontWeight.Bold) },
                    navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Outlined.ArrowBack, contentDescription = "Back") } }
                )
            },
            containerColor = PostPalette.Ink
        ) { padding ->
            Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 14.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                preview?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "Brand preview",
                        modifier = Modifier.fillMaxWidth().height(170.dp),
                        contentScale = ContentScale.Fit
                    )
                }
                Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                    listOf("Logo", "Colors", "Footer").forEach { tab ->
                        FilterChip(selected = activeTab == tab, onClick = { activeTab = tab }, label = { Text(tab) })
                    }
                }
                when (activeTab) {
                    "Logo" -> Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Logo placement", color = PostPalette.Text, fontWeight = FontWeight.Bold)
                        OutlinedButton(onClick = onPickLogo, modifier = Modifier.fillMaxWidth()) {
                            Text(if (brand.logoPath != null) "Replace logo" else "Pick logo from phone")
                        }
                        Text("Corner", fontSize = 12.sp, color = PostPalette.Muted)
                        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                            LogoCorner.values().forEach { corner ->
                                FilterChip(selected = brand.logoCorner == corner, onClick = { onBrand(brand.copy(logoCorner = corner)) }, label = { Text(corner.name.lowercase().replaceFirstChar { it.uppercase() }) })
                            }
                        }
                        Text("Size", fontSize = 12.sp, color = PostPalette.Muted)
                        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                            listOf("Small" to 0.8f, "Medium" to 1f, "Large" to 1.25f).forEach { (name, scale) ->
                                FilterChip(selected = abs(brand.logoScale - scale) < 0.01f, onClick = { onBrand(brand.copy(logoScale = scale)) }, label = { Text(name) })
                            }
                        }
                        if (brand.logoPath != null) TextButton(onClick = { onBrand(brand.copy(logoPath = null, logoStamp = System.currentTimeMillis())) }) { Text("Use bundled logo") }
                    }
                    "Colors" -> Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Academy palette", color = PostPalette.Text, fontWeight = FontWeight.Bold)
                        PostColorPicker("Main color", brand.primaryHex, brand, { if (it != null) onBrand(brand.copy(primaryHex = it)) }, includeBrandChoice = false)
                        PostColorPicker("Second color", brand.secondaryHex, brand, { if (it != null) onBrand(brand.copy(secondaryHex = it)) }, includeBrandChoice = false)
                        PostColorPicker("Light color", brand.lightHex, brand, { if (it != null) onBrand(brand.copy(lightHex = it)) }, includeBrandChoice = false)
                        PostColorPicker("Dark color", brand.darkHex, brand, { if (it != null) onBrand(brand.copy(darkHex = it)) }, includeBrandChoice = false)
                    }
                    else -> Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Footer and social links", color = PostPalette.Text, fontWeight = FontWeight.Bold)
                        FooterEditor(brand, onBrand)
                    }
                }
                Spacer(Modifier.height(4.dp))
            }
        }
    }
}
