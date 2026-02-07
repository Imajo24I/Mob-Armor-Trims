package net.majo24.naturally_trimmed.trim_application;

import net.majo24.naturally_trimmed.NaturallyTrimmed;
import net.majo24.naturally_trimmed.config.Config.TrimMobsSubConfig.TrimSystem;

import static net.majo24.naturally_trimmed.NaturallyTrimmed.isModLoaded;
import static net.majo24.naturally_trimmed.config.Config.CONFIG_MANAGER;

import net.minecraft.IdentifierException;
import net.minecraft.util.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.equipment.trim.*;
import net.minecraft.world.item.ItemStack;

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
        //ArmorTrim.setTrim(registryAccess, itemStack, armorTrim);
    }

    /**
     * Returns a random but filtered armor trim. The filter is to avoid missing-texture textures:
     * <ul>
     *   <li>Ensures at least one of the two trim parts is non-modded (a mod's trim parts are only rarely compatible with another mod's trim parts)</li>
     *   <li>Ensures the trim pattern isn't added by the elytra trims mod, as elytra trims 4.5 adds patterns from some other mods to the registry even though these mods may not be loaded. This causes the missing-texture texture since elytra trims only adds an elytra-compatible version of the trim.</li>
     *   <li>Ensures the trim material and pattern aren't blacklisted by the mods blacklists (default is blacklisting tooltrims patterns, as they're only compatible with tools)</li>
     * </ul>
     */
    @Nullable
    public static ArmorTrim getRandomTrim(RegistryAccess registryAccess, RandomSource random) {
        List<Holder.Reference<TrimMaterial>> trimMaterials = getTrimMaterials(registryAccess);
        List<Holder.Reference<TrimPattern>> trimPatterns = getTrimPatterns(registryAccess);

        Holder.Reference<TrimMaterial> trimMaterial;
        Holder.Reference<TrimPattern> trimPattern;

        // Blacklists
        trimMaterials.removeIf(material -> CONFIG_MANAGER.instance().materialBlacklist.stream().anyMatch(regexPattern -> regexPattern.matcher(material.key().identifier().toString()).find()));
        trimPatterns.removeIf(pattern -> CONFIG_MANAGER.instance().patternBlacklist.stream().anyMatch(regexPattern -> regexPattern.matcher(pattern.key().identifier().toString()).find()));

        // Ensure no trim patterns added by elytra trims are used
        trimPatterns.removeIf(pattern -> (isModLoaded("elytratrims") && (!isModLoaded(pattern.key().identifier().getNamespace()) || pattern.key().identifier().getNamespace().equals("elytratrims"))));

        if (trimMaterials.isEmpty() || trimPatterns.isEmpty()) return null;

        // Ensure at least one of the two trim parts is non-modded
        do {
            trimMaterial = Util.getRandom(trimMaterials, random);
            trimPattern = Util.getRandom(trimPatterns, random);
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

    public static List<Holder.Reference<TrimPattern>> getTrimPatterns(RegistryAccess registryAccess) {
        //? if >=1.21.2 {
        return new ArrayList<>(registryAccess.lookupOrThrow(Registries.TRIM_PATTERN).listElements().toList());
        //?} else
        //return new ArrayList<>(registryAccess.registryOrThrow(Registries.TRIM_PATTERN).holders().toList());
    }

    public static List<Holder.Reference<TrimMaterial>> getTrimMaterials(RegistryAccess registryAccess) {
        //? if >=1.21.2 {
        return new ArrayList<>(registryAccess.lookupOrThrow(Registries.TRIM_MATERIAL).listElements().toList());
        //?} else
        //return new ArrayList<>(registryAccess.registryOrThrow(Registries.TRIM_MATERIAL).holders().toList());
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
