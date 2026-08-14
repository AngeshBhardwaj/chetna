import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

// Shared setup for the apps/domains/* Kotlin Multiplatform modules — was
// copy-pasted identically across all four before this convention plugin
// (only `namespace` differs per module, still set in each consumer).
//
// `libs.foo` generated accessors aren't available inside precompiled script
// plugins (a known, still-open Gradle limitation — gradle/gradle#15383), so
// this looks the catalog up directly instead.
val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("com.android.kotlin.multiplatform.library")
}

kotlin {
    jvm()
    android {
        compileSdk = 37
        minSdk = 26
    }
    // ios target is added when mobile scaffolding extends to iOS (ADR-0003).

    sourceSets {
        commonMain.dependencies {
            implementation(libs.findLibrary("kotlinx-coroutines-core").get())
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.findLibrary("kotest-runner-junit5").get())
            implementation(libs.findLibrary("kotest-assertions-core").get())
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
