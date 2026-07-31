package net.majo24.naturally_trimmed.config.screen;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.*;
import net.majo24.naturally_trimmed.NaturallyTrimmed;
import net.majo24.naturally_trimmed.config.Config;
import net.majo24.naturally_trimmed.trim_application.TrimApplier;
import net.majo24.naturally_trimmed.trim_application.TrimData;
import net.majo24.naturally_trimmed.config.FilterRule;
import net.minecraft.ChatFormatting;
import net.minecraft.IdentifierException;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.equipment.trim.*;
import org.jetbrains.annotations.NotNull;


import java.util.*;

import static net.majo24.naturally_trimmed.config.Config.INSTANCE;
import static net.majo24.naturally_trimmed.config.Config.DEFAULT;
import static net.minecraft.network.chat.Component.translatable;
import static net.minecraft.network.chat.Component.literal;

public class ConfigScreen {
    private ConfigScreen() {
    }

    private static final Formatters.Percentage percentageFormatter = new Formatters.Percentage();
    private static final Formatters.TrimSystem trimSystemFormatter = new Formatters.TrimSystem();

    private static final String TRANSLATION_KEY_PREFIX = "naturally_trimmed.config.";
    private static final String TRANSLATION_KEY_DESCRIPTION = ".desc";

    public static Screen getConfigScreen(Screen parent) {
        YetAnotherConfigLib.Builder configScreen = YetAnotherConfigLib.createBuilder()
                .title(literal("Naturally Trimmed"))
                .save(INSTANCE::saveToFile)

                .category(buildGeneralCategory())
                .category(buildFilteringCategory())
                .category(buildUtilsCategory());

        return configScreen.build().generateScreen(parent);
    }

    private static ConfigCategory buildGeneralCategory() {
        return ConfigCategory.createBuilder()
                .name(prefixed("general"))
                .tooltip(desc("general"))

                .group(OptionGroup.createBuilder()
                        .name(prefixed("enable_features"))
                        .description(optionDesc("enable_features"))
                        .collapsed(true)

                        .option(Option.<Boolean>createBuilder()
                                .name(prefixed("enable_features.enableTrimMobs"))
                                .description(optionDesc("enable_features.enableTrimMobs"))
                                .binding(DEFAULT.enableTrimMobs,
                                        () -> INSTANCE.enableTrimMobs,
                                        enableTrimMobs -> INSTANCE.enableTrimMobs = enableTrimMobs)
                                .controller(BooleanControllerBuilder::create)
                                .build())

                        .option(Option.<Boolean>createBuilder()
                                .name(prefixed("enable_features.enableTrimLootTables"))
                                .description(optionDesc("enable_features.enableTrimLootTables"))
                                .binding(DEFAULT.enableTrimLootTables,
                                        () -> INSTANCE.enableTrimLootTables,
                                        enableTrimLootTables -> INSTANCE.enableTrimLootTables = enableTrimLootTables)
                                .controller(BooleanControllerBuilder::create)
                                .build())

                        .option(Option.<Boolean>createBuilder()
                                .name(prefixed("enable_features.enableTrimTrades"))
                                .description(optionDesc("enable_features.enableTrimTrades"))
                                .binding(DEFAULT.enableTrimTrades,
                                        () -> INSTANCE.enableTrimTrades,
                                        enableTrimTrades -> INSTANCE.enableTrimTrades = enableTrimTrades)
                                .controller(BooleanControllerBuilder::create)
                                .build())
                        .build())

                .group(buildTrimMobsGroup())

                .group(OptionGroup.createBuilder()
                        .name(prefixed("trimLootTables"))
                        .description(optionDesc("trimLootTables"))
                        .collapsed(true)

                        .option(Option.<Integer>createBuilder()
                                .name(prefixed("trimLootTables.trimChance"))
                                .description(optionDesc("trimLootTables.trimChance"))
                                .binding(DEFAULT.trimLootTables.trimChance,
                                        () -> INSTANCE.trimLootTables.trimChance,
                                        trimChance -> INSTANCE.trimLootTables.trimChance = trimChance)
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                        .range(0, 100)
                                        .step(1)
                                        .formatValue(percentageFormatter))
                                .build()
                        ).build())

                .group(OptionGroup.createBuilder()
                        .name(prefixed("trimTrades"))
                        .description(optionDesc("trimTrades"))
                        .collapsed(true)

                        .option(Option.<Integer>createBuilder()
                                .name(prefixed("trimTrades.trimChance"))
                                .description(optionDesc("trimTrades.trimChance"))
                                .binding(DEFAULT.trimTrades.trimChance,
                                        () -> INSTANCE.trimTrades.trimChance,
                                        trimChance -> INSTANCE.trimTrades.trimChance = trimChance)
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                        .range(0, 100)
                                        .step(1)
                                        .formatValue(percentageFormatter))
                                .build())

                        .option(Option.<Integer>createBuilder()
                                .name(prefixed("trimTrades.minLevel"))
                                .description(optionDesc("trimTrades.minLevel"))
                                .binding(DEFAULT.trimTrades.minLevel,
                                        () -> INSTANCE.trimTrades.minLevel,
                                        minLevel -> INSTANCE.trimTrades.minLevel = minLevel)
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                        .range(1, 5)
                                        .step(1))
                                .build())
                        .build())
                .build();
    }

    private static OptionGroup buildTrimMobsGroup() {
        return OptionGroup.createBuilder()
                .name(prefixed("trimMobs"))
                .description(optionDesc("trimMobs"))
                .collapsed(true)

                .option(Option.<Config.TrimMobsSubConfig.TrimSystem>createBuilder()
                        .name(prefixed("trimMobs.trimSystem"))
                        .description(optionDesc("trimMobs.trimSystem"))
                        .binding(DEFAULT.trimMobs.trimSystem,
                                () -> INSTANCE.trimMobs.trimSystem,
                                enabledSystem -> INSTANCE.trimMobs.trimSystem = enabledSystem)
                        .controller(opt -> EnumControllerBuilder.create(opt)
                                .enumClass(Config.TrimMobsSubConfig.TrimSystem.class)
                                .formatValue(trimSystemFormatter))
                        .build())

                .option(Option.<Integer>createBuilder()
                        .name(prefixed("trimMobs.noTrimsChance"))
                        .description(optionDesc("trimMobs.noTrimsChance"))
                        .binding(DEFAULT.trimMobs.noTrimsChance,
                                () -> INSTANCE.trimMobs.noTrimsChance,
                                noTrimsChance -> INSTANCE.trimMobs.noTrimsChance = noTrimsChance)
                        .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                .range(0, 100)
                                .step(1)
                                .formatValue(percentageFormatter))
                        .build())

                .option(Option.<Integer>createBuilder()
                        .name(prefixed("trimMobs.trimChance"))
                        .description(optionDesc("trimMobs.trimChance"))
                        .binding(DEFAULT.trimMobs.trimChance,
                                () -> INSTANCE.trimMobs.trimChance,
                                trimsChance -> INSTANCE.trimMobs.trimChance = trimsChance)
                        .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                .range(0, 100)
                                .step(1)
                                .formatValue(percentageFormatter))
                        .build())
                .build();
    }

    private static ConfigCategory buildFilteringCategory() {
        return ConfigCategory.createBuilder()
                .name(prefixed("filtering"))
                .tooltip(desc("filtering"))

                .option(Option.<Boolean>createBuilder()
                        .name(prefixed("filtering.textureValidationFiltering"))
                        .description(optionDesc("filtering.textureValidationFiltering"))
                        .binding(DEFAULT.trimFiltering.textureValidationFiltering,
                                () -> INSTANCE.trimFiltering.textureValidationFiltering,
                                textureValidationFiltering -> INSTANCE.trimFiltering.textureValidationFiltering = textureValidationFiltering)
                        .controller(BooleanControllerBuilder::create)
                        .build())

                .option(Option.<Boolean>createBuilder()
                        .name(prefixed("filtering.vanillaOnly"))
                        .description(optionDesc("filtering.vanillaOnly"))
                        .binding(DEFAULT.trimFiltering.vanillaOnly,
                                () -> INSTANCE.trimFiltering.vanillaOnly,
                                vanillaOnly -> INSTANCE.trimFiltering.vanillaOnly = vanillaOnly)
                        .controller(BooleanControllerBuilder::create)
                        .build())

                .group(ListOption.<String>createBuilder()
                        .name(prefixed("filtering.materialFilter"))
                        .description(optionDesc("filtering.materialFilter"))
                        .collapsed(true)
                        .binding(DEFAULT.trimFiltering.materialFilter.stream().map(FilterRule::toString).toList(),
                                () -> INSTANCE.trimFiltering.materialFilter.stream().map(FilterRule::toString).toList(),
                                materialFilters -> INSTANCE.trimFiltering.materialFilter = materialFilters.stream().map(filter -> FilterRule.<TrimMaterial>construct(filter, true)).toList())
                        .controller(StringControllerBuilder::create)
                        .initial("")
                        .build())

                .group(ListOption.<String>createBuilder()
                        .name(prefixed("filtering.patternFilter"))
                        .description(optionDesc("filtering.patternFilter"))
                        .collapsed(true)
                        .binding(DEFAULT.trimFiltering.patternFilter.stream().map(FilterRule::toString).toList(),
                                () -> INSTANCE.trimFiltering.patternFilter.stream().map(FilterRule::toString).toList(),
                                patternFilters -> INSTANCE.trimFiltering.patternFilter = patternFilters.stream().map(pattern -> FilterRule.<TrimPattern>construct(pattern, false)).toList())
                        .controller(StringControllerBuilder::create)
                        .initial("")
                        .build())
                .build();
    }

    private static ConfigCategory buildUtilsCategory() {
        boolean isInWorld = Minecraft.getInstance().level != null;

        return ConfigCategory.createBuilder()
                .name(prefixed("utils"))
                .tooltip(desc("utils"))

                .option(ButtonOption.createBuilder()
                        .name((prefixed("utils.openFile")))
                        .description(optionDesc("utils.openFile"))
                        .text(prefixed("utils.run"))
                        .action((screen, option) -> Util.getPlatform().openPath(NaturallyTrimmed.getConfigPath()))
                        .build())

                .option(ButtonOption.createBuilder()
                        .name(prefixed("utils.reloadConfig"))
                        .description(optionDesc("utils.reloadConfig"))
                        .text(prefixed("utils.run"))
                        .action((screen, option) -> {
                            INSTANCE.loadFromFile();
                            screen.onClose();
                        })
                        .build())

                .option(ButtonOption.createBuilder()
                        .name(prefixed("utils.validatePredefinedTrims"))
                        .description(optionDesc("utils.validatePredefinedTrims"))
                        .text(isInWorld ? prefixed("utils.run") : prefixed("utils.run").withStyle(ChatFormatting.STRIKETHROUGH))
                        .action((screen, option) -> validatePredefinedTrims())
                        .build())

                .option(ButtonOption.createBuilder()
                        .name(prefixed("utils.validateBlacklists"))
                        .description(optionDesc("utils.validateBlacklists"))
                        .text(isInWorld ? prefixed("utils.run") : prefixed("utils.run").withStyle(ChatFormatting.STRIKETHROUGH))
                        .action((screen, option) -> validateFilters())
                        .build())
                .build();
    }

    private static MutableComponent prefixed(String path) {
        return translatable(TRANSLATION_KEY_PREFIX + path);
    }

    private static MutableComponent desc(String path) {
        return translatable(TRANSLATION_KEY_PREFIX + path + TRANSLATION_KEY_DESCRIPTION);
    }

    private static OptionDescription optionDesc(String path) {
        return OptionDescription.of(desc(path));
    }

    public static void validatePredefinedTrims() {
        LocalPlayer player = Minecraft.getInstance().player;
        ClientLevel level = Minecraft.getInstance().level;

        if (level == null || player == null) return;
        RegistryAccess registryAccess = level.registryAccess();

        message(player, literal("\nValidating predefined trims...\n"));

        int total = INSTANCE.trimMobs.predefinedTrims.size();
        int valid = 0;
        int index = 0;

        for (TrimData trimData : INSTANCE.trimMobs.predefinedTrims) {
            try {
                trimData.getTrim(registryAccess);
                valid += 1;
            } catch (NoSuchElementException | IdentifierException ignored) {
                message(player, literal("Found invalid trim: \"" + trimData + "\" with index " + index));
            } finally {
                index++;
            }
        }

        message(player, literal("\n" + valid + " out of " + total + " trims are valid."));
        message(player, literal("Done validating predefined trims"));
    }

    public static void validateFilters() {
        LocalPlayer player = Minecraft.getInstance().player;
        ClientLevel level = Minecraft.getInstance().level;

        if (level == null || player == null) return;
        RegistryAccess registryAccess = level.registryAccess();

        message(player, literal("\nValidating material filter:\n").withStyle(ChatFormatting.UNDERLINE).withStyle(ChatFormatting.BOLD));
        validateFilterList(TrimApplier.getTrimMaterials(registryAccess).stream().toList(), INSTANCE.trimFiltering.materialFilter, player);
        message(player, literal("\nDone validating material filter").withStyle(ChatFormatting.UNDERLINE));

        message(player, literal("\nValidating pattern filter:\n").withStyle(ChatFormatting.UNDERLINE).withStyle(ChatFormatting.BOLD));
        validateFilterList(TrimApplier.getTrimPatterns(registryAccess).stream().toList(), INSTANCE.trimFiltering.patternFilter, player);
        message(player, literal("\nDone validating pattern filter").withStyle(ChatFormatting.UNDERLINE));

        if (INSTANCE.trimFiltering.vanillaOnly) {
            message(player, literal("\nNote that all non-vanilla trims are blacklisted through the vanillaOnly config entry"));
        }
    }

    private static <T> void validateFilterList(List<Holder.Reference<T>> trimParts, List<FilterRule<T>> filter, LocalPlayer player) {
        // Trim Part, Filter that applies the trim part
        Map<Holder.Reference<T>, Optional<FilterRule<T>>> trimStates = new HashMap<>();
        List<FilterRule<T>> unnecessaryRules = new ArrayList<>(filter);

        for (Holder.Reference<T> trimPart : trimParts) {
            Optional<FilterRule<T>> relevantRule = Optional.empty();
            for (FilterRule<T> rule : filter) {
                if (rule.matches(trimPart) != FilterRule.FilterResult.Irrelevant) {
                    unnecessaryRules.remove(rule);
                    relevantRule = Optional.of(rule);
                    break;
                }
            }

            trimStates.put(trimPart, relevantRule);
        }

        message(player, literal("Trim Part -> State -> Rule").withStyle(ChatFormatting.UNDERLINE));
        for (Map.Entry<Holder.Reference<T>, Optional<FilterRule<T>>> state : trimStates.entrySet()) {
            String filteredState = "Whitelisted";
            String ruleString = "whitelisted by default";

            if (state.getValue().isPresent()) {
                FilterRule<T> rule = state.getValue().get();
                filteredState = rule.filterDirection.toString() + "ed";
                ruleString = rule.toString();
            }

            message(player, literal(state.getKey().key().identifier() + " -> " + filteredState + " -> " + ruleString));
        }

        message(player, literal("\nChecking for unused rules...").withStyle(ChatFormatting.UNDERLINE));
        for (FilterRule<T> rule : unnecessaryRules) {
            message(player, literal(rule.toString()));
        }
        message(player, literal("Done checking for unused rules").withStyle(ChatFormatting.UNDERLINE));
    }

    public static class Formatters {
        private Formatters() {
        }

        public static class Percentage implements ValueFormatter<Integer> {
            @Override
            public Component format(Integer value) {
                return literal(value + "%");
            }
        }

        public static class TrimSystem implements ValueFormatter<Config.TrimMobsSubConfig.TrimSystem> {
            @Override
            public Component format(Config.TrimMobsSubConfig.TrimSystem selectedSystem) {
                return switch (selectedSystem) {
                    case RANDOM_TRIMS -> prefixed("trimMobs.trimSystem.randomTrims");
                    case PREDEFINED_TRIMS -> prefixed("trimMobs.trimSystem.predefinedTrims");
                };
            }
        }
    }

    static class BackupScreen extends OptionsSubScreen {
        public BackupScreen(Screen parent) {
            super(parent, Minecraft.getInstance().options, literal("Naturally Trimmed"));
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

        @Override
        protected void addOptions() {
        }

        @Override
                //? if >=26.1 {
        public void extractRenderState(@NotNull net.minecraft.client.gui.GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
         //?} else
        //public void render(@NotNull net.minecraft.client.gui.GuiGraphics graphics, int mouseX, int mouseY, float delta) {
            //? if >=26.1 {
            super.extractRenderState(graphics, mouseX, mouseY, delta);
            graphics.centeredText(font, title, width / 2, 5, 0xffffff);
            //?} else {
            /*super.render(graphics, mouseX, mouseY, delta);
            graphics.drawCenteredString(font, title, width / 2, 5, 0xffffff);
            *///?}
        }
    }

    private static void message(LocalPlayer player, Component message) {
        //? if >=26.1 {
        player.sendSystemMessage(message);
         //?} else
        //player.displayClientMessage(message, false);
    }
}