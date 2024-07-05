plugins {
    id("dev.architectury.loom") version "1.7.+"
    id("me.modmuss50.mod-publish-plugin") version "0.5.1"
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

val modmenuVersion = property("deps.modmenu_version")
val yaclVersion = findProperty("deps.yacl_version")

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
        optionalProp("deps.parchment_version") {
            parchment("org.parchmentmc.data:parchment-${property("mod.mc_version")}:$it@zip")
        }

    })

    if (loader.isFabric) {
        modImplementation("net.fabricmc:fabric-loader:${property("deps.fabric_loader")}")

        // YACL
        modImplementation("dev.isxander:yet-another-config-lib:${yaclVersion}+${mcVersion}-fabric")

        // Mod Menu
        modImplementation("com.terraformersmc:modmenu:${modmenuVersion}")

        // NightConfig
        include("com.electronwill.night-config:core:${property("deps.night_config_version")}")
        include("com.electronwill.night-config:toml:${property("deps.night_config_version")}")
    }
    if (loader.isNeoforge) {
        "neoForge"("net.neoforged:neoforge:${findProperty("deps.neoforge")}")

        // YACL
        compileOnly("dev.isxander:yet-another-config-lib:${yaclVersion}+${mcVersion}-neoforge")
    }
    if (loader.isForge) {
        "forge"("net.minecraftforge:forge:${property("deps.forge")}")

        // YACL
        compileOnly("dev.isxander:yet-another-config-lib:${yaclVersion}+${mcVersion}-forge")
    }

    // NightConfig
    implementation("com.electronwill.night-config:core:${property("deps.night_config_version")}")
    implementation("com.electronwill.night-config:toml:${property("deps.night_config_version")}")
}

loom {
    runConfigs.all {
        ideConfigGenerated(stonecutter.current.isActive)
        runDir = "../../run"
    }
}

java {
    val java = if (stonecutter.compare(
                stonecutter.current.version,
                "1.20.6"
            ) >= 0
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
        put("mcdep", mcDep)
        put("description", mod.description)
        put("github_link", mod.githubLink)
        put("issues_link", mod.issuesLink)
        put("modmenu_version", modmenuVersion)
        put("yacl_version", yaclVersion)

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

publishMods {
    displayName = "${mod.name} ${mod.version}"
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

    //fixme: should be all versions in mcDep, not just the version given to stonecutter
    // for example, if on 1.20.6, 1.20.5 wouldn't be added, even though its currently supported
    val stableMCVersions = listOf(stonecutter.current.version)

    dryRun = providers.environmentVariable("MODRINTH_TOKEN").getOrNull() == null ||
            providers.environmentVariable("CURSEFORGE_TOKEN").getOrNull() == null

    modrinth {
        projectId.set("hHVaPgFK")
        accessToken = providers.environmentVariable("MODRINTH_TOKEN")
        minecraftVersions.addAll(stableMCVersions)
        optional("yacl")
        if (loader.isFabric) {
            optional("modmenu")
        }
    }

    curseforge {
        projectId.set("1005441")
        accessToken = providers.environmentVariable("CURSEFORGE_TOKEN")
        minecraftVersions.addAll(stableMCVersions)
        serverRequired = true
        optional("yacl")
        if (loader.isFabric) {
            optional("modmenu")
        }
    }
}

fun <T> optionalProp(property: String, block: (String) -> T?): T? =
    findProperty(property)?.toString()?.takeUnless { it.isBlank() }?.let(block)

fun isPropDefined(property: String): Boolean {
    return property(property)?.toString()?.isNotBlank() ?: false
}
