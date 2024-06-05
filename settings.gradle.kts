import dev.kikugie.stonecutter.StonecutterSettings

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/")
        maven("https://maven.neoforged.net/releases/")
        maven("https://maven.minecraftforge.net/")
        maven("https://maven.architectury.dev")
        maven("https://maven.kikugie.dev/snapshots")
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.4-beta.2"
}

extensions.configure<StonecutterSettings> {
    kotlinController= true
    centralScript = "build.gradle.kts"

    shared {
        fun mc(mcVersion: String, loaders: Iterable<String>) {
            for (loader in loaders) {
                versions("$mcVersion-$loader")
            }
        }

        mc("1.20.1", listOf("fabric", "forge"))
        mc("1.20.4", listOf("fabric", "neoforge"))
        mc("1.20.6", listOf("fabric", "neoforge"))
    }
    create(rootProject)
}

rootProject.name = "Mob Armor Trims"