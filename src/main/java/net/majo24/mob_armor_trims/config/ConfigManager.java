package net.majo24.mob_armor_trims.config;

import net.majo24.mob_armor_trims.config.custom_trim_combinations.TrimCombination;

import java.nio.file.Path;
import java.util.*;

public class ConfigManager {
    public static final ConfigWrapper.TrimSystems DEFAULT_ENABLED_SYSTEM = ConfigWrapper.TrimSystems.RANDOM_TRIMS;
    public static final int DEFAULT_TRIM_CHANCE = 50;
    public static final int DEFAULT_SIMILAR_TRIM_CHANCE = 75;
    public static final int DEFAULT_NO_TRIMS_CHANCE = 25;

    public static final List<TrimCombination> DEFAULT_TRIM_COMBINATIONS = new ArrayList<>();

    public static final int DEFAULT_STACKED_TRIM_CHANCE = 10;
    public static final int DEFAULT_MAX_STACKED_TRIMS = 3;

    private final ConfigWrapper config;
    public final ConfigFileHandler fileHandler;

    public ConfigManager(ConfigWrapper config, Path configPath) {
        this.config = config;
        this.fileHandler = new ConfigFileHandler(configPath);
    }

    public static ConfigWrapper getDefaultConfig() {
        return new ConfigWrapper(DEFAULT_ENABLED_SYSTEM, DEFAULT_TRIM_CHANCE, DEFAULT_SIMILAR_TRIM_CHANCE, DEFAULT_NO_TRIMS_CHANCE,
                DEFAULT_TRIM_COMBINATIONS,
                DEFAULT_STACKED_TRIM_CHANCE, DEFAULT_MAX_STACKED_TRIMS);
    }

    public void saveConfig() {
        this.fileHandler.saveConfig(this.config);
    }

    public ConfigWrapper getConfig() { return config; }
}