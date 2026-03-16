plugins {
    id("fabric-loom")
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
    val modmenu = findProperty("deps.modmenu_version")
    val fabricApi = findProperty("deps.fabric_api")
}

class McData {
    val version = property("mod.mc_version")
    val dep = property("mod.mc_dep")
    val targets = property("mod.mc_targets").toString().split(", ")
}

val mc = McData()
val mod = ModData()
val deps = ModDependencies()

version = "${mod.version}+${mc.version}-fabric"
group = mod.group
base.archivesName = mod.id

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

    //TODO: Update to newest and remove this
    // Loom 1.13 no longer uses the legacy mixin stuff by default,
    // which for some reason breaks the VillagerTradesMixin on fabric
    // This probably is a bad solution to the problem, but it works for now.
    // Once mc versions are unobfuscated, this will probably no longer be a problem and can be removed.
    mixin.useLegacyMixinAp = true
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
    minecraft("com.mojang:minecraft:${mc.version}")
    mappings(loom.layered {
        // Mojmap mappings
        officialMojangMappings()

        // Parchment mappings (it adds parameter mappings & javadoc)
        if (hasProperty("deps.parchment_version"))
            parchment("org.parchmentmc.data:parchment-${mc.version}:${property("deps.parchment_version")}@zip")

    })

    modImplementation("net.fabricmc:fabric-loader:${property("deps.fabric_loader")}")

    // YACL
    modImplementation("dev.isxander:yet-another-config-lib:${deps.yacl}")

    // Fabric API
    modRuntimeOnly("net.fabricmc.fabric-api:fabric-api:${deps.fabricApi}")

    // Mod Menu
    modImplementation("com.terraformersmc:modmenu:${deps.modmenu}")

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
    val props = buildMap {
        put("id", mod.id)
        put("name", mod.name)
        put("version", mod.version)
        put("mcdep", mc.dep)
        put("description", mod.description)
        put("github_link", mod.githubLink)
        put("issues_link", mod.issuesLink)
        put("yacl_version", deps.yacl)
        put("modmenu_version", deps.modmenu)
        put("fabric_api", deps.fabricApi)
    }

    props.forEach(inputs::property)

    filesMatching("fabric.mod.json") { expand(props) }
    exclude(listOf("META-INF/mods.toml", "META-INF/neoforge.mods.toml"))
}

publishMods {
    displayName = "${mod.name} ${mod.version} for Fabric ${mc.version}"
    file.set(tasks.remapJar.get().archiveFile)
    version = mod.version.toString()
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

    modrinth {
        projectId.set("hHVaPgFK")
        accessToken = providers.environmentVariable("MODRINTH_TOKEN")
        minecraftVersions.addAll(mc.targets)
        optional("yacl")
        requires("fabric-api")
        optional("modmenu")
    }

    curseforge {
        projectId.set("1005441")
        accessToken = providers.environmentVariable("CURSEFORGE_TOKEN")
        minecraftVersions.addAll(mc.targets)
        serverRequired = true
        optional("yacl")
        requires("fabric-api")
        optional("modmenu")
    }
}
