package net.majo24.naturally_trimmed.trim_combination;

import net.majo24.naturally_trimmed.config.Config;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.equipment.trim.*;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public record TrimCombination(List<String> allowedArmorMaterials, TrimKey helmetTrim, TrimKey chestplateTrim, TrimKey leggingsTrim, TrimKey bootsTrim) {
    /**
     * @return A list of all trim keys
     */
    public List<TrimKey> trims() {
        return new ArrayList<>(List.of(this.helmetTrim, this.chestplateTrim, this.leggingsTrim, this.bootsTrim));
    }

    public void validate(RegistryAccess registryAccess, LocalPlayer player, int index) {
        for (TrimKey trim : this.trims()) {
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
                if (trimCombination.allowedArmorMaterials().contains(requiredMaterial)) {
                    return trimCombination;
                }
            }
        }

        return null;
    }

    private static final Map<TrimKey, ArmorTrim> cachedTrims = new HashMap<>();

    @Nullable
    public static ArmorTrim getOrCreateCachedTrim(String material, String pattern, RegistryAccess registryAccess) {
        ArmorTrim trim = cachedTrims.get(new TrimKey(material, pattern));

        if (trim == null) {
            trim = new TrimKey(material, pattern).getTrim(registryAccess);
            if (trim == null) return null;

            cachedTrims.put(new TrimKey(material, pattern), trim);
        }

        return trim;
    }
}