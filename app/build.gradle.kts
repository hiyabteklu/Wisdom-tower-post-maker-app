import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

/**
 * Copies new_logo.png from the repo root into the app's resources as @drawable/launcher_logo, so the launcher icon is
 * exactly that file. Keep new_logo.png in the repo root (to change the icon, replace that file).
 */
abstract class PrepareLauncherLogo : DefaultTask() {
    @get:InputFile
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val logo: RegularFileProperty

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun copyLogo() {
        val target = outputDir.get().asFile.resolve("drawable-nodpi")
        target.mkdirs()
        logo.get().asFile.copyTo(target.resolve("launcher_logo.png"), overwrite = true)
    }
}

// Permanent signing key (see signing/README.md). It is stored as text in the repo; turn it back into a keystore file.
val wtpKeystoreFile = rootProject.file("signing/wtp.keystore")
val wtpKeystoreText = rootProject.file("signing/wtp.keystore.b64")
if (!wtpKeystoreFile.exists() && wtpKeystoreText.exists()) {
    wtpKeystoreFile.writeBytes(java.util.Base64.getDecoder().decode(wtpKeystoreText.readText().trim()))
}

android {
    namespace = "com.wisdomtower.post"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.wisdomtower.post"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0"
        // Only keep English and Amharic strings from the libraries (they ship dozens of languages).
        resourceConfigurations += listOf("en", "am")
    }

    // Same key for every build, so every new APK installs over the old one.
    signingConfigs {
        create("wtp") {
            storeFile = wtpKeystoreFile
            storePassword = "sr7O64XCj2DfqsIXpXtp"
            keyAlias = "wtpost"
            keyPassword = "sr7O64XCj2DfqsIXpXtp"
        }
    }

    buildTypes {
        release {
            // R8 removes the code and the icons the app never uses.
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("wtp")
        }
    }

    buildFeatures { compose = true }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}

androidComponents {
    onVariants { variant ->
        val cap = variant.name.replaceFirstChar { it.uppercase() }
        val prepare = tasks.register<PrepareLauncherLogo>("prepare${cap}LauncherLogo") {
            logo.set(rootProject.layout.projectDirectory.file("new_logo.png"))
        }
        variant.sources.res?.addGeneratedSourceDirectory(prepare, PrepareLauncherLogo::outputDir)
    }
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2024.12.01"))
    implementation("androidx.activity:activity-compose:1.10.0")
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    debugImplementation("androidx.compose.ui:ui-tooling")
}
