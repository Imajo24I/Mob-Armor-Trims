package net.majo24.mob_armor_trims;

import com.mojang.datafixers.util.Pair;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

//? >=1.21.2 {
import net.minecraft.world.item.equipment.trim.*;
//?} else {
/*import net.minecraft.world.item.armortrim.*;
 *///?}

import java.util.List;

/**
 * Contains code for supporting both the Tool Trims mod and the Trimmable Tools mod
 */
public class ToolTrimsCompat {
    public static final String TOOL_TRIMS_ID = "tooltrims";
    public static final TagKey<Item> TRIMMABLE_TOOL_TAG;

    public static final String TRIMMABLE_TOOLS_ID = "trimmable_tools";

    static {
        ResourceLocation trimmableToolsResourceLocation = ResourceLocation.tryBuild(TOOL_TRIMS_ID, "trimmable_tools");
        assert trimmableToolsResourceLocation != null;

        TRIMMABLE_TOOL_TAG = TagKey.create(Registries.ITEM, trimmableToolsResourceLocation);
    }

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

        // Tool Trims mod only supports trimming tools with its custom patterns,
        // meaning other patterns should be removed
        if (MobArmorTrims.isModLoaded(TOOL_TRIMS_ID)) {
            patterns.removeIf(pattern -> !(pattern.key().location().getNamespace().equals(TOOL_TRIMS_ID)));
        }

        return Util.getRandom(patterns, random);
    }
}
