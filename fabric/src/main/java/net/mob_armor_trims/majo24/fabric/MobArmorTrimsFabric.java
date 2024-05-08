package net.mob_armor_trims.majo24.fabric;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.loader.api.FabricLoader;
import net.mob_armor_trims.majo24.MobArmorTrims;

import java.nio.file.Path;

public final class MobArmorTrimsFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        MobArmorTrims.isStackedArmorTrimsLoaded = FabricLoader.getInstance().isModLoaded("stacked_trims");
        Path configPath = FabricLoader.getInstance().getConfigDir().resolve(MobArmorTrims.MOD_ID + ".json");
        // Run our common setup.
        MobArmorTrims.init(configPath);
    }
}
