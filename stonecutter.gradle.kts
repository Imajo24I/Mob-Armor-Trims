import dev.kikugie.stonecutter.data.ParsedVersion

plugins {
    id("dev.kikugie.stonecutter")
    id("me.modmuss50.mod-publish-plugin") version "0.5.1"
    id("fabric-loom") version "1.15-SNAPSHOT" apply false
    id("net.neoforged.moddev") version "2.0.140" apply false
    id("net.minecraftforge.gradle") version "7.0.15" apply false
}
stonecutter active "1.21.11-fabric" /* [SC] DO NOT EDIT */

stonecutter parameters {
    var mcVersion = ParsedVersion(node.metadata.project.substringBefore("-"))
    var loader = node.metadata.project.substringAfterLast("-")

    constants {
        match(loader, "fabric", "neoforge", "forge")
        put("forgeLike", loader == "neoforge" || loader == "forge")
    }

    replacements {
        string {
            direction = mcVersion >= "1.21.2"
            replace("item.armortrim.*;", "item.equipment.trim.*;")
        }

        string {
            direction = mcVersion >= "1.21.11"
            replace("ResourceLocation", "Identifier")
        }

        string {
            direction = mcVersion >= "1.21.11"
            replace(".location()", ".identifier()")
        }

        string {
            direction = mcVersion >= "1.21.11"
            replace("minecraft.Util;", "minecraft.util.Util;")
        }

        for (movedClass in listOf("AbstractVillager", "VillagerDataHolder", "VillagerTrades")) {
            string {
                direction = mcVersion >= "1.21.11"
                replace("npc.${movedClass}", "npc.villager.${movedClass}")
            }
        }
    }
}
