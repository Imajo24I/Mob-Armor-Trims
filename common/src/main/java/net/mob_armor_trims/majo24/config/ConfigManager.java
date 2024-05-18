package net.mob_armor_trims.majo24.config;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.util.RandomSource;
import net.mob_armor_trims.majo24.MobArmorTrims;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ConfigManager {
    public static final TrimSystem DEFAULT_ENABLED_SYSTEM = TrimSystem.RANDOM_TRIMS;
    public static final int DEFAULT_TRIM_CHANCE = 50;
    public static final int DEFAULT_SIMILAR_TRIM_CHANCE = 75;
    public static final int DEFAULT_NO_TRIMS_CHANCE = 25;

    public static final List<CustomTrim> DEFAULT_CUSTOM_TRIMS_LIST = new ArrayList<>();

    public static final int DEFAULT_STACKED_TRIM_CHANCE = 10;
    public static final int DEFAULT_MAX_STACKED_TRIMS = 3;

    private final Config config;
    public final Path configPath;
    public static final Gson GSON = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES)
                .setPrettyPrinting()
                .create();

    public ConfigManager(Config config, Path configPath) {
        this.config = config;
        this.configPath = configPath;
    }

    public static Config getConfigFromFile(Path configPath) {
        if (!Files.exists(configPath)) {
            Config newConfig = getDefaultConfig();
            try {
                MobArmorTrims.LOGGER.info("Creating config file");
                Files.createFile(configPath);
                String jsonConfig = GSON.toJson(newConfig);
                Files.writeString(configPath, jsonConfig);
            } catch (IOException e) {
                MobArmorTrims.LOGGER.error("Could not create config file", e);
            }
            return newConfig;
        } else {
            String jsonConfig;
            try {
                // Get config from file
                MobArmorTrims.LOGGER.info("Reading config file");
                jsonConfig = new String(Files.readAllBytes(configPath));
                Config config = GSON.fromJson(jsonConfig, Config.class);
                validateConfig(config);
                return config;
            } catch (IOException e) {
                MobArmorTrims.LOGGER.error("Could not read config file", e);
                return getDefaultConfig();
            }
        }
    }

    public static void validateConfig(Config config) {
        if (config.getEnabledSystem() == null) {
            config.setEnabledSystem(TrimSystem.RANDOM_TRIMS);
            MobArmorTrims.LOGGER.warn("Enabled System Config is invalid or couldn't be found. Using default value: {}. Please make sure your config is valid", DEFAULT_ENABLED_SYSTEM);
        }
        if (config.getCustomTrimsList() == null) {
            config.setCustomTrimsList(DEFAULT_CUSTOM_TRIMS_LIST);
            MobArmorTrims.LOGGER.warn("Custom Trims List is invalid or couldn't be found. Using default value: {}. Please make sure your config is valid", DEFAULT_CUSTOM_TRIMS_LIST);
        }
        List<CustomTrim> customTrimsList = config.getCustomTrimsList();
        customTrimsList.removeIf(customTrim -> (customTrim.getMaterialSNBT() == null) || (customTrim.getPatternSNBT() == null));
    }

    public static Config getDefaultConfig() {
        return new Config(DEFAULT_ENABLED_SYSTEM, DEFAULT_TRIM_CHANCE, DEFAULT_SIMILAR_TRIM_CHANCE, DEFAULT_NO_TRIMS_CHANCE,
                DEFAULT_CUSTOM_TRIMS_LIST,
            DEFAULT_STACKED_TRIM_CHANCE, DEFAULT_MAX_STACKED_TRIMS);
    }

    public void saveConfig() {
        MobArmorTrims.LOGGER.info("Saving config file");
        String jsonConfig = GSON.toJson(config);
        try {
            Files.writeString(configPath, jsonConfig);
        } catch (IOException e) {
            MobArmorTrims.LOGGER.error("Could not save config file", e);
        }
    }

    public TrimSystem getEnabledSystem() {
        return this.config.getEnabledSystem();
    }

    public void setEnabledSystem(TrimSystem enabledSystem) {
        this.config.setEnabledSystem(enabledSystem);
    }

    public int getTrimChance() {
        return this.config.getTrimChance();
    }

    public void setTrimChance(int trimChance) {
        this.config.setTrimChance(trimChance);
    }

    public int getSimilarTrimChance() { return this.config.getSimilarTrimChance(); }

    public void setSimilarTrimChance(int sameTrimChance) { this.config.setSimilarTrimChance(sameTrimChance); }

    public int getNoTrimsChance() { return this.config.getNoTrimsChance(); }

    public void setNoTrimsChance(int noTrimsChance) { this.config.setNoTrimsChance(noTrimsChance); }

    /**
     * @return Returns random Arraylist with SNBTs of trim material and pattern out of the custom trims list
    */
    public CustomTrim getCustomTrim(RandomSource random) {
        List<CustomTrim> customTrimsList = this.config.getCustomTrimsList();
        if (customTrimsList.isEmpty()) {
            return null;
        } else {
            return customTrimsList.get(random.nextInt(customTrimsList.size()));
        }
    }

    public List<String> getCustomTrimsList() {
        List<CustomTrim> customTrimsList = config.getCustomTrimsList();
        List<String> customTrimsListStringified = new ArrayList<>();
        for (CustomTrim customTrim : customTrimsList) {
            customTrimsListStringified.add(customTrim.getMaterialSNBT() + "; " + customTrim.getPatternSNBT());
        }
        return customTrimsListStringified;
    }

    public void setCustomTrimsList(List<String> customTrimsListStringified) {
        List<CustomTrim> customTrimsList = new ArrayList<>();
        for (String stringifiedCustomTrims : customTrimsListStringified) {
            CustomTrim customTrim = CustomTrim.fromStringified(stringifiedCustomTrims);
            if (customTrim != null) {
                customTrimsList.add(CustomTrim.fromStringified(stringifiedCustomTrims));
            }
        }
        this.config.setCustomTrimsList(customTrimsList);
    }

    public int getStackedTrimChance() {
        return this.config.getStackedTrimChance();
    }

    public void setStackedTrimChance(int stackedTrimChance) { this.config.setStackedTrimChance(stackedTrimChance); }

    public int getMaxStackedTrims() {return this.config.getMaxStackedTrims();}

    public void setMaxStackedTrims(int maxStackedTrims) { this.config.setMaxStackedTrims(maxStackedTrims); }
}