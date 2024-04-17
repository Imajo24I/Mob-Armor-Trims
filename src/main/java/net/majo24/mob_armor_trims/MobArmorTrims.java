package net.majo24.mob_armor_trims;

import net.fabricmc.api.ModInitializer;

import net.majo24.mob_armor_trims.config.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MobArmorTrims implements ModInitializer {
	public static final String MOD_ID = "mob_armor_trims";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final ConfigManager configManager = new ConfigManager(ConfigManager.getConfigFromFile());

	@Override
	public void onInitialize() {
	}

}