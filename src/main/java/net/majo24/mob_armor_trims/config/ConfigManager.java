package net.majo24.mob_armor_trims.config;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.majo24.mob_armor_trims.MobArmorTrims;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigManager {
    private final Config config;
    public static final Path configPath = FabricLoader.getInstance().getConfigDir().resolve(MobArmorTrims.MOD_ID + ".json");
    public static final Gson GSON = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES)
                .setPrettyPrinting()
                .create();

    public ConfigManager(Config configToSet) {
        config = configToSet;
    }

    public static Config getConfigFromFile() {
        if (!Files.exists(configPath)) {
            Config newConfig = new Config(50);
            try {
                MobArmorTrims.LOGGER.info("Creating config file for Mob Armor Trims");
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
                MobArmorTrims.LOGGER.info("Reading config file for Mob Armor Trims");
                jsonConfig = new String(Files.readAllBytes(configPath));
                return GSON.fromJson(jsonConfig, Config.class);
            } catch (IOException e) {
                MobArmorTrims.LOGGER.error("Could not read config file", e);
                return new Config(50);
            }
        }
    }




    public void saveConfig() {
        String jsonConfig = GSON.toJson(config);
        try {
            Files.writeString(configPath, jsonConfig);
        } catch (IOException e) {
            MobArmorTrims.LOGGER.error("Could not save config file", e);
        }
    }

    public int getTrimChance() {
        return this.config.trimChance;
    }

    public void setTrimChance(int trimChanceToSet) {
        this.config.trimChance = trimChanceToSet;
    }
}