plugins {
    id("dev.kikugie.stonecutter")
    id("net.minecraftforge.gradle")
    id("net.minecraftforge.jarjar") version "0.2.3"
    id("me.modmuss50.mod-publish-plugin")
}

stonecutter.properties.tags(sc.current.version)
version = "${property("mod.version")}+${sc.current.version}-forge"
group = property("mod.group") as String
base.archivesName = property("mod.id") as String

minecraft {
    version = property("deps.forge") as String
    mappings("official", "1.20.1")
    // rootProject is for some reason set to `D:\Projekte\Minecraft Modding\Naturally-Trimmed\versions\1.20.1-forge\src\main\resources\`
    // Since everything else works fine, just do some relative pathing to the actual access transformer
    setAccessTransformer("../../../../../src/main/resources/META-INF/accesstransformer.cfg")

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
    implementation(minecraft.dependency("net.minecraftforge:forge:${property("deps.forge")}"))

    // YACL
    implementation("dev.isxander:yet-another-config-lib:${property("deps.yacl")}-forge") {
        isTransitive = false
    }

    // Quilt Parser
    implementation("org.quiltmc.parsers:json:${property("deps.quilt_parser")}")
    "jarJar"("org.quiltmc.parsers:json:${property("deps.quilt_parser")}")
    implementation("org.quiltmc.parsers:gson:${property("deps.quilt_parser")}")
    "jarJar"("org.quiltmc.parsers:gson:${property("deps.quilt_parser")}")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
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
        put("forge_constraint", property("deps.forge_constraint"))
    }

    props.forEach(inputs::property)

    filesMatching("META-INF/mods.toml") { expand(props) }
    exclude("fabric.mod.json", "META-INF/neoforge.mods.toml")
}

publishMods {
    displayName = "${property("mod.name")} ${property("mod.version")} for Forge $sc.current.version"
    file = tasks.jar.map { it.archiveFile.get() }
    version = property("mod.version").toString()
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
