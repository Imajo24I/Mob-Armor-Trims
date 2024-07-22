package net.majo24.mob_armor_trims.config;

import net.majo24.mob_armor_trims.config.custom_trim_combinations.CustomTrim;
import net.majo24.mob_armor_trims.config.custom_trim_combinations.TrimCombination;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.armortrim.ArmorTrim;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.*;

public class ConfigManager {
    public static final Config.TrimSystems DEFAULT_ENABLED_SYSTEM = Config.TrimSystems.RANDOM_TRIMS;
    public static final int DEFAULT_TRIM_CHANCE = 50;
    public static final int DEFAULT_SIMILAR_TRIM_CHANCE = 75;
    public static final int DEFAULT_NO_TRIMS_CHANCE = 25;

    public static final List<TrimCombination> DEFAULT_TRIM_COMBINATIONS = new ArrayList<>();

    public static final int DEFAULT_STACKED_TRIM_CHANCE = 10;
    public static final int DEFAULT_MAX_STACKED_TRIMS = 3;

    private final Config config;
    private final Map<List<String>, ArmorTrim> cachedCustomTrims;

    public final ConfigFileHandler fileHandler;

    public ConfigManager(Config config, Path configPath) {
        this.config = config;
        this.fileHandler = new ConfigFileHandler(configPath);
        this.cachedCustomTrims = new HashMap<>();
    }

    public static Config getDefaultConfig() {
        return new Config(DEFAULT_ENABLED_SYSTEM, DEFAULT_TRIM_CHANCE, DEFAULT_SIMILAR_TRIM_CHANCE, DEFAULT_NO_TRIMS_CHANCE,
                DEFAULT_TRIM_COMBINATIONS,
                DEFAULT_STACKED_TRIM_CHANCE, DEFAULT_MAX_STACKED_TRIMS);
    }

    public void saveConfig() {
        this.fileHandler.saveConfig(this.config);
    }

    public Config getConfig() { return config; }

    /**
     * @param requiredMaterial The material the trim combination has to match
     * @return A random trim combination that matches the given required material. Null if no trim combination matches.
     */
    @Nullable
    public TrimCombination getRandomTrimCombination(String requiredMaterial) {
        List<TrimCombination> trimCombinations = this.config.getTrimCombinations();
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