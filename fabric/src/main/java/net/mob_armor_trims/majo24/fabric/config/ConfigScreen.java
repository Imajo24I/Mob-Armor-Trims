package net.mob_armor_trims.majo24.fabric.config;

import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.impl.builders.DropdownMenuBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.armortrim.TrimMaterials;
import net.mob_armor_trims.majo24.MobArmorTrims;
import net.mob_armor_trims.majo24.RandomTrims;
import net.mob_armor_trims.majo24.config.ConfigManager;

import java.util.ArrayList;

public class ConfigScreen {
    private ConfigScreen() {}

    public static Screen getConfigScreen(Screen parent) {
           ConfigBuilder builder = ConfigBuilder.create()
        .setParentScreen(parent)
        .setTitle(Component.literal("Mob Armor Trims"));
        builder.setSavingRunnable(MobArmorTrims.configManager::saveConfig);

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        // Default System
        ConfigCategory general = builder.getOrCreateCategory(Component.literal("General"));

        ArrayList<String> systemSuggestions = new ArrayList<>();
        systemSuggestions.add("random Trims");
        systemSuggestions.add("custom Trims");
        general.addEntry(entryBuilder.startStringDropdownMenu(Component.literal("Determine Trims System"), MobArmorTrims.configManager.getEnabledSystem())
                        .setDefaultValue(ConfigManager.DEFAULT_ENABLED_SYSTEM)
                        .setTooltip(Component.literal("""
                                Select the system used to
                                determine what trims to apply.
                                - random Trims: Apply random trims
                                  to the mob, while also highly taking
                                  previously applied trims into account
                                - custom Trims: Choose a random trim
                                  out of a list of pre-defined trims
                                  and apply it to the mobs armor.
                                  You can yourself edit this list"""))
                        .setSaveConsumer(MobArmorTrims.configManager::setEnabledSystem)
                        .setSelections(systemSuggestions)
                        .build());

        ConfigCategory randomTrims = builder.getOrCreateCategory(Component.literal("Random Trims"));

        randomTrims.addEntry(entryBuilder.startIntSlider(Component.literal("Trim Chance"), MobArmorTrims.configManager.getTrimChance(), 0, 100)
                        .setDefaultValue(ConfigManager.DEFAULT_TRIM_CHANCE)
                        .setTooltip(Component.literal("Chance of each armor piece of\na mob having an armor trim"))
                        .setSaveConsumer(MobArmorTrims.configManager::setTrimChance)
                        .build());

        randomTrims.addEntry(entryBuilder.startIntSlider(Component.literal("Similar Trim Chance"), MobArmorTrims.configManager.getSimilarTrimChance(), 0, 100)
                .setDefaultValue(ConfigManager.DEFAULT_SIMILAR_TRIM_CHANCE)
                .setTooltip(Component.literal("Chance of each armor piece having a similar armor\ntrim as the previous armor piece"))
                .setSaveConsumer(MobArmorTrims.configManager::setSimilarTrimChance)
                .build());

        randomTrims.addEntry(entryBuilder.startIntSlider(Component.literal("No Trims Chance"), MobArmorTrims.configManager.getNoTrimsChance(), 0, 100)
                .setDefaultValue(ConfigManager.DEFAULT_NO_TRIMS_CHANCE)
                .setTooltip(Component.literal("Chance of the mob having no trims at all"))
                .setSaveConsumer(MobArmorTrims.configManager::setNoTrimsChance)
                .build());

        // Custom Trims system
        ConfigCategory customTrims = builder.getOrCreateCategory(Component.literal("Custom Trims"));

        customTrims.addEntry(entryBuilder.startStrField(Component.literal("Trim Material"), MobArmorTrims.configManager.getSelectedMaterial())
                        .setDefaultValue(ConfigManager.DEFAULT_SELECTED_MATERIAL)
                        .setTooltip(Component.literal("""
                                Enter the material you want the
                                custom trim to have.
                                To select a material, use the SNBT,
                                also know as data tag,
                                of the material.
                                This would look something like this:
                                minecraft:quartz"""))
                        .setSaveConsumer(MobArmorTrims.configManager::setSelectedMaterial)
                        .build());

        customTrims.addEntry(entryBuilder.startStrField(Component.literal("Trim Pattern"), MobArmorTrims.configManager.getSelectedPattern())
                        .setDefaultValue(ConfigManager.DEFAULT_SELECTED_PATTERN)
                        .setTooltip(Component.literal("""
                                Enter the pattern you want the
                                custom trim to have.
                                To select a pattern, use the SNBT,
                                also know as data tag,
                                of the pattern.
                                This would look something like this:
                                minecraft:silence"""))
                        .setSaveConsumer(MobArmorTrims.configManager::setSelectedPattern)
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