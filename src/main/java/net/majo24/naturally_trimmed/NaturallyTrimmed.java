package net.majo24.naturally_trimmed;

//? if fabric {
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
//?} else {
/*import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

import net.majo24.naturally_trimmed.config.screen.ConfigScreenProvider;
*///?}

import net.majo24.naturally_trimmed.config.Config;
import net.minecraft.client.Minecraft;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

//? if neoforge
//@Mod("naturally_trimmed")
public class NaturallyTrimmed /*? if fabric {*/ implements ModInitializer/*?}*/ {
    public static final String MOD_ID = "naturally_trimmed";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static boolean isClientAvailable = false;

    //? if neoforge {
    /*public NaturallyTrimmed() {
        onInitialize();

        //? if >=1.21.11 {
        if (FMLLoader.getCurrent().getDist().isClient()) {
        //?} else
        //if (FMLLoader.getDist().isClient()) {
            registerConfigScreen();
        }

        //? if <26.1
        //RegistryHelper.FUNCTION_REGISTER.register(ModLoadingContext.get().getActiveContainer().getEventBus());
    }
    *///?}

    //? if fabric
    @Override
    public void onInitialize() {
        Config.CONFIG_MANAGER.loadInstance();
        Events.registerEvents();
        setIsClientAvailable();

        //? if <26.1
        //RegistryHelper.initClass();
    }

    public static boolean isModLoaded(String modId) {
        //? if fabric {
        return FabricLoader.getInstance().isModLoaded(modId);
         //?} else
        //return ModList.get().isLoaded(modId);
    }

    private static void setIsClientAvailable() {
        try {
            Minecraft mc = Minecraft.getInstance();
            isClientAvailable = mc != null;
        } catch (Exception e) {
            isClientAvailable = false;
        }
    }

    public static Path getConfigPath() {
        //? if fabric {
        Path configDirPath = FabricLoader.getInstance().getConfigDir();
        //?} else
        //Path configDirPath = FMLPaths.CONFIGDIR.get();

        return configDirPath.resolve(MOD_ID + ".json5");
    }

    //? if neoforge {
    /*public static void registerConfigScreen() {
        ModLoadingContext.get().getActiveContainer()
                .registerExtensionPoint(IConfigScreenFactory.class, (modContainer, screen) -> ConfigScreenProvider.getConfigScreen(screen));
    }
    *///?}
}
