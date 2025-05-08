import java.util.Properties
import java.io.FileInputStream

val apiProperties = Properties().apply {
    load(FileInputStream(rootProject.file("androidApp/api.properties")))
}

val webClientId = apiProperties.getProperty("webClientID")

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.compose.compiler)
    id("com.google.gms.google-services")
}
android {
    namespace = "com.example.nanutakehome.android"
    compileSdk = 35
    defaultConfig {
        applicationId = "com.example.nanutakehome.android"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        buildConfigField("String", "WEB_CLIENT_ID", "\"${webClientId}\"")
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    lint {
        disable += "MutableCollectionMutableState"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

}

dependencies {
    implementation(projects.shared)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.androidx.activity.compose)
    debugImplementation(libs.compose.ui.tooling)
    implementation("com.google.firebase:firebase-auth-ktx:23.2.0")
    implementation("com.google.firebase:firebase-firestore-ktx:25.1.4")
    implementation("com.google.android.gms:play-services-auth:21.3.0")
    implementation(platform("com.google.firebase:firebase-bom:33.13.0"))
    implementation("com.google.android.gms:play-services-auth:21.0.0")

}