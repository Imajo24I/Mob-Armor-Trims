plugins {
    id("dev.kikugie.stonecutter")
    id("net.neoforged.moddev")
    id("me.modmuss50.mod-publish-plugin")
}

class ModData {
    val id = property("mod.id").toString()
    val name = property("mod.name")
    val version = property("mod.version")
    val group = property("mod.group").toString()
    val description = property("mod.description")
    val githubLink = property("mod.github_link")
    val issuesLink = property("mod.issues_link")
}

class ModDependencies {
    val yacl = property("deps.yacl_version")
    val neoforge = property("deps.neoforge")
}

class McData {
    val version = property("mod.mc_version")
    val dep = property("mod.mc_dep")
    val targets = property("mod.mc_targets").toString().split(", ")
}

val mc = McData()
val mod = ModData()
val deps = ModDependencies()

version = "${mod.version}+${mc.version}-neoforge"
group = mod.group
base.archivesName = mod.id

stonecutter {
    constants {
        match("neoforge", "fabric", "neoforge", "forge")
        put("forgeLike", true)
    }

    replacements {
        string {
            direction = sc.current.parsed >= "1.21.2"
            replace("item.armortrim.*;", "item.equipment.trim.*;")
        }

        string {
            direction = sc.current.parsed >= "1.21.11"
            replace("ResourceLocation", "Identifier")
        }

        string {
            direction = sc.current.parsed >= "1.21.11"
            replace(".location()", ".identifier()")
        }

        string {
            direction = sc.current.parsed >= "1.21.11"
            replace("minecraft.Util;", "minecraft.util.Util;")
        }

        for (movedClass in listOf("AbstractVillager", "VillagerDataHolder", "VillagerTrades")) {
            string {
                direction = sc.current.parsed >= "1.21.11"
                replace("npc.${movedClass}", "npc.villager.${movedClass}")
            }
        }
    }
}

neoForge {
    version = deps.neoforge as String
    validateAccessTransformers = true

    // Parchment
    if (hasProperty("deps.parchment_version")) parchment {
        mappingsVersion = property("deps.parchment_version") as String
        minecraftVersion = mc.version as String
    }


    runs {
        register("client") {
            gameDirectory = file("../../run/")
            client()
        }

        register("server") {
            gameDirectory = file("../../run/")
            server()
        }
    }

    mods {
        register(mod.id) {
            sourceSet(sourceSets["main"])
        }
    }
}

repositories {
    // Parchment mappings
    maven("https://maven.parchmentmc.org")

    // YACL
    maven("https://maven.isxander.dev/releases")

    // Kotlin for Forge - required by YACL
    maven("https://thedarkcolour.github.io/KotlinForForge/")

    // Neoforge
    maven("https://maven.neoforged.net/releases/")

    // Quilt Parser
    maven("https://maven.quiltmc.org/repository/release/")
}

dependencies {
    // YACL
    implementation("dev.isxander:yet-another-config-lib:${deps.yacl}") {
        isTransitive = false
    }

    // Quilt Parser
    implementation("org.quiltmc.parsers:json:${property("deps.quilt_parser")}")
    jarJar("org.quiltmc.parsers:json:${property("deps.quilt_parser")}")
    implementation("org.quiltmc.parsers:gson:${property("deps.quilt_parser")}")
    jarJar("org.quiltmc.parsers:gson:${property("deps.quilt_parser")}")
}

java {
    val java = if (sc.current.parsed >= "1.20.6") JavaVersion.VERSION_21 else JavaVersion.VERSION_17
    sourceCompatibility = java
    targetCompatibility = java
    withSourcesJar()
}

tasks.processResources {
    val props = buildMap {
        put("id", mod.id)
        put("name", mod.name)
        put("version", mod.version)
        put("mcdep", mc.dep)
        put("description", mod.description)
        put("github_link", mod.githubLink)
        put("issues_link", mod.issuesLink)
        put("yacl_version", deps.yacl)

        put("forgeConstraint", findProperty("modstoml.forge_constraint"))
    }

    props.forEach(inputs::property)

    filesMatching("META-INF/neoforge.mods.toml") { expand(props) }
    exclude("fabric.mod.json", "META-INF/mods.toml")
}

tasks {
    named("createMinecraftArtifacts") {
        dependsOn("stonecutterGenerate")
    }
}

publishMods {
    displayName = "${mod.name} ${mod.version} for Neoforge ${mc.version}"
    file = tasks.jar.map { it.archiveFile.get() }
    version = mod.version.toString()
    changelog.set(
        rootProject.file("CHANGELOG.md")
            .takeIf { it.exists() }
            ?.readText()
            ?: "No changelog provided."
    )
    type = STABLE
    modLoaders.add("neoforge")

    dryRun = providers.environmentVariable("MODRINTH_TOKEN").getOrNull() == null ||
            providers.environmentVariable("CURSEFORGE_TOKEN").getOrNull() == null

    modrinth {
        projectId.set("hHVaPgFK")
        accessToken = providers.environmentVariable("MODRINTH_TOKEN")
        minecraftVersions.addAll(mc.targets)
        optional("yacl")
    }

    curseforge {
        projectId.set("1005441")
        accessToken = providers.environmentVariable("CURSEFORGE_TOKEN")
        minecraftVersions.addAll(mc.targets)
        serverRequired = true
        optional("yacl")
    }
}
