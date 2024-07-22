package net.majo24.mob_armor_trims.config;

import net.majo24.mob_armor_trims.config.custom_trim_combinations.CustomTrim;
import net.majo24.mob_armor_trims.config.custom_trim_combinations.TrimCombination;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.armortrim.ArmorTrim;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ConfigWrapper extends BaseConfig {
    private final Map<List<String>, ArmorTrim> cachedCustomTrims;

    public ConfigWrapper(TrimSystems enabledSystem, int trimChance, int similarTrimChance, int noTrimsChance, List<TrimCombination> trimCombinations, int stackedTrimChance, int maxStackedTrims) {
        super(enabledSystem, trimChance, similarTrimChance, noTrimsChance, trimCombinations, stackedTrimChance, maxStackedTrims);
        this.cachedCustomTrims = new HashMap<>();
    }

    /**
     * @param requiredMaterial The material the trim combination has to match
     * @return A random trim combination that matches the given required material. Null if no trim combination matches.
     */
    @Nullable
    public TrimCombination getRandomTrimCombination(String requiredMaterial) {
        List<TrimCombination> trimCombinations = getTrimCombinations();
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

    public void addCustomTrimToCache(String material, String pattern, ArmorTrim trim) {
        this.cachedCustomTrims.put(Arrays.asList(material, pattern), trim);
    }

    @Nullable
    public ArmorTrim getOrCreateCachedTrim(String material, String pattern, RegistryAccess registryAccess) throws IllegalStateException {
        ArmorTrim cachedTrim = this.cachedCustomTrims.get(Arrays.asList(material, pattern));
        if (cachedTrim == null) {
            ArmorTrim newTrim = new CustomTrim(material, pattern).getTrim(registryAccess);
            this.addCustomTrimToCache(material, pattern, newTrim);
            return newTrim;
        }
        return cachedTrim;
    }
}
