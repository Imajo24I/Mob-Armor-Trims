plugins {
    id("fabric-loom")
    id("me.modmuss50.mod-publish-plugin")
}

val mcVersion = sc.current.project.split("-").first()
stonecutter.properties.tags(mcVersion)

version = "${property("mod.version")}+${mcVersion}-fabric"
group = property("mod.group") as String
base.archivesName = property("mod.id") as String

stonecutter {
    constants {
        match("fabric", "fabric", "neoforge", "forge")
        put("forgeLike", false)
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


loom {
    mods {
        create("naturally_trimmed") {
            sourceSet(sourceSets["main"])
        }
    }
}

repositories {
    // Parchment mappings
    maven("https://maven.parchmentmc.org")

    // YACL
    maven("https://maven.isxander.dev/releases")

    // Mod Menu
    maven("https://maven.terraformersmc.com/")

    // Quilt Parser
    maven("https://maven.quiltmc.org/repository/release/")
}

dependencies {
    minecraft("com.mojang:minecraft:${mcVersion}")
    mappings(loom.layered {
        // Mojmap mappings
        officialMojangMappings()

        // Parchment mappings (it adds parameter mappings & javadoc)
        if (hasProperty("deps.parchment"))
            parchment("org.parchmentmc.data:parchment-${mcVersion}:${property("deps.parchment")}@zip")

    })

    modImplementation("net.fabricmc:fabric-loader:${property("deps.fabric_loader")}")

    // YACL
    modImplementation("dev.isxander:yet-another-config-lib:${property("deps.yacl")}-fabric")

    // Fabric API
    modRuntimeOnly("net.fabricmc.fabric-api:fabric-api:${property("deps.fabric_api")}")

    // Mod Menu
    modImplementation("com.terraformersmc:modmenu:${property("deps.modmenu")}")

    // Quilt Parser
    implementation("org.quiltmc.parsers:json:${property("deps.quilt_parser")}")
    include("org.quiltmc.parsers:json:${property("deps.quilt_parser")}")
    implementation("org.quiltmc.parsers:gson:${property("deps.quilt_parser")}")
    include("org.quiltmc.parsers:gson:${property("deps.quilt_parser")}")
}

loom {
    runConfigs.all {
        ideConfigGenerated(stonecutter.current.isActive)
        runDir = "../../run"
    }
}

java {
    val java = if (sc.current.parsed >= "1.20.6") JavaVersion.VERSION_21 else JavaVersion.VERSION_17
    sourceCompatibility = java
    targetCompatibility = java
    withSourcesJar()
}

tasks.processResources {
    // For some reason just property() isn't actually able to find the properties in this task,
    // so a shorthand for project.property() is used
    fun property(name: String): Any? = project.property(name)

    val props = buildMap {
        put("id", property("mod.id"))
        put("name", property("mod.name"))
        put("version", property("mod.version"))
        put("description", property("mod.description"))
        put("github_link", property("mod.github_link"))
        put("issues_link", property("mod.issues_link"))
        put("mc", property("mc.dep"))
        put("yacl", property("deps.yacl"))
        put("modmenu", property("deps.modmenu"))
        put("fabric_api", property("deps.fabric_api"))
    }

    props.forEach(inputs::property)

    filesMatching("fabric.mod.json") { expand(props) }
    exclude(listOf("META-INF/mods.toml", "META-INF/neoforge.mods.toml"))
}

publishMods {
    displayName = "${property("mod.name")} ${property("mod.version")} for Fabric $mcVersion"
    file.set(tasks.remapJar.get().archiveFile)
    version = property("mod.version").toString()
    changelog.set(
        rootProject.file("CHANGELOG.md")
            .takeIf { it.exists() }
            ?.readText()
            ?: "No changelog provided."
    )
    type = STABLE
    modLoaders.add("fabric")

    dryRun = providers.environmentVariable("MODRINTH_TOKEN").getOrNull() == null ||
            providers.environmentVariable("CURSEFORGE_TOKEN").getOrNull() == null

    val targets = property("mc.targets").toString().split(", ")

    modrinth {
        projectId.set("hHVaPgFK")
        accessToken = providers.environmentVariable("MODRINTH_TOKEN")
        minecraftVersions.addAll(targets)
        optional("yacl")
        requires("fabric-api")
        optional("modmenu")
    }

    curseforge {
        projectId.set("1005441")
        accessToken = providers.environmentVariable("CURSEFORGE_TOKEN")
        minecraftVersions.addAll(targets)
        serverRequired = true
        optional("yacl")
        requires("fabric-api")
        optional("modmenu")
    }
}
