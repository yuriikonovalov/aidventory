plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
}

android {
    val compileSdkVersion: Int by rootProject.extra
    val minSdkVersion: Int by rootProject.extra

    compileSdk = compileSdkVersion
    namespace = "com.aidventory.feature.scanner"
    defaultConfig {
        minSdk = minSdkVersion
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
}

dependencies {
    implementation(project(":core"))
    implementation(project(":feature:supplies"))
    implementation(project(":feature:containers"))
    implementation(libs.androidx.core.coreKtx)
    implementation(libs.androidx.lifecycle.lifecycleRuntimeCompose)
    implementation(libs.androidx.compose.ui.uiToolingPreview)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.windowSizeClass)
    implementation(libs.androidx.navigation.navigationCompose)
    implementation(libs.dagger.hiltAndroid)
    kapt(libs.dagger.hiltAndroidCompiler)
    implementation(libs.androidx.hilt.hiltNavigationCompose)
    implementation(libs.google.mlkit.barcodeScanning)
    implementation(libs.google.accompanist.accompanistNavigationAnimation)

    debugImplementation(libs.androidx.compose.ui.uiTooling)
}