pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/") { name = "Fabric"}
        maven("https://maven.neoforged.net/releases/") { name = "NeoForged" }
        maven("https://maven.kikugie.dev/snapshots") { name = "KikuGie Snapshots" }
        maven("https://maven.kikugie.dev/releases") { name = "KikuGie" }
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.9"
}

stonecutter {
    create(rootProject) {
        fun create(mcVersion: String, loaders: Iterable<String>, obf: Boolean = false) {
            for (loader in loaders) {
                var version = version("$mcVersion-$loader", mcVersion)

                if (obf && loader == "fabric") {
                    version.buildscript = "build.fabric-obf.gradle.kts"
                } else {
                    version.buildscript = "build.$loader.gradle.kts"
                }
            }
        }

        create("1.21", listOf("fabric", "neoforge"), true)
        create("1.21.2", listOf("fabric", "neoforge"), true)
        create("1.21.5", listOf("fabric", "neoforge"), true)
        create("1.21.9", listOf("fabric", "neoforge"), true)
        create("1.21.11", listOf("fabric", "neoforge"), true)
        create("26.1", listOf("fabric", "neoforge"))

        vcsVersion = "26.1-fabric"
    }
}

rootProject.name = "Naturally Trimmed"
