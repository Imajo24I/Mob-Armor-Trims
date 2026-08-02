package net.majo24.naturally_trimmed.trim_application;

import net.majo24.naturally_trimmed.NaturallyTrimmed;
import net.minecraft.util.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.equipment.trim.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Objects;

import static net.majo24.naturally_trimmed.config.Config.INSTANCE;

/**
 * Contains code for supporting both the Tool Trims mod and the Trimmable Tools mod
 */
public class ToolTrimsCompat {
    public static final String TRIMMABLE_TOOLS_ID = "trimmable_tools";
    public static final String TOOL_TRIMS_ID = "tooltrims";
    public static final TagKey<Item> TRIMMABLE_TOOLS_TAG = TagKey.create(Registries.ITEM, Objects.requireNonNull(Identifier.tryBuild(TOOL_TRIMS_ID, TRIMMABLE_TOOLS_ID)));

    private ToolTrimsCompat() {
    }

    /**
     * Applies an armor trim to the given tool using tooltrims or trimmable_tools.
     * If neither is present, no trim will be applied.
     * The trim consists of a random material and a random, tools compatible armor trim
     */
    public static void applyTrimToTool(ItemStack itemStack, RegistryAccess registryAccess, RandomSource random) {
        if (!itemStack.isEmpty() && (itemStack.is(TRIMMABLE_TOOLS_TAG) || itemStack.is(ItemTags.TRIMMABLE_ARMOR))) {
            TrimApplier.applyTrim(itemStack, getToolTrim(registryAccess, random));
        }
    }

    private static ArmorTrim getToolTrim(RegistryAccess registryAccess, RandomSource random) {
        List<Holder.Reference<TrimMaterial>> materials = TrimApplier.getTrimMaterials(registryAccess);
        List<Holder.Reference<TrimPattern>> patterns = TrimApplier.getTrimPatterns(registryAccess);

        if (NaturallyTrimmed.isModLoaded(TRIMMABLE_TOOLS_ID)) {
            // Trimmable Tools only supports trimming tools with minecraft's patterns,
            // meaning all other patterns shouldn't be used
            patterns.removeIf(pattern -> !(pattern.key().identifier().getNamespace().equals("minecraft")));

            if (INSTANCE.trimFiltering.vanillaOnly) {
                materials.removeIf(material -> !material.key().identifier().getNamespace().equals("minecraft"));
            }
        } else if (NaturallyTrimmed.isModLoaded(TOOL_TRIMS_ID)) {
            // Tool Trims only supports trimming tools with its custom patterns,
            // meaning all other patterns shouldn't be used
            patterns.removeIf(pattern -> !(pattern.key().identifier().getNamespace().equals(TOOL_TRIMS_ID)));

            // Remove every non-vanilla trim material,
            // since two modded trim parts will likely result in a missing-texture texture
            materials.removeIf(material -> !material.key().identifier().getNamespace().equals("minecraft"));
        }

        return new ArmorTrim(Util.getRandom(materials, random), Util.getRandom(patterns, random));
    }
}
