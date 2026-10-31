package com.wisdomtower.post

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlin.math.abs

private const val MAX_ICONS = 4

/**
 * Everything about the social icons in one place: footer style, icon look, icon size, and per icon the platform,
 * the handle or link, and an own color. Used by the Brand screen and by the studio, so there is one set of icons.
 */
@Composable
fun FooterEditor(brand: PostBrand, onBrand: (PostBrand) -> Unit) {
    fun update(index: Int, change: (FooterItem) -> FooterItem) {
        onBrand(brand.copy(footerItems = brand.footerItems.mapIndexed { i, old -> if (i == index) change(old) else old }))
    }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        PostSmallLabel("Footer style")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FooterStyle.values().forEach { style ->
                FilterChip(
                    selected = brand.footerStyle == style,
                    onClick = { onBrand(brand.copy(footerStyle = style)) },
                    label = { Text(if (style == FooterStyle.PILL) "Pills" else "Line") }
                )
            }
        }

        PostSmallLabel("Icon look")
        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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

        PostSmallLabel("Icon size")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Small" to 0.85f, "Medium" to 1f, "Large" to 1.25f).forEach { (name, scale) ->
                FilterChip(
                    selected = abs(brand.iconScale - scale) < 0.01f,
                    onClick = { onBrand(brand.copy(iconScale = scale)) },
                    label = { Text(name) }
                )
            }
        }

        brand.footerItems.forEachIndexed { index, item ->
            Card(colors = CardDefaults.cardColors(containerColor = PostPalette.Ink), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        PostIcons.kinds.forEach { kind ->
                            FilterChip(
                                selected = item.kind == kind,
                                onClick = { update(index) { it.copy(kind = kind) } },
                                label = { Text(PostIcons.title(kind)) }
                            )
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = item.label,
                            onValueChange = { text -> update(index) { it.copy(label = text) } },
                            label = { Text("Handle or link") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { onBrand(brand.copy(footerItems = brand.footerItems.filterIndexed { i, _ -> i != index })) }) {
                            Icon(Icons.Outlined.Delete, contentDescription = "Remove")
                        }
                    }
                    PostColorPicker(
                        label = "Icon color",
                        value = item.colorHex.takeIf { it.isNotBlank() },
                        brand = brand,
                        onPick = { hex -> update(index) { it.copy(colorHex = hex.orEmpty()) } }
                    )
                }
            }
        }

        if (brand.footerItems.size < MAX_ICONS) {
            OutlinedButton(
                onClick = { onBrand(brand.copy(footerItems = brand.footerItems + FooterItem("telegram", "@"))) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Outlined.Add, contentDescription = null)
                Text("Add social icon")
            }
        }
        PostSmallLabel("Up to " + MAX_ICONS + " icons. Short handles look best. Social templates show them as big cards.")
    }
}
