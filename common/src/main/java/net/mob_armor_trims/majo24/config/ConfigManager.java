package net.mob_armor_trims.majo24.config;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.mob_armor_trims.majo24.MobArmorTrims;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class ConfigManager {
    public static final Config.TrimSystem DEFAULT_ENABLED_SYSTEM = Config.TrimSystem.RANDOM_TRIMS;
    public static final int DEFAULT_TRIM_CHANCE = 50;
    public static final int DEFAULT_SIMILAR_TRIM_CHANCE = 75;
    public static final int DEFAULT_NO_TRIMS_CHANCE = 25;

    public static final List<Config.CustomTrim> DEFAULT_CUSTOM_TRIMS_LIST = new ArrayList<>();
    public static final boolean DEFAULT_APPLY_TO_ENTIRE_ARMOR = true;

    public static final int DEFAULT_STACKED_TRIM_CHANCE = 10;
    public static final int DEFAULT_MAX_STACKED_TRIMS = 3;

    private final Config config;
    private final Map<List<String>, ArmorTrim> cachedCustomTrims;

    public final Path configPath;
    public static final Gson GSON = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES)
                .setPrettyPrinting()
                .create();

    public ConfigManager(Config config, Path configPath) {
        this.config = config;
        this.configPath = configPath;
        this.cachedCustomTrims = new HashMap<>();
    }

    public static Config getConfigFromFile(Path configPath) {
        if (!Files.exists(configPath)) {
            Config newConfig = getDefaultConfig();
            try {
                MobArmorTrims.LOGGER.info("Creating Mob Armor Trims config file");
                Files.createFile(configPath);
                String jsonConfig = GSON.toJson(newConfig);
                Files.writeString(configPath, jsonConfig);
            } catch (IOException e) {
                MobArmorTrims.LOGGER.error("Could not create Mob Armor Trims config file", e);
            }
            return newConfig;
        } else {
            String jsonConfig;
            try {
                // Get config from file
                MobArmorTrims.LOGGER.info("Reading Mob Armor Trims config file");
                jsonConfig = new String(Files.readAllBytes(configPath));
                Config config = GSON.fromJson(jsonConfig, Config.class);
                validateConfig(config);
                return config;
            } catch (IOException e) {
                MobArmorTrims.LOGGER.error("Could not read Mob Armor Trims config file. Using default config", e);
                return getDefaultConfig();
            }
        }
    }

    public static void validateConfig(Config config) {
        if (config.getEnabledSystem() == null) {
            config.setEnabledSystem(Config.TrimSystem.RANDOM_TRIMS);
            MobArmorTrims.LOGGER.warn("Enabled System Config is invalid or couldn't be found. Using default value: {}. Please make sure your config is valid", DEFAULT_ENABLED_SYSTEM);
        }
        if (config.getCustomTrimsList() == null) {
            config.setCustomTrimsList(DEFAULT_CUSTOM_TRIMS_LIST);
            MobArmorTrims.LOGGER.warn("Custom Trims List is invalid or couldn't be found. Using default value: {}. Please make sure your config is valid", DEFAULT_CUSTOM_TRIMS_LIST);
        }

        List<Config.CustomTrim> customTrimsList = config.getCustomTrimsList();
        customTrimsList.removeIf(customTrim -> (customTrim.material() == null) || (customTrim.pattern() == null));
    }

    public static Config getDefaultConfig() {
        return new Config(DEFAULT_ENABLED_SYSTEM, DEFAULT_TRIM_CHANCE, DEFAULT_SIMILAR_TRIM_CHANCE, DEFAULT_NO_TRIMS_CHANCE,
                DEFAULT_CUSTOM_TRIMS_LIST, DEFAULT_APPLY_TO_ENTIRE_ARMOR,
                DEFAULT_STACKED_TRIM_CHANCE, DEFAULT_MAX_STACKED_TRIMS);
    }

    public void saveConfig() {
        MobArmorTrims.LOGGER.info("Saving Mob Armor Trims config to file");
        String jsonConfig = GSON.toJson(config);
        try {
            Files.writeString(configPath, jsonConfig);
        } catch (IOException e) {
            MobArmorTrims.LOGGER.error("Could not save Mob Armor Trims config to file - ", e);
        }
    }

    public Config.TrimSystem getEnabledSystem() {
        return this.config.getEnabledSystem();
    }

    public void setEnabledSystem(Config.TrimSystem enabledSystem) {
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
     * @return Returns random Custom Trim out of the Custom Trims List
    */
    @Nullable
    public Config.CustomTrim getCustomTrim(RandomSource random) {
        List<Config.CustomTrim> customTrimsList = this.config.getCustomTrimsList();
        if (customTrimsList.isEmpty()) {
            return null;
        } else {
            return customTrimsList.get(random.nextInt(customTrimsList.size()));
        }
    }

    public List<Config.CustomTrim> getCustomTrimsList() {
        return config.getCustomTrimsList();
    }

    public void setCustomTrimsList(List<Config.CustomTrim> customTrimsList) {
        this.config.setCustomTrimsList(customTrimsList);
    }

    public void addCustomTrimToCache(String material, String pattern, ArmorTrim trim) {
        this.cachedCustomTrims.put(Arrays.asList(material, pattern), trim);
    }

    @Nullable
    public ArmorTrim getOrCreateCachedCustomTrim(String material, String pattern, RegistryAccess registryAccess) {
        ArmorTrim cachedTrim = this.cachedCustomTrims.get(Arrays.asList(material, pattern));
        if (cachedTrim == null) {
            ArmorTrim newTrim = new Config.CustomTrim(material, pattern).getTrim(registryAccess);
            if (newTrim == null) {
                return null;
            }
            this.addCustomTrimToCache(material, pattern, newTrim);
            return newTrim;
        }
        return cachedTrim;
    }

    public boolean getApplyToEntireArmor() {
        return this.config.getApplyToEntireArmor();
    }

    public void setApplyToEntireArmor(boolean applyToEntireArmor) {
        this.config.setApplyToEntireArmor(applyToEntireArmor);
    }

    public int getStackedTrimChance() {
        return this.config.getStackedTrimChance();
    }

    public void setStackedTrimChance(int stackedTrimChance) { this.config.setStackedTrimChance(stackedTrimChance); }

    public int getMaxStackedTrims() {return this.config.getMaxStackedTrims();}

    public void setMaxStackedTrims(int maxStackedTrims) { this.config.setMaxStackedTrims(maxStackedTrims); }
}