plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
}

android {
    val compileSdkVersion: Int by rootProject.extra
    val minSdkVersion: Int by rootProject.extra

    compileSdk = compileSdkVersion
    namespace = "com.aidventory.feature.home"
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
    implementation(project(":feature:containers"))
    implementation(project(":feature:supplies"))
    implementation(project(":feature:settings"))
    implementation(libs.androidx.core.coreKtx)
    implementation(libs.androidx.lifecycle.lifecycleRuntimeKtx)
    implementation(libs.androidx.lifecycle.lifecycleRuntimeCompose)
    implementation(libs.androidx.compose.ui.uiToolingPreview)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.windowSizeClass)
    implementation(libs.androidx.navigation.navigationCompose)
    implementation(libs.google.accompanist.accompanistAdaptive)
    implementation(libs.google.accompanist.accompanistNavigationAnimation)
    implementation(libs.dagger.hiltAndroid)
    kapt(libs.dagger.hiltAndroidCompiler)
    implementation(libs.androidx.hilt.hiltNavigationCompose)

    debugImplementation(libs.androidx.compose.ui.uiTooling)
    androidTestImplementation(testLibs.androidx.compose.ui.uiTestJunit4)
    debugImplementation(testLibs.androidx.compose.ui.uiTestManifest)
}