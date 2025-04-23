package net.majo24.mob_armor_trims.trim_combination;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.equipment.trim.*;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;


import java.util.Objects;

public record TrimKey(String material, String pattern) {
    private static final String TRIM_PATTERN_SUFFIX = "_armor_trim_smithing_template";


    /**
     * @return an ArmorTrim created from this trim key. Null if the trim couldn't be created
     */
    @Nullable
    public ArmorTrim getTrim(RegistryAccess registryAccess) {
        Holder<TrimMaterial> trimMaterial = getMaterial(material, registryAccess);
        if (trimMaterial == null) return null;

        Holder<TrimPattern> trimPattern = getPattern(pattern, registryAccess);
        if (trimPattern == null) {
            trimPattern = getPattern(pattern + TRIM_PATTERN_SUFFIX, registryAccess);
            if (trimPattern == null) return null;
        }

        return new ArmorTrim(trimMaterial, trimPattern);
    }

    @Nullable
    private Holder<TrimMaterial> getMaterial(String material, RegistryAccess registryAccess) {
        try {
            ItemStack materialItem = getItemFromId(material);
            return TrimMaterials.getFromIngredient(registryAccess, materialItem).orElseThrow();
        } catch (Exception e) {
            return null;
        }
    }

    @Nullable
    private Holder<TrimPattern> getPattern(String pattern, RegistryAccess registryAccess) {
        try {
            ResourceLocation resourceLocation = ResourceLocation.tryParse(pattern);
            return registryAccess.lookupOrThrow(Registries.TRIM_PATTERN).listElements().filter(reference -> reference.key().location().equals(resourceLocation)).findFirst().orElseThrow();
        } catch (Exception e) {
            return null;
        }
    }

    private ItemStack getItemFromId(String id) {
        return BuiltInRegistries.ITEM.get(Objects.requireNonNull(ResourceLocation.tryParse(id)))
                //? >=1.21.2 {
                .orElseThrow().value()
                //?}
                .getDefaultInstance();
    }
}
