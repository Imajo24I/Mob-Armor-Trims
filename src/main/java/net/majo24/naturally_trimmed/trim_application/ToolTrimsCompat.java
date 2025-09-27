package net.majo24.naturally_trimmed.trim_application;

import com.mojang.datafixers.util.Pair;
import net.majo24.naturally_trimmed.NaturallyTrimmed;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.equipment.trim.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Objects;

/**
 * Contains code for supporting both the Tool Trims mod and the Trimmable Tools mod
 */
public class ToolTrimsCompat {
    public static final String TRIMMABLE_TOOLS_ID = "trimmable_tools";

    public static final String TOOL_TRIMS_ID = "tooltrims";
    public static final TagKey<Item> TRIMMABLE_TOOL_TAG = TagKey.create(Registries.ITEM, Objects.requireNonNull(ResourceLocation.tryBuild(TOOL_TRIMS_ID, TRIMMABLE_TOOLS_ID)));

    private ToolTrimsCompat() {
    }

    public static void toolTrimsCompat(ItemStack itemStack, RegistryAccess registryAccess, RandomSource random) {
        if (!itemStack.isEmpty() && (itemStack.is(TRIMMABLE_TOOL_TAG) || itemStack.is(ItemTags.TRIMMABLE_ARMOR))) {
            Pair<Registry<TrimMaterial>, Registry<TrimPattern>> registries = TrimApplier.getTrimRegistries(registryAccess);
            Registry<TrimMaterial> materialRegistry = registries.getFirst();
            Registry<TrimPattern> patternRegistry = registries.getSecond();

            ArmorTrim trim = new ArmorTrim(
                    materialRegistry.getRandom(random).orElseThrow(),
                    getRandomToolPattern(patternRegistry, random)
            );

            TrimApplier.applyTrim(itemStack, trim, registryAccess);
        }
    }

    private static Holder.Reference<TrimPattern> getRandomToolPattern(Registry<TrimPattern> patternRegistry, RandomSource random) {
        List<Holder.Reference<TrimPattern>> patterns = TrimApplier.getPatterns(patternRegistry);

        if (NaturallyTrimmed.isModLoaded(TRIMMABLE_TOOLS_ID)) {
            // Trimmable Tools only supports trimming tools with minecraft's patterns,
            // meaning all other patterns should be removed
            patterns.removeIf(pattern -> !(pattern.key().location().getNamespace().equals("minecraft")));
        } else if (NaturallyTrimmed.isModLoaded(TOOL_TRIMS_ID)) {
            // Tool Trims only supports trimming tools with its custom patterns,
            // meaning all other patterns should be removed
            patterns.removeIf(pattern -> !(pattern.key().location().getNamespace().equals(TOOL_TRIMS_ID)));
        }

        return Util.getRandom(patterns, random);
    }
}
