package com.wisdomtower.post

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlin.math.abs

private const val MAX_ICONS = 4
private val IconColors = listOf(
    "#FFFFFF", "#F4C95D", "#5DE0A5", "#229ED9", "#E1306C", "#FF0000", "#25D366", "#8A5CF6", "#FF8A3D", "#141414"
)

/**
 * Everything about the social icons, laid out sideways so nothing stacks into a long form.
 * Row one: Style, Look, Size, then one chip per icon, then Add. Row two: the options of whatever is selected.
 * Colors are circles, never typed codes. Used by the Brand screen.
 */
@Composable
fun FooterEditor(brand: PostBrand, onBrand: (PostBrand) -> Unit) {
    var focus by rememberSaveable { mutableStateOf("style") }

    fun update(index: Int, change: (FooterItem) -> FooterItem) {
        onBrand(brand.copy(footerItems = brand.footerItems.mapIndexed { i, old -> if (i == index) change(old) else old }))
    }

    val itemIndex = if (focus.startsWith("item:")) focus.removePrefix("item:").toIntOrNull() else null
    if (itemIndex != null && itemIndex !in brand.footerItems.indices) focus = "style"

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(selected = focus == "style", onClick = { focus = "style" }, label = { Text("Style") })
            FilterChip(selected = focus == "look", onClick = { focus = "look" }, label = { Text("Look") })
            FilterChip(selected = focus == "size", onClick = { focus = "size" }, label = { Text("Size") })
            brand.footerItems.forEachIndexed { index, item ->
                FilterChip(
                    selected = focus == "item:" + index,
                    onClick = { focus = "item:" + index },
                    label = { Text(PostIcons.title(item.kind)) }
                )
            }
            if (brand.footerItems.size < MAX_ICONS) {
                FilterChip(
                    selected = false,
                    onClick = {
                        onBrand(brand.copy(footerItems = brand.footerItems + FooterItem("telegram", "@")))
                        focus = "item:" + brand.footerItems.size
                    },
                    label = { Text("Add") },
                    leadingIcon = { Icon(Icons.Outlined.Add, contentDescription = null) }
                )
            }
        }

        when {
            focus == "style" -> Row(
                Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = brand.footerMinimal,
                    onClick = { onBrand(brand.copy(footerMinimal = !brand.footerMinimal)) },
                    label = { Text("Minimal slim line") }
                )
                FooterStyle.values().forEach { style ->
                    FilterChip(
                        selected = !brand.footerMinimal && brand.footerStyle == style,
                        onClick = { onBrand(brand.copy(footerMinimal = false, footerStyle = style)) },
                        label = { Text(if (style == FooterStyle.PILL) "Pills" else "Line") }
                    )
                }
            }

            focus == "look" -> Row(
                Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconLook.values().forEach { look ->
                    FilterChip(
                        selected = brand.iconLook == look,
                        onClick = { onBrand(brand.copy(iconLook = look)) },
                        label = {
                            Text(
                                when (look) {
                                    IconLook.PLAIN -> "Plain"
                                    IconLook.BADGE -> "Badge"
                                    IconLook.OFFICIAL -> "Platform colors"
                                }
                            )
                        }
                    )
                }
            }

            focus == "size" -> Row(
                Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Small" to 0.85f, "Medium" to 1f, "Large" to 1.25f).forEach { (name, scale) ->
                    FilterChip(
                        selected = abs(brand.iconScale - scale) < 0.01f,
                        onClick = { onBrand(brand.copy(iconScale = scale)) },
                        label = { Text(name) }
                    )
                }
            }

            itemIndex != null && itemIndex in brand.footerItems.indices -> {
                val item = brand.footerItems[itemIndex]
                Row(
                    Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    PostIcons.kinds.forEach { kind ->
                        FilterChip(
                            selected = item.kind == kind,
                            onClick = { update(itemIndex) { it.copy(kind = kind) } },
                            label = { Text(PostIcons.title(kind)) }
                        )
                    }
                }
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = item.label,
                        onValueChange = { text -> update(itemIndex) { it.copy(label = text) } },
                        label = { Text("Handle or link") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = {
                        onBrand(brand.copy(footerItems = brand.footerItems.filterIndexed { i, _ -> i != itemIndex }))
                        focus = "style"
                    }) {
                        Icon(Icons.Outlined.Delete, contentDescription = "Remove")
                    }
                }
                Row(
                    Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PostSwatch(null, item.colorHex.isBlank()) { update(itemIndex) { it.copy(colorHex = "") } }
                    IconColors.forEach { hex ->
                        PostSwatch(hex, item.colorHex.equals(hex, ignoreCase = true)) { update(itemIndex) { it.copy(colorHex = hex) } }
                    }
                }
            }
        }
    }
}
