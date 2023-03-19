plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    val compileSdkVersion: Int by rootProject.extra
    val minSdkVersion: Int by rootProject.extra
    val targetSdkVersion: Int by rootProject.extra
    namespace = "com.aidventory"
    compileSdk = compileSdkVersion

    defaultConfig {
        applicationId = "com.aidventory"
        minSdk = minSdkVersion
        targetSdk = targetSdkVersion
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        resourceConfigurations.addAll(listOf("en", "uk"))
    }

    buildTypes {
        release {
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packagingOptions {
        resources {
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
        }
    }
}

dependencies {
    implementation(project(":core"))
    implementation(project(":feature:home"))
    implementation(project(":feature:scanner"))
    implementation(project(":feature:expired"))
    implementation(project(":feature:settings"))
    implementation(project(":feature:containers"))
    implementation(project(":feature:supplies"))

    implementation(platform("com.google.firebase:firebase-bom:32.0.0"))
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-crashlytics-ktx")


    implementation(libs.androidx.core.coreKtx)
    implementation(libs.androidx.core.coreSplashscreen)
    implementation(libs.androidx.lifecycle.lifecycleRuntimeKtx)
    implementation(libs.androidx.activity.activityCompose)
    implementation(libs.google.accompanist.accompanistSystemUiController)
    implementation(libs.google.accompanist.accompanistAdaptive)
    implementation(libs.google.accompanist.accompanistPermissions)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.uiToolingPreview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.windowSizeClass)
    implementation(libs.dagger.hiltAndroid)
    implementation(libs.androidx.hilt.hiltNavigationCompose)
    kapt(libs.dagger.hiltAndroidCompiler)
    implementation(libs.androidx.navigation.navigationCompose)
    implementation(libs.androidx.work.workRuntimeKtx)
    implementation(libs.androidx.hilt.hiltWork)
    kapt(libs.androidx.hilt.hiltCompiler)
    implementation(libs.google.accompanist.accompanistNavigationAnimation)


    testImplementation(testLibs.junit)
    androidTestImplementation(testLibs.androidx.test.espresso.espressoCore)
    androidTestImplementation(testLibs.androidx.compose.ui.uiTestJunit4)
    debugImplementation(libs.androidx.compose.ui.uiTooling)
    debugImplementation(libs.androidx.compose.ui.uiTestManifest)
}

// Allow references to generated code
kapt {
    correctErrorTypes = true
}
