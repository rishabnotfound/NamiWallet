plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
}

tasks.register("buildRustLibrary") {
    group = "rust"
    description = "Build Rust library for all Android targets"

    doLast {
        val rustProjectDir = file("rust-core")
        val targets = listOf(
            "aarch64-linux-android",
            "armv7-linux-androideabi",
            "x86_64-linux-android",
            "i686-linux-android"
        )

        targets.forEach { target ->
            exec {
                workingDir = rustProjectDir
                commandLine("cargo", "ndk", "-t", target, "build", "--release")
            }
        }
    }
}
