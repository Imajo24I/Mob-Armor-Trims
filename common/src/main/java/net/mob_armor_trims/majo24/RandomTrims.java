package net.mob_armor_trims.majo24;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.armortrim.*;
import net.mob_armor_trims.majo24.config.CustomTrim;
import net.mob_armor_trims.majo24.config.TrimSystem;

public class RandomTrims {
    private RandomTrims() {}

    public static void applyTrims(RegistryAccess registryAccess, RandomSource random, Iterable<ItemStack> equippedArmor) {
        if (MobArmorTrims.configManager.getNoTrimsChance() > random.nextInt(100)) {return;}

        if (MobArmorTrims.configManager.getEnabledSystem() == TrimSystem.RANDOM_TRIMS) {
            runRandomTrimsSystem(registryAccess, random, equippedArmor);
        }
        else if (MobArmorTrims.configManager.getEnabledSystem() == TrimSystem.CUSTOM_TRIMS) {
            runCustomTrimsSystem(equippedArmor, random, registryAccess);
        }
    }

    public static void runRandomTrimsSystem(RegistryAccess registryAccess, RandomSource random, Iterable<ItemStack> equippedArmor) {
        ResourceKey<Registry<TrimMaterial>> materialKey = Registries.TRIM_MATERIAL;
        Registry<TrimMaterial> materialRegistry = registryAccess.registryOrThrow(materialKey);
        ResourceKey<Registry<TrimPattern>> patternKey = Registries.TRIM_PATTERN;
        Registry<TrimPattern> patternRegistry = registryAccess.registryOrThrow(patternKey);

        ArmorTrim lastTrim = null;

        for (ItemStack armor : equippedArmor) {
            if (MobArmorTrims.configManager.getTrimChance() < random.nextInt(100)) {
                continue;
            }
            if (armor.getItem() != Items.AIR) {
                lastTrim = applyRandomTrim(registryAccess, materialRegistry, patternRegistry, random, armor, lastTrim);
            }

            // Stacked Armor Trims compatibility
            if (MobArmorTrims.isStackedArmorTrimsLoaded) {
                int appliedArmorTrims = 0;
                while ((MobArmorTrims.configManager.getStackedTrimChance() >= random.nextInt(100)) && (appliedArmorTrims < MobArmorTrims.configManager.getMaxStackedTrims())) {
                    applyRandomTrim(registryAccess, materialRegistry, patternRegistry, random, armor, null);
                    appliedArmorTrims++;
                }
            }
        }
    }

    public static void runCustomTrimsSystem(Iterable<ItemStack> equippedArmor, RandomSource random, RegistryAccess registryAccess) {
        CustomTrim customTrim = MobArmorTrims.configManager.getCustomTrim(random);
        if (customTrim == null) {
            return;
        }
        String trimPatternStringified = customTrim.patternSNBT();
        if (!trimPatternStringified.contains("_armor_trim_smithing_template")) {
            trimPatternStringified += "_armor_trim_smithing_template";
        }

        Holder.Reference<TrimMaterial> trimMaterial;
        Holder.Reference<TrimPattern> trimPattern;

        try {
            trimMaterial = TrimMaterials.getFromIngredient(registryAccess, BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(customTrim.materialSNBT())).getDefaultInstance()).orElseThrow();
            trimPattern = TrimPatterns.getFromTemplate(registryAccess, BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(trimPatternStringified)).getDefaultInstance()).orElseThrow();
        } catch (Exception e) {
            MobArmorTrims.LOGGER.error("Failed to apply custom trim. Please ensure this is a valid custom trim: {}; {} - {}", customTrim.materialSNBT(), customTrim.patternSNBT(), e);
            return;
        }
        ArmorTrim armorTrim = new ArmorTrim(trimMaterial, trimPattern);


        for (ItemStack armor : equippedArmor) {
            if (armor.getItem() != Items.AIR) {
                ArmorTrim.setTrim(registryAccess, armor, armorTrim);
            }
        }
    }

    private static ArmorTrim applyRandomTrim(RegistryAccess registryAccess, Registry<TrimMaterial> materialRegistry, Registry<TrimPattern> patternRegistry, RandomSource random, ItemStack armor, ArmorTrim lastTrim) {
        Holder.Reference<TrimMaterial> randomTrimMaterial = materialRegistry.getRandom(random).orElseThrow();
        Holder.Reference<TrimPattern> randomTrimPattern = patternRegistry.getRandom(random).orElseThrow();
        ArmorTrim armorTrim = new ArmorTrim(randomTrimMaterial, randomTrimPattern);

        if (lastTrim != null) {
            if (MobArmorTrims.configManager.getSimilarTrimChance() >= random.nextInt(100)) {
                armorTrim = new ArmorTrim(lastTrim.material(), armorTrim.pattern());
            }
            if (MobArmorTrims.configManager.getSimilarTrimChance() >= random.nextInt(100)) {
                armorTrim = new ArmorTrim(armorTrim.material(), lastTrim.pattern());
            }
        }

        ArmorTrim.setTrim(registryAccess, armor, armorTrim);
        return armorTrim;
    }
}
