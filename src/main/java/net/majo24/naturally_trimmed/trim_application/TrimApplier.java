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
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.equipment.trim.*;
import net.minecraft.world.item.ItemStack;
import com.mojang.datafixers.util.Pair;

//? >=1.21.5
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

    /**
     * Applies the given armor trim onto the given itemStack
     */
    public static void applyTrim(ItemStack itemStack, ArmorTrim armorTrim, RegistryAccess registryAccess) {
        //? >=1.20.5 {
        itemStack.applyComponents(DataComponentPatch.builder().set(DataComponents.TRIM, armorTrim).build());
        //?} else {
        /*ArmorTrim.setTrim(registryAccess, itemStack, armorTrim);
         *///?}
    }

    /**
     * Returns a random non-blacklisted armor trim. Also ensures at least one of the two trim parts is non-modded
     */
    @Nullable
    public static ArmorTrim getRandomTrim(Registry<TrimMaterial> materialRegistry, Registry<TrimPattern> patternRegistry, RandomSource random) {
        List<Holder.Reference<TrimPattern>> trimPatterns = getAndFilterPatterns(patternRegistry);
        if (trimPatterns.isEmpty()) return null;

        Holder.Reference<TrimMaterial> trimMaterial;
        Holder.Reference<TrimPattern> trimPattern;

        // Ensure at least one of the two trim parts is non-modded
        // If both trim parts are modded, they will most times result in a missing-texture texture
        do {
            trimPattern = Util.getRandom(trimPatterns, random);
            trimMaterial = materialRegistry.getRandom(random).orElseThrow();
        } while (!trimMaterial.key().location().getNamespace().equals("minecraft") && !trimPattern.key().location().getNamespace().equals("minecraft"));

        return new ArmorTrim(trimMaterial, trimPattern);
    }

    /**
     * Applies a random non-blacklisted armor trim onto the given item.
     * Also ensures at least one of the two trim parts is non-modded
     */
    public static void applyRandomTrim(ItemStack itemStack, RegistryAccess registryAccess, RandomSource random) {
        Pair<Registry<TrimMaterial>, Registry<TrimPattern>> registries = TrimApplier.getTrimRegistries(registryAccess);
        Registry<TrimMaterial> materialRegistry = registries.getFirst();
        Registry<TrimPattern> patternRegistry = registries.getSecond();

        ArmorTrim trim = getRandomTrim(materialRegistry, patternRegistry, random);
        applyTrim(itemStack, trim, registryAccess);
    }

    /**
     * Runs the selected Trim System on the armor of the entity. Also applies trims to the entity's equipment, if possible.
     */
    public static void trimEquipment(LivingEntity entity) {
        if (!CONFIG_MANAGER.instance().enableTrimMobs) return;

        //? if >=1.21.5 {
        List<ItemStack> armor = EquipmentSlotGroup.ARMOR.slots().stream()
                .map(entity::getItemBySlot)
                .filter(armorPiece -> !armorPiece.isEmpty() && armorPiece.is(ItemTags.TRIMMABLE_ARMOR))
                .toList();
        //?} else {
        /*List<ItemStack> armor = java.util.stream.StreamSupport.stream(entity.getArmorSlots().spliterator(), false)
                .filter(armorPiece -> !armorPiece.isEmpty() && armorPiece.is(ItemTags.TRIMMABLE_ARMOR))
                .toList();
        *///?}

        RandomSource random = entity.getRandom();
        RegistryAccess registryAccess = entity.level().registryAccess();

        if (CONFIG_MANAGER.instance().trimMobs.noTrimsChance < random.nextInt(100)) {
            // Run selected trimming system
            if (!armor.isEmpty()) {
                TrimMobsSubConfig.TrimSystem enabledSystem = CONFIG_MANAGER.instance().trimMobs.trimSystem;

                if (enabledSystem == TrimMobsSubConfig.TrimSystem.RANDOM_TRIMS) {
                    runRandomTrimsSystem(armor, registryAccess, random);
                } else {
                    runCustomTrimCombinationsSystem(armor, registryAccess, random);
                }
            }

            // Run tool trims compatibility code
            if ((NaturallyTrimmed.isModLoaded(ToolTrimsCompat.TOOL_TRIMS_ID) || NaturallyTrimmed.isModLoaded(ToolTrimsCompat.TRIMMABLE_TOOLS_ID))
                    && CONFIG_MANAGER.instance().trimMobs.trimChance >= random.nextInt(100)) {
                ToolTrimsCompat.toolTrimsCompat(entity.getMainHandItem(), registryAccess, random);
            }
        }
    }

    private static void runRandomTrimsSystem(Iterable<ItemStack> armor, RegistryAccess registryAccess, RandomSource random) {
        Pair<Registry<TrimMaterial>, Registry<TrimPattern>> registries = getTrimRegistries(registryAccess);
        Registry<TrimMaterial> materialRegistry = registries.getFirst();
        Registry<TrimPattern> patternRegistry = registries.getSecond();

        ArmorTrim trim = getRandomTrim(materialRegistry, patternRegistry, random);

        for (ItemStack armorPiece : armor) {
            if (CONFIG_MANAGER.instance().trimMobs.trimChance >= random.nextInt(100)) {
                applyTrim(armorPiece, trim, registryAccess);
            }
        }
    }

    private static void runCustomTrimCombinationsSystem(List<ItemStack> armor, RegistryAccess registryAccess, RandomSource random) {
        String requiredMaterial = getArmorMaterial(armor.getFirst());
        if (requiredMaterial == null) return;

        TrimCombination trimCombination = TrimCombination.getRandomTrimCombination(requiredMaterial);
        if (trimCombination == null) return;

        Iterator<ItemStack> armorIterator = armor.iterator();

        for (TrimKey trim : trimCombination.trims().reversed()) {
            ItemStack armorPiece = armorIterator.next();

            if (CONFIG_MANAGER.instance().trimMobs.trimChance >= random.nextInt(100)) {
                ArmorTrim armorTrim = TrimCombination.getOrCreateCachedTrim(trim.material(), trim.pattern(), registryAccess);

                if (armorTrim != null) {
                    applyTrim(armorPiece, armorTrim, registryAccess);
                }
            }
        }
    }

    @Nullable
    private static String getArmorMaterial(ItemStack armorPiece) {
        for (String material : List.of("netherite", "diamond", "gold", "iron", "chainmail", "copper", "leather")) {
            if (armorPiece.toString().contains(material)) {
                return material;
            }
        }

        NaturallyTrimmed.LOGGER.error("Could not find armor material for {}", armorPiece);
        return null;
    }

    protected static Pair<Registry<TrimMaterial>, Registry<TrimPattern>> getTrimRegistries(RegistryAccess registryAccess) {
        //? >=1.21.2 {
        Registry<TrimMaterial> materialRegistry = registryAccess.lookupOrThrow(Registries.TRIM_MATERIAL);
        Registry<TrimPattern> patternRegistry = registryAccess.lookupOrThrow(Registries.TRIM_PATTERN);
        //?} else {
        /*Registry<TrimMaterial> materialRegistry = registryAccess.registryOrThrow(Registries.TRIM_MATERIAL);
        Registry<TrimPattern> patternRegistry = registryAccess.registryOrThrow(Registries.TRIM_PATTERN);
        *///?}

        return new Pair<>(materialRegistry, patternRegistry);
    }

    protected static List<Holder.Reference<TrimPattern>> getAndFilterPatterns(Registry<TrimPattern> patternRegistry) {
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

    protected static List<Holder.Reference<TrimPattern>> getPatterns(Registry<TrimPattern> patternRegistry) {
        //? if >=1.21.2 {
        return new ArrayList<>(patternRegistry.listElements().toList());
        //?} else {
        /*return new ArrayList<>(patternRegistry.holders().toList());
         *///?}

    }
}
