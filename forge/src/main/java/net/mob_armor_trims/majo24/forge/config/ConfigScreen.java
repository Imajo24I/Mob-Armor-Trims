package net.mob_armor_trims.majo24.forge.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.mob_armor_trims.majo24.MobArmorTrims;
import net.mob_armor_trims.majo24.config.ConfigManager;

public class ConfigScreen {
    private ConfigScreen() {}

    public static Screen getConfigScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
        .setParentScreen(parent)
        .setTitle(Component.literal("Mob Armor Trims"));
        builder.setSavingRunnable(MobArmorTrims.configManager::saveConfig);

        ConfigCategory general = builder.getOrCreateCategory(Component.literal("General"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        general.addEntry(entryBuilder.startIntSlider(Component.literal("Trim Chance"), MobArmorTrims.configManager.getTrimChance(), 0, 100)
                        .setDefaultValue(ConfigManager.DEFAULT_TRIM_CHANCE)
                        .setTooltip(Component.literal("Chance of each armor piece\nof a mob having an armor trim"))
                        .setSaveConsumer(MobArmorTrims.configManager::setTrimChance)
                        .build());

        general.addEntry(entryBuilder.startIntSlider(Component.literal("Similar Trim Chance"), MobArmorTrims.configManager.getSimilarTrimChance(), 0, 100)
                        .setDefaultValue(ConfigManager.DEFAULT_SIMILAR_TRIM_CHANCE)
                        .setTooltip(Component.literal("Chance of each armor piece having\na similar armor trim as the previous armor piece"))
                        .setSaveConsumer(MobArmorTrims.configManager::setSimilarTrimChance)
                        .build());

        if (MobArmorTrims.isStackedArmorTrimsLoaded) {
            general.addEntry(entryBuilder.startIntSlider(Component.literal("Stacked Trim Chance"), MobArmorTrims.configManager.getStackedTrimChance(), 0, 100)
                            .setDefaultValue(ConfigManager.DEFAULT_STACKED_TRIM_CHANCE)
                            .setTooltip(Component.literal("Chance of each armor piece having an additional armor trim on it\nwhen the Stacked Armor Trims mod is enabled"))
                            .setSaveConsumer(MobArmorTrims.configManager::setStackedTrimChance)
                            .build());

            general.addEntry(entryBuilder.startIntSlider(Component.literal("Max Stacked Trims"), MobArmorTrims.configManager.getMaxStackedTrims(), 0, 5)
                            .setDefaultValue(ConfigManager.DEFAULT_MAX_STACKED_TRIMS)
                            .setTooltip(Component.literal("The maximum amount of armor trims that can be stacked on each other\nwhen the Stacked Armor Trims mod is enabled"))
                            .setSaveConsumer(MobArmorTrims.configManager::setMaxStackedTrims)
                            .build());
        }

        return builder.build();
    }
}