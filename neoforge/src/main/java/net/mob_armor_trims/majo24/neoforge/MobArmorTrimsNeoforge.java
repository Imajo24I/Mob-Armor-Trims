package net.mob_armor_trims.majo24.neoforge;

import net.mob_armor_trims.majo24.MobArmorTrims;
import net.mob_armor_trims.majo24.neoforge.config.ConfigScreen;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(MobArmorTrims.MOD_ID)
public final class MobArmorTrimsNeoforge {
    public MobArmorTrimsNeoforge() {
        MobArmorTrims.isStackedArmorTrimsLoaded = ModList.get().isLoaded("stacked_trims");

        // Register Config Screen
        if (ModList.get().isLoaded("yet_another_config_lib_v3")) {
            ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class,
                    () -> (client, parent) -> ConfigScreen.getConfigScreen(parent));
        }


        // Run our common setup.
        MobArmorTrims.init(FMLPaths.CONFIGDIR.get().resolve("mob_armor_trims.json"));
    }
}
