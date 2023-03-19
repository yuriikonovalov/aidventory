pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }


    versionCatalogs {
        val composeVersion = "1.4.3"

        create("libs") {
            version("compose", composeVersion)
            version("hilt", "2.44")
            version("room", "2.5.0")
            version("accompanist", "0.30.0")
            version("lifecycle", "2.6.1")
            version("sheets-compose-dialogs", "1.1.1")
            version("camerax", "1.3.0-alpha05")

            library("androidx.core.coreKtx", "androidx.core:core-ktx:1.9.0")
            library("androidx.lifecycle.lifecycleRuntimeKtx", "androidx.lifecycle","lifecycle-runtime-ktx").versionRef("lifecycle")
            library("androidx.lifecycle.lifecycleRuntimeCompose","androidx.lifecycle","lifecycle-runtime-compose").versionRef("lifecycle")
            library("androidx.activity.activityCompose", "androidx.activity:activity-compose:1.6.1")
            library("androidx.compose.ui", "androidx.compose.ui", "ui").versionRef("compose")
            library("androidx.compose.ui.uiToolingPreview", "androidx.compose.ui", "ui-tooling-preview").versionRef("compose")
            library("androidx.compose.material","androidx.compose.material:material:1.4.3")
            library("androidx.compose.material3", "androidx.compose.material3:material3:1.1.0")
            library("androidx.compose.material3.windowSizeClass","androidx.compose.material3:material3-window-size-class:1.1.0")
            library("androidx.compose.ui.uiTooling", "androidx.compose.ui", "ui-tooling").versionRef("compose")
            library("androidx.compose.ui.uiTestManifest", "androidx.compose.ui", "ui-test-manifest").versionRef("compose")
            library("dagger.hiltAndroid", "com.google.dagger", "hilt-android").versionRef("hilt")
            library("dagger.hiltAndroidCompiler", "com.google.dagger", "hilt-android-compiler").versionRef("hilt")
            library("androidx.hilt.hiltNavigationCompose", "androidx.hilt:hilt-navigation-compose:1.0.0")
            library("androidx.navigation.navigationCompose", "androidx.navigation:navigation-compose:2.5.3")
            library("androidx.room.roomRuntime", "androidx.room", "room-runtime").versionRef("room")
            // Kotlin Extensions and Coroutines support for Room
            library("androidx.room.roomKtx", "androidx.room", "room-ktx").versionRef("room")
            // Kotlin annotation processing tool (kapt)
            library("androidx.room.roomCompiler", "androidx.room", "room-compiler").versionRef("room")
            library("androidx.datastore.datastorePreferences","androidx.datastore:datastore-preferences:1.0.0")
            library("androidx.core.coreSplashscreen","androidx.core:core-splashscreen:1.0.0-beta02")
            library("google.accompanist.accompanistSystemUiController","com.google.accompanist","accompanist-systemuicontroller").versionRef("accompanist")
            library("google.accompanist.accompanistAdaptive","com.google.accompanist","accompanist-adaptive").versionRef("accompanist")
            library("google.accompanist.accompanistPermissions","com.google.accompanist","accompanist-permissions").versionRef("accompanist")
            library("androidx.constraintlayout.constraintlayoutCompose","androidx.constraintlayout:constraintlayout-compose:1.0.1")
            library("androidx.camera.cameraCamera2","androidx.camera","camera-camera2").versionRef("camerax")
            library("androidx.camera.cameraLifecycle","androidx.camera","camera-lifecycle").versionRef("camerax")
            library("androidx.camera.cameraView","androidx.camera","camera-view").versionRef("camerax")
            library("androidx.camera.cameraMlkitVision","androidx.camera","camera-mlkit-vision").versionRef("camerax")
            library("androidx.camera.cameraExtensions","androidx.camera","camera-extensions").versionRef("camerax")
            library("google.mlkit.barcodeScanning","com.google.mlkit:barcode-scanning:17.1.0")
            library("google.zxing","com.google.zxing:core:3.5.1")
            library("airbnb.android.lottieCompose", "com.airbnb.android:lottie-compose:6.0.0")
            library("squareup.moshi.moshi","com.squareup.moshi:moshi:1.14.0")
            library("squareup.moshi.moshiKotlin","com.squareup.moshi:moshi-kotlin:1.14.0")
            library("squareup.moshi.moshiKotlinCodegen","com.squareup.moshi:moshi-kotlin-codegen:1.14.0")
            library("androidx.work.workRuntimeKtx","androidx.work:work-runtime-ktx:2.8.1")
            library("androidx.hilt.hiltWork","androidx.hilt:hilt-work:1.0.0")
            library("androidx.hilt.hiltCompiler","androidx.hilt:hilt-compiler:1.0.0")
            library("google.accompanist.accompanistNavigationAnimation","com.google.accompanist:accompanist-navigation-animation:0.31.1-alpha")
        }

        create("testLibs") {
            library("junit", "junit:junit:4.13.2")
            library("androidx.test.runner","androidx.test:runner:1.5.2")
            library("androidx.test.core","androidx.test:core:1.5.0")
            library("androidx.test.ext.junit", "androidx.test.ext:junit:1.1.5")
            library("androidx.test.espresso.espressoCore", "androidx.test.espresso:espresso-core:3.5.1")
            library("androidx.compose.ui.uiTestJunit4", "androidx.compose.ui", "ui-test-junit4").version(composeVersion)
            library("androidx.compose.ui.uiTestManifest", "androidx.compose.ui", "ui-test-manifest").version(composeVersion)
            library( "google.truth","com.google.truth:truth:1.1.3")
            library("kotlinx.kotlinxCoroutinesTest","org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.0-Beta")
            library("mockito.kotlin.mockitoKotlin","org.mockito.kotlin:mockito-kotlin:4.1.0")
        }

    }
}
rootProject.name = "Aidventory"
include(":app")
include(":core")
include(":feature:home")
include(":feature:scanner")
include(":feature:settings")
include(":feature:containers")
include(":feature:supplies")
include(":feature:expired")
