package net.majo24.naturally_trimmed.config.screen;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.*;
import net.majo24.naturally_trimmed.NaturallyTrimmed;
import net.majo24.naturally_trimmed.config.Config;
import net.majo24.naturally_trimmed.trim_application.TrimApplier;
import net.majo24.naturally_trimmed.trim_application.TrimData;
import net.minecraft.ChatFormatting;
import net.minecraft.IdentifierException;
import net.minecraft.util.Util;
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
 *///?} else
import net.minecraft.client.gui.screens.options.OptionsSubScreen;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

                // This is intentionally in a group,
                // as the option would be put at the top of the other groups, if it wasn't a group
                .group(OptionGroup.createBuilder()
                    .name(translatable("naturally_trimmed.config.vanillaOnly"))
                    .description(OptionDescription.of(translatable("naturally_trimmed.config.vanillaOnly.description")))
                        .collapsed(true)
                    .option(Option.<Boolean>createBuilder()
                        .name(translatable("naturally_trimmed.config.vanillaOnly"))
                        .description(OptionDescription.of(translatable("naturally_trimmed.config.vanillaOnly.description")))
                        .binding(CONFIG_MANAGER.defaults().vanillaOnly,
                                () -> CONFIG_MANAGER.instance().vanillaOnly,
                                vanillaOnly -> CONFIG_MANAGER.instance().vanillaOnly = vanillaOnly)
                        .controller(BooleanControllerBuilder::create)
                        .build())
                    .build())

                .group(ListOption.<String>createBuilder()
                        .name(translatable("naturally_trimmed.config.materialBlacklist"))
                        .description(OptionDescription.of(translatable("naturally_trimmed.config.materialBlacklist.description")))
                        .collapsed(true)
                        .binding(CONFIG_MANAGER.defaults().materialBlacklist.stream().map(Pattern::pattern).toList(),
                                () -> CONFIG_MANAGER.instance().materialBlacklist.stream().map(Pattern::pattern).toList(),
                                materialBlacklist -> CONFIG_MANAGER.instance().materialBlacklist = materialBlacklist.stream().map(Pattern::compile).toList())
                        .controller(StringControllerBuilder::create)
                        .initial("")
                        .build())

                .group(ListOption.<String>createBuilder()
                        .name(translatable("naturally_trimmed.config.patternBlacklist"))
                        .description(OptionDescription.of(translatable("naturally_trimmed.config.patternBlacklist.description")))
                        .collapsed(true)
                        .binding(CONFIG_MANAGER.defaults().patternBlacklist.stream().map(Pattern::pattern).toList(),
                                () -> CONFIG_MANAGER.instance().patternBlacklist.stream().map(Pattern::pattern).toList(),
                                patternBlacklist -> CONFIG_MANAGER.instance().patternBlacklist = patternBlacklist.stream().map(Pattern::compile).toList())
                        .controller(StringControllerBuilder::create)
                        .initial("")
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
                        .name((translatable("naturally_trimmed.config.utils.openFile")))
                        .description(OptionDescription.of(translatable("naturally_trimmed.config.utils.openFile")))
                        .text(translatable("naturally_trimmed.config.utils.run"))
                        .action((screen, option) -> Util.getPlatform().openPath(NaturallyTrimmed.getConfigPath()))
                        .build())

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
                        .text(isInWorld ? translatable("naturally_trimmed.config.utils.run") : translatable("naturally_trimmed.config.utils.run").withStyle(ChatFormatting.STRIKETHROUGH))
                        .action((screen, option) -> validatePredefinedTrims())
                        .build())

                .option(ButtonOption.createBuilder()
                        .name(translatable("naturally_trimmed.config.utils.validateBlacklists"))
                        .description(OptionDescription.of(translatable("naturally_trimmed.config.utils.validateBlacklists.description")))
                        .text(isInWorld ? translatable("naturally_trimmed.config.utils.run") : translatable("naturally_trimmed.config.utils.run").withStyle(ChatFormatting.STRIKETHROUGH))
                        .action((screen, option) -> validateBlacklists())
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
            } catch (NoSuchElementException | IdentifierException ignored) {
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

    public static void validateBlacklists() {
        LocalPlayer player = Minecraft.getInstance().player;
        ClientLevel level = Minecraft.getInstance().level;

        if (level == null || player == null) return;
        RegistryAccess registryAccess = level.registryAccess();

        player.displayClientMessage(Component.literal("\nValidating material blacklist:\n").withStyle(ChatFormatting.UNDERLINE).withStyle(ChatFormatting.BOLD), false);
        validateBlacklist(TrimApplier.getTrimMaterials(registryAccess).stream().map(material -> material.key().identifier().toString()).toList(), CONFIG_MANAGER.instance().materialBlacklist, player);
        player.displayClientMessage(Component.literal("\nDone validating material blacklist"), false);

        player.displayClientMessage(Component.literal("\nValidating pattern blacklist:\n").withStyle(ChatFormatting.UNDERLINE).withStyle(ChatFormatting.BOLD), false);
        validateBlacklist(TrimApplier.getTrimPatterns(registryAccess).stream().map(pattern -> pattern.key().identifier().toString()).toList(), CONFIG_MANAGER.instance().patternBlacklist, player);
        player.displayClientMessage(Component.literal("\nDone validating pattern blacklist"), false);

        if (CONFIG_MANAGER.instance().vanillaOnly) {
            player.displayClientMessage(Component.literal("\nNote that all non-vanilla trims are blacklisted through the vanillaOnly config entry"), false);
        }
    }

    private static void validateBlacklist(List<String> trims, List<Pattern> patterns, LocalPlayer player) {
        // Trim Identifier, Is Trim Blacklisted
        Map<String, Boolean> trimsStatus = trims.stream().collect(Collectors.toMap(pattern -> pattern, pattern -> false));

        // Regex Pattern, Does the Pattern blacklist a trim
        Map<Pattern, Boolean> patternsStatus = patterns.stream().collect(Collectors.toMap(pattern -> pattern, pattern -> false));

        player.displayClientMessage(Component.literal("Checking for blacklisted trim parts...").withStyle(ChatFormatting.UNDERLINE), false);

        for (Map.Entry<Pattern, Boolean> pattern : patternsStatus.entrySet()) {
            for (Map.Entry<String, Boolean> trim : trimsStatus.entrySet()) {
                if (pattern.getKey().matcher(trim.getKey()).find()) {
                    player.displayClientMessage(Component.literal("Regex pattern \"" + pattern.getKey() + "\" blacklists trim part \"" + trim.getKey() + "\""), false);
                    pattern.setValue(true);
                    trim.setValue(true);
                }
            }
        }

        player.displayClientMessage(Component.literal("\nChecking for not blacklisted trim parts...\n").withStyle(ChatFormatting.UNDERLINE), false);

        for (Map.Entry<String, Boolean> trim : trimsStatus.entrySet()) {
            if (!trim.getValue()) {
                player.displayClientMessage(Component.literal("Trim part \"" + trim.getKey() + "\" isn't blacklisted by any regex pattern"), false);
            }
        }

        player.displayClientMessage(Component.literal("\nChecking for unnecessary regex patterns...\n").withStyle(ChatFormatting.UNDERLINE), false);


        for (Map.Entry<Pattern, Boolean> pattern : patternsStatus.entrySet()) {
            if (!pattern.getValue()) {
                player.displayClientMessage(Component.literal("Regex pattern \"" + pattern.getKey() + "\" does not blacklist any registered trim patterns."), false);
            }
        }
    }

    
    public static class Formatters {
        private Formatters() {
        }

        public static class Percentage implements ValueFormatter<Integer> {
            @Override
            public Component format(Integer value) {
                return Component.literal(value + "%");
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
            //renderDirtBackground(graphics);
            super.render(graphics, mouseX, mouseY, delta);
            graphics.drawCenteredString(font, title, width / 2, 5, 0xffffff);
        }
    }
}