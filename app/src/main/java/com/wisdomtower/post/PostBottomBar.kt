package com.wisdomtower.post

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.outlined.FormatSize
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Landscape
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.TextFields
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * The editor's tool bar (bottom of the Editor screen). Each button shows one panel of controls under the template strip:
 * Text: the message boxes and the overall text size. Color: color pairs, accent, default text color.
 * Font: the post fonts. Photo: the template photo (add, zoom, move up, down, left, right).
 * Background: background type, solid or gradient colors, and the full-canvas background image.
 * More: layers, layer manager, logo, icons and footer.
 */
enum class EditorTool(val label: String, val icon: ImageVector, val iconOn: ImageVector) {
    TEXT("Text", Icons.Outlined.TextFields, Icons.Filled.TextFields),
    COLOR("Color", Icons.Outlined.Palette, Icons.Filled.Palette),
    FONT("Font", Icons.Outlined.FormatSize, Icons.Filled.FormatSize),
    PHOTO("Photo", Icons.Outlined.Image, Icons.Filled.Image),
    BACKGROUND("Background", Icons.Outlined.Landscape, Icons.Filled.Landscape),
    MORE("More", Icons.Outlined.MoreHoriz, Icons.Filled.MoreHoriz)
}

/**
 * What the Inbox knows, kept in memory so the Inbox opens at once and the editor can show a dot when drafts are waiting.
 * The list is also cached on disk by PostChatStore, so it survives a restart.
 */
object PostInboxState {
    var drafts by mutableStateOf(0)
    var batches: List<PostChatBatch> = emptyList()
    var loadedAt: Long = 0L

    fun update(list: List<PostChatBatch>) {
        batches = list
        loadedAt = System.currentTimeMillis()
        drafts = list.count { it.status == "draft" }
    }
}

/**
 * The bar always keeps itself clear of the system gesture / navigation bar (navigationBarsPadding AFTER the background,
 * so the bar color also fills the area behind the gesture line). Callers must not add that padding themselves.
 */
@Composable
fun PostToolBar(selected: EditorTool, onSelect: (EditorTool) -> Unit, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth().background(PostPalette.Surface).navigationBarsPadding()) {
        Box(Modifier.fillMaxWidth().height(1.dp).background(PostPalette.Line))
        Row(Modifier.fillMaxWidth().height(46.dp)) {
            EditorTool.values().forEach { tool ->
                val on = tool == selected
                val tint = if (on) PostPalette.Mint else PostPalette.Muted
                Column(
                    Modifier.weight(1f).height(46.dp).clickable(role = Role.Tab) { onSelect(tool) },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        Modifier.width(48.dp).height(24.dp).clip(RoundedCornerShape(14.dp))
                            .background(if (on) PostPalette.Mint.copy(alpha = 0.16f) else Color.Transparent),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(if (on) tool.iconOn else tool.icon, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
                    }
                    Text(
                        tool.label,
                        fontSize = 10.5.sp,
                        fontWeight = if (on) FontWeight.Bold else FontWeight.Medium,
                        color = tint,
                        maxLines = 1,
                        overflow = TextOverflow.Clip,
                        modifier = Modifier.padding(top = 0.dp)
                    )
                }
            }
        }
    }
}
