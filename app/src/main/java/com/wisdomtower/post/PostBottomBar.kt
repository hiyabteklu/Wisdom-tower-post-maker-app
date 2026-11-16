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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesomeMosaic
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.outlined.AutoAwesomeMosaic
import androidx.compose.material.icons.outlined.Brush
import androidx.compose.material.icons.outlined.FormatSize
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material.icons.outlined.Landscape
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Settings
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
 * Top-level 56dp bottom navigation tabs: Studio, Library, Batch/Inbox, Brand, Settings.
 */
enum class AppNavTab(val route: String, val label: String, val icon: ImageVector, val iconOn: ImageVector) {
    STUDIO("studio", "Studio", Icons.Outlined.AutoAwesomeMosaic, Icons.Filled.AutoAwesomeMosaic),
    LIBRARY("library", "Library", Icons.Outlined.GridView, Icons.Filled.GridView),
    INBOX("inbox", "Batch/Inbox", Icons.Outlined.Inbox, Icons.Filled.Inbox),
    BRAND("brand", "Brand", Icons.Outlined.Brush, Icons.Filled.Brush),
    SETTINGS("settings", "Settings", Icons.Outlined.Settings, Icons.Filled.Settings)
}

/**
 * What the Inbox knows, kept in memory so the Inbox opens at once and the editor can show a dot when something is waiting.
 * The list is also cached on disk by PostChatStore, so it survives a restart.
 *
 * `drafts` is the number that decides the dot on the editor's Inbox icon (PostStudio): batches WAITING for the owner, that is
 * drafts still to review PLUS approved (planned) batches that are not marked posted yet (docs/URGENT_PLAN.md item 2). The name is kept
 * so PostStudio.kt did not have to change. `approved` is the approved part alone; the Inbox shows it as a reminder card.
 */
object PostInboxState {
    var drafts by mutableStateOf(0)
    var approved by mutableStateOf(0)
    var batches: List<PostChatBatch> = emptyList()
    var loadedAt: Long = 0L

    /** Sets the counts only (for example from the list cached on disk at start-up). Does not mark the list as freshly loaded. */
    fun countsFrom(list: List<PostChatBatch>) {
        approved = list.count { it.status == "approved" }
        drafts = list.count { it.status == "draft" } + approved
    }

    fun update(list: List<PostChatBatch>) {
        batches = list
        loadedAt = System.currentTimeMillis()
        countsFrom(list)
    }
}

/**
 * Persistent 56dp bottom navigation bar (Studio, Library, Batch/Inbox, Brand, Settings).
 * Respects navigationBarsPadding after the background so the bar fills behind the system gesture bar.
 */
@Composable
fun PostAppBottomNav(
    currentScreen: String,
    inboxBadgeCount: Int,
    onSelect: (AppNavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val activeTab = when (currentScreen) {
        "library" -> AppNavTab.LIBRARY
        "inbox", "batch" -> AppNavTab.INBOX
        "brand" -> AppNavTab.BRAND
        "settings", "chat", "qa" -> AppNavTab.SETTINGS
        else -> AppNavTab.STUDIO
    }
    Column(
        modifier
            .fillMaxWidth()
            .background(PostPalette.Surface)
            .navigationBarsPadding()
    ) {
        Box(Modifier.fillMaxWidth().height(1.dp).background(PostPalette.Line))
        Row(
            Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppNavTab.values().forEach { tab ->
                val on = tab == activeTab
                val tint = if (on) PostPalette.Mint else PostPalette.Muted
                Column(
                    Modifier
                        .weight(1f)
                        .height(56.dp)
                        .clickable(role = Role.Tab) { onSelect(tab) },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        Modifier
                            .width(44.dp)
                            .height(24.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (on) PostPalette.Mint.copy(alpha = 0.16f) else Color.Transparent),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (on) tab.iconOn else tab.icon,
                            contentDescription = tab.label,
                            tint = tint,
                            modifier = Modifier.size(18.dp)
                        )
                        if (tab == AppNavTab.INBOX && inboxBadgeCount > 0) {
                            Box(
                                Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(top = 2.dp, end = 6.dp)
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(PostPalette.Mint)
                            )
                        }
                    }
                    Text(
                        text = tab.label,
                        fontSize = 10.sp,
                        lineHeight = 11.sp,
                        fontWeight = if (on) FontWeight.Bold else FontWeight.Medium,
                        color = tint,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }
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
