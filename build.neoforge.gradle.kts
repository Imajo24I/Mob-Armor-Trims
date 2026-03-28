plugins {
    id("dev.kikugie.stonecutter")
    id("net.neoforged.moddev")
    id("me.modmuss50.mod-publish-plugin")
}

stonecutter.properties.tags(sc.current.version)
version = "${property("mod.version")}+${sc.current.version}-neoforge"
group = property("mod.group") as String
base.archivesName = property("mod.id") as String

neoForge {
    version = property("deps.neoforge") as String
    validateAccessTransformers = true

    // Parchment
    if (hasProperty("deps.parchment")) parchment {
        mappingsVersion = property("deps.parchment") as String
        minecraftVersion = sc.current.version
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
        register(property("mod.id") as String) {
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
    implementation("dev.isxander:yet-another-config-lib:${property("deps.yacl")}-neoforge") {
        isTransitive = false
    }

    // Quilt Parser
    implementation("org.quiltmc.parsers:json:${property("deps.quilt_parser")}")
    jarJar("org.quiltmc.parsers:json:${property("deps.quilt_parser")}")
    implementation("org.quiltmc.parsers:gson:${property("deps.quilt_parser")}")
    jarJar("org.quiltmc.parsers:gson:${property("deps.quilt_parser")}")
}

java {
    val java = if (sc.current.parsed >= "26.1") JavaVersion.VERSION_25 else JavaVersion.VERSION_21
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
        put("mc", property("mc.dep"))
        put("description", property("mod.description"))
        put("github_link", property("mod.github_link"))
        put("issues_link", property("mod.issues_link"))
        put("yacl", property("deps.yacl"))

        put("neoforge_constraint", property("deps.neoforge_constraint"))
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
    displayName = "${property("mod.name")} ${property("mod.version")} for Neoforge $sc.current.version"
    file = tasks.jar.map { it.archiveFile.get() }
    version = property("mod.version") as String
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

    val targets = property("mc.targets").toString().split(", ")

    modrinth {
        projectId.set("hHVaPgFK")
        accessToken = providers.environmentVariable("MODRINTH_TOKEN")
        minecraftVersions.addAll(targets)
        optional("yacl")
    }

    curseforge {
        projectId.set("1005441")
        accessToken = providers.environmentVariable("CURSEFORGE_TOKEN")
        minecraftVersions.addAll(targets)
        serverRequired = true
        optional("yacl")
    }
}
