package net.mob_armor_trims.majo24.config.configscreen;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.mob_armor_trims.majo24.MobArmorTrims;
import net.mob_armor_trims.majo24.config.Config;
import net.mob_armor_trims.majo24.config.ConfigManager;

import java.util.ArrayList;

import static net.minecraft.network.chat.Component.literal;

public class ConfigScreen {
    private ConfigScreen() {}

    public static final OptionDescription trimSytemOptionDescription = OptionDescription.of(literal("""
        Select the System of how to select, what trims to give mobs.
        - Random Trims: Randomly choose the trim, but also take the previous trim highly into account.
        - Custom Trims: Choose the trim out of a list of custom trims. You can manage the trims yourself"""));

    public static final OptionDescription customTrimsListOptionDescription = OptionDescription.of(literal("""
            Manage the list of custom trims here. You can add, edit and remove custom trims.
            
            In the left option, enter a valid trim material.
            As an example: "quartz".
            
            In the right option, enter a valid trim pattern.
            As an example: "silence"
            
            To not have to specify the whole trim pattern, you can leave out the "_armor_trim_smithing_template" part of the pattern, as it is the same for every pattern.
            """));

    public static final Formatters.IntegerToPercentage integerToPercentageFormatter = new Formatters.IntegerToPercentage();
    public static final Formatters.TrimSystem trimSystemFormatter = new Formatters.TrimSystem();

    public static Screen getConfigScreen(Screen parent) {
        YetAnotherConfigLib.Builder configScreen = YetAnotherConfigLib.createBuilder()
                .title(literal("Mob Armor Trims"))
                .save(MobArmorTrims.configManager::saveConfig)

                .category(buildGeneralCategory())
                .category(buildRandomTrimsCategory())
                .category(buildCustomTrimsCategory());

        if (MobArmorTrims.isStackedArmorTrimsLoaded) {
            configScreen.category(buildStackedTrimsCategory());
        }

        return configScreen.build().generateScreen(parent);
    }

    private static ConfigCategory buildGeneralCategory() {
        return ConfigCategory.createBuilder()
            .name(literal("General"))
            .tooltip(literal("General settings for this mod."))
            .option(Option.<Config.TrimSystem>createBuilder()
                .name(literal("Trim System"))
                .description(trimSytemOptionDescription)
                .binding(Config.TrimSystem.RANDOM_TRIMS,
                        () -> MobArmorTrims.configManager.getEnabledSystem(),
                        enabledSystem -> MobArmorTrims.configManager.setEnabledSystem(enabledSystem))
                .controller(opt -> EnumControllerBuilder.create(opt)
                        .enumClass(Config.TrimSystem.class)
                        .formatValue(trimSystemFormatter))
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
                        .formatValue(integerToPercentageFormatter))
                .build())
            .build();
    }

    private static ConfigCategory buildRandomTrimsCategory() {
        return ConfigCategory.createBuilder()
            .name(literal("Random Trims"))
            .tooltip(literal("Settings for the Random Trims System.\nThese settings will only make a difference, if the random trims system is enabled."))

            .option(Option.<Integer>createBuilder()
                .name(literal("Trim Chance"))
                .description(OptionDescription.of(literal("Chance of each armor piece of a mob having an armor trim")))
                .binding(ConfigManager.DEFAULT_TRIM_CHANCE,
                        () -> MobArmorTrims.configManager.getTrimChance(),
                        trimsChance -> MobArmorTrims.configManager.setTrimChance(trimsChance))
                .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                        .range(0, 100)
                        .step(1)
                        .formatValue(integerToPercentageFormatter))
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
                        .formatValue(integerToPercentageFormatter))
                .build())

            .build();
    }

    private static ConfigCategory buildCustomTrimsCategory() {
        return ConfigCategory.createBuilder()
            .name(literal("Custom Trims"))
            .tooltip(literal("Settings for the Custom Trims System.\nThese settings will only make a difference, if the custom trims system is enabled."))

            .group(ListOption.<Config.CustomTrim>createBuilder()
                .name(literal("Custom Trims List"))
                .description(customTrimsListOptionDescription)
                .binding(new ArrayList<>(),
                        () -> MobArmorTrims.configManager.getCustomTrimsList(),
                        customTrimsList -> MobArmorTrims.configManager.setCustomTrimsList(customTrimsList))
                .controller(opt -> CustomTrimsListController.Builder.create(opt)
                        .patternController(StringControllerBuilder::create)
                        .materialController(StringControllerBuilder::create))
                .initial(new Config.CustomTrim("", ""))
                .build())

            .option(Option.<Boolean>createBuilder()
                    .name(literal("Apply To Entire Armor"))
                    .description(OptionDescription.of(literal("Should the custom armor trim be applied to the entire armor.\nIf false, a new custom trim will be chosen for each armor piece")))
                    .binding(ConfigManager.DEFAULT_APPLY_TO_ENTIRE_ARMOR,
                            () -> MobArmorTrims.configManager.getApplyToEntireArmor(),
                            applyToEntireArmor -> MobArmorTrims.configManager.setApplyToEntireArmor(applyToEntireArmor))
                    .controller(BooleanControllerBuilder::create)
                    .build())

            .build();
    }

    private static ConfigCategory buildStackedTrimsCategory() {
        return ConfigCategory.createBuilder()
            .name(literal("Stacked Trims"))
            .tooltip(literal("Settings for the Stacked Armor Trims mod compatibility.\nThese settings will only make a difference, if the Stacked Armor Trims mod is used and the random trims System is enabled."))

            .option(Option.<Integer>createBuilder()
                .name(literal("Stacked Trim Chance"))
                .description(OptionDescription.of(literal("Chance of each armor piece having an additional armor trim on it when the Stacked Armor Trims mod is enabled")))
                .binding(ConfigManager.DEFAULT_STACKED_TRIM_CHANCE,
                        () -> MobArmorTrims.configManager.getStackedTrimChance(),
                        stackedTrimChance -> MobArmorTrims.configManager.setStackedTrimChance(stackedTrimChance))
                .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                    .range(0, 100)
                    .step(1)
                    .formatValue(integerToPercentageFormatter))
                .build())

            .option(Option.<Integer>createBuilder()
                .name(literal("Max Stacked Trims"))
                .description(OptionDescription.of(literal("The maximum amount of armor trims that can be stacked on each other when the Stacked Armor Trims mod is enabled")))
                .binding(ConfigManager.DEFAULT_MAX_STACKED_TRIMS,
                        () -> MobArmorTrims.configManager.getMaxStackedTrims(),
                        maxStackedTrims -> MobArmorTrims.configManager.setMaxStackedTrims(maxStackedTrims))
                .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                    .range(0, 5)
                    .step(1))
                .build())
            .build();
    }

    static class BackupScreen extends OptionsSubScreen {
        public BackupScreen(Screen parent) {
            super(parent, Minecraft.getInstance().options, Component.literal("screen, default"));
        }
                @Override
        public void init() {
            MultiLineTextWidget messageWidget = new MultiLineTextWidget(
                    width / 2 - 100, height / 2 - 40,
                    Component.literal("Install Yet Another Config Lib to access the config screen."),
                    minecraft.font);
            messageWidget.setMaxWidth(240);
            messageWidget.setCentered(true);
            addRenderableWidget(messageWidget);

            Button openLinkButton = Button.builder(Component.literal("View on Modrinth"),
                            button -> minecraft.setScreen(new ConfirmLinkScreen(
                                    open -> {
                                        if (open) Util.getPlatform().openUri("https://modrinth.com/mod/yacl");
                                        minecraft.setScreen(lastScreen);
                                    }, "https://modrinth.com/mod/yacl", true)))
                    .pos(width / 2 - 120, height / 2)
                    .size(115, 20)
                    .build();
            addRenderableWidget(openLinkButton);

            Button exitButton = Button.builder(CommonComponents.GUI_OK,
                            button -> onClose())
                    .pos(width / 2 + 5, height / 2)
                    .size(115, 20)
                    .build();
            addRenderableWidget(exitButton);
        }
    }
}