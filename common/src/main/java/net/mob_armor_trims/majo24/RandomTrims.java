package net.mob_armor_trims.majo24;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.armortrim.*;

import java.util.List;

public class RandomTrims {
    public static void applyTrims(RegistryAccess registryAccess, RandomSource random, Iterable<ItemStack> equippedArmor) {
        if (MobArmorTrims.configManager.getEnabledSystem().equals("random Trims")) {
            runRandomTrimsSystem(registryAccess, random, equippedArmor);
        }
        else {
            runCustomTrimsSystem(equippedArmor, random, registryAccess);
        }
    }

    public static void runRandomTrimsSystem(RegistryAccess registryAccess, RandomSource random, Iterable<ItemStack> equippedArmor) {
        if (MobArmorTrims.configManager.getNoTrimsChance() > random.nextInt(100)) {return;}

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
        for (ItemStack armor : equippedArmor) {
            if (armor.getItem() == Items.AIR) {continue;}
            applyCustomTrim(MobArmorTrims.configManager.getCustomTrim(random), registryAccess, armor);
        }
    }

    private static ArmorTrim applyRandomTrim(RegistryAccess registryAccess, Registry<TrimMaterial> materialRegistry, Registry<TrimPattern> patternRegistry, RandomSource random, ItemStack armor, ArmorTrim lastTrim) {
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

        ArmorTrim.setTrim(registryAccess, armor, armorTrim);
        return armorTrim;
    }

    private static void applyCustomTrim(List<String> trimSNBT, RegistryAccess registryAccess, ItemStack armor) {
        ArmorTrim armorTrim;

        try {
            armorTrim = new ArmorTrim(
                TrimMaterials.getFromIngredient(registryAccess, ItemStack.of(NbtUtils.snbtToStructure(trimSNBT.get(0)))).get(),
                TrimPatterns.getFromTemplate(registryAccess, ItemStack.of(NbtUtils.snbtToStructure(trimSNBT.get(1)))).get()
            );
        } catch (Exception e) {
            MobArmorTrims.LOGGER.error("Invalid SNBT: {} {} {}", trimSNBT.get(0), trimSNBT.get(1), e);
            return;
        }

        ArmorTrim.setTrim(registryAccess, armor, armorTrim);
    }
}
