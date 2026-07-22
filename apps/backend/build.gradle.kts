plugins {
    id("jvm-test-suite")
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.plugin.spring)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
}

dependencies {
    implementation(project(":domains:consent"))
    implementation(project(":domains:enforcement"))
    implementation(project(":domains:credits"))
    implementation(project(":domains:device"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-amqp")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("io.micrometer:micrometer-registry-prometheus")
    implementation("io.micrometer:micrometer-tracing-bridge-otel")
    implementation("io.opentelemetry:opentelemetry-exporter-otlp")
    implementation(libs.temporal.sdk)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.springdoc.openapi.webmvc.ui)

    runtimeOnly("org.postgresql:postgresql")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.mockk)
}

testing {
    suites {
        val test = named<JvmTestSuite>("test") {
            useJUnitJupiter()
        }

        val integrationTest = register<JvmTestSuite>("integrationTest") {
            useJUnitJupiter()
            dependencies {
                implementation(project())
                implementation("org.springframework.boot:spring-boot-starter-test")
                implementation(libs.kotest.runner.junit5)
                implementation(libs.kotest.assertions.core)
                implementation(libs.testcontainers.postgresql)
                implementation(libs.testcontainers.rabbitmq)
                implementation(libs.testcontainers.junit.jupiter)
                implementation(libs.temporal.testing)
            }
            targets {
                all {
                    testTask.configure {
                        shouldRunAfter(test)
                        testLogging {
                            showStandardStreams = true
                        }
                    }
                }
            }
        }

        register<JvmTestSuite>("e2eTest") {
            useJUnitJupiter()
            dependencies {
                implementation(project())
                implementation("org.springframework.boot:spring-boot-starter-test")
                implementation(libs.kotest.runner.junit5)
                implementation(libs.kotest.assertions.core)
            }
            targets {
                all {
                    testTask.configure {
                        shouldRunAfter(test, integrationTest)
                    }
                }
            }
        }
    }
}

tasks.named("check") {
    dependsOn(testing.suites.named("integrationTest"))
    // e2eTest needs a running application/stack; run explicitly (./gradlew e2eTest), not as part of `check`.
}
