package net.mob_armor_trims.majo24.forge;

import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;

import net.minecraftforge.fml.loading.FMLPaths;
import net.mob_armor_trims.majo24.MobArmorTrims;
import net.mob_armor_trims.majo24.config.configscreen.ConfigScreenProvider;

@Mod(MobArmorTrims.MOD_ID)
public final class MobArmorTrimsForge {
    public MobArmorTrimsForge() {
        MobArmorTrims.isStackedArmorTrimsLoaded = ModList.get().isLoaded("stacked_trims");

        // Register Config Screen
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory((client, parent) -> ConfigScreenProvider.getConfigScreen(parent)));

        // Run our common setup.
        MobArmorTrims.init(FMLPaths.CONFIGDIR.get().resolve("mob_armor_trims.toml"));
    }
}
