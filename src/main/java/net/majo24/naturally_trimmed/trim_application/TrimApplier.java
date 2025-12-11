package net.majo24.naturally_trimmed.trim_application;

import net.majo24.naturally_trimmed.NaturallyTrimmed;
import net.majo24.naturally_trimmed.config.Config.TrimMobsSubConfig.TrimSystem;

import static net.majo24.naturally_trimmed.NaturallyTrimmed.isModLoaded;
import static net.majo24.naturally_trimmed.config.Config.CONFIG_MANAGER;

import net.minecraft.IdentifierException;
import net.minecraft.util.Util;
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
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

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
        //?} else
        /*ArmorTrim.setTrim(registryAccess, itemStack, armorTrim);*/
    }

    /**
     * Returns a random but filtered armor trim. The filter is to avoid missing-texture textures:
     * <ul>
     *   <li>Ensures no trim patterns only compatible with tools are used (these patterns are added by tooltrims)</li>
     *   <li>Ensures at least one of the two trim parts is non-modded (a mod's trim parts are only rarely compatible with another mod's trim parts)</li>
     *   <li>Ensures the trim pattern isn't added by the elytra trims mod, as elytra trims 4.5 adds patterns from some other mods to the registry even though these mods may not be loaded. This causes the missing-texture texture since elytra trims only adds an elytra-compatible version of the trim.</li>
     * </ul>
     */
    public static ArmorTrim getRandomTrim(RegistryAccess registryAccess, RandomSource random) {
        Pair<Registry<TrimMaterial>, Registry<TrimPattern>> registries = getTrimRegistries(registryAccess);
        List<Holder.Reference<TrimPattern>> trimPatterns = getTrimPatterns(registries.getSecond());

        Holder.Reference<TrimMaterial> trimMaterial;
        Holder.Reference<TrimPattern> trimPattern;

        // Ensure no tool trim patterns are used
        trimPatterns.removeIf(pattern -> pattern.key().identifier().getNamespace().equals("tooltrims"));

        // Ensure no trim patterns added by elytra trims are used
        trimPatterns.removeIf(pattern -> (isModLoaded("elytratrims") && (!isModLoaded(pattern.key().identifier().getNamespace()) || pattern.key().identifier().getNamespace().equals("elytratrims"))));

        // Ensure at least one of the two trim parts is non-modded
        do {
            trimPattern = Util.getRandom(trimPatterns, random);
            trimMaterial = registries.getFirst().getRandom(random).orElseThrow();
        } while (!trimMaterial.key().identifier().getNamespace().equals("minecraft") && !trimPattern.key().identifier().getNamespace().equals("minecraft"));

        return new ArmorTrim(trimMaterial, trimPattern);
    }

    /**
     * Runs the selected Trim System on the armor of the entity. Also applies trims to the entity's equipment, if possible.
     */
    public static void trimEquipment(LivingEntity entity) {
        if (!CONFIG_MANAGER.instance().enableTrimMobs) return;
        if (CONFIG_MANAGER.instance().trimMobs.noTrimsChance >= entity.getRandom().nextInt(100)) return;

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

        TrimSystem enabledSystem = CONFIG_MANAGER.instance().trimMobs.trimSystem;
        ArmorTrim trim = (enabledSystem == TrimSystem.RANDOM_TRIMS)
                ? getRandomTrim(registryAccess, random)
                : getPredefinedTrim(registryAccess);

        if (trim == null) return;

        // Apply trim to the armor
        for (ItemStack armorPiece : armor) {
            if (CONFIG_MANAGER.instance().trimMobs.trimChance >= random.nextInt(100)) {
                applyTrim(armorPiece, trim, registryAccess);
            }
        }

        // Apply trim to the equipment, if possible
        if ((NaturallyTrimmed.isModLoaded(ToolTrimsCompat.TOOL_TRIMS_ID) || NaturallyTrimmed.isModLoaded(ToolTrimsCompat.TRIMMABLE_TOOLS_ID))
                && CONFIG_MANAGER.instance().trimMobs.trimChance >= random.nextInt(100)) {
            ToolTrimsCompat.applyTrimToTool(entity.getMainHandItem(), trim.material(), registryAccess, random);
        }
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

    protected static List<Holder.Reference<TrimPattern>> getTrimPatterns(Registry<TrimPattern> patternRegistry) {
        //? if >=1.21.2 {
        return new ArrayList<>(patternRegistry.listElements().toList());
        //?} else
        /*return new ArrayList<>(patternRegistry.holders().toList());*/
    }

    @Nullable
    private static ArmorTrim getPredefinedTrim(RegistryAccess registryAccess) {
        List<TrimData> predefinedTrims = CONFIG_MANAGER.instance().trimMobs.predefinedTrims;
        Collections.shuffle(predefinedTrims);

        for (TrimData predefinedTrim : predefinedTrims) {
            try {
                return predefinedTrim.getTrim(registryAccess);
            } catch (NoSuchElementException | IdentifierException e) {
                NaturallyTrimmed.LOGGER.error("Failed to load predefined trim '{}' - '{}'. Please ensure this is a valid trim.", predefinedTrim.material(), predefinedTrim.pattern(), e);
            }
        }

        return null;
    }
}
