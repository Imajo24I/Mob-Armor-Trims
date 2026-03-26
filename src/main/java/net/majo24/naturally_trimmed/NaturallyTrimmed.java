package net.majo24.naturally_trimmed;

//? if fabric {
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
//?} elif neoforge {
/*import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
//? if 1.20.1 {
/^import net.neoforged.neoforge.client.ConfigScreenHandler;
^///?} else {
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
//?}
*///?} else {
/*import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;
*///?}

//? if forgeLike
//import net.majo24.naturally_trimmed.config.screen.ConfigScreenProvider;

import net.majo24.naturally_trimmed.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

//? if forgeLike
//@Mod("naturally_trimmed")
public class NaturallyTrimmed /*? if fabric {*/ implements ModInitializer/*?}*/ {
    public static final String MOD_ID = "naturally_trimmed";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    //? if forgeLike {
    /*public NaturallyTrimmed() {
        onInitialize();

        //? if >=1.21.9 {
        if (FMLLoader.getCurrent().getDist().isClient()) {
        //?} else {
        /^if (FMLLoader.getDist().isClient()) {
        ^///?}
            registerConfigScreen();
        }

        //? if <26.1 {
        /^//? if neoforge{
        /^¹RegistryHelper.FUNCTION_REGISTER.register(ModLoadingContext.get().getActiveContainer().getEventBus());
        ¹^///?} else {
        RegistryHelper.FUNCTION_REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());
        //?}
        ^///?}
    }
    *///?}

    //? if fabric
    @Override
    public void onInitialize() {
        Config.CONFIG_MANAGER.loadInstance();
        Events.registerEvents();

        //? if <26.1
        //RegistryHelper.initClass();
    }

    public static boolean isModLoaded(String modId) {
        //? if fabric {
        return FabricLoader.getInstance().isModLoaded(modId);
         //?} else
        //return ModList.get().isLoaded(modId);
    }

    public static Path getConfigPath() {
        //? if fabric {
        Path configDirPath = FabricLoader.getInstance().getConfigDir();
        //?} else
        //Path configDirPath = FMLPaths.CONFIGDIR.get();

        return configDirPath.resolve(MOD_ID + ".json5");
    }

    //? if forgeLike {
    /*public static void registerConfigScreen() {
        //? if 1.20.1 {
        /^ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory((client, parent) -> ConfigScreenProvider.getConfigScreen(parent)));
        ^//^?} else {^/
        ModLoadingContext.get().getActiveContainer()
                .registerExtensionPoint(IConfigScreenFactory.class, (modContainer, screen) -> ConfigScreenProvider.getConfigScreen(screen));
        /^?}^/
    }
    *///?}
}
