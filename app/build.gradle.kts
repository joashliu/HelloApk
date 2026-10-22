import org.gradle.api.GradleException

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    // 如果你用 Kotlin 2.0+ 嘅 Compose Compiler Plugin，加下面呢行：
    // id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.gms.google-services")     // Firebase
}

android {
    namespace = "com.example.hello"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.hello"
        minSdk = 24
        targetSdk = 34
        versionCode = 499
        versionName = "51.4"

        vectorDrawables {
            useSupportLibrary = true
        }
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
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
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

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    // 🟢 新增：防止 shrinkResources 誤刪 ML Kit 模型檔（bundled OCR 模型）
    androidResources {
        noCompress += listOf("tflite", "pb", "lite", "bin", "dat")
    }
}

dependencies {

    // ============ Core ============
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.core:core-splashscreen:1.0.1")

    // ============ Lifecycle (ViewModel + Compose 整合) ============
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")

    // ============ Compose BOM ============
    val composeBom = platform("androidx.compose:compose-bom:2024.09.03")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.foundation:foundation")

    // Material 3
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")

    // Activity Compose
    implementation("androidx.activity:activity-compose:1.9.3")

    // ============ Firebase ============
    implementation(platform("com.google.firebase:firebase-bom:33.5.1"))
    implementation("com.google.firebase:firebase-firestore-ktx")

    // ============ Coil (圖片載入) ============
    implementation("io.coil-kt:coil-compose:2.7.0")

    // ============ Coroutines ============
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.9.0")

    implementation("com.google.android.gms:play-services-mlkit-subject-segmentation:16.0.0-beta1")

    // 🟢 新增：ML Kit 中文文字識別（bundled 模型，離線可用）
    //    包含中文 + 拉丁字符，支援正則提取 HKD 金額
    //    APK 會大咗約 20MB，但唔使首次聯網下載
    implementation("com.google.mlkit:text-recognition-chinese:16.0.1")

    // ============ 測試 ============
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
