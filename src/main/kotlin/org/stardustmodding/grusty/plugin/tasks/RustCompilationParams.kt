package org.stardustmodding.grusty.plugin.tasks

import org.gradle.api.provider.Property
import org.gradle.workers.WorkParameters
import org.stardustmodding.grusty.plugin.util.RustCompilationMode
import java.io.File

interface RustCompilationParams : WorkParameters {
    val mode: Property<RustCompilationMode>
    val target: Property<Pair<String, String>>
    val projectDir: Property<File>
    val zigbuild: Property<Boolean>
    val cross: Property<Boolean>
}
