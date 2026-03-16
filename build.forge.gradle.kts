plugins {
    id("dev.kikugie.stonecutter")
    id("net.minecraftforge.gradle")
    id("net.minecraftforge.jarjar") version "0.2.3"
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
    val forge = property("deps.forge")
}

class McData {
    val version = property("mod.mc_version")
    val dep = property("mod.mc_dep")
    val targets = property("mod.mc_targets").toString().split(", ")
}

val mc = McData()
val mod = ModData()
val deps = ModDependencies()

version = "${mod.version}+${mc.version}-forge"
group = mod.group
base.archivesName = mod.id

stonecutter {
    constants {
        match("forge", "fabric", "neoforge", "forge")
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

minecraft {
    version = deps.forge as String
    mappings("official", "1.20.1")
    // rootProject is for some reason set to `D:\Projekte\Minecraft Modding\Naturally-Trimmed\versions\1.20.1-forge\src\main\resources\`
    // Since everything else works fine, just do some relative pathing to the actual access transformer
    setAccessTransformer("..\\..\\..\\..\\..\\src\\main\\resources\\META-INF\\accesstransformer.cfg")

    runs {
        register("client") {
            workingDir = file("../../run/")
            sourceSets.add(sourceSets["main"])
        }

        register("server") {
            workingDir = file("../../run/")
            sourceSets.add(sourceSets["main"])
        }
    }
}

jarJar.register {
    archiveClassifier = null
}

repositories {
    // Minecraft
    minecraft.mavenizer(this)
    maven(fg.minecraftLibsMaven)

    // Forge
    maven(fg.forgeMaven)

    // Parchment mappings
    maven("https://maven.parchmentmc.org")

    // YACL
    maven("https://maven.isxander.dev/releases")

    // Kotlin for Forge - required by YACL
    maven("https://thedarkcolour.github.io/KotlinForForge/")

    // Quilt Parser
    maven("https://maven.quiltmc.org/repository/release/")
}



dependencies {
    implementation(minecraft.dependency("net.minecraftforge:forge:${deps.forge}"))

    // YACL
    implementation("dev.isxander:yet-another-config-lib:${deps.yacl}") {
        isTransitive = false
    }

    // Quilt Parser
    implementation("org.quiltmc.parsers:json:${property("deps.quilt_parser")}")
    "jarJar"("org.quiltmc.parsers:json:${property("deps.quilt_parser")}")
    implementation("org.quiltmc.parsers:gson:${property("deps.quilt_parser")}")
    "jarJar"("org.quiltmc.parsers:gson:${property("deps.quilt_parser")}")
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
        put("forge_id", "forge")
    }

    props.forEach(inputs::property)

    filesMatching("META-INF/mods.toml") { expand(props) }
    exclude("fabric.mod.json", "META-INF/neoforge.mods.toml")
}

publishMods {
    displayName = "${mod.name} ${mod.version} for Forge ${mc.version}"
    file = tasks.jar.map { it.archiveFile.get() }
    version = mod.version.toString()
    changelog.set(
        rootProject.file("CHANGELOG.md")
            .takeIf { it.exists() }
            ?.readText()
            ?: "No changelog provided."
    )
    type = STABLE
    modLoaders.add("forge")

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
