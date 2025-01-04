package org.stardustmodding.grusty.plugin.util

object HostUtil {
    fun getHostTriple(): String {
        val proc = ProcessBuilder("rustc", "--version", "--verbose").redirectOutput(ProcessBuilder.Redirect.PIPE)
            .redirectError(ProcessBuilder.Redirect.PIPE).start()

        proc.waitFor()

        val data = proc.inputStream.bufferedReader().readText().split("\n")
        val item = data.find { it.startsWith("host:") }!!

        return item.split(":").last().trim()
    }

    fun getHostNativeDir(): String {
        val rustTargets = mapOf(
            "x86_64-pc-windows-gnu" to "win32-x86-64",
            "i686-pc-windows-gnu" to "win32-x86",
            "x86_64-pc-windows-msvc" to "win32-x86-64",
            "i686-pc-windows-msvc" to "win32-x86",
            "aarch64-pc-windows-gnullvm" to "win32-aarch64",
            "x86_64-unknown-linux-gnu" to "linux-x86-64",
            "aarch64-unknown-linux-gnu" to "linux-aarch64",
            "arm-unknown-linux-gnueabihf" to "linux-arm",
            "x86_64-unknown-linux-musl" to "linux-x86-64",
            "aarch64-unknown-linux-musl" to "linux-aarch64",
            "arm-unknown-linux-musleabihf" to "linux-arm",
            "i686-unknown-linux-gnu" to "linux-x86",
            "i686-unknown-linux-musl" to "linux-x86",
            "x86_64-apple-darwin" to "darwin-x86-64",
            "aarch64-apple-darwin" to "darwin-aarch64",
        )

        return rustTargets[getHostTriple()] ?: "unknown"
    }
}
