plugins {
    id("dev.architectury.loom") version "1.6.+"
}

class ModData {
    val id = property("mod.id").toString()
    val name = property("mod.name")
    val version = property("mod.version")
    val group = property("mod.group").toString()
    val description = property("mod.description")
    val github_link = property("mod.github_link")
    val issues_link = property("mod.issues_link")
}

val mod = ModData()

class LoaderData {
    val loader = loom.platform.get().name.lowercase()
    val isFabric = loader == "fabric"
    val isNeoforge = loader == "neoforge"
    val isForge = loader == "forge"
    val isForgeLike = isNeoforge || isForge
}

val loader = LoaderData()

val mcVersion = property("mod.mc_version")
val mcDep = property("mod.mc_dep")

val modmenu_version = property("deps.modmenu_version")
val yacl_version = if (mcVersion == "1.20.6") {
    // YACL version for 1.20.6 has 1.20.5 in name
    "${property("deps.yacl_version")}+1.20.5"
} else {
    "${property("deps.yacl_version")}+${property("mod.mc_version")}"
}

version = "${mod.version}+${mcVersion}-${loader.loader}"
group = mod.group
base { archivesName.set(mod.id) }

stonecutter.const("fabric", loader.isFabric)
stonecutter.const("neoforge", loader.isNeoforge)
stonecutter.const("forge", loader.isForge)
stonecutter.const("forgeLike", loader.isForgeLike)

loom {
    mods {
        create("mob_armor_trims") {
            sourceSet(sourceSets["main"])
        }
    }

    if (isForge) {
        forge.mixinConfigs("mob_armor_trims.mixins.json")
    }
}

repositories {
    // Parchment mappings
    maven("https://maven.parchmentmc.org")

    // YACL
    maven("https://maven.isxander.dev/releases")

    // Kotlin for Forge - required by YACL
    maven("https://thedarkcolour.github.io/KotlinForForge/")

    // Mod Menu
    maven("https://maven.terraformersmc.com/")

    // Neoforge
    maven("https://maven.neoforged.net/releases/")
}

dependencies {
    minecraft("com.mojang:minecraft:${mcVersion}")
    mappings(loom.layered {
        // Mojmap mappings
        officialMojangMappings()
        // Parchment mappings (it adds parameter mappings & javadoc)
        parchment("org.parchmentmc.data:parchment-${property("mod.mc_version")}:${property("deps.parchment_version")}@zip")
    })

    if (loader.isFabric) {
        modImplementation("net.fabricmc:fabric-loader:${property("deps.fabric_loader")}")

        // YACL
        modImplementation("dev.isxander:yet-another-config-lib:${yacl_version}-fabric")

        // Mod Menu
        modImplementation("com.terraformersmc:modmenu:${modmenu_version}")

        // Fabric API - Not a dependency, but so yacl and modmenu don't create errors
        //TODO: find alternative
        modImplementation("net.fabricmc.fabric-api:fabric-api:${property("deps.fabric_api")}")

        // NightConfig
        include("com.electronwill.night-config:core:${property("deps.night_config_version")}")
        include("com.electronwill.night-config:toml:${property("deps.night_config_version")}")
    }
    if (loader.isNeoforge) {
        "neoForge"("net.neoforged:neoforge:${findProperty("deps.neoforge")}")

        // YACL
        compileOnly("dev.isxander:yet-another-config-lib:${yacl_version}-neoforge")
    }
    if (loader.isForge) {
        "forge"("net.minecraftforge:forge:${property("deps.forge")}")

        // YACL
        compileOnly("dev.isxander:yet-another-config-lib:${yacl_version}-forge")
    }

    // NightConfig
    implementation("com.electronwill.night-config:core:${property("deps.night_config_version")}")
    implementation("com.electronwill.night-config:toml:${property("deps.night_config_version")}")
}

loom {
    runConfigs.all {
        ideConfigGenerated(stonecutter.current.isActive)
        vmArgs("-Dmixin.debug.export=true")
        runDir = "../../run"
    }
}

java {
    withSourcesJar()
}

tasks.processResources {
    val props = buildMap {
        put("id", mod.id)
        put("name", mod.name)
        put("version", mod.version)
        put("mcdep", mcDep)
        put("description", mod.description)
        put("github_link", mod.github_link)
        put("issues_link", mod.issues_link)
        put("modmenu_version", modmenu_version)
        put("yacl_version", yacl_version)

        if (loader.isForgeLike) {
            put("forgeConstraint", findProperty("modstoml.forge_constraint"))
        }
        if (mcVersion == "1.20.1" || mcVersion == "1.20.4") {
            put("forge_id", loader.loader)
        }
    }

    props.forEach(inputs::property)

    if (loader.isFabric) {
        filesMatching("fabric.mod.json") { expand(props) }
        exclude(listOf("META-INF/mods.toml", "META-INF/neoforge.mods.toml"))
    }
    if (loader.isForge) {
        filesMatching("META-INF/mods.toml") { expand(props) }
        exclude("fabric.mod.json", "META-INF/neoforge.mods.toml")
    }

    if (loader.isNeoforge) {
        if (mcVersion == "1.20.4") {
            filesMatching("META-INF/mods.toml") { expand(props) }
            exclude("fabric.mod.json", "META-INF/neoforge.mods.toml")
        } else {
            filesMatching("META-INF/neoforge.mods.toml") { expand(props) }
            exclude("fabric.mod.json", "META-INF/mods.toml")
        }
    }
}

afterEvaluate {
    loom {
        runs {
            configureEach {
                // 1.20.6 doesn't support shenandoah?
                // vmArgs("-Xmx2G", "-XX:+UseShenandoahGC")

                property("fabric.development", "true")
                property("mixin.debug", "true")
                property("mixin.debug.export.decompile", "false")
                property("mixin.debug.verbose", "true")
                property("mixin.dumpTargetOnFailure", "true")
                // makes silent failures into hard-failures
                property("mixin.checks", "true")
                property("mixin.hotSwap", "true")

                val mixinJarFile = configurations.compileClasspath.get().files {
                    it.group == "net.fabricmc" && it.name == "sponge-mixin"
                }.firstOrNull()
                /*
                Commented out, as Neoforge doesn't support it yet or smth like that and it throws an error
                if (mixinJarFile != null)
                    vmArg("-javaagent:$mixinJarFile")
                 */
            }
        }
    }
}

