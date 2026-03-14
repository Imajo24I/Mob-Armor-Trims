pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/") { name = "Fabric"}
        maven("https://maven.neoforged.net/releases/") { name = "NeoForged" }
        maven("https://maven.minecraftforge.net/") { name = "Forge" }
        maven("https://maven.kikugie.dev/snapshots") { name = "KikuGie" }
        maven("https://maven.kikugie.dev/releases") { name = "KikuGie" }
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.8"
}

stonecutter {
    create(rootProject) {
        fun mc(mcVersion: String, loaders: Iterable<String>) {
            for (loader in loaders) {
                version("$mcVersion-$loader", mcVersion).buildscript = "build.$loader.gradle.kts"
            }
        }

        //TODO: readd support for 1.20.1, drop 1.20.4-1.20.6
        mc("1.20.1", listOf("fabric", "forge"))
        // mc("1.20.4", listOf("fabric", "neoforge"))
        // mc("1.20.6", listOf("fabric", "neoforge"))
        mc("1.21", listOf("fabric", "neoforge"))
        mc("1.21.2", listOf("fabric", "neoforge"))
        mc("1.21.5", listOf("fabric", "neoforge"))
        mc("1.21.9", listOf("fabric", "neoforge"))
        mc("1.21.11", listOf("fabric", "neoforge"))

        vcsVersion = "1.21.11-fabric"
    }
}

rootProject.name = "Naturally Trimmed"
