package net.mob_armor_trims.majo24;

import net.mob_armor_trims.majo24.config.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public final class MobArmorTrims {
    public static final String MOD_ID = "mob_armor_trims";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static boolean isStackedArmorTrimsLoaded = false;
    public static ConfigManager configManager;

    public static void init(Path configPath) {
        configManager = new ConfigManager(ConfigManager.getConfigFromFile(configPath), configPath);
    }
}
