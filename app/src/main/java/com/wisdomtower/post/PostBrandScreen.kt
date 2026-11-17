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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.graphics.Brush
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
 * A ready-made brand set: the four brand colors plus a backdrop look (PostBackdrops, docs/BACKDROPS.md). One tap sets Main, Second, Light, Dark
 * and the poster background together, so every theme changes the whole look of the posters, not only a tint.
 *
 * Colors follow the Wisdom Tower app: near-black or blue-black Dark, bright neon cyan #22E0FF / #00C4E6, gold #FFD54A / #FFC83D,
 * bronze #B8893A, white text. Main is the bright accent (bands, rings, buttons, glows); the renderer keeps the posters dark-first,
 * so white text never sits on a bright accent. Second is the other bright accent (badges, glows, logo-box edge).
 * look is the id of a BackdropLook (1 to 10).
 */
data class BrandTheme(val name: String, val primary: String, val secondary: String, val light: String, val dark: String, val look: Int = 0)

object BrandThemes {
    val all = listOf(
        BrandTheme("Wisdom Signature", "#22E0FF", "#FFD54A", "#FFFFFF", "#060B14", 1),
        BrandTheme("Gold Lead", "#FFD54A", "#22E0FF", "#FFFFFF", "#0A0805", 2),
        BrandTheme("Cyan Mono", "#22E0FF", "#7DF0FF", "#F2FCFF", "#04101A", 3),
        BrandTheme("Gold Mono", "#FFC83D", "#FFE08A", "#FFFAEB", "#0B0905", 4),
        BrandTheme("Cyan and White", "#22E0FF", "#FFFFFF", "#FFFFFF", "#070B12", 5),
        BrandTheme("Gold and White", "#FFD54A", "#FFFFFF", "#FFFBF0", "#0B0905", 6),
        BrandTheme("Midnight Cyan Gold", "#00C4E6", "#FFD54A", "#F6FAFF", "#070B1A", 7),
        BrandTheme("Charcoal Gold", "#FFD54A", "#22E0FF", "#F3F5F7", "#14171C", 8),
        BrandTheme("Bronze Ribbon", "#B8893A", "#FFD54A", "#FFF8E6", "#0E0B07", 9),
        BrandTheme("Neon Pink Touch", "#22E0FF", "#FFD54A", "#FFFFFF", "#0B0F1C", 10)
    )

    fun matches(t: BrandTheme, b: PostBrand): Boolean =
        t.primary.equals(b.primaryHex, ignoreCase = true) &&
            t.secondary.equals(b.secondaryHex, ignoreCase = true) &&
            t.light.equals(b.lightHex, ignoreCase = true) &&
            t.dark.equals(b.darkHex, ignoreCase = true) &&
            t.look == b.bgLook

    fun apply(t: BrandTheme, b: PostBrand): PostBrand =
        b.copy(primaryHex = t.primary, secondaryHex = t.secondary, lightHex = t.light, darkHex = t.dark, bgLook = t.look)
}

private fun themeColor(hex: String): Color =
    runCatching { Color(android.graphics.Color.parseColor(hex)) }.getOrDefault(Color.Gray)

/** The gradient of a look as a Compose brush, for the little cards. look null (Auto) shows the plain Dark color. */
private fun backdropBrush(look: BackdropLook?, fallbackDark: String): Brush {
    if (look == null) {
        val c = themeColor(fallbackDark)
        return Brush.linearGradient(listOf(c, c))
    }
    val colors = listOfNotNull(themeColor(look.from), look.mid?.let { themeColor(it) }, themeColor(look.to))
    return Brush.linearGradient(colors)
}

/**
 * Global Color Theme bar: 6 curated two-tone round swatch chips + "Template default" chip.
 * Tapping a theme updates brand.globalThemeId and restyles all 70 templates (including Notes/Study).
 */
@Composable
internal fun GlobalThemeSwatchBar(
    brand: PostBrand,
    onBrand: (PostBrand) -> Unit
) {
    val scroll = rememberScrollState()
    val currentId = brand.globalThemeId ?: PostThemes.TEMPLATE_DEFAULT_ID
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        PostSmallLabel("Global Color Theme (applies across all templates)")
        Row(
            Modifier.fillMaxWidth().horizontalScroll(scroll),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val isDefault = currentId == PostThemes.TEMPLATE_DEFAULT_ID || PostThemes.byId(currentId) == null
            CompactChip(
                label = "Template default",
                selected = isDefault,
                onClick = { onBrand(brand.copy(globalThemeId = PostThemes.TEMPLATE_DEFAULT_ID)) }
            )
            PostThemes.all.forEach { gt ->
                val selected = gt.id.equals(currentId, ignoreCase = true)
                val shape = RoundedCornerShape(18.dp)
                Row(
                    Modifier
                        .clip(shape)
                        .background(if (selected) PostPalette.Raised else PostPalette.Card)
                        .border(if (selected) 2.dp else 1.dp, if (selected) PostPalette.Mint else PostPalette.Line, shape)
                        .clickable {
                            onBrand(
                                brand.copy(
                                    globalThemeId = gt.id,
                                    primaryHex = gt.accentHex,
                                    secondaryHex = gt.secondaryHex,
                                    darkHex = gt.darkBgHex,
                                    lightHex = gt.lightBgHex
                                )
                            )
                        }
                        .padding(horizontal = 8.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .border(1.dp, PostPalette.Line, CircleShape)
                    ) {
                        Row(Modifier.fillMaxSize()) {
                            Box(Modifier.weight(1f).fillMaxSize().background(gt.darkBgColor))
                            Box(Modifier.weight(1f).fillMaxSize().background(gt.accentColor))
                        }
                    }
                    Text(
                        text = gt.name,
                        fontSize = 11.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
                        color = if (selected) PostPalette.Mint else PostPalette.Text,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

/** A card showing the background of a theme on top and its four colors as four bands under it, with its name below. */
@Composable
private fun ThemeCard(theme: BrandTheme, selected: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(12.dp)
    Column(Modifier.width(92.dp).clickable(onClick = onClick), horizontalAlignment = Alignment.CenterHorizontally) {
        Column(
            Modifier.fillMaxWidth().height(52.dp).clip(shape)
                .border(if (selected) 3.dp else 1.dp, if (selected) PostPalette.Mint else PostPalette.Line, shape)
        ) {
            Box(Modifier.fillMaxWidth().weight(1f).background(backdropBrush(PostBackdrops.byId(theme.look), theme.dark)))
            Row(Modifier.fillMaxWidth().height(14.dp)) {
                Box(Modifier.weight(1f).fillMaxSize().background(themeColor(theme.dark)))
                Box(Modifier.weight(1f).fillMaxSize().background(themeColor(theme.primary)))
                Box(Modifier.weight(1f).fillMaxSize().background(themeColor(theme.secondary)))
                Box(Modifier.weight(1f).fillMaxSize().background(themeColor(theme.light)))
            }
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

/** A card showing one background look (look null = Auto). Tapping it changes only the poster background. */
@Composable
private fun BackdropCard(name: String, look: BackdropLook?, darkHex: String, selected: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(12.dp)
    Column(Modifier.width(92.dp).clickable(onClick = onClick), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            Modifier.fillMaxWidth().height(52.dp).clip(shape)
                .background(backdropBrush(look, darkHex))
                .border(if (selected) 3.dp else 1.dp, if (selected) PostPalette.Mint else PostPalette.Line, shape)
        )
        Text(
            name,
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
 * The Colors tab starts with Themes: ready-made sets that change all four colors and the poster background with one tap.
 * The Background chip changes only the poster background (10 looks, or Auto).
 * The logo always sits in the header box at the top-right corner, so there is no corner choice.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostBrandScreen(
    brand: PostBrand,
    onBrand: (PostBrand) -> Unit,
    onPickLogo: () -> Unit,
    onBack: () -> Unit,
    onOpenChat: () -> Unit
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
                    navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Outlined.ArrowBack, contentDescription = "Back") } },
                    actions = { TextButton(onClick = onOpenChat) { Text("Chat link", fontWeight = FontWeight.Bold) } }
                )
            },
            containerColor = PostPalette.Ink
        ) { padding ->
            Column(Modifier.fillMaxSize().padding(padding)) {
                Box(Modifier.padding(horizontal = 12.dp, vertical = 4.dp)) {
                    GlobalThemeSwatchBar(brand = brand, onBrand = onBrand)
                }
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
                    Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    when (tab) {
                        "icons" -> {
                            PostSmallLabel("9:16 Story / Reels footer & logo position")
                            Row(
                                Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                FooterPosition.values().forEach { fp ->
                                    CompactChip(
                                        label = fp.label,
                                        selected = brand.footerPosition == fp,
                                        onClick = { onBrand(brand.copy(footerPosition = fp)) }
                                    )
                                }
                            }
                            FooterEditor(brand, onBrand)
                        }

                        "logo" -> {
                            Text(
                                "Transparent brand mark with auto-contrast luminance sampling and safe margins.",
                                fontSize = 13.sp,
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
                            PostSmallLabel("Default placement (templates can override unless locked per post)")
                            Row(
                                Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                LogoAnchor.values().forEach { anchor ->
                                    CompactChip(
                                        label = anchor.label,
                                        selected = brand.logoAnchor == anchor,
                                        onClick = { onBrand(brand.copy(logoAnchor = anchor)) }
                                    )
                                }
                            }
                            PostSmallLabel("Mark style")
                            Row(
                                Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                LogoStyle.values().forEach { st ->
                                    CompactChip(
                                        label = st.label,
                                        selected = brand.logoStyle == st,
                                        onClick = { onBrand(brand.copy(logoStyle = st)) }
                                    )
                                }
                            }
                            PostSmallLabel("Contrast mode")
                            Row(
                                Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                LogoContrast.values().forEach { cm ->
                                    CompactChip(
                                        label = cm.label,
                                        selected = brand.logoContrast == cm,
                                        onClick = { onBrand(brand.copy(logoContrast = cm)) }
                                    )
                                }
                            }
                            Row(
                                Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("Size", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PostPalette.Text)
                                listOf("Small" to 0.8f, "Medium" to 1f, "Large" to 1.25f).forEach { (name, scale) ->
                                    CompactChip(
                                        label = name,
                                        selected = kotlin.math.abs(brand.logoScale - scale) < 0.01f,
                                        onClick = { onBrand(brand.copy(logoScale = scale)) }
                                    )
                                }
                            }
                            PostLabeledSlider(
                                label = "Opacity",
                                value = brand.logoOpacity,
                                range = 0.25f..1f,
                                display = "${(brand.logoOpacity * 100).toInt()}%"
                            ) { v ->
                                onBrand(brand.copy(logoOpacity = v))
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
                                FilterChip(selected = colorSlot == "background", onClick = { colorSlot = "background" }, label = { Text("Background", fontWeight = FontWeight.Bold) })
                                FilterChip(selected = colorSlot == "primary", onClick = { colorSlot = "primary" }, label = { Text("Main", fontWeight = FontWeight.Bold) })
                                FilterChip(selected = colorSlot == "secondary", onClick = { colorSlot = "secondary" }, label = { Text("Second", fontWeight = FontWeight.Bold) })
                                FilterChip(selected = colorSlot == "light", onClick = { colorSlot = "light" }, label = { Text("Light", fontWeight = FontWeight.Bold) })
                                FilterChip(selected = colorSlot == "dark", onClick = { colorSlot = "dark" }, label = { Text("Dark", fontWeight = FontWeight.Bold) })
                            }
                            Text(
                                when (colorSlot) {
                                    "themes" -> "Tap one to set all four colors and the background at once"
                                    "background" -> "Tap one to change only the poster background"
                                    "primary" -> "Main color: accents, glows and footer"
                                    "secondary" -> "Second color: badges, highlights and the logo box edge"
                                    "light" -> "Light: text on dark posters"
                                    else -> "Dark: base of the posters, button text and the logo box"
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
                                "background" -> Row(
                                    Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    BackdropCard("Auto", null, brand.darkHex, brand.bgLook == 0) { onBrand(brand.copy(bgLook = 0)) }
                                    PostBackdrops.all.forEach { look ->
                                        BackdropCard(look.name, look, brand.darkHex, brand.bgLook == look.id) { onBrand(brand.copy(bgLook = look.id)) }
                                    }
                                }
                                "primary" -> PaletteStrip(brand.primaryHex, null, false) { hex -> if (hex != null) onBrand(brand.copy(primaryHex = hex)) }
                                "secondary" -> PaletteStrip(brand.secondaryHex, null, false) { hex -> if (hex != null) onBrand(brand.copy(secondaryHex = hex)) }
                                "light" -> PaletteStrip(brand.lightHex, null, false) { hex -> if (hex != null) onBrand(brand.copy(lightHex = hex)) }
                                else -> PaletteStrip(brand.darkHex, null, false) { hex -> if (hex != null) onBrand(brand.copy(darkHex = hex)) }
                            }
                            Text(
                                when (colorSlot) {
                                    "themes" -> "Each card shows the background, then Dark, Main, Second, Light. Fine-tune any of them in the other chips."
                                    "background" -> "Glows follow your Main and Second colors. Auto uses a plain tint of Dark."
                                    else -> "Glows and accents are built from these colors, so changing one restyles all templates."
                                },
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
