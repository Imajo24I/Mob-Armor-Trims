package net.mob_armor_trims.majo24;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimPattern;

public class RandomTrims {
    public static void randomlyApplyRandomTrims(RegistryAccess registryAccess, RandomSource random, Iterable<ItemStack> equippedArmor) {
        if (MobArmorTrims.configManager.getNoTrimsChance() > random.nextInt(100)) {return;}

        ResourceKey<Registry<TrimMaterial>> materialKey = Registries.TRIM_MATERIAL;
        Registry<TrimMaterial> materialRegistry = registryAccess.registryOrThrow(materialKey);
        ResourceKey<Registry<TrimPattern>> patternKey = Registries.TRIM_PATTERN;
        Registry<TrimPattern> patternRegistry = registryAccess.registryOrThrow(patternKey);

        DataComponentType<ArmorTrim> trimComponentType = DataComponents.TRIM;

        ArmorTrim lastTrim = null;

        for (ItemStack armor : equippedArmor) {
            if (MobArmorTrims.configManager.getTrimChance() < random.nextInt(100)) {continue;}
            if (armor.getItem() != Items.AIR) {
                lastTrim = applyRandomTrim(trimComponentType, materialRegistry, patternRegistry, random, armor, lastTrim);
            }

            // Stacked Armor Trims compatibility
            if (MobArmorTrims.isStackedArmorTrimsLoaded) {
                int appliedArmorTrims = 0;
                while ((MobArmorTrims.configManager.getStackedTrimChance() >= random.nextInt(100)) && (appliedArmorTrims < MobArmorTrims.configManager.getMaxStackedTrims())) {
                    applyRandomTrim(trimComponentType, materialRegistry, patternRegistry, random, armor, null);
                    appliedArmorTrims++;
                }
            }
        }
    }

    public static ArmorTrim applyRandomTrim(DataComponentType<ArmorTrim> trimComponentType, Registry<TrimMaterial> materialRegistry, Registry<TrimPattern> patternRegistry, RandomSource random, ItemStack armor, ArmorTrim lastTrim) {
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

        armor.applyComponents(DataComponentPatch.builder().set(trimComponentType, armorTrim).build());
        return armorTrim;
    }
}
