package org.stardustmodding.grusty.plugin.util

enum class RustCompilationMode {
    Release,
    Debug;

    override fun toString(): String = when (this) {
        Release -> "release"
        Debug -> "debug"
    }
}
