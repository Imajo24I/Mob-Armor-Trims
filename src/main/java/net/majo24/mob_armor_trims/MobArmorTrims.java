package net.majo24.mob_armor_trims;

//? if fabric {

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
//?} elif neoforge {
/*import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;

//? >1.20.4 {
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.fml.ModContainer;
//?} else {
/^import net.neoforged.neoforge.client.ConfigScreenHandler;
^///?}
*///?} elif forge {
/*import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;
 *///?}

//? if forgeLike {
/*import net.majo24.mob_armor_trims.config.screen.ConfigScreenProvider;
 *///?}

import net.majo24.mob_armor_trims.config.Config;
import net.majo24.mob_armor_trims.config.backend.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

//? if forgeLike
/*@Mod("mob_armor_trims")*/
public class MobArmorTrims /*? if fabric {*/ implements ModInitializer/*?}*/ {
    public static final String MOD_ID = "mob_armor_trims";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static boolean isStackedArmorTrimsLoaded = false;
    public static ConfigManager<Config> configManager;

    public MobArmorTrims(
            //? if >1.20.4 && neoforge
            /*ModContainer container*/
    ) {
        onInitialize();
        //? if forgeLike {
        /*registerConfigScreen(
        //? if >1.20.4
                /^container^/
        );
        *///?}
    }

    //? if fabric
    @Override
    public void onInitialize() {
        isStackedArmorTrimsLoaded = isModLoaded("stacked_trims");
        configManager = new ConfigManager<>(Config.class, getConfigPath(), LOGGER);
    }

    public static boolean isModLoaded(String modId) {
        boolean isModLoaded;

        //? if fabric {
        isModLoaded = FabricLoader.getInstance().isModLoaded(modId);
        //?} else {
        /*isModLoaded = ModList.get().isLoaded(modId);
         *///?}

        return isModLoaded;
    }

    public static Path getConfigPath() {
        Path configDirPath;

        //? if fabric {
        configDirPath = FabricLoader.getInstance().getConfigDir();
        //?} else {
        /*configDirPath = FMLPaths.CONFIGDIR.get();
         *///?}

        return configDirPath.resolve(MOD_ID + ".toml");
    }

    //? if forgeLike {
    /*public static void registerConfigScreen(
            //? if >1.20.4
            /^ModContainer container^/
    ) {
        /^? <1.20.5 {^/
        
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory((client, parent) -> ConfigScreenProvider.getConfigScreen(parent)));
        /^?} else {^/
        /^container.registerExtensionPoint(IConfigScreenFactory.class, (modContainer, screen) -> ConfigScreenProvider.getConfigScreen(screen));
        ^//^?}^/
    }
    *///?}
}
