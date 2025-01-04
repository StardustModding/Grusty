package org.stardustmodding.grusty.plugin.util

import com.akuleshov7.ktoml.Toml
import com.akuleshov7.ktoml.TomlInputConfig
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer
import java.io.File
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.exists

object CargoUtil {
    fun copyToTarget(projectDir: Path, mode: RustCompilationMode, target: Pair<String, String>? = null) {
        if (target == null) {
            copyToTargetInner(projectDir, mode)
        } else {
            copyToTargetInner(projectDir, mode, target)
        }
    }

    private fun copyToTargetInner(projectDir: Path, mode: RustCompilationMode, target: Pair<String, String>) {
        val res = projectDir
            .resolve("src")
            .resolve("generated")
            .resolve("resources")
            .resolve(target.second)

        if (!res.exists()) {
            res.createDirectories()
        }

        val bin = res.resolve(getPrefix(target.first) + getPackageName(projectDir) + getExt(target.first)).toFile()

        getBinaryPath(projectDir, mode, target.first)?.copyTo(bin, true)
    }

    private fun copyToTargetInner(projectDir: Path, mode: RustCompilationMode) {
        val res = projectDir
            .resolve("src")
            .resolve("generated")
            .resolve("resources")
            .resolve(HostUtil.getHostNativeDir())

        if (!res.exists()) {
            res.createDirectories()
        }

        val bin = res.resolve(getHostPrefix() + getPackageName(projectDir) + getHostExt()).toFile()

        getBinaryPath(projectDir, mode)?.copyTo(bin, true)
    }

    private fun getBinaryPath(projectDir: Path, mode: RustCompilationMode, target: String): File? {
        return projectDir
            .resolve("rust")
            .resolve("target")
            .resolve(target)
            .resolve(mode.toString())
            .resolve(getPrefix(target) + getPackageName(projectDir) + getExt(target))
            .toFile()
    }

    private fun getBinaryPath(projectDir: Path, mode: RustCompilationMode): File? {
        return projectDir
            .resolve("rust")
            .resolve("target")
            .resolve(mode.toString())
            .resolve(getHostPrefix() + getPackageName(projectDir) + getHostExt())
            .toFile()
    }

    private fun getPackageName(dir: Path): String? {
        val path = dir.resolve("rust").resolve("Cargo.toml")

        if (!path.exists()) {
            return null
        }

        val tomlString = path.toFile().readText()

        val toml = Toml.partiallyDecodeFromString<NeededHead>(
            serializer(),
            tomlString,
            "package",
            TomlInputConfig(ignoreUnknownNames = true)
        )

        return toml.name.replace("-", "_")
    }

    private fun getPrefix(target: String): String {
        if ("-pc-" in target) return ""
        if ("-apple-" in target) return "lib"

        return "lib"
    }

    private fun getHostPrefix(): String {
        val platform = System.getProperty("os.name").lowercase()

        if ("windows" in platform) return ""
        if ("mac" in platform) return "lib"

        return "lib"
    }

    private fun getExt(target: String): String {
        if ("-pc-" in target) return ".dll"
        if ("-apple-" in target) return ".dylib"

        return ".so"
    }

    private fun getHostExt(): String {
        val platform = System.getProperty("os.name").lowercase()

        if ("windows" in platform) return ".dll"
        if ("mac" in platform) return ".dylib"

        return ".so"
    }

    @Serializable
    private data class NeededHead(val name: String)
}
