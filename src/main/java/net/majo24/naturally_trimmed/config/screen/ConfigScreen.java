package net.majo24.naturally_trimmed.config.screen;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.*;
import net.majo24.naturally_trimmed.config.Config;
import net.majo24.naturally_trimmed.trim_application.TrimData;
import net.minecraft.ChatFormatting;
import net.minecraft.ResourceLocationException;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

/*? <1.21 {*/
/*import net.minecraft.client.gui.screens.OptionsSubScreen;
 *//*?} else {*/
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
/*?}*/

import java.util.NoSuchElementException;
import java.util.Objects;

import static net.majo24.naturally_trimmed.config.Config.CONFIG_MANAGER;
import static net.minecraft.network.chat.Component.translatable;

public class ConfigScreen {
    private ConfigScreen() {
    }

    public static final Formatters.Percentage percentageFormatter = new Formatters.Percentage();
    public static final Formatters.TrimSystem trimSystemFormatter = new Formatters.TrimSystem();

    public static Screen getConfigScreen(Screen parent) {
        YetAnotherConfigLib.Builder configScreen = YetAnotherConfigLib.createBuilder()
                .title(Component.literal("Naturally Trimmed"))
                .save(CONFIG_MANAGER::saveInstance)

                .category(buildGeneralCategory())
                .category(buildUtilsCategory());

        return configScreen.build().generateScreen(parent);
    }

    private static ConfigCategory buildGeneralCategory() {
        return ConfigCategory.createBuilder()
                .name(translatable("naturally_trimmed.config.general"))
                .tooltip(translatable("naturally_trimmed.config.general.tooltip"))

                .group(OptionGroup.createBuilder()
                        .name(translatable("naturally_trimmed.config.enable_features"))
                        .description(OptionDescription.of(translatable("naturally_trimmed.config.enable_features.description")))
                        .collapsed(true)

                        .option(Option.<Boolean>createBuilder()
                                .name(translatable("naturally_trimmed.config.enable_features.enableTrimMobs"))
                                .description(OptionDescription.of(translatable("naturally_trimmed.config.enable_features.enableTrimMobs.description")))
                                .binding(CONFIG_MANAGER.defaults().enableTrimMobs,
                                        () -> CONFIG_MANAGER.instance().enableTrimMobs,
                                        enableTrimMobs -> CONFIG_MANAGER.instance().enableTrimMobs = enableTrimMobs)
                                .controller(BooleanControllerBuilder::create)
                                .build())

                        .option(Option.<Boolean>createBuilder()
                                .name(translatable("naturally_trimmed.config.enable_features.enableTrimLootTables"))
                                .description(OptionDescription.of(translatable("naturally_trimmed.config.enable_features.enableTrimLootTables.description")))
                                .binding(CONFIG_MANAGER.defaults().enableTrimLootTables,
                                        () -> CONFIG_MANAGER.instance().enableTrimLootTables,
                                        enableTrimLootTables -> CONFIG_MANAGER.instance().enableTrimLootTables = enableTrimLootTables)
                                .controller(BooleanControllerBuilder::create)
                                .build())

                        .option(Option.<Boolean>createBuilder()
                                .name(translatable("naturally_trimmed.config.enable_features.enableTrimTrades"))
                                .description(OptionDescription.of(translatable("naturally_trimmed.config.enable_features.enableTrimTrades.description")))
                                .binding(CONFIG_MANAGER.defaults().enableTrimTrades,
                                        () -> CONFIG_MANAGER.instance().enableTrimTrades,
                                        enableTrimTrades -> CONFIG_MANAGER.instance().enableTrimTrades = enableTrimTrades)
                                .controller(BooleanControllerBuilder::create)
                                .build())
                        .build())

                .group(buildTrimMobsGroup())

                .group(OptionGroup.createBuilder()
                        .name(translatable("naturally_trimmed.config.trimLootTables"))
                        .description(OptionDescription.of(translatable("naturally_trimmed.config.trimLootTables.description")))
                        .collapsed(true)

                        .option(Option.<Integer>createBuilder()
                                .name(translatable("naturally_trimmed.config.trimLootTables.trimChance"))
                                .description(OptionDescription.of(translatable("naturally_trimmed.config.trimLootTables.trimChance.description")))
                                .binding(CONFIG_MANAGER.defaults().trimLootTables.trimChance,
                                        () -> CONFIG_MANAGER.instance().trimLootTables.trimChance,
                                        trimChance -> CONFIG_MANAGER.instance().trimLootTables.trimChance = trimChance)
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                        .range(0, 100)
                                        .step(1)
                                        .formatValue(percentageFormatter))
                                .build()
                        ).build())

                .group(OptionGroup.createBuilder()
                        .name(translatable("naturally_trimmed.config.trimTrades"))
                        .description(OptionDescription.of(translatable("naturally_trimmed.config.trimTrades.description")))
                        .collapsed(true)

                        .option(Option.<Integer>createBuilder()
                                .name(translatable("naturally_trimmed.config.trimTrades.trimChance"))
                                .description(OptionDescription.of(translatable("naturally_trimmed.config.trimTrades.trimChance.description")))
                                .binding(CONFIG_MANAGER.defaults().trimTrades.trimChance,
                                        () -> CONFIG_MANAGER.instance().trimTrades.trimChance,
                                        trimChance -> CONFIG_MANAGER.instance().trimTrades.trimChance = trimChance)
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                        .range(0, 100)
                                        .step(1)
                                        .formatValue(percentageFormatter))
                                .build())

                        .option(Option.<Integer>createBuilder()
                                .name(translatable("naturally_trimmed.config.trimTrades.minLevel"))
                                .description(OptionDescription.of(translatable("naturally_trimmed.config.trimTrades.minLevel.description")))
                                .binding(CONFIG_MANAGER.defaults().trimTrades.minLevel,
                                        () -> CONFIG_MANAGER.instance().trimTrades.minLevel,
                                        minLevel -> CONFIG_MANAGER.instance().trimTrades.minLevel = minLevel)
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                        .range(1, 5)
                                        .step(1))
                                .build())
                        .build())
                .build();
    }

    private static OptionGroup buildTrimMobsGroup() {
        return OptionGroup.createBuilder()
                .name(translatable("naturally_trimmed.config.trimMobs"))
                .description(OptionDescription.of(translatable("naturally_trimmed.config.trimMobs.tooltip")))
                .collapsed(true)

                .option(Option.<Config.TrimMobsSubConfig.TrimSystem>createBuilder()
                        .name(translatable("naturally_trimmed.config.trimMobs.trimSystem"))
                        .description(OptionDescription.of(translatable("naturally_trimmed.config.trimMobs.trimSystem.description")))
                        .binding(CONFIG_MANAGER.defaults().trimMobs.trimSystem,
                                () -> CONFIG_MANAGER.instance().trimMobs.trimSystem,
                                enabledSystem -> CONFIG_MANAGER.instance().trimMobs.trimSystem = enabledSystem)
                        .controller(opt -> EnumControllerBuilder.create(opt)
                                .enumClass(Config.TrimMobsSubConfig.TrimSystem.class)
                                .formatValue(trimSystemFormatter))
                        .build())

                .option(Option.<Integer>createBuilder()
                        .name(translatable("naturally_trimmed.config.trimMobs.noTrimsChance"))
                        .description(OptionDescription.of(translatable("naturally_trimmed.config.trimMobs.noTrimsChance.description")))
                        .binding(CONFIG_MANAGER.defaults().trimMobs.noTrimsChance,
                                () -> CONFIG_MANAGER.instance().trimMobs.noTrimsChance,
                                noTrimsChance -> CONFIG_MANAGER.instance().trimMobs.noTrimsChance = noTrimsChance)
                        .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                .range(0, 100)
                                .step(1)
                                .formatValue(percentageFormatter))
                        .build())

                .option(Option.<Integer>createBuilder()
                        .name(translatable("naturally_trimmed.config.trimMobs.trimChance"))
                        .description(OptionDescription.of(translatable("naturally_trimmed.config.trimMobs.trimChance.description")))
                        .binding(CONFIG_MANAGER.defaults().trimMobs.trimChance,
                                () -> CONFIG_MANAGER.instance().trimMobs.trimChance,
                                trimsChance -> CONFIG_MANAGER.instance().trimMobs.trimChance = trimsChance)
                        .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                .range(0, 100)
                                .step(1)
                                .formatValue(percentageFormatter))
                        .build())
                .build();
    }

    private static ConfigCategory buildUtilsCategory() {
        boolean isInWorld = Minecraft.getInstance().level != null;

        return ConfigCategory.createBuilder()
                .name(translatable("naturally_trimmed.config.utils"))
                .tooltip(translatable("naturally_trimmed.config.utils.tooltip"))

                .option(ButtonOption.createBuilder()
                        .name(translatable("naturally_trimmed.config.utils.reloadConfig"))
                        .description(OptionDescription.of(translatable("naturally_trimmed.config.utils.reloadConfig.description")))
                        .text(translatable("naturally_trimmed.config.utils.run"))
                        .action((screen, option) -> {
                            CONFIG_MANAGER.loadInstance();
                            screen.onClose();
                        })
                        .build())

                .option(ButtonOption.createBuilder()
                        .name(translatable("naturally_trimmed.config.utils.validatePredefinedTrims"))
                        .description(OptionDescription.of(translatable("naturally_trimmed.config.utils.validatePredefinedTrims.description")))
                        .text(isInWorld
                                ? translatable("naturally_trimmed.config.utils.run")
                                : translatable("naturally_trimmed.config.utils.run").withStyle(ChatFormatting.STRIKETHROUGH))
                        .action((screen, option) -> validatePredefinedTrims())
                        .build())

                .build();
    }

    public static void validatePredefinedTrims() {
        LocalPlayer player = Minecraft.getInstance().player;
        ClientLevel level = Minecraft.getInstance().level;

        if (level == null || player == null) return;
        RegistryAccess registryAccess = level.registryAccess();

        player.displayClientMessage(Component.literal("\nValidating predefined trims...\n"), false);

        int total = CONFIG_MANAGER.instance().trimMobs.predefinedTrims.size();
        int valid = 0;
        int index = 0;

        for (TrimData trimData : CONFIG_MANAGER.instance().trimMobs.predefinedTrims) {
            try {
                trimData.getTrim(registryAccess);
                valid += 1;
            } catch (NoSuchElementException | ResourceLocationException ignored) {
                player.displayClientMessage(Component.literal(
                        "Found invalid trim: \"" + trimData + "\" with index " + index
                ), false);
            } finally {
                index++;
            }
        }

        player.displayClientMessage(Component.literal("\n" + valid + " out of " + total + " trims are valid."), false);
        player.displayClientMessage(Component.literal("Done validating predefined trims"), false);
    }

    public static class Formatters {
        private Formatters() {
        }

        public static class Percentage implements ValueFormatter<Integer> {
            @Override
            public Component format(Integer value) {
                return Component.literal(value.toString() + "%");
            }
        }

        public static class TrimSystem implements ValueFormatter<Config.TrimMobsSubConfig.TrimSystem> {
            @Override
            public Component format(Config.TrimMobsSubConfig.TrimSystem selectedSystem) {
                return switch (selectedSystem) {
                    case RANDOM_TRIMS -> translatable("naturally_trimmed.config.trimMobs.trimSystem.randomTrims");
                    case PREDEFINED_TRIMS ->
                            translatable("naturally_trimmed.config.trimMobs.trimSystem.predefinedTrims");
                };
            }
        }
    }

    static class BackupScreen extends OptionsSubScreen {
        public BackupScreen(Screen parent) {
            super(parent, Minecraft.getInstance().options, Component.literal("Naturally Trimmed"));
        }

        @Override
        public void init() {
            MultiLineTextWidget messageWidget = new MultiLineTextWidget(
                    width / 2 - 110, height / 2 - 40,
                    translatable("naturally_trimmed.config.backup_screen.installYACL"),
                    Objects.requireNonNull(minecraft).font);
            messageWidget.setMaxWidth(240);
            messageWidget.setCentered(true);
            addRenderableWidget(messageWidget);

            Button openLinkButton = Button.builder(translatable("naturally_trimmed.config.backup_screen.viewOnModrinth"),
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