package com.wisdomtower.post

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostChatScreen(onBack: () -> Unit, onOpenInbox: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var endpoint by remember { mutableStateOf(PostChatStore.loadUrl(context)) }
    var secret by remember { mutableStateOf(PostChatStore.loadKey(context)) }
    var busy by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("The chat link is optional. The editor and batch tools remain offline-capable.") }
    var success by remember { mutableStateOf(false) }

    PostTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Chat link", fontWeight = FontWeight.Bold) },
                    navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Outlined.ArrowBack, contentDescription = "Back") } }
                )
            },
            containerColor = PostPalette.Ink
        ) { padding ->
            Column(
                Modifier.fillMaxSize().padding(padding).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text("Connect Wisdom Tower Post to the private wt_post backend. Only poster text and captions are transferred.", color = PostPalette.Muted)
                OutlinedTextField(
                    value = endpoint,
                    onValueChange = { endpoint = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Backend URL") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = secret,
                    onValueChange = { secret = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Secret key") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation()
                )
                Button(
                    onClick = {
                        busy = true
                        message = "Testing connection…"
                        success = false
                        scope.launch {
                            try {
                                message = PostChatApi.testConnection(context, endpoint, secret)
                                success = true
                            } catch (e: Exception) {
                                message = e.message?.take(180) ?: "Connection failed"
                            } finally {
                                busy = false
                            }
                        }
                    },
                    enabled = !busy && endpoint.isNotBlank() && secret.isNotBlank(),
                    modifier = Modifier.fillMaxWidth()
                ) { Text(if (busy) "Testing…" else "Test connection") }
                Text(message, color = if (success) PostPalette.Mint else PostPalette.Text, fontWeight = FontWeight.SemiBold)
                TextButton(onClick = { endpoint = PostChatStore.DEFAULT_URL }) { Text("Restore default URL") }\n                Button(onClick = onOpenInbox, enabled = success || PostChatStore.loadKey(context).length >= 24, modifier = Modifier.fillMaxWidth()) { Text("Open poster inbox") }
            }
        }
    }
}
