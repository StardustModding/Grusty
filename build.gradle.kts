@file:Suppress("UnstableApiUsage")

plugins {
    id("java")
    kotlin("jvm") version "2.0.20"
    kotlin("plugin.serialization") version "2.0.20"
    id("java-gradle-plugin")
    id("maven-publish")
}

group = "org.stardustmodding"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(gradleApi())
    implementation("com.akuleshov7:ktoml-core:0.5.1")
    implementation("com.akuleshov7:ktoml-file:0.5.1")
}

kotlin {
    jvmToolchain(21)
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

gradlePlugin {
    website = "https://github.com/StardustModding/Grusty"
    vcsUrl = "https://github.com/StardustModding/Grusty.git"

    plugins {
        create("org.stardustmodding.grusty") {
            id = "org.stardustmodding.grusty"
            implementationClass = "org.stardustmodding.grusty.plugin.GrustyPlugin"
            version = "0.1.0"
            description = "A simple Gradle plugin to help facilitate Rust/JNI-based libraries."
            displayName = "grusty"

            tags.set(listOf("rust", "gradle", "grusty"))
        }
    }
}

publishing {
    if (System.getenv("MAVEN_USER") != null && System.getenv("MAVEN_PASSWORD") != null) {
        repositories {
            maven {
                name = "StardustModding"
                url = uri("https://maven.stardustmodding.org/snapshots")

                credentials {
                    username = System.getenv("MAVEN_USER")
                    password = System.getenv("MAVEN_PASSWORD")
                }
            }
        }
    }
}
