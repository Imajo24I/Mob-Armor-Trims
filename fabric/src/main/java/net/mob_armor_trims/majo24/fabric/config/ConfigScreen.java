package net.mob_armor_trims.majo24.fabric.config;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.mob_armor_trims.majo24.MobArmorTrims;
import net.mob_armor_trims.majo24.config.ConfigManager;
import net.mob_armor_trims.majo24.config.TrimSystem;

import static net.minecraft.network.chat.Component.literal;

public class ConfigScreen {
    private ConfigScreen() {}

    public static final OptionDescription trimSytemOptionDescription = OptionDescription.of(literal("""
        Select the System of how to select, what trims to give mobs.
        - Random Trims: Randomly choose the trim, but also take the previous trim highly into account.
        - Custom Trims: Choose the trim out of a list of custom trims. You can manage the trims yourself"""));

    public static final OptionDescription customTrimsListOptionDescription = OptionDescription.of(literal("""
            Manage the list of custom trims here. You can add, edit and remove custom trims.
            """));

    public static Screen getConfigScreen(Screen parent) {
        return YetAnotherConfigLib.createBuilder()
                .title(literal("Mob Armor Trims"))
                .save(MobArmorTrims.configManager::saveConfig)
                .category(ConfigCategory.createBuilder()
                        .name(literal("General"))
                        .tooltip(literal("General settings for this mod."))
                        .option(Option.<TrimSystem>createBuilder()
                                .name(literal("Trim System"))
                                .description(trimSytemOptionDescription)
                                .binding(TrimSystem.RANDOM_TRIMS,
                                        () -> MobArmorTrims.configManager.getEnabledSystem(),
                                        enabledSystem -> MobArmorTrims.configManager.setEnabledSystem(enabledSystem))
                                .controller(opt -> EnumControllerBuilder.create(opt)
                                        .enumClass(TrimSystem.class)
                                        .valueFormatter(v -> {
                                            switch (v) {
                                                case RANDOM_TRIMS -> {
                                                    return literal("Random Trims");
                                                }
                                                case CUSTOM_TRIMS -> {
                                                    return literal("Custom Trims");
                                                }
                                                default -> {
                                                    return literal("Error: Unknown Trim System");
                                                }
                                            }
                                        }))
                                .build())
                        .build())

                .category(ConfigCategory.createBuilder()
                        .name(literal("Random Trims"))
                        .tooltip(literal("Settings for the Random Trims System."))
                        .option(Option.<Integer>createBuilder()
                                .name(literal("Trim Chance"))
                                .description(OptionDescription.of(literal("Chance of each armor piece of a mob having an armor trim")))
                                .binding(ConfigManager.DEFAULT_TRIM_CHANCE,
                                        () -> MobArmorTrims.configManager.getTrimChance(),
                                        trimsChance -> MobArmorTrims.configManager.setTrimChance(trimsChance))
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                        .range(0, 100)
                                        .step(1)
                                        .valueFormatter(v -> literal(v.toString() + "%")))
                                .build())

                        .option(Option.<Integer>createBuilder()
                                .name(literal("Similar Trim Chance"))
                                .description(OptionDescription.of(literal("Chance of each armor piece having a similar armor trim as the previous armor piece")))
                                .binding(ConfigManager.DEFAULT_SIMILAR_TRIM_CHANCE,
                                        () -> MobArmorTrims.configManager.getSimilarTrimChance(),
                                        similarTrimChance -> MobArmorTrims.configManager.setSimilarTrimChance(similarTrimChance))
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                        .range(0, 100)
                                        .step(1)
                                        .valueFormatter(v -> literal(v.toString() + "%")))
                                .build())

                        .option(Option.<Integer>createBuilder()
                                .name(literal("No Trims Chance"))
                                .description(OptionDescription.of(literal("Chance of the mob having no trims at all")))
                                .binding(ConfigManager.DEFAULT_NO_TRIMS_CHANCE,
                                        () -> MobArmorTrims.configManager.getNoTrimsChance(),
                                        noTrimsChance -> MobArmorTrims.configManager.setNoTrimsChance(noTrimsChance))
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                        .range(0, 100)
                                        .step(1)
                                        .valueFormatter(v -> literal(v.toString() + "%")))
                                .build())

                        .build())

                .category(ConfigCategory.createBuilder()
                        .name(literal("Custom Trims"))
                        .tooltip(literal("Settings for the Custom Trims System."))
                        .option(ListOption.<String>createBuilder()
                                .name(literal("Custom Trims List"))
                                .description(customTrimsListOptionDescription)
                                .binding(MobArmorTrims.configManager.getCustomTrimsList(),
                                        () -> MobArmorTrims.configManager.getCustomTrimsList(),
                                        customTrimsList -> MobArmorTrims.configManager.setCustomTrimsList(customTrimsList))
                                .controller(StringControllerBuilder::create)
                                .initial("")
                                .build())
                        .build())

                .build()
                .generateScreen(parent);
    }
}