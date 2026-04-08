package net.majo24.naturally_trimmed.trim_application;

import net.minecraft.IdentifierException;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.equipment.trim.*;

import java.util.NoSuchElementException;

/**
 * Record used for holding the material and pattern of an armor trim until the actual {@link ArmorTrim} object can be constructed
 */
public record TrimData(String material, String pattern) {
    /**
     * Constructs an {@link ArmorTrim} using the material and pattern.
     * @return an {@link ArmorTrim} object
     * @throws IdentifierException when either the material or pattern is invalid
     * @throws NoSuchElementException same as above
     */
    public ArmorTrim getTrim(RegistryAccess registryAccess) throws NoSuchElementException, IdentifierException {
        //? if >=1.21.11 {
        Registry<TrimMaterial> materialRegistry = registryAccess.lookupOrThrow(Registries.TRIM_MATERIAL);
        Registry<TrimPattern> patternRegistry = registryAccess.lookupOrThrow(Registries.TRIM_PATTERN);
        //?} else {
        /*Registry<TrimMaterial> materialRegistry = registryAccess.registryOrThrow(Registries.TRIM_MATERIAL);
        Registry<TrimPattern> patternRegistry = registryAccess.registryOrThrow(Registries.TRIM_PATTERN);
        *///?}

        //? if >=1.21.11 {
        Holder.Reference<TrimMaterial> trimMaterial = materialRegistry.get(Identifier.parse(material)).orElseThrow();
        Holder.Reference<TrimPattern> trimPattern = patternRegistry.get(Identifier.parse(pattern)).orElseThrow();
        //?} else {
        /*Holder.Reference<TrimMaterial> trimMaterial = materialRegistry.getHolder(Identifier.parse(material)).orElseThrow();
        Holder.Reference<TrimPattern> trimPattern = patternRegistry.getHolder(Identifier.parse(pattern)).orElseThrow();
        *///?}

        return new ArmorTrim(trimMaterial, trimPattern);
    }
}
