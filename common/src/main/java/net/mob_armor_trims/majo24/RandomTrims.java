package net.mob_armor_trims.majo24;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.armortrim.*;
import net.mob_armor_trims.majo24.config.Config;

public class RandomTrims {
    private RandomTrims() {}

    public static void applyTrims(RegistryAccess registryAccess, RandomSource random, Iterable<ItemStack> equippedArmor) {
        if (MobArmorTrims.configManager.getNoTrimsChance() > random.nextInt(100)) {return;}

        if (MobArmorTrims.configManager.getEnabledSystem() == Config.TrimSystem.RANDOM_TRIMS) {
            runRandomTrimsSystem(registryAccess, random, equippedArmor);
        }
        else if (MobArmorTrims.configManager.getEnabledSystem() == Config.TrimSystem.CUSTOM_TRIMS) {
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
            if (MobArmorTrims.configManager.getTrimChance() < random.nextInt(100)) {continue;}
            if (armor.getItem() != Items.AIR) {
                lastTrim = applyRandomTrim(materialRegistry, patternRegistry, random, armor, lastTrim);
            }

            // Stacked Armor Trims compatibility
            if (MobArmorTrims.isStackedArmorTrimsLoaded) {
                int appliedArmorTrims = 0;
                while ((MobArmorTrims.configManager.getStackedTrimChance() >= random.nextInt(100)) && (appliedArmorTrims < MobArmorTrims.configManager.getMaxStackedTrims())) {
                    applyRandomTrim(materialRegistry, patternRegistry, random, armor, null);
                    appliedArmorTrims++;
                }
            }
        }
    }

    public static void runCustomTrimsSystem(Iterable<ItemStack> equippedArmor, RandomSource random, RegistryAccess registryAccess) {
        Config.CustomTrim customTrim = MobArmorTrims.configManager.getCustomTrim(random);
        if (customTrim == null) {return;}

        ArmorTrim armorTrim = customTrim.getTrim(registryAccess);
        if (armorTrim == null) {return;}

        for (ItemStack armor : equippedArmor) {
            if (armor.getItem() != Items.AIR) {
                armor.applyComponents(DataComponentPatch.builder().set(DataComponents.TRIM, armorTrim).build());
            }
        }
    }

    public static ArmorTrim applyRandomTrim(Registry<TrimMaterial> materialRegistry, Registry<TrimPattern> patternRegistry, RandomSource random, ItemStack armor, ArmorTrim lastTrim) {
        Holder.Reference<TrimMaterial> randomTrimMaterial = materialRegistry.getRandom(random).get();
        Holder.Reference<TrimPattern> randomTrimPattern = patternRegistry.getRandom(random).get();
        ArmorTrim armorTrim = new ArmorTrim(randomTrimMaterial, randomTrimPattern);

        if (lastTrim != null) {
            if (MobArmorTrims.configManager.getSimilarTrimChance() >= random.nextInt(100)) {
                armorTrim = new ArmorTrim(lastTrim.material(), armorTrim.pattern());
            }
            if (MobArmorTrims.configManager.getSimilarTrimChance() >= random.nextInt(100)) {
                armorTrim = new ArmorTrim(armorTrim.material(), lastTrim.pattern());
            }
        }

        armor.applyComponents(DataComponentPatch.builder().set(DataComponents.TRIM, armorTrim).build());
        return armorTrim;
    }
}
