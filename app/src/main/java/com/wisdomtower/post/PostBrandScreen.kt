package com.wisdomtower.post

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

/**
 * The one place where the brand is set: social icons, logo, colors.
 * Same layout rule as the editor: the preview stays fixed at the top, categories are one sideways row of tabs,
 * and the options for the chosen category sit in an area of fixed height at the bottom, so nothing jumps.
 * The preview is the Follow us poster, so you see the icons big. Colors are picked from circles, never typed.
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
    var tab by rememberSaveable { mutableStateOf("icons") }
    var colorSlot by rememberSaveable { mutableStateOf("primary") }
    var preview by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(brand) {
        delay(150)
        val spec = postTemplate("social-follow")
        val sample = PostDoc(templateId = spec.id, texts = spec.samples)
        preview = withContext(Dispatchers.Default) { PostRenderer.thumbnail(context, sample, brand, 720).bitmap }
    }

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
            Column(Modifier.fillMaxSize().padding(padding)) {
                Box(Modifier.weight(1f).fillMaxWidth().padding(12.dp), contentAlignment = Alignment.Center) {
                    preview?.let {
                        Image(
                            bitmap = it.asImageBitmap(),
                            contentDescription = "Brand preview",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                Row(
                    Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(selected = tab == "icons", onClick = { tab = "icons" }, label = { Text("Social icons") })
                    FilterChip(selected = tab == "logo", onClick = { tab = "logo" }, label = { Text("Logo") })
                    FilterChip(selected = tab == "colors", onClick = { tab = "colors" }, label = { Text("Colors") })
                }

                Column(
                    Modifier.fillMaxWidth().height(250.dp).padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    when (tab) {
                        "icons" -> FooterEditor(brand, onBrand)

                        "logo" -> {
                            Row(
                                Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(onClick = onPickLogo) {
                                    Text(if (brand.logoPath != null) "Replace logo" else "Pick logo from phone")
                                }
                                if (brand.logoPath != null) {
                                    TextButton(onClick = { onBrand(brand.copy(logoPath = null, logoStamp = System.currentTimeMillis())) }) {
                                        Text("Use bundled logo")
                                    }
                                }
                            }
                            Row(
                                Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("Corner", fontSize = 12.sp, color = PostPalette.Muted)
                                LogoCorner.values().forEach { corner ->
                                    FilterChip(
                                        selected = brand.logoCorner == corner,
                                        onClick = { onBrand(brand.copy(logoCorner = corner)) },
                                        label = { Text(corner.name.lowercase().replaceFirstChar { it.uppercase() }) }
                                    )
                                }
                            }
                            Row(
                                Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("Size", fontSize = 12.sp, color = PostPalette.Muted)
                                listOf("Small" to 0.8f, "Medium" to 1f, "Large" to 1.25f).forEach { (name, scale) ->
                                    FilterChip(
                                        selected = kotlin.math.abs(brand.logoScale - scale) < 0.01f,
                                        onClick = { onBrand(brand.copy(logoScale = scale)) },
                                        label = { Text(name) }
                                    )
                                }
                            }
                        }

                        else -> {
                            Row(
                                Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilterChip(selected = colorSlot == "primary", onClick = { colorSlot = "primary" }, label = { Text("Main") })
                                FilterChip(selected = colorSlot == "secondary", onClick = { colorSlot = "secondary" }, label = { Text("Second") })
                                FilterChip(selected = colorSlot == "light", onClick = { colorSlot = "light" }, label = { Text("Light") })
                                FilterChip(selected = colorSlot == "dark", onClick = { colorSlot = "dark" }, label = { Text("Dark") })
                            }
                            Text(
                                when (colorSlot) {
                                    "primary" -> "Main color: accents and footer"
                                    "secondary" -> "Second color: glows and highlights"
                                    "light" -> "Light: backgrounds"
                                    else -> "Dark: text and dark posters"
                                },
                                fontSize = 12.sp,
                                color = PostPalette.Muted
                            )
                            when (colorSlot) {
                                "primary" -> PaletteStrip(brand.primaryHex, null, false) { hex -> if (hex != null) onBrand(brand.copy(primaryHex = hex)) }
                                "secondary" -> PaletteStrip(brand.secondaryHex, null, false) { hex -> if (hex != null) onBrand(brand.copy(secondaryHex = hex)) }
                                "light" -> PaletteStrip(brand.lightHex, null, false) { hex -> if (hex != null) onBrand(brand.copy(lightHex = hex)) }
                                else -> PaletteStrip(brand.darkHex, null, false) { hex -> if (hex != null) onBrand(brand.copy(darkHex = hex)) }
                            }
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
    }
}
