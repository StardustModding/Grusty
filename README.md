# Grusty

A simple Gradle plugin to help facilitate Rust/JNI-based libraries.

## Usage

```kts
// settings.gradle.kts

pluginManagement {
    repositories {
        maven("https://maven.stardustmodding.org/public-snapshots")
    }
}

// build.gradle.kts

plugins {
    id("org.stardustmodding.grusty") version "1.0-SNAPSHOT"
}
```
