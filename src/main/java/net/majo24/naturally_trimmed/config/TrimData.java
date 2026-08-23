package net.majo24.naturally_trimmed.config;

import net.minecraft.world.item.equipment.trim.*;

/**
 * Record used for holding the material and pattern of an armor trim until the actual {@link ArmorTrim} object can be constructed
 */
public record TrimData(String material, String pattern) {
    /// Returns the material, prefixed with the "minecraft:" identifier prefix if there's otherwise no ':'
    public String fullMaterial() {
        if (!material.contains(":")) {
            return "minecraft:" + material;
        }
        return material;
    }

    /// Returns the pattern, prefixed with the "minecraft:" identifier prefix if there's otherwise no ':'
    public String fullPattern() {
        if (!pattern.contains(":")) {
            return "minecraft:" + pattern;
        }
        return pattern;
    }
}
