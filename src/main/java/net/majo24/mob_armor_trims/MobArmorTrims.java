package net.majo24.mob_armor_trims;

/*? if fabric {*/
import net.fabricmc.api.ModInitializer;

import net.fabricmc.loader.api.FabricLoader;
import net.majo24.mob_armor_trims.config.ConfigFileHandler;
import net.majo24.mob_armor_trims.config.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public class MobArmorTrims implements ModInitializer {
    public static final String MOD_ID = "mob_armor_trims";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static boolean isStackedArmorTrimsLoaded = false;
    public static ConfigManager configManager;


	@Override
	public void onInitialize() {
	    isStackedArmorTrimsLoaded = FabricLoader.getInstance().isModLoaded("stacked_trims");

		Path configPath = FabricLoader.getInstance().getConfigDir().resolve(MOD_ID + ".toml");
		configManager = new ConfigManager(ConfigFileHandler.getConfigFromFile(configPath), configPath);
	}
}
/*?} elif neoforge {*/
/*import net.majo24.mob_armor_trims.config.ConfigManager;
import net.majo24.mob_armor_trims.config.configscreen.screen.ConfigScreenProvider;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;
/^? >1.20.5 {^/
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
/^?} else {^/
/^import net.neoforged.neoforge.client.ConfigScreenHandler;
^//^?}^/

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

@Mod("mob_armor_trims")
public class MobArmorTrims {
    public static final String MOD_ID = "mob_armor_trims";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static boolean isStackedArmorTrimsLoaded = false;
    public static ConfigManager configManager;

    public MobArmorTrims() {
        isStackedArmorTrimsLoaded = ModList.get().isLoaded("stacked_trims");

        // Register Config Screen
        /^? <1.20.5 {^/
        /^ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory((client, parent) -> ConfigScreenProvider.getConfigScreen(parent)));
        ^//^?} else {^/
        ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class,
            () -> (client, parent) -> ConfigScreenProvider.getConfigScreen(parent));
        /^?}^/

        Path configPath = FMLPaths.CONFIGDIR.get().resolve(MOD_ID + ".toml");
		configManager = new ConfigManager(ConfigManager.getConfigFromFile(configPath), configPath);
    }
}
*//*?} elif forge {*/
/*import net.majo24.mob_armor_trims.config.configscreen.screen.ConfigScreenProvider;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.majo24.mob_armor_trims.config.ConfigManager;
import net.minecraftforge.fml.loading.FMLPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

@Mod("mob_armor_trims")
public class MobArmorTrims {
    public static final String MOD_ID = "mob_armor_trims";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static boolean isStackedArmorTrimsLoaded = false;
    public static ConfigManager configManager;

    public MobArmorTrims() {
        isStackedArmorTrimsLoaded = ModList.get().isLoaded("stacked_trims");

        // Register Config Screen
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory((client, parent) -> ConfigScreenProvider.getConfigScreen(parent)));

        Path configPath = FMLPaths.CONFIGDIR.get().resolve(MOD_ID + ".toml");
		configManager = new ConfigManager(ConfigManager.getConfigFromFile(configPath), configPath);
    }
}
*//*?}*/