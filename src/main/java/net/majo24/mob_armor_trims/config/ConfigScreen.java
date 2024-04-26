package net.majo24.mob_armor_trims.config;

import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.majo24.mob_armor_trims.MobArmorTrims;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ConfigScreen {
    private ConfigScreen() {}

    public static Screen getConfigScreen(Screen parent) {
           ConfigBuilder builder = ConfigBuilder.create()
        .setParentScreen(parent)
        .setTitle(Text.literal(MobArmorTrims.MOD_ID));
        builder.setSavingRunnable(MobArmorTrims.configManager::saveConfig);

        ConfigCategory general = builder.getOrCreateCategory(Text.literal("General"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        general.addEntry(entryBuilder.startIntSlider(Text.literal("Trim Chance"), MobArmorTrims.configManager.getTrimChance(), 0, 100)
                        .setDefaultValue(ConfigManager.DEFAULT_TRIM_CHANCE)
                        .setTooltip(Text.literal("Chance of each armor piece\nof a mob having an armor trim"))
                        .setSaveConsumer(MobArmorTrims.configManager::setTrimChance)
                        .build());

        if (FabricLoader.getInstance().isModLoaded("stacked_trims")) {
            general.addEntry(entryBuilder.startIntSlider(Text.literal("Stacked Trim Chance"), MobArmorTrims.configManager.getStackedTrimChance(), 0, 100)
                            .setDefaultValue(ConfigManager.DEFAULT_STACKED_TRIM_CHANCE)
                            .setTooltip(Text.literal("Chance of each armor piece having an additional armor trim on it\nwhen the Stacked Armor Trims mod is enabled"))
                            .setSaveConsumer(MobArmorTrims.configManager::setStackedTrimChance)
                            .build());

            general.addEntry(entryBuilder.startIntSlider(Text.literal("Max Stacked Trims"), MobArmorTrims.configManager.getMaxStackedTrims(), 0, 100)
                            .setDefaultValue(ConfigManager.DEFAULT_MAX_STACKED_TRIMS)
                            .setTooltip(Text.literal("The maximum amount of armor trims that can be stacked on each other\nwhen the Stacked Armor Trims mod is enabled"))
                            .setSaveConsumer(MobArmorTrims.configManager::setMaxStackedTrims)
                            .build());
        }

        return builder.build();
    }
}
