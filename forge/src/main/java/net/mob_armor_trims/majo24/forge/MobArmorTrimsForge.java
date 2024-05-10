package net.mob_armor_trims.majo24.forge;

// import dev.architectury.platform.Platform;
// import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.mob_armor_trims.majo24.MobArmorTrims;

@Mod(MobArmorTrims.MOD_ID)
public final class MobArmorTrimsForge {
    public MobArmorTrimsForge() {
        // Submit our event bus to let Architectury API register our content on the right time.
        // EventBuses.registerModEventBus(MobArmorTrims.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());

        MobArmorTrims.isStackedArmorTrimsLoaded = ModList.get().isLoaded("stacked_trims");

        // Run our common setup.
        // MobArmorTrims.init(Platform.getConfigFolder().resolve("mob_armor_trims.json"));
    }
}
