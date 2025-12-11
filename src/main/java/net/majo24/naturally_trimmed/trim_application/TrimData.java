package net.majo24.naturally_trimmed.trim_application;

import com.mojang.datafixers.util.Pair;
import net.minecraft.IdentifierException;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.equipment.trim.*;

//? if <1.21 {
/*import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
*///?}

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
        Pair<Registry<TrimMaterial>, Registry<TrimPattern>> registries = TrimApplier.getTrimRegistries(registryAccess);

        //? if >1.21 {
        Holder.Reference<TrimMaterial> trimMaterial = registries.getFirst().get(Identifier.parse(material)).orElseThrow();
        Holder.Reference<TrimPattern> trimPattern = registries.getSecond().get(Identifier.parse(pattern)).orElseThrow();
        //?} else if 1.21 {
        /*Holder.Reference<TrimMaterial> trimMaterial = registries.getFirst().getHolder(Identifier.parse(material)).orElseThrow();
        Holder.Reference<TrimPattern> trimPattern = registries.getSecond().getHolder(Identifier.parse(pattern)).orElseThrow();
        *///?} else {
        /*Holder.Reference<TrimMaterial> trimMaterial = registries.getFirst().getHolder(ResourceKey.create(Registries.TRIM_MATERIAL, new Identifier(material))).orElseThrow();
        Holder.Reference<TrimPattern> trimPattern = registries.getSecond().getHolder(ResourceKey.create(Registries.TRIM_PATTERN, new Identifier(pattern))).orElseThrow();
        *///?}

        return new ArmorTrim(trimMaterial, trimPattern);
    }
}
