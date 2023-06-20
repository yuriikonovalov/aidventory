// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:7.4.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.10")
        classpath("com.google.gms:google-services:4.3.15")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.9.5")
    }
}

plugins {
    id("com.google.dagger.hilt.android") version "2.44" apply false
    id("org.jetbrains.kotlin.jvm") version "1.8.0" apply false
    id("com.android.library") version "7.4.2" apply false
    id("org.jetbrains.kotlin.android") version "1.8.0" apply false
}

val compileSdkVersion by extra(33)
val targetSdkVersion by extra(33)
val minSdkVersion by extra(28)

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}