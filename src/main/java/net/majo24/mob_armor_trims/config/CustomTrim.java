package net.majo24.mob_armor_trims.config;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.armortrim.*;

import java.util.List;

public record CustomTrim(String material, String pattern) {
    public static final CustomTrim EMPTY = new CustomTrim("", "");

    public ArmorTrim getTrim(RegistryAccess registryAccess) throws IllegalStateException {
        String trimPatternIdentifier = pattern;
        if (!trimPatternIdentifier.contains("_armor_trim_smithing_template")) {
            trimPatternIdentifier += "_armor_trim_smithing_template";
        }

        Holder.Reference<TrimMaterial> trimMaterial;
        Holder.Reference<TrimPattern> trimPattern;

        try {
            trimMaterial = TrimMaterials.getFromIngredient(registryAccess, BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(material)).getDefaultInstance()).orElseThrow();
            trimPattern = TrimPatterns.getFromTemplate(registryAccess, BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(trimPatternIdentifier)).getDefaultInstance()).orElseThrow();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to create armor trim. Please ensure this is a valid custom trim: " + material + " - " + pattern, e);
        }

        return new ArmorTrim(trimMaterial, trimPattern);
    }

    public List<String> toStringList() {
        return List.of(material, pattern);
    }

    public static CustomTrim fromList(List<String> customTrimAsStringList) {
        return new CustomTrim(customTrimAsStringList.get(0), customTrimAsStringList.get(1));
    }
}
