package net.majo24.mob_armor_trims;

import net.majo24.mob_armor_trims.config.custom_trim_combinations.CustomTrim;
import net.majo24.mob_armor_trims.config.custom_trim_combinations.TrimCombination;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.armortrim.*;
import net.majo24.mob_armor_trims.config.Config.TrimSystems;

import java.util.Iterator;
import java.util.Objects;

//? >=1.20.5 {
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
//?}

public class TrimApplier {
    private TrimApplier() {
    }

    public static void applyTrims(RegistryAccess registryAccess, RandomSource random, Iterable<ItemStack> equippedArmor) {
        if (MobArmorTrims.configManager.getNoTrimsChance() > random.nextInt(100)) {
            return;
        }

        if (MobArmorTrims.configManager.getEnabledSystem() == TrimSystems.RANDOM_TRIMS) {
            runRandomTrimsSystem(registryAccess, random, equippedArmor);
        } else if (MobArmorTrims.configManager.getEnabledSystem() == TrimSystems.CUSTOM_TRIM_COMBINATIONS) {
            runCustomTrimsSystem(equippedArmor, registryAccess);
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

    public static void runCustomTrimsSystem(Iterable<ItemStack> equippedArmor, RegistryAccess registryAccess) {
        String requiredMaterial = "";

        for (ItemStack armor : equippedArmor) {
            if (armor.getItem() != Items.AIR) {
                requiredMaterial = getArmorMaterial(armor);
                break;
            }
        }
        if (Objects.equals(requiredMaterial, "")) {
            return;
        }

        TrimCombination trimCombination = MobArmorTrims.configManager.getTrimCombination(requiredMaterial);
        if (trimCombination == null) {
            return;
        }

        Iterator<ItemStack> armorIterable = equippedArmor.iterator();

        for (CustomTrim trim : trimCombination.trims()) {
            ItemStack armorPiece = armorIterable.next();
            if (armorPiece.getItem() == Items.AIR) {
                continue;
            }

            ArmorTrim armorTrim = MobArmorTrims.configManager.getOrCreateCachedTrim(trim.material(), trim.pattern(), registryAccess);
            if (armorTrim == null) {
                continue;
            }

            applyTrim(armorPiece, armorTrim, registryAccess);
        }
    }

    private static String getArmorMaterial(ItemStack armor) {
        if (armor.toString().contains("netherite")) {
            return "netherite";
        } else if (armor.toString().contains("diamond")) {
            return "diamond";
        } else if (armor.toString().contains("gold")) {
            return "gold";
        } else if (armor.toString().contains("iron")) {
            return "iron";
        } else if (armor.toString().contains("chain")) {
            return "chain";
        } else if (armor.toString().contains("leather")) {
            return "leather";
        } else {
            MobArmorTrims.LOGGER.error("Could not find armor material for {}", armor);
            return "";
        }
    }

    public static void applyTrim(ItemStack armor, ArmorTrim armorTrim, RegistryAccess registryAccess) {
        //? >=1.20.5 {
        armor.applyComponents(DataComponentPatch.builder().set(DataComponents.TRIM, armorTrim).build());
        //?} else {
        /*ArmorTrim.setTrim(registryAccess, armor, armorTrim);
         *///?}
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