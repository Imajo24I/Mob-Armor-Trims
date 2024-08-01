package net.majo24.mob_armor_trims;

//? if fabric {
import net.fabricmc.api.ModInitializer;

import net.fabricmc.loader.api.FabricLoader;
import net.majo24.mob_armor_trims.config.Config;
import net.majo24.mob_armor_trims.config.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public class MobArmorTrims implements ModInitializer {
    public static final String MOD_ID = "mob_armor_trims";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static boolean isStackedArmorTrimsLoaded = false;
    public static ConfigManager<Config> configManager;


	@Override
	public void onInitialize() {
	    isStackedArmorTrimsLoaded = FabricLoader.getInstance().isModLoaded("stacked_trims");

		Path configPath = FabricLoader.getInstance().getConfigDir().resolve(MOD_ID + ".toml");
		configManager = new ConfigManager<>(Config.class, configPath);
	}
}
//?} elif neoforge {
/*import net.majo24.mob_armor_trims.config.Config;
import net.majo24.mob_armor_trims.config.ConfigManager;
import net.majo24.mob_armor_trims.config.configscreen.screen.ConfigScreenProvider;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;
//? >1.20.4 {
/^import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.fml.ModContainer;
^///?} else {
import net.neoforged.neoforge.client.ConfigScreenHandler;
//?}

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

@Mod("mob_armor_trims")
public class MobArmorTrims {
    public static final String MOD_ID = "mob_armor_trims";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static boolean isStackedArmorTrimsLoaded = false;
    public static ConfigManager<Config> configManager;

    public MobArmorTrims(/^? >1.20.4 {^//^ModContainer container^//^?}^/) {
        isStackedArmorTrimsLoaded = ModList.get().isLoaded("stacked_trims");

        // Register Config Screen
        /^? <1.20.5 {^/
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory((client, parent) -> ConfigScreenProvider.getConfigScreen(parent)));
        /^?} else {^/
        /^container.registerExtensionPoint(IConfigScreenFactory.class, getConfigScreen());
        ^//^?}^/

        Path configPath = FMLPaths.CONFIGDIR.get().resolve(MOD_ID + ".toml");
		configManager = new ConfigManager<>(Config.class, configPath);
    }

    /^? >1.20.4 {^/
    /^private IConfigScreenFactory getConfigScreen() {
        return (modContainer, screen) -> ConfigScreenProvider.getConfigScreen(screen);
    }
    ^//^?}^/
}
*///?} elif forge {
/*import net.majo24.mob_armor_trims.config.Config;
import net.majo24.mob_armor_trims.config.configscreen.screen.ConfigScreenProvider;
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
    public static ConfigManager<Config> configManager;

    public MobArmorTrims() {
        isStackedArmorTrimsLoaded = ModList.get().isLoaded("stacked_trims");

        // Register Config Screen
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory((client, parent) -> ConfigScreenProvider.getConfigScreen(parent)));

        Path configPath = FMLPaths.CONFIGDIR.get().resolve(MOD_ID + ".toml");
		configManager = new ConfigManager<>(Config.class, configPath);
    }
}
*///?}