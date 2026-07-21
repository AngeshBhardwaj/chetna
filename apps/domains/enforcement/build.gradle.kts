plugins {
    alias(libs.plugins.kotlin.multiplatform)
}

kotlin {
    jvm()
    // android() and ios targets are added when mobile scaffolding follows (ADR-0003).

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.kotest.runner.junit5)
            implementation(libs.kotest.assertions.core)
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
