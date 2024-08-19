package net.majo24.mob_armor_trims.config.screen;

import net.minecraft.client.gui.screens.Screen;

public class ConfigScreenProvider {
    private ConfigScreenProvider() {}

    /**
     * Returns the config screen of this mod
     * @param parent The parent screen
     * @return Config screen if YACL is loaded. Else the back-up screen which recommends installing YACL
     */
    public static Screen getConfigScreen(Screen parent) {
        try {
            return ConfigScreen.getConfigScreen(parent);
        } catch (NoClassDefFoundError ignored) {
            // provide back-up screen if YACL is not loaded
            return new ConfigScreen.BackupScreen(parent);
        }
    }
}
