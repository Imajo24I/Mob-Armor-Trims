package net.mob_armor_trims.majo24.neoforge;

import dev.architectury.platform.Platform;
import net.mob_armor_trims.majo24.MobArmorTrims;
import net.mob_armor_trims.majo24.neoforge.config.ConfigScreen;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.ConfigScreenHandler;

@Mod(MobArmorTrims.MOD_ID)
public final class MobArmorTrimsNeoforge {
    public MobArmorTrimsNeoforge() {
        MobArmorTrims.isStackedArmorTrimsLoaded = ModList.get().isLoaded("stacked_trims");

        // Register Config Screen
        if (ModList.get().isLoaded("cloth-config")) {
            ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                    () -> new ConfigScreenHandler.ConfigScreenFactory((client, parent) -> ConfigScreen.getConfigScreen(parent)));
        }

        // Run our common setup.
        MobArmorTrims.init(Platform.getConfigFolder().resolve("mob_armor_trims.json"));
    }
}
