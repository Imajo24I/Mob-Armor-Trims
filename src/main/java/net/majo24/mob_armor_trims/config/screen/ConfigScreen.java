package net.majo24.mob_armor_trims.config.screen;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.ValueFormatter;
import net.majo24.mob_armor_trims.config.Config;
import net.majo24.mob_armor_trims.trim_combinations_system.CustomTrim;
import net.majo24.mob_armor_trims.trim_combinations_system.TrimCombination;
import net.majo24.mob_armor_trims.config.screen.controllers.TrimCombinationsController;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.majo24.mob_armor_trims.MobArmorTrims;
import org.jetbrains.annotations.NotNull;

import static net.majo24.mob_armor_trims.MobArmorTrims.configManager;

/*? <1.21 {*/
/*import net.minecraft.client.gui.screens.OptionsSubScreen;
 *//*?} else {*/
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
/*?}*/

import static net.minecraft.network.chat.Component.translatable;

public class ConfigScreen {
    private ConfigScreen() {
    }

    public static final Formatters.IntegerToPercentage integerToPercentageFormatter = new Formatters.IntegerToPercentage();
    public static final Formatters.TrimSystem trimSystemFormatter = new Formatters.TrimSystem();

    public static Screen getConfigScreen(Screen parent) {
        YetAnotherConfigLib.Builder configScreen = YetAnotherConfigLib.createBuilder()
                .title(Component.literal("Mob Armor Trims"))
                .save(configManager::saveConfigToFile)

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
                .name(translatable("mob_armor_trims.config.general"))
                .tooltip(translatable("mob_armor_trims.config.general.tooltip"))
                .option(Option.<Config.TrimSystems>createBuilder()
                        .name(translatable("mob_armor_trims.config.general.trimSystem"))
                        .description(OptionDescription.of(translatable("mob_armor_trims.config.general.trimSystem.description")))
                        .binding(Config.TrimSystems.RANDOM_TRIMS,
                                () -> configManager.getConfig().general.enabledSystem.getValue(),
                                enabledSystem -> configManager.getConfig().general.enabledSystem.setValue(enabledSystem))
                        .controller(opt -> EnumControllerBuilder.create(opt)
                                .enumClass(Config.TrimSystems.class)
                                .formatValue(trimSystemFormatter))
                        .build())

                .option(Option.<Integer>createBuilder()
                        .name(translatable("mob_armor_trims.config.general.noTrimsChance"))
                        .description(OptionDescription.of(translatable("mob_armor_trims.config.general.noTrimsChance.description")))
                        .binding(configManager.getConfig().general.noTrimsChance.getDefaultValue(),
                                () -> configManager.getConfig().general.noTrimsChance.getValue(),
                                noTrimsChance -> configManager.getConfig().general.noTrimsChance.setValue(noTrimsChance))
                        .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                .range(0, 100)
                                .step(1)
                                .formatValue(integerToPercentageFormatter))
                        .build())
                .build();
    }

    private static ConfigCategory buildRandomTrimsCategory() {
        return ConfigCategory.createBuilder()
                .name(translatable("mob_armor_trims.config.randomTrims"))
                .tooltip(translatable("mob_armor_trims.config.randomTrims.tooltip"))

                .option(Option.<Integer>createBuilder()
                        .name(translatable("mob_armor_trims.config.randomTrims.trimChance"))
                        .description(OptionDescription.of(translatable("mob_armor_trims.config.randomTrims.trimChance.description")))
                        .binding(configManager.getConfig().randomTrims.trimChance.getDefaultValue(),
                                () -> configManager.getConfig().randomTrims.trimChance.getValue(),
                                trimsChance -> configManager.getConfig().randomTrims.trimChance.setValue(trimsChance))
                        .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                .range(0, 100)
                                .step(1)
                                .formatValue(integerToPercentageFormatter))
                        .build())

                .option(Option.<Integer>createBuilder()
                        .name(translatable("mob_armor_trims.config.randomTrims.similarTrimChance"))
                        .description(OptionDescription.of(translatable("mob_armor_trims.config.randomTrims.similarTrimChance.description")))
                        .binding(configManager.getConfig().randomTrims.similarTrimChance.getDefaultValue(),
                                () -> configManager.getConfig().randomTrims.similarTrimChance.getValue(),
                                similarTrimChance -> configManager.getConfig().randomTrims.similarTrimChance.setValue(similarTrimChance))
                        .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                .range(0, 100)
                                .step(1)
                                .formatValue(integerToPercentageFormatter))
                        .build())

                .build();
    }

    private static ConfigCategory buildCustomTrimsCategory() {
        return ConfigCategory.createBuilder()
                .name(translatable("mob_armor_trims.config.customTrimCombinations"))
                .tooltip(translatable("mob_armor_trims.config.customTrimCombinations.tooltip"))

                .group(ListOption.<TrimCombination>createBuilder()
                        .name(translatable("mob_armor_trims.config.customTrimCombinations"))
                        .description(OptionDescription.of(translatable("mob_armor_trims.config.customTrimCombinations.trimCombinations.description")))
                        .binding(configManager.getConfig().customTrimCombinations.trimCombinations.getDefaultTrimCombinations(),
                                () -> configManager.getConfig().customTrimCombinations.trimCombinations.getTrimCombinations(),
                                trimCombinations -> configManager.getConfig().customTrimCombinations.trimCombinations.setTrimCombinations(trimCombinations))
                        .controller(TrimCombinationsController.Builder::create)
                        .initial(new TrimCombination("", CustomTrim.EMPTY, CustomTrim.EMPTY, CustomTrim.EMPTY, CustomTrim.EMPTY))
                        .build())
                .build();
    }

    private static ConfigCategory buildStackedTrimsCategory() {
        return ConfigCategory.createBuilder()
                .name(translatable("mob_armor_trims.config.stackedTrims"))
                .tooltip(translatable("mob_armor_trims.config.stackedTrims.tooltip"))

                .option(Option.<Integer>createBuilder()
                        .name(translatable("mob_armor_trims.config.stackedTrims.stackedTrimsChance"))
                        .description(OptionDescription.of(translatable("mob_armor_trims.config.stackedTrims.stackedTrimsChance.description")))
                        .binding(configManager.getConfig().stackedTrims.stackedTrimChance.getDefaultValue(),
                                () -> configManager.getConfig().stackedTrims.stackedTrimChance.getValue(),
                                stackedTrimChance -> configManager.getConfig().stackedTrims.stackedTrimChance.setValue(stackedTrimChance))
                        .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                .range(0, 100)
                                .step(1)
                                .formatValue(integerToPercentageFormatter))
                        .build())

                .option(Option.<Integer>createBuilder()
                        .name(translatable("mob_armor_trims.config.stackedTrims.maxStackedTrims"))
                        .description(OptionDescription.of(translatable("mob_armor_trims.config.stackedTrims.maxStackedTrims.description")))
                        .binding(configManager.getConfig().stackedTrims.maxStackedTrims.getDefaultValue(),
                                () -> configManager.getConfig().stackedTrims.maxStackedTrims.getValue(),
                                maxStackedTrims -> configManager.getConfig().stackedTrims.maxStackedTrims.setValue(maxStackedTrims))
                        .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                .range(0, 5)
                                .step(1))
                        .build())
                .build();
    }

    public static class Formatters {
        public static class IntegerToPercentage implements ValueFormatter<Integer> {
            @Override
            public Component format(Integer value) {
                return Component.literal(value.toString() + "%");
            }
        }

        public static class TrimSystem implements ValueFormatter<Config.TrimSystems> {
            @Override
            public Component format(Config.TrimSystems selectedSystem) {
                return switch (selectedSystem) {
                    case RANDOM_TRIMS -> Component.literal("Random Trims");
                    case CUSTOM_TRIM_COMBINATIONS -> Component.literal("Custom Trim Combinations");
                    case NONE -> Component.literal("Disabled");
                };
            }
        }
    }

    static class BackupScreen extends OptionsSubScreen {
        public BackupScreen(Screen parent) {
            super(parent, Minecraft.getInstance().options, Component.literal("Mob Armor Trims"));
        }

        @Override
        public void init() {
            MultiLineTextWidget messageWidget = new MultiLineTextWidget(
                    width / 2 - 110, height / 2 - 40,
                    translatable("mob_armor_trims.config.backup_screen.installYACL"),
                    minecraft.font);
            messageWidget.setMaxWidth(240);
            messageWidget.setCentered(true);
            addRenderableWidget(messageWidget);

            Button openLinkButton = Button.builder(translatable("mob_armor_trims.config.backup_screen.viewOnModrinth"),
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

        //? >=1.21 {
        @Override
        protected void addOptions() {
        }
        //?}

        @Override
        public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float delta) {
            //? <=1.20.1
            /*renderDirtBackground(graphics);*/
            super.render(graphics, mouseX, mouseY, delta);
            graphics.drawCenteredString(font, title, width / 2, 5, 0xffffff);
        }
    }
}