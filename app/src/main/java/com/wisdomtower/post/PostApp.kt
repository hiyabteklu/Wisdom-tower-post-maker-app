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

/** App navigation and offline state owner. */
@Composable
fun PostApp() {
    val context = LocalContext.current
    var doc by remember { mutableStateOf(PostStore.loadDoc(context)) }
    var brand by remember { mutableStateOf(PostStore.loadBrand(context)) }
    var projects by remember { mutableStateOf(PostProjects.load(context)) }
    var screen by rememberSaveable { mutableStateOf("home") }
    var brandReturn by rememberSaveable { mutableStateOf("home") }

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

    fun openBrand() {
        brandReturn = screen
        screen = "brand"
    }

    BackHandler(enabled = screen != "home") {
        screen = if (screen == "brand") brandReturn else "home"
    }

    when (screen) {
        "brand" -> PostBrandScreen(
            brand = brand,
            onBrand = { brand = it },
            onPickLogo = { logoPicker.launch(arrayOf("image/*")) },
            onBack = { screen = brandReturn }
        )
        "editor" -> PostStudioScreen(
            doc = doc,
            onDoc = { doc = it },
            brand = brand,
            onBrand = { brand = it },
            onPickPhoto = { photoPicker.launch(arrayOf("image/*")) },
            onPickBackgroundPhoto = { backgroundPhotoPicker.launch(arrayOf("image/*")) },
            onPickLogo = { logoPicker.launch(arrayOf("image/*")) },
            onOpenBrand = { openBrand() },
            onBackHome = { screen = "home" },
            onSaveProject = {
                projects = PostProjects.save(context, doc)
                Toast.makeText(context, "Saved to My posts", Toast.LENGTH_SHORT).show()
            }
        )
        else -> PostHomeScreen(
            brand = brand,
            projects = projects,
            onChooseTemplate = { spec, size ->
                doc = doc.withTemplate(spec).copy(size = size)
                screen = "editor"
            },
            onOpenProject = { project ->
                doc = project.document
                screen = "editor"
            },
            onDeleteProject = { id -> projects = PostProjects.delete(context, id) },
            onOpenBrand = { openBrand() }
        )
    }
}
