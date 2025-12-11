plugins {
    id("dev.kikugie.stonecutter")
    id("dev.architectury.loom") version "1.13.+"
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

class LoaderData {
    val loader = loom.platform.get().name.lowercase()
    val isFabric = loader == "fabric"
    val isNeoforge = loader == "neoforge"
    val isForge = loader == "forge"
    val isForgeLike = isNeoforge || isForge
}

class McData {
    val version = property("mod.mc_version")
    val dep = property("mod.mc_dep")
    val targets = property("mod.mc_targets").toString().split(", ")
}

val mc = McData()
val mod = ModData()
val deps = ModDependencies()
val loader = LoaderData()

version = "${mod.version}+${mc.version}-${loader.loader}"
group = mod.group
base { archivesName.set(mod.id) }

stonecutter {
    constants {
        match(loader.loader, "fabric", "neoforge", "forge")
        put("forgeLike", loader.isForgeLike)
    }

    replacements {
        string {
            direction = eval(current.version, ">=1.21.2")
            replace("item.armortrim.*;", "item.equipment.trim.*;")
        }

        string {
            direction = eval(current.version, ">=1.21.11")
            replace("ResourceLocation", "Identifier")
        }

        string {
            direction = eval(current.version, ">=1.21.11")
            replace(".location()", ".identifier()")
        }

        string {
            direction = eval(current.version, ">=1.21.11")
            replace("minecraft.Util;", "minecraft.util.Util;")
        }

        for (movedClass in listOf("AbstractVillager", "VillagerDataHolder", "VillagerTrades")) {
            string {
                direction = eval(current.version, ">=1.21.11")
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

    if (isForgeLike) {
        if (isForge) {
            forge.mixinConfigs("naturally_trimmed.mixins.json")
            accessWidenerPath = rootProject.file("src/main/resources/naturally_trimmed.1_20_1.accesswidener")
            forge.convertAccessWideners.set(true)
        } else {
            accessWidenerPath = rootProject.file("src/main/resources/naturally_trimmed.accesswidener")
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

    // Mod Menu
    maven("https://maven.terraformersmc.com/")

    // Neoforge
    maven("https://maven.neoforged.net/releases/")

    // Quilt Parser
    maven("https://maven.quiltmc.org/repository/release/")
}

dependencies {
    minecraft("com.mojang:minecraft:${mc.version}")
    mappings(loom.layered {
        // Mojmap mappings
        officialMojangMappings()

        // Parchment mappings (it adds parameter mappings & javadoc)
        optionalProp("deps.parchment_version") {
            parchment("org.parchmentmc.data:parchment-${mc.version}:$it@zip")
        }

    })

    if (loader.isFabric) {
        modImplementation("net.fabricmc:fabric-loader:${property("deps.fabric_loader")}")

        // YACL
        modImplementation("dev.isxander:yet-another-config-lib:${deps.yacl}")

        // Fabric API
        modRuntimeOnly("net.fabricmc.fabric-api:fabric-api:${deps.fabricApi}")

        // Mod Menu
        modImplementation("com.terraformersmc:modmenu:${deps.modmenu}")
    } else if (loader.isNeoforge) {
        "neoForge"("net.neoforged:neoforge:${findProperty("deps.neoforge")}")

        // YACL
        implementation("dev.isxander:yet-another-config-lib:${deps.yacl}") {
            isTransitive = false
        }
    } else if (loader.isForge) {
        "forge"("net.minecraftforge:forge:${property("deps.forge")}")

        // YACL
        compileOnly("dev.isxander:yet-another-config-lib:${deps.yacl}") {
            isTransitive = false
        }
    }

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
    val java = if (stonecutter.eval(
            stonecutter.current.version,
            ">=1.20.6"
        )
    ) JavaVersion.VERSION_21 else JavaVersion.VERSION_17
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

        if (loader.isForgeLike) {
            put("forgeConstraint", findProperty("modstoml.forge_constraint"))
        } else {
            put("modmenu_version", deps.modmenu)
            put("fabric_api", deps.fabricApi)
        }
        if (mc.version == "1.20.1" || mc.version == "1.20.4") {
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
        if (mc.version == "1.20.4") {
            filesMatching("META-INF/mods.toml") { expand(props) }
            exclude("fabric.mod.json", "META-INF/neoforge.mods.toml")
        } else {
            filesMatching("META-INF/neoforge.mods.toml") { expand(props) }
            exclude("fabric.mod.json", "META-INF/mods.toml")
        }
    }
}

tasks.remapJar {
    if (loader.isNeoforge) {
        atAccessWideners.add("naturally_trimmed.accesswidener")
    }
}

publishMods {
    displayName = "${mod.name} ${mod.version} for ${loader.loader.replaceFirstChar { it.uppercase() }} ${mc.version}"
    file.set(tasks.remapJar.get().archiveFile)
    version = mod.version.toString()
    changelog.set(
        rootProject.file("CHANGELOG.md")
            .takeIf { it.exists() }
            ?.readText()
            ?: "No changelog provided."
    )
    type = STABLE
    modLoaders.add(loader.loader)

    dryRun = providers.environmentVariable("MODRINTH_TOKEN").getOrNull() == null ||
            providers.environmentVariable("CURSEFORGE_TOKEN").getOrNull() == null

    modrinth {
        projectId.set("hHVaPgFK")
        accessToken = providers.environmentVariable("MODRINTH_TOKEN")
        minecraftVersions.addAll(mc.targets)
        optional("yacl")
        if (loader.isFabric) {
            requires("fabric-api")
            optional("modmenu")
        }
    }

    curseforge {
        projectId.set("1005441")
        accessToken = providers.environmentVariable("CURSEFORGE_TOKEN")
        minecraftVersions.addAll(mc.targets)
        serverRequired = true
        optional("yacl")
        if (loader.isFabric) {
            requires("fabric-api")
            optional("modmenu")
        }
    }
}

fun <T> optionalProp(property: String, block: (String) -> T?): T? =
    findProperty(property)?.toString()?.takeUnless { it.isBlank() }?.let(block)
