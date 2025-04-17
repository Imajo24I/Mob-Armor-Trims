package net.majo24.mob_armor_trims;

import com.mojang.datafixers.util.Pair;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
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

public class ToolTrimsCompat {
    public static final String TOOL_TRIMS_ID = "tooltrims";
    public static final TagKey<Item> TRIMMABLE_TOOL_TAG;

    static {
        ResourceLocation trimmableToolsResourceLocation = ResourceLocation.tryBuild(TOOL_TRIMS_ID, "trimmable_tools");
        assert trimmableToolsResourceLocation != null;

        TRIMMABLE_TOOL_TAG = TagKey.create(Registries.ITEM, trimmableToolsResourceLocation);
    }

    private ToolTrimsCompat() {
    }

    public static void toolTrimsCompat(LivingEntity entity) {
        ItemStack mainhand = entity.getMainHandItem();

        if (!mainhand.isEmpty() && mainhand.is(TRIMMABLE_TOOL_TAG)) {
            RegistryAccess registryAccess = entity.level().registryAccess();

            Pair<Registry<TrimMaterial>, Registry<TrimPattern>> registries = TrimApplier.getTrimRegistries(registryAccess);
            Registry<TrimMaterial> materialRegistry = registries.getFirst();
            Registry<TrimPattern> patternRegistry = registries.getSecond();

            ArmorTrim trim = new ArmorTrim(
                    materialRegistry.getRandom(entity.getRandom()).orElseThrow(),
                    getRandomToolPattern(patternRegistry, entity.getRandom())
            );

            TrimApplier.applyTrim(mainhand, trim, registryAccess);
        }
    }

    private static Holder.Reference<TrimPattern> getRandomToolPattern(Registry<TrimPattern> patternRegistry, RandomSource random) {
        List<Holder.Reference<TrimPattern>> patterns = TrimApplier.getPatterns(patternRegistry);
        patterns.removeIf(pattern -> !(pattern.key().location().getNamespace().equals(TOOL_TRIMS_ID)));

        return Util.getRandom(patterns, random);
    }
}
