pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "chetna"

include(
    ":domains:consent",
    ":domains:enforcement",
    ":domains:credits",
    ":domains:device",
    ":backend",
    ":mobile:android",
)
