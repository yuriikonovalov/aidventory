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
        val composeVersion = "1.3.3"

        create("libs") {
            version("compose", composeVersion)

            library("androidx.coreKtx", "androidx.core:core-ktx:1.9.0")
            library(
                "androidx.lifecycleRuntimeKtx",
                "androidx.lifecycle:lifecycle-runtime-ktx:2.3.1"
            )
            library("androidx.activityCompose", "androidx.activity:activity-compose:1.6.1")
            library("androidx.compose.ui", "androidx.compose.ui", "ui").versionRef("compose")
            library(
                "androidx.compose.uiToolingPreview",
                "androidx.compose.ui",
                "ui-tooling-preview"
            ).versionRef("compose")
            library(
                "androidx.compose.material3",
                "androidx.compose.material3:material3:1.0.1"
            )

            library(
                "androidx.compose.uiTooling",
                "androidx.compose.ui",
                "ui-tooling"
            ).versionRef("compose")
            library(
                "androidx.compose.uiTestManifest",
                "androidx.compose.ui",
                "ui-test-manifest"
            ).versionRef("compose")
        }

        create("testLibs") {
            library("junit", "junit:junit:4.13.2")
            library("androidx.test.junit", "androidx.test.ext:junit:1.1.5")
            library("androidx.test.espresso.core", "androidx.test.espresso:espresso-core:3.5.1")
            library(
                "androidx.compose.uiTestJunit4",
                "androidx.compose.ui",
                "ui-test-junit4"
            ).version(composeVersion)
        }

    }
}
rootProject.name = "Aidventory"
include(":app")
