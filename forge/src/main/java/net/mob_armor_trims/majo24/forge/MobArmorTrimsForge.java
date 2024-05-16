package net.mob_armor_trims.majo24.forge;

import dev.architectury.platform.Platform;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import net.minecraftforge.fml.loading.FMLPaths;
import net.mob_armor_trims.majo24.MobArmorTrims;
import net.mob_armor_trims.majo24.forge.config.ConfigScreen;

@Mod(MobArmorTrims.MOD_ID)
public final class MobArmorTrimsForge {
    public MobArmorTrimsForge() {
        // Submit our event bus to let Architectury API register our content on the right time.
        EventBuses.registerModEventBus(MobArmorTrims.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());

        MobArmorTrims.isStackedArmorTrimsLoaded = ModList.get().isLoaded("stacked_trims");

        // Register Config Screen
        if (ModList.get().isLoaded("cloth_config")) {
            ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                    () -> new ConfigScreenHandler.ConfigScreenFactory((client, parent) -> ConfigScreen.getConfigScreen(parent)));
        }

        // Run our common setup.
        MobArmorTrims.init(Platform.getConfigFolder().resolve("mob_armor_trims.json"));
    }
}
