package net.majo24.mob_armor_trims.config.screen;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.isxander.yacl3.api.controller.ValueFormatter;
import net.majo24.mob_armor_trims.config.Config;
import net.majo24.mob_armor_trims.config.TrimMobsSubConfig;
import net.majo24.mob_armor_trims.trim_combinations_system.TrimCombination;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.equipment.trim.*;
import org.jetbrains.annotations.NotNull;

/*? <1.21 {*/
/*import net.minecraft.client.gui.screens.OptionsSubScreen;
 *//*?} else {*/
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
/*?}*/

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static net.majo24.mob_armor_trims.config.Config.CONFIG_MANAGER;
import static net.minecraft.network.chat.Component.translatable;

public class ConfigScreen {
    private ConfigScreen() {
    }

    public static final Formatters.IntegerToPercentage integerToPercentageFormatter = new Formatters.IntegerToPercentage();
    public static final Formatters.TrimSystem trimSystemFormatter = new Formatters.TrimSystem();

    public static Screen getConfigScreen(Screen parent) {
        YetAnotherConfigLib.Builder configScreen = YetAnotherConfigLib.createBuilder()
                .title(Component.literal("Mob Armor Trims"))
                .save(CONFIG_MANAGER::saveInstance)

                .category(buildGeneralCategory())
                .category(buildRandomTrimsCategory())
                .category(buildUtilsCategory());

        return configScreen.build().generateScreen(parent);
    }

    private static ConfigCategory buildGeneralCategory() {
        return ConfigCategory.createBuilder()
                .name(translatable("mob_armor_trims.config.general"))
                .tooltip(translatable("mob_armor_trims.config.general.tooltip"))

                .group(OptionGroup.createBuilder()
                        .name(translatable("mob_armor_trims.config.general.trimMobs"))
                        .description(OptionDescription.of(translatable("mob_armor_trims.config.general.trimMobs.description")))

                        .option(Option.<TrimMobsSubConfig.TrimSystem>createBuilder()
                                .name(translatable("mob_armor_trims.config.general.trimMobs.trimSystem"))
                                .description(OptionDescription.of(translatable("mob_armor_trims.config.general.trimMobs.trimSystem.description")))
                                .binding(CONFIG_MANAGER.defaults().trimMobs.trimSystem,
                                        () -> CONFIG_MANAGER.instance().trimMobs.trimSystem,
                                        enabledSystem -> CONFIG_MANAGER.instance().trimMobs.trimSystem = enabledSystem)
                                .controller(opt -> EnumControllerBuilder.create(opt)
                                        .enumClass(TrimMobsSubConfig.TrimSystem.class)
                                        .formatValue(trimSystemFormatter))
                                .build())

                        .option(Option.<Integer>createBuilder()
                                .name(translatable("mob_armor_trims.config.general.trimMobs.noTrimsChance"))
                                .description(OptionDescription.of(translatable("mob_armor_trims.config.general.trimMobs.noTrimsChance.description")))
                                .binding(CONFIG_MANAGER.defaults().trimMobs.noTrimsChance,
                                        () -> CONFIG_MANAGER.instance().trimMobs.noTrimsChance,
                                        noTrimsChance -> CONFIG_MANAGER.instance().trimMobs.noTrimsChance = noTrimsChance)
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                        .range(0, 100)
                                        .step(1)
                                        .formatValue(integerToPercentageFormatter))
                                .build())

                        .build())

                .group(OptionGroup.createBuilder()
                        .name(translatable("mob_armor_trims.config.general.trimLootTables"))
                        .description(OptionDescription.of(translatable("mob_armor_trims.config.general.trimLootTables.description")))

                        .option(Option.<Integer>createBuilder()
                                .name(translatable("mob_armor_trims.config.general.trimLootTables.trimChance"))
                                .description(OptionDescription.of(translatable("mob_armor_trims.config.general.trimLootTables.trimChance.description")))
                                .binding(CONFIG_MANAGER.defaults().trimLootTables.trimChance,
                                        () -> CONFIG_MANAGER.instance().trimLootTables.trimChance,
                                        trimChance -> CONFIG_MANAGER.instance().trimLootTables.trimChance = trimChance)
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                        .range(0, 100)
                                        .step(1)
                                        .formatValue(integerToPercentageFormatter))
                                .build()

                        ).build())

                .group(OptionGroup.createBuilder()
                        .name(translatable("mob_armor_trims.config.general.trimTrades"))
                        .description(OptionDescription.of(translatable("mob_armor_trims.config.general.trimTrades.description")))

                        .option(Option.<Integer>createBuilder()
                                .name(translatable("mob_armor_trims.config.general.trimTrades.trimChance"))
                                .description(OptionDescription.of(translatable("mob_armor_trims.config.general.trimTrades.trimChance.description")))
                                .binding(CONFIG_MANAGER.defaults().trimTrades.trimChance,
                                        () -> CONFIG_MANAGER.instance().trimTrades.trimChance,
                                        trimChance -> CONFIG_MANAGER.instance().trimTrades.trimChance = trimChance)
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                        .range(0, 100)
                                        .step(1)
                                        .formatValue(integerToPercentageFormatter))
                                .build())

                        .option(Option.<Integer>createBuilder()
                                .name(translatable("mob_armor_trims.config.general.trimTrades.minLevel"))
                                .description(OptionDescription.of(translatable("mob_armor_trims.config.general.trimTrades.minLevel.description")))
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

    private static ConfigCategory buildRandomTrimsCategory() {
        System.out.println(CONFIG_MANAGER.defaults().trimMobs.trimCombinations.get(0).getClass());

        return ConfigCategory.createBuilder()
                .name(translatable("mob_armor_trims.config.randomTrims"))
                .tooltip(translatable("mob_armor_trims.config.randomTrims.tooltip"))

                .option(Option.<Integer>createBuilder()
                        .name(translatable("mob_armor_trims.config.randomTrims.trimChance"))
                        .description(OptionDescription.of(translatable("mob_armor_trims.config.randomTrims.trimChance.description")))
                        .binding(CONFIG_MANAGER.defaults().trimMobs.randomTrims.trimChance,
                                () -> CONFIG_MANAGER.instance().trimMobs.randomTrims.trimChance,
                                trimsChance -> CONFIG_MANAGER.instance().trimMobs.randomTrims.trimChance = trimsChance)
                        .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                .range(0, 100)
                                .step(1)
                                .formatValue(integerToPercentageFormatter))
                        .build())

                .option(Option.<Integer>createBuilder()
                        .name(translatable("mob_armor_trims.config.randomTrims.similarTrimChance"))
                        .description(OptionDescription.of(translatable("mob_armor_trims.config.randomTrims.similarTrimChance.description")))
                        .binding(CONFIG_MANAGER.defaults().trimMobs.randomTrims.similarTrimChance,
                                () -> CONFIG_MANAGER.instance().trimMobs.randomTrims.similarTrimChance,
                                similarTrimChance -> CONFIG_MANAGER.instance().trimMobs.randomTrims.similarTrimChance = similarTrimChance)
                        .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                .range(0, 100)
                                .step(1)
                                .formatValue(integerToPercentageFormatter))
                        .build())

                .group(ListOption.<String>createBuilder()
                        .name(translatable("mob_armor_trims.config.randomTrims.blacklist"))
                        .description(OptionDescription.of(translatable("mob_armor_trims.config.randomTrims.blacklist.description")))
                        .binding(CONFIG_MANAGER.defaults().trimMobs.randomTrims.blacklist.stream().map(patter -> patter.pattern()).toList(),
                                () -> CONFIG_MANAGER.instance().trimMobs.randomTrims.blacklist.stream().map(patter -> patter.pattern()).toList(),
                                blacklist -> CONFIG_MANAGER.instance().trimMobs.randomTrims.blacklist = blacklist.stream().map(pattern -> Pattern.compile(pattern)).toList())
                        .controller(StringControllerBuilder::create)
                        .initial("")
                        .build()
                )

                .build();
    }

    private static ConfigCategory buildUtilsCategory() {
        return ConfigCategory.createBuilder()
                .name(translatable("mob_armor_trims.config.utils"))
                .tooltip(translatable("mob_armor_trims.config.utils.tooltip"))

                .option(ButtonOption.createBuilder()
                        .name(translatable("mob_armor_trims.config.utils.reloadConfig"))
                        .description(OptionDescription.of(translatable("mob_armor_trims.config.utils.reloadConfig.description")))
                        .action((screen, option) -> {
                            CONFIG_MANAGER.loadInstance();
                            screen.onClose();
                        })
                        .build())

                .option(ButtonOption.createBuilder()
                        .name(translatable("mob_armor_trims.config.utils.validateCustomTrimCombinations"))
                        .description(OptionDescription.of(translatable("mob_armor_trims.config.utils.validateCustomTrimCombinations.description")))
                        .action((screen, option) -> {
                            LocalPlayer player = Minecraft.getInstance().player;
                            ClientLevel level = Minecraft.getInstance().level;

                            if (level == null || player == null) return;

                            RegistryAccess registryAccess = level.registryAccess();

                            player.displayClientMessage(Component.literal("Validating custom trim combinations..."), false);

                            int index = 1;
                            for (TrimCombination trimCombination : CONFIG_MANAGER.instance().trimMobs.trimCombinations) {
                                trimCombination.validate(registryAccess, player, index);
                                index++;
                            }


                            player.displayClientMessage(Component.literal("Done validating custom trim combinations"), false);
                        })
                        .build())

                .option(ButtonOption.createBuilder()
                        .name(translatable("mob_armor_trims.config.utils.validateBlacklist"))
                        .description(OptionDescription.of(translatable("mob_armor_trims.config.utils.validateBlacklist.description")))
                        .action((screen, option) -> {
                            LocalPlayer player = Minecraft.getInstance().player;
                            ClientLevel level = Minecraft.getInstance().level;

                            if (level == null || player == null) return;

                            //? >=1.21.2 {
                            Registry<TrimPattern> patternRegistry = level.registryAccess().lookupOrThrow(Registries.TRIM_PATTERN);
                            List<Holder.Reference<TrimPattern>> trimPatternsList = new ArrayList<>(patternRegistry.listElements().toList());
                            //?} else {
                            /*Registry<TrimPattern> patternRegistry = level.registryAccess().registryOrThrow(Registries.TRIM_PATTERN);
                            List<Holder.Reference<TrimPattern>> trimPatternsList = new ArrayList<>(patternRegistry.holders().toList());
                            *///?}

                            Map<String, Boolean> trimPatterns = trimPatternsList.stream().collect(
                                    Collectors.toMap(trimPattern -> trimPattern.key().location().toString(), trimPattern -> false)
                            );

                            Map<Pattern, Boolean> blacklist = CONFIG_MANAGER.instance().trimMobs.randomTrims.blacklist.stream().collect(
                                    Collectors.toMap(pattern -> pattern, pattern -> false)
                            );


                            player.displayClientMessage(Component.literal("Validating blacklist..."), false);
                            player.displayClientMessage(Component.literal("\n"), false);


                            for (Map.Entry<Pattern, Boolean> pattern : blacklist.entrySet()) {
                                for (Map.Entry<String, Boolean> trimPattern : trimPatterns.entrySet()) {
                                    if (pattern.getKey().matcher(trimPattern.getKey()).find()) {
                                        pattern.setValue(true);
                                        trimPattern.setValue(true);
                                        player.displayClientMessage(Component.literal("Regex pattern \"" + pattern.getKey() + "\" blacklists trim pattern \"" + trimPattern.getKey() + "\""), false);
                                    }
                                }
                            }

                            player.displayClientMessage(Component.literal("\nChecking for not blacklisted trim patterns\n"), false);

                            for (Map.Entry<String, Boolean> trimPattern : trimPatterns.entrySet()) {
                                if (!trimPattern.getValue()) {
                                    player.displayClientMessage(Component.literal("Trim pattern \"" + trimPattern.getKey() + "\" is not blacklisted"), false);
                                }
                            }

                            player.displayClientMessage(Component.literal("\nChecking for unnecessary regex patterns\n"), false);

                            for (Map.Entry<Pattern, Boolean> pattern : blacklist.entrySet()) {
                                if (!pattern.getValue()) {
                                    player.displayClientMessage(Component.literal("Regex pattern \"" + pattern.getKey() + "\" does not blacklist any registered trim pattern."), false);
                                }
                            }

                            player.displayClientMessage(Component.literal("\n"), false);
                            player.displayClientMessage(Component.literal("Done validating blacklist"), false);
                        })
                        .build()
                )

                .build();
    }

    public static class Formatters {
        public static class IntegerToPercentage implements ValueFormatter<Integer> {
            @Override
            public Component format(Integer value) {
                return Component.literal(value.toString() + "%");
            }
        }

        public static class TrimSystem implements ValueFormatter<TrimMobsSubConfig.TrimSystem> {
            @Override
            public Component format(TrimMobsSubConfig.TrimSystem selectedSystem) {
                return switch (selectedSystem) {
                    case RANDOM_TRIMS -> Component.literal("Random Trims");
                    case CUSTOM_TRIM_COMBINATIONS -> Component.literal("Custom Trim Combinations");
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
                    Objects.requireNonNull(minecraft).font);
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