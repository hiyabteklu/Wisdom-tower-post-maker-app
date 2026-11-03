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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.Palette
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
 * The five main places of the app, always one tap away in the bottom bar.
 * Inbox: batches your AI chat saved. Templates: the Library. Editor: the open post. Batch: make many posts from a pasted list.
 * Brand: logo, colors, footer and the chat link.
 */
enum class PostTab(val label: String, val icon: ImageVector, val iconOn: ImageVector) {
    INBOX("Inbox", Icons.Outlined.Inbox, Icons.Filled.Inbox),
    LIBRARY("Templates", Icons.Outlined.GridView, Icons.Filled.GridView),
    EDITOR("Editor", Icons.Outlined.Edit, Icons.Filled.Edit),
    BATCH("Batch", Icons.Outlined.Layers, Icons.Filled.Layers),
    BRAND("Brand", Icons.Outlined.Palette, Icons.Filled.Palette)
}

/**
 * What the Inbox knows, kept in memory so the screen opens at once and the bar can show a dot when drafts are waiting.
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

@Composable
fun PostBottomBar(selected: PostTab, onSelect: (PostTab) -> Unit, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth().background(PostPalette.Surface)) {
        Box(Modifier.fillMaxWidth().height(1.dp).background(PostPalette.Line))
        Row(Modifier.fillMaxWidth().navigationBarsPadding().height(62.dp)) {
            PostTab.values().forEach { tab ->
                val on = tab == selected
                val tint = if (on) PostPalette.Mint else PostPalette.Muted
                Column(
                    Modifier.weight(1f).height(62.dp).clickable(role = Role.Tab) { onSelect(tab) },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        Modifier.width(60.dp).height(30.dp).clip(RoundedCornerShape(15.dp))
                            .background(if (on) PostPalette.Mint.copy(alpha = 0.16f) else Color.Transparent),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(if (on) tab.iconOn else tab.icon, contentDescription = null, tint = tint, modifier = Modifier.size(22.dp))
                        if (tab == PostTab.INBOX && PostInboxState.drafts > 0) {
                            Box(
                                Modifier.align(Alignment.TopEnd).padding(top = 3.dp, end = 11.dp).size(9.dp)
                                    .clip(CircleShape).background(PostPalette.Gold)
                            )
                        }
                    }
                    Text(
                        tab.label,
                        fontSize = 11.sp,
                        fontWeight = if (on) FontWeight.Bold else FontWeight.Medium,
                        color = tint,
                        maxLines = 1,
                        overflow = TextOverflow.Clip,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }
    }
}
