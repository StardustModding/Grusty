package org.stardustmodding.grusty.plugin

import org.gradle.api.Project
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.stardustmodding.grusty.plugin.util.RustCompilationMode
import javax.inject.Inject

abstract class GrustyExtension
@Inject
constructor(project: Project) {
    private val objects = project.objects

    val zigbuild: Property<Boolean> = objects.property(Boolean::class.java).convention(true)

    val cross: Property<Boolean> = objects.property(Boolean::class.java).convention(false)

    val targets: MapProperty<String, String> = objects.mapProperty(String::class.java, String::class.java)

    val mode: Property<RustCompilationMode> =
        objects.property(RustCompilationMode::class.java).convention(RustCompilationMode.Debug)
}
