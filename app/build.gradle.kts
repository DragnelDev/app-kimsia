import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.example.kimsia"
    compileSdk {
        version = release(37) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.example.kimsia"
        minSdk = 26 // supabase-kt 3.x exige API 26+
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        // Credenciales de Supabase desde local.properties (no se suben a git). Si faltan, la app
        // funciona offline con los datos de ejemplo.
        val localProps = Properties().apply {
            val file = rootProject.file("local.properties")
            if (file.exists()) file.inputStream().use { load(it) }
        }
        buildConfigField("String", "SUPABASE_URL", "\"${localProps.getProperty("supabase.url", "")}\"")
        buildConfigField("String", "SUPABASE_ANON_KEY", "\"${localProps.getProperty("supabase.anonKey", "")}\"")
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    debugImplementation(libs.androidx.compose.ui.tooling)

    // Lifecycle + Navigation
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)

    // Coroutines + Serialization (DTOs de Supabase)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)

    // Supabase (Auth + PostgREST + Storage) y motor HTTP de Ktor
    implementation(platform(libs.supabase.bom))
    implementation(libs.supabase.auth)
    implementation(libs.supabase.postgrest)
    implementation(libs.supabase.storage)
    implementation(libs.ktor.client.android)

    // Audio (Media3 ExoPlayer) e imágenes (Coil)
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    // Room (caché offline)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    testImplementation(libs.junit)
}
