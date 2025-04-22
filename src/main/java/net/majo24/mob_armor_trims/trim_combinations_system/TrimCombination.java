package net.majo24.mob_armor_trims.trim_combinations_system;

import net.majo24.mob_armor_trims.config.Config;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import org.graalvm.collections.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public record TrimCombination(String materialToApplyTo, CustomTrim helmetTrim, CustomTrim chestplateTrim, CustomTrim leggingsTrim, CustomTrim bootsTrim) {
    /**
     * @return A list of all custom trims from this trim combination
     */
    public List<CustomTrim> trims() {
        return new ArrayList<>(List.of(this.helmetTrim, this.chestplateTrim, this.leggingsTrim, this.bootsTrim));
    }

    public void validate(RegistryAccess registryAccess, LocalPlayer player, int index) {
        for (CustomTrim trim : this.trims()) {
            if (trim.getTrim(registryAccess) == null) {
                player.displayClientMessage(Component.literal(
                        "Found invalid trim: \"" + trim + "\" in trim combination " + index
                ), false);
            }
        }
    }

    /**
     * @param requiredMaterial The material the trim combination has to match
     * @return A random trim combination that matches the given required material. Null if no trim combination matches.
     */
    @Nullable
    public static TrimCombination getRandomTrimCombination(String requiredMaterial) {
        List<TrimCombination> trimCombinations = Config.CONFIG_MANAGER.instance().trimMobs.trimCombinations;

        if (!trimCombinations.isEmpty()) {
            Collections.shuffle(trimCombinations);
            for (TrimCombination trimCombination : trimCombinations) {
                if (trimCombination.materialToApplyTo().equals(requiredMaterial)) {
                    return trimCombination;
                }
            }
        }

        return null;
    }

    private static final Map<CustomTrim, ArmorTrim> cachedTrims = new HashMap<>();

    @Nullable
    public static ArmorTrim getOrCreateCachedTrim(String material, String pattern, RegistryAccess registryAccess) {
        ArmorTrim trim = cachedTrims.get(new CustomTrim(material, pattern));

        if (trim == null) {
            trim = new CustomTrim(material, pattern).getTrim(registryAccess);
            if (trim == null) return null;

            cachedTrims.put(new CustomTrim(material, pattern), trim);
        }

        return trim;
    }
}