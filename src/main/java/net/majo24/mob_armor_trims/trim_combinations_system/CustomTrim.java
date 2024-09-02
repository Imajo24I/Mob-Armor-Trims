package net.majo24.mob_armor_trims.trim_combinations_system;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.armortrim.*;

import java.util.List;

public record CustomTrim(String material, String pattern) {
    public static final CustomTrim EMPTY = new CustomTrim("", "");
    private static final String TRIM_PATTER_SUFFIX = "_armor_trim_smithing_template";


    /**
     * @return the ArmorTrim of the custom trim
     * @throws IllegalStateException if the trim couldn't be created. Most likely due to invalid material or pattern
     */
    public ArmorTrim getTrim(RegistryAccess registryAccess) throws IllegalStateException {
        String trimPatternId = pattern;
        if (!trimPatternId.endsWith(TRIM_PATTER_SUFFIX)) {
            trimPatternId += TRIM_PATTER_SUFFIX;
        }

        Holder.Reference<TrimMaterial> trimMaterial = getMaterial(material, registryAccess);
        if (trimMaterial == null) {
            throw new IllegalStateException("Failed to create armor trim. Please ensure this is a valid trim material: " + material);
        }

        Holder.Reference<TrimPattern> trimPattern = getPattern(trimPatternId, registryAccess);
        if (trimPattern == null) {
            trimPatternId = trimPatternId.replace(TRIM_PATTER_SUFFIX, "");
            trimPattern = getPattern(trimPatternId, registryAccess);

            if (trimPattern == null) {
                throw new IllegalStateException("Failed to create armor trim. Please ensure this is a valid trim pattern: " + trimPatternId);
            }
        }

        return new ArmorTrim(trimMaterial, trimPattern);
    }

    private Holder.Reference<TrimMaterial> getMaterial(String material, RegistryAccess registryAccess) {
        try {
            return TrimMaterials.getFromIngredient(registryAccess, BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(material)).getDefaultInstance()).orElseThrow();
        } catch (Exception e) {
            return null;
        }
    }

    private Holder.Reference<TrimPattern> getPattern(String pattern, RegistryAccess registryAccess) {
        try {
            return TrimPatterns.getFromTemplate(registryAccess, BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(pattern)).getDefaultInstance()).orElseThrow();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * @return The custom trim converted to a list containing the material and pattern as strings
     */
    public List<String> toList() {
        return List.of(material, pattern);
    }

    /**
     * @param customTrimAsStringList The custom trim in the form of a list containing the material and pattern as strings
     * @return The custom trim
     */
    public static CustomTrim fromList(List<String> customTrimAsStringList) {
        return new CustomTrim(customTrimAsStringList.get(0), customTrimAsStringList.get(1));
    }
}
