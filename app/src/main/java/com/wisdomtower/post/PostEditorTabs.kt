package com.wisdomtower.post

import android.graphics.Bitmap
import android.util.LruCache
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.FontDownload
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material.icons.outlined.TextFields
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import kotlin.math.roundToInt

/**
 * In-memory LRU cache for template strip thumbnails so scrolling and slider drags never re-render strip cards.
 */
private object StripThumbCache {
    private val cache = object : LruCache<String, Bitmap>(60) {
        override fun sizeOf(key: String, value: Bitmap): Int = 1
    }

    @Synchronized
    fun get(key: String): Bitmap? {
        val b = cache.get(key)
        return if (b != null && !b.isRecycled) b else null
    }

    @Synchronized
    fun put(key: String, bitmap: Bitmap) {
        if (!bitmap.isRecycled) cache.put(key, bitmap)
    }
}

internal data class EditorSlider(
    val label: String,
    val value: Float,
    val range: ClosedFloatingPointRange<Float>,
    val display: String,
    val onChange: (Float) -> Unit
)

internal val OrderedTemplateFamilies = listOf(
    "All", "Letter", "Type", "Photo", "Info", "Marketing", "Study", "Device"
)

internal fun filterTemplatesByFamily(family: String, library: PostLibrary? = null): List<PostTemplateSpec> {
    val list = when (family) {
        "All" -> PostTemplates
        "Saved" -> if (library != null) PostTemplates.filter { it.id in library.bookmarks } else PostTemplates
        "Info" -> PostTemplates.filter {
            it.family.equals("Info", ignoreCase = true) || it.family.equals("Information", ignoreCase = true)
        }
        else -> PostTemplates.filter { it.family.equals(family, ignoreCase = true) }
    }
    return list.ifEmpty { PostTemplates }
}

private val StripColors = listOf(
    "#FFFFFF", "#F8FAFC", "#CBD5E1", "#475569", "#1E293B", "#0F172A", "#05070B", "#000000",
    "#EF4444", "#DC2626", "#F97316", "#FACC15", "#FFD84D", "#22C55E", "#10B981", "#14B8A6",
    "#06B6D4", "#0EA5E9", "#3B82F6", "#2563EB", "#6366F1", "#8B5CF6", "#A855F7", "#EC4899"
)

@Composable
internal fun PaletteStrip(
    currentHex: String?,
    brandHex: String?,
    allowAuto: Boolean,
    onPick: (String?) -> Unit
) {
    val cleanBrand = brandHex?.uppercase()?.takeIf { it.matches(Regex("^#[0-9A-F]{6}$")) }
    val cleanCurrent = currentHex?.uppercase()?.takeIf { it.matches(Regex("^#[0-9A-F]{6}$")) }
    val colors = (listOfNotNull(cleanBrand, cleanCurrent) + StripColors).distinct()
    val scroll = rememberScrollState()

    Row(
        Modifier
            .fillMaxWidth()
            .horizontalScrollWithFade(scroll, PostPalette.Surface)
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (allowAuto) {
            PostSwatch(null, currentHex.isNullOrBlank()) { onPick(null) }
        }
        colors.forEach { hex ->
            PostSwatch(hex, hex.equals(currentHex, ignoreCase = true)) { onPick(hex) }
        }
    }
}

@Composable
internal fun TemplateStrip(
    doc: PostDoc,
    onDoc: (PostDoc) -> Unit,
    library: PostLibrary,
    onOpenLibrary: () -> Unit,
    brand: PostBrand = PostBrand(),
    selectedFamily: String = "All",
    onSelectFamily: ((String) -> Unit)? = null
) {
    val context = LocalContext.current
    val activeSpec = postTemplate(doc.templateId)
    var localFamily by rememberSaveable { mutableStateOf("All") }
    val activeFamily = if (onSelectFamily != null) selectedFamily else localFamily
    val setFamily: (String) -> Unit = { fam ->
        if (onSelectFamily != null) onSelectFamily(fam) else localFamily = fam
    }

    val families = if (library.bookmarks.isNotEmpty()) {
        OrderedTemplateFamilies + "Saved"
    } else {
        OrderedTemplateFamilies
    }
    val filtered = filterTemplatesByFamily(activeFamily, library)
    val listState = rememberLazyListState()
    val filterScroll = rememberScrollState()

    // Keep the horizontal template strip in sync when swiping the preview pager
    LaunchedEffect(activeSpec.id, activeFamily, filtered.size) {
        val idx = filtered.indexOfFirst { it.id == activeSpec.id }
        if (idx >= 0) {
            runCatching { listState.animateScrollToItem(idx) }
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        // Single Family filter row + Gallery button
        Row(
            Modifier.fillMaxWidth().horizontalScrollWithFade(filterScroll, PostPalette.Surface),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CompactChip(
                label = "Gallery",
                icon = Icons.Outlined.GridView,
                selected = false,
                onClick = onOpenLibrary
            )
            families.forEach { fam ->
                CompactChip(
                    label = fam,
                    selected = activeFamily == fam,
                    onClick = { setFamily(fam) }
                )
            }
        }

        // Real cached thumbnail cards synced with the swipe pager
        LazyRow(
            state = listState,
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            itemsIndexed(filtered, key = { _, spec -> spec.id }) { _, spec ->
                val isSelected = spec.id == activeSpec.id
                val shape = RoundedCornerShape(10.dp)
                val heroWord = doc.texts[PostSlots.HEADLINE]?.take(24) ?: ""
                val thumbKey = remember(spec.id, doc.size, heroWord, brand.primaryHex, brand.darkHex, brand.logoStamp) {
                    "${spec.id}:${doc.size.name}:${heroWord.hashCode()}:${brand.primaryHex}:${brand.darkHex}:${brand.logoStamp}"
                }
                val thumbBmp by produceState<Bitmap?>(StripThumbCache.get(thumbKey), thumbKey) {
                    if (value != null) return@produceState
                    val bmp = withContext(Dispatchers.Default) {
                        val tDoc = PostDrafts.factory(spec.id, doc.size).copy(
                            texts = PostSlots.withDerivedSlots(doc.texts, spec.slots),
                            photoUri = doc.photoUri,
                            secondaryPhotoUri = doc.secondaryPhotoUri
                        )
                        PostRenderer.thumbnail(context, tDoc, brand, 168).bitmap
                    }
                    StripThumbCache.put(thumbKey, bmp)
                    value = bmp
                }

                Column(
                    Modifier
                        .width(104.dp)
                        .clip(shape)
                        .background(if (isSelected) PostPalette.Raised else PostPalette.Card)
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) PostPalette.Mint else PostPalette.Line,
                            shape = shape
                        )
                        .clickable {
                            val nextTexts = PostSlots.withDerivedSlots(doc.texts, spec.slots)
                            onDoc(
                                doc.copy(
                                    templateId = spec.id,
                                    lookIndex = 0,
                                    texts = nextTexts
                                )
                            )
                        }
                        .padding(5.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(58.dp)
                            .clip(RoundedCornerShape(7.dp))
                            .background(PostPalette.Ink),
                        contentAlignment = Alignment.Center
                    ) {
                        val b = thumbBmp
                        if (b != null && !b.isRecycled) {
                            Image(
                                bitmap = b.asImageBitmap(),
                                contentDescription = spec.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                    Text(
                        text = spec.name,
                        color = if (isSelected) PostPalette.Mint else PostPalette.Text,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        // Look chips when the template has color & mood looks
        if (activeSpec.looks.isNotEmpty()) {
            val lookScroll = rememberScrollState()
            Row(
                Modifier.fillMaxWidth().horizontalScrollWithFade(lookScroll, PostPalette.Surface),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PostSmallLabel("Look")
                val totalLooks = activeSpec.looks.size.coerceAtLeast(1)
                for (i in 0 until totalLooks) {
                    val lookName = activeSpec.looks.getOrNull(i)?.name ?: "Look ${i + 1}"
                    CompactChip(
                        label = "${i + 1}: $lookName",
                        selected = doc.lookIndex == i,
                        onClick = {
                            val look = activeSpec.looks.getOrNull(i)
                            onDoc(
                                doc.copy(
                                    lookIndex = i,
                                    accentHex = look?.accentHex,
                                    bgMode = if (look?.bgHex != null) BgMode.SOLID else BgMode.TEMPLATE,
                                    bgStartHex = look?.bgHex ?: doc.bgStartHex
                                )
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
internal fun EditorTabs(
    tab: String,
    onTab: (String) -> Unit,
    doc: PostDoc,
    onDoc: (PostDoc) -> Unit,
    brand: PostBrand,
    library: PostLibrary,
    selectedSlot: String?,
    onSelectSlot: (String?) -> Unit,
    selectedIcon: Int,
    onSelectIcon: (Int) -> Unit,
    moveLogo: Boolean,
    onMoveLogo: (Boolean) -> Unit,
    moveOn: Boolean,
    onMove: (Boolean) -> Unit,
    onPickPhoto: () -> Unit,
    onPickBackgroundPhoto: () -> Unit,
    onPickLogo: () -> Unit,
    onBringWords: (() -> Unit)?,
    onOpenLibrary: () -> Unit,
    selectedFamily: String = "All",
    onSelectFamily: ((String) -> Unit)? = null,
    showTabBar: Boolean = true
) {
    val spec = postTemplate(doc.templateId).withLookIndex(doc.lookIndex)
    val layout = spec.layout(doc.size)
    var styleSub by rememberSaveable { mutableStateOf("colors") }
    var photoSub by rememberSaveable { mutableStateOf("bg") }
    var iconGroup by rememberSaveable { mutableStateOf("mine") }

    val tabs = listOf(
        Triple("template", "Template", Icons.Outlined.Dashboard),
        Triple("words", "Words", Icons.Outlined.TextFields),
        Triple("style", "Style", Icons.Outlined.Palette),
        Triple("photo", "Photo", Icons.Outlined.Image),
        Triple("icons", "Icons", Icons.Outlined.Category)
    )

    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (showTabBar) {
            val tabScroll = rememberScrollState()
            Row(
                Modifier.fillMaxWidth().horizontalScrollWithFade(tabScroll, PostPalette.Surface),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                tabs.forEach { (id, label, icon) ->
                    CompactChip(
                        label = label,
                        icon = icon,
                        selected = tab == id,
                        onClick = { onTab(id) }
                    )
                }
            }
        }

        when (tab) {
            "template" -> Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                TemplateStrip(
                    doc = doc,
                    onDoc = onDoc,
                    library = library,
                    onOpenLibrary = onOpenLibrary,
                    brand = brand,
                    selectedFamily = selectedFamily,
                    onSelectFamily = onSelectFamily
                )
            }

            "words" -> Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                if (onBringWords != null || doc.hasOffsets()) {
                    val actionScroll = rememberScrollState()
                    Row(
                        Modifier.fillMaxWidth().horizontalScrollWithFade(actionScroll, PostPalette.Surface),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (onBringWords != null) {
                            CompactButton(
                                label = "Last words",
                                icon = Icons.Outlined.History,
                                onClick = onBringWords
                            )
                        }
                        if (doc.hasOffsets()) {
                            CompactButton(
                                label = "Reset boxes",
                                icon = Icons.Outlined.RestartAlt,
                                onClick = { onDoc(doc.withoutOffsets()) }
                            )
                        }
                    }
                }

                // Post-wide size & shadow grouped in a compact foldable header
                FoldableSection(
                    title = "All words size & shadow",
                    summary = "${(doc.textScale * 100).roundToInt()}% · ${if (doc.shadowAll.strength == 0) "Auto" else doc.shadowAll.strength}"
                ) {
                    PostLabeledSlider(
                        label = "All size",
                        value = doc.textScale,
                        range = 0.65f..1.45f,
                        display = "${(doc.textScale * 100).roundToInt()}%"
                    ) { v ->
                        onDoc(doc.copy(textScale = v))
                    }
                    PostLabeledSlider(
                        label = "All shadow",
                        value = doc.shadowAll.strength.toFloat(),
                        range = 0f..100f,
                        display = if (doc.shadowAll.strength == 0) "Auto" else "${doc.shadowAll.strength}"
                    ) { v ->
                        onDoc(doc.copy(shadowAll = doc.shadowAll.copy(strength = v.roundToInt().coerceIn(0, 100))))
                    }
                }

                val slots = spec.slots.ifEmpty {
                    listOf(PostSlots.EYEBROW, PostSlots.HEADLINE, PostSlots.BODY)
                }
                slots.forEach { slot ->
                    WordBox(
                        slot = slot,
                        doc = doc,
                        onDoc = onDoc,
                        brand = brand,
                        selectedOnCanvas = selectedSlot == slot,
                        onSelectOnCanvas = { onSelectSlot(if (selectedSlot == slot) null else slot) }
                    )
                }

                OtherTextStyles(doc = doc, onDoc = onDoc, brand = brand)
            }

            "style" -> Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                val styleScroll = rememberScrollState()
                Row(
                    Modifier.fillMaxWidth().horizontalScrollWithFade(styleScroll, PostPalette.Surface),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        Triple("colors", "Colors", Icons.Outlined.Palette),
                        Triple("backdrop", "Backdrop", Icons.Outlined.Layers),
                        Triple("fonts", "Fonts", Icons.Outlined.FontDownload),
                        Triple("logo", "Logo & Footer", Icons.Outlined.AutoAwesome)
                    ).forEach { (id, label, icon) ->
                        CompactChip(
                            label = label,
                            icon = icon,
                            selected = styleSub == id,
                            onClick = { styleSub = id }
                        )
                    }
                }

                when (styleSub) {
                    "colors" -> Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        PostSmallLabel("Accent color (A = brand default)")
                        PaletteStrip(
                            currentHex = doc.accentHex,
                            brandHex = brand.accentHex,
                            allowAuto = true,
                            onPick = { hex -> onDoc(doc.copy(accentHex = hex)) }
                        )
                        PostSmallLabel("Text color (A = template default)")
                        PaletteStrip(
                            currentHex = doc.textHex,
                            brandHex = brand.lightHex,
                            allowAuto = true,
                            onPick = { hex -> onDoc(doc.copy(textHex = hex)) }
                        )
                        PostSmallLabel("Background color (A = template default)")
                        PaletteStrip(
                            currentHex = if (doc.bgMode == BgMode.SOLID) doc.bgStartHex else null,
                            brandHex = brand.darkHex,
                            allowAuto = true,
                            onPick = { hex ->
                                if (hex == null) {
                                    onDoc(doc.copy(bgMode = BgMode.TEMPLATE, backgroundHex = null))
                                } else {
                                    onDoc(doc.copy(bgMode = BgMode.SOLID, bgStartHex = hex, backgroundHex = hex))
                                }
                            }
                        )
                    }

                    "backdrop" -> Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        PostSmallLabel("Background pattern")
                        val patScroll = rememberScrollState()
                        Row(
                            Modifier.fillMaxWidth().horizontalScrollWithFade(patScroll, PostPalette.Surface),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            CompactChip(
                                label = "Template",
                                selected = doc.backdrop == null,
                                onClick = { onDoc(doc.copy(backdrop = null)) }
                            )
                            Backdrop.values().forEach { b ->
                                CompactChip(
                                    label = b.name.lowercase().replaceFirstChar { it.uppercase() },
                                    selected = doc.backdrop == b,
                                    onClick = { onDoc(doc.copy(backdrop = b)) }
                                )
                            }
                        }

                        PostLabeledSlider(
                            label = "Panels",
                            value = doc.panelOpacity.toFloat(),
                            range = 0f..100f,
                            display = "${doc.panelOpacity}%"
                        ) { v ->
                            onDoc(doc.copy(panelOpacity = v.roundToInt().coerceIn(0, 100)))
                        }

                        PostSmallLabel("Glow & grain")
                        val meshScroll = rememberScrollState()
                        Row(
                            Modifier.fillMaxWidth().horizontalScrollWithFade(meshScroll, PostPalette.Surface),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            MeshStyle.values().forEach { m ->
                                CompactChip(
                                    label = m.label,
                                    selected = doc.meshStyle == m,
                                    onClick = { onDoc(doc.copy(meshStyle = m)) }
                                )
                            }
                            CompactChip(
                                label = "Film grain",
                                selected = doc.grain,
                                onClick = { onDoc(doc.copy(grain = !doc.grain)) }
                            )
                        }
                    }

                    "fonts" -> FontsSection(doc = doc, onDoc = onDoc)

                    "logo" -> Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        PostSwitchRow("Show logo mark", doc.showLogo) { onDoc(doc.copy(showLogo = it)) }
                        PostSwitchRow("Show social footer", doc.showFooter) { onDoc(doc.copy(showFooter = it)) }
                        CompactButton(
                            label = "Change logo image",
                            icon = Icons.Outlined.Image,
                            onClick = onPickLogo
                        )
                        if (doc.showLogo) {
                            PostSmallLabel("Logo anchor on this post")
                            val anchorScroll = rememberScrollState()
                            Row(
                                Modifier.fillMaxWidth().horizontalScrollWithFade(anchorScroll, PostPalette.Surface),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                CompactChip(
                                    label = "Template default",
                                    selected = doc.logoAnchor == null,
                                    onClick = { onDoc(doc.copy(logoAnchor = null, logoCx = null, logoCy = null)) }
                                )
                                LogoAnchor.values().forEach { a ->
                                    CompactChip(
                                        label = a.label,
                                        selected = doc.logoAnchor == a,
                                        onClick = { onDoc(doc.copy(logoAnchor = a, logoCx = null, logoCy = null)) }
                                    )
                                }
                            }
                            PostSmallLabel("Logo style")
                            val lStyleScroll = rememberScrollState()
                            Row(
                                Modifier.fillMaxWidth().horizontalScrollWithFade(lStyleScroll, PostPalette.Surface),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                CompactChip(
                                    label = "Brand default",
                                    selected = doc.logoStyle == null,
                                    onClick = { onDoc(doc.copy(logoStyle = null)) }
                                )
                                LogoStyle.values().forEach { st ->
                                    CompactChip(
                                        label = st.label,
                                        selected = doc.logoStyle == st,
                                        onClick = { onDoc(doc.copy(logoStyle = st)) }
                                    )
                                }
                            }
                            PostSmallLabel("Logo contrast")
                            val lContrastScroll = rememberScrollState()
                            Row(
                                Modifier.fillMaxWidth().horizontalScrollWithFade(lContrastScroll, PostPalette.Surface),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                CompactChip(
                                    label = "Brand default",
                                    selected = doc.logoContrast == null,
                                    onClick = { onDoc(doc.copy(logoContrast = null)) }
                                )
                                LogoContrast.values().forEach { cm ->
                                    CompactChip(
                                        label = cm.label,
                                        selected = doc.logoContrast == cm,
                                        onClick = { onDoc(doc.copy(logoContrast = cm)) }
                                    )
                                }
                            }
                            val activeLogoOp = doc.logoOpacity ?: brand.logoOpacity
                            PostLabeledSlider(
                                label = "Opacity",
                                value = activeLogoOp,
                                range = 0.25f..1f,
                                display = "${(activeLogoOp * 100).roundToInt()}%"
                            ) { v ->
                                onDoc(doc.copy(logoOpacity = v))
                            }
                            LogoMoveRow(doc = doc, onDoc = onDoc, moveLogo = moveLogo, onMoveLogo = onMoveLogo)
                        }
                    }
                }
            }

            "photo" -> {
                val context = LocalContext.current
                val secondaryPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
                    if (uri != null) {
                        val stored = PostStore.importPhoto(context, uri)
                        if (stored != null) onDoc(doc.copy(secondaryPhotoUri = stored))
                        else Toast.makeText(context, "That file is not a readable image", Toast.LENGTH_LONG).show()
                    }
                }
                val tertiaryPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
                    if (uri != null) {
                        val stored = PostStore.importPhoto(context, uri)
                        if (stored != null) onDoc(doc.copy(tertiaryPhotoUri = stored))
                        else Toast.makeText(context, "That file is not a readable image", Toast.LENGTH_LONG).show()
                    }
                }
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    val photoSubScroll = rememberScrollState()
                    Row(
                        Modifier.fillMaxWidth().horizontalScrollWithFade(photoSubScroll, PostPalette.Surface),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            Triple("bg", "Bg photo", Icons.Outlined.Image),
                            Triple("window", "Frame photos", Icons.Outlined.PhotoCamera),
                            Triple("cutout", "Cut-out & Move", Icons.Outlined.Tune)
                        ).forEach { (id, label, icon) ->
                            CompactChip(
                                label = label,
                                icon = icon,
                                selected = photoSub == id,
                                onClick = { photoSub = id }
                            )
                        }
                    }

                    when (photoSub) {
                        "bg" -> Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            val bgBtnScroll = rememberScrollState()
                            Row(
                                Modifier.fillMaxWidth().horizontalScrollWithFade(bgBtnScroll, PostPalette.Surface),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CompactButton(
                                    label = if (doc.backgroundPhotoUri == null) "Pick bg photo" else "Change bg photo",
                                    icon = Icons.Outlined.Image,
                                    onClick = onPickBackgroundPhoto
                                )
                                if (!doc.photoUri.isNullOrBlank()) {
                                    CompactChip(
                                        label = "Use frame photo",
                                        selected = doc.photoAsBackground,
                                        onClick = {
                                            val next = !doc.photoAsBackground
                                            val nextOpacity = if (next && doc.backgroundPhotoOpacity <= 0.01f) 0.55f else doc.backgroundPhotoOpacity
                                            onDoc(doc.copy(photoAsBackground = next, backgroundPhotoOpacity = nextOpacity))
                                        }
                                    )
                                }
                                if (doc.backgroundPhotoUri != null || doc.photoAsBackground) {
                                    CompactButton(
                                        label = "Remove",
                                        icon = Icons.Outlined.RestartAlt,
                                        onClick = {
                                            onDoc(
                                                doc.copy(
                                                    backgroundPhotoUri = null,
                                                    photoAsBackground = false,
                                                    backgroundPhotoOpacity = 0.35f,
                                                    backgroundPhotoDim = 0,
                                                    backgroundPhotoBlur = 0,
                                                    backgroundPhotoZoom = 1f,
                                                    backgroundPhotoTone = BgPhotoTone.NONE
                                                )
                                            )
                                        }
                                    )
                                }
                            }

                            val hasBgPhoto = !doc.backgroundPhotoUri.isNullOrBlank() || (doc.photoAsBackground && !doc.photoUri.isNullOrBlank())
                            if (hasBgPhoto) {
                                PostLabeledSlider(
                                    label = "Strength",
                                    value = doc.backgroundPhotoOpacity,
                                    range = 0f..1f,
                                    display = "${(doc.backgroundPhotoOpacity * 100).roundToInt()}%"
                                ) { v ->
                                    onDoc(doc.copy(backgroundPhotoOpacity = v))
                                }

                                PostLabeledSlider(
                                    label = "Darken",
                                    value = doc.backgroundPhotoDim.toFloat(),
                                    range = 0f..100f,
                                    display = "${doc.backgroundPhotoDim}%"
                                ) { v ->
                                    onDoc(doc.copy(backgroundPhotoDim = v.roundToInt().coerceIn(0, 100)))
                                }

                                PostLabeledSlider(
                                    label = "Blur",
                                    value = doc.backgroundPhotoBlur.toFloat(),
                                    range = 0f..100f,
                                    display = "${doc.backgroundPhotoBlur}%"
                                ) { v ->
                                    onDoc(doc.copy(backgroundPhotoBlur = v.roundToInt().coerceIn(0, 100)))
                                }

                                PostLabeledSlider(
                                    label = "Zoom",
                                    value = doc.backgroundPhotoZoom,
                                    range = 1f..2.5f,
                                    display = String.format("%.1fx", doc.backgroundPhotoZoom)
                                ) { v ->
                                    onDoc(doc.copy(backgroundPhotoZoom = v))
                                }

                                PostLabeledSlider(
                                    label = "Panels",
                                    value = doc.panelOpacity.toFloat(),
                                    range = 0f..100f,
                                    display = "${doc.panelOpacity}%"
                                ) { v ->
                                    onDoc(doc.copy(panelOpacity = v.roundToInt().coerceIn(0, 100)))
                                }

                                val toneScroll = rememberScrollState()
                                Row(
                                    Modifier.fillMaxWidth().horizontalScrollWithFade(toneScroll, PostPalette.Surface),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    PostSmallLabel("Tone")
                                    BgPhotoTone.values().forEach { t ->
                                        CompactChip(
                                            label = when (t) {
                                                BgPhotoTone.NONE -> "Normal"
                                                BgPhotoTone.MONO -> "B&W"
                                                BgPhotoTone.DUOTONE -> "Duotone"
                                            },
                                            selected = doc.backgroundPhotoTone == t,
                                            onClick = { onDoc(doc.copy(backgroundPhotoTone = t)) }
                                        )
                                    }
                                }
                            }
                        }

                        "window" -> Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            val winBtnScroll = rememberScrollState()
                            Row(
                                Modifier.fillMaxWidth().horizontalScrollWithFade(winBtnScroll, PostPalette.Surface),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                CompactButton(
                                    label = if (doc.photoUri == null) "Pick Photo 1" else "Change Photo 1",
                                    icon = Icons.Outlined.PhotoCamera,
                                    onClick = onPickPhoto
                                )
                                if (doc.photoUri != null) {
                                    CompactButton(
                                        label = "Clear 1",
                                        icon = Icons.Outlined.RestartAlt,
                                        onClick = {
                                            onDoc(doc.copy(photoUri = null, photoZoom = 1f, photoFocusX = 0.5f, photoFocusY = 0.5f))
                                        }
                                    )
                                }
                                CompactButton(
                                    label = if (doc.secondaryPhotoUri == null) "Pick Photo 2" else "Change Photo 2",
                                    icon = Icons.Outlined.PhotoCamera,
                                    onClick = { secondaryPicker.launch(arrayOf("image/*")) }
                                )
                                if (doc.secondaryPhotoUri != null) {
                                    CompactButton(
                                        label = "Clear 2",
                                        icon = Icons.Outlined.RestartAlt,
                                        onClick = { onDoc(doc.copy(secondaryPhotoUri = null)) }
                                    )
                                }
                                CompactButton(
                                    label = if (doc.tertiaryPhotoUri == null) "Pick Photo 3" else "Change Photo 3",
                                    icon = Icons.Outlined.PhotoCamera,
                                    onClick = { tertiaryPicker.launch(arrayOf("image/*")) }
                                )
                                if (doc.tertiaryPhotoUri != null) {
                                    CompactButton(
                                        label = "Clear 3",
                                        icon = Icons.Outlined.RestartAlt,
                                        onClick = { onDoc(doc.copy(tertiaryPhotoUri = null)) }
                                    )
                                }
                            }
                            if (doc.photoUri != null) {
                                PostLabeledSlider(
                                    label = "Zoom",
                                    value = doc.photoZoom,
                                    range = 1f..3f,
                                    display = String.format("%.1fx", doc.photoZoom)
                                ) { onDoc(doc.copy(photoZoom = it)) }
                                PostLabeledSlider(
                                    label = "Focus X",
                                    value = doc.photoFocusX,
                                    range = 0f..1f,
                                    display = "${(doc.photoFocusX * 100).roundToInt()}%"
                                ) { onDoc(doc.copy(photoFocusX = it)) }
                                PostLabeledSlider(
                                    label = "Focus Y",
                                    value = doc.photoFocusY,
                                    range = 0f..1f,
                                    display = "${(doc.photoFocusY * 100).roundToInt()}%"
                                ) { onDoc(doc.copy(photoFocusY = it)) }
                            }
                            if (layout.photo != null) {
                                val frameScroll = rememberScrollState()
                                Row(
                                    Modifier.fillMaxWidth().horizontalScrollWithFade(frameScroll, PostPalette.Surface),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    PostSmallLabel("Frame")
                                    CompactChip(
                                        label = "Template",
                                        selected = doc.photoFrame == null,
                                        onClick = { onDoc(doc.copy(photoFrame = null)) }
                                    )
                                    PhotoFrame.values().forEach { frame ->
                                        CompactChip(
                                            label = frame.label,
                                            selected = doc.photoFrame == frame,
                                            onClick = { onDoc(doc.copy(photoFrame = frame)) }
                                        )
                                    }
                                    CompactChip(
                                        label = "Border",
                                        selected = doc.frameBorder,
                                        onClick = { onDoc(doc.copy(frameBorder = !doc.frameBorder)) }
                                    )
                                }
                            }
                        }

                        "cutout" -> EngineControls(
                            doc = doc,
                            onDoc = onDoc,
                            slots = spec.slots,
                            hasPhotoWindow = layout.photo != null,
                            moveOn = moveOn,
                            onMove = onMove
                        )
                    }
                }
            }

            "icons" -> Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                val slider = if (iconGroup == "size") iconSizeSlider(doc, selectedIcon, onDoc) else null
                if (slider != null) {
                    PostLabeledSlider(
                        label = slider.label,
                        value = slider.value,
                        range = slider.range,
                        display = slider.display,
                        onChange = slider.onChange
                    )
                }
                val iconScroll = rememberScrollState()
                Row(
                    Modifier.fillMaxWidth().horizontalScrollWithFade(iconScroll, PostPalette.Surface),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        "mine" to "My icons",
                        "shapes" to "Shapes",
                        "placed" to "On post",
                        "size" to "Size",
                        "color" to "Color",
                        "layer" to "Layer"
                    ).forEach { (id, label) ->
                        CompactChip(
                            label = label,
                            selected = iconGroup == id,
                            onClick = { iconGroup = id }
                        )
                    }
                }
                IconOptions(
                    group = iconGroup,
                    doc = doc,
                    onDoc = onDoc,
                    selected = selectedIcon,
                    onSelect = onSelectIcon
                )
            }
        }
        Spacer(Modifier.height(4.dp))
    }
}
