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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs

private const val MAX_ICONS = 4

/** Compact footer editor: choose one item, edit it, then move to the next. */
@Composable
fun FooterEditor(brand: PostBrand, onBrand: (PostBrand) -> Unit) {
    var selectedIndex by rememberSaveable { mutableStateOf(0) }
    val items = brand.footerItems
    val index = selectedIndex.coerceIn(0, (items.size - 1).coerceAtLeast(0))
    val selected = items.getOrNull(index)

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        PostSmallLabel("Footer style")
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            FooterStyle.values().forEach { style ->
                FilterChip(selected = brand.footerStyle == style, onClick = { onBrand(brand.copy(footerStyle = style)) }, label = { Text(if (style == FooterStyle.PILL) "Pills" else "Line") })
            }
            IconLook.values().forEach { look ->
                FilterChip(
                    selected = brand.iconLook == look,
                    onClick = { onBrand(brand.copy(iconLook = look)) },
                    label = { Text(when (look) { IconLook.PLAIN -> "Plain"; IconLook.BADGE -> "Badge"; IconLook.OFFICIAL -> "Official" }) }
                )
            }
        }
        PostSmallLabel("Footer items")
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items.forEachIndexed { i, item ->
                FilterChip(
                    selected = i == index,
                    onClick = { selectedIndex = i },
                    label = { Text(PostIcons.title(item.kind), maxLines = 1) }
                )
            }
            if (items.size < MAX_ICONS) {
                FilterChip(
                    selected = false,
                    onClick = {
                        onBrand(brand.copy(footerItems = brand.footerItems + FooterItem("telegram", "@")))
                        selectedIndex = brand.footerItems.size
                    },
                    label = { Text("+ Add") }
                )
            }
        }
        if (selected != null) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(Modifier.weight(1f).horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    PostIcons.kinds.forEach { kind ->
                        FilterChip(
                            selected = selected.kind == kind,
                            onClick = { onBrand(brand.copy(footerItems = brand.footerItems.mapIndexed { i, item -> if (i == index) item.copy(kind = kind) else item })) },
                            label = { Text(PostIcons.title(kind)) }
                        )
                    }
                }
                IconButton(onClick = {
                    onBrand(brand.copy(footerItems = brand.footerItems.filterIndexed { i, _ -> i != index }))
                    selectedIndex = (index - 1).coerceAtLeast(0)
                }) { Icon(Icons.Outlined.Delete, contentDescription = "Remove footer item") }
            }
            OutlinedTextField(
                value = selected.label,
                onValueChange = { label ->
                    onBrand(brand.copy(footerItems = brand.footerItems.mapIndexed { i, item -> if (i == index) item.copy(label = label) else item }))
                },
                label = { Text("Handle or link") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            PostColorPicker(
                label = "Icon color",
                value = selected.colorHex.takeIf { it.isNotBlank() },
                brand = brand,
                onPick = { hex ->
                    onBrand(brand.copy(footerItems = brand.footerItems.mapIndexed { i, item -> if (i == index) item.copy(colorHex = hex.orEmpty()) else item }))
                }
            )
        } else {
            Text("Add a footer item to show your social links on every post.", color = PostPalette.Muted, fontSize = 11.sp)
        }
        PostSmallLabel("Icon size")
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf("Small" to 0.85f, "Medium" to 1f, "Large" to 1.25f).forEach { (name, scale) ->
                FilterChip(selected = abs(brand.iconScale - scale) < 0.01f, onClick = { onBrand(brand.copy(iconScale = scale)) }, label = { Text(name) })
            }
        }
    }
}
