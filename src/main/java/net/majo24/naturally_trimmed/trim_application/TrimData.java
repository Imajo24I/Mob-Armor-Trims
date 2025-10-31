package net.majo24.naturally_trimmed.trim_application;

import com.mojang.datafixers.util.Pair;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
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
     * @throws NoSuchElementException when either the material or pattern is invalid
     */
    public ArmorTrim getTrim(RegistryAccess registryAccess) throws NoSuchElementException, ResourceLocationException {
        Pair<Registry<TrimMaterial>, Registry<TrimPattern>> registries = TrimApplier.getTrimRegistries(registryAccess);

        //? if >1.21 {
        Holder.Reference<TrimMaterial> trimMaterial = registries.getFirst().get(ResourceLocation.parse(material)).orElseThrow();
        Holder.Reference<TrimPattern> trimPattern = registries.getSecond().get(ResourceLocation.parse(pattern)).orElseThrow();
        //?} else if 1.21 {
        /*Holder.Reference<TrimMaterial> trimMaterial = registries.getFirst().getHolder(ResourceLocation.parse(material)).orElseThrow();
        Holder.Reference<TrimPattern> trimPattern = registries.getSecond().getHolder(ResourceLocation.parse(pattern)).orElseThrow();
        *///?} else {
        /*Holder.Reference<TrimMaterial> trimMaterial = registries.getFirst().getHolder(ResourceKey.create(Registries.TRIM_MATERIAL, new ResourceLocation(material))).orElseThrow();
        Holder.Reference<TrimPattern> trimPattern = registries.getSecond().getHolder(ResourceKey.create(Registries.TRIM_PATTERN, new ResourceLocation(pattern))).orElseThrow();
        *///?}

        return new ArmorTrim(trimMaterial, trimPattern);
    }
}
