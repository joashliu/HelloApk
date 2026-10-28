import org.gradle.api.GradleException // 🟢 新增：導入 GradleException

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
        versionCode = 151
        versionName = "62.0"

        vectorDrawables {
            useSupportLibrary = true
        }

        // 🔥 終極絕招：直接由 Gradle 喺編譯時注入 string 資源！
        // 咁樣就算 GitHub 搵唔到 strings.xml，系統都會自動生成呢串字，永不報錯！
        resValue("string", "accessibility_description", "此服務僅用於偵測支付 App 的通知，作為畫面擷取及記帳功能的觸發信號。絕不會主動讀取您的私人畫面或對話內容，並且不設任何後台持續擷取。")
    }

    // ★★★ 由舊版加返：release keystore 設定 ★★★
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
        // ★★★ 由舊版加返：debug 都用 release keystore ★★★
        debug {
            signingConfig = signingConfigs.getByName("release")
        }
        release {
            // 🟢 優化：啟用 R8 壓縮與混淆，消除 JIT 編譯熱身帶來嘅卡頓
            isMinifyEnabled = true
            // 🟢 優化：縮減 APK 大小
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
        // ★ 如果你用 Kotlin 1.9.x → 保持下面呢行
        // ★ 如果你升到 Kotlin 2.0+ → 刪咗下面呢行，改用 compose plugin
        kotlinCompilerExtensionVersion = "1.5.14"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    // ============ Core ============
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    
    // 🟢 新增：官方 Splash Screen API，負責極速渲染開屏畫面
    implementation("androidx.core:core-splashscreen:1.0.1")

    // ============ Lifecycle (ViewModel + Compose 整合) ============
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")

    // ============ Compose BOM ============
    val composeBom = platform("androidx.compose:compose-bom:2024.09.03")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    // Compose UI
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

    // 圖像分割 (原有)
    implementation("com.google.android.gms:play-services-mlkit-subject-segmentation:16.0.0-beta1")

    // 🟢 新增 ============ ML Kit (OCR) & Vision ============
    // 用於離線識別畫面中嘅文字同金額
    implementation("com.google.mlkit:text-recognition:16.0.0")
    implementation("com.google.android.gms:play-services-vision:20.1.3")

    // ============ 測試 ============
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
