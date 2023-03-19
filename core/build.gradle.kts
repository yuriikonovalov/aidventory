plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
}

android {
    val compileSdkVersion: Int by rootProject.extra
    val minSdkVersion: Int by rootProject.extra

    compileSdk = compileSdkVersion
    namespace = "com.aidventory.core"
    defaultConfig {
        minSdk = minSdkVersion
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf(
                    "room.schemaLocation" to "$projectDir/schemas",
                    "room.incremental" to "true"
                )
            }
        }
    }

    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
}

dependencies {
    implementation(libs.androidx.core.coreKtx)
    implementation(libs.androidx.activity.activityCompose)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.uiToolingPreview)
    implementation(libs.google.accompanist.accompanistAdaptive)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.windowSizeClass)
    implementation(libs.dagger.hiltAndroid)
    kapt(libs.dagger.hiltAndroidCompiler)
    implementation(libs.androidx.room.roomRuntime)
    implementation(libs.androidx.room.roomKtx)
    kapt(libs.androidx.room.roomCompiler)
    implementation(libs.androidx.datastore.datastorePreferences)
    implementation(libs.androidx.camera.cameraCamera2)
    implementation(libs.androidx.camera.cameraLifecycle)
    implementation(libs.androidx.camera.cameraView)
    implementation(libs.androidx.camera.cameraMlkitVision)
    implementation(libs.androidx.camera.cameraExtensions)
    implementation(libs.google.accompanist.accompanistPermissions)
    implementation(libs.google.mlkit.barcodeScanning)
    implementation(libs.google.zxing)
    implementation(libs.squareup.moshi.moshi)
    implementation(libs.squareup.moshi.moshiKotlin)
    kapt(libs.squareup.moshi.moshiKotlinCodegen)
    implementation(libs.androidx.work.workRuntimeKtx)
    implementation(libs.androidx.hilt.hiltWork)
    kapt(libs.androidx.hilt.hiltCompiler)

    implementation(libs.google.accompanist.accompanistNavigationAnimation)
    implementation(libs.androidx.hilt.hiltNavigationCompose)

    testImplementation(testLibs.androidx.test.ext.junit)
    testImplementation(testLibs.google.truth)
    testImplementation(testLibs.androidx.test.core)
    testImplementation(testLibs.mockito.kotlin.mockitoKotlin)
    androidTestImplementation(testLibs.androidx.test.core)
    androidTestImplementation(testLibs.google.truth)
    androidTestImplementation(testLibs.kotlinx.kotlinxCoroutinesTest)
    androidTestImplementation(testLibs.androidx.test.ext.junit)
    androidTestImplementation(testLibs.androidx.test.runner)
}