package org.stardustmodding.grusty.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.stardustmodding.grusty.plugin.tasks.CompileRustMultiTask
import org.stardustmodding.grusty.plugin.tasks.CompileRustTask

abstract class GrustyPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val ext = project.extensions.create("grusty", GrustyExtension::class.java, project)

        project.tasks.register("compileRustMulti", CompileRustMultiTask::class.java) {
            it.mode.set(ext.mode.get())
            it.zigbuild.set(ext.zigbuild.get())
            it.targets.set(ext.targets.getOrElse(mapOf()))
            it.cross.set(ext.cross.get())
        }

        project.tasks.register("compileRustHost", CompileRustTask::class.java) {
            it.mode.set(ext.mode.get())
            it.zigbuild.set(ext.zigbuild.get())
            it.cross.set(ext.cross.get())
        }

        project.afterEvaluate {
            for (item in ext.targets.getOrElse(mapOf())) {
                project.tasks.register("compileRust_${item}", CompileRustTask::class.java) {
                    it.mode.set(ext.mode.get())
                    it.target.set(item.key to item.value)
                    it.zigbuild.set(ext.zigbuild.get())
                    it.cross.set(ext.cross.get())
                }
            }
        }
    }
}
