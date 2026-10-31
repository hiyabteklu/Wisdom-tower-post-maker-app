package com.wisdomtower.post

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

/**
 * The one place where the brand is set: logo, colors, social icons. Sections are collapsed except one,
 * so the screen stays short. The preview at the top is the Follow us poster, so you see the icons big.
 * Everything here applies to every post that has no override. Colors are picked from circles, never typed.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostBrandScreen(
    brand: PostBrand,
    onBrand: (PostBrand) -> Unit,
    onPickLogo: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var open by rememberSaveable { mutableStateOf("footer") }
    var preview by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(brand) {
        delay(150)
        val spec = postTemplate("social-follow")
        val sample = PostDoc(templateId = spec.id, texts = spec.samples)
        preview = withContext(Dispatchers.Default) { PostRenderer.thumbnail(context, sample, brand, 720).bitmap }
    }

    fun toggle(name: String) { open = if (open == name) "" else name }

    PostTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Brand and social icons", fontWeight = FontWeight.Bold) },
                    navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Outlined.ArrowBack, contentDescription = "Back") } }
                )
            },
            containerColor = PostPalette.Ink
        ) { padding ->
            Column(
                Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                preview?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "Brand preview",
                        modifier = Modifier.fillMaxWidth().aspectRatio(1f).clip(RoundedCornerShape(12.dp))
                    )
                }

                BrandSection(
                    title = "Social icons",
                    summary = brand.footerItems.size.toString() + " icons, " + brand.iconLook.name.lowercase() + " look",
                    open = open == "footer",
                    onToggle = { toggle("footer") }
                ) {
                    FooterEditor(brand, onBrand)
                }

                BrandSection(
                    title = "Logo",
                    summary = (if (brand.logoPath != null) "Your logo" else "Bundled logo") + ", " + brand.logoCorner.name.lowercase(),
                    open = open == "logo",
                    onToggle = { toggle("logo") }
                ) {
                    OutlinedButton(onClick = onPickLogo, modifier = Modifier.fillMaxWidth()) {
                        Text(if (brand.logoPath != null) "Replace logo" else "Pick logo from phone")
                    }
                    Text("Corner", fontSize = 12.sp, color = PostPalette.Muted)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        LogoCorner.values().forEach { corner ->
                            FilterChip(
                                selected = brand.logoCorner == corner,
                                onClick = { onBrand(brand.copy(logoCorner = corner)) },
                                label = { Text(corner.name.lowercase().replaceFirstChar { it.uppercase() }) }
                            )
                        }
                    }
                    Text("Size", fontSize = 12.sp, color = PostPalette.Muted)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Small" to 0.8f, "Medium" to 1f, "Large" to 1.25f).forEach { (name, scale) ->
                            FilterChip(
                                selected = kotlin.math.abs(brand.logoScale - scale) < 0.01f,
                                onClick = { onBrand(brand.copy(logoScale = scale)) },
                                label = { Text(name) }
                            )
                        }
                    }
                    if (brand.logoPath != null) {
                        TextButton(onClick = { onBrand(brand.copy(logoPath = null, logoStamp = System.currentTimeMillis())) }) {
                            Text("Use the bundled logo instead")
                        }
                    }
                }

                BrandSection(
                    title = "Colors",
                    summary = "Four brand colors. Tap a circle to change one.",
                    open = open == "colors",
                    onToggle = { toggle("colors") }
                ) {
                    BrandColorStrip("Main color (accents, footer)", brand.primaryHex) { onBrand(brand.copy(primaryHex = it)) }
                    BrandColorStrip("Second color (glows, highlights)", brand.secondaryHex) { onBrand(brand.copy(secondaryHex = it)) }
                    BrandColorStrip("Light (backgrounds)", brand.lightHex) { onBrand(brand.copy(lightHex = it)) }
                    BrandColorStrip("Dark (text, dark posters)", brand.darkHex) { onBrand(brand.copy(darkHex = it)) }
                    Text(
                        "Every gradient, ring and glow is built from these four, so changing one restyles all templates.",
                        fontSize = 11.sp,
                        color = PostPalette.Muted
                    )
                }
            }
        }
    }
}

@Composable
private fun BrandSection(
    title: String,
    summary: String,
    open: Boolean,
    onToggle: () -> Unit,
    content: @Composable () -> Unit
) {
    Card(colors = CardDefaults.cardColors(containerColor = PostPalette.Surface), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.fillMaxWidth().padding(12.dp)) {
            Row(Modifier.fillMaxWidth().clickable { onToggle() }, verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(title, fontWeight = FontWeight.Bold)
                    Text(summary, fontSize = 12.sp, color = PostPalette.Muted)
                }
                Icon(if (open) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore, contentDescription = null)
            }
            if (open) {
                Spacer(Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) { content() }
            }
        }
    }
}

/** One brand color: its name, then one horizontal strip of circles. */
@Composable
private fun BrandColorStrip(label: String, value: String, onPick: (String) -> Unit) {
    Column {
        Text(label, fontSize = 12.sp, color = PostPalette.Muted)
        PaletteStrip(value, null, false) { hex -> if (hex != null) onPick(hex) }
    }
}
