package net.majo24.mob_armor_trims;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.armortrim.*;
import net.majo24.mob_armor_trims.config.Config.CustomTrim;
import net.majo24.mob_armor_trims.config.Config.TrimSystem;

/*? >=1.20.5 {*/
/*import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
*//*?}*/

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
        if (MobArmorTrims.configManager.getApplyToEntireArmor()) {
            CustomTrim customTrim = MobArmorTrims.configManager.getCustomTrim(random);
            if (customTrim == null) {
                return;
            }

            ArmorTrim armorTrim;
            try {
                armorTrim = MobArmorTrims.configManager.getOrCreateCachedCustomTrim(customTrim.material(), customTrim.pattern(), registryAccess);
            } catch (IllegalStateException e) {
                MobArmorTrims.LOGGER.error("Failed to create armor trim. Please ensure this is a valid custom trim: {} - {}", customTrim.material(), customTrim.pattern(), e);
                return;
            }

            for (ItemStack armor : equippedArmor) {
                if (armor.getItem() != Items.AIR) {
                    applyTrim(armor, armorTrim, registryAccess);
                }
            }
        } else {
            for (ItemStack armor : equippedArmor) {
                if (armor.getItem() == Items.AIR) {continue;}

                CustomTrim customTrim = MobArmorTrims.configManager.getCustomTrim(random);
                if (customTrim == null) {continue;}

                ArmorTrim armorTrim = MobArmorTrims.configManager.getOrCreateCachedCustomTrim(customTrim.material(), customTrim.pattern(), registryAccess);
                if (armorTrim == null) {continue;}

                applyTrim(armor, armorTrim, registryAccess);
            }
        }
    }

    // TODO: only make registryAccess a parameter for 1.20.4 and lower
    public static void applyTrim(ItemStack armor, ArmorTrim armorTrim, RegistryAccess registryAccess) {
        /*? >=1.20.5 {*/
        /*armor.applyComponents(DataComponentPatch.builder().set(DataComponents.TRIM, armorTrim).build());
        *//*?} else {*/
        ArmorTrim.setTrim(registryAccess, armor, armorTrim);
         /*?}*/
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

        applyTrim(armor, armorTrim, registryAccess);
        return armorTrim;
    }
}