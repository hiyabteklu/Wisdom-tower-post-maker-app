package com.wisdomtower.post

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.delay

/** Owns the post and the brand, saves them, and switches between the studio, the brand screen and the old studio. */
@Composable
fun PostApp() {
    val context = LocalContext.current
    var doc by remember { mutableStateOf(PostStore.loadDoc(context)) }
    var brand by remember { mutableStateOf(PostStore.loadBrand(context)) }
    var screen by rememberSaveable { mutableStateOf("studio") }

    LaunchedEffect(doc, brand) {
        delay(800)
        PostStore.save(context, doc, brand)
    }

    val logoPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            val path = PostStore.importLogo(context, uri)
            if (path != null) brand = brand.copy(logoPath = path, logoStamp = System.currentTimeMillis())
            else Toast.makeText(context, "That file is not a readable image", Toast.LENGTH_LONG).show()
        }
    }
    val backgroundPhotoPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            val stored = PostStore.importPhoto(context, uri)
            if (stored != null) doc = doc.copy(backgroundPhotoUri = stored, backgroundPhotoOpacity = 1f)
            else Toast.makeText(context, "That file is not a readable image", Toast.LENGTH_LONG).show()
        }
    }

    val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            val stored = PostStore.importPhoto(context, uri)
            if (stored != null) doc = doc.copy(photoUri = stored, photoZoom = 1f, photoFocusX = 0.5f, photoFocusY = 0.5f)
            else Toast.makeText(context, "That file is not a readable image", Toast.LENGTH_LONG).show()
        }
    }

    when (screen) {
        "brand" -> {
            BackHandler { screen = "studio" }
            PostBrandScreen(
                brand = brand,
                onBrand = { brand = it },
                onPickLogo = { logoPicker.launch(arrayOf("image/*")) },
                onBack = { screen = "studio" }
            )
        }
        "old" -> {
            BackHandler { screen = "studio" }
            DesignStudioApp()
        }
        else -> PostStudioScreen(
            doc = doc,
            onDoc = { doc = it },
            brand = brand,
            onBrand = { brand = it },
            onPickPhoto = { photoPicker.launch(arrayOf("image/*")) },
            onPickBackgroundPhoto = { backgroundPhotoPicker.launch(arrayOf("image/*")) },
            onPickLogo = { logoPicker.launch(arrayOf("image/*")) },
            onOpenBrand = { screen = "brand" },
            onOpenOldStudio = { screen = "old" }
        )
    }
}
