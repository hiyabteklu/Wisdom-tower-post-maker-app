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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext

/**
 * A ready-made set of the four brand colors. One tap sets Main, Second, Light and Dark together.
 * All ten sets stay inside the Wisdom Tower app palette: deep navy-charcoal, brand cyan, gold or bronze, and a neon pink glow.
 * Contrast of every set: Dark on Light is above 15:1, Second on Dark is above 7:1, and Main (cyan, gold or blue)
 * is about 3.5:1 or more on Light and 4:1 or more on Dark, which suits headings, bands, buttons and footers.
 */
data class BrandTheme(val name: String, val primary: String, val secondary: String, val light: String, val dark: String)

object BrandThemes {
    val all = listOf(
        BrandTheme("Wisdom Cyan", "#0A8DAD", "#FFD54A", "#F1FAFC", "#0A1220"),
        BrandTheme("Aqua Glow", "#0A8FAF", "#67E8F9", "#ECFEFF", "#07151F"),
        BrandTheme("Gold Standard", "#9A6A0A", "#FFD54A", "#FFF9E8", "#0D1117"),
        BrandTheme("Cyan Neon Pink", "#0A8DAD", "#FF5CD6", "#F4F9FB", "#0B0F1C"),
        BrandTheme("Bronze Ribbon", "#9C6A1C", "#E9B866", "#FAF5EC", "#14110D"),
        BrandTheme("Ice Midnight", "#0E8FB5", "#A5F3FC", "#F0F9FF", "#0B1626"),
        BrandTheme("Charcoal Cyan", "#0A8DAD", "#FFE066", "#F3F5F7", "#161A23"),
        BrandTheme("Gold Magenta", "#9A6A0A", "#FF5CCB", "#FFFBEF", "#120C1A"),
        BrandTheme("Deep Teal", "#0B8A8F", "#7CF2E1", "#EFFAF8", "#07191D"),
        BrandTheme("Cyan Blue", "#1C74D6", "#22D3EE", "#F2F8FF", "#0A1328")
    )

    fun matches(t: BrandTheme, b: PostBrand): Boolean =
        t.primary.equals(b.primaryHex, ignoreCase = true) &&
            t.secondary.equals(b.secondaryHex, ignoreCase = true) &&
            t.light.equals(b.lightHex, ignoreCase = true) &&
            t.dark.equals(b.darkHex, ignoreCase = true)

    fun apply(t: BrandTheme, b: PostBrand): PostBrand =
        b.copy(primaryHex = t.primary, secondaryHex = t.secondary, lightHex = t.light, darkHex = t.dark)
}

private fun themeColor(hex: String): Color =
    runCatching { Color(android.graphics.Color.parseColor(hex)) }.getOrDefault(Color.Gray)

/** A card showing the four colors of a theme as four bands, with its name under it. */
@Composable
private fun ThemeCard(theme: BrandTheme, selected: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(12.dp)
    Column(Modifier.width(92.dp).clickable(onClick = onClick), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            Modifier.fillMaxWidth().height(52.dp).clip(shape)
                .border(if (selected) 3.dp else 1.dp, if (selected) PostPalette.Mint else PostPalette.Line, shape)
        ) {
            Box(Modifier.weight(1f).fillMaxSize().background(themeColor(theme.dark)))
            Box(Modifier.weight(1f).fillMaxSize().background(themeColor(theme.primary)))
            Box(Modifier.weight(1f).fillMaxSize().background(themeColor(theme.secondary)))
            Box(Modifier.weight(1f).fillMaxSize().background(themeColor(theme.light)))
        }
        Text(
            theme.name,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = if (selected) PostPalette.Mint else PostPalette.Text,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

/**
 * The one place where the brand is set: social icons, logo, colors.
 * Same layout rule as the editor: the preview stays fixed at the top, categories are one sideways row of tabs,
 * and the options for the chosen category sit in an area of fixed height at the bottom, so nothing jumps.
 * The preview is the Follow us poster, so you see the icons big. Colors are picked from circles, never typed.
 * The Colors tab starts with Themes: ready-made sets that change all four colors with one tap.
 * The logo always sits in the header box at the top-right corner, so there is no corner choice.
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
    var colorSlot by rememberSaveable { mutableStateOf("themes") }
    var preview by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(brand) {
        delay(150)
        val spec = postTemplate("social-follow")
        val sample = PostDoc(templateId = spec.id, texts = spec.samples)
        // drawn straight at 720 px wide; a newer brand change stops this render early
        preview = withContext(Dispatchers.Default) { PostRenderer.preview(context, sample, brand, 720) { !isActive }.bitmap }
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
                    FilterChip(selected = tab == "icons", onClick = { tab = "icons" }, label = { Text("Social icons", fontWeight = FontWeight.Bold) })
                    FilterChip(selected = tab == "logo", onClick = { tab = "logo" }, label = { Text("Logo", fontWeight = FontWeight.Bold) })
                    FilterChip(selected = tab == "colors", onClick = { tab = "colors" }, label = { Text("Colors", fontWeight = FontWeight.Bold) })
                    FilterChip(selected = tab == "about", onClick = { tab = "about" }, label = { Text("About", fontWeight = FontWeight.Bold) })
                }

                Column(
                    Modifier.fillMaxWidth().height(250.dp).padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    when (tab) {
                        "icons" -> FooterEditor(brand, onBrand)

                        "logo" -> {
                            Text(
                                "Your logo is placed in a dark brand box in the top-right corner of every post.",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = PostPalette.Text
                            )
                            Row(
                                Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(onClick = onPickLogo) {
                                    Text(if (brand.logoPath != null) "Replace logo" else "Pick logo from phone", fontWeight = FontWeight.Bold)
                                }
                                if (brand.logoPath != null) {
                                    TextButton(onClick = { onBrand(brand.copy(logoPath = null, logoStamp = System.currentTimeMillis())) }) {
                                        Text("Use bundled logo", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            Row(
                                Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("Size", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PostPalette.Text)
                                listOf("Small" to 0.8f, "Medium" to 1f, "Large" to 1.25f).forEach { (name, scale) ->
                                    FilterChip(
                                        selected = kotlin.math.abs(brand.logoScale - scale) < 0.01f,
                                        onClick = { onBrand(brand.copy(logoScale = scale)) },
                                        label = { Text(name, fontWeight = FontWeight.Bold) }
                                    )
                                }
                            }
                        }

                        "about" -> {
                            Text(
                                "Wisdom Tower Post works fully offline. Templates, fonts, icons, photos and drafts all stay on this phone.",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = PostPalette.Text
                            )
                        }

                        else -> {
                            Row(
                                Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilterChip(selected = colorSlot == "themes", onClick = { colorSlot = "themes" }, label = { Text("Themes", fontWeight = FontWeight.Bold) })
                                FilterChip(selected = colorSlot == "primary", onClick = { colorSlot = "primary" }, label = { Text("Main", fontWeight = FontWeight.Bold) })
                                FilterChip(selected = colorSlot == "secondary", onClick = { colorSlot = "secondary" }, label = { Text("Second", fontWeight = FontWeight.Bold) })
                                FilterChip(selected = colorSlot == "light", onClick = { colorSlot = "light" }, label = { Text("Light", fontWeight = FontWeight.Bold) })
                                FilterChip(selected = colorSlot == "dark", onClick = { colorSlot = "dark" }, label = { Text("Dark", fontWeight = FontWeight.Bold) })
                            }
                            Text(
                                when (colorSlot) {
                                    "themes" -> "Tap one to set all four colors at once"
                                    "primary" -> "Main color: accents and footer"
                                    "secondary" -> "Second color: glows and highlights"
                                    "light" -> "Light: backgrounds"
                                    else -> "Dark: text, dark posters and the logo box"
                                },
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = PostPalette.Text
                            )
                            when (colorSlot) {
                                "themes" -> Row(
                                    Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    BrandThemes.all.forEach { theme ->
                                        ThemeCard(theme, BrandThemes.matches(theme, brand)) { onBrand(BrandThemes.apply(theme, brand)) }
                                    }
                                }
                                "primary" -> PaletteStrip(brand.primaryHex, null, false) { hex -> if (hex != null) onBrand(brand.copy(primaryHex = hex)) }
                                "secondary" -> PaletteStrip(brand.secondaryHex, null, false) { hex -> if (hex != null) onBrand(brand.copy(secondaryHex = hex)) }
                                "light" -> PaletteStrip(brand.lightHex, null, false) { hex -> if (hex != null) onBrand(brand.copy(lightHex = hex)) }
                                else -> PaletteStrip(brand.darkHex, null, false) { hex -> if (hex != null) onBrand(brand.copy(darkHex = hex)) }
                            }
                            Text(
                                if (colorSlot == "themes") "Each card shows Dark, Main, Second, Light. Fine-tune any of them in the other chips."
                                else "Every gradient and glow is built from these four, so changing one restyles all templates.",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = PostPalette.Muted
                            )
                        }
                    }
                }
            }
        }
    }
}
