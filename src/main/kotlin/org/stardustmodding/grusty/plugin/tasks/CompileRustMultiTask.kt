package org.stardustmodding.grusty.plugin.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import org.gradle.workers.WorkerExecutor
import org.stardustmodding.grusty.plugin.util.HostUtil
import org.stardustmodding.grusty.plugin.util.RustCompilationMode
import javax.inject.Inject

abstract class CompileRustMultiTask : DefaultTask() {
    init {
        description = "Compile the Rust project for multiple targets."
        group = "grusty"
    }

    @get:Input
    @get:Optional
    @get:Option(option = "target", description = "The targets to compile for.")
    abstract val targets: MapProperty<String, String>

    @get:Input
    @get:Optional
    @get:Option(option = "mode", description = "The mode to compile in.")
    abstract val mode: Property<RustCompilationMode>

    @get:Input
    @get:Option(option = "zigbuild", description = "Do we use cargo-zigbuild?")
    abstract val zigbuild: Property<Boolean>

    @get:Input
    @get:Option(option = "cross", description = "Do we use cross?")
    abstract val cross: Property<Boolean>

    @Inject
    abstract fun getWorkerExecutor(): WorkerExecutor

    @TaskAction
    fun compile() {
        val queue = getWorkerExecutor().noIsolation()

        if ((!targets.isPresent || targets.get().isEmpty()) && "msvc" !in HostUtil.getHostTriple()) {
            queue.submit(RustCompilationWorker::class.java) {
                it.mode.set(mode.getOrElse(RustCompilationMode.Debug))
                it.projectDir.set(project.projectDir)
                it.zigbuild.set(zigbuild.get())
                it.cross.set(cross.get())
            }
        } else {
            for (target in targets.get()) {
                queue.submit(RustCompilationWorker::class.java) {
                    it.target.set(target.key to target.value)
                    it.mode.set(mode.getOrElse(RustCompilationMode.Debug))
                    it.projectDir.set(project.projectDir)
                    it.zigbuild.set(zigbuild.get())
                    it.cross.set(cross.get())
                }
            }
        }
    }
}
