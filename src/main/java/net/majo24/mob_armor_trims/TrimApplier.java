package net.majo24.mob_armor_trims;

import net.majo24.mob_armor_trims.trim_combinations_system.CustomTrim;
import net.majo24.mob_armor_trims.trim_combinations_system.TrimCombination;
import net.majo24.mob_armor_trims.config.Config.TrimSystems;

import static net.majo24.mob_armor_trims.MobArmorTrims.configManager;

import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import com.mojang.datafixers.util.Pair;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

//? >=1.21.2 {
import net.minecraft.world.item.equipment.trim.*;
//?} else {
/*import net.minecraft.world.item.armortrim.*;
 *///?}

//? >=1.20.5 {
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
//?}

public class TrimApplier {
    private TrimApplier() {
    }

    /**
     * Runs the selected system on the given armor
     *
     * @param armor              Armor to apply the trims on
     * @param usePiglinMaterials If true and random trims system is active, only netherite or gold will be used as the trim material
     */
    public static void applyTrims(RegistryAccess registryAccess, RandomSource random, List<ItemStack> armor, boolean usePiglinMaterials) {
        if (configManager.getConfig().general.noTrimsChance.getValue() > random.nextInt(100)) {
            return;
        }

        TrimSystems enabledSystem = configManager.getConfig().general.enabledSystem.getValue();

        if (enabledSystem == TrimSystems.RANDOM_TRIMS) {
            runRandomTrimsSystem(registryAccess, random, armor, usePiglinMaterials);
        } else if (enabledSystem == TrimSystems.CUSTOM_TRIM_COMBINATIONS) {
            runCustomTrimCombinationsSystem(armor, registryAccess);
        }
    }

    public static void runRandomTrimsSystem(RegistryAccess registryAccess, RandomSource random, Iterable<ItemStack> armor, boolean usePiglinMaterials) {
        Pair<Registry<TrimMaterial>, Registry<TrimPattern>> registries = getTrimRegistries(registryAccess);
        Registry<TrimMaterial> materialRegistry = registries.getFirst();
        Registry<TrimPattern> patternRegistry = registries.getSecond();

        int trimChance = configManager.getConfig().randomTrims.trimChance.getValue();
        ArmorTrim lastTrim = null;

        for (ItemStack armorPiece : armor) {
            if (trimChance >= random.nextInt(100)) {
                lastTrim = applyRandomTrim(registryAccess, materialRegistry, patternRegistry, random, armorPiece, lastTrim, usePiglinMaterials);
            }
        }
    }

    public static Pair<Registry<TrimMaterial>, Registry<TrimPattern>> getTrimRegistries(RegistryAccess registryAccess) {
        ResourceKey<Registry<TrimMaterial>> materialKey = Registries.TRIM_MATERIAL;
        ResourceKey<Registry<TrimPattern>> patternKey = Registries.TRIM_PATTERN;

        //? >=1.21.2 {
        Registry<TrimMaterial> materialRegistry = registryAccess.lookupOrThrow(materialKey);
        Registry<TrimPattern> patternRegistry = registryAccess.lookupOrThrow(patternKey);
        //?} else {
        /*Registry<TrimMaterial> materialRegistry = registryAccess.registryOrThrow(materialKey);
        Registry<TrimPattern> patternRegistry = registryAccess.registryOrThrow(patternKey);
        *///?}

        return new Pair<>(materialRegistry, patternRegistry);
    }

    public static void runCustomTrimCombinationsSystem(List<ItemStack> armor, RegistryAccess registryAccess) {
        String requiredMaterial = getArmorMaterial(armor.getFirst());

        if (requiredMaterial.isEmpty()) {
            return;
        }

        TrimCombination trimCombination = configManager.getConfig().getRandomTrimCombination(requiredMaterial);
        if (trimCombination == null) {
            return;
        }

        Iterator<ItemStack> armorIterator = armor.iterator();

        for (CustomTrim trim : trimCombination.trims().reversed()) {
            ItemStack armorPiece = armorIterator.next();
            ArmorTrim armorTrim = configManager.getConfig().getOrCreateCachedTrim(trim.material(), trim.pattern(), registryAccess);

            if (armorTrim != null) {
                applyTrim(armorPiece, armorTrim, registryAccess);
            }

        }
    }

    /**
     * Get the material of the given armor piece
     */
    private static String getArmorMaterial(ItemStack armorPiece) {
        for (String material : List.of("netherite", "diamond", "gold", "iron", "chain", "leather")) {
            if (armorPiece.toString().contains(material)) {
                return material;
            }
        }

        MobArmorTrims.LOGGER.error("Could not find armor material for {}", armorPiece);
        return "";
    }

    /**
     * Apply a trim on the armor
     *
     * @param armorPiece Armor to apply the trim on
     * @param armorTrim  Trim to apply on the armor
     */
    public static void applyTrim(ItemStack armorPiece, ArmorTrim armorTrim, RegistryAccess registryAccess) {
        //? >=1.20.5 {
        armorPiece.applyComponents(DataComponentPatch.builder().set(DataComponents.TRIM, armorTrim).build());
         //?} else {
        /*ArmorTrim.setTrim(registryAccess, armorPiece, armorTrim);
        *///?}
    }

    /**
     * Applies a random trim on the given armor piece. The random trim also takes referenceTrim and the blacklist into account.
     *
     * @param armorPiece    Armor piece to apply the trim on
     * @param referenceTrim The trim, the new random trim should take into account
     * @return null if all TrimPatterns inside the patternRegistry are blacklisted, otherwise the random trim which was used
     */
    @Nullable
    public static ArmorTrim applyRandomTrim(RegistryAccess registryAccess, Registry<TrimMaterial> materialRegistry, Registry<TrimPattern> patternRegistry, RandomSource random, ItemStack armorPiece, @Nullable ArmorTrim referenceTrim, boolean usePiglinMaterials) {
        List<Holder.Reference<TrimPattern>> trimPatterns = getAndFilterPatterns(patternRegistry);
        if (trimPatterns.isEmpty()) return null;

        Holder.Reference<TrimMaterial> randomTrimMaterial = materialRegistry.getRandom(random).orElseThrow();
        Holder.Reference<TrimPattern> randomTrimPattern = Util.getRandom(trimPatterns, random);
        ArmorTrim armorTrim = new ArmorTrim(randomTrimMaterial, randomTrimPattern);

        if (referenceTrim != null) {
            int similarTrimChance = configManager.getConfig().randomTrims.similarTrimChance.getValue();

            if (similarTrimChance >= random.nextInt(100)) {
                armorTrim = new ArmorTrim(referenceTrim.material(), armorTrim.pattern());
            }

            if (similarTrimChance >= random.nextInt(100)) {
                armorTrim = new ArmorTrim(armorTrim.material(), referenceTrim.pattern());
            }
        }

        if (usePiglinMaterials) {
            //? >=1.21.2 {
            Holder.Reference<TrimMaterial> trimMaterial = ((random.nextBoolean())
                    ? materialRegistry.get(TrimMaterials.NETHERITE)
                    : materialRegistry.get(TrimMaterials.GOLD)).orElseThrow();
            //?} else {
            /*Holder.Reference<TrimMaterial> trimMaterial = (random.nextBoolean())
                    ? materialRegistry.getHolderOrThrow(TrimMaterials.NETHERITE)
                    : materialRegistry.getHolderOrThrow(TrimMaterials.GOLD);
            *///?}

            armorTrim = new ArmorTrim(trimMaterial, armorTrim.pattern());
        }

        applyTrim(armorPiece, armorTrim, registryAccess);
        return armorTrim;
    }

    private static List<Holder.Reference<TrimPattern>> getAndFilterPatterns(Registry<TrimPattern> patternRegistry) {
        //? if >=1.21.2 {
        List<Holder.Reference<TrimPattern>> trimPatterns = new ArrayList<>(patternRegistry.listElements().toList());
        //?} else {
        /*List<Holder.Reference<TrimPattern>> trimPatterns = new ArrayList<>(patternRegistry.holders().toList());
        *///?}

        List<Pattern> patterns = configManager.getConfig().randomTrims.blacklist.getPatterns();

        trimPatterns.removeIf(trimPattern -> {
            String resourceLocation = trimPattern.key().location().toString();
            for (Pattern pattern : patterns) {
                if (pattern.matcher(resourceLocation).find()) {
                    return true;
                }
            }

            return false;
        });

        return trimPatterns;
    }

    /**
     * Apply a random trim to an item.
     * Should only be used when theres only item to trim.
     * Should not be used when theres multiple, related items that should be trimmed
     *
     * @param itemStack Item to apply the trim on
     */
    public static void applyRandomTrimToItem(ItemStack itemStack, RandomSource random, RegistryAccess registryAccess) {
        Pair<Registry<TrimMaterial>, Registry<TrimPattern>> registries = TrimApplier.getTrimRegistries(registryAccess);
        Registry<TrimMaterial> materialRegistry = registries.getFirst();
        Registry<TrimPattern> patternRegistry = registries.getSecond();

        TrimApplier.applyRandomTrim(registryAccess, materialRegistry, patternRegistry, random, itemStack, null, false);
    }
}