package net.mob_armor_trims.majo24.fabric.config;

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

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        ConfigCategory general = builder.getOrCreateCategory(Component.literal("General"));

        general.addEntry(entryBuilder.startIntSlider(Component.literal("Trim Chance"), MobArmorTrims.configManager.getTrimChance(), 0, 100)
                        .setDefaultValue(ConfigManager.DEFAULT_TRIM_CHANCE)
                        .setTooltip(Component.literal("Chance of each armor piece of\na mob having an armor trim"))
                        .setSaveConsumer(MobArmorTrims.configManager::setTrimChance)
                        .build());

        general.addEntry(entryBuilder.startIntSlider(Component.literal("Similar Trim Chance"), MobArmorTrims.configManager.getSimilarTrimChance(), 0, 100)
                .setDefaultValue(ConfigManager.DEFAULT_SIMILAR_TRIM_CHANCE)
                .setTooltip(Component.literal("Chance of each armor piece having a similar armor\ntrim as the previous armor piece"))
                .setSaveConsumer(MobArmorTrims.configManager::setSimilarTrimChance)
                .build());

        general.addEntry(entryBuilder.startIntSlider(Component.literal("No Trims Chance"), MobArmorTrims.configManager.getNoTrimsChance(), 0, 100)
                .setDefaultValue(ConfigManager.DEFAULT_NO_TRIMS_CHANCE)
                .setTooltip(Component.literal("Chance of the mob having no trims at all"))
                .setSaveConsumer(MobArmorTrims.configManager::setNoTrimsChance)
                .build());

        if (MobArmorTrims.isStackedArmorTrimsLoaded) {
            ConfigCategory stackedArmorTrimsCategory = builder.getOrCreateCategory(Component.literal("Stacked Armor Trims"));
            stackedArmorTrimsCategory.addEntry(entryBuilder.startIntSlider(Component.literal("Stacked Trim Chance"), MobArmorTrims.configManager.getStackedTrimChance(), 0, 100)
                            .setDefaultValue(ConfigManager.DEFAULT_STACKED_TRIM_CHANCE)
                            .setTooltip(Component.literal("Chance of each armor piece having an additional armor\ntrim on it when the Stacked Armor Trims mod is enabled"))
                            .setSaveConsumer(MobArmorTrims.configManager::setStackedTrimChance)
                            .build());

            stackedArmorTrimsCategory.addEntry(entryBuilder.startIntSlider(Component.literal("Max Stacked Trims"), MobArmorTrims.configManager.getMaxStackedTrims(), 0, 5)
                            .setDefaultValue(ConfigManager.DEFAULT_MAX_STACKED_TRIMS)
                            .setTooltip(Component.literal("The maximum amount of armor trims that can be stacked on\neach other when the Stacked Armor Trims mod is enabled"))
                            .setSaveConsumer(MobArmorTrims.configManager::setMaxStackedTrims)
                            .build());
        }

        return builder.build();
    }
}