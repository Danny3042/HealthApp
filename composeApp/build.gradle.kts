plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.sqlDelight)
    alias(libs.plugins.googleServices)
    alias(libs.plugins.kotlinSerialization)
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin") version "2.0.0"

}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "11"
            }
        }
    }

    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    
    sourceSets {
        
        androidMain.dependencies {
            implementation(libs.compose.ui.tooling.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.sqldelight.androidDriver)
            implementation ("com.google.firebase:firebase-analytics-ktx:22.0.0")
            implementation ("androidx.health.connect:connect-client:1.1.0-alpha07")
            implementation("androidx.appcompat:appcompat:1.7.0")
            implementation("com.google.firebase:firebase-appcheck-playintegrity")
            implementation("com.google.android.play:integrity:1.3.0")
            implementation("com.google.accompanist:accompanist-drawablepainter:0.34.0")






        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            // Ktor
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            // SQLDelight
            implementation(libs.sqldelight.coroutines)
            implementation(libs.sqldelight.primitiveAdapters)
            // resources
            implementation(compose.components.resources)
            // Firebase
            implementation(libs.firebase.auth)
            implementation(libs.jetbrains.navigation.compose)
            // voyager TabNav
            implementation(libs.voyager.tabNavigator)
            // extended icons
            implementation("org.jetbrains.compose.material:material-icons-extended:1.6.2")
            implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0")
            // Health APIs
            implementation("com.vitoksmile.health-kmp:core:0.0.3")
            implementation("io.ktor:ktor-client-logging:2.3.10")
            implementation("com.google.ai.client.generativeai:generativeai:0.8.0")
            implementation("com.mikepenz:multiplatform-markdown-renderer:0.10.0")


        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
            // SQLDelight
            implementation(libs.sqldelight.nativeDriver)
            implementation("io.ktor:ktor-client-darwin:2.3.10")
        }
    }
}


android {
    namespace = "org.example.project"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = "org.danielramzani.HealthCompose"
        minSdk = 27
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    dependencies {
        debugImplementation(libs.compose.ui.tooling)
    }
}
dependencies {
    implementation(libs.firebase.common.ktx)
    implementation(libs.play.services.measurement.api)
}

