import org.gradle.api.GradleException

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.hello"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.hello"
        minSdk = 24
        targetSdk = 34
        versionCode = 277
        versionName = "29.1"
    }

    signingConfigs {
        create("release") {
            val keystorePath = System.getenv("KEYSTORE_PATH")
            val ksFile = if (keystorePath.isNullOrBlank()) file("release.keystore")
                         else file(keystorePath)
            if (!ksFile.exists()) {
                throw GradleException(
                    "keystore 唔存在: ${ksFile.absolutePath}\n" +
                    "請確認 workflow 有 decode keystore,同 KEYSTORE_PATH 環境變數正確。"
                )
            }
            storeFile = ksFile
            storePassword = System.getenv("KEYSTORE_PASSWORD")
            keyAlias = System.getenv("KEY_ALIAS")
            keyPassword = System.getenv("KEY_PASSWORD")
        }
    }

    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("release")
        }
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2024.09.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.activity:activity-compose:1.9.0")

    implementation(platform("com.google.firebase:firebase-bom:33.1.0"))
    implementation("com.google.firebase:firebase-firestore-ktx")

    implementation("io.coil-kt:coil-compose:2.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.8.0")
}
