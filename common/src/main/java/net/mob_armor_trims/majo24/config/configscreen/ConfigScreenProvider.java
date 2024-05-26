package net.mob_armor_trims.majo24.config.configscreen;

import net.minecraft.client.gui.screens.Screen;

public class ConfigScreenProvider {
    private ConfigScreenProvider() {}

    public static Screen getConfigScreen(Screen parent) {
        try {
            return ConfigScreen.getConfigScreen(parent);
        } catch (NoClassDefFoundError ignored) {
            // provide back-up screen if YACL is not loaded
            return new ConfigScreen.BackupScreen(parent);
        }
    }
}