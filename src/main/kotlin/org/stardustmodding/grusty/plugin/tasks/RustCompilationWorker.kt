package org.stardustmodding.grusty.plugin.tasks

import org.gradle.workers.WorkAction
import org.stardustmodding.grusty.plugin.util.CargoUtil
import org.stardustmodding.grusty.plugin.util.RustCompilationMode
import kotlin.concurrent.thread

abstract class RustCompilationWorker : WorkAction<RustCompilationParams> {
    override fun execute() {
        val cmd = mutableListOf<String>()

        if (parameters.cross.get() || System.getProperty("grusty.cross") == "true") {
            cmd.add("cross")
        } else {
            cmd.add("cargo")
        }

        if (parameters.zigbuild.get()) {
            cmd.add("zigbuild")
        } else {
            cmd.add("build")
        }

        if (parameters.target.isPresent) {
            cmd.addAll(listOf("--target", parameters.target.get().first))
        }

        if (parameters.mode.get() == RustCompilationMode.Release) {
            cmd.add("--release")
        }

        val builder = ProcessBuilder(cmd)
            .directory(parameters.projectDir.get().resolve("rust"))
            .redirectOutput(ProcessBuilder.Redirect.PIPE)
            .redirectError(ProcessBuilder.Redirect.PIPE)

        if ("-pc-" in parameters.target.getOrElse("" to "").first) {
            val file = parameters.projectDir.get().resolve("rust").resolve("exports.def").absolutePath

            builder.environment()["RUSTFLAGS"] = "-C link-arg=${file}"
        }

        val proc = builder.start()

        val stdout = thread(isDaemon = true) {
            proc.inputStream.bufferedReader().useLines { it ->
                it.filter(String::isNotEmpty).forEach {
                    println("[stdout] [${parameters.target.map { it.first }.getOrElse("(host)")}] $it")
                }
            }
        }

        val stderr = thread(isDaemon = true) {
            proc.errorStream.bufferedReader().useLines { it ->
                it.filter(String::isNotEmpty).forEach {
                    println("[stderr] [${parameters.target.map { it.first }.getOrElse("(host)")}] $it")
                }
            }
        }

        val res = proc.waitFor()

        stdout.interrupt()
        stderr.interrupt()

        if (res != 0) {
            throw RuntimeException(
                "Could not compile the Rust project in ${parameters.mode.get()} mode for target ${
                    parameters.target.map { it.first }.getOrElse(
                        "(host)"
                    )
                }! (Exit code ${res})\nstdio:\n${
                    proc.inputStream.bufferedReader().readText()
                }\nstderr:\n${proc.errorStream.bufferedReader().readText()}"
            )
        }

        CargoUtil.copyToTarget(parameters.projectDir.get().toPath(), parameters.mode.get(), parameters.target.orNull)
    }
}
