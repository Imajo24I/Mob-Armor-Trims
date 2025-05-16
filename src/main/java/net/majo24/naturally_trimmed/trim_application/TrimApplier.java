package net.majo24.naturally_trimmed.trim_application;

import net.majo24.naturally_trimmed.NaturallyTrimmed;
import net.majo24.naturally_trimmed.config.TrimMobsSubConfig;
import net.majo24.naturally_trimmed.trim_combination.TrimKey;
import net.majo24.naturally_trimmed.trim_combination.TrimCombination;

import static net.majo24.naturally_trimmed.config.Config.CONFIG_MANAGER;

import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.equipment.trim.*;
import net.minecraft.world.item.ItemStack;
import com.mojang.datafixers.util.Pair;

//? if >=1.21.5
import net.minecraft.world.entity.EquipmentSlotGroup;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;


//? >=1.20.5 {
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
//?}

public class TrimApplier {
    private TrimApplier() {
    }

    public static void trimEquipment(LivingEntity entity) {
        if (!CONFIG_MANAGER.instance().enableTrimMobs) return;

        //? if >=1.21.5 {
        List<ItemStack> armor = EquipmentSlotGroup.ARMOR.slots().stream()
                .map(entity::getItemBySlot)
                .filter(armorPiece -> !armorPiece.isEmpty() && armorPiece.is(ItemTags.TRIMMABLE_ARMOR))
                .toList();
        //?} else {
        /*List<ItemStack> armor = java.util.stream.StreamSupport.stream(entity.getArmorSlots().spliterator(), false)
                .filter(armorPiece -> !armorPiece.isEmpty())
                .toList();
        *///?}

        RandomSource random = entity.getRandom();

        if (CONFIG_MANAGER.instance().trimMobs.noTrimsChance < random.nextInt(100)) {
            if (!armor.isEmpty()) {
                applyTrims(entity.level().registryAccess(), random, armor);
            }

            if ((NaturallyTrimmed.isModLoaded(ToolTrimsCompat.TOOL_TRIMS_ID) || NaturallyTrimmed.isModLoaded(ToolTrimsCompat.TRIMMABLE_TOOLS_ID))
                    && CONFIG_MANAGER.instance().trimMobs.randomTrims.trimChance >= random.nextInt(100)) {
                ToolTrimsCompat.toolTrimsCompat(entity.getMainHandItem(), entity.level().registryAccess(), entity.getRandom());
            }
        }
    }

    /**
     * Runs the selected trim system on the given armor
     */
    public static void applyTrims(RegistryAccess registryAccess, RandomSource random, List<ItemStack> armor) {
        TrimMobsSubConfig.TrimSystem enabledSystem = CONFIG_MANAGER.instance().trimMobs.trimSystem;

        if (enabledSystem == TrimMobsSubConfig.TrimSystem.RANDOM_TRIMS) {
            runRandomTrimsSystem(registryAccess, random, armor);
        } else {
            runCustomTrimCombinationsSystem(armor, registryAccess);
        }
    }

    public static void runRandomTrimsSystem(RegistryAccess registryAccess, RandomSource random, Iterable<ItemStack> armor) {
        Pair<Registry<TrimMaterial>, Registry<TrimPattern>> registries = getTrimRegistries(registryAccess);
        Registry<TrimMaterial> materialRegistry = registries.getFirst();
        Registry<TrimPattern> patternRegistry = registries.getSecond();

        ArmorTrim lastTrim = null;

        for (ItemStack armorPiece : armor) {
            if (CONFIG_MANAGER.instance().trimMobs.randomTrims.trimChance >= random.nextInt(100)) {
                lastTrim = applyRandomTrim(registryAccess, materialRegistry, patternRegistry, random, armorPiece, lastTrim);
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
        if (requiredMaterial == null) return;

        TrimCombination trimCombination = TrimCombination.getRandomTrimCombination(requiredMaterial);
        if (trimCombination == null) return;

        Iterator<ItemStack> armorIterator = armor.iterator();

        for (TrimKey trim : trimCombination.trims().reversed()) {
            ItemStack armorPiece = armorIterator.next();
            ArmorTrim armorTrim = TrimCombination.getOrCreateCachedTrim(trim.material(), trim.pattern(), registryAccess);

            if (armorTrim != null) {
                applyTrim(armorPiece, armorTrim, registryAccess);
            }

        }
    }
    /**
     * Get the material of the given armor piece
     */
    @Nullable
    private static String getArmorMaterial(ItemStack armorPiece) {
        for (String material : List.of("netherite", "diamond", "gold", "iron", "chainmail", "leather")) {
            if (armorPiece.toString().contains(material)) {
                return material;
            }
        }

        NaturallyTrimmed.LOGGER.error("Could not find armor material for {}", armorPiece);
        return null;
    }

    /**
     * Applies the armor trim onto the itemStack
     *
     */
    public static void applyTrim(ItemStack itemStack, ArmorTrim armorTrim, RegistryAccess registryAccess) {
        //? >=1.20.5 {
        itemStack.applyComponents(DataComponentPatch.builder().set(DataComponents.TRIM, armorTrim).build());
         //?} else {
        /*ArmorTrim.setTrim(registryAccess, itemStack, armorTrim);
        *///?}
    }

    /** Applies a random armor trim on the given itemStack. The random trim also considers referenceTrim and the blacklist. */
    @Nullable
    public static ArmorTrim applyRandomTrim(RegistryAccess registryAccess, Registry<TrimMaterial> materialRegistry, Registry<TrimPattern> patternRegistry, RandomSource random, ItemStack itemStack, @Nullable ArmorTrim referenceTrim) {
        List<Holder.Reference<TrimPattern>> trimPatterns = getAndFilterPatterns(patternRegistry);
        if (trimPatterns.isEmpty()) return null;

        Holder.Reference<TrimMaterial> randomTrimMaterial = materialRegistry.getRandom(random).orElseThrow();
        Holder.Reference<TrimPattern> randomTrimPattern = Util.getRandom(trimPatterns, random);
        ArmorTrim armorTrim = new ArmorTrim(randomTrimMaterial, randomTrimPattern);

        if (referenceTrim != null) {
            int similarTrimChance = CONFIG_MANAGER.instance().trimMobs.randomTrims.similarTrimChance;

            if (similarTrimChance >= random.nextInt(100)) {
                armorTrim = new ArmorTrim(referenceTrim.material(), armorTrim.pattern());
            }

            if (similarTrimChance >= random.nextInt(100)) {
                armorTrim = new ArmorTrim(armorTrim.material(), referenceTrim.pattern());
            }
        }

        applyTrim(itemStack, armorTrim, registryAccess);
        return armorTrim;
    }

    private static List<Holder.Reference<TrimPattern>> getAndFilterPatterns(Registry<TrimPattern> patternRegistry) {
        List<Holder.Reference<TrimPattern>> trimPatterns = getPatterns(patternRegistry);

        List<Pattern> patterns = CONFIG_MANAGER.instance().blacklist;

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

    public static List<Holder.Reference<TrimPattern>> getPatterns(Registry<TrimPattern> patternRegistry) {
        //? if >=1.21.2 {
        return new ArrayList<>(patternRegistry.listElements().toList());
        //?} else {
        /*return new ArrayList<>(patternRegistry.holders().toList());
         *///?}

    }

    /**
     * Apply a random trim to an item.
     * Should only be used when theres only item to trim.
     * Should not be used when theres multiple, related items that should be trimmed
     *
     * @param itemStack Item to apply the trim on
     */
    public static void applyRandomTrimToItem(ItemStack itemStack, RegistryAccess registryAccess, RandomSource random) {
        Pair<Registry<TrimMaterial>, Registry<TrimPattern>> registries = TrimApplier.getTrimRegistries(registryAccess);
        Registry<TrimMaterial> materialRegistry = registries.getFirst();
        Registry<TrimPattern> patternRegistry = registries.getSecond();

        TrimApplier.applyRandomTrim(registryAccess, materialRegistry, patternRegistry, random, itemStack, null);
    }
}